# Client partition, membership and subscription scope — 2026-10-28

**Task 75: 158 assigned / 158 classified / zero unresolved: 46 required-client and 112 excluded-server.** All implementation statuses remain missing.

Evidence is pinned to GemFire **10.4.0-build.3056.df98c835**, source commit `df98c8357afe85853d9a4776255d4f0e7cf064cb` in `/Users/uk016635/projects/gemfire`, read with git show. Native paths below are relative to `gemfire-core/src/main/java/org/apache/geode/`, with a `.java` suffix. Exact IDs include constructors, constants, overloads and inherited members. Source settles these scope decisions; no new runtime probe is required.

## client-subscription-policy-values

**23 IDs — required-client.** Sources: `cache/{InterestPolicy,SubscriptionAttributes,AttributesFactory,RegionAttributes}`, `cache/client/internal/ClientRegionFactoryImpl.initAttributeFactoryDefaults`, and `internal/cache/{InternalAttributesFactory,AbstractRegion}`. InterestPolicy has ALL (ordinal 0), CACHE_CONTENT (1), and DEFAULT aliasing CACHE_CONTENT; predicates use identity, toString returns the name and fromOrdinal directly indexes its canonical array. Invalid ordinals fail rather than silently choosing a default. SubscriptionAttributes() uses DEFAULT, but ClientRegionFactoryImpl explicitly installs ALL while initializing client-region defaults, along with Scope.LOCAL. Preserve this distinction, including creation from named attributes. ClientRegionFactory has no public subscription-policy setter: do not invent native setter support or bypass its default override.

SubscriptionAttributes stores its constructor argument, including null; equals permits null, whereas hashCode, toString and toData dereference it. Its hash is 17*37 plus the policy hash. toData/fromData encode/decode one ordinal byte; externalization delegates to these methods. Keep native serialization/canonical identities inside the adapter and define typed equivalent conversion, coordinated with 55/58. Do not promise every invalid state is rejected at construction. InternalAttributesFactory.setSubscriptionAttributes forwards the value and marks it specified; validation/readback belongs to the actual creation path.

AbstractRegion copies and exposes subscription attributes, and uses the policy in isCacheContentProxy/isAllEvents. Retain the client-visible configuration/value contract without importing distributed peer event semantics. ALL is not a registerInterest call, an InterestResultPolicy for initial results, pool subscription enablement, or a guarantee of all server events. Implementation 48 and adoption 30 own supported creation paths, defaults, readback, conversion and failure compatibility. Ticket 45 owns registration/delivery and 52 durable lifecycle. A mock may model values but must not claim delivery from configuration alone.

## client-request-routing-contracts

**13 IDs — required-client.** Sources: `cache/{PartitionResolver,EntryOperation,CacheCallback,Declarable}`, `cache/client/internal/{ClientMetadataService,ClientPartitionAdvisor}`, and `internal/cache/EntryOperationImpl`. ClientMetadataService.getResolver selects the advisor resolver first, then a resolver implemented by the key, then by the callback argument. resolveBucketId constructs EntryOperationImpl, calls getRoutingObject, rejects a null routing result and hashes that result against the metadata bucket count. Without a resolver it uses the key; absent metadata falls back through the client routing path. Function routing also constructs an entry operation with FUNCTION_EXECUTION and null new value/callback. This is direct client use, not evidence for exposing hosted partition regions.

EntryOperation carries region, operation, key, callback argument/availability and new value. EntryOperationImpl unwraps WrappedCallbackArgument, handles Token.NOT_AVAILABLE via AbstractRegion.handleNotAvailable, and reports availability from the raw token. Operation/new value may be null; their deprecated status does not erase these assigned contracts. Use native-free region/operation/context and payload adaptation, coordinated with region callbacks (66) and serialization (58), and preserve failure semantics.

ClientPartitionAdvisor instantiates the resolver class advertised in server metadata with its no-argument constructor. It does not invoke Declarable.init/initialize there. PartitionResolver inherits CacheCallback.close and Declarable.init/initialize, so those exact IDs remain required typed callback lifecycle contracts. The XML lifecycle is separately evidenced in task 66 (ClientCacheCreation/CacheCreation.initializeDeclarablesMap invokes initialize then init); programmatic registration and metadata reflection must not be described as automatically invoking these hooks. Default hooks are no-ops. Adapt the initializer cache to the client owner without exposing native Cache or a peer cache surface.

Task 48 must define the native-free callback/key representation and private native bridge for resolver selection and metadata class construction; task 30 adopts it in supported client configuration paths. Test selection precedence, routing result and errors, context conversion, inherited hooks and metadata instantiation in versioned driver compatibility tests. Coordinate function routing with 25. Native class names, bucket/server metadata and peer PartitionAttributes stay driver-private. Classification establishes the required behavior, not the final bridge design or a completed implementation.

## client-string-prefix-routing

