: ${DOCKER_IMAGE_PREFIX:=black32167}
imageName() {
	echo "${DOCKER_IMAGE_PREFIX}/grapheus-${1}:latest"
}
