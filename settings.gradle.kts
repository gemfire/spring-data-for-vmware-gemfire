/*
 * Copyright 2024-2026 Broadcom. All rights reserved.
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
    if (providers.gradleProperty("useMavenLocal").getOrElse("false").toBoolean()) {
      mavenLocal()
    }
    addGemFireRepositories(
      providers,
      addMavenCentral = providers.gradleProperty("useMavenCentral").getOrElse("false").toBoolean()
    )
  }
  versionCatalogs {
    create("libs") {
      overrideProperty("gemfireVersion")
    }
  }
}

fun RepositoryHandler.addGemFireRepositories(
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

fun VersionCatalogBuilder.overrideProperty(property: String) {
  val value = System.getProperty(property)
    ?: (settings as? ExtensionAware)?.extensions?.extraProperties?.let {
      if (it.has(property)) it.get(property) as? String else null
    }
  if (value != null) {
    logger.debug("Overriding $property: $value")
    version(property, value)
  }
}
