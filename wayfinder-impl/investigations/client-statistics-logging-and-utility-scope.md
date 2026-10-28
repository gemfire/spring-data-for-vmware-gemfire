# Client statistics, logging and utility scope — 2026-10-28

**Task 73: 265 assigned / 265 classified / zero unresolved: 227 required-client and 38 excluded-server.** All implementation statuses remain missing; scope classification does not implement these contracts.

Evidence is pinned to GemFire **10.4.0-build.3056.df98c835**, source commit `df98c8357afe85853d9a4776255d4f0e7cf064cb` in `/Users/uk016635/projects/gemfire`, read with git show. Unless qualified below, source paths are relative to `gemfire-core/src/main/java/org/apache/geode/` and have a `.java` suffix. Current Spring consumers and `api-edges.tsv` establish which native internals are actually reachable. Exact IDs below include every assigned type, overload, constant and inherited member. No unanswered scope question needs a runtime probe.

## client-statistics-model

**107 IDs — required-client.** These 107 declarations describe local counters/gauges and types, not server statistics administration. Pinned core sources `StatisticDescriptor`, `Statistics`, `StatisticsFactory`, `StatisticsType`, `StatisticsTypeFactory`, `internal/statistics/{StatisticDescriptorImpl,StatisticsImpl,StatisticsTypeFactoryImpl}` and `distributed/internal/InternalDistributedSystem.initialize` establish client use. Task 72 already retained the inherited DistributedSystem factory declarations; these are the separately assigned receiver IDs.

Preserve descriptor name/description/unit/type, counter and larger-better flags, type-local IDs and name ordering (`StatisticDescriptorImpl.compareTo`); type creation/lookup, XML reader loading and descriptor limits; atomic versus non-atomic creation, text/numeric/unique identity and matching lookups. `StatisticsTypeFactoryImpl.addType` returns an equal existing type but rejects a conflicting same-name definition; `findType` can return null. Default counter/gauge larger-better flags differ. Deprecated int factories use long descriptors in this version, int writes/increments delegate to long and reads narrow to int: do not infer storage from method spelling.

Preserve name/descriptor/ID overloads, wrong-name/type failures, raw double bits, get/set/increment and closed-state behavior. `StatisticsImpl` unregisters on close; numeric reads then return zero and writes/increments do nothing, but name/descriptor validation can happen before the closed check. Supplier methods compute an initial sample, return the previous supplier, replace it and refresh during sampling; getters need not recompute. The interface documents null as removal. The pinned implementation calls the supplier immediately, catches Exception and writes zero on that initial failure, then puts the supplier in its map; sampling catches failures separately. Task 23 must test null replacement, exception handling, sample scheduling and close behavior against the versioned adapter instead of assuming registration is lazy or every failure is swallowed. These details do not change client scope, so no scope probe is needed. Use injected ownership and explicit mock semantics, not a new public global statistics singleton.

## client-logging-adapters

**96 IDs — required-client.** The 96 declarations cover system/security logs used by client runtime and `AbstractAuthInitialize.init`. Pinned core `LogWriter`, `i18n/LogWriterI18n`, `internal/logging/LogWriterImpl` define level checks, message/Throwable/combined overloads, localized message/argument overloads, entering/exiting/throwing, getHandler and conversions. `LogWriterImpl` filters before emission; trace helpers use finer; its Java Handler forwards to the same writer and both conversions return this. Preserve effective levels, message/argument/cause association and common destination through a typed logging collaborator or ordinary logging adapter, with sensitive authentication data redacted. A Java logging Handler is a standard-library bridge; no native handler/logger class may escape. This does not require public clones of the two deprecated native interfaces. Tasks 23/36 coordinate authentication adoption with 24/35 and test supported logging paths in mock and native drivers.

## client-localized-message-values

**9 IDs — required-client.** All nine `i18n/StringId` declarations are reachable from retained client logging. Its constructor and public id retain message identity; getRawText differs from formatted text; LITERAL is ID 1 with pattern {0}. Both toString overloads use MessageFormat. Localized overloads resolve a resource bundle and include the msgID prefix only outside raw mode; the default locale is captured at class initialization. Record those semantics in versioned compatibility tests, including arguments and raw fallback. Native bundle loading and ambient locale state stay private. Tasks 23/36 preserve client diagnostics without exposing StringId or a native bundle registry.

## client-internal-utility-replacements

**6 IDs — required-client.** Six IDs remain client obligations. `DistributedSystemUtils.getDistributedSystem` currently calls `distributed/internal/InternalDistributedSystem.getAnyInstance`; replace global lookup with an injected client runtime handle, preserving absence/closed behavior and ownership (36 with 23/35). `internal/cache/GemFireCacheImpl` is the shared client cache implementation; the current CacheUtils import is documentation-only, not evidence of an extra operation. Remove that stale reference during task 36 adoption and use existing client cache contracts (29/35).

`internal/cache/InternalAttributesFactory` is the base of the retained client configuration builder, and `InternalRegionAttributes` is its protected configuration state; the AttributesFactory members were classified by task 67. Keep supported copying/validation/attributes through typed configuration (36 with 29/30), not native inheritance or field exposure. `RegionUtils.isLocal` currently tests `instanceof LocalRegion`. That internal superclass is not a reliable distinction between client-local storage and server/distributed semantics; task 36 with 30 must define the client configuration/capability check, preserve null behavior and cover local/caching/proxy cases. Native LocalRegion and subclasses stay private. These type IDs authorize only those client replacements, not arbitrary methods on these internal classes.

## client-function-identity

**5 IDs — required-client.** Five declarations in `gemfire-common/src/main/java/org/apache/geode/lang/Identifiable.java` are reached by `cache.execute.Function` inheritance, not solely management DTOs. Retain getId as typed function metadata. The static helpers compare IDs with String.equals: exists checks any match, find returns the first match or null, remove removes matching entries from the supplied list. No normalization or null-safe matching is promised. Use ordinary Java helpers where appropriate; do not require native Serializable/Identifiable inheritance or server registration. All five unfinished IDs move from implementation 23/consumer 36 to active 25/32.

## client-server-region-discovery

**4 IDs — required-client.** Four declarations preserve the existing handoff to tasks 25/32. In `gemfire-gfsh/src/main/java/org/apache/geode/management/internal/cli/`, `functions/GetRegionsFunction.execute` accepts the false verbose flag, enumerates root regions and returns RegionInformation values (or a null last result for no roots); exceptions are sent as function failures. `domain/RegionInformation` constructs name from full path with the initial slash removed, and getName returns it. Spring callers are `FunctionGemfireAdminTemplate` and `GemfireDataSourcePostProcessor`.

The typed discovery service must aggregate names across servers, preserve the naming contract, distinguish empty success from failure, and reject/translate malformed native results. No management DTO, native function instance or untyped Object[] protocol crosses into Spring. Native construction/execution/conversion and any required fixture stay private. These four IDs move to implementation 25/consumer 32, matching their existing concrete acceptance; no new representation investigation is required. Root/subregion selection and duplicates must be explicit in driver tests rather than silently inferred from the recursive DTO.

## excluded-internal-locator-hosting

**3 IDs — excluded-server.** The two `distributed/internal/InternalLocator` declarations are local locator type/registry lookup. Task 72 removed the public DistributedSystemUtils.getLocator accessor and validated the private LocatorProcess shutdown callback. The single HostAddress type ID comes only from the excluded `distributed.Locator.hostAddress` field in api-edges.tsv; its source is `gemfire-tcp-server/src/main/java/org/apache/geode/distributed/internal/tcpserver/HostAddress.java`. Neither creates a client endpoint requirement. Native locator hosting remains private test infrastructure; client pool endpoints and discovery stay with 20/50. Replace the old internal-boundary suggestions to inject a locator service or mirror its address storage: those suggestions no longer apply.

## excluded-internal-peer-cache

