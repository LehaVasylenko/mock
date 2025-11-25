#!/usr/bin/env bash
set -Eeuo pipefail

### === Настройки ===
APP_NAME="mock"                                   # имя сервиса
BRANCH="main"                                     # ветка
JAVA_OPTS='-XX:+UseStringDeduplication -Xms256m -Xmx512m'

### === Пути ===
RUN_USER="$(id -un)"
REPO_DIR="$(pwd)"
DB_DIR="${REPO_DIR}/db"
WORKDIR="${REPO_DIR}/target/quarkus-app"
RUN_JAR="${WORKDIR}/quarkus-run.jar"
ENV_DIR="/etc/${APP_NAME}"
ENV_FILE="${ENV_DIR}/env"
SERVICE_FILE="/etc/systemd/system/${APP_NAME}.service"

### === Функции ===
start_ssh_agent_if_needed() {
  if ! ssh-add -l >/dev/null 2>&1; then
    echo "[ssh] starting ssh-agent..."
    eval "$(ssh-agent -s)"
    [[ -f "$HOME/.ssh/id_ed25519" ]] && ssh-add "$HOME/.ssh/id_ed25519" >/dev/null 2>&1 || true
    [[ -f "$HOME/.ssh/id_rsa"     ]] && ssh-add "$HOME/.ssh/id_rsa"     >/dev/null 2>&1 || true
  else
    echo "[ssh] agent is running"
  fi
}

git_pull() {
  [[ -d .git ]] || { echo "[git] нет .git — запусти из корня репозитория"; exit 1; }
  echo "[git] updating ${BRANCH}..."
  git fetch origin "${BRANCH}"
  git checkout "${BRANCH}"
  # если были локальные изменения — жестко откатываемся к origin/BRANCH
  git reset --hard "origin/${BRANCH}"
  git pull --ff-only origin "${BRANCH}"
}

stop_app() {
  if systemctl is-active --quiet "${APP_NAME}.service"; then
    echo "[svc] stopping ${APP_NAME}..."
    sudo systemctl stop "${APP_NAME}.service"
  fi
}

build_app() {
  echo "[maven] mvn -B -U clean package -DskipTests"
  if [[ -x ./mvnw ]]; then
    ./mvnw -B -U clean package -DskipTests
  else
    mvn -B -U clean package -DskipTests
  fi
}

write_env_and_unit() {
  [[ -f "$RUN_JAR" ]] || { echo "ERROR: нет ${RUN_JAR}. Сборка прошла?"; exit 1; }

  echo "[env] ${ENV_FILE}"
  sudo install -d -m0755 "${ENV_DIR}"
  # Пишем значения сразу, без sed
  sudo tee "${ENV_FILE}" >/dev/null <<EOF
JAVA_OPTS="${JAVA_OPTS}"
APP_OPTS="${APP_OPTS:-}"
OUTBOX_DIR="${DB_DIR}"
EOF
  sudo chmod 0644 "${ENV_FILE}"

  echo "[unit] ${SERVICE_FILE}"
  # Одинарный heredoc — переменные не раскрываются; подставим ниже через sed.
  sudo tee "${SERVICE_FILE}" >/dev/null <<'EOF'
[Unit]
Description=mock (Quarkus fast-jar in-place)
After=network-online.target
Wants=network-online.target

[Service]
Type=simple
User=__RUN_USER__
WorkingDirectory=__WORKDIR__
EnvironmentFile=__ENV_FILE__
ExecStart=/usr/bin/java $JAVA_OPTS -jar quarkus-run.jar $APP_OPTS
Restart=always
RestartSec=3
SuccessExitStatus=143
LimitNOFILE=65536

[Install]
WantedBy=multi-user.target
EOF

  # Подставляем реальные значения
  sudo sed -i \
    -e "s|__RUN_USER__|${RUN_USER}|g" \
    -e "s|__WORKDIR__|${WORKDIR}|g" \
    -e "s|__ENV_FILE__|${ENV_FILE}|g" \
    "${SERVICE_FILE}"
}

enable_and_start() {
  echo "[svc] daemon-reload + enable --now"
  sudo systemctl daemon-reload
  sudo systemctl enable --now "${APP_NAME}.service"
  sudo systemctl --no-pager --full status "${APP_NAME}.service" || true
  echo "[logs] journalctl -u ${APP_NAME} -f"
}

### === Выполнение ===
start_ssh_agent_if_needed
git_pull
stop_app
build_app
write_env_and_unit
enable_and_start
