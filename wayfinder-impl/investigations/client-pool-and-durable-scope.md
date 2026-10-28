# Client pool and durable scope — 2026-10-28

[Task 69](../../wayfinder-issues/69-audit-pool-and-durable-client-scope.md) settles **101 assigned IDs across four native types: 101 required-client / zero excluded-server / zero unresolved**. This closes classification only; implementation statuses and existing mappings remain unchanged.

Native evidence: GemFire **10.4.0-build.3056.df98c835**, source commit `df98c8357afe85853d9a4776255d4f0e7cf064cb` in `/Users/uk016635/projects/gemfire`, inspected with git show. Native paths below are relative to `gemfire-core/src/main/java/org/apache/geode/`; Spring consumers are under `org.springframework.data.gemfire`. Each batch applies only to the fixed exact IDs listed below. Existing socket evidence is reused; no Java/build change or new runtime scope probe was needed.

## client-pool-settings

**53 IDs — required-client.** `cache/client/Pool`, `PoolFactory`, `internal/cache/PoolFactoryImpl` and `cache/client/internal/PoolImpl` supply configuration setters and effective getters. Retain name, configured locator/server lists, connection limits, timeouts/intervals, subscription controls, routing, group/zone, multiuser mode and socket selection. Deprecated global min/max settings remain callable alongside per-server limits. Validation and inter-setting restrictions belong to the versioned driver: explicit serverConnectionTimeout=0 differs from omission, whose effective value can derive from freeConnectionTimeout for a bounded single-hop pool. Null group/zone normalizes to the native empty default. Configured endpoints are distinct from live topology.

Task 50's [validated foundation](named-pool-configuration-and-sockets.md) already provides optional typed configuration, effective snapshots, callback identity and real socket transport coverage. Preserve those results and its endpoint override/inheritance rules. Do not introduce the retired thread-local setting. Native PoolFactoryImpl.setSocketFactory assigns null without an immediate check, while the current typed builder rejects explicit null; task 20 must document and test the retained consumer policy and any translation needed. This observation does not establish successful native null-socket transport or justify another scope probe. Task 35 migrates PoolFactoryBean and PoolAdapter/DelegatingPoolAdapter/DefaultableDelegatingPoolAdapter, retaining fallback and explicit-value distinctions.

## client-pool-defaults

**26 IDs — required-client.** All 26 `PoolFactory` constants describe client configuration defaults, including IDLE_TIMEOUT_NEVER and DEFAULT_SOCKET_FACTORY. The public interface and PoolFactoryImpl.PoolAttributes establish defaults; PoolImpl establishes effective runtime values. A provider default must not be confused with an explicitly supplied constant: explicit server connection timeout zero suppresses the derived fallback, for example. Preserve version-specific defaults in driver policy and native-free consumer equivalents, without exposing a native SocketFactory constant or assuming every GemFire version uses the same numbers. This is completion of public defaults/consumer behavior, not repetition of task 50's socket implementation.

## client-pool-assembly

**5 IDs — required-client.** `PoolFactoryImpl.addLocator/addServer` rejects mixed endpoint kinds and nonpositive ports. `reset` replaces PoolAttributes (preserving only private startDisabled state); it clears endpoints and explicit-setting flags, including serverConnectionTimeoutSet. `create` constructs a PoolImpl and registers it through PoolManagerImpl; no endpoints and duplicate names fail. Creation does not implicitly reset the factory. Preserve these distinctions through typed construction/reuse rather than exporting native factories or private test-only controls. The foundation's existing named-pool and ownership behavior remains valid; task 20 completes the retained API observations and task 35 consumer adoption.

## client-pool-lifecycle

**5 IDs — required-client.** `PoolImpl.destroy()` delegates to destroy(false). destroy(keepAlive) checks attached users before unregistering and invoking basicDestroy; basicDestroy marks the pool destroyed and passes keepAlive to queue and connection shutdown. Repeated public destruction is governed by registry identity/unregistration. Private special-durable behavior is not a new public SPI requirement. The Pool type and isDestroyed observation belong with this lifecycle.

