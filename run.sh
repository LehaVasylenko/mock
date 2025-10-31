#!/usr/bin/env bash
set -Eeuo pipefail

### === Настройки (меняй под себя при желании) ===
APP_NAME="mapper"                                    # имя для PID/логов
JAR_PATH="target/quarkus-app/quarkus-run.jar"        # куда собирает Quarkus
LOG_DIR="logs"
RUN_DIR="run"
LOG_FILE="${LOG_DIR}/${APP_NAME}.out"
PID_FILE="${RUN_DIR}/${APP_NAME}.pid"
JAVA_OPTS="-XX:+UseStringDeduplication -Xms256m -Xmx512m"

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
  set -euo pipefail

  # Имя сервиса и пользователь (можешь экспортировать заранее)
  local APP_NAME="${APP_NAME:-myapp}"
  local RUN_USER="${RUN_USER:-$USER}"

  local APP_DIR="/opt/${APP_NAME}"
  local ENV_DIR="/etc/${APP_NAME}"
  local ENV_FILE="${ENV_DIR}/env"
  local SERVICE_FILE="/etc/systemd/system/${APP_NAME}.service"

  # Где взять JAR: если переменная не задана — берём последний из target/
  if [[ -z "${JAR_PATH:-}" ]]; then
    JAR_PATH="$(ls -1t target/*-runner.jar target/*-all.jar target/*with-dependencies*.jar target/*.jar 2>/dev/null | head -n1 || true)"
  fi
  if [[ -z "${JAR_PATH:-}" || ! -f "$JAR_PATH" ]]; then
    echo "ERROR: не найден jar в target/ (собери проект: mvn -DskipTests package)"; return 1
  fi

  local JAVA_BIN
  JAVA_BIN="$(command -v java || true)"
  [[ -n "$JAVA_BIN" ]] || { echo "ERROR: java не найдена в PATH"; return 1; }

  # Директории и деплой JAR
  sudo install -d -m0755 "$APP_DIR" "$ENV_DIR" "/var/log/${APP_NAME}"
  sudo cp -f "$JAR_PATH" "${APP_DIR}/app.jar"
  sudo chown -R "$RUN_USER":"$RUN_USER" "$APP_DIR" "/var/log/${APP_NAME}"
  sudo chmod 0755 "${APP_DIR}/app.jar"

  # Те же опции, что ты сейчас передаёшь в ручном запуске
  sudo tee "$ENV_FILE" >/dev/null <<EOF
JAVA_OPTS="${JAVA_OPTS:-}"
APP_OPTS="${APP_OPTS:-}"
EOF
  sudo chmod 0644 "$ENV_FILE"

  # systemd unit (минимальный)
  sudo tee "$SERVICE_FILE" >/dev/null <<EOF
[Unit]
Description=${APP_NAME} (Java/Quarkus)
After=network-online.target
Wants=network-online.target

[Service]
Type=simple
User=${RUN_USER}
WorkingDirectory=${APP_DIR}
EnvironmentFile=${ENV_FILE}
ExecStart=${JAVA_BIN} \$JAVA_OPTS -jar ${APP_DIR}/app.jar \$APP_OPTS
Restart=always
RestartSec=3
SuccessExitStatus=143

[Install]
WantedBy=multi-user.target
EOF

  # Перезагрузка unit’ов, автозапуск и старт
  sudo systemctl daemon-reload
  sudo systemctl enable --now "${APP_NAME}.service"

  echo "[OK] ${APP_NAME} запущен через systemd"
  echo "Статус:  sudo systemctl status ${APP_NAME}"
  echo "Логи:    journalctl -u ${APP_NAME} -f"
  echo "Опции:   sudo nano ${ENV_FILE}  &&  sudo systemctl restart ${APP_NAME}"
}


### === Выполнение ===
start_ssh_agent_if_needed
git_pull
stop_app
build_app
start_app

echo
echo "[done] tail -f $LOG_FILE   # посмотреть логи"
