---
title: Bootstrapping GemFire with the Spring Container Using Annotations
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

Spring Data for VMware GemFire (Spring Data for VMware GemFire) 2.0 introduces a new annotation-based
configuration model to configure and bootstrap GemFire using
the Spring container.

The primary motivation for introducing an annotation-based approach to
the configuration of GemFire in a Spring context is to enable
Spring application developers to *get up and running* as *quickly* and
as *easily* as possible.

<p class="note"><strong>Tip</strong>: To get started more quickly, see <a href="bootstrap-annotations-quickstart.html">Annotation-based Configuration Quick Start</a>.
</p>

## <a id="introduction"></a>Introduction

GemFire can be difficult to setup and use correctly, given all
the [configuration properties](https://docs.vmware.com/en/VMware-GemFire/9.15/gf/reference-topics-gemfire_properties.html) and different configuration options:


- [Java API](https://geode.apache.org/releases/latest/javadoc/)

- [cache.xml](https://docs-staging.vmware.com/en/VMware-GemFire/9.15/gf/reference-topics-chapter_overview_cache_xml.html)

- [gfsh](https://docs-staging.vmware.com/en/VMware-GemFire/9.15/gf/tools_modules-gfsh-chapter_overview.html)
  with [Cluster Configuration](https://docs-staging.vmware.com/en/VMware-GemFire/9.15/gf/configuring-chapter_overview.html)

- [Spring XML/Java-based configuration](bootstrap.html)

Further complexity arises from the different supported topologies:

- [Client/Server](https://docs-staging.vmware.com/en/VMware-GemFire/9.15/gf/topologies_and_comm-cs_configuration-chapter_overview.html)

- [Peer-to-peer](https://docs-staging.vmware.com/en/VMware-GemFire/9.15/gf/topologies_and_comm-p2p_configuration-chapter_overview.html)

- [Multi-site (WAN)](https://docs-staging.vmware.com/en/VMware-GemFire/9.15/gf/topologies_and_comm-multi_site_configuration-chapter_overview.html)

- Distributed system design patterns (such as shared-nothing architecture)

The annotation-based configuration model aims to simplify all this and
more.

The annotation-based configuration model is an alternative to XML-based
configuration using Spring Data for VMware GemFire's XML namespace. With XML, you could use
the `gfe` XML schema for configuration and the `gfe-data` XML schema for
data access. For more details. see [Bootstrapping GemFire with the Spring Container](bootstrap.html).

<p class="note"><strong>Note</strong>: The annotation-based
configuration model does not support the configuration of
GemFire's WAN components and topology.</p>

Like Spring Boot, Spring Data for VMware GemFire's annotation-based configuration model was
designed as an opinionated, convention-over-configuration approach for
using GemFire. Indeed, this annotation-based configuration
model was inspired by Spring Boot as well as several other Spring and
Spring Data projects, collectively.

By following convention, all annotations provide reasonable and sensible
defaults for all configuration attributes. The default value for a given
annotation attribute directly corresponds to the default value provided
in GemFire for the same configuration property.

The intention is to let you enable GemFire features or an
embedded services by declaring the appropriate annotation on your Spring
`@Configuration` or `@SpringBootApplication` class without needing to
unnecessarily configure a large number of properties just to use the
feature or service.

Again, *getting started*, *quickly* and as *easily*, is the primary
objective.

However, the option to customize the configuration metadata and behavior
of GemFire is there if you need it, and Spring Data for VMware GemFire's
annotation-based configuration quietly backs away. You need only specify
the configuration attributes you wish to adjust. Also, as we will see
later in this document, there are several ways to configure a
GemFire feature or embedded service by using the annotations.

You can find all the new Spring Data for VMware GemFire Java `Annotations` in the
`org.springframework.data.gemfire.config.annotation` package.

## <a id="configuring-with-spring"></a>Configuring GemFire Applications with Spring

Like all Spring Boot applications that begin by annotating the
application class with `@SpringBootApplication`, a Spring Boot
application can easily become a GemFire cache application by
declaring any one of three main annotations:

- `@ClientCacheApplication`

- `@PeerCacheApplication`

- `@CacheServerApplication`

These three annotations are the Spring application developer's starting
point when working with GemFire.

To realize the intent behind these annotations, you must understand that
there are two types of cache instances that can be created with
GemFire: a client cache or a peer cache.

You can configure a Spring Boot application as a GemFire cache
client with an instance of `ClientCache`, which can communicate with an
existing cluster of GemFire servers used to manage the
application's data. The client-server topology is the most common system
architecture employed when using GemFire and you can make your
Spring Boot application a cache client, with a `ClientCache` instance,
simply by annotating it with `@ClientCacheApplication`.

Alternatively, a Spring Boot application may be a peer member of a
GemFire cluster. That is, the application itself is just
another server in a cluster of servers that manages data. The Spring
Boot application creates an "embedded", peer `Cache` instance when you
annotate your application class with `@PeerCacheApplication`.

By extension, a peer cache application may also serve as a `CacheServer`
too, allowing cache clients to connect and perform data access
operations on the server. This is accomplished by annotating the
application class with `@CacheServerApplication` in place of
`@PeerCacheApplication`, which creates a peer `Cache` instance along
with the `CacheServer` that allows cache clients to connect.

<p class="note"><strong>Note</strong>: A GemFire server is not necessarily a
cache server by default. That is, a server is not necessarily set up to
serve cache clients just because it is a server. A GemFire
server can be a peer member (data node) of the cluster managing data
without serving any clients while other peer members in the cluster are
indeed set up to serve clients in addition to managing data. It is also
possible to set up certain peer members in the cluster as non-data
nodes, <a href="https://docs-staging.vmware.com/en/VMware-GemFire/9.15/gf/developing-region_options-data_hosts_and_accessors.html">data accessors</a>,
which do not store data, but act as a proxy to service
clients as <code>CacheServers</code>. Many different topologies and
cluster arrangements are supported by GemFire, but are beyond
the scope of this document.</p>

As an example, to create a Spring Boot cache client
application, start with the following:

**Spring-based GemFire `ClientCache` application**

```highlight
@SpringBootApplication
@ClientCacheApplication
class ClientApplication { .. }
```

Or, to create a Spring Boot application with an embedded
peer `Cache` instance, where your application will be a server and peer
member of a cluster (distributed system) formed by GemFire,
start with the following:

**Spring-based GemFire embedded peer `Cache` application**

```highlight
@SpringBootApplication
@PeerCacheApplication
class ServerApplication { .. }
```

Alternatively, you can use the `@CacheServerApplication` annotation in
place of `@PeerCacheApplication` to create both an embedded peer `Cache`
instance along with a `CacheServer` running on `localhost`, listening on
the default cache server port, `40404`, as follows:

**Spring-based GemFire embedded peer `Cache` application with `CacheServer`**

```highlight
@SpringBootApplication
@CacheServerApplication
class ServerApplication { .. }
```

## <a id="client-server-applicaion-in-detail"></a>Client/Server Applications In Detail

There are multiple ways that a client can connect to and communicate
with servers in a GemFire cluster. The most common and
recommended approach is to use GemFire Locators.

A cache client can connect to one or more Locators
in the GemFire cluster instead of directly to a
<code>CacheServer</code>. The advantage of using Locators over direct
<code>CacheServer</code> connections is that Locators provide metadata
about the cluster to which the client is connected. This metadata
includes information such as which servers contain the data of interest
or which servers have the least amount of load. A client
<code>Pool</code> in conjunction with a Locator also provides fail-over
capabilities in case a <code>CacheServer</code> crashes. By enabling the
<code>PARTITION</code> Region (PR) single-hop feature in the client
<code>Pool</code>, the client is routed directly to the server
containing the data requested and needed by the client.

Locators are also peer members in a cluster.
Locators actually constitute what makes up a cluster of
GemFire nodes. All nodes connected by a Locator are
peers in the cluster, and new members use Locators to join a cluster and
find other members.

By default, GemFire sets up a "DEFAULT" `Pool` connected to a
`CacheServer` running on `localhost`, listening on port `40404` when a
`ClientCache` instance is created. A `CacheServer` listens on port
`40404`, accepting connections on all system NICs. You do not need to do
anything special to use the client-server topology. Simply annotate your
server-side Spring Boot application with `@CacheServerApplication` and
your client-side Spring Boot application with `@ClientCacheApplication`,
and you are ready to go.


If you prefer, you can  start your servers with the `gfsh start server` command.
Your Spring Boot `@ClientCacheApplication` can
still connect to the server regardless of how it was started. However,
you may prefer to configure and start your servers by using the
Spring Data for VMware GemFire approach since a properly annotated Spring Boot application
class is far more intuitive and easier to debug.

You can customize the "DEFAULT" `Pool` set up by GemFire to possibly connect to one
or more Locators, as the following example demonstrates:

**Spring-based GemFire `ClientCache` application using Locators**

```highlight
@SpringBootApplication
@ClientCacheApplication(locators = {
    @Locator(host = "boombox" port = 11235),
    @Locator(host = "skullbox", port = 12480)
})
class ClientApplication { .. }
```

Along with the `locators` attribute, the `@ClientCacheApplication`
annotation has a `servers` attribute as well. The `servers` attribute
can be used to specify one or more nested `@Server` annotations that let
the cache client connect directly to one or more servers, if necessary.

<p class="note"><strong>Note</strong>: You can use either the <code>locators</code> or
<code>servers</code> attribute, but not both. This is enforced by GemFire.</p>

You can also configure additional `Pool` instances, other than the
"DEFAULT" `Pool` provided by GemFire when a `ClientCache`
instance is created with the `@ClientCacheApplication` annotation, by
using the `@EnablePool` and `@EnablePools` annotations.

<code>@EnablePools</code> is a composite annotation
that aggregates several nested <code>@EnablePool</code> annotations on a
single class. Java 8 and earlier do not allow more than one annotation
of the same type to be declared on a single class.

The following example uses the `@EnablePool` and `@EnablePools`
annotations:

**Spring-based GemFire `ClientCache` application using multiple named `Pools`**

```highlight
@SpringBootApplication
@ClientCacheApplication(logLevel = "info")
@EnablePool(name = "VenusPool", servers = @Server(host = "venus", port = 48484),
    min-connections = 50, max-connections = 200, ping-internal = 15000,
    prSingleHopEnabled = true, readTimeout = 20000, retryAttempts = 1,
    subscription-enable = true)
@EnablePools(pools = {
    @EnablePool(name = "SaturnPool", locators = @Locator(host="skullbox", port=20668),
        subsription-enabled = true),
    @EnablePool(name = "NeptunePool", severs = {
            @Server(host = "saturn", port = 41414),
            @Server(host = "neptune", port = 42424)
        }, min-connections = 25))
})
class ClientApplication { .. }
```

The `name` attribute is the only required attribute of the `@EnablePool`
annotation. The value of the `name` attribute
corresponds to both the name of the `Pool` bean created in the Spring
container as well as the name used to reference the corresponding
configuration properties. It is also the name of the `Pool` registered
and used by GemFire.

On the server, you can configure multiple `CacheServers` that a client can connect to as follows:

**Spring-based GemFire `CacheServer` application using multiple named `CacheServers`**

```highlight
@SpringBootApplication
@CacheSeverApplication(logLevel = "info", autoStartup = true, maxConnections = 100)
@EnableCacheServer(name = "Venus", autoStartup = true,
    hostnameForClients = "venus", port = 48484)
@EnableCacheServers(servers = {
    @EnableCacheServer(name = "Saturn", hostnameForClients = "saturn", port = 41414),
    @EnableCacheServer(name = "Neptune", hostnameForClients = "neptune", port = 42424)
})
class ServerApplication { .. }
```

Like <code>@EnablePools</code>,
<code>@EnableCacheServers</code> is a composite annotation for
aggregating multiple <code>@EnableCacheServer</code> annotations on a
single class. Java 8 and earlier do not allow more than one
annotation of the same type to be declared on a single class.

In all cases, you have specified hard-coded values for all hostnames, ports, and
configuration-oriented annotation attributes. This is not ideal when the
application gets promoted and deployed to different environments, such
as from DEV to QA to STAGING to PROD.

The next section covers how to handle dynamic configuration determined
at runtime.

## <a id="configuring-and-bootstrapping-locators"></a>Configuring and Bootstrapping Locators

Besides GemFire Cache applications, you may also create
GemFire Locator applications.

A GemFire Locator is a JVM process that allows nodes to join a
GemFire cluster as peer members. Locators also enable clients
to discover servers in a cluster. A Locator provides metadata to the
clients to uniformly balance the load across the members in the cluster,
enables single-hop data access operations.

A complete discussion of Locators is beyond the scope of this document.
For more details about Locators and their role in the clusters, see
[How Member Discovery Works](https://docs.vmware.com/en/VMware-GemFire/9.15/gf/topologies_and_comm-topology_concepts-how_member_discovery_works.html)
in the GemFire product documentation.

To configure and bootstrap a standalone Locator process, do the following:

**Spring Boot, GemFire Locator Application**

```highlight
@SpringBootApplication
@LocatorApplication(port = 12345)
class LocatorApplication { ... }
```

You can start multiple Locators in your cluster. The only requirement is
that the member name must be unique in the cluster. Use the `name`
attribute of the `@LocatorApplication` annotation to name the member
Locator in the cluster accordingly. Alternatively, you can set the
`spring.data.gemfire.locator.name` property in Spring Boot's
`application.properties`.

Additionally, you must ensure that each Locator starts on a unique port
if you fork multiple Locators on the same machine. Set either the `port`
annotation attribute or the `spring.data.gemfire.locator.port` property.

You can then start one or more GemFire peer cache members in the
cluster joined by the Locator, or Locators, also configured and
bootstrapped with Spring, as follows:

**Spring Boot, GemFire `CacheServer` Application joined by the Locator on `localhost`, port `12345`**

```highlight
@SpringBootApplication
@CacheServerApplication(locators = "localhost[12345]")
class ServerApplication { ... }
```

You can start as many of the `ServerApplication` classes, joined
by our Locator above, as long as each member is uniquely named.

`@LocatorApplication` is for configuring and bootstrapping standalone,
GemFire Locator application processes. This process can only
be a Locator and nothing else. If you try to start a Locator with a
cache instance, Spring Data for VMware GemFire will throw an error.

To simultaneously start a cache instance along with an
embedded Locator, then you should use the `@EnableLocator` annotation
instead.

Starting an embedded Locator is convenient during development. However,
it is highly recommended that you run standalone Locator processes in
production for high availability. If all your Locators in the cluster go
down, then the cluster will remain intact, however, no new members will
be able to join the cluster, which is important to scale-out linearly in
order to satisfy demand.

For more details, see [Configuring an Embedded Locator](#configuring-an-embedded-locator).

## <a id="runtime-configuration-using-configurers"></a>Runtime Configuration Using `Configurers`

Another goal when designing the annotation-based configuration model was
to preserve type safety in the annotation attributes. For example, if
the configuration attribute could be expressed as an `int` (such as a
port number), then the attribute's type should be an `int`.

Unfortunately, this is not conducive to dynamic and resolvable
configuration at runtime.

One of the finer features of Spring is the ability to use property
placeholders and SpEL expressions in properties or attributes of the
configuration metadata when configuring beans in the Spring container.
However, this would require all annotation attributes to be of type
`String`, thereby giving up type safety, which is not desirable.

So, Spring Data for VMware GemFire borrows from another commonly used pattern in Spring,
`Configurers`. Many different `Configurer` interfaces are provided in
Spring Web MVC, including the
[org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer](https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/web/servlet/config/annotation/ContentNegotiationConfigurer.html).

The `Configurers` design pattern enables application developers to
receive a callback to customize the configuration of a component or bean
on startup. The framework calls back to user-provided code to adjust the
configuration at runtime. One of the more common uses of this pattern is
to supply conditional configuration based on the application's runtime
environment.

Spring Data for VMware GemFire provides several `Configurer` callback interfaces to
customize different aspects of the annotation-based configuration
metadata at runtime, before the Spring managed beans that the
annotations create are initialized:

- `CacheServerConfigurer`

- `ClientCacheConfigurer`

- `ContinuousQueryListenerContainerConfigurer`

- `DiskStoreConfigurer`

- `IndexConfigurer`

- `PeerCacheConfigurer`

- `PoolConfigurer`

- `RegionConfigurer`

- `GatewayReceiverConfigurer`

- `GatewaySenderConfigurer`

For example, you can use the `CacheServerConfigurer` and
`ClientCacheConfigurer` to customize the port numbers used by your
Spring Boot `CacheServer` and `ClientCache` applications, respectively.

Consider the following example from a server application:

**Customizing a Spring Boot `CacheServer` application with a `CacheServerConfigurer`**

```highlight
@SpringBootApplication
@CacheServerApplication(name = "SpringServerApplication")
class ServerApplication {

  @Bean
  CacheServerConfigurer cacheServerPortConfigurer(
          @Value("${gemfire.cache.server.host:localhost}") String cacheServerHost
          @Value("${gemfire.cache.server.port:40404}") int cacheServerPort) {

      return (beanName, cacheServerFactoryBean) -> {
          cacheServerFactoryBean.setBindAddress(cacheServerHost);
          cacheServerFactoryBean.setHostnameForClients(cacheServerHost);
          cacheServerFactoryBean.setPort(cacheServerPort);
      };
  }
}
```

Next, consider the following example from a client application:

**Customizing a Spring Boot `ClientCache` application with a `ClientCacheConfigurer`**

```highlight
@SpringBootApplication
@ClientCacheApplication
class ClientApplication {

  @Bean
  ClientCacheConfigurer clientCachePoolPortConfigurer(
          @Value("${gemfire.cache.server.host:localhost}") String cacheServerHost
          @Value("${gemfire.cache.server.port:40404}") int cacheServerPort) {

      return (beanName, clientCacheFactoryBean) ->
          clientCacheFactoryBean.setServers(Collections.singletonList(
              new ConnectionEndpoint(cacheServerHost, cacheServerPort)));
  }
}
```

By using the provided `Configurers`, you can receive a callback to
further customize the configuration that is enabled by the associated
annotation at runtime, during startup.

Additionally, when the `Configurer` is declared as a bean in the Spring
container, the bean definition can take advantage of other Spring
container features, such as property placeholders and SpEL expressions by
using the `@Value` annotation on factory method parameters.

All `Configurers` provided by Spring Data for VMware GemFire take two bits of information in
the callback: the name of the bean created in the Spring container by
the annotation and a reference to the `FactoryBean` used by the
annotation to create and configure the GemFire component (for
example, a `ClientCache` instance is created and configured with
`ClientCacheFactoryBean`).

<p class="note"><strong>Note</strong>: Spring Data for VMware GemFire <code>FactoryBeans</code> are part of
the Spring Data for VMware GemFire public API and are what you would use in Spring's
<a href="https://docs.spring.io/spring-framework/docs/current/reference/html/core.html#beans-java">Java-based container configuration</a>
if this new annotation-based configuration model were not
provided. The annotations themselves use these same
<code>FactoryBeans</code> for their configuration. In essence, the
annotations are a facade providing an extra layer of abstraction for
convenience.</p>

Given that a `Configurer` can be declared as a regular bean definition
like any other POJO, you can combine different Spring configuration
options, such as the use of Spring Profiles with `Conditions` that use
both property placeholders and SpEL expressions. These and other nifty
features let you create even more sophisticated and flexible
configurations.

However, `Configurers` are not the only option.

## <a id="runtime-configuration-using-properties"></a>Runtime Configuration Using `Properties`

In addition to `Configurers`, each annotation attribute in the
annotation-based configuration model is associated with a corresponding
configuration property, prefixed with `spring.data.gemfire.`, which you can
declare in a Spring Boot `application.properties` file.

Building on the earlier examples, the client's `application.properties`
file would define the following set of properties:

**Client `application.properties`**

```highlight
spring.data.gemfire.cache.log-level=info
spring.data.gemfire.pool.Venus.servers=venus[48484]
spring.data.gemfire.pool.Venus.max-connections=200
spring.data.gemfire.pool.Venus.min-connections=50
spring.data.gemfire.pool.Venus.ping-interval=15000
spring.data.gemfire.pool.Venus.pr-single-hop-enabled=true
spring.data.gemfire.pool.Venus.read-timeout=20000
spring.data.gemfire.pool.Venus.subscription-enabled=true
spring.data.gemfire.pool.Saturn.locators=skullbox[20668]
spring.data.gemfire.pool.Saturn.subscription-enabled=true
spring.data.gemfire.pool.Neptune.servers=saturn[41414],neptune[42424]
spring.data.gemfire.pool.Neptune.min-connections=25
```

The corresponding server's `application.properties` file would define
the following properties:

**Server `application.properties`**

```highlight
spring.data.gemfire.cache.log-level=info
spring.data.gemfire.cache.server.port=40404
spring.data.gemfire.cache.server.Venus.port=43434
spring.data.gemfire.cache.server.Saturn.port=41414
spring.data.gemfire.cache.server.Neptune.port=41414
```

Then you can simplify the `@ClientCacheApplication` class to the following:

**Spring `@ClientCacheApplication` class**

```highlight
@SpringBootApplication
@ClientCacheApplication
@EnablePools(pools = {
    @EnablePool(name = "Venus"),
    @EnablePool(name = "Saturn"),
    @EnablePool(name = "Neptune")
})
class ClientApplication { .. }
```
Also, the `@CacheServerApplication` class becomes the following:

**Spring `@CacheServerApplication` class**

```highlight
@SpringBootApplication
@CacheServerApplication(name = "SpringServerApplication")
@EnableCacheServers(servers = {
    @EnableCacheServer(name = "Venus"),
    @EnableCacheServer(name = "Saturn"),
    @EnableCacheServer(name = "Neptune")
})
class ServerApplication { .. }
```

The preceding example shows why it is important to "name" your
annotation-based beans (other than because it is required in certain
cases). Doing so makes it possible to reference the bean in the Spring
container from XML, properties, and Java. It is even possible to inject
annotation-defined beans into an application class, for whatever
purpose, as the following example demonstrates:

```highlight
@Component
class MyApplicationComponent {

  @Resource(name = "Saturn")
  CacheServer saturnCacheServer;

  ...
}
```

Similarly, naming an annotation-defined bean lets you code a `Configurer`
to customize a specific, "named" bean since the `beanName` is one of two
arguments passed to the callback.

Ofte, an associated annotation attribute property takes two forms:
a "named" property along with an "unnamed" property.

The following example shows such an arrangement:

```highlight
spring.data.gemfire.cache.server.bind-address=10.105.20.1
spring.data.gemfire.cache.server.Venus.bind-address=10.105.20.2
spring.data.gemfire.cache.server.Saturn...
spring.data.gemfire.cache.server.Neptune...
```

While there are three named `CacheServers` above, there is also one
unnamed `CacheServer` property providing the default value for any
unspecified value of that property, even for "named" `CacheServers`. So,
while "Venus" sets and overrides its own `bind-address`, "Saturn" and
"Neptune" inherit from the "unnamed"
`spring.data.gemfire.cache.server.bind-address` property.

See an annotation's Javadoc for which annotation attributes support
property-based configuration and whether they support "named" properties
over default, "unnamed" properties.

### <a id="properties-of-properties"></a>`Properties` of `Properties`

You can express `Properties` in terms of other `Properties`.

The following example shows a nested property being set in an `application.properties` file:

**Properties of Properties**

```highlight
spring.data.gemfire.cache.server.port=${gemfire.cache.server.port:40404}
```

The following example shows a nested property being set in Java:

**Property placeholder nesting**

```highlight
@Bean
CacheServerConfigurer cacheServerPortConfigurer(
    @Value("${gemfire.cache.server.port:${some.other.property:40404}}")
    int cacheServerPort) {
  ...
}
```

Property placeholder nesting can be arbitrarily deep.

## <a id="configuring-embedded-services"></a>Configuring Embedded Services

GemFire provides the ability to start many different embedded
services that are required by an application, depending on the use case.

### <a id="configuring-an-embedded-locator"></a>Configuring an Embedded Locator

GemFire Locators are used by clients
to connect to and find servers in a cluster. Additionally, new members
joining an existing cluster use Locators to find their peers.

It is often convenient for application developers as they are developing
their Spring Boot and Spring Data for VMware GemFire applications to startup up a small
cluster of two or three GemFire servers. Rather than starting
a separate Locator process, you can annotate your Spring Boot
`@CacheServerApplication` class with `@EnableLocator`, as follows:

**Spring, GemFire `CacheServer` application running an embedded Locator**

```highlight
@SpringBootApplication
@CacheServerApplication
@EnableLocator
class ServerApplication { .. }
```

The `@EnableLocator` annotation starts an embedded Locator in the Spring
GemFire `CacheServer` application running on `localhost`,
listening on the default Locator port, `10334`. You can customize the
`host` (bind address) and `port` that the embedded Locator binds to by
using the corresponding annotation attributes.

Alternatively, you can set the `@EnableLocator` attributes by setting
the corresponding `spring.data.gemfire.locator.host` and
`spring.data.gemfire.locator.port` properties in
`application.properties`.

Then you can start other Spring Boot `@CacheServerApplication`-enabled
applications by connecting to this Locator with the following:

**Spring, GemFire `CacheServer` application connecting to a Locator**

```highlight
@SpringBootApplication
@CacheServerApplication(locators = "localhost[10334]")
class ServerApplication { .. }
```

You can combine both application classes shown earlier into a
single class and use your IDE to create different run profile
configurations to launch different instances of the same class with
slightly modified configuration by using Java system properties, as
follows:

**Spring `CacheServer` application running an embedded Locator and connecting to the Locator**

```highlight
@SpringBootApplication
@CacheServerApplication(locators = "localhost[10334]")
public class ServerApplication {

  public static void main(String[] args) {
    SpringApplication.run(ServerApplication.class);
  }

  @EnableLocator
  @Profile("embedded-locator")
  static class Configuration { }

}
```

Then, for each run profile, you can set and change the following system properties:

**IDE run profile configuration**

```highlight
spring.data.gemfire.name=SpringCacheServerOne
spring.data.gemfire.cache.server.port=41414
spring.profiles.active=embedded-locator
```
Only one of the run profiles for the `ServerApplication` class should set
the `-Dspring.profiles.active=embedded-locator` Java system property.
Then you can change the `..name` and `..cache.server.port` for each of
the other run profiles and have a small cluster (distributed system) of
GemFire servers running on your local system.

<p class="note"><strong>Note</strong>: The <code>@EnableLocator</code> annotation was meant
to be a development-time annotation only and not something an
application developer would use in production. We strongly recommend
running Locators as standalone, independent processes in the
cluster.</p>

For more information about how GemFire Locators work, see
[How Member Discovery Works](https://docs.vmware.com/en/VMware-GemFire/9.15/gf/topologies_and_comm-topology_concepts-how_member_discovery_works.html)
in the GemFire product documentation.

### <a id="configuring-an-embedded-manager"></a>Configuring an Embedded Manager

A GemFire Manager is another peer member or node in the
cluster that is responsible for cluster "management". Management
involves creating `Regions`, `Indexes`, `DiskStores`, among other
things, along with monitoring the runtime operations and behavior of the
cluster components.

The Manager lets a JMX-enabled client, (such as the `gfsh` shell tool,
connect to the Manager to manage the cluster. It is also possible to
connect to a Manager with JDK-provided tools such as JConsole or
JVisualVM, given that these are both JMX-enabled clients as well.

Perhaps you would also like to enable the Spring
`@CacheServerApplication` shown earlier as a Manager as well. To do so,
annotate your Spring `@Configuration` or `@SpringBootApplication` class
with `@EnableManager`.

By default, the Manager binds to `localhost`, listening on the default
Manager port of `1099`. Several aspects of the Manager can be configured
with annotation attributes or the corresponding properties.

The following example shows how to create an embedded Manager in Java:

**Spring `CacheServer` application running an embedded Manager**

```highlight
@SpringBootApplication
@CacheServerApplication(locators = "localhost[10334]")
public class ServerApplication {

  public static void main(String[] args) {
    SpringApplication.run(ServerApplication.class);
  }

  @EnableLocator
  @EnableManager
  @Profile("embedded-locator-manager")
  static class Configuration { }

}
```

With the preceding class, you can use `gfsh` to connect to the
small cluster and manage it, as follows:

```highlight
$ gfsh
    _________________________     __
   / _____/ ______/ ______/ /____/ /
  / /  __/ /___  /_____  / _____  /
 / /__/ / ____/  _____/ / /    / /
/______/_/      /______/_/    /_/    1.2.1

Monitor and Manage GemFire

gfsh>connect
Connecting to Locator at [host=localhost, port=10334] ..
Connecting to Manager at [host=10.99.199.5, port=1099] ..
Successfully connected to: [host=10.99.199.5, port=1099]

gfsh>list members
         Name          | Id
---------------------- | ----------------------------------------------------
SpringCacheServerOne   | 10.99.199.5(SpringCacheServerOne:14842)<ec><v0>:1024
SpringCacheServerTwo   | 10.99.199.5(SpringCacheServerTwo:14844)<v1>:1025
SpringCacheServerThree | 10.99.199.5(SpringCacheServerThree:14846)<v2>:1026
```

Because we also have the embedded Locator enabled, we can connect
indirectly to the Manager through the Locator. A Locator lets JMX
clients connect and find a Manager in the cluster. If none exists, the
Locator assumes the role of a Manager. However, if no Locator exists, we
would need to connect directly to the Manager by using the following:

**`gfsh` `connect` command connecting directly to the Manager**

```highlight
gfsh>connect --jmx-manager=localhost[1099]
```

<p class="note"><strong>Note</strong>: The <code>@EnableManager</code> annotation is meant to be a
development-time only annotation and not something used in production. We strongly recommend that Managers,
like Locators, be standalone, independent and dedicated processes in the cluster.</p>

For more details about GemFire management and monitoring, see [Managing GemFire](https://docs.vmware.com/en/VMware-GemFire/9.15/gf/managing-book_intro.html)
in the GemFire product documentation.

### <a id="configuring-the-embedded-http-server"></a>Configuring the Embedded HTTP Server

GemFire is also capable of running an embedded HTTP server.
The current implementation is backed by [Eclipse
Jetty](https://www.eclipse.org/jetty/).

The embedded HTTP server is used to host GemFire's Management
(Admin) REST API (not a publicly advertised API), the
[Developer REST API](https://docs.vmware.com/en/VMware-GemFire/9.15/gf/rest_apps-book_intro.html), and
the [Pulse Monitoring Web Application](https://docs.vmware.com/en/VMware-GemFire/9.15/gf/tools_modules-pulse-pulse-overview.html).

However, to use any of these GemFire-provided web
applications, you must have a full installation of GemFire
installed on your system, and you must set the `GEODE_HOME` environment
variable to your installation directory.

To enable the embedded HTTP server, add the `@EnableHttpService`
annotation to any `@PeerCacheApplication` or `@CacheServerApplication`
annotated class, as follows:

**Spring `CacheServer` application running the embedded HTTP server**

```highlight
@SpringBootApplication
@CacheServerApplication
@EnableHttpService
public class ServerApplication { .. }
```

By default, the embedded HTTP server listens on port `7070` for HTTP
client requests. You can use the annotation attributes or
corresponding configuration properties to adjust the port as needed.

### <a id="configuring-the-embedded-memcached-server"></a>Configuring the Embedded Memcached Server (Gemcached)

GemFire also implements the Memcached protocol with the
ability to service Memcached clients. That is, Memcached clients can
connect to a GemFire cluster and perform Memcached operations
as if the GemFire servers in the cluster were actual Memcached
servers.

To enable the embedded Memcached service, add the
`@EnableMemcachedServer` annotation to any `@PeerCacheApplication` or
`@CacheServerApplication` annotated class, as follows:

**Spring `CacheServer` application running an embedded Memcached server**

```highlight
@SpringBootApplication
@CacheServerApplication
@EnabledMemcachedServer
public class ServerApplication { .. }
```

For more details about GemFire's Memcached service, see [Gemcached](https://docs.vmware.com/en/VMware-GemFire/9.15/gf/tools_modules-gemcached-chapter_overview.html)
in the GemFire product documentation.

## <a id="configuring-logging"></a>Configuring Logging

Often, it is necessary to turn up logging to understand
exactly what GemFire is doing and when.

To enable Logging, annotate your application class with `@EnableLogging`
and set the appropriate attributes or associated properties, as follows:

**Spring `ClientCache` application with Logging enabled**

```highlight
@SpringBootApplication
@ClientCacheApplication
@EnableLogging(logLevel="info", logFile="/absolute/file/system/path/to/application.log)
public class ClientApplication { .. }
```

While the `logLevel` attribute can be specified with all the cache-based
application annotations (for example,
`@ClientCacheApplication(logLevel="info")`), it is easier to customize
logging behavior with the `@EnableLogging` annotation.

Additionally, you can configure the `log-level` by setting the
`spring.data.gemfire.logging.level` property in
`application.properties`.

For more details, see the [@EnableLogging annotation Javadoc](https://docs.spring.io/spring-data/gemfire/docs/current/api/org/springframework/data/gemfire/config/annotation/EnableLogging.html).

## <a id="configuring-statistics"></a>Configuring Statistics

To gain even deeper insight into GemFire at runtime, you can
enable statistics. Gathering statistical data facilitates system
analysis and troubleshooting when complex problems, which are often
distributed in nature and where timing is a crucial factor, occur.

When statistics are enabled, you can use GemFire's
[Visual Statistics Display (VSD)](https://docs.vmware.com/en/VMware-GemFire/9.15/gf/tools_modules-vsd-chapter_overview.html)
tool to analyze the statistical data that is collected.

To enable statistics, annotate your application class with
`@EnableStatistics`, as follows:

**Spring `ClientCache` application with Statistics enabled**

```highlight
@SpringBootApplication
@ClientCacheApplication
@EnableStatistics
public class ClientApplication { .. }
```

Enabling statistics on a server is particularly valuable when evaluating
performance. To do so, annotate your `@PeerCacheApplication` or
`@CacheServerApplication` class with `@EnableStatistics`.

You can use the `@EnableStatistics` annotation attributes or associated
properties to customize the statistics gathering and collection process.

For more details, see the [@EnableStatistics annotation Javadoc](https://docs.spring.io/spring-data/gemfire/docs/current/api/org/springframework/data/gemfire/config/annotation/EnableStatistics.html).

For more information about GemFire's statistics, see [Statistics](https://docs.vmware.com/en/VMware-GemFire/9.15/gf/managing-statistics-chapter_overview.html)
in the GemFire product documentation.

## <a id="configuring-pdx"></a>Configuring PDX




One of the more powerful features of GemFire is [PDX serialization](https://docs.vmware.com/en/VMware-GemFire/9.15/gf/developing-data_serialization-gemfire_pdx_serialization.html).
While a complete discussion of PDX is beyond the scope
of this document, serialization using PDX is a much better alternative
to Java serialization, with the following benefits:

- PDX uses a centralized type registry to keep the serialized bytes of
  an object more compact.

- PDX is a neutral serialization format, allowing both Java and Native
  clients to operate on the same data set.

- PDX supports versioning and lets object fields be added or removed
  without affecting existing applications using either older or newer
  versions of the PDX serialized objects that have changed, without data
  loss.

- PDX lets object fields be accessed individually in OQL query
  projections and predicates without the object needing to be
  de-serialized first.

In general, serialization in GemFire is required any time data
is transferred to or from clients and servers or between peers in a
cluster during normal distribution and replication processes as well as
when data is overflowed or persisted to disk.

Enabling PDX serialization is much simpler than modifying all of your
application domain object types to implement `java.io.Serializable`,
especially when it may be undesirable to impose such restrictions on
your application domain model or you do not have any control over the
objects your are serializing, which is especially true when using a 3rd
party library (e.g. think of a geo-spatial API with `Coordinate` types).

To enable PDX, annotate your application class with `@EnablePdx`, as
follows:

**Spring `ClientCache` application with PDX enabled**

```highlight
@SpringBootApplication
@ClientCacheApplication
@EnablePdx
public class ClientApplication { .. }
```

Typically, an application's domain object types either implements the
[org.apache.geode.pdx.PdxSerializable](https://geode.apache.org/releases/latest/javadoc/org/apache/geode/pdx/PdxSerializable.html)
interface or you can implement and register a non-invasive
implementation of the
[org.apache.geode.pdx.PdxSerializer](https://geode.apache.org/releases/latest/javadoc/org/apache/geode/pdx/PdxSerializer.html)
interface to handle all the application domain object types that need to
be serialized.

Unfortunately, GemFire only lets one `PdxSerializer` be
registered, which suggests that all application domain object types need
to be handled by a single `PdxSerializer` instance. However, that is a
serious anti-pattern and an unmaintainable practice.

Even though only a single `PdxSerializer` instance can be registered
with GemFire, it makes sense to create a single
`PdxSerializer` implementation per application domain object type.

By using the [Composite Software Design Pattern](https://en.wikipedia.org/wiki/Composite_pattern), you can
provide an implementation of the `PdxSerializer` interface that
aggregates all of the application domain object type-specific
`PdxSerializer` instances, but acts as a single `PdxSerializer` instance
and register it.

You can declare this composite `PdxSerializer` as a managed bean in the
Spring container and refer to this composite `PdxSerializer` by its bean
name in the `@EnablePdx` annotation using the `serializerBeanName`
attribute. Spring Data for VMware GemFire takes care of registering it with
GemFire on your behalf.

The following example shows how to create a custom composite
`PdxSerializer`:

**Spring `ClientCache` application with PDX enabled, using a custom composite `PdxSerializer`**

```highlight
@SpringBootApplication
@ClientCacheApplication
@EnablePdx(serializerBeanName = "compositePdxSerializer")
public class ClientApplication {

  @Bean
  PdxSerializer compositePdxSerializer() {
      return new CompositePdxSerializerBuilder()...
  }
}
```

It is also possible to declare GemFire's
[org.apache.geode.pdx.ReflectionBasedAutoSerializer](https://geode.apache.org/releases/latest/javadoc/org/apache/geode/pdx/ReflectionBasedAutoSerializer.html)
as a bean definition in a Spring context.

Alternatively, you should use Spring Data for VMware GemFire's more robust
[org.springframework.data.gemfire.mapping.MappingPdxSerializer](https://docs.spring.io/spring-data/geode/docs/current/api/org/springframework/data/gemfire/mapping/MappingPdxSerializer.html),
which uses Spring Data mapping metadata and infrastructure applied to
the serialization process for more efficient handling than reflection
alone.

Many other aspects and features of PDX can be adjusted with the
`@EnablePdx` annotation attributes or associated configuration
properties.

For more details, see the [@EnablePdx annotation Javadoc](https://docs.spring.io/spring-data/gemfire/docs/current/api/org/springframework/data/gemfire/config/annotation/EnablePdx.html).

## <a id="configuring-gemfire-properties"></a>Configuring GemFire Properties

While many of the `gemfire.properties`
are encapsulated in and abstracted with an annotation in
the Spring Data for VMware GemFire annotation-based configuration model, the less
commonly used GemFire properties are still accessible from the
`@EnableGemFireProperties` annotation.

Annotating your application class with `@EnableGemFireProperties` is
convenient and a nice alternative to creating a `gemfire.properties`
file or setting GemFire properties as Java system properties
on the command line when launching your application.

We recommend that you set these GemFire properties
in a <code>gemfire.properties</code> file when deploying your
application to production. However, at development time, it can be
convenient to set these properties individually, as needed, for
prototyping, debugging and testing purposes.

A few examples of some of the less common GemFire properties
that you usually need not worry about include, but are not limited to:
`ack-wait-threshold`, `disable-tcp`, `socket-buffer-size`, and others.

To individually set any GemFire property, annotate your
application class with `@EnableGemFireProperties` and set the
GemFire properties to change from the default value
set by GemFire with the corresponding attribute, as follows:

**Spring `ClientCache` application with specific GemFire properties set**

```highlight
@SpringBootApplication
@ClientCacheApplication
@EnableGemFireProperties(conflateEvents = true, socketBufferSize = 16384)
public class ClientApplication { .. }
```

Some of the GemFire properties are
client-specific (for example, `conflateEvents`), while others are
server-specific (for example `distributedSystemId`,
`enableNetworkPartitionDetection`, `enforceUniqueHost`, `memberTimeout`,
`redundancyZone`).

For more details about GemFire properties, see [GemFire properties](https://docs.vmware.com/en/VMware-GemFire/9.15/gf/reference-topics-gemfire_properties.html)
in the GemFire product documentation.

## <a id="configuring-regions"></a>Configuring Regions

So far, outside of PDX, our discussion has centered around configuring
GemFire's more administrative functions: creating a cache
instance, starting embedded services, enabling logging and statistics,
configuring PDX, and using `gemfire.properties` to affect low-level
configuration and behavior. While all these configuration options are
important, none of them relate directly to your application. In other
words, we still need some place to store our application data and make
it generally available and accessible.

GemFire organizes data in a cache into
[Data Regions](https://docs.vmware.com/en/VMware-GemFire/9.15/gf/basic_config-data_regions-chapter_overview.html?hWord=N4IghgNiBcICZgC5gAQCcCmBzAlgewDsBnEAXyA).
You can think of a Region as a table in a relational database.
Generally, a Region should only store a single type of object, which
makes it more conducive for building effective indexes and writing
queries.

Previously, Spring Data for VMware GemFire users needed to explicitly define and declare the
Regions used by their applications to store data by writing very verbose
Spring configuration metadata, whether using Spring Data for VMware GemFire's
`FactoryBeans` from the API with Spring's
<a href="https://docs.spring.io/spring-framework/docs/current/reference/html/core.html#beans-java">Java-based container configuration</a>
or using [XML](region.html).

The following example demonstrates how to configure a Region bean in
Java:

**Example Region bean definition using Spring's Java-based container configuration**

```highlight
@Configuration
class GemFireConfiguration {

  @Bean("Example")
  PartitionedRegionFactoryBean exampleRegion(GemFireCache gemfireCache) {

      PartitionedRegionFactoryBean<Long, Example> exampleRegion =
          new PartitionedRegionFactoryBean≤>();

      exampleRegion.setCache(gemfireCache);
      exampleRegion.setClose(false);
      exampleRegion.setPersistent(true);

      return exampleRegion;
  }

  ...
}
```

The following example demonstrates how to configure the same Region bean
in XML:

**Example Region bean definition using Spring Data for VMware GemFire's XML Namespace**

```highlight
<gfe:partitioned-region id="exampleRegion" name="Example" persistent="true">
    ...
</gfe:partitioned-region>
```

While neither Java nor XML configuration is all that difficult to
specify, either one can be cumbersome, especially if an application
requires a large number of Regions. Many relational database-based
applications can have hundreds or even thousands of tables.

Defining and declaring all these Regions by hand would be cumbersome and
error prone. Well, now there is a better way.

Now you can define and configure Regions based on their application
domain objects (entities) themselves. No longer do you need to
explicitly define `Region` bean definitions in Spring configuration
metadata, unless you require finer-grained control.

To simplify Region creation, Spring Data for VMware GemFire combines the use of Spring Data
Repositories with the expressive power of annotation-based configuration
using the new `@EnableEntityDefinedRegions` annotation.

<p class="note"><strong>Note</strong>: Most Spring Data application developers should
already be familiar with the
<a href="https://docs.spring.io/spring-data/data-commons/docs/current/reference/html/#repositories">Spring Data Repository abstraction</a>
and Spring Data for VMware GemFire's <a
href=repositories.html>implementation/extension</a>, which has
been specifically customized to optimize data access operations for
GemFire.</p>

First, an application developer starts by defining the application's
domain objects (entities), as follows:

**Application domain object type modeling a Book**

```highlight
@Region("Books")
class Book {

  @Id
  private ISBN isbn;

  private Author author;

  private Category category;

  private LocalDate releaseDate;

  private Publisher publisher;

  private String title;

}
```

Next, you define a basic repository for `Books` by extending Spring Data
Commons `org.springframework.data.repository.CrudRepository` interface,
as follows:

**Repository for Books**

```highlight
interface BookRepository extends CrudRepository<Book, ISBN> { .. }
```

The `org.springframe.data.repository.CrudRepository` is a Data Access
Object (DAO) providing basic data access operations (CRUD) along with
support for simple queries (such as `findById(..)`). You can define
additional, more sophisticated queries by declaring query methods on the
repository interface (for example,
`List<Book> findByAuthor(Author author);`).

Under the hood, Spring Data for VMware GemFire provides an implementation of your
application's repository interfaces when the Spring container is
bootstrapped. Spring Data for VMware GemFire even implements the query methods you define
so long as you follow the
[conventions](https://docs.spring.io/spring-data/geode/docs/current/reference/html/#gemfire-repositories.executing-queries).

Now, when you defined the `Book` class, you also specified the Region in
which instances of `Book` are mapped (stored) by declaring the
Spring Data for VMware GemFire mapping annotation, `@Region` on the entity's type. Of
course, if the entity type (`Book`, in this case) referenced in the type
parameter of the repository interface (`BookRepository`, in this case)
is not annotated with `@Region`, the name is derived from the simple
class name of the entity type (also `Book`, in this case).

Spring Data for VMware GemFire uses the mapping context, which contains mapping metadata for
all the entities defined in your application, to determine all the
Regions that are needed at runtime.

To enable and use this feature, annotate the application class with
`@EnableEntityDefinedRegions`, as follows:

**Entity-defined Region Configuration**

```highlight
@SpringBootApplication
@ClientCacheApplication
@EnableEntityDefinedRegions(basePackages = "example.app.domain")
@EnableGemfireRepositories(basePackages = "example.app.repo")
class ClientApplication { .. }
```

Creating Regions from entity classes is most useful
when using Spring Data Repositories in your application. Spring Data for VMware GemFire's
Repository support is enabled with the
<code>@EnableGemfireRepositories</code> annotation, as shown in the
preceding example.

<p class="note"><strong>Note</strong>: Only entity classes explicitly annotated
with <code>@Region</code> are picked up by the scan and will have
Regions created. If an entity class is not explicitly mapped with
<code>@Region</code> no Region will be created.</p>

By default, the `@EnableEntityDefinedRegions` annotation scans for
entity classes recursively, starting from the package of the
configuration class on which the `@EnableEntityDefinedRegions`
annotation is declared.

However, it is common to limit the search during the scan by setting the
`basePackages` attribute with the package names containing your
application entity classes.

Alternatively, you can use the more type-safe `basePackageClasses`
attribute for specifying the package to scan by setting the attribute to
an entity type in the package that contains the entity's class, or by
using a non-entity placeholder class specifically created for
identifying the package to scan.

The following example shows how to specify the entity types to scan:

**Entity-defined Region Configuration using the Entity class type**

```highlight
@SpringBootApplication
@ClientCacheApplication
@EnableGemfireRepositories
@EnableEntityDefinedRegions(basePackageClasses = {
    example.app.books.domain.Book.class,
    example.app.customers.domain.Customer.class
})
class ClientApplication { .. }
```

In addition to specifying where to begin the scan, like Spring's
`@ComponentScan` annotation, you can specify `include` and `exclude`
filters with all the same semantics of the
`org.springframework.context.annotation.ComponentScan.Filter`
annotation.

For more details, see the [@EnableEntityDefinedRegions annotation Javadoc](https://docs.spring.io/spring-data/gemfire/docs/current/api/org/springframework/data/gemfire/config/annotation/EnableEntityDefinedRegions.html).

### <a id="configuring-type-specific-regions"></a>Configuring Type-Specific Regions


GemFire supports many different types of Regions. Each type corresponds to the Region's
[DataPolicy](https://geode.apache.org/releases/latest/javadoc/org/apache/geode/cache/DataPolicy.html),
which determines exactly how the data in the Region will be managed.

Other configuration settings, such as the Region's `scope`, can also affect how data is managed.
For details, see [Storage and Distribution Options](https://docs.vmware.com/en/VMware-GemFire/9.15/gf/developing-region_options-storage_distribution_options.html)
in the GemFire product documentation.

When you annotate your application domain object types with the generic
`@Region` mapping annotation, Spring Data for VMware GemFire decides which type of Region to
create. Spring Data for VMware GemFire's default strategy takes the cache type into
consideration when determining the type of Region to create.

For example, if you declare the application as a `ClientCache` by using
the `@ClientCacheApplication` annotation, Spring Data for VMware GemFire creates a client
`PROXY` `Region` by default. Alternatively, if you declare the
application as a peer `Cache` by using either the
`@PeerCacheApplication` or `@CacheServerApplication` annotations,
Spring Data for VMware GemFire creates a server `PARTITION` `Region` by default.

You can always override the default when necessary. To
override the default applied by Spring Data for VMware GemFire, four new Region mapping
annotations have been introduced:

- `@ClientRegion`

- `@LocalRegion`

- `@PartitionRegion`

- `@ReplicateRegion`

The `@ClientRegion` mapping annotation is specific to client
applications. All of the other Region mapping annotations listed above
can only be used in server applications that have an embedded peer
`Cache`.

It is sometimes necessary for client applications to create and use
local-only Regions, perhaps to aggregate data from other Regions in
order to analyze the data locally and carry out some function performed
by the application on the user's behalf. In this case, the data does not
need to be distributed back to the server unless other applications need
access to the results. This Region might even be temporary and discarded
after use, which could be accomplished with Idle-Timeout (TTI) and
Time-To-Live (TTL) expiration policies on the Region itself. For more information
about expiration policies, see [Configuring Expiration](#configuring-expiration).

<p class="note"><strong>Note</strong>:  Region-level Idle-Timeout (TTI) and Time-To-Live
(TTL) expiration policies are independent of and different from entry-level TTI and TTL expiration policies.</p>

To create a local-only client Region where the
data is not going to be distributed back to a corresponding Region on
the server with the same name, you can declare the `@ClientRegion`
mapping annotation and set the `shortcut` attribute to
`ClientRegionShortcut.LOCAL`, as follows:

**Spring `ClientCache` application with a local-only, client Region**

```highlight
@ClientRegion(shortcut = ClientRegionShortcut.LOCAL)
class ClientLocalEntityType { .. }
```

All Region type-specific annotations provide additional attributes that
are both common across Region types as well as specific to only that
type of Region. For example, the `collocatedWith` and `redundantCopies`
attributes in the `PartitionRegion` annotation apply to server-side,
`PARTITION` Regions only.

For more details about GemFire Region types, see [Region Types](https://docs.vmware.com/en/VMware-GemFire/9.15/gf/developing-region_options-region_types.html)
in the GemFire product documentation.

### <a id="configured-cluster-defined-regions"></a>Configured Cluster-Defined Regions

In addition to the `@EnableEntityDefinedRegions` annotation, Spring Data for VMware GemFire
also provides the inverse annotation, `@EnableClusterDefinedRegions`.
Rather than basing your Regions on the entity classes defined and driven
from your application use cases (UC) and requirements (the most common
and logical approach), alternatively, you can declare your Regions from
the Regions already defined in the cluster to which your `ClientCache`
application will connect.

This allows you to centralize your configuration using the cluster of
servers as the primary source of data definitions and ensure that all
client applications of the cluster have a consistent configuration. This
is particularly useful when quickly scaling up a large number instances
of the same client application to handle the increased load in a
cloud-managed environment.

The idea is, rather than the client application(s) driving the data
dictionary, the user defines Regions using GemFire's `gfsh`
CLI shell tool. This has the added advantage that when additional peers
are added to the cluster, they too will also have and share the same
configuration since it is remembered by GemFire's *Cluster
Configuration Service*.

As an example, a user might defined a Region in `gfsh` as follows:

**Defining a Region with `gfsh`**

```highlight
gfsh>create region --name=Books --type=PARTITION
 Member   | Status
--------- | --------------------------------------
ServerOne | Region "/Books" created on "ServerOne"
ServerTwo | Region "/Books" created on "ServerTwo"

gfsh>list regions
List of regions
---------------
Books

gfsh>describe region --name=/Books
..........................................................
Name            : Books
Data Policy     : partition
Hosting Members : ServerTwo
                  ServerOne

Non-Default Attributes Shared By Hosting Members

 Type  |    Name     | Value
------ | ----------- | ---------
Region | size        | 0
       | data-policy | PARTITION
```

With GemFire's *Cluster Configuration Service*, any additional
peer members added to the cluster of servers to handle the increased
load (on the backend) will also have the same configuration, for
example:

**Adding an additional peer member to the cluster**

```highlight
gfsh>list members
  Name    | Id
--------- | ----------------------------------------------
Locator   | 10.0.0.121(Locator:68173:locator)<ec><v0>:1024
ServerOne | 10.0.0.121(ServerOne:68242)<v3>:1025
ServerTwo | 10.0.0.121(ServerTwo:68372)<v4>:1026

gfsh>start server --name=ServerThree --log-level=config --server-port=41414
Starting a GemFire Server in /Users/you/geode/cluster/ServerThree...
...
Server in /Users/you/geode/cluster/ServerThree... on 10.0.0.121[41414] as ServerThree is currently online.
Process ID: 68467
Uptime: 3 seconds
GemFire Version: 1.2.1
Java Version: 1.8.0_152
Log File: /Users/you/geode/cluster/ServerThree/ServerThree.log
JVM Arguments: -Dgemfire.default.locators=10.0.0.121[10334]
  -Dgemfire.use-cluster-configuration=true
  -Dgemfire.start-dev-rest-api=false
  -Dgemfire.log-level=config
  -XX:OnOutOfMemoryError=kill -KILL %p
  -Dgemfire.launcher.registerSignalHandlers=true
  -Djava.awt.headless=true
  -Dsun.rmi.dgc.server.gcInterval=9223372036854775806
Class-Path: /Users/you/geode/cluster/apache-geode-1.2.1/lib/geode-core-1.2.1.jar
  :/Users/you/geode/cluster/apache-geode-1.2.1/lib/geode-dependencies.jar

gfsh>list members
   Name     | Id
----------- | ----------------------------------------------
Locator     | 10.0.0.121(Locator:68173:locator)<ec><v0>:1024
ServerOne   | 10.0.0.121(ServerOne:68242)vv3>:1025
ServerTwo   | 10.0.0.121(ServerTwo:68372)<v4>:1026
ServerThree | 10.0.0.121(ServerThree:68467)<v5>:1027

gfsh>describe member --name=ServerThree
Name        : ServerThree
Id          : 10.0.0.121(ServerThree:68467)<v5>:1027
Host        : 10.0.0.121
Regions     : Books
PID         : 68467
Groups      :
Used Heap   : 37M
Max Heap    : 3641M
Working Dir : /Users/you/geode/cluster/ServerThree
Log file    : /Users/you/geode/cluster/ServerThree/ServerThree.log
Locators    : 10.0.0.121[10334]

Cache Server Information
Server Bind              :
Server Port              : 41414
Running                  : true
Client Connections       : 0
```

As shown, "ServerThree" now has the "Books" Region. If the any or
all of the server go down, they will have the same configuration along
with the "Books" Region when they come back up.

On the client-side, many Book Store client application instances might
be started to process books against the Book Store online service. The
"Books" Region might be one of many different Regions needed to implement
the Book Store application service. Rather than have to create and
configure each Region individually, Spring Data for VMware GemFire conveniently allows
the client application Regions to be defined from the cluster, as
follows:

**Defining Client Regions from the Cluster with `@EnableClusterDefinedRegions`**

```highlight
@ClientCacheApplication
@EnableClusterDefinedRegions
class BookStoreClientApplication {

    public static void main(String[] args) {
        ....
    }
    ...
}
```
<p class="note"><strong>Note</strong>: <code>@EnableClusterDefinedRegions</code> can only
used on the client.</p>

<p class="note"><strong>Note</strong>: 
You can use the <code>clientRegionShortcut</code>
annotation attribute to control the type of Region created on the
client. By default, a client <code>PROXY</code> Region is created. Set
<code>clientRegionShortcut</code> to
<code>ClientRegionShortcut.CACHING_PROXY</code> to implement "<em>near
caching</em>". This setting applies to all client Regions created from
Cluster-defined Regions. To control individual settings
(like data policy) of the client Regions created from Regions defined on
the Cluster, implement a <a href="https://docs.spring.io/spring-data/geode/docs/current/api/org/springframework/data/gemfire/config/annotation/RegionConfigurer.html">RegionConfigurer</a>
with custom logic based on the Region name.</p>

To use the "Books" Region in your application, inject the "Books" Region directly, as follows:

**Using the "Books" Region**

```highlight
@org.springframework.stereotype.Repository
class BooksDataAccessObject {

    @Resource(name = "Books")
    private Region<ISBN, Book> books;

    // implement CRUD and queries with the "Books" Region
}
```

Or you can define a Spring Data Repository definition based on the
application domain type (entity), `Book`, mapped to the "Books" Region,
as follows:

**Using the "Books" Region with a SD Repository**

```highlight
interface BookRepository extends CrudRepository<Book, ISBN> {
    ...
}
```

You can then either inject your custom `BooksDataAccessObject` or the
`BookRepository` into your application service components to carry out
whatever business function required.

### <a id="configuring-eviction"></a>Configuring Eviction

Managing data with GemFire is an active task. Tuning is
generally required, and you must employ a combination of features (for
example, both eviction and expiration) to
effectively manage your data in memory with GemFire.

Given that GemFire is an In-Memory Data Grid (IMDG), data is
managed in-memory and distributed to other nodes that participate in a
cluster to minimize latency, maximize throughput and ensure
that data is highly available. Since not all of an application's data is
going to typically fit in memory (even across an entire cluster of
nodes, much less on a single node), you can increase capacity by adding
new nodes to the cluster. This is commonly referred to as linear
scale-out (rather than scaling up, which means adding more memory, more
CPU, more disk, or more network bandwidth — basically more of every
system resource to handle the load).

Still, even with a cluster of nodes, it is usually imperative that only
the most important data be kept in memory. Running out of memory, or
even venturing near full capacity, is rarely, if ever, a good thing.
Stop-the-world GCs or worse, `OutOfMemoryErrors`, will bring your
application to a halt.

To help manage memory and keep the most important data around,
GemFire supports Least Recently Used (LRU) eviction. That is,
GemFire evicts Region entries based on when those entries were
last accessed by using the Least Recently Used algorithm.

To enable eviction, annotate the application class with
`@EnableEviction`, as follows:

**Spring application with eviction enabled**

```highlight
@SpringBootApplication
@PeerCacheApplication
@EnableEviction(policies = {
    @EvictionPolicy(regionNames = "Books", action = EvictionActionType.INVALIDATE),
    @EvictionPolicy(regionNames = { "Customers", "Orders" }, maximum = 90,
        action = EvictionActionType.OVERFLOW_TO_DISK,
        type = EvictonPolicyType.HEAP_PERCENTAGE)
})
class ServerApplication { .. }
```

Eviction policies are usually set on the Regions in the servers.

As shown earlier, the `policies` attribute can specify one or more
nested `@EvictionPolicy` annotations, with each one being individually
catered to one or more Regions where the eviction policy needs to be
applied.

Additionally, you can reference a custom implementation of
GemFire's [org.apache.geode.cache.util.ObjectSizer](https://geode.apache.org/releases/latest/javadoc/org/apache/geode/cache/util/ObjectSizer.html)
interface, which can be defined as a bean in the Spring container and
referenced by name by using the `objectSizerName` attribute.

An `ObjectSizer` lets you define the criteria used to evaluate and
determine the the size of objects stored in a Region.

For a complete list of eviction configuration options, see the
[@EnableEviction annotation Javadoc](https://docs.spring.io/spring-data/gemfire/docs/current/api/org/springframework/data/gemfire/config/annotation/EnableEviction.html)

For more details about GemFire eviction, see [Eviction](https://docs.vmware.com/en/VMware-GemFire/9.15/gf/developing-eviction-chapter_overview.html)
in the GemFire product documentation.

### <a id="configuring-expiration"></a>Configuring Expiration

Along with [eviction](#configuring-eviction),
expiration can also be used to manage memory by allowing entries stored
in a Region to expire. GemFire supports both Time-to-Live
(TTL) and Idle-Timeout (TTI) entry expiration policies.

Spring Data for VMware GemFire provides the following expiration annotations:

- `Expiration`

- `IdleTimeoutExpiration`

- `TimeToLiveExpiration`

An application domain object type can be annotated with one or more of
the expiration annotations, as follows:

**Application domain object specific expiration policy**

```highlight
@Region("Books")
@TimeToLiveExpiration(timeout = 30000, action = "INVALIDATE")
class Book { .. }
```
To enable expiration, annotate the application class with
`@EnableExpiration`, as follows:

**Spring application with expiration enabled**

```highlight
@SpringBootApplication
@PeerCacheApplication
@EnableExpiration
class ServerApplication { .. }
```

In addition to application domain object type-level expiration policies,
you can directly and individually configure expiration policies on a
Region by Region basis using the `@EnableExpiration` annotation, as
follows:

**Spring application with region-specific expiration policies**

```highlight
@SpringBootApplication
@PeerCacheApplication
@EnableExpiration(policies = {
    @ExpirationPolicy(regionNames = "Books", types = ExpirationType.TIME_TO_LIVE),
    @ExpirationPolicy(regionNames = { "Customers", "Orders" }, timeout = 30000,
        action = ExpirationActionType.LOCAL_DESTROY)
})
class ServerApplication { .. }
```

The preceding example sets expiration policies for the `Books`,
`Customers`, and `Orders` Regions.

Expiration policies are usually set on the Regions in the servers.

For a complete list of expiration configuration options,
see the [@EnableExpiration annotation Javadoc](https://docs.spring.io/spring-data/gemfire/docs/current/api/org/springframework/data/gemfire/config/annotation/EnableExpiration.html).

For more details about GemFire expiration, see [Expiration](https://docs.vmware.com/en/VMware-GemFire/9.15/gf/developing-expiration-chapter_overview.html)
in the GemFire product documentation.

### <a id="configuring-compression"></a>Configuring Compression

In addition to [eviction](#configuring-eviction) and
[expiration](#configuring-expiration), you can
configure your data Regions with compression to reduce memory
consumption.

GemFire lets you compress in memory Region values by using
pluggable [Compressors](https://geode.apache.org/releases/latest/javadoc/org/apache/geode/compression/Compressor.html),
or different compression codecs. GemFire uses Google's [Snappy](https://google.github.io/snappy/) compression library by
default.

To enable compression, annotate the application class with
`@EnableCompression`, as follows:

**Spring application with Region compression enabled**

```highlight
@SpringBootApplication
@ClientCacheApplication
@EnableCompression(compressorBeanName = "MyCompressor", regionNames = { "Customers", "Orders" })
class ClientApplication { .. }
```

In the above example, neither the <code>compressorBeanName</code> nor the
<code>regionNames</code> attributes are required.

The `compressorBeanName` defaults to `SnappyCompressor`, enabling
[SnappyCompressor](https://geode.apache.org/releases/latest/javadoc/org/apache/geode/compression/SnappyCompressor.html).

The `regionNames` attribute is an array of Region names that specify the
Regions that have compression enabled. By default, all Regions compress
values if the `regionNames` attribute is not explicitly set.

Alternatively, you can use the
<code>spring.data.gemfire.cache.compression.compressor-bean-name</code>
and <code>spring.data.gemfire.cache.compression.region-names</code>
properties in the <code>application.properties</code> file to set and
configure the values of these <code>@EnableCompression</code> annotation
attributes.

<p class="note warning"><strong>Warning</strong>: To use GemFire's Region compression
feature, you must include the <code>org.iq80.snappy:snappy</code>
dependency in your application's <code>pom.xml</code> file (for Maven)
or <code>build.gradle</code> file (for Gradle). This is necessary only
if you use GemFire's default support for Region compression,
which uses the <a href="https://geode.apache.org/releases/latest/javadoc/org/apache/geode/compression/SnappyCompressor.html">SnappyCompressor</a>
by default. If you use another compression library, you must include dependencies for that compression library on your
application's classpath. Additionally, you must implement the <a href="https://geode.apache.org/releases/latest/javadoc/org/apache/geode/compression/Compressor.html">Compressor</a>
interface to adapt your compression library of choice, define it as a
bean in the Spring compressor, and set the <code>compressorBeanName</code> to this custom bean definition.</p>

For more details, see the [@EnableCompression annotation Javadoc](https://docs.spring.io/spring-data/gemfire/docs/current/api/org/springframework/data/gemfire/config/annotation/EnableCompression.html).

For more information about compression, see [Region Compression](https://docs.vmware.com/en/VMware-GemFire/9.15/gf/managing-region_compression.html)
in the GemFire product documentation.

### <a id="configuring-off-heap-memory"></a>Configuring Off-Heap Memory

Another effective means for reducing pressure on the JVM's Heap memory
and minimizing GC activity is to use GemFire's off-heap memory
support.

Rather than storing Region entries on the JVM Heap, entries are stored
in the system's main memory. Off-heap memory generally works best when
the objects being stored are uniform in size, are mostly less than 128K,
and do not need to be deserialized frequently, as explained in
[Managing Off-Heap Memory](https://docs.vmware.com/en/VMware-GemFire/9.15/gf/managing-heap_use-off_heap_management.html)
in the GemFire product documentation.

To enable off-heap, annotate the application class with
`@EnableOffHeap`, as follows:

**Spring application with Off-Heap enabled**

```highlight
@SpringBootApplication
@PeerCacheApplication
@EnableOffHeap(memorySize = 8192m regionNames = { "Customers", "Orders" })
class ServerApplication { .. }
```

The `memorySize` attribute is required. The value for the `memorySize`
attribute specifies the amount of main memory a Region can use in either
megabytes (`m`) or gigabytes (`g`).

The `regionNames` attribute is an array of Region names that specifies
the Regions that store entries in main memory. By default, all Regions
use main memory if the `regionNames` attribute is not explicitly set.

Alternatively, you can use the
<code>spring.data.gemfire.cache.off-heap.memory-size</code> and
<code>spring.data.gemfire.cache.off-heap.region-names</code> properties
in the <code>application.properties</code> file to set and configure the
values of these <code>@EnableOffHeap</code> annotation attributes.

For more details, see the [@EnableOffHeap annotation Javadoc](https://docs.spring.io/spring-data/gemfire/docs/current/api/org/springframework/data/gemfire/config/annotation/EnableOffHeap.html).

### <a id="configuring-disk-stores"></a>Configuring Disk Stores

Alternatively, you can configure Regions to persist data to disk. You
can also configure Regions to overflow data to disk when Region entries
are evicted. In both cases, a `DiskStore` is required to persist and/or
overflow the data. When an explicit `DiskStore` has not been configured
for a Region with persistence or overflow, GemFire uses the
`DEFAULT` `DiskStore`.

We recommend defining Region-specific `DiskStores` when persisting
or overflowing data to disk.

Spring Data for VMware GemFire provides annotation support for defining and creating
application Region `DiskStores` by annotating the application class with
the `@EnableDiskStore` and `@EnableDiskStores` annotations.

<code>@EnableDiskStores</code> is a composite
annotation for aggregating one or more <code>@EnableDiskStore</code>
annotations.

For example, while `Book` information might mostly consist of reference
data from some external data source (such as Amazon), `Order` data is
most likely going to be transactional in nature and something the
application must retain and possibly overflow to disk
if the transaction volume is high enough.

Using the `@EnableDiskStore` annotation, you can define and create a
`DiskStore` as follows:

**Spring application defining a `DiskStore`**

```highlight
@SpringBootApplication
@PeerCacheApplication
@EnableDiskStore(name = "OrdersDiskStore", autoCompact = true, compactionThreshold = 70,
    maxOplogSize = 512, diskDirectories = @DiskDiretory(location = "/absolute/path/to/order/disk/files"))
class ServerApplication { .. }
```

More than one `DiskStore` can be defined by using the composite,
`@EnableDiskStores` annotation.

Both `@EnableDiskStore` and `@EnableDiskStores` have many
attributes along with associated configuration properties to customize
the `DiskStores` created at runtime.

Additionally, the `@EnableDiskStores` annotation defines certain common
`DiskStore` attributes that apply to all `DiskStores` created from
`@EnableDiskStore` annotations composed with the `@EnableDiskStores`
annotation itself. Individual `DiskStore` configuration override a
particular global setting, but the `@EnableDiskStores` annotation
conveniently defines common configuration attributes that apply across
all `DiskStores` aggregated by the annotation.

Spring Data for VMware GemFire also provides the `DiskStoreConfigurer` callback interface,
which can be declared in Java configuration and used instead of
configuration properties to customize a `DiskStore` at runtime, as the
following example shows:

**Spring application with custom DiskStore configuration**

```highlight
@SpringBootApplication
@PeerCacheApplication
@EnableDiskStore(name = "OrdersDiskStore", autoCompact = true, compactionThreshold = 70,
    maxOplogSize = 512, diskDirectories = @DiskDiretory(location = "/absolute/path/to/order/disk/files"))
class ServerApplication {

  @Bean
  DiskStoreConfigurer ordersDiskStoreDiretoryConfigurer(
          @Value("${orders.disk.store.location}") String location) {

      return (beanName, diskStoreFactoryBean) -> {

          if ("OrdersDiskStore".equals(beanName) {
              diskStoreFactoryBean.setDiskDirs(Collections.singletonList(new DiskDir(location));
          }
      }
  }
}
```

For more details about the available attributes as well as associated configuration properties, see:

- [@EnableDiskStore Javadoc](https://docs.spring.io/spring-data/gemfire/docs/current/api/org/springframework/data/gemfire/config/annotation/EnableDiskStore.html)

- [@EnableDiskStores Javadoc](https://docs.spring.io/spring-data/gemfire/docs/current/api/org/springframework/data/gemfire/config/annotation/EnableDiskStores.html)

For more details about Region persistence and overflow, see
[Persistence and Overflow](https://docs.vmware.com/en/VMware-GemFire/9.15/gf/developing-storing_data_on_disk-chapter_overview.html)
in the GemFire product documentation.

### <a id="configuring-indexes"></a>Configuring Indexes

There is not much use in storing data in Regions unless the data can be
accessed.

In addition to `Region.get(key)` operations, particularly when the key
is known in advance, data is commonly retrieved by executing queries on
the Regions that contain the data. With GemFire, queries are
written by using the Object Query Language (OQL), and the specific data
set that a client wishes to access is expressed in the query's predicate
(for example, `SELECT * FROM /Books b WHERE b.author.name = 'Jon Doe'`).

Generally, querying without indexes is inefficient. When executing
queries without an index, GemFire performs the equivalent of a
full table scan.

Indexes are created and maintained for fields on objects used in query
predicates to match the data of interest, as expressed by the query's
projection. Different types of indexes, such as
[key](https://docs.vmware.com/en/VMware-GemFire/9.15/gf/developing-query_index-creating_key_indexes.html)
and [hash](https://docs.vmware.com/en/VMware-GemFire/9.15/gf/developing-query_index-creating_hash_indexes.html)
indexes, can be created.

Spring Data for VMware GemFire makes it easy to create indexes on Regions where the data is
stored and accessed. Rather than explicitly declaring `Index` bean
definitions by using Spring config as before, we can create an `Index`
bean definition in Java, as follows:

**Index bean definition using Java config**

```highlight
@Bean("BooksIsbnIndex")
IndexFactoryBean bookIsbnIndex(GemFireCache gemfireCache) {

    IndexFactoryBean bookIsbnIndex = new IndexFactoryBean();

    bookIsbnIndex.setCache(gemfireCache);
    bookIsbnIndex.setName("BookIsbnIndex");
    bookIsbnIndex.setExpression("isbn");
    bookIsbnIndex.setFrom("/Books"));
    bookIsbnIndex.setType(IndexType.KEY);

    return bookIsbnIndex;
}
```
Alternatively, we can use [XML](bootstrap.html#configuring-an-index) to create an
`Index` bean definition, as follows:

**Index bean definition using XML**

```highlight
<gfe:index id="BooksIsbnIndex" expression="isbn" from="/Books" type="KEY"/>
```

However, now you can directly define indexes on the fields of your
application domain object types for which you know will be used in query
predicates to speed up those queries. You can even apply indexes for OQL
queries generated from user-defined query methods on an application's
repository interfaces.

Re-using the example `Book` entity class from earlier, we can annotate
the fields on `Book` that we know are used in queries that we define
with query methods in the `BookRepository` interface, as follows:

**Application domain object type modeling a book using indexes**

```highlight
@Region("Books")
class Book {

  @Id
  private ISBN isbn;

  @Indexed
  private Author author;

  private Category category;

  private LocalDate releaseDate;

  private Publisher publisher;

  @LuceneIndexed
  private String title;

}
```

In our new `Book` class definition, we annotated the `author` field with
`@Indexed` and the `title` field with `@LuceneIndexed`. Also, the `isbn`
field had previously been annotated with Spring Data's `@Id` annotation,
which identifies the field containing the unique identifier for `Book`
instances, and, in Spring Data for VMware GemFire, the `@Id` annotated field or property is
used as the key in the Region when storing the entry.

- `@Id` annotated fields or properties result in the creation of an
  GemFire `KEY` Index.

- `@Indexed` annotated fields or properties result in the creation of an
  GemFire `HASH` Index (the default).

- `@LuceneIndexed` annotated fields or properties result in the creation
  of an GemFire Lucene Index, used in text-based searches with
  GemFire's Lucene integration and support.

When the `@Indexed` annotation is used without setting any attributes,
the index `name`, `expression`, and `fromClause` are derived from the
field or property of the class on which the `@Indexed` annotation has
been added. The `expression` is exactly the name of the field or
property. The `fromClause` is derived from the `@Region` annotation on
the domain object's class, or the simple name of the domain object class
if the `@Region` annotation was not specified.

You can explicitly set any of the `@Indexed` annotation
attributes to override the default values provided by Spring Data for VMware GemFire.

**Application domain object type modeling a Book with customized indexes**

```highlight
@Region("Books")
class Book {

  @Id
  private ISBN isbn;

  @Indexed(name = "BookAuthorNameIndex", expression = "author.name", type = "FUNCTIONAL")
  private Author author;

  private Category category;

  private LocalDate releaseDate;

  private Publisher publisher;

  @LuceneIndexed(name = "BookTitleIndex", destroy = true)
  private String title;

}
```
The `name` of the index, which is auto-generated when not explicitly
set, is also used as the name of the bean registered in the Spring
container for the index. If necessary, this index bean can even be
injected by name into another application component.



The generated name of the index follows this pattern:
`<Region Name><Field/Property Name><Index Type>Idx`. For example, the
name of the `author` index would be, `BooksAuthorHashIdx`.

To enable indexing, annotate the application class with
`@EnableIndexing`, as follows:

**Spring application with Indexing enabled**

```highlight
@SpringBootApplication
@PeerCacheApplication
@EnableEntityDefinedRegions
@EnableIndexing
class ServerApplication { .. }
```

<p class="note"><strong>Note</strong>: The <code>@EnablingIndexing</code> annotation has no
effect unless the <code>@EnableEntityDefinedRegions</code> is also
declared. Essentially, indexes are defined from fields or properties on
the entity class types, and entity classes must be scanned to inspect
the entity's fields and properties for the presence of index
annotations. Without this scan, index annotations cannot be found. We
also strongly recommend that you limit the scope of the scan.</p>


While Lucene queries are not supported on Spring Data for VMware GemFire repositories,
Spring Data for VMware GemFire does provide comprehensive [support](lucene.html)
for GemFire Lucene queries by using the Spring
template design pattern.

Finally, we close this section with a few extra tips to keep in mind
when using indexes:

- While OQL indexes are not required to execute OQL Queries, Lucene
  Indexes are required to execute Lucene text-based searches.

- OQL indexes are not persisted to disk. They are only maintained in
  memory. So, when an GemFire node is restarted, the index
  must be rebuilt.

- You also need to be aware of the overhead associated in maintaining
  indexes, particularly since an index is stored exclusively in memory
  and especially when Region entries are updated. Index "maintenance"
  can be [configured](https://geode.apache.org/releases/latest/javadoc/org/apache/geode/cache/RegionFactory.html#setIndexMaintenanceSynchronous-boolean-)
  as an asynchronous task.

Another optimization that you can use when restarting your Spring
application where indexes have to be rebuilt is to first define all the
indexes up front and then create them all at once, which, in Spring Data for VMware GemFire,
happens when the Spring container is refreshed.

You can define indexes up front and then create them all at once by
setting the `define` attribute on the `@EnableIndexing` annotation to
`true`.

For more details, see [Creating Multiple Indexes at Once](https://docs.vmware.com/en/VMware-GemFire/9.15/gf/developing-query_index-create_multiple_indexes.html)
in the GemFire product documentation.

Creating sensible indexes is an important task, since it is possible for
a poorly designed index to do more harm than good.

For a complete list of configuration options, see:

* [@Indexed](https://docs.spring.io/spring-data/gemfire/docs/current/api/org/springframework/data/gemfire/mapping/annotation/Indexed.html)
annotation

* [@LuceneIndexed](https://docs.spring.io/spring-data/gemfire/docs/current/api/org/springframework/data/gemfire/mapping/annotation/LuceneIndexed.html)
annotation Javadoc

For more details about GemFire OQL queries, see [Querying](https://docs.vmware.com/en/VMware-GemFire/9.15/gf/developing-querying_basics-chapter_overview.html)
in the GemFire product documentation.

For more details about GemFire indexes, see
see [Working with Indexes](https://docs.vmware.com/en/VMware-GemFire/9.15/gf/developing-query_index-query_index.html)
in the GemFire product documentation.

For more details about GemFire Lucene queries, see [Apache Lucene® Integration](https://docs.vmware.com/en/VMware-GemFire/9.15/gf/tools_modules-lucene_integration.html)
in the GemFire product documentation.

## <a id="configuring-continuous-queries"></a>Configuring Continuous Queries

Another very important and useful feature of GemFire is [Continuous Queries](https://docs.vmware.com/en/VMware-GemFire/9.15/gf/developing-continuous_querying-chapter_overview.html).

In a world of Internet-enabled things, events and streams of data come
from everywhere. Being able to handle and process a large stream of data
and react to events in real time is an increasingly important
requirement for many applications. One example is self-driving vehicles.
Being able to receive, filter, transform, analyze, and act on data in
real time is a key differentiator and characteristic of real time
applications.

Fortunately, GemFire was ahead of its time in this regard. By
using Continuous Queries (CQ), a client application can express the data
or events it is interested in and register listeners to handle and
process the events as they occur. The data that a client application may
be interested in is expressed as an OQL query, where the query predicate
is used to filter or identify the data of interest. When data is changed
or added and it matches the criteria defined in the query predicate of
the registered CQ, the client application is notified.

Spring Data for VMware GemFire makes it easy to define and register CQs, along with an
associated listener to handle and process CQ events without all the
cruft of GemFire's plumbing. Spring Data for VMware GemFire's new
annotation-based configuration for CQs builds on the existing Continuous
Query support in the [continuous query listener container](cq-container.html).

For instance, say a banking application registers interest in every
customers' checking account to detect overdraft withdrawals and handle
this event by either applying overdraft protection or notifying the
customer. Then, the application might register the following CQ:

**Spring `ClientCache` application with registered CQ and listener**

```highlight
@SpringBootApplication
@ClientCacheApplication(subcriptionEnabled = true)
@EnableContinuousQueries
class PublisherPrintApplication {

    @ContinuousQuery(name = "OverdraftProtection", query = "SELECT * FROM /CheckingAccount ca WHERE ca.balance ≤ 0.0")
    void handleOverdraft(CqEvent event) {
        // Quick!!! Put more money into the checking account or notify the customer of the checking account!
    }
}
```

To enable Continuous Queries, annotate your application class with
`@EnableContinuousQueries`.

Defining Continuous Queries consists of annotating any Spring
`@Component`-annotated POJO class methods with the `@ContinuousQuery`
annotation (in similar fashion to Spring Data for VMware GemFire's Function-annotated
POJO methods). A POJO method defined with a CQ by using the
`@ContinuousQuery` annotation is called any time data matching the query
predicate is added or changed.

Additionally, the POJO method signature should adhere to the
requirements outlined in 
[`ContinuousQueryListener` and `ContinuousQueryListenerAdapter`](cq-container.html#continuousquerylistener-and-continuousquerylisteneradapter).

For more details about available attributes and configuration settings, see the following annotation Javadocs:

* [@EnableContinuousQueries](https://docs.spring.io/spring-data/gemfire/docs/current/api/org/springframework/data/gemfire/config/annotation/EnableContinuousQueries.html)

* [@ContinuousQuery](https://docs.spring.io/spring-data/gemfire/docs/current/api/index.html?org/springframework/data/gemfire/config/annotation/EnableContinuousQueries.html)

For more information about continuous query support, see [Continuous Query (CQ)](cq-container.html).

For more information about Continuous Querying in GemFire, see [Continuous Querying](https://docs.vmware.com/en/VMware-GemFire/9.15/gf/developing-continuous_querying-chapter_overview.html)
in the GemFire product documentation.

## <a id="configuring-springs-cache-abstraction"></a>Configuring Spring's Cache Abstraction

With Spring Data for VMware GemFire, GemFire can be used as a caching provider in
[cache abstraction](https://docs.spring.io/spring-framework/docs/current/reference/html/integration.html#cache).

In Cache Abstraction, the caching annotations (such as
`@Cacheable`) identify the cache on which a cache lookup is performed
before invoking a potentially expensive operation. The results of an
application service method are cached after the operation is invoked.

In Spring Data for VMware GemFire, a Spring `Cache` corresponds directly to a
GemFire Region. The Region must exist before any caching
annotated application service methods are called. This is true for any
of Spring's caching annotations (that is, `@Cacheable`, `@CachePut` and
`@CacheEvict`) that identify the cache to use in the service operation.

For example, our publisher's Point-of-Sale (PoS) application might have
a feature to determine or lookup the `Price` of a `Book` during a sales
transaction, as the following example shows:

```highlight
@Service
class PointOfSaleService

  @Cacheable("BookPrices")
  Price runPriceCheckFor(Book book) {
      ...
  }

  @Transactional
  Receipt checkout(Order order) {
      ...
  }

  ...
}
```

To make your work easier when you use Spring Data for VMware GemFire with Spring's Cache
Abstraction, two new features have been added to the annotation-based
configuration model.

Consider the following Spring caching configuration:

**Enabling Caching using GemFire as the caching provider**

```highlight
@EnableCaching
class CachingConfiguration {

  @Bean
  GemfireCacheManager cacheManager(GemFireCache gemfireCache) {

      GemfireCacheManager cacheManager = new GemfireCacheManager();

      cacheManager.setCache(gemfireCache);

      return cacheManager;
  }

  @Bean("BookPricesCache")
  ReplicatedRegionFactoryBean<Book, Price> bookPricesRegion(GemFireCache gemfireCache) {

    ReplicatedRegionFactoryBean<Book, Price> bookPricesRegion =
        new ReplicatedRegionFactoryBean<>();

    bookPricesRegion.setCache(gemfireCache);
    bookPricesRegion.setClose(false);
    bookPricesRegion.setPersistent(false);

    return bookPricesRegion;
  }

  @Bean("PointOfSaleService")
  PointOfSaleService pointOfSaleService(..) {
      return new PointOfSaleService(..);
  }
}
```

Using Spring Data for VMware GemFire's features, you can simplify the same caching
configuration to the following:

**Enabling GemFire Caching**

```highlight
@EnableGemfireCaching
@EnableCachingDefinedRegions
class CachingConfiguration {

  @Bean("PointOfSaleService")
  PointOfSaleService pointOfSaleService(..) {
      return new PointOfSaleService(..);
  }
}
```

First, the `@EnableGemfireCaching` annotation replaces both the Spring
`@EnableCaching` annotation and the need to declare an explicit
`CacheManager` bean definition (named "cacheManager") in the Spring
config.

Second, the `@EnableCachingDefinedRegions` annotation, like the
`@EnableEntityDefinedRegions` annotation described in [Configuring Regions](#configuring-regions), inspects the entire
Spring application, caching annotated service components to identify all
the caches that are needed by the application at runtime and creates
Regions in GemFire for these caches on application startup.

The Regions created are local to the application process that created
the Regions. If the application is a peer `Cache`, the Regions exist
only on the application node. If the application is a `ClientCache`,
then Spring Data for VMware GemFire creates client `PROXY` Regions and expects those
Regions with the same name to already exist on the servers in the
cluster.

<p class="note"><strong>Note</strong>: 
Spring Data for VMware GemFire cannot determine the cache required by
a service method using a Spring <code>CacheResolver</code> to resolve
the cache used in the operation at runtime.</p>

<p class="note"><strong>Tip</strong>: <td class="content">Spring Data for VMware GemFire also supports JCache (JSR-107) cache
annotations on application service components. For the equivalent Spring caching
annotation to use in place of JCache caching annotations,
see <a href="https://docs.spring.io/spring-data/geode/docs/current/reference/html/#bootstrap-annotation-config-configurers">JCache (JSR-107) Annotations</a>
in the core
<em>Spring Framework Reference Guide</em>.</p>



For more details about using GemFire as a caching provider in Spring's Cache
Abstraction, see [Support for the Spring Cache Abstraction](data.html#support-for-spring-cache-abstraction)
in _Working with GemFire APIs_. 

For more information about Spring's Cache Abstraction, see
[Cache Abstraction](https://docs.spring.io/spring-framework/docs/current/reference/html/integration.html#cache).

## <a id="configuring-cluster-configuration-push"></a>Configuring Cluster Configuration Push

When a client application class is annotated with
`@EnableClusterConfiguration`, any Regions or Indexes defined and
declared as beans in the Spring Container by the client application are
"pushed" to the cluster of servers to which the client is connected. Not
only that, but this "push" is performed in such a way that
GemFire remembers the configuration pushed by the client when
using HTTP. If all the nodes in the cluster go down, they come back up
with the same configuration as before. If a new server is added to the
cluster, it will acquire identical configuration.

In a sense, this feature is not much different than if you were to use
`gfsh` to manually create the Regions and Indexes on all the servers in
the cluster. Except that now, with Spring Data for VMware GemFire, you no longer need to use
`gfsh` to create Regions and Indexes. Your Spring Boot application,
enabled with the power of Spring Data for VMware GemFire, already contains all the
configuration metadata needed to create Regions and Indexes for you.

When you use the Spring Data Repository abstraction, we know all the
Regions (such as those defined by the `@Region` annotated entity
classes) and Indexes (such as those defined by the `@Indexed`-annotated
entity fields and properties) that your application will need.

When you use Spring's Cache Abstraction, we also know all the Regions
for all the caches identified in the caching annotations needed by your
application's service components.

Essentially, you are already telling us everything we need to know
simply by developing your application with the Spring Framework simply
by using all of its API and features, whether expressed in annotation
metadata, Java, XML or otherwise, and whether for configuration,
mapping, or whatever the purpose.

The point is, you can focus on your application's business logic while
using the framework's features and supporting infrastructure (such as
Spring's Cache Abstraction, Spring Data Repositories, Spring's
Transaction Management, and so on) and Spring Data for VMware GemFire takes care of all the
GemFire plumbing required by those framework features on your
behalf.



Pushing configuration from the client to the servers in the cluster and
having the cluster remember it is made possible in part by the use of
GemFire's [Cluster Configuration](https://docs.vmware.com/en/VMware-GemFire/9.15/gf/configuring-cluster_config-gfsh_persist.html)
service. GemFire's Cluster Configuration service is the same service used by `gfsh` to record schema-related issued by the
user to the cluster from the shell.

Since the cluster may "remember" the prior configuration
pushed by a client from a previous run, Spring Data for VMware GemFire is careful not to
stomp on any existing Regions and Indexes already defined in the
servers. This is especially important, for instance, when Regions
already contain data.


<p class="note"><strong>Note</strong>: There is no option to overwrite any
existing Region or Index definitions. To re-create a Region or Index,
you must use <code>gfsh</code> to first destroy the Region or Index and then
restart the client application so that configuration is pushed up to the
server again. Alternatively, you can use <code>gfsh</code> to redefine
the Regions and Indexes manually.</p>

Unlike `gfsh`, Spring Data for VMware GemFire supports the
creation of Regions and Indexes only on the servers from a client. For
advanced configuration and use cases, you should use `gfsh` to
manage the server-side cluster.</p>

<p class="note"><strong>Note</strong>: To use this feature you must explicitly declare the
<code>org.springframework:spring-web</code> dependency on the classpath
of your Spring, GemFire <code>ClientCache</code> application.</p>

Consider the power expressed in the following configuration:

**Spring `ClientCache` application**

```highlight
@SpringBootApplication
@ClientCacheApplication
@EnableCachingDefinedRegions
@EnableEntityDefinedRegions
@EnableIndexing
@EnableGemfireCaching
@EnableGemfireRepositories
@EnableClusterConfiguration
class ClientApplication { .. }
```

You instantly get a Spring Boot application with a GemFire
`ClientCache` instance, Spring Data Repositories, Spring's Cache
Abstraction with GemFire as the caching provider (where
Regions and Indexes are not only created on the client but pushed to the
servers in the cluster).

From there, do the following:

- Define the application's domain model objects annotated with mapping
  and index annotations.

- Define Repository interfaces to support basic data access operations
  and simple queries for each of your entity types.

- Define the service components containing the business logic
  transacting the entities.

- Declare the appropriate annotations on service methods that require
  caching, transactional behavior, and so on.

Nothing in this case pertains to the infrastructure and plumbing
required in the application's back-end services (such as
GemFire). Database users have similar features. Now Spring and
GemFire developers do too.

When combined with the following Spring Data for VMware GemFire annotations, this
application really starts to take flight, with very little effort:

- `@EnableContinuousQueries`

- `@EnableGemfireFunctionExecutions`

- `@EnableGemfireCacheTransactions`

For more details, see the [@EnableClusterConfiguration annotation Javadoc](https://docs.spring.io/spring-data/gemfire/docs/current/api/index.html?org/springframework/data/gemfire/config/annotation/EnableClusterConfiguration.html).

## <a id="configiuring-ssl"></a>Configuring SSL

Equally important to serializing data to be transferred over the wire is
securing the data while in transit. Of course, the common way to
accomplish this in Java is by using the Secure Sockets Extension (SSE)
and Transport Layer Security (TLS).

To enable SSL, annotate your application class with `@EnableSsl`, as
follows:

**Spring `ClientCache` application with SSL enabled**

```highlight
@SpringBootApplication
@ClientCacheApplication
@EnableSsl
public class ClientApplication { .. }
```

Then you need to set the necessary SSL configuration attributes or
properties: keystores, usernames/passwords, and so on.

You can individually configure different GemFire components
(`GATEWAY`, `HTTP`, `JMX`, `LOCATOR`, and `SERVER`) with SSL, or you can
collectively configure them to use SSL by using the `CLUSTER` enumerated
value.

You can specify which GemFire components the SSL configuration
settings should applied by using the nested `@EnableSsl` annotation,
`components` attribute with enumerated values from the `Component` enum,
as follows:

**Spring `ClientCache` application with SSL enabled by component**

```highlight
@SpringBootApplication
@ClientCacheApplication
@EnableSsl(components = { GATEWAY, LOCATOR, SERVER })
public class ClientApplication { .. }
```

Additionally, you can specify component-level SSL configuration
(`ciphers`, `protocols` and `keystore`/`truststore` information) by
using the corresponding annotation attribute or associated configuration
properties.

For more details, see the [@EnableSsl annotation Javadoc](https://docs.spring.io/spring-data/gemfire/docs/current/api/org/springframework/data/gemfire/config/annotation/EnableSsl.html).

For more details about GemFire SSL support, see [SSL](https://docs.vmware.com/en/VMware-GemFire/9.15/gf/managing-security-ssl_overview.html).

## <a id="configuring-security"></a>Configuring Security

Application security is important, and
Spring Data for VMware GemFire provides comprehensive support for securing both
GemFire clients and servers.

GemFire includes an [Integrated Security](https://docs.vmware.com/en/VMware-GemFire/9.15/gf/managing-security-implementing_security.html)
framework for handling authentication and authorization. One of
the main features and benefits of this new security framework is that it
integrates with [Apache Shiro](https://shiro.apache.org/) and can
therefore delegate both authentication and authorization requests to
Apache Shiro to enforce security.

The remainder of this section demonstrates how Spring Data for VMware GemFire can simplify
GemFire's security story even further.

### <a id="configuring-server-security"></a>Configuring Server Security

There are several different ways in which you can configure security for
servers in a GemFire cluster.

- Implement the GemFire
  `org.apache.geode.security.SecurityManager` interface and set
  GemFire's `security-manager` property to refer to your
  application `SecurityManager` implementation using the fully qualified
  class name. Alternatively, users can construct and initialize an
  instance of their `SecurityManager` implementation and set it with the
  [CacheFactory.setSecurityManager(:SecurityManager)](https://geode.apache.org/releases/latest/javadoc/org/apache/geode/cache/CacheFactory.html#setSecurityManager-org.apache.geode.security.SecurityManager)
  method when creating a GemFire peer `Cache`.

- Create an Apache Shiro
  [shiro.ini](https://shiro.apache.org/configuration.html) file with
  the users, roles, and permissions defined for your application and
  then set the GemFire `security-shiro-init` property to refer
  to this `shiro.ini` file, which must be available in the `CLASSPATH`.

- Using only Apache Shiro, annotate your Spring Boot application class
  with Spring Data for VMware GemFire's new `@EnableSecurity` annotation and define one or
  more Apache Shiro [Realms](https://shiro.apache.org/realm.html) as
  beans in the Spring container for accessing your application's
  security metadata (that is, authorized users, roles, and permissions).

The problem with the first approach is that you must implement your own
`SecurityManager`, which can be quite tedious and error-prone.
Implementing a custom `SecurityManager` offers some flexibility in
accessing security metadata from whatever data source stores the
metadata, such as LDAP or even a proprietary, internal data source.
However, that problem has already been solved by configuring and using
Apache Shiro `Realms`, which is more universally known and
non-GemFire-specific.

The second approach, using an Apache Shiro INI file, is marginally
better, but you still need to be familiar with the INI file format in
the first place. Additionally, an INI file is static and not easily
updatable at runtime.

The third approach is the most ideal, since it adheres to widely known
and industry-accepted concepts (that is, Apache Shiro's Security
framework) and is easy to setup, as the following example shows:

**Spring server application using Apache Shiro**

```highlight
@SpringBootApplication
@CacheServerApplication
@EnableSecurity
class ServerApplication {

  @Bean
  PropertiesRealm shiroRealm() {

      PropertiesRealm propertiesRealm = new PropertiesRealm();

      propertiesRealm.setResourcePath("classpath:shiro.properties");
      propertiesRealm.setPermissionResolver(new GemFirePermissionResolver());

      return propertiesRealm;
  }
}
```

The configured `Realm` shown in the preceding example could have been any of Apache Shiro's supported
`Realms`:

- [ActiveDirectory](https://shiro.apache.org/static/1.3.2/apidocs/org/apache/shiro/realm/activedirectory/package-frame.html)

- [JDBC](https://shiro.apache.org/static/1.3.2/apidocs/org/apache/shiro/realm/jdbc/package-frame.html)

- [JNDI](https://shiro.apache.org/static/1.3.2/apidocs/org/apache/shiro/realm/jndi/package-frame.html)

- [LDAP](https://shiro.apache.org/static/1.3.2/apidocs/org/apache/shiro/realm/ldap/package-frame.html)

- A `Realm` supporting the [INI format](https://shiro.apache.org/static/1.3.2/apidocs/org/apache/shiro/realm/text/IniRealm.html).

You could also create a custom implementation of an Apache Shiro `Realm`.

For more information, see [Apache Shiro Realms](https://shiro.apache.org/realm.html) in the Apache Shiro documentation.

When Apache Shiro is on the `CLASSPATH` of the servers in the cluster
and one or more Apache Shiro `Realms` have been defined as beans in the
Spring container, Spring Data for VMware GemFire detects this configuration and uses Apache
Shiro as the security provider to secure your GemFire servers
when the `@EnableSecurity` annotation is used.

For more details about available attributes and associated configuration properties, see
the [@EnableSecurity](https://docs.spring.io/spring-data/gemfire/docs/current/api/index.html?org/springframework/data/gemfire/config/annotation/EnableSecurity.html)
annotation Javadoc.

For more details about GemFire security, see [Security](https://docs.vmware.com/en/VMware-GemFire/9.15/gf/managing-security-chapter_overview.html)
in the GemFire product documentation.

### <a id="configuring-client-security"></a>Configuring Client Security

The security story would not be complete without discussing how to
secure Spring-based, GemFire cache client applications as
well.

To secure a client application you must:

1. Provide an implementation of the [org.apache.geode.security.AuthInitialize](https://geode.apache.org/releases/latest/javadoc/org/apache/geode/security/AuthInitialize.html) interface.

2. Set the GemFire `security-client-auth-init` (System) property to refer to the custom, application-provided `AuthInitialize` interface.

3. Specify the user credentials in a proprietary, GemFire `gfsecurity.properties` file.

Spring Data for VMware GemFire simplifies all of those steps by using the same
`@EnableSecurity` annotation that was used in the server applications.
In other words, the same `@EnableSecurity` annotation handles security
for both client and server applications. This feature makes it easier
for users when they decide to switch their applications from an
embedded, peer `Cache` application to a `ClientCache` application, for
instance. Simply change the Spring Data for VMware GemFire annotation from
`@PeerCacheApplication` or `@CacheServerApplication` to
`@ClientCacheApplication`, and you are done.

Effectively, all you need to do on the client is the following:

**Spring client application using `@EnableSecurity`**

```highlight
@SpringBootApplication
@ClientCacheApplication
@EnableSecurity
class ClientApplication { .. }
```

Then you can define the familiar Spring Boot `application.properties`
file containing the required username and password, as the following
example shows.

**Spring Boot `application.properties` file with the required Security credentials**

```highlight
spring.data.gemfire.security.username=jackBlack
spring.data.gemfire.security.password=b@cK!nB1@cK
```

By default, Spring Boot can find your
`code>application.properties` file when it is placed in the root
of the application's `CLASSPATH`. Spring supports
many ways to locate resources by using its resource abstraction.

For more details about available attributes and associated configuration properties, see the
[@EnableSecurity](https://docs.spring.io/spring-data/gemfire/docs/current/api/index.html?org/springframework/data/gemfire/config/annotation/EnableSecurity.html)
annotation Javadoc.

For more details about GemFire security, see [Security](https://docs.vmware.com/en/VMware-GemFire/9.15/gf/managing-security-chapter_overview.html)
in the GemFire product documentation.

## <a id="configuration-tips"></a>Configuration Tips

The following tips can help you get the most out of using the new
annotation-based configuration model:

- [Configuration Organization](#configuration-organization)

- [Additional Configuration-based Annotations](#additional-configuration-based-annotations)

### <a id="configuration-organization"></a>Configuration Organization

As stated in [Configuring Cluster Configuration Push](#configuring-cluster-configuration-push), when many
GemFire or Spring Data for VMware GemFire features are enabled by using
annotations, we begin to stack a lot of annotations on the Spring
`@Configuration` or `@SpringBootApplication` class. In this situation,
it makes sense to start compartmentalizing the configuration a bit.

For instance, consider the following declaration:

**Spring `ClientCache` application with the kitchen sink**

```highlight
@SpringBootApplication
@ClientCacheApplication
@EnableContinuousQueries
@EnableCachingDefinedRegions
@EnableEntityDefinedRegions
@EnableIndexing
@EnableGemfireCacheTransactions
@EnableGemfireCaching
@EnableGemfireFunctionExecutions
@EnableGemfireRepositories
@EnableClusterConfiguration
class ClientApplication { .. }
```

We could break this configuration down by concern, as follows:

**Spring `ClientCache` application with the kitchen sink to boot**

```highlight
@SpringBootApplication
@Import({ GemFireConfiguration.class, CachingConfiguration.class,
    FunctionsConfiguration.class, QueriesConfiguration.class,
    RepositoriesConfiguration.class })
class ClientApplication { .. }

@ClientCacheApplication
@EnableClusterConfiguration
@EnableGemfireCacheTransactions
class GemFireConfiguration { .. }

@EnableGemfireCaching
@EnableCachingDefinedRegions
class CachingConfiguration { .. }

@EnableGemfireFunctionExecutions
class FunctionsConfiguration { .. }

@EnableContinuousQueries
class QueriesConfiguration {

   @ContinuousQuery(..)
   void processCqEvent(CqEvent event) {
       ...
   }
}

@EnableEntityDefinedRegions
@EnableGemfireRepositories
@EnableIndexing
class RepositoriesConfiguration { .. }
```

### <a id="additional-configuration-based-annotations"></a>Additional Configuration-based Annotations

The following Spring Data for VMware GemFire Annotations were not discussed in this
reference documentation, either because the annotation supports a
deprecated feature of GemFire or because there are better,
alternative ways to accomplishing the function that the annotation
provides:

- `@EnableAuth`: Enables GemFire's old authentication and
  authorization security model. (Deprecated. GemFire's new
  integrated security framework can be enabled on both clients and
  servers by using Spring Data for VMware GemFire's `@EnableSecurity` annotation, as
  described in [Configuring Security](#configuring-security)".)

- `@EnableAutoRegionLookup`: Not recommended. Essentially, this
  annotation supports finding Regions defined in external configuration
  metadata (such as `cache.xml` or Cluster Configuration when applied to
  a server) and automatically registers those Regions as beans in the
  Spring container. This annotation corresponds with the
  `<gfe:auto-region-lookup>` element in Spring Data for VMware GemFire's XML namespace.
  For more details, see [Auto Region Lookup](#auto-region-lookup) in _Configuring a Region_.
  Users should generally prefer Spring configuration when using Spring and
  Spring Data for VMware GemFire. See "[Configuring Regions](#configuring-regions) and
  [Configuring Cluster Configuration Push](#configuring-cluster-configuration-push) instead.

- `@EnableBeanFactoryLocator`: Enables the Spring Data for VMware GemFire
  `GemfireBeanFactoryLocator` feature, which is only useful when using
  external configuration metadata (for example, `cache.xml`). For
  example, if you define a `CacheLoader` on a Region defined in
  `cache.xml`, you can still autowire this `CacheLoader` with, say, a
  relational database `DataSource` bean defined in Spring configuration.
  This annotation takes advantage of this Spring Data for VMware GemFire
  [declarable feature](data.html#wiring-declarable-components)
  and might be useful if you have a large
  amount of legacy configuration metadata, such as `cache.xml` files.

- `@EnableGemFireAsLastResource`: Discussed in [Global - JTA Transaction
  Management](data.html#global-jta-transaction-management) with
  GemFire.

- `@EnableMcast`: Enables GemFire's old peer discovery
  mechanism that uses UDP-based multi-cast networking. (*Deprecated*.
  Use GemFire Locators instead. See [Configuring an Embedded Locator](#configuring-an-embedded-locator).

- `@EnableRegionDataAccessTracing`: Useful for debugging purposes. This
  annotation enables tracing for all data access operations performed on
  a Region by registering an AOP Aspect that proxies all Regions
  declared as beans in the Spring container, intercepting the Region
  operation and logging the event.

## <a id="conclusion"></a>Conclusion

As we learned in the previous sections, Spring Data for VMware GemFire's new
annotation-based configuration model provides a tremendous amount of
power. Hopefully, it lives up to its goal of making it easier for you to
*get started quickly* and *easily* when using GemFire with
Spring.

When you use the new annotations, you can still use
Java configuration or XML configuration. You can even combine all three
approaches by using Spring's
[@Import](https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/context/annotation/Import.html)
and [@ImportResource](https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/context/annotation/ImportResource.html)
annotations on a Spring `@Configuration` or `@SpringBootApplication`
class. The moment you explicitly provide a bean definition that would
otherwise be provided by Spring Data for VMware GemFire using one of the annotations, the
annotation-based configuration is disabled.

Note
</div></td>
<td class="content"><p>In certain cases, you may even need to fall back to Java
configuration, as in the <code>Configurers</code> case, to handle more
complex or conditional configuration logic that is not easily expressed
in or cannot be accomplished by using annotations alone. Do not be
alarmed. This behavior is to be expected.</p>
<p>For example, another case where you need Java or XML configuration is
when configuring GemFire WAN components, which currently do
not have any annotation configuration support. However, defining and
registering WAN components requires only using the
<code>org.springframework.data.gemfire.wan.GatewayReceiverFactoryBean</code>
and
<code>org.springframework.data.gemfire.wan.GatewaySenderFactoryBean</code>
API classes in the Java configuration of your Spring
<code>@Configuration</code> or <code>@SpringBootApplication</code>
classes (recommended).</p>
</div></td>
</tr>
</tbody>
</table>

The annotations were not meant to handle every situation. The
annotations were meant to help you *get up and running* as *quickly* and
as *easily* as possible, especially during development.

