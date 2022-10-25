---
title: Reference Documentation Structure
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

The following chapters explain the core functionality offered by
Spring Data for VMware GemFire:


- [Bootstrapping GemFire with the Spring Container](bootstrap.html) describes the configuration support provided for configuring, initializing, and accessing GemFire Caches, Regions, and related distributed system components.

- [Working with GemFire APIs](data.html) explains the integration between the GemFire APIs and the various data access features available in Spring, such as template-based data access, exception translation, transaction management, and caching.

- [Working with GemFire Serialization](serialization.html) describes enhancements to GemFire's serialization and deserialization of managed objects.

- [POJO Mapping](mapping.html) describes persistence mapping for POJOs stored in GemFire using Spring Data.

- [Spring Data for VMware GemFire Repositories](repositories.html) describes how to create and use Spring Data Repositories to access data stored in GemFire by using basic CRUD and simple query operations.

- [Annotation Support for Function Execution](function-annotations.html) describes how to create and use GemFire Functions by using annotations to perform distributed computations where the data lives.

- [Continuous Query (CQ)](#apis:continuous-query) describes how to use GemFire's Continuous Query (CQ) functionality to process a stream of events based on interest that is defined and registered with GemFire's OQL (Object Query Language).

- [Bootstrapping a Spring ApplicationContext in GemFire](gemfire-bootstrap.html) describes how to configure and bootstrap a Spring `ApplicationContext` running in an GemFire server using `gfsh`.

- [Sample Applications](samples.html) describes the examples provided with the distribution to illustrate the various features available in Spring Data for VMware GemFire.
