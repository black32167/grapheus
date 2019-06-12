#!/bin/bash

CLI_ASSEMBLY="${BASH_SOURCE%/*/*}/grapheus-distributive/grapheus-dist-cli/target/grapheus-cli"

: ${RXSERVER_PORT:=8081}
JAVA_OPTS="-Dgrapheus.server.baseURL=http://127.0.0.1:${RXSERVER_PORT}/grapheus" \
    ${CLI_ASSEMBLY}/grapheus-cli.sh "$@"
