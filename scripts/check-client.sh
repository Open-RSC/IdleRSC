#!/usr/bin/env bash

if [ ! -f scripts/variables.sh ]; then
  echo "This script needs to be run from the root directory!"
  exit 1
fi

source scripts/variables.sh

TMP_HASHDIR="$(mktemp --tmpdir --directory client-hash.XXXXXXXXXX)"

# client hash
pushd "${CLIENT_DIR}/src" &>/dev/null || exit 1
  CLIENT_HASH="$(hashdir)"
popd &>/dev/null || exit 1

# core hash
pushd "${TMP_HASHDIR}" &>/dev/null || exit 1
  mkdir -p src/main/{java,resources}
  cp -r "${CLIENT_BASE_DIR:?}"/src/* src/main/java
  mv src/main/{java/res,resources}
  cp -r "${PC_CLIENT_DIR:?}"/src/* src/main/java
  cd src
  CORE_HASH="$(hashdir)"
popd &>/dev/null || exit 1

if [ "${CLIENT_HASH}" = "${CORE_HASH}" ]; then
  echo "Client does not need to be updated!"
else
  echo "Updating client…"

  # clean client directory
  rm -rf "${CLIENT_DIR:?}/src"

  # update client
  mv "${TMP_HASHDIR}/src" "${CLIENT_DIR}/src"
fi

# remove temporary directory on exit
trap 'rm -rf -- "${TMP_HASHDIR}"' EXIT

