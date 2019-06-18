#!/bin/bash

# This script can build 'release' and 'current' versions of Grapheus artifacts including:
# * Maven artifacts
# * Docker images
# * Command-line runner

##################################### Globals ###############################################
set -e
set -u

SCRIPTS_ROOT="${BASH_SOURCE%/*}"

. ${SCRIPTS_ROOT}/include/log.sh build.log
. ${SCRIPTS_ROOT}/include/docker-common.sh
. ${SCRIPTS_ROOT}/include/version-utils.sh

VER_SPECIFIER=${1-}
CMD="${2-}"

##################################### Functions ###############################################
usage() {
    echo "Usage:"
    echo "    $0 {current|release} {docker-back|docker-web|maven|runner-zip|all}"
    exit 1
}
artifact_unpack() {
    local ARTIFACT="${1}"
    local OUTPUT="${2}"
    
    echo "Unpacking ${ARTIFACT} to ${OUTPUT}..."
    
    mvn dependency:unpack -pl . \
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

    local target_folder="$(pwd)/target"
    local release_checkout_folder="${target_folder}/release-checkout"
    
    mkdir -p "${target_folder}"
    rm -rf "${release_checkout_folder}"
    
    git clone . "${release_checkout_folder}"
    cd "${release_checkout_folder}" || die "Cannot navigate to folder ${release_checkout_folder}"
    git checkout "$(latest_tag)"

    mvn clean install -DskipTests|| die "Can't build release artifacts"
}

mbuild_current() {
    mvn clean install -DskipTests || die "Can't build current snapshot artifacts"
}

mbuild() {
    case "${VER_SPECIFIER}" in
        release) mbuild_release ;;
        current) mbuild_current ;;
        *) die "Invalid version specifier: ${VER_SPECIFIER}"
    esac
}

build_runner_zip() {
    local grapheus_terminal_folder="grapheus-${VERSION}"
    local tempate_folder="${SCRIPTS_ROOT}/runner"
    local target_folder="$(pwd)/target/grapheus-runner"
    local destination="${target_folder}/grapheus-runner-${VERSION}.zip"
    
    # Initialize 'target' folder:
    rm -rf "${target_folder}"
    mkdir -p "${target_folder}"
    
    # Copy templates to 'target'
    cp -r "${tempate_folder}" "${target_folder}/${grapheus_terminal_folder}"
 
    # Replace template variables
    sed -i.bak -e "s/\${VERSION}/${VERSION}/" "${target_folder}/${grapheus_terminal_folder}/docker-compose"
    find "${target_folder}" -name "*.bak" | xargs rm
    
    # Archiving
    cd "${target_folder}"
    zip -r "${destination}" "${grapheus_terminal_folder}" 
    
    echo "Archive created: ${destination}"
}

build_docker_web() {
    dbuild web  || die "Can't build web-ui module"
}
build_docker_server() {
    dbuild back || die "Can't build backend module"
}

build_docker_all() {
    build_docker_web
    build_docker_server
}

build_all() {
    mbuild
    build_docker_all
    build_runner_zip
}

##################################### Entry point ###############################################
rm -rf "./target"

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
    all)
    	build_all
    	;;
 	*)
 	    usage
	    ;;
esac



  
