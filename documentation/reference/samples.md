---
title: Sample Applications
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

The Spring Data for VMware GemFire project also includes one sample application. Named
"Hello World", the sample application demonstrates how to configure and
use GemFire inside a Spring application. At run time, the
sample offers a shell that lets you run various commands against the
data grid. It provides an excellent starting point for developers who
are unfamiliar with the essential components or with Spring and
GemFire concepts.

The sample is bundled with the distribution and is Maven-based. You can
import it into any Maven-aware IDE (such as the [Spring Tool
Suite](https://spring.io/tools/sts)) or run them from the command-line.

## <a id="hello-world"></a>The "Hello World" Sample Application

The "Hello World" sample application demonstrates the core functionality
of the Spring Data for VMware GemFire project. It bootstraps GemFire, configures
it, executes arbitrary commands against the cache, and shuts it down
when the application exits. Multiple instances of the application can be
started at the same time and work together, sharing data without any
user intervention.

<p class="note"><strong>Running under Linux</strong>: 
If you experience networking problems when starting GemFire or
the samples, try adding the following system property
<code>java.net.preferIPv4Stack=true</code> to the command line (for
example, <code>-Djava.net.preferIPv4Stack=true</code>). For an
alternative (global) fix (especially on Ubuntu), see <a
href="https://jira.spring.io/browse/SGF-28">SGF-28</a>.</p>

### <a id="starting-and-stopping"></a>Starting and Stopping

The "Hello World" sample application is designed as a stand-alone Java
application. It features a `main` class that can be started either from
your IDE (in Eclipse or STS, through `Run As/Java Application`) or from
the command line through Maven with `mvn exec:java`. If the classpath is
properly set, you can also use `java` directly on the resulting
artifact.

To stop the sample application, type `exit` at the command line or press `Ctrl+C` to
stop the JVM and shutdown the Spring container.

### <a id="using-sample-application"></a>Using the Sample Application

Once started, the sample application creates a shared data grid and lets you issue
commands against it. The output should resemble the following:

```highlight
INFO: Created GemFire Cache [Spring GemFire World] v. X.Y.Z
INFO: Created new cache region [myWorld]
INFO: Member xxxxxx:50694/51611 connecting to region [myWorld]
Hello World!
Want to interact with the world ? ...
Supported commands are:
  
get <key> - retrieves an entry (by key) from the grid
put <key> <value> - puts a new entry into the grid
remove <key> - removes an entry (by key) from the grid
...
```

For example, to add new items to the grid, you can use the following
commands:

```highlight
-> Bold Section qName:emphasis level:5, chunks:[put 1 unu] attrs:[role:bold]
INFO: Added [1=unu] to the cache
null
-> Bold Section qName:emphasis level:5, chunks:[put 1 one] attrs:[role:bold]
INFO: Updated [1] from [unu] to [one]
unu
-> Bold Section qName:emphasis level:5, chunks:[size] attrs:[role:bold]
1
-> Bold Section qName:emphasis level:5, chunks:[put 2 two] attrs:[role:bold]
INFO: Added [2=two] to the cache
null
-> Bold Section qName:emphasis level:5, chunks:[size] attrs:[role:bold]
2
```

You can run multiple instances at the same time. Once started, the new
VMs automatically see the existing region and its information, as the
following example shows:

```highlight
INFO: Connected to Distributed System ['Spring GemFire World'=xxxx:56218/49320@yyyyy]
Hello World!
...

-> Bold Section qName:emphasis level:5, chunks:[size] attrs:[role:bold]
2
-> Bold Section qName:emphasis level:5, chunks:[map] attrs:[role:bold]
[2=two] [1=one]
-> Bold Section qName:emphasis level:5, chunks:[query length = 3] attrs:[role:bold]
[one, two]
```

We encourage you to experiment with the example, start and stop as
many instances as you want, and run various commands in one instance and
see how the others react. To preserve data, at least one instance needs
to be alive all times. If all instances are shut down, the grid data is
completely destroyed.

## <a id="explanation"></a>"Hello World" Sample Application Explained

The "Hello World" sample application uses both Spring XML and annotations for its
configuration. The initial bootstrapping configuration is
`app-context.xml`, which includes the cache configuration defined in the
`cache-context.xml` file and performs classpath [component
scanning](https://docs.spring.io/spring/docs/current/spring-framework-reference/htmlsingle/#beans-classpath-scanning)
for Spring
[components](https://docs.spring.io/spring/docs/current/spring-framework-reference/htmlsingle/#beans-annotation-config).

The cache configuration defines the GemFire cache, a region,
and for illustrative purposes, a `CacheListener` that acts as a logger.

The main beans are `HelloWorld` and `CommandProcessor`, which rely on
the `GemfireTemplate` to interact with the distributed fabric. Both
classes use annotations to define their dependency and life-cycle
callbacks.
