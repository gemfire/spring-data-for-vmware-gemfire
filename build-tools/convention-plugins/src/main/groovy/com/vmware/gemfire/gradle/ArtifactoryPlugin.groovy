/*
 * Copyright 2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package com.vmware.gemfire.gradle


import org.gradle.api.Plugin
import org.gradle.api.Project

class ArtifactoryPlugin implements Plugin<Project> {
    @Override
    void apply(Project project) {
        project.plugins.apply('com.jfrog.artifactory')
        def jf_ext = org.jfrog.gradle.plugin.artifactory.utils.ExtensionsUtils.getOrCreateArtifactoryExtension(project)
        def artifactoryURL = project.hasProperty('artifactoryURL') ? project.property('artifactoryURL') : ""
        jf_ext.setContextUrl(artifactoryURL)
        jf_ext.publish {
            repository.repoKey = project.hasProperty('artifactoryRepo') ? project.property('artifactoryRepo') : ""
            repository.username = project.hasProperty('artifactoryUsername') ? project.property('artifactoryUsername') : ""
            repository.password = project.hasProperty('artifactoryPassword') ? project.property('artifactoryPassword') : ""
            defaults {
                publications('ALL_PUBLICATIONS')
                publishArtifacts = true
                publishBuildInfo = true
                publishPom = true
                publishIvy = false

                forkCount = 5
            }
        }
        def clientConfig = jf_ext.clientConfig
        // clientConfig.setIncludeEnvVars(true)

        def buildInfo = clientConfig.info
        buildInfo.setBuildName("spring-data-3.1-gemfire-10.0-1.0.x")
        buildInfo.setBuildNumber(project.properties.getOrDefault("buildId", '0000') as String)
        buildInfo.setProject("tds-gemfire")
        buildInfo.setAgentName(System.properties['user.name'] as String)
    }
}
