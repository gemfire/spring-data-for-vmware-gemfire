/*
 * Copyright $originalComment.match(" (\d+)", 1, "-", $today.year)2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

/*
 * @AI-Generated
 * Generated in whole or in part by Cursor
 * Description:
 * 2026-03-11: Created GUD Core module build configuration
 */

plugins {
    id("java-library")
    id("commercial-repositories")
}

repositories {
    mavenLocal()
}

description = "GemFire Unified Driver Core"

java {
    toolchain { languageVersion.set(JavaLanguageVersion.of(17)) }
}

dependencies {
    api(project(":gud-api"))
    implementation("org.springframework:spring-context:${project.ext.get("spring-framework.version")}")
    implementation("org.slf4j:slf4j-api:2.0.9")
}
