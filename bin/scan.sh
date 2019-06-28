#!/bin/bash

SCRIPTS_ROOT="${BASH_SOURCE%/*}"
. ${SCRIPTS_ROOT}/include/version-utils.sh

: ${VERSION:=$(current_version)}

export SCANNER_JAR="${HOME}/.m2/repository/grapheus/grapheus-jar-scanner/${VERSION}/grapheus-jar-scanner-${VERSION}-jar-with-dependencies.jar"

SCRIPTS_ROOT="${BASH_SOURCE%/*}"

${SCRIPTS_ROOT}/jarscanner-runner/scan.sh $@