`GemFireCacheImpl.close(boolean)` passes keepAlive through cache shutdown to PoolManager.close(keepAlive). `PoolManagerImpl.close` invokes basicDestroy across its registry directly, then empties it; this differs from calling public destroy on an attached pool. Task 52 must preserve the intended cache-owned shutdown sequence, attachment failures and durable keepalive consequences while preventing closure of borrowed or other-context pools through an injected owner. Task 35 migrates ClientCacheFactoryBean/PoolFactoryBean destroy policy. Mock local lifecycle is not proof of remote queue retention.

## client-durable-readiness

**2 IDs — required-client.** `GemFireCacheImpl.readyForEvents` dispatches for a durable client through PoolManagerImpl, ensuring default-pool determination where needed. PoolManagerImpl iterates durable pools; a nonempty registry with no durable pool fails, while its empty-registry path does not. `PoolImpl.readyForEvents` returns early when nondurable or without a queue manager; otherwise it sets readyForEventsCalled and invokes QueueManagerImpl.readyForEvents. Thus a blanket claim that every readiness call without subscriptions throws would be wrong. QueueManagerImpl sends client-ready to the primary and retains readiness across retry/failover.

`PoolImpl.getPendingEventCount` rejects nondurable/no-queue use and calls after readiness. It returns the primary queue size captured on connection, not a live backlog counter. Pool Javadoc defines a stable approximate session value: zero means no queued events, -1 queue expiration after durable timeout, -2 first connection/no previous queue. Preserve negative values and failure timing. Task 52 owns real reconnect, keepalive, replay and timeout tests and explicit mock capability limits; task 35 owns Spring readiness/keepalive configuration. Scope is settled from source; implementation acceptance still needs the actual runtime effects.

## client-pool-topology

**2 IDs — required-client.** `PoolImpl.getOnlineLocators` delegates to its ConnectionSource. AutoConnectionSourceImpl returns an unmodifiable copy of online locators; ExplicitConnectionSourceImpl returns an empty list. `PoolImpl.getCurrentServers` uses current endpoint-map keys. `GemFireCacheImpl.getCurrentServers` aggregates pool observations into a set of unresolved InetSocketAddress values, empty if there are none. These are client connection observations, not peer membership or server-hosting APIs. Task 52 supplies typed endpoint views and task 35 adopts them; mock observations must reflect supported local state rather than invent a live network.

## client-default-pool-selection

**1 IDs — required-client.** `GemFireCacheImpl.getDefaultPool/determineDefaultPool` implements selection. Without an explicit factory: no pools creates a localhost/default-port factory, one pool reuses it, and multiple pools search for compatibility with that default factory; if none matches, the default is null. With an explicit factory, missing endpoints receive localhost/default port and a compatible existing pool is sought. If creation is needed, DEFAULT, DEFAULT1, etc. avoid name collisions. Compatibility selection is not a lexical ordering guarantee. `findFirstCompatiblePool`, `createDefaultPF`, `addLocalHostAsServer` and PoolImpl.isCompatible provide the source trace. Task 51 owns actual default-cache assembly/selection and task 35 the consumer cutover. Do not equate this accessor to lookup of a literal DEFAULT or gemfirePool name.

## client-pool-registry

**7 IDs — required-client.** `cache/client/PoolManager` delegates createFactory, find(name), find(region), getAll and both close overloads to PoolManagerImpl. find(region) resolves its attributes' pool name; absent name lookup returns null. getMap supplies a new mutable HashMap snapshot whose pool values remain live. close() means close(false); global close uses basicDestroy and clears the map as described above. The public static utility type is a client capability requirement, not a mandate to reproduce global static mutable SPI state. Task 52 owns scoped registry/lifecycle behavior, task 35 injects resolvers and factory dependencies, including its existing null-only fallback rules and owned/borrowed isolation.

## Ownership and reconciliation

