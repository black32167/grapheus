#!/bin/bash

run() {
    set -x
    docker run --rm -it \
        -e grapheus_server_baseURL="${JBACKEND_URL}" \
        -e grapheus_name="${grapheus_name}" \
        -e grapheus_password_base64="${grapheus_password_base64}" \
        ${DEBUG_OPT} \
        ${VOLUME_OPT} \
        ${RX_CLI_IMG} "$@"
}
upload() {
    DATA_FOLDER="${1}"
    shift
    VOLUME_OPT="-v ${DATA_FOLDER}:/tmp/grapheus"
    run upload -p /tmp/grapheus "$@"
}

DEBUG_OPT=""
if [ ! -z "${DEBUG+x}" ]; then
    DEBUG_OPT="-e DEBUG=1 -p 5005:5005"
fi
set -u
: ${RX_CLI_IMG:=rxcli}
: ${VOLUME_OPT:=}
: ${JBACKEND_URL:=http://localhost:8080/grapheus}
: ${grapheus_name:=user}
: ${grapheus_password_base64:=cGFzc3dvcmQ=}

if [[ "${1}" == "upload" ]]; then
    shift
    upload "$@"
else
    run "$@"
fi

