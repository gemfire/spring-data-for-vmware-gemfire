# Region and callback client scope — 2026-10-28

[Task 66](../../wayfinder-issues/66-audit-region-and-callback-client-scope.md) is bounded to **137 assigned IDs across nine native types**: **129 required-client / eight excluded-server / zero unresolved**. This completes scope classification, not outstanding SPI or Spring implementation. No runtime probe or Java/build change was needed.

Evidence uses GemFire **10.4.0-build.3056.df98c835**, pinned source commit `df98c8357afe85853d9a4776255d4f0e7cf064cb` in `/Users/uk016635/projects/gemfire`, read with `git show`/`git grep` against that commit. Paths below are relative to `gemfire-core/src/main/java/org/apache/geode/`. The exact ID ledger includes constructors, constants, overloads and inherited members; no type-wide rule decides unassigned members.

## client-region-operations-and-values

**94 IDs.** `cache/Region` extends ConcurrentMap and exposes client-local and pool-backed region behavior. `internal/cache/LocalRegion` implements CRUD, callback arguments, bulk operations, local/global invalidate/destroy/clear/close, collection views, entry lookup and hierarchy. `containsKeyOnServer`, `keySetOnServer`, `sizeOnServer`, `sizeLongOnServer` and `isEmptyOnServer` use ServerRegionProxy and reject regions without a pool; they inspect a remote region from a client and do not host servers. Keep these distinct from the client's local size/content. `getCache`/`getRegionService` become typed owner/service access, not permission to create peer caches.

`internal/cache/NonTXEntry` implements Region.Entry: entry lookup does not invoke a loader; invalid values read as null; destroyed entries fail. Entry observations can resolve a recreated key until destruction has been observed, so do not replace native behavior with an immutable identity snapshot. Preserve entry mutation, user attributes, isLocal, equality/hash and owning typed region. `internal/cache/AbstractRegion.getStatistics` checks readiness before statistics enablement; `CacheStatisticsImpl` preserves entry failure order, counters, reset and timestamps, including zero ratio when there are no hits/misses. Retain actual region/subregion aggregation and sampling behavior, not an atomic statistics snapshot promise.

Reuse [region-operation evidence](region-operations-and-metadata.md) and its shared/native tests for map defaults, conditional mutation, bulk callback/event operation tags, live views, invalid entries, entry recreation, statistics and remote inspection. Existing `DriverRegion`, `DriverRegionEntry`, `DriverCacheStatistics` and typed service mappings remain intact. Mock operations must preserve supported local semantics and reject unavailable network behavior explicitly. `closeRegion` keeps explicit ownership rather than introducing Spring inferred bean destruction.

The 83 implemented rows retain foundation task 18 as historical implementation owner and task 31 as active consumer owner. The 11 partial rows (Region/Entry types, entrySet overloads and reachable entry/region/service/hierarchy handles) move from resolved task 18 to active task 31. Task 31 owns completion/adoption with configuration, query, service, snapshot and interest collaborators 19/21/29/44/45; task 30 owns factory/configuration wiring. No implementation status is upgraded.

## client-region-interest

**24 IDs.** `LocalRegion.registerInterest/registerInterestRegex/processSingleInterest`, unregister and list-inspection methods use the pool/server proxy. Preserve default policy, non-durable default, receive-values default, durable-client/subscription validation, null/invalid input and unsupported no-pool behavior. Initial registration can replace affected local entries according to policy. receiveValues=false requests update invalidations; it is not permission to discard all notifications.

`Region.registerInterestForAllKeys` delegates to regex `.*`. Crucially, `registerInterestForKeys` iterates and invokes registration per key, including its overload flags: do not turn it into one atomic list operation or erase partial progress on failure. Legacy registerInterest(List) and ALL_KEYS paths are separate LocalRegion dispatch. Preserve key and regex registration lists, unregister semantics and durable lifecycle.

