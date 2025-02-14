#!/usr/bin/env bash

# Forces the script to relaunch in bash if necessary
if [ -z "$BASH_VERSION" ]; then
  exec /bin/bash "$0" "$@"
fi

# Set variables
SCRIPT_DIR="$(cd "$(dirname "${0}")" && pwd)"
LOG_DIR="${SCRIPT_DIR}/logs"
JAVA_VERSION=$(java -version 2>&1 | grep -oP '(?<=version ")[^"]+')
declare -A USER_ARRAY

# Check if Java is installed
if ! command -v java &> /dev/null; then
echo "Java is not installed. You need to install Java 1.8."
  exit 1
fi

# Check Java version
if [[ "${JAVA_VERSION}" != 1.8* ]]; then
  echo "It is recommended to use Java 1.8 for IdleRSC!"
fi

# Add an entry for each account you want to launch a session for
# The 'keys' in USER_ARRAY are used for creating log files per instance, while the 'values' are for passing args to that instance.
# For example, 'USER_ARRAY["USERNAME"]="--auto-start --account testuser"'
# would run an instance with '--auto-start --account testuser' and log output to 'IdleRSC/scripts/logs/instance_USERNAME.log'

USER_ARRAY["USERNAME"]="--auto-start --account USERNAME"

# Delete old log files
if [ -n "${LOG_DIR}" ] && [ -d "${LOG_DIR}" ]; then
	if [ -z "$(find "${LOG_DIR}" -maxdepth 0 -type d -empty)" ]; then
		rm -r "${LOG_DIR}/instance_"*
	fi
fi

# Create new log directory if it doesn't exist
if [ ! -d "${LOG_DIR}" ]; then
	mkdir "${LOG_DIR}"
fi

cd "$SCRIPT_DIR/.." || exit

# Loop through the array to open an instance for each index
for user in "${!USER_ARRAY[@]}"; do
	LOG_FILE="${LOG_DIR}/instance_${user}.log"

  # Log useful information to the top of the instances log file
  printf "User: '%s'\nJava Info:\n%s\n" "${user}" "$(java -version 2>&1)" > "${LOG_FILE}"

	# Launch an instance and echo the output to logs/instance_username.log.
  nohup java -jar ./IdleRSC.jar "${USER_ARRAY[$user]}" >> "${LOG_FILE}" 2>&1 & sleep 0.3
done
