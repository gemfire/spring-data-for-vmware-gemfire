# Query, index and CQ scope completion — 2026-10-28

Second and final scope batch for [task 65](../../wayfinder-issues/65-audit-query-and-cq-client-scope.md): **177 IDs, 175 required-client and two excluded-server**. Together with [the first 84 result IDs](client-query-result-scope.md), all **261 assigned IDs** are classified: **259 required-client / two excluded-server / zero unresolved**. No implementation status changes and no new runtime probe.

Evidence uses GemFire **10.4.0-build.3056.df98c835**, source commit `df98c8357afe85853d9a4776255d4f0e7cf064cb` in `/Users/uk016635/projects/gemfire`. Read with `git show <commit>:<path>` rather than the moving worktree. Core paths below start at `gemfire-core/src/main/java/org/apache/geode/`; CQ implementation paths start at `gemfire-cq/src/main/java/org/apache/geode/cache/query/cq/internal/`. Public declarations are in core `cache/query/`, with listener adapter in `cache/util/` and type metadata in `cache/query/types/`.

## client-query-routing-and-execution

Core `cache/client/internal/PoolImpl.getQueryService`, `internal/cache/GemFireCacheImpl.getQueryService/getLocalQueryService` and `cache/query/internal/DefaultQueryService.newQuery` distinguish local execution, default/named pool execution and authenticated views. `cache/query/internal/DefaultQuery.execute(Object[])` uses its server proxy when present; local execution retains its own evaluation and statistics. Preserve null-parameter rejection, scalar/collection/struct adaptation, query text and execution/time inspection. `compile` explicitly throws unsupported and `isCompiled` returns false at this version: client scope does not promise successful compilation. `QueryService.UNDEFINED` needs a typed sentinel distinct from null.

`internal/cache/LocalRegion.query` uses the server proxy for server-backed regions and the local query service otherwise. `internal/cache/AbstractRegion.existsValue/selectValue` use that result: existence tests emptiness; selection returns null for empty, the sole value for one and a domain failure for multiple results. Do not route according to the client's local size. Task 21 implements these behaviors, task 31 migrates templates/repositories and task 35 wires pool access. Spring `GemfireTemplate.resolveQueryService` already distinguishes local and remote client paths.

## client-local-index-operations

Core `cache/query/internal/DefaultQueryService` implements create/define/clear/create-defined/get/remove index operations against local regions. Its direct index operations reject a configured server pool. The no-argument `getIndexes` traverses roots/subregions and calls the regional getter, so do not invent an unconditional failure for an empty traversal. Deferred definitions are stored before creation; preserve per-index failures and names from `MultiIndexCreationException`. `cache/query/internal/ProxyQueryService` rejects index operations on authenticated query views. These restrictions do not remove local-client indexing from scope.

Core `cache/query/internal/index/AbstractIndex`, `RangeIndex`, `CompactRangeIndex` and `PrimaryKeyIndex` implement the returned metadata/statistics. Preserve name, original/canonical expressions/projections/from-clause, typed region, index type, validity, counts and timing. `AbstractIndex.InternalIndexStatistics` supplies zero bucket-index counts for local indexes; deprecated int getters cast the long values. A bucket-named observation is not permission to host partitioned regions. Retain the actual per-index lock/map/value counters and unsupported behavior rather than manufacturing server metrics. `cache/query/IndexType` preserves FUNCTIONAL/RANGE, PRIMARY_KEY/KEY and deprecated HASH naming plus enum and synonym parsing. Task 21 owns native/mock equivalents; task 31 owns exposed query/result consumers. No current production Spring create/define-index call was found; this does not erase the retained reachable contract.

## client-cq-lifecycle-and-callbacks

`CqServiceImpl.newCq` explicitly creates client CQs and rejects server-side creation through this API. `ClientCQImpl.execute/executeWithInitialResults/stop/close` uses the pool/server proxy, validates states and queues concurrent initial-result events. `CqQueryImpl` supplies query/name/state/durability/statistics observations. Preserve named/unnamed and durable creation, duplicate/name/query failures, STOPPED/RUNNING/CLOSING/CLOSED observations, initial key/value structs, query inspection and client lifecycle. Initial results plus event delivery are not an invented exactly-once snapshot guarantee.

Core `DefaultQueryService` provides client CQ lookup/listing, per-region/bulk lifecycle and durable-name retrieval through the pool. Preserve method-specific empty/null/failure behavior: several bulk operations catch/log CqException; `getAllDurableCqsFromServer` requires a pool. Core `ProxyQueryService` scopes CQ access to the authenticated view; its statistics getter rejects access and its pinned durable-name implementation delegates then returns an empty list. Record these actual version-specific outcomes in adapter tests; do not silently substitute another user's or global service. Native swallowed failures are not grounds for new adapter fallbacks.

