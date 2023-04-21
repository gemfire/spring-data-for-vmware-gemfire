---
title: Bootstrapping a Spring ApplicationContext in GemFire
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

Normally, a Spring-based application [bootstraps
GemFire](bootstrap.html) by using Spring Data for VMware GemFire's features. By
specifying a `<gfe:cache/>` element that uses the Spring Data for VMware GemFire XML
namespace, a single embedded GemFire peer `Cache` instance is
created and initialized with default settings in the same JVM process as
your application.

However, it is sometimes necessary (perhaps as a requirement imposed by
your IT organization) that GemFire be fully managed and
operated by the provided GemFire tool suite, perhaps using
[gfsh](https://docs.vmware.com/en/VMware-GemFire/9.15/gf/tools_modules-gfsh-chapter_overview.html). By
using `gfsh`, GemFire bootstraps your Spring
`ApplicationContext` rather than the other way around. Instead of an
application server or a Java main class that uses Spring Boot,
GemFire does the bootstrapping and hosts your application.

<p class="note"><strong>Note</strong>: GemFire is not an application server. Additionally, there are limitations to using this approach where the GemFire cache configuration is concerned.</p>

## <a id="using-gemfire-to-bootstrap"></a>Using GemFire to Bootstrap a Spring Context Started with gfsh

Ro bootstrap a Spring `ApplicationContext` in GemFire
when starting a GemFire server using `gfsh`, you must use
GemFire's [initializer](https://docs.vmware.com/en/VMware-GemFire/9.15/gf/basic_config-the_cache-setting_cache_initializer.html) capability. An initializer block can declare a application callback that is launched after the cache is initialized by GemFire.

An initializer is declared within an
[initializer](https://docs.vmware.com/en/VMware-GemFire/9.15/gf/reference-topics-cache_xml.html#initializer)
element by using a minimal snippet of GemFire's native
`cache.xml`. To bootstrap the Spring `ApplicationContext`, a `cache.xml`
file is required, in much the same way as a minimal snippet of Spring
XML config is needed to bootstrap a Spring `ApplicationContext`
configured with component scanning (for example
`<context:component-scan base-packages="…​"/>`).

Fortunately, such an initializer is already conveniently provided by the
framework: the
[SpringContextBootstrappingInitializer](https://docs.spring.io/spring-data/gemfire/docs/current/api/org/springframework/data/gemfire/support/SpringContextBootstrappingInitializer.html).

The following example shows a typical, yet minimal, configuration for
this class inside GemFire's `cache.xml` file:

```highlight
<?xml version="1.0" encoding="UTF-8"?>
<cache xmlns="http://geode.apache.org/schema/cache"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xsi:schemaLocation="http://geode.apache.org/schema/cache https://geode.apache.org/schema/cache/cache-1.0.xsd"
       version="1.0">

  <initializer>
    <class-name>org.springframework.data.gemfire.support.SpringContextBootstrappingInitializer</class-name>
    <parameter name="contextConfigLocations">
      <string>classpath:application-context.xml</string>
    </parameter>
  </initializer>

</cache>
```

The `SpringContextBootstrappingInitializer` class follows conventions
similar to Spring's `ContextLoaderListener` class, which is used to
bootstrap a Spring `ApplicationContext` inside a web application, where
`ApplicationContext` configuration files are specified with the
`contextConfigLocations` Servlet context parameter.

In addition, the `SpringContextBootstrappingInitializer` class can also
be used with a `basePackages` parameter to specify a comma-separated
list of base packages that contain appropriately annotated application
components. The Spring container searches these components to find and
create Spring beans and other application components in the classpath,
as the following example shows:

```highlight
<?xml version="1.0" encoding="UTF-8"?>
<cache xmlns="http://geode.apache.org/schema/cache"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xsi:schemaLocation="http://geode.apache.org/schema/cache https://geode.apache.org/schema/cache/cache-1.0.xsd"
       version="1.0">

  <initializer>
    <class-name>org.springframework.data.gemfire.support.SpringContextBootstrappingInitializer</class-name>
    <parameter name="basePackages">
      <string>org.mycompany.myapp.services,org.mycompany.myapp.dao,...</string>
    </parameter>
  </initializer>

</cache>
```

Then, with a properly configured and constructed `CLASSPATH` and
`cache.xml` file (shown earlier) specified as a command-line option when
starting a GemFire server in `gfsh`, the command-line would be
as follows:

```highlight
gfsh>start server --name=ExampleServer --log-level=config ...
    --classpath="/path/to/application/classes.jar:/path/to/spring-data-geode-<major>.<minor>.<maint>.RELEASE.jar"
    --cache-xml-file="/path/to/geode/cache.xml"
```

The `application-context.xml` can be any valid Spring configuration
metadata, including all of the Spring Data for VMware GemFire XML namespace elements. The
only limitation with this approach is that a GemFire cache
cannot be configured by using the Spring Data for VMware GemFire XML namespace. In other
words, none of the `<gfe:cache/>` element attributes (such as
`cache-xml-location`, `properties-ref`, `critical-heap-percentage`,
`pdx-serializer-ref`, `lock-lease`, and others) can be specified. If
used, these attributes are ignored.

The reason for this is that GemFire itself has already created
and initialized the cache before the initializer gets invoked. As a
result, the cache already exists and, since it is a "singleton", it
cannot be re-initialized or have any of its configuration augmented.

## <a id="lazy-wiring-gemfire-components"></a>Lazy-wiring GemFire Components

Spring Data for VMware GemFire already provides support for auto-wiring GemFire
components (such as `CacheListeners`, `CacheLoaders`, `CacheWriters` and
so on) that are declared and created by GemFire in `cache.xml`
by using Spring Data for VMware GemFire's `WiringDeclarableSupport` class, as described
in [Configuration Using Auto-Wiring and Annotations](data.html#configurations-using-auto-wiring-and-annotations) in _Working with GemFire APIs_. However, this works only when Spring is the one doing the bootstrapping
(that is, when Spring bootstraps GemFire).

When your Spring `ApplicationContext` is bootstrapped by
GemFire, these GemFire application components go
unnoticed, because the Spring `ApplicationContext` does not exist yet.
The Spring `ApplicationContext` does not get created until
GemFire calls the initializer block, which only occurs after
all the other GemFire components (cache, Regions, and others)
have already been created and initialized.

To solve this problem, a new `LazyWiringDeclarableSupport` class was
introduced. This new class is aware of the Spring `ApplicationContext`.
The intention behind this abstract base class is that any implementing
class registers itself to be configured by the Spring container that is
eventually created by GemFire once the initializer is called.
In essence, this gives your GemFire application components a
chance to be configured and auto-wired with Spring beans defined in the
Spring container.

In order for your GemFire application components to be
auto-wired by the Spring container, you should create an application
class that extends the `LazyWiringDeclarableSupport` and annotate any
class member that needs to be provided as a Spring bean dependency,
similar to the following example:

```highlight
public class UserDataSourceCacheLoader extends LazyWiringDeclarableSupport
    implements CacheLoader<String, User> {

  @Autowired
  private DataSource userDataSource;

  ...
}
```

As implied in the `CacheLoader` example above, you might necessarily
(though rarely) have defined both a Region and a `CacheListener`
component in GemFire `cache.xml`. The `CacheLoader` may need
access to an application Repository (or perhaps a JDBC `DataSource`
defined in the Spring `ApplicationContext`) for loading `Users` into a
GemFire `REPLICATE` Region on startup.

Be careful when mixing the different life-cycles of GemFire
and the Spring container together in this manner. Not all use cases and
scenarios are supported. The GemFire `cache.xml` configuration
would be similar to the following (which comes from Spring Data for VMware GemFire's test
suite):

```highlight
<?xml version="1.0" encoding="UTF-8"?>
<cache xmlns="http://geode.apache.org/schema/cache"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xsi:schemaLocation="http://geode.apache.org/schema/cache https://geode.apache.org/schema/cache/cache-1.0.xsd"
       version="1.0">

  <region name="Users" refid="REPLICATE">
    <region-attributes initial-capacity="101" load-factor="0.85">
      <key-constraint>java.lang.String</key-constraint>
      <value-constraint>org.springframework.data.gemfire.repository.sample.User</value-constraint>
      <cache-loader>
        <class-name>
          org.springframework.data.gemfire.support.SpringContextBootstrappingInitializerIntegrationTests$UserDataStoreCacheLoader
        </class-name>
      </cache-loader>
    </region-attributes>
  </region>

  <initializer>
    <class-name>org.springframework.data.gemfire.support.SpringContextBootstrappingInitializer</class-name>
    <parameter name="basePackages">
      <string>org.springframework.data.gemfire.support.sample</string>
    </parameter>
  </initializer>

</cache>
```
