/*
 * Copyright 2022-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
plugins {
  id("groovy-gradle-plugin")
  `kotlin-dsl`
}

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
}

dependencies {
  implementation(libs.kotlin)
}
