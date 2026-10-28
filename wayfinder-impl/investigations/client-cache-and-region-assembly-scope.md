# Client cache and region assembly scope — 2026-10-28

[Task 67](../../wayfinder-issues/67-audit-cache-and-region-assembly-client-scope.md) classifies its complete fixed inventory: **332 IDs across 20 types: 275 required-client / 57 excluded-server / zero unresolved**. This resolves scope only; implementation and consumer acceptance remain in active tasks. No new runtime probe or Java/build change was necessary.

Evidence is GemFire **10.4.0-build.3056.df98c835**, pinned source commit `df98c8357afe85853d9a4776255d4f0e7cf064cb` in `/Users/uk016635/projects/gemfire`, read using git show/git grep. Paths below are relative to `gemfire-core/src/main/java/org/apache/geode/` unless explicitly qualified. Exact IDs include constructors, protected members, constants, overloads and inherited receiver rows. No type-wide rule classifies unassigned IDs.

## client-cache-services

**57 IDs — required-client.** `internal/cache/GemFireCacheImpl` implements close (including keepAlive), cancellation, region/root lookup, copy-on-read, initializer properties, JNDI and named region attributes. `setRegionAttributes` removes a name on null; `listRegionAttributes` is an unmodifiable map view. Keep connection ownership, identity, closed-state failures and explicit/default behavior. `cache/RegionService` is also implemented by `cache/client/internal/ProxyCache`: its close closes the authenticated view, its region lookup requires EMPTY data policy, and root enumeration filters to eligible regions. A service handle must not imply ownership of the containing cache.

`GemFireCacheImpl.loadCacheXml` saves/restores the thread-local cache used during callbacks, parses XML and creates its contents; optional property substitution and IO failure translation are observable. Preserve caller stream ownership and cleanup of internally created readers. `internal/cache/xmlcache/ClientCacheCreation.create` checks client mode, creates pools/disks and named attributes, initializes declarables, creates regions and runs the initializer. `CacheCreation.initializeDeclarablesMap` calls initialize then init. Client XML is retained; the presence of shared server parser branches does not authorize their exposure.

Task 29 owns unfinished assembly; task 35 owns cache entry-point adoption and task 30 region/configuration wiring. Use typed collaborators for XML callback construction, not a static Spring locator. No native Cache, RegionAttributes or JNDI implementation type leaks through the SPI.

## client-cache-collaborators

**42 IDs — required-client.** `GemFireCacheImpl.createDiskStoreFactory/findDiskStore/getResourceManager/getSnapshotService` have client-local paths; the snapshot accessor constructs CacheSnapshotServiceImpl without a peer-only guard. `getDistributedSystem` exposes the client's system, and logger/security logger (including inherited I18n variants) are client runtime collaborators. Preserve client observations through native-free equivalents, not public internal loggers or distributed-system implementation objects.

`getQueryService()` requires the default client pool; `getQueryService(poolName)` validates named pool existence; `getLocalQueryService()` is separate. JSON factory defaults to BSON or uses the explicit storage format. `ProxyCache` supplies user-associated query/JSON services. `createAuthenticatedView` requires an existing selected/default pool and multiuser authentication; closing the view must not close its owner. `readyForEvents` uses client durable properties and PoolManagerImpl, preserving lifecycle and subscription validation.

Task 29 assembles these accessors and task 35 adopts them. Existing subsystem owners remain: 20 pools/durable, 21 queries, 23 runtime/logging, 24 authentication, 26 JSON, 27 disk/resource and 44 snapshots. Audits 69–74 retain their own fixed payload IDs. This decision does not claim those payloads or services are implemented, or require server cluster administration.

## client-cache-lookup

**5 IDs — required-client.** `cache/CacheFactory` delegates getAnyInstance/getInstance/getInstanceCloseOk to `internal/cache/CacheFactoryStatics`. Those lookups do not exclude clients: they check existence, closed/cancellation state and distributed-system identity, with a close-ok variation. getVersion is version observation. Retain these four operations and the mixed type row through injected owner/borrower services (tasks 29/35); do not recreate static service locators. CacheFactory constructors/create remain excluded by the earlier peer-creation audit. The four peer builder setters are separately excluded below.

## client-attribute-assembly

**43 IDs — required-client.** `cache/client/internal/ClientRegionFactoryImpl` resolves named attributes and creates `InternalClientRegionAttributesFactory` copies, applies LOCAL scope and client subscription defaults, then uses the attributes for root/subregion creation. Its missing required default pool fails explicitly. `InternalClientRegionAttributesFactory` preserves serverRegionName and validates the pool relationship; `internal/cache/InternalAttributesFactory` copies explicit-setting flags, validates, and returns cloned attributes. Retain both empty and copy constructors, constants, create/createRegionAttributes and validation. The inherited protected regionAttributes field represents client factory extension state; use a typed equivalent, never expose InternalRegionAttributes.

`AttributesFactory`, `AttributesMutator`, `Region.getAttributes/getAttributesMutator` and `AbstractRegion/LocalRegion` carry callback registration, loader/writer access, cloning, constraints, capacity/load/concurrency, statistics, pool name and disk settings. Preserve listener order and identity, old loader/writer return values, live mutation and getRegion ownership; singular listener access fails if multiple listeners exist. Named disk-store and legacy directory/size/write attributes retain client-local persistence/overflow semantics and mutual-exclusion validation. Explicit statistics/expiry choices and defaults must not be flattened.

Task 30 takes unfinished rows previously owned by resolved foundation 46; implemented cloning accessors retain their historical foundation owner. Existing active 19/49 owners remain. Task 30 is the consumer owner, with 19/29/47/49 collaborators. Task 66 already classified attribute-copy subregion creation: this batch supplies its configuration requirements without taking its ID. No peer factories, partition hosting or native attribute signatures are restored.

## client-legacy-local-policy

**16 IDs — required-client.** `InternalAttributesFactory.setMirrorType` maps NONE back to NORMAL when the current policy replicates; KEYS/KEYS_VALUES select REPLICATE unless already replicating. setPersistBackup maps NORMAL/REPLICATE to PERSISTENT_REPLICATE and reverses persistence when false. The partition branch is outside the client-only product. `AbstractRegion.getMirrorType` observes NORMAL/PRELOADED/EMPTY as NONE, replicated policies as KEYS_VALUES, and otherwise fails; getPersistBackup observes persistence.

`cache/MirrorType` has NONE (ordinal 0, NORMAL), KEYS (1, REPLICATE) and KEYS_VALUES (2, REPLICATE), identity predicates, ordinal lookup and named string values. `CacheXmlParser` accepts legacy mirror/persist-backup attributes; `ClientRegionFactoryImpl` supplies LOCAL scope. Thus these legacy values can describe client-local storage/persistence even though their names originate in peer mirroring. Retain equivalent conversions and invalid-input behavior in task 49 with task 30 adoption, not a server region shortcut or distributed replication API. Exact enum shape is an implementation choice constrained by these observable conversions.

## client-eviction-config-values

**112 IDs — required-client.** `cache/EvictionAttributes.convertToConfigEvictionAttributes` is a public conversion from retained client eviction attributes to `cache/configuration/RegionAttributesType.EvictionAttributes`; it selects heap, memory or entry values and carries action, maximum and optional sizer class. `internal/cache/EvictionAttributesImpl.fromConfig` consumes these nodes. This is the concrete client path that justifies retaining the values; client XML itself uses RegionAttributesCreation, not these JAXB classes.

`RegionAttributesType.EvictionAttributes.generate` returns null when every argument is null; otherwise action parsing defaults blank to local-destroy. Memory maximum wins over entry maximum, and no maximum selects heap. The String sizer overload constructs a ClassName value; the ClassName overload preserves its initialization properties. DTO setters permit independently populated branches. fromConfig chooses heap before entry before memory for algorithm, but memory before entry before heap for action and memory before entry for maximum. Do not silently normalize ambiguous combinations. Class loading validates ObjectSizer and Declarable before construction, with the native missing-class fallback; downstream implementation must keep conversion failures and defaults explicit.

`EnumActionDestroyOverflow` defines local-destroy and overflow-to-disk strings, blank/default parsing, invalid-value failure and ordinary enum lookup. The three LRU value classes expose action/maximum and, for heap/memory, inherited declarable state. `ClassNameType`, `DeclarableType`, `ObjectType` and `ParameterType` supply that state and nested parameter values: constructors, protected fields, setters, getters and equality/hash/string observations belong to the value contract. Declarable's blank constructor leaves fields unset; getParameters lazily creates a live list, and setParameters(null) restores null. Properties conversion uses string properties. ObjectType permits both string and declarable fields; its string representation prefers the string. ParameterType adds name; equality includes superclass state, while its hash is name-based. Declarable equality requires exact class and compares class name plus parameters; inherited LRU equality does not additionally compare action/maximum. Preserve these distinctions where equivalent conversion exposes them, without copying the entire server schema or requiring JAXB in Spring.

Task 49 owns these missing values/conversions, task 30 adoption and task 19/47 eviction collaborators. Audit 68 still owns EvictionAttributes itself. The separately excluded DeclarableType.EMPTY is a gfsh removal sentinel, not a normal absent client callback value.

## server-partition-config

**31 IDs — excluded-server.** `RegionAttributesType.PartitionAttributes` fields/accessors, generate and combine carry local/total memory, bucket counts, redundant copies, colocation, recovery delays, partition resolver and listeners. These configure hosting; they do not inspect or route a client request. `InternalAttributesFactory.setBucketRegion` marks server bucket regions, and partition validation rejects a pool name. Approved client-only scope excludes hosting even if a shared low-level factory can construct such values. All 31 assigned rows are missing with no SPI mapping; no implementation is retired by changing their scope.

## server-conflation-aliases

**4 IDs — excluded-server.** `InternalAttributesFactory.setEnableBridgeConflation/setEnableConflation` delegate to subscription conflation; matching RegionAttributes getters observe that server-region outgoing queue setting. This follows the prior subscription-conflation exclusion, not the similarly named client subscription or interest configuration. Four rows, all missing/unmapped. Client pool subscription controls remain tasks 20/45 and audit 69.

## server-cache-runtime

**15 IDs — excluded-server.** `GemFireCacheImpl.setLockLease/setLockTimeout/setSearchTimeout/setMessageSyncInterval` call throwIfClient; their getters expose those same peer/server settings. ClientCacheCreation also rejects those setters. Message synchronization uses HARegionQueue. The eight setter/getter rows are excluded; similarly named client pool timeouts remain required elsewhere.

