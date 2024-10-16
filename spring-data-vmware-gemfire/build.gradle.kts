/*
 * Copyright 2022-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
import com.google.cloud.storage.BlobId
import com.google.cloud.storage.BlobInfo
import com.google.cloud.storage.StorageOptions
import org.apache.tools.ant.taskdefs.condition.Os


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
  toolchain { languageVersion.set(JavaLanguageVersion.of(17)) }
}

tasks.register<Test>("testOn21") {
  javaLauncher = javaToolchains.launcherFor {
    languageVersion = JavaLanguageVersion.of(21)
  }
}

tasks.named<Javadoc>("javadoc") {
  title = "Spring Data for VMware GemFire Java API Reference"
  isFailOnError = false
}

tasks.withType<JavaCompile>().configureEach {
  options.compilerArgs.add("-parameters")
}

publishingDetails {
  artifactName.set("spring-data-3.3-gemfire-${getGemFireBaseVersion()}")
  longName.set("Spring Data VMware GemFire")
  description.set("Spring Data For VMware GemFire")
  test.set(false)
}

dependencies {
  api(platform("org.springframework.data:spring-data-bom:${project.ext.get("spring-data-bom.version")}"))
  api(platform("org.springframework:spring-framework-bom:${project.ext.get("spring-framework.version")}"))

  compileOnly(libs.bundles.gemfire)

  implementation(libs.cache.api)
  api("org.springframework:spring-context-support")
  api("org.springframework:spring-tx")
  api("org.springframework:spring-web")
  api("org.springframework.data:spring-data-commons")
  implementation(libs.spring.shiro)
  implementation(libs.aspectJ)
  implementation(libs.bundles.jackson)
  runtimeOnly(libs.antlr)
  compileOnly(libs.cdi.api) {
    exclude("javax.annotation", "jsr250-api")
  }

  testImplementation(libs.bundles.gemfire)

  testImplementation(libs.cdi.api) {
    exclude("javax.annotation", "jsr250-api")
  }
  testImplementation(libs.interceptor.api)
  testImplementation(libs.logback)
  testImplementation(libs.log4J)
  testImplementation(libs.annotation.api)
  testImplementation(libs.derby)
  testImplementation(variantOf(libs.openwebbeans.se) { classifier("jakarta") })
  testImplementation(variantOf(libs.openwebbeans.spi) { classifier("jakarta") })
  testImplementation(variantOf(libs.openwebbeans.impl) { classifier("jakarta") })
  testImplementation(libs.assertJ)
  testImplementation(libs.snappy)
  testImplementation(libs.spring.shell) {
    exclude("com.google.guava", "guava")
  }
  testImplementation(libs.multithreadedtc)

  testImplementation(libs.junit.jupiter.api)
  testImplementation(libs.junit.vintage.engine)
  testRuntimeOnly(libs.junit.jupiter.engine)

  testImplementation(libs.junit)
  testImplementation(libs.assertJ)
  testImplementation(libs.mockito)
  testImplementation(libs.lombok)
  testImplementation("org.springframework:spring-test")
  testImplementation(libs.spring.boot)
  testImplementation(libs.awaitility)
  testImplementation(libs.gemfire.testcontainers)
  testImplementation(project(":spring-test-vmware-gemfire"))
}

tasks.register("prepareKotlinBuildScriptModel") {}

tasks {
  test {
    forkEvery = 1
    maxParallelForks = 2
    val springTestGemfireDockerImage: String by project

    systemProperty(
      "java.util.logging.config.file",
      "${project.layout.buildDirectory}/test-classes/java-util-logging.properties"
    )
    systemProperty("javax.net.ssl.keyStore", "${project.layout.buildDirectory}/test-classes/trusted.keystore")
    systemProperty("gemfire.disableShutdownHook", "true")
    systemProperty("logback.log.level", "error")
    systemProperty("spring.profiles.active", "apache-geode")
    systemProperty("spring.test.gemfire.docker.image", springTestGemfireDockerImage)

    filter {
      includeTestsMatching("*.*Tests")
      includeTestsMatching("*.*Test")
    }
  }
  getByName("testOn21", Test::class) {
    forkEvery = 1
//  maxParallelForks = 1
    val springTestGemfireDockerImage: String by project

    systemProperty(
      "java.util.logging.config.file",
      "${project.layout.buildDirectory}/test-classes/java-util-logging.properties"
    )
    systemProperty("javax.net.ssl.keyStore", "${project.layout.buildDirectory}/test-classes/trusted.keystore")
    systemProperty("gemfire.disableShutdownHook", "true")
    systemProperty("logback.log.level", "error")
    systemProperty("spring.profiles.active", "apache-geode")
    systemProperty("spring.test.gemfire.docker.image", springTestGemfireDockerImage)

    filter {
      includeTestsMatching("*.*Tests")
      includeTestsMatching("*.*Test")
    }
  }
}

gradle.taskGraph.whenReady {
  tasks.withType<Test>().forEach { test ->
    tasks.named("check").get().dependsOn(test)
    test.jvmArgs(
      "-XX:+HeapDumpOnOutOfMemoryError", "-ea",
      // Product: BufferPool uses DirectBuffer
      "--add-exports=java.base/sun.nio.ch=ALL-UNNAMED",
      // Tests: CertificateBuilder uses numerous types declared here
      "--add-exports=java.base/sun.security.x509=ALL-UNNAMED",
      // Product: ManagementAgent"s custom MBean servers extend types declared here
      "--add-exports=java.management/com.sun.jmx.remote.security=ALL-UNNAMED",
      // Product: UnsafeThreadLocal accesses fields and methods of ThreadLocal
      "--add-opens=java.base/java.lang=ALL-UNNAMED",
      // Product: AddressableMemoryManager accesses DirectByteBuffer constructor
      "--add-opens=java.base/java.nio=ALL-UNNAMED",
      // Tests: EnvironmentVariables rule accesses Collections$UnmodifiableMap.m
      "--add-opens=java.base/java.util=ALL-UNNAMED",
      // Tests: SecurityTestUtils resets SSL-related fields
      "--add-opens=java.base/sun.security.ssl=ALL-UNNAMED",
      "-XX:+EnableDynamicAgentLoading",
      "--add-opens=java.base/javax.net.ssl=ALL-UNNAMED"
    )

    if (!Os.isFamily(Os.FAMILY_WINDOWS)) {
      test.jvmArgs("-XX:+UseZGC")
    }
  }
}

repositories {
  val additionalMavenRepoURLs: String? by project
  additionalMavenRepoURLs?.apply {
    if (this.isNotEmpty() && this.isNotBlank()) {
      this.split(",").forEach {
        project.repositories.maven {
          this.url = uri(it)
        }
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
    val storage =
      StorageOptions.newBuilder().setProjectId(project.properties["docsGCSProject"].toString()).build().getService()
    val blobId = BlobId.of(
      project.properties["docsGCSBucket"].toString(),
      "${publishingDetails.artifactName.get()}/${project.version}/${
        tasks.named("javadocJar").get().outputs.files.singleFile.name
      }"
    )
    val blobInfo = BlobInfo.newBuilder(blobId).build()
    storage.createFrom(blobInfo, tasks.named("javadocJar").get().outputs.files.singleFile.toPath())
  }
}

tasks.register<Jar>("testJar") {
  from(sourceSets.test.get().output)
  from(sourceSets.main.get().output)
  archiveFileName = "testJar.jar"
  duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}

tasks.getByName<Test>("test") {
  dependsOn(tasks.getByPath("testJar"))
  systemProperty("TEST_JAR_PATH", tasks.getByName<Jar>("testJar").outputs.files.singleFile.absolutePath)
}

tasks.getByName<Test>("testOn21") {
  dependsOn(tasks.getByPath("testJar"))
  systemProperty("TEST_JAR_PATH", tasks.getByName<Jar>("testJar").outputs.files.singleFile.absolutePath)
}
