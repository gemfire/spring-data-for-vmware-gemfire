/*
 * Copyright 2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

plugins {
  id("com.jfrog.artifactory")
}

val projectBuildName = "${project.name}-${project.version}"
val projectBuildNumber = project.properties.getOrDefault("buildId", "0000") as String
val projectAgentName = System.getProperty("user.name") as String

artifactory {
  clientConfig.isIncludeEnvVars = true

  val url = providers.gradleProperty("artifactoryURL").getOrNull()
  setContextUrl(url)
  publish {
    contextUrl = url
    repository {
      // The Artifactory repository key to publish to
      repoKey = providers.gradleProperty("artifactoryRepo").getOrNull()
      // The publisher username
      username = providers.gradleProperty("artifactoryUsername").getOrNull()
      // The publisher password
      password = providers.gradleProperty("artifactoryPassword").getOrNull()
    }
    defaults {
      publications("ALL_PUBLICATIONS")
      setPublishArtifacts(true)
      isPublishBuildInfo = true
      setPublishPom(true)
      setPublishIvy(false)

      forkCount = 5
    }
  }
  buildInfo {
    buildName = projectBuildName
    buildNumber = projectBuildNumber
    project = "tds-gemfire"
    agentName = projectAgentName
  }
}
