---
title: Configuring a Region
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

A Region is required to store and retrieve data from the cache.
`org.apache.geode.cache.Region` is an interface extending
`java.util.Map` and enables basic data access using familiar key-value
semantics. The `Region` interface is wired into application classes that
require it so the actual Region type is decoupled from the programming
model. Typically, each Region is associated with one domain object,
similar to a table in a relational database.

[vmware-gemfire-short-name] implements the following types of Regions:

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
capabilities as well as configuration options, see [Region Types](https://docs.vmware.com/en/VMware-GemFire/9.15/gf/developing-region_options-region_types.html) in the [vmware-gemfire-short-name] product documentation.

## <a id="using-externally-configured-region"></a>Using an Externally Configured Region

To reference Regions already configured in a [vmware-gemfire-short-name] native
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

## <a id="auto-region-lookup"></a>Auto Region Lookup

`auto-region-lookup` lets you import all Regions defined in a
[vmware-gemfire-short-name] native `cache.xml` file into a Spring
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

[spring-data-gemfire-name] automatically creates beans for all [vmware-gemfire-short-name] Regions
defined in `cache.xml` that have not been explicitly added to the Spring
container with explicit `<gfe:lookup-region>` bean declarations.

Note that [spring-data-gemfire-name] uses a Spring
[BeanPostProcessor](https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/beans/factory/config/BeanPostProcessor.html)
to post-process the cache after it is both created and initialized to
determine the Regions defined in [vmware-gemfire-short-name] to add as beans in
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

Doing so ensures that the [vmware-gemfire-short-name] cache and all the Regions
defined in `cache.xml` are created before any components with auto-wire
references when using the `<gfe:auto-region-lookup>` element.

## <a id="configuring-regions"></a>Configuring Regions

[spring-data-gemfire-name] provides comprehensive support for configuring any type of
Region through the following elements:

* LOCAL Region: `<local-region>`

* PARTITION Region: `<partitioned-region>`

* REPLICATE Region: `<replicated-region>`

* Client Region: `<client-region>`

For description of these types, see [Region Types](https://docs.vmware.com/en/VMware-GemFire/9.15/gf/developing-region_options-region_types.html) in the [vmware-gemfire-short-name] product documentation.

### <a id="common-region-attributes"></a>Common Region Attributes

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
      <td>[vmware-gemfire-short-name] Cache bean reference</td>
      <td>The name of the bean defining the [vmware-gemfire-short-name] Cache. Defaults to
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

### <a id="cachelistener-instances"></a>`CacheListener` instances

`CacheListener` instances are registered with a Region to handle Region
events, such as when entries are created, updated, and destroyed.
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
pattern used in the XML namespace anywhere [vmware-gemfire-short-name] provides a
callback interface to be implemented to invoke custom code in
response to cache or Region events. When you use Spring's IoC container,
the implementation is a standard Spring bean. To simplify the
configuration, the schema allows a single occurrence of the
<code>cache-listener</code> element, but, if multiple instances are
permitted, it may contain nested bean references and inner bean
definitions in any combination. The convention is to use the singular
form, <code>cache-listener</code> instead of <code>cache-listeners</code>, reflecting that the most common scenario is a single instance. THis pattern can be seen in the <a href="#advanced-cache-configuration">advanced cache</a>
configuration example.

### <a id="cacheloaders-and-cachewriters"></a>CacheLoaders and CacheWriters

Similar to `cache-listener`, the XML namespace provides `cache-loader`
and `cache-writer` elements to register these [vmware-gemfire-short-name]
components for a Region.

A `CacheLoader` is invoked on a cache miss to let an entry be loaded
from an external data source, such as a database. A `CacheWriter` is
invoked before an entry is created or updated, to allow the entry to be
synchronized to an external data source. The main difference is that
[vmware-gemfire-short-name] supports, at most, a single instance of `CacheLoader`
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

## <a id="compression"></a>Compression

[vmware-gemfire-short-name] Regions may be compressed to reduce JVM
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

For more information, see [Region Compression](https://docs.vmware.com/en/VMware-GemFire/9.15/gf/managing-region_compression.html) in the [vmware-gemfire-short-name] product documentation.

## <a id="off-heap"></a>Off-Heap

[vmware-gemfire-short-name] Regions may be configured to store Region values
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
the following [vmware-gemfire-short-name] configuration properties using 
`<gfe:cache>` elements:

```highlight
<gfe:cache critical-off-heap-percentage="90" eviction-off-heap-percentage"80"/>
```

[vmware-gemfire-short-name]'s `ResourceManager` will use the `critical-off-heap-percentage` and  `eviction-off-heap-percentage` threshold values to more effectively manage the off-heap memory in a way similar to how 
the JVM manages heap memory. [vmware-gemfire-short-name]
`ResourceManager` will prevent the cache from consuming too much
off-heap memory by evicting old data. If the off-heap manager is unable
to keep up, then the `ResourceManager` refuses additions to the cache
until the off-heap memory manager has freed up an adequate amount of
memory.

For more information, see [Managing Off-Heap Memory](https://docs.vmware.com/en/VMware-GemFire/9.15/gf/managing-heap_use-off_heap_management.html) in the [vmware-gemfire-short-name] product documentation:

## <a id="subregions"></a>Subregions

[spring-data-gemfire-name] supports Sub-Regions, allowing Regions to be arranged in
a hierarchical relationship.

For example, [vmware-gemfire-short-name] allows for a `/Customer/Address` Region
and a different `/Employee/Address` Region. Additionally, a Sub-Region
may have its own Sub-Regions and configuration. A Sub-Region does not
inherit attributes from its parent Region. Regions types may be mixed
and matched subject to [vmware-gemfire-short-name] constraints. A Sub-Region is
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

## <a id="region-templates"></a>Region Templates

[spring-data-gemfire-name] supports Region templates.

This feature allows developers to define common Region configuration and
attributes once and reuse the configuration among many Region bean
definitions declared in the Spring `ApplicationContext`.

[spring-data-gemfire-name] includes five Region template tags in its namespace:


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

### <a id="how-templating-works"></a>How Templating Works

[spring-data-gemfire-name] applies Region templates when the Spring `ApplicationContext`
configuration metadata is parsed, and therefore, Region templates must
be declared in the order of inheritance. In other words, parent
templates must be defined before child templates. Doing so ensures that
the proper configuration is applied, especially when element attributes
or sub-elements are overridden.

<p class="note"><strong>Note</strong>: Region templates are single-inheritance, and Region
types must only inherit from other similarly typed Regions. For
example, a <code><gfe:replicated-region></code> cannot inherit from a
<code><gfe:partitioned-region-template></code>.</p>

### <a id="caution"></a>Caution Concerning Regions, Sub-Regions, and Lookups

Previously, one of the underlying properties of the `replicated-region`,
`partitioned-region`, `local-region`, and `client-region` elements in
the [spring-data-gemfire-name] XML namespace was to perform a lookup first before
attempting to create a Region. This was done in case the Region already
existed, which would be the case if the Region was defined in an
imported [vmware-gemfire-short-name] native `cache.xml` configuration file.
Therefore, the lookup was performed first to avoid any errors. This was
by design and subject to change.

This behavior has been altered and the default behavior is now to create
the Region first. If the Region already exists, then the creation logic
fails-fast and an appropriate exception is thrown. However, much like
the `CREATE TABLE IF NOT EXISTS …​` DDL syntax, the [spring-data-gemfire-name]
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

Consider the following native [vmware-gemfire-short-name] `cache.xml`
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

[vmware-gemfire-short-name] is flexible in referencing both parent Regions and
Sub-Regions with or without the leading forward slash. For example, the
parent can be referenced as `/Customers` or `Customers` and the child as
`/Customers/Accounts` or `Customers/Accounts`. However, [spring-data-gemfire-name] is
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
native [vmware-gemfire-short-name] `cache.xml` configuration file as `REPLICATE`
and exist before the cache bean is initialized (once the `<gfe:cache>`
element is processed).

## <a id="data-eviction"></a>Data Eviction (with Overflow)

Based on various constraints, each Region can have an eviction policy in
place for evicting data from memory. Currently, in [vmware-gemfire-short-name],
eviction applies to the Least Recently Used entry (also known as
[LRU](https://en.wikipedia.org/wiki/Cache_replacement_policies#Least_recently_used_(LRU)).
Evicted entries are either destroyed or paged to disk (referred to as
"overflow to disk").

[spring-data-gemfire-name] supports all eviction policies (entry count, memory, and heap
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
eviction since that would invalidate them. For more information, see the <a href="https://docs.vmware.com/en/VMware-GemFire/index.html">[vmware-gemfire-short-name] product documentation</a>.</p>

When configuring Regions for overflow, you should configure the storage
through the `disk-store` element for maximum efficiency.

For a detailed description of eviction policies, see [Eviction](https://docs.vmware.com/en/VMware-GemFire/9.15/gf/developing-eviction-chapter_overview.html?hWord=N4IghgNiBcIKYDcCWBjALkg9gOxAXyA) in the [vmware-gemfire-short-name] product documentation.

## <a id="data-expiration"></a>Data Expiration

[vmware-gemfire-short-name] lets you control how long entries exist in the cache.
Expiration is driven by elapsed time, as opposed to eviction, which is
driven by the entry count or heap or memory usage. Once an entry
expires, it may no longer be accessed from the cache.

[vmware-gemfire-short-name] supports the following expiration types:

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
Region. [spring-data-gemfire-name] provides `<region-ttl>`, `<region-tti>`,
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

For a detailed description of expiration policies, see [Expiration](https://docs.vmware.com/en/VMware-GemFire/9.15/gf/developing-expiration-chapter_overview.html) in the [vmware-gemfire-short-name] product documentation.

### <a id="annotation-based-data-expiration"></a>Annotation-Based Data Expiration

With [spring-data-gemfire-name], you can define expiration policies and settings on
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

All <code>@Expiration</code>-based annotations apply only to Region entry values. Expiration for a Region is not covered by [spring-data-gemfire-name]'s
expiration annotation support. However, [vmware-gemfire-short-name] and [spring-data-gemfire-name]
do let you set Region expiration by using the [spring-data-gemfire-name] XML
namespace, as follows:

```
<gfe:*-region id="Example" persistent="false">
  <gfe:region-ttl timeout="600" action="DESTROY"/>
  <gfe:region-tti timeout="300" action="INVALIDATE"/>
</gfe:*-region>
```

[spring-data-gemfire-name]'s `@Expiration` annotation support is implemented with
[vmware-gemfire-short-name]'s [CustomExpiry](https://geode.apache.org/releases/latest/javadoc/org/apache/geode/cache/CustomExpiry.html) interface. For more information, see [Configure Data Expiration](https://docs.vmware.com/en/VMware-GemFire/9.15/gf/developing-expiration-configuring_data_expiration.html) in the [vmware-gemfire-short-name] product documentation.

The [spring-data-gemfire-name] `AnnotationBasedExpiration` class and `CustomExpiry`
implementation is responsible for processing the [spring-data-gemfire-name]
`@Expiration` annotations and applying the expiration policy
configuration appropriately for Region entry expiration on request.

To use [spring-data-gemfire-name] to configure specific [vmware-gemfire-short-name] Regions to
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
    one of [spring-data-gemfire-name]'s `@Expiration` annotations: `@Expiration`,
    `@IdleTimeoutExpiration`, or `@TimeToLiveExpiration`

3. (Optional) In cases where particular application domain objects have
    not been annotated with [spring-data-gemfire-name]'s `@Expiration` annotations at
    all, but the [vmware-gemfire-short-name] Region is configured to use
    [spring-data-gemfire-name]'s custom `AnnotationBasedExpiration` class to
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

You may have noticed that [spring-data-gemfire-name]'s `@Expiration` annotations use a
`String` as the attribute type rather than, and perhaps more
appropriately, being strongly typed — for example, `int` for 'timeout'
and [spring-data-gemfire-name]'s `ExpirationActionType` for 'action'. Why is that?

Well, enter one of [spring-data-gemfire-name]'s other features, leveraging Spring's core
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
[spring-data-gemfire-name] test suite. For details, see the [Spring Data](https://github.com/spring-projects/spring-data-geode) repository in GitHub.

## <a id="data-persistence"></a>Data Persistence

Regions can be persistent. [vmware-gemfire-short-name] ensures that all the data
you put into a Region that is configured for persistence is written to
disk in a way that is recoverable the next time you recreate the Region.
Doing so lets data be recovered after machine or process failure or even
after an orderly shutdown and subsequent restart of the
[vmware-gemfire-short-name] data node.

To enable persistence with [spring-data-gemfire-name], set the `persistent` attribute to
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

## <a id="subscription-policy"></a>Subscription Policy


[vmware-gemfire-short-name] allows configuration of [peer-to-peer
(P2P) event messaging](https://docs.vmware.com/en/VMware-GemFire/9.15/gf/developing-events-configure_p2p_event_messaging.html) to control the entry events that the Region
receives. [spring-data-gemfire-name] provides the `<gfe:subscription/>` sub-element to
set the subscription policy on `REPLICATE` and `PARTITION` Regions to
either `ALL` or `CACHE_CONTENT`. The following example shows a region
with its subscription policy set to `CACHE_CONTENT`:

```highlight
<gfe:partitioned-region id="examplePartitionRegionWithCustomSubscription">
  <gfe:subscription type="CACHE_CONTENT"/>
</gfe:partitioned-region>
```

## <a id="local-region"></a>Local Region

[spring-data-gemfire-name] offers a dedicated `local-region` element for creating local
Regions. Local Regions, as the name implies, are standalone, meaning
that they do not share data with any other distributed system member.
Other than that, all common Region configuration options apply.

The following example shows a minimal declaration. The example
relies on the [spring-data-gemfire-name] XML namespace naming conventions to wire the
cache:

```highlight
<gfe:local-region id="exampleLocalRegion"/>
```

In the preceding example, a local Region is created if a Region by the
same name does not already exist. The name of the Region is the same as
the bean ID (`exampleLocalRegion`), and the bean assumes the existence
of a [vmware-gemfire-short-name] cache named `gemfireCache`.

## <a id="replicated-region"></a>Replicated Region

One of the common Region types is a `REPLICATE` Region or "replica". In
short, when a Region is configured to be a `REPLICATE`, every member
that hosts the Region stores a copy of the Region's entries locally. Any
update to a `REPLICATE` Region is distributed to all copies of the
Region. When a replica is created, it goes through an initialization
stage, in which it discovers other replicas and automatically copies all
the entries. While one replica is initializing, you can still continue
to use the other replicas.

All common configuration options are available for REPLICATE Regions.
[spring-data-gemfire-name] offers a `replicated-region` element. The following example
shows a minimal declaration:

```highlight
<gfe:replicated-region id="exampleReplica"/>
```

For more information, see [Distributed and Replicated Regions](https://docs.vmware.com/en/VMware-GemFire/9.15/gf/developing-distributed_regions-chapter_overview.html) in the [vmware-gemfire-short-name] product documentation.

## <a id="partitioned-region"></a>Partitioned Region

The [spring-data-gemfire-name] XML namespace also supports `PARTITION` Regions.

A partitioned region is a region where data is divided between peer
servers hosting the region so that each peer stores a subset of the
data. When using a partitioned region, applications are presented with a
logical view of the region that looks like a single map containing all
of the data in the region. Reads or writes to this map are transparently
routed to the peer that hosts the entry that is the target of the
operation. [vmware-gemfire-short-name] divides the domain of hashcodes into
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

For more information, see [Partitioned Regions](https://docs.vmware.com/en/VMware-GemFire/9.15/gf/developing-partitioned_regions-chapter_overview.html) in the [vmware-gemfire-short-name] product documentation.

### <a id="partitioned-region-attributes"></a>Partitioned Region Attributes

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


## <a id="client-region"></a>Client Region

[vmware-gemfire-short-name] supports various deployment topologies for managing
and distributing data. The topic of [vmware-gemfire-short-name] topologies is
beyond the scope of this documentation, but [vmware-gemfire-short-name]'s supported topologies can be classified as:

* peer-to-peer (p2p)
* client-server
* wide area network (WAN).

In the last two configurations, it is common to declare client Regions that connect to a cache server.

[spring-data-gemfire-name] offers dedicated support for each configuration through its
[client-cache](cache.html#configuring-gemfire-clientcache) elements: `client-region` and
`pool`. `client-region` defines a client Region,
and `pool` defines a Pool of connections used and shared by the
various client Regions.

The following example shows a typical client Region configuration:

```highlight
<bean id="myListener" class="example.CacheListener"/>
  
<!-- client Region using the default [spring-data-gemfire-name] gemfirePool Pool -->
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
`Pool`, see the [[spring-data-gemfire-name] Schema](../appendix/appendix-schema.html) and
[Client/Server Configuration](https://docs.vmware.com/en/VMware-GemFire/9.15/gf/topologies_and_comm-cs_configuration-chapter_overview.html) in the [vmware-gemfire-short-name] product documentation.

### <a id="client-interests"></a>

To minimize network traffic, each client can separately define its own
'interests' policies, indicating to [vmware-gemfire-short-name] the data it
actually requires. In [spring-data-gemfire-name], 'interests' can be defined for each
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
Event Distribution](https://docs.vmware.com/en/VMware-GemFire/9.15/gf/developing-events-how_client_server_distribution_works.html) in the [vmware-gemfire-short-name] product documentation.

## <a id="json-support"></a>JSON Support

[vmware-gemfire-short-name] has support for caching JSON documents in Regions,
along with the ability to query stored JSON documents using the
[vmware-gemfire-short-name] OQL (Object Query Language). JSON documents are stored
internally as
[PdxInstance](https://geode.apache.org/releases/latest/javadoc/org/apache/geode/pdx/PdxInstance.html) types by using the [JSONFormatter](https://geode.apache.org/releases/latest/javadoc/org/apache/geode/pdx/JSONFormatter.html) class to perform conversion to and from JSON documents (as a `String`).

[spring-data-gemfire-name] provides the `<gfe-data:json-region-autoproxy/>` element to
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
use [vmware-gemfire-short-name]'s proprietary <code>Region.Entry</code>, such as 
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
