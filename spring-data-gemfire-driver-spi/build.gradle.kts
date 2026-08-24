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
  api(platform(libs.spring.framework.bom))
  api("org.springframework:spring-tx")
}
