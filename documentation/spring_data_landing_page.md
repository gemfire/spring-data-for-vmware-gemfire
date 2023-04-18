---
title: [spring-data-gemfire-name] Quick Start
---

[spring-data-gemfire-name] brings the full power of the Spring Framework to your [vmware-gemfire-name] applications.The primary goal of the [spring-data-gemfire-name] project is to make it easier to build highly scalable Spring powered applications using [vmware-gemfire-name] for distributed data management.

This reference guide explains how to add the [spring-data-gemfire-name] dependency to your project. Once the dependency has been added, refer to the [[spring-data-gemfire-name] Reference Guide](https://docs.spring.io/spring-data/geode/docs/current/reference/html/) for in-depth information about using the dependency.

## Release Notes:
### 1.1.1
* Update to latest Spring Data patch version (3.0.4, 2.7.10 and 2.6.10)
* Update to latest Spring Framework patch version (6.0.7 and 5.3.26)
* Update all dependencies to latest patch version

### 1.1.0
* First release that removes dependency on [spring-data-gemfire-name]
* "ByoG" - Bring your own [vmware-gemfire-short-name]. Requires users to provide a working version of [vmware-gemfire-name].

### 1.0.0
* Initial [spring-data-gemfire-name] for [vmware-gemfire-name], still dependent on [spring-data-gemfire-name]


## Add [spring-data-gemfire-name] to a Project

The [spring-data-gemfire-name] dependencies are available from the [Pivotal Commercial Maven Repository](https://commercial-repo.pivotal.io/login/auth). Access to the Pivotal Commercial Maven Repository requires a one-time registration step to create an account.

[spring-data-gemfire-name] requires users to add the [vmware-gemfire-short-name] repository to their projects.

To add [spring-data-gemfire-name] to a project:

1. In a browser, navigate to the [Pivotal Commercial Maven Repository](https://commercial-repo.pivotal.io/login/auth).

1. Click the **Create Account** link.

1. Complete the information in the registration page.

1. Click **Register**.

1. After registering, you will receive a confirmation email. Follow the instruction in this email to activate your account.

1. After account activation, log in to the [Pivotal Commercial Maven Repository](https://commercial-repo.pivotal.io/login/auth) to access the configuration information found in [gemfire-release-repo](https://commercial-repo.pivotal.io/repository/gemfire-release-repo).

1. Add the [vmware-gemfire-short-name] repository to your project:

    * **Maven**: Add the following block to the `pom.xml` file:

        ```
        <repository>
            <id>gemfire-release-repo</id>
            <name>[vmware-gemfire-name] Release Repository</name>
            <url>https://commercial-repo.pivotal.io/data3/gemfire-release-repo/gemfire</url>
        </repository>
        ```

    * **Gradle**: Add the following block to the `repositories` section of the `build.gradle` file:

        ```
        repositories {
            mavenCentral()
            maven {
                credentials {
                    username "$gemfireRepoUsername"
                    password "$gemfireRepoPassword"
                }
                url = uri("https://commercial-repo.pivotal.io/data3/gemfire-release-repo/gemfire")
            }
        }
        ```

1. Add your Pivotal Commercial Maven Repository credentials.

    * **Maven**: Add the following to the `.m2/settings.xml` file. Replace `MY-USERNAME@example` and `MY-DECRYPTED-PASSWORD` with your Pivotal Commercial Maven Repository credentials.

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

    * **Gradle**: Add the following to the local (`.gradle/gradle.properties`) or project `gradle.properties` file. Replace `MY-USERNAME@example` and `MY-DECRYPTED-PASSWORD` with your Pivotal Commercial Maven Repository credentials.

        ```
        gemfireRepoUsername=MY-USERNAME@example.com 
        gemfireRepoPassword=MY-DECRYPTED-PASSWORD
        ```

1. After you have set up the repository and credentials, add the [spring-data-gemfire-name] dependency to your application.

    For version 1.0.0:

    * **Maven**: Add the following to your `pom.xml` file. Replace `VERSION` with the current version of [spring-data-gemfire-name] available.

        ```
        <dependency>
            <groupId>com.vmware.gemfire</groupId>
            <artifactId>spring-data-2.7-gemfire-9.15</artifactId>
            <version>VERSION</version>
        </dependency>
        ```

    * **Gradle**: Add the following to your `build.gradle` file. Replace `VERSION` with the current version of [spring-data-gemfire-name] available.

        ```
        dependencies {
            implementation "com.vmware.gemfire:spring-data-2.7-gemfire-9.15:VERSION"
        
        ```

    For version 1.1.0 and later:

    Starting in version 1.1.0, you will be required to "Bring Your Own [vmware-gemfire-short-name]," which will allow for improved flexibility with [vmware-gemfire-short-name] patch versions. In addition to the [spring-data-gemfire-name] dependency, you must add an explicit dependency on the desired version of [vmware-gemfire-short-name]. The required dependencies will differ for clients and servers.

    For clients:

    * **Maven**: Add the following to your `pom.xml` file. Replace `VERSION` with the current version of [spring-data-gemfire-name] available and `GEMFIRE_VERSION` with the version of [vmware-gemfire-name] being used for the project.

        ```
        <dependency>
            <groupId>com.vmware.gemfire</groupId>
            <artifactId>spring-data-2.7-gemfire-9.15</artifactId>
            <version>VERSION</version>
        </dependency>
        <dependency>
            <groupId>com.vmware.gemfire</groupId>
            <artifactId>geode-core</artifactId>
            <version>GEMFIRE_VERSION</version>
        </dependency>
        <!--if using continuous queries-->
        <dependency>
            <groupId>com.vmware.gemfire</groupId>
            <artifactId>geode-cq</artifactId>
            <version>GEMFIRE_VERSION</version>
        </dependency>
        ```

    * **Gradle**: Add the following to your `build.gradle` file. Replace `VERSION` with the current version of [spring-data-gemfire-name] available and `GEMFIRE_VERSION` with the version of [vmware-gemfire-name] being used for the project.

        ```
        dependencies {
            implementation "com.vmware.gemfire:spring-data-2.7-gemfire-9.15:VERSION"
            implementation "com.vmware.gemfire:geode-core:GEMFIRE_VERSION"
            // if using continuous queries
            implementation "com.vmware.gemfire:geode-cq:GEMFIRE_VERSION"
        }
        ```

    For servers:

    NOTE: The server dependencies are only required if the user is starting an embedded [vmware-gemfire-short-name] server using Spring.

    * **Maven**: Add the following to your `pom.xml` file. Replace `VERSION` with the current version of [spring-data-gemfire-name] available and `GEMFIRE_VERSION` with the version of [vmware-gemfire-name] being used for the project.

        ```
        <dependency>
           <groupId>com.vmware.gemfire</groupId>
             <artifactId>spring-data-2.7-gemfire-9.15</artifactId>
             <version>VERSION</version>
         </dependency>
         <dependency>
             <groupId>com.vmware.gemfire</groupId>
             <artifactId>geode-server-all</artifactId>
             <version>GEMFIRE_VERSION</version>
             <exclusions>
                 <exclusion>
                     <groupId>com.vmware.gemfire</groupId>
                     <artifactId>geode-log4j</artifactId>
                 </exclusion>
             </exclusions>
         </dependency>
        ```

    * **Gradle**: Add the following to your `build.gradle` file. Replace `VERSION` with the current version of [spring-data-gemfire-name] available and `GEMFIRE_VERSION` with the version of [vmware-gemfire-name] being used for the project.

        ```
        dependencies {
            implementation "com.vmware.gemfire:spring-data-2.7-gemfire-9.15:VERSION"
            implementation ("com.vmware.gemfire:geode-server-all:GEMFIRE_VERSION"){
                exclude group: 'com.vmware.gemfire', module: 'geode-log4j'
            }
        }
        ```

1. Your application is now ready to connect with your [vmware-gemfire-short-name] instance.

## Reference Guide

For further information, refer to the [[spring-data-gemfire-name] Reference Guide](https://docs.spring.io/spring-data/geode/docs/current/reference/html/).
