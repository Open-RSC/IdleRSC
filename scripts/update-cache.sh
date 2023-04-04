#!/usr/bin/env bash

if [ ! -f scripts/variables.sh ]; then
  echo "This script needs to be run from the root directory!"
  exit 1
fi

source scripts/variables.sh

source scripts/detect-repository.sh

# clean cache directory
rm -rf "${ASSET_DIR}/cache"
mkdir -p "${ASSET_DIR}/cache"

# copy cache from Client_Base
cp -r "${CLIENT_BASE_DIR}"/Cache/{video,audio,config.txt} "${ASSET_DIR}/cache"
