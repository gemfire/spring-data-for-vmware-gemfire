---
title: Configuring the Snapshot Service
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


Spring Data for VMware GemFire supports cache and Region snapshots by using
GemFire's Snapshot Service. The out-of-the-box Snapshot Service support offers
several convenient features to simplify the use of GemFire's [Cache](https://geode.apache.org/releases/latest/javadoc/org/apache/geode/cache/snapshot/CacheSnapshotService.html) and [Region](https://geode.apache.org/releases/latest/javadoc/org/apache/geode/cache/snapshot/RegionSnapshotService.html) Snapshot Service APIs. For more information about the Snapshot Service, see [Cache and Region Snapshots](https://docs.vmware.com/en/VMware-GemFire/9.15/gf/managing-cache_snapshots-chapter_overview.html)  in the GemFire product documentation.

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

## <a id="snapshot-location"></a>Snapshot Location

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

## <a id="snapshot-filters"></a>Snapshot Filters


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

## <a id="snapshot-events"></a>Snapshot Events

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
[Scheduling](https://docs.spring.io/spring-framework/docs/current/reference/html/#scheduling-task-scheduler)
services to fire snapshot application events on a periodic basis.

