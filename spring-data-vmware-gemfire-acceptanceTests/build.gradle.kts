/*
 * Copyright (c) VMware, Inc. 2024. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
import org.apache.tools.ant.taskdefs.condition.Os
import org.gradle.jvm.tasks.Jar

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
    alias(libs.plugins.lombok)
    alias(libs.plugins.dependency.management)
}

tasks.withType<JavaCompile>().configureEach {
    options.compilerArgs.add("-parameters")
}

java {
    toolchain { languageVersion.set(JavaLanguageVersion.of(17)) }
}

repositories {
    mavenCentral()
}

dependencies {
    val gemfireVersion: String by project
    val springDataVersion: String by project

    testImplementation(libs.cache.api)
    testImplementation(libs.spring.context.support)
    testImplementation(libs.spring.tx)
    testImplementation(libs.spring.web)
    testImplementation(libs.spring.data.commons)
    testImplementation(libs.spring.shiro)
    testImplementation(libs.aspectJ)
    testImplementation(libs.bundles.jackson)
//    runtimeOnly(libs.antlr)
//    compileOnly(libs.cdi.api) {
//        exclude("javax.annotation", "jsr250-api")
//    }

    testImplementation(project(":spring-data-vmware-gemfire-testing"))
    testImplementation(project(":spring-data-vmware-gemfire-client"))
    testImplementation(project(":spring-data-vmware-gemfire-server"))

    testImplementation(libs.bundles.gemfire)

    testImplementation(libs.cdi.api) {
        exclude("javax.annotation", "jsr250-api")
    }
    testImplementation(libs.interceptor.api)
    testImplementation(libs.logback)
    testImplementation(libs.log4J)
    testImplementation(libs.annotation.api)
    testImplementation(libs.derby)
    testImplementation(variantOf(libs.openwebbeans.se) { classifier("jakarta")})
    testImplementation(variantOf(libs.openwebbeans.spi) { classifier("jakarta")})
    testImplementation(variantOf(libs.openwebbeans.impl) { classifier("jakarta")})
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
    testImplementation(libs.spring.test)
    testImplementation(libs.spring.boot)
    testImplementation(libs.awaitility)
    testImplementation(libs.gemfire.testcontainers)
}

gradle.taskGraph.whenReady {
    tasks.test {
        tasks.named("check").get().dependsOn(this)
        jvmArgs("-XX:+HeapDumpOnOutOfMemoryError", "-ea",
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
            "--add-opens=java.base/javax.net.ssl=ALL-UNNAMED")

        if (!Os.isFamily(Os.FAMILY_WINDOWS)) {
            jvmArgs("-XX:+UseZGC")
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

tasks.register<Jar>("testJar") {
    from(sourceSets.test.get().output)
    from(sourceSets.main.get().output)
    archiveFileName = "testJar.jar"
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}

tasks.getByName<Test>("test") {
    dependsOn(tasks.named("testJar"))
    systemProperty("TEST_JAR_PATH", tasks.getByName<Jar>("testJar").outputs.files.singleFile.absolutePath)
    val springTestGemfireDockerImage: String by project
    systemProperty("spring.test.gemfire.docker.image", springTestGemfireDockerImage)
}
