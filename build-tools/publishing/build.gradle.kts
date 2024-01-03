// Copyright (c) VMware, Inc. 2022. All rights reserved.
// SPDX-License-Identifier: Apache-2.0
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
  implementation(libs.rat)
  implementation(libs.gradle.versions.plugin)
  implementation(libs.sonarqube)
  implementation(libs.japicmp)
  implementation(libs.spotless)
}