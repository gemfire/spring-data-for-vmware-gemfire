/*
 * Copyright 2024-2025 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

plugins {
  id("groovy-gradle-plugin")
  `kotlin-dsl`
}

repositories {
  gradlePluginPortal()
}

dependencies {
  implementation(libs.kotlin)
  implementation(gradleApi())
  implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:2.1.10")
  implementation("org.jfrog.buildinfo:build-info-extractor-gradle:5.2.2")
}

gradlePlugin {
  plugins.register("gemfire-artifactory") {
    id = "gemfire-artifactory"
    implementationClass = "com.vmware.gemfire.gradle.ArtifactoryPlugin"
  }
}