**1 IDs — excluded-server.** The sole frozen `internal/cache/InternalCache` ID is reached by four RegionFactory constructors and getCache, all already excluded in task 67. The internal interface is also used within the native engine, but that alone does not authorize a public SPI representation. There is no current Spring production reference to retire. Replace the old blanket suggestion to convert these peer constructor arguments into connection handles. ClientCache/RegionService behavior and the other client configuration replacements remain required under their own IDs.

## excluded-wan-property-helper

**1 IDs — excluded-server.** The sole frozen edge is `cache.wan.GatewaySender.removeFromQueueOnException`, an excluded WAN sender setting. `gemfire-logging/src/main/java/org/apache/geode/logging/internal/deprecation/DeprecatedSystemProperty.java` performs generic deprecated property parsing/logging, but this reachable use is exclusively server/WAN configuration. No production Spring consumer or existing SPI mapping exists. Replace the prior internal-boundary recommendation to adapt the WAN setting; do not add a public utility merely because the native engine uses it. Client configuration parsing remains independently owned by tasks 23/35.

## excluded-redundancy-management-results

**33 IDs — excluded-server.** All 33 IDs are in `gemfire-management/src/main/java/org/apache/geode/`: `management/runtime/{OperationResult,RegionRedundancyStatus,RestoreRedundancyResults}` and their nested enums, plus `management/api/JsonSerializable`. They describe cluster operations and restoring partition bucket redundancy, not client subscription redundancy. Preserve the frozen constructors/type/enum constants/values/valueOf and inherited result declarations as exclusions. Results expose configured/actual redundancy, zero/under/satisfied region maps, per-region lookup, status/message, included regions without members and primary transfer counts/time. OperationResult supplies success/status text; JsonSerializable is the class-discriminator JSON marker reached by these management types. No client operation is established by DTO serialization alone. There are no current Spring production consumers or existing public SPI mappings for these missing rows. Client pool redundancy, application JSON/PDX and typed server-region discovery are separate retained contracts.

## Ownership and source reconciliation

Statistics/logging/message values stay with implementation 23 and consumer 36 (212 IDs). Six retained internal utility IDs stay with 36, coordinating 23/29/30/35. Function identity and discovery transfer nine missing IDs to 25/32. All owners are active; no new dependency cycle or blocking implementation prerequisite is added to the audit. Every exclusion is missing and unmapped. InternalLocator production cleanup was completed and validated in task 72; remaining native locator hosting is private test infrastructure. The excluded carrier/management types have no linked current production source or existing-SPI reference. Retained native imports remain migration work with the stated owners. Client XML, pool discovery and client-local statistics/logging are preserved.

The 2,723-entry assignment ledger is unchanged. This task changes no Java/build behavior, native declaration, implementation status or SPI/native/mock/test mapping. It corrects stale internal-boundary prose and transfers the nine function-related ownership rows. Deterministic generation, policy tests, independent declaration/ownership checks and exact assignment accounting validate the classification. No Gradle command is needed or run. Tasks 59/64 remain open; task 74 is next.

## Exact ID ledger

### client-statistics-model — 107 IDs

