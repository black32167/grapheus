#!/bin/bash

# This script can build 'release' and 'current' versions of Grapheus artifacts including:
# * Maven artifacts
# * Docker images
# * Command-line runner

##################################### Globals ###############################################
set -e
set -u

SCRIPTS_ROOT="${BASH_SOURCE%/*}"
TARGET_FOLDER="$(pwd)/target"

. ${SCRIPTS_ROOT}/include/log.sh build.log
. ${SCRIPTS_ROOT}/include/docker-common.sh
. ${SCRIPTS_ROOT}/include/version-utils.sh

VER_SPECIFIER=${1-}
CMD="${2-}"

##################################### Functions ###############################################
usage() {
    echo "Usage:"
    echo "    $0 {current|release} {docker-back|docker-web|maven|runner-zip|scanner-zip|all}"
    exit 1
}

artifact_unpack() {
    local ARTIFACT="${1}"
    local OUTPUT="${2}"
    
    echo "Unpacking ${ARTIFACT} to ${OUTPUT}..."

    mvn dependency:unpack \
        -Dartifact="${ARTIFACT}" \
        -DoutputDirectory="${OUTPUT}" >> ${LOG} || die "Could not unpack maven artifact ${ARTIFACT}"
}
artifact_copy() {
    local ARTIFACT="${1}"
    local OUTPUT="${2}"

    echo "Copying ${ARTIFACT} to ${OUTPUT}..."

    mvn dependency:copy \
        -Dartifact="${ARTIFACT}" \
        -DoutputDirectory="${OUTPUT}" >> ${LOG} || die "Could not unpack maven artifact ${ARTIFACT}"
}


dbuild() {
    local MODULE="${1}"
    local IMAGE_NAME="$(imageName ${MODULE} ${VERSION})"
	local CTX_DIR="target/${MODULE}-docker"
	
	echo "==== Building docker image ${IMAGE_NAME} ===="
	
	# Unpack CLI distributive
	artifact_unpack "grapheus:grapheus-dist-${MODULE}-assembly:${VERSION}:zip" "${CTX_DIR}/${MODULE}-dist"
	
	docker build -t ${IMAGE_NAME} -f "${BASH_SOURCE%/*}/build/${MODULE}/Dockerfile" ${CTX_DIR}
}

mbuild_release() {
    echo "========================================"
    echo "=== Building maven RELEASE artifacts ==="
    echo "========================================"

    local release_checkout_folder="${TARGET_FOLDER}/release-checkout"
    
    mkdir -p "${TARGET_FOLDER}"
    rm -rf "${release_checkout_folder}"
    
    git clone . "${release_checkout_folder}"
    cd "${release_checkout_folder}" || die "Cannot navigate to folder ${release_checkout_folder}"
    git checkout "$(latest_tag)"

    mvn clean install -DskipTests|| die "Can't build release artifacts"
}

mbuild_current() {
    echo ""
    echo "========================================"
    echo "=== Building maven CURRENT artifacts ==="
    echo "========================================"
    echo ""

    mvn clean install -DskipTests || die "Can't build current snapshot artifacts"
}

mbuild() {
    case "${VER_SPECIFIER}" in
        release) mbuild_release ;;
        current) mbuild_current ;;
        *) die "Invalid version specifier: ${VER_SPECIFIER}"
    esac
}

build_scanner_runner_zip() {
    echo ""
    echo "=================================================="
    echo "=== Building jar scanner distributable archive ==="
    echo "=================================================="
    echo ""

    local distributive_folder="grapheus-jar-scanner-${VERSION}"
    local output_path="${TARGET_FOLDER}/${distributive_folder}"
    local target_zip_path="${TARGET_FOLDER}/grapheus-jar-scanner-${VERSION}.zip"

    mkdir -p "${output_path}"
    rm -rf "${output_path}"
    rm -rf "${target_zip_path}"

    # Copy scanner's uber-jar
    artifact_copy "grapheus:grapheus-jar-scanner:${VERSION}:jar:jar-with-dependencies" "${output_path}"

    # Copy scanner runner script
    cp -r "${SCRIPTS_ROOT}/jarscanner-runner/." "${output_path}/"

    # Replace template variables
    sed -i.bak -e "s/\${VERSION}/${VERSION}/" ${output_path}/*.sh
    find "${output_path}" -name "*.bak" | xargs rm

    # zip content
    cd "${TARGET_FOLDER}"
    set -x
    zip -r "${target_zip_path}" "${distributive_folder}"

    echo "Archive created: ${target_zip_path}"
}

build_runner_zip() {
    local distributive_folder="grapheus-${VERSION}"
    local tempate_folder="${SCRIPTS_ROOT}/grapheus-runner"
    local runner_target_path="${TARGET_FOLDER}/${distributive_folder}"
    local target_zip_path="${TARGET_FOLDER}/grapheus-runner-${VERSION}.zip"
    
    # Initialize 'target' folder:
    rm -rf "${runner_target_path}"
    rm -rf "${target_zip_path}"
    mkdir -p "${runner_target_path}"
    
    # Copy templates to 'target'
    cp -r "${tempate_folder}/." "${runner_target_path}/"
 
    # Replace template variables
    sed -i.bak -e "s/\${VERSION}/${VERSION}/" "${runner_target_path}/grapheus.sh"
    find "${runner_target_path}" -name "*.bak" | xargs rm
    
    # Archiving
    cd "${TARGET_FOLDER}"
    zip -r "${target_zip_path}" "${distributive_folder}"
    
    echo "Archive created: ${target_zip_path}"
}

build_docker_web() {
    echo ""
    echo "============================================="
    echo "=== Building web application docker image ==="
    echo "============================================="
    echo ""

    dbuild web  || die "Can't build web-ui module"
}
build_docker_server() {
    echo ""
    echo "============================================"
    echo "=== Building backend server docker image ==="
    echo "============================================"
    echo ""

    dbuild back || die "Can't build backend module"
}

build_docker_all() {
    build_docker_web
    build_docker_server
}

build_all() {
    (mbuild)
    (build_docker_all)
    (build_runner_zip)
    (build_scanner_runner_zip)
}

##################################### Entry point ###############################################

case "${VER_SPECIFIER}" in
    release)
        : ${VERSION:=$(release_version)}
        ;;
    current)
        : ${VERSION:=$(current_version)}
        ;;
    *)
        usage
        ;;
esac

case "$CMD" in
    version)
        echo "${VERSION}"
        ;;
    docker)
        build_docker_all
        ;;
    docker-web)
    	build_docker_web
    	;;
    docker-back)
    	build_docker_web
    	;;
    maven)
    	mbuild
    	;;
    runner-zip)
        build_runner_zip
        ;;
    scanner-zip)
        build_scanner_runner_zip
        ;;
    all)
    	build_all
    	;;
 	*)
 	    usage
	    ;;
esac



  