Core `CqAttributesFactory` and `cache/query/internal/cq/CqAttributesImpl` provide default/copy construction, add/init/remove, observation and excluded-event settings. The copy constructor copies listeners only; `create` uses a shallow clone. `getCqListeners` returns an array, the singular getter returns null/one or fails for multiple listeners, add rejects null, and init with null/empty clears listeners. Excluded-event observation is an unmodifiable view of the configured set, not a guaranteed defensive snapshot. Preserve registration identity/order, actual duplicate/alias behavior and listener close on mutation; do not silently copy away observable state. `ClientCQImpl.createOn` submits excluded events to the server, so CREATE/UPDATE/DESTROY/INVALIDATE configuration remains a client contract.

`CqServiceImpl.dispatchCqListeners` selects onEvent/onError and invokes connection-status callbacks. `CqEventImpl` retains base versus query operation, key, new value, delta bytes, CQ and throwable. A missing new value with delta throws InvalidDeltaException; dispatch can fetch a full value and re-invoke the listener. Adapt this retry path without exposing native events/exceptions or suppressing full-value recovery. `CqStatisticsImpl`, `CqServiceStatisticsImpl` and `CqStateImpl` provide per-query/service counters and state strings; region CQ counts use local service lookup and can return zero on failure, not remote server administration.

Inherited `close`, `init(Properties)` and `initialize(Cache, Properties)` remain client callback behavior on CqListener, CqStatusListener and CqListenerAdapter. Core `cache/Declarable` provides no-op defaults; `internal/cache/xmlcache/CacheCreation.initializeDeclarablesMap` invokes initialize then legacy init, and `ClientCacheCreation.create` calls that initialization path. Programmatic CQ registration does not automatically invoke those hooks. Use a typed client owner instead of native Cache, propagate initialization failures, and retain adapter no-op defaults and listener cleanup semantics; this is not a requirement to introduce server XML or gfsh administration. The abstract adapter constructor maps to constructible/subclassable typed callback defaults, not native inheritance.

Task 22 implements all CQ-owned rows. QueryService CQ entry-point rows retain matrix owner 21, with task 22 supplying their CQ collaborator. Task 33 migrates `listener/ContinuousQueryListenerContainer` registration, initialization, dispatch and cleanup; task 31 handles shared QueryService consumers. Task 35 coordinates service routing. Shared exception and security scope remain with tasks 76 and 70; no extra IDs are taken from them. Native/mock delivery and lifecycle tests remain implementation acceptance, with explicit rejection of unavailable mock streaming.

## client-query-metadata-serialization

Core `cache/query/internal/types/ObjectTypeImpl.toData/fromData` bridge the inherited DataSerializable signatures to contextual codecs and write/read the represented class. CollectionTypeImpl adds the nested element type; StructTypeImpl adds ordered field-name and field-type arrays. The client response path `cache/client/internal/QueryOp.QueryOpImpl.processResponse` consumes CollectionType and StructType to construct client results, as established in the first batch. These are client wire values, not server-only callbacks.

All six inherited rows remain required through equivalent typed encoding/decoding of query metadata. Keep the native wire codecs inside the versioned driver; expose no native DataSerializable, implementation Class or unwrap handle. Preserve class resolution, nested types, field ordering and IO/class-resolution failures. A private response decode alone does not establish complete equivalence for callable encode/decode rows: task 21 must map those operations through native-free serialization collaborators and validate round trips with task 55. Do not promise a universal cross-version binary format or make the mock fabricate native bytes. Task 31 owns consumer adaptation. Task 71 is informed of this six-row disposition; its own fixed 401-ID inventory is unchanged.

## server-function-query

Only `Query.execute(RegionFunctionContext)` and `Query.execute(RegionFunctionContext,Object[])` are excluded. Core `cache/query/internal/DefaultQuery` requires that context, checks for a partition-region query, obtains `LocalDataSet` through `PartitionRegionHelper.getLocalDataForContext`, and evaluates the function's local bucket set. This is server function-body execution. Ordinary client execute overloads and typed client function submission remain required elsewhere.

Both rows are missing with an empty SPI mapping and pending native/mock/test placeholders; neither is a removed native feature. Current Spring production has no RegionFunctionContext reference, and no existing SPI comparison entry maps either ID. Therefore there is no public implementation or production call to retire for these two exclusions. Query's type row remains required-client, so source references to Query stay conservatively retained rather than being broadly excluded. Private server fixture bodies, if added later, belong inside versioned integration infrastructure, not public SPI.

## Reconciliation and handoff

All 177 rows retain their implementation and consumer owners: 91 under task 21/31, one under 21/35 and 85 under 22/33. Required rows remain missing. Source/resource references inherit type scope conservatively; mixed QueryService references stay with task 21 and downstream consumers. No existing public SPI operation implements the new query/CQ requirements. Existing SPI mappings and the frozen assignment ledger remain unchanged. Supporting serialization acceptance is recorded in task 55, with no dependency back to audit task 65 and no new cycle.

There are no unanswered scope questions in this fixed family. Adapter behavior, alias compatibility, errors, secured routing and wire round trips remain implementation validation, not additional audit probes. Global totals are **758 required-client / 631 excluded-server / 2,462 unresolved**; implementation totals stay **3,111 missing / 484 partial / 256 implemented**. Tasks 59/64 remain open.

