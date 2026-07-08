/*
 * Copyright 2022-2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
import com.google.auth.oauth2.GoogleCredentials
import com.google.cloud.storage.BlobId
import com.google.cloud.storage.BlobInfo
import com.google.cloud.storage.StorageOptions
import org.apache.tools.ant.taskdefs.condition.Os
import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent
import java.io.FileInputStream


buildscript {
  repositories {
    val repositoryConfigFilePath = providers.gradleProperty("spring.gemfire.repositories").getOrElse(
      providers.environmentVariable("HOME").get() + "/.gradle/gradleRepositories.json"
    )

    val jsonString = File(repositoryConfigFilePath).readText(Charsets.UTF_8)
    val repositories = groovy.json.JsonSlurper().parseText(jsonString) as Map<*, *>
    (repositories["repositories"] as List<*>).filterNotNull().map { entry -> entry as Map<*, *> }
      .forEach { entry ->
        entry.apply {
          maven {
            url = uri(entry["url"]!! as String)
            if (!entry["username"]?.toString().isNullOrBlank()) {
              credentials {
                username = entry["username"] as String
                password = entry["password"] as String
              }
            }
          }
        }
      }

    if (providers.gradleProperty("useMavenCentral").getOrElse("false").toBoolean()) {
      mavenCentral()
    }
  }
  dependencies {
    classpath(libs.google.cloud.storage)
  }
}

plugins {
  id("java-library")
  id("idea")
  id("eclipse")
  id("gemfire-repo-artifact-publishing")
  id("gemfire-artifactory")
  alias(libs.plugins.lombok)
}

sourceSets {
  register("integrationTest") {
    compileClasspath += sourceSets.main.get().output + sourceSets.test.get().output
    runtimeClasspath += sourceSets.main.get().output + sourceSets.test.get().output
    java.srcDir("src/integrationTest/java")
    resources.srcDir("src/test/resources")
  }
}

configurations {
  getByName("integrationTestImplementation") {
    extendsFrom(configurations.implementation.get())
    extendsFrom(configurations.getByName("testImplementation"))
  }
  getByName("integrationTestRuntimeOnly") {
    extendsFrom(configurations.runtimeOnly.get())
    extendsFrom(configurations.getByName("testRuntimeOnly"))
  }
}

java {
  withJavadocJar()
  withSourcesJar()
  toolchain { languageVersion.set(JavaLanguageVersion.of(17)) }
}

tasks.named<Javadoc>("javadoc") {
  title = "Spring Data for VMware GemFire Java API Reference"
  isFailOnError = false
}

tasks.withType<JavaCompile>().configureEach {
  options.compilerArgs.add("-parameters")
}

val baseGemFireVersion: String by project
val baseSpringVersion: String by project

publishingDetails {
  artifactName.set("spring-data-${baseSpringVersion}-gemfire-${baseGemFireVersion}")
  longName.set("Spring Data VMware GemFire")
  description.set("Spring Data For VMware GemFire")
  test.set(false)
}

dependencies {
  api(platform(libs.spring.framework.bom))
  api(platform(libs.spring.data.bom))

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
  testImplementation(libs.derby.tools)
  testImplementation(libs.openwebbeans.se)
  testImplementation(libs.openwebbeans.spi)
  testImplementation(libs.openwebbeans.impl)
  testImplementation(libs.assertJ)
  testImplementation(libs.snappy)
  testImplementation(libs.spring.shell) {
    exclude("com.google.guava", "guava")
  }
  testImplementation(libs.multithreadedtc)

  testImplementation(libs.junit.jupiter.api)
  testImplementation(libs.junit.vintage.engine)
  testRuntimeOnly(platform(libs.junit.jupiter.bom))
  testRuntimeOnly("org.junit.platform:junit-platform-launcher")
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

tasks {
  test {
    dependsOn("testJar")
  }
  this.register<Test>("integrationTest") {
    description = "Runs the integration tests."
    group = "verification"

    testClassesDirs = sourceSets["integrationTest"].output.classesDirs
    classpath = sourceSets["integrationTest"].runtimeClasspath

    dependsOn("testJar")

    forkEvery = 1
    maxParallelForks = 2
  }
}

gradle.taskGraph.whenReady {
  tasks.withType<Test>().forEach { testTask ->
    with(testTask) {
      jvmArgs(
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
        jvmArgs("-XX:+UseZGC")
      }

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

      systemProperty("TEST_JAR_PATH", tasks.getByName<Jar>("testJar").outputs.files.singleFile.absolutePath)

      testLogging { events(TestLogEvent.FAILED); exceptionFormat = TestExceptionFormat.FULL }
      useJUnitPlatform()
    }
  }
}

tasks.register("copyJavadocsToBucket") {
  dependsOn(tasks.named("javadocJar"))
  doLast {
    val storage =
      StorageOptions.newBuilder().setProjectId(project.properties["docsGCSProject"].toString()).setCredentials(
        GoogleCredentials.fromStream(FileInputStream(project.properties["docsGCSProjectCredentials"].toString()))).build().getService()
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