| API ID | Native receiver | Member | JVM descriptor | Owner | Consumer |
| --- | --- | --- | --- | --- | --- |
| `API-843b4b2098474f69` | `org.apache.geode.StatisticDescriptor` | `<type>` | `(type)` | 23 | 36 |
| `API-0bdc152da69f8a04` | `org.apache.geode.StatisticDescriptor` | `compareTo` | `(Ljava/lang/Object;)I` | 23 | 36 |
| `API-b9ddfb197c99a4b4` | `org.apache.geode.StatisticDescriptor` | `getDescription` | `()Ljava/lang/String;` | 23 | 36 |
| `API-55fb8f4e3304f256` | `org.apache.geode.StatisticDescriptor` | `getId` | `()I` | 23 | 36 |
| `API-4473346856118311` | `org.apache.geode.StatisticDescriptor` | `getName` | `()Ljava/lang/String;` | 23 | 36 |
| `API-50f1fcd3ce4de458` | `org.apache.geode.StatisticDescriptor` | `getType` | `()Ljava/lang/Class;` | 23 | 36 |
| `API-1b41ae377f8a3ca0` | `org.apache.geode.StatisticDescriptor` | `getUnit` | `()Ljava/lang/String;` | 23 | 36 |
| `API-1bb0a36ddb898613` | `org.apache.geode.StatisticDescriptor` | `isCounter` | `()Z` | 23 | 36 |
| `API-0afeb0e11fbe984e` | `org.apache.geode.StatisticDescriptor` | `isLargerBetter` | `()Z` | 23 | 36 |
| `API-d6c6ce322ec74eab` | `org.apache.geode.Statistics` | `<type>` | `(type)` | 23 | 36 |
| `API-6f6a617eab9b5e81` | `org.apache.geode.Statistics` | `close` | `()V` | 23 | 36 |
| `API-40894ec2fac1347f` | `org.apache.geode.Statistics` | `get` | `(Ljava/lang/String;)Ljava/lang/Number;` | 23 | 36 |
| `API-97982b8ebb79d2c9` | `org.apache.geode.Statistics` | `get` | `(Lorg/apache/geode/StatisticDescriptor;)Ljava/lang/Number;` | 23 | 36 |
| `API-fed0d83be6b79170` | `org.apache.geode.Statistics` | `getDouble` | `(I)D` | 23 | 36 |
| `API-049e71d2a5bb0edd` | `org.apache.geode.Statistics` | `getDouble` | `(Ljava/lang/String;)D` | 23 | 36 |
| `API-38da8fcd929db93c` | `org.apache.geode.Statistics` | `getDouble` | `(Lorg/apache/geode/StatisticDescriptor;)D` | 23 | 36 |
| `API-fc783a7d23091113` | `org.apache.geode.Statistics` | `getInt` | `(I)I` | 23 | 36 |
| `API-70a83fdec35370c6` | `org.apache.geode.Statistics` | `getInt` | `(Ljava/lang/String;)I` | 23 | 36 |
| `API-3f5e3e96b80afe57` | `org.apache.geode.Statistics` | `getInt` | `(Lorg/apache/geode/StatisticDescriptor;)I` | 23 | 36 |
| `API-f3c4b864c246ddca` | `org.apache.geode.Statistics` | `getLong` | `(I)J` | 23 | 36 |
| `API-0f29efc53c1e62f1` | `org.apache.geode.Statistics` | `getLong` | `(Ljava/lang/String;)J` | 23 | 36 |
| `API-c2d738a036e73d3a` | `org.apache.geode.Statistics` | `getLong` | `(Lorg/apache/geode/StatisticDescriptor;)J` | 23 | 36 |
| `API-b8e7573c46c556b3` | `org.apache.geode.Statistics` | `getNumericId` | `()J` | 23 | 36 |
| `API-d149af28f7b4f030` | `org.apache.geode.Statistics` | `getRawBits` | `(Ljava/lang/String;)J` | 23 | 36 |
| `API-c0d81c66038956c6` | `org.apache.geode.Statistics` | `getRawBits` | `(Lorg/apache/geode/StatisticDescriptor;)J` | 23 | 36 |
| `API-9d57702a96b81eb8` | `org.apache.geode.Statistics` | `getTextId` | `()Ljava/lang/String;` | 23 | 36 |
| `API-31d5cc2ab9d95743` | `org.apache.geode.Statistics` | `getType` | `()Lorg/apache/geode/StatisticsType;` | 23 | 36 |
| `API-9fb52329102fa0db` | `org.apache.geode.Statistics` | `getUniqueId` | `()J` | 23 | 36 |
| `API-9d0bc5ecb0e824d2` | `org.apache.geode.Statistics` | `incDouble` | `(ID)V` | 23 | 36 |
| `API-2bb1e50886392d79` | `org.apache.geode.Statistics` | `incDouble` | `(Ljava/lang/String;D)V` | 23 | 36 |
| `API-c9593231e2283f90` | `org.apache.geode.Statistics` | `incDouble` | `(Lorg/apache/geode/StatisticDescriptor;D)V` | 23 | 36 |
| `API-803e172d1dd39ae9` | `org.apache.geode.Statistics` | `incInt` | `(II)V` | 23 | 36 |
| `API-ec5667e3559f0b9f` | `org.apache.geode.Statistics` | `incInt` | `(Ljava/lang/String;I)V` | 23 | 36 |
| `API-eeab40c02b28bad8` | `org.apache.geode.Statistics` | `incInt` | `(Lorg/apache/geode/StatisticDescriptor;I)V` | 23 | 36 |
| `API-ca0d147b3067c2c7` | `org.apache.geode.Statistics` | `incLong` | `(IJ)V` | 23 | 36 |
| `API-a4379373f9a334f4` | `org.apache.geode.Statistics` | `incLong` | `(Ljava/lang/String;J)V` | 23 | 36 |
| `API-5ada78ff3dae0c17` | `org.apache.geode.Statistics` | `incLong` | `(Lorg/apache/geode/StatisticDescriptor;J)V` | 23 | 36 |
| `API-5880f2316bc4cc83` | `org.apache.geode.Statistics` | `isAtomic` | `()Z` | 23 | 36 |
| `API-c3ca72deddb4e706` | `org.apache.geode.Statistics` | `isClosed` | `()Z` | 23 | 36 |
| `API-f8d924595773a9c7` | `org.apache.geode.Statistics` | `nameToDescriptor` | `(Ljava/lang/String;)Lorg/apache/geode/StatisticDescriptor;` | 23 | 36 |
| `API-650fb8fb0eadff6f` | `org.apache.geode.Statistics` | `nameToId` | `(Ljava/lang/String;)I` | 23 | 36 |
| `API-51e0861a78eb47a7` | `org.apache.geode.Statistics` | `setDouble` | `(ID)V` | 23 | 36 |
| `API-9a79fd8905cfa354` | `org.apache.geode.Statistics` | `setDouble` | `(Ljava/lang/String;D)V` | 23 | 36 |
| `API-083680ed098f7461` | `org.apache.geode.Statistics` | `setDouble` | `(Lorg/apache/geode/StatisticDescriptor;D)V` | 23 | 36 |
| `API-25855f0ff3d22d57` | `org.apache.geode.Statistics` | `setDoubleSupplier` | `(ILjava/util/function/DoubleSupplier;)Ljava/util/function/DoubleSupplier;` | 23 | 36 |
| `API-86cfcf4ff2f9cc91` | `org.apache.geode.Statistics` | `setDoubleSupplier` | `(Ljava/lang/String;Ljava/util/function/DoubleSupplier;)Ljava/util/function/DoubleSupplier;` | 23 | 36 |
| `API-fdd673322c970d20` | `org.apache.geode.Statistics` | `setDoubleSupplier` | `(Lorg/apache/geode/StatisticDescriptor;Ljava/util/function/DoubleSupplier;)Ljava/util/function/DoubleSupplier;` | 23 | 36 |
| `API-c3ba9c21960d2ba9` | `org.apache.geode.Statistics` | `setInt` | `(II)V` | 23 | 36 |
| `API-8c85881cb3299dd5` | `org.apache.geode.Statistics` | `setInt` | `(Ljava/lang/String;I)V` | 23 | 36 |
| `API-2696fa63eeaf40ea` | `org.apache.geode.Statistics` | `setInt` | `(Lorg/apache/geode/StatisticDescriptor;I)V` | 23 | 36 |
| `API-4c798a54833e152e` | `org.apache.geode.Statistics` | `setIntSupplier` | `(ILjava/util/function/IntSupplier;)Ljava/util/function/IntSupplier;` | 23 | 36 |
| `API-4467939d7059a2dd` | `org.apache.geode.Statistics` | `setIntSupplier` | `(Ljava/lang/String;Ljava/util/function/IntSupplier;)Ljava/util/function/IntSupplier;` | 23 | 36 |
| `API-2438832aab680365` | `org.apache.geode.Statistics` | `setIntSupplier` | `(Lorg/apache/geode/StatisticDescriptor;Ljava/util/function/IntSupplier;)Ljava/util/function/IntSupplier;` | 23 | 36 |
| `API-aef0b2be0d91b5ec` | `org.apache.geode.Statistics` | `setLong` | `(IJ)V` | 23 | 36 |
| `API-daf1451816cf9f6f` | `org.apache.geode.Statistics` | `setLong` | `(Ljava/lang/String;J)V` | 23 | 36 |
| `API-d0a569716a4d2450` | `org.apache.geode.Statistics` | `setLong` | `(Lorg/apache/geode/StatisticDescriptor;J)V` | 23 | 36 |
| `API-93a1a43881e8dc5a` | `org.apache.geode.Statistics` | `setLongSupplier` | `(ILjava/util/function/LongSupplier;)Ljava/util/function/LongSupplier;` | 23 | 36 |
| `API-88c188274300fe71` | `org.apache.geode.Statistics` | `setLongSupplier` | `(Ljava/lang/String;Ljava/util/function/LongSupplier;)Ljava/util/function/LongSupplier;` | 23 | 36 |
| `API-eb7ab103be0ba2e1` | `org.apache.geode.Statistics` | `setLongSupplier` | `(Lorg/apache/geode/StatisticDescriptor;Ljava/util/function/LongSupplier;)Ljava/util/function/LongSupplier;` | 23 | 36 |
| `API-66be44afd14df7bc` | `org.apache.geode.StatisticsFactory` | `<type>` | `(type)` | 23 | 36 |
| `API-3744a1d2f066bac4` | `org.apache.geode.StatisticsFactory` | `createAtomicStatistics` | `(Lorg/apache/geode/StatisticsType;)Lorg/apache/geode/Statistics;` | 23 | 36 |
| `API-93d0fb9b885f8526` | `org.apache.geode.StatisticsFactory` | `createAtomicStatistics` | `(Lorg/apache/geode/StatisticsType;Ljava/lang/String;)Lorg/apache/geode/Statistics;` | 23 | 36 |
| `API-bf997c33a96126de` | `org.apache.geode.StatisticsFactory` | `createAtomicStatistics` | `(Lorg/apache/geode/StatisticsType;Ljava/lang/String;J)Lorg/apache/geode/Statistics;` | 23 | 36 |
| `API-3f1e6de468eaf4f9` | `org.apache.geode.StatisticsFactory` | `createDoubleCounter` | `(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Lorg/apache/geode/StatisticDescriptor;` | 23 | 36 |
| `API-6c8b98b7b7cc9d7a` | `org.apache.geode.StatisticsFactory` | `createDoubleCounter` | `(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Z)Lorg/apache/geode/StatisticDescriptor;` | 23 | 36 |
| `API-a307ab9b2800843b` | `org.apache.geode.StatisticsFactory` | `createDoubleGauge` | `(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Lorg/apache/geode/StatisticDescriptor;` | 23 | 36 |
| `API-cae74385916c8bab` | `org.apache.geode.StatisticsFactory` | `createDoubleGauge` | `(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Z)Lorg/apache/geode/StatisticDescriptor;` | 23 | 36 |
| `API-f66135949bf9d0ef` | `org.apache.geode.StatisticsFactory` | `createIntCounter` | `(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Lorg/apache/geode/StatisticDescriptor;` | 23 | 36 |
| `API-d525cf202d867c41` | `org.apache.geode.StatisticsFactory` | `createIntCounter` | `(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Z)Lorg/apache/geode/StatisticDescriptor;` | 23 | 36 |
| `API-4a9e2a6522daebe5` | `org.apache.geode.StatisticsFactory` | `createIntGauge` | `(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Lorg/apache/geode/StatisticDescriptor;` | 23 | 36 |
| `API-85fd89025a1d2a34` | `org.apache.geode.StatisticsFactory` | `createIntGauge` | `(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Z)Lorg/apache/geode/StatisticDescriptor;` | 23 | 36 |
| `API-7823e3129c62ebe3` | `org.apache.geode.StatisticsFactory` | `createLongCounter` | `(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Lorg/apache/geode/StatisticDescriptor;` | 23 | 36 |
| `API-d4f91425ce557d08` | `org.apache.geode.StatisticsFactory` | `createLongCounter` | `(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Z)Lorg/apache/geode/StatisticDescriptor;` | 23 | 36 |
| `API-d293bd5ff02073ab` | `org.apache.geode.StatisticsFactory` | `createLongGauge` | `(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Lorg/apache/geode/StatisticDescriptor;` | 23 | 36 |
| `API-71f3cd2bcba545ff` | `org.apache.geode.StatisticsFactory` | `createLongGauge` | `(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Z)Lorg/apache/geode/StatisticDescriptor;` | 23 | 36 |
| `API-8f68c9c052164ad3` | `org.apache.geode.StatisticsFactory` | `createStatistics` | `(Lorg/apache/geode/StatisticsType;)Lorg/apache/geode/Statistics;` | 23 | 36 |
| `API-e7d1bae4476acd95` | `org.apache.geode.StatisticsFactory` | `createStatistics` | `(Lorg/apache/geode/StatisticsType;Ljava/lang/String;)Lorg/apache/geode/Statistics;` | 23 | 36 |
| `API-4b0c5bfc44f31dc5` | `org.apache.geode.StatisticsFactory` | `createStatistics` | `(Lorg/apache/geode/StatisticsType;Ljava/lang/String;J)Lorg/apache/geode/Statistics;` | 23 | 36 |
| `API-e7aa067e81d98f06` | `org.apache.geode.StatisticsFactory` | `createType` | `(Ljava/lang/String;Ljava/lang/String;[Lorg/apache/geode/StatisticDescriptor;)Lorg/apache/geode/StatisticsType;` | 23 | 36 |
| `API-eda0bf7f825da6f5` | `org.apache.geode.StatisticsFactory` | `createTypesFromXml` | `(Ljava/io/Reader;)[Lorg/apache/geode/StatisticsType;` | 23 | 36 |
| `API-7f8bd0c65a512f5f` | `org.apache.geode.StatisticsFactory` | `findStatisticsByNumericId` | `(J)[Lorg/apache/geode/Statistics;` | 23 | 36 |
| `API-4d4cae8bb83fcf7e` | `org.apache.geode.StatisticsFactory` | `findStatisticsByTextId` | `(Ljava/lang/String;)[Lorg/apache/geode/Statistics;` | 23 | 36 |
| `API-278d9a588646c2b3` | `org.apache.geode.StatisticsFactory` | `findStatisticsByType` | `(Lorg/apache/geode/StatisticsType;)[Lorg/apache/geode/Statistics;` | 23 | 36 |
| `API-662716218d909608` | `org.apache.geode.StatisticsFactory` | `findType` | `(Ljava/lang/String;)Lorg/apache/geode/StatisticsType;` | 23 | 36 |
| `API-506e904652e0ece1` | `org.apache.geode.StatisticsType` | `<type>` | `(type)` | 23 | 36 |
| `API-e24abdd0e2fba17d` | `org.apache.geode.StatisticsType` | `getDescription` | `()Ljava/lang/String;` | 23 | 36 |
| `API-9558c3e5b22f85d5` | `org.apache.geode.StatisticsType` | `getName` | `()Ljava/lang/String;` | 23 | 36 |
| `API-294c63c97fc0706d` | `org.apache.geode.StatisticsType` | `getStatistics` | `()[Lorg/apache/geode/StatisticDescriptor;` | 23 | 36 |
| `API-a62698a27967cfc7` | `org.apache.geode.StatisticsType` | `nameToDescriptor` | `(Ljava/lang/String;)Lorg/apache/geode/StatisticDescriptor;` | 23 | 36 |
| `API-1cd33f3091e970e7` | `org.apache.geode.StatisticsType` | `nameToId` | `(Ljava/lang/String;)I` | 23 | 36 |
| `API-cb2cae7deef12cbd` | `org.apache.geode.StatisticsTypeFactory` | `<type>` | `(type)` | 23 | 36 |
| `API-dd038b91722ac96a` | `org.apache.geode.StatisticsTypeFactory` | `MAX_DESCRIPTORS_PER_TYPE` | `I` | 23 | 36 |
| `API-f76bc74d46635dae` | `org.apache.geode.StatisticsTypeFactory` | `createDoubleCounter` | `(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Lorg/apache/geode/StatisticDescriptor;` | 23 | 36 |
| `API-ccd957b3b662d1df` | `org.apache.geode.StatisticsTypeFactory` | `createDoubleCounter` | `(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Z)Lorg/apache/geode/StatisticDescriptor;` | 23 | 36 |
| `API-5d9fd7645183b671` | `org.apache.geode.StatisticsTypeFactory` | `createDoubleGauge` | `(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Lorg/apache/geode/StatisticDescriptor;` | 23 | 36 |
| `API-8e47fef221a5d799` | `org.apache.geode.StatisticsTypeFactory` | `createDoubleGauge` | `(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Z)Lorg/apache/geode/StatisticDescriptor;` | 23 | 36 |
| `API-3944da9e9fd85cfe` | `org.apache.geode.StatisticsTypeFactory` | `createIntCounter` | `(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Lorg/apache/geode/StatisticDescriptor;` | 23 | 36 |
| `API-0133f42ecd728b78` | `org.apache.geode.StatisticsTypeFactory` | `createIntCounter` | `(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Z)Lorg/apache/geode/StatisticDescriptor;` | 23 | 36 |
| `API-49c52f558098b8a5` | `org.apache.geode.StatisticsTypeFactory` | `createIntGauge` | `(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Lorg/apache/geode/StatisticDescriptor;` | 23 | 36 |
| `API-00d7015d3d8b1e24` | `org.apache.geode.StatisticsTypeFactory` | `createIntGauge` | `(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Z)Lorg/apache/geode/StatisticDescriptor;` | 23 | 36 |
| `API-e5c16106fe4eaba0` | `org.apache.geode.StatisticsTypeFactory` | `createLongCounter` | `(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Lorg/apache/geode/StatisticDescriptor;` | 23 | 36 |
| `API-2d673a7286393243` | `org.apache.geode.StatisticsTypeFactory` | `createLongCounter` | `(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Z)Lorg/apache/geode/StatisticDescriptor;` | 23 | 36 |
| `API-2099f192805cd54c` | `org.apache.geode.StatisticsTypeFactory` | `createLongGauge` | `(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Lorg/apache/geode/StatisticDescriptor;` | 23 | 36 |
| `API-3881f0835e9badc3` | `org.apache.geode.StatisticsTypeFactory` | `createLongGauge` | `(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Z)Lorg/apache/geode/StatisticDescriptor;` | 23 | 36 |
| `API-983e749d3549cc85` | `org.apache.geode.StatisticsTypeFactory` | `createType` | `(Ljava/lang/String;Ljava/lang/String;[Lorg/apache/geode/StatisticDescriptor;)Lorg/apache/geode/StatisticsType;` | 23 | 36 |
| `API-764e1e473fd95a94` | `org.apache.geode.StatisticsTypeFactory` | `createTypesFromXml` | `(Ljava/io/Reader;)[Lorg/apache/geode/StatisticsType;` | 23 | 36 |
| `API-143e5dedbee30221` | `org.apache.geode.StatisticsTypeFactory` | `findType` | `(Ljava/lang/String;)Lorg/apache/geode/StatisticsType;` | 23 | 36 |

