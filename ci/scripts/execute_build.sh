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

if [[ -z "${GRADLE_TASK}" ]]; then
  echo "GRADLE_TASK must be set. exiting..."
  exit 1
fi

ROOT_DIR=$(pwd)
BUILD_DATE=$(date +%s)

DEFAULT_GRADLE_TASK_OPTIONS="--console=plain --no-daemon"
GRADLE_GLOBAL_ARGS="${GRADLE_GLOBAL_ARGS:-}"

SSHKEY_FILE="instance-data/sshkey"
SSH_OPTIONS="-i ${SSHKEY_FILE} -o ConnectionAttempts=60 -o StrictHostKeyChecking=no"

INSTANCE_IP_ADDRESS="$(cat instance-data/instance-ip-address)"

SET_JAVA_HOME="export JAVA_HOME=/usr/lib/jvm/bellsoft-java${JAVA_BUILD_VERSION}-amd64"
mkdir -p ${HOME}/.ssh
cat <<EOF > ${HOME}/.ssh/config
SendEnv ORG_GRADLE_PROJECT_mavenUser
SendEnv ORG_GRADLE_PROJECT_mavenPassword
EOF

GRADLE_COMMAND="./gradlew \
    ${DEFAULT_GRADLE_TASK_OPTIONS} \
    ${GRADLE_GLOBAL_ARGS} \
    clean build mavenCompileSDG publishToMavenLocal"

SCRIPT_COMMAND="scripts/setupSDGEnv.sh -b 2.5.x"
echo "${GRADLE_COMMAND}"
ssh ${SSH_OPTIONS} geode@${INSTANCE_IP_ADDRESS} "set -x  && mkdir -p tmp && cd spring-data-gemfire && ${SET_JAVA_HOME} && ${GRADLE_COMMAND}"
#ssh ${SSH_OPTIONS} geode@${INSTANCE_IP_ADDRESS} "set -x  && mkdir -p tmp && cd spring-data-tanzu-gemfire && ${SET_JAVA_HOME} && ${SCRIPT_COMMAND}"
