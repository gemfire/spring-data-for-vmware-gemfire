---
title:  Spring Data for VMware GemFire 2.3 Documentation
---

<!-- 
 Copyright (c) VMware, Inc. 2022. All rights reserved.
 Licensed to the Apache Software Foundation (ASF) under one or more contributor license
 agreements. See the NOTICE file distributed with this work for additional information regarding
 copyright ownership. The ASF licenses this file to You under the Apache License, Version 2.0 (the
 "License"); you may not use this file except in compliance with the License. You may obtain a
 copy of the License at
 
 http://www.apache.org/licenses/LICENSE-2.0
 
 Unless required by applicable law or agreed to in writing, software distributed under the License
 is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 or implied. See the License for the specific language governing permissions and limitations under
 the License.
-->

<!--
Licensed to the Apache Software Foundation (ASF) under one or more
contributor license agreements.  See the NOTICE file distributed with
this work for additional information regarding copyright ownership.
The ASF licenses this file to You under the Apache License, Version 2.0
(the "License"); you may not use this file except in compliance with
the License.  You may obtain a copy of the License at

     http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
-->

Spring Data for VMware GemFire focuses on integrating the Spring Framework's powerful, non-invasive programming model and concepts with GemFire to simplify configuration and development of Java applications when using GemFire as your data management solution.

## <a id="setup"></a>Setup

Spring Data for VMware GemFire and Spring Boot Data for GemFire are the Spring dependencies to use when developing applications for GemFire. This guide is intended to help you set up and start using Spring Data for VMware GemFire and Spring Boot Data for GemFire.

The Spring Data for VMware GemFire dependencies are available from the [Pivotal Commercial Maven Repository](https://commercial-repo.pivotal.io/login/auth). Access to the Pivotal Commercial Maven Repository requires a one-time registration step to create an account.

1. In a browser, navigate to the [Pivotal Commercial Maven Repository](https://commercial-repo.pivotal.io/login/auth).

2. Click the **Create Account** link.

3. Complete the information in the registration page.

4. Click **Register**.

5. After registering, you will receive a confirmation email. Follow the instruction in this email to activate your account.

6. After account activation, log in to the  [Pivotal Commercial Maven Repository](https://commercial-repo.pivotal.io/login/auth) to access the configuration information found in [gemfire-release-repo](https://commercial-repo.pivotal.io/repository/gemfire-release-repo).

7. Add the GemFire repository to your project

    * **Maven**

        Add the following block to the `pom.xml` file:
        ```xml
        <repository>
            <id>gemfire-release-repo</id>
            <name>Pivotal GemFire Release Repository</name>
            <url>https://commercial-repo.pivotal.io/data3/gemfire-release-repo/gemfire</url>
        </repository>
        ```

    * **Gradle**

        Add the following block to the `build.gradle` file:
        ```
        maven {
            credentials {
                username "$gemfireRepoUserName"
                password "$gemfireRepoPassword"
            }
            url = uri("https://commercial-repo.pivotal.io/data3/gemfire-release-repo/gemfire")
        }
        ```

8. Add your Pivotal Commercial Maven Repository credentials.

    * **Maven**

        Add the following to the `.m2/settings.xml` file:
        ```
        <settings>
            <servers>
                <server>
                    <id>gemfire-release-repo</id>
                    <username>MY-USERNAME@example.com</username>
                    <password>MY-DECRYPTED-PASSWORD</password>
                </server>
            </servers>
        </settings>
        ```
        Replace `MY-USERNAME@example` and `MY-DECRYPTED-PASSWORD` with your Pivotal Commercial Maven Repository credentials.

    * **Gradle**

        Add the following to the local (`.gradle/gradle.properties`) or project `gradle.properties` file:
        ```
        gemfireRepoUsername=MY-USERNAME@example.com
        gemfireRepoPassword=MY-DECRYPTED-PASSWORD
        ```
        Replace `MY-USERNAME@example` and `MY-DECRYPTED-PASSWORD` with your Pivotal Commercial Maven Repository credentials.

9. After you have set up the repository and credentials, add the Spring dependencies needed for your application. The `springDataGemFireVersion` and `springBootDataGemFireVersion` variables are property placeholders for the dependency versions.

    * **Spring Data for VMware GemFire**

        * **Maven**

            ```
            <dependency>
                <groupId>com.vmware.gemfire</groupId>
                <artifactId>spring-data-gemfire</artifactId>
                <version>${spring.data.gemFire.version}</version>
            </dependency>
            ```

        * **Gradle**

            ```
            implementation "com.vmware.gemfire:spring-data-gemfire:$springDataGemFireVersion"
            ```

    * **Spring Boot Data for GemFire**

        * **Maven**

            ```
            <dependency>
                <groupId>com.vmware.gemfire.boot</groupId>
                <artifactId>spring-gemfire-starter</artifactId>
                <version>${spring.boot.data.gemFire.version}</version>
            </dependency>
            ```

        * **Gradle**

            ```
            implementation "com.vmware.gemfire.boot:spring-gemfire-starter:$springBootDataGemFireVersion"
            ```
 
Your application is now ready to connect with your GemFire instance.

## <a id="reference-guide"></a>Reference Guide

All reference documentation currently corresponds with the reference documentation for [Spring Data Geode](https://spring.io/projects/spring-data-geode#learn) and [Spring Boot Data Geode](https://docs.spring.io/spring-boot-data-geode-build/current/reference/html5/).

## <a id="release-notes"></a>Spring Data for VMware GemFire Release Notes 

### <a id="release-2-2"></a>v2.2 Release Notes 

* Upgraded to Apache Geode 1.9.0.

* Upgraded to Spring Framework 5.2.0.RELEASE.

* Upgraded to Spring Data Commons 2.2.0.RELEASE.

* Added Annotation configuration support to configure and bootstrap Apache Geode Locator applications using @LocatorApplication.

* Added Annotation configuration support for GatewayReceivers and GatewaySenders.

### <a id="release-2-1"></a>v2.1 Release Notes 

* Upgraded to Apache Geode 1.9.0.

* Upgraded to Spring Framework 5.1.0.RELEASE.

* Upgraded to Spring Data Commons 2.1.0.RELEASE.

* Added support for parallel cache/Region snapshots along with invoking callbacks when loading snapshots.

* Added support for registering QueryPostProcessors to customize the OQL generated fro Repository query methods.

* Added support for include/exclude TypeFilters in o.s.d.g.mapping.MappingPdxSerializer.

### <a id="release-2-0"></a>v2.0 Release Notes 

* Upgraded to Apache Geode 9.1.1.

* Upgraded to Spring Data Commons 2.0.8.RELEASE.

* Upgraded to Spring Framework 5.0.7.RELEASE.

* Reorganized the Spring Data for VMware GemFire codebase by packaging different classes and components by concern.

* Added extensive support for Java 8 types, particularly in the SD Repository abstraction.

* Changed to the Repository interface and abstraction. For example, IDs are no longer required to be java.io.Serializable.

* Set @EnableEntityDefinedRegions annotation ignoreIfExists attribute to true by default.

* Set @Indexed annotation override attribute to false by default.

* Renamed @EnableIndexes to @EnableIndexing.

* Introduced an InterestsBuilder class to express Interests in keys and values between client and server when using JavaConfig.

* Added support in the Annotation configuration model for Off-Heap, Redis Adapter, and Apache Geode's new Security framework.

