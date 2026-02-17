#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "$0")" && pwd)"
cd "$ROOT_DIR"

if command -v mvn >/dev/null 2>&1; then
  mvn test
  exit 0
fi

echo "mvn not found. Running local compile fallback."

JFX_VERSION="21"
H2_VERSION="2.2.224"
M2_DIR="${HOME}/.m2/repository"

JFX_BASE="${M2_DIR}/org/openjfx/javafx-base/${JFX_VERSION}/javafx-base-${JFX_VERSION}-mac.jar"
JFX_GRAPHICS="${M2_DIR}/org/openjfx/javafx-graphics/${JFX_VERSION}/javafx-graphics-${JFX_VERSION}-mac.jar"
JFX_CONTROLS="${M2_DIR}/org/openjfx/javafx-controls/${JFX_VERSION}/javafx-controls-${JFX_VERSION}-mac.jar"
JFX_FXML="${M2_DIR}/org/openjfx/javafx-fxml/${JFX_VERSION}/javafx-fxml-${JFX_VERSION}-mac.jar"
H2_JAR="${M2_DIR}/com/h2database/h2/${H2_VERSION}/h2-${H2_VERSION}.jar"

JUNIT_API="${M2_DIR}/org/junit/jupiter/junit-jupiter-api/5.10.1/junit-jupiter-api-5.10.1.jar"
JUNIT_PARAMS="${M2_DIR}/org/junit/jupiter/junit-jupiter-params/5.10.1/junit-jupiter-params-5.10.1.jar"
JUNIT_COMMONS="${M2_DIR}/org/junit/platform/junit-platform-commons/1.10.1/junit-platform-commons-1.10.1.jar"
OPEN_TEST4J="${M2_DIR}/org/opentest4j/opentest4j/1.3.0/opentest4j-1.3.0.jar"
API_GUARDIAN="${M2_DIR}/org/apiguardian/apiguardian-api/1.1.2/apiguardian-api-1.1.2.jar"

MODULE_PATH="${JFX_BASE}:${JFX_GRAPHICS}:${JFX_CONTROLS}:${JFX_FXML}"

mkdir -p target/classes target/test-classes
find src/main/java -name "*.java" > target/sources.list
find src/test -name "*.java" > target/test-sources.list

javac \
  --module-path "$MODULE_PATH" \
  --add-modules javafx.controls,javafx.fxml \
  -cp "$H2_JAR" \
  -d target/classes \
  @target/sources.list

TEST_CP="target/classes:${JUNIT_API}:${JUNIT_PARAMS}:${JUNIT_COMMONS}:${OPEN_TEST4J}:${API_GUARDIAN}"

javac \
  -cp "$TEST_CP" \
  -d target/test-classes \
  @target/test-sources.list

CONSOLE_VERSION="1.10.1"
CONSOLE_JAR="$(find "${M2_DIR}" -name "junit-platform-console-standalone-${CONSOLE_VERSION}.jar" | head -n 1 || true)"

if [[ -z "${CONSOLE_JAR}" ]]; then
  TOOLS_DIR="target/tools"
  CONSOLE_JAR="${TOOLS_DIR}/junit-platform-console-standalone-${CONSOLE_VERSION}.jar"
  if [[ ! -f "${CONSOLE_JAR}" ]]; then
    mkdir -p "${TOOLS_DIR}"
    echo "Downloading junit-platform-console-standalone ${CONSOLE_VERSION}..."
    curl -fsSL \
      -o "${CONSOLE_JAR}" \
      "https://repo1.maven.org/maven2/org/junit/platform/junit-platform-console-standalone/${CONSOLE_VERSION}/junit-platform-console-standalone-${CONSOLE_VERSION}.jar"
  fi
fi

java -jar "${CONSOLE_JAR}" \
  --class-path "target/classes:target/test-classes:${H2_JAR}" \
  --scan-class-path \
  --details summary
