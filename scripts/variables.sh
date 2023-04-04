#!/usr/bin/env bash

# project-specific variables
export ASSET_DIR="$(pwd)/assets"
export CLIENT_DIR="$(pwd)/client"

# repository-related variables
export CORE_REPOSITORY="https://gitlab.com/open-runescape-classic/core.git"
export CORE_REPOSITORY_NAME="core"
export CORE_REPOSITORY_DIR="${ASSET_DIR}/${CORE_REPOSITORY_NAME}"

# folders within the core repository
export CLIENT_BASE_DIR="${CORE_REPOSITORY_DIR}/Client_Base"
export PC_CLIENT_DIR="${CORE_REPOSITORY_DIR}/PC_Client"

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