**10 IDs — required-client.** Source: `cache/util/StringPrefixPartitionResolver`, read with ClientPartitionAdvisor's metadata instantiation and ClientMetadataService routing above. The resolver uses a String key and returns the substring before the first fixed `|` delimiter (DEFAULT_DELIMITER); an empty prefix is valid. Missing delimiter throws IllegalArgumentException; non-String keys fail the cast and null keys fail on dereference. getName uses the runtime class name; equals requires a compatible resolver and matching name/delimiter, while hashCode uses the name. close and inherited init/initialize are no-ops in this implementation.

Preserve the constructor, constant, routing behavior and applicable equality/lifecycle obligations through typed configuration/value conversion in 48/30. Do not copy a native class-name hash into a native-free value and claim equivalence without conversion tests. Server-advertised native class identity belongs to the private driver bridge. This required routing implementation does not make a PartitionAttributes factory or server bucket hosting part of the SPI.

## excluded-partition-hosting-configuration

**37 IDs — excluded-server.** Sources: `cache/{PartitionAttributes,PartitionAttributesFactory}` and `internal/cache/InternalAttributesFactory.{setPartitionAttributes,validateAttributes}`. These 37 declarations configure hosted partition regions: memory, redundant copies, total buckets, colocation, recovery delays, listeners and resolver attachment. A zero-local-memory accessor is still a peer partition region, not a client pool region. Validation rejects a partition region with a pool name and rejects explicitly setting scope; ClientRegionFactoryImpl explicitly sets LOCAL. InternalClientRegionAttributesFactory.create delegates into this shared validation (its own extra validation concerns serverRegionName).

The resolver getter/setter on these hosting receivers is excluded; the resolver contract itself is retained through ClientMetadataService and ClientPartitionAdvisor. Do not restore PartitionAttributes or its factory merely to attach a client resolver. All assigned rows are missing/unmapped, with no linked current production/SPI reference requiring retirement.

## excluded-partition-hosting-listeners

**13 IDs — excluded-server.** Sources: `cache/partition/{PartitionListener,PartitionListenerAdapter}`. These 13 declarations concern bucket creation/removal, primary/secondary transitions and partition-region creation on hosting members. They are not client entry/cache callbacks or subscription delivery listeners. The adapter's inherited afterSecondary is included in the exact ledger and excluded with the same hosting contract. No assigned declaration has a public SPI implementation or linked current production reference to retire.

## excluded-partition-rebalance-metadata

**23 IDs — excluded-server.** Sources: `cache/partition/{PartitionMemberInfo,PartitionRebalanceInfo}`. These 23 declarations expose hosting-member memory/size/bucket/primary counts and region rebalance before/after membership, bucket creation/removal/transfer and primary transfer timing/count/byte statistics. The native rebalance operations were excluded in task 74. ClientMetadataService's internal bucket/server routing data is a different mechanism; required member identity remains with 23. No public mapping or linked production/SPI reference exists for these assigned exclusions.

## excluded-peer-role-reliability

**39 IDs — excluded-server.** Sources: `cache/{MembershipAttributes,LossAction,ResumptionAction}` and `internal/cache/InternalAttributesFactory.validateAttributes`. These 39 declarations define required distributed roles, access restrictions on role loss, and reconnect/reinitialize/resumption actions. Validation rejects required roles with LOCAL scope, which the client region factory installs. Empty required-role defaults do not create a client reliability API. Constructors, constants, predicates, ordinal/value methods and serialization/externalization all belong to this excluded peer contract. Do not conflate these actions with client connection recovery, durable subscription readiness or pool failover (20/52). No public implementation mapping or linked current production/SPI reference needs retirement.

## Ownership and reconciliation

All 46 retained IDs remain assigned to active implementation task 48 and consumer task 30. The earlier blanket exclusion of partition resolvers and entry-operation contexts in tasks 19/48 is corrected by the client routing evidence. All 112 exclusions are missing/unmapped and have no linked current production or existing SPI reference to retire. No Java/build changes are necessary for this audit.

All 3,851 frozen API IDs and 2,723 assignment rows, native declarations, implementation statuses, ownership and SPI/native/mock/test mappings remain unchanged. Tasks 59/64 remain open. Task 76 (390 shared-exception IDs) is the last classification family; task 77 retains fixture cleanup.

## Exact ID ledger

### client-subscription-policy-values — 23 IDs

