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

REPODIR=$(cd geode; git rev-parse --show-toplevel)

. ${SCRIPTDIR}/shared_utilities.sh
is_source_from_pr_testable "geode" "$(get_geode_pr_exclusion_dirs)" || exit 0

SSHKEY_FILE="instance-data/sshkey"
SSH_OPTIONS="-i ${SSHKEY_FILE} -o ConnectionAttempts=60 -o StrictHostKeyChecking=no"

INSTANCE_IP_ADDRESS="$(cat instance-data/instance-ip-address)"

time rsync -e "ssh ${SSH_OPTIONS}" -ah ${REPODIR} geode@${INSTANCE_IP_ADDRESS}:.
