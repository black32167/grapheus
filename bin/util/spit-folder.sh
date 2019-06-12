#!/bin/bash 
set -u

if [[ $# < 1 ]]; then
    echo "Usage:"
    echo "    ${BASH_SOURCE} <folder_to_split>"
    exit 1
fi

FOLDER_TO_SPLIT="${1}"
FILES_PER_FOLDER="3000"

i=0; 
for f in ${FOLDER_TO_SPLIT}/*; 
do
    echo "Processing ${f}"
    DEST_FOLDER="${FOLDER_TO_SPLIT}_split/$((i/${FILES_PER_FOLDER}+1))"; 
    mkdir -p "${DEST_FOLDER}";
    cp "$f" "${DEST_FOLDER}";
    let i++;
done