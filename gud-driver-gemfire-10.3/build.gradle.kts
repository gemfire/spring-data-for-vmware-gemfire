/*
 * Copyright $originalComment.match(" (\d+)", 1, "-", $today.year)2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

/*
 * @AI-Generated
 * Generated in whole or in part by Cursor
 * Description:
 * 2026-03-11: Created GemFire 10.3 driver module build configuration
 */

plugins {
    id("java-library")
    id("commercial-repositories")
}

repositories {
    mavenLocal()
}

description = "GemFire Unified Driver for GemFire 10.3.x"

java {
    toolchain { languageVersion.set(JavaLanguageVersion.of(17)) }
}

dependencies {
    api(project(":gud-api"))
    implementation(project(":gud-core"))
    
    implementation(libs.bundles.gemfire)
}
