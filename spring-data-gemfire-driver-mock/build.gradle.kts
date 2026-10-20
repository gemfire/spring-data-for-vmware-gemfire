/*
 * Copyright 2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
import java.util.zip.ZipFile
import org.gradle.api.artifacts.component.ModuleComponentIdentifier

plugins {
  id("java-library")
}

java {
  toolchain { languageVersion.set(JavaLanguageVersion.of(17)) }
  withSourcesJar()
}

tasks.withType<Test>().configureEach { useJUnitPlatform() }

extra["driverProvider"] = "com.vmware.gemfire.driver.mock.MockGemFireDriver"
extra["mockNativeVersion"] = "mock"
apply(from = rootProject.file("gradle/driver-stamp.gradle.kts"))

dependencies {
  api(project(":spring-data-gemfire-driver-spi"))
  testImplementation(project(":spring-data-gemfire-driver-contract-tests"))
  testRuntimeOnly(libs.junit.jupiter.engine)
  testRuntimeOnly(libs.junit.platform.launcher)
}

val verifyNativeFreeClasspath = tasks.register("verifyNativeFreeClasspath") {
  val classpaths = listOf(configurations.compileClasspath, configurations.testRuntimeClasspath)
  inputs.files(classpaths)
  doLast {
    classpaths.forEach { classpath ->
      classpath.get().incoming.resolutionResult.allComponents.forEach { component ->
        val id = component.id as? ModuleComponentIdentifier
        check(id == null || !(id.group.startsWith("org.apache.geode") ||
          (id.group == "com.vmware.gemfire" && id.module.startsWith("gemfire-")))) {
          "Native GemFire dependency on mock classpath: $id"
        }
      }
      classpath.get().files.forEach { entry ->
        if (entry.isDirectory) {
          check(!entry.resolve("org/apache/geode").exists()) { "Native GemFire classes in $entry" }
        } else if (entry.extension == "jar") {
          ZipFile(entry).use { zip ->
            check(zip.entries().asSequence().none { it.name.startsWith("org/apache/geode/") }) {
              "Native GemFire classes on mock classpath: ${entry.name}"
            }
          }
        }
      }
    }
  }
}
tasks.check { dependsOn(verifyNativeFreeClasspath) }
