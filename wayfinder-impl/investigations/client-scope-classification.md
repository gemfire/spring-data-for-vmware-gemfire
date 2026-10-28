# Client scope classification evidence

Recorded 2026-10-23 for [task 60](../../wayfinder-issues/60-client-scope-coverage-classification.md), under the user's [approved client-only boundary](../client-only-scope.md). This separates scope from implementation; it does not claim API removal or completed client coverage.

The reviewed artifact is **10.4.0-build.3056.df98c835**. Exact receivers, members, descriptors and artifact hashes remain in [API coverage](../coverage/api-coverage.tsv) and its manifest. [Scope policy](../coverage/client-scope-policy.json) enumerates reviewed stable IDs, rather than matching future rows by package/type name. Each row retains its Spring source/reachability evidence and historical implementation status. New overloads remain unresolved until reviewed.

## Client region creation

The native `ClientRegionFactory` declares client `create`/`createSubregion` and listener, constraint, expiration, eviction, compression, storage, pool and server-region-name configuration. Its separate `ClientRegionShortcut` includes LOCAL and persistent local variants. These 38 rows are required client behavior, with native shape translated through typed SPI as needed. `GemFireConnection.createClientRegion`, `GemFireRegion.createClientSubregion`, `GemFireRegionConfiguration.apply(ClientRegionFactory, ...)`, `RegionConfigurationContract` and `NativeDistributedCallbackTests.configuredClientRegionCanRouteToADifferentlyNamedServerRegion` provide existing implementation/caller evidence, not complete acceptance for all settings. A required scope does not upgrade their implementation status.

## Client cache assembly

`ClientCacheFactory.create` returns `ClientCache`; its 40 rows describe client construction, properties, PDX and default-pool assembly. These are required capabilities under tickets 29/51/54, using injected driver creation instead of reproducing native globals. Native static lookup/version rows require equivalent typed ownership/metadata behavior, not a new static SPI registry. `GemFire104Driver.connect` already has a client path; deleting its peer branch belongs to task 61. Pool thread-local configuration remains required to have an explicit supported/unsupported contract; classification does not promise native support for every historical setting.

## Client interest and sockets

The 15 `InterestResultPolicy` rows govern client initial interest results, independently of server registration observers. `NativeInterestResultPolicyTests` verifies the constants/default and predicates; actual registration remains open under tickets 45/48. The three `SocketFactory` rows describe creation of client pool sockets. `GemFireSocketFactory` and `NativeDistributedCallbackTests.configuredSocketFactoryCreatesSocketsUsedByARealClientPool` provide native client evidence. All 18 rows remain required.

## Peer region creation

The 58 `RegionFactory` and 32 `RegionShortcut` rows describe peer creation and configuration. They are excluded under the approved boundary. Their local/constraint/PDX-related methods do not authorize removing equivalent client behavior: the client receiver's rows are independent requirements. SPI `RegionShortcut` and three `DriverConnection.createRegion` overloads plus `DriverRegion.createSubregion` still exist; task 61 removes them and migrates their retained assertions. No production removal is claimed by these scope decisions.

## Peer cache creation

The two `CacheFactory` constructors and two `create` overloads construct peer caches; four `Cache.createRegionFactory` overloads plus `createRegion`/`createVMRegion` construct peer regions. These ten exact rows are excluded. `CacheFactory` lookup/version/PDX settings and the shared/inherited `Cache` methods are deliberately unresolved rather than excluded wholesale. Their current Spring callers may need a client replacement. The enum `CacheConfiguration.Mode.PEER` and native/mock peer creation remain removal work for task 61.

## Server hosting

