#!/bin/bash

#
# Copyright 2024 Broadcom. All rights reserved.
# SPDX-License-Identifier: Apache-2.0
#

rm -Rf `find . -name "BACKUPDEFAULT*"`
rm -Rf `find . -name "ConfigDiskDir*"`
rm -Rf `find . -name "locator*" | grep -v "src"`
rm -Rf `find . -name "newDB"`
rm -Rf `find . -name "server" | grep -v "src"`
rm -Rf `find . -name "*.log"`
