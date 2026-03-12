/*
 * Copyright $originalComment.match(" (\d+)", 1, "-", $today.year)2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

/*
 * @AI-Generated
 * Generated in whole or in part by Cursor
 * Description:
 * 2026-03-11: Created GUD API module build configuration
 */

plugins {
    id("java-library")
    id("commercial-repositories")
}

repositories {
    mavenLocal()
}

description = "GemFire Unified Driver API"

java {
    toolchain { languageVersion.set(JavaLanguageVersion.of(17)) }
}

dependencies {
    compileOnly("org.springframework:spring-context:${project.ext.get("spring-framework.version")}")
}
