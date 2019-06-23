#!/bin/bash

: ${SCANNER_JAR="${BASH_SOURCE%/*}/grapheus-jar-scanner-${VERSION}-jar-with-dependencies.jar"}

java -jar "${SCANNER_JAR}" $@