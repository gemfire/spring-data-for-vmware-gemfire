/*
 * Copyright (c) VMware, Inc. 2023. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package com.vmware.gemfire.publishing.extension

import org.gradle.api.provider.Property

interface ManifestExtension {
  val artifactName: Property<String>
  val longName: Property<String>
  val description: Property<String>
}