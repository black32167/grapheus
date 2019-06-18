#!/bin/bash

# This script enables conveniently run the Grapheus from within root folder of system working copy

##################################### Globals ###############################################
set -e
set -u

SCRIPTS_ROOT="${BASH_SOURCE%/*}"

. ${SCRIPTS_ROOT}/include/version-utils.sh

VERSION=$(current_version) ${SCRIPTS_ROOT}/runner/grapheus.sh $@