The 84 unfinished rows previously assigned to resolved foundation 50 move to open task 20 for defaults, factory completion and consumer acceptance. Task 50 remains resolved and its socket implementation/test evidence stays intact. The other 17 rows retain active owners: 16 under task 52 and one under task 51. All 101 retain task 35 as consumer owner. No implementation status, SPI/native/mock/test mapping or native declaration changes. No exclusions or server surface retirement arise in this family. Linked source/resource and existing-SPI classifications follow the generated requirement links. Tasks 59/64 remain open; task 70 is next with 104 security/authentication IDs.

## Exact ID ledger

Every row inherits its batch behavior/evidence. Consumer owner is **35** throughout; implementation owner below reflects the active handoff.

### client-pool-settings — 53 IDs

| API ID | Native receiver | Member | JVM descriptor | Owner |
| --- | --- | --- | --- | --- |
| `API-432a99ba688fb741` | `org.apache.geode.cache.client.Pool` | `getAvailabilityZone` | `()Ljava/lang/String;` | 20 |
| `API-e7b291918ce96c14` | `org.apache.geode.cache.client.Pool` | `getFreeConnectionTimeout` | `()I` | 20 |
| `API-32354e3139a35007` | `org.apache.geode.cache.client.Pool` | `getIdleTimeout` | `()J` | 20 |
| `API-a06d74297dd0b8a9` | `org.apache.geode.cache.client.Pool` | `getLoadConditioningInterval` | `()I` | 20 |
| `API-808cd6a9a8d488d8` | `org.apache.geode.cache.client.Pool` | `getLocators` | `()Ljava/util/List;` | 20 |
| `API-9a0896f3da84abd9` | `org.apache.geode.cache.client.Pool` | `getMaxConnections` | `()I` | 20 |
| `API-26fdea15058de6dd` | `org.apache.geode.cache.client.Pool` | `getMaxConnectionsPerServer` | `()I` | 20 |
| `API-584c4a16055fa788` | `org.apache.geode.cache.client.Pool` | `getMinConnections` | `()I` | 20 |
| `API-1ad43cedd95f3bbc` | `org.apache.geode.cache.client.Pool` | `getMinConnectionsPerServer` | `()I` | 20 |
| `API-9330b3eba28688d5` | `org.apache.geode.cache.client.Pool` | `getMultiuserAuthentication` | `()Z` | 20 |
| `API-bf3fb094e518dfe1` | `org.apache.geode.cache.client.Pool` | `getName` | `()Ljava/lang/String;` | 20 |
| `API-7042e50b1c89031d` | `org.apache.geode.cache.client.Pool` | `getPRSingleHopEnabled` | `()Z` | 20 |
| `API-c58724e902a6d05c` | `org.apache.geode.cache.client.Pool` | `getPingInterval` | `()J` | 20 |
| `API-557bc56c6dbc6a08` | `org.apache.geode.cache.client.Pool` | `getPingTimeout` | `()I` | 20 |
| `API-3966533f64fbb161` | `org.apache.geode.cache.client.Pool` | `getReadTimeout` | `()I` | 20 |
| `API-9cff1cf40408fa10` | `org.apache.geode.cache.client.Pool` | `getRetryAttempts` | `()I` | 20 |
| `API-eb5f9c642d72e083` | `org.apache.geode.cache.client.Pool` | `getServerConnectionTimeout` | `()I` | 20 |
| `API-0826bda4dd2dd13a` | `org.apache.geode.cache.client.Pool` | `getServerGroup` | `()Ljava/lang/String;` | 20 |
| `API-06a3499cdcce590a` | `org.apache.geode.cache.client.Pool` | `getServers` | `()Ljava/util/List;` | 20 |
| `API-fc79b230fdd0a7b5` | `org.apache.geode.cache.client.Pool` | `getSocketBufferSize` | `()I` | 20 |
| `API-e54bbcfaf348d1f3` | `org.apache.geode.cache.client.Pool` | `getSocketConnectTimeout` | `()I` | 20 |
| `API-99c687b2fc06e940` | `org.apache.geode.cache.client.Pool` | `getSocketFactory` | `()Lorg/apache/geode/cache/client/SocketFactory;` | 20 |
| `API-167d718eb27d1f73` | `org.apache.geode.cache.client.Pool` | `getStatisticInterval` | `()I` | 20 |
| `API-23bf8193a7ceebb5` | `org.apache.geode.cache.client.Pool` | `getSubscriptionAckInterval` | `()I` | 20 |
| `API-e1eaf55fa5357194` | `org.apache.geode.cache.client.Pool` | `getSubscriptionEnabled` | `()Z` | 20 |
| `API-ea0285b3db74936f` | `org.apache.geode.cache.client.Pool` | `getSubscriptionMessageTrackingTimeout` | `()I` | 20 |
| `API-e7d56c8e6f536ae2` | `org.apache.geode.cache.client.Pool` | `getSubscriptionRedundancy` | `()I` | 20 |
| `API-bc9ffd5cdb53201f` | `org.apache.geode.cache.client.Pool` | `getSubscriptionTimeoutMultiplier` | `()I` | 20 |
| `API-22cf5d9ce6bcd27b` | `org.apache.geode.cache.client.PoolFactory` | `setAvailabilityZone` | `(Ljava/lang/String;)Lorg/apache/geode/cache/client/PoolFactory;` | 20 |
| `API-0ca8c8fcd305b32c` | `org.apache.geode.cache.client.PoolFactory` | `setFreeConnectionTimeout` | `(I)Lorg/apache/geode/cache/client/PoolFactory;` | 20 |
| `API-2e047565bcaa0b94` | `org.apache.geode.cache.client.PoolFactory` | `setIdleTimeout` | `(J)Lorg/apache/geode/cache/client/PoolFactory;` | 20 |
| `API-350b7fa3685a3f6a` | `org.apache.geode.cache.client.PoolFactory` | `setLoadConditioningInterval` | `(I)Lorg/apache/geode/cache/client/PoolFactory;` | 20 |
| `API-8274400900004803` | `org.apache.geode.cache.client.PoolFactory` | `setMaxConnections` | `(I)Lorg/apache/geode/cache/client/PoolFactory;` | 20 |
| `API-ef0ca5718c7bc5c9` | `org.apache.geode.cache.client.PoolFactory` | `setMaxConnectionsPerServer` | `(I)Lorg/apache/geode/cache/client/PoolFactory;` | 20 |
| `API-f4055bbf5d829d09` | `org.apache.geode.cache.client.PoolFactory` | `setMinConnections` | `(I)Lorg/apache/geode/cache/client/PoolFactory;` | 20 |
| `API-44bfb01cd32afb86` | `org.apache.geode.cache.client.PoolFactory` | `setMinConnectionsPerServer` | `(I)Lorg/apache/geode/cache/client/PoolFactory;` | 20 |
| `API-c5b3328ede973b68` | `org.apache.geode.cache.client.PoolFactory` | `setMultiuserAuthentication` | `(Z)Lorg/apache/geode/cache/client/PoolFactory;` | 20 |
| `API-09ca99dffe2b60ed` | `org.apache.geode.cache.client.PoolFactory` | `setPRSingleHopEnabled` | `(Z)Lorg/apache/geode/cache/client/PoolFactory;` | 20 |
| `API-da67a50df8720ac2` | `org.apache.geode.cache.client.PoolFactory` | `setPingInterval` | `(J)Lorg/apache/geode/cache/client/PoolFactory;` | 20 |
| `API-a60d0adcf6d31df0` | `org.apache.geode.cache.client.PoolFactory` | `setPingTimeout` | `(I)Lorg/apache/geode/cache/client/PoolFactory;` | 20 |
| `API-0338f48ec2cfb8e8` | `org.apache.geode.cache.client.PoolFactory` | `setReadTimeout` | `(I)Lorg/apache/geode/cache/client/PoolFactory;` | 20 |
| `API-84768e5b9a72cee0` | `org.apache.geode.cache.client.PoolFactory` | `setRetryAttempts` | `(I)Lorg/apache/geode/cache/client/PoolFactory;` | 20 |
| `API-b0ee88b4ca256486` | `org.apache.geode.cache.client.PoolFactory` | `setServerConnectionTimeout` | `(I)Lorg/apache/geode/cache/client/PoolFactory;` | 20 |
| `API-24dfbd4d256ab311` | `org.apache.geode.cache.client.PoolFactory` | `setServerGroup` | `(Ljava/lang/String;)Lorg/apache/geode/cache/client/PoolFactory;` | 20 |
| `API-af6dd9db448779f3` | `org.apache.geode.cache.client.PoolFactory` | `setSocketBufferSize` | `(I)Lorg/apache/geode/cache/client/PoolFactory;` | 20 |
| `API-1ca73df3bb5449ef` | `org.apache.geode.cache.client.PoolFactory` | `setSocketConnectTimeout` | `(I)Lorg/apache/geode/cache/client/PoolFactory;` | 20 |
| `API-e02e788180643abf` | `org.apache.geode.cache.client.PoolFactory` | `setSocketFactory` | `(Lorg/apache/geode/cache/client/SocketFactory;)Lorg/apache/geode/cache/client/PoolFactory;` | 20 |
| `API-a1a6e39572fcaeb7` | `org.apache.geode.cache.client.PoolFactory` | `setStatisticInterval` | `(I)Lorg/apache/geode/cache/client/PoolFactory;` | 20 |
| `API-777c1b4d0adf5a40` | `org.apache.geode.cache.client.PoolFactory` | `setSubscriptionAckInterval` | `(I)Lorg/apache/geode/cache/client/PoolFactory;` | 20 |
| `API-5d0236b1e4af8dea` | `org.apache.geode.cache.client.PoolFactory` | `setSubscriptionEnabled` | `(Z)Lorg/apache/geode/cache/client/PoolFactory;` | 20 |
| `API-038d91ab0fc721d8` | `org.apache.geode.cache.client.PoolFactory` | `setSubscriptionMessageTrackingTimeout` | `(I)Lorg/apache/geode/cache/client/PoolFactory;` | 20 |
| `API-e7d46861556a3054` | `org.apache.geode.cache.client.PoolFactory` | `setSubscriptionRedundancy` | `(I)Lorg/apache/geode/cache/client/PoolFactory;` | 20 |
| `API-23a91a180c265836` | `org.apache.geode.cache.client.PoolFactory` | `setSubscriptionTimeoutMultiplier` | `(I)Lorg/apache/geode/cache/client/PoolFactory;` | 20 |

