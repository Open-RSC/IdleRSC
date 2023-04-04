#!/usr/bin/env bash

if [ ! -f scripts/variables.sh ]; then
  echo "This script needs to be run from the root directory!"
  exit 1
fi

source scripts/variables.sh

if [ ! -d "${CORE_REPOSITORY_DIR}" ]; then
  echo "Core repository not found. Please run the following:"
  echo ""
  echo "git clone ${CORE_REPOSITORY} ${CORE_REPOSITORY_DIR}"
  exit 1
fi
