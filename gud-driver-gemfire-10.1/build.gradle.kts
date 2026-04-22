/*
 * Copyright (c) 2026 Broadcom. All rights reserved.
 */

/*
 * Copyright $originalComment.match(" (\d+)", 1, "-", $today.year)2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

/*
 * @AI-Generated
 * Generated in whole or in part by Cursor
 * Description:
 * 2026-03-14: Created GemFire 10.1 driver module build configuration
 */

plugins {
    id("java-library")
    id("commercial-repositories")
}

repositories {
    mavenLocal()
}

description = "GemFire Unified Driver for GemFire 10.1.x"

java {
    toolchain { languageVersion.set(JavaLanguageVersion.of(17)) }
}

val gemfire101Version = "10.1.7"

dependencies {
    api(project(":gud-api"))
    implementation(project(":gud-core"))
    
    implementation("com.vmware.gemfire:gemfire-core:$gemfire101Version")
    implementation("com.vmware.gemfire:gemfire-logging:$gemfire101Version")
    implementation("com.vmware.gemfire:gemfire-cq:$gemfire101Version")
    implementation("com.vmware.gemfire:gemfire-wan:$gemfire101Version")
    implementation("com.vmware.gemfire:gemfire-gfsh:$gemfire101Version")
    implementation("com.vmware.gemfire:gemfire-tcp-server:$gemfire101Version")
    implementation("com.vmware.gemfire:gemfire-deployment-chained-classloader:$gemfire101Version")

    testImplementation(libs.junit)
    testImplementation(libs.assertJ)
    testImplementation(libs.mockito)
}

tasks.withType<Test> {
    useJUnit()
}