### client-pool-defaults — 26 IDs

| API ID | Native receiver | Member | JVM descriptor | Owner |
| --- | --- | --- | --- | --- |
| `API-c4dd6464a5257b6c` | `org.apache.geode.cache.client.PoolFactory` | `DEFAULT_AVAILABILITY_ZONE` | `Ljava/lang/String;` | 20 |
| `API-b19a92d883ba4410` | `org.apache.geode.cache.client.PoolFactory` | `DEFAULT_FREE_CONNECTION_TIMEOUT` | `I` | 20 |
| `API-0878b9fa89274e60` | `org.apache.geode.cache.client.PoolFactory` | `DEFAULT_IDLE_TIMEOUT` | `J` | 20 |
| `API-1ec957e5f64027d5` | `org.apache.geode.cache.client.PoolFactory` | `DEFAULT_LOAD_CONDITIONING_INTERVAL` | `I` | 20 |
| `API-c5574cc58b8f85a4` | `org.apache.geode.cache.client.PoolFactory` | `DEFAULT_MAX_CONNECTIONS` | `I` | 20 |
| `API-64b57d47da246474` | `org.apache.geode.cache.client.PoolFactory` | `DEFAULT_MAX_CONNECTIONS_PER_SERVER` | `I` | 20 |
| `API-e31971bd1a5c6d25` | `org.apache.geode.cache.client.PoolFactory` | `DEFAULT_MIN_CONNECTIONS` | `I` | 20 |
| `API-c8433d50b8c9bbaf` | `org.apache.geode.cache.client.PoolFactory` | `DEFAULT_MIN_CONNECTIONS_PER_SERVER` | `I` | 20 |
| `API-6ae1a6b453673a5c` | `org.apache.geode.cache.client.PoolFactory` | `DEFAULT_MULTIUSER_AUTHENTICATION` | `Z` | 20 |
| `API-319f6fbdc218b31f` | `org.apache.geode.cache.client.PoolFactory` | `DEFAULT_PING_INTERVAL` | `J` | 20 |
| `API-356dd0977f518b06` | `org.apache.geode.cache.client.PoolFactory` | `DEFAULT_PING_TIMEOUT` | `I` | 20 |
| `API-f8cbca8da4104e9a` | `org.apache.geode.cache.client.PoolFactory` | `DEFAULT_PR_SINGLE_HOP_ENABLED` | `Z` | 20 |
| `API-6de39d12f5be93e4` | `org.apache.geode.cache.client.PoolFactory` | `DEFAULT_READ_TIMEOUT` | `I` | 20 |
| `API-852818768e81869f` | `org.apache.geode.cache.client.PoolFactory` | `DEFAULT_RETRY_ATTEMPTS` | `I` | 20 |
| `API-1061978dca26b89a` | `org.apache.geode.cache.client.PoolFactory` | `DEFAULT_SERVER_CONNECTION_TIMEOUT` | `I` | 20 |
| `API-07099b11f79b3968` | `org.apache.geode.cache.client.PoolFactory` | `DEFAULT_SERVER_GROUP` | `Ljava/lang/String;` | 20 |
| `API-c60c1e531dd0185e` | `org.apache.geode.cache.client.PoolFactory` | `DEFAULT_SOCKET_BUFFER_SIZE` | `I` | 20 |
| `API-81511336e83a2232` | `org.apache.geode.cache.client.PoolFactory` | `DEFAULT_SOCKET_CONNECT_TIMEOUT` | `I` | 20 |
| `API-fd54b8526228be00` | `org.apache.geode.cache.client.PoolFactory` | `DEFAULT_SOCKET_FACTORY` | `Lorg/apache/geode/cache/client/SocketFactory;` | 20 |
| `API-48103d1e08df73cf` | `org.apache.geode.cache.client.PoolFactory` | `DEFAULT_STATISTIC_INTERVAL` | `I` | 20 |
| `API-9ceb77fe3dac1170` | `org.apache.geode.cache.client.PoolFactory` | `DEFAULT_SUBSCRIPTION_ACK_INTERVAL` | `I` | 20 |
| `API-3ed0749eb1728e7b` | `org.apache.geode.cache.client.PoolFactory` | `DEFAULT_SUBSCRIPTION_ENABLED` | `Z` | 20 |
| `API-7e478b3270363521` | `org.apache.geode.cache.client.PoolFactory` | `DEFAULT_SUBSCRIPTION_MESSAGE_TRACKING_TIMEOUT` | `I` | 20 |
| `API-8b6cf0f53b195ea2` | `org.apache.geode.cache.client.PoolFactory` | `DEFAULT_SUBSCRIPTION_REDUNDANCY` | `I` | 20 |
| `API-5330a39f1c01d7e3` | `org.apache.geode.cache.client.PoolFactory` | `DEFAULT_SUBSCRIPTION_TIMEOUT_MULTIPLIER` | `I` | 20 |
| `API-51b549509c01c599` | `org.apache.geode.cache.client.PoolFactory` | `IDLE_TIMEOUT_NEVER` | `J` | 20 |

### client-pool-assembly — 5 IDs

| API ID | Native receiver | Member | JVM descriptor | Owner |
| --- | --- | --- | --- | --- |
| `API-2df2774e42c97e77` | `org.apache.geode.cache.client.PoolFactory` | `<type>` | `(type)` | 20 |
| `API-66bbbbad5a28e1da` | `org.apache.geode.cache.client.PoolFactory` | `addLocator` | `(Ljava/lang/String;I)Lorg/apache/geode/cache/client/PoolFactory;` | 20 |
| `API-29e313efe5131942` | `org.apache.geode.cache.client.PoolFactory` | `addServer` | `(Ljava/lang/String;I)Lorg/apache/geode/cache/client/PoolFactory;` | 20 |
| `API-facbf87d7bfe1d69` | `org.apache.geode.cache.client.PoolFactory` | `create` | `(Ljava/lang/String;)Lorg/apache/geode/cache/client/Pool;` | 20 |
| `API-021bedf309b96fab` | `org.apache.geode.cache.client.PoolFactory` | `reset` | `()Lorg/apache/geode/cache/client/PoolFactory;` | 20 |

### client-pool-lifecycle — 5 IDs

| API ID | Native receiver | Member | JVM descriptor | Owner |
| --- | --- | --- | --- | --- |
| `API-88344d815595d6d3` | `org.apache.geode.cache.client.ClientCache` | `close` | `(Z)V` | 52 |
| `API-5e277e9b91a91e7b` | `org.apache.geode.cache.client.Pool` | `<type>` | `(type)` | 52 |
| `API-6a8a4bd25506af1a` | `org.apache.geode.cache.client.Pool` | `destroy` | `()V` | 52 |
| `API-43b70db7bed8e3c7` | `org.apache.geode.cache.client.Pool` | `destroy` | `(Z)V` | 52 |
| `API-ee5f65fd3384db8a` | `org.apache.geode.cache.client.Pool` | `isDestroyed` | `()Z` | 52 |

### client-durable-readiness — 2 IDs

| API ID | Native receiver | Member | JVM descriptor | Owner |
| --- | --- | --- | --- | --- |
| `API-b88fbfcb0ed8e1fd` | `org.apache.geode.cache.client.ClientCache` | `readyForEvents` | `()V` | 52 |
| `API-433365d328bf1f08` | `org.apache.geode.cache.client.Pool` | `getPendingEventCount` | `()I` | 52 |

### client-pool-topology — 2 IDs

| API ID | Native receiver | Member | JVM descriptor | Owner |
| --- | --- | --- | --- | --- |
| `API-d86ac0fb7fb22874` | `org.apache.geode.cache.client.ClientCache` | `getCurrentServers` | `()Ljava/util/Set;` | 52 |
| `API-0bb395ff3754d8d1` | `org.apache.geode.cache.client.Pool` | `getOnlineLocators` | `()Ljava/util/List;` | 52 |

### client-default-pool-selection — 1 IDs

| API ID | Native receiver | Member | JVM descriptor | Owner |
| --- | --- | --- | --- | --- |
| `API-07e6c13ba24f9951` | `org.apache.geode.cache.client.ClientCache` | `getDefaultPool` | `()Lorg/apache/geode/cache/client/Pool;` | 51 |

### client-pool-registry — 7 IDs

