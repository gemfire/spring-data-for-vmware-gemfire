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

// The SPI's own major.minor — the ABI compatibility unit (spec §6.1). Minor bumps are additive only; a major
// bump is the only point at which anything is removed or changed.
val spiVersion = "1.0"
version = spiVersion

tasks.processResources {
  inputs.property("spiVersion", spiVersion)
  filesMatching("**/spi-version.properties") {
    expand(mapOf("spiVersion" to spiVersion))
  }
}

dependencies {
  api(platform(libs.spring.framework.bom))
  api("org.springframework:spring-tx")

  testImplementation(platform(libs.junit.jupiter.bom))
  testImplementation(libs.junit.jupiter.api)
  testImplementation(libs.assertJ)
  testRuntimeOnly("org.junit.platform:junit-platform-launcher")
  testRuntimeOnly(libs.junit.jupiter.engine)
}

tasks.withType<Test>().configureEach {
  useJUnitPlatform()
}
