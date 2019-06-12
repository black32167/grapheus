#!/bin/bash

. ${BASH_SOURCE%/*/*}/include/log.sh publish-docker-tools.log

set -u

: ${RX_DT_VERSION:=2.1}
BUILD_DIR="$(pwd)/target/docker-tools"
ARCHIVE_NAME="docker-tools.zip"
ARTIFACT_ID="grapheus-docker-tools"
: ${GROUP_ID:=grapheus}

echo "Publishing artifact '${GROUP_ID}:${ARTIFACT_ID}:${RX_DT_VERSION}:zip'"
echo "Repo URL: ${MAVEN_REPO_URL}"
echo "Repo Id: ${REPO_ID}"

# Zip tools
echo "Archiving..."
rm -rf "${BUILD_DIR}"
mkdir -p "${BUILD_DIR}"
(cd ${BASH_SOURCE%/*/*}/docker && zip -r "${BUILD_DIR}/${ARCHIVE_NAME}" .) >> "${LOG}"

# Publish tools
echo "Deploying..."
mvn deploy:deploy-file -pl . -Durl="${MAVEN_REPO_URL}" \
                               -DrepositoryId=${REPO_ID} \
                               -Dfile="${BUILD_DIR}/${ARCHIVE_NAME}" \
                               -DgroupId="${GROUP_ID}" \
                               -DartifactId="${ARTIFACT_ID}" \
                               -Dversion="${RX_DT_VERSION}" \
                               -Dpackaging=zip \
                               -DgeneratePom=true >> "${LOG}"
if (( $? != 0 )); then
    echo -e "\nvvvvvvvvvvvvvv ERROR vvvvvvvvvvvvv\n"
    tail -n100 "${LOG}"
    echo -e "\n^^^^^^^^^^^^^^ ERROR ^^^^^^^^^^^^^"
else
    echo "Done!"
fi
                               