getMembers() returns distribution-manager peers; getMembers(region) reads distributed/partitioned region membership; getAdminMembers is a legacy empty default in Cache. These three member-discovery operations do not establish client server-list/pool observation requirements. Event member payloads are unaffected.

The four reconnect APIs delegate to distributed-system rejoin and reconnected cache lookup. `ClientCacheFactory.basicCreate` forces locators empty and client mode; `distributed/internal/InternalDistributedSystem.initialize` treats an empty locator set as a loner, and reconnect initialization/tryReconnect concern forced-disconnect or missing-role peer recovery. Client pool reconnect/failover remains a separate retained contract. These 15 rows are missing/unmapped; no client behavior or existing SPI mapping is removed.

## server-wan-factory

**2 IDs — excluded-server.** `GemFireCacheImpl.createGatewaySenderFactory` returns GatewaySenderFactoryImpl; `gemfire-wan/.../cache/wan/internal/GatewaySenderFactoryImpl` builds serial/parallel WAN senders. There is no claim that this accessor itself throws on a client: approved product scope excludes hosting despite this inherited callable path. Both GemFireCache and ClientCache receiver rows are missing with no SPI mapping. The Cache receiver was already classified by the earlier audit.

## server-cache-builder

**4 IDs — excluded-server.** `CacheFactory` configures InternalCacheBuilder's peer construction path. Its set(String,String), setSecurityManager, setPostProcessor and addMeterSubregistry entries follow the already excluded constructors/create. This does not exclude all properties or client metrics: separately inventoried ClientCacheFactory contracts retain their own audit owners. Four missing/unmapped rows; its mixed type and existing-client lookup methods are retained separately.

## server-declarable-removal-sentinel

**1 IDs — excluded-server.** `cache/configuration/DeclarableType.EMPTY` is documented in pinned source as the sentinel for gfsh `alter region --cache-loader=''`. Client callback replacement/absence is covered by typed region attributes/mutators; it does not require this shared mutable static sentinel. This one missing row has no public SPI mapping. Other DeclarableType value operations remain required through eviction configuration conversion.

## Ownership and reconciliation

The fixed assignment ledger is unchanged. Required unfinished rows keep active tasks 19/29/49 or move from completed foundation 46 to active task 30. Implemented cloning rows retain foundation history; all consumers remain active tasks 30/35. The exact matrix records the owners and remaining behavior. None of the 57 exclusions has a SPI mapping or implemented status. Generated source dispositions and existing-SPI dispositions are reconciled by their linked type/member IDs, including inherited methods. Ordinary client XML, client-local persistence and typed callbacks remain requirements.

Task 68 is next with its fixed 154 expiry/eviction/compression IDs. Tasks 69–76 retain their own fixed IDs; the service access and value decisions above are inputs, not additional assignments. Full native/mock behavior and Spring migration remain downstream acceptance. Tasks 59/64 stay open.

## Exact ID ledger

Each subsection inherits the decision, evidence and behavior above. Owner columns identify the active implementation/consumer tasks (or historical implemented owner); excluded rows are reviewed by task 67 and their historical implementation owners are not requests to implement them.

### client-cache-services — 57 IDs

| API ID | Native receiver | Member | JVM descriptor | Implementation / consumer task |
| --- | --- | --- | --- | --- |
| `API-d85a207e6a52cbfe` | `org.apache.geode.cache.Cache` | `<type>` | `(type)` | 29 / 35 |
| `API-c4fea72f8051f833` | `org.apache.geode.cache.Cache` | `close` | `()V` | 29 / 35 |
| `API-f6d933340f772c96` | `org.apache.geode.cache.Cache` | `close` | `(Z)V` | 29 / 35 |
| `API-a76dd2ada9cffb5a` | `org.apache.geode.cache.Cache` | `getCancelCriterion` | `()Lorg/apache/geode/CancelCriterion;` | 29 / 35 |
| `API-9af7268c67a69b84` | `org.apache.geode.cache.Cache` | `getCopyOnRead` | `()Z` | 29 / 35 |
| `API-b4f631b6636375a8` | `org.apache.geode.cache.Cache` | `getInitializer` | `()Lorg/apache/geode/cache/Declarable;` | 29 / 35 |
| `API-663fe0ef6fef3460` | `org.apache.geode.cache.Cache` | `getInitializerProps` | `()Ljava/util/Properties;` | 29 / 35 |
| `API-592d9f0775c933e2` | `org.apache.geode.cache.Cache` | `getJNDIContext` | `()Ljavax/naming/Context;` | 29 / 35 |
| `API-9f71921da0abae35` | `org.apache.geode.cache.Cache` | `getName` | `()Ljava/lang/String;` | 29 / 35 |
| `API-1aac3d7591e05202` | `org.apache.geode.cache.Cache` | `getRegion` | `(Ljava/lang/String;)Lorg/apache/geode/cache/Region;` | 29 / 35 |
| `API-b6a9273261f09bb4` | `org.apache.geode.cache.Cache` | `getRegionAttributes` | `(Ljava/lang/String;)Lorg/apache/geode/cache/RegionAttributes;` | 29 / 35 |
| `API-ec93f2b5f430a911` | `org.apache.geode.cache.Cache` | `isClosed` | `()Z` | 29 / 35 |
| `API-38539b316142bc6d` | `org.apache.geode.cache.Cache` | `listRegionAttributes` | `()Ljava/util/Map;` | 29 / 35 |
| `API-b5ca2b14b0b439ec` | `org.apache.geode.cache.Cache` | `loadCacheXml` | `(Ljava/io/InputStream;)V` | 29 / 35 |
| `API-fd8be6da6a5f07fd` | `org.apache.geode.cache.Cache` | `rootRegions` | `()Ljava/util/Set;` | 29 / 35 |
| `API-ffef20d9866541db` | `org.apache.geode.cache.Cache` | `setCopyOnRead` | `(Z)V` | 29 / 35 |
| `API-8b71d4adbefc0fc8` | `org.apache.geode.cache.Cache` | `setRegionAttributes` | `(Ljava/lang/String;Lorg/apache/geode/cache/RegionAttributes;)V` | 29 / 35 |
| `API-1ef9410dd409569f` | `org.apache.geode.cache.GemFireCache` | `<type>` | `(type)` | 29 / 35 |
| `API-c30f1a7e6fd0fb10` | `org.apache.geode.cache.GemFireCache` | `close` | `()V` | 29 / 35 |
| `API-cb4f9ef55a291a52` | `org.apache.geode.cache.GemFireCache` | `getCancelCriterion` | `()Lorg/apache/geode/CancelCriterion;` | 29 / 35 |
| `API-8efbfdedc92f7a59` | `org.apache.geode.cache.GemFireCache` | `getCopyOnRead` | `()Z` | 29 / 35 |
| `API-69363a10ebf91011` | `org.apache.geode.cache.GemFireCache` | `getInitializer` | `()Lorg/apache/geode/cache/Declarable;` | 29 / 35 |
| `API-be9375fededed065` | `org.apache.geode.cache.GemFireCache` | `getInitializerProps` | `()Ljava/util/Properties;` | 29 / 35 |
| `API-ef1b42d47669cb8a` | `org.apache.geode.cache.GemFireCache` | `getJNDIContext` | `()Ljavax/naming/Context;` | 29 / 35 |
| `API-aee7b04bd52b2979` | `org.apache.geode.cache.GemFireCache` | `getName` | `()Ljava/lang/String;` | 29 / 35 |
| `API-03ffbdc613fb993a` | `org.apache.geode.cache.GemFireCache` | `getRegion` | `(Ljava/lang/String;)Lorg/apache/geode/cache/Region;` | 29 / 35 |
| `API-11c9dd353e31ed3e` | `org.apache.geode.cache.GemFireCache` | `getRegionAttributes` | `(Ljava/lang/String;)Lorg/apache/geode/cache/RegionAttributes;` | 29 / 35 |
| `API-01b92094108e86c3` | `org.apache.geode.cache.GemFireCache` | `isClosed` | `()Z` | 29 / 35 |
| `API-8cde6d395e0e3be2` | `org.apache.geode.cache.GemFireCache` | `listRegionAttributes` | `()Ljava/util/Map;` | 29 / 35 |
| `API-7ce2978ec4910ede` | `org.apache.geode.cache.GemFireCache` | `loadCacheXml` | `(Ljava/io/InputStream;)V` | 29 / 35 |
| `API-2eeeca8de987e303` | `org.apache.geode.cache.GemFireCache` | `rootRegions` | `()Ljava/util/Set;` | 29 / 35 |
| `API-14592ee5b3828482` | `org.apache.geode.cache.GemFireCache` | `setCopyOnRead` | `(Z)V` | 29 / 35 |
| `API-dceb26e4771019ae` | `org.apache.geode.cache.GemFireCache` | `setRegionAttributes` | `(Ljava/lang/String;Lorg/apache/geode/cache/RegionAttributes;)V` | 29 / 35 |
| `API-34c10533ad084ee2` | `org.apache.geode.cache.RegionService` | `<type>` | `(type)` | 29 / 35 |
| `API-d0cf1dbdda4024e9` | `org.apache.geode.cache.RegionService` | `close` | `()V` | 29 / 35 |
| `API-c4cffb3f2b63f6f6` | `org.apache.geode.cache.RegionService` | `getCancelCriterion` | `()Lorg/apache/geode/CancelCriterion;` | 29 / 35 |
| `API-342d815d22dfc6fe` | `org.apache.geode.cache.RegionService` | `getRegion` | `(Ljava/lang/String;)Lorg/apache/geode/cache/Region;` | 29 / 35 |
| `API-2da27b2098ff34e4` | `org.apache.geode.cache.RegionService` | `isClosed` | `()Z` | 29 / 35 |
| `API-4f71af216f69b72f` | `org.apache.geode.cache.RegionService` | `rootRegions` | `()Ljava/util/Set;` | 29 / 35 |
| `API-031f9961cfa86710` | `org.apache.geode.cache.client.ClientCache` | `<type>` | `(type)` | 29 / 35 |
| `API-03bdc0b3c14ecbce` | `org.apache.geode.cache.client.ClientCache` | `close` | `()V` | 29 / 35 |
| `API-327aca70776709b4` | `org.apache.geode.cache.client.ClientCache` | `createClientRegionFactory` | `(Ljava/lang/String;)Lorg/apache/geode/cache/client/ClientRegionFactory;` | 29 / 35 |
| `API-f9976028bba8142d` | `org.apache.geode.cache.client.ClientCache` | `createClientRegionFactory` | `(Lorg/apache/geode/cache/client/ClientRegionShortcut;)Lorg/apache/geode/cache/client/ClientRegionFactory;` | 29 / 35 |
| `API-301ff589e4f15df7` | `org.apache.geode.cache.client.ClientCache` | `getCancelCriterion` | `()Lorg/apache/geode/CancelCriterion;` | 29 / 35 |
| `API-a03bee1245eaf347` | `org.apache.geode.cache.client.ClientCache` | `getCopyOnRead` | `()Z` | 29 / 35 |
| `API-43b0dcacc0458612` | `org.apache.geode.cache.client.ClientCache` | `getInitializer` | `()Lorg/apache/geode/cache/Declarable;` | 29 / 35 |
| `API-1212f7b2d64904cb` | `org.apache.geode.cache.client.ClientCache` | `getInitializerProps` | `()Ljava/util/Properties;` | 29 / 35 |
| `API-9abfb8674ce4be34` | `org.apache.geode.cache.client.ClientCache` | `getJNDIContext` | `()Ljavax/naming/Context;` | 29 / 35 |
| `API-fc740bda8292e52c` | `org.apache.geode.cache.client.ClientCache` | `getName` | `()Ljava/lang/String;` | 29 / 35 |
| `API-fbeb9edf5db5420d` | `org.apache.geode.cache.client.ClientCache` | `getRegion` | `(Ljava/lang/String;)Lorg/apache/geode/cache/Region;` | 29 / 35 |
| `API-4ab10fa50eeddea9` | `org.apache.geode.cache.client.ClientCache` | `getRegionAttributes` | `(Ljava/lang/String;)Lorg/apache/geode/cache/RegionAttributes;` | 29 / 35 |
| `API-97b9cf2ae2124611` | `org.apache.geode.cache.client.ClientCache` | `isClosed` | `()Z` | 29 / 35 |
| `API-1cf84207f8ada222` | `org.apache.geode.cache.client.ClientCache` | `listRegionAttributes` | `()Ljava/util/Map;` | 29 / 35 |
| `API-ee13f1bf216637f5` | `org.apache.geode.cache.client.ClientCache` | `loadCacheXml` | `(Ljava/io/InputStream;)V` | 29 / 35 |
| `API-040cef4e96d2b737` | `org.apache.geode.cache.client.ClientCache` | `rootRegions` | `()Ljava/util/Set;` | 29 / 35 |
| `API-775dbcbe9868b1bd` | `org.apache.geode.cache.client.ClientCache` | `setCopyOnRead` | `(Z)V` | 29 / 35 |
| `API-76ec8c313e0e8dda` | `org.apache.geode.cache.client.ClientCache` | `setRegionAttributes` | `(Ljava/lang/String;Lorg/apache/geode/cache/RegionAttributes;)V` | 29 / 35 |

