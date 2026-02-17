#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "$0")" && pwd)"
cd "$ROOT_DIR"

# JavaFX on macOS can fail to reactivate when launched from some IDE integrated terminals.
if [[ "${XPC_SERVICE_NAME:-}" == application.com.jetbrains* ]]; then
  echo "Detected JetBrains integrated terminal."
  echo "If JavaFX window does not open, run this from Terminal.app:"
  echo "  cd \"$ROOT_DIR\" && bash run_app.sh"
fi

# Avoid inherited JVM options from shell profile that can break javac/javafx startup.
unset JAVA_TOOL_OPTIONS
unset JDK_JAVA_OPTIONS
unset _JAVA_OPTIONS

JFX_VERSION="21"
H2_VERSION="2.2.224"
M2_DIR="${HOME}/.m2/repository"

JFX_BASE="${M2_DIR}/org/openjfx/javafx-base/${JFX_VERSION}/javafx-base-${JFX_VERSION}-mac.jar"
JFX_GRAPHICS="${M2_DIR}/org/openjfx/javafx-graphics/${JFX_VERSION}/javafx-graphics-${JFX_VERSION}-mac.jar"
JFX_CONTROLS="${M2_DIR}/org/openjfx/javafx-controls/${JFX_VERSION}/javafx-controls-${JFX_VERSION}-mac.jar"
JFX_FXML="${M2_DIR}/org/openjfx/javafx-fxml/${JFX_VERSION}/javafx-fxml-${JFX_VERSION}-mac.jar"
H2_JAR="${M2_DIR}/com/h2database/h2/${H2_VERSION}/h2-${H2_VERSION}.jar"

for file in "$JFX_BASE" "$JFX_GRAPHICS" "$JFX_CONTROLS" "$JFX_FXML" "$H2_JAR"; do
  if [[ ! -f "$file" ]]; then
    echo "Missing dependency: $file"
    echo "Open IntelliJ -> Maven -> Reload project (or run mvn dependency:resolve) and retry."
    exit 1
  fi
done

MODULE_PATH="${JFX_BASE}:${JFX_GRAPHICS}:${JFX_CONTROLS}:${JFX_FXML}"

mkdir -p target/classes
mkdir -p target/javafx-cache
find src/main/java -name "*.java" > target/sources.list

javac \
  --module-path "$MODULE_PATH" \
  --add-modules javafx.controls,javafx.fxml \
  -cp "$H2_JAR" \
  -d target/classes \
  @target/sources.list

# Fresh JavaFX native cache per run avoids stale native artifacts.
rm -rf target/javafx-cache
mkdir -p target/javafx-cache

JAVA_ARGS=(
  --enable-native-access=javafx.graphics
  -Djavafx.cachedir="${ROOT_DIR}/target/javafx-cache"
  --module-path "$MODULE_PATH"
  --add-modules javafx.controls,javafx.fxml
  -cp "target/classes:${H2_JAR}"
  ui.ShelterManagementApp
)

# Optional fallback for environments that require first-thread startup.
if [[ "${START_ON_FIRST_THREAD:-0}" == "1" ]]; then
  JAVA_ARGS=(-XstartOnFirstThread "${JAVA_ARGS[@]}")
fi

java "${JAVA_ARGS[@]}"