| API ID | Native receiver | Member | JVM descriptor | Owner | Consumer |
| --- | --- | --- | --- | --- | --- |
| `API-23fc3afcdad3cf50` | `org.apache.geode.cache.AttributesFactory` | `setSubscriptionAttributes` | `(Lorg/apache/geode/cache/SubscriptionAttributes;)V` | 48 | 30 |
| `API-3f66d25a135c3cfe` | `org.apache.geode.cache.InterestPolicy` | `<type>` | `(type)` | 48 | 30 |
| `API-7b7ed84bb890c000` | `org.apache.geode.cache.InterestPolicy` | `ALL` | `Lorg/apache/geode/cache/InterestPolicy;` | 48 | 30 |
| `API-08b6f53b08b70e1f` | `org.apache.geode.cache.InterestPolicy` | `CACHE_CONTENT` | `Lorg/apache/geode/cache/InterestPolicy;` | 48 | 30 |
| `API-ac664179440a851e` | `org.apache.geode.cache.InterestPolicy` | `DEFAULT` | `Lorg/apache/geode/cache/InterestPolicy;` | 48 | 30 |
| `API-f349b2ff641cb619` | `org.apache.geode.cache.InterestPolicy` | `fromOrdinal` | `(B)Lorg/apache/geode/cache/InterestPolicy;` | 48 | 30 |
| `API-9bb5574514b3a6f5` | `org.apache.geode.cache.InterestPolicy` | `isAll` | `()Z` | 48 | 30 |
| `API-fced3ec813b14b63` | `org.apache.geode.cache.InterestPolicy` | `isCacheContent` | `()Z` | 48 | 30 |
| `API-60dc27c40a4d942d` | `org.apache.geode.cache.InterestPolicy` | `isDefault` | `()Z` | 48 | 30 |
| `API-0a09644ace10dcc2` | `org.apache.geode.cache.InterestPolicy` | `ordinal` | `B` | 48 | 30 |
| `API-6e166bf4619916e8` | `org.apache.geode.cache.InterestPolicy` | `toString` | `()Ljava/lang/String;` | 48 | 30 |
| `API-541e155f171bd064` | `org.apache.geode.cache.RegionAttributes` | `getSubscriptionAttributes` | `()Lorg/apache/geode/cache/SubscriptionAttributes;` | 48 | 30 |
| `API-4b86a7caa1d3f559` | `org.apache.geode.cache.SubscriptionAttributes` | `<init>` | `()V` | 48 | 30 |
| `API-e92b139a6d2a91b5` | `org.apache.geode.cache.SubscriptionAttributes` | `<init>` | `(Lorg/apache/geode/cache/InterestPolicy;)V` | 48 | 30 |
| `API-ea609f8c897fb520` | `org.apache.geode.cache.SubscriptionAttributes` | `<type>` | `(type)` | 48 | 30 |
| `API-2ce718bdf24e293a` | `org.apache.geode.cache.SubscriptionAttributes` | `equals` | `(Ljava/lang/Object;)Z` | 48 | 30 |
| `API-90a6cc7dce2f7b54` | `org.apache.geode.cache.SubscriptionAttributes` | `fromData` | `(Ljava/io/DataInput;)V` | 48 | 30 |
| `API-580ed89b0b4cd746` | `org.apache.geode.cache.SubscriptionAttributes` | `getInterestPolicy` | `()Lorg/apache/geode/cache/InterestPolicy;` | 48 | 30 |
| `API-0ef3f00a4edf381b` | `org.apache.geode.cache.SubscriptionAttributes` | `hashCode` | `()I` | 48 | 30 |
| `API-c50c04a803182b3c` | `org.apache.geode.cache.SubscriptionAttributes` | `readExternal` | `(Ljava/io/ObjectInput;)V` | 48 | 30 |
| `API-a878c0401c3f088f` | `org.apache.geode.cache.SubscriptionAttributes` | `toData` | `(Ljava/io/DataOutput;)V` | 48 | 30 |
| `API-6a23f42c6b623771` | `org.apache.geode.cache.SubscriptionAttributes` | `toString` | `()Ljava/lang/String;` | 48 | 30 |
| `API-1f291aba8aa2e0bc` | `org.apache.geode.cache.SubscriptionAttributes` | `writeExternal` | `(Ljava/io/ObjectOutput;)V` | 48 | 30 |

### client-request-routing-contracts — 13 IDs

| API ID | Native receiver | Member | JVM descriptor | Owner | Consumer |
| --- | --- | --- | --- | --- | --- |
| `API-b92ed7617780248b` | `org.apache.geode.cache.EntryOperation` | `<type>` | `(type)` | 48 | 30 |
| `API-15a8a8393d2ff84f` | `org.apache.geode.cache.EntryOperation` | `getCallbackArgument` | `()Ljava/lang/Object;` | 48 | 30 |
| `API-0e9b653b79f22fbd` | `org.apache.geode.cache.EntryOperation` | `getKey` | `()Ljava/lang/Object;` | 48 | 30 |
| `API-74020eac612787a3` | `org.apache.geode.cache.EntryOperation` | `getNewValue` | `()Ljava/lang/Object;` | 48 | 30 |
| `API-2e82dcb94c75c41d` | `org.apache.geode.cache.EntryOperation` | `getOperation` | `()Lorg/apache/geode/cache/Operation;` | 48 | 30 |
| `API-31d00b2defa042a8` | `org.apache.geode.cache.EntryOperation` | `getRegion` | `()Lorg/apache/geode/cache/Region;` | 48 | 30 |
| `API-19c598d0810c36de` | `org.apache.geode.cache.EntryOperation` | `isCallbackArgumentAvailable` | `()Z` | 48 | 30 |
| `API-c02a0c45ddec4d9e` | `org.apache.geode.cache.PartitionResolver` | `<type>` | `(type)` | 48 | 30 |
| `API-d9573047a76ed142` | `org.apache.geode.cache.PartitionResolver` | `close` | `()V` | 48 | 30 |
| `API-7c130d0a6bd8dbd3` | `org.apache.geode.cache.PartitionResolver` | `getName` | `()Ljava/lang/String;` | 48 | 30 |
| `API-2d7d1f22382d4952` | `org.apache.geode.cache.PartitionResolver` | `getRoutingObject` | `(Lorg/apache/geode/cache/EntryOperation;)Ljava/lang/Object;` | 48 | 30 |
| `API-783d66fc51601017` | `org.apache.geode.cache.PartitionResolver` | `init` | `(Ljava/util/Properties;)V` | 48 | 30 |
| `API-32edcdf90a95bd03` | `org.apache.geode.cache.PartitionResolver` | `initialize` | `(Lorg/apache/geode/cache/Cache;Ljava/util/Properties;)V` | 48 | 30 |

