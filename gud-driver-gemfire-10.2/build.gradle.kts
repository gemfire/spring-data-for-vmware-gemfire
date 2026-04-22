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
 * 2026-03-14: Created GemFire 10.2 driver module build configuration
 */

plugins {
    id("java-library")
    id("commercial-repositories")
}

repositories {
    mavenLocal()
}

description = "GemFire Unified Driver for GemFire 10.2.x"

java {
    toolchain { languageVersion.set(JavaLanguageVersion.of(17)) }
}

val gemfire102Version = "10.2.2"

dependencies {
    api(project(":gud-api"))
    implementation(project(":gud-core"))
    
    implementation("com.vmware.gemfire:gemfire-core:$gemfire102Version")
    implementation("com.vmware.gemfire:gemfire-logging:$gemfire102Version")
    implementation("com.vmware.gemfire:gemfire-cq:$gemfire102Version")
    implementation("com.vmware.gemfire:gemfire-wan:$gemfire102Version")
    implementation("com.vmware.gemfire:gemfire-gfsh:$gemfire102Version")
    implementation("com.vmware.gemfire:gemfire-tcp-server:$gemfire102Version")
    implementation("com.vmware.gemfire:gemfire-deployment-chained-classloader:$gemfire102Version")

    testImplementation(libs.junit)
    testImplementation(libs.assertJ)
    testImplementation(libs.mockito)
}

tasks.withType<Test> {
    useJUnit()
}
