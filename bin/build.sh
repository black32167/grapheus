#!/bin/bash

artifact_unpack() {
    ARTIFACT="${1}"
    OUTPUT="${2}"
    
    echo "Unpacking ${ARTIFACT} to ${OUTPUT}..."
    
    mvn dependency:unpack -pl . \
        -Dartifact="${ARTIFACT}" \
        -DoutputDirectory="${OUTPUT}" >> ${LOG}
}

dbuild() {
    local MODULE="${1}"
    local IMAGE_NAME="$(imageName ${MODULE})"
	local CTX_DIR="target/${MODULE}-docker"
	
	echo "==== Building docker image ${IMAGE_NAME} ===="
	
	# Unpack CLI distributive
	artifact_unpack "grapheus:grapheus-dist-${MODULE}-assembly:${VERSION}:zip" "${CTX_DIR}/${MODULE}-dist"
	
	docker build -t ${IMAGE_NAME} -f "${BASH_SOURCE%/*}/build/${MODULE}/Dockerfile" ${CTX_DIR}
}

mbuild() {
	mvn clean install -DskipTests
}

build_runner_zip() {
    local target_folder="$(pwd)/target"
    rm -rf "${target_folder}"
    mkdir -p "${target_folder}"
    
    cd ${BASH_SOURCE%/*}
    zip -r "${target_folder}/grapheus-runner.zip" grapheus 
}

. ${BASH_SOURCE%/*}/include/log.sh build.log
. ${BASH_SOURCE%/*}/include/docker-common.sh

set -e
set -u

: ${VERSION:=1.6-SNAPSHOT}
MODULE="${1-}"

rm -rf "./target"

case "$MODULE" in
    docker-web)
    	dbuild web || die "Can't build module web"
    	;;
    docker-back)
    	dbuild back || die "Can't build module back"
    	;;
    maven-artifacts)
    	mbuild
    	;;
    runner-zip)
        build_runner_zip
        ;;
    all)
    	mbuild
    	dbuild web || die "Can't build module web"
    	dbuild back || die "Can't build module back"
    	;;
 	*)
 	    echo "Usage:"
	    echo "    $0 {docker-back|docker-web|maven-artifacts|runner-zip|all}"
	    exit 1
	    ;;
esac



  