Task 45 owns all 24 missing implementations; task 31 is the matrix consumer owner and task 30 owns factory interest wiring. Coordinate policy task 48 and pool/durable task 20 (audit 69) without taking their IDs. Existing task 45 native delivery/unregister acceptance remains required; the local mock must not invent a remote stream. Source suffices for scope, so no new probe is needed here.

## client-attribute-subregions

**One ID.** `cache/client/internal/ClientRegionFactoryImpl.createSubregion` calls `InternalRegion.createSubregion(name, createRegionAttributes())`. `LocalRegion.createSubregion` validates readiness, region names, existing child/server mappings and attributes, then constructs a local child for local attributes. `validateSubregionAttributes` rejects null attributes and nonlocal child scope under a local parent. Native attribute-copy subregion creation is therefore client-applicable even though the same overload also has peer implementation paths.

Task 30 now owns this missing row and its consumer migration, with configuration collaborators 19/29/47. Provide a native-free equivalent for **client-compatible attribute-copy creation**, including explicit/omitted settings, callback identity, client-local settings, pool and server-region routing, and native validation/failures. Existing typed shortcut/configuration creation is useful but does not establish full equivalence. Do not restore RegionShortcut, peer creation, native RegionAttributes signatures or unwrap. Task 67 audits the separately assigned attribute/factory members; it does not acquire this ID. Native/mock parity tests belong to implementation acceptance, not another scope probe.

## client-callback-lifecycle

**Seven IDs.** `cache/CacheCallback` extends `Declarable`; close, legacy init(Properties) and initialize(Cache,Properties) have distinct default no-op contracts. `internal/cache/xmlcache/CacheCreation.initializeDeclarablesMap` calls initialize then init; `ClientCacheCreation.create` uses that path. Programmatic callback registration alone does not invoke declarative initialization. Preserve typed client owner, properties, hook ordering, failures, instance identity and cleanup; no peer Cache is exposed by adapting initialize's argument.

Reuse [callback contracts](region-callback-contracts.md) and [client-only callback validation](client-only-callbacks-and-configuration.md), including client loader/writer lifecycle and retained client XML tests. The three implemented close/init rows retain foundation task 17 history. Four partial type/initialize rows move from resolved task 17 to active task 30 for complete typed assembly and Spring XML/annotation/configurer adoption, with task 29's cache assembly collaborator. Existing SPI mappings remain unchanged.

## client-transaction-access

**Three inherited receiver IDs.** `internal/cache/GemFireCacheImpl.getCacheTransactionManager` returns the cache transaction manager without a client exclusion. Cache, GemFireCache and ClientCache receiver rows remain required and map to connection-owned typed transaction access. Preserve manager ownership, lifecycle and association with the correct client; do not substitute a static/global service.

The three partial rows move from resolved foundation task 28 to active task 34. Reuse the [validated client-local transaction outcomes](client-local-transaction-outcomes.md) rather than repeating probes. Task 34 owns Spring transaction/configuration adoption and remaining partial behavior; task 37 owns native-free test collaborators. Server writer/distributed-mode exclusions remain intact. The mock currently rejects transaction operations explicitly; consumer tests requiring actual transactions need implementation before acceptance, not skipped assertions.

## server-async-event-sequence

**Eight IDs**, including the type, byte-array/long/long constructor, three getters, equals/hashCode and toString. A pinned repository-wide production-source search for EventSequenceID finds only the value class, `cache/asyncqueue/AsyncEvent.getEventSequenceID` and `internal/cache/wan/GatewaySenderEventImpl.getEventSequenceID`. The latter constructs it from internal EventID membership/thread/sequence components for server queue events. Client EntryEvent and CQ callbacks do not expose this value. The conclusion is based on the actual public payload path, not the `wan` package name.

The constructor decodes member bytes (falling back to their array string) and stores sequence/thread values; equality/hash/string are observations of that server-event identity. Public constructibility alone does not make this a retained GemFire client callback contract. The associated async queue/WAN hosting path is already excluded. All eight rows are missing with empty SPI mappings, and the existing-SPI comparison maps none of them. A search of current Spring/driver sources finds no EventSequenceID reference to retire. No public implementation or retained client assertion is removed. Keep native declarations and missing status as historical availability evidence; exclusion is not native removal. Shared GatewayException scope stays in task 76.

