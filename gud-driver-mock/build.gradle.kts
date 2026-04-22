/*
 * Copyright (c) 2026 Broadcom. All rights reserved.
 */

// Copyright (c) 2026 Broadcom. All Rights Reserved.

/*
 * @AI-Generated
 * Generated in whole or in part by Cursor
 * Description:
 * 2026-04-17: Created build config for mock GUD driver used by unit tests
 */

plugins {
    id("java-library")
}

description = "In-memory mock GUD driver for unit testing without native GemFire."

java {
    toolchain { languageVersion.set(JavaLanguageVersion.of(17)) }
}

repositories {
    mavenCentral()
}

dependencies {
    api(project(":gud-api"))
    api(project(":gud-core"))

    // Mockito is used to stub out the long tail of rarely-invoked Gud* subsystem
    // interfaces (QueryService, PdxInstanceFactory, LogWriter, etc.).  It leaks out
    // via `api` because the mock factories return mock instances.
    api(libs.mockito)

    // JUnit 4 + 5 are needed to compile GudResetRule / GudResetExtension.  They are
    // declared compileOnly so consumers are forced to bring their own test framework.
    compileOnly(libs.junit)
    compileOnly(libs.junit.jupiter.api)

    testImplementation(libs.junit)
    testImplementation(libs.assertJ)
}

tasks.withType<Test> {
    useJUnit()
}
