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
    implementation(project(":spring-data-vmware-gemfire-server"))
    implementation(project(":spring-data-vmware-gemfire-client"))

    implementation(libs.bundles.gemfire)

    implementation(libs.cdi.api) {
        exclude("javax.annotation", "jsr250-api")
    }
    implementation(libs.interceptor.api)
    implementation(libs.logback)
    implementation(libs.log4J)
    implementation(libs.annotation.api)
    implementation(libs.derby)
    implementation(variantOf(libs.openwebbeans.se) { classifier("jakarta")})
    implementation(variantOf(libs.openwebbeans.spi) { classifier("jakarta")})
    implementation(variantOf(libs.openwebbeans.impl) { classifier("jakarta")})
    implementation(libs.bundles.jackson)
    implementation(libs.snappy)
    implementation(libs.spring.shell) {
        exclude("com.google.guava", "guava")
    }
    implementation(libs.multithreadedtc)
    implementation(libs.cache.api)
    implementation(libs.junit.jupiter.api)
    implementation(libs.junit.vintage.engine)
    testRuntimeOnly(libs.junit.jupiter.engine)

    implementation(libs.junit)
    implementation(libs.assertJ)
    implementation(libs.mockito)
    implementation(libs.lombok)
    implementation(libs.spring.test)
    implementation(libs.spring.boot)
    implementation(libs.awaitility)
    implementation(libs.gemfire.testcontainers)
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