#!/usr/bin/env bash

set -ex

BASE_DIR=$(pwd)

SOURCE="${BASH_SOURCE[0]}"
while [ -h "$SOURCE" ]; do # resolve $SOURCE until the file is no longer a symlink
  SCRIPTDIR="$( cd -P "$( dirname "$SOURCE" )" && pwd )"
  SOURCE="$(readlink "$SOURCE")"
  [[ $SOURCE != /* ]] && SOURCE="$DIR/$SOURCE" # if $SOURCE was a relative symlink, we need to resolve it relative to the path where the symlink file was located
done
SCRIPTDIR="$( cd -P "$( dirname "$SOURCE" )" && pwd )"

SSHKEY_FILE="instance-data/sshkey"

test -e ${SSHKEY_FILE}
SSH_OPTIONS="-i ${SSHKEY_FILE} -o ConnectTimeout=5 -o ConnectionAttempts=60 -o StrictHostKeyChecking=no"

INSTANCE_IP_ADDRESS="$(cat instance-data/instance-ip-address)"

OUTPUT_DIR=${BASE_DIR}/spring-data-gemfire-results

case $ARTIFACT_SLUG in
  windows*)
    JAVA_BUILD_PATH=C:/java8
    del=";"
    ;;
  *)
    JAVA_BUILD_PATH=/usr/lib/jvm/bellsoft-java${JAVA_BUILD_VERSION}-amd64
    del=";"
    ;;
esac

EXEC_COMMAND="bash -c 'export JAVA_HOME=${JAVA_BUILD_PATH} \
  ${del} cd spring-data-gemfire \
  ${del} ./gradlew --no-daemon combineReports \
  ${del} ./gradlew --stop \
  ${del} cd .. \
  ${del} rm -rf .gradle/caches .gradle/wrapper'"

time ssh ${SSH_OPTIONS} geode@${INSTANCE_IP_ADDRESS} "${EXEC_COMMAND}"

time ssh ${SSH_OPTIONS} "geode@${INSTANCE_IP_ADDRESS}" tar -czf - spring-data-gemfire .gradle | tar -C "${OUTPUT_DIR}" -zxf -

mv "${OUTPUT_DIR}/.gradle" "${OUTPUT_DIR}/spring-data-tanzu-gemfire/.gradle_logs"

set +x
