#!/bin/bash

# This script pushes released versions of images to docker-hub

##################################### Globals ###############################################

set -e
set -u

SCRIPTS_ROOT="${BASH_SOURCE%/*}"

. ${SCRIPTS_ROOT}/include/log.sh rdocker.log
. ${SCRIPTS_ROOT}/include/docker-common.sh
. ${SCRIPTS_ROOT}/include/version-utils.sh


##################################### Functions ###############################################

dpush() {
	local MODULE="${1}"
	local IMAGE_NAME="$(imageName ${MODULE} $(release_version))"
	
	echo "Pushing image '${IMAGE_NAME}' to the remote..."
	
   	docker push "${IMAGE_NAME}"
}

##################################### Entry point ###############################################

dpush web
dpush back

echo "Done!"