### client-cache-collaborators — 42 IDs

| API ID | Native receiver | Member | JVM descriptor | Implementation / consumer task |
| --- | --- | --- | --- | --- |
| `API-1ab4c45b2e9912cd` | `org.apache.geode.cache.Cache` | `createDiskStoreFactory` | `()Lorg/apache/geode/cache/DiskStoreFactory;` | 29 / 35 |
| `API-e78bcee4753a13a4` | `org.apache.geode.cache.Cache` | `findDiskStore` | `(Ljava/lang/String;)Lorg/apache/geode/cache/DiskStore;` | 29 / 35 |
| `API-36972244445f2896` | `org.apache.geode.cache.Cache` | `getDistributedSystem` | `()Lorg/apache/geode/distributed/DistributedSystem;` | 29 / 35 |
| `API-c7ae3199d3860769` | `org.apache.geode.cache.Cache` | `getJsonDocumentFactory` | `()Lorg/apache/geode/json/JsonDocumentFactory;` | 29 / 35 |
| `API-43cc01c744b8d4c9` | `org.apache.geode.cache.Cache` | `getJsonDocumentFactory` | `(Lorg/apache/geode/json/StorageFormat;)Lorg/apache/geode/json/JsonDocumentFactory;` | 29 / 35 |
| `API-a0320665e0570018` | `org.apache.geode.cache.Cache` | `getJsonFormatter` | `()Lorg/apache/geode/pdx/JSONFormatter;` | 29 / 35 |
| `API-78b1b1ddbe514306` | `org.apache.geode.cache.Cache` | `getLogger` | `()Lorg/apache/geode/LogWriter;` | 29 / 35 |
| `API-c6f444964145de89` | `org.apache.geode.cache.Cache` | `getLoggerI18n` | `()Lorg/apache/geode/i18n/LogWriterI18n;` | 29 / 35 |
| `API-612d37b3932f801d` | `org.apache.geode.cache.Cache` | `getQueryService` | `()Lorg/apache/geode/cache/query/QueryService;` | 29 / 35 |
| `API-cb4e8229934de77a` | `org.apache.geode.cache.Cache` | `getResourceManager` | `()Lorg/apache/geode/cache/control/ResourceManager;` | 29 / 35 |
| `API-9262207ec59172b6` | `org.apache.geode.cache.Cache` | `getSecurityLogger` | `()Lorg/apache/geode/LogWriter;` | 29 / 35 |
| `API-b7c36822007d09ba` | `org.apache.geode.cache.Cache` | `getSecurityLoggerI18n` | `()Lorg/apache/geode/i18n/LogWriterI18n;` | 29 / 35 |
| `API-cba3435d7a7f02fa` | `org.apache.geode.cache.Cache` | `getSnapshotService` | `()Lorg/apache/geode/cache/snapshot/CacheSnapshotService;` | 29 / 35 |
| `API-a688295344d86bb7` | `org.apache.geode.cache.Cache` | `readyForEvents` | `()V` | 29 / 35 |
| `API-87bea3d26b80b6ac` | `org.apache.geode.cache.GemFireCache` | `createDiskStoreFactory` | `()Lorg/apache/geode/cache/DiskStoreFactory;` | 29 / 35 |
| `API-0139542d343476b8` | `org.apache.geode.cache.GemFireCache` | `findDiskStore` | `(Ljava/lang/String;)Lorg/apache/geode/cache/DiskStore;` | 29 / 35 |
| `API-b17c6e5a9b6f9ec5` | `org.apache.geode.cache.GemFireCache` | `getDistributedSystem` | `()Lorg/apache/geode/distributed/DistributedSystem;` | 29 / 35 |
| `API-7d7a74556b0c9b62` | `org.apache.geode.cache.GemFireCache` | `getJsonDocumentFactory` | `()Lorg/apache/geode/json/JsonDocumentFactory;` | 29 / 35 |
| `API-12f0bb194a06a147` | `org.apache.geode.cache.GemFireCache` | `getJsonDocumentFactory` | `(Lorg/apache/geode/json/StorageFormat;)Lorg/apache/geode/json/JsonDocumentFactory;` | 29 / 35 |
| `API-7eb0acf98e90f88b` | `org.apache.geode.cache.GemFireCache` | `getJsonFormatter` | `()Lorg/apache/geode/pdx/JSONFormatter;` | 29 / 35 |
| `API-298683f0d20a1330` | `org.apache.geode.cache.GemFireCache` | `getLogger` | `()Lorg/apache/geode/LogWriter;` | 29 / 35 |
| `API-ea9fa36d61afc174` | `org.apache.geode.cache.GemFireCache` | `getQueryService` | `()Lorg/apache/geode/cache/query/QueryService;` | 29 / 35 |
| `API-27b1fc484654f13b` | `org.apache.geode.cache.GemFireCache` | `getResourceManager` | `()Lorg/apache/geode/cache/control/ResourceManager;` | 29 / 35 |
| `API-d7363e7f6708cd56` | `org.apache.geode.cache.GemFireCache` | `getSecurityLogger` | `()Lorg/apache/geode/LogWriter;` | 29 / 35 |
| `API-1241bfbb26c7fb0f` | `org.apache.geode.cache.RegionService` | `getJsonDocumentFactory` | `()Lorg/apache/geode/json/JsonDocumentFactory;` | 29 / 35 |
| `API-adf1c74f9d057552` | `org.apache.geode.cache.RegionService` | `getJsonDocumentFactory` | `(Lorg/apache/geode/json/StorageFormat;)Lorg/apache/geode/json/JsonDocumentFactory;` | 29 / 35 |
| `API-d1246c93d6ae488c` | `org.apache.geode.cache.RegionService` | `getJsonFormatter` | `()Lorg/apache/geode/pdx/JSONFormatter;` | 29 / 35 |
| `API-bde2f4f9df6155fb` | `org.apache.geode.cache.RegionService` | `getQueryService` | `()Lorg/apache/geode/cache/query/QueryService;` | 29 / 35 |
| `API-e70eaad3fc5d16f6` | `org.apache.geode.cache.client.ClientCache` | `createAuthenticatedView` | `(Ljava/util/Properties;)Lorg/apache/geode/cache/RegionService;` | 29 / 35 |
| `API-39d1ab982de147d6` | `org.apache.geode.cache.client.ClientCache` | `createAuthenticatedView` | `(Ljava/util/Properties;Ljava/lang/String;)Lorg/apache/geode/cache/RegionService;` | 29 / 35 |
| `API-3fa94cbec5d292ba` | `org.apache.geode.cache.client.ClientCache` | `createDiskStoreFactory` | `()Lorg/apache/geode/cache/DiskStoreFactory;` | 29 / 35 |
| `API-d1ef8c1a50b79d86` | `org.apache.geode.cache.client.ClientCache` | `findDiskStore` | `(Ljava/lang/String;)Lorg/apache/geode/cache/DiskStore;` | 29 / 35 |
| `API-20731306df51eb7b` | `org.apache.geode.cache.client.ClientCache` | `getDistributedSystem` | `()Lorg/apache/geode/distributed/DistributedSystem;` | 29 / 35 |
| `API-fe37f802788696b0` | `org.apache.geode.cache.client.ClientCache` | `getJsonDocumentFactory` | `()Lorg/apache/geode/json/JsonDocumentFactory;` | 29 / 35 |
| `API-227f1a26e0dc0c13` | `org.apache.geode.cache.client.ClientCache` | `getJsonDocumentFactory` | `(Lorg/apache/geode/json/StorageFormat;)Lorg/apache/geode/json/JsonDocumentFactory;` | 29 / 35 |
| `API-608c7343d829fee4` | `org.apache.geode.cache.client.ClientCache` | `getJsonFormatter` | `()Lorg/apache/geode/pdx/JSONFormatter;` | 29 / 35 |
| `API-74aa9297e1a687ec` | `org.apache.geode.cache.client.ClientCache` | `getLocalQueryService` | `()Lorg/apache/geode/cache/query/QueryService;` | 29 / 35 |
| `API-b2d4799898af9613` | `org.apache.geode.cache.client.ClientCache` | `getLogger` | `()Lorg/apache/geode/LogWriter;` | 29 / 35 |
| `API-d32697a4c6bbfc94` | `org.apache.geode.cache.client.ClientCache` | `getQueryService` | `()Lorg/apache/geode/cache/query/QueryService;` | 29 / 35 |
| `API-e0fc1df5e85aa10e` | `org.apache.geode.cache.client.ClientCache` | `getQueryService` | `(Ljava/lang/String;)Lorg/apache/geode/cache/query/QueryService;` | 29 / 35 |
| `API-fed815f2fd58f889` | `org.apache.geode.cache.client.ClientCache` | `getResourceManager` | `()Lorg/apache/geode/cache/control/ResourceManager;` | 29 / 35 |
| `API-45ee6a4276ce0245` | `org.apache.geode.cache.client.ClientCache` | `getSecurityLogger` | `()Lorg/apache/geode/LogWriter;` | 29 / 35 |

