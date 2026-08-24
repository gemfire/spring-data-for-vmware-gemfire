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
}
