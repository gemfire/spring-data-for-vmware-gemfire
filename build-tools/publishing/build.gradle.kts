/*
 * Copyright 2022-2024 Broadcom. All rights reserved.
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
  implementation("org.jfrog.buildinfo:build-info-extractor-gradle:5.2.2")
}