### client-cache-lookup — 5 IDs

| API ID | Native receiver | Member | JVM descriptor | Implementation / consumer task |
| --- | --- | --- | --- | --- |
| `API-566c9bd961a00f5f` | `org.apache.geode.cache.CacheFactory` | `<type>` | `(type)` | 29 / 35 |
| `API-31e6c3f8701ad5a6` | `org.apache.geode.cache.CacheFactory` | `getAnyInstance` | `()Lorg/apache/geode/cache/Cache;` | 29 / 35 |
| `API-b3ec22a1dcb5eea1` | `org.apache.geode.cache.CacheFactory` | `getInstance` | `(Lorg/apache/geode/distributed/DistributedSystem;)Lorg/apache/geode/cache/Cache;` | 29 / 35 |
| `API-b968d7d876f081a3` | `org.apache.geode.cache.CacheFactory` | `getInstanceCloseOk` | `(Lorg/apache/geode/distributed/DistributedSystem;)Lorg/apache/geode/cache/Cache;` | 29 / 35 |
| `API-461cb8222c329a30` | `org.apache.geode.cache.CacheFactory` | `getVersion` | `()Ljava/lang/String;` | 29 / 35 |

### client-attribute-assembly — 43 IDs

| API ID | Native receiver | Member | JVM descriptor | Implementation / consumer task |
| --- | --- | --- | --- | --- |
| `API-5e71046a221e8c42` | `org.apache.geode.cache.AttributesFactory` | `<init>` | `()V` | 49 / 30 |
| `API-20764e0708b30a33` | `org.apache.geode.cache.AttributesFactory` | `<init>` | `(Lorg/apache/geode/cache/RegionAttributes;)V` | 49 / 30 |
| `API-97059dbe3fdcca1b` | `org.apache.geode.cache.AttributesFactory` | `<type>` | `(type)` | 49 / 30 |
| `API-0f60d776c81b8064` | `org.apache.geode.cache.AttributesFactory` | `DEFAULT_CONCURRENCY_LEVEL` | `I` | 49 / 30 |
| `API-8ba15619f8b141f0` | `org.apache.geode.cache.AttributesFactory` | `DEFAULT_DISK_SYNCHRONOUS` | `Z` | 49 / 30 |
| `API-817801adb21b35e2` | `org.apache.geode.cache.AttributesFactory` | `addCacheListener` | `(Lorg/apache/geode/cache/CacheListener;)V` | 30 / 30 |
| `API-09b845fe03b0d463` | `org.apache.geode.cache.AttributesFactory` | `create` | `()Lorg/apache/geode/cache/RegionAttributes;` | 49 / 30 |
| `API-43202701da8ef526` | `org.apache.geode.cache.AttributesFactory` | `createRegionAttributes` | `()Lorg/apache/geode/cache/RegionAttributes;` | 49 / 30 |
| `API-01ca818bad1d48e9` | `org.apache.geode.cache.AttributesFactory` | `initCacheListeners` | `([Lorg/apache/geode/cache/CacheListener;)V` | 30 / 30 |
| `API-e1711d86ec6c4ebd` | `org.apache.geode.cache.AttributesFactory` | `regionAttributes` | `Lorg/apache/geode/internal/cache/InternalRegionAttributes;` | 49 / 30 |
| `API-32226c35d9a98fca` | `org.apache.geode.cache.AttributesFactory` | `setCacheListener` | `(Lorg/apache/geode/cache/CacheListener;)V` | 49 / 30 |
| `API-4ce84e9c5b87df6e` | `org.apache.geode.cache.AttributesFactory` | `setCacheLoader` | `(Lorg/apache/geode/cache/CacheLoader;)V` | 30 / 30 |
| `API-9923023fc353a1cc` | `org.apache.geode.cache.AttributesFactory` | `setCacheWriter` | `(Lorg/apache/geode/cache/CacheWriter;)V` | 30 / 30 |
| `API-fd18c2bc9c503d56` | `org.apache.geode.cache.AttributesFactory` | `setCloningEnabled` | `(Z)V` | 30 / 30 |
| `API-fb13a53797d3ada7` | `org.apache.geode.cache.AttributesFactory` | `setConcurrencyChecksEnabled` | `(Z)V` | 30 / 30 |
| `API-65f3c689a669268b` | `org.apache.geode.cache.AttributesFactory` | `setConcurrencyLevel` | `(I)V` | 30 / 30 |
| `API-6914951d786270a8` | `org.apache.geode.cache.AttributesFactory` | `setDiskDirs` | `([Ljava/io/File;)V` | 49 / 30 |
| `API-13ac8c2df3dc7ca9` | `org.apache.geode.cache.AttributesFactory` | `setDiskDirsAndSizes` | `([Ljava/io/File;[I)V` | 49 / 30 |
| `API-43a97c7410f648f1` | `org.apache.geode.cache.AttributesFactory` | `setDiskStoreName` | `(Ljava/lang/String;)V` | 30 / 30 |
| `API-132882879c0aa022` | `org.apache.geode.cache.AttributesFactory` | `setDiskSynchronous` | `(Z)V` | 30 / 30 |
| `API-cecdfdfa6bf9f1fd` | `org.apache.geode.cache.AttributesFactory` | `setDiskWriteAttributes` | `(Lorg/apache/geode/cache/DiskWriteAttributes;)V` | 49 / 30 |
| `API-805b4619cec29908` | `org.apache.geode.cache.AttributesFactory` | `setInitialCapacity` | `(I)V` | 30 / 30 |
| `API-e3c1485bf5b0b564` | `org.apache.geode.cache.AttributesFactory` | `setKeyConstraint` | `(Ljava/lang/Class;)V` | 30 / 30 |
| `API-0e0c4b89d65f90af` | `org.apache.geode.cache.AttributesFactory` | `setLoadFactor` | `(F)V` | 30 / 30 |
| `API-3bb48583b266c57e` | `org.apache.geode.cache.AttributesFactory` | `setPoolName` | `(Ljava/lang/String;)V` | 30 / 30 |
| `API-50ee94adc0a56419` | `org.apache.geode.cache.AttributesFactory` | `setStatisticsEnabled` | `(Z)V` | 30 / 30 |
| `API-28861fa2f0f0267a` | `org.apache.geode.cache.AttributesFactory` | `setValueConstraint` | `(Ljava/lang/Class;)V` | 30 / 30 |
| `API-377fbcb84dfe6eda` | `org.apache.geode.cache.AttributesFactory` | `validateAttributes` | `(Lorg/apache/geode/cache/RegionAttributes;)V` | 49 / 30 |
| `API-d88353ba1409e7e6` | `org.apache.geode.cache.AttributesMutator` | `<type>` | `(type)` | 19 / 30 |
| `API-dc424625344bf0d7` | `org.apache.geode.cache.AttributesMutator` | `addCacheListener` | `(Lorg/apache/geode/cache/CacheListener;)V` | 30 / 30 |
| `API-99b7f6f1fcf9e64b` | `org.apache.geode.cache.AttributesMutator` | `getCloningEnabled` | `()Z` | 46 / 30 |
| `API-4687594521621e8e` | `org.apache.geode.cache.AttributesMutator` | `getRegion` | `()Lorg/apache/geode/cache/Region;` | 30 / 30 |
| `API-c40fedc9a2732f32` | `org.apache.geode.cache.AttributesMutator` | `initCacheListeners` | `([Lorg/apache/geode/cache/CacheListener;)V` | 30 / 30 |
| `API-18d4975974bb31df` | `org.apache.geode.cache.AttributesMutator` | `removeCacheListener` | `(Lorg/apache/geode/cache/CacheListener;)V` | 30 / 30 |
| `API-bc08f094e22afbcb` | `org.apache.geode.cache.AttributesMutator` | `setCacheLoader` | `(Lorg/apache/geode/cache/CacheLoader;)Lorg/apache/geode/cache/CacheLoader;` | 30 / 30 |
| `API-e58e3b25adadc51c` | `org.apache.geode.cache.AttributesMutator` | `setCacheWriter` | `(Lorg/apache/geode/cache/CacheWriter;)Lorg/apache/geode/cache/CacheWriter;` | 30 / 30 |
| `API-006c8789087a3b0e` | `org.apache.geode.cache.AttributesMutator` | `setCloningEnabled` | `(Z)V` | 46 / 30 |
| `API-9e8bcb6d141a61b6` | `org.apache.geode.cache.Region` | `getAttributes` | `()Lorg/apache/geode/cache/RegionAttributes;` | 30 / 30 |
| `API-957b3bb7a310fd1d` | `org.apache.geode.cache.Region` | `getAttributesMutator` | `()Lorg/apache/geode/cache/AttributesMutator;` | 30 / 30 |
| `API-02e6c7ae689446f1` | `org.apache.geode.cache.RegionAttributes` | `getCacheListener` | `()Lorg/apache/geode/cache/CacheListener;` | 49 / 30 |
| `API-41ceea3a2ad8707a` | `org.apache.geode.cache.RegionAttributes` | `getDiskDirSizes` | `()[I` | 49 / 30 |
| `API-8fd5d1a07a9592b0` | `org.apache.geode.cache.RegionAttributes` | `getDiskDirs` | `()[Ljava/io/File;` | 49 / 30 |
| `API-bd66b96120a5f1cc` | `org.apache.geode.cache.RegionAttributes` | `getDiskWriteAttributes` | `()Lorg/apache/geode/cache/DiskWriteAttributes;` | 49 / 30 |

