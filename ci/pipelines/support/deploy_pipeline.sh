#!/usr/bin/env bash


SOURCE="${BASH_SOURCE[0]}"
SCRIPTDIR="$( cd -P "$( dirname "$SOURCE" )" && pwd )"

for cmd in Jinja2 PyYAML yq; do
  if ! [[ $(pip3 list |grep ${cmd}) ]]; then
    echo "${cmd} must be installed for pipeline deployment to work."
    echo " 'pip3 install ${cmd}'"
    echo ""
    exit 1
  fi
done

META_PROPERTIES=${SCRIPTDIR}/meta.properties
LOCAL_META_PROPERTIES=${SCRIPTDIR}/meta.properties.local

## Load default properties
source ${META_PROPERTIES}
echo "**************************************************"
echo "Default Environment variables for this deployment:"
cat ${SCRIPTDIR}/meta.properties | grep -v "^#"
source ${META_PROPERTIES}

## Load local overrides properties file
if [[ -f ${LOCAL_META_PROPERTIES} ]]; then
  echo "Local Environment overrides for this deployment:"
  cat ${SCRIPTDIR}/meta.properties.local
  source ${LOCAL_META_PROPERTIES}
  echo "**************************************************"
fi

read -n 1 -s -r -p "Press any key to continue or x to abort" DEPLOY
echo
if [[ "${DEPLOY}" == "x" ]]; then
  echo "x pressed, aborting deploy."
  exit 0
fi
set -e
set -x

FLY=${FLY:-$(command -v fly)}
if [ -z "${FLY}" ]; then
  echo "Concourse 'fly' not found. Download the version-specific version at:"
  echo "  http://${CONCOURSE_HOST}"
  exit 1
fi

GEODE_DIR=$(realpath ${SCRIPTDIR}/../../../../geode)

if [ ! -d ${GEODE_DIR} ]; then
  echo "Deploy pipeline depends on render.py from Geode CI."
  echo "  Checkout geode parallel to this repo"
  echo "  ie: ${GEODE_DIR}"
  exit 1
fi

CONCOURSE_URL="https://${CONCOURSE_HOST}"

GEODE_SUPPORT_FORK=$(yq -r .repositories.geodeSupport.fork \
  ${SCRIPTDIR}/../shared/jinja.variables.yml)

GEODE_SUPPORT_REPO=$(yq -r .repositories.geodeSupport.repo \
  ${SCRIPTDIR}/../shared/jinja.variables.yml)

GEODE_SUPPORT_BRANCH=$(yq -r .repositories.geodeSupport.branch \
  ${SCRIPTDIR}/../shared/jinja.variables.yml)

GEMFIRE_BRANCH=$(yq -r .repositories.gemfireAssembly.branch \
  ${SCRIPTDIR}/../shared/jinja.variables.yml)

. ${GEODE_DIR}/ci/pipelines/shared/utilities.sh
SANITIZED_GEODE_SUPPORT_FORK=$(sanitizeName ${GEODE_SUPPORT_FORK})
SANITIZED_GEODE_SUPPORT_REPO=$(sanitizeName ${GEODE_SUPPORT_REPO})
SANITIZED_GEODE_SUPPORT_BRANCH=$(getSanitizedBranch ${GEODE_SUPPORT_BRANCH})
PIPELINE_NAME=${SANITIZED_GEODE_SUPPORT_REPO}-${SANITIZED_GEODE_SUPPORT_BRANCH}-sync-publish

SANITIZED_GEODE_BRANCH=$(getSanitizedBranch ${GEODE_BRANCH})
SANITIZED_GEMFIRE_BRANCH=$(getSanitizedBranch ${GEMFIRE_BRANCH})

pushd ${SCRIPTDIR} 2>&1 > /dev/null
  python3 ${GEODE_DIR}/ci/pipelines/render.py ${SCRIPTDIR}/support_sync_and_publish.yml \
    --variable-file ${SCRIPTDIR}/../shared/jinja.variables.yml \
    --environment ${SCRIPTDIR}/../shared \
    --output ${SCRIPTDIR}/generated-pipeline.yml || exit 1

  grep -n . ${SCRIPTDIR}/generated-pipeline.yml

  ${FLY} -t ${CONCOURSE_HOST}-${CONCOURSE_TEAM} status ||
    ${FLY} -t ${CONCOURSE_HOST}-${CONCOURSE_TEAM} login \
      --team-name=${CONCOURSE_TEAM} \
      --concourse-url=${CONCOURSE_URL}

  ${FLY} -t ${CONCOURSE_HOST}-${CONCOURSE_TEAM} set-pipeline \
    --pipeline ${PIPELINE_NAME} \
    --config ${SCRIPTDIR}/generated-pipeline.yml \
    --var sanitized-geode-support-branch=${SANITIZED_GEODE_SUPPORT_BRANCH} \
    --var sanitized-gemfire-branch=${SANITIZED_GEMFIRE_BRANCH} \
    --var concourse-team=${CONCOURSE_TEAM} \
    --var concourse-url=${CONCOURSE_URL} \
    --var gcp-project=${GCP_PROJECT} \
    --var geode-branch=${GEODE_BRANCH} \
    --var sanitized-geode-branch=${SANITIZED_GEODE_BRANCH} \
    --var support-branch=${GEODE_SUPPORT_BRANCH} \
    --var geode-artifact-bucket=${GEODE_ARTIFACT_BUCKET}
popd 2>&1 > /dev/null

