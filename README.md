<!--
  ~ Copyright (c) VMware, Inc. 2022. All rights reserved.
  ~ SPDX-License-Identifier: Apache-2.0
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

## Code of Conduct
This project adheres to the Contributor Covenant [code of conduct](https://github.com/gemfire/spring-data-for-vmware-gemfire/CODE-OF-CONDUCT.md). By participating, you are expected to uphold this code. 

## Reporting Issues
In the event that issue were to be found, please raise a [GitHub issue](https://github.com/gemfire/spring-data-for-vmware-gemfire/issues).
Please provide:
* Project version
* Issue description
* Ways to reproduce issue AND/OR links to a repo which demonstrates the issue raised.