### client-legacy-local-policy — 16 IDs

| API ID | Native receiver | Member | JVM descriptor | Implementation / consumer task |
| --- | --- | --- | --- | --- |
| `API-ab976a11f44241e3` | `org.apache.geode.cache.AttributesFactory` | `setMirrorType` | `(Lorg/apache/geode/cache/MirrorType;)V` | 49 / 30 |
| `API-d985d99e83d45f34` | `org.apache.geode.cache.AttributesFactory` | `setPersistBackup` | `(Z)V` | 49 / 30 |
| `API-e18540bdb6e01d8b` | `org.apache.geode.cache.MirrorType` | `<type>` | `(type)` | 49 / 30 |
| `API-04953c62ae18c739` | `org.apache.geode.cache.MirrorType` | `KEYS` | `Lorg/apache/geode/cache/MirrorType;` | 49 / 30 |
| `API-bcd7857a1b4a4679` | `org.apache.geode.cache.MirrorType` | `KEYS_VALUES` | `Lorg/apache/geode/cache/MirrorType;` | 49 / 30 |
| `API-70c2f3dfb058d5d8` | `org.apache.geode.cache.MirrorType` | `NONE` | `Lorg/apache/geode/cache/MirrorType;` | 49 / 30 |
| `API-dc02da37ab61dbde` | `org.apache.geode.cache.MirrorType` | `fromOrdinal` | `(I)Lorg/apache/geode/cache/MirrorType;` | 49 / 30 |
| `API-198723d0c6739527` | `org.apache.geode.cache.MirrorType` | `getDataPolicy` | `()Lorg/apache/geode/cache/DataPolicy;` | 49 / 30 |
| `API-8dbd38b33c0f50d5` | `org.apache.geode.cache.MirrorType` | `isKeys` | `()Z` | 49 / 30 |
| `API-de4e2c02dfc0104e` | `org.apache.geode.cache.MirrorType` | `isKeysValues` | `()Z` | 49 / 30 |
| `API-a49410a933e466b5` | `org.apache.geode.cache.MirrorType` | `isMirrored` | `()Z` | 49 / 30 |
| `API-f1eb1a10bec82692` | `org.apache.geode.cache.MirrorType` | `isNone` | `()Z` | 49 / 30 |
| `API-79e343f51fd462b9` | `org.apache.geode.cache.MirrorType` | `ordinal` | `I` | 49 / 30 |
| `API-b07176856c807fd4` | `org.apache.geode.cache.MirrorType` | `toString` | `()Ljava/lang/String;` | 49 / 30 |
| `API-1f532fc788d4d78c` | `org.apache.geode.cache.RegionAttributes` | `getMirrorType` | `()Lorg/apache/geode/cache/MirrorType;` | 49 / 30 |
| `API-65da41a5a734065c` | `org.apache.geode.cache.RegionAttributes` | `getPersistBackup` | `()Z` | 49 / 30 |

### client-eviction-config-values — 112 IDs

