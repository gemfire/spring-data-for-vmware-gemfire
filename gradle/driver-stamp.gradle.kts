/*
 * Copyright 2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
import java.util.Properties
import java.util.zip.ZipFile
import org.gradle.api.attributes.LibraryElements
import org.gradle.api.attributes.Usage
import org.gradle.api.tasks.SourceSetContainer
import org.gradle.jvm.toolchain.JavaToolchainService

// Applied by each driver after declaring driverProvider and, for the native-free mock only,
// mockNativeVersion. Read the resolved SPI jar, not the root project's unrelated artifact version.
val coreStamp = extra.has("coreSpiStamp")
val providerClass = if (coreStamp) "driver-core" else extra["driverProvider"] as String
val stampResource = if (coreStamp) "META-INF/gemfire-driver-core.properties"
  else "META-INF/gemfire-driver/$providerClass.properties"
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
fun resolvedStampVersions(): Pair<String, String?> {
    val spiResource = "com/vmware/gemfire/driver/spi/spi-version.properties"
    val artifacts = stampClasspath.resolvedConfiguration.resolvedArtifacts
    val spiArtifacts = artifacts.filter { it.name == "spring-data-gemfire-driver-spi" }
    check(spiArtifacts.size == 1) { "Expected exactly one resolved SPI artifact" }
    val spiArtifact = spiArtifacts.single()
    val spiVersion = ZipFile(spiArtifact.file).use { zip ->
      val entry = zip.getEntry(spiResource) ?: error("Resolved SPI jar has no version resource")
      zip.getInputStream(entry).use { Properties().apply { load(it) }.getProperty("spi.version") }
    }
    check(spiVersion != null && spiVersion.matches(Regex("[0-9]+\\.[0-9]+"))) { "Invalid resolved SPI version" }
    check(spiArtifact.moduleVersion.id.version == spiVersion) {
      "SPI artifact version ${spiArtifact.moduleVersion.id.version} differs from resource $spiVersion"
    }
    check(!coreStamp || project.version.toString() == spiVersion) {
      "Core artifact version ${project.version} differs from resolved SPI $spiVersion"
    }
    val nativeVersions = artifacts.filter { it.moduleVersion.id.group == "com.vmware.gemfire" &&
      it.name in listOf("gemfire-core", "gemfire-version") }.associate { it.name to it.moduleVersion.id.version }
    val nativeVersion = if (coreStamp || mockNativeVersion != null) {
      check(nativeVersions.isEmpty()) { "Core/mock must not depend on native GemFire" }
      mockNativeVersion
    } else {
      check(nativeVersions.keys == setOf("gemfire-core", "gemfire-version") && nativeVersions.values.distinct().size == 1) {
        "Resolved native core/version artifacts must match exactly: $nativeVersions"
      }
      nativeVersions.getValue("gemfire-core")
    }
    return spiVersion to nativeVersion
}
val generateDriverStamp = tasks.register("generateDriverStamp") {
  inputs.files(stampClasspath).withPropertyName("resolvedDriverClasspath").withPathSensitivity(PathSensitivity.NONE)
  inputs.property("providerClass", providerClass)
  inputs.property("mockNativeVersion", mockNativeVersion ?: "")
  outputs.dir(generatedResources)
  doLast {
    val (spiVersion, gemFireVersion) = resolvedStampVersions()
    val root = generatedResources.get().asFile
    // Clear only task-owned output so renaming a provider cannot leave a second service/stamp behind.
    root.deleteRecursively()
    root.resolve(stampResource).apply {
      parentFile.mkdirs()
      writeText("spi.version=$spiVersion\n" + (gemFireVersion?.let { "gemfire.version=$it\n" } ?: ""))
    }
    if (!coreStamp) root.resolve("META-INF/services/com.vmware.gemfire.driver.spi.GemFireDriver").apply {
      parentFile.mkdirs()
      writeText("$providerClass\n")
    }
  }
}
extensions.getByType<SourceSetContainer>().named("main") {
  resources.srcDir(generateDriverStamp)
}

val verifyDriverStamp = tasks.register<JavaExec>("verifyDriverStamp") {
  group = "verification"
  javaLauncher.set(project.extensions.getByType<JavaToolchainService>().launcherFor {
    languageVersion.set(JavaLanguageVersion.of(17))
  })
  dependsOn(tasks.named("jar"), rootProject.tasks.named("compileSpiCompatibilityTool"))
  inputs.files(stampClasspath, tasks.named("jar"))
  classpath = rootProject.files(rootProject.layout.buildDirectory.dir("spi-compatibility-classes"))
  mainClass.set("StampVerifier")
  doFirst {
    val (spi, native) = resolvedStampVersions()
    args(tasks.named<Jar>("jar").get().archiveFile.get().asFile, stampResource, spi, native ?: "")
  }
}
tasks.named("check") { dependsOn(verifyDriverStamp) }
tasks.matching { it.name.startsWith("publish") || it.name == "artifactoryPublish" }.configureEach {
  dependsOn(verifyDriverStamp, rootProject.tasks.named("verifySpiCompatibility"))
}
