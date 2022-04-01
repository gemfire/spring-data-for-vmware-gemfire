#!/usr/bin/env bash

sanitizeName() {
  echo ${1} | tr "._/" "-" | tr '[:upper:]' '[:lower:]'
}

getSanitizedBranch () {
  echo $(sanitizeName ${1}) | cut -c 1-20
}

getSanitizedFork () {
  echo $(sanitizeName ${1}) | cut -c 1-16
}

shortenJobName () {
  echo $(sanitizeName ${1}) | sed -e 's/windows/win/' -e 's/distributed/dst/' -e 's/acceptance/acc/' -e 's/openjdk/oj/' | cut -c 1-18
}