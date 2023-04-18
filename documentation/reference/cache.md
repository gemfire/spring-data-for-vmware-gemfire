---
title: Configuring a Cache
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

To use [vmware-gemfire-short-name], you must either create a new cache or
connect to an existing one. With the current version of
[vmware-gemfire-short-name], you can have only one open cache per VM (more
strictly speaking, per `ClassLoader`). In most cases, the cache should
only be created once.

<p class="note"><strong>Note</strong>: This section of this topic describes the creation and
configuration of a peer <code>Cache</code> member, appropriate in
peer-to-peer (P2P) topologies and cache servers. A <code>Cache</code>
member can also be used in stand-alone applications and integration
tests. However, in typical production systems, most application
processes act as cache clients, creating a <code>ClientCache</code>
instance instead. This is described in the <a
href="#configuring-gemfire-clientcache">Configuring a [vmware-gemfire-short-name]
ClientCache</a> and <a href="#client-region">Client Region</a>
sections.</p>

You can create a peer `Cache` with default configuration with the
following declaration:

```highlight
<gfe:cache/>
```

During Spring container initialization, any `ApplicationContext`
containing this cache definition registers a `CacheFactoryBean` that
creates a Spring bean named `gemfireCache`, which references a
[vmware-gemfire-short-name] `Cache` instance. This bean refers to either an
existing `Cache` or, if one does not already exist, a newly created one.
Since no additional properties were specified, a newly created `Cache`
applies the default cache configuration.

All [spring-data-gemfire-name] components that depend on the `Cache` respect this naming
convention, so you need not explicitly declare the `Cache` dependency.
If you prefer, you can make the dependency explicit by using the
`cache-ref` attribute provided by various [spring-data-gemfire-name] XML namespace
elements. Also, you can override the cache's bean name using the `id`
attribute, as follows:

```highlight
<gfe:cache id="myCache"/>
```

