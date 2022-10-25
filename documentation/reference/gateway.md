---
title: Configuring WAN Gateways
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

WAN Gateways provides a way to synchronize GemFire Distributed
Systems across geographic locations. Spring Data for VMware GemFire provides XML namespace
support for configuring WAN Gateways as illustrated in the following
examples.

WAN Gateways provides a way to synchronize GemFire Distributed
Systems across geographic locations. Spring Data for VMware GemFire provides XML namespace
support for configuring WAN Gateways as illustrated in the following
examples.

## <a id="wan-gateway-configuration-7"></a>WAN Configuration in GemFire 7.0

In the following example, `GatewaySenders` are configured for a
`PARTITION` Region by adding child elements (`gateway-sender` and
`gateway-sender-ref`) to the Region. A `GatewaySender` may register
`EventFilters` and `TransportFilters`.

The following example also shows a sample configuration of an
`AsyncEventQueue`, which must also be auto-wired into a Region (not
shown):

```highlight
<gfe:partitioned-region id="region-with-inner-gateway-sender" >
    <gfe:gateway-sender remote-distributed-system-id="1">
        <gfe:event-filter>
            <bean class="org.springframework.data.gemfire.example.SomeEventFilter"/>
        </gfe:event-filter>
        <gfe:transport-filter>
            <bean class="org.springframework.data.gemfire.example.SomeTransportFilter"/>
        </gfe:transport-filter>
    </gfe:gateway-sender>
    <gfe:gateway-sender-ref bean="gateway-sender"/>
</gfe:partitioned-region>

<gfe:async-event-queue id="async-event-queue" batch-size="10" persistent="true" disk-store-ref="diskstore"
        maximum-queue-memory="50">
    <gfe:async-event-listener>
        <bean class="example.AsyncEventListener"/>
    </gfe:async-event-listener>
</gfe:async-event-queue>

<gfe:gateway-sender id="gateway-sender" remote-distributed-system-id="2">
    <gfe:event-filter>
        <ref bean="event-filter"/>
        <bean class="org.springframework.data.gemfire.example.SomeEventFilter"/>
    </gfe:event-filter>
    <gfe:transport-filter>
        <ref bean="transport-filter"/>
        <bean class="org.springframework.data.gemfire.example.SomeTransportFilter"/>
    </gfe:transport-filter>
</gfe:gateway-sender>
  
<bean id="event-filter" class="org.springframework.data.gemfire.example.AnotherEventFilter"/>
<bean id="transport-filter" class="org.springframework.data.gemfire.example.AnotherTransportFilter"/>
```

On the other end of a `GatewaySender` is a corresponding
`GatewayReceiver` to receive Gateway events. The `GatewayReceiver` may
also be configured with `EventFilters` and `TransportFilters`, as
follows:

```highlight
<gfe:gateway-receiver id="gateway-receiver" start-port="12345" end-port="23456" bind-address="192.168.0.1">
    <gfe:transport-filter>
        <bean class="org.springframework.data.gemfire.example.SomeTransportFilter"/>
    </gfe:transport-filter>
</gfe:gateway-receiver>
```

For a detailed explanation of the configuration options. see [Multi-site (WAN) Configuration](https://docs.vmware.com/en/VMware-Tanzu-GemFire/9.15/tgf/GUID-topologies_and_comm-multi_site_configuration-chapter_overview.html) in the GemFire product documentation.