### client-logging-adapters — 96 IDs

| API ID | Native receiver | Member | JVM descriptor | Owner | Consumer |
| --- | --- | --- | --- | --- | --- |
| `API-619b8f4c35b51863` | `org.apache.geode.LogWriter` | `<type>` | `(type)` | 23 | 36 |
| `API-3cbdb4d2dd888ac3` | `org.apache.geode.LogWriter` | `config` | `(Ljava/lang/String;)V` | 23 | 36 |
| `API-5126f7e80e558f2e` | `org.apache.geode.LogWriter` | `config` | `(Ljava/lang/String;Ljava/lang/Throwable;)V` | 23 | 36 |
| `API-859eba74960436af` | `org.apache.geode.LogWriter` | `config` | `(Ljava/lang/Throwable;)V` | 23 | 36 |
| `API-665aa9652cb804ae` | `org.apache.geode.LogWriter` | `configEnabled` | `()Z` | 23 | 36 |
| `API-4fe920686585d772` | `org.apache.geode.LogWriter` | `convertToLogWriterI18n` | `()Lorg/apache/geode/i18n/LogWriterI18n;` | 23 | 36 |
| `API-bd730af30f28de86` | `org.apache.geode.LogWriter` | `entering` | `(Ljava/lang/String;Ljava/lang/String;)V` | 23 | 36 |
| `API-30df1a17a2c1c44d` | `org.apache.geode.LogWriter` | `error` | `(Ljava/lang/String;)V` | 23 | 36 |
| `API-05f1e49273d73ff2` | `org.apache.geode.LogWriter` | `error` | `(Ljava/lang/String;Ljava/lang/Throwable;)V` | 23 | 36 |
| `API-05b73fe8d755842c` | `org.apache.geode.LogWriter` | `error` | `(Ljava/lang/Throwable;)V` | 23 | 36 |
| `API-fb0ad74add0e9066` | `org.apache.geode.LogWriter` | `errorEnabled` | `()Z` | 23 | 36 |
| `API-8d007a2690fe61ff` | `org.apache.geode.LogWriter` | `exiting` | `(Ljava/lang/String;Ljava/lang/String;)V` | 23 | 36 |
| `API-e53ed931dbce572f` | `org.apache.geode.LogWriter` | `fine` | `(Ljava/lang/String;)V` | 23 | 36 |
| `API-e01e1ea1107e84e2` | `org.apache.geode.LogWriter` | `fine` | `(Ljava/lang/String;Ljava/lang/Throwable;)V` | 23 | 36 |
| `API-b7cb473e7999e2bd` | `org.apache.geode.LogWriter` | `fine` | `(Ljava/lang/Throwable;)V` | 23 | 36 |
| `API-58c6cae06dcfa3fe` | `org.apache.geode.LogWriter` | `fineEnabled` | `()Z` | 23 | 36 |
| `API-db3a4e7314a84117` | `org.apache.geode.LogWriter` | `finer` | `(Ljava/lang/String;)V` | 23 | 36 |
| `API-1e41c4e4872ba8f5` | `org.apache.geode.LogWriter` | `finer` | `(Ljava/lang/String;Ljava/lang/Throwable;)V` | 23 | 36 |
| `API-1ae96a30170bef78` | `org.apache.geode.LogWriter` | `finer` | `(Ljava/lang/Throwable;)V` | 23 | 36 |
| `API-4c0d1451619879f0` | `org.apache.geode.LogWriter` | `finerEnabled` | `()Z` | 23 | 36 |
| `API-2a531e7748a8fc42` | `org.apache.geode.LogWriter` | `finest` | `(Ljava/lang/String;)V` | 23 | 36 |
| `API-cad423e362e0ffe0` | `org.apache.geode.LogWriter` | `finest` | `(Ljava/lang/String;Ljava/lang/Throwable;)V` | 23 | 36 |
| `API-dcfe659e49fb0147` | `org.apache.geode.LogWriter` | `finest` | `(Ljava/lang/Throwable;)V` | 23 | 36 |
| `API-aa626448336ed5d0` | `org.apache.geode.LogWriter` | `finestEnabled` | `()Z` | 23 | 36 |
| `API-0cf0bff82fc6994b` | `org.apache.geode.LogWriter` | `getHandler` | `()Ljava/util/logging/Handler;` | 23 | 36 |
| `API-0edc66df3dea0cd2` | `org.apache.geode.LogWriter` | `info` | `(Ljava/lang/String;)V` | 23 | 36 |
| `API-6c11b77a1ac0875f` | `org.apache.geode.LogWriter` | `info` | `(Ljava/lang/String;Ljava/lang/Throwable;)V` | 23 | 36 |
| `API-bc228d657601b826` | `org.apache.geode.LogWriter` | `info` | `(Ljava/lang/Throwable;)V` | 23 | 36 |
| `API-bd38b11fdbaa02f0` | `org.apache.geode.LogWriter` | `infoEnabled` | `()Z` | 23 | 36 |
| `API-278f5a6449c0bb94` | `org.apache.geode.LogWriter` | `severe` | `(Ljava/lang/String;)V` | 23 | 36 |
| `API-88b7f8c9da219bee` | `org.apache.geode.LogWriter` | `severe` | `(Ljava/lang/String;Ljava/lang/Throwable;)V` | 23 | 36 |
| `API-0a014cc014123ae9` | `org.apache.geode.LogWriter` | `severe` | `(Ljava/lang/Throwable;)V` | 23 | 36 |
| `API-0fbc43870a387648` | `org.apache.geode.LogWriter` | `severeEnabled` | `()Z` | 23 | 36 |
| `API-cf23ff8f669f7deb` | `org.apache.geode.LogWriter` | `throwing` | `(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)V` | 23 | 36 |
| `API-5b469fd34dc40b54` | `org.apache.geode.LogWriter` | `warning` | `(Ljava/lang/String;)V` | 23 | 36 |
| `API-5e0647ea83172901` | `org.apache.geode.LogWriter` | `warning` | `(Ljava/lang/String;Ljava/lang/Throwable;)V` | 23 | 36 |
| `API-3cb6db8c9f50a84a` | `org.apache.geode.LogWriter` | `warning` | `(Ljava/lang/Throwable;)V` | 23 | 36 |
| `API-f4230da0a8dc5ad8` | `org.apache.geode.LogWriter` | `warningEnabled` | `()Z` | 23 | 36 |
| `API-811e7a63efead2fa` | `org.apache.geode.i18n.LogWriterI18n` | `<type>` | `(type)` | 23 | 36 |
| `API-f5e3f70f46e9e70e` | `org.apache.geode.i18n.LogWriterI18n` | `config` | `(Ljava/lang/Throwable;)V` | 23 | 36 |
| `API-202a89fff8e72957` | `org.apache.geode.i18n.LogWriterI18n` | `config` | `(Lorg/apache/geode/i18n/StringId;)V` | 23 | 36 |
| `API-c405b82d8212b99e` | `org.apache.geode.i18n.LogWriterI18n` | `config` | `(Lorg/apache/geode/i18n/StringId;Ljava/lang/Object;)V` | 23 | 36 |
| `API-a3f55ae2a539e02e` | `org.apache.geode.i18n.LogWriterI18n` | `config` | `(Lorg/apache/geode/i18n/StringId;Ljava/lang/Object;Ljava/lang/Throwable;)V` | 23 | 36 |
| `API-ef5050f1807abd7a` | `org.apache.geode.i18n.LogWriterI18n` | `config` | `(Lorg/apache/geode/i18n/StringId;Ljava/lang/Throwable;)V` | 23 | 36 |
| `API-e7553d703724bd9b` | `org.apache.geode.i18n.LogWriterI18n` | `config` | `(Lorg/apache/geode/i18n/StringId;[Ljava/lang/Object;)V` | 23 | 36 |
| `API-2bf10d55e63c1542` | `org.apache.geode.i18n.LogWriterI18n` | `config` | `(Lorg/apache/geode/i18n/StringId;[Ljava/lang/Object;Ljava/lang/Throwable;)V` | 23 | 36 |
| `API-7a5b0a49d51472c2` | `org.apache.geode.i18n.LogWriterI18n` | `configEnabled` | `()Z` | 23 | 36 |
| `API-606d1b65c8963724` | `org.apache.geode.i18n.LogWriterI18n` | `convertToLogWriter` | `()Lorg/apache/geode/LogWriter;` | 23 | 36 |
| `API-a18e6bebd0acd43a` | `org.apache.geode.i18n.LogWriterI18n` | `entering` | `(Ljava/lang/String;Ljava/lang/String;)V` | 23 | 36 |
| `API-d9f64ec663ff0f8f` | `org.apache.geode.i18n.LogWriterI18n` | `error` | `(Ljava/lang/Throwable;)V` | 23 | 36 |
| `API-44f65800b53fce12` | `org.apache.geode.i18n.LogWriterI18n` | `error` | `(Lorg/apache/geode/i18n/StringId;)V` | 23 | 36 |
| `API-3cb7c2e685fe4305` | `org.apache.geode.i18n.LogWriterI18n` | `error` | `(Lorg/apache/geode/i18n/StringId;Ljava/lang/Object;)V` | 23 | 36 |
| `API-d895447300b3d5ff` | `org.apache.geode.i18n.LogWriterI18n` | `error` | `(Lorg/apache/geode/i18n/StringId;Ljava/lang/Object;Ljava/lang/Throwable;)V` | 23 | 36 |
| `API-00f26d2645ba1ff5` | `org.apache.geode.i18n.LogWriterI18n` | `error` | `(Lorg/apache/geode/i18n/StringId;Ljava/lang/Throwable;)V` | 23 | 36 |
| `API-1d05358f169233d0` | `org.apache.geode.i18n.LogWriterI18n` | `error` | `(Lorg/apache/geode/i18n/StringId;[Ljava/lang/Object;)V` | 23 | 36 |
| `API-26d2e2d5e697bc4d` | `org.apache.geode.i18n.LogWriterI18n` | `error` | `(Lorg/apache/geode/i18n/StringId;[Ljava/lang/Object;Ljava/lang/Throwable;)V` | 23 | 36 |
| `API-f0049eba237b7103` | `org.apache.geode.i18n.LogWriterI18n` | `errorEnabled` | `()Z` | 23 | 36 |
| `API-0c85c32ea0d40931` | `org.apache.geode.i18n.LogWriterI18n` | `exiting` | `(Ljava/lang/String;Ljava/lang/String;)V` | 23 | 36 |
| `API-0982dbdc61fc6553` | `org.apache.geode.i18n.LogWriterI18n` | `fine` | `(Ljava/lang/String;)V` | 23 | 36 |
| `API-158827e2704c8a1f` | `org.apache.geode.i18n.LogWriterI18n` | `fine` | `(Ljava/lang/String;Ljava/lang/Throwable;)V` | 23 | 36 |
| `API-0faaab3139444b99` | `org.apache.geode.i18n.LogWriterI18n` | `fine` | `(Ljava/lang/Throwable;)V` | 23 | 36 |
| `API-e6b5831ede87bb9f` | `org.apache.geode.i18n.LogWriterI18n` | `fineEnabled` | `()Z` | 23 | 36 |
| `API-56b64ff9db780469` | `org.apache.geode.i18n.LogWriterI18n` | `finer` | `(Ljava/lang/String;)V` | 23 | 36 |
| `API-ec17c7f3fb2945b8` | `org.apache.geode.i18n.LogWriterI18n` | `finer` | `(Ljava/lang/String;Ljava/lang/Throwable;)V` | 23 | 36 |
| `API-c3a18824ca094969` | `org.apache.geode.i18n.LogWriterI18n` | `finer` | `(Ljava/lang/Throwable;)V` | 23 | 36 |
| `API-35c9e36df922aaa4` | `org.apache.geode.i18n.LogWriterI18n` | `finerEnabled` | `()Z` | 23 | 36 |
| `API-4af3e9e483789f44` | `org.apache.geode.i18n.LogWriterI18n` | `finest` | `(Ljava/lang/String;)V` | 23 | 36 |
| `API-8b50ae01ce163f76` | `org.apache.geode.i18n.LogWriterI18n` | `finest` | `(Ljava/lang/String;Ljava/lang/Throwable;)V` | 23 | 36 |
| `API-2eae5fbe9e0bbdf8` | `org.apache.geode.i18n.LogWriterI18n` | `finest` | `(Ljava/lang/Throwable;)V` | 23 | 36 |
| `API-51f0d2e6411cabf4` | `org.apache.geode.i18n.LogWriterI18n` | `finestEnabled` | `()Z` | 23 | 36 |
| `API-4f31e061651d9301` | `org.apache.geode.i18n.LogWriterI18n` | `getHandler` | `()Ljava/util/logging/Handler;` | 23 | 36 |
| `API-586ce692734d1ea6` | `org.apache.geode.i18n.LogWriterI18n` | `info` | `(Ljava/lang/Throwable;)V` | 23 | 36 |
| `API-372f0bc684de016d` | `org.apache.geode.i18n.LogWriterI18n` | `info` | `(Lorg/apache/geode/i18n/StringId;)V` | 23 | 36 |
| `API-a637672bba2a1bb9` | `org.apache.geode.i18n.LogWriterI18n` | `info` | `(Lorg/apache/geode/i18n/StringId;Ljava/lang/Object;)V` | 23 | 36 |
| `API-749464e1c3496061` | `org.apache.geode.i18n.LogWriterI18n` | `info` | `(Lorg/apache/geode/i18n/StringId;Ljava/lang/Object;Ljava/lang/Throwable;)V` | 23 | 36 |
| `API-da2e8a1371a32bad` | `org.apache.geode.i18n.LogWriterI18n` | `info` | `(Lorg/apache/geode/i18n/StringId;Ljava/lang/Throwable;)V` | 23 | 36 |
| `API-635b389b07e58731` | `org.apache.geode.i18n.LogWriterI18n` | `info` | `(Lorg/apache/geode/i18n/StringId;[Ljava/lang/Object;)V` | 23 | 36 |
| `API-a366c15d28af5359` | `org.apache.geode.i18n.LogWriterI18n` | `info` | `(Lorg/apache/geode/i18n/StringId;[Ljava/lang/Object;Ljava/lang/Throwable;)V` | 23 | 36 |
| `API-a8ac7a2880512fdf` | `org.apache.geode.i18n.LogWriterI18n` | `infoEnabled` | `()Z` | 23 | 36 |
| `API-e338283bf27ac732` | `org.apache.geode.i18n.LogWriterI18n` | `severe` | `(Ljava/lang/Throwable;)V` | 23 | 36 |
| `API-ba496f6541ac2509` | `org.apache.geode.i18n.LogWriterI18n` | `severe` | `(Lorg/apache/geode/i18n/StringId;)V` | 23 | 36 |
| `API-4b2bcfeebbf7cf23` | `org.apache.geode.i18n.LogWriterI18n` | `severe` | `(Lorg/apache/geode/i18n/StringId;Ljava/lang/Object;)V` | 23 | 36 |
| `API-5065919bdc51f6c3` | `org.apache.geode.i18n.LogWriterI18n` | `severe` | `(Lorg/apache/geode/i18n/StringId;Ljava/lang/Object;Ljava/lang/Throwable;)V` | 23 | 36 |
| `API-4838fc7516f7e37d` | `org.apache.geode.i18n.LogWriterI18n` | `severe` | `(Lorg/apache/geode/i18n/StringId;Ljava/lang/Throwable;)V` | 23 | 36 |
| `API-98a4feeda5536ca7` | `org.apache.geode.i18n.LogWriterI18n` | `severe` | `(Lorg/apache/geode/i18n/StringId;[Ljava/lang/Object;)V` | 23 | 36 |
| `API-26f9d2967e8e3722` | `org.apache.geode.i18n.LogWriterI18n` | `severe` | `(Lorg/apache/geode/i18n/StringId;[Ljava/lang/Object;Ljava/lang/Throwable;)V` | 23 | 36 |
| `API-6c9ae2c17280d118` | `org.apache.geode.i18n.LogWriterI18n` | `severeEnabled` | `()Z` | 23 | 36 |
| `API-e545e193113afeb2` | `org.apache.geode.i18n.LogWriterI18n` | `throwing` | `(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)V` | 23 | 36 |
| `API-37d35b19f44d1ac5` | `org.apache.geode.i18n.LogWriterI18n` | `warning` | `(Ljava/lang/Throwable;)V` | 23 | 36 |
| `API-0557733284ac85b2` | `org.apache.geode.i18n.LogWriterI18n` | `warning` | `(Lorg/apache/geode/i18n/StringId;)V` | 23 | 36 |
| `API-cd14baa64d7f17c9` | `org.apache.geode.i18n.LogWriterI18n` | `warning` | `(Lorg/apache/geode/i18n/StringId;Ljava/lang/Object;)V` | 23 | 36 |
| `API-4391681dce5d9e6f` | `org.apache.geode.i18n.LogWriterI18n` | `warning` | `(Lorg/apache/geode/i18n/StringId;Ljava/lang/Object;Ljava/lang/Throwable;)V` | 23 | 36 |
| `API-a2895784362b33ac` | `org.apache.geode.i18n.LogWriterI18n` | `warning` | `(Lorg/apache/geode/i18n/StringId;Ljava/lang/Throwable;)V` | 23 | 36 |
| `API-96e33b252a5270d5` | `org.apache.geode.i18n.LogWriterI18n` | `warning` | `(Lorg/apache/geode/i18n/StringId;[Ljava/lang/Object;)V` | 23 | 36 |
| `API-bed9a5af234a40bb` | `org.apache.geode.i18n.LogWriterI18n` | `warning` | `(Lorg/apache/geode/i18n/StringId;[Ljava/lang/Object;Ljava/lang/Throwable;)V` | 23 | 36 |
| `API-85c8491683f55495` | `org.apache.geode.i18n.LogWriterI18n` | `warningEnabled` | `()Z` | 23 | 36 |