## Reconciliation and handoff

The fixed assignment ledger is unchanged. Type-linked source/resource dispositions are regenerated conservatively; mixed Region/Cache receivers do not exclude their other members. Existing SPI mappings retain implementation/test evidence and now report reviewed scopes. Eighteen partial rows move to active tasks 30/31/34, and the one missing attribute-copy row moves to task 30; all other required missing rows already belong to active task 45. Implemented rows retain historical foundation ownership with active consumer acceptance. Eight excluded rows gain an explicit no-client-work remaining disposition, without changing their status or mappings.

There are no unanswered scope questions in this family. Whole inventory is **887 required-client / 639 excluded-server / 2,325 unresolved**; implementation totals remain **3,111 missing / 484 partial / 256 implemented**. Tasks 59/64 remain open; task 67 is next with its fixed 332 IDs.

## Frozen member ledger

### client-region-operations-and-values — 94 IDs

| API ID | Native receiver | Member | JVM descriptor |
| --- | --- | --- | --- |
| `API-63d2c04d5bfbd37b` | `org.apache.geode.cache.CacheStatistics` | `<type>` | `(type)` |
| `API-c99304a743d81b05` | `org.apache.geode.cache.CacheStatistics` | `getHitCount` | `()J` |
| `API-048f288067bf872e` | `org.apache.geode.cache.CacheStatistics` | `getHitRatio` | `()F` |
| `API-8708b371fd9feb1c` | `org.apache.geode.cache.CacheStatistics` | `getLastAccessedTime` | `()J` |
| `API-4f0e5a56bad2fe23` | `org.apache.geode.cache.CacheStatistics` | `getLastModifiedTime` | `()J` |
| `API-574375504b242827` | `org.apache.geode.cache.CacheStatistics` | `getMissCount` | `()J` |
| `API-0390ae9ec1ac59ef` | `org.apache.geode.cache.CacheStatistics` | `resetCounts` | `()V` |
| `API-c7a97c486ab13f6b` | `org.apache.geode.cache.Region` | `<type>` | `(type)` |
| `API-2a4d7c7d71a8a543` | `org.apache.geode.cache.Region` | `SEPARATOR` | `Ljava/lang/String;` |
| `API-172da0f2cc416a5a` | `org.apache.geode.cache.Region` | `SEPARATOR_CHAR` | `C` |
| `API-110da928068b0310` | `org.apache.geode.cache.Region` | `clear` | `()V` |
| `API-4c0b94e43e34c26e` | `org.apache.geode.cache.Region` | `close` | `()V` |
| `API-497384bef31ce128` | `org.apache.geode.cache.Region` | `compute` | `(Ljava/lang/Object;Ljava/util/function/BiFunction;)Ljava/lang/Object;` |
| `API-49cb80c1f4e4466e` | `org.apache.geode.cache.Region` | `computeIfAbsent` | `(Ljava/lang/Object;Ljava/util/function/Function;)Ljava/lang/Object;` |
| `API-e0cbb2b1956bc326` | `org.apache.geode.cache.Region` | `computeIfPresent` | `(Ljava/lang/Object;Ljava/util/function/BiFunction;)Ljava/lang/Object;` |
| `API-eeb2ac690535d581` | `org.apache.geode.cache.Region` | `containsKey` | `(Ljava/lang/Object;)Z` |
| `API-31361d230e5eacf7` | `org.apache.geode.cache.Region` | `containsKeyOnServer` | `(Ljava/lang/Object;)Z` |
| `API-49d941255fbef4ac` | `org.apache.geode.cache.Region` | `containsValue` | `(Ljava/lang/Object;)Z` |
| `API-879d0a4e1d954467` | `org.apache.geode.cache.Region` | `containsValueForKey` | `(Ljava/lang/Object;)Z` |
| `API-872d07495165318b` | `org.apache.geode.cache.Region` | `create` | `(Ljava/lang/Object;Ljava/lang/Object;)V` |
| `API-1099e15a2ac6cecc` | `org.apache.geode.cache.Region` | `create` | `(Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;)V` |
| `API-430c031c4b4d9aef` | `org.apache.geode.cache.Region` | `destroy` | `(Ljava/lang/Object;)Ljava/lang/Object;` |
| `API-4e2bd07952716f3c` | `org.apache.geode.cache.Region` | `destroy` | `(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;` |
| `API-432a078820157068` | `org.apache.geode.cache.Region` | `destroyRegion` | `()V` |
| `API-7416a36eb08c1693` | `org.apache.geode.cache.Region` | `destroyRegion` | `(Ljava/lang/Object;)V` |
| `API-d3ebe537875890cf` | `org.apache.geode.cache.Region` | `entrySet` | `()Ljava/util/Set;` |
| `API-eb2980879b1a8988` | `org.apache.geode.cache.Region` | `entrySet` | `(Z)Ljava/util/Set;` |
| `API-bbaf921bd53c2f24` | `org.apache.geode.cache.Region` | `equals` | `(Ljava/lang/Object;)Z` |
| `API-22c56ee6b33d4ccb` | `org.apache.geode.cache.Region` | `forEach` | `(Ljava/util/function/BiConsumer;)V` |
| `API-ffd1a570199440b2` | `org.apache.geode.cache.Region` | `get` | `(Ljava/lang/Object;)Ljava/lang/Object;` |
| `API-792c5c618bdb67ee` | `org.apache.geode.cache.Region` | `get` | `(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;` |
| `API-0ad3dcacde6b2217` | `org.apache.geode.cache.Region` | `getAll` | `(Ljava/util/Collection;)Ljava/util/Map;` |
| `API-dbba7d2fbbf4eab4` | `org.apache.geode.cache.Region` | `getAll` | `(Ljava/util/Collection;Ljava/lang/Object;)Ljava/util/Map;` |
| `API-21661048d3c5dee9` | `org.apache.geode.cache.Region` | `getCache` | `()Lorg/apache/geode/cache/Cache;` |
| `API-4b6a1dd6e2a5d2c8` | `org.apache.geode.cache.Region` | `getEntry` | `(Ljava/lang/Object;)Lorg/apache/geode/cache/Region$Entry;` |
| `API-e170723315276ff8` | `org.apache.geode.cache.Region` | `getFullPath` | `()Ljava/lang/String;` |
| `API-749e118b77b5967e` | `org.apache.geode.cache.Region` | `getName` | `()Ljava/lang/String;` |
| `API-369873a36cb32747` | `org.apache.geode.cache.Region` | `getOrDefault` | `(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;` |
| `API-926d60e554a3581d` | `org.apache.geode.cache.Region` | `getParentRegion` | `()Lorg/apache/geode/cache/Region;` |
| `API-980707f4fa3c7471` | `org.apache.geode.cache.Region` | `getRegionService` | `()Lorg/apache/geode/cache/RegionService;` |
| `API-f47b827edcc452b3` | `org.apache.geode.cache.Region` | `getStatistics` | `()Lorg/apache/geode/cache/CacheStatistics;` |
| `API-9c8c55613c56e26b` | `org.apache.geode.cache.Region` | `getSubregion` | `(Ljava/lang/String;)Lorg/apache/geode/cache/Region;` |
| `API-d68300c767720a86` | `org.apache.geode.cache.Region` | `getUserAttribute` | `()Ljava/lang/Object;` |
| `API-9dc3986a245ae3da` | `org.apache.geode.cache.Region` | `hashCode` | `()I` |
| `API-aa7156d4992c49d9` | `org.apache.geode.cache.Region` | `invalidate` | `(Ljava/lang/Object;)V` |
| `API-9fbae92ea8f63c3d` | `org.apache.geode.cache.Region` | `invalidate` | `(Ljava/lang/Object;Ljava/lang/Object;)V` |
| `API-05fe0a1bdde7548b` | `org.apache.geode.cache.Region` | `invalidateRegion` | `()V` |
| `API-c686801b7e8488b0` | `org.apache.geode.cache.Region` | `invalidateRegion` | `(Ljava/lang/Object;)V` |
| `API-a757c7e7454d5d4a` | `org.apache.geode.cache.Region` | `isDestroyed` | `()Z` |
| `API-94ba153ad8cf423f` | `org.apache.geode.cache.Region` | `isEmpty` | `()Z` |
| `API-afa0aa28aa4dfeb8` | `org.apache.geode.cache.Region` | `isEmptyOnServer` | `()Z` |
| `API-eb827b31065ce88f` | `org.apache.geode.cache.Region` | `keySet` | `()Ljava/util/Set;` |
| `API-588b5c8746daf911` | `org.apache.geode.cache.Region` | `keySetOnServer` | `()Ljava/util/Set;` |
| `API-806d50c16b3990a0` | `org.apache.geode.cache.Region` | `localClear` | `()V` |
| `API-317080dece24baf5` | `org.apache.geode.cache.Region` | `localDestroy` | `(Ljava/lang/Object;)V` |
| `API-27c61594616edd90` | `org.apache.geode.cache.Region` | `localDestroy` | `(Ljava/lang/Object;Ljava/lang/Object;)V` |
| `API-addc53b71e651c01` | `org.apache.geode.cache.Region` | `localDestroyRegion` | `()V` |
| `API-a9126d46360cb22c` | `org.apache.geode.cache.Region` | `localDestroyRegion` | `(Ljava/lang/Object;)V` |
| `API-284a6b588bf69d74` | `org.apache.geode.cache.Region` | `localInvalidate` | `(Ljava/lang/Object;)V` |
| `API-f7a0f5a87cd47842` | `org.apache.geode.cache.Region` | `localInvalidate` | `(Ljava/lang/Object;Ljava/lang/Object;)V` |
| `API-2064735e08c91ff7` | `org.apache.geode.cache.Region` | `localInvalidateRegion` | `()V` |
| `API-3fc9faaa7ac587db` | `org.apache.geode.cache.Region` | `localInvalidateRegion` | `(Ljava/lang/Object;)V` |
| `API-e324f7bd117726b9` | `org.apache.geode.cache.Region` | `merge` | `(Ljava/lang/Object;Ljava/lang/Object;Ljava/util/function/BiFunction;)Ljava/lang/Object;` |
| `API-9e3f6a0986546e2e` | `org.apache.geode.cache.Region` | `put` | `(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;` |
| `API-251a52420730f966` | `org.apache.geode.cache.Region` | `put` | `(Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;` |
| `API-c861ca345d0646d3` | `org.apache.geode.cache.Region` | `putAll` | `(Ljava/util/Map;)V` |
| `API-ed535754c41ae9b1` | `org.apache.geode.cache.Region` | `putAll` | `(Ljava/util/Map;Ljava/lang/Object;)V` |
| `API-d847f0f22c313ef0` | `org.apache.geode.cache.Region` | `putIfAbsent` | `(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;` |
| `API-ed57451fb49afefb` | `org.apache.geode.cache.Region` | `remove` | `(Ljava/lang/Object;)Ljava/lang/Object;` |
| `API-f6000d53504eb594` | `org.apache.geode.cache.Region` | `remove` | `(Ljava/lang/Object;Ljava/lang/Object;)Z` |
| `API-c705f9bb1b029070` | `org.apache.geode.cache.Region` | `removeAll` | `(Ljava/util/Collection;)V` |
| `API-0c357bdc6dcd01f8` | `org.apache.geode.cache.Region` | `removeAll` | `(Ljava/util/Collection;Ljava/lang/Object;)V` |
| `API-895892ec82c269f8` | `org.apache.geode.cache.Region` | `replace` | `(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;` |
| `API-8e24958650f32a8b` | `org.apache.geode.cache.Region` | `replace` | `(Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;)Z` |
| `API-78f1a4b29fa5e280` | `org.apache.geode.cache.Region` | `replaceAll` | `(Ljava/util/function/BiFunction;)V` |
| `API-7c8a4f6d99be55c8` | `org.apache.geode.cache.Region` | `setUserAttribute` | `(Ljava/lang/Object;)V` |
| `API-21e273ccccbb2406` | `org.apache.geode.cache.Region` | `size` | `()I` |
| `API-b09d537e9b519140` | `org.apache.geode.cache.Region` | `sizeLong` | `()J` |
| `API-0c38acacacfe4999` | `org.apache.geode.cache.Region` | `sizeLongOnServer` | `()J` |
| `API-2f12a70b4d78a32c` | `org.apache.geode.cache.Region` | `sizeOnServer` | `()I` |
| `API-d54d859dacc51b19` | `org.apache.geode.cache.Region` | `subregions` | `(Z)Ljava/util/Set;` |
| `API-396d02611b12fdd2` | `org.apache.geode.cache.Region` | `values` | `()Ljava/util/Collection;` |
| `API-dd24873ff2519c07` | `org.apache.geode.cache.Region$Entry` | `<type>` | `(type)` |
| `API-f1908e5b4e45eebd` | `org.apache.geode.cache.Region$Entry` | `equals` | `(Ljava/lang/Object;)Z` |
| `API-577843f8d76863cc` | `org.apache.geode.cache.Region$Entry` | `getKey` | `()Ljava/lang/Object;` |
| `API-5f00e9bad7b36b82` | `org.apache.geode.cache.Region$Entry` | `getRegion` | `()Lorg/apache/geode/cache/Region;` |
| `API-2667d84fc0393ead` | `org.apache.geode.cache.Region$Entry` | `getStatistics` | `()Lorg/apache/geode/cache/CacheStatistics;` |
| `API-7aa67416e6eff7c2` | `org.apache.geode.cache.Region$Entry` | `getUserAttribute` | `()Ljava/lang/Object;` |
| `API-d3653a50a2715751` | `org.apache.geode.cache.Region$Entry` | `getValue` | `()Ljava/lang/Object;` |
| `API-eee7a5141f8837c0` | `org.apache.geode.cache.Region$Entry` | `hashCode` | `()I` |
| `API-b15958a3e38ad5a8` | `org.apache.geode.cache.Region$Entry` | `isDestroyed` | `()Z` |
| `API-d3d8ef86fb5c7c0a` | `org.apache.geode.cache.Region$Entry` | `isLocal` | `()Z` |
| `API-1fa011464c284d76` | `org.apache.geode.cache.Region$Entry` | `setUserAttribute` | `(Ljava/lang/Object;)Ljava/lang/Object;` |
| `API-20e444dbb5467c45` | `org.apache.geode.cache.Region$Entry` | `setValue` | `(Ljava/lang/Object;)Ljava/lang/Object;` |

