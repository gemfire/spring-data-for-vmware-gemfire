---
title: Working with [vmware-gemfire-short-name] APIs
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



Once the [vmware-gemfire-short-name] Cache and Regions have been configured, they
can be injected and used inside application objects. This chapter
describes the integration with Spring's Transaction Management
functionality and DAO exception hierarchy. This chapter also covers
support for dependency injection of [vmware-gemfire-short-name] managed objects.

## <a id="gemfiretemplate"></a>GemfireTemplate

As with many other high-level abstractions provided by Spring,
[spring-data-gemfire-name] provides a **template** to simplify [vmware-gemfire-short-name] data
access operations. The class provides several methods containing common
Region operations, but also provides the capability to **execute** code
against native [vmware-gemfire-short-name] APIs without having to deal with
[vmware-gemfire-short-name] checked exceptions by using a `GemfireCallback`.

The template class requires a [vmware-gemfire-short-name] `Region`, and once
configured, is thread-safe and is reusable across multiple application
classes:

```highlight
<bean id="gemfireTemplate" class="org.springframework.data.gemfire.GemfireTemplate" p:region-ref="SomeRegion"/>
```

Once the template is configured, a developer can use it alongside
`GemfireCallback` to work directly with the [vmware-gemfire-short-name] `Region`
without having to deal with checked exceptions, threading or resource
management concerns:

```highlight
template.execute(new GemfireCallback<Iterable<String>>() {

    public Iterable<String> doInGemfire(Region region)
            throws GemFireCheckedException, GemFireException {

        Region<String, String> localRegion = (Region<String, String>) region;

        localRegion.put("1", "one");
        localRegion.put("3", "three");

        return localRegion.query("length < 5");
    }
});
```

For accessing the full power of the [vmware-gemfire-short-name] query language, a
developer can use the `find` and `findUnique` methods, which, compared
to the `query` method, can execute queries across multiple Regions,
execute projections, and the like.

The `find` method should be used when the query selects multiple items
(through `SelectResults`) and the latter, `findUnique`, as the name
suggests, when only one object is returned.

## <a id="exception-translation"></a>Exception Translation

Using a new data access technology requires not only accommodating a new
API but also handling exceptions specific to that technology.