### client-localized-message-values — 9 IDs

| API ID | Native receiver | Member | JVM descriptor | Owner | Consumer |
| --- | --- | --- | --- | --- | --- |
| `API-4162c5b377859ca7` | `org.apache.geode.i18n.StringId` | `<init>` | `(ILjava/lang/String;)V` | 23 | 36 |
| `API-59cfc0a52f93a17b` | `org.apache.geode.i18n.StringId` | `<type>` | `(type)` | 23 | 36 |
| `API-fdefaa59126d339a` | `org.apache.geode.i18n.StringId` | `LITERAL` | `Lorg/apache/geode/i18n/StringId;` | 23 | 36 |
| `API-2fb4462bc36dcf41` | `org.apache.geode.i18n.StringId` | `getRawText` | `()Ljava/lang/String;` | 23 | 36 |
| `API-7932e97ca61086e4` | `org.apache.geode.i18n.StringId` | `id` | `I` | 23 | 36 |
| `API-8df085c867298981` | `org.apache.geode.i18n.StringId` | `toLocalizedString` | `()Ljava/lang/String;` | 23 | 36 |
| `API-0630c148f5f5f11d` | `org.apache.geode.i18n.StringId` | `toLocalizedString` | `([Ljava/lang/Object;)Ljava/lang/String;` | 23 | 36 |
| `API-7330bc42aebb63dc` | `org.apache.geode.i18n.StringId` | `toString` | `()Ljava/lang/String;` | 23 | 36 |
| `API-bd3736498854b769` | `org.apache.geode.i18n.StringId` | `toString` | `([Ljava/lang/Object;)Ljava/lang/String;` | 23 | 36 |

