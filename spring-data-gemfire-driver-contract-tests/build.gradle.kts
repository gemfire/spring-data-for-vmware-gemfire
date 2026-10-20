/*
 * Copyright 2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
plugins {
  id("java-library")
}

java {
  toolchain { languageVersion.set(JavaLanguageVersion.of(17)) }
  withSourcesJar()
}

tasks.withType<Test>().configureEach { useJUnitPlatform() }

dependencies {
  api(project(":spring-data-gemfire-driver-spi"))
  api(project(":spring-data-gemfire-driver-core"))
  api(platform(libs.junit.jupiter.bom))
  api(libs.junit.jupiter.api)
  api(libs.assertJ)
  testRuntimeOnly(libs.junit.jupiter.engine)
  testRuntimeOnly(libs.junit.platform.launcher)
}