### client-string-prefix-routing — 10 IDs

| API ID | Native receiver | Member | JVM descriptor | Owner | Consumer |
| --- | --- | --- | --- | --- | --- |
| `API-6fa4ff50ed2562c1` | `org.apache.geode.cache.util.StringPrefixPartitionResolver` | `<init>` | `()V` | 48 | 30 |
| `API-19614a323226a7eb` | `org.apache.geode.cache.util.StringPrefixPartitionResolver` | `<type>` | `(type)` | 48 | 30 |
| `API-153ff638ad367c97` | `org.apache.geode.cache.util.StringPrefixPartitionResolver` | `DEFAULT_DELIMITER` | `Ljava/lang/String;` | 48 | 30 |
| `API-994c31869d10a1e2` | `org.apache.geode.cache.util.StringPrefixPartitionResolver` | `close` | `()V` | 48 | 30 |
| `API-ec281331044a5c94` | `org.apache.geode.cache.util.StringPrefixPartitionResolver` | `equals` | `(Ljava/lang/Object;)Z` | 48 | 30 |
| `API-977e50b3a41c0edb` | `org.apache.geode.cache.util.StringPrefixPartitionResolver` | `getName` | `()Ljava/lang/String;` | 48 | 30 |
| `API-ee7f5248d34b8d73` | `org.apache.geode.cache.util.StringPrefixPartitionResolver` | `getRoutingObject` | `(Lorg/apache/geode/cache/EntryOperation;)Ljava/lang/Object;` | 48 | 30 |
| `API-4003068e9c0f6608` | `org.apache.geode.cache.util.StringPrefixPartitionResolver` | `hashCode` | `()I` | 48 | 30 |
| `API-71a86f5ab9853db5` | `org.apache.geode.cache.util.StringPrefixPartitionResolver` | `init` | `(Ljava/util/Properties;)V` | 48 | 30 |
| `API-dd7a3e2cad5e5d4e` | `org.apache.geode.cache.util.StringPrefixPartitionResolver` | `initialize` | `(Lorg/apache/geode/cache/Cache;Ljava/util/Properties;)V` | 48 | 30 |

### excluded-partition-hosting-configuration — 37 IDs

