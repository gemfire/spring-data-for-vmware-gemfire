/*
 * Copyright 2022-2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

plugins {
  id("java-library")
}

java {
  toolchain { languageVersion.set(JavaLanguageVersion.of(17)) }
}

tasks.withType<JavaCompile>().configureEach {
  options.compilerArgs.add("-parameters")
}

dependencies {
  api(project(":spring-data-gemfire-driver-spi"))

  testImplementation(platform(libs.junit.jupiter.bom))
  testImplementation(libs.junit.jupiter.api)
  testImplementation(libs.assertJ)
  testRuntimeOnly("org.junit.platform:junit-platform-launcher")
  testRuntimeOnly(libs.junit.jupiter.engine)
}

tasks.withType<Test>().configureEach {
  useJUnitPlatform()
}

// Core is shipped with the SPI and carries its own resolved compile-time stamp (§6.1).
evaluationDependsOn(":spring-data-gemfire-driver-spi")
version = project(":spring-data-gemfire-driver-spi").version
extra["coreSpiStamp"] = true
apply(from = rootProject.file("gradle/driver-stamp.gradle.kts"))

tasks.test {
  // Fresh isolated jars are assembled by tests; no published or cached artifact is edited.
  doFirst {
    systemProperty("fixtureRuntimeClasspath", sourceSets.test.get().runtimeClasspath.asPath)
    systemProperty("fixtureCoreClasses", sourceSets.main.get().output.classesDirs.asPath)
    systemProperty("fixtureSpiClasses", project(":spring-data-gemfire-driver-spi")
      .extensions.getByType<SourceSetContainer>().getByName("main").output.classesDirs.asPath)
  }
}