| API ID | Native receiver | Member | JVM descriptor | Implementation / consumer task |
| --- | --- | --- | --- | --- |
| `API-5311a1f28c1328a2` | `org.apache.geode.cache.configuration.ClassNameType` | `<init>` | `()V` | 49 / 30 |
| `API-e093d1f0f531445c` | `org.apache.geode.cache.configuration.ClassNameType` | `<init>` | `(Ljava/lang/String;)V` | 49 / 30 |
| `API-4db4b998adf8c9ba` | `org.apache.geode.cache.configuration.ClassNameType` | `<type>` | `(type)` | 49 / 30 |
| `API-7b4aa9404c443e73` | `org.apache.geode.cache.configuration.ClassNameType` | `className` | `Ljava/lang/String;` | 49 / 30 |
| `API-e27c82cbe12bf996` | `org.apache.geode.cache.configuration.ClassNameType` | `equals` | `(Ljava/lang/Object;)Z` | 49 / 30 |
| `API-49f8f02e0fe6b1a4` | `org.apache.geode.cache.configuration.ClassNameType` | `getClassName` | `()Ljava/lang/String;` | 49 / 30 |
| `API-b83df70a15aef6c9` | `org.apache.geode.cache.configuration.ClassNameType` | `hashCode` | `()I` | 49 / 30 |
| `API-6f6067b11c055066` | `org.apache.geode.cache.configuration.ClassNameType` | `setClassName` | `(Ljava/lang/String;)V` | 49 / 30 |
| `API-eab888c1a9965021` | `org.apache.geode.cache.configuration.ClassNameType` | `toString` | `()Ljava/lang/String;` | 49 / 30 |
| `API-b5292fd8be641f0c` | `org.apache.geode.cache.configuration.DeclarableType` | `<init>` | `()V` | 49 / 30 |
| `API-7da8294fe86c2b05` | `org.apache.geode.cache.configuration.DeclarableType` | `<init>` | `(Ljava/lang/String;)V` | 49 / 30 |
| `API-6dba4c94c2c38984` | `org.apache.geode.cache.configuration.DeclarableType` | `<init>` | `(Ljava/lang/String;Ljava/lang/String;)V` | 49 / 30 |
| `API-edf853f40830f7a8` | `org.apache.geode.cache.configuration.DeclarableType` | `<init>` | `(Ljava/lang/String;Ljava/util/Properties;)V` | 49 / 30 |
| `API-85cc219d029e9daa` | `org.apache.geode.cache.configuration.DeclarableType` | `<type>` | `(type)` | 49 / 30 |
| `API-f145abba9be71897` | `org.apache.geode.cache.configuration.DeclarableType` | `className` | `Ljava/lang/String;` | 49 / 30 |
| `API-766bfa51eb332ade` | `org.apache.geode.cache.configuration.DeclarableType` | `equals` | `(Ljava/lang/Object;)Z` | 49 / 30 |
| `API-6f3567ed7c8151e0` | `org.apache.geode.cache.configuration.DeclarableType` | `getClassName` | `()Ljava/lang/String;` | 49 / 30 |
| `API-00be06ea4f51e589` | `org.apache.geode.cache.configuration.DeclarableType` | `getParameters` | `()Ljava/util/List;` | 49 / 30 |
| `API-5e2d1a62309b3896` | `org.apache.geode.cache.configuration.DeclarableType` | `hashCode` | `()I` | 49 / 30 |
| `API-47ca491f07676d97` | `org.apache.geode.cache.configuration.DeclarableType` | `parameters` | `Ljava/util/List;` | 49 / 30 |
| `API-62d5d55c8885dc06` | `org.apache.geode.cache.configuration.DeclarableType` | `setClassName` | `(Ljava/lang/String;)V` | 49 / 30 |
| `API-e497ae3483dfbb57` | `org.apache.geode.cache.configuration.DeclarableType` | `setParameters` | `(Ljava/util/Properties;)V` | 49 / 30 |
| `API-6c356bd32551cc03` | `org.apache.geode.cache.configuration.DeclarableType` | `toString` | `()Ljava/lang/String;` | 49 / 30 |
| `API-f05e937b9f4b2f9a` | `org.apache.geode.cache.configuration.EnumActionDestroyOverflow` | `<type>` | `(type)` | 49 / 30 |
| `API-fd9ec9d0fcc96a36` | `org.apache.geode.cache.configuration.EnumActionDestroyOverflow` | `LOCAL_DESTROY` | `Lorg/apache/geode/cache/configuration/EnumActionDestroyOverflow;` | 49 / 30 |
| `API-f884119e9a2c0ea2` | `org.apache.geode.cache.configuration.EnumActionDestroyOverflow` | `OVERFLOW_TO_DISK` | `Lorg/apache/geode/cache/configuration/EnumActionDestroyOverflow;` | 49 / 30 |
| `API-83780bfc38af438b` | `org.apache.geode.cache.configuration.EnumActionDestroyOverflow` | `fromValue` | `(Ljava/lang/String;)Lorg/apache/geode/cache/configuration/EnumActionDestroyOverflow;` | 49 / 30 |
| `API-15ac4b69d8fa5027` | `org.apache.geode.cache.configuration.EnumActionDestroyOverflow` | `value` | `()Ljava/lang/String;` | 49 / 30 |
| `API-8d471a4ef1e0729c` | `org.apache.geode.cache.configuration.EnumActionDestroyOverflow` | `valueOf` | `(Ljava/lang/String;)Lorg/apache/geode/cache/configuration/EnumActionDestroyOverflow;` | 49 / 30 |
| `API-fb5dda7ac0d12eef` | `org.apache.geode.cache.configuration.EnumActionDestroyOverflow` | `values` | `()[Lorg/apache/geode/cache/configuration/EnumActionDestroyOverflow;` | 49 / 30 |
| `API-1206b4ab2c306bab` | `org.apache.geode.cache.configuration.ObjectType` | `<init>` | `()V` | 49 / 30 |
| `API-1dca7ebe146f21e1` | `org.apache.geode.cache.configuration.ObjectType` | `<init>` | `(Ljava/lang/String;)V` | 49 / 30 |
| `API-fd77e122f282a676` | `org.apache.geode.cache.configuration.ObjectType` | `<init>` | `(Lorg/apache/geode/cache/configuration/DeclarableType;)V` | 49 / 30 |
| `API-ffedaefc10e60b0a` | `org.apache.geode.cache.configuration.ObjectType` | `<type>` | `(type)` | 49 / 30 |
| `API-1d2aebb608fa4415` | `org.apache.geode.cache.configuration.ObjectType` | `declarable` | `Lorg/apache/geode/cache/configuration/DeclarableType;` | 49 / 30 |
| `API-ed5a00981409f585` | `org.apache.geode.cache.configuration.ObjectType` | `equals` | `(Ljava/lang/Object;)Z` | 49 / 30 |
| `API-b0760486a99fc146` | `org.apache.geode.cache.configuration.ObjectType` | `getDeclarable` | `()Lorg/apache/geode/cache/configuration/DeclarableType;` | 49 / 30 |
| `API-b47e4ad899546dbf` | `org.apache.geode.cache.configuration.ObjectType` | `getString` | `()Ljava/lang/String;` | 49 / 30 |
| `API-8ca5e3f375801496` | `org.apache.geode.cache.configuration.ObjectType` | `hashCode` | `()I` | 49 / 30 |
| `API-cffb9072ab41d826` | `org.apache.geode.cache.configuration.ObjectType` | `setDeclarable` | `(Lorg/apache/geode/cache/configuration/DeclarableType;)V` | 49 / 30 |
| `API-0e652a34e8ee0c8e` | `org.apache.geode.cache.configuration.ObjectType` | `setString` | `(Ljava/lang/String;)V` | 49 / 30 |
| `API-07c0d5ce988950b7` | `org.apache.geode.cache.configuration.ObjectType` | `string` | `Ljava/lang/String;` | 49 / 30 |
| `API-96ca99cf3ad3db90` | `org.apache.geode.cache.configuration.ObjectType` | `toString` | `()Ljava/lang/String;` | 49 / 30 |
| `API-7a4563d0ee5e8a34` | `org.apache.geode.cache.configuration.ParameterType` | `<init>` | `()V` | 49 / 30 |
| `API-b478b127f0437567` | `org.apache.geode.cache.configuration.ParameterType` | `<init>` | `(Ljava/lang/String;)V` | 49 / 30 |
| `API-265aeeb4ecc30af0` | `org.apache.geode.cache.configuration.ParameterType` | `<init>` | `(Ljava/lang/String;Ljava/lang/String;)V` | 49 / 30 |
| `API-bf0d898339c86619` | `org.apache.geode.cache.configuration.ParameterType` | `<init>` | `(Ljava/lang/String;Lorg/apache/geode/cache/configuration/DeclarableType;)V` | 49 / 30 |
| `API-4d50e9dba1a8e2cb` | `org.apache.geode.cache.configuration.ParameterType` | `<type>` | `(type)` | 49 / 30 |
| `API-40dcf0dbc481b01a` | `org.apache.geode.cache.configuration.ParameterType` | `declarable` | `Lorg/apache/geode/cache/configuration/DeclarableType;` | 49 / 30 |
| `API-39f4888556214dab` | `org.apache.geode.cache.configuration.ParameterType` | `equals` | `(Ljava/lang/Object;)Z` | 49 / 30 |
| `API-779236eb541911b1` | `org.apache.geode.cache.configuration.ParameterType` | `getDeclarable` | `()Lorg/apache/geode/cache/configuration/DeclarableType;` | 49 / 30 |
| `API-3dfc698567bf8707` | `org.apache.geode.cache.configuration.ParameterType` | `getName` | `()Ljava/lang/String;` | 49 / 30 |
| `API-bc0f2f7700e28deb` | `org.apache.geode.cache.configuration.ParameterType` | `getString` | `()Ljava/lang/String;` | 49 / 30 |
| `API-a13edce576f6e32b` | `org.apache.geode.cache.configuration.ParameterType` | `hashCode` | `()I` | 49 / 30 |
| `API-0cd4776daeea6c01` | `org.apache.geode.cache.configuration.ParameterType` | `name` | `Ljava/lang/String;` | 49 / 30 |
| `API-bfccaca3cdc8ac38` | `org.apache.geode.cache.configuration.ParameterType` | `setDeclarable` | `(Lorg/apache/geode/cache/configuration/DeclarableType;)V` | 49 / 30 |
| `API-8b436d77dc55af05` | `org.apache.geode.cache.configuration.ParameterType` | `setName` | `(Ljava/lang/String;)V` | 49 / 30 |
| `API-d93d4d98f0b0628a` | `org.apache.geode.cache.configuration.ParameterType` | `setString` | `(Ljava/lang/String;)V` | 49 / 30 |
| `API-afb1f6b3c60327dc` | `org.apache.geode.cache.configuration.ParameterType` | `string` | `Ljava/lang/String;` | 49 / 30 |
| `API-30a7c457117ff720` | `org.apache.geode.cache.configuration.ParameterType` | `toString` | `()Ljava/lang/String;` | 49 / 30 |
| `API-312df536591a767a` | `org.apache.geode.cache.configuration.RegionAttributesType$EvictionAttributes` | `<init>` | `()V` | 49 / 30 |
| `API-26de5152bb75e86c` | `org.apache.geode.cache.configuration.RegionAttributesType$EvictionAttributes` | `<type>` | `(type)` | 49 / 30 |
| `API-fb6529a020d3492e` | `org.apache.geode.cache.configuration.RegionAttributesType$EvictionAttributes` | `generate` | `(Ljava/lang/String;Ljava/lang/Integer;Ljava/lang/Integer;Ljava/lang/String;)Lorg/apache/geode/cache/configuration/RegionAttributesType$EvictionAttributes;` | 49 / 30 |
| `API-7676b7c3c6e22d2c` | `org.apache.geode.cache.configuration.RegionAttributesType$EvictionAttributes` | `generate` | `(Ljava/lang/String;Ljava/lang/Integer;Ljava/lang/Integer;Lorg/apache/geode/management/configuration/ClassName;)Lorg/apache/geode/cache/configuration/RegionAttributesType$EvictionAttributes;` | 49 / 30 |
| `API-4a545ca7c0143035` | `org.apache.geode.cache.configuration.RegionAttributesType$EvictionAttributes` | `getLruEntryCount` | `()Lorg/apache/geode/cache/configuration/RegionAttributesType$EvictionAttributes$LruEntryCount;` | 49 / 30 |
| `API-06109ee3ed41a264` | `org.apache.geode.cache.configuration.RegionAttributesType$EvictionAttributes` | `getLruHeapPercentage` | `()Lorg/apache/geode/cache/configuration/RegionAttributesType$EvictionAttributes$LruHeapPercentage;` | 49 / 30 |
| `API-52e81337bf696eed` | `org.apache.geode.cache.configuration.RegionAttributesType$EvictionAttributes` | `getLruMemorySize` | `()Lorg/apache/geode/cache/configuration/RegionAttributesType$EvictionAttributes$LruMemorySize;` | 49 / 30 |
| `API-9f92141b4c6c0f04` | `org.apache.geode.cache.configuration.RegionAttributesType$EvictionAttributes` | `lruEntryCount` | `Lorg/apache/geode/cache/configuration/RegionAttributesType$EvictionAttributes$LruEntryCount;` | 49 / 30 |
| `API-f9453f961618e756` | `org.apache.geode.cache.configuration.RegionAttributesType$EvictionAttributes` | `lruHeapPercentage` | `Lorg/apache/geode/cache/configuration/RegionAttributesType$EvictionAttributes$LruHeapPercentage;` | 49 / 30 |
| `API-38f69c2234188ee7` | `org.apache.geode.cache.configuration.RegionAttributesType$EvictionAttributes` | `lruMemorySize` | `Lorg/apache/geode/cache/configuration/RegionAttributesType$EvictionAttributes$LruMemorySize;` | 49 / 30 |
| `API-e5b2045292b79ce4` | `org.apache.geode.cache.configuration.RegionAttributesType$EvictionAttributes` | `setLruEntryCount` | `(Lorg/apache/geode/cache/configuration/RegionAttributesType$EvictionAttributes$LruEntryCount;)V` | 49 / 30 |
| `API-ba2a35048b03f157` | `org.apache.geode.cache.configuration.RegionAttributesType$EvictionAttributes` | `setLruHeapPercentage` | `(Lorg/apache/geode/cache/configuration/RegionAttributesType$EvictionAttributes$LruHeapPercentage;)V` | 49 / 30 |
| `API-75ac055e32e16ff0` | `org.apache.geode.cache.configuration.RegionAttributesType$EvictionAttributes` | `setLruMemorySize` | `(Lorg/apache/geode/cache/configuration/RegionAttributesType$EvictionAttributes$LruMemorySize;)V` | 49 / 30 |
| `API-887c1c867548cbb9` | `org.apache.geode.cache.configuration.RegionAttributesType$EvictionAttributes$LruEntryCount` | `<init>` | `()V` | 49 / 30 |
| `API-6c084598396f1854` | `org.apache.geode.cache.configuration.RegionAttributesType$EvictionAttributes$LruEntryCount` | `<type>` | `(type)` | 49 / 30 |
| `API-b9203c044ed4f054` | `org.apache.geode.cache.configuration.RegionAttributesType$EvictionAttributes$LruEntryCount` | `action` | `Lorg/apache/geode/cache/configuration/EnumActionDestroyOverflow;` | 49 / 30 |
| `API-41234509c32f0380` | `org.apache.geode.cache.configuration.RegionAttributesType$EvictionAttributes$LruEntryCount` | `getAction` | `()Lorg/apache/geode/cache/configuration/EnumActionDestroyOverflow;` | 49 / 30 |
| `API-f9acb0e3a8415a91` | `org.apache.geode.cache.configuration.RegionAttributesType$EvictionAttributes$LruEntryCount` | `getMaximum` | `()Ljava/lang/String;` | 49 / 30 |
| `API-06a9b9a2b5cfcdad` | `org.apache.geode.cache.configuration.RegionAttributesType$EvictionAttributes$LruEntryCount` | `maximum` | `Ljava/lang/String;` | 49 / 30 |
| `API-b0c0819c6438f091` | `org.apache.geode.cache.configuration.RegionAttributesType$EvictionAttributes$LruEntryCount` | `setAction` | `(Lorg/apache/geode/cache/configuration/EnumActionDestroyOverflow;)V` | 49 / 30 |
| `API-f949b52929c7f3a9` | `org.apache.geode.cache.configuration.RegionAttributesType$EvictionAttributes$LruEntryCount` | `setMaximum` | `(Ljava/lang/String;)V` | 49 / 30 |
| `API-7b55b58caad1377c` | `org.apache.geode.cache.configuration.RegionAttributesType$EvictionAttributes$LruHeapPercentage` | `<init>` | `()V` | 49 / 30 |
| `API-fe7daf3a58d28c03` | `org.apache.geode.cache.configuration.RegionAttributesType$EvictionAttributes$LruHeapPercentage` | `<type>` | `(type)` | 49 / 30 |
| `API-cf2818db222f8be8` | `org.apache.geode.cache.configuration.RegionAttributesType$EvictionAttributes$LruHeapPercentage` | `action` | `Lorg/apache/geode/cache/configuration/EnumActionDestroyOverflow;` | 49 / 30 |
| `API-6d249ad2f4e8268d` | `org.apache.geode.cache.configuration.RegionAttributesType$EvictionAttributes$LruHeapPercentage` | `className` | `Ljava/lang/String;` | 49 / 30 |
| `API-51ec6613c525f42c` | `org.apache.geode.cache.configuration.RegionAttributesType$EvictionAttributes$LruHeapPercentage` | `equals` | `(Ljava/lang/Object;)Z` | 49 / 30 |
| `API-b86abc9dec79f38a` | `org.apache.geode.cache.configuration.RegionAttributesType$EvictionAttributes$LruHeapPercentage` | `getAction` | `()Lorg/apache/geode/cache/configuration/EnumActionDestroyOverflow;` | 49 / 30 |
| `API-dfa535b7acf6bc00` | `org.apache.geode.cache.configuration.RegionAttributesType$EvictionAttributes$LruHeapPercentage` | `getClassName` | `()Ljava/lang/String;` | 49 / 30 |
| `API-84dcf32ffb457c62` | `org.apache.geode.cache.configuration.RegionAttributesType$EvictionAttributes$LruHeapPercentage` | `getParameters` | `()Ljava/util/List;` | 49 / 30 |
| `API-4e6ad224cac52c31` | `org.apache.geode.cache.configuration.RegionAttributesType$EvictionAttributes$LruHeapPercentage` | `hashCode` | `()I` | 49 / 30 |
| `API-67ca2c5a7a71f63e` | `org.apache.geode.cache.configuration.RegionAttributesType$EvictionAttributes$LruHeapPercentage` | `parameters` | `Ljava/util/List;` | 49 / 30 |
| `API-436426e6021aba3a` | `org.apache.geode.cache.configuration.RegionAttributesType$EvictionAttributes$LruHeapPercentage` | `setAction` | `(Lorg/apache/geode/cache/configuration/EnumActionDestroyOverflow;)V` | 49 / 30 |
| `API-e8238cd2fce64069` | `org.apache.geode.cache.configuration.RegionAttributesType$EvictionAttributes$LruHeapPercentage` | `setClassName` | `(Ljava/lang/String;)V` | 49 / 30 |
| `API-7ddc565e890073ca` | `org.apache.geode.cache.configuration.RegionAttributesType$EvictionAttributes$LruHeapPercentage` | `setParameters` | `(Ljava/util/Properties;)V` | 49 / 30 |
| `API-87ebb7304e559b63` | `org.apache.geode.cache.configuration.RegionAttributesType$EvictionAttributes$LruHeapPercentage` | `toString` | `()Ljava/lang/String;` | 49 / 30 |
| `API-3155497ffd478ed4` | `org.apache.geode.cache.configuration.RegionAttributesType$EvictionAttributes$LruMemorySize` | `<init>` | `()V` | 49 / 30 |
| `API-966a8d04c41e6865` | `org.apache.geode.cache.configuration.RegionAttributesType$EvictionAttributes$LruMemorySize` | `<type>` | `(type)` | 49 / 30 |
| `API-f86ada1415d91a0d` | `org.apache.geode.cache.configuration.RegionAttributesType$EvictionAttributes$LruMemorySize` | `action` | `Lorg/apache/geode/cache/configuration/EnumActionDestroyOverflow;` | 49 / 30 |
| `API-9dbdb321d62ebe52` | `org.apache.geode.cache.configuration.RegionAttributesType$EvictionAttributes$LruMemorySize` | `className` | `Ljava/lang/String;` | 49 / 30 |
| `API-bdc0a72dccb5a839` | `org.apache.geode.cache.configuration.RegionAttributesType$EvictionAttributes$LruMemorySize` | `equals` | `(Ljava/lang/Object;)Z` | 49 / 30 |
| `API-07086f7d28028fd9` | `org.apache.geode.cache.configuration.RegionAttributesType$EvictionAttributes$LruMemorySize` | `getAction` | `()Lorg/apache/geode/cache/configuration/EnumActionDestroyOverflow;` | 49 / 30 |
| `API-d7785d22840fc150` | `org.apache.geode.cache.configuration.RegionAttributesType$EvictionAttributes$LruMemorySize` | `getClassName` | `()Ljava/lang/String;` | 49 / 30 |
| `API-508c26fc013af0c5` | `org.apache.geode.cache.configuration.RegionAttributesType$EvictionAttributes$LruMemorySize` | `getMaximum` | `()Ljava/lang/String;` | 49 / 30 |
| `API-e40310d6d54f750d` | `org.apache.geode.cache.configuration.RegionAttributesType$EvictionAttributes$LruMemorySize` | `getParameters` | `()Ljava/util/List;` | 49 / 30 |
| `API-e495ac346b789b5d` | `org.apache.geode.cache.configuration.RegionAttributesType$EvictionAttributes$LruMemorySize` | `hashCode` | `()I` | 49 / 30 |
| `API-738aeb32527f7c24` | `org.apache.geode.cache.configuration.RegionAttributesType$EvictionAttributes$LruMemorySize` | `maximum` | `Ljava/lang/String;` | 49 / 30 |
| `API-ed80e26a813e32f4` | `org.apache.geode.cache.configuration.RegionAttributesType$EvictionAttributes$LruMemorySize` | `parameters` | `Ljava/util/List;` | 49 / 30 |
| `API-7c79399e3c42de83` | `org.apache.geode.cache.configuration.RegionAttributesType$EvictionAttributes$LruMemorySize` | `setAction` | `(Lorg/apache/geode/cache/configuration/EnumActionDestroyOverflow;)V` | 49 / 30 |
| `API-a7815f5d1a1bd1c3` | `org.apache.geode.cache.configuration.RegionAttributesType$EvictionAttributes$LruMemorySize` | `setClassName` | `(Ljava/lang/String;)V` | 49 / 30 |
| `API-e9119ae083a6bac2` | `org.apache.geode.cache.configuration.RegionAttributesType$EvictionAttributes$LruMemorySize` | `setMaximum` | `(Ljava/lang/String;)V` | 49 / 30 |
| `API-f592457f1f9b7f79` | `org.apache.geode.cache.configuration.RegionAttributesType$EvictionAttributes$LruMemorySize` | `setParameters` | `(Ljava/util/Properties;)V` | 49 / 30 |
| `API-8003caef8a7d7585` | `org.apache.geode.cache.configuration.RegionAttributesType$EvictionAttributes$LruMemorySize` | `toString` | `()Ljava/lang/String;` | 49 / 30 |