| API ID | Native receiver | Member | JVM descriptor | Owner | Consumer |
| --- | --- | --- | --- | --- | --- |
| `API-1e092e8c6f613838` | `org.apache.geode.cache.PartitionAttributes` | `<type>` | `(type)` | 48 | 30 |
| `API-6a858d0c19054a93` | `org.apache.geode.cache.PartitionAttributes` | `convertToConfigPartitionAttributes` | `()Lorg/apache/geode/cache/configuration/RegionAttributesType$PartitionAttributes;` | 48 | 30 |
| `API-03c89bb42915431b` | `org.apache.geode.cache.PartitionAttributes` | `getColocatedWith` | `()Ljava/lang/String;` | 48 | 30 |
| `API-08eb7ca21216431f` | `org.apache.geode.cache.PartitionAttributes` | `getGlobalProperties` | `()Ljava/util/Properties;` | 48 | 30 |
| `API-2b77eb5670e83023` | `org.apache.geode.cache.PartitionAttributes` | `getLocalMaxMemory` | `()I` | 48 | 30 |
| `API-29521c884968875e` | `org.apache.geode.cache.PartitionAttributes` | `getLocalProperties` | `()Ljava/util/Properties;` | 48 | 30 |
| `API-279d07195ba271f9` | `org.apache.geode.cache.PartitionAttributes` | `getPartitionListeners` | `()[Lorg/apache/geode/cache/partition/PartitionListener;` | 48 | 30 |
| `API-e13fddb9a0a6f16b` | `org.apache.geode.cache.PartitionAttributes` | `getPartitionResolver` | `()Lorg/apache/geode/cache/PartitionResolver;` | 48 | 30 |
| `API-f5caf9cb03fabc8b` | `org.apache.geode.cache.PartitionAttributes` | `getRecoveryDelay` | `()J` | 48 | 30 |
| `API-c0169fc3ff84abfd` | `org.apache.geode.cache.PartitionAttributes` | `getRedundantCopies` | `()I` | 48 | 30 |
| `API-e1c533578d65edfb` | `org.apache.geode.cache.PartitionAttributes` | `getStartupRecoveryDelay` | `()J` | 48 | 30 |
| `API-24b5a7df1e4b49be` | `org.apache.geode.cache.PartitionAttributes` | `getTotalMaxMemory` | `()J` | 48 | 30 |
| `API-90b6fb7dba8ab70d` | `org.apache.geode.cache.PartitionAttributes` | `getTotalNumBuckets` | `()I` | 48 | 30 |
| `API-58dfd18447674d47` | `org.apache.geode.cache.PartitionAttributes` | `getTotalSize` | `()J` | 48 | 30 |
| `API-9a8da3d095cefc1d` | `org.apache.geode.cache.PartitionAttributesFactory` | `<init>` | `()V` | 48 | 30 |
| `API-f866e36a3ea6ffb3` | `org.apache.geode.cache.PartitionAttributesFactory` | `<init>` | `(Lorg/apache/geode/cache/PartitionAttributes;)V` | 48 | 30 |
| `API-35ade031078b630e` | `org.apache.geode.cache.PartitionAttributesFactory` | `<type>` | `(type)` | 48 | 30 |
| `API-294c9b2d4f685faa` | `org.apache.geode.cache.PartitionAttributesFactory` | `GLOBAL_MAX_BUCKETS_DEFAULT` | `I` | 48 | 30 |
| `API-6d05512184dd08ec` | `org.apache.geode.cache.PartitionAttributesFactory` | `GLOBAL_MAX_BUCKETS_PROPERTY` | `Ljava/lang/String;` | 48 | 30 |
| `API-37b270d4ae476cd5` | `org.apache.geode.cache.PartitionAttributesFactory` | `GLOBAL_MAX_MEMORY_DEFAULT` | `J` | 48 | 30 |
| `API-457773f49ebb2c18` | `org.apache.geode.cache.PartitionAttributesFactory` | `GLOBAL_MAX_MEMORY_PROPERTY` | `Ljava/lang/String;` | 48 | 30 |
| `API-154690a5b018230b` | `org.apache.geode.cache.PartitionAttributesFactory` | `LOCAL_MAX_MEMORY_DEFAULT` | `I` | 48 | 30 |
| `API-125ed1cfe33c1062` | `org.apache.geode.cache.PartitionAttributesFactory` | `LOCAL_MAX_MEMORY_PROPERTY` | `Ljava/lang/String;` | 48 | 30 |
| `API-4b48a2db8b2497b7` | `org.apache.geode.cache.PartitionAttributesFactory` | `RECOVERY_DELAY_DEFAULT` | `J` | 48 | 30 |
| `API-1d4933de0fd61b49` | `org.apache.geode.cache.PartitionAttributesFactory` | `STARTUP_RECOVERY_DELAY_DEFAULT` | `J` | 48 | 30 |
| `API-4481696755bd3114` | `org.apache.geode.cache.PartitionAttributesFactory` | `addPartitionListener` | `(Lorg/apache/geode/cache/partition/PartitionListener;)Lorg/apache/geode/cache/PartitionAttributesFactory;` | 48 | 30 |
| `API-0782ceba4930971a` | `org.apache.geode.cache.PartitionAttributesFactory` | `create` | `()Lorg/apache/geode/cache/PartitionAttributes;` | 48 | 30 |
| `API-c12fe62c5b901aa5` | `org.apache.geode.cache.PartitionAttributesFactory` | `setColocatedWith` | `(Ljava/lang/String;)Lorg/apache/geode/cache/PartitionAttributesFactory;` | 48 | 30 |
| `API-b5c9364ddc91e584` | `org.apache.geode.cache.PartitionAttributesFactory` | `setGlobalProperties` | `(Ljava/util/Properties;)Lorg/apache/geode/cache/PartitionAttributesFactory;` | 48 | 30 |
| `API-2280fdb7663a0740` | `org.apache.geode.cache.PartitionAttributesFactory` | `setLocalMaxMemory` | `(I)Lorg/apache/geode/cache/PartitionAttributesFactory;` | 48 | 30 |
| `API-951f5911ff411921` | `org.apache.geode.cache.PartitionAttributesFactory` | `setLocalProperties` | `(Ljava/util/Properties;)Lorg/apache/geode/cache/PartitionAttributesFactory;` | 48 | 30 |
| `API-df1f6a4733c6f174` | `org.apache.geode.cache.PartitionAttributesFactory` | `setPartitionResolver` | `(Lorg/apache/geode/cache/PartitionResolver;)Lorg/apache/geode/cache/PartitionAttributesFactory;` | 48 | 30 |
| `API-fc56bf884af1287b` | `org.apache.geode.cache.PartitionAttributesFactory` | `setRecoveryDelay` | `(J)Lorg/apache/geode/cache/PartitionAttributesFactory;` | 48 | 30 |
| `API-c6421cf7697353eb` | `org.apache.geode.cache.PartitionAttributesFactory` | `setRedundantCopies` | `(I)Lorg/apache/geode/cache/PartitionAttributesFactory;` | 48 | 30 |
| `API-17a8a2f88869ec4e` | `org.apache.geode.cache.PartitionAttributesFactory` | `setStartupRecoveryDelay` | `(J)Lorg/apache/geode/cache/PartitionAttributesFactory;` | 48 | 30 |
| `API-040fb31a94c12f67` | `org.apache.geode.cache.PartitionAttributesFactory` | `setTotalMaxMemory` | `(J)Lorg/apache/geode/cache/PartitionAttributesFactory;` | 48 | 30 |
| `API-aac69bcd09da617c` | `org.apache.geode.cache.PartitionAttributesFactory` | `setTotalNumBuckets` | `(I)Lorg/apache/geode/cache/PartitionAttributesFactory;` | 48 | 30 |

