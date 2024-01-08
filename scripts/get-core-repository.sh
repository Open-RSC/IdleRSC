#!/usr/bin/env bash
#
# Checks if the core repository exists, then creates/updates the repository.
#

# source necessary variables/functions
if [ ! -f scripts/variables.sh ]; then
  echo "This script needs to be run from the root directory!"
  exit 1
else
  source scripts/variables.sh
fi

# either clones or updates the repository
if [ ! -d "${CORE_REPOSITORY_DIR}" ]; then
  #
  git clone "${CORE_REPOSITORY}" "${CORE_REPOSITORY_DIR}"
else
  pushd "${CORE_REPOSITORY_DIR}" &>/dev/null || exit 1
  git pull
  popd &>/dev/null || exit 1
fi

