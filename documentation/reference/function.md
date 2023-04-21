---
title: Configuring the Function Service
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

Spring Data for VMware GemFire provides [annotation](function-annotations.html) support for
implementing, registering and executing GemFire Functions.

Spring Data for VMware GemFire also provides XML namespace support for registering
GemFire [Functions](https://gemfire.docs.pivotal.io/apidocs/gf-100/org/apache/geode/cache/execute/Function.html) for remote function execution.

For more information about the Function execution framework, see [Function Execution](https://docs.vmware.com/en/VMware-GemFire/10.0/gf/developing-function_exec-chapter_overview.html) in the GemFire product documentation.

GemFire Functions are declared as Spring beans and must
implement the `org.apache.geode.cache.execute.Function` interface or
extend `org.apache.geode.cache.execute.FunctionAdapter`.

The namespace uses a familiar pattern to declare Functions, as the
following example shows:

```highlight
<gfe:function-service>
  <gfe:function>
      <bean class="example.FunctionOne"/>
      <ref bean="function2"/>
  </gfe:function>
</gfe:function-service>

<bean id="function2" class="example.FunctionTwo"/>
```