### excluded-partition-hosting-listeners — 13 IDs

| API ID | Native receiver | Member | JVM descriptor | Owner | Consumer |
| --- | --- | --- | --- | --- | --- |
| `API-e6857074a5052dc2` | `org.apache.geode.cache.partition.PartitionListener` | `<type>` | `(type)` | 48 | 30 |
| `API-951abbf76833abc9` | `org.apache.geode.cache.partition.PartitionListener` | `afterBucketCreated` | `(ILjava/lang/Iterable;)V` | 48 | 30 |
| `API-6c96f729acca3abe` | `org.apache.geode.cache.partition.PartitionListener` | `afterBucketRemoved` | `(ILjava/lang/Iterable;)V` | 48 | 30 |
| `API-22e27ef1d63e5d9f` | `org.apache.geode.cache.partition.PartitionListener` | `afterPrimary` | `(I)V` | 48 | 30 |
| `API-05964d93b69eb1c2` | `org.apache.geode.cache.partition.PartitionListener` | `afterRegionCreate` | `(Lorg/apache/geode/cache/Region;)V` | 48 | 30 |
| `API-f821b4f1671ff0cb` | `org.apache.geode.cache.partition.PartitionListener` | `afterSecondary` | `(I)V` | 48 | 30 |
| `API-3f426fcc14b82236` | `org.apache.geode.cache.partition.PartitionListenerAdapter` | `<init>` | `()V` | 48 | 30 |
| `API-2472a33675215d04` | `org.apache.geode.cache.partition.PartitionListenerAdapter` | `<type>` | `(type)` | 48 | 30 |
| `API-09402d154bef7834` | `org.apache.geode.cache.partition.PartitionListenerAdapter` | `afterBucketCreated` | `(ILjava/lang/Iterable;)V` | 48 | 30 |
| `API-8b51574eea3c6fc8` | `org.apache.geode.cache.partition.PartitionListenerAdapter` | `afterBucketRemoved` | `(ILjava/lang/Iterable;)V` | 48 | 30 |
| `API-e7d2eb3c15f1079a` | `org.apache.geode.cache.partition.PartitionListenerAdapter` | `afterPrimary` | `(I)V` | 48 | 30 |
| `API-eb29bcad71fb87e5` | `org.apache.geode.cache.partition.PartitionListenerAdapter` | `afterRegionCreate` | `(Lorg/apache/geode/cache/Region;)V` | 48 | 30 |
| `API-260c96b43bf8b83b` | `org.apache.geode.cache.partition.PartitionListenerAdapter` | `afterSecondary` | `(I)V` | 48 | 30 |

### excluded-partition-rebalance-metadata — 23 IDs

