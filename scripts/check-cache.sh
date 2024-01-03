#!/usr/bin/env bash
#
# Checks if the client cache needs updating or not.
#

# source necessary variables/functions
if [ ! -f scripts/variables.sh ]; then
  echo "This script needs to be run from the root directory!"
  exit 1
else
  source scripts/variables.sh
fi

# check if core repository exists before doing anything
if ! core_repository_exists; then
  echo "Core repository not found. Run 'make update-core'."
  exit 1
fi

# create temporary directory to store client files for comparison
TMP_HASHDIR="$(mktemp --tmpdir --directory cache-hash.XXXXXXXXXX)"

# assets hash
pushd "${ASSET_DIR}/cache" &>/dev/null || exit 1
  ASSETS_HASH="$(hashdir)"
popd &>/dev/null || exit 1

# core hash
pushd "${TMP_HASHDIR}" &>/dev/null || exit 1
  cp -r "${CLIENT_BASE_DIR}"/Cache/{video,audio} .
  CORE_HASH="$(hashdir)"
popd &>/dev/null || exit 1

# if the hashes match, no updating is necessary
if [ "${ASSETS_HASH}" = "${CORE_HASH}" ]; then
  echo "Cache does not need to be updated!"
else
  echo "Updating cache…"
  # clean cache directory
  rm -rf "${ASSET_DIR}/cache"

  # update cache
  mv "${TMP_HASHDIR}" "${ASSET_DIR}/cache"
fi

# remove temporary directory on exit
trap 'rm -rf -- "${TMP_HASHDIR}"' EXIT
