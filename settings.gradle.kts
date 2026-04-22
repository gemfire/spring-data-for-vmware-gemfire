/*
 * Copyright (c) 2026 Broadcom. All rights reserved.
 */

import java.io.FileInputStream
import java.util.*

pluginManagement {
  includeBuild("build-tools/publishing")
  includeBuild("build-tools/convention-plugins")
  repositories {
    maven { url = uri("https://repo.spring.io/milestone") }
    gradlePluginPortal()
  }
}

include("spring-data-vmware-gemfire")

project(":spring-data-vmware-gemfire").name = "spring-data-vmware-gemfire"

rootProject.name = "spring-data-for-vmware-gemfire"

dependencyResolutionManagement {
  versionCatalogs {
    create("libs") {
      val properties = Properties()
      properties.load(FileInputStream("gradle.properties"))
      versionOverrideFromProperties(this, properties)
    }
  }
}

private fun versionOverrideFromProperty(versionCatalogBuilder: VersionCatalogBuilder, propertyName: String, propertiesFile: Properties): String {
  val propertyValue = providers.systemProperty(propertyName).getOrElse(propertiesFile.getProperty(propertyName))
  return versionCatalogBuilder.version(propertyName, propertyValue)
}

private fun versionOverrideFromProperties(versionCatalogBuilder: VersionCatalogBuilder, properties: Properties) {
  versionOverrideFromProperty(versionCatalogBuilder, "gemfireVersion", properties)
}

include("spring-data-vmware-gemfire")
include("spring-test-vmware-gemfire")
include("gud-api")
include("gud-core")
include("gud-driver-gemfire-10.0")
include("gud-driver-gemfire-10.1")
include("gud-driver-gemfire-10.2")
include("gud-driver-gemfire-10.3")
include("gud-driver-mock")
include("TestCachingApp")
