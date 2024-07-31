/*
 * Copyright 2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package com.vmware.gemfire.gradle

import org.gradle.api.Plugin
import org.gradle.api.Project

class ArtifactoryPlugin : Plugin<Project> {
  override fun apply(project: Project) {
    project.plugins.apply("com.jfrog.artifactory")
    val jfrogExtension =
      org.jfrog.gradle.plugin.artifactory.utils.ExtensionsUtils.getOrCreateArtifactoryExtension(project)
    jfrogExtension.setContextUrl("https://usw1.packages.broadcom.com/artifactory")
    jfrogExtension.publish {
      repository.repoKey = (project.findProperty("artifactoryRepo") ?: let { "" }) as String
      repository.username = (project.findProperty("artifactoryUsername") ?: let { "" }) as String
      repository.password = (project.findProperty("artifactoryPassword") ?: let { "" }) as String
      defaults {
        publications("ALL_PUBLICATIONS")
        setPublishArtifacts(true)
//        publishBuildInfo = true
        setPublishPom(true)
        setPublishIvy(false)

        forkCount = 5
      }
    }
    val clientConfig = jfrogExtension.clientConfig

    val buildInfo = clientConfig.info
    buildInfo.buildName = "${project.name}-${project.version}"
    buildInfo.buildNumber = (project.properties.getOrDefault("buildId", "0000") as String)
    buildInfo.project = "tds-gemfire"
    buildInfo.agentName = System.getProperty("user.name") as String
  }
}
