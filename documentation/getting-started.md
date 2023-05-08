---
title: Getting Started
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


The Spring Data for VMware GemFire libraries are available from the [Pivotal Commercial Maven Repository](https://commercial-repo.pivotal.io/login/auth). Access to the Pivotal Commercial Maven Repository requires a one-time registration step to create an account.


### Add The Pivotal Commercial Maven Repository 

1. In a browser, navigate to the [Pivotal Commercial Maven Repository](https://commercial-repo.pivotal.io/login/auth).

1. Click the **Create Account** link.

1. Complete the information in the registration page.

1. Click **Register**.

1. After registering, you will receive a confirmation email. Follow the instruction in this email to activate your account.

1. After account activation, log in to the [Pivotal Commercial Maven Repository](https://commercial-repo.pivotal.io/login/auth) to access the configuration information found in [gemfire-release-repo](https://commercial-repo.pivotal.io/repository/gemfire-release-repo).

1. Add the repository to your project:

  * **Maven**: Add the following block to the `pom.xml` file:

      ```xml
      <repository>
          <id>gemfire-release-repo</id>
          <name>Pivotal GemFire Release Repository</name>
          <url>https://commercial-repo.pivotal.io/data3/gemfire-release-repo/gemfire</url>
      </repository>
      ```

  * **Gradle**: Add the following block to the `repositories` section of the `build.gradle` file:

      ```groovy
      repositories {
          mavenCentral()
          maven {
              credentials {
                  username "$pivotalCommercialMavenRepoUsername"
                  password "$pivotalCommercialMavenRepoPassword"
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
      pivotalCommercialMavenRepoUsername=MY-USERNAME@example.com 
      pivotalCommercialMavenRepoPassword=MY-DECRYPTED-PASSWORD
      ```

### Add The Libraries To The Project

After you have set up the repository and credentials, add the Spring Data for VMware GemFire library to your application. To allow for more flexibility with multiple GemFire version, the Spring Data for VMware GemFire library requires users to add an explicit dependency on the desired version of GemFire. The required dependencies differ depending on whether users a building a client application or a server application.

In the following examples:

- Update the `springDataForGemFire.version` with the version of the library that your project requires. 
- Update the `vmwareGemFire.version` with the version of GemFire that your project requires. 

For **client** applications:

* **Maven**: Add the following to your `pom.xml` file. 

    ```xml
    <properties>
        <springDataForGemFire.version>1.1.1</springDataForGemFire.version>
        <vmwareGemFire.version>9.15.5</vmwareGemFire.version>
    </properties>    
    
    <dependencies>
       <dependency>
           <groupId>com.vmware.gemfire</groupId>
           <artifactId>spring-data-2.7-gemfire-9.15</artifactId>
           <version>${springDataForGemFire.version}</version>
       </dependency>
       <dependency>
           <groupId>com.vmware.gemfire</groupId>
           <artifactId>geode-core</artifactId>
           <version>${vmwareGemFire.version}</version>
       </dependency>
    <!--if using continuous queries-->
       <dependency>
            <groupId>com.vmware.gemfire</groupId>
            <artifactId>geode-cq</artifactId>
            <version>${vmwareGemFire.version}</version>
       </dependency>
    </dependencies>
    ```

* **Gradle**: Add the following to your `build.gradle` file. 
 
    ```groovy
   ext {
        springDataForGemFireVersion = '1.1.1'
        vmwareGemFireVersion = '9.15.5'
   }
        
   dependencies {
        implementation "com.vmware.gemfire:spring-data-2.7-gemfire-9.15:$springDataForGemFireVersion"
        implementation "com.vmware.gemfire:geode-core:$vmwareGemFireVersion"
    // if using continuous queries
        implementation "com.vmware.gemfire:geode-cq:$vmwareGemFireVersion"
    }
    ```

For server applications:

> NOTE: The server dependencies are only required if the user is starting an embedded GemFire server using Spring.

* **Maven**: Add the following to your `pom.xml` file. 
  
    ```xml
  <properties>
        <springDataForGemFire.version>1.1.1</springDataForGemFire.version>
        <vmwareGemFire.version>9.15.5</vmwareGemFire.version>
  </properties>         
  
  <dependencies>
      <dependency>
          <groupId>com.vmware.gemfire</groupId>
          <artifactId>spring-data-2.7-gemfire-9.15</artifactId>
          <version>${springDataForGemFire.version}</version>
      </dependency>
      <dependency>
             <groupId>com.vmware.gemfire</groupId>
             <artifactId>geode-server-all</artifactId>
             <version>${vmwareGemFire.version}</version>
             <exclusions>
                  <exclusion>
                       <groupId>com.vmware.gemfire</groupId>
                       <artifactId>geode-log4j</artifactId>
                  </exclusion>
             </exclusions>
      </dependency>
  </dependencies>
     ```

* **Gradle**: Add the following to your `build.gradle` file. 
  
```groovy
    ext {
        springDataForGemFireVersion = '1.1.1'
        vmwareGemFireVersion = '9.15.5'
    }

    dependencies {
       implementation "com.vmware.gemfire:spring-data-2.7-gemfire-9.15:$springDataForGemFireVersion"
       implementation ("com.vmware.gemfire:geode-server-all:$vmwareGemFireVersion"){
       exclude group: 'com.vmware.gemfire', module: 'geode-log4j'
       }
    }
```
