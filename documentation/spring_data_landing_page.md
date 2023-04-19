---
title: Spring Data for VMware GemFire Quick Start
---

Spring Data for VMware GemFire brings the full power of the Spring Framework to your VMware GemFire applications.The primary goal of the Spring Data for VMware GemFire project is to make it easier to build highly scalable Spring powered applications using VMware GemFire for distributed data management.

This reference guide explains how to add the Spring Data for VMware GemFire dependency to your project. Once the dependency has been added, refer to the [Spring Data for VMware GemFire Reference Guide](https://docs.spring.io/spring-data/geode/docs/current/reference/html/) for in-depth information about using the dependency.

## Compatibility

Spring Data for VMware GemFire 1.1.0 is compatibile with the following:

<table>
    <tr>
        <td>Spring Data</td>
        <td>2.6, 2.7, 3.0</td>
    </tr>
    <tr>
        <td>GemFire</td>
        <td>9.15.0 to 9.15.4</td>
    </tr>
</table>


## Release Notes:

### 1.1.1

* Update to latest Spring Data patch version (3.0.4, 2.7.10 and 2.6.10)
* Update to latest Spring Framework patch version (6.0.7 and 5.3.26)
* Update all dependencies to latest patch version

### 1.1.0

* First release that removes dependency on Spring Data For Apache Geode
* **"Bring your own GemFire" (ByoG)**:  You can now explicitly specify the GemFire version that you need. See below for examples of declaring the GemFire version. ByoG requires that you provide a working version of VMware GemFire.

### 1.0.0

* Initial Spring Data For VMware GemFire for VMware GemFire, still dependent on Spring Data For Apache Geode

## Add Spring Data for VMware GemFire to a Project

The Spring Data for VMware GemFire dependencies are available from the [Pivotal Commercial Maven Repository](https://commercial-repo.pivotal.io/login/auth). Access to the Pivotal Commercial Maven Repository requires a one-time registration step to create an account.

Spring Data for VMware GemFire requires users to add the GemFire repository to their projects.

To add Spring Data for VMware GemFire to a project:

1. In a browser, navigate to the [Pivotal Commercial Maven Repository](https://commercial-repo.pivotal.io/login/auth).

1. Click the **Create Account** link.

1. Complete the information in the registration page.

1. Click **Register**.

1. After registering, you will receive a confirmation email. Follow the instruction in this email to activate your account.

1. After account activation, log in to the [Pivotal Commercial Maven Repository](https://commercial-repo.pivotal.io/login/auth) to access the configuration information found in [gemfire-release-repo](https://commercial-repo.pivotal.io/repository/gemfire-release-repo).

1. Add the GemFire repository to your project:

    * **Maven**: Add the following block to the `pom.xml` file:

        ```
        <repository>
            <id>gemfire-release-repo</id>
            <name>VMware GemFire Release Repository</name>
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

1. After you have set up the repository and credentials, add the Spring Data for VMware GemFire dependency to your application.

    For version 1.0.0:

    * **Maven**: Add the following to your `pom.xml` file. Replace `VERSION` with the current version of Spring Data for VMware GemFire available.

        ```
        <dependency>
            <groupId>com.vmware.gemfire</groupId>
            <artifactId>spring-data-2.7-gemfire-9.15</artifactId>
            <version>VERSION</version>
        </dependency>
        ```

    * **Gradle**: Add the following to your `build.gradle` file. Replace `VERSION` with the current version of Spring Data for VMware GemFire available.

        ```
        dependencies {
            implementation "com.vmware.gemfire:spring-data-2.7-gemfire-9.15:VERSION"
        
        ```

    For version 1.1.0 and later:

    Starting in version 1.1.0, you will be required to "Bring Your Own GemFire," which will allow for improved flexibility with GemFire patch versions. In addition to the Spring Data for VMware GemFire dependency, you must add an explicit dependency on the desired version of GemFire. The required dependencies will differ for clients and servers.

    For clients:

    * **Maven**: Add the following to your `pom.xml` file. Replace `VERSION` with the current version of Spring Data for VMware GemFire available and `GEMFIRE_VERSION` with the version of VMware GemFire being used for the project.

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

    * **Gradle**: Add the following to your `build.gradle` file. Replace `VERSION` with the current version of Spring Data for VMware GemFire available and `GEMFIRE_VERSION` with the version of VMware GemFire being used for the project.

        ```
        dependencies {
            implementation "com.vmware.gemfire:spring-data-2.7-gemfire-9.15:VERSION"
            implementation "com.vmware.gemfire:geode-core:GEMFIRE_VERSION"
            // if using continuous queries
            implementation "com.vmware.gemfire:geode-cq:GEMFIRE_VERSION"
        }
        ```

    For servers:

    NOTE: The server dependencies are only required if the user is starting an embedded GemFire server using Spring.

    * **Maven**: Add the following to your `pom.xml` file. Replace `VERSION` with the current version of Spring Data for VMware GemFire available and `GEMFIRE_VERSION` with the version of VMware GemFire being used for the project.

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

    * **Gradle**: Add the following to your `build.gradle` file. Replace `VERSION` with the current version of Spring Data for VMware GemFire available and `GEMFIRE_VERSION` with the version of VMware GemFire being used for the project.

        ```
        dependencies {
            implementation "com.vmware.gemfire:spring-data-2.7-gemfire-9.15:VERSION"
            implementation ("com.vmware.gemfire:geode-server-all:GEMFIRE_VERSION"){
                exclude group: 'com.vmware.gemfire', module: 'geode-log4j'
            }
        }
        ```

1. Your application is now ready to connect with your GemFire instance.

## Reference Guide

For further information, refer to the [Spring Data for VMware GemFire Reference Guide](https://docs.spring.io/spring-data/geode/docs/current/reference/html/).