| API ID | Native receiver | Member | JVM descriptor | Owner |
| --- | --- | --- | --- | --- |
| `API-ced318422094b72f` | `org.apache.geode.cache.client.PoolManager` | `<type>` | `(type)` | 52 |
| `API-7f2d7f9de7b398db` | `org.apache.geode.cache.client.PoolManager` | `close` | `()V` | 52 |
| `API-c8b2e09c36210b8f` | `org.apache.geode.cache.client.PoolManager` | `close` | `(Z)V` | 52 |
| `API-dacd4348020e7fd8` | `org.apache.geode.cache.client.PoolManager` | `createFactory` | `()Lorg/apache/geode/cache/client/PoolFactory;` | 52 |
| `API-f0345d377ef88ca6` | `org.apache.geode.cache.client.PoolManager` | `find` | `(Ljava/lang/String;)Lorg/apache/geode/cache/client/Pool;` | 52 |
| `API-76c5b059bbb0a7ac` | `org.apache.geode.cache.client.PoolManager` | `find` | `(Lorg/apache/geode/cache/Region;)Lorg/apache/geode/cache/client/Pool;` | 52 |
| `API-143267f4f709479c` | `org.apache.geode.cache.client.PoolManager` | `getAll` | `()Ljava/util/Map;` | 52 |