### client-region-interest — 24 IDs

| API ID | Native receiver | Member | JVM descriptor |
| --- | --- | --- | --- |
| `API-562d56f28f57dd6c` | `org.apache.geode.cache.Region` | `getInterestList` | `()Ljava/util/List;` |
| `API-25e01d9228f6c776` | `org.apache.geode.cache.Region` | `getInterestListRegex` | `()Ljava/util/List;` |
| `API-3d6a6b700b3bcce4` | `org.apache.geode.cache.Region` | `registerInterest` | `(Ljava/lang/Object;)V` |
| `API-931f94bf71363132` | `org.apache.geode.cache.Region` | `registerInterest` | `(Ljava/lang/Object;Lorg/apache/geode/cache/InterestResultPolicy;)V` |
| `API-c3f12004061156d5` | `org.apache.geode.cache.Region` | `registerInterest` | `(Ljava/lang/Object;Lorg/apache/geode/cache/InterestResultPolicy;Z)V` |
| `API-332eb20412e57326` | `org.apache.geode.cache.Region` | `registerInterest` | `(Ljava/lang/Object;Lorg/apache/geode/cache/InterestResultPolicy;ZZ)V` |
| `API-3b889cc88626cad9` | `org.apache.geode.cache.Region` | `registerInterest` | `(Ljava/lang/Object;Z)V` |
| `API-0b1d3eed6b1df875` | `org.apache.geode.cache.Region` | `registerInterest` | `(Ljava/lang/Object;ZZ)V` |
| `API-edd13f00491cb8e8` | `org.apache.geode.cache.Region` | `registerInterestForAllKeys` | `()V` |
| `API-098d00754e0a2358` | `org.apache.geode.cache.Region` | `registerInterestForAllKeys` | `(Lorg/apache/geode/cache/InterestResultPolicy;)V` |
| `API-7c8ab1fde34617bf` | `org.apache.geode.cache.Region` | `registerInterestForAllKeys` | `(Lorg/apache/geode/cache/InterestResultPolicy;Z)V` |
| `API-eca8a3c22dfa36fe` | `org.apache.geode.cache.Region` | `registerInterestForAllKeys` | `(Lorg/apache/geode/cache/InterestResultPolicy;ZZ)V` |
| `API-0703aebc882601c8` | `org.apache.geode.cache.Region` | `registerInterestForKeys` | `(Ljava/lang/Iterable;)V` |
| `API-8a6c84f065981f56` | `org.apache.geode.cache.Region` | `registerInterestForKeys` | `(Ljava/lang/Iterable;Lorg/apache/geode/cache/InterestResultPolicy;)V` |
| `API-5c1290433a9a0d23` | `org.apache.geode.cache.Region` | `registerInterestForKeys` | `(Ljava/lang/Iterable;Lorg/apache/geode/cache/InterestResultPolicy;Z)V` |
| `API-e336a93ed7cfd9da` | `org.apache.geode.cache.Region` | `registerInterestForKeys` | `(Ljava/lang/Iterable;Lorg/apache/geode/cache/InterestResultPolicy;ZZ)V` |
| `API-e845b9d3dad5120b` | `org.apache.geode.cache.Region` | `registerInterestRegex` | `(Ljava/lang/String;)V` |
| `API-d0d13749ef25b23e` | `org.apache.geode.cache.Region` | `registerInterestRegex` | `(Ljava/lang/String;Lorg/apache/geode/cache/InterestResultPolicy;)V` |
| `API-88db3a7dcf852f2d` | `org.apache.geode.cache.Region` | `registerInterestRegex` | `(Ljava/lang/String;Lorg/apache/geode/cache/InterestResultPolicy;Z)V` |
| `API-5b7d7bee7e43cf16` | `org.apache.geode.cache.Region` | `registerInterestRegex` | `(Ljava/lang/String;Lorg/apache/geode/cache/InterestResultPolicy;ZZ)V` |
| `API-d31c180169f5dfbf` | `org.apache.geode.cache.Region` | `registerInterestRegex` | `(Ljava/lang/String;Z)V` |
| `API-2284fb5129bd0ba7` | `org.apache.geode.cache.Region` | `registerInterestRegex` | `(Ljava/lang/String;ZZ)V` |
| `API-143d6150f691f8f1` | `org.apache.geode.cache.Region` | `unregisterInterest` | `(Ljava/lang/Object;)V` |
| `API-5957ba32ea54634e` | `org.apache.geode.cache.Region` | `unregisterInterestRegex` | `(Ljava/lang/String;)V` |

