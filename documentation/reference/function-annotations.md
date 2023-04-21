---
title: Annotation Support for Function Execution
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

Spring Data for VMware GemFire includes annotation support to simplify working with
GemFire [Function execution](https://docs.vmware.com/en/VMware-GemFire/9.15/gf/developing-function_exec-chapter_overview.html).


Under the hood, the GemFire API provides classes to implement
and register [Functions](https://gemfire.docs.pivotal.io/apidocs/gf-100/org/apache/geode/cache/execute/Function.html)
that are deployed on GemFire servers, which may then be
invoked by other peer member applications or remotely from cache
clients.

Functions can execute in parallel, distributed among multiple
GemFire servers in the cluster, aggregating the results using
the map-reduce pattern and sent back to the caller. Functions can also
be targeted to run on a single server or Region. The GemFire
API supports remote execution of Functions targeted by using various
predefined scopes: on Region, on members (in groups), on servers, and
others. The implementation and execution of remote Functions, as with
any RPC protocol, requires some boilerplate code.

Spring Data for VMware GemFire, true to Spring's core value proposition, aims to hide the
mechanics of remote Function execution and let you focus on core POJO
programming and business logic. To this end, Spring Data for VMware GemFire introduces
annotations to declaratively register the public methods of a POJO class
as GemFire Functions along with the ability to invoke
registered Functions (including remotely) by using annotated interfaces.

## <a id="implementation-versus-execution"></a>Implementation Versus Execution

There are two separate concerns to address: implementation and
execution.

The first is Function implementation (server-side), which must interact
with the
[FunctionContext](https://gemfire.docs.pivotal.io/apidocs/gf-100/org/apache/geode/cache/execute/FunctionContext.html)
to access the invocation arguments, [ResultsSender](https://gemfire.docs.pivotal.io/apidocs/gf-100/org/apache/geode/cache/execute/ResultSender.html) to send results, and other execution context information. The Function
implementation typically accesses the cache and Regions and is registered with the
[FunctionService](https://gemfire.docs.pivotal.io/apidocs/gf-100/org/apache/geode/cache/execute/FunctionService.html) under a unique ID.

A cache client application invoking a Function does not depend on the
implementation. To invoke a Function, the application instantiates an [Execution](https://gemfire.docs.pivotal.io/apidocs/gf-100/org/apache/geode/cache/execute/FunctionService.html) providing the Function ID, invocation arguments, and the Function
target, which defines its scope: Region, server, servers, member, or
members. If the Function produces a result, the invoker uses a
[ResultCollector](https://gemfire.docs.pivotal.io/apidocs/gf-100/org/apache/geode/cache/execute/ResultCollector.html)
to aggregate and acquire the execution results. In certain cases, a
custom `ResultCollector` implementation is required and may be
registered with the `Execution`.

<p class="note"><strong>Note</strong>: "Client" and "Server" are used here in the context
of Function execution, which may have a different meaning than client
and server in GemFire's client-server topology. While it is
common for an application using a <code>ClientCache</code> instance to
invoke a Function on one or more GemFire servers in a cluster,
it is also possible to execute Functions in a peer-to-peer (P2P)
configuration, where the application is a member of the cluster hosting
a peer <code>Cache</code> instance. Keep in mind that a peer member
cache application is subject to all the constraints of being a peer
member of the cluster.</p>

## <a id="implementing-a-function"></a>Implementing a Function

Using GemFire APIs, the `FunctionContext` provides a runtime
invocation context that includes the client's calling arguments and a
`ResultSender` implementation to send results back to the client.
Additionally, if the Function is executed on a Region, the
`FunctionContext` is actually an instance of `RegionFunctionContext`,
which provides additional information, such as the target Region on
which the Function was invoked, any filter (a set of specific keys)
associated with the `Execution`, and so on. If the Region is a
`PARTITION` Region, the Function should use the `PartitionRegionHelper`
to extract the local data set.

By using Spring, you can write a simple POJO and use the Spring
container to bind one or more of your POJO's public methods to a
Function. The signature for a POJO method intended to be used as a
Function must generally conform to the client's execution arguments.
However, in the case of a Region execution, the Region data may also be
provided (presumably the data is held in the local partition if the
Region is a `PARTITION` Region).

Additionally, the Function may require the filter that was applied, if
any. This suggests that the client and server share a contract for the
calling arguments but that the method signature may include additional
parameters to pass values provided by the `FunctionContext`. One
possibility is for the client and server to share a common interface,
but this is not strictly required. The only constraint is that the
method signature includes the same sequence of calling arguments with
which the Function was invoked after the additional parameters are
resolved.

For example, suppose the client provides a `String` and an `int` as the
calling arguments. These are provided in the `FunctionContext` as an
array, as the following example shows:

```
Object[] args = new Object[] { "test", 123 };
```

The Spring container should be able to bind to any method signature
similar to the following (ignoring the return type for the moment):

```highlight
public Object method1(String s1, int i2) { ... }
public Object method2(Map≤?, ?> data, String s1, int i2) { ... }
public Object method3(String s1, Map≤?, ?> data, int i2) { ... }
public Object method4(String s1, Map≤?, ?> data, Set≤?> filter, int i2) { ... }
public void method4(String s1, Set≤?> filter, int i2, Region≤?,?> data) { ... }
public void method5(String s1, ResultSender rs, int i2) { ... }
public void method6(FunctionContest context) { ... }
```

The general rule is that once any additional arguments (that is, Region
data and filter) are resolved, the remaining arguments must correspond
exactly, in order and type, to the expected Function method parameters.
The method's return type must be void or a type that may be serialized
(as a `java.io.Serializable`, `DataSerializable`, or `PdxSerializable`).
The latter is also a requirement for the calling arguments.

The Region data should normally be defined as a `Map`, to facilitate
unit testing, but may also be of type Region, if necessary. As shown in
the preceding example, it is also valid to pass the `FunctionContext`
itself or the `ResultSender` if you need to control over how the results
are returned to the client.

### <a id="annotations-for-function-implementation"></a>Annotations for Function Implementation

The following example shows how Spring Data for VMware GemFire's Function annotations are
used to expose POJO methods as GemFire Functions:

```highlight
@Component
public class ApplicationFunctions {

   @GemfireFunction
   public String function1(String value, @RegionData Map?, ?> data, int i2) { ... }

   @GemfireFunction(id = "myFunction", batchSize=100, HA=true, optimizedForWrite=true)
   public List<String> function2(String value, @RegionData Map≤?, ?> data, int i2, @Filter Set≤?> keys) { ... }

   @GemfireFunction(hasResult=true)
   public void functionWithContext(FunctionContext functionContext) { ... }

}
```

Note that the class itself must be registered as a Spring bean and each
GemFire Function is annotated with `@GemfireFunction`. In the
preceding example, Spring's `@Component` annotation was used, but you
can register the bean by using any method supported by Spring (such as
XML configuration or with a Java configuration class when using Spring
Boot). This lets the Spring container create an instance of this class
and wrap it in a [PojoFunctionWrapper](https://docs.spring.io/spring-data-gemfire/docs/current/api/org/springframework/data/gemfire/function/PojoFunctionWrapper.html).
Spring creates a wrapper instance for each method annotated with
`@GemfireFunction`. Each wrapper instance shares the same target object
instance to invoke the corresponding method.

<p class="note"><strong>Tip</strong>: The fact that the POJO Function class is a Spring
bean may offer other benefits. Since it shares the <code>ApplicationContext</code> with GemFire components, such as the cache and Regions, these may be injected into the class if
necessary.</p>

Spring creates the wrapper class and registers the Functions with
GemFire's `FunctionService`. The Function ID used to register
each Function must be unique. By using convention, it defaults to the
simple (unqualified) method name. The name can be explicitly defined by
using the `id` attribute of the `@GemfireFunction` annotation.

The `@GemfireFunction` annotation also provides other configuration
attributes: `HA` and `optimizedForWrite`, which correspond to properties
defined by the [Function](https://gemfire.docs.pivotal.io/apidocs/gf-100/org/apache/geode/cache/execute/Function.html) interface.


If the POJO Function method's return type is `void`, then the
`hasResult` attribute is automatically set to `false`. Otherwise, if the
method returns a value, the `hasResult` attributes is set to `true`.
Even for `void` method return types, the `GemfireFunction` annotation's
`hasResult` attribute can be set to `true` to override this convention,
as shown in the `functionWithContext` method shown previously.
Presumably, the intention is that you will use the `ResultSender`
directly to send results to the caller.

Finally, the `GemfireFunction` annotation supports the
`requiredPermissions` attribute, which specifies the permissions
required to execute the Function. By default, all Functions require the
`DATA:WRITE` permission. The attribute accepts an array of Strings
allowing you to modify the permissions as required by your application
and/or Function UC. Each resource permission is expected to be in the
following format: `<RESOURCE>:<OPERATION>:[Target]:[Key]`.

* `RESOURCE` can be one of the [ResourcePermission.Resource](https://gemfire.docs.pivotal.io/apidocs/gf-100/org/apache/geode/security/ResourcePermission.html) **Resource** enumerated values.

* `OPERATION` can be one of the [ResourcePermission.Resource](https://gemfire.docs.pivotal.io/apidocs/gf-100/org/apache/geode/security/ResourcePermission.html) **Operation** enumerated values.

* `Target` (optional) can be the name of a Region or one 
of the [ResourcePermission.Resource](https://gemfire.docs.pivotal.io/apidocs/gf-100/org/apache/geode/security/ResourcePermission.html) **Target** enumerated values.

* `Key` (optional) is a valid Key in the `Target` Region, if specified.

The `PojoFunctionWrapper` implements GemFire's `Function`
interface, binds method parameters, and invokes the target method in its
`execute()` method. It also sends the method's return value back to the
caller by using the `ResultSender`.

### <a id="batching-results"></a>Batching Results

If the return type is an array or `Collection`, then some consideration
must be given to how the results are returned. By default, the
`PojoFunctionWrapper` returns the entire array or `Collection` at once.
If the number of elements in the array or `Collection` is quite large,
it may incur a performance penalty. To divide the payload into smaller,
more manageable chunks, you can set the `batchSize` attribute, as
illustrated in `function2`, shown earlier.

<p class="note"><strong>Tip</strong>: If you need more control of the
<code>ResultSender</code>, especially if the method itself would use too
much memory to create the <code>Collection</code>, you can pass in the
<code>ResultSender</code> or access it through the
<code>FunctionContext</code> and use it directly within the method to
sends results back to the caller.</p>

### <a id="enabling-annotation-processing"></a>Enabling Annotation Processing

In accordance with Spring standards, you must explicitly activate
annotation processing for `@GemfireFunction` annotations. The following
example activates annotation processing with XML:

```highlight
<gfe:annotation-driven/>
```

The following example activates annotation processing by annotating a
Java configuration class:

```highlight
@Configuration
@EnableGemfireFunctions
class ApplicationConfiguration { ... }
```

## <a id="executing-a-function"></a>Executing a Function

A process that invokes a remote Function needs to provide the Function's
ID, calling arguments, the execution target (`onRegion`, `onServers`,
`onServer`, `onMember`, or `onMembers`) and (optionally) a filter set.
By using Spring Data for VMware GemFire, all you need do is define an interface supported by
annotations. Spring creates a dynamic proxy for the interface, which
uses the `FunctionService` to create an `Execution`, invoke the
`Execution`, and (if necessary) coerce the results to the defined return
type. This technique is similar to the way Spring Data for VMware GemFire's Repository
extension works. Thus, some of the configuration and concepts should be
familiar.

Generally, a single interface definition maps to multiple Function
executions, one corresponding to each method defined in the interface.

### <a id="annotations-for-function-execution"></a>Annotations for Function Execution

To support client-side Function execution, the following Spring Data for VMware GemFire
Function annotations are provided: 

* `@OnRegion`
* `@OnServer`
* `@OnServers`
* `@OnMember`
* `@OnMembers`

These annotations
correspond to the `Execution` implementations provided by the [FunctionService](https://gemfire.docs.pivotal.io/apidocs/gf-100/org/apache/geode/cache/execute/FunctionService.html) class.

Each annotation exposes the appropriate attributes. These annotations
also provide an optional `resultCollector` attribute whose value is the
name of a Spring bean implementing the [ResultCollector](https://gemfire.docs.pivotal.io/apidocs/gf-100/org/apache/geode/cache/execute/ResultCollector.html) interface to use for the execution.



<p class="note"><strong>Warning</strong>: The proxy interface binds all declared methods to
the same execution configuration. Although it is expected that single
method interfaces are common, all methods in the interface are backed by
the same proxy instance and therefore all share the same
configuration.</p>

The following listing shows a few examples:

```highlight
@OnRegion(region="SomeRegion", resultCollector="myCollector")
public interface FunctionExecution {

    @FunctionId("function1")
    String doIt(String s1, int i2);

    String getString(Object arg1, @Filter Set<Object> keys);

}
```

By default, the Function ID is the simple, unqualified method name. The
`@FunctionId` annotation can be used to bind this invocation to a
different Function ID.

### <a id="enabling-automation-processing"></a>Enabling Annotation Processing

The client-side uses Spring's classpath component scanning capability to
discover annotated interfaces. To enable Function execution annotation
processing in XML, insert the following element in your XML
configuration:

```highlight
<gfe-data:function-executions base-package="org.example.myapp.gemfire.functions"/>
```

The `function-executions` element is provided in the `gfe-data` XML
namespace. The `base-package` attribute is required to avoid scanning
the entire classpath. Additional filters can be provided as described in
the Spring [reference
documentation](https://docs.spring.io/spring-framework/docs/current/reference/html/core.html#beans-scanning-filters).

Optionally, you can annotate your Java configuration class as follows:

```highlight
@EnableGemfireFunctionExecutions(basePackages = "org.example.myapp.gemfire.functions")
```

## <a id="programmatic-function-execution"></a>Programmatic Function Execution

Using the Function execution annotated interface defined in the previous
section, auto-wire your interface into an application bean that
will invoke the Function:

```highlight
@Component
public class MyApplication {

    @Autowired
    FunctionExecution functionExecution;

    public void doSomething() {
         functionExecution.doIt("hello", 123);
    }
}
```

Alternately, you can use a Function execution template directly. In the
following example, the `GemfireOnRegionFunctionTemplate` creates an
`onRegion` Function `Execution`:

**Example 1. Using the `GemfireOnRegionFunctionTemplate`**


```highlight
Set≤?, ?>> myFilter = getFilter();
Region≤?, ?> myRegion = getRegion();
GemfireOnRegionOperations template = new GemfireOnRegionFunctionTemplate(myRegion);
String result = template.executeAndExtract("someFunction", myFilter, "hello", "world", 1234);
```

Internally, Function `Executions` always return a `List`.
`executeAndExtract` assumes a singleton `List` containing the result and
attempts to coerce that value into the requested type. There is also an
`execute` method that returns the `List` as is. The first parameter is
the Function ID. The filter argument is optional. The remaining
arguments are a variable argument `List`.

## <a id="function-execution-with-pdx"></a>Function Execution with PDX

When using Spring Data for VMware GemFire's Function annotation support combined with
GemFire's [PDX Serialization](https://docs.vmware.com/en/VMware-GemFire/9.15/gf/developing-data_serialization-gemfire_pdx_serialization.html), there are a few logistical things to keep in mind.

As explained earlier in this section, and by way of example, you should
typically define GemFire Functions by using POJO classes
annotated with Spring Data for VMware GemFire [Function
annotations](https://docs.spring.io/spring-data-gemfire/docs/current/api/org/springframework/data/gemfire/function/annotation/package-summary.html),
as follows:

```highlight
public class OrderFunctions {

  @GemfireFunction(...)
  Order process(@RegionData data, Order order, OrderSource orderSourceEnum, Integer count) { ... }

}
```

<p class="note"><strong>Note</strong>: 
The <code>Integer</code> typed <code>count</code>
parameter is arbitrary, as is the separation of the <code>Order</code>
class and the <code>OrderSource</code> enum, which might be logical to
combine. However, the arguments were setup this way to demonstrate the
problem with Function executions in the context of PDX.</p>

Your `Order` class and `OrderSource` enum might be defined as follows:

```highlight
public class Order ... {

  private Long orderNumber;
  private LocalDateTime orderDateTime;
  private Customer customer;
  private List<Item> items

  ...
}

public enum OrderSource {
  ONLINE,
  PHONE,
  POINT_OF_SALE
  ...
}
```

You can define a Function `Execution` interface to call the
'process' GemFire server Function, as follows:

```highlight
@OnServer
public interface OrderProcessingFunctions {
  Order process(Order order, OrderSource orderSourceEnum, Integer count);
}
```

This `process(..)` `Order` Function is being called from the
client-side with a `ClientCache` instance, `<gfe:client-cache/>`). This implies that the Function
arguments must also be serializable. The same is true when invoking peer-to-peer member
Functions (such as `@OnMember(s)`) between peers in the cluster. Any
form of `distribution` requires the data transmitted between client and
server (or peers) to be serialized.

Now, if you have configured GemFire to use PDX for
serialization (instead of Java serialization, for instance) you can also
set the `pdx-read-serialized` attribute to `true` in your configuration
of the GemFire server(s), as follows:

```highlight
<gfe:cache pdx-read-serialized="true"/>
```

Alternatively, you can set the `pdx-read-serialized` attribute to `true`
for a GemFire cache client application, as follows:

```highlight
<gfe:client-cache pdx-read-serialized="true"/>
```

Doing so causes all values read from the cache (that is, Regions) as
well as information passed between client and servers (or peers) to
remain in serialized form, including, but not limited to, Function
arguments.

GemFire serializes only application domain object types that
you have specifically configured (registered) either by using
[ReflectionBasedAutoSerializer](https://gemfire.docs.pivotal.io/apidocs/gf-100/org/apache/geode/pdx/ReflectionBasedAutoSerializer.html),
or specifically (and recommended) by using a "custom" GemFire
[PdxSerializer](https://gemfire.docs.pivotal.io/apidocs/gf-100/org/apache/geode/pdx/PdxSerializer.html).
If you use Spring Data for VMware GemFire's Repository extension, you might even want to
consider using Spring Data for VMware GemFire's [MappingPdxSerializer](https://docs.spring.io/spring-data/geode/docs/current/api/org/springframework/data/gemfire/mapping/MappingPdxSerializer.html),
which uses an entity's mapping metadata to determine data from the
application domain object that is serialized to the PDX instance.

What is less than apparent, though, is that GemFire
automatically handles Java `Enum` types regardless of whether they are
explicitly configured (that is, registered with a
`ReflectionBasedAutoSerializer`, using a regex pattern and the `classes`
parameter or are handled by a "custom" GemFire
`PdxSerializer`), despite the fact that Java enumerations implement
`java.io.Serializable`.

So, when you set `pdx-read-serialized` to `true` on GemFire
servers where the GemFire Functions (including Spring Data for VMware GemFire
Function-annotated POJO classes) are registered, then you may encounter
surprising behavior when invoking the Function `Execution`.

You might pass the following arguments when invoking the Function:

```highlight
orderProcessingFunctions.process(new Order(123, customer, LocalDateTime.now(), items), OrderSource.ONLINE, 400);
```

However, the GemFire Function on the server gets the
following:

```highlight
process(regionData, order:PdxInstance, :PdxInstanceEnum, 400);
```

The `Order` and `OrderSource` have been passed to the Function as
[PDX instances](https://gemfire.docs.pivotal.io/apidocs/gf-100/org/apache/geode/pdx/PdxInstance.html). This all happens because `pdx-read-serialized` is
set to `true`, which may be necessary in cases where the
GemFire servers interact with multiple different clients (for
example, a combination of Java clients and native clients, such as
C/C++, C#, and others).

This flies in the face of Spring Data for VMware GemFire's strongly-typed Function-annotated
POJO class method signatures, where you would reasonably expect
application domain object types instead, not PDX serialized instances.

Consequently, Spring Data for VMware GemFire includes enhanced Function support to
automatically convert PDX typed method arguments to the desired
application domain object types defined by the Function method's
signature (parameter types).

However, this also requires you to explicitly register a
GemFire `PdxSerializer` on GemFire servers where
Spring Data for VMware GemFire Function-annotated POJOs are registered and used, as the
following example shows:

```highlight
<bean id="customPdxSerializer" class="x.y.z.gemfire.serialization.pdx.MyCustomPdxSerializer"/>

<gfe:cache pdx-serializer-ref="customPdxSerializeer" pdx-read-serialized="true"/>
```

Alternatively, you can use GemFire's [ReflectionBasedAutoSerializer](https://gemfire.docs.pivotal.io/apidocs/gf-100/org/apache/geode/pdx/ReflectionBasedAutoSerializer.html) for convenience. We recommend that you use a
custom `PdxSerializer` to maintain finer-grained control over your
serialization strategy when possible.

Finally, Spring Data for VMware GemFire is careful not to convert your Function arguments if
you treat your Function arguments generically or as one of
GemFire's PDX types, as follows:

```highlight
@GemfireFunction
public Object genericFunction(String value, Object domainObject, PdxInstanceEnum pdxEnum) {
  // ...
}
```

Spring Data for VMware GemFire converts PDX typed data to the corresponding application
domain types if and only if the corresponding application domain types
are on the classpath and the Function-annotated POJO method expects it.
