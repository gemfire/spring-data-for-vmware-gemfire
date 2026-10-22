/* Copyright 2026 Broadcom. SPDX-License-Identifier: Apache-2.0 */
import java.util.Properties
import org.gradle.jvm.toolchain.JavaToolchainService

val toolDirectory = rootProject.file("build-tools/spi-compatibility")
val toolClasspath = configurations.create("spiCompatibilityTool") {
    isCanBeConsumed = false
    isCanBeResolved = true
}
dependencies { add(toolClasspath.name, "com.github.siom79.japicmp:japicmp:0.23.1") }
val toolchains = extensions.getByType<JavaToolchainService>()
val toolJava = toolchains.launcherFor { languageVersion.set(JavaLanguageVersion.of(17)) }
val compileTool = tasks.register<JavaCompile>("compileSpiCompatibilityTool") {
    source(fileTree(toolDirectory) { include("*.java") })
    classpath = toolClasspath
    destinationDirectory.set(layout.buildDirectory.dir("spi-compatibility-classes"))
    javaCompiler.set(toolchains.compilerFor { languageVersion.set(JavaLanguageVersion.of(17)) })
}
val policyFile = toolDirectory.resolve("baseline.properties")
val policy = Properties().apply { policyFile.inputStream().use { load(it) } }
check(policy.getProperty("status") in listOf("unreleased", "released")) { "Invalid SPI baseline status" }
val baseline = configurations.create("releasedSpiBaseline") {
    isCanBeConsumed = false
    isCanBeResolved = true
    resolutionStrategy.useGlobalDependencySubstitutionRules.set(false)
}
if (policy.getProperty("status") == "released") {
    val coordinates = policy.getProperty("coordinates", "")
    check(coordinates.matches(Regex("[^:]+:spring-data-gemfire-driver-spi:[0-9]+\\.[0-9]+"))) {
        "Released SPI baseline must have exact, non-snapshot Maven coordinates"
    }
    dependencies.add(baseline.name, coordinates)
}
val fixtures = tasks.register<JavaExec>("testSpiCompatibility") {
    group = "verification"
    dependsOn(compileTool)
    javaLauncher.set(toolJava)
    classpath = files(compileTool.map { it.destinationDirectory }, toolClasspath)
    mainClass.set("CompatibilityFixtures")
    args(layout.buildDirectory.dir("spi-compatibility-fixtures").get().asFile)
}
val verify = tasks.register<JavaExec>("verifySpiCompatibility") {
    group = "verification"
    description = "Checks the released SPI ABI baseline, or explicitly reports pre-first-release status."
    dependsOn(compileTool, fixtures, ":spring-data-gemfire-driver-spi:jar")
    javaLauncher.set(toolJava)
    classpath = files(compileTool.map { it.destinationDirectory }, toolClasspath)
    mainClass.set("AbiCheck")
    inputs.file(policyFile)
    // Always report first-release status and evaluate the resolved dependency graph.
    outputs.upToDateWhen { false }
    doFirst {
        val spi = project(":spring-data-gemfire-driver-spi")
        val current = spi.tasks.named<Jar>("jar").get().archiveFile.get().asFile
        val oldJar = if (policy.getProperty("status") == "released") {
            baseline.resolvedConfiguration.resolvedArtifacts.single {
                it.name == "spring-data-gemfire-driver-spi"
            }.file.absolutePath
        } else ""
        args(policyFile, current, oldJar, baseline.asPath,
            spi.configurations.getByName("compileClasspath").asPath)
    }
}
gradle.projectsEvaluated {
    val modules = rootProject.subprojects.filter { it.name.startsWith("spring-data-gemfire-driver-") }
    (modules + rootProject).forEach { module ->
        module.tasks.named("check") { dependsOn(verify) }
        module.tasks.matching { it.name.startsWith("publish") || it.name == "artifactoryPublish" }.configureEach {
            dependsOn(verify)
        }
    }
    // CI/release entry point includes artifact checks and executable old-driver runtime fixtures.
    val releaseCheck = rootProject.tasks.register("verifyDriverCompatibility") {
        group = "verification"
        dependsOn(verify, ":spring-data-gemfire-driver-core:test")
        modules.forEach { module -> module.tasks.findByName("verifyDriverStamp")?.let { dependsOn(it) } }
    }
    rootProject.tasks.named("check") { dependsOn(releaseCheck) }
    allprojects {
        tasks.matching { it.name.startsWith("publish") || it.name == "artifactoryPublish" }.configureEach {
            dependsOn(releaseCheck)
        }
    }
}
