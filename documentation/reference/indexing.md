---
title: Configuring an Index
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

The `Index` `type` may be one of three enumerated values defined by the [IndexType](https://docs.spring.io/spring-data/gemfire/docs/current/api/org/springframework/data/gemfire/IndexType.html) enumeration: `FUNCTIONAL`, `HASH`, and `PRIMARY_KEY`.

Each of the enumerated values corresponds to one of the
[QueryService](https://gemfire.docs.pivotal.io/apidocs/gf-100/org/apache/geode/cache/query/QueryService.html) `create[|Key|Hash]Index` methods invoked when the actual `Index` is to
be created or defined. For example, if the `IndexType` is `PRIMARY_KEY`,
then the [QueryService.createKeyIndex(..)](https://gemfire.docs.pivotal.io/apidocs/gf-100/org/apache/geode/cache/query/QueryService.html#createKeyIndex-java.lang.String-java.lang.String-java.lang.String-)
is invoked to create a `KEY` `Index`.

The default is `FUNCTIONAL` and results in one of the
`QueryService.createIndex(..)` methods being invoked. See the Spring Data for VMware GemFire
XML schema for a full set of options.

For more information about indexing in GemFire, see [Working
with
Indexes](https://docs.vmware.com/en/VMware-GemFire/9.15/gf/developing-query_index-query_index.html) in the GemFire product documentation.

## <a id="defining-indexes"></a>Defining Indexes

In addition to creating indexes as `Index` bean definitions are
processed by Spring Data for VMware GemFire on Spring container initialization, you may also
define all of your application indexes prior to creating them by using
the `define` attribute, as follows:

```highlight
<gfe:index id="myDefinedIndex" expression="someField" from="/SomeRegion" define="true"/>
```

When `define` is set to `true`, it does not immediately create the `Index`. All "defined" Indexes are created at the same time when the Spring `ApplicationContext`. This occurs when a `ContextRefreshedEvent` is published by the Spring container. Spring Data for VMware GemFire registers itself as an `ApplicationListener` listening for the `ContextRefreshedEvent`. When
fired, Spring Data for VMware GemFire calls
[QueryService.createDefinedIndexes()](https://gemfire.docs.pivotal.io/apidocs/gf-100/org/apache/geode/cache/query/QueryService.html#createDefinedIndexes).

Defining indexes and creating them at the same time boosts speed and
efficiency when creating indexes.

For more information, see [Creating Multiple Indexes at Once](https://docs.vmware.com/en/VMware-GemFire/9.15/gf/developing-query_index-create_multiple_indexes.html) in the GemFire product documentation.

## <a id="ignoreiexists-and-override"></a>`IgnoreIfExists` and `Override`

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

* An [IndexExistsException](https://gemfire.docs.pivotal.io/apidocs/gf-100/org/apache/geode/cache/query/IndexExistsException.html) is thrown when there exists another `Index` with the same definition
  but a different name when attempting to create an `Index`.

* An [IndexNameConflictException](https://gemfire.docs.pivotal.io/apidocs/gf-100/org/apache/geode/cache/query/IndexNameConflictException.html) is thrown when there exists another `Index` with the same name but possibly different definition when attempting to create an `Index`.

Spring Data for VMware GemFire's default behavior is fail-fast strategy. Neither of the above `Index` exceptions are handled by default. These `Index` exceptions are wrapped in a Spring Data for VMware GemFire `GemfireIndexException` and rethrown. If you want Spring Data for VMware GemFire to handle them for you, you can set either of
these `Index` bean definition options to `true`.

`IgnoreIfExists` always takes precedence over `Override` because it uses fewer resources. It returns the "existing" `Index` in both exception cases.

### <a id="ignoreiexists-behavior"></a>`IgnoreIfExists` Behavior

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

### <a id="override-behavior"></a>`Override` Behavior

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

### <a id="indexnameconflictexceptions"></a>How Does `IndexNameConflictExceptions` Actually Happen?

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
