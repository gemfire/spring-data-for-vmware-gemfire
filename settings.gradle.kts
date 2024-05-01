/*
 * Copyright 2022-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

import java.io.FileInputStream
import java.util.*

pluginManagement {
  includeBuild("build-tools/publishing")
  repositories {
    maven { url = uri("https://repo.spring.io/milestone") }
    gradlePluginPortal()
  }
}

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