A [vmware-gemfire-short-name] `Cache` can be fully configured using Spring.
However, [vmware-gemfire-short-name]'s native XML configuration file, `cache.xml`,
is also supported. For situations where the [vmware-gemfire-short-name] cache
must be configured natively, you can provide a reference to the
[vmware-gemfire-short-name] XML configuration file by using the
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
also specify [vmware-gemfire-short-name] System [properties](https://docs.vmware.com/en/VMware-GemFire/9.15/gf/reference-topics-gemfire_properties.html)
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

## <a id="advanced-cache-configuration"></a>Advanced Cache Configuration

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
    regarding anything shown in this example, see the [[vmware-gemfire-short-name]
    product documentation](https://docs.vmware.com/en/VMware-GemFire/). The
    `close` attribute determines whether the cache should be closed when
    the Spring application context is closed. The default is `true`.
    However, for use cases in which multiple application contexts use
    the cache (common in web applications), set this value to `false`.

2. Setting the `enable-auto-reconnect` attribute to `true` (the default
    is `false`) lets a disconnected [vmware-gemfire-short-name] member
    automatically reconnect and rejoin the [vmware-gemfire-short-name] cluster.
    For more details, see [Handling Forced Cache Disconnection Using Autoreconnect](https://docs.vmware.com/en/VMware-GemFire/9.15/gf/managing-member-reconnect.html) in the [vmware-gemfire-short-name] product documentation.

3. Setting the `use-bean-factory-locator` attribute to `true` (it
    defaults to `false`) applies only when both Spring (XML)
    configuration metadata and [vmware-gemfire-short-name] `cache.xml` is used to
    configure the [vmware-gemfire-short-name] cache node (whether client or peer).
    This option lets [vmware-gemfire-short-name] components (such as
    `CacheLoader`) expressed in `cache.xml` be auto-wired with beans
    (such as `DataSource`) defined in the Spring application context.
    This option is typically used in conjunction with
    `cache-xml-location`.

4. Setting the `use-cluster-configuration` attribute to `true` (the
    default is `false`) enables a [vmware-gemfire-short-name] member to retrieve
    the common, shared Cluster-based configuration from a Locator. For more details, see
    [Overview of the Cluster Configuration Service](https://docs.vmware.com/en/VMware-GemFire/9.15/gf/configuring-cluster_config-gfsh_persist.html) in the [vmware-gemfire-short-name] product documentation.

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
    [vmware-gemfire-short-name] transaction.

### <a id="enabling-pdx-serialization"></a>Enabling PDX Serialization

The preceding example includes a number of attributes related to
[vmware-gemfire-short-name]'s enhanced serialization framework, PDX. While a
complete discussion of PDX is beyond the scope of this reference guide,
it is important to note that PDX is enabled by registering a
`PdxSerializer`, which is specified by setting the `pdx-serializer`
attribute.

[vmware-gemfire-short-name] provides an implementing class
(`org.apache.geode.pdx.ReflectionBasedAutoSerializer`) that uses Java
Reflection. However, it is common for developers to provide their own
implementation. The value of the attribute is simply a reference to a
Spring bean that implements the `PdxSerializer` interface.

For more information about serialization support, see
[Wiring deserialized instances](serialization.html#wiring-deserialized-instances)
in _Working with [vmware-gemfire-short-name] Serialization_.

### <a id="enabling-auto-reconnect"></a>Enabling Auto-reconnect

You should be careful when setting the 
`<gfe:cache enable-auto-reconnect="[true|false*]">` attribute to `true`.

Generally, `auto-reconnect` should only be enabled in cases where
[spring-data-gemfire-name]'s XML namespace is used to configure and bootstrap a new,
non-application [vmware-gemfire-short-name] server added to a cluster. 'auto-reconnect' should not be enabled when [spring-data-gemfire-name] is used to
develop and build a [vmware-gemfire-short-name] application that also happens to
be a peer `Cache` member of the [vmware-gemfire-short-name] cluster.

The main reason for this restriction is that most [vmware-gemfire-short-name]
applications use references to the [vmware-gemfire-short-name] `Cache` or Regions
to perform data access operations. These references are
"injected" by the Spring container into application components (such as
Repositories) for use by the application. When a peer member is
forcefully disconnected from the rest of the cluster, presumably because
the peer member has become unresponsive or a network partition separates
one or more peer members into a group too small to function as an
independent distributed system, the peer member shuts down and all
[vmware-gemfire-short-name] component references (caches, Regions, and others)
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
caches, Regions, and other [vmware-gemfire-short-name] components are
reconstructed. Therefore, all old references, which may have been
injected into application by the Spring container, are now stale and no
longer valid.

[vmware-gemfire-short-name] makes no guarantee (even when using the
[vmware-gemfire-short-name] public Java API) that application cache, Regions, or
other component references are automatically refreshed by the reconnect
operation. As such, [vmware-gemfire-short-name] applications must take care to
refresh their own references.

Unfortunately, there is no way to be notified of a disconnect event and,
subsequently, a reconnect event either. If that were the case, you would
have a clean way to know when to call
`ConfigurableApplicationContext.refresh()`, if it were even applicable
for an application to do so, which is why this "feature" of
[vmware-gemfire-short-name] is not recommended for peer `Cache` applications.

For more information about `auto-reconnect`, see [Handling Forced Cache Disconnection Using Autoreconnect](https://docs.vmware.com/en/VMware-GemFire/9.15/gf/managing-member-reconnect.html) in the [vmware-gemfire-short-name] product documentation.

### <a id="using-cluster-based-configuration"></a>Using Cluster-based Configuration

[vmware-gemfire-short-name]'s Cluster Configuration Service is a convenient way
for any peer member joining the cluster to get a "consistent view" of
the cluster by using the shared, persistent configuration maintained by
a Locator. Using the cluster-based configuration ensures the peer
member's configuration is compatible with the [vmware-gemfire-short-name]
Distributed System when the member joins.

This feature of [spring-data-gemfire-name] (setting the `use-cluster-configuration`
attribute to `true`) works in the same way as the `cache-xml-location`
attribute, except the source of the [vmware-gemfire-short-name] configuration
meta-data comes from the network through a Locator, as opposed to a
native `cache.xml` file residing in the local file system.

All [vmware-gemfire-short-name] native configuration metadata, whether from
`cache.xml` or from the Cluster Configuration Service, gets applied
before any Spring (XML) configuration metadata. As a result, Spring's
config serves to "augment" the native [vmware-gemfire-short-name] configuration
metadata and would most likely be specific to the application.

To enable this feature, specify the following in the Spring XML
config:

```highlight
<gfe:cache use-cluster-configuration="true"/>
```
<p class="note"><strong>Note</strong>: While certain [vmware-gemfire-short-name] tools, such as
<code>gfsh</code>, have their actions "recorded" when schema-like changes
are made (for example, <code>gfsh>create region --name=Example --type=PARTITION</code>),
[spring-data-gemfire-name]'s configuration metadata is not recorded. The same is true
when using [vmware-gemfire-short-name]'s public Java API directly. It, too, is not
recorded.</p>

For more information about [vmware-gemfire-short-name]'s Cluster Configuration
Service, see [Overview of the Cluster Configuration Service](https://docs.vmware.com/en/VMware-GemFire/9.15/gf/configuring-cluster_config-gfsh_persist.html) in the [vmware-gemfire-short-name] product documentation.

## <a id="configuring-gemfire-cacheserver"></a>Configuring a [vmware-gemfire-short-name] CacheServer

[spring-data-gemfire-name] includes dedicated support for configuring a
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
  
  <!-- Example depicting several [vmware-gemfire-short-name] CacheServer configuration options -->
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
<code>CacheServer</code> started by [spring-data-gemfire-name] starts
<strong>after</strong> the Spring container has been fully initialized.
Doing so lets potential Regions, listeners, writers or instantiators
that are defined declaratively to be fully initialized and registered
before the server starts accepting connections. Keep this in mind when
programmatically configuring these elements, as the server might start
before your components and thus not be seen by the clients connecting
immediately.

## <a id="configuring-gemfire-clientcache"></a>Configuring a [vmware-gemfire-short-name] ClientCache

In addition to defining a [vmware-gemfire-short-name] peer
[Cache](https://geode.apache.org/releases/latest/javadoc/org/apache/geode/cache/Cache.html),
[spring-data-gemfire-name] also supports the definition of a [vmware-gemfire-short-name]
[ClientCache](https://geode.apache.org/releases/latest/javadoc/org/apache/geode/cache/client/ClientCache.html)
in a Spring container. A `ClientCache` definition is similar in
configuration and use to the [vmware-gemfire-short-name] peer
[Cache](#configuring-cache) and is supported by the
`org.springframework.data.gemfire.client.ClientCacheFactoryBean`.

The simplest definition of a [vmware-gemfire-short-name] cache client using
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

### <a id="default-pool"></a>[vmware-gemfire-short-name]'s DEFAULT Pool and [spring-data-gemfire-name] Pool Definitions

If a [vmware-gemfire-short-name] `ClientCache` is local-only, then no Pool
definition is required. For instance, you can define the following:

```highlight
<gfe:client-cache/>

<gfe:client-region id="Example" shortcut="LOCAL"/>
```

In this case, the "Example" Region is `LOCAL` and no data is distributed
between the client and a server. Therefore, no Pool is necessary. This
is true for any client-side, local-only Region, as defined by the
[vmware-gemfire-short-name]'s
[ClientRegionShortcut](https://geode.apache.org/releases/latest/javadoc/org/apache/geode/cache/client/ClientRegionShortcut.html).

However, if a client Region is a (caching) proxy to a server-side
Region, a Pool is required. In that case, there are several ways to
define and use a Pool.

When a `ClientCache`, a Pool, and a proxy-based Region are all defined
but not explicitly identified, [spring-data-gemfire-name] resolves the references
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
"Example". However, the `ClientCache` initializes [vmware-gemfire-short-name]'s
`DEFAULT` Pool from `gemfirePool`, and the client Region uses the
`gemfirePool` when distributing data between the client and the server.

Basically, [spring-data-gemfire-name] resolves the preceding configuration to the
following:

```highlight
<gfe:client-cache id="gemfireCache" pool-name="gemfirePool"/>

<gfe:pool id="gemfirePool">
  <gfe:locator host="${geode.locator.host}" port="${geode.locator.port}"/>
</gfe:pool>

<gfe:client-region id="Example" cache-ref="gemfireCache" pool-name="gemfirePool" shortcut="PROXY"/>
```

[vmware-gemfire-short-name] still creates a Pool named `DEFAULT`. [spring-data-gemfire-name]
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

In this example, the [vmware-gemfire-short-name] `client-cache` `DEFAULT` pool is
initialized from `locatorPool`, as specified by the `pool-name`
attribute. There is no [spring-data-gemfire-name]-defined `gemfirePool`, since both
Pools were explicitly identified (named) as `locatorPool` and
`serverPool`, respectively.

The "Example" Region explicitly refers to and exclusively uses the
`serverPool`. The `AnotherExample` Region uses [vmware-gemfire-short-name]'s
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