## Frozen member ledger

The first batch's 84 rows stay in its linked ledger. These are the remaining 177 exact IDs; receivers and JVM descriptors are unchanged from the frozen assignment file.

### client-query-routing-and-execution — 17 IDs

| API ID | Native receiver | Member | JVM descriptor |
| --- | --- | --- | --- |
| `API-d14d0c9fdc14513b` | `org.apache.geode.cache.Region` | `existsValue` | `(Ljava/lang/String;)Z` |
| `API-9fe5c261c1507575` | `org.apache.geode.cache.Region` | `query` | `(Ljava/lang/String;)Lorg/apache/geode/cache/query/SelectResults;` |
| `API-0866e2d354a9683d` | `org.apache.geode.cache.Region` | `selectValue` | `(Ljava/lang/String;)Ljava/lang/Object;` |
| `API-90b2555d61937e43` | `org.apache.geode.cache.client.Pool` | `getQueryService` | `()Lorg/apache/geode/cache/query/QueryService;` |
| `API-1241d239e2cf497b` | `org.apache.geode.cache.query.Query` | `<type>` | `(type)` |
| `API-70f9093f48feeb62` | `org.apache.geode.cache.query.Query` | `compile` | `()V` |
| `API-da819afca212506b` | `org.apache.geode.cache.query.Query` | `execute` | `()Ljava/lang/Object;` |
| `API-8a16d8924707b47b` | `org.apache.geode.cache.query.Query` | `execute` | `([Ljava/lang/Object;)Ljava/lang/Object;` |
| `API-add4b862c9f4f7bd` | `org.apache.geode.cache.query.Query` | `getQueryString` | `()Ljava/lang/String;` |
| `API-cb864a32508929be` | `org.apache.geode.cache.query.Query` | `getStatistics` | `()Lorg/apache/geode/cache/query/QueryStatistics;` |
| `API-6587083b02c1422a` | `org.apache.geode.cache.query.Query` | `isCompiled` | `()Z` |
| `API-a901fedfea7c41d5` | `org.apache.geode.cache.query.QueryService` | `<type>` | `(type)` |
| `API-5c26903e177d2796` | `org.apache.geode.cache.query.QueryService` | `UNDEFINED` | `Ljava/lang/Object;` |
| `API-f872786b10bbeb3f` | `org.apache.geode.cache.query.QueryService` | `newQuery` | `(Ljava/lang/String;)Lorg/apache/geode/cache/query/Query;` |
| `API-6aff7852bb90036a` | `org.apache.geode.cache.query.QueryStatistics` | `<type>` | `(type)` |
| `API-0fc5d1e4d653feec` | `org.apache.geode.cache.query.QueryStatistics` | `getNumExecutions` | `()J` |
| `API-637314b0b8377d4d` | `org.apache.geode.cache.query.QueryStatistics` | `getTotalExecutionTime` | `()J` |

### client-cq-lifecycle-and-callbacks — 99 IDs

