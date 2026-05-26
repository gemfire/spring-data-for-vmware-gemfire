// Copyright (c) 2026 Broadcom. All Rights Reserved.

/*
 * @AI-Generated
 * Generated in whole or in part by Cursor
 * Description:
 * 2026-05-26: Shared RepositoryHandler extension for loading GemFire Maven repositories from JSON config
 */

import org.gradle.api.artifacts.dsl.RepositoryHandler
import org.gradle.api.provider.ProviderFactory
import java.io.File
import java.net.URI

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
                url = URI(entry["url"]!! as String)
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
