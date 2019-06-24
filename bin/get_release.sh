#!/bin/bash

set -eu

# This scripts checks out release version

SCRIPTS_ROOT="${BASH_SOURCE%/*}"

. ${SCRIPTS_ROOT}/include/version-utils.sh

TARGET_FOLDER="$(pwd)/target"
RELEASE_CHECKOUT_FOLDER="${TARGET_FOLDER}/release-checkout"

mkdir -p "${TARGET_FOLDER}"
rm -rf "${RELEASE_CHECKOUT_FOLDER}"

git clone . "${RELEASE_CHECKOUT_FOLDER}"

cd "${RELEASE_CHECKOUT_FOLDER}" || die "Cannot navigate to folder ${RELEASE_CHECKOUT_FOLDER}"
git checkout "$(latest_tag)"

echo "Release is checked out in '${RELEASE_CHECKOUT_FOLDER}'"