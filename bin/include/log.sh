#!/bin/bash

log_setup() {
    LOG="./logs/${1}"
    
    rm -rf "${LOG}" > /dev/null
    mkdir -p $(dirname "${LOG}")
    echo "Log file: ${LOG}"
}

log_setup "${1}"

die() {
	echo "[ERROR] ${1}"
	echo "See '${LOG}' for details"
	exit 1
}