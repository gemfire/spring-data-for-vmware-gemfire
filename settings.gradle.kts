/*
 * Copyright 2024-2025 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

pluginManagement {
  includeBuild("build-tools/publishing")
  includeBuild("build-tools/convention-plugins")
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
      gradlePluginPortal()
    }
  }
}

include("spring-data-vmware-gemfire")
include("spring-test-vmware-gemfire")

project(":spring-data-vmware-gemfire").name = "spring-data-vmware-gemfire"
rootProject.name = "spring-data-for-vmware-gemfire"

dependencyResolutionManagement {
  repositories {
    addGemFireRepositories(
      providers,
      addMavenCentral = providers.gradleProperty("useMavenCentral").getOrElse("false").toBoolean()
    )
  }
  versionCatalogs {
    create("libs") {
      // Override gemfireVersion only when explicitly passed as a system property (-D)
      // or Gradle property (-P). The TOML is the source of truth for normal builds.
      val gemfireVersion = System.getProperty("gemfireVersion")
        ?: (settings as? ExtensionAware)?.extensions?.extraProperties?.let {
          if (it.has("gemfireVersion")) it.get("gemfireVersion") as? String else null
        }

      if (gemfireVersion != null) {
        logger.debug("Overriding gemfireVersion: $gemfireVersion")
        version("gemfireVersion", gemfireVersion)
      }
    }
  }
}

fun org.gradle.api.artifacts.dsl.RepositoryHandler.addGemFireRepositories(
  providers: org.gradle.api.provider.ProviderFactory,
  addGradlePluginPortal: Boolean = false,
  addMavenCentral: Boolean = false
) {
  val configFilePath = providers.gradleProperty("spring.gemfire.repositories").getOrElse(
    providers.environmentVariable("HOME").get() + "/.gradle/gradleRepositories.json"
  )
  val jsonString = java.io.File(configFilePath).readText(Charsets.UTF_8)
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
