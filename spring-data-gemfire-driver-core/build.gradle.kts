/*
 * Copyright 2022-2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

plugins {
  id("java-library")
}

java {
  toolchain { languageVersion.set(JavaLanguageVersion.of(17)) }
}

tasks.withType<JavaCompile>().configureEach {
  options.compilerArgs.add("-parameters")
}

dependencies {
  api(project(":spring-data-gemfire-driver-spi"))

  testImplementation(platform(libs.junit.jupiter.bom))
  testImplementation(libs.junit.jupiter.api)
  testImplementation(libs.assertJ)
  testRuntimeOnly("org.junit.platform:junit-platform-launcher")
  testRuntimeOnly(libs.junit.jupiter.engine)
}

tasks.withType<Test>().configureEach {
  useJUnitPlatform()
}
