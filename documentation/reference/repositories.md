---
title: Spring Data for VMware GemFire Repositories
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

Spring Data for VMware GemFire provides support for using the Spring Data Repository
abstraction to easily persist entities into GemFire along with
executing queries. For a general introduction to the Repository programming
model, see [Working with Spring Data Repositories](https://docs.spring.io/spring-data/data-commons/docs/current/reference/html/#repositories) in the Spring Data Commons Reference Documentation.

## <a id="spring-xml-configuration"></a>Spring XML Configuration

To bootstrap Spring Data Repositories, use the `<repositories/>` element
from the Spring Data for VMware GemFire Data namespace, as the following example shows:

**Example 1. Bootstrap Spring Data for VMware GemFire Repositories in XML**

```highlight
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:gfe-data="{spring-data-access-schema-namespace}"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xsi:schemaLocation="
    http://www.springframework.org/schema/beans https://www.springframework.org/schema/beans/spring-beans.xsd
    {spring-data-access-schema-namespace} {spring-data-access-schema-location}
">

  <gfe-data:repositories base-package="com.example.acme.repository"/>

</beans>
```

The preceding configuration snippet looks for interfaces below the
configured base package and creates Repository instances for those
interfaces backed by a
[SimpleGemFireRepository](https://docs.spring.io/spring-data/geode/docs/current/api/org/springframework/data/gemfire/repository/support/SimpleGemfireRepository.html).

<p class="note"><strong>Note</strong>: The bootstrap process fails unless you have correctly mapped your application domain classes to configured Regions.</p>

## <a id="spring-java-based-configuration"></a>Spring Java-based Configuration

You can also use Spring's Java-based container configuration. For more information about this configuration, see [Java-based Container Configuration](https://docs.spring.io/spring-framework/docs/current/reference/html/core.html#beans-java) in _Core Technologies_ in the Spring product documentation.

Using this approach, you can bootstrap Spring Data Repositories by using
the Spring Data for VMware GemFire `@EnableGemfireRepositories` annotation, as the
following example shows:

**Example 2. Bootstrap Spring Data for VMware GemFire Repositories with `@EnableGemfireRepositories`**

```highlight
@SpringBootApplication
@EnableGemfireRepositories(basePackages = "com.example.acme.repository")
class SpringDataApplication {
  ...
}
```

You can use the type-safe `basePackageClasses` attribute instead of using the `basePackages`
attribute. The `basePackageClasses` lets you specify the package that contains all your
application Repository classes by specifying only one of your
application Repository interface types. Consider creating a special
no-op marker class or interface in each package that serves no purpose
other than to identify the location of application Repositories
referenced by this attribute.

In addition to the `basePackages and basePackageClasses` attributes,
like Spring's [@ComponentScan](https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/context/annotation/ComponentScan.html)
annotation, the `@EnableGemfireRepositories` annotation provides include
and exclude filters, based on Spring's
[ComponentScan.Filter](https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/context/annotation/ComponentScan.Filter.html)
type. You can use the `filterType` attribute to filter by different
aspects, such as whether an application Repository type is annotated
with a particular annotation or extends a particular class type. For more details, see the [FilterType Javadoc](https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/context/annotation/FilterType.html).

The `@EnableGemfireRepositories` annotation also lets you specify the
location of named OQL queries, which reside in a Java `Properties` file,
by using the `namedQueriesLocation` attribute. The property name must
match the name of a Repository query method and the property value is
the OQL query you want executed when the Repository query method is
called.

The `repositoryImplementationPostfix` attribute can be set to an
alternate value (defaults to `Impl`) if your application requires one or
more [custom repository implementations](https://docs.spring.io/spring-data/commons/docs/current/reference/html/#repositories.custom-implementations). This feature is commonly used to extend
the Spring Data Repository infrastructure to implement a feature not
provided by the data store (for example, Spring Data for VMware GemFire).

One example of where custom repository implementations are needed with
GemFire is when performing joins. Joins are not supported by
Spring Data for VMware GemFire Repositories. With a GemFire `PARTITION` Region,
the join must be performed on collocated `PARTITION` Regions, since
GemFire does not support "distributed" joins. In addition, the
Equi-Join OQL Query must be performed inside a GemFire
Function. For more information about GemFire Equi-Join Queries, see [Performing an Equi-Join Query on Partitioned Regions](https://docs.vmware.com/en/VMware-Tanzu-GemFire/9.15/tgf/GUID-developing-partitioned_regions-join_query_partitioned_regions.html) in the GemFire product documentation.

You can customize many other aspects of the Spring Data for VMware GemFire's Repository infrastructure
extension. For details about all configuration settings, see [@EnableGemfireRepositories](https://docs.spring.io/spring-data/gemfire/docs/current/api/org/springframework/data/gemfire/repository/config/EnableGemfireRepositories.html).

## <a id="executing-oql-queries"></a>Executing OQL Queries

Spring Data for VMware GemFire Repositories enable the definition of query methods to easily
execute GemFire OQL queries against the Region the managed
entity maps to, as the following example shows:

**Example 3. Sample Repository**

```highlight
@Region("People")
public class Person { … }
```

```highlight
public interface PersonRepository extends CrudRepository<Person, Long> {

  Person findByEmailAddress(String emailAddress);

  Collection<Person> findByFirstname(String firstname);

  @Query("SELECT * FROM /People p WHERE p.firstname = $1")
  Collection<Person> findByFirstnameAnnotated(String firstname);

  @Query("SELECT * FROM /People p WHERE p.firstname IN SET $1")
  Collection<Person> findByFirstnamesAnnotated(Collection<String> firstnames);
}
```

The first query method listed in the preceding example causes the
following OQL query to be derived:
`SELECT x FROM /People x WHERE x.emailAddress = $1`. The second query
method works the same way except it returns all entities found, whereas
the first query method expects a single result to be found.

If the supported keywords are not sufficient to declare and express your
OQL query, or the method name becomes too verbose, then you can annotate
the query methods with `@Query` as shown on the third and fourth
methods.

The following table gives brief samples of the supported keywords that
you can use in query methods:


<table>
  <caption>Table 1. Supported keywords for query methods</caption>
  <colgroup>
    <col style="width: 20%" />
    <col style="width: 40%" />
    <col style="width: 40%" />
  </colgroup>
  <thead>
    <tr>
      <th>Keyword</th>
      <th>Sample</th>
      <th>Logical result</th>
    </tr>
  </thead>
  <tbody>
    <tr>
      <td><p><code>GreaterThan</code></p></td>
      <td><p><code>findByAgeGreaterThan(int age)</code></p></td>
      <td><p><code>x.age > $1</code></p></td>
    </tr>
    <tr>
      <td><p><code>GreaterThanEqual</code></p></td>
      <td><p><code>findByAgeGreaterThanEqual(int age)</code></p></td>
      <td><p><code>x.age >= $1</code></p></td>
    </tr>
    <tr>
      <td><p><code>LessThan</code></p></td>
      <td><p><code>findByAgeLessThan(int age)</code></p></td>
      <td><p><code>x.age < $1</code></p></td>
    </tr>
    <tr>
      <td><p><code>LessThanEqual</code></p></td>
      <td><p><code>findByAgeLessThanEqual(int age)</code></p></td>
      <td><p><code>x.age ⇐ $1</code></p></td>
    </tr>
    <tr>
      <td><p><code>IsNotNull</code>,<code>NotNull</code></p></td>
      <td><p><code>findByFirstnameNotNull()</code></p></td>
      <td><p><code>x.firstname =! NULL</code></p></td>
    </tr>
    <tr>
      <td><p><code>IsNull</code>,<code>Null</code></p></td>
      <td><p><code>findByFirstnameNull()</code></p></td>
      <td><p><code>x.firstname = NULL</code></p></td>
    </tr>
    <tr>
      <td><p><code>In</code></p></td>
      <td><p><code>findByFirstnameIn(Collection<String> x)</code></p></td>
      <td><p><code>x.firstname IN SET $1</code></p></td>
    </tr>
    <tr>
      <td><p><code>NotIn</code></p></td>
      <td><p><code>findByFirstnameNotIn(Collection<String> x)</code></p></td>
      <td><p><code>x.firstname NOT IN SET $1</code></p></td>
    </tr>
    <tr>
      <td><p><code>IgnoreCase</code></p></td>
      <td><p><code>findByFirstnameIgnoreCase(String firstName)</code></p></td>
      <td><p><code>x.firstname.equalsIgnoreCase($1)</code></p></td>
    </tr>
    <tr>
      <td><p>(No keyword)</p></td>
      <td><p><code>findByFirstname(String name)</code></p></td>
      <td><p><code>x.firstname = $1</code></p></td>
    </tr>
    <tr>
      <td><p><code>Like</code></p></td>
      <td><p><code>findByFirstnameLike(String name)</code></p></td>
      <td><p><code>x.firstname LIKE $1</code></p></td>
    </tr>
    <tr>
      <td><p><code>Not</code></p></td>
      <td><p><code>findByFirstnameNot(String name)</code></p></td>
      <td><p><code>x.firstname != $1</code></p></td>
    </tr>
    <tr>
      <td><p><code>IsTrue</code>,<code>True</code></p></td>
      <td><p><code>findByActiveIsTrue()</code></p></td>
      <td><p><code>x.active = true</code></p></td>
    </tr>
    <tr>
      <td><p><code>IsFalse</code>,<code>False</code></p></td>
      <td><p><code>findByActiveIsFalse()</code></p></td>
      <td><p><code>x.active = false</code></p></td>
    </tr>
  </tbody>
</table>

## <a id="oql--query-extensions-using-annotations"></a>OQL Query Extensions Using Annotations


Many query languages, such as GemFire's OQL (Object Query
Language), have extensions that are not directly supported by Spring
Data Commons' Repository infrastructure.

One of Spring Data Commons' Repository infrastructure goals is to
function as the lowest common denominator to maintain support for and
portability across the widest array of data stores available and in use
for application development today. Technically, this means developers
can access multiple different data stores supported by Spring Data
Commons within their applications by reusing their existing
application-specific Repository interfaces — a convenient and powerful
abstraction.

To support GemFire's OQL Query language extensions and
preserve portability across different data stores, Spring Data for VMware GemFire adds
support for OQL Query extensions by using Java annotations. These
annotations are ignored by other Spring Data Repository implementations
(such as Spring Data JPA or Spring Data Redis) that do not have similar
query language features.

For example, many data stores most likely do not implement
GemFire's OQL `IMPORT` keyword. Implementing `IMPORT` as an
annotation (that is, `@Import`) rather than as part of the query method
signature (specifically, the method 'name') does not interfere with the
parsing infrastructure when evaluating the query method name to
construct another data store language appropriate query.

The set of GemFire OQL Query language extensions that are supported by Spring Data for VMware GemFire
include the following:

<table>
  <caption>Table 2. Supported GemFire OQL extensions for Repository query methods</caption>
  <colgroup>
    <col style="width: 11%" />
    <col style="width: 29%" />
    <col style="width: 29%" />
    <col style="width: 29%" />
  </colgroup>
  <thead>
    <tr>
      <th>Keyword</th>
      <th>Annotation</th>
      <th>Description</th>
      <th>Arguments</th>
    </tr>
  </thead>
  <tbody>
    <tr>
      <td><a href="https://docs.vmware.com/en/VMware-Tanzu-GemFire/9.15/tgf/GUID-developing-query_index-query_index_hints.html">HINT</a></td>
      <td><code>@Hint</code></td>
      <td>OQL query index hints</td>
      <td><code>String[]</code> (Example: @Hint({ "IdIdx", "TxDateIdx" }))</td>
    </tr>
    <tr>
      <td><a href="https://docs.vmware.com/en/VMware-Tanzu-GemFire/9.15/tgf/GUID-developing-query_select-the_import_statement.html">IMPORT</a></td>
      <td><code>@Import</code></td>
      <td>Qualify application-specific types.</td>
      <td><code>String</code> (Example: @Import("org.example.app.domain.Type"))</td>
    </tr>
    <tr>
      <td><a href="https://docs.vmware.com/en/VMware-Tanzu-GemFire/9.15/tgf/GUID-developing-query_select-the_select_statement.html#limit">LIMIT</a></td>
      <td><code>@Limit</code></td>
      <td>Limit the returned query result set.</td>
      <td><code>Integer</code> (Example: @Limit(10); default is Integer.MAX_VALUE)</td>
    </tr>
    <tr>
      <td><a href="https://docs.vmware.com/en/VMware-Tanzu-GemFire/9.15/tgf/GUID-developing-query_additional-query_debugging.html">TRACE</a></td>
      <td><code>@Trace</code></td>
      <td>Enable OQL query-specific debugging.</td>
      <td>N/A</td>
    </tr>
  </tbody>
</table>

As an example, suppose you have a `Customers` application domain class
and corresponding GemFire Region along with a
`CustomerRepository` and a query method to lookup `Customers` by last
name, as follows:

**Example 4. Sample Customers Repository**

```highlight
package ...;

import org.springframework.data.annotation.Id;
import org.springframework.data.gemfire.mapping.annotation.Region;
...

@Region("Customers")
public class Customer ... {

  @Id
  private Long id;

  ...
}
```

```highlight
package ...;

import org.springframework.data.gemfire.repository.GemfireRepository;
...

public interface CustomerRepository extends GemfireRepository<Customer, Long> {

  @Trace
  @Limit(10)
  @Hint("LastNameIdx")
  @Import("org.example.app.domain.Customer")
  List<Customer> findByLastName(String lastName);

  ...
}
```

The preceding example results in the following OQL Query:

`<TRACE> <HINT 'LastNameIdx'> IMPORT org.example.app.domain.Customer; SELECT * FROM /Customers x WHERE x.lastName = $1 LIMIT 10`

Spring Data for VMware GemFire's Repository extension is careful not to create conflicting
declarations when the OQL annotation extensions are used in combination
with the `@Query` annotation.

As another example, suppose you have a raw `@Query` annotated query
method defined in your `CustomerRepository`, as follows:

**Example 5. CustomerRepository**

```highlight
public interface CustomerRepository extends GemfireRepository<Customer, Long> {

  @Trace
  @Limit(10)
  @Hint("CustomerIdx")
  @Import("org.example.app.domain.Customer")
  @Query("<TRACE> <HINT 'ReputationIdx'> SELECT DISTINCT * FROM /Customers c WHERE c.reputation > $1 ORDER BY c.reputation DESC LIMIT 5")
  List<Customer> findDistinctCustomersByReputationGreaterThanOrderByReputationDesc(Integer reputation);

}
```

The preceding query method results in the following OQL query:

`IMPORT org.example.app.domain.Customer; <TRACE> <HINT 'ReputationIdx'> SELECT DISTINCT * FROM /Customers x WHERE x.reputation > $1 ORDER BY c.reputation DESC LIMIT 5`

The `@Limit(10)` annotation does not override the `LIMIT` explicitly
defined in the raw query. Also, the `@Hint("CustomerIdx")` annotation
does not override the `HINT` explicitly defined in the raw query.
Finally, the `@Trace` annotation is redundant and has no additional
effect.

<p class="note"><strong>Note</strong>: The <code>ReputationIdx</code> index is probably not the most
sensible index, given the number of customers who may possibly have the
same value for their reputation, which reduces the effectiveness of the
index. Please choose indexes and other optimizations wisely, as an
improper or poorly chosen index can have the opposite effect on your
performance because of the overhead in maintaining the index. The
<code>ReputationIdx</code> was used only to serve the purpose of the
example.</p>

## <a id="query-post-processing"></a>Query Post Processing

Thanks to using the Spring Data Repository abstraction, the query method
convention for defining data store specific queries (e.g. OQL) is easy
and convenient. However, it is sometimes desirable to still want to
inspect or even possibly modify the query generated from the Repository
query method.

Since 2.0.x, Spring Data for VMware GemFire includes the
`o.s.d.gemfire.repository.query.QueryPostProcessor` functional
interface. The interface is loosely defined as follows:

**Example 6. QueryPostProcessor**

```highlight
package org.springframework.data.gemfire.repository.query;

import org.springframework.core.Ordered;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.QueryMethod;
import ...;

@FunctionalInterface
interface QueryPostProcessor<T extends Repository, QUERY> extends Ordered {

  QUERY postProcess(QueryMethod queryMethod, QUERY query, Object... arguments);

}
```

There are additional default methods provided that let you compose
instances of `QueryPostProcessor` similar to the way that
[java.util.function.Function.andThen(:Function)](https://docs.oracle.com/javase/8/docs/api/java/util/function/Function.html#compose-java.util.function.Function-)
and
[java.util.function.Function.compose(:Function)](https://docs.oracle.com/javase/8/docs/api/java/util/function/Function.html#compose-java.util.function.Function-)
work.

Additionally, the `QueryPostProcessor` interface implements the
[org.springframework.core.Ordered](https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/core/Ordered.html)
interface, which is useful when multiple `QueryPostProcessors` are
declared and registered in the Spring container and used to create a
pipeline of processing for a group of generated query method queries.

Finally, the `QueryPostProcessor` accepts type arguments corresponding
to the type parameters, `T` and `QUERY`, respectively. Type `T` extends
the Spring Data Commons marker interface,
[org.springframework.data.repository.Repository](https://docs.spring.io/spring-data/commons/docs/current/api/org/springframework/data/repository/Repository.html).
We discuss this further later in this section. All `QUERY` type
parameter arguments in Spring Data for VMware GemFire's case are of type `java.lang.String`.

<p class="note"><strong>Note</strong>: Note
	It is useful to define the query as type
<code>QUERY</code>, since this <code>QueryPostProcessor</code> interface
may be ported to Spring Data Commons and therefore must handle all forms
of queries by different data stores such as JPA, MongoDB, or
Redis.</p>

You can implement this interface to receive a callback with the query
that was generated from the application `Repository` interface method
when the method is called.

For example, you can log all queries from all application
Repository interface definitions. Do this by using the following
`QueryPostProcessor` implementation:

**Example 7. LoggingQueryPostProcessor**

```highlight
package example;

import ...;

class LoggingQueryPostProcessor implements QueryPostProcessor<Repository, String> {

  private Logger logger = Logger.getLogger("someLoggerName");

  @Override
  public String postProcess(QueryMethod queryMethod, String query, Object... arguments) {

      String message = String.format("Executing query [%s] with arguments [%s]", query, Arrays.toString(arguments));

      this.logger.info(message);
  }
}
```

The `LoggingQueryPostProcessor` was typed to the Spring Data
`org.springframework.data.repository.Repository` marker interface, and,
therefore, logs all application Repository interface query method
generated queries.

You could limit the scope of this logging to queries only from certain
types of application Repository interfaces, such as a
`CustomerRepository`, as the following example shows:

**Example 8. CustomerRepository**

```highlight
interface CustomerRepository extends CrudRepository<Customer, Long> {

  Customer findByAccountNumber(String accountNumber);

  List<Customer> findByLastNameLike(String lastName);

}
```

Then you could have typed the `LoggingQueryPostProcessor` specifically
to the `CustomerRepository`, as follows:

**Example 9. CustomerLoggingQueryPostProcessor**

```highlight
class LoggingQueryPostProcessor implements QueryPostProcessor<CustomerRepository, String> { .. }
```

As a result, only queries defined in the `CustomerRepository` interface,
such as `findByAccountNumber`, are logged.

You might want to create a `QueryPostProcessor` for a specific query
defined by a Repository query method. For example, suppose you want to
limit the OQL query generated from the
`CustomerRepository.findByLastNameLike(:String)` query method to only
return five results along with ordering the `Customers` by `firstName`,
in ascending order . To do so, you can define a custom
`QueryPostProcessor`, as the following example shows:

**Example 10. OrderedLimitedCustomerByLastNameQueryPostProcessor**

```highlight
class OrderedLimitedCustomerByLastNameQueryPostProcessor implements QueryPostProcessor<CustomerRepository, String> {

  private final int limit;

  public OrderedLimitedCustomerByLastNameQueryPostProcessor(int limit) {
    this.limit = limit;
  }

  @Override
  public String postProcess(QueryMethod queryMethod, String query, Object... arguments) {

    return "findByLastNameLike".equals(queryMethod.getName())
      ? query.trim()
          .replace("SELECT", "SELECT DISTINCT")
          .concat(" ORDER BY firstName ASC")
          .concat(String.format(" LIMIT %d", this.limit))
      : query;
  }
}
```

While the preceding example works, you can achieve the same effect by
using the Spring Data Repository convention provided by Spring Data for VMware GemFire. For
example, the same query could be defined as follows:

**Example 11. CustomerRepository using the convention**

```highlight
interface CustomerRepository extends CrudRepository<Customer, Long> {

  @Limit(5)
  List<Customer> findDistinctByLastNameLikeOrderByFirstNameDesc(String lastName);

}
```

However, if you do not have control over the application
`CustomerRepository` interface definition, then the `QueryPostProcessor`
(that is, `OrderedLimitedCustomerByLastNameQueryPostProcessor`) is
convenient.

If you want to ensure that the `LoggingQueryPostProcessor` always comes
after the other application-defined `QueryPostProcessors` that may have
bean declared and registered in the Spring `ApplicationContext`, you can
set the `order` property by overriding the `o.s.core.Ordered.getOrder()`
method, as the following example shows:

**Example 12. Defining the `order` property**

```highlight
class LoggingQueryPostProcessor implements QueryPostProcessor<Repository, String> {

  @Override
  int getOrder() {
    return 1;
  }
}

class CustomerQueryPostProcessor implements QueryPostProcessor<CustomerRepository, String> {

  @Override
  int getOrder() {
    return 0;
  }
}
```

This ensures that you always see the effects of the post processing
applied by other `QueryPostProcessors` before the
`LoggingQueryPostProcessor` logs the query.

You can define as many `QueryPostProcessors` in the Spring
`ApplicationContext` as you like and apply them in any order, to all or
specific application Repository interfaces, and be as granular as you
like by using the provided arguments to the `postProcess(..)` method
callback.
