/*
 * Copyright 2024-2025 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

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
    id("gemfire-repo-artifact-publishing")
    id("commercial-repositories")
    id("gemfire-artifactory")
    alias(libs.plugins.lombok)
}

java {
    withJavadocJar()
    withSourcesJar()

    toolchain { languageVersion.set(JavaLanguageVersion.of(17)) }
}

tasks.named<Javadoc>("javadoc") {
    title = "Spring Test for VMware GemFire Java API Reference"
    isFailOnError = false
}

val baseGemFireVersion: String by project
val baseSpringVersion: String by project

publishingDetails {
    artifactName.set("spring-data-${baseSpringVersion}-gemfire-test-framework-${baseGemFireVersion}")
    longName.set("Spring Test Framework for VMware GemFire ${baseGemFireVersion} and Spring Data ${baseSpringVersion}")
    description.set("Spring Test Framework for VMware GemFire ${baseGemFireVersion} and Spring Data ${baseSpringVersion}")
    test.set(true)
}

dependencies {
    api(platform("org.springframework:spring-framework-bom:${project.ext.get("spring-framework.version")}"))

    api(libs.multithreadedtc)
    api(libs.junit)
    api(libs.assertJ)
    api(libs.mockito)
    api(libs.lombok)
    api("org.springframework:spring-test")

    compileOnly(project(":spring-data-vmware-gemfire"))

    implementation(libs.annotation.api)

    implementation(libs.logback)

    compileOnly(libs.spring.boot) {
        exclude("org.springframework.boot", "spring-boot-starter-logging")
    }

    compileOnly(libs.bundles.gemfire)

    testImplementation(libs.bundles.gemfire)

    testImplementation(project(":spring-data-vmware-gemfire")) {
        exclude("com.vmware.gemfire")
    }
}

repositories {
    val additionalMavenRepoURLs: String? by project
    additionalMavenRepoURLs?.apply {
        if (this.isNotEmpty() && this.isNotBlank()) {
            this.split(",").forEach {
                project.repositories.maven {
                    this.url = uri(it)
                }
            }
        }
    }
    maven { url = uri("https://repo.spring.io/milestone") }
}
