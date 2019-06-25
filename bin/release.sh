#!/bin/bash

##################################### Globals ###############################################
set -u

SCRIPTS_ROOT="${BASH_SOURCE%/*}"

. ${SCRIPTS_ROOT}/include/log.sh release.log
. ${SCRIPTS_ROOT}/include/version-utils.sh

CMD="${1-}"

TARGET_FOLDER="$(pwd)/target"
RELEASE_CHECKOUT_FOLDER="${TARGET_FOLDER}/release-checkout"

##################################### Functions ###############################################

# Checks out latest release tag into separate folder
get_release() {
    mkdir -p "${TARGET_FOLDER}"
    rm -rf "${RELEASE_CHECKOUT_FOLDER}"
    
    git clone . "${RELEASE_CHECKOUT_FOLDER}"

    (
        cd "${RELEASE_CHECKOUT_FOLDER}" || die "Cannot navigate to folder ${RELEASE_CHECKOUT_FOLDER}"
        git checkout "$(latest_tag)"
    )
    
    echo "Release is checked out in '${RELEASE_CHECKOUT_FOLDER}'"
}

# Performs release:prepare
perform_prepare() {
    local last_release_version=$(release_version)
    read -p "New version (last released was ${last_release_version}): " new_version
    echo "Preparing new release v ${new_version}"

    mvn clean -B \
        -Darguments=-DskipTests \
        -DreleaseVersion="${new_version}" \
        release:prepare || die "Could not prepare release"
    
    get_release
}

case "${CMD}" in
    get) get_release ;;
    perform) perform_prepare ;;
    *)
        echo "Usage:"
        echo "  $0 {get|perform}"
esac