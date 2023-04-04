#!/usr/bin/env bash

if [ ! -f scripts/variables.sh ]; then
  echo "This script needs to be run from the root directory!"
  exit 1
fi

source scripts/variables.sh

source scripts/detect-repository.sh

# clean client directory
rm -rf "${CLIENT_DIR:?}/src"
mkdir -p "${CLIENT_DIR}"/src/main/{java,resources}

# copy source from Client_Base
cp -r "${CLIENT_BASE_DIR}"/src/* "${CLIENT_DIR}/src/main/java"
mv "${CLIENT_DIR}/src/main/"{java/res,resources}

# copy source from PC_Client
cp -r "${PC_CLIENT_DIR}"/src/* "${CLIENT_DIR}/src/main/java"
