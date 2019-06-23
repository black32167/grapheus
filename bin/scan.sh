#!/bin/bash

export SCANNER_JAR="${HOME}/.m2/repository/grapheus/grapheus-jar-scanner/1.0.1-SNAPSHOT/grapheus-jar-scanner-1.0.1-SNAPSHOT-jar-with-dependencies.jar"

SCRIPTS_ROOT="${BASH_SOURCE%/*}"

${SCRIPTS_ROOT}/jarscanner-runner/scan.sh $@