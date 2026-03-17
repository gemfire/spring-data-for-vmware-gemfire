/*
 * Copyright $originalComment.match(" (\d+)", 1, "-", $today.year)2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

/*
 * Copyright $originalComment.match(" (\d+)", 1, "-", $today.year)2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

/*
 * Copyright $originalComment.match(" (\d+)", 1, "-", $today.year)2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

plugins {
    id("java-library")
    id("commercial-repositories")
    application
}

repositories {
    mavenLocal()
    mavenCentral()
}

group = "com.example"
version = "1.0-SNAPSHOT"
description = "Test application for GUD driver validation"

java {
    toolchain { languageVersion.set(JavaLanguageVersion.of(17)) }
}

dependencies {
    // Spring Data VMware GemFire - provides GUD API transitively
    implementation(project(":spring-data-vmware-gemfire"))

    runtimeOnly(project(":gud-driver-gemfire-10.1"))
//    runtimeOnly("com.vwmare.gemfire:gemfire-core:10.1.7")

    // Logging
    implementation(libs.logback)
    implementation(libs.log4J)
    
    // Test
    testImplementation(libs.junit.jupiter.api)
    testRuntimeOnly(libs.junit.jupiter.engine)
}

application {
    mainClass.set("com.example.testcaching.TestCachingApplication")
}

tasks.withType<Test> {
    useJUnitPlatform()
}
