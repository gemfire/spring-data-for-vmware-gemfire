---
title: Configuring a DiskStore
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
Spring Data for VMware GemFire supports `DiskStore` configuration and creation through the
`disk-store` element, as the following example shows:

```highlight
<gfe:disk-store id="Example" auto-compact="true" max-oplog-size="10"
                queue-size="50" time-interval="9999">
    <gfe:disk-dir location="/disk/location/one" max-size="20"/>
    <gfe:disk-dir location="/disk/location/two" max-size="20"/>
</gfe:disk-store>
```

`DiskStore` instances are used by Regions for file system persistent
backup and overflow of evicted entries as well as persistent backup for
WAN Gateways. Multiple GemFire components may share the same
`DiskStore`. Additionally, multiple file system directories may be
defined for a single `DiskStore`, as shown in the preceding example.

For an explanation of persistence, overflow, and configuration options on `DiskStore` instances, see [Persistence and Overflow](https://docs.vmware.com/en/VMware-Tanzu-GemFire/9.15/tgf/GUID-developing-storing_data_on_disk-chapter_overview.html) in the GemFire product documentation.
