#!/bin/bash

log_setup() {
    LOG="$(pwd)/logs/${1}"
    
    rm -rf "${LOG}" > /dev/null
    mkdir -p $(dirname "${LOG}")
    #echo "Log file: ${LOG}"
}

die() {
	echo "[ERROR] ${1}"
	echo "See '${LOG}' for details"
	exit 1
}

log_setup "${1}"