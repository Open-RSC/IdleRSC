#!/usr/bin/env bash

# Set your custom args here
ARGS=""

# Forces the script to relaunch in bash if necessary
if [ -z "$BASH_VERSION" ]; then
  exec /bin/bash "$0" "$@"
fi

# Set variables
SCRIPT_DIR="$(cd "$(dirname "${0}")" && pwd)"
LOG_DIR="${SCRIPT_DIR}/logs"
JAVA_VERSION=$(java -version 2>&1 | grep -oP '(?<=version ")[^"]+')
LOG_FILE="${LOG_DIR}/no_console.log"

# Check if Java is installed
if ! command -v java &> /dev/null; then
  echo "Java is not installed. You need to install Java 1.8."
  exit 1
fi

# Check Java version
if [[ "${JAVA_VERSION}" != 1.8* ]]; then
  echo "It is recommended to use Java 1.8 for IdleRSC!"
fi

# Delete old log files
if [ -n "${LOG_DIR}" ] && [ -d "${LOG_DIR}" ]; then
	if [ -z "$(find "${LOG_DIR}" -maxdepth 0 -type d -empty)" ]; then
		rm -r "${LOG_DIR}/no_console.log"
	fi
fi

# Create new log directory if it doesn't exist
if [ ! -d "${LOG_DIR}" ]; then
	mkdir "${LOG_DIR}"
fi

cd "$SCRIPT_DIR/.." || exit

# Log useful information to the top of the instances log file
printf "Java Info:\n%s\n" "$(java -version 2>&1)" > "${LOG_FILE}"

# Launch an instance and echo the output to logs/instance_username.log.
nohup java -jar ./IdleRSC.jar "${ARGS}" >> "${LOG_FILE}" 2>&1 & sleep 0.3
