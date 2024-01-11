#!/usr/bin/env bash

# project-specific variables
export ASSET_DIR="$(pwd)/assets"
export CLIENT_DIR="$(pwd)/client"
export PATCHER_DIR="$(pwd)/patcher"

# repository-related variables
export CORE_REPOSITORY="https://gitlab.com/open-runescape-classic/core.git"
export CORE_REPOSITORY_NAME="core"
export CORE_REPOSITORY_DIR="${ASSET_DIR}/${CORE_REPOSITORY_NAME}"

# folders within the core repository
export CLIENT_BASE_DIR="${CORE_REPOSITORY_DIR}/Client_Base"
export PC_CLIENT_DIR="${CORE_REPOSITORY_DIR}/PC_Client"

# creates a deterministic hash of the current directory
hashdir() {
  find \
    . \
    -type f \
    -not -path '*/\.git/*' \
    -exec sha256sum {} + | \
    LC_ALL=C sort | \
    sha256sum | \
    cut -d ' ' -f 1
}

# checks if the core repository exists
core_repository_exists() {
  if [ ! -d "${CORE_REPOSITORY_DIR}" ]; then
    return 1
  else
    return 0
  fi
}