### client-internal-utility-replacements — 6 IDs

| API ID | Native receiver | Member | JVM descriptor | Owner | Consumer |
| --- | --- | --- | --- | --- | --- |
| `API-59055650a364a6ed` | `org.apache.geode.distributed.internal.InternalDistributedSystem` | `<type>` | `(type)` | 36 | 36 |
| `API-bf36805a47c4bce2` | `org.apache.geode.distributed.internal.InternalDistributedSystem` | `getAnyInstance` | `()Lorg/apache/geode/distributed/internal/InternalDistributedSystem;` | 36 | 36 |
| `API-d90ae01089bdb8d0` | `org.apache.geode.internal.cache.GemFireCacheImpl` | `<type>` | `(type)` | 36 | 36 |
| `API-d008442cd75a6c20` | `org.apache.geode.internal.cache.InternalAttributesFactory` | `<type>` | `(type)` | 36 | 36 |
| `API-30cb5958e4ab3937` | `org.apache.geode.internal.cache.InternalRegionAttributes` | `<type>` | `(type)` | 36 | 36 |
| `API-03bffdd6b4f81aa3` | `org.apache.geode.internal.cache.LocalRegion` | `<type>` | `(type)` | 36 | 36 |

### client-function-identity — 5 IDs

| API ID | Native receiver | Member | JVM descriptor | Owner | Consumer |
| --- | --- | --- | --- | --- | --- |
| `API-9fde64c6a43fa5e0` | `org.apache.geode.lang.Identifiable` | `<type>` | `(type)` | 25 | 32 |
| `API-496b36b72e4df8fd` | `org.apache.geode.lang.Identifiable` | `exists` | `(Ljava/util/List;Ljava/lang/String;)Z` | 25 | 32 |
| `API-ab2c371eb81216fe` | `org.apache.geode.lang.Identifiable` | `find` | `(Ljava/util/List;Ljava/lang/String;)Lorg/apache/geode/lang/Identifiable;` | 25 | 32 |
| `API-1642a7a1ac5d046e` | `org.apache.geode.lang.Identifiable` | `getId` | `()Ljava/lang/Comparable;` | 25 | 32 |
| `API-ada3a1af1e6493b3` | `org.apache.geode.lang.Identifiable` | `remove` | `(Ljava/util/List;Ljava/lang/String;)V` | 25 | 32 |

