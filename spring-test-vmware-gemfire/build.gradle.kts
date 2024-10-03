/*
 * Copyright 2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

buildscript {
    repositories {
        mavenCentral()
    }
}

plugins {
    id("java-library")
    id("gemfire-repo-artifact-publishing")
    id("commercial-repositories")
    alias(libs.plugins.lombok)
    alias(libs.plugins.dependency.management)
}

java {
    withJavadocJar()
    withSourcesJar()

    toolchain { languageVersion.set(JavaLanguageVersion.of(8)) }
}

tasks.named<Javadoc>("javadoc") {
    title = "Spring Test for VMware GemFire Java API Reference"
    isFailOnError = false
}

publishingDetails {
    artifactName.set("spring-data-3.3-gemfire-${getGemFireBaseVersion()}")
    longName.set("Spring Test Framework for VMware GemFire ${getGemFireBaseVersion()} and Spring Data 3.3")
    description.set("Spring Test Framework for VMware GemFire ${getGemFireBaseVersion()} and Spring Data 3.3")
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

fun getGemFireBaseVersion(): String {
    val gemfireVersion: String by project
    val split = gemfireVersion.split(".")
    if (split.size < 2) {
        throw RuntimeException("gemfireVersion is malformed")
    }
    return "${split[0]}.${split[1]}"
}
