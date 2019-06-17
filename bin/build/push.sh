#!/bin/bash

dpush() {
	local MODULE="${1}"
	local IMAGE_NAME="$(imageName ${MODULE} $(version))"
	
	echo "Pushing image '${IMAGE_NAME}' to the remote..."
	
   	docker push "${IMAGE_NAME}"
}

. ${BASH_SOURCE%/*/*}/include/log.sh rdocker.log
. ${BASH_SOURCE%/*/*}/include/docker-common.sh
. ${BASH_SOURCE%/*/*}/include/version-utils.sh

: ${VERSION:=$(version)}
set -e
set -u

dpush web
dpush back

echo "Done!"

