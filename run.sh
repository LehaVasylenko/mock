#!/usr/bin/env bash
set -Eeuo pipefail

### === Настройки (меняй под себя при желании) ===
APP_NAME="mapper"                                    # имя для PID/логов
JAR_PATH="target/quarkus-app/quarkus-run.jar"        # куда собирает Quarkus
LOG_DIR="logs"
RUN_DIR="run"
LOG_FILE="${LOG_DIR}/${APP_NAME}.out"
PID_FILE="${RUN_DIR}/${APP_NAME}.pid"

# OUTBOX_DIR: можно задать в окружении перед запуском,
# по умолчанию — папка ./db в корне проекта
OUTBOX_DIR="${OUTBOX_DIR:-./db}"

### === Функции ===
start_ssh_agent_if_needed() {
  # Если агент не запущен — запустим
  if ! ssh-add -l >/dev/null 2>&1; then
    echo "[ssh] starting ssh-agent..."
    eval "$(ssh-agent -s)"
    # По желанию можно автоматически добавить ключ:
    # [ -f "$HOME/.ssh/id_ed25519" ] && ssh-add "$HOME/.ssh/id_ed25519" || true
    # [ -f "$HOME/.ssh/id_rsa" ]     && ssh-add "$HOME/.ssh/id_rsa"     || true
  else
    echo "[ssh] agent is running"
  fi
}

git_pull() {
  # Скрипт предполагает, что ты запускаешь его из папки репозитория (где есть .git)
  if [ ! -d .git ]; then
    echo "[git] Здесь нет .git, запусти скрипт из корня репозитория" >&2
    exit 1
  fi

  echo "[git] pulling origin main..."
  git fetch origin main
  git checkout main
  git pull --ff-only origin main
}

stop_app() {
  mkdir -p "$RUN_DIR"

  if [ -f "$PID_FILE" ]; then
    local PID
    PID="$(cat "$PID_FILE" || true)"
    if [ -n "${PID:-}" ] && ps -p "$PID" > /dev/null 2>&1; then
      echo "[app] stopping by PID ${PID}..."
      kill -TERM "$PID" || true

      # ждём до 10 секунд
      for i in {1..10}; do
        if ps -p "$PID" > /dev/null 2>&1; then
          sleep 1
        else
          break
        fi
      done

      # если не умер — добьём
      if ps -p "$PID" > /dev/null 2>&1; then
        echo "[app] still running, kill -9 ${PID}"
        kill -KILL "$PID" || true
      fi
    fi
    rm -f "$PID_FILE"
  else
    # Фоллбэк: попробуем найти по jar-ку
    local PIDS
    if PIDS="$(pgrep -f 'quarkus-run\.jar' || true)"; then
      if [ -n "$PIDS" ]; then
        echo "[app] stopping by pgrep: ${PIDS}"
        kill -TERM $PIDS || true
      fi
    fi
  fi
}

build_app() {
  echo "[maven] mvn clean package -DskipTests"
  mvn -B -U clean package -DskipTests
}

start_app() {
  # Абсолютные пути
  OUTBOX_DIR="$(realpath -m "$OUTBOX_DIR")"
  mkdir -p "$OUTBOX_DIR" "$LOG_DIR" "$RUN_DIR"

  if [ ! -f "$JAR_PATH" ]; then
    echo "[app] Jar не найден: $JAR_PATH" >&2
    exit 1
  fi

  echo "[app] starting…"
  echo "      JAR: $(realpath "$JAR_PATH")"
  echo "      OUTBOX_DIR: $OUTBOX_DIR"
  echo "      LOG: $LOG_FILE"

  # Пробросим системное свойство в Java:
  # -Dapp.outbox.dir=<абсолютный путь к ./db или OUTBOX_DIR из окружения>
  nohup java \
    -Dapp.outbox.dir="$OUTBOX_DIR" \
    -jar "$JAR_PATH" >> "$LOG_FILE" 2>&1 &

  echo $! > "$PID_FILE"
  echo "[app] started, pid=$(cat "$PID_FILE")"
}

### === Выполнение ===
start_ssh_agent_if_needed
git_pull
stop_app
build_app
start_app

echo
echo "[done] tail -f $LOG_FILE   # посмотреть логи"