### client-attribute-subregions — 1 IDs

| API ID | Native receiver | Member | JVM descriptor |
| --- | --- | --- | --- |
| `API-30da94e001d7cb96` | `org.apache.geode.cache.Region` | `createSubregion` | `(Ljava/lang/String;Lorg/apache/geode/cache/RegionAttributes;)Lorg/apache/geode/cache/Region;` |

### client-callback-lifecycle — 7 IDs

| API ID | Native receiver | Member | JVM descriptor |
| --- | --- | --- | --- |
| `API-80c52edb599d7350` | `org.apache.geode.cache.CacheCallback` | `<type>` | `(type)` |
| `API-0e699457e2cc0c46` | `org.apache.geode.cache.CacheCallback` | `close` | `()V` |
| `API-543d346c620eb789` | `org.apache.geode.cache.CacheCallback` | `init` | `(Ljava/util/Properties;)V` |
| `API-8e9ec4b2ea30f5b8` | `org.apache.geode.cache.CacheCallback` | `initialize` | `(Lorg/apache/geode/cache/Cache;Ljava/util/Properties;)V` |
| `API-8ebc346fd1c32024` | `org.apache.geode.cache.Declarable` | `<type>` | `(type)` |
| `API-b3a0e30354c877e6` | `org.apache.geode.cache.Declarable` | `init` | `(Ljava/util/Properties;)V` |
| `API-cad0f2fdf487683f` | `org.apache.geode.cache.Declarable` | `initialize` | `(Lorg/apache/geode/cache/Cache;Ljava/util/Properties;)V` |

