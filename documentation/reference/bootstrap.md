---
title: Bootstrapping GemFire with the Spring Container
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

Spring Data for VMware GemFire provides full configuration and initialization of the
GemFire In-Memory Data Grid (IMDG) using the Spring Inversion of Control (IoC)
container. The framework includes several classes to help simplify the
configuration of GemFire components, including: Caches,
Regions, Indexes, DiskStores, Functions, WAN Gateways, persistence
backup, and several other Distributed System components to support a
variety of application use cases with minimal effort.

This topic assumes that you have basic familiarity with GemFire. For more information
about GemFire, see the [GemFire product documentation](https://docs.vmware.com/en/VMware-Tanzu-GemFire/9.15/tgf/GUID-about_gemfire.html).

## <a id="advantages"></a>Advantages of using Spring over GemFire `cache.xml`

Spring Data for VMware GemFire's XML namespace supports full configuration of the
GemFire In-Memory Data Grid (IMDG). The XML namespace is one
of two ways to configure GemFire in a Spring context
to properly manage GemFire's lifecycle inside the Spring
container. The other way to configure GemFire in a Spring
context is by using [annotation-based
configuration](#bootstrap-annotation-config).

While support for GemFire's native `cache.xml` persists for
legacy reasons, GemFire application developers who use XML
configuration are encouraged use Spring XML to take
advantage of the many features that Spring has to offer, such as
modular XML configuration, property placeholders and overrides, SpEL
([Spring Expression
Language](https://docs.spring.io/spring-framework/docs/current/reference/html/core.html#expressions)), and environment profiles. Behind the XML namespace,
Spring Data for VMware GemFire makes extensive use of Spring's `FactoryBean` pattern to
simplify the creation, configuration, and initialization of
GemFire components.

GemFire provides several callback interfaces, such as
`CacheListener`, `CacheLoader`, and `CacheWriter`, that let developers
add custom event handlers. Using Spring's Inversion of Control (IoC) container, you can
configure these callbacks as normal Spring beans and inject them into
GemFire components. This is a significant improvement over
native `cache.xml`, which provides relatively limited configuration
options and requires callbacks to implement GemFire's
`Declarable` interface. For information about continuing to use `Declarables` within Spring's container, see [Wiring `Declarable` Components](data.html#wiring-declarable-components)
in _Working with GemFire APIs_.

In addition, IDEs, such as the Spring Tool Suite (STS), provide
support for Spring XML namespaces, including code completion,
pop-up annotations, and real time validation.

## <a id="using-core-namespace"></a>Using the Core Namespace

To simplify configuration, Spring Data for VMware GemFire provides a dedicated XML namespace
for configuring core GemFire components. You can
configure beans directly by using Spring's standard `<bean>` definition.
All bean properties are exposed through the XML namespace, which minimizes the need to use
raw bean definitions.

For more information about XML Schema-based configuration in Spring, see the
[Appendix](https://docs.spring.io/spring-framework/docs/current/reference/html/core.html#appendix) in the Spring Framework reference documentation.

<p class="note"><strong>Note</strong>: Spring Data Repository support uses a separate XML
namespace. For more information about configuring Spring Data for VMware GemFire Repositories, see
<a href="repositories.html">Spring Data for VMware GemFire Repositories.</a></p>

To use the Spring Data for VMware GemFire XML namespace, declare it in your Spring XML
configuration meta-data, as the following example shows:

```highlight
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:gfe="https://www.springframework.org/schema/geode" <!--SEE COMMENT 1--><!--SEE COMMENT 2-->
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xsi:schemaLocation="
    http://www.springframework.org/schema/beans https://www.springframework.org/schema/beans/spring-beans.xsd
    https://www.springframework.org/schema/geode https://www.springframework.org/schema/geode/spring-geode.xsd <!--SEE COMMENT 3-->
">
  
  <bean id ... >
  
  <gfe:cache ...> <!--SEE COMMENT 4-->
  
</beans>
```

Comments:

1. Spring Data for VMware GemFire XML namespace prefix. This reference documentation uses `gfe`.

2. The XML namespace prefix is mapped to the URI.

3. The XML namespace URI location. Although the location in this example points to an external valid address, Spring resolves the schema locally because the address is included in the Spring Data for VMware GemFire library.

4. Example declaration using the XML namespace with the `gfe` prefix.

**Changing the default namespace**

You can change the default namespace from `beans` to `gfe`. Change the default namespace to
`gfe` with XML configurations composed mainly of GemFire components to avoid declaring the prefix.

To make this change, replace the namespace prefix declaration shown earlier, as the
following example shows:</p>

```highlight
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="https://www.springframework.org/schema/geode" <!--SEE COMMENT 1 -->
       xmlns:beans="http://www.springframework.org/schema/beans" <!--SEE COMMENT 2-->
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xsi:schemaLocation="
    http://www.springframework.org/schema/beans https://www.springframework.org/schema/beans/spring-beans.xsd
    https://www.springframework.org/schema/geode https://www.springframework.org/schema/geode/spring-geode.xsd
">
  
  <beans:bean id ... > <!--SEE COMMENT 3-->
  
  <cache ...> <!--SEE COMMENT 4-->
  
</beans>
```

Comments:

1. The default namespace declaration for this XML document points to
the Spring Data for VMware GemFire XML namespace.

2. The `beans` namespace prefix declaration for Spring's
raw bean definitions.

3. Bean declaration using the `beans` namespace. Observe the prefix.

4. Bean declaration using the `gfe` namespace. Observe the lack of prefix because `gfe`
was set as the default namespace.

## <a id="using-data-accesscore-namespace"></a>Using the Data Access Namespace

In addition to the core XML namespace (`gfe`), Spring Data for VMware GemFire provides a
data access XML namespace (`gfe-data`), which is primarily intended to
simplify the development of GemFire client applications. This
namespace currently contains support for [GemFire
Repositories](repositories.html) and [Function
execution](function-annotations.html), as well as a `<datasource>` tag that
offers a convenient way to connect to a GemFire cluster.

### <a id="connect-to-gemfire"></a>An Easy Way to Connect to GemFire

For many applications, a basic connection to a GemFire data
grid using default values is sufficient. Spring Data for VMware GemFire's `<datasource>` tag
provides a simple way to access data. The data source creates a
`ClientCache` and connection `Pool`. In addition, it queries the cluster
servers for all existing root Regions and creates an empty client
Region proxy for each one.

```highlight
<gfe-data:datasource>
  <locator host="remotehost" port="1234"/>
</gfe-data:datasource>
```

The `<datasource>` tag is syntactically similar to `<gfe:pool>`. It may
be configured with one or more nested `locator` or `server` elements to
connect to an existing data grid. Additionally, all attributes available
to configure a Pool are supported. This configuration automatically
creates client Region beans for each Region defined on cluster members
connected to the Locator, so that they can be seamlessly referenced by Spring
Data mapping annotations (`GemfireTemplate`) and autowired into
application classes.

You can also explicitly configure client Regions. For example, if
you want to cache data in local memory, as the following example shows:

```highlight
<gfe-data:datasource>
  <locator host="remotehost" port="1234"/>
</gfe-data:datasource>

<gfe:client-region id="Example" shortcut="CACHING_PROXY"/>
```

## <a id="configuring-cache"></a>Configuring a Cache

To use GemFire, you must either create a new cache or
connect to an existing one. With the current version of
GemFire, you can have only one open cache per VM (more
strictly speaking, per `ClassLoader`). In most cases, the cache should
only be created once.

<p class="note"><strong>Note</strong>: This section of this topic describes the creation and
configuration of a peer <code>Cache</code> member, appropriate in
peer-to-peer (P2P) topologies and cache servers. A <code>Cache</code>
member can also be used in stand-alone applications and integration
tests. However, in typical production systems, most application
processes act as cache clients, creating a <code>ClientCache</code>
instance instead. This is described in the <a href="#configuring-gemfire-clientcache">Configuring a GemFire ClientCache</a> and <a href="#client-region">Client Region</a>
sections.</p>

You can create a peer `Cache` with default configuration with the
following declaration:

```highlight
<gfe:cache/>
```

During Spring container initialization, any `ApplicationContext`
containing this cache definition registers a `CacheFactoryBean` that
creates a Spring bean named `gemfireCache`, which references a
GemFire `Cache` instance. This bean refers to either an
existing `Cache` or, if one does not already exist, a newly created one.
Since no additional properties were specified, a newly created `Cache`
applies the default cache configuration.

All Spring Data for VMware GemFire components that depend on the `Cache` respect this naming
convention, so you need not explicitly declare the `Cache` dependency.
If you prefer, you can make the dependency explicit by using the
`cache-ref` attribute provided by various Spring Data for VMware GemFire XML namespace
elements. Also, you can override the cache's bean name using the `id`
attribute, as follows:

```highlight
<gfe:cache id="myCache"/>
```

A GemFire `Cache` can be fully configured using Spring.
However, GemFire's native XML configuration file, `cache.xml`,
is also supported. For situations where the GemFire cache
must be configured natively, you can provide a reference to the
GemFire XML configuration file by using the
`cache-xml-location` attribute, as follows:

```highlight
<gfe:cache id="cacheConfiguredWithNativeCacheXml" cache-xml-location="classpath:cache.xml"/>
```

In this example, if a cache must be created, it uses a file named
`cache.xml` located in the classpath root to configure it.

<p class="note"><strong>Note</strong>: The configuration makes use of Spring's <a
href="https://docs.spring.io/spring/docs/current/spring-framework-reference/htmlsingle/#resources"><code>Resource</code></a>
abstraction to locate the file. The <code>Resource</code> abstraction
lets various search patterns be used, depending on the runtime
environment or the prefix specified (if any) in the resource
location.</p>

In addition to referencing an external XML configuration file, you can
also specify GemFire System [properties](https://docs.vmware.com/en/VMware-Tanzu-GemFire/9.15/tgf/GUID-reference-topics-gemfire_properties.html)
that use any of Spring's `Properties` support features.

For example, you can use the `properties` element defined in the `util`
namespace to define `Properties` directly or load properties from a
properties file, as follows:

```highlight
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:gfe="https://www.springframework.org/schema/geode"
       xmlns:util="http://www.springframework.org/schema/util"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xsi:schemaLocation="
    http://www.springframework.org/schema/beans https://www.springframework.org/schema/beans/spring-beans.xsd
    https://www.springframework.org/schema/geode https://www.springframework.org/schema/geode/spring-geode.xsd
    http://www.springframework.org/schema/util https://www.springframework.org/schema/util/spring-util.xsd
">
  
  <util:properties id="gemfireProperties" location="file:/path/to/gemfire.properties"/>
  
  <gfe:cache properties-ref="gemfireProperties"/>
  
</beans>
```

Using a properties file is recommended for externalizing
environment-specific settings outside the application configuration.

<p class="note"><strong>Note</strong>: Cache settings apply only when a new cache needs to
be created. If an open cache already exists in the VM, these settings
are ignored.</p>

### <a id="advanced-cache-configuration"></a>Advanced Cache Configuration

For advanced cache configuration, the `cache` element provides a number
of configuration options exposed as attributes or child elements, as the
following listing shows:

```highlight
<!--SEE COMMENT 1-->
<gfe:cache
    cache-xml-location=".."
    properties-ref=".."
    close="false"
    copy-on-read="true"
    critical-heap-percentage="90"
    eviction-heap-percentage="70"
    enable-auto-reconnect="false" <!--SEE COMMENT 2-->
    lock-lease="120"
    lock-timeout="60"
    message-sync-interval="1"
    pdx-serializer-ref="myPdxSerializer"
    pdx-persistent="true"
    pdx-disk-store="diskStore"
    pdx-read-serialized="false"
    pdx-ignore-unread-fields="true"
    search-timeout="300"
    use-bean-factory-locator="true" <!--SEE COMMENT 3-->
    use-cluster-configuration="false" <!--SEE COMMENT 4-->
>
  
  <gfe:transaction-listener ref="myTransactionListener"/> <!--SEE COMMENT 5-->
  
  <gfe:transaction-writer> <!--SEE COMMENT 6-->
    <bean class="org.example.app.gemfire.transaction.TransactionWriter"/>
  </gfe:transaction-writer>
  
  <gfe:gateway-conflict-resolver ref="myGatewayConflictResolver"/> <!--SEE COMMENT 7-->
  
  <gfe:jndi-binding jndi-name="myDataSource" type="ManagedDataSource"/> <!--SEE COMMENT 8-->
  
</gfe:cache>
```

Comments:
1. Attributes support various cache options. For further information
    regarding anything shown in this example, see the [GemFire
    product documentation](https://docs.vmware.com/en/VMware-Tanzu-GemFire/). The
    `close` attribute determines whether the cache should be closed when
    the Spring application context is closed. The default is `true`.
    However, for use cases in which multiple application contexts use
    the cache (common in web applications), set this value to `false`.

2. Setting the `enable-auto-reconnect` attribute to `true` (the default
    is `false`) lets a disconnected GemFire member
    automatically reconnect and rejoin the GemFire cluster.
    For more details, see [Handling Forced Cache Disconnection Using Autoreconnect](https://docs.vmware.com/en/VMware-Tanzu-GemFire/9.15/tgf/GUID-managing-member-reconnect.html) in the GemFire product documentation.

3. Setting the `use-bean-factory-locator` attribute to `true` (it
    defaults to `false`) applies only when both Spring (XML)
    configuration metadata and GemFire `cache.xml` is used to
    configure the GemFire cache node (whether client or peer).
    This option lets GemFire components (such as
    `CacheLoader`) expressed in `cache.xml` be auto-wired with beans
    (such as `DataSource`) defined in the Spring application context.
    This option is typically used in conjunction with
    `cache-xml-location`.

4. Setting the `use-cluster-configuration` attribute to `true` (the
    default is `false`) enables a GemFire member to retrieve
    the common, shared Cluster-based configuration from a Locator. For more details, see
    [Overview of the Cluster Configuration Service](https://docs.vmware.com/en/VMware-Tanzu-GemFire/9.15/tgf/GUID-configuring-cluster_config-gfsh_persist.html) in the GemFire product documentation.

5. Example of a `TransactionListener` callback declaration that uses a
    bean reference. The referenced bean must implement
    [TransactionListener](https://geode.apache.org/releases/latest/javadoc/org/apache/geode/cache/TransactionListener.html).
    A `TransactionListener` can be implemented to handle transaction
    related events, such as afterCommit and afterRollback.

6. Example of a `TransactionWriter` callback declaration using an inner
    bean declaration. The bean must implement
    [TransactionWriter](https://geode.apache.org/releases/latest/javadoc/org/apache/geode/cache/TransactionWriter.html).
    The `TransactionWriter` is a callback that can veto a transaction.

7. Example of a `GatewayConflictResolver` callback declaration using a
    bean reference. The referenced bean must implement
    [GatewayConflictResolver](https://geode.apache.org/releases/latest/javadoc/org/apache/geode/cache/util/GatewayConflictResolver.html). A `GatewayConflictResolver` is a
    `Cache`-level plugin that is called upon to decide what to do with
    events that originate in other systems and arrive through the WAN
    Gateway. which provides a distributed Region creation service.

8. Declares a JNDI binding to enlist an external DataSource in a
    GemFire transaction.

#### Enabling PDX Serialization

The preceding example includes a number of attributes related to
GemFire's enhanced serialization framework, PDX. While a
complete discussion of PDX is beyond the scope of this reference guide,
it is important to note that PDX is enabled by registering a
`PdxSerializer`, which is specified by setting the `pdx-serializer`
attribute.

GemFire provides an implementing class
(`org.apache.geode.pdx.ReflectionBasedAutoSerializer`) that uses Java
Reflection. However, it is common for developers to provide their own
implementation. The value of the attribute is simply a reference to a
Spring bean that implements the `PdxSerializer` interface.

For more information about serialization support, see
[Wiring deserialized instances](serialization.html#wiring-deserialized-instances)
in _Working with GemFire Serialization_.

#### Enabling Auto-reconnect

You should be careful when setting the 
`<gfe:cache enable-auto-reconnect="[true|false*]">` attribute to `true`.

Generally, `auto-reconnect` should only be enabled in cases where
Spring Data for VMware GemFire's XML namespace is used to configure and bootstrap a new,
non-application GemFire server added to a cluster. 'auto-reconnect' should not be enabled when Spring Data for VMware GemFire is used to
develop and build a GemFire application that also happens to
be a peer `Cache` member of the GemFire cluster.

The main reason for this restriction is that most GemFire
applications use references to the GemFire `Cache` or Regions
to perform data access operations. These references are
"injected" by the Spring container into application components (such as
Repositories) for use by the application. When a peer member is
forcefully disconnected from the rest of the cluster, presumably because
the peer member has become unresponsive or a network partition separates
one or more peer members into a group too small to function as an
independent distributed system, the peer member shuts down and all
GemFire component references (caches, Regions, and others)
become invalid.

Essentially, the current forced disconnect processing logic in each peer
member dismantles the system from the ground up. The JGroups stack shuts
down, the distributed system is put in a shutdown state and, finally,
the cache is closed. Effectively, all memory references become stale and
are lost.

After being disconnected from the distributed system, a peer member
enters a "reconnecting" state and periodically attempts to rejoin the
distributed system. If the peer member succeeds in reconnecting, the
member rebuilds its "view" of the distributed system from existing
members and receives a new distributed system ID. Additionally, all
caches, Regions, and other GemFire components are
reconstructed. Therefore, all old references, which may have been
injected into application by the Spring container, are now stale and no
longer valid.

GemFire makes no guarantee (even when using the
GemFire public Java API) that application cache, Regions, or
other component references are automatically refreshed by the reconnect
operation. As such, GemFire applications must take care to
refresh their own references.

Unfortunately, there is no way to be notified of a disconnect event and,
subsequently, a reconnect event either. If that were the case, you would
have a clean way to know when to call
`ConfigurableApplicationContext.refresh()`, if it were even applicable
for an application to do so, which is why this "feature" of
GemFire is not recommended for peer `Cache` applications.

For more information about `auto-reconnect`, see [Handling Forced Cache Disconnection Using Autoreconnect](https://docs.vmware.com/en/VMware-Tanzu-GemFire/9.15/tgf/GUID-managing-member-reconnect.html) in the GemFire product documentation.

#### Using Cluster-based Configuration

GemFire's Cluster Configuration Service is a convenient way
for any peer member joining the cluster to get a "consistent view" of
the cluster by using the shared, persistent configuration maintained by
a Locator. Using the cluster-based configuration ensures the peer
member's configuration is compatible with the GemFire
Distributed System when the member joins.

This feature of Spring Data for VMware GemFire (setting the `use-cluster-configuration`
attribute to `true`) works in the same way as the `cache-xml-location`
attribute, except the source of the GemFire configuration
meta-data comes from the network through a Locator, as opposed to a
native `cache.xml` file residing in the local file system.

All GemFire native configuration metadata, whether from
`cache.xml` or from the Cluster Configuration Service, gets applied
before any Spring (XML) configuration metadata. As a result, Spring's
config serves to "augment" the native GemFire configuration
metadata and would most likely be specific to the application.

To enable this feature, specify the following in the Spring XML
config:

```highlight
<gfe:cache use-cluster-configuration="true"/>
```
<p class="note"><strong>Note</strong>: While certain GemFire tools, such as
<code>gfsh</code>, have their actions "recorded" when schema-like changes
are made (for example, <code>gfsh>create region --name=Example --type=PARTITION</code>),
Spring Data for VMware GemFire's configuration metadata is not recorded. The same is true
when using GemFire's public Java API directly. It, too, is not
recorded.</p>

For more information about GemFire's Cluster Configuration
Service, see [Overview of the Cluster Configuration Service](https://docs.vmware.com/en/VMware-Tanzu-GemFire/9.15/tgf/GUID-configuring-cluster_config-gfsh_persist.html) in the GemFire product documentation.

### <a id="configuring-gemfire-cacheserver"></a>Configuring a GemFire CacheServer

Spring Data for VMware GemFire includes dedicated support for configuring a
[CacheServer](https://geode.apache.org/releases/latest/javadoc/org/apache/geode/cache/server/CacheServer.html). This allows complete configuration through the Spring container, as shown in the following example:

```highlight
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:context="http://www.springframework.org/schema/context"
       xmlns:gfe="https://www.springframework.org/schema/geode"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xsi:schemaLocation="
    http://www.springframework.org/schema/beans https://www.springframework.org/schema/beans/spring-beans.xsd
    http://www.springframework.org/schema/context https://www.springframework.org/schema/context/spring-context.xsd
    https://www.springframework.org/schema/geode https://www.springframework.org/schema/geode/spring-geode.xsd
">
  
  <gfe:cache/>
  
  <!-- Example depicting several GemFire CacheServer configuration options -->
  <gfe:cache-server id="advanced-config" auto-startup="true"
       bind-address="localhost" host-name-for-clients="localhost" port="${gemfire.cache.server.port}"
       load-poll-interval="2000" max-connections="22" max-message-count="1000" max-threads="16"
       max-time-between-pings="30000" groups="test-server">
  
    <gfe:subscription-config eviction-type="ENTRY" capacity="1000" disk-store="file://${java.io.tmpdir}"/>
  
  </gfe:cache-server>
  
  <context:property-placeholder location="classpath:cache-server.properties"/>
  
</beans>
```

The preceding configuration shows the `cache-server` element and the
many available options.

Rather than hard-coding the port, this configuration
uses Spring's <a
href="https://docs.spring.io/spring/docs/current/spring-framework-reference/htmlsingle/#xsd-config-body-schemas-context">context</a>
namespace to declare a <code>property-placeholder</code>. A <a
href="https://docs.spring.io/spring/docs/current/spring-framework-reference/htmlsingle/#beans-factory-placeholderconfigurer">property
placeholder</a> reads one or more properties files and then replaces
property placeholders with values at runtime. Doing so lets
administrators change values without having to touch the main
application configuration. Spring also provides <a
href="https://docs.spring.io/spring/docs/current/spring-framework-reference/htmlsingle/#expressions">SpEL</a>
and an <a
href="https://docs.spring.io/spring/docs/current/spring-framework-reference/htmlsingle/#beans-environment">environment
abstraction</a> to support externalization of environment-specific
properties from the main codebase, easing deployment across multiple
machines.

To avoid initialization problems, the
<code>CacheServer</code> started by Spring Data for VMware GemFire starts
<strong>after</strong> the Spring container has been fully initialized.
Doing so lets potential Regions, listeners, writers or instantiators
that are defined declaratively to be fully initialized and registered
before the server starts accepting connections. Keep this in mind when
programmatically configuring these elements, as the server might start
before your components and thus not be seen by the clients connecting
immediately.

### <a id="configuring-gemfire-clientcache"></a>Configuring a GemFire ClientCache

In addition to defining a GemFire peer
[Cache](https://geode.apache.org/releases/latest/javadoc/org/apache/geode/cache/Cache.html),
Spring Data for VMware GemFire also supports the definition of a GemFire
[ClientCache](https://geode.apache.org/releases/latest/javadoc/org/apache/geode/cache/client/ClientCache.html)
in a Spring container. A `ClientCache` definition is similar in
configuration and use to the GemFire peer
[Cache](#configuring-cache) and is supported by the
`org.springframework.data.gemfire.client.ClientCacheFactoryBean`.

The simplest definition of a GemFire cache client using
default configuration follows:

```highlight
<beans>
  <gfe:client-cache/>
</beans>
```

`client-cache` supports many of the same options as the
[Cache](#advanced-cache-configuration) element. However, as opposed to a
full-fledged peer `Cache` member, a cache client connects to a remote
cache server through a Pool. By default, a Pool is created to connect to
a server running on `localhost` and listening to port `40404`. The
default Pool is used by all client Regions unless the Region is
configured to use a specific Pool.

Pools can be defined with the `pool` element. This client-side Pool can
be used to configure connectivity directly to a server for individual
entities or for the entire cache through one or more Locators.

For example, to customize the default Pool used by the `client-cache`,
the developer must define a Pool and wire it to the cache
definition, as the following example shows:

```highlight
<beans>
  <gfe:client-cache id="myCache" pool-name="myPool"/>

  <gfe:pool id="myPool" subscription-enabled="true">
    <gfe:locator host="${gemfire.locator.host}" port="${gemfire.locator.port}"/>
  </gfe:pool>
</beans>
```

The `<client-cache>` element also has a `ready-for-events` attribute. If
the attribute is set to `true`, the client cache initialization includes
a call to
[ClientCache.readyForEvents()](https://geode.apache.org/releases/latest/javadoc/org/apache/geode/cache/client/ClientCache.html#readyForEvents).

[Client Region](#client-region) describes client-side
configuration in more detail.

#### GemFire's DEFAULT Pool and Spring Data for VMware GemFire Pool Definitions

If a GemFire `ClientCache` is local-only, then no Pool
definition is required. For instance, you can define the following:

```highlight
<gfe:client-cache/>

<gfe:client-region id="Example" shortcut="LOCAL"/>
```

In this case, the "Example" Region is `LOCAL` and no data is distributed
between the client and a server. Therefore, no Pool is necessary. This
is true for any client-side, local-only Region, as defined by the
GemFire's
[ClientRegionShortcut](https://geode.apache.org/releases/latest/javadoc/org/apache/geode/cache/client/ClientRegionShortcut.html).

However, if a client Region is a (caching) proxy to a server-side
Region, a Pool is required. In that case, there are several ways to
define and use a Pool.

When a `ClientCache`, a Pool, and a proxy-based Region are all defined
but not explicitly identified, Spring Data for VMware GemFire resolves the references
automatically, as shown in the following example:

```highlight
<gfe:client-cache/>

<gfe:pool>
  <gfe:locator host="${geode.locator.host}" port="${geode.locator.port}"/>
</gfe:pool>

<gfe:client-region id="Example" shortcut="PROXY"/>
```

In this example, the `ClientCache` is identified as
`gemfireCache`, the Pool as `gemfirePool`, and the client Region as
"Example". However, the `ClientCache` initializes GemFire's
`DEFAULT` Pool from `gemfirePool`, and the client Region uses the
`gemfirePool` when distributing data between the client and the server.

Basically, Spring Data for VMware GemFire resolves the preceding configuration to the
following:

```highlight
<gfe:client-cache id="gemfireCache" pool-name="gemfirePool"/>

<gfe:pool id="gemfirePool">
  <gfe:locator host="${geode.locator.host}" port="${geode.locator.port}"/>
</gfe:pool>

<gfe:client-region id="Example" cache-ref="gemfireCache" pool-name="gemfirePool" shortcut="PROXY"/>
```

GemFire still creates a Pool named `DEFAULT`. Spring Data for VMware GemFire
causes the `DEFAULT` Pool to be initialized from the `gemfirePool`.
Doing so is useful in situations where multiple Pools are defined and
client Regions are using separate Pools, or do not declare a Pool at
all.

Consider the following:

```highlight
<gfe:client-cache pool-name="locatorPool"/>

<gfe:pool id="locatorPool">
  <gfe:locator host="${geode.locator.host}" port="${geode.locator.port}"/>
</gfe:pool>

<gfe:pool id="serverPool">
  <gfe:server host="${geode.server.host}" port="${geode.server.port}"/>
</gfe:pool>

<gfe:client-region id="Example" pool-name="serverPool" shortcut="PROXY"/>

<gfe:client-region id="AnotherExample" shortcut="CACHING_PROXY"/>

<gfe:client-region id="YetAnotherExample" shortcut="LOCAL"/>
```

In this example, the GemFire `client-cache` `DEFAULT` pool is
initialized from `locatorPool`, as specified by the `pool-name`
attribute. There is no Spring Data for VMware GemFire-defined `gemfirePool`, since both
Pools were explicitly identified (named) as `locatorPool` and
`serverPool`, respectively.

The "Example" Region explicitly refers to and exclusively uses the
`serverPool`. The `AnotherExample` Region uses GemFire's
`DEFAULT` Pool, which was configured from the `locatorPool`
based on the client cache bean definition's `pool-name` attribute.

Finally, the `YetAnotherExample` Region does not use a Pool, because it
is `LOCAL`.

The <code>AnotherExample</code> Region would first
look for a Pool bean named <code>gemfirePool</code>, but that would
require the definition of an anonymous Pool bean: <code><gfe:pool/></code> or a Pool bean explicitly named <code>gemfirePool</code>. For example,
<code><gfe:pool id="gemfirePool"/></code>.

If we either changed the name of
<code>locatorPool</code> to <code>gemfirePool</code> or made the Pool
bean definition be anonymous, it would have the same effect as the
preceding configuration.

## <a id="configuring-region"></a>Configuring a Region

A Region is required to store and retrieve data from the cache.
`org.apache.geode.cache.Region` is an interface extending
`java.util.Map` and enables basic data access using familiar key-value
semantics. The `Region` interface is wired into application classes that
require it so the actual Region type is decoupled from the programming
model. Typically, each Region is associated with one domain object,
similar to a table in a relational database.

GemFire implements the following types of Regions:

* **REPLICATE**: Data is replicated across all cache members in the
  cluster that define the Region. This provides very high read
  performance but writes take longer to perform the replication.

* **PARTITION**: Data is partitioned into buckets (sharded) among many
  cache members in the cluster that define the Region. This provides
  high read and write performance and is suitable for large data sets
  that are too large for a single node.

* **LOCAL**: Data only exists on the local node.

* **Client**: Technically, a client Region is a LOCAL Region that acts
  as a PROXY to a REPLICATE or PARTITION Region hosted on cache servers
  in a cluster. It may hold data created or fetched locally.
  Alternately, it can be empty. Local updates are synchronized to the
  cache server. Also, a client Region may subscribe to events in order
  to stay up-to-date (synchronized) with changes originating from remote
  processes that access the same server Region.

For more information about the various Region types and their
capabilities as well as configuration options, see [Region Types](https://docs.vmware.com/en/VMware-Tanzu-GemFire/9.15/tgf/GUID-developing-region_options-region_types.html) in the GemFire product documentation.

### <a id="using-externally-configured-region"></a>Using an Externally Configured Region

To reference Regions already configured in a GemFire native
`cache.xml` file, use the `lookup-region` element. Declare the
target Region name with the `name` attribute. For example, to declare a
bean definition identified as `ordersRegion` for an existing Region
named `Orders`, you can use the following bean definition:

```highlight
<gfe:lookup-region id="ordersRegion" name="Orders"/>
```

If `name` is not specified, the bean's `id` will be used as the name of
the Region. The example above becomes:

```highlight
<!-- lookup for a Region called 'Orders' -->
<gfe:lookup-region id="Orders"/>
```

<p class="note warning"><strong>Warning</strong>: If the Region does not exist, an initialization
exception will be thrown. To configure new Regions, proceed to the
appropriate sections below.</p>

In the previous examples, since no cache name was explicitly defined,
the default naming convention (`gemfireCache`) was used. Alternately,
one can reference the cache bean with the `cache-ref` attribute:

```highlight
<gfe:cache id="myCache"/>
<gfe:lookup-region id="ordersRegion" name="Orders" cache-ref="myCache"/>
```

`lookup-region` lets you retrieve existing, pre-configured Regions
without exposing the Region semantics or setup infrastructure.

### <a id="auto-region-lookup"></a>Auto Region Lookup

`auto-region-lookup` lets you import all Regions defined in a
GemFire native `cache.xml` file into a Spring
`ApplicationContext` when you use the `cache-xml-location` attribute on
the `<gfe:cache>` element.

For example, consider the following `cache.xml` file:

```highlight
<?xml version="1.0" encoding="UTF-8"?>
<cache xmlns="https://geode.apache.org/schema/cache"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xsi:schemaLocation="http://geode.apache.org/schema/cache https://geode.apache.org/schema/cache/cache-1.0.xsd"
       version="1.0">
  
  <region name="Parent" refid="REPLICATE">
    <region name="Child" refid="REPLICATE"/>
  </region>
  
</cache>
```

You can import the preceding `cache.xml` file as follows:

```highlight
<gfe:cache cache-xml-location="cache.xml"/>
```

You can then use the `<gfe:lookup-region>` element (for example,
`<gfe:lookup-region id="Parent"/>`) to reference specific Regions as
beans in the Spring container, or you can choose to import all Regions
defined in `cache.xml` by using the following:

```highlight
<gfe:auto-region-lookup/>
```

Spring Data for VMware GemFire automatically creates beans for all GemFire Regions
defined in `cache.xml` that have not been explicitly added to the Spring
container with explicit `<gfe:lookup-region>` bean declarations.

Note that Spring Data for VMware GemFire uses a Spring
[BeanPostProcessor](https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/beans/factory/config/BeanPostProcessor.html)
to post-process the cache after it is both created and initialized to
determine the Regions defined in GemFire to add as beans in
the Spring `ApplicationContext`.

You can inject these "auto-looked-up" Regions as you would any other
bean defined in the Spring `ApplicationContext`, with one exception: You
may need to define a `depends-on` association with the ‘gemfireCache'
bean, as follows:

```highlight
package example;
  
import ...
  
@Repository("appDao")
@DependsOn("gemfireCache")
public class ApplicationDao extends DaoSupport {
  
    @Resource(name = "Parent")
    private Region<?, ?> parent;
  
    @Resource(name = "/Parent/Child")
    private Region<?, ?> child;
  
    ...
}
```

The preceding example only applies when you use Spring's
`component-scan` functionality.

If you declare your components by using Spring XML config, then you
would use the following:

```highlight
<bean class="example.ApplicationDao" depends-on="gemfireCache"/>
```

Doing so ensures that the GemFire cache and all the Regions
defined in `cache.xml` are created before any components with auto-wire
references when using the `<gfe:auto-region-lookup>` element.

### <a id="configuring-regions"></a>Configuring Regions

Spring Data for VMware GemFire provides comprehensive support for configuring any type of
Region through the following elements:

* LOCAL Region: `<local-region>`

* PARTITION Region: `<partitioned-region>`

* REPLICATE Region: `<replicated-region>`

* Client Region: `<client-region>`

For description of these types, see [Region Types](https://docs.vmware.com/en/VMware-Tanzu-GemFire/9.15/tgf/GUID-developing-region_options-region_types.html) in the GemFire product documentation.

#### Common Region Attributes

The following table lists the attributes available for all Region types:

<table>
  <caption>Table 1. Common Region Attributes</caption>
  <colgroup>
    <col style="width: 20%" />
    <col style="width: 30%" />
    <col style="width: 50%" />
  </colgroup>
  <thead>
    <tr class="header">
      <th>Name</th>
      <th>Values</th>
      <th>Description</th>
    </tr>
  </thead>
  <tbody>
    <tr>
      <td>cache-ref</td>
      <td>GemFire Cache bean reference</td>
      <td>The name of the bean defining the GemFire Cache. Defaults to
<code>gemfireCache</code>.</td>
    </tr>
    <tr>
      <td>cloning-enabled</td>
      <td>boolean (default: <code>false</code>)</td>
      <td>When <code>true</code>, updates are applied to a clone of the value, then the clone is saved to the cache. When <code>false</code>, the value is modified in place in the cache.</td>
    </tr>
    <tr>
      <td>close</td>
      <td>boolean (default: <code>false</code>)</td>
      <td>Determines whether the region should be closed at shutdown.</td>
    </tr>
    <tr>
      <td>concurrency-checks-enabled</td>
      <td>boolean (default: <code>true</code>)</td>
      <td>Determines whether members perform checks to provide consistent handling for concurrent or out-of-order updates to distributed regions.</td>
    </tr>
    <tr>
      <td>data-policy</td>
      <td>See <a href="https://geode.apache.org/releases/latest/javadoc/org/apache/geode/cache/DataPolicy.html">DataPolicy</a>.</td>
      <td>The region's data policy. Not all data policies are supported for every Region type.</td>
    </tr>
    <tr>
      <td>destroy</td>
      <td>boolean (default: <code>false</code>)</td>
      <td>Determines whether the region should be destroyed at shutdown.</td>
    </tr>
    <tr>
      <td>disk-store-ref</td>
      <td>The name of a configured disk store.</td>
      <td>A reference to a bean created through the <code>disk-store</code> element.</td>
    </tr>
    <tr>
      <td>disk-synchronous</td>
      <td>boolean (default: <code>true</code>)</td>
      <td>Determines whether disk store writes are synchronous.</td>
    </tr>
    <tr>
      <td>id</td>
      <td>Any valid bean name.</td>
      <td>The default region name if no <code>name</code> attribute is specified.</td>
    </tr>
    <tr>
      <td>ignore-if-exists</td>
      <td>boolean (default: <code>false</code>)</td>
      <td>Ignores this bean definition if the region already exists in the cache, resulting in a lookup instead.</td>
    </tr>
    <tr>
      <td>ignore-jta</td>
    <td>boolean (default: <code>false</code>)</td>
      <td>Determines whether this Region participates in JTA (Java Transaction API) transactions.</td>
    </tr>
    <tr>
      <td>index-update-type</td>
      <td><code>synchronous</code> or <code>asynchronous</code> (default: <code>synchronous</code>)</td>
      <td>Determines whether Indices are updated synchronously or asynchronously on entry creation.</td>
    </tr>
    <tr>
      <td>initial-capacity</td>
      <td>integer (default: 16)</td>
      <td>The initial memory allocation for the number of Region entries.</td>
    </tr>
    <tr>
      <td>key-constraint</td>
      <td>Any valid, fully-qualified Java class name.</td>
      <td>Expected key type.</td>
    </tr>
    <tr>
      <td>load-factor</td>
      <td>float (default: .75)</td>
      <td>Sets the initial parameters on the underlying <code>java.util.ConcurrentHashMap</code> used for storing region entries.</td>
    </tr>
    <tr>
      <td>name</td>
      <td>Any valid region name.</td>
      <td>The name of the region. If not specified, it assumes the value of the <code>id</code> attribute, the bean name).</td>
    </tr>
    <tr>
      <td>persistent</td>
      <td>boolean (default: <code>false</code>)</td>
      <td>Determines whether the region persists entries to local disk (disk store).</td>
    </tr>
    <tr>
      <td>shortcut</td>
      <td>See <a href="https://geode.apache.org/releases/latest/javadoc/org/apache/geode/cache/RegionShortcut.html">RegionShortcut</a></td>
      <td>The <code>RegionShortcut</code> for this region. Allows easy initialization of the region based on pre-defined defaults.</td>
    </tr>
    <tr>
      <td>statistics</td>
      <td>boolean (default: <code>false</code>)</td>
      <td>Determines whether the region reports statistics.</td>
    </tr>
    <tr>
      <td>template</td>
      <td>The name of a region template.</td>
      <td>A reference to a bean created through one of the <code>region-template</code> elements.</td>
    </tr>
    <tr>
      <td>value-constraint</td>
      <td>Any valid, fully-qualified Java class name.</td>
      <td>Expected value type.</td>
    </tr>
  </tbody>
</table>

#### `CacheListener` instances

`CacheListener` instances are registered with a Region to handle Region
events, such as when entries are created, updated, destroyed, and so on.
A `CacheListener` can be any bean that implements the
[CacheListener](https://geode.apache.org/releases/latest/javadoc/org/apache/geode/cache/CacheListener.html) interface. A Region may have multiple listeners, declared with the
`cache-listener` element nested in the containing `*-region` element.

The following example has two declared `CacheListener's`. The first
references a named, top-level Spring bean. The second is an anonymous
inner bean definition.

```highlight
<bean id="myListener" class="org.example.app.geode.cache.SimpleCacheListener"/>

<gfe:replicated-region id="regionWithListeners">
  <gfe:cache-listener>
    <!-- nested CacheListener bean reference -->
    <ref bean="myListener"/>
    <!-- nested CacheListener bean definition -->
    <bean class="org.example.app.geode.cache.AnotherSimpleCacheListener"/>
  </gfe:cache-listener>
</gfe:replicated-region>
```

The following example uses an alternate form of the `cache-listener`
element with the `ref` attribute. Doing so allows for more concise
configuration when defining a single `CacheListener`.

The XML namespace allows only a single `cache-listener` element,
so either the style shown in the preceding example or the style in the
following example must be used.

```highlight
<beans>
  <gfe:replicated-region id="exampleReplicateRegionWithCacheListener">
    <gfe:cache-listener ref="myListener"/>
  </gfe:replicated-region>

  <bean id="myListener" class="example.CacheListener"/>
</beans>
```

<p class="note warning"><strong>Warning</strong>: Using <code>ref</code> and a nested declaration in
the <code>cache-listener</code> element is illegal. The two options are
mutually exclusive and using both in the same element results in an
exception.</p>

**Bean Reference Conventions**

The <code>cache-listener</code> element is an example of a common
pattern used in the XML namespace anywhere GemFire provides a
callback interface to be implemented to invoke custom code in
response to cache or Region events. When you use Spring's IoC container,
the implementation is a standard Spring bean. To simplify the
configuration, the schema allows a single occurrence of the
<code>cache-listener</code> element, but, if multiple instances are
permitted, it may contain nested bean references and inner bean
definitions in any combination. The convention is to use the singular
form, <code>cache-listener</code> instead of <code>cache-listeners</code>, reflecting that the most common scenario is a single instance. THis pattern can be seen in the <a href="#advanced-cache-configuration">advanced cache</a>
configuration example.

#### CacheLoaders and CacheWriters

Similar to `cache-listener`, the XML namespace provides `cache-loader`
and `cache-writer` elements to register these GemFire
components for a Region.

A `CacheLoader` is invoked on a cache miss to let an entry be loaded
from an external data source, such as a database. A `CacheWriter` is
invoked before an entry is created or updated, to allow the entry to be
synchronized to an external data source. The main difference is that
GemFire supports, at most, a single instance of `CacheLoader`
and `CacheWriter` per Region. However, either declaration style may be
used.

The following example declares a Region with both a `CacheLoader` and a
`CacheWriter`:

```highlight
<beans>
  <gfe:replicated-region id="exampleReplicateRegionWithCacheLoaderAndCacheWriter">
    <gfe:cache-loader ref="myLoader"/>
    <gfe:cache-writer>
      <bean class="example.CacheWriter"/>
    </gfe:cache-writer>
  </gfe:replicated-region>

  <bean id="myLoader" class="example.CacheLoader">
    <property name="dataSource" ref="mySqlDataSource"/>
  </bean>
  
  <!-- DataSource bean definition -->
</beans>
```

For more details, see [CacheLoader](https://geode.apache.org/releases/latest/javadoc/org/apache/geode/cache/CacheLoader.html) and [CacheWriter](https://geode.apache.org/releases/latest/javadoc/org/apache/geode/cache/CacheWriter.html).

### <a id="compression"></a>Compression

GemFire Regions may be compressed to reduce JVM
memory consumption and pressure to possibly avoid global GCs. When you
enable compression for a Region, all values stored in memory for the
Region are compressed, while keys and indexes remain uncompressed. New
values are compressed when put into the Region and all values are
decompressed automatically when read back from the Region. Values are
not compressed when persisted to disk or when sent over the wire to
other peer members or clients.

The following example shows a Region with compression enabled:

```highlight
<beans>
  <gfe:replicated-region id="exampleReplicateRegionWithCompression">
    <gfe:compressor>
      <bean class="org.apache.geode.compression.SnappyCompressor"/>
    </gfe:compressor>
  </gfe:replicated-region>
</beans>
```

For more information, see [Region Compression](https://docs.vmware.com/en/VMware-Tanzu-GemFire/9.15/tgf/GUID-managing-region_compression.html) in the GemFire product documentation.

### <a id="off-heap"></a>Off-Heap

GemFire Regions may be configured to store Region values
in off-heap memory, which is a portion of JVM memory that is not subject
to Garbage Collection (GC). By avoid expensive GC cycles, your
application can spend more time on other tasks such as processing requests.

To use off-heap memory, declare the amount of memory to
use, then enable your Regions to use off-heap memory, as shown in
the following configuration:

```highlight
<util:properties id="gemfireProperties">
    <prop key="off-heap-memory-size">200G</prop>
</util:properties>
  
<gfe:cache properties-ref="gemfireProperties"/>
  
<gfe:partitioned-region id="ExampleOffHeapRegion" off-heap="true"/>
```

You can control other aspects of off-heap memory management by setting
the following GemFire configuration properties using 
`<gfe:cache>` elements:

```highlight
<gfe:cache critical-off-heap-percentage="90" eviction-off-heap-percentage"80"/>
```

GemFire's `ResourceManager` will use the `critical-off-heap-percentage` and  `eviction-off-heap-percentage` threshold values to more effectively manage the off-heap memory in a way similar to how 
the JVM manages heap memory. GemFire
`ResourceManager` will prevent the cache from consuming too much
off-heap memory by evicting old data. If the off-heap manager is unable
to keep up, then the `ResourceManager` refuses additions to the cache
until the off-heap memory manager has freed up an adequate amount of
memory.

For more information, see [Managing Off-Heap Memory](https://docs.vmware.com/en/VMware-Tanzu-GemFire/9.15/tgf/GUID-managing-heap_use-off_heap_management.html) in the GemFire product documentation:

### <a id="subregions"></a>Subregions

Spring Data for VMware GemFire supports Sub-Regions, allowing Regions to be arranged in
a hierarchical relationship.

For example, GemFire allows for a `/Customer/Address` Region
and a different `/Employee/Address` Region. Additionally, a Sub-Region
may have its own Sub-Regions and configuration. A Sub-Region does not
inherit attributes from its parent Region. Regions types may be mixed
and matched subject to GemFire constraints. A Sub-Region is
naturally declared as a child element of a Region. The Sub-Region's
`name` attribute is the simple name. The preceding example might be
configured as follows:

```highlight
<beans>
  <gfe:replicated-region name="Customer">
    <gfe:replicated-region name="Address"/>
  </gfe:replicated-region>

  <gfe:replicated-region name="Employee">
    <gfe:replicated-region name="Address"/>
  </gfe:replicated-region>
</beans>
```

The `Monospaced ([id])` attribute is not permitted for a
Sub-Region. Sub-Regions are created with bean names such as `/Customer/Address`
and `/Employee/Address` in this example. This allows them to be
injected into other application beans, such as a `GemfireTemplate`, that
requires them by using the full path name of the Region. The full pathname
of the Region should also be used in OQL query strings.

### <a id="region-templates"></a>Region Templates

Spring Data for VMware GemFire supports Region templates.

This feature allows developers to define common Region configuration and
attributes once and reuse the configuration among many Region bean
definitions declared in the Spring `ApplicationContext`.

Spring Data for VMware GemFire includes five Region template tags in its namespace:


<table>
  <caption>Table 2. Region Template Tags</caption>
  <thead>
    <tr class="header">
      <th>Tag Name</th>
      <th>Description</th>
    </tr>
  </thead>
  <tbody>
    <tr>
      <td><code>&lt;gfe:region-template&gt;</code></td>
      <td>Defines common generic Region attributes. Extends <code>regionType</code> in the XML namespace.</td>
    </tr>
    <tr>
      <td><code>&lt;gfe:local-region-template&gt;</code></td>
    <td>Defines common 'Local' Region attributes. Extends <code>localRegionType</code> in the XML namespace.</td>
    </tr>
    <tr>
      <td><code>&lt;gfe:partitioned-region-template&gt;</code></td>
    <td>Defines common 'PARTITION' Region attributes. Extends <code>partitionedRegionType</code> in the XML namespace.</td>
    </tr>
    <tr>
      <td><code>&lt;gfe:replicated-region-template&gt;</code></td>
      <td>Defines common 'REPLICATE' Region attributes. Extends <code>replicatedRegionType</code> in the XML namespace.</td>
    </tr>
    <tr>
      <td><code>&lt;gfe:client-region-template&gt;</code></td>
      <td>Defines common 'Client' Region attributes. Extends <code>clientRegionType</code> in the XML namespace.</td>
    </tr>
  </tbody>
</table>

In addition to the tags, concrete `<gfe:*-region>` elements and the abstract `<gfe:*-region-template>` elements have a `template`
attribute used to define the Region template from which the Region
inherits its configuration. Region templates may even inherit from other
Region templates.

The following example shows one possible configuration:

```highlight
<beans>
  <gfe:async-event-queue id="AEQ" persistent="false" parallel="false" dispatcher-threads="4">
    <gfe:async-event-listener>
      <bean class="example.AeqListener"/>
    </gfe:async-event-listener>
  </gfe:async-event-queue>

  <gfe:region-template id="BaseRegionTemplate" initial-capacity="51" load-factor="0.85" persistent="false" statistics="true"
      key-constraint="java.lang.Long" value-constraint="java.lang.String">
    <gfe:cache-listener>
      <bean class="example.CacheListenerOne"/>
      <bean class="example.CacheListenerTwo"/>
    </gfe:cache-listener>
    <gfe:entry-ttl timeout="600" action="DESTROY"/>
    <gfe:entry-tti timeout="300" action="INVALIDATE"/>
  </gfe:region-template>

  <gfe:region-template id="ExtendedRegionTemplate" template="BaseRegionTemplate" load-factor="0.55">
    <gfe:cache-loader>
      <bean class="example.CacheLoader"/>
    </gfe:cache-loader>
    <gfe:cache-writer>
      <bean class="example.CacheWriter"/>
    </gfe:cache-writer>
    <gfe:async-event-queue-ref bean="AEQ"/>
  </gfe:region-template>

  <gfe:partitioned-region-template id="PartitionRegionTemplate" template="ExtendedRegionTemplate"
      copies="1" load-factor="0.70" local-max-memory="1024" total-max-memory="16384" value-constraint="java.lang.Object">
    <gfe:partition-resolver>
      <bean class="example.PartitionResolver"/>
    </gfe:partition-resolver>
    <gfe:eviction type="ENTRY_COUNT" threshold="8192000" action="OVERFLOW_TO_DISK"/>
  </gfe:partitioned-region-template>

  <gfe:partitioned-region id="TemplateBasedPartitionRegion" template="PartitionRegionTemplate"
      copies="2" local-max-memory="8192" persistent="true" total-buckets="91"/>
</beans>
```

Region templates work for Sub-Regions as well. Notice that
'TemplateBasedPartitionRegion' extends 'PartitionRegionTemplate', which
extends 'ExtendedRegionTemplate', which extends 'BaseRegionTemplate'.
Attributes and sub-elements defined in subsequent, inherited Region bean
definitions override what exists in the parent.

#### How Templating Works

Spring Data for VMware GemFire applies Region templates when the Spring `ApplicationContext`
configuration metadata is parsed, and therefore, Region templates must
be declared in the order of inheritance. In other words, parent
templates must be defined before child templates. Doing so ensures that
the proper configuration is applied, especially when element attributes
or sub-elements are overridden.

<p class="note"><strong>Note</strong>: Region templates are single-inheritance, and Region
types must only inherit from other similarly typed Regions. For
example, a <code><gfe:replicated-region></code> cannot inherit from a
<code><gfe:partitioned-region-template></code>.</p>

#### Caution Concerning Regions, Sub-Regions, and Lookups

Previously, one of the underlying properties of the `replicated-region`,
`partitioned-region`, `local-region`, and `client-region` elements in
the Spring Data for VMware GemFire XML namespace was to perform a lookup first before
attempting to create a Region. This was done in case the Region already
existed, which would be the case if the Region was defined in an
imported GemFire native `cache.xml` configuration file.
Therefore, the lookup was performed first to avoid any errors. This was
by design and subject to change.

This behavior has been altered and the default behavior is now to create
the Region first. If the Region already exists, then the creation logic
fails-fast and an appropriate exception is thrown. However, much like
the `CREATE TABLE IF NOT EXISTS …​` DDL syntax, the Spring Data for VMware GemFire
`<gfe:*-region>` XML namespace elements now include a `ignore-if-exists`
attribute, which reinstates the old behavior by first performing a
lookup of an existing Region identified by name before attempting to
create the Region. If an existing Region is found by name and
`ignore-if-exists` is set to `true`, then the Region bean definition
defined in Spring configuration is ignored.

<p class="note warning"><strong>Warning</strong>: 

<td class="content">The Spring team highly recommends that the
<code>replicated-region</code>, <code>partitioned-region</code>,
<code>local-region</code>, and <code>client-region</code> XML namespace
elements be strictly used for defining new Regions only. One problem
that could arise when the Regions defined by these elements already
exist and the Region elements perform a lookup first is, if you defined
different Region semantics and behaviors for eviction, expiration,
subscription, and so on in your application config, then the Region
definition might not match and could exhibit contrary behaviors to those
required by the application. Even worse, you might want to define the
Region as a distributed Region (for example, <code>PARTITION</code>)
when, in fact, the existing Region definition is local only.
<br><br>
As a recommended practice, use only
<code>replicated-region</code>, <code>partitioned-region</code>,
<code>local-region</code>, and <code>client-region</code> XML namespace
elements to define new Regions.
</p>

Consider the following native GemFire `cache.xml`
configuration file:

```highlight
<?xml version="1.0" encoding="UTF-8"?>
<cache xmlns="https://geode.apache.org/schema/cache"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xsi:schemaLocation="http://geode.apache.org/schema/cache https://geode.apache.org/schema/cache/cache-1.0.xsd"
       version="1.0">
  
  <region name="Customers" refid="REPLICATE">
    <region name="Accounts" refid="REPLICATE">
      <region name="Orders" refid="REPLICATE">
        <region name="Items" refid="REPLICATE"/>
      </region>
    </region>
  </region>
  
</cache>
```

Furthermore, consider that you may have defined an application DAO as
follows:

```highlight
public class CustomerAccountDao extends GemDaoSupport {
  
    @Resource(name = "Customers/Accounts")
    private Region customersAccounts;
  
    ...
}
```

Here, we inject a reference to the `Customers/Accounts` Region in our
application DAO. Consequently, it is not uncommon for a developer to
define beans for some or all of these Regions in Spring XML
configuration metadata as follows:

```highlight
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:gfe="https://www.springframework.org/schema/geode"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xsi:schemaLocation="
    http://www.springframework.org/schema/beans https://www.springframework.org/schema/beans/spring-beans.xsd
    https://www.springframework.org/schema/geode https://www.springframework.org/schema/geode/spring-geode.xsd
">
  
  <gfe:cache cache-xml-location="classpath:cache.xml"/>
  
  <gfe:lookup-region name="Customers/Accounts"/>
  <gfe:lookup-region name="Customers/Accounts/Orders"/>
  
</beans>
```

The `Customers/Accounts` and `Customers/Accounts/Orders` Regions are
referenced as beans in the Spring container as `Customers/Accounts` and
`Customers/Accounts/Orders`, respectively. The nice thing about using
the `lookup-region` element and the corresponding syntax (described
earlier) is that it lets you reference a Sub-Region directly without
unnecessarily defining a bean for the parent Region (`Customers`, in
this case).

Consider the following bad example, which changes the configuration
metadata syntax to use the nested format:

```highlight
<gfe:lookup-region name="Customers">
  <gfe:lookup-region name="Accounts">
    <gfe:lookup-region name="Orders"/>
  </gfe:lookup-region>
</gfe:lookup-region>
```

Now consider another bad example which uses the top-level
`replicated-region` element along with the `ignore-if-exists` attribute
set to perform a lookup first:

```highlight
<gfe:replicated-region name="Customers" persistent="true" ignore-if-exists="true">
  <gfe:replicated-region name="Accounts" persistent="true" ignore-if-exists="true">
    <gfe:replicated-region name="Orders" persistent="true" ignore-if-exists="true"/>
  </gfe:replicated-region>
</gfe:replicated-region>
```

The Region beans defined in the Spring `ApplicationContext` consist of
the following:
`{ "Customers", "/Customers/Accounts", "/Customers/Accounts/Orders" }.`
This means the dependency injected reference shown in the earlier
example (that is, `@Resource(name = "Customers/Accounts")`) is now
broken, since no bean with name `Customers/Accounts` is actually
defined. For this reason, you should not configure Regions as shown in
the two preceding examples.

GemFire is flexible in referencing both parent Regions and
Sub-Regions with or without the leading forward slash. For example, the
parent can be referenced as `/Customers` or `Customers` and the child as
`/Customers/Accounts` or `Customers/Accounts`. However, Spring Data for VMware GemFire is
very specific when it comes to naming beans after Regions. It always
uses the forward slash (/) to represent Sub-Regions (for example,
`/Customers/Accounts`).

Therefore, you should use the non-nested `lookup-region` syntax shown
earlier or define direct references with a leading forward slash (/), as
follows:

```highlight
<gfe:lookup-region name="/Customers/Accounts"/>
<gfe:lookup-region name="/Customers/Accounts/Orders"/>
```

The earlier example, where the nested `replicated-region` elements were
used to reference the Sub-Regions, shows the problem stated earlier. Are
the Customers, Accounts and Orders Regions and Sub-Regions persistent or
not? They are not persistent, because the Regions were defined in the
native GemFire `cache.xml` configuration file as `REPLICATE`
and exist before the cache bean is initialized (once the `<gfe:cache>`
element is processed).

### <a id="data-eviction"></a>Data Eviction (with Overflow)

Based on various constraints, each Region can have an eviction policy in
place for evicting data from memory. Currently, in GemFire,
eviction applies to the Least Recently Used entry (also known as
[LRU](https://en.wikipedia.org/wiki/Cache_replacement_policies#Least_recently_used_(LRU)).
Evicted entries are either destroyed or paged to disk (referred to as
"overflow to disk").

Spring Data for VMware GemFire supports all eviction policies (entry count, memory, and heap
usage) for PARTITION Regions, REPLICATE Regions, and client, local
Regions by using the nested `eviction` element.

For example, to configure a PARTITION Region to overflow to disk if the
memory size exceeds more than 512 MB, you can specify the following
configuration:

```highlight
<gfe:partitioned-region id="examplePartitionRegionWithEviction">
  <gfe:eviction type="MEMORY_SIZE" threshold="512" action="OVERFLOW_TO_DISK"/>
</gfe:partitioned-region>
```

<p class="note warning"><strong>Warning</strong>: Replicas cannot use <code>local destroy</code>
eviction since that would invalidate them. For more information, see the <a href="https://docs.vmware.com/en/VMware-Tanzu-GemFire/index.html">GemFire product documentation</a>.</p>

When configuring Regions for overflow, you should configure the storage
through the `disk-store` element for maximum efficiency.

For a detailed description of eviction policies, see [Eviction](https://docs.vmware.com/en/VMware-Tanzu-GemFire/9.15/tgf/GUID-developing-eviction-chapter_overview.html?hWord=N4IghgNiBcIKYDcCWBjALkg9gOxAXyA) in the GemFire product documentation.

### <a id="data-expiration"></a>Data Expiration

GemFire lets you control how long entries exist in the cache.
Expiration is driven by elapsed time, as opposed to eviction, which is
driven by the entry count or heap or memory usage. Once an entry
expires, it may no longer be accessed from the cache.

GemFire supports the following expiration types:

* **Time-to-Live (TTL)**: The amount of time in seconds that an object
  may remain in the cache after the last creation or update. For
  entries, the counter is set to zero for create and put operations.
  Region counters are reset when the Region is created and when an entry
  has its counter reset.

* **Idle Timeout (TTI)**: The amount of time in seconds that an object
  may remain in the cache after the last access. The Idle Timeout
  counter for an object is reset any time its TTL counter is reset. In
  addition, an entry's Idle Timeout counter is reset any time the entry
  is accessed through a get operation or a `netSearch`. The Idle Timeout
  counter for a Region is reset whenever the Idle Timeout is reset for
  one of its entries.

Each of these expiration types may be applied to the Region itself or to entries in the
Region. Spring Data for VMware GemFire provides `<region-ttl>`, `<region-tti>`,
`<entry-ttl>`, and `<entry-tti>` Region child elements to specify
timeout values and expiration actions.

The following example shows a `PARTITION` Region with expiration values
set:

```highlight
<gfe:partitioned-region id="examplePartitionRegionWithExpiration">
  <gfe:region-ttl timeout="30000" action="INVALIDATE"/>
  <gfe:entry-tti timeout="600" action="LOCAL_DESTROY"/>
</gfe:replicated-region>
```

For a detailed description of expiration policies, see [Expiration](https://docs.vmware.com/en/VMware-Tanzu-GemFire/9.15/tgf/GUID-developing-expiration-chapter_overview.html) in the GemFire product documentation.

#### Annotation-based Data Expiration

With Spring Data for VMware GemFire, you can define expiration policies and settings on
individual Region entry values (or, to put it differently, directly on
application domain objects). For instance, you can define expiration
policies on a Session-based application domain object as follows:

```highlight
@Expiration(timeout = "1800", action = "INVALIDATE")
public class SessionBasedApplicationDomainObject {
  ...
}
```
You can also specify expiration type specific settings on Region entries
by using the `@IdleTimeoutExpiration` and `@TimeToLiveExpiration`
annotations for Idle Timeout (TTI) and Time-to-Live (TTL) expiration,
respectively, as the following example shows:

```highlight
@TimeToLiveExpiration(timeout = "3600", action = "LOCAL_DESTROY")
@IdleTimeoutExpiration(timeout = "1800", action = "LOCAL_INVALIDATE")
@Expiration(timeout = "1800", action = "INVALIDATE")
public class AnotherSessionBasedApplicationDomainObject {
  ...
}
```
Both `@IdleTimeoutExpiration` and `@TimeToLiveExpiration` take
precedence over the generic `@Expiration` annotation when more than one
expiration annotation type is specified, as shown in the preceding
example. Neither `@IdleTimeoutExpiration` nor `@TimeToLiveExpiration`
overrides the other. Rather, they compliment each other when different
Region entry expiration policies, such as TTL and TTI, are configured.

All <code>@Expiration</code>-based annotations apply only to Region entry values. Expiration for a Region is not covered by Spring Data for VMware GemFire's
expiration annotation support. However, GemFire and Spring Data for VMware GemFire
do let you set Region expiration by using the Spring Data for VMware GemFire XML
namespace, as follows:

```
<gfe:*-region id="Example" persistent="false">
  <gfe:region-ttl timeout="600" action="DESTROY"/>
  <gfe:region-tti timeout="300" action="INVALIDATE"/>
</gfe:*-region>
```

Spring Data for VMware GemFire's `@Expiration` annotation support is implemented with
GemFire's [CustomExpiry](https://geode.apache.org/releases/latest/javadoc/org/apache/geode/cache/CustomExpiry.html) interface. For more information, see [Configure Data Expiration](https://docs.vmware.com/en/VMware-Tanzu-GemFire/9.15/tgf/GUID-developing-expiration-configuring_data_expiration.html) in the GemFire product documentation.

The Spring Data for VMware GemFire `AnnotationBasedExpiration` class and `CustomExpiry`
implementation is responsible for processing the Spring Data for VMware GemFire
`@Expiration` annotations and applying the expiration policy
configuration appropriately for Region entry expiration on request.

To use Spring Data for VMware GemFire to configure specific GemFire Regions to
appropriately apply the expiration policy to your application domain
objects annotated with `@Expiration`-based annotations, you must:

1. Define a bean in the Spring `ApplicationContext` of type
    `AnnotationBasedExpiration` by using the appropriate constructor or
    one of the convenient factory methods. When configuring expiration
    for a specific expiration type, such as Idle Timeout (TTI) or
    Time-to-Live (TTL), you should use one of the factory methods in the
    `AnnotationBasedExpiration` class, as follows:
    ```highlight
    <bean id="ttlExpiration" class="org.springframework.data.gemfire.expiration.AnnotationBasedExpiration"
          factory-method="forTimeToLive"/>

    <gfe:partitioned-region id="Example" persistent="false">
        <gfe:custom-entry-ttl ref="ttlExpiration"/>
    </gfe:partitioned-region>
    ```
    <p class="note"><strong>Note</strong>: To configure Idle Timeout (TTI) Expiration instead, use the <code>forIdleTimeout</code> factory method along with the <code><gfe:custom-entry-tti ref="ttiExpiration"/></code> element to set TTI.</p>
    
2. (Optional) Annotate your application domain objects that are stored
    in the Region with expiration policies and custom settings by using
    one of Spring Data for VMware GemFire's `@Expiration` annotations: `@Expiration`,
    `@IdleTimeoutExpiration`, or `@TimeToLiveExpiration`

3. (Optional) In cases where particular application domain objects have
    not been annotated with Spring Data for VMware GemFire's `@Expiration` annotations at
    all, but the GemFire Region is configured to use
    Spring Data for VMware GemFire's custom `AnnotationBasedExpiration` class to
    determine the expiration policy and settings for objects stored in
    the Region, you can set "default" expiration attributes on the
    `AnnotationBasedExpiration` bean by doing the following:
    ```highlight
    <bean id="defaultExpirationAttributes" class="org.apache.geode.cache.ExpirationAttributes">
        <constructor-arg value="600"/>
        <constructor-arg value="#{T(org.apache.geode.cache.ExpirationAction).DESTROY}"/>
    </bean>
      
    <bean id="ttiExpiration" class="org.springframework.data.gemfire.expiration.AnnotationBasedExpiration"
      factory-method="forIdleTimeout">
        <constructor-arg ref="defaultExpirationAttributes"/>
    </bean>
      
    <gfe:partitioned-region id="Example" persistent="false">
        <gfe:custom-entry-tti ref="ttiExpiration"/>
    </gfe:partitioned-region>
    ```

You may have noticed that Spring Data for VMware GemFire's `@Expiration` annotations use a
`String` as the attribute type rather than, and perhaps more
appropriately, being strongly typed — for example, `int` for 'timeout'
and Spring Data for VMware GemFire's `ExpirationActionType` for 'action'. Why is that?

Well, enter one of Spring Data for VMware GemFire's other features, leveraging Spring's core
infrastructure for configuration convenience: property placeholders and
Spring Expression Language (SpEL) expressions.

For instance, a developer can specify both the expiration 'timeout' and
'action' by using property placeholders in the `@Expiration` annotation
attributes, as the following example shows:

```highlight
@TimeToLiveExpiration(timeout = "${geode.region.entry.expiration.ttl.timeout}"
    action = "${geode.region.entry.expiration.ttl.action}")
public class ExampleApplicationDomainObject {
  ...
}
```

Then, in your Spring XML config or in JavaConfig, you can declare the
following beans:

```highlight
<util:properties id="expirationSettings">
  <prop key="geode.region.entry.expiration.ttl.timeout">600</prop>
  <prop key="geode.region.entry.expiration.ttl.action">INVALIDATE</prop>
  ...
</util:properties>

<context:property-placeholder properties-ref="expirationProperties"/>
```

This is convenient both when multiple application domain objects might
share similar expiration policies and when you wish to externalize the
configuration.

However, you may want more dynamic expiration configuration determined
by the state of the running system. This is where the power of SpEL
comes into play and is the recommended approach, actually. Not only can
you refer to beans in the Spring container and access bean properties,
invoke methods, and so on, but the values for expiration 'timeout' and
'action' can be strongly typed. Consider the following example (which
builds on the preceding example):

```highlight
<util:properties id="expirationSettings">
  <prop key="geode.region.entry.expiration.ttl.timeout">600</prop>
  <prop key="geode.region.entry.expiration.ttl.action">#{T(org.springframework.data.gemfire.expiration.ExpirationActionType).DESTROY}</prop>
  <prop key="geode.region.entry.expiration.tti.action">#{T(org.apache.geode.cache.ExpirationAction).INVALIDATE}</prop>
  ...
</util:properties>

<context:property-placeholder properties-ref="expirationProperties"/>
```

Then, on your application domain object, you can define a timeout and an
action as follows:

```highlight
@TimeToLiveExpiration(timeout = "@expirationSettings['geode.region.entry.expiration.ttl.timeout']"
    action = "@expirationSetting['geode.region.entry.expiration.ttl.action']")
public class ExampleApplicationDomainObject {
  ...
}
```

You can imagine that the 'expirationSettings' bean could be a more
interesting and useful object than a simple instance of
`java.util.Properties`. In the preceding example, the `properties`
element (`expirationSettings`) uses SpEL to base the action value on the
actual `ExpirationAction` enumerated type, quickly leading to identified
failures if the enumerated type ever changes.

As an example, all of this has been demonstrated and tested in the
Spring Data for VMware GemFire test suite. For details, see the [Spring Data](https://github.com/spring-projects/spring-data-geode) repository in GitHub.

### <a id="data-persistence"></a>Data Persistence

Regions can be persistent. GemFire ensures that all the data
you put into a Region that is configured for persistence is written to
disk in a way that is recoverable the next time you recreate the Region.
Doing so lets data be recovered after machine or process failure or even
after an orderly shutdown and subsequent restart of the
GemFire data node.

To enable persistence with Spring Data for VMware GemFire, set the `persistent` attribute to
`true` on any of the `<*-region>` elements, as the following example
shows:

```highlight
<gfe:partitioned-region id="examplePersitentPartitionRegion" persistent="true"/>
```

Persistence may also be configured by setting the `data-policy`
attribute. To do so, set the attribute's value to one of
the [DataPolicy](https://geode.apache.org/releases/latest/javadoc/org/apache/geode/cache/DataPolicy.html) settings, as the following example shows:

```highlight
<gfe:partitioned-region id="anotherExamplePersistentPartitionRegion" data-policy="PERSISTENT_PARTITION"/>
```

The `DataPolicy` must match the Region type and must also agree with the
`persistent` attribute if it is also explicitly set. If the `persistent`
attribute is set to `false` but a persistent `DataPolicy` was specified
(such as `PERSISTENT_REPLICATE` or `PERSISTENT_PARTITION`), an
initialization exception is thrown.

For maximum efficiency when persisting Regions, you should configure the
storage through the `disk-store` element. The `DiskStore` is referenced
by using the `disk-store-ref` attribute. Additionally, the Region may
perform disk writes synchronously or asynchronously. The following
example shows a synchronous `DiskStore`:

```highlight
<gfe:partitioned-region id="yetAnotherExamplePersistentPartitionRegion" persistent="true"
    disk-store-ref="myDiskStore" disk-synchronous="true"/>
```

For more information, see [Configuring a DiskStore](#configuring-a-diskstore).

### <a id="subscription-policy"></a>Subscription Policy


GemFire allows configuration of [peer-to-peer
(P2P) event messaging](https://docs.vmware.com/en/VMware-Tanzu-GemFire/9.15/tgf/GUID-developing-events-configure_p2p_event_messaging.html) to control the entry events that the Region
receives. Spring Data for VMware GemFire provides the `<gfe:subscription/>` sub-element to
set the subscription policy on `REPLICATE` and `PARTITION` Regions to
either `ALL` or `CACHE_CONTENT`. The following example shows a region
with its subscription policy set to `CACHE_CONTENT`:

```highlight
<gfe:partitioned-region id="examplePartitionRegionWithCustomSubscription">
  <gfe:subscription type="CACHE_CONTENT"/>
</gfe:partitioned-region>
```

### <a id="local-region"></a>Local Region

Spring Data for VMware GemFire offers a dedicated `local-region` element for creating local
Regions. Local Regions, as the name implies, are standalone, meaning
that they do not share data with any other distributed system member.
Other than that, all common Region configuration options apply.

The following example shows a minimal declaration. The example
relies on the Spring Data for VMware GemFire XML namespace naming conventions to wire the
cache:

```highlight
<gfe:local-region id="exampleLocalRegion"/>
```

In the preceding example, a local Region is created if a Region by the
same name does not already exist. The name of the Region is the same as
the bean ID (`exampleLocalRegion`), and the bean assumes the existence
of a GemFire cache named `gemfireCache`.

### <a id="replicated-region"></a>Replicated Region

One of the common Region types is a `REPLICATE` Region or "replica". In
short, when a Region is configured to be a `REPLICATE`, every member
that hosts the Region stores a copy of the Region's entries locally. Any
update to a `REPLICATE` Region is distributed to all copies of the
Region. When a replica is created, it goes through an initialization
stage, in which it discovers other replicas and automatically copies all
the entries. While one replica is initializing, you can still continue
to use the other replicas.

All common configuration options are available for REPLICATE Regions.
Spring Data for VMware GemFire offers a `replicated-region` element. The following example
shows a minimal declaration:

```highlight
<gfe:replicated-region id="exampleReplica"/>
```

For more information, see [Distributed and Replicated Regions](https://docs.vmware.com/en/VMware-Tanzu-GemFire/9.15/tgf/GUID-developing-distributed_regions-chapter_overview.html) in the GemFire product documentation.

### <a id="partitioned-region"></a>Partitioned Region

The Spring Data for VMware GemFire XML namespace also supports `PARTITION` Regions.

A partitioned region is a region where data is divided between peer
servers hosting the region so that each peer stores a subset of the
data. When using a partitioned region, applications are presented with a
logical view of the region that looks like a single map containing all
of the data in the region. Reads or writes to this map are transparently
routed to the peer that hosts the entry that is the target of the
operation. GemFire divides the domain of hashcodes into
buckets. Each bucket is assigned to a specific peer, but may be
relocated at any time to another peer to improve the
utilization of resources across the cluster.

A `PARTITION` Region is created by using the `partitioned-region`
element. Its configuration options are similar to that of the
`replicated-region` with the addition of partition-specific features,
such as the number of redundant copies, total maximum memory, number of
buckets, partition resolver, etc.

The following example shows how to set up a `PARTITION` Region with two
redundant copies:

```highlight
<gfe:partitioned-region id="examplePartitionRegion" copies="2" total-buckets="17">
  <gfe:partition-resolver>
    <bean class="example.PartitionResolver"/>
  </gfe:partition-resolver>
</gfe:partitioned-region>
```

For more information, see [Partitioned Regions](https://docs.vmware.com/en/VMware-Tanzu-GemFire/9.15/tgf/GUID-developing-partitioned_regions-chapter_overview.html) in the GemFire product documentation.

#### Partitioned Region Attributes

The following table offers a quick overview of configuration options
specific to `PARTITION` Regions. These options are in addition to the
common Region configuration options described in [Configuring Regions](#configuring-regions).

<table>
  <caption>Table 3. partitioned-region attributes</caption>
  <colgroup>
    <col style="width: 20%" />
    <col style="width: 20%" />
    <col style="width: 60%" />
  </colgroup>
  <thead>
    <tr class="header">
      <th class="tableblock halign-left valign-top">Name</th>
      <th class="tableblock halign-left valign-top">Values</th>
      <th class="tableblock halign-left valign-top">Description</th>
    </tr>
  </thead>
  <tbody>
    <tr>
      <td>copies</td>
      <td>0..4</td>
      <td>The number of copies for each partition for high-availability. By default, no copies are created, meaning there is no redundancy. Each copy provides extra backup at the expense of extra storage.</td>
    </tr>
    <tr>
      <td>colocated-with</td>
      <td>valid region name</td>
      <td>The name of the <code>PARTITION</code> region with which this newly created <code>PARTITION</code> Region is collocated.</td>
    </tr>
    <tr>
      <td>local-max-memory</td>
      <td>positive integer</td>
      <td>The maximum amount of memory (in megabytes) used by the region in <strong>this</strong> process.</td>
    </tr>
    <tr>
      <td>total-max-memory</td>
      <td>any integer value</td>
      <td>The maximum amount of memory (in megabytes) used by the region in <strong>all</strong> processes.</td>
    </tr>
    <tr>
      <td>partition-listener</td>
      <td>bean name</td>
      <td>The name of the <code>PartitionListener</code> used by this region for handling partition events.</td>
    </tr>
    <tr>
      <td>partition-resolver</td>
      <td>bean name</td>
      <td>The name of the <code>PartitionResolver</code> used by this region for custom partitioning.</td>
    </tr>
    <tr>
      <td>recovery-delay</td>
      <td>any long value</td>
      <td>The delay in milliseconds that existing members wait before satisfying redundancy after another member crashes. <code>-1</code> (the default) indicates that redundancy is not recovered after a failure.</td>
    </tr>
    <tr>
      <td>startup-recovery-delay</td>
      <td>any long value</td>
      <td>The delay in milliseconds that new members wait before satisfying redundancy. <code>-1</code> indicates that adding new members does not trigger redundancy recovery. The default is to recover redundancy immediately when a new member is added.</td>
    </tr>
  </tbody>
</table>


### <a id="client-region"></a>Client Region

GemFire supports various deployment topologies for managing
and distributing data. The topic of GemFire topologies is
beyond the scope of this documentation, but GemFire's supported topologies can be classified as:

* peer-to-peer (p2p)
* client-server
* wide area network (WAN).

In the last two configurations, it is common to declare client Regions that connect to a cache server.

Spring Data for VMware GemFire offers dedicated support for each configuration through its
[client-cache](#configuring-gemfire-clientcache) elements: `client-region` and
`pool`. `client-region` defines a client Region,
and `pool` defines a Pool of connections used and shared by the
various client Regions.

The following example shows a typical client Region configuration:

```highlight
<bean id="myListener" class="example.CacheListener"/>
  
<!-- client Region using the default Spring Data for VMware GemFire gemfirePool Pool -->
<gfe:client-region id="Example">
  <gfe:cache-listener ref="myListener"/>
</gfe:client-region>
  
<!-- client Region using its own dedicated Pool -->
<gfe:client-region id="AnotherExample" pool-name="myPool">
  <gfe:cache-listener ref="myListener"/>
</gfe:client-region>
  
<!-- Pool definition -->
<gfe:pool id="myPool" subscription-enabled="true">
  <gfe:locator host="remoteHost" port="12345"/>
</gfe:pool>
```

As with the other Region types, `client-region` supports `CacheListener`
instances as well as a `CacheLoader` and a `CacheWriter`. It also
requires a connection `Pool` for connecting to a set of either Locators
or servers. Each client Region can have its own `Pool`, or they can
share the same one. If a Pool is not specified, then the "DEFAULT" Pool
will be used.

<p class="note"><strong>Note</strong>: In the preceding example, the <code>Pool</code> is
configured with a Locator. A Locator is a separate process used to
discover cache servers and peer data members in the distributed system
and is recommended for production systems. It is also possible to
configure the <code>Pool</code> to connect directly to one or more cache
servers by using the <code>server</code> element.</p>

For a full list of options to set on the client and especially on the
`Pool`, see the [Spring Data for VMware GemFire Schema](../appendix/appendix-schema.html) and
[Client/Server Configuration](https://docs.vmware.com/en/VMware-Tanzu-GemFire/9.15/tgf/GUID-topologies_and_comm-cs_configuration-chapter_overview.html) in the GemFire product documentation.

#### Client Interests

To minimize network traffic, each client can separately define its own
'interests' policies, indicating to GemFire the data it
actually requires. In Spring Data for VMware GemFire, 'interests' can be defined for each
client Region separately. Both key-based and regular expression-based
interest types are supported.

The following example shows both key-based and regular expression-based
`interest` types:

```highlight
<gfe:client-region id="Example" pool-name="myPool">
    <gfe:key-interest durable="true" result-policy="KEYS">
        <bean id="key" class="java.lang.String">
            <constructor-arg value="someKey"/>
        </bean>
    </gfe:key-interest>
    <gfe:regex-interest pattern=".*" receive-values="false"/>
</gfe:client-region>
```

A special key, `ALL_KEYS`, means that `interest` is registered for all keys.
The same can be accomplished by using the regular expression, `".\*"`.

The `<gfe:*-interest>` key and regular expression elements support three
attributes: `durable`, `receive-values`, and `result-policy`.

**durable**

`durable` indicates whether the `interest` policy and subscription queue
created for the client when the client connects to one or more servers
in the cluster is maintained across client sessions. If the client goes
away and comes back, a `durable` subscription queue on the servers for
the client is maintained while the client is disconnected. When the
client reconnects, the client receives any events that occurred while
the client was disconnected from the servers in the cluster.

A subscription queue on the servers in the cluster is maintained for
each `Pool` of connections defined in the client where a subscription
has also been "enabled" for that `Pool`. The subscription queue is used
to store (and possibly conflate) events sent to the client. If the
subscription queue is durable, it persists between client sessions (that
is, connections), potentially up to a specified timeout. If the client
does not return within a given time frame the client Pool subscription
queue is destroyed to reduce resource consumption on servers in
the cluster. If the subscription queue is not `durable`, it is destroyed
immediately when the client disconnects. You must decide whether your
client should receive events that came while it was disconnected or if
it needs to receive only the latest events after it reconnects.

**receive-values**

The `receive-values` attribute indicates whether or not the entry values
are received for create and update events. If `true`, values are
received. If `false`, only invalidation events are received.


**result-policy**

The `result-policy` is an enumeration of `KEYS`,
`KEYS_VALUE`, and `NONE`. The default is `KEYS_VALUES`. The
`result-policy` controls the initial dump when the client first connects
to initialize the local cache, essentially seeding the client with
events for all the entries that match the interest policy.

Client-side interest registration does not do much good without enabling
subscription on the `Pool`, as mentioned earlier. In fact, it is an
error to attempt interest registration without subscription enabled. The
following example shows how to do so:

```highlight
<gfe:pool ... subscription-enabled="true">
  ...
</gfe:pool>
```

In addition to `subscription-enabled`, can you also set
`subscription-ack-interval`, `subscription-message-tracking-timeout`,
and `subscription-redundancy`. `subscription-redundancy` is used to
control how many copies of the subscription queue should be maintained
by the servers in the cluster. If redundancy is greater than one, and
the "primary" subscription queue (that is, the server) goes down, then a
"secondary" subscription queue takes over, keeping the client from
missing events in a HA scenario.

In addition to the `Pool` settings, the server-side Regions use an
additional attribute, `enable-subscription-conflation`, to control the
conflation of events that are sent to the clients. This can also help
further minimize network traffic and is useful in situations where the
application only cares about the latest value of an entry. However, when
the application keeps a time series of events that occurred, conflation
is going to hinder that use case. The default value is `false`. The
following example shows a Region configuration on the server, for which
the client contains a corresponding client `[CACHING_]PROXY` Region with
interests in keys in this server Region:

```highlight
<gfe:partitioned-region name="ServerSideRegion" enable-subscription-conflation="true">
  ...
</gfe:partitioned-region>
```

To control the amount of time (in seconds) that a "durable" subscription
queue is maintained after a client is disconnected from the servers in
the cluster, set the `durable-client-timeout` attribute on the
`<gfe:client-cache>` element as follows:


```highlight
<gfe:client-cache durable-client-timeout="600">
  ...
</gfe:client-cache>
```

For more information, see [Client-to-Server
Event Distribution](https://docs.vmware.com/en/VMware-Tanzu-GemFire/9.15/tgf/GUID-developing-events-how_client_server_distribution_works.html) in the GemFire product documentation.

### <a id="json-support"></a>JSON Support

GemFire has support for caching JSON documents in Regions,
along with the ability to query stored JSON documents using the
GemFire OQL (Object Query Language). JSON documents are stored
internally as
[PdxInstance](https://geode.apache.org/releases/latest/javadoc/org/apache/geode/pdx/PdxInstance.html) types by using the [JSONFormatter](https://geode.apache.org/releases/latest/javadoc/org/apache/geode/pdx/JSONFormatter.html) class to perform conversion to and from JSON documents (as a `String`).

Spring Data for VMware GemFire provides the `<gfe-data:json-region-autoproxy/>` element to
enable an [AOP](https://docs.spring.io/spring-framework/docs/current/reference/html/#aop-introduction) component to
advise appropriate, proxied Region operations, which encapsulates the `JSONFormatter` and allows your applications to work directly with JSON Strings.

In addition, Java objects written to JSON configured Regions are
automatically converted to JSON using Jackson's `ObjectMapper`. When
these values are read back, they are returned as a JSON String.

By default, `<gfe-data:json-region-autoproxy/>` performs the conversion
for all Regions. To apply this feature to selected Regions, provide a
comma-delimited list of Region bean IDs in the `region-refs` attribute.
Other attributes include a `pretty-print` flag (defaults to `false`) and
`convert-returned-collections`.

Also, by default, the results of the `getAll()` and `values()` Region
operations are converted for configured Regions. This is done by
creating a parallel data structure in local memory. This can incur
significant overhead for large collections, so set the
`convert-returned-collections` to `false` if you want to disable
automatic conversion for these Region operations.

<p class="note"><strong>Note</strong>: Certain Region operations that
use GemFire's proprietary <code>Region.Entry</code>, such as 
<code>entries(boolean)</code>, <code>entrySet(boolean)</code>, and
<code>getEntry()</code> type, are not targeted for AOP advice. Additionally, , the <code>entrySet()</code> method, which returns a
<code>Set&lt;java.util.Map.Entry&lt;?, ?&gt;&gt;</code>, is not
affected.</p>

The following example configuration shows how to set the `pretty-print`
and `convert-returned-collections` attributes:

```highlight
<gfe-data:json-region-autoproxy region-refs="myJsonRegion" pretty-print="true" convert-returned-collections="false"/>
```

This feature also works seamlessly with `GemfireTemplate` operations,
provided that the template is declared as a Spring bean. The
native `QueryService` operations are not supported.

## <a id="configuring-an-index"></a>Configuring an Index

GemFire allows indexes to be created on Region data to improve the performance of OQL (Object
Query Language) queries.

In Spring Data for VMware GemFire, indexes are declared with the `index` element, as the
following example shows:

```highlight
<gfe:index id="myIndex" expression="someField" from="/SomeRegion" type="HASH"/>
```

In Spring Data for VMware GemFire's XML schema, the Spring Data for VMware GemFire XML
namespace, `index` bean declarations are not bound to a Region, unlike
GemFire's native `cache.xml`. Instead, they are top-level
elements similar to `<gfe:cache>` element. This allows you to declare any
number of indexes on any Region, whether they were just created or
already exist.

An `Index` must have a name. You can give the `Index` an explicit name
by using the `name` attribute. Otherwise, the bean name of the `index` bean definition is used as
the `Index` name.

The `expression` and `from` clause form the main components of an
`Index`, identifying the data to index along with what criteria (`expression`)
is used to index the data. The `expression` should be based on what
application domain object fields are used in the predicate of
application-defined OQL queries used to query and look up the objects
stored in the Region.

Consider the following example, which has a `lastName` property:

```highlight
@Region("Customers")
class Customer {

  @Id
  Long id;

  String lastName;
  String firstName;

  ...
}
```

Now consider the following example, which has an application-defined
Spring Data for VMware GemFire Repository to query for `Customer` objects:

```highlight
interface CustomerRepository extends GemfireRepository<Customer, Long> {
  
  Customer findByLastName(String lastName);
  
  ...
}
```


The Spring Data for VMware GemFire Repository finder/query method results in the
following OQL statement being generated and run:

```highlight
SELECT * FROM /Customers c WHERE c.lastName = '$1'
```

Therefore, you might want to create an `Index` with a statement similar
to the following:

```highlight
<gfe:index id="myIndex" name="CustomersLastNameIndex" expression="lastName" from="/Customers" type="HASH"/>
```

The `from` clause must refer to a valid, existing Region and is how an
`Index` gets applied to a Region. This is not specific to Spring Data for VMware GemFire. It
is a feature of GemFire.

The `Index` `type` may be one of three enumerated values defined by the [IndexType](https://docs.spring.io/spring-data/geode/docs/current/api/org/springframework/data/gemfire/IndexType.html) enumeration: `FUNCTIONAL`, `HASH`, and `PRIMARY_KEY`.

Each of the enumerated values corresponds to one of the
[QueryService](https://geode.apache.org/releases/latest/javadoc/org/apache/geode/cache/query/QueryService.html) `create[|Key|Hash]Index` methods invoked when the actual `Index` is to
be created or defined. For example, if the `IndexType` is `PRIMARY_KEY`,
then the [QueryService.createKeyIndex(..)](https://geode.apache.org/releases/latest/javadoc/org/apache/geode/cache/query/QueryService.html#createKeyIndex-java.lang.String-java.lang.String-java.lang.String-)
is invoked to create a `KEY` `Index`.

The default is `FUNCTIONAL` and results in one of the
`QueryService.createIndex(..)` methods being invoked. See the Spring Data for VMware GemFire
XML schema for a full set of options.

For more information about indexing in GemFire, see [Working
with
Indexes](https://docs.vmware.com/en/VMware-Tanzu-GemFire/9.15/tgf/GUID-developing-query_index-query_index.html) in the GemFire product documentation.

### <a id="defining-indexes"></a>Defining Indexes

In addition to creating indexes as `Index` bean definitions are
processed by Spring Data for VMware GemFire on Spring container initialization, you may also
define all of your application indexes prior to creating them by using
the `define` attribute, as follows:

```highlight
<gfe:index id="myDefinedIndex" expression="someField" from="/SomeRegion" define="true"/>
```

When `define` is set to `true`, it does not immediately create the `Index`. All "defined" Indexes are created at the same time when the Spring `ApplicationContext`. This occurs when a `ContextRefreshedEvent` is published by the Spring container. Spring Data for VMware GemFire registers itself as an `ApplicationListener` listening for the `ContextRefreshedEvent`. When
fired, Spring Data for VMware GemFire calls
[QueryService.createDefinedIndexes()](https://geode.apache.org/releases/latest/javadoc/org/apache/geode/cache/query/QueryService.html#createDefinedIndexes).

Defining indexes and creating them at the same time boosts speed and
efficiency when creating indexes.

For more information, see [Creating Multiple Indexes at Once](https://docs.vmware.com/en/VMware-Tanzu-GemFire/9.15/tgf/GUID-developing-query_index-create_multiple_indexes.html) in the GemFire product documentation.

### <a id="ignoreiexists-and-override"></a>`IgnoreIfExists` and `Override`

The `ignoreIfExists` and `override` configuration options correspond to the `ignore-if-exists` and `override` attributes on the `<gfe:index>` element in Spring Data for VMware GemFire's XML namespace.

<p class="note warning"><strong>Warning</strong>: These options can affect the
performance and resources such as memory consumed by your application
at runtime. As a result, both of these options are disabled (set to
<code>false</code>) in Spring Data for VMware GemFire by default.<br>br>These options are only available in Spring Data for VMware GemFire and exist to workaround known limitations with GemFire. GemFire has no equivalent options or functionality.</p>

Each option significantly differs in behavior and entirely depends on
the type of GemFire `Index` exception thrown. This also means
that neither option has any effect if a GemFire Index-type
exception is not thrown. These options are meant to specifically handle
GemFire `IndexExistsException` and
`IndexNameConflictException` instances, which can occur for various reasons. The exceptions have the following causes:

* An [IndexExistsException](https://geode.apache.org/releases/latest/javadoc/org/apache/geode/cache/query/IndexExistsException.html) is thrown when there exists another `Index` with the same definition
  but a different name when attempting to create an `Index`.

* An [IndexNameConflictException](https://geode.apache.org/releases/latest/javadoc/org/apache/geode/cache/query/IndexNameConflictException.html) is thrown when there exists another `Index` with the same name but possibly different definition when attempting to create an `Index`.

Spring Data for VMware GemFire's default behavior is fail-fast strategy. Neither of the above `Index` exceptions are handled by default. These `Index` exceptions are wrapped in a Spring Data for VMware GemFire `GemfireIndexException` and rethrown. If you want Spring Data for VMware GemFire to handle them for you, you can set either of
these `Index` bean definition options to `true`.

`IgnoreIfExists` always takes precedence over `Override` because it uses fewer resources. It returns the "existing" `Index` in both exception cases.

#### `IgnoreIfExists` Behavior

When an `IndexExistsException` is thrown and `ignoreIfExists` is set to
`true`, or `<gfe:index ignore-if-exists="true">`, then the `Index` that
would have been created by this `index` bean definition or declaration
is ignored, and the existing `Index` is returned.

There is little consequence in returning the existing `Index`, since the
`index` bean definition is the same, as determined by GemFire
itself, not Spring Data for VMware GemFire.

However, this also means that no `Index` with the "name" specified in
your `index` bean definition or declaration actually exists from
GemFire's perspective.
Therefore, you should be careful when writing OQL query statements that
use query hints, especially query hints that refer to the application
`Index` being ignored. Those query hints must be changed.

When an `IndexNameConflictException` is thrown and `ignoreIfExists` is
set to `true`, or `<gfe:index ignore-if-exists="true">`, the `Index`
that would have been created by this `index` bean definition or
declaration is also ignored, and the "existing" `Index` is again
returned, as when an `IndexExistsException` is thrown.

However, there is more risk in returning the existing `Index` and
ignoring the application's definition of the `Index` when an
`IndexNameConflictException` is thrown. For a
`IndexNameConflictException`, while the names of the conflicting indexes
are the same, the definitions could be different. This situation could
have implications for OQL queries specific to the application, where you
would presume the indexes were defined specifically with the application
data access patterns and queries in mind. However, if like-named indexes
differ in definition, this might not be the case. Consequently, you
should verify your `Index` names.

<p class="note"><strong>Note</strong>: Spring Data for VMware GemFire makes a best effort to inform the user
when the <code>Index</code> being ignored is significantly different in
its definition from the existing <code>Index</code>. However, for Spring Data for VMware GemFire to accomplish this, it must be able to find the existing <code>Index</code>, which is found using the
GemFire API.</p>

#### `Override` Behavior

When an `IndexExistsException` is thrown and `override` is set to `true`, or `<gfe:index override="true">`, the `Index` is effectively renamed.
`IndexExistsExceptions` are thrown when multiple indexes exist
that have the same definition but different names.

Spring Data for VMware GemFire can only accomplish this by using GemFire's API, by
first removing the existing `Index` and then recreating the `Index` with
the new name. It is possible that either the remove or subsequent create
invocation could fail. There is no way to execute both actions
atomically and rollback this joint operation if either fails.

However, if it succeeds, then you have the same problem as before with
the `ignoreIfExists` option. Any existing OQL query statement using
query hints that refer to the old `Index` by name must be changed.

When an `IndexNameConflictException` is thrown and `override` is set to
`true`, or `<gfe:index override="true">`, the existing `Index` can
potentially be re-defined. We say "potentially" because it is possible
for the like-named, existing `Index` to have exactly the same definition
and name when an `IndexNameConflictException` is thrown.

If so, Spring Data for VMware GemFire returns the existing `Index` as is,
even on `override`. There is no harm in this behavior, since both the
name and the definition are exactly the same. Of course, Spring Data for VMware GemFire
can only accomplish this when Spring Data for VMware GemFire is able to find the existing
`Index`, which is dependent on GemFire's APIs. If it cannot be
found, nothing happens and a Spring Data for VMware GemFire `GemfireIndexException` is
thrown that wraps the `IndexNameConflictException`.

However, when the definition of the existing `Index` is different,
Spring Data for VMware GemFire attempts to re-create the `Index` by using the `Index`
definition specified in the `index` bean definition. Make sure that this is
intended and that the `index` bean definition matches your
expectations and application requirements.

#### How Does `IndexNameConflictExceptions` Actually Happen?

It is probably not all that uncommon for `IndexExistsExceptions` to be
thrown, especially when multiple configuration sources are used to
configure GemFire (Spring Data for VMware GemFire, GemFire Cluster
Config, GemFire native `cache.xml`, the API, and so on). You
should definitely prefer one configuration method and stick with it.

However, when does an `IndexNameConflictException` get thrown?

One particular case is an `Index` defined on a `PARTITION` Region (PR).
When an `Index` is defined on a `PARTITION` Region (for example, `X`),
GemFire distributes the `Index` definition (and name) to other
peer members in the cluster that also host the same `PARTITION` Region
(that is, "X"). The distribution of this `Index` definition to, and
subsequent creation of, this `Index` by peer members is on a
need-to-know basis (that is, by peer member hosting the same PR) is
performed asynchronously.

During this window of time, it is possible that these pending PR
`Indexes` cannot be identified by GemFire. As a result, the only way for Spring Data for VMware GemFire or other GemFire
cache client applications (not involving Spring) to know for sure is to
attempt to create the `Index`. If it fails with either an
`IndexNameConflictException` or even an `IndexExistsException`, the
application knows there is a problem. This is because the `QueryService`
`Index` creation waits on pending `Index` definitions, whereas the other
GemFire API calls do not.

In any case, Spring Data for VMware GemFire makes a best effort and attempts to inform
you what has happened or is happening and tell you the corrective
action. Given that all GemFire `QueryService.createIndex(..)`
methods are synchronous, blocking operations, the state of
GemFire should be consistent and accessible after either of
these index-type exceptions are thrown. Consequently, Spring Data for VMware GemFire can
inspect the state of the system and act accordingly, based on your
configuration.

In all other cases, Spring Data for VMware GemFire embraces a fail-fast strategy.

## <a id="configuring-a-diskstore"></a>Configuring a DiskStore

Spring Data for VMware GemFire supports `DiskStore` configuration and creation through the
`disk-store` element, as the following example shows:

```highlight
<gfe:disk-store id="Example" auto-compact="true" max-oplog-size="10"
                queue-size="50" time-interval="9999">
    <gfe:disk-dir location="/disk/location/one" max-size="20"/>
    <gfe:disk-dir location="/disk/location/two" max-size="20"/>
</gfe:disk-store>
```

`DiskStore` instances are used by Regions for file system persistent
backup and overflow of evicted entries as well as persistent backup for
WAN Gateways. Multiple GemFire components may share the same
`DiskStore`. Additionally, multiple file system directories may be
defined for a single `DiskStore`, as shown in the preceding example.

For an explanation of persistence, overflow, and configuration options on `DiskStore` instances, see [Persistence and Overflow](https://docs.vmware.com/en/VMware-Tanzu-GemFire/9.15/tgf/GUID-developing-storing_data_on_disk-chapter_overview.html) in the GemFire product documentation.

## <a id="configuring-snapshot-service"></a>Configuring the Snapshot Service

Spring Data for VMware GemFire supports cache and Region snapshots by using
GemFire's Snapshot Service. The out-of-the-box Snapshot Service support offers
several convenient features to simplify the use of GemFire's [Cache](https://geode.apache.org/releases/latest/javadoc/org/apache/geode/cache/snapshot/CacheSnapshotService.html) and [Region](https://geode.apache.org/releases/latest/javadoc/org/apache/geode/cache/snapshot/RegionSnapshotService.html) Snapshot Service APIs. For more information about the Snapshot Service, see [Cache and Region Snapshots](https://docs.vmware.com/en/VMware-Tanzu-GemFire/9.15/tgf/GUID-managing-cache_snapshots-chapter_overview.html)  in the GemFire product documentation.

Snapshots let you save and subsequently reload
the cached data later, which can be useful for moving data between
environments, such as from production to a staging or test environment
to reproduce data-related issues in a controlled context. You
can combine Spring Data for VMware GemFire's Snapshot Service support with [Spring's bean
definition
profiles](https://docs.spring.io/spring/docs/current/spring-framework-reference/htmlsingle/#beans-definition-profiles)
to load snapshot data specific to the environment as necessary.

Spring Data for VMware GemFire's support for GemFire's Snapshot Service begins
with the `<gfe-data:snapshot-service>` element from the `<gfe-data>` XML
namespace.

For example, you can define cache-wide snapshots to be loaded as well as
saved by using a couple of snapshot imports and a data export
definition, as follows:

```highlight
<gfe-data:snapshot-service id="gemfireCacheSnapshotService">
  <gfe-data:snapshot-import location="/absolute/filesystem/path/to/import/fileOne.snapshot"/>
  <gfe-data:snapshot-import location="relative/filesystem/path/to/import/fileTwo.snapshot"/>
  <gfe-data:snapshot-export
      location="/absolute/or/relative/filesystem/path/to/export/directory"/>
</gfe-data:snapshot-service>
```

You can define as many imports and exports as you like. You can define
only imports or only exports. The file locations and directory paths can
be absolute or relative to the Spring Data for VMware GemFire application, which is the JVM
process's working directory.

The preceding example is pretty simple, and the Snapshot Service defined
in this case refers to the GemFire cache instance with the
default name of `gemfireCache` (as described in [Configuring a
Cache](#configuring-cache)). If you name your cache bean definition
something other than the default, you can use the `cache-ref` attribute
to refer to the cache bean by name, as follows:

```highlight
<gfe:cache id="myCache"/>
...
<gfe-data:snapshot-service id="mySnapshotService" cache-ref="myCache">
  ...
</gfe-data:snapshot-service>
```

You can also define a Snapshot Service for a particular Region by
specifying the `region-ref` attribute, as follows:

```highlight
<gfe:partitioned-region id="Example" persistent="false" .../>
...
<gfe-data:snapshot-service id="gemfireCacheRegionSnapshotService" region-ref="Example">
  <gfe-data:snapshot-import location="relative/path/to/import/example.snapshot/>
  <gfe-data:snapshot-export location="/absolute/path/to/export/example.snapshot/>
</gfe-data:snapshot-service>
```

When the `region-ref` attribute is specified, Spring Data for VMware GemFire's
`SnapshotServiceFactoryBean` resolves the `region-ref` attribute value
to a Region bean defined in the Spring container and creates a
[RegionSnapshotService](https://geode.apache.org/releases/latest/javadoc/org/apache/geode/cache/snapshot/RegionSnapshotService.html).
The snapshot import and export definitions function the same way.
However, the `location` must refer to a file on an export.

<p class="note"><strong>Note</strong>: GemFire is strict about imported snapshot
files actually existing before they are referenced. For exports,
GemFire creates the snapshot file. If the snapshot file for
export already exists, the data is overwritten.</p>

Spring Data for VMware GemFire includes a <code>suppress-import-on-init</code> attribute on the
<code><gfe-data:snapshot-service></code> element to suppress the
configured Snapshot Service from trying to import data into the cache or
Region on initialization. Doing so is useful, for example, when data
exported from one Region is used to feed the import of another
Region.

### <a id="snapshot-location"></a>Snapshot Location

With the cache-based Snapshot Service (that is, a [CacheSnapshotService](https://geode.apache.org/releases/latest/javadoc/org/apache/geode/cache/snapshot/CacheSnapshotService.html))
you would typically pass it a directory containing all the snapshot
files to load rather than individual snapshot files, as the overloaded
[load](https://geode.apache.org/releases/latest/javadoc/org/apache/geode/cache/snapshot/CacheSnapshotService.html#load-java.io.File-org.apache.geode.cache.snapshot.SnapshotOptions.SnapshotFormat)
method in the `CacheSnapshotService` API indicates.

You can also use the overloaded <code>load(:File[], :SnapshotFormat, :SnapshotOptions)</code> method to specify which snapshot files to load into the GemFire cache.


However, Spring Data for VMware GemFire recognizes that a typical developer workflow might
be to extract and export data from one environment into several snapshot
files, zip all of them up, and then conveniently move the zip file to
another environment for import.

Therefore, Spring Data for VMware GemFire lets you specify a jar or zip file on import for a
`cache`-based Snapshot Service, as follows:

```highlight
  <gfe-data:snapshot-service id="cacheBasedSnapshotService" cache-ref="gemfireCache">
    <gfe-data:snapshot-import location="/path/to/snapshots.zip"/>
  </gfe-data:snapshot-service>
```

Spring Data for VMware GemFire conveniently extracts the provided zip file and treats it as
a directory import (load).

### <a id="snapshot-filters"></a>Snapshot Filters


The real power of defining multiple snapshot imports and exports is
realized through the use of snapshot filters. Snapshot filters implement the 
[SnapshotFilter](https://geode.apache.org/releases/latest/javadoc/org/apache/geode/cache/snapshot/SnapshotFilter.html)
interface and are used to filter Region entries for inclusion into the
Region on import and for inclusion into the snapshot on export.

Spring Data for VMware GemFire lets you use snapshot filters on import and export by using
the `filter-ref` attribute or an anonymous, nested bean definition, as
the following example shows:

```highlight
<gfe:cache/>

<gfe:partitioned-region id="Admins" persistent="false"/>
<gfe:partitioned-region id="Guests" persistent="false"/>

<bean id="activeUsersFilter" class="example.gemfire.snapshot.filter.ActiveUsersFilter/>

<gfe-data:snapshot-service id="adminsSnapshotService" region-ref="Admins">
  <gfe-data:snapshot-import location="/path/to/import/users.snapshot">
    <bean class="example.gemfire.snapshot.filter.AdminsFilter/>
  </gfe-data:snapshot-import>
  <gfe-data:snapshot-export location="/path/to/export/active/admins.snapshot" filter-ref="activeUsersFilter"/>
</gfe-data:snapshot-service>

<gfe-data:snapshot-service id="guestsSnapshotService" region-ref="Guests">
  <gfe-data:snapshot-import location="/path/to/import/users.snapshot">
    <bean class="example.gemfire.snapshot.filter.GuestsFilter/>
  </gfe-data:snapshot-import>
  <gfe-data:snapshot-export location="/path/to/export/active/guests.snapshot" filter-ref="activeUsersFilter"/>
</gfe-data:snapshot-service>
```

Additionally, you can express more complex snapshot filters by using the
`ComposableSnapshotFilter` class. This class implements
the [SnapshotFilter](https://geode.apache.org/releases/latest/javadoc/org/apache/geode/cache/snapshot/SnapshotFilter.html)
interface as well as the
[Composite](https://en.wikipedia.org/wiki/Composite_pattern) software
design pattern.

The Composite software
design pattern lets you compose multiple objects of the same type and
treat the aggregate as single instance of the object type.

`ComposableSnapshotFilter` has two factory methods, `and` and `or`. They
let you logically combine individual snapshot filters using the AND and
OR logical operators, respectively. The factory methods take a list of
`SnapshotFilters`.

The following example shows a definition for a
`ComposableSnapshotFilter`:

```highlight
<bean id="activeUsersSinceFilter" class="org.springframework.data.gemfire.snapshot.filter.ComposableSnapshotFilter"
      factory-method="and">
  <constructor-arg index="0">
    <list>
      <bean class="org.example.app.gemfire.snapshot.filter.ActiveUsersFilter"/>
      <bean class="org.example.app.gemfire.snapshot.filter.UsersSinceFilter"
            p:since="2015-01-01"/>
    </list>
  </constructor-arg>
</bean>
```

You could then combine the `activesUsersSinceFilter` with
another filter by using `or`, as follows:

```highlight
<bean id="covertOrActiveUsersSinceFilter" class="org.springframework.data.gemfire.snapshot.filter.ComposableSnapshotFilter"
      factory-method="or">
  <constructor-arg index="0">
    <list>
      <ref bean="activeUsersSinceFilter"/>
      <bean class="example.gemfire.snapshot.filter.CovertUsersFilter"/>
    </list>
  </constructor-arg>
</bean>
```

### <a id="snapshot-events"></a>Snapshot Events

By default, Spring Data for VMware GemFire uses GemFire's Snapshot Services on
startup to import data and on shutdown to export data. However, you may
want to trigger periodic, event-based snapshots, for either import or
export, from within your Spring application.

For this purpose, Spring Data for VMware GemFire defines two additional Spring application
events, extending Spring's
[ApplicationEvent](https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/context/ApplicationEvent.html) class for imports and exports, respectively:
`ImportSnapshotApplicationEvent` and `ExportSnapshotApplicationEvent`.

The two application events can be targeted for the entire
GemFire cache or for individual GemFire Regions. The
constructors in these classes accept an optional Region pathname (such
as `/Example`) as well as zero or more `SnapshotMetadata` instances.

The array of `SnapshotMetadata` overrides the snapshot metadata defined
by `<gfe-data:snapshot-import>` and `<gfe-data:snapshot-export>`
sub-elements, which are used in cases where snapshot application events
do not explicitly provide `SnapshotMetadata`. Each individual
`SnapshotMetadata` instance can define its own `location` and `filters`
properties.

All snapshot service beans defined in the Spring `ApplicationContext`
receive import and export snapshot application events. However, only
matching Snapshot Service beans process import and export events.

A Region-based `[Import|Export]SnapshotApplicationEvent` matches if the
Snapshot Service bean defined is a `RegionSnapshotService` and its
Region reference (as determined by the `region-ref` attribute) matches
the Region's pathname, as specified by the snapshot application event.

A Cache-based `[Import|Export]SnapshotApplicationEvent` (that is, a
snapshot application event without a Region pathname) triggers all
Snapshot Service beans, including any `RegionSnapshotService` beans, to
perform either an import or export, respectively.

You can use Spring's [ApplicationEventPublisher](https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/context/ApplicationEventPublisher.html) interface to fire import and export snapshot application events from your application as follows:

```highlight
@Component
public class ExampleApplicationComponent {
  
  @Autowired
  private ApplicationEventPublisher eventPublisher;
  
  @Resource(name = "Example")
  private Region<?, ?> example;
  
  public void someMethod() {
  
    ...
  
    File dataSnapshot = new File(System.getProperty("user.dir"), "/path/to/export/data.snapshot");
  
    SnapshotFilter myFilter = ...;
  
    SnapshotMetadata exportSnapshotMetadata =
        new SnapshotMetadata(dataSnapshot, myFilter, null);
  
    ExportSnapshotApplicationEvent exportSnapshotEvent =
        new ExportSnapshotApplicationEvent(this, example.getFullPath(), exportSnapshotMetadata)
  
    eventPublisher.publishEvent(exportSnapshotEvent);
  
    ...
  }
}
```

In the preceding example, only the `/Example` Region's Snapshot Service
bean picks up and handles the export event, saving the filtered,
"/Example" Region's data to the `data.snapshot` file in a sub-directory
of the application's working directory.

Using the Spring application events and messaging subsystem is a good
way to keep your application loosely coupled. You can also use Spring's
[Scheduling](https://docs.spring.io/spring-framework/docs/current/reference/html/#scheduling-task-scheduler) services to fire snapshot application events on a periodic basis.

## <a id="configuring-function-service"></a>Configuring the Function Service

Spring Data for VMware GemFire provides [annotation](#function-annotations.html) support for
implementing, registering and executing GemFire Functions.

Spring Data for VMware GemFire also provides XML namespace support for registering
GemFire [Functions](https://geode.apache.org/releases/latest/javadoc/org/apache/geode/cache/execute/Function.html) for remote function execution.

For more information about the Function execution framework, see [Function Execution](https://docs.vmware.com/en/VMware-Tanzu-GemFire/9.15/tgf/GUID-developing-function_exec-chapter_overview.html) in the GemFire product documentation.

GemFire Functions are declared as Spring beans and must
implement the `org.apache.geode.cache.execute.Function` interface or
extend `org.apache.geode.cache.execute.FunctionAdapter`.

The namespace uses a familiar pattern to declare Functions, as the
following example shows:

```highlight
<gfe:function-service>
  <gfe:function>
      <bean class="example.FunctionOne"/>
      <ref bean="function2"/>
  </gfe:function>
</gfe:function-service>

<bean id="function2" class="example.FunctionTwo"/>
```

## <a id="configuring-wan-gateways"></a>Configuring WAN Gateways

WAN Gateways provides a way to synchronize GemFire Distributed
Systems across geographic locations. Spring Data for VMware GemFire provides XML namespace
support for configuring WAN Gateways as illustrated in the following
examples.

### <a id="wan-gateway-configuration-7"></a>WAN Configuration in GemFire 7.0

In the following example, `GatewaySenders` are configured for a
`PARTITION` Region by adding child elements (`gateway-sender` and
`gateway-sender-ref`) to the Region. A `GatewaySender` may register
`EventFilters` and `TransportFilters`.

The following example also shows a sample configuration of an
`AsyncEventQueue`, which must also be auto-wired into a Region (not
shown):

```highlight
<gfe:partitioned-region id="region-with-inner-gateway-sender" >
    <gfe:gateway-sender remote-distributed-system-id="1">
        <gfe:event-filter>
            <bean class="org.springframework.data.gemfire.example.SomeEventFilter"/>
        </gfe:event-filter>
        <gfe:transport-filter>
            <bean class="org.springframework.data.gemfire.example.SomeTransportFilter"/>
        </gfe:transport-filter>
    </gfe:gateway-sender>
    <gfe:gateway-sender-ref bean="gateway-sender"/>
</gfe:partitioned-region>

<gfe:async-event-queue id="async-event-queue" batch-size="10" persistent="true" disk-store-ref="diskstore"
        maximum-queue-memory="50">
    <gfe:async-event-listener>
        <bean class="example.AsyncEventListener"/>
    </gfe:async-event-listener>
</gfe:async-event-queue>

<gfe:gateway-sender id="gateway-sender" remote-distributed-system-id="2">
    <gfe:event-filter>
        <ref bean="event-filter"/>
        <bean class="org.springframework.data.gemfire.example.SomeEventFilter"/>
    </gfe:event-filter>
    <gfe:transport-filter>
        <ref bean="transport-filter"/>
        <bean class="org.springframework.data.gemfire.example.SomeTransportFilter"/>
    </gfe:transport-filter>
</gfe:gateway-sender>
  
<bean id="event-filter" class="org.springframework.data.gemfire.example.AnotherEventFilter"/>
<bean id="transport-filter" class="org.springframework.data.gemfire.example.AnotherTransportFilter"/>
```

On the other end of a `GatewaySender` is a corresponding
`GatewayReceiver` to receive Gateway events. The `GatewayReceiver` may
also be configured with `EventFilters` and `TransportFilters`, as
follows:

```highlight
<gfe:gateway-receiver id="gateway-receiver" start-port="12345" end-port="23456" bind-address="192.168.0.1">
    <gfe:transport-filter>
        <bean class="org.springframework.data.gemfire.example.SomeTransportFilter"/>
    </gfe:transport-filter>
</gfe:gateway-receiver>
```

For a detailed explanation of the configuration options. see [Multi-site (WAN) Configuration](https://docs.vmware.com/en/VMware-Tanzu-GemFire/9.15/tgf/GUID-topologies_and_comm-multi_site_configuration-chapter_overview.html) in the GemFire product documentation.
