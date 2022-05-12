#!/usr/bin/env bash

set -e

BASE_DIR=$(pwd)

SOURCE="${BASH_SOURCE[0]}"
while [ -h "$SOURCE" ]; do # resolve $SOURCE until the file is no longer a symlink
  SCRIPTDIR="$( cd -P "$( dirname "$SOURCE" )" && pwd )"
  SOURCE="$(readlink "$SOURCE")"
  [[ $SOURCE != /* ]] && SOURCE="$DIR/$SOURCE" # if $SOURCE was a relative symlink, we need to resolve it relative to the path where the symlink file was located
done
SCRIPTDIR="$( cd -P "$( dirname "$SOURCE" )" && pwd )"

INSTANCE_NAME="$(cat instance-data/instance-name)"
PERMITTED_ZONES=($(gcloud compute zones list --filter="name~'us-central.*'" --format=json | jq -r .[].name))
DELETE_INSTANCE=${DELETE_INSTANCE:-true}
echo 'StrictHostKeyChecking no' >> /etc/ssh/ssh_config

# Ensure no existing instance with this name in any zone
for KILL_ZONE in $(echo ${PERMITTED_ZONES[*]}); do
  if [[ "${DELETE_INSTANCE}" != "true" ]]; then
    echo "gcloud compute instances delete ${INSTANCE_NAME} --zone=${KILL_ZONE} --quiet &>/dev/null || true"
  else
    gcloud compute instances delete ${INSTANCE_NAME} --zone=${KILL_ZONE} --quiet &>/dev/null || true
  fi
done
