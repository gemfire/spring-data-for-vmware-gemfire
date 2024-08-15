/*
 * Copyright 2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

plugins {
  id("groovy-gradle-plugin")
  `kotlin-dsl`
}

repositories {
  mavenCentral()
  gradlePluginPortal()
}

dependencies {
  implementation(libs.kotlin)
  implementation(gradleApi())
  implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:2.0.10")
  implementation("org.jfrog.buildinfo:build-info-extractor-gradle:5.2.2")
}

gradlePlugin {
  plugins.register("gemfire-artifactory2") {
    id = "gemfire-artifactory2"
    implementationClass = "com.vmware.gemfire.gradle.ArtifactoryPlugin"
  }
}