| API ID | Native receiver | Member | JVM descriptor | Owner | Consumer |
| --- | --- | --- | --- | --- | --- |
| `API-abfe283281fab7d0` | `org.apache.geode.cache.partition.PartitionMemberInfo` | `<type>` | `(type)` | 48 | 30 |
| `API-deca88a839012f5e` | `org.apache.geode.cache.partition.PartitionMemberInfo` | `getBucketCount` | `()I` | 48 | 30 |
| `API-c820dd8f409d27af` | `org.apache.geode.cache.partition.PartitionMemberInfo` | `getConfiguredMaxMemory` | `()J` | 48 | 30 |
| `API-21d41eefa8300b6d` | `org.apache.geode.cache.partition.PartitionMemberInfo` | `getDistributedMember` | `()Lorg/apache/geode/distributed/DistributedMember;` | 48 | 30 |
| `API-076807c4080ae2d9` | `org.apache.geode.cache.partition.PartitionMemberInfo` | `getPrimaryCount` | `()I` | 48 | 30 |
| `API-c5a441fd7f437efb` | `org.apache.geode.cache.partition.PartitionMemberInfo` | `getSize` | `()J` | 48 | 30 |
| `API-db2561d4555b4c26` | `org.apache.geode.cache.partition.PartitionRebalanceInfo` | `<type>` | `(type)` | 48 | 30 |
| `API-5db24ac6cbf0cb5d` | `org.apache.geode.cache.partition.PartitionRebalanceInfo` | `getBucketCreateBytes` | `()J` | 48 | 30 |
| `API-b83f3b6f8f2f220e` | `org.apache.geode.cache.partition.PartitionRebalanceInfo` | `getBucketCreateTime` | `()J` | 48 | 30 |
| `API-9526a152c41ccefd` | `org.apache.geode.cache.partition.PartitionRebalanceInfo` | `getBucketCreatesCompleted` | `()I` | 48 | 30 |
| `API-779a8ab0dd7fdb0e` | `org.apache.geode.cache.partition.PartitionRebalanceInfo` | `getBucketRemoveBytes` | `()J` | 48 | 30 |
| `API-58fd7a0cc6a18003` | `org.apache.geode.cache.partition.PartitionRebalanceInfo` | `getBucketRemoveTime` | `()J` | 48 | 30 |
| `API-44c60394f721bb8c` | `org.apache.geode.cache.partition.PartitionRebalanceInfo` | `getBucketRemovesCompleted` | `()I` | 48 | 30 |
| `API-768a5fc2c2996301` | `org.apache.geode.cache.partition.PartitionRebalanceInfo` | `getBucketTransferBytes` | `()J` | 48 | 30 |
| `API-5f50ac295838b077` | `org.apache.geode.cache.partition.PartitionRebalanceInfo` | `getBucketTransferTime` | `()J` | 48 | 30 |
| `API-63b4338c4a1097b0` | `org.apache.geode.cache.partition.PartitionRebalanceInfo` | `getBucketTransfersCompleted` | `()I` | 48 | 30 |
| `API-d00cedf4b177b5b9` | `org.apache.geode.cache.partition.PartitionRebalanceInfo` | `getNumberOfMembersExecutedOn` | `()I` | 48 | 30 |
| `API-fecb31128401de3d` | `org.apache.geode.cache.partition.PartitionRebalanceInfo` | `getPartitionMemberDetailsAfter` | `()Ljava/util/Set;` | 48 | 30 |
| `API-356ed3ebf02148f1` | `org.apache.geode.cache.partition.PartitionRebalanceInfo` | `getPartitionMemberDetailsBefore` | `()Ljava/util/Set;` | 48 | 30 |
| `API-e958ed6052045a38` | `org.apache.geode.cache.partition.PartitionRebalanceInfo` | `getPrimaryTransferTime` | `()J` | 48 | 30 |
| `API-f0f7d18828b1ed92` | `org.apache.geode.cache.partition.PartitionRebalanceInfo` | `getPrimaryTransfersCompleted` | `()I` | 48 | 30 |
| `API-696f59fc86730b1b` | `org.apache.geode.cache.partition.PartitionRebalanceInfo` | `getRegionPath` | `()Ljava/lang/String;` | 48 | 30 |
| `API-eea2886e80285194` | `org.apache.geode.cache.partition.PartitionRebalanceInfo` | `getTime` | `()J` | 48 | 30 |

### excluded-peer-role-reliability — 39 IDs

