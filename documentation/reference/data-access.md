---
title: Using the Data Access Namespace
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

In addition to the core XML namespace (`gfe`), Spring Data for VMware GemFire provides a
data access XML namespace (`gfe-data`), which is primarily intended to
simplify the development of GemFire client applications. This
namespace currently contains support for [GemFire
Repositories](repositories.html) and [Function
execution](function-annotations.html), as well as a `<datasource>` tag that
offers a convenient way to connect to a GemFire cluster.

## <a id="connect-to-gemfire"></a>An Easy Way to Connect to GemFire

For many applications, a basic connection to a GemFire data
grid using default values is sufficient. Spring Data for VMware GemFire's `<datasource>` tag
provides a simple way to access data. The data source creates a
`ClientCache` and connection `Pool`. In addition, it queries the cluster
servers for all existing root Regions and creates an empty client
Region proxy for each one.

```highlight
<gfe-data:datasource>
  <locator host="remotehost" port="1234"/>
</gfe-data:datasource>
```

The `<datasource>` tag is syntactically similar to `<gfe:pool>`. It may
be configured with one or more nested `locator` or `server` elements to
connect to an existing data grid. Additionally, all attributes available
to configure a Pool are supported. This configuration automatically
creates client Region beans for each Region defined on cluster members
connected to the Locator, so that they can be seamlessly referenced by Spring
Data mapping annotations (`GemfireTemplate`) and autowired into
application classes.

You can also explicitly configure client Regions. For example, if
you want to cache data in local memory, as the following example shows:

```highlight
<gfe-data:datasource>
  <locator host="remotehost" port="1234"/>
</gfe-data:datasource>

<gfe:client-region id="Example" shortcut="CACHING_PROXY"/>
```