To accommodate the exception handling case, the *Spring Framework*
provides a technology agnostic and consistent [exception
hierarchy](https://docs.spring.io/spring-framework/docs/current/reference/html/data-access.html#dao-exceptions)
that abstracts the application from proprietary, and usually "checked",
exceptions to a set of focused runtime exceptions.

As mentioned in the Spring Framework documentation, [Exception
translation](https://docs.spring.io/spring-framework/docs/current/reference/html/data-access.html#orm-exception-translation)
can be applied transparently to your Data Access Objects (DAO) through
the use of the `@Repository` annotation and AOP by defining a
`PersistenceExceptionTranslationPostProcessor` bean. The same exception
translation functionality is enabled when using [vmware-gemfire-short-name] as
long as the `CacheFactoryBean` is declared, e.g. using either a
`<gfe:cache/>` or `<gfe:client-cache>` declaration, which acts as an
exception translator and is automatically detected by the Spring
infrastructure and used accordingly.

## <a id="local-cache-transaction-management"></a>Local, Cache Transaction Management

One of the most popular features of the Spring Framework is
[Transaction Management](https://docs.spring.io/spring-framework/docs/current/reference/html/data-access.html#transaction).

For more information about Spring's transaction abstraction, see
[Advantages of the Spring Framework’s Transaction Support Model](https://docs.spring.io/spring-framework/docs/current/reference/html/data-access.html#transaction-motivation)
in the Spring documentation.

For [vmware-gemfire-short-name], [spring-data-gemfire-name] provides a dedicated, per-cache,
`PlatformTransactionManager` that, once declared, allows Region
operations to be executed atomically through Spring:

**Enable Transaction Management using XML**

```highlight
<gfe:transaction-manager id="txManager" cache-ref="myCache"/>
```

<p class="note"><strong>Note</strong>: The example above can be simplified even further by
eliminating the <code>cache-ref</code> attribute if the
[vmware-gemfire-short-name] cache is defined under the default name,
<code>gemfireCache</code>. As with the other [spring-data-gemfire-name] namespace
elements, if the cache bean name is not configured, the aforementioned
naming convention will be used. Additionally, the transaction manager
name is "gemfireTransactionManager" if not explicitly specified.</p>

[vmware-gemfire-short-name] supports optimistic transactions with
**read committed** isolation. Furthermore, to guarantee this isolation,
developers should avoid making **in-place** changes that manually modify
values present in the cache. To prevent this from happening, the
transaction manager configures the cache to use **copy on read**
semantics by default, meaning a clone of the actual value is created
each time a read is performed. This behavior can be disabled if needed
through the `copyOnRead` property.

Since a copy of the value for a given key is made when **copy on read**
is enabled, you must subsequently call `Region.put(key, value)` inorder
for the value to be updated, transactionally.

For more information about the semantics and behavior of the underlying
[vmware-gemfire-short-name] transaction manager, see:

* [CacheTransactionManager Javadoc](https://geode.apache.org/releases/latest/javadoc/org/apache/geode/cache/CacheTransactionManager.html)
* [Transactions](https://docs.vmware.com/en/VMware-GemFire/9.15/gf/developing-transactions-chapter_overview.html) in the [vmware-gemfire-short-name] product documentation

## <a id="global-jta-transaction-management"></a>Global - JTA Transaction Management

It is also possible for [vmware-gemfire-short-name] to participate in Global,
JTA-based transactions, such as a transaction managed by an Java EE
Application Server (e.g. WebSphere Application Server (WAS)) using
Container Managed Transactions (CMT) along with other JTA resources.

However, unlike many other JTA "compliant" resources (e.g. JMS Message
Brokers like ActiveMQ), [vmware-gemfire-short-name] is **not** an XA compliant
resource. Therefore, [vmware-gemfire-short-name] must be positioned as the "*Last
Resource*" in a JTA transaction (*prepare phase*) since it does not
implement the 2-phase commit protocol, or rather does not handle
distributed transactions.

Many managed environments capable of CMT maintain support for "*Last
Resource*", non-XA compliant resources in JTA-based transactions, though
it is not actually required in the JTA spec. For more information about what a
non-XA compliant, "Last Resource" means, see [Last Resource Commit Optimization (LRCO)](https://access.redhat.com/documentation/en-us/jboss_enterprise_application_platform/5/html/administration_and_configuration_guide/lrco-overview)
in the Red Hat documentation.

However, whether you are using [vmware-gemfire-short-name] in a standalone
environment with an Open Source JTA Transaction Management
implementation that supports "*Last Resource*", or a managed environment
(e.g. Java EE AS such as WAS), [spring-data-gemfire-name] has you covered.

There are a series of steps you must complete to properly use
[vmware-gemfire-short-name] as a "*Last Resource*" in a JTA transaction involving
more than one transactional resource. Additionally, there can only be 1
non-XA compliant resource (e.g. [vmware-gemfire-short-name]) in such an
arrangement.

1\. First, you must complete Steps 1-4 in [JTA Global Transactions with [vmware-gemfire-short-name]](https://docs.vmware.com/en/VMware-GemFire/9.15/gf/reference-archive_transactions-JTA_transactions.html).
This is independent of your Spring [Boot] and/or [Data for [vmware-gemfire-short-name]] application and must be completed
successfully.

2\. Referring to Step 5 in [vmware-gemfire-short-name]'s [JTA Global Transactions with [vmware-gemfire-short-name]](https://docs.vmware.com/en/VMware-GemFire/9.15/gf/reference-archive_transactions-JTA_transactions.html),
[spring-data-gemfire-name]'s Annotation support will attempt to set the `GemFireCache`,
[copyOnRead](https://geode.apache.org/releases/latest/javadoc/org/apache/geode/cache/GemFireCache.html#setCopyOnRead-boolean-)
property for you when using the `@EnableGemFireAsLastResource` annotation.f [spring-data-gemfire-name]'s auto-configuration is unsuccessful in this regard,
then you must explicitly set the `copy-on-read` attribute in the
`<gfe:cache>` or `<gfe:client-cache>` XML element or set the
`copyOnRead` property of the `CacheFactoryBean` class in JavaConfig to
**true**. For example:

`ClientCache` XML:

**Set copy-on-read using XML (client)**

```highlight
<gfe:client-cache ... copy-on-read="true"/>
```
`ClientCache` *JavaConfig*:

**Set copyOnRead using JavaConfig (client)**

```highlight
@Bean
ClientCacheFactoryBean gemfireCache() {

  ClientCacheFactoryBean gemfireCache = new ClientCacheFactoryBean();

  gemfireCache.setCopyOnRead(true);

  return gemfireCache;
}
```

Peer `Cache` XML:

**Set copy-on-read using XML (server)**

```highlight
<gfe:cache ... copy-on-read="true"/>
```
Peer `Cache` *JavaConfig*:

**Set copyOnRead using JavaConfig (server)**

```highlight
@Bean
CacheFactoryBean gemfireCache() {

  CacheFactoryBean gemfireCache = new CacheFactoryBean();

  gemfireCache.setCopyOnRead(true);

  return gemfireCache;
}
```

<p class="note"><strong>Note</strong>: Explicitly setting the <code>copy-on-read</code>
attribute or the <code>copyOnRead</code> property unnecessary. Enabling transaction management takes cae of copying on
reads.</p>

3\. Skip Steps 6-8 in [vmware-gemfire-short-name]'s
[JTA Global Transactions with [vmware-gemfire-short-name]](https://docs.vmware.com/en/VMware-GemFire/9.15/gf/reference-archive_transactions-JTA_transactions.html).
Annotate your Spring `@Configuration` class with [spring-data-gemfire-name]'s
`@EnableGemFireAsLastResource` annotation and a combination of Spring's
[Transaction Management](https://docs.spring.io/spring-framework/docs/current/reference/html/data-access.html#transaction)
infrastructure and [spring-data-gemfire-name]'s `@EnableGemFireAsLastResource`
annotation configuration completes the task.

The configuration looks like this:

```highlight
@Configuration
@EnableGemFireAsLastResource
@EnableTransactionManagement(order = 1)
class GeodeConfiguration {
  ...
}
```

The only requirements are:

3.1 The `@EnableGemFireAsLastResource` annotation must be declared on
the same Spring `@Configuration` class where Spring's
`@EnableTransactionManagement` annotation is also specified.



3.2 The `order` attribute of the `@EnableTransactionManagement`
annotation must be explicitly set to an integer value that is not
`Integer.MAX_VALUE` or `Integer.MIN_VALUE` (defaults to
`Integer.MAX_VALUE`).


You must also configure Spring's `JtaTransactionManager` when using JTA transactions as follows:


```highlight
@Bean
public JtaTransactionManager transactionManager(UserTransaction userTransaction) {

   JtaTransactionManager transactionManager = new JtaTransactionManager();

   transactionManager.setUserTransaction(userTransaction);

   return transactionManager;
}
```
<p class="note"><strong>Note</strong>: 
The configuration in section <a
href="#local-cache-transaction-management">Local, Cache Transaction Management</a> does
not apply in this case. The use of
[spring-data-gemfire-name]'s <code>GemfireTransactionManager</code> is applicable in
"Local-only", Cache Transactions, <strong>not</strong> "Global", JTA
Transactions. Therefore, do not configure the [spring-data-gemfire-name]
<code>GemfireTransactionManager</code> in this case. Instead, configure
Spring's <code>JtaTransactionManager</code> as shown above.</p>

Effectively, [spring-data-gemfire-name]'s `@EnableGemFireAsLastResource` annotation
imports configuration containing two Aspect bean definitions that handles
the [vmware-gemfire-short-name] `o.a.g.ra.GFConnectionFactory.getConnection()` and
`o.a.g.ra.GFConnection.close()` operations at the appropriate points
during the transactional operation.

Specifically, the correct sequence of events follow:

1. `jtaTransation.begin()`

2. `GFConnectionFactory.getConnection()`

3. Call the application's `@Transactional` service method

4. Either `jtaTransaction.commit()` or `jtaTransaction.rollback()`

5. Finally, `GFConnection.close()`

After applying the appropriate configuration, shown above:

**Declaring a service method as @Transactional**

```highlight
@Service
class MyTransactionalService {

  @Transactional
  public <Return-Type> someTransactionalServiceMethod() {
    // perform business logic interacting with and accessing multiple JTA resources atomically
  }

  ...
}
```

\#1 & \#4 above are appropriately handled for you by Spring's JTA based
`PlatformTransactionManager` once the `@Transactional` boundary is
entered by your application (i.e. when the
`MyTransactionService.someTransactionalServiceMethod()` is called).

\#2 & \#3 are handled by [spring-data-gemfire-name]'s new Aspects enabled with the
`@EnableGemFireAsLastResource` annotation.

\#3 is the responsibility of your application.

With the appropriate logging configured, you will see the
correct sequence of events:

**Transaction Log Output**

```highlight
2017-Jun-22 11:11:37 TRACE TransactionInterceptor - Getting transaction for [example.app.service.MessageService.send]

2017-Jun-22 11:11:37 TRACE GemFireAsLastResourceConnectionAcquiringAspect - Acquiring [vmware-gemfire-short-name] Connection
from [vmware-gemfire-short-name] JCA ResourceAdapter registered at [gfe/jca]

2017-Jun-22 11:11:37 TRACE MessageService - PRODUCER [ Message :
[{ @type = example.app.domain.Message, id= MSG0000000000, message = SENT }],
JSON : [{"id":"MSG0000000000","message":"SENT"}] ]

2017-Jun-22 11:11:37 TRACE TransactionInterceptor - Completing transaction for [example.app.service.MessageService.send]

2017-Jun-22 11:11:37 TRACE GemFireAsLastResourceConnectionClosingAspect - Closed [vmware-gemfire-short-name] Connection @ [Reference [...]]
```

## <a id="using-transactionaleventlistener"></a>Using @TransactionalEventListener

When using transactions, you may want to register a listener to
perform certain actions before or after the transaction commits, or
after a rollback occurs.

[spring-data-gemfire-name] makes it easy to create listeners that will be invoked during
specific phases of a transaction with the `@TransactionalEventListener`
annotation. Methods annotated with `@TransactionalEventListener` (as
shown below) will be notified of events published from transactional
methods, during the specified `phase`.

**After Transaction Commit Event Listener**

```highlight
@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
public void handleAfterCommit(MyEvent event) {
    // do something after transaction is committed
}
```

For the above method to be invoked, you must publish an event
from within your transaction, as in the following example:

**Publishing a Transactional Event**

```highlight
@Service
class MyTransactionalService {

  @Autowired
  private final ApplicationEventPublisher applicationEventPublisher;

  @Transactional
  public <Return-Type> someTransactionalServiceMethod() {

    // Perform business logic interacting with and accessing multiple transactional resources atomically, then...

    applicationEventPublisher.publishEvent(new MyApplicationEvent(...));
  }

  ...
}
```

The `@TransactionalEventListener` annotation allows you to specify the
transaction `phase` in which the event handler method will be invoked.
Options include: `AFTER_COMMIT`, `AFTER_COMPLETION`, `AFTER_ROLLBACK`,
and `BEFORE_COMMIT`. If not specified, the `phase` defaults to
`AFTER_COMMIT`. If you wish the listener to be called even when no
transaction is present, you may set `fallbackExecution` to `true`.

## <a id="auto-transaction-event-publishing"></a>Auto Transaction Event Publishing

You can enable auto transaction event publishing.

Using the `@EnableGemfireCacheTransactions` annotation, set the
`enableAutoTransactionEventPublishing` attribute to **true**. The
default is **false**.

**Enable auto transaction event publishing**

```highlight
@EnableGemfireCacheTransactions(enableAutoTransactionEventPublishing = true)
class GeodeConfiguration { ... }
```

Then you can create `@TransactionalEventListener` annotated POJO methods
to handle transaction events during either the `AFTER_COMMIT` or
`AFTER_ROLLBACK` transaction phases.

```highlight
@Component
class TransactionEventListeners {

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleAfterCommit(TransactionApplicationEvent event) {
        ...
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_ROLLBACK)
    public void handleAfterRollback(TransactionApplicationEvent event) {
        ...
    }
}
```

<p class="note warning"><strong>Warning</strong>: Only <code>TransactionPhase.AFTER_COMMIT</code> and
<code>TransactionPhase.AFTER_ROLLBACK</code> are supported.
<code>TransactionPhase.BEFORE_COMMIT</code> is not supported because
[spring-data-gemfire-name] adapts [vmware-gemfire-short-name]'s <code>TransactionListener</code> and
<code>TransactionWriter</code> interfaces to implement auto transaction
event publishing, and when [vmware-gemfire-short-name]'s
<code>TransactionWriter.beforeCommit(:TransactionEvent)</code> is
called, it is already after the
<code>AbstractPlatformTransactionManager.triggerBeforeCommit(:TransactionStatus)</code>
call where <code>@TranactionalEventListener</code> annotated POJO
methods are called during the transaction lifecycle.</p>

With auto transaction event publishing, you do not need to explicitly
call the `applicationEventPublisher.publishEvent(..)` method inside your
application `@Transactional` `@Service` methods.

However, if you still want to receive transaction events "*before
commit*", then you must still call the
`applicationEventPublisher.publishEvent(..)` method within your
application `@Transactional` `@Service` methods. See the **note** above
for more details.

## <a id="continuous-query"></a>Continuous Query (CQ)

A powerful functionality offered by [vmware-gemfire-short-name] is [Continuous Query](https://docs.vmware.com/en/VMware-GemFire/9.15/gf/developing-continuous_querying-chapter_overview.html) (CQ).

CQ allows a developer to create and register an OQL query, and
then automatically be notified when new data that gets added to
[vmware-gemfire-short-name] matches the query predicate. [spring-data-gemfire-name] provides
dedicated support for CQs through the
`org.springframework.data.gemfire.listener` package and its **listener
container**; very similar in functionality and naming to the JMS
integration in the *Spring Framework*; in fact, users familiar with the
JMS support in Spring, should feel right at home.

[spring-data-gemfire-name] allows methods on POJOs to become end-points for
CQ. Simply define the query and indicate the method that should be
called to be notified when there is a match. [spring-data-gemfire-name] takes care of
the rest. This is similar to Java EE's message-driven bean style,
but without any requirement for base class or interface implementations,
based on [vmware-gemfire-short-name].

<p class="note"><strong>Note</strong>: Continuous Query is only supported in
[vmware-gemfire-short-name]'s client/server topology. Additionally, the client
Pool used is required to have the subscription enabled. For more information, see <a href="https://docs.vmware.com/en/VMware-GemFire/9.15/gf/developing-continuous_querying-implementing_continuous_querying.htmlPlease">Implementing Continuous Querying</a>.</p>

### <a id="continuous-query-listener-container"></a>Continuous Query Listener Container

[spring-data-gemfire-name] simplifies creation, registration, life-cycle, and dispatch of
CQ events by taking care of the infrastructure around CQ with the use of
[spring-data-gemfire-name]'s `ContinuousQueryListenerContainer`, which does all the heavy
lifting on behalf of the user. Users familiar with EJB and JMS should
find the concepts familiar as it is designed as close as possible to the
support provided in the *Spring Framework* with its Message-driven POJOs
(MDPs).

The [spring-data-gemfire-name] `ContinuousQueryListenerContainer` acts as an event (or message)
listener container; it is used to receive the events from the registered
CQs and invoke the POJOs that are injected into it. The listener
container is responsible for all threading of message reception and
dispatches into the listener for processing. It acts as the intermediary
between an EDP (Event-driven POJO) and the event provider and takes care
of creation and registration of CQs (to receive events), resource
acquisition and release, exception conversion and the like. This allows
you, as an application developer, to write the (possibly complex)
business logic associated with receiving an event (and reacting to it),
and delegate the boilerplate [vmware-gemfire-short-name] infrastructure concerns
to the framework.

The listener container is fully customizable. A developer can chose
either to use the CQ thread to perform the dispatch (synchronous
delivery) or a new thread (from an existing pool) for an asynchronous
approach by defining the suitable `java.util.concurrent.Executor` (or
Spring's `TaskExecutor`). Depending on the load, the number of listeners
or the runtime environment, the developer should change or tweak the
executor to better serve her needs. In particular, in managed
environments (such as app servers), it is highly recommended to pick a
proper `TaskExecutor` to take advantage of its runtime.

### <a id="continuousquerylistener-and-continuousquerylisteneradapter"></a>`ContinuousQueryListener` and `ContinuousQueryListenerAdapter`

The `ContinuousQueryListenerAdapter` class is the final component in
[spring-data-gemfire-name] CQ support. In a nutshell, class allows you to expose almost
**any** implementing class as an EDP with minimal constraints.
`ContinuousQueryListenerAdapter` implements the
`ContinuousQueryListener` interface, a simple listener interface similar
to [CqListener](https://geode.apache.org/releases/latest/javadoc/org/apache/geode/cache/query/CqListener.html).

Consider the following interface definition. Notice the various event
handling methods and their parameters:

```highlight
public interface EventDelegate {
     void handleEvent(CqEvent event);
     void handleEvent(Operation baseOp);
     void handleEvent(Object key);
     void handleEvent(Object key, Object newValue);
     void handleEvent(Throwable throwable);
     void handleQuery(CqQuery cq);
     void handleEvent(CqEvent event, Operation baseOp, byte[] deltaValue);
     void handleEvent(CqEvent event, Operation baseOp, Operation queryOp, Object key, Object newValue);
}
```

```highlight
package example;

class DefaultEventDelegate implements EventDelegate {
    // implementation elided for clarity...
}
```

In particular, note how the above implementation of the `EventDelegate`
interface has no [vmware-gemfire-short-name] dependencies. It is
a POJO that we can and will make into an EDP using the following
configuration.

The class does not have to implement an interface. An interface is only used to better showcase the decoupling between the contract and the implementation.

```highlight
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:gfe="{spring-data-schema-namespace}"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xsi:schemaLocation="
    http://www.springframework.org/schema/beans https://www.springframework.org/schema/beans/spring-beans.xsd
    {spring-data-schema-namespace} {spring-data-schema-location}
">

    <gfe:client-cache/>

    <gfe:pool subscription-enabled="true">
       <gfe:server host="localhost" port="40404"/>
    </gfe:pool>

    <gfe:cq-listener-container>
       <!-- default handle method -->
       <gfe:listener ref="listener" query="SELECT * FROM /SomeRegion"/>
       <gfe:listener ref="another-listener" query="SELECT * FROM /AnotherRegion" name="myQuery" method="handleQuery"/>
    </gfe:cq-listener-container>

    <bean id="listener" class="example.DefaultMessageDelegate"/>
    <bean id="another-listener" class="example.DefaultMessageDelegate"/>
  ...
<beans>
```

The example above shows a few of the various forms
that a listener can have; at its minimum, the listener reference and the
actual query definition are required. It's possible, however, to specify
a name for the resulting Continuous Query (useful for monitoring) but
also the name of the method (the default is <code>handleEvent</code>).
The specified method can have various argument types, the
<code>EventDelegate</code> interface lists the allowed types.

The example above uses the [spring-data-gemfire-name] namespace to declare the event
listener container and automatically register the listeners. The complete definition is displayed below:

```highlight
<!-- this is the Event Driven POJO (MDP) -->
<bean id="eventListener" class="org.springframework.data.gemfire.listener.adapter.ContinuousQueryListenerAdapter">
    <constructor-arg>
        <bean class="gemfireexample.DefaultEventDelegate"/>
    </constructor-arg>
</bean>

<!-- and this is the event listener container... -->
<bean id="gemfireListenerContainer" class="org.springframework.data.gemfire.listener.ContinuousQueryListenerContainer">
    <property name="cache" ref="gemfireCache"/>
    <property name="queryListeners">
      <!-- set of CQ listeners -->
      <set>
        <bean class="org.springframework.data.gemfire.listener.ContinuousQueryDefinition" >
               <constructor-arg value="SELECT * FROM /SomeRegion" />
               <constructor-arg ref="eventListener"/>
        </bean>
      </set>
    </property>
</bean>
```

Each time an event is received, the adapter automatically performs type
translation between the [vmware-gemfire-short-name] event and the required method
arguments transparently. Any exception caused by the method invocation
is caught and handled by the container (by default, being logged).

## <a id="wiring-declarable-components"></a>Wiring `Declarable` Components

[vmware-gemfire-short-name] XML configuration (usually referred to as `cache.xml`)
allows **user** objects to be declared as part of the configuration.
Usually these objects are `CacheLoaders` or other pluggable callback
components supported by [vmware-gemfire-short-name]. Using native
[vmware-gemfire-short-name] configuration, each user type declared through XML
must implement the `Declarable` interface, which allows arbitrary
parameters to be passed to the declared class through a `Properties`
instance.

In this section, we describe how you can configure these pluggable
components when defined in `cache.xml` using Spring while keeping your
Cache/Region configuration defined in `cache.xml`. This allows your
pluggable components to focus on the application logic and not the
location or creation of `DataSources` or other collaborators.

However, if you are starting a green field project, it is recommended
that you configure Cache, Region, and other pluggable [vmware-gemfire-short-name]
components directly in Spring. This avoids inheriting from the
`Declarable` interface or the base class presented in this section.

**Eliminate `Declarable` components**

A developer can configure custom types entirely through Spring as
mentioned in [Configuring a Regions](#region.html). That way, a
developer does not have to implement the `Declarable` interface, and
also benefits from all the features of the Spring IoC container (not
just dependency injection but also life-cycle and instance management).

As an example of configuring a `Declarable` component using Spring,
consider the following declaration.

```highlight
<cache-loader>
   <class-name>com.company.app.DBLoader</class-name>
   <parameter name="URL">
     <string>jdbc://12.34.56.78/mydb</string>
   </parameter>
</cache-loader>
```

To simplify the task of parsing, converting the parameters and
initializing the object, [spring-data-gemfire-name] offers a base class
(`WiringDeclarableSupport`) that allows [vmware-gemfire-short-name] user objects
to be wired through a **template** bean definition or, in case that is
missing, perform auto-wiring through the Spring IoC container. To take
advantage of this feature, the user objects need to extend
`WiringDeclarableSupport`, which automatically locates the declaring
`BeanFactory` and performs wiring as part of the initialization process.

**Why is a base class needed?**

In [vmware-gemfire-short-name], there is no concept of an **object factory** and the types declared are instantiated and used as
is. In other words, there is no easy way to manage object creation outside of [vmware-gemfire-short-name].

### <a id="configuration-using-template-bean-definitions"></a>Configuration using **template** bean definitions

When used, `WiringDeclarableSupport` tries to first locate an existing
bean definition and use that as the wiring template. Unless specified,
the component class name will be used as an implicit bean definition
name.

Let's see how our `DBLoader` declaration would look in that case:

```highlight
class DBLoader extends WiringDeclarableSupport implements CacheLoader {

  private DataSource dataSource;

  public void setDataSource(DataSource dataSource){
    this.dataSource = dataSource;
  }

  public Object load(LoaderHelper helper) { ... }
}
```

```highlight
<cache-loader>
   <class-name>com.company.app.DBLoader</class-name>
   <!-- no parameter is passed (use the bean's implicit name, which is the class name) -->
</cache-loader>
```

```highlight
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:p="http://www.springframework.org/schema/p"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xsi:schemaLocation="
    http://www.springframework.org/schema/beans https://www.springframework.org/schema/beans/spring-beans.xsd
">

  <bean id="dataSource" ... />

  <!-- template bean definition -->
  <bean id="com.company.app.DBLoader" abstract="true" p:dataSource-ref="dataSource"/>
</beans>
```

In the scenario above, as no parameter was specified, a bean with the
id/name `com.company.app.DBLoader` was used as a template for wiring the
instance created by [vmware-gemfire-short-name]. For cases where the bean name
uses a different convention, one can pass in the `bean-name` parameter
in the [vmware-gemfire-short-name] configuration:

```highlight
<cache-loader>
   <class-name>com.company.app.DBLoader</class-name>
   <!-- pass the bean definition template name as parameter -->
   <parameter name="bean-name">
     <string>template-bean</string>
   </parameter>
</cache-loader>
```

```highlight
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:p="http://www.springframework.org/schema/p"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xsi:schemaLocation="
    http://www.springframework.org/schema/beans https://www.springframework.org/schema/beans/spring-beans.xsd
">

  <bean id="dataSource" ... />

   <!-- template bean definition -->
   <bean id="template-bean" abstract="true" p:dataSource-ref="dataSource"/>

</beans>
```

<p class="note"><strong>Note</strong>: The <strong>template</strong> bean definitions do
not have to be declared in XML. Any format is allowed (Groovy,
annotations, etc).</p>

### <a id="configurations-using-auto-wiring-and-annotations"></a>Configuration Using Auto-Wiring and Annotations


By default, if no bean definition is found, `WiringDeclarableSupport` will
[autowire](https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/beans/factory/annotation/Autowire.html)
the declaring instance. This means that unless any dependency injection
**metadata** is offered by the instance, the container will find the
object setters and try to automatically satisfy these dependencies.
However, a developer can also use JDK 5 annotations to provide
additional information to the auto-wiring process.

For example, the hypothetical `DBLoader` declaration above can be
injected with a Spring-configured `DataSource` in the following way:

```highlight
class DBLoader extends WiringDeclarableSupport implements CacheLoader {

  // use annotations to 'mark' the needed dependencies
  @javax.inject.Inject
  private DataSource dataSource;

  public Object load(LoaderHelper helper) { ... }
}
```

```highlight
<cache-loader>
   <class-name>com.company.app.DBLoader</class-name>
   <!-- no need to declare any parameters since the class is auto-wired -->
</cache-loader>
```

```highlight
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:context="http://www.springframework.org/schema/context"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xsi:schemaLocation="
    http://www.springframework.org/schema/beans https://www.springframework.org/schema/beans/spring-beans.xsd
    http://www.springframework.org/schema/context https://www.springframework.org/schema/context/spring-context.xsd
">

     <!-- enable annotation processing -->
     <context:annotation-config/>

</beans>
```

By using the JSR-330 annotations, the `CacheLoader` code has been
simplified since the location and creation of the `DataSource` has been
externalized and the user code is concerned only with the loading
process. The `DataSource` might be transactional, created lazily, shared
between multiple objects or retrieved from JNDI. These aspects can
easily be configured and changed through the Spring container without
touching the `DBLoader` code.

## <a id="support-for-spring-cache-abstraction"></a>Support for the Spring Cache Abstraction

[spring-data-gemfire-name] provides an implementation of the Spring
[Cache Abstraction](https://docs.spring.io/spring-framework/docs/current/reference/html/integration.html#cache)
to position [vmware-gemfire-short-name] as a *caching provider* in Spring's
caching infrastructure.

To use [vmware-gemfire-short-name] as a backing implementation, a "*caching
provider*" *in Spring's Cache Abstraction*, add
`GemfireCacheManager` to your configuration:

```highlight
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:cache="http://www.springframework.org/schema/cache"
       xmlns:gfe="{spring-data-schema-namespace}"
       xmlns:p="http://www.springframework.org/schema/p"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xsi:schemaLocation="
    http://www.springframework.org/schema/beans https://www.springframework.org/schema/beans/spring-beans.xsd
    http://www.springframework.org/schema/cache https://www.springframework.org/schema/cache/spring-cache.xsd
    {spring-data-schema-namespace} {spring-data-schema-location}
">

  <!-- enable declarative caching -->
  <cache:annotation-driven/>

  <gfe:cache id="gemfire-cache"/>

  <!-- declare GemfireCacheManager; must have a bean ID of 'cacheManager' -->
  <bean id="cacheManager" class="org.springframework.data.gemfire.cache.GemfireCacheManager"
      p:cache-ref="gemfire-cache">

</beans>
```

<p class="note"><strong>Note</strong>: The <code>cache-ref</code> attribute on the
<code>CacheManager</code> bean definition is not necessary if the
default cache bean name is used (i.e. "gemfireCache"), i.e.
<code>&lt;gfe:cache&gt;</code> without an explicit ID.</p>

When the `GemfireCacheManager` (Singleton) bean instance is declared and
declarative caching is enabled (either in XML with
`<cache:annotation-driven/>` or in JavaConfig with Spring's
`@EnableCaching` annotation), the Spring caching annotations (e.g.
`@Cacheable`) identify the "caches" that will cache data in-memory using
[vmware-gemfire-short-name] Regions.

These caches (i.e. Regions) must exist before the caching annotations
that use them otherwise an error will occur.

By way of example, suppose you have a Customer Service application with
a `CustomerService` application component that performs caching…​

```highlight
@Service
class CustomerService {

@Cacheable(cacheNames="Accounts", key="#customer.id")
Account createAccount(Customer customer) {
  ...
}
```

Then you will need the following config.

XML:

```highlight
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:cache="http://www.springframework.org/schema/cache"
       xmlns:gfe="{spring-data-schema-namespace}"
       xmlns:p="http://www.springframework.org/schema/p"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xsi:schemaLocation="
    http://www.springframework.org/schema/beans https://www.springframework.org/schema/beans/spring-beans.xsd
    http://www.springframework.org/schema/cache https://www.springframework.org/schema/cache/spring-cache.xsd
    {spring-data-schema-namespace} {spring-data-schema-location}
">

  <!-- enable declarative caching -->
  <cache:annotation-driven/>

  <bean id="cacheManager" class="org.springframework.data.gemfire.cache.GemfireCacheManager">

  <gfe:cache/>

  <gfe:partitioned-region id="accountsRegion" name="Accounts" persistent="true" ...>
    ...
  </gfe:partitioned-region>
</beans>
```

JavaConfig:

```highlight
@Configuration
@EnableCaching
class ApplicationConfiguration {

  @Bean
  CacheFactoryBean gemfireCache() {
    return new CacheFactoryBean();
  }

  @Bean
  GemfireCacheManager cacheManager() {
    GemfireCacheManager cacheManager = GemfireCacheManager();
    cacheManager.setCache(gemfireCache());
    return cacheManager;
  }

  @Bean("Accounts")
  PartitionedRegionFactoryBean accountsRegion() {
    PartitionedRegionFactoryBean accounts = new PartitionedRegionFactoryBean();

    accounts.setCache(gemfireCache());
    accounts.setClose(false);
    accounts.setPersistent(true);

    return accounts;
  }
}
```

You can choose whatever Region type you like (e.g. REPLICATE, PARTITION, LOCAL, etc).