| API ID | Native receiver | Member | JVM descriptor | Owner | Consumer |
| --- | --- | --- | --- | --- | --- |
| `API-b33a8f32e7d35932` | `org.apache.geode.cache.LossAction` | `<type>` | `(type)` | 48 | 30 |
| `API-85d4ca376360ef87` | `org.apache.geode.cache.LossAction` | `FULL_ACCESS` | `Lorg/apache/geode/cache/LossAction;` | 48 | 30 |
| `API-6950c0af57bd3e98` | `org.apache.geode.cache.LossAction` | `LIMITED_ACCESS` | `Lorg/apache/geode/cache/LossAction;` | 48 | 30 |
| `API-c2576e0e27e52217` | `org.apache.geode.cache.LossAction` | `NO_ACCESS` | `Lorg/apache/geode/cache/LossAction;` | 48 | 30 |
| `API-9b618538d8a23541` | `org.apache.geode.cache.LossAction` | `RECONNECT` | `Lorg/apache/geode/cache/LossAction;` | 48 | 30 |
| `API-f96dd44ade2b3ca9` | `org.apache.geode.cache.LossAction` | `VALUES` | `Ljava/util/List;` | 48 | 30 |
| `API-b9d65ece5c2a5bff` | `org.apache.geode.cache.LossAction` | `fromName` | `(Ljava/lang/String;)Lorg/apache/geode/cache/LossAction;` | 48 | 30 |
| `API-11fa5f32aa7d9bc0` | `org.apache.geode.cache.LossAction` | `fromOrdinal` | `(B)Lorg/apache/geode/cache/LossAction;` | 48 | 30 |
| `API-36cf50e1e6cebf46` | `org.apache.geode.cache.LossAction` | `isAllAccess` | `()Z` | 48 | 30 |
| `API-6f5f71aacd05e330` | `org.apache.geode.cache.LossAction` | `isLimitedAccess` | `()Z` | 48 | 30 |
| `API-ab1e781338a2bd87` | `org.apache.geode.cache.LossAction` | `isNoAccess` | `()Z` | 48 | 30 |
| `API-4284d6a86d6f9f46` | `org.apache.geode.cache.LossAction` | `isReconnect` | `()Z` | 48 | 30 |
| `API-8ade574021760bfe` | `org.apache.geode.cache.LossAction` | `ordinal` | `B` | 48 | 30 |
| `API-5f75cfb85da5c96c` | `org.apache.geode.cache.LossAction` | `toString` | `()Ljava/lang/String;` | 48 | 30 |
| `API-d90852cbcbd4d112` | `org.apache.geode.cache.MembershipAttributes` | `<init>` | `()V` | 48 | 30 |
| `API-fa91a479ba03be70` | `org.apache.geode.cache.MembershipAttributes` | `<init>` | `([Ljava/lang/String;)V` | 48 | 30 |
| `API-1c47341a6f27654e` | `org.apache.geode.cache.MembershipAttributes` | `<init>` | `([Ljava/lang/String;Lorg/apache/geode/cache/LossAction;Lorg/apache/geode/cache/ResumptionAction;)V` | 48 | 30 |
| `API-97b682af66589a88` | `org.apache.geode.cache.MembershipAttributes` | `<type>` | `(type)` | 48 | 30 |
| `API-b04991278fc1553e` | `org.apache.geode.cache.MembershipAttributes` | `equals` | `(Ljava/lang/Object;)Z` | 48 | 30 |
| `API-44aefe1034b7c8be` | `org.apache.geode.cache.MembershipAttributes` | `fromData` | `(Ljava/io/DataInput;)V` | 48 | 30 |
| `API-9a1ab4ddfde196c5` | `org.apache.geode.cache.MembershipAttributes` | `getLossAction` | `()Lorg/apache/geode/cache/LossAction;` | 48 | 30 |
| `API-4c2a4553f8d6e817` | `org.apache.geode.cache.MembershipAttributes` | `getRequiredRoles` | `()Ljava/util/Set;` | 48 | 30 |
| `API-6339990e3ff3bc73` | `org.apache.geode.cache.MembershipAttributes` | `getResumptionAction` | `()Lorg/apache/geode/cache/ResumptionAction;` | 48 | 30 |
| `API-4c20031f035c2cc3` | `org.apache.geode.cache.MembershipAttributes` | `hasRequiredRoles` | `()Z` | 48 | 30 |
| `API-b956bf5cfcb6bb89` | `org.apache.geode.cache.MembershipAttributes` | `hashCode` | `()I` | 48 | 30 |
| `API-a0b8d0d1af0fb0bd` | `org.apache.geode.cache.MembershipAttributes` | `readExternal` | `(Ljava/io/ObjectInput;)V` | 48 | 30 |
| `API-0cb11846e3c3c995` | `org.apache.geode.cache.MembershipAttributes` | `toData` | `(Ljava/io/DataOutput;)V` | 48 | 30 |
| `API-6eb5d112fcf5d3c4` | `org.apache.geode.cache.MembershipAttributes` | `toString` | `()Ljava/lang/String;` | 48 | 30 |
| `API-1b193801f1e82cb0` | `org.apache.geode.cache.MembershipAttributes` | `writeExternal` | `(Ljava/io/ObjectOutput;)V` | 48 | 30 |
| `API-181650eb1e0c5bed` | `org.apache.geode.cache.ResumptionAction` | `<type>` | `(type)` | 48 | 30 |
| `API-84d17630f0a5fa16` | `org.apache.geode.cache.ResumptionAction` | `NONE` | `Lorg/apache/geode/cache/ResumptionAction;` | 48 | 30 |
| `API-f1f53de8f3595cac` | `org.apache.geode.cache.ResumptionAction` | `REINITIALIZE` | `Lorg/apache/geode/cache/ResumptionAction;` | 48 | 30 |
| `API-fe54d633d4904959` | `org.apache.geode.cache.ResumptionAction` | `VALUES` | `Ljava/util/List;` | 48 | 30 |
| `API-d4fc4843753a1ee9` | `org.apache.geode.cache.ResumptionAction` | `fromName` | `(Ljava/lang/String;)Lorg/apache/geode/cache/ResumptionAction;` | 48 | 30 |
| `API-49a4f1f629e629ed` | `org.apache.geode.cache.ResumptionAction` | `fromOrdinal` | `(B)Lorg/apache/geode/cache/ResumptionAction;` | 48 | 30 |
| `API-1e676cd69e29be74` | `org.apache.geode.cache.ResumptionAction` | `isNone` | `()Z` | 48 | 30 |
| `API-b5266c3add129c03` | `org.apache.geode.cache.ResumptionAction` | `isReinitialize` | `()Z` | 48 | 30 |
| `API-8e7d5b3e3071f961` | `org.apache.geode.cache.ResumptionAction` | `ordinal` | `B` | 48 | 30 |
| `API-c9984fb9cf8363ed` | `org.apache.geode.cache.ResumptionAction` | `toString` | `()Ljava/lang/String;` | 48 | 30 |
