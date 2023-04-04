#!/usr/bin/env bash

if [ ! -f scripts/variables.sh ]; then
  echo "This script needs to be run from the root directory!"
  exit 1
fi

source scripts/variables.sh

if [ ! -d "${CORE_REPOSITORY_DIR}" ]; then
  git clone "${CORE_REPOSITORY}" "${CORE_REPOSITORY_DIR}"
else
  # cd and update
  pushd "${CORE_REPOSITORY_DIR}" &>/dev/null || exit 1
  git pull
  popd &>/dev/null || exit 1
fi