| API ID | Native receiver | Member | JVM descriptor |
| --- | --- | --- | --- |
| `API-1d06c4bd7080908e` | `org.apache.geode.cache.query.CqAttributes` | `<type>` | `(type)` |
| `API-3280b9433593c2f4` | `org.apache.geode.cache.query.CqAttributes` | `getCqListener` | `()Lorg/apache/geode/cache/query/CqListener;` |
| `API-aadd16201ef474df` | `org.apache.geode.cache.query.CqAttributes` | `getCqListeners` | `()[Lorg/apache/geode/cache/query/CqListener;` |
| `API-b9d2c68966753836` | `org.apache.geode.cache.query.CqAttributes` | `getExcludedEvents` | `()Ljava/util/Set;` |
| `API-70065899233dfa3b` | `org.apache.geode.cache.query.CqAttributesFactory` | `<init>` | `()V` |
| `API-8ea3c199cf0f3dae` | `org.apache.geode.cache.query.CqAttributesFactory` | `<init>` | `(Lorg/apache/geode/cache/query/CqAttributes;)V` |
| `API-1176b225b9b6342a` | `org.apache.geode.cache.query.CqAttributesFactory` | `<type>` | `(type)` |
| `API-976f8c7a1143be1a` | `org.apache.geode.cache.query.CqAttributesFactory` | `addCqListener` | `(Lorg/apache/geode/cache/query/CqListener;)V` |
| `API-bd2f62a07fdceea1` | `org.apache.geode.cache.query.CqAttributesFactory` | `create` | `()Lorg/apache/geode/cache/query/CqAttributes;` |
| `API-3b4f3d482074560d` | `org.apache.geode.cache.query.CqAttributesFactory` | `initCqListeners` | `([Lorg/apache/geode/cache/query/CqListener;)V` |
| `API-b6f5b6f002783973` | `org.apache.geode.cache.query.CqAttributesFactory` | `setExcludedEvents` | `(Ljava/util/Set;)V` |
| `API-baae134dbb4c2c8a` | `org.apache.geode.cache.query.CqAttributesMutator` | `<type>` | `(type)` |
| `API-425419ec424e488d` | `org.apache.geode.cache.query.CqAttributesMutator` | `addCqListener` | `(Lorg/apache/geode/cache/query/CqListener;)V` |
| `API-8b2d27899f226374` | `org.apache.geode.cache.query.CqAttributesMutator` | `initCqListeners` | `([Lorg/apache/geode/cache/query/CqListener;)V` |
| `API-7c5f56380e393fa6` | `org.apache.geode.cache.query.CqAttributesMutator` | `removeCqListener` | `(Lorg/apache/geode/cache/query/CqListener;)V` |
| `API-94ff6fe8b84564bf` | `org.apache.geode.cache.query.CqEvent` | `<type>` | `(type)` |
| `API-30ae7b1c38bff083` | `org.apache.geode.cache.query.CqEvent` | `getBaseOperation` | `()Lorg/apache/geode/cache/Operation;` |
| `API-dcebabee38fb5a67` | `org.apache.geode.cache.query.CqEvent` | `getCq` | `()Lorg/apache/geode/cache/query/CqQuery;` |
| `API-a01c45bb3ca40d01` | `org.apache.geode.cache.query.CqEvent` | `getDeltaValue` | `()[B` |
| `API-2772051709c302bb` | `org.apache.geode.cache.query.CqEvent` | `getKey` | `()Ljava/lang/Object;` |
| `API-8ab1245a692264bd` | `org.apache.geode.cache.query.CqEvent` | `getNewValue` | `()Ljava/lang/Object;` |
| `API-a592f892e1c3cffd` | `org.apache.geode.cache.query.CqEvent` | `getQueryOperation` | `()Lorg/apache/geode/cache/Operation;` |
| `API-2dc9af71d22065ca` | `org.apache.geode.cache.query.CqEvent` | `getThrowable` | `()Ljava/lang/Throwable;` |
| `API-5137328cf7f6d36b` | `org.apache.geode.cache.query.CqListener` | `<type>` | `(type)` |
| `API-1fa69b497dcc5ef7` | `org.apache.geode.cache.query.CqListener` | `close` | `()V` |
| `API-1067eec5ee2c3f30` | `org.apache.geode.cache.query.CqListener` | `init` | `(Ljava/util/Properties;)V` |
| `API-d8168958d0a54999` | `org.apache.geode.cache.query.CqListener` | `initialize` | `(Lorg/apache/geode/cache/Cache;Ljava/util/Properties;)V` |
| `API-48bbde55d66c55ae` | `org.apache.geode.cache.query.CqListener` | `onError` | `(Lorg/apache/geode/cache/query/CqEvent;)V` |
| `API-6e3de7cf90029e88` | `org.apache.geode.cache.query.CqListener` | `onEvent` | `(Lorg/apache/geode/cache/query/CqEvent;)V` |
| `API-39c4133af8249559` | `org.apache.geode.cache.query.CqQuery` | `<type>` | `(type)` |
| `API-6b07f8041b619268` | `org.apache.geode.cache.query.CqQuery` | `close` | `()V` |
| `API-21185b04cabd8229` | `org.apache.geode.cache.query.CqQuery` | `execute` | `()V` |
| `API-db681943ddad8df5` | `org.apache.geode.cache.query.CqQuery` | `executeWithInitialResults` | `()Lorg/apache/geode/cache/query/CqResults;` |
| `API-2e2305abdd02372d` | `org.apache.geode.cache.query.CqQuery` | `getCqAttributes` | `()Lorg/apache/geode/cache/query/CqAttributes;` |
| `API-d483d5e2869f8cd7` | `org.apache.geode.cache.query.CqQuery` | `getCqAttributesMutator` | `()Lorg/apache/geode/cache/query/CqAttributesMutator;` |
| `API-3ed8392319a143cf` | `org.apache.geode.cache.query.CqQuery` | `getName` | `()Ljava/lang/String;` |
| `API-dfa1d5f9c9ab0dfb` | `org.apache.geode.cache.query.CqQuery` | `getQuery` | `()Lorg/apache/geode/cache/query/Query;` |
| `API-d09bb65f0be3e5b3` | `org.apache.geode.cache.query.CqQuery` | `getQueryString` | `()Ljava/lang/String;` |
| `API-3bfd80c2f69705bb` | `org.apache.geode.cache.query.CqQuery` | `getState` | `()Lorg/apache/geode/cache/query/CqState;` |
| `API-dfb2f2c681b98933` | `org.apache.geode.cache.query.CqQuery` | `getStatistics` | `()Lorg/apache/geode/cache/query/CqStatistics;` |
| `API-c3768595907dc933` | `org.apache.geode.cache.query.CqQuery` | `isClosed` | `()Z` |
| `API-c4f64a96e706bae7` | `org.apache.geode.cache.query.CqQuery` | `isDurable` | `()Z` |
| `API-37bfd3b4d63e349a` | `org.apache.geode.cache.query.CqQuery` | `isRunning` | `()Z` |
| `API-5be57fa66f4f671d` | `org.apache.geode.cache.query.CqQuery` | `isStopped` | `()Z` |
| `API-a3917bce0891bb7c` | `org.apache.geode.cache.query.CqQuery` | `stop` | `()V` |
| `API-6a655dbf325e2904` | `org.apache.geode.cache.query.CqServiceStatistics` | `<type>` | `(type)` |
| `API-b9dff09d3de83fe6` | `org.apache.geode.cache.query.CqServiceStatistics` | `numCqsActive` | `()J` |
| `API-8c7eb737c088f13b` | `org.apache.geode.cache.query.CqServiceStatistics` | `numCqsClosed` | `()J` |
| `API-a873284efa32e0de` | `org.apache.geode.cache.query.CqServiceStatistics` | `numCqsCreated` | `()J` |
| `API-09b3d307625c9355` | `org.apache.geode.cache.query.CqServiceStatistics` | `numCqsOnClient` | `()J` |
| `API-2010f00e2fe5d6d3` | `org.apache.geode.cache.query.CqServiceStatistics` | `numCqsOnRegion` | `(Ljava/lang/String;)J` |
| `API-4400a86863ed2878` | `org.apache.geode.cache.query.CqServiceStatistics` | `numCqsStopped` | `()J` |
| `API-bb19ebf015810b50` | `org.apache.geode.cache.query.CqState` | `<type>` | `(type)` |
| `API-e0d1e68164d94bca` | `org.apache.geode.cache.query.CqState` | `isClosed` | `()Z` |
| `API-0fa9e0e7e3a2fa29` | `org.apache.geode.cache.query.CqState` | `isClosing` | `()Z` |
| `API-ea136d8024a9760a` | `org.apache.geode.cache.query.CqState` | `isRunning` | `()Z` |
| `API-116e67c9a3343943` | `org.apache.geode.cache.query.CqState` | `isStopped` | `()Z` |
| `API-c0111f6a3a99a96f` | `org.apache.geode.cache.query.CqState` | `toString` | `()Ljava/lang/String;` |
| `API-dd101d36d8b616c7` | `org.apache.geode.cache.query.CqStatistics` | `<type>` | `(type)` |
| `API-26a570c667d41691` | `org.apache.geode.cache.query.CqStatistics` | `numDeletes` | `()J` |
| `API-e08ca8055a05b399` | `org.apache.geode.cache.query.CqStatistics` | `numEvents` | `()J` |
| `API-256216a30f0a034f` | `org.apache.geode.cache.query.CqStatistics` | `numInserts` | `()J` |
| `API-f57f83095264fcde` | `org.apache.geode.cache.query.CqStatistics` | `numUpdates` | `()J` |
| `API-225c352f621bc283` | `org.apache.geode.cache.query.CqStatusListener` | `<type>` | `(type)` |
| `API-f79e8387e7f1c382` | `org.apache.geode.cache.query.CqStatusListener` | `close` | `()V` |
| `API-6d8cabff8a58e120` | `org.apache.geode.cache.query.CqStatusListener` | `init` | `(Ljava/util/Properties;)V` |
| `API-5cf8789898f1b0ab` | `org.apache.geode.cache.query.CqStatusListener` | `initialize` | `(Lorg/apache/geode/cache/Cache;Ljava/util/Properties;)V` |
| `API-fdea96d777f47250` | `org.apache.geode.cache.query.CqStatusListener` | `onCqConnected` | `()V` |
| `API-a02ca3b82f00b155` | `org.apache.geode.cache.query.CqStatusListener` | `onCqDisconnected` | `()V` |
| `API-70e47e5edeeef451` | `org.apache.geode.cache.query.CqStatusListener` | `onError` | `(Lorg/apache/geode/cache/query/CqEvent;)V` |
| `API-bbb67ebd53a5669e` | `org.apache.geode.cache.query.CqStatusListener` | `onEvent` | `(Lorg/apache/geode/cache/query/CqEvent;)V` |
| `API-7187ca72d5ba6e9b` | `org.apache.geode.cache.query.ExcludedEvent` | `<type>` | `(type)` |
| `API-ad61332a4252494b` | `org.apache.geode.cache.query.ExcludedEvent` | `CREATE` | `Lorg/apache/geode/cache/query/ExcludedEvent;` |
| `API-8a9b40fac3eeca07` | `org.apache.geode.cache.query.ExcludedEvent` | `DESTROY` | `Lorg/apache/geode/cache/query/ExcludedEvent;` |
| `API-73d8f54565ea8ba9` | `org.apache.geode.cache.query.ExcludedEvent` | `INVALIDATE` | `Lorg/apache/geode/cache/query/ExcludedEvent;` |
| `API-b788ae80391b9a0a` | `org.apache.geode.cache.query.ExcludedEvent` | `UPDATE` | `Lorg/apache/geode/cache/query/ExcludedEvent;` |
| `API-895c8d45f01260bf` | `org.apache.geode.cache.query.ExcludedEvent` | `valueOf` | `(Ljava/lang/String;)Lorg/apache/geode/cache/query/ExcludedEvent;` |
| `API-c1ff4c35867606f4` | `org.apache.geode.cache.query.ExcludedEvent` | `values` | `()[Lorg/apache/geode/cache/query/ExcludedEvent;` |
| `API-3d08f4c502a4bf24` | `org.apache.geode.cache.query.QueryService` | `closeCqs` | `()V` |
| `API-c854aef1e47693ec` | `org.apache.geode.cache.query.QueryService` | `executeCqs` | `()V` |
| `API-fc9e26db52ea7035` | `org.apache.geode.cache.query.QueryService` | `executeCqs` | `(Ljava/lang/String;)V` |
| `API-c9c5cda457edb930` | `org.apache.geode.cache.query.QueryService` | `getAllDurableCqsFromServer` | `()Ljava/util/List;` |
| `API-2df840a8fc717b49` | `org.apache.geode.cache.query.QueryService` | `getCq` | `(Ljava/lang/String;)Lorg/apache/geode/cache/query/CqQuery;` |
| `API-69f97d3eb39d40ed` | `org.apache.geode.cache.query.QueryService` | `getCqStatistics` | `()Lorg/apache/geode/cache/query/CqServiceStatistics;` |
| `API-792462b11ad901f2` | `org.apache.geode.cache.query.QueryService` | `getCqs` | `()[Lorg/apache/geode/cache/query/CqQuery;` |
| `API-98e98185ea9a629f` | `org.apache.geode.cache.query.QueryService` | `getCqs` | `(Ljava/lang/String;)[Lorg/apache/geode/cache/query/CqQuery;` |
| `API-91ef92cc381e85bf` | `org.apache.geode.cache.query.QueryService` | `newCq` | `(Ljava/lang/String;Ljava/lang/String;Lorg/apache/geode/cache/query/CqAttributes;)Lorg/apache/geode/cache/query/CqQuery;` |
| `API-75ebb06fa15df85b` | `org.apache.geode.cache.query.QueryService` | `newCq` | `(Ljava/lang/String;Ljava/lang/String;Lorg/apache/geode/cache/query/CqAttributes;Z)Lorg/apache/geode/cache/query/CqQuery;` |
| `API-5024478114c7b21a` | `org.apache.geode.cache.query.QueryService` | `newCq` | `(Ljava/lang/String;Lorg/apache/geode/cache/query/CqAttributes;)Lorg/apache/geode/cache/query/CqQuery;` |
| `API-6d4c0f3d7b2d738e` | `org.apache.geode.cache.query.QueryService` | `newCq` | `(Ljava/lang/String;Lorg/apache/geode/cache/query/CqAttributes;Z)Lorg/apache/geode/cache/query/CqQuery;` |
| `API-743f8e2b1810ec14` | `org.apache.geode.cache.query.QueryService` | `stopCqs` | `()V` |
| `API-8e784fac6e805e0f` | `org.apache.geode.cache.query.QueryService` | `stopCqs` | `(Ljava/lang/String;)V` |
| `API-d0a580ff021dd2bf` | `org.apache.geode.cache.util.CqListenerAdapter` | `<init>` | `()V` |
| `API-20ad19527264bebf` | `org.apache.geode.cache.util.CqListenerAdapter` | `<type>` | `(type)` |
| `API-d6f7ed4fb466b1f8` | `org.apache.geode.cache.util.CqListenerAdapter` | `close` | `()V` |
| `API-c916a76fcf586f02` | `org.apache.geode.cache.util.CqListenerAdapter` | `init` | `(Ljava/util/Properties;)V` |
| `API-ce06f713a3e68818` | `org.apache.geode.cache.util.CqListenerAdapter` | `initialize` | `(Lorg/apache/geode/cache/Cache;Ljava/util/Properties;)V` |
| `API-ebfbaad0cd8fb762` | `org.apache.geode.cache.util.CqListenerAdapter` | `onError` | `(Lorg/apache/geode/cache/query/CqEvent;)V` |
| `API-5417622d31a95a58` | `org.apache.geode.cache.util.CqListenerAdapter` | `onEvent` | `(Lorg/apache/geode/cache/query/CqEvent;)V` |

