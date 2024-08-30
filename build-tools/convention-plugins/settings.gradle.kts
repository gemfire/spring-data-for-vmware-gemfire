/*
 * Copyright 2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

pluginManagement {
  repositories.mavenCentral()
  repositories.gradlePluginPortal()
  repositories.google()
  repositories.maven {
    url = uri("https://repo.spring.io/plugins-release")
  }
}

plugins {
  id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}

dependencyResolutionManagement {
  versionCatalogs {
    create("libs") {
      from(files("../../gradle/publishing.versions.toml"))
    }
  }
}
