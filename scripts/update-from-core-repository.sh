#!/usr/bin/env bash
# Updates client/src and cache from the Core Framework Gitlab repository.

# --------------VARIABLES--------------
# colors for echo -e
GREEN='\033[1;36m'
YELLOW='\033[1;33m'
RED='\033[1;31m'
PURPLE='\033[1;35m'
LIGHTBLUE='\033[1;34m'
RESET='\033[0m' #Use at the end of an echo so the following lines aren't also colored

# project-specific variables
ASSET_DIR="$(pwd)/assets"
CLIENT_DIR="$(pwd)/client"

# repository-related variables
CORE_REPOSITORY="https://gitlab.com/open-runescape-classic/core.git"
CORE_REPOSITORY_NAME="core"
CORE_REPOSITORY_DIR="${ASSET_DIR}/${CORE_REPOSITORY_NAME}"

# folders within the core repository
CLIENT_BASE_DIR="${CORE_REPOSITORY_DIR}/Client_Base"
PC_CLIENT_DIR="${CORE_REPOSITORY_DIR}/PC_Client"

# --------------FUNCTIONS--------------
# check confirmation to run script
run_confirmation () {
  clear
  echo -e ${GREEN}"THIS SCRIPT WILL DO THE FOLLOWING:"${RESET}
  echo -e ${LIGHTBLUE}" - ${PURPLE}Execute the '${YELLOW}gradle clean${PURPLE}' task to clean old files."${RESET}
  echo -e ${LIGHTBLUE}" - ${PURPLE}Clone the Core Framework (${YELLOW}~1GB${PURPLE}) GitLab repository."${RESET}
  echo -e ${LIGHTBLUE}" - ${PURPLE}Copy the needed files from the Core Framework download"${RESET}
  echo -e ${LIGHTBLUE}" - ${PURPLE}Delete the unnecessary files from the Core Framework download"${RESET}
  echo -e ${LIGHTBLUE}" - ${PURPLE}Execute the '${YELLOW}gradle build${PURPLE}' task to rebuild IdleRSC.\n"${RESET}

  echo -e ${YELLOW}"RUNNING THIS SCRIPT IS NOT NECESSARY TO USE IDLERSC.\n" ${RESET}
  read -e -p "Do you want to continue? (Y/N): " choice
  if [[ "$choice" == [Yy] ]]; then
    echo
  elif [[ "$choice" == [Nn] ]]; then
    echo -e ${RED}"Script aborted\n"${RESET} && exit 1
  else
    run_confirmation
  fi
}

# ------------SCRIPT--START------------
if [ ! -f gradlew ]; then
  echo -e ${RED}"This script needs to be ran from the root project directory!"${RESET}
  exit 1
fi

run_confirmation
echo -e ${LIGHTBLUE}"Running gradle clean task!"${RESET}
./gradlew clean

echo -e ${LIGHTBLUE}"\nCloning the Core Framework repository! \n"${RESET}
git clone "${CORE_REPOSITORY}" "${CORE_REPOSITORY_DIR}"

# delete existing cache and client/src
for dir in "${ASSET_DIR}/cache" "${CLIENT_DIR}/src" "Cache"
do
  if [ -d $dir ]; then rm -rf $dir; fi
done

# Copy newly cloned cache and client/src
mkdir -p "${CLIENT_DIR}/src/main/java" "${CLIENT_DIR}/src/main/resources" "${ASSET_DIR}/cache"
cp -r "${CLIENT_BASE_DIR}"/Cache/{video,audio} "${ASSET_DIR}/cache"
cp -r "${CLIENT_BASE_DIR}"/src/* "${CLIENT_DIR}/src/main/java"
cp -r "${PC_CLIENT_DIR}"/src/* "${CLIENT_DIR}/src/main/java"
mv "${CLIENT_DIR}"/src/main/java/res/ "${CLIENT_DIR}/src/main/resources/"
echo -e ${LIGHTBLUE}"\nCopied over newly cloned ${GREEN}CACHE${LIGHTBLUE} and ${GREEN}CLIENT/SRC${LIGHTBLUE}!"${RESET}

rm -rf ${CORE_REPOSITORY_DIR}
echo -e ${LIGHTBLUE}"\nDeleted leftover Core Framework files to save space!"${RESET}

echo -e ${LIGHTBLUE}"\nRunning gradle build task!"${RESET}
 ./gradlew build

echo -e ${LIGHTBLUE}"\nFINISHED!"${RESET}

