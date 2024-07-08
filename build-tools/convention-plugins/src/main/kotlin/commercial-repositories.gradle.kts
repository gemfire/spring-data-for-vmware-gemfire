/*
 * Copyright 2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

plugins {
  id("java-library")
  id("idea")
  id("eclipse")
}

repositories {
  mavenCentral()
  val repositoryConfigFilePath = providers.gradleProperty("spring.gemfire.repositories").getOrElse(
    providers.environmentVariable("HOME").get() + "/.gradle/gradleRepositories.json"
  )

  val enablePrivateCommercialRepos =
    providers.gradleProperty("spring.gemfire.enable.private.repositories")
      .getOrElse("false").toBoolean()
  val jsonString = File(repositoryConfigFilePath).readText(Charsets.UTF_8)
  val repositories = groovy.json.JsonSlurper().parseText(jsonString) as Map<*, *>
  (repositories["repositories"] as List<*>).filterNotNull().map { entry -> entry as Map<*, *> }
    .filter { entry ->
      return@filter if (entry["private"]!! as Boolean) {
        enablePrivateCommercialRepos
      } else {
        true
      }
    }.forEach { entry ->
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
}
