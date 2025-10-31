#!/usr/bin/env bash
set -Eeuo pipefail

### === Настройки (минимум) ===
APP_NAME="mock"                       # имя сервиса
BRANCH="main"                           # какую ветку тянуть
JAVA_OPTS="-XX:+UseStringDeduplication -Xms256m -Xmx512m"

### === Вспомогательные пути ===
RUN_USER="$(id -un)"
REPO_DIR="$(pwd)"
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
  echo "[git] fetch/pull ${BRANCH}..."
  git fetch origin "${BRANCH}"
  git checkout "${BRANCH}"
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
  mvn -B -U clean package -DskipTests
}

write_env_and_unit() {
  [[ -f "$RUN_JAR" ]] || { echo "ERROR: нет ${RUN_JAR}. Сборка прошла?"; exit 1; }

  echo "[env] ${ENV_FILE}"
  sudo install -d -m0755 "${ENV_DIR}"
  sudo tee "${ENV_FILE}" >/dev/null <<EOF
  JAVA_OPTS="${JAVA_OPTS}"
  APP_OPTS="${APP_OPTS:-}"
EOF
  sudo chmod 0644 "${ENV_FILE}"

  # подставим твои JAVA_OPTS, если заданы вверху
  sudo sed -i "s|^JAVA_OPTS=.*|JAVA_OPTS=\"'"${JAVA_OPTS}"'\"|" "${ENV_FILE}"

  echo "[unit] ${SERVICE_FILE}"
  # важное: одинарный heredoc, чтобы $ не раскрывались
  sudo tee "${SERVICE_FILE}" >/dev/null <<'EOF'
[Unit]
Description=mapper (Quarkus fast-jar in-place)
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

  # подставим реальные значения путей/пользователя
  sudo sed -i "s|__RUN_USER__|${RUN_USER}|; s|__WORKDIR__|${WORKDIR}|; s|__ENV_FILE__|${ENV_FILE}|;" "${SERVICE_FILE}"
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
