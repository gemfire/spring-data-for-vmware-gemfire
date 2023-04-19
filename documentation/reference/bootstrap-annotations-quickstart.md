---
title: Annotation-based Configuration Quick Start
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


The following sections provide an overview to the Spring Data for VMware GemFire
annotations to get started quickly.

All annotations provide additional configuration
attributes along with associated <a
href="#bootstrap-annotation-config-properties">properties</a> to
conveniently customize the configuration and behavior of
GemFire at runtime. However, in general, none of the
attributes or associated properties are required to use a particular
GemFire feature. Simply declare the annotation to enable the
feature and you are done. Refer to the individual Javadoc of each
annotation for more details.

## <a id="configure-clientcache-application"></a>Configure a `ClientCache` Application

To configure and bootstrap a GemFire `ClientCache`
application, use the following:

```highlight
@SpringBootApplication
@ClientCacheApplication
public class ClientApplication {
  
  public static void main(String[] args) {
    SpringApplication.run(ClientApplication.class, args);
  }
}
```

See [@ClientCacheApplication Javadoc](https://docs.spring.io/spring-data/geode/docs/current/api/org/springframework/data/gemfire/config/annotation/ClientCacheApplication.html).

For more information, see [Configuring GemFire Applications with Spring](bootstrap-annotations.html#configuring-with-spring)
in _Bootstrapping GemFire with the Spring Container Using Annotations_.

## <a id="configure-peer-cache-application"></a>Configure a Peer `Cache` Application

To configure and bootstrap a GemFire Peer `Cache` application,
use the following:

```highlight
@SpringBootApplication
@PeerCacheApplication
public class ServerApplication {

  public static void main(String[] args) {
    SpringApplication.run(ServerApplication.class, args);
  }
}
```

If you want to enable a
<code>CacheServer</code> that allows <code>ClientCache</code>
applications to connect to this server, replace the
<code>@PeerCacheApplication</code> annotation with the
<code>@CacheServerApplication</code> annotation. This will start a
<code>CacheServer</code> running on "localhost", listening on the
default <code>CacheServer</code> port of <code>40404</code>.


See [@CacheServerApplication Javadoc](https://docs.spring.io/spring-data/geode/docs/current/api/org/springframework/data/gemfire/config/annotation/CacheServerApplication.html).



See
[@PeerCacheApplication Javadoc](https://docs.spring.io/spring-data/geode/docs/current/api/org/springframework/data/gemfire/config/annotation/PeerCacheApplication.html).

For more information, see [Configuring GemFire Applications with Spring](bootstrap-annotations.html#configuring-with-spring)
in _Bootstrapping GemFire with the Spring Container Using Annotations_.

## <a id="configure-embedded-locator"></a>Configure an Embedded Locator

Annotate your Spring `@PeerCacheApplication` or
`@CacheServerApplication` class with `@EnableLocator` to start an
embedded Locator bound to all NICs listening on the default Locator
port, `10334`, as follows:

```highlight
@SpringBootApplication
@CacheServerApplication
@EnableLocator
public class ServerApplication {

  public static void main(String[] args) {
    SpringApplication.run(ServerApplication.class, args);
  }
}
```

<code>@EnableLocator</code> can only be used with GemFire server applications.

See
[@EnableLocator Javadoc](https://docs.spring.io/spring-data/geode/docs/current/api/org/springframework/data/gemfire/config/annotation/EnableLocator.html).

For more information, see [Configuring an Embedded Locator](bootstrap-annotations.html#configuring-an-embedded-locator) in
__Bootstrapping GemFire with the Spring Container Using Annotations_._

## <a id="configure-embedded-manager"></a>Configure an Embedded Manager

Annotate your Spring `@PeerCacheApplication` or
`@CacheServerApplication` class with `@EnableManager` to start an
embedded Manager bound to all NICs listening on the default Manager
port, `1099`, as follows:

```highlight
@SpringBootApplication
@CacheServerApplication
@EnableManager
public class ServerApplication {

  public static void main(String[] args) {
    SpringApplication.run(ServerApplication.class, args);
  }
}
```

<code>@EnableManager</code> can only be used with GemFire server applications.

See [@EnableManager Javadoc](https://docs.spring.io/spring-data/geode/docs/current/api/org/springframework/data/gemfire/config/annotation/EnableManager.html).

For more information, see [Configuring an Embedded Manager](bootstrap-annotations.html#configuring-an-embedded-manager)
in _Bootstrapping GemFire with the Spring Container Using Annotations_.

## <a id="configure-embedded-http-server"></a>Configure the Embedded HTTP Server

Annotate your Spring `@PeerCacheApplication` or
`@CacheServerApplication` class with `@EnableHttpService` to start the
embedded HTTP server (Jetty) listening on port `7070`, as follows:

```highlight
@SpringBootApplication
@CacheServerApplication
@EnableHttpService
public class ServerApplication {

  public static void main(String[] args) {
    SpringApplication.run(ServerApplication.class, args);
  }
}
```
@EnableHttpService</code> can only be used with GemFire server applications.

See [@EnableHttpService Javadoc](https://docs.spring.io/spring-data/geode/docs/current/api/org/springframework/data/gemfire/config/annotation/EnableHttpService.html).

For more information, see [Configuring the Embedded HTTP Server](bootstrap-annotations.html#configuring-the-embedded-http-server)
in _Bootstrapping GemFire with the Spring Container Using Annotations_.

## <a id="configure-embedded-memcached-server"></a>Configure the Embedded Memcached Server

Annotate your Spring `@PeerCacheApplication` or
`@CacheServerApplication` class with `@EnableMemcachedServer` to start
the embedded Memcached server (Gemcached) listening on port `11211`, as
follows:

```highlight
@SpringBootApplication
@CacheServerApplication
@EnableMemcachedServer
public class ServerApplication {

  public static void main(String[] args) {
    SpringApplication.run(ServerApplication.class, args);
  }
}
```

<code>@EnableMemcachedServer</code> can only be used with GemFire server applications.

See [@EnableMemcachedServer Javadoc](https://docs.spring.io/spring-data/geode/docs/current/api/org/springframework/data/gemfire/config/annotation/EnableMemcachedServer.html).

For more information, see [Configuring the Embedded Memcached Server (Gemcached)](bootstrap-annotations.html#configuring-the-embedded-memcached-server)
in _Bootstrapping GemFire with the Spring Container Using Annotations_.

## <a id="configure-logging"></a>Configure Logging

To configure or adjust GemFire logging, annotate your Spring,
GemFire client or server application class with
`@EnableLogging`, as follows:

```highlight
@SpringBootApplication
@ClientCacheApplication
@EnableLogging(logLevel="trace")
public class ClientApplication {

  public static void main(String[] args) {
    SpringApplication.run(ClientApplication.class, args);
  }
}
```

Default <code>log-level</code> is **config**. This annotation does not adjust log levels in your application and is only used for GemFire.

See
[@EnableLogging Javadoc](https://docs.spring.io/spring-data/geode/docs/current/api/org/springframework/data/gemfire/config/annotation/EnableLogging.html).

For more information, see [Configuring Logging](bootstrap-annotations.html#configuring-logging)
_Bootstrapping GemFire with the Spring Container Using Annotations_.

## <a id="configure-statistics"></a>Configure Statistics

To gather GemFire statistics at runtime, annotate your Spring,
GemFire client or server application class with
`@EnableStatistics`, as follows:

```highlight
@SpringBootApplication
@ClientCacheApplication
@EnableStatistics
public class ClientApplication {

  public static void main(String[] args) {
    SpringApplication.run(ClientApplication.class, args);
  }
}
```

See [@EnableStatistics Javadoc](https://docs.spring.io/spring-data/geode/docs/current/api/org/springframework/data/gemfire/config/annotation/EnableStatistics.html).

For more information, see [Configuring Statistics](bootstrap-annotations.html#configuring-statistics)
_Bootstrapping GemFire with the Spring Container Using Annotations_.

## <a id="configure-pdx"></a>Configure PDX

To enable GemFire PDX serialization, annotate your Spring,
GemFire client or server application class with `@EnablePdx`,
as follows:

```highlight
@SpringBootApplication
@ClientCacheApplication
@EnablePdx
public class ClientApplication {

  public static void main(String[] args) {
    SpringApplication.run(ClientApplication.class, args);
  }
}
```

GemFire PDX Serialization is an 
alternative to Java Serialization with many added benefits. For one, it
makes short work of making all of your application domain model types
serializable without having to implement
<code>java.io.Serializable</code>.

By default, Spring Data for VMware GemFire configures the
<code>MappingPdxSerializer</code> to serialize your application domain
model types, which does not require any special configuration
out-of-the-box to properly identify application domain objects
that need to be serialized and then perform the serialization since the
logic in <code>MappingPdxSerializer</code> is based on Spring Data's
mapping infrastructure.
For more details, see [MappingPdxSerializer](mapping.html#mappingpdxserializer)
in _POJO Mapping_.

See [@EnablePdx Javadoc](https://docs.spring.io/spring-data/geode/docs/current/api/org/springframework/data/gemfire/config/annotation/EnablePdx.html).

For more information, see [Configuring PDX](bootstrap-annotations.html#configuring-pdx) in _Bootstrapping GemFire with the Spring Container Using Annotations_.

## <a id="configure-ssl"></a>Configure SSL

To enable GemFire SSL, annotate your Spring, GemFire
client or server application class with `@EnableSsl`, as follows:

```highlight
@SpringBootApplication
@ClientCacheApplication
@EnableSsl(components = SERVER)
public class ClientApplication {

  public static void main(String[] args) {
    SpringApplication.run(ClientApplication.class, args);
  }
}
```

Minimally, GemFire requires you to specify
a keystore and truststore using the appropriate configuration
attributes or properties. Both keystore and truststore configuration
attributes or properties may refer to the same <code>KeyStore</code>
file. Additionally, you must specify a username and password to
access the <code>KeyStore</code> file if the file has been secured.

GemFire SSL allows you to configure the
specific components of the system that require TLS, such as
client/server, Locators, Gateways, etc. Optionally, you can specify that
all components of GemFire use SSL with `ALL`.

See [@EnableSsl Javadoc](https://docs.spring.io/spring-data/gemfire/docs/current/api/org/springframework/data/gemfire/config/annotation/EnableSsl.html).

For more information, see [Configuring SSL](bootstrap-annotations.html#configuring-ssl) in _Bootstrapping GemFire with the Spring Container Using Annotations_.

## <a id="configure-security"></a>Configure Security

To enable GemFire security, annotate your Spring,
GemFire client or server application class with
`@EnableSecurity`, as follows:

```highlight
@SpringBootApplication
@ClientCacheApplication
@EnableSecurity
public class ClientApplication {

  public static void main(String[] args) {
    SpringApplication.run(ClientApplication.class, args);
  }
}
```

On the server, you must configure access to the auth
credentials. You may either implement the [SecurityManager](https://geode.apache.org/releases/latest/javadoc/org/apache/geode/security/SecurityManager.html) interface or declare one or more Apache Shiro <code>Realms</code>. For more details, see [Configuring Server Security](bootstrap-annotations.html#configuring-server-security) in _Bootstrapping GemFire with the Spring Container Using Annotations_.

On the client, you must configure a username and password. For more details, see [Configuring Client Security](bootstrap-annotations.html#configuring-client-security) in _Bootstrapping GemFire with the Spring Container Using Annotations_.

See [@EnableSecurity Javadoc](https://docs.spring.io/spring-data/geode/docs/current/api/org/springframework/data/gemfire/config/annotation/EnableSecurity.html).


For more information, see [Configuring Security](bootstrap-annotations.html#configuring-security) in _Bootstrapping GemFire with the Spring Container Using Annotations_.

## <a id="configure-gemfire-properties"></a>Configure GemFire Properties

To configure other, low-level GemFire properties not covered
by the feature-oriented, Spring Data for VMware GemFire configuration annotations,
annotate your Spring, GemFire client or server application
class with `@GemFireProperties`, as follows:

```highlight
@SpringBootApplication
@PeerCacheApplication
@EnableGemFireProperties(
    cacheXmlFile = "/path/to/cache.xml",
    conserveSockets = true,
    groups = "GroupOne",
    remoteLocators = "lunchbox[11235],mailbox[10101],skullbox[12480]"
)
public class ServerApplication {

  public static void main(String[] args) {
    SpringApplication.run(ServerApplication.class, args);
  }
}
```

Some GemFire properties are client-side only while others are server-side only. For
the appropriate use of each property, see [gemfire.properties vars.and gfsecurity.properties: GemFire Properties](https://docs.vmware.com/en/VMware-GemFire/9.15/gf/reference-topics-gemfire_properties.html) in the GemFire product documentation.

See [@EnableGemFireProperties Javadoc](https://docs.spring.io/spring-data/geode/docs/current/api/org/springframework/data/gemfire/config/annotation/EnableGemFireProperties.html).

For more information, see [Configuring GemFire Properties](#configuring-gemfire-properties) in _Bootstrapping GemFire with the Spring Container Using Annotations_.

## <a id="configure-caching"></a>Configure Caching

To use GemFire as a *caching provider* in Spring's [Cache Abstraction](https://docs.spring.io/spring-framework/docs/current/reference/html/integration.html#cache) and have Spring Data for VMware GemFire automatically create GemFire Regions for the caches required by your application
service components, annotate your Spring, GemFire client, or server application
class with `@EnableGemfireCaching` and `@EnableCachingDefinedRegions`  as follows:

```highlight
@SpringBootApplication
@ClientCacheApplication
@EnableCachingDefinedRegions
@EnableGemfireCaching
public class ClientApplication {

  public static void main(String[] args) {
    SpringApplication.run(ClientApplication.class, args);
  }
}
```

Then define the application services that require caching as follows:

```highlight
@Service
public class BookService {
  
    @Cacheable("Books")
    public Book findBy(ISBN isbn) {
        ...
    }
}
```
<code>@EnableCachingDefinedRegions</code> is optional. You can manually define your Regions instead.

See [@EnableCachingDefinedRegions Javadoc](https://docs.spring.io/spring-data/geode/docs/current/api/org/springframework/data/gemfire/config/annotation/EnableCachingDefinedRegions.html).

See
[@EnableGemfireCaching Javadoc](https://docs.spring.io/spring-data/geode/docs/current/api/org/springframework/data/gemfire/cache/config/EnableGemfireCaching.html).

For more information, see [Configuring Spring's Cache Abstraction](bootstrap-annotations.html#configuring-springs-cache-abstraction) in _Bootstrapping GemFire with the Spring Container Using Annotations_.

## <a id="configure-regions-etc"></a>Configure Regions, Indexes, Repositories, and Entities for Persistent Applications

To quickly create Spring, GemFire persistent client, or server applications, annotate your application class with `@EnableEntityDefinedRegions`, `@EnableGemfireRepositories`, and
`@EnableIndexing`, as follows:

```highlight
@SpringBootApplication
@ClientCacheApplication
@EnableEntityDefinedRegions(basePackageClasses = Book.class)
@EnableGemfireRepositories(basePackageClasses = BookRepository.class)
@EnableIndexing
public class ClientApplication {
  
  public static void main(String[] args) {
    SpringApplication.run(ClientApplication.class, args);
  }
}
```

<p classs="note"><strong>Note</strong>: The <code>@EnableEntityDefinedRegions</code> annotation is required when using the <code>@EnableIndexing</code> annotation. For more details, see <a href="bootstrap-annotations.html#configuring-indexes">Configuring Indexes</a> in <em>Bootstrapping GemFire with the Spring Container Using Annotations</em>.</p>

Next, define your entity class and use the `@Region` mapping annotation
to specify the Region in which your entity will be stored. Use the
`@Indexed` annotation to define Indexes on entity fields used in your
application queries, as follows:

```highlight
package example.app.model;

import ...;

@Region("Books")
public class Book {

  @Id
  private ISBN isbn;

  @Indexed;
  private Author author;

  @Indexed
  private LocalDate published;

  @LuceneIndexed
  private String title;

}
```

<p class="note"><strong>Note</strong>: The <code>@Region("Books")</code> entity class
annotation is used by the <code>@EnableEntityDefinedRegions</code> to determine the Regions required by the application. For additional details, see <a
href="https://docs.spring.io/spring-data/geode/docs/current/reference/html/#bootstrap-annotation-config-region-types">Configuring Type-specific Regions</a> in the <em>Spring Data for VMware GemFire Reference Guide</em>
and <a href="mapping.html#object-mapping-fundamentals">Object Mapping Fundamentals</a> in <em>POJO Mapping</em>.</p>

Finally, define your CRUD Repository with simple queries to persist and
access `Books`, as follows:

```highlight
package example.app.repo;

import ...;

public interface BookRepository extends CrudRepository {

  List<Book> findByAuthorOrderByPublishedDesc(Author author);

}
```
For more information, see [Spring Data for VMware GemFire Repositories](repositories.html).

See also the following: 

* [@EnableEntityDefinedRegions Javadoc](https://docs.spring.io/spring-data/geode/docs/current/api/org/springframework/data/gemfire/config/annotation/EnableEntityDefinedRegions.html)

* [@EnableGemfireRepositories Javadoc](https://docs.spring.io/spring-data/geode/docs/current/api/org/springframework/data/gemfire/repository/config/EnableGemfireRepositories.html).

* [@EnableIndexing Javadoc](https://docs.spring.io/spring-data/geode/docs/current/api/org/springframework/data/gemfire/config/annotation/EnableIndexing.html)

* [@Region Javadoc](https://docs.spring.io/spring-data/geode/docs/current/api/org/springframework/data/gemfire/mapping/annotation/Region.html)

* [@Indexed Javadoc](https://docs.spring.io/spring-data/geode/docs/current/api/org/springframework/data/gemfire/mapping/annotation/Indexed.html)

* [@LuceneIndexed Javadoc](https://docs.spring.io/spring-data/geode/docs/current/api/org/springframework/data/gemfire/mapping/annotation/LuceneIndexed.html)

* [Configuring Regions](bootstrap-annotations.html#configuring-regions) in _Bootstrapping GemFire with the Spring Container Using Annotations_

* [Spring Data for VMware GemFire Repositories](repositories.html)

## <a id="configure-client-regions"></a>Configure Client Regions from Cluster-defined Regions

Alternatively, you can define client \[\*PROXY\] Regions from Regions
already defined in the cluster using `@EnableClusterDefinedRegions`, as
follows:

```highlight
@SpringBootApplication
@ClientCacheApplication
@EnableClusterDefinedRegions
@EnableGemfireRepositories
public class ClientApplication {

  public static void main(String[] args) {
    SpringApplication.run(ClientApplication.class, args);
  }

  ...
}
```

For more information, see [Configured Cluster-Defined Regions](bootstrap-annotations.html#configured-cluster-defined-regions)
in _Bootstrapping GemFire with the Spring Container Using Annotations_.

## <a id="configure-functions"></a>Configure Functions

GemFire Functions are useful in distributed compute scenarios
where a potentially expensive computation requiring data can be
performed in parallel across the nodes in the cluster. In this case, it
is more efficient to bring the logic to where the data is located
(stored) rather than requesting and fetching the data to be processed by
the computation.

Use the `@EnableGemfireFunctions` along with the `@GemfireFunction`
annotation to enable GemFire Functions definitions implemented
as methods on POJOs, as follows:

```highlight
@PeerCacheApplication
@EnableGemfireFunctions
class ServerApplication {

  public static void main(String[] args) {
    SpringApplication.run(ServerApplication.class, args);
  }

  @GemfireFunction
  Integer computeLoyaltyPoints(Customer customer) {
    ...
  }
}
```

Use the `@EnableGemfireFunctionExecutions` along with one of the Function
calling annotations: `@OnMember`, `@OnMembers`, `@OnRegion`, `@OnServer`
and `@OnServers`.

```highlight
@ClientCacheApplication
@EnableGemfireFunctionExecutions(basePackageClasses = CustomerRewardsFunction.class)
class ClientApplication {

  public static void main(String[] args) {
    SpringApplication.run(ClientApplication.class, args);
  }
}

@OnRegion("Customers")
interface CustomerRewardsFunctions {

  Integer computeLoyaltyPoints(Customer customer);

}
```

## <a id="configure-continuous-query"></a>Configure Continuous Query

Real-time, event stream processing is becoming an increasingly important
task for data-intensive applications, primarily to respond to
user requests in a timely manner. GemFire Continuous Query
(CQ) will help you achieve this rather complex task quite easily.

Enable CQ by annotating your application class with
`@EnableContinuousQueries` and define your CQs along with the associated
event handlers, as follows:

```highlight
@ClientCacheApplication
@EnableContinuousQueries
class ClientApplication {

  public static void main(String[] args) {
    SpringApplication.run(ClientApplication.class, args);
  }
}
```
Then, define your CQs by annotating the associated handler method with
`@ContinousQuery`, as follows:

```highlight
@Service
class CustomerService {

  @ContinuousQuery(name = "CustomerQuery", query = "SELECT * FROM /Customers c WHERE ...")
  public void process(CqEvent event) {
    ...
  }
}
```

Anytime an event occurs changing the `Customer` data to match the
predicate in your continuous OQL query (CQ), the `process` method will
be called.

GemFire CQ is a client-side feature only.

## <a id="configure-cluster-configurations"></a>Configure Cluster Configuration

When developing Spring Data applications using GemFire as
GemFire `ClientCache` applications, it is useful during
development to configure the server to match the client in a
client/server topology. In fact, GemFire expects that when you
have a "/Example" PROXY `Region` on the client, that a matching `Region`
by name (i.e. "Example") exists in the server.

You could use `gfsh` to create every Region and Index that your
application requires, or, you could simply push the configuration
meta-data already expressed when developing your Spring Data application
using GemFire when you run it.

This is as simple as annotation your main application class with
`@EnableClusterConfiguration(..)`:

**Using `@EnableClusterConfiguration`**

```highlight
@ClientCacheApplication
@EnableClusterConfiguration(useHttp = true)
class ClientApplication {
  ...
}
```
Most of the time, when using a client/server
topology, particularly in production environments, the servers of the
cluster will be started using `gfsh`. In which case, it customary
to use HTTP(S) to send the configuration metadata (e.g. Region and
Index definitions) to the cluster. When HTTP is used, the configuration
metadata is sent to the Manager in the cluster and distributed across
the server nodes in the cluster consistently.

<p class="note"><strong>Note</strong>: To use
<code>@EnableClusterConfiguration</code> you must declare the
<code>org.springframework:spring-web</code> dependency in your Spring
application classpath.</p>

## <a id="configure-gatewayreceivers"></a>Configure `GatewayReceivers`

The replication of data between different GemFire clusters is
an increasingly important fault-tolerance and high-availability (HA)
mechanism. GemFire WAN replication is a mechanism that allows
one GemFire cluster to replicate its data to another
GemFire cluster in a reliable and fault-tolerant manner.

GemFire WAN replication requires two components to be
configured:

- `GatewayReceiver`: The WAN replication component that receives data
  from a remote GemFire cluster's `GatewaySender`.

- `GatewaySender`: The WAN replication component that sends data to a
  remote GemFire cluster's `GatewayReceiver`.

To enable a `GatewayReceiver`, the application class must be
annotated with `@EnableGatewayReceiver` as follows:

```highlight
@CacheServerApplication
@EnableGatewayReceiver(manualStart = false, startPort = 10000, endPort = 11000, maximumTimeBetweenPings = 1000,
    socketBufferSize = 16384, bindAddress = "localhost",transportFilters = {"transportBean1", "transportBean2"},
    hostnameForSenders = "hostnameLocalhost"){
      ...
      ...
    }
}
class MySpringApplication { .. }
```

<p class="note"><strong>Note</strong>: GemFire <code>GatewayReceiver</code> is a
server-side feature only and can only be configured on a
<code>CacheServer</code> or peer <code>Cache</code> node.</p>

## <a id="configure-gatewaysenders"></a>Configure `GatewaySenders`

To enable `GatewaySender`, the application class must be annotated
with `@EnableGatewaySenders` and `@EnableGatewaySender` as follows:

```highlight
@CacheServerApplication
@EnableGatewaySenders(gatewaySenders = {
        @EnableGatewaySender(name = "GatewaySender", manualStart = true,
            remoteDistributedSystemId = 2, diskSynchronous = true, batchConflationEnabled = true,
            parallel = true, persistent = false,diskStoreReference = "someDiskStore",
            orderPolicy = OrderPolicyType.PARTITION, alertThreshold = 1234, batchSize = 100,
            eventFilters = "SomeEventFilter", batchTimeInterval = 2000, dispatcherThreads = 22,
            maximumQueueMemory = 400,socketBufferSize = 16384,
            socketReadTimeout = 4000, regions = { "Region1"}),
        @EnableGatewaySender(name = "GatewaySender2", manualStart = true,
            remoteDistributedSystemId = 2, diskSynchronous = true, batchConflationEnabled = true,
            parallel = true, persistent = false, diskStoreReference = "someDiskStore",
            orderPolicy = OrderPolicyType.PARTITION, alertThreshold = 1234, batchSize = 100,
            eventFilters = "SomeEventFilter", batchTimeInterval = 2000, dispatcherThreads = 22,
            maximumQueueMemory = 400, socketBufferSize = 16384,socketReadTimeout = 4000,
            regions = { "Region2" })
}){
class MySpringApplication { .. }
}
```

<p class="note"><strong>Note</strong>: GemFire <code>GatewaySender</code> is a
server-side feature only and can only be configured on a
<code>CacheServer</code> or a peer <code>Cache</code> node.</p>

In the above example, the application is configured with two Regions,
`Region1` and `Region2`. Additionally, two `GatewaySenders` will be
configured to service both Regions. `GatewaySender1` will be configured
to replicate `Region1`'s data and `GatewaySender2` will be configured
to replicate `Region2`'s data.

As demonstrated, each `GatewaySender` property can be configured on each
`EnableGatewaySender` annotation.

It is also possible to have a more generic, "defaulted" properties
approach, where all properties are configured on the
`EnableGatewaySenders` annotation. In this way, a set of generic, defaulted
values can be set on the parent annotation and then overridden on the
child if required, as demonstrated below:


```highlight
@CacheServerApplication
@EnableGatewaySenders(gatewaySenders = {
        @EnableGatewaySender(name = "GatewaySender", transportFilters = "transportBean1", regions = "Region2"),
        @EnableGatewaySender(name = "GatewaySender2")},
        manualStart = true, remoteDistributedSystemId = 2,
        diskSynchronous = false, batchConflationEnabled = true, parallel = true, persistent = true,
        diskStoreReference = "someDiskStore", orderPolicy = OrderPolicyType.PARTITION, alertThreshold = 1234, batchSize = 1002,
        eventFilters = "SomeEventFilter", batchTimeInterval = 2000, dispatcherThreads = 22, maximumQueueMemory = 400,
        socketBufferSize = 16384, socketReadTimeout = 4000, regions = { "Region1", "Region2" },
        transportFilters = { "transportBean2", "transportBean1" })
class MySpringApplication { .. }
```

<p class="note"><strong>Note</strong>: When the <code>regions</code> attribute is left
empty or not populated, the <code>GatewaySender</code> will
automatically attach itself to every configured <code>Region</code>
within the application.</p>
