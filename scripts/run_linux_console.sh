#!/usr/bin/env bash

# Forces the script to relaunch in bash if necessary
if [ -z "$BASH_VERSION" ]; then
  exec /bin/bash "$0" "$@"
fi

# Set variables
SCRIPT_DIR="$(cd "$(dirname "${0}")" && pwd)"
JAVA_VERSION=$(java -version 2>&1 | grep -oP '(?<=version ")[^"]+')

# Check if Java is installed
if ! command -v java &> /dev/null; then
  echo "Java is not installed. You need to install Java 1.8."
  exit 1
fi

# Check Java version
if [[ "${JAVA_VERSION}" != 1.8* ]]; then
  echo "It is recommended to use Java 1.8 for IdleRSC!"
fi

cd "$SCRIPT_DIR/.." || exit
java -jar IdleRSC.jar
