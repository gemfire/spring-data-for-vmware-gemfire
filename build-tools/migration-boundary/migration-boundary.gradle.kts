/* Copyright 2026 Broadcom. SPDX-License-Identifier: Apache-2.0 */
import groovy.json.JsonOutput
import org.gradle.api.artifacts.component.ModuleComponentIdentifier
import org.gradle.api.artifacts.component.ProjectComponentIdentifier
import org.gradle.api.tasks.SourceSetContainer
import org.gradle.jvm.toolchain.JavaToolchainService

// Whole-module checks complement the temporary spiCache source sets. A baseline pass is not compliance.
val boundaryTool = rootProject.file("build-tools/migration-boundary")
val boundaryOutput = rootProject.layout.buildDirectory.dir("reports/migration-boundary")
val toolchains = extensions.getByType<JavaToolchainService>()
val boundaryJava = toolchains.launcherFor { languageVersion.set(JavaLanguageVersion.of(17)) }
val compileBoundaryTool = tasks.register<JavaCompile>("compileMigrationBoundaryTool") {
    source(boundaryTool.resolve("SourceFacts.java"))
    classpath = files()
    destinationDirectory.set(layout.buildDirectory.dir("migration-boundary-classes"))
    javaCompiler.set(toolchains.compilerFor { languageVersion.set(JavaLanguageVersion.of(17)) })
}
val exportBoundary = tasks.register("exportMigrationBoundaryClasspaths") {
    outputs.file(boundaryOutput.map { it.file("classpaths.json") })
    // Resolution and source roots must reflect this invocation, including init-script/build changes.
    outputs.upToDateWhen { false }
}
val boundaryFixtures = tasks.register<Exec>("testMigrationBoundary") {
    group = "verification"
    description = "Exercises boundary rules with controlled positive and negative fixtures."
    dependsOn(compileBoundaryTool)
    doFirst {
        commandLine("python3", boundaryTool.resolve("test_boundary.py"), "--java", boundaryJava.get().executablePath.asFile,
            "--classes", compileBoundaryTool.get().destinationDirectory.get().asFile)
    }
}
fun boundaryCheck(name: String, mode: String) = tasks.register<Exec>(name) {
    group = "verification"
    description = when (mode) {
        "strict" -> "Requires zero native boundary violations across the whole module."
        "prune" -> "Removes resolved baseline entries only; refuses additions and outstanding regressions."
        else -> "Rejects migration regressions against the explicit transitional baseline; NOT native-free compliance."
    }
    dependsOn(compileBoundaryTool, exportBoundary, boundaryFixtures)
    doFirst {
        commandLine("python3", boundaryTool.resolve("check_boundary.py"),
            "--root", rootProject.projectDir, "--java", boundaryJava.get().executablePath.asFile,
            "--classes", compileBoundaryTool.get().destinationDirectory.get().asFile,
            "--classpaths", boundaryOutput.get().file("classpaths.json").asFile,
            "--mode", mode)
    }
}
val regressionBoundary = boundaryCheck("verifyMigrationBoundary", "regression")
boundaryCheck("verifyNativeFreeBoundary", "strict")
boundaryCheck("pruneMigrationBoundaryBaseline", "prune")

gradle.projectsEvaluated {
    val modules = rootProject.subprojects.filter {
        it.name == "spring-data-vmware-gemfire" || it.name.startsWith("spring-data-gemfire-driver-")
    }.sortedBy { it.name }
    val classpaths = modules.flatMap { module ->
        module.extensions.getByType<SourceSetContainer>().flatMap { source ->
            listOf(source.compileClasspathConfigurationName, source.runtimeClasspathConfigurationName)
                .map { module.configurations.getByName(it) }
        }
    }
    exportBoundary.configure {
        dependsOn(classpaths)
        doLast {
            val model = modules.map { module ->
                mapOf("module" to module.name, "sourceSets" to module.extensions.getByType<SourceSetContainer>().map { source ->
                    mapOf("name" to source.name,
                        "java" to source.java.srcDirs.map { rootProject.relativePath(it) },
                        "resources" to source.resources.srcDirs.map { rootProject.relativePath(it) },
                        "classpaths" to listOf(source.compileClasspathConfigurationName, source.runtimeClasspathConfigurationName).map { name ->
                            val configuration = module.configurations.getByName(name)
                            mapOf("name" to name, "components" to configuration.incoming.resolutionResult.allComponents.mapNotNull { component ->
                                when (val id = component.id) {
                                    is ModuleComponentIdentifier -> mapOf("group" to id.group, "name" to id.module, "version" to id.version)
                                    is ProjectComponentIdentifier -> if (id.projectPath == module.path) null
                                        else mapOf("project" to id.projectPath.removePrefix(":"))
                                    else -> error("Unrecognized dependency component: $id")
                                }
                            }, "files" to configuration.files.map { it.absolutePath }.sorted())
                        })
                })
            }
            val output = boundaryOutput.get().file("classpaths.json").asFile
            output.parentFile.mkdirs()
            output.writeText(JsonOutput.prettyPrint(JsonOutput.toJson(model)) + "\n")
        }
    }
    (modules + rootProject).forEach { module -> module.tasks.named("check") { dependsOn(regressionBoundary) } }
}
