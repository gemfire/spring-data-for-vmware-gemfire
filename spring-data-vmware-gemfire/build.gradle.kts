/*
 * Copyright 2022-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
import com.google.cloud.storage.BlobId
import com.google.cloud.storage.BlobInfo
import com.google.cloud.storage.StorageOptions


buildscript {
  repositories {
    mavenCentral()
  }
  dependencies {
    classpath(libs.google.cloud.storage)
  }
}

plugins {
  id("java-library")
  id("gemfire-repo-artifact-publishing")
  id("commercial-repositories")
  id("gemfire-artifactory")
  alias(libs.plugins.lombok)
  alias(libs.plugins.dependency.management)
}

java {
  withJavadocJar()
  withSourcesJar()

  toolchain{ languageVersion.set(JavaLanguageVersion.of(8))}
}

tasks.named<Javadoc>("javadoc") {
  title = "Spring Data 2.6 for VMware GemFire 9.15 Java API Reference"
  isFailOnError = false
}

publishingDetails {
  artifactName.set("spring-data-2.6-gemfire-${getGemFireBaseVersion()}")
  longName.set("Spring Data VMware GemFire")
  description.set("Spring Data For VMware GemFire")
}

dependencies {
  api(platform("org.springframework.data:spring-data-bom:${project.ext.get("spring-data-bom.version")}"))
  api(platform("org.springframework:spring-framework-bom:${project.ext.get("spring-framework.version")}"))

  compileOnly(libs.bundles.gemfire)

  implementation(libs.cache.api)
  api(libs.spring.context.support)
  api(libs.spring.tx)
  api(libs.spring.web)
  api(libs.spring.data.commons)
  implementation(libs.spring.shiro)
  implementation(libs.aspectJ)
  implementation(libs.bundles.jackson)
  runtimeOnly(libs.antlr)
  compileOnly(libs.cdi.api) {
    exclude("javax.annotation", "jsr250-api")
  }

  testImplementation(libs.bundles.gemfire)
  testImplementation(libs.spring.test.gemfire)
  testImplementation(libs.geronimo.jcdi)
  testImplementation(libs.el.api)
  testImplementation(libs.interceptor.api)
  testImplementation(libs.logback)
  testImplementation(libs.log4J)
  testImplementation(libs.annotation.api)
  testImplementation(libs.derby)
  testImplementation(libs.openwebbeans.se)
  testImplementation(libs.snappy)
  testImplementation(libs.spring.shell) {
    exclude("com.google.guava", "guava")
  }
  testImplementation(libs.multithreadedtc)
  testImplementation(libs.cdi.api) {
    exclude("javax.annotation", "jsr250-api")
  }
  testImplementation(libs.junit)
  testImplementation(libs.assertJ)
  testImplementation(libs.mockito)
  testImplementation(libs.lombok)
  testImplementation(libs.spring.test)
  testImplementation(libs.awaitility)
}

tasks.register("prepareKotlinBuildScriptModel") {}

tasks {
  test {
    forkEvery = 1

    systemProperty("java.util.logging.config.file", "${project.layout.buildDirectory}/test-classes/java-util-logging.properties")
    systemProperty("javax.net.ssl.keyStore", "${project.layout.buildDirectory}/test-classes/trusted.keystore")
    systemProperty("gemfire.disableShutdownHook", "true")
    systemProperty("logback.log.level", "error")
    systemProperty("spring.profiles.active", "apache-geode")

    filter {
      includeTestsMatching("*.*Tests")
      includeTestsMatching("*.*Test")
    }
  }
}

repositories {
  val additionalMavenRepoURLs: String by project
  if (additionalMavenRepoURLs.isNotEmpty() && additionalMavenRepoURLs.isNotBlank()) {
    additionalMavenRepoURLs.split(",").forEach {
      project.repositories.maven {
        this.url = uri(it)
      }
    }
  }
  maven { url = uri("https://repo.spring.io/milestone") }
}

fun getGemFireBaseVersion(): String {
  val gemfireVersion: String by project
  val split = gemfireVersion.split(".")
  if (split.size < 2) {
    throw RuntimeException("gemfireVersion is malformed")
  }
  return "${split[0]}.${split[1]}"
}

tasks.register("copyJavadocsToBucket") {
  dependsOn(tasks.named("javadocJar"))
  doLast {
    val storage = StorageOptions.newBuilder().setProjectId(project.properties["docsGCSProject"].toString()).build().getService()
    val blobId = BlobId.of(project.properties["docsGCSBucket"].toString(), "${publishingDetails.artifactName.get()}/${project.version}/${tasks.named("javadocJar").get().outputs.files.singleFile.name}")
    val blobInfo = BlobInfo.newBuilder(blobId).build()
    storage.createFrom(blobInfo, tasks.named("javadocJar").get().outputs.files.singleFile.toPath())
  }
}