### server-partition-config — 31 IDs

| API ID | Native receiver | Member | JVM descriptor | Implementation / consumer task |
| --- | --- | --- | --- | --- |
| `API-c1830686b64c92a9` | `org.apache.geode.cache.AttributesFactory` | `setBucketRegion` | `(Z)V` | 49 / 30 |
| `API-522ddf08031a71a2` | `org.apache.geode.cache.configuration.RegionAttributesType$PartitionAttributes` | `<init>` | `()V` | 49 / 30 |
| `API-2b13322e6587b573` | `org.apache.geode.cache.configuration.RegionAttributesType$PartitionAttributes` | `<type>` | `(type)` | 49 / 30 |
| `API-449e77e78d00cfc8` | `org.apache.geode.cache.configuration.RegionAttributesType$PartitionAttributes` | `colocatedWith` | `Ljava/lang/String;` | 49 / 30 |
| `API-566a489ed4ce9357` | `org.apache.geode.cache.configuration.RegionAttributesType$PartitionAttributes` | `combine` | `(Lorg/apache/geode/cache/configuration/RegionAttributesType$PartitionAttributes;Lorg/apache/geode/cache/configuration/RegionAttributesType$PartitionAttributes;)Lorg/apache/geode/cache/configuration/RegionAttributesType$PartitionAttributes;` | 49 / 30 |
| `API-422236339cdcffd3` | `org.apache.geode.cache.configuration.RegionAttributesType$PartitionAttributes` | `generate` | `(Ljava/lang/String;Ljava/util/List;Ljava/lang/Integer;Ljava/lang/Long;Ljava/lang/Integer;Ljava/lang/Long;Ljava/lang/Long;Ljava/lang/Integer;Ljava/lang/String;)Lorg/apache/geode/cache/configuration/RegionAttributesType$PartitionAttributes;` | 49 / 30 |
| `API-19c6e8866f783383` | `org.apache.geode.cache.configuration.RegionAttributesType$PartitionAttributes` | `getColocatedWith` | `()Ljava/lang/String;` | 49 / 30 |
| `API-b3dc7cd6fc65a165` | `org.apache.geode.cache.configuration.RegionAttributesType$PartitionAttributes` | `getLocalMaxMemory` | `()Ljava/lang/String;` | 49 / 30 |
| `API-7bf9e71c5c3b9ebb` | `org.apache.geode.cache.configuration.RegionAttributesType$PartitionAttributes` | `getPartitionListeners` | `()Ljava/util/List;` | 49 / 30 |
| `API-a0ef5a5860fe7034` | `org.apache.geode.cache.configuration.RegionAttributesType$PartitionAttributes` | `getPartitionResolver` | `()Lorg/apache/geode/cache/configuration/DeclarableType;` | 49 / 30 |
| `API-9abef2007f04eb03` | `org.apache.geode.cache.configuration.RegionAttributesType$PartitionAttributes` | `getRecoveryDelay` | `()Ljava/lang/String;` | 49 / 30 |
| `API-431909498ca15726` | `org.apache.geode.cache.configuration.RegionAttributesType$PartitionAttributes` | `getRedundantCopies` | `()Ljava/lang/String;` | 49 / 30 |
| `API-257836ffcdf78b93` | `org.apache.geode.cache.configuration.RegionAttributesType$PartitionAttributes` | `getStartupRecoveryDelay` | `()Ljava/lang/String;` | 49 / 30 |
| `API-dc3ff040598d979e` | `org.apache.geode.cache.configuration.RegionAttributesType$PartitionAttributes` | `getTotalMaxMemory` | `()Ljava/lang/String;` | 49 / 30 |
| `API-efa428a7c5ed2364` | `org.apache.geode.cache.configuration.RegionAttributesType$PartitionAttributes` | `getTotalNumBuckets` | `()Ljava/lang/String;` | 49 / 30 |
| `API-4b32424c40c851ba` | `org.apache.geode.cache.configuration.RegionAttributesType$PartitionAttributes` | `localMaxMemory` | `Ljava/lang/String;` | 49 / 30 |
| `API-fd64e16a7ad11953` | `org.apache.geode.cache.configuration.RegionAttributesType$PartitionAttributes` | `partitionListeners` | `Ljava/util/List;` | 49 / 30 |
| `API-aa67638a2cf3481d` | `org.apache.geode.cache.configuration.RegionAttributesType$PartitionAttributes` | `partitionResolver` | `Lorg/apache/geode/cache/configuration/DeclarableType;` | 49 / 30 |
| `API-3047d385df2652f5` | `org.apache.geode.cache.configuration.RegionAttributesType$PartitionAttributes` | `recoveryDelay` | `Ljava/lang/String;` | 49 / 30 |
| `API-367f97384c83213d` | `org.apache.geode.cache.configuration.RegionAttributesType$PartitionAttributes` | `redundantCopies` | `Ljava/lang/String;` | 49 / 30 |
| `API-4eafe14b127e03f3` | `org.apache.geode.cache.configuration.RegionAttributesType$PartitionAttributes` | `setColocatedWith` | `(Ljava/lang/String;)V` | 49 / 30 |
| `API-cfb2839eb89aca15` | `org.apache.geode.cache.configuration.RegionAttributesType$PartitionAttributes` | `setLocalMaxMemory` | `(Ljava/lang/String;)V` | 49 / 30 |
| `API-e1cf4e5b43400e03` | `org.apache.geode.cache.configuration.RegionAttributesType$PartitionAttributes` | `setPartitionResolver` | `(Lorg/apache/geode/cache/configuration/DeclarableType;)V` | 49 / 30 |
| `API-1258b30c14c41514` | `org.apache.geode.cache.configuration.RegionAttributesType$PartitionAttributes` | `setRecoveryDelay` | `(Ljava/lang/String;)V` | 49 / 30 |
| `API-9dee2cc437b383a2` | `org.apache.geode.cache.configuration.RegionAttributesType$PartitionAttributes` | `setRedundantCopies` | `(Ljava/lang/String;)V` | 49 / 30 |
| `API-df6bea61614c479c` | `org.apache.geode.cache.configuration.RegionAttributesType$PartitionAttributes` | `setStartupRecoveryDelay` | `(Ljava/lang/String;)V` | 49 / 30 |
| `API-8412f719f514786b` | `org.apache.geode.cache.configuration.RegionAttributesType$PartitionAttributes` | `setTotalMaxMemory` | `(Ljava/lang/String;)V` | 49 / 30 |
| `API-a317af9736377f1c` | `org.apache.geode.cache.configuration.RegionAttributesType$PartitionAttributes` | `setTotalNumBuckets` | `(Ljava/lang/String;)V` | 49 / 30 |
| `API-3b2fd3ae15d8b1a7` | `org.apache.geode.cache.configuration.RegionAttributesType$PartitionAttributes` | `startupRecoveryDelay` | `Ljava/lang/String;` | 49 / 30 |
| `API-8ec133eb303843b7` | `org.apache.geode.cache.configuration.RegionAttributesType$PartitionAttributes` | `totalMaxMemory` | `Ljava/lang/String;` | 49 / 30 |
| `API-6189cea5c94e3bf0` | `org.apache.geode.cache.configuration.RegionAttributesType$PartitionAttributes` | `totalNumBuckets` | `Ljava/lang/String;` | 49 / 30 |