### client-local-index-operations — 53 IDs

| API ID | Native receiver | Member | JVM descriptor |
| --- | --- | --- | --- |
| `API-edc8dcedcf8e5362` | `org.apache.geode.cache.query.Index` | `<type>` | `(type)` |
| `API-b97af8525dce7086` | `org.apache.geode.cache.query.Index` | `getCanonicalizedFromClause` | `()Ljava/lang/String;` |
| `API-80828cbdf7fa8d7f` | `org.apache.geode.cache.query.Index` | `getCanonicalizedIndexedExpression` | `()Ljava/lang/String;` |
| `API-7620d53543157c7a` | `org.apache.geode.cache.query.Index` | `getCanonicalizedProjectionAttributes` | `()Ljava/lang/String;` |
| `API-b1a9ee9e4ec0ce10` | `org.apache.geode.cache.query.Index` | `getFromClause` | `()Ljava/lang/String;` |
| `API-fee986887fb63c46` | `org.apache.geode.cache.query.Index` | `getIndexedExpression` | `()Ljava/lang/String;` |
| `API-71b2cea58b7c94c4` | `org.apache.geode.cache.query.Index` | `getName` | `()Ljava/lang/String;` |
| `API-209a0de62291608d` | `org.apache.geode.cache.query.Index` | `getProjectionAttributes` | `()Ljava/lang/String;` |
| `API-2f2fe977bac5b06e` | `org.apache.geode.cache.query.Index` | `getRegion` | `()Lorg/apache/geode/cache/Region;` |
| `API-d107d75e33f158eb` | `org.apache.geode.cache.query.Index` | `getStatistics` | `()Lorg/apache/geode/cache/query/IndexStatistics;` |
| `API-5b1d0adcb6a347ab` | `org.apache.geode.cache.query.Index` | `getType` | `()Lorg/apache/geode/cache/query/IndexType;` |
| `API-eadc50f24c63ee0e` | `org.apache.geode.cache.query.Index` | `isValid` | `()Z` |
| `API-44c615a7548106ee` | `org.apache.geode.cache.query.IndexStatistics` | `<type>` | `(type)` |
| `API-24b47fda0a3ac4fe` | `org.apache.geode.cache.query.IndexStatistics` | `getNumUpdates` | `()J` |
| `API-07f206514423b549` | `org.apache.geode.cache.query.IndexStatistics` | `getNumberOfBucketIndexes` | `()I` |
| `API-b572fa78a5fd5da9` | `org.apache.geode.cache.query.IndexStatistics` | `getNumberOfBucketIndexesLong` | `()J` |
| `API-9326ebe1cdc55978` | `org.apache.geode.cache.query.IndexStatistics` | `getNumberOfKeys` | `()J` |
| `API-a32503caf42ebbbb` | `org.apache.geode.cache.query.IndexStatistics` | `getNumberOfMapIndexKeys` | `()J` |
| `API-25c259250e637f87` | `org.apache.geode.cache.query.IndexStatistics` | `getNumberOfValues` | `()J` |
| `API-f8778c89ab13ae81` | `org.apache.geode.cache.query.IndexStatistics` | `getNumberOfValues` | `(Ljava/lang/Object;)J` |
| `API-acfd6a43fe76296f` | `org.apache.geode.cache.query.IndexStatistics` | `getReadLockCount` | `()I` |
| `API-e6b70404eafb2103` | `org.apache.geode.cache.query.IndexStatistics` | `getReadLockCountLong` | `()J` |
| `API-9373a36f488d5edd` | `org.apache.geode.cache.query.IndexStatistics` | `getTotalUpdateTime` | `()J` |
| `API-f1ab1e30bab84790` | `org.apache.geode.cache.query.IndexStatistics` | `getTotalUses` | `()J` |
| `API-564440f8055e501e` | `org.apache.geode.cache.query.IndexType` | `<type>` | `(type)` |
| `API-9fc23c3907ebf729` | `org.apache.geode.cache.query.IndexType` | `FUNCTIONAL` | `Lorg/apache/geode/cache/query/IndexType;` |
| `API-e968679805325b93` | `org.apache.geode.cache.query.IndexType` | `HASH` | `Lorg/apache/geode/cache/query/IndexType;` |
| `API-a2d0079db375fd7c` | `org.apache.geode.cache.query.IndexType` | `PRIMARY_KEY` | `Lorg/apache/geode/cache/query/IndexType;` |
| `API-7d7b059bfc1f09ff` | `org.apache.geode.cache.query.IndexType` | `getName` | `()Ljava/lang/String;` |
| `API-894375abb9d9b635` | `org.apache.geode.cache.query.IndexType` | `valueOf` | `(Ljava/lang/String;)Lorg/apache/geode/cache/query/IndexType;` |
| `API-8607377b680611c6` | `org.apache.geode.cache.query.IndexType` | `valueOfSynonym` | `(Ljava/lang/String;)Lorg/apache/geode/cache/query/IndexType;` |
| `API-6918923f3f43e1fc` | `org.apache.geode.cache.query.IndexType` | `values` | `()[Lorg/apache/geode/cache/query/IndexType;` |
| `API-f2ce54a0fca0d120` | `org.apache.geode.cache.query.QueryService` | `clearDefinedIndexes` | `()Z` |
| `API-6aea9197f669298f` | `org.apache.geode.cache.query.QueryService` | `createDefinedIndexes` | `()Ljava/util/List;` |
| `API-e8b046d474769131` | `org.apache.geode.cache.query.QueryService` | `createHashIndex` | `(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Lorg/apache/geode/cache/query/Index;` |
| `API-2087e379babef6b1` | `org.apache.geode.cache.query.QueryService` | `createHashIndex` | `(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Lorg/apache/geode/cache/query/Index;` |
| `API-643ba1f9d185c494` | `org.apache.geode.cache.query.QueryService` | `createIndex` | `(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Lorg/apache/geode/cache/query/Index;` |
| `API-75d65faa5e20aa86` | `org.apache.geode.cache.query.QueryService` | `createIndex` | `(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Lorg/apache/geode/cache/query/Index;` |
| `API-726e0b955a5ee1e1` | `org.apache.geode.cache.query.QueryService` | `createIndex` | `(Ljava/lang/String;Lorg/apache/geode/cache/query/IndexType;Ljava/lang/String;Ljava/lang/String;)Lorg/apache/geode/cache/query/Index;` |
| `API-26f1b64ff1694b89` | `org.apache.geode.cache.query.QueryService` | `createIndex` | `(Ljava/lang/String;Lorg/apache/geode/cache/query/IndexType;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Lorg/apache/geode/cache/query/Index;` |
| `API-ab8bd32dc3ebb3da` | `org.apache.geode.cache.query.QueryService` | `createKeyIndex` | `(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Lorg/apache/geode/cache/query/Index;` |
| `API-64042212e8d8c44f` | `org.apache.geode.cache.query.QueryService` | `defineHashIndex` | `(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)V` |
| `API-435cac3b50e148e3` | `org.apache.geode.cache.query.QueryService` | `defineHashIndex` | `(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)V` |
| `API-6b327f913b89dbb3` | `org.apache.geode.cache.query.QueryService` | `defineIndex` | `(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)V` |
| `API-b04df7d7999cd424` | `org.apache.geode.cache.query.QueryService` | `defineIndex` | `(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)V` |
| `API-90d0bfeeb5d54385` | `org.apache.geode.cache.query.QueryService` | `defineKeyIndex` | `(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)V` |
| `API-d154bd053ca761b8` | `org.apache.geode.cache.query.QueryService` | `getIndex` | `(Lorg/apache/geode/cache/Region;Ljava/lang/String;)Lorg/apache/geode/cache/query/Index;` |
| `API-90fbacfd8499bb36` | `org.apache.geode.cache.query.QueryService` | `getIndexes` | `()Ljava/util/Collection;` |
| `API-1919ecb65f2ae469` | `org.apache.geode.cache.query.QueryService` | `getIndexes` | `(Lorg/apache/geode/cache/Region;)Ljava/util/Collection;` |
| `API-d57fba95d0d813cc` | `org.apache.geode.cache.query.QueryService` | `getIndexes` | `(Lorg/apache/geode/cache/Region;Lorg/apache/geode/cache/query/IndexType;)Ljava/util/Collection;` |
| `API-b6fe2372932ea2ee` | `org.apache.geode.cache.query.QueryService` | `removeIndex` | `(Lorg/apache/geode/cache/query/Index;)V` |
| `API-5dc9f80bb960f69d` | `org.apache.geode.cache.query.QueryService` | `removeIndexes` | `()V` |
| `API-5bce98a5f4d7f206` | `org.apache.geode.cache.query.QueryService` | `removeIndexes` | `(Lorg/apache/geode/cache/Region;)V` |