### client-transaction-access — 3 IDs

| API ID | Native receiver | Member | JVM descriptor |
| --- | --- | --- | --- |
| `API-f00c22384a1e5bf0` | `org.apache.geode.cache.Cache` | `getCacheTransactionManager` | `()Lorg/apache/geode/cache/CacheTransactionManager;` |
| `API-1cd0aca39939890c` | `org.apache.geode.cache.GemFireCache` | `getCacheTransactionManager` | `()Lorg/apache/geode/cache/CacheTransactionManager;` |
| `API-c00abfc92512dd69` | `org.apache.geode.cache.client.ClientCache` | `getCacheTransactionManager` | `()Lorg/apache/geode/cache/CacheTransactionManager;` |

### server-async-event-sequence — 8 IDs

| API ID | Native receiver | Member | JVM descriptor |
| --- | --- | --- | --- |
| `API-b0ba7cc5cdd9e628` | `org.apache.geode.cache.wan.EventSequenceID` | `<init>` | `([BJJ)V` |
| `API-77325592eabf6ce7` | `org.apache.geode.cache.wan.EventSequenceID` | `<type>` | `(type)` |
| `API-5791e94433c0759a` | `org.apache.geode.cache.wan.EventSequenceID` | `equals` | `(Ljava/lang/Object;)Z` |
| `API-f4e9e00ec8dbb40b` | `org.apache.geode.cache.wan.EventSequenceID` | `getMembershipID` | `()Ljava/lang/String;` |
| `API-efe6053eeea874df` | `org.apache.geode.cache.wan.EventSequenceID` | `getSequenceID` | `()J` |
| `API-9ac2521573ff8d77` | `org.apache.geode.cache.wan.EventSequenceID` | `getThreadID` | `()J` |
| `API-11cf1e2e3e3a3b9a` | `org.apache.geode.cache.wan.EventSequenceID` | `hashCode` | `()I` |
| `API-fb3234c59b14408c` | `org.apache.geode.cache.wan.EventSequenceID` | `toString` | `()Ljava/lang/String;` |

## Validation receipt — 2026-10-28

Deterministic coverage generation/check and all 10 scope-policy tests passed. Independent javap matched 372 public declarations across 21 native types; inventory, ownership, source mapping and negative controls passed (3,851 API rows, 133 production imports, 549 source/resource references, 509 Spring exposures). Assignment checks preserve all 2,723 unique frozen IDs and receiver/member/descriptors: 398 classified and 2,325 unresolved, each unresolved row still routed to its assigned child. Exactly 137 rows changed from unresolved; outside scope fields, only 19 implementation owners and nine remaining-work descriptions changed, as explicitly recorded above. Implementation statuses, native declarations and SPI/native/mock/test mappings are unchanged. Required unfinished rows and consumers have active owners; none of the eight exclusions has an existing SPI mapping. Source dispositions are 273 required-client / three excluded-server / 273 unresolved; existing SPI scope dispositions are 125 required-client / 24 unresolved. The 77-task dependency graph is acyclic. No new runtime probe, Java/build change or Gradle execution was needed.