### client-server-region-discovery — 4 IDs

| API ID | Native receiver | Member | JVM descriptor | Owner | Consumer |
| --- | --- | --- | --- | --- | --- |
| `API-187926d4df2a942e` | `org.apache.geode.management.internal.cli.domain.RegionInformation` | `<type>` | `(type)` | 25 | 32 |
| `API-f5c210ade9f72402` | `org.apache.geode.management.internal.cli.domain.RegionInformation` | `getName` | `()Ljava/lang/String;` | 25 | 32 |
| `API-b687ae3b8d4dbff7` | `org.apache.geode.management.internal.cli.functions.GetRegionsFunction` | `<init>` | `()V` | 25 | 32 |
| `API-25734c29d74f07f5` | `org.apache.geode.management.internal.cli.functions.GetRegionsFunction` | `<type>` | `(type)` | 25 | 32 |

### excluded-internal-locator-hosting — 3 IDs

| API ID | Native receiver | Member | JVM descriptor | Owner | Consumer |
| --- | --- | --- | --- | --- | --- |
| `API-ebfbaa66f4529dd3` | `org.apache.geode.distributed.internal.InternalLocator` | `<type>` | `(type)` | 36 | 36 |
| `API-678123e7de298a38` | `org.apache.geode.distributed.internal.InternalLocator` | `getLocator` | `()Lorg/apache/geode/distributed/internal/InternalLocator;` | 36 | 36 |
| `API-2cab6e8c25e0ef41` | `org.apache.geode.distributed.internal.tcpserver.HostAddress` | `<type>` | `(type)` | 36 | 36 |

### excluded-internal-peer-cache — 1 IDs

