: ${DOCKER_IMAGE_PREFIX:=black32167}

imageName() {
    local service_name="${1}"
    local version="${2}"
	echo "${DOCKER_IMAGE_PREFIX}/grapheus-${service_name}:${version}"
}
