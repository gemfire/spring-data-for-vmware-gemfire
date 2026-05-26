/*
 * Copyright 2022-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

/*
 * @AI-Generated
 * Generated in whole or in part by Cursor
 * Description:
 * 2026-05-23: Removed gemfireVersion from keep.versions so vCU can detect and apply GemFire patch updates
 * 2026-05-23: Expanded isAllowedUpdate to handle 4-part versions (A.B.C.D) and broader non-stable exclusions
 * 2026-05-26: Wired isAllowedUpdate into versionCatalogUpdate.versionSelector so vCU direct resolution is also filtered
 * 2026-05-26: Extracted addGemFireRepositories() helper; allprojects.repositories delegates to it
 */

import nl.littlerobots.vcu.plugin.versionSelector

buildscript {
  repositories {
    val repositoryConfigFilePath = providers.gradleProperty("spring.gemfire.repositories").getOrElse(
      providers.environmentVariable("HOME").get() + "/.gradle/gradleRepositories.json"
    )

    val jsonString = File(repositoryConfigFilePath).readText(Charsets.UTF_8)
    val repositories = groovy.json.JsonSlurper().parseText(jsonString) as Map<*, *>
    (repositories["repositories"] as List<*>).filterNotNull().map { entry -> entry as Map<*, *> }
      .forEach { entry ->
        entry.apply {
          maven {
            url = uri(entry["url"]!! as String)
            if (!entry["username"]?.toString().isNullOrBlank()) {
              credentials {
                username = entry["username"] as String
                password = entry["password"] as String
              }
            }
          }
        }
      }

    if (providers.gradleProperty("useMavenCentral").getOrElse("false").toBoolean()) {
      mavenCentral()
    }
  }
}

plugins {
  id("java-library")
  id("commercial-repositories")
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
    addGemFireRepositories(
      providers,
      addMavenCentral = providers.gradleProperty("useMavenCentral").getOrElse("false").toBoolean()
    )
  }
}

versionCatalogUpdate {
  // These options will be set as default for all version catalogs
  sortByKey = true
  // Referenced versions that are pinned are not automatically updated.
  // They are also not automatically kept however (use keep for that).
  pin {
  }

  keep {
    keepUnusedVersions = true
  }

  // vCU v1.x resolves catalog entries directly via its own detached configurations,
  // independently of DependencyUpdatesTask. Without this selector the rejectVersionIf
  // filter above is bypassed for that second resolution path (e.g. GemFire compileOnly
  // deps that only appear in subprojects). Mirror the same logic here so both paths
  // apply isAllowedUpdate consistently.
  versionSelector { 
    val allowMajor = project.hasProperty("updateMajor")
    val allowMinor = project.hasProperty("updateMinor")
    isAllowedUpdate(it.candidate.version, it.currentVersion, allowMajor, allowMinor)
  }

  versionCatalogs {
    create("publishCatalog") {
      catalogFile = file("gradle/publishing.versions.toml")
    }
  }
}

allprojects {
}

fun isAllowedUpdate(
  candidateVersion: String,
  currentVersion: String,
  allowMajor: Boolean,
  allowMinor: Boolean
): Boolean {
  // Exclude non-stable / pre-release candidates.
  val nonStableMarkers = listOf("alpha", "beta", "rc", "snapshot", "dev", "preview", "build", "milestone")
  if (nonStableMarkers.any { candidateVersion.contains(it, ignoreCase = true) }) {
    return false
  }
  // Also catch milestone shorthand like 4.0.0.M1 or 6.0.0-M2.
  if (candidateVersion.contains(Regex("""[.\-][Mm]\d"""))) {
    return false
  }

  // Normalize Gradle version ranges (e.g., "[10.2,10.3)" -> "10.2").
  val cleanCurrentVersion = if (currentVersion.startsWith("[") || currentVersion.startsWith("(")) {
    currentVersion
      .replace("[", "")
      .replace("]", "")
      .replace("(", "")
      .replace(")", "")
      .split(",")
      .first()
      .trim()
  } else {
    currentVersion
  }

  if (allowMajor) return true

  // Extract major and minor from a dot-separated version string.
  // Parsing stops at the first non-numeric segment (e.g. ".RELEASE" is ignored).
  // Returns null if major or minor cannot be determined.
  fun parseMajorMinor(v: String): Pair<Int, Int>? {
    val parts = v.split(".")
    val major = parts.getOrNull(0)?.takeWhile { it.isDigit() }?.toIntOrNull() ?: return null
    val minor = parts.getOrNull(1)?.takeWhile { it.isDigit() }?.toIntOrNull() ?: return null
    return major to minor
  }

  val (currentMajor, currentMinor) = parseMajorMinor(cleanCurrentVersion) ?: return false
  val (candidateMajor, candidateMinor) = parseMajorMinor(candidateVersion) ?: return false

  // Major must always match.
  if (currentMajor != candidateMajor) return false

  if (allowMinor) return true

  // The lock boundary in patch mode is major.minor.
  // Anything to the right — 3rd component, 4th component, or changes in component count
  // (e.g. 10.17.0 -> 10.17.1.0, 4.0.6 -> 4.0.6.1, 4.0.6.1 -> 4.0.7) — is a patch/hotfix
  // update and is allowed as long as major and minor are unchanged.
  return currentMinor == candidateMinor
}

gradle.projectsEvaluated {
  project(":spring-data-vmware-gemfire").tasks["publishToMavenLocal"].mustRunAfter(project(":spring-test-vmware-gemfire").tasks["publishToMavenLocal"])
  project(":spring-data-vmware-gemfire").tasks.findByName("publishMavenPublicationToGCSRepository")?.let {
    project(":spring-data-vmware-gemfire").tasks["publishMavenPublicationToGCSRepository"].mustRunAfter(project(":spring-test-vmware-gemfire").tasks["publishMavenPublicationToGCSRepository"])
  }
}

fun RepositoryHandler.addGemFireRepositories(
  providers: ProviderFactory,
  addGradlePluginPortal: Boolean = false,
  addMavenCentral: Boolean = false
) {
  val configFilePath = providers.gradleProperty("spring.gemfire.repositories").getOrElse(
    providers.environmentVariable("HOME").get() + "/.gradle/gradleRepositories.json"
  )
  val jsonString = File(configFilePath).readText(Charsets.UTF_8)
  val repos = groovy.json.JsonSlurper().parseText(jsonString) as Map<*, *>
  (repos["repositories"] as List<*>).filterNotNull().map { it as Map<*, *> }
    .forEach { entry ->
      maven {
        url = uri(entry["url"]!! as String)
        if (!entry["username"]?.toString().isNullOrBlank()) {
          credentials {
            username = entry["username"] as String
            password = entry["password"] as String
          }
        }
      }
    }
  if (addGradlePluginPortal) gradlePluginPortal()
  if (addMavenCentral) mavenCentral()
}
