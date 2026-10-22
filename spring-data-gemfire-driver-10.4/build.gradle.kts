/*
 * Copyright 2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
import org.gradle.api.artifacts.component.ModuleComponentIdentifier

plugins {
  id("java-library")
}

java {
  toolchain { languageVersion.set(JavaLanguageVersion.of(17)) }
  withSourcesJar()
}

tasks.withType<Test>().configureEach { useJUnitPlatform() }

// Select the requested 10.4 line, then pin this build to the one concrete resolved version.
// The artifact version, compile dependency, stamp and tests must all refer to that same release.
val nativeEngineVersion = configurations.create("nativeEngineVersion") {
  isCanBeConsumed = false
  isTransitive = false
}
val requestedNativeVersion = providers.systemProperty("gemfireVersion")
  .orElse(providers.gradleProperty("gemfireVersion")).orElse("10.4.+").get()
require(requestedNativeVersion.startsWith("10.4.")) { "This driver supports the GemFire 10.4 line" }
dependencies { add(nativeEngineVersion.name, "com.vmware.gemfire:gemfire-core:$requestedNativeVersion") }
val resolvedNativeVersion = nativeEngineVersion.incoming.resolutionResult.allComponents.mapNotNull {
  (it.id as? ModuleComponentIdentifier)?.takeIf {
    it.group == "com.vmware.gemfire" && it.module == "gemfire-core"
  }?.version
}.single()
version = resolvedNativeVersion
extra["driverProvider"] = "com.vmware.gemfire.driver.gemfire104.GemFire104Driver"
apply(from = rootProject.file("gradle/driver-stamp.gradle.kts"))

val integrationTest = sourceSets.create("integrationTest")
configurations[integrationTest.implementationConfigurationName].extendsFrom(configurations.implementation.get())
configurations[integrationTest.runtimeOnlyConfigurationName].extendsFrom(configurations.runtimeOnly.get())

dependencies {
  api(project(":spring-data-gemfire-driver-spi"))
  implementation("com.vmware.gemfire:gemfire-core") { version { strictly(resolvedNativeVersion) } }
  implementation("com.vmware.gemfire:gemfire-version") { version { strictly(resolvedNativeVersion) } }
  add(integrationTest.implementationConfigurationName, project(":spring-data-gemfire-driver-contract-tests"))
  add(integrationTest.runtimeOnlyConfigurationName, libs.junit.jupiter.engine)
  add(integrationTest.runtimeOnlyConfigurationName, libs.junit.platform.launcher)
}
integrationTest.compileClasspath += sourceSets.main.get().output
integrationTest.runtimeClasspath += sourceSets.main.get().output

val integrationTestTask = tasks.register<Test>("integrationTest") {
  description = "Runs the shared driver contract against the resolved GemFire 10.4 release."
  group = "verification"
  testClassesDirs = integrationTest.output.classesDirs
  classpath = integrationTest.runtimeClasspath
  maxParallelForks = 1
  forkEvery = 1
  systemProperty("expectedGemFireVersion", resolvedNativeVersion)
  systemProperty("nativeFixtureClasspath", integrationTest.runtimeClasspath.asPath)
  jvmArgs("--add-exports=java.base/sun.nio.ch=ALL-UNNAMED",
    "--add-opens=java.base/java.lang=ALL-UNNAMED", "--add-opens=java.base/java.nio=ALL-UNNAMED")
}
tasks.check { dependsOn(integrationTestTask) }
