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
import java.util.zip.ZipFile


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
  test {
    java.srcDir(rootProject.file("test-fixtures/function-protocol/java"))
    java.srcDir(rootProject.file("test-fixtures/client-server-protocol/java"))
  }
  register("integrationTest") {
    compileClasspath += sourceSets.main.get().output + sourceSets.test.get().output
    runtimeClasspath += sourceSets.main.get().output + sourceSets.test.get().output
    java.srcDir("src/integrationTest/java")
    resources.srcDir("src/test/resources")
    resources.srcDir(rootProject.file("spring-data-gemfire-driver-10.4/src/serverFixtures/resources"))
  }
}

// Resolve deployment payloads separately; never add private server code to a client classpath.
fun deploymentPayload(name: String): Configuration {
  val payload = configurations.create(name) {
    isCanBeConsumed = false
    isTransitive = false
  }
  dependencies.add(payload.name, dependencies.project(mapOf("path" to ":spring-data-gemfire-driver-10.4",
    "configuration" to name)))
  return payload
}
val serverFunctionFixtures = deploymentPayload("serverFunctionFixtures")
val serverSecurityFixtures = deploymentPayload("serverSecurityFixtures")
val templateDomainFixtures = deploymentPayload("templateDomainFixtures")
val deploymentPayloads = listOf(serverFunctionFixtures, serverSecurityFixtures, templateDomainFixtures)

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
  api(project(":spring-data-gemfire-driver-spi"))
  api(project(":spring-data-gemfire-driver-core"))
  testRuntimeOnly(project(":spring-data-gemfire-driver-mock"))
  api(platform(libs.spring.framework.bom))
  api(platform(libs.spring.data.bom))

  compileOnly(libs.bundles.gemfire)

  implementation(libs.cache.api)
  api("org.springframework:spring-context-support")
  api("org.springframework:spring-tx")
  api("org.springframework:spring-web")
  api("org.springframework.data:spring-data-commons")
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
  this.register<Test>("integrationTest") {
    description = "Runs the integration tests."
    group = "verification"

    testClassesDirs = sourceSets["integrationTest"].output.classesDirs
    classpath = sourceSets["integrationTest"].runtimeClasspath

    dependsOn(deploymentPayloads)
    inputs.files(deploymentPayloads)
    doFirst {
      systemProperty("SERVER_FUNCTION_FIXTURE_JAR_PATH", serverFunctionFixtures.singleFile.absolutePath)
      systemProperty("SERVER_SECURITY_FIXTURE_JAR_PATH", serverSecurityFixtures.singleFile.absolutePath)
      systemProperty("TEMPLATE_DOMAIN_FIXTURE_JAR_PATH", templateDomainFixtures.singleFile.absolutePath)
    }

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

val verifyClientServerFixturePackaging = tasks.register("verifyClientServerFixturePackaging") {
  description = "Checks isolated deployment payloads and rejects private server code in client artifacts."
  group = "verification"
  val clientClasspaths = sourceSets.main.get().compileClasspath + sourceSets.main.get().runtimeClasspath +
    sourceSets.test.get().compileClasspath + sourceSets.test.get().runtimeClasspath +
    sourceSets["integrationTest"].compileClasspath + sourceSets["integrationTest"].runtimeClasspath
  val clientJar = tasks.named<Jar>("jar")
  val driverJar = provider {
    project(":spring-data-gemfire-driver-10.4").tasks.named<Jar>("jar").get().archiveFile.get().asFile
  }
  dependsOn(deploymentPayloads, clientClasspaths, clientJar, ":spring-data-gemfire-driver-10.4:jar")
  inputs.files(deploymentPayloads, clientClasspaths, clientJar, driverJar)
  doLast {
    fun entries(file: File): Set<String> = when {
      file.isDirectory -> file.walkTopDown().filter { it.isFile }
        .map { it.relativeTo(file).invariantSeparatorsPath }.toSet()
      file.isFile && file.extension == "jar" -> ZipFile(file).use { zip ->
        zip.entries().asSequence().map { it.name }.toSet()
      }
      else -> emptySet()
    }
    fun payloadEntries(file: File) = entries(file).filter {
      !it.endsWith("/") && !it.startsWith("META-INF/")
    }.toSet()
    val protocolClasses = setOf(
      "org/springframework/data/gemfire/function/sample/Metric.class",
      "org/springframework/data/gemfire/function/execution/FunctionFixtureIds.class")
    val payload = payloadEntries(serverFunctionFixtures.singleFile)
    val pdxXml = "client-cache-function-execution-with-pdx-cache.xml"
    check(payload.containsAll(protocolClasses + pdxXml)) { "Function deployment is missing protocol/PDX configuration" }
    val functionClasses = payload - protocolClasses - pdxXml
    // 12 top-level function classes plus a holder and its three nested functions.
    check(functionClasses.size == 16 && functionClasses.all {
      it.endsWith(".class") && it.startsWith("org/springframework/data/gemfire/function/")
    }) { "Unexpected private function inventory: $functionClasses" }
    val securityClasses = setOf(
      "org/springframework/data/gemfire/config/annotation/TestSecurityManager.class",
      "org/springframework/data/gemfire/config/annotation/TestSecurityManager\$TestPrincipal.class")
    val credentialsClass = "org/springframework/data/gemfire/fixtures/security/SecurityFixtureCredentials.class"
    check(payloadEntries(serverSecurityFixtures.singleFile) == securityClasses + credentialsClass) {
      "Security deployment must contain only the manager, principal and shared credentials"
    }
    val domainClass = "org/springframework/data/gemfire/fixtures/template/User.class"
    check(payloadEntries(templateDomainFixtures.singleFile) == setOf(domainClass)) {
      "Template deployment must contain only its shared domain class"
    }
    val clientEntries = clientClasspaths.files.map { entries(it) }
    check(clientEntries.any { domainClass in it } && clientEntries.any { credentialsClass in it }) {
      "Client is missing shared domain/credential classes"
    }
    val privateClasses = functionClasses + securityClasses
    (clientClasspaths.files + clientJar.get().archiveFile.get().asFile +
      driverJar.get()).forEach { file ->
      val leaked = entries(file).intersect(privateClasses)
      check(leaked.isEmpty()) { "Private server fixtures leaked onto a client classpath/artifact $file: $leaked" }
    }
    listOf(clientJar.get().archiveFile.get().asFile, driverJar.get()).forEach { file ->
      check(entries(file).intersect(protocolClasses + credentialsClass + domainClass).isEmpty()) {
        "Test protocol classes leaked into production artifact $file"
      }
    }
  }
}
// Preserve the existing verification command while checking all deployment fixtures.
tasks.register("verifyFunctionFixturePackaging") { dependsOn(verifyClientServerFixturePackaging) }
tasks.named("integrationTest") { dependsOn(verifyClientServerFixturePackaging) }
tasks.check { dependsOn(verifyClientServerFixturePackaging) }

// Transitional verification of the actual migrated sources. These source sets do not inherit
// main/test's native dependencies; expand their inventory as the migration advances.
val spiCacheSourceFiles = listOf(
  "org/springframework/data/gemfire/GemfireExceptionTranslator.java",
  "org/springframework/data/gemfire/GemfireQueryException.java",
  "org/springframework/data/gemfire/GemfireIndexException.java",
  "org/springframework/data/gemfire/GemfireCancellationException.java",
  "org/springframework/data/gemfire/GemfireSystemException.java",
  "org/springframework/data/gemfire/DriverConnectionFactoryBean.java",
  "org/springframework/data/gemfire/DriverPoolFactoryBean.java",
  "org/springframework/data/gemfire/CacheResolver.java",
  "org/springframework/data/gemfire/RegionResolver.java",
  "org/springframework/data/gemfire/support/AbstractCachingCacheResolver.java",
  "org/springframework/data/gemfire/support/SingleCacheCacheResolver.java",
  "org/springframework/data/gemfire/support/BeanFactoryCacheResolver.java",
  "org/springframework/data/gemfire/support/ComposableCacheResolver.java",
  "org/springframework/data/gemfire/support/AbstractCachingRegionResolver.java",
  "org/springframework/data/gemfire/support/SingleRegionRegionResolver.java",
  "org/springframework/data/gemfire/support/BeanFactoryRegionResolver.java",
  "org/springframework/data/gemfire/support/RegionServiceRegionResolver.java",
  "org/springframework/data/gemfire/support/ComposableRegionResolver.java",
  "org/springframework/data/gemfire/util/ArrayUtils.java",
  "org/springframework/data/gemfire/util/CollectionUtils.java",
  "org/springframework/data/gemfire/cache/**/*.java",
  "org/springframework/data/gemfire/util/RegionPaths.java"
)
val spiCacheTestFiles = listOf(
  "org/springframework/data/gemfire/GemfireExceptionTranslatorTests.java",
  "org/springframework/data/gemfire/DriverConnectionFactoryBeanTests.java",
  "org/springframework/data/gemfire/DriverPoolFactoryBeanTests.java",
  "org/springframework/data/gemfire/support/InjectedPoolIntegrationTests.java",
  "org/springframework/data/gemfire/CacheResolverUnitTests.java",
  "org/springframework/data/gemfire/RegionResolverUnitTests.java",
  "org/springframework/data/gemfire/support/AbstractCachingCacheResolverUnitTests.java",
  "org/springframework/data/gemfire/support/SingleCacheCacheResolverUnitTests.java",
  "org/springframework/data/gemfire/support/BeanFactoryCacheResolverUnitTests.java",
  "org/springframework/data/gemfire/support/ComposableCacheResolverUnitTests.java",
  "org/springframework/data/gemfire/support/AbstractCachingRegionResolverUnitTests.java",
  "org/springframework/data/gemfire/support/SingleRegionRegionResolverUnitTests.java",
  "org/springframework/data/gemfire/support/BeanFactoryRegionResolverUnitTests.java",
  "org/springframework/data/gemfire/support/RegionServiceRegionResolverUnitTests.java",
  "org/springframework/data/gemfire/support/ComposableRegionResolverUnitTests.java",
  "org/springframework/data/gemfire/support/InjectedResolverIntegrationTests.java",
  "org/springframework/data/gemfire/util/RegionPathsTests.java",
  "org/springframework/data/gemfire/cache/CallableCacheLoaderAdapterTest.java",
  "org/springframework/data/gemfire/cache/GemfireCacheUnitTests.java",
  "org/springframework/data/gemfire/cache/GemfireCacheManagerUnitTests.java",
  "org/springframework/data/gemfire/cache/config/GemfireCachingConfigurationTests.java",
  "org/springframework/data/gemfire/cache/ClientCacheManagerIntegrationTests.java",
  "org/springframework/data/gemfire/cache/CachingWithGemFireIntegrationTests.java",
  "org/springframework/data/gemfire/cache/CacheErrorHandlerIntegrationTests.java"
)
val spiCache = sourceSets.create("spiCache") {
  java.setSrcDirs(sourceSets.main.get().java.srcDirs)
  java.setIncludes(spiCacheSourceFiles)
}
val spiCacheTest = sourceSets.create("spiCacheTest") {
  java.setSrcDirs(sourceSets.test.get().java.srcDirs + sourceSets["integrationTest"].java.srcDirs)
  java.setIncludes(spiCacheTestFiles)
  resources.setSrcDirs(sourceSets.test.get().resources.srcDirs)
  resources.include("org/springframework/data/gemfire/cache/*-context.xml",
    "org/springframework/data/gemfire/support/*-context.xml")
  compileClasspath += spiCache.output
  runtimeClasspath += spiCache.output
}
val spiCacheIntegrationTest = sourceSets.create("spiCacheIntegrationTest") {
  java.setSrcDirs(sourceSets.test.get().java.srcDirs + sourceSets["integrationTest"].java.srcDirs)
  java.setIncludes(spiCacheTestFiles)
  resources.setSrcDirs(sourceSets.test.get().resources.srcDirs)
  resources.include("org/springframework/data/gemfire/cache/*-context.xml",
    "org/springframework/data/gemfire/support/*-context.xml")
  compileClasspath += spiCache.output
  runtimeClasspath += spiCache.output
}
configurations[spiCacheTest.implementationConfigurationName].extendsFrom(configurations[spiCache.implementationConfigurationName])
configurations[spiCacheIntegrationTest.implementationConfigurationName].extendsFrom(configurations[spiCache.implementationConfigurationName])
dependencies {
  add(spiCache.implementationConfigurationName, project(":spring-data-gemfire-driver-core"))
  add(spiCache.implementationConfigurationName, platform(libs.spring.framework.bom))
  add(spiCache.implementationConfigurationName, "org.springframework:spring-context-support")
  listOf(spiCacheTest, spiCacheIntegrationTest).forEach { suite ->
    add(suite.implementationConfigurationName, platform(libs.junit.jupiter.bom))
    add(suite.implementationConfigurationName, libs.junit.jupiter.api)
    add(suite.implementationConfigurationName, libs.assertJ)
    add(suite.implementationConfigurationName, libs.junit)
    add(suite.implementationConfigurationName, libs.mockito)
    add(suite.implementationConfigurationName, libs.multithreadedtc)
    add(suite.runtimeOnlyConfigurationName, libs.junit.vintage.engine)
    add(suite.runtimeOnlyConfigurationName, libs.junit.jupiter.engine)
    add(suite.runtimeOnlyConfigurationName, libs.junit.platform.launcher)
  }
  add(spiCacheTest.runtimeOnlyConfigurationName, project(":spring-data-gemfire-driver-mock"))
  add(spiCacheIntegrationTest.runtimeOnlyConfigurationName, project(":spring-data-gemfire-driver-10.4"))
}
val verifySpiCacheBoundary = tasks.register("verifySpiCacheBoundary") {
  val nativeFreeClasspaths = listOf(configurations[spiCache.compileClasspathConfigurationName],
    configurations[spiCacheTest.runtimeClasspathConfigurationName])
  inputs.files(spiCache.allJava, spiCacheTest.allJava, nativeFreeClasspaths)
  doLast {
    (spiCache.allJava.files + spiCacheTest.allJava.files).forEach { source ->
      check(!source.readText().contains("org.apache.geode")) { "Native reference in migrated source: $source" }
    }
    nativeFreeClasspaths.forEach { classpath ->
      classpath.incoming.resolutionResult.allComponents.forEach { component ->
        val id = component.id as? org.gradle.api.artifacts.component.ModuleComponentIdentifier
        check(id == null || !(id.group.startsWith("org.apache.geode") ||
          (id.group == "com.vmware.gemfire" && id.module.startsWith("gemfire-")))) {
          "Native GemFire dependency on migrated cache/utility/resolver/connection classpath: $id"
        }
      }
      classpath.files.filter { it.extension == "jar" }.forEach { jar ->
        ZipFile(jar).use { zip ->
          check(zip.entries().asSequence().none { it.name.startsWith("org/apache/geode/") }) {
            "Native GemFire classes on migrated cache/utility/resolver/connection classpath: ${jar.name}"
          }
        }
      }
    }
  }
}
val spiCacheTestTask = tasks.register<Test>("spiCacheTest") {
  description = "Tests the migrated Spring Cache, utility, resolver and connection sources with a native-free mock runtime."
  group = "verification"
  testClassesDirs = spiCacheTest.output.classesDirs
  classpath = spiCacheTest.runtimeClasspath
  dependsOn(verifySpiCacheBoundary)
}
val spiCacheIntegrationTestTask = tasks.register<Test>("spiCacheIntegrationTest") {
  description = "Runs the same Spring Cache, utility, resolver and connection behavior tests against the resolved native 10.4 driver."
  group = "verification"
  testClassesDirs = spiCacheIntegrationTest.output.classesDirs
  classpath = spiCacheIntegrationTest.runtimeClasspath
  maxParallelForks = 1
  forkEvery = 1
}
tasks.check { dependsOn(spiCacheTestTask, spiCacheIntegrationTestTask) }

configurations["integrationTestRuntimeClasspath"].exclude(
  group = "com.vmware.gemfire", module = "spring-data-gemfire-driver-mock")
dependencies {
  add("integrationTestRuntimeOnly", project(":spring-data-gemfire-driver-10.4"))
}