### server-conflation-aliases — 4 IDs

| API ID | Native receiver | Member | JVM descriptor | Implementation / consumer task |
| --- | --- | --- | --- | --- |
| `API-2a7ed4e76557346e` | `org.apache.geode.cache.AttributesFactory` | `setEnableBridgeConflation` | `(Z)V` | 49 / 30 |
| `API-7a45d4621a598d97` | `org.apache.geode.cache.AttributesFactory` | `setEnableConflation` | `(Z)V` | 49 / 30 |
| `API-ddfa1b730d6c2e8f` | `org.apache.geode.cache.RegionAttributes` | `getEnableBridgeConflation` | `()Z` | 49 / 30 |
| `API-dcbb2942fc831185` | `org.apache.geode.cache.RegionAttributes` | `getEnableConflation` | `()Z` | 49 / 30 |

### server-cache-runtime — 15 IDs

| API ID | Native receiver | Member | JVM descriptor | Implementation / consumer task |
| --- | --- | --- | --- | --- |
| `API-37c5b705187d15fc` | `org.apache.geode.cache.Cache` | `getAdminMembers` | `()Ljava/util/Set;` | 29 / 35 |
| `API-569946aa63a690e4` | `org.apache.geode.cache.Cache` | `getLockLease` | `()I` | 29 / 35 |
| `API-4ee96d616395d993` | `org.apache.geode.cache.Cache` | `getLockTimeout` | `()I` | 29 / 35 |
| `API-fa9959b4c759b92d` | `org.apache.geode.cache.Cache` | `getMembers` | `()Ljava/util/Set;` | 29 / 35 |
| `API-8a5e6c9c70f69c62` | `org.apache.geode.cache.Cache` | `getMembers` | `(Lorg/apache/geode/cache/Region;)Ljava/util/Set;` | 29 / 35 |
| `API-5631ceb1490a7bb7` | `org.apache.geode.cache.Cache` | `getMessageSyncInterval` | `()I` | 29 / 35 |
| `API-76e3dd482909d27b` | `org.apache.geode.cache.Cache` | `getReconnectedCache` | `()Lorg/apache/geode/cache/Cache;` | 29 / 35 |
| `API-8254fe38963201cc` | `org.apache.geode.cache.Cache` | `getSearchTimeout` | `()I` | 29 / 35 |
| `API-5583f059ee9e658a` | `org.apache.geode.cache.Cache` | `isReconnecting` | `()Z` | 29 / 35 |
| `API-eedccd0115833eb4` | `org.apache.geode.cache.Cache` | `setLockLease` | `(I)V` | 29 / 35 |
| `API-8b62eb4fd5fe837a` | `org.apache.geode.cache.Cache` | `setLockTimeout` | `(I)V` | 29 / 35 |
| `API-fc977ab3d8601099` | `org.apache.geode.cache.Cache` | `setMessageSyncInterval` | `(I)V` | 29 / 35 |
| `API-c013cc33e061c2eb` | `org.apache.geode.cache.Cache` | `setSearchTimeout` | `(I)V` | 29 / 35 |
| `API-c6dec72ccfdb8767` | `org.apache.geode.cache.Cache` | `stopReconnecting` | `()V` | 29 / 35 |
| `API-efbc6bc38b1ad6d5` | `org.apache.geode.cache.Cache` | `waitUntilReconnected` | `(JLjava/util/concurrent/TimeUnit;)Z` | 29 / 35 |

### server-wan-factory — 2 IDs

| API ID | Native receiver | Member | JVM descriptor | Implementation / consumer task |
| --- | --- | --- | --- | --- |
| `API-f2d28ef8863f25e4` | `org.apache.geode.cache.GemFireCache` | `createGatewaySenderFactory` | `()Lorg/apache/geode/cache/wan/GatewaySenderFactory;` | 29 / 35 |
| `API-35d7d8527d90b2d7` | `org.apache.geode.cache.client.ClientCache` | `createGatewaySenderFactory` | `()Lorg/apache/geode/cache/wan/GatewaySenderFactory;` | 29 / 35 |

### server-cache-builder — 4 IDs

| API ID | Native receiver | Member | JVM descriptor | Implementation / consumer task |
| --- | --- | --- | --- | --- |
| `API-e1e288b9f22ed174` | `org.apache.geode.cache.CacheFactory` | `addMeterSubregistry` | `(Lio/micrometer/core/instrument/MeterRegistry;)Lorg/apache/geode/cache/CacheFactory;` | 29 / 35 |
| `API-ace5508eed88bac2` | `org.apache.geode.cache.CacheFactory` | `set` | `(Ljava/lang/String;Ljava/lang/String;)Lorg/apache/geode/cache/CacheFactory;` | 29 / 35 |
| `API-93f33b6db442c6b8` | `org.apache.geode.cache.CacheFactory` | `setPostProcessor` | `(Lorg/apache/geode/security/PostProcessor;)Lorg/apache/geode/cache/CacheFactory;` | 29 / 35 |
| `API-91b63b83a73f61fd` | `org.apache.geode.cache.CacheFactory` | `setSecurityManager` | `(Lorg/apache/geode/security/SecurityManager;)Lorg/apache/geode/cache/CacheFactory;` | 29 / 35 |

### server-declarable-removal-sentinel — 1 IDs

| API ID | Native receiver | Member | JVM descriptor | Implementation / consumer task |
| --- | --- | --- | --- | --- |
| `API-472f37eeb0e35318` | `org.apache.geode.cache.configuration.DeclarableType` | `EMPTY` | `Lorg/apache/geode/cache/configuration/DeclarableType;` | 49 / 30 |

## Validation receipt — 2026-10-28

Deterministic coverage generation/check and all 10 scope-policy tests passed. Independent javap matched 372 declarations across 21 native types; inventory, ownership, source mapping and negative controls passed (3,851 API rows, 133 production imports, 549 source/resource references and 509 Spring exposures). All 2,723 fixed assignments remain unique and receiver/member/descriptor-stable: 730 classified and 1,993 unresolved. Exactly task 67's 332 IDs changed scope; outside scope fields only 23 unfinished implementation owners and 330 remaining-work descriptions changed. Native declarations, implementation statuses and SPI/native/mock/test mappings are unchanged. Every required unfinished row and consumer has an active owner. All 57 exclusions are missing/unmapped, and no linked current source reference needs retirement. Source dispositions are 323 required-client / three excluded-server / 223 unresolved; existing-SPI dispositions are 138 required-client / 11 unresolved. The 77-task dependency graph remains acyclic. No new runtime probe, Java/build change or Gradle execution was required.
