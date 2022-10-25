---
title: Continuous Query (CQ)
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



A powerful functionality offered by GemFire is [Continuous Query](https://docs.vmware.com/en/VMware-Tanzu-GemFire/9.15/tgf/GUID-developing-continuous_querying-chapter_overview.html) (CQ).

CQ allows a developer to create and register an OQL query, and
then automatically be notified when new data that gets added to
GemFire matches the query predicate. Spring Data for VMware GemFire provides
dedicated support for CQs through the
`org.springframework.data.gemfire.listener` package and its **listener
container**; very similar in functionality and naming to the JMS
integration in the *Spring Framework*; in fact, users familiar with the
JMS support in Spring, should feel right at home.

Spring Data for VMware GemFire allows methods on POJOs to become end-points for
CQ. Simply define the query and indicate the method that should be
called to be notified when there is a match. Spring Data for VMware GemFire takes care of
the rest. This is similar to Java EE's message-driven bean style,
but without any requirement for base class or interface implementations,
based on GemFire.

<p class="note"><strong>Note</strong>: Continuous Query is only supported in
GemFire's client/server topology. Additionally, the client
Pool used is required to have the subscription enabled. For more information, see <a href="https://docs.vmware.com/en/VMware-Tanzu-GemFire/9.15/tgf/GUID-developing-continuous_querying-implementing_continuous_querying.htmlPlease">Implementing Continuous Querying</a>.</p>


## <a id="continuous-query-listener-container"></a>Continuous Query Listener Container

Spring Data for VMware GemFire simplifies creation, registration, life-cycle, and dispatch of
CQ events by taking care of the infrastructure around CQ with the use of
Spring Data for VMware GemFire's `ContinuousQueryListenerContainer`, which does all the heavy
lifting on behalf of the user. Users familiar with EJB and JMS should
find the concepts familiar as it is designed as close as possible to the
support provided in the *Spring Framework* with its Message-driven POJOs
(MDPs).

The Spring Data for VMware GemFire `ContinuousQueryListenerContainer` acts as an event (or message)
listener container; it is used to receive the events from the registered
CQs and invoke the POJOs that are injected into it. The listener
container is responsible for all threading of message reception and
dispatches into the listener for processing. It acts as the intermediary
between an EDP (Event-driven POJO) and the event provider and takes care
of creation and registration of CQs (to receive events), resource
acquisition and release, exception conversion and the like. This allows
you, as an application developer, to write the (possibly complex)
business logic associated with receiving an event (and reacting to it),
and delegate the boilerplate GemFire infrastructure concerns
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

## <a id="continuousquerylistener-and-continuousquerylisteneradapter"></a>`ContinuousQueryListener` and `ContinuousQueryListenerAdapter`

The `ContinuousQueryListenerAdapter` class is the final component in
Spring Data for VMware GemFire CQ support. In a nutshell, class allows you to expose almost
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
interface has no GemFire dependencies. It is
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

The example above uses the Spring Data for VMware GemFire namespace to declare the event
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
translation between the GemFire event and the required method
arguments transparently. Any exception caused by the method invocation
is caught and handled by the container (by default, being logged).

