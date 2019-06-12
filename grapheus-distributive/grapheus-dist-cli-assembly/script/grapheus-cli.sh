#!/bin/bash
#set -x

if [ ! -z "${DEBUG+x}" ]; then 
    echo grapheus_server_baseURL="${grapheus_server_baseURL}"
    JAVA_OPTS="-Xdebug -Xrunjdwp:transport=dt_socket,address=5005,server=y,suspend=n"
fi

java ${JAVA_OPTS} -cp "${BASH_SOURCE%/*}/lib/*" org.grapheus.cli.GrapheusCL "$@"
