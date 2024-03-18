plugins {
    id("java-library")
    alias(libs.plugins.lombok)
    alias(libs.plugins.dependency.management)
}

group = "org.example"
version = "1.0.1-build.9999"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(project(":spring-data-vmware-gemfire-server"))
    testImplementation(project(":spring-data-vmware-gemfire-client"))

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
    testImplementation(libs.bundles.jackson)
    testImplementation(libs.snappy)
    testImplementation(libs.spring.shell) {
        exclude("com.google.guava", "guava")
    }
    testImplementation(libs.multithreadedtc)
    testImplementation(libs.cache.api)
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

tasks.test {
    useJUnitPlatform()
}