#!/usr/bin/env bash

#
# Copyright (c) VMware, Inc. 2022. All rights reserved.
# SPDX-License-Identifier: Apache-2.0
#

#if anything errors, bail.
set -e

sdgPath="spring-data-geode"

Clone() {
  # Clone SDG repo
  rm -rf "$sdgPath"
  git clone https://github.com/spring-projects/spring-data-geode.git $sdgPath
}

Checkout() {
  # Checkout correct branch
  cd "$sdgPath" || exit
  git checkout "$branch"
}

CopyTestResources() {
  rm -rf $projectDir"/src/sdg-test-read-only"
  mkdir -p $projectDir"/src"
  mkdir -p "$sdgPath"
  cp -R $sdgPath"/spring-data-geode/src/test/" $projectDir"/src/sdg-test-read-only"
}

while [[ $# -gt 0 ]]; do
  case $1 in
  -l)
    sdgPath="$2"
    shift # past argument
    shift # past value
    ;;
  -b)
    branch="$2"
    shift # past argument
    shift # past value
    ;;
  -t)
    projectDir="$2"
    shift # past argument
    shift # past value
    ;;
  -h)
    Help
    exit
    ;;
  -* | --*)
    echo "Unknown option $1"
    exit 1
    ;;
  esac
done

Clone
Checkout
CopyTestResources
