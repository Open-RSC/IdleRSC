#!/usr/bin/env bash

if [ ! -f scripts/variables.sh ]; then
  echo "This script needs to be run from the root directory!"
  exit 1
fi

source scripts/variables.sh

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

TMP_HASHDIR="$(mktemp --tmpdir --directory cache-hash.XXXXXXXXXX)"

# assets hash
pushd "${ASSET_DIR}/cache" &>/dev/null || exit 1
  ASSETS_HASH="$(hashdir)"
popd &>/dev/null || exit 1

# core hash
pushd "${TMP_HASHDIR}" &>/dev/null || exit 1
  cp -r "${CLIENT_BASE_DIR}"/Cache/{video,audio,config.txt} .
  CORE_HASH="$(hashdir)"
popd &>/dev/null || exit 1

if [ "${ASSETS_HASH}" = "${CORE_HASH}" ]; then
  echo "Cache does not need to be updated"
else
  echo "Cache needs to be updated!"
fi

# remove temporary directory on exit
trap 'rm -rf -- "${TMP_HASHDIR}"' EXIT
