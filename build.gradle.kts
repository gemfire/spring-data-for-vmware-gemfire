/*
 * Copyright 2022-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

import com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask

buildscript {
  repositories {
    mavenCentral()
  }
}

plugins {
  id("java-library")
  id("commercial-repositories")
  alias(libs.plugins.versions)
  alias(libs.plugins.version.catalog.update)
  id("gemfire-artifactory")
}

// Suppress warning from gemfire-artifactory plugin. We need the module to be on this project in order to get buildInfo
// uploaded, but there is no artifact on the root project, so we skip that part.
tasks.artifactoryPublish {
  skip = true
}

allprojects {
  repositories {
    mavenCentral()
  }
}

versionCatalogUpdate {
  // These options will be set as default for all version catalogs
  sortByKey = true
  // Referenced that are pinned are not automatically updated.
  // They are also not automatically kept however (use keep for that).
  pin {
  }
  keep {
    keepUnusedVersions = true
    // keep all libraries that aren't used in the project
    keepUnusedLibraries = true
    // keep all plugins that aren't used in the project
    keepUnusedPlugins = true
  }

  versionCatalogs {
    create("publishCatalog") {
      catalogFile = file("gradle/publishing.versions.toml")
    }
  }
}

tasks.withType<DependencyUpdatesTask> {
  rejectVersionIf {
    !isPatch(candidate.version, currentVersion)
  }
}

fun isPatch(candidateVersion: String, currentVersion: String): Boolean {
  val candidateSplit = candidateVersion.split(".")
  val currentSplit = currentVersion.split(".")

  if (currentSplit.size == 3) {
    if (candidateSplit.size == currentSplit.size) {
      if (candidateSplit[0] != currentSplit[0]) {
        return false
      }
      if (candidateSplit[1] != currentSplit[1]) {
        return false
      }
      return true
    }
  } else {
    return false
  }
  return false
}

gradle.projectsEvaluated {
  project(":spring-test-vmware-gemfire").tasks["publish"].mustRunAfter(project(":spring-data-vmware-gemfire").tasks["publish"])
  project(":spring-test-vmware-gemfire").tasks["publishToMavenLocal"].mustRunAfter(project(":spring-data-vmware-gemfire").tasks["publishToMavenLocal"])
  project(":spring-test-vmware-gemfire").tasks["publishToInternalGCS"].mustRunAfter(project(":spring-data-vmware-gemfire").tasks["publishToInternalGCS"])
}