### server-function-query — 2 IDs

| API ID | Native receiver | Member | JVM descriptor |
| --- | --- | --- | --- |
| `API-1528d1af17f5537a` | `org.apache.geode.cache.query.Query` | `execute` | `(Lorg/apache/geode/cache/execute/RegionFunctionContext;)Ljava/lang/Object;` |
| `API-d074c20a19e84252` | `org.apache.geode.cache.query.Query` | `execute` | `(Lorg/apache/geode/cache/execute/RegionFunctionContext;[Ljava/lang/Object;)Ljava/lang/Object;` |

### client-query-metadata-serialization — 6 IDs

| API ID | Native receiver | Member | JVM descriptor |
| --- | --- | --- | --- |
| `API-40a8cac6889d03aa` | `org.apache.geode.cache.query.types.CollectionType` | `fromData` | `(Ljava/io/DataInput;)V` |
| `API-9ae7ae5bf743b170` | `org.apache.geode.cache.query.types.CollectionType` | `toData` | `(Ljava/io/DataOutput;)V` |
| `API-35b1f49107860628` | `org.apache.geode.cache.query.types.ObjectType` | `fromData` | `(Ljava/io/DataInput;)V` |
| `API-c42b35f29af716a7` | `org.apache.geode.cache.query.types.ObjectType` | `toData` | `(Ljava/io/DataOutput;)V` |
| `API-4c3846b01052f912` | `org.apache.geode.cache.query.types.StructType` | `fromData` | `(Ljava/io/DataInput;)V` |
| `API-782f00ebda252174` | `org.apache.geode.cache.query.types.StructType` | `toData` | `(Ljava/io/DataOutput;)V` |

## Final task-65 validation receipt — 2026-10-28

Deterministic coverage generation/check and all 10 scope-policy tests passed. Independent javap matched 372 public declarations across 21 types; inventory, ownership, source mappings and negative controls passed. The final batch changes exactly 177 rows from unresolved to 175 required-client and two excluded-server; all 3,851 API IDs and non-scope fields are unchanged. All 2,723 frozen assignments remain unique and receiver/member/descriptor-stable; task 65 has 261 classified and zero unresolved, with the other 2,462 unresolved IDs still routed to their assigned child. Required rows have active implementation/consumer owners; neither excluded row has an existing SPI mapping. No Java/build change, new runtime probe or Gradle execution was required.
