---
title: Apache Lucene Integration
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

[Vmware GemFire](https://docs.vmware.com/en/VMware-GemFire/index.html)
integrates with [Apache Lucene](https://lucene.apache.org/) to let you index and search data
stored in GemFire by using Lucene queries. Search-based
queries also include the ability to page through query results.

Additionally, Spring Data for VMware GemFire adds support for query projections based on the
Spring Data Commons projection infrastructure. This feature lets the
query results be projected into first-class application domain types as
needed by the application.

A Lucene `Index` must be created before any Lucene search-based query
can be run. A `LuceneIndex` can be created in Spring Data for
GemFire XML config as follows:

```highlight
<gfe:lucene-index id="IndexOne" fields="fieldOne, fieldTwo" region-path="/Example"/>
```

Additionally, Apache Lucene allows the specification of
[analyzers](https://lucene.apache.org/core/6_5_0/core/org/apache/lucene/analysis/Analyzer.html)
per field and can be configured as shown in the following example:

```highlight
<gfe:lucene-index id="IndexTwo" lucene-service-ref="luceneService" region-path="/AnotherExample">
    <gfe:field-analyzers>
        <map>
            <entry key="fieldOne">
                <bean class="example.AnalyzerOne"/>
             </entry>
            <entry key="fieldTwo">
                <bean class="example.AnalyzerTwo"/>
             </entry>
        </map>
    </gfe:field-analyzers>
</gfe:lucene-index>
```

The `Map` can be specified as a top-level bean definition and referenced
by using the `ref` attribute in the nested `<gfe:field-analyzers>`
element, as follows:
`<gfe-field-analyzers ref="refToTopLevelMapBeanDefinition"/>`.


Spring Data for VMware GemFire's `LuceneIndexFactoryBean` API and Spring Data for VMware GemFire's XML
namespace also allows you to specify a [org.apache.geode.cache.lucene.LuceneSerializer](https://geode.apache.org/releases/latest/javadoc/org/apache/geode/cache/lucene/LuceneSerializer.html)
when you create the `LuceneIndex`. The `LuceneSerializer`
allows you to configure the way objects are converted to Lucene documents for
the index when the object is indexed.

The following example shows how to add an `LuceneSerializer` to the
`LuceneIndex`:

```highlight
<bean id="MyLuceneSerializer" class="example.CustomLuceneSerializer"/>

<gfe:lucene-index id="IndexThree" lucene-service-ref="luceneService" region-path="/YetAnotherExample">
    <gfe:lucene-serializer ref="MyLuceneSerializer">
</gfe:lucene-index>
```

You can specify the `LuceneSerializer` as an anonymous, nested bean
definition as well, as follows:

```highlight
<gfe:lucene-index id="IndexThree" lucene-service-ref="luceneService" region-path="/YetAnotherExample">
    <gfe:lucene-serializer>
        <bean class="example.CustomLuceneSerializer"/>
    </gfe:lucene-serializer>
</gfe:lucene-index>
```

Alternatively, you can declare or define a `LuceneIndex` in Spring Java
config, inside a `@Configuration` class, as the following example shows:

```highlight
@Bean(name = "Books")
@DependsOn("bookTitleIndex")
PartitionedRegionFactoryBean<Long, Book> booksRegion(GemFireCache gemfireCache) {

    PartitionedRegionFactoryBean<Long, Book> peopleRegion =
        new PartitionedRegionFactoryBean<>();

    peopleRegion.setCache(gemfireCache);
    peopleRegion.setClose(false);
    peopleRegion.setPersistent(false);

    return peopleRegion;
}

@Bean
LuceneIndexFactoryBean bookTitleIndex(GemFireCache gemFireCache,
        LuceneSerializer luceneSerializer) {

    LuceneIndexFactoryBean luceneIndex = new LuceneIndexFactoryBean();

    luceneIndex.setCache(gemFireCache);
    luceneIndex.setFields("title");
    luceneIndex.setLuceneSerializer(luceneSerializer);
    luceneIndex.setRegionPath("/Books");

    return luceneIndex;
}

@Bean
CustomLuceneSerializer myLuceneSerialier() {
    return new CustomeLuceneSerializer();
}
```

A number of limitations exist with GemFire's Apache Lucene integration and support.

- A `LuceneIndex` can only be created on a GemFire `PARTITION` Region.

- All `LuceneIndexes` must be created before the Region to which the `LuceneIndex` applies.

<p class="note"><strong>Note</strong>: To help ensure that all declared
<code>LuceneIndexes</code> defined in a Spring container are created
before the Regions on which they apply, Spring Data for VMware GemFire includes the
<code>org.springframework.data.gemfire.config.support.LuceneIndexRegionBeanFactoryPostProcessor</code>. You can register this Spring <a href="https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/beans/factory/config/BeanFactoryPostProcessor.html">BeanFactoryPostProcessor</a>in XML config by using
<code>&lt;bean class="org.springframework.data.gemfire.config.support.LuceneIndexRegionBeanFactoryPostProcessor"/&gt;</code>. The
<code>o.s.d.g.config.support.LuceneIndexRegionBeanFactoryPostProcessor</code>
may only be used when using Spring Data for VMware GemFire XML config. For more details about
Spring's <code>BeanFactoryPostProcessors</code>, see <a href="https://docs.spring.io/spring-framework/docs/current/reference/html/core.html#beans-factory-extension-factory-postprocessors">Customizing Configuration Metadata with a BeanFactoryPostProcessor</a>
in the Spring Framework documentation.</p>

In the preceding example, the
presence of Spring's <code>@DependsOn</code> annotation on the
<code>Books</code> Region bean definition creates a dependency
from the <code>Books</code> Region bean to the
<code>bookTitleIndex</code> <code>LuceneIndex</code> bean definition,
ensuring that the <code>LuceneIndex</code> is created before the Region
on which it applies.

After we have a `LuceneIndex`, we can perform Lucene-based data
access operations such as queries.

## <a id="lucene-template-data-accessors"></a>Lucene Template Data Accessors

Spring Data for VMware GemFire provides two primary templates for Lucene data access
operations, depending on how low of a level your application is prepared
to deal with.

The `LuceneOperations` interface defines query operations by using
GemFire
[Lucene types](https://geode.apache.org/releases/latest/javadoc/org/apache/geode/cache/lucene/package-summary.html), which are defined in the following interface definition:

```highlight
public interface LuceneOperations {

    <K, V> List<LuceneResultStruct<K, V>> query(String query, String defaultField [, int resultLimit]
        , String... projectionFields);

    <K, V> PageableLuceneQueryResults<K, V> query(String query, String defaultField,
        int resultLimit, int pageSize, String... projectionFields);

    <K, V> List<LuceneResultStruct<K, V>> query(LuceneQueryProvider queryProvider [, int resultLimit]
        , String... projectionFields);

    <K, V> PageableLuceneQueryResults<K, V> query(LuceneQueryProvider queryProvider,
        int resultLimit, int pageSize, String... projectionFields);

    <K> Collection<K> queryForKeys(String query, String defaultField [, int resultLimit]);

    <K> Collection<K> queryForKeys(LuceneQueryProvider queryProvider [, int resultLimit]);

    <V> Collection<V> queryForValues(String query, String defaultField [, int resultLimit]);

    <V> Collection<V> queryForValues(LuceneQueryProvider queryProvider [, int resultLimit]);
}
```
The <code>[, int resultLimit]</code> indicates that
the <code>resultLimit</code> parameter is optional.


The operations in the `LuceneOperations` interface match the operations
provided by the [LuceneQuery](https://geode.apache.org/releases/latest/javadoc/org/apache/geode/cache/lucene/LuceneQuery.html)
interface. However, Spring Data for VMware GemFire has the added value of translating
proprietary GemFire or Apache Lucene `Exceptions` into
Spring's highly consistent and expressive DAO [exception
hierarchy](https://docs.spring.io/spring/docs/current/spring-framework-reference/htmlsingle/#dao-exceptions),
particularly as many modern data access operations involve more than one
store or repository.

Additionally, Spring Data for VMware GemFire's `LuceneOperations` interface can shield
your application from interface-breaking changes introduced by the
underlying GemFire or Apache Lucene APIs when they occur.

However, it would be sad to offer a Lucene Data Access Object (DAO) that
only uses GemFire and Apache Lucene data types (such as
GemFire's `LuceneResultStruct`). Therefore, Spring Data for VMware GemFire
gives you the `ProjectingLuceneOperations` interface to remedy these
important application concerns. The following listing shows the
`ProjectingLuceneOperations` interface definition:


```highlight
public interface ProjectingLuceneOperations {

    <T> List<T> query(String query, String defaultField [, int resultLimit], Class<T> projectionType);

    <T> Page<T> query(String query, String defaultField, int resultLimit, int pageSize, Class<T> projectionType);

    <T> List<T> query(LuceneQueryProvider queryProvider [, int resultLimit], Class<T> projectionType);

    <T> Page<T> query(LuceneQueryProvider queryProvider, int resultLimit, int pageSize, Class<T> projectionType);
}
```

The `ProjectingLuceneOperations` interface primarily uses application
domain object types that let you work with your application data. The
`query` method variants accept a projection type, and the template
applies the query results to instances of the given projection type by
using the Spring Data Commons Projection infrastructure.

Additionally, the template wraps the paged Lucene query results in an
instance of the Spring Data Commons `Page` abstraction. The same
projection logic can still be applied to the results in the page and are
lazily projected as each page in the collection is accessed.

By way of example, suppose you have a class representing a `Person`, as
follows:

```highlight
class Person {

    Gender gender;

    LocalDate birthDate;

    String firstName;
    String lastName;

    ...

    String getName() {
        return String.format("%1$s %2$s", getFirstName(), getLastName());
    }
}
```

You might have a single interface to represent people as
`Customers`, depending on your application view, as follows:

```highlight
interface Customer {

    String getName()

}
```

If we define the following `LuceneIndex`…​

```highlight
@Bean
LuceneIndexFactoryBean personLastNameIndex(GemFireCache gemfireCache) {

    LuceneIndexFactoryBean personLastNameIndex =
        new LuceneIndexFactoryBean();

    personLastNameIndex.setCache(gemfireCache);
    personLastNameIndex.setFields("lastName");
    personLastNameIndex.setRegionPath("/People");

    return personLastNameIndex;
}
```

Then you could query for people as `Person` objects, as follows:

```highlight
List<Person> people = luceneTemplate.query("lastName: D*", "lastName", Person.class);
```

Alternatively, you could query for a `Page` of type `Customer`, as
follows:

```highlight
Page<Customer> customers = luceneTemplate.query("lastName: D*", "lastName", 100, 20, Customer.class);
```

The `Page` can then be used to fetch individual pages of the results, as
follows:

```highlight
List<Customer> firstPage = customers.getContent();
```

Conveniently, the Spring Data Commons `Page` interface also implements
`java.lang.Iterable<T>`, making it easy to iterate over the contents.

The only restriction to the Spring Data Commons Projection
infrastructure is that the projection type must be an interface.
However, it is possible to extend the provided SDC Projection
infrastructure and provide a custom
[ProjectionFactory](https://docs.spring.io/spring-data/commons/docs/current/api/org/springframework/data/projection/ProjectionFactory.html)
that uses [CGLIB](https://github.com/cglib/cglib) to generate proxy
classes as the projected entity.

You can use `setProjectionFactory(:ProjectionFactory)` to set a custom
`ProjectionFactory` on a Lucene template.

## <a id="annotation-configuration-support"></a>Annotation Configuration Support

Finally, Spring Data for VMware GemFire provides annotation configuration support for
`LuceneIndexes`.

You can express `LuceneIndexes` directly on your application domain
objects, as the following example shows:

```highlight
@PartitionRegion("People")
class Person {

    Gender gender;

    @Index
    LocalDate birthDate;

    String firstName;

    @LuceneIndex;
    String lastName;

    ...
}
```

To enable this feature, you must use Spring Data for VMware GemFire's annotation
configuration support specifically with the `@EnableEntityDefineRegions`
and `@EnableIndexing` annotations, as follows:

```highlight
@PeerCacheApplication
@EnableEntityDefinedRegions
@EnableIndexing
class ApplicationConfiguration {

  ...
}
```

<code>LuceneIndexes</code> can only be created on
GemFire servers because <code>LuceneIndexes</code> only apply to
<code>PARTITION</code> Regions.

Given the earlier definition of the `Person` class, the Spring Data for VMware GemFire
annotation configuration support finds the `Person` entity class
definition and determines that people are stored in a `PARTITION` Region
called "People" and that the `Person` has an OQL `Index` on `birthDate`
along with a `LuceneIndex` on `lastName`.