The reviewed server receivers are `CacheServer`, `ClientSubscriptionConfig`, `ServerLoad`, `ServerLoadProbe`, `ServerLoadProbeAdapter`, `ServerMetrics`, `ClientSession`, `InterestRegistrationEvent` and `InterestRegistrationListener`, together with four `Cache` hosting methods (`addCacheServer`, `getCacheServers`, `isServer`, `setIsServer`). Their 131 rows create/manage a server endpoint or describe server-side sessions, scheduling/load and registration observation. The approved boundary explicitly excludes these. Client pool subscriptions, client interest calls and client initial-result policy remain separate requirements. Task 59's earlier working-tree cleanup removed the unfinished server SPI; task 62 audits remaining committed references. Private native endpoints in `CallbackPeerProcess` still support real client tests.

## WAN and queue hosting

The 248 reviewed rows comprise WAN gateway sender/receiver/filter/failure/queue contracts, async event queues/listeners, gateway conflict helper/resolver and ten exact `Cache` gateway/queue methods. These require server hosting under the approved boundary. The policy deliberately does **not** exclude `EventSequenceID` or `GatewayException`: a package/name is insufficient evidence that clients cannot receive a payload or failure. Their remaining 14 retired-owner rows move to active review task 64, preserving existing implementation evidence. Timestamped events and mixed configuration/attributes also remain unresolved for individual review in task 62/64.

Follow-up, 2026-10-28: [task 66](client-region-and-callback-scope.md#server-async-event-sequence) resolves all eight EventSequenceID rows as excluded after tracing their only public payload path through server AsyncEvent/WAN infrastructure; no existing SPI or Spring use needs retirement. This is a source-backed follow-up to the original conservative decision. GatewayException remains assigned to task 76.

## Peer listener receivers

The 70 rows under `RegionMembershipListener`, `RegionRoleListener` and their two adapters are peer-listener receiver contracts. The approved scope excludes peer membership/role callbacks. General inherited callbacks under these receivers are excluded only with that receiver; their independent `CacheListener`/`CacheListenerAdapter` rows survive. `GemFireCacheListenerFactory`, native membership/role adapters and `NativeDistributedCallbackTests.realRemoteMembershipRoleChangesAndSerializedEntryEventsAreForwarded` are existing peer implementation/test evidence requiring disposition in task 62. Retained client event/serialization tests must be migrated, not discarded with the peer scenario. `DistributedMember`, `Role`, general events and callback initialization remain unresolved pending client-callability review.

## Unresolved contracts

All other rows stay unresolved and remain obligations, with active scope-review owner [task 64](../../wayfinder-issues/64-complete-client-scope-reconciliation.md). This is explicit pending classification, not an exclusion or a completed review. Mixed native `Cache`/`Region`/attributes, transaction callbacks, shared exceptions, functions, snapshots, security, resource managers and internal boundaries need member-level evidence. Persistent local data-policy values must survive where clients use them. Unknown source references without an API row likewise remain unresolved with task 64 ownership.

The final gate must have **zero unresolved rows**. Until then, report required-client, excluded-server and unresolved counts separately; unresolved rows must never reduce the remaining required work. Source classification follows its linked *type* row conservatively and therefore does not infer the scope of individual method calls. Existing SPI mappings list all linked scopes, exposing mixed mappings without declaring them implemented or removed.

## Validation boundary

Class-file extraction and independent `javap` checks verify the recorded native declarations. Scope decisions combine those declarations, existing local implementation/test evidence and the approved product boundary; they do not establish runtime client support for unresolved members. No runtime code/tests are deleted by task 60. Tasks 61–64 must provide the removal ledger, client regressions, Spring compilation and final reconciled inventory before task 59 closes.

## Cache/region assembly follow-up — 2026-10-28

[Task 67's member-level review](client-cache-and-region-assembly-scope.md) classifies 275 client requirements and 57 server exclusions without shrinking the frozen inventory. Inherited cache/service access remains client-applicable; peer builders, WAN senders, peer lock/search/rejoin and partition hosting do not. Legacy MirrorType/persist-backup conversions retain client-local storage/persistence semantics. Shared eviction/declarable values are retained through the public EvictionAttributes conversion path, not merely shared schema reachability. Ordinary client XML remains required. No excluded row has an existing SPI mapping; unfinished implementation and adoption stay with active tasks 19/29/30/35/49.
