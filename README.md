<!--
  ~ Copyright (c) VMware, Inc. 2022. All rights reserved.
  ~ SPDX-License-Identifier: Apache-2.0
-->

<!--
  @AI-Generated
  Generated in whole or in part by Cursor
  Description:
  2026-05-26: Added "Updating Versions" section documenting vCU behavior, updateMinor/updateMajor properties, and gemfireVersion override
-->

# Spring Data For VMware GemFire
This project aims to provide an integration of [VMware GemFire](https://tanzu.vmware.com/gemfire) with [Spring Data](https://spring.io/projects/spring-data) project.

This project builds on the great work already provided by [Spring Data for Apache Geode](https://spring.io/projects/spring-data-geode).

## Project Structure
The current project structure is different to normal projects, as it does not have a `main` or `develop` branch with meaningful code in it. This is because there is no "common" code branch, as this project is a combination of VMware GemFire and Spring Data. Thus all code contributions will be found under different branches.
Current branches are:
* [9.15 - Spring Data 2.6](https://github.com/gemfire/spring-data-for-vmware-gemfire/tree/9.15-SD26)
* [9.15 - Spring Data 2.7](https://github.com/gemfire/spring-data-for-vmware-gemfire/tree/9.15-SD27)

## Versioning
As this project provides an integration between two great products, a versioning schema that adequately represents both products was chosen. The major.minor version component of the GemFire product will be added to the artifact id. The major.minor component from the Spring Data project will be use as the major.minor component of the Spring Data For VMware GemFire version. The patch version of the Spring Data For VMware GemFire project, will be independent of the two projects and will be incremented each time there is a patch version update in either project or there are bug fixes in the Spring Data For VMware GemFire project. 

## Updating Versions

Dependency versions are managed in `gradle/libs.versions.toml` (and `gradle/publishing.versions.toml` for publishing-related dependencies). The [version-catalog-update](https://github.com/littlerobots/version-catalog-update-plugin) (vCU) Gradle plugin is used to detect newer versions and update the catalogs in place.

### Default behaviour — patch updates only

Running the update task without any extra flags only allows **patch-level** (and hotfix-level) upgrades. Concretely, the candidate version must share the same `major.minor` as the currently pinned version; only the third or fourth version component may change.

```bash
./gradlew versionCatalogUpdate
```

Pre-release identifiers are always excluded regardless of mode — any candidate version containing `alpha`, `beta`, `rc`, `snapshot`, `dev`, `preview`, `build`, `milestone`, or the milestone shorthand pattern `*.M<N>` (e.g. `4.0.0.M1`, `6.0.0-M2`) will never be selected.

### Allow minor-version updates — `updateMinor`

Pass the `-PupdateMinor` Gradle property to also allow updates where the **minor** version number increases, as long as the **major** version stays the same.

```bash
./gradlew versionCatalogUpdate -PupdateMinor
```

Example: `3.1.4` → `3.2.0` would be accepted; `4.0.0` would still be rejected.

### Allow major-version updates — `updateMajor`

Pass the `-PupdateMajor` Gradle property to allow **any** stable upgrade, including major-version bumps. This is the most permissive mode and implicitly includes minor and patch updates.

```bash
./gradlew versionCatalogUpdate -PupdateMajor
```

> **Note:** `updateMajor` takes precedence over `updateMinor`. Supplying both flags is equivalent to supplying only `updateMajor`.

### Overriding `gemfireVersion` for a single build

The `gemfireVersion` entry in `gradle/libs.versions.toml` is the authoritative source of truth for normal builds. To compile or test against a **different** GemFire version without modifying the catalog file, pass `gemfireVersion` as either a Gradle property or a Java system property:

```bash
# Gradle property
./gradlew build -PgemfireVersion=10.3.1

# Java system property
./gradlew build -DgemfireVersion=10.3.1
```

The override is wired in `settings.gradle.kts` inside the `versionCatalogs { create("libs") { … } }` block. When the property is present it replaces the `gemfireVersion` version entry at settings-evaluation time, so all subprojects that reference `libs.gemfireVersion` (or any `libs.gemfire.*` library alias) automatically pick up the overridden value without any further changes to the build scripts. The TOML file itself is left untouched.

## Code of Conduct
This project adheres to the Contributor Covenant [code of conduct](https://github.com/gemfire/spring-data-for-vmware-gemfire/CODE-OF-CONDUCT.md). By participating, you are expected to uphold this code. 

## Reporting Issues
In the event that issue were to be found, please raise a [GitHub issue](https://github.com/gemfire/spring-data-for-vmware-gemfire/issues).
Please provide:
* Project version
* Issue description
* Ways to reproduce issue AND/OR links to a repo which demonstrates the issue raised.