| API ID | Native receiver | Member | JVM descriptor | Owner | Consumer |
| --- | --- | --- | --- | --- | --- |
| `API-a7360909ee53b0a1` | `org.apache.geode.internal.cache.InternalCache` | `<type>` | `(type)` | 36 | 36 |

### excluded-wan-property-helper — 1 IDs

| API ID | Native receiver | Member | JVM descriptor | Owner | Consumer |
| --- | --- | --- | --- | --- | --- |
| `API-c805e28adf076662` | `org.apache.geode.logging.internal.deprecation.DeprecatedSystemProperty` | `<type>` | `(type)` | 36 | 36 |

### excluded-redundancy-management-results — 33 IDs

| API ID | Native receiver | Member | JVM descriptor | Owner | Consumer |
| --- | --- | --- | --- | --- | --- |
| `API-4dee65be7890aaf6` | `org.apache.geode.management.api.JsonSerializable` | `<type>` | `(type)` | 23 | 36 |
| `API-f68da161171ef24e` | `org.apache.geode.management.runtime.OperationResult` | `<type>` | `(type)` | 23 | 36 |
| `API-7637f89ef7db9708` | `org.apache.geode.management.runtime.OperationResult` | `getStatusMessage` | `()Ljava/lang/String;` | 23 | 36 |
| `API-8f525c585b0b572d` | `org.apache.geode.management.runtime.OperationResult` | `getSuccess` | `()Z` | 23 | 36 |
| `API-04adb593df514d2d` | `org.apache.geode.management.runtime.RegionRedundancyStatus` | `<type>` | `(type)` | 23 | 36 |
| `API-4b168a72745c1882` | `org.apache.geode.management.runtime.RegionRedundancyStatus` | `getActualRedundancy` | `()I` | 23 | 36 |
| `API-25c20325ef81bffb` | `org.apache.geode.management.runtime.RegionRedundancyStatus` | `getConfiguredRedundancy` | `()I` | 23 | 36 |
| `API-992918e0e109dfbb` | `org.apache.geode.management.runtime.RegionRedundancyStatus` | `getRegionName` | `()Ljava/lang/String;` | 23 | 36 |
| `API-91b48f1ca0c7e1d1` | `org.apache.geode.management.runtime.RegionRedundancyStatus` | `getStatus` | `()Lorg/apache/geode/management/runtime/RegionRedundancyStatus$RedundancyStatus;` | 23 | 36 |
| `API-3d0217da50e657b4` | `org.apache.geode.management.runtime.RegionRedundancyStatus$RedundancyStatus` | `<type>` | `(type)` | 23 | 36 |
| `API-f4369dc27268298f` | `org.apache.geode.management.runtime.RegionRedundancyStatus$RedundancyStatus` | `NOT_SATISFIED` | `Lorg/apache/geode/management/runtime/RegionRedundancyStatus$RedundancyStatus;` | 23 | 36 |
| `API-58404a5fd4d55f20` | `org.apache.geode.management.runtime.RegionRedundancyStatus$RedundancyStatus` | `NO_REDUNDANT_COPIES` | `Lorg/apache/geode/management/runtime/RegionRedundancyStatus$RedundancyStatus;` | 23 | 36 |
| `API-a073d0032ff028ba` | `org.apache.geode.management.runtime.RegionRedundancyStatus$RedundancyStatus` | `SATISFIED` | `Lorg/apache/geode/management/runtime/RegionRedundancyStatus$RedundancyStatus;` | 23 | 36 |
| `API-e4abf540bad05b81` | `org.apache.geode.management.runtime.RegionRedundancyStatus$RedundancyStatus` | `valueOf` | `(Ljava/lang/String;)Lorg/apache/geode/management/runtime/RegionRedundancyStatus$RedundancyStatus;` | 23 | 36 |
| `API-ea299533e4a528f1` | `org.apache.geode.management.runtime.RegionRedundancyStatus$RedundancyStatus` | `values` | `()[Lorg/apache/geode/management/runtime/RegionRedundancyStatus$RedundancyStatus;` | 23 | 36 |
| `API-9c7b44cfad2c3644` | `org.apache.geode.management.runtime.RestoreRedundancyResults` | `<type>` | `(type)` | 23 | 36 |
| `API-057b9d235fdaf1a7` | `org.apache.geode.management.runtime.RestoreRedundancyResults` | `getIncludedRegionsWithNoMembers` | `()Ljava/util/List;` | 23 | 36 |
| `API-4a355dd3019bfa04` | `org.apache.geode.management.runtime.RestoreRedundancyResults` | `getRegionOperationMessage` | `()Ljava/lang/String;` | 23 | 36 |
| `API-3bc2c5b7deee4dec` | `org.apache.geode.management.runtime.RestoreRedundancyResults` | `getRegionOperationStatus` | `()Lorg/apache/geode/management/runtime/RestoreRedundancyResults$Status;` | 23 | 36 |
| `API-a2ae523e9a54e8c8` | `org.apache.geode.management.runtime.RestoreRedundancyResults` | `getRegionResult` | `(Ljava/lang/String;)Lorg/apache/geode/management/runtime/RegionRedundancyStatus;` | 23 | 36 |
| `API-e3a784b58fea63eb` | `org.apache.geode.management.runtime.RestoreRedundancyResults` | `getRegionResults` | `()Ljava/util/Map;` | 23 | 36 |
| `API-8dcd763f5a971cf0` | `org.apache.geode.management.runtime.RestoreRedundancyResults` | `getSatisfiedRedundancyRegionResults` | `()Ljava/util/Map;` | 23 | 36 |
| `API-5769f7b4ca86a310` | `org.apache.geode.management.runtime.RestoreRedundancyResults` | `getStatusMessage` | `()Ljava/lang/String;` | 23 | 36 |
| `API-92cd99c8dfe3411f` | `org.apache.geode.management.runtime.RestoreRedundancyResults` | `getSuccess` | `()Z` | 23 | 36 |
| `API-c585c62953b910cc` | `org.apache.geode.management.runtime.RestoreRedundancyResults` | `getTotalPrimaryTransferTime` | `()J` | 23 | 36 |
| `API-ea51320e7fa57343` | `org.apache.geode.management.runtime.RestoreRedundancyResults` | `getTotalPrimaryTransfersCompleted` | `()I` | 23 | 36 |
| `API-7bd91152a1e64c70` | `org.apache.geode.management.runtime.RestoreRedundancyResults` | `getUnderRedundancyRegionResults` | `()Ljava/util/Map;` | 23 | 36 |
| `API-faaa4f4e57d39347` | `org.apache.geode.management.runtime.RestoreRedundancyResults` | `getZeroRedundancyRegionResults` | `()Ljava/util/Map;` | 23 | 36 |
| `API-c641ac993609b136` | `org.apache.geode.management.runtime.RestoreRedundancyResults$Status` | `<type>` | `(type)` | 23 | 36 |
| `API-bfa2588b07e424f9` | `org.apache.geode.management.runtime.RestoreRedundancyResults$Status` | `FAILURE` | `Lorg/apache/geode/management/runtime/RestoreRedundancyResults$Status;` | 23 | 36 |
| `API-29c66deb45627f2b` | `org.apache.geode.management.runtime.RestoreRedundancyResults$Status` | `SUCCESS` | `Lorg/apache/geode/management/runtime/RestoreRedundancyResults$Status;` | 23 | 36 |
| `API-f73d5258ec808cb5` | `org.apache.geode.management.runtime.RestoreRedundancyResults$Status` | `valueOf` | `(Ljava/lang/String;)Lorg/apache/geode/management/runtime/RestoreRedundancyResults$Status;` | 23 | 36 |
| `API-5b26588986217448` | `org.apache.geode.management.runtime.RestoreRedundancyResults$Status` | `values` | `()[Lorg/apache/geode/management/runtime/RestoreRedundancyResults$Status;` | 23 | 36 |
