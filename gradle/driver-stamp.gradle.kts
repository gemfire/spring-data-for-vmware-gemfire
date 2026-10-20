/*
 * Copyright 2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
import java.util.Properties
import java.util.zip.ZipFile
import org.gradle.api.artifacts.component.ModuleComponentIdentifier
import org.gradle.api.attributes.LibraryElements
import org.gradle.api.attributes.Usage
import org.gradle.api.tasks.SourceSetContainer

// Applied by each driver after declaring driverProvider and, for the native-free mock only,
// mockNativeVersion. Read the resolved SPI jar, not the root project's unrelated artifact version.
val providerClass = extra["driverProvider"] as String
val mockNativeVersion = if (extra.has("mockNativeVersion")) extra["mockNativeVersion"] as String else null
val stampClasspath = configurations.create("driverStampClasspath") {
  isCanBeConsumed = false
  isCanBeResolved = true
  extendsFrom(configurations.getByName("compileClasspath"))
  attributes {
    attribute(Usage.USAGE_ATTRIBUTE, objects.named(Usage.JAVA_API))
    attribute(LibraryElements.LIBRARY_ELEMENTS_ATTRIBUTE, objects.named(LibraryElements.JAR))
  }
  shouldResolveConsistentlyWith(configurations.getByName("compileClasspath"))
}
val generatedResources = layout.buildDirectory.dir("generated/driver-resources")
val generateDriverStamp = tasks.register("generateDriverStamp") {
  inputs.files(stampClasspath).withPropertyName("resolvedDriverClasspath").withPathSensitivity(PathSensitivity.NONE)
  inputs.property("providerClass", providerClass)
  inputs.property("mockNativeVersion", mockNativeVersion ?: "")
  outputs.dir(generatedResources)
  doLast {
    val spiResource = "com/vmware/gemfire/driver/spi/spi-version.properties"
    val spiVersions = stampClasspath.files.filter { it.extension == "jar" }.mapNotNull { jar ->
      ZipFile(jar).use { zip ->
        zip.getEntry(spiResource)?.let { entry ->
          zip.getInputStream(entry).use { input ->
            Properties().apply { load(input) }.getProperty("spi.version")
          }
        }
      }
    }
    check(spiVersions.size == 1) { "Expected exactly one resolved SPI version resource" }
    val spiVersion = spiVersions.single()
    check(spiVersion.matches(Regex("[0-9]+\\.[0-9]+"))) { "Invalid SPI major.minor in resolved SPI jar" }
    val nativeVersions = stampClasspath.incoming.resolutionResult.allComponents.mapNotNull {
      (it.id as? ModuleComponentIdentifier)?.takeIf {
        it.group == "com.vmware.gemfire" && it.module == "gemfire-core"
      }?.version
    }
    val gemFireVersion = if (mockNativeVersion != null) {
      check(nativeVersions.isEmpty()) { "The mock must not depend on native GemFire" }
      mockNativeVersion
    } else {
      check(nativeVersions.size == 1) { "Expected exactly one resolved native GemFire version" }
      nativeVersions.single()
    }
    val root = generatedResources.get().asFile
    // Clear only task-owned output so renaming a provider cannot leave a second service/stamp behind.
    root.deleteRecursively()
    root.resolve("META-INF/gemfire-driver/$providerClass.properties").apply {
      parentFile.mkdirs()
      writeText("spi.version=$spiVersion\ngemfire.version=$gemFireVersion\n")
    }
    root.resolve("META-INF/services/com.vmware.gemfire.driver.spi.GemFireDriver").apply {
      parentFile.mkdirs()
      writeText("$providerClass\n")
    }
  }
}
extensions.getByType<SourceSetContainer>().named("main") {
  resources.srcDir(generateDriverStamp)
}
