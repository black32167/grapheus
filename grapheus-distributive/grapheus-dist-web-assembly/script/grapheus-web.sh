#!/bin/bash
set -x
cd $(dirname $0)

echo JAVA_OPTS="${JAVA_OPTS}"

java ${JAVA_OPTS} -cp "./lib/*" org.grapheus.web.GrapheusWebAppRunner
