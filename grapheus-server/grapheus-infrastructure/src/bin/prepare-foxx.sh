#!/bin/bash

SRC_DIR="${1}"
ARCHIVE_FILE_PATH="${2}"

echo "Zipping ${SRC_DIR} to ${ARCHIVE_FILE_PATH}"

cd "${SRC_DIR}"
rm "${ARCHIVE_FILE_PATH}"
mkdir -p "${ARCHIVE_FILE_PATH%/*}"
zip -r "${ARCHIVE_FILE_PATH}" *

