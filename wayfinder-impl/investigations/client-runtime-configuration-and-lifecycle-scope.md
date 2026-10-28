# Client runtime configuration and lifecycle scope — 2026-10-28

Task 72 classifies **291 assigned IDs across six types: 145 required-client / 146 excluded-server / zero unresolved**. Implementation statuses remain unchanged. Task 72 is resolved after user-run Java/boundary validation and post-removal inventory checks.

Evidence is pinned to GemFire **10.4.0-build.3056.df98c835**, source commit `df98c8357afe85853d9a4776255d4f0e7cf064cb` in `/Users/uk016635/projects/gemfire`, inspected with git show/git grep. Unless qualified, source paths below are relative to `gemfire-core/src/main/java/org/apache/geode/` with `.java` suffix. Exact IDs below define each batch, including constructors/constants/overloads/inheritance. No new runtime probe is required for these source-explicit scope decisions.

## client-cancellation

**9 IDs — required-client.** CancelCriterion defines a monotonic cancellation reason, cause-aware exception generation, both check overloads, isCancelInProgress and the NONE implementation. checkCancelInProgress first consults SystemFailure; protected checkFailure reports JVM failure. Preserve the client-visible cancellation/cause contract through typed lifecycle services, with native failure machinery private. Constructors/type represent that contract, not a public native subclass requirement. Pinned source: CancelCriterion, especially cancelInProgress, checkCancelInProgress, checkFailure and NONE.

## client-durable-identity

**11 IDs — required-client.** distributed/DurableClientAttributes constructors, accessors, updates and equality/hash/string methods describe client identity. The String constructor splits the first _gem_ into ID/pool, accepts no null ID, and performs no timeout validation. The no-arg form has null ID and zero timeout. getId reads ClientProxyMembershipID.getPoolName when ID is nonempty and mutates poolName before returning a qualified ID. hashCode/toString call getId; equality compares timeout/raw ID/pool, and distinct null-ID instances are not equal. Existing DriverDurableClientAttributes is only an ID/timeout record: its three partial rows remain partial. Task 23 coordinates task 20 on typed durable snapshots, pool identity, mutation and intentional replacement of ambient thread-local behavior; task 36 adopts the replacement in utilities. Pinned sources: distributed/DurableClientAttributes and internal/cache/tier/sockets/ClientProxyMembershipID.getPoolName.

## client-runtime-context

**28 IDs — required-client.** distributed/DistributedSystem connect/getConnection, construction, identity, properties, connectedness, cancellation and disconnect are shared native scaffolding for a client runtime. ClientCacheFactory.basicCreate forces locators empty, rejects nonempty peer locators and connects through InternalDistributedSystem.connectInternal; InternalDistributedSystem.initialize selects LonerDistributionManager for that client context. A locally connected runtime does not establish a live server connection. addSystem/removeSystem/existingSystems/existingSystemsLock remain private native implementation concerns; use injected context and owned close semantics instead of copying mutable global registries. Property-file constants and lookup methods retain configured/default names, filesystem/classpath resolution and absent-file behavior. Task 23 owns typed context, safe/redacted properties and versioned lifecycle; task 36 removes native utility exposure, coordinating entrypoint task 35. Do not add a public peer connect factory.

## client-runtime-statistics

**26 IDs — required-client.** distributed/DistributedSystem inherits StatisticsFactory operations for atomic/non-atomic statistics, descriptor counters/gauges, types/XML, and identity/text/type lookup. InternalDistributedSystem initializes a statistics manager for clients too. getLogWriter/getSecurityLogWriter expose shared local logging. Preserve creation options, type/descriptor identity, closed/missing behavior and redaction through typed services. Task 23 owns these exact inherited IDs; task 73 audits the separate Statistics/LogWriter types and task 36 adopts them. No server statistics hosting or management endpoint follows from these local services.

## peer-runtime-administration

**12 IDs — excluded-server.** distributed/DistributedSystem connectForAdmin and SHUTDOWN_COMMAND belong to peer administration. getAllOtherMembers/getGroupMembers/findDistributedMember/findDistributedMembers query the peer membership view; a loner can report itself for its own group, which does not establish server discovery. Client local identity/groups and pool-discovered servers survive independently. isReconnecting/waitUntilReconnected/stopReconnecting/getReconnectedSystem concern peer system auto-rejoin, not pool retry/redundancy. setThreadsSocketPolicy/releaseThreadsSockets control deprecated peer thread-owned transport (DirectChannel/ThreadOwnedSenderManager), not client pool sockets. Pinned sources: distributed/DistributedSystem and distributed/internal/{InternalDistributedSystem,LonerDistributionManager}. These missing rows have no SPI implementation mapping to remove.

## locator-hosting

**25 IDs — excluded-server.** All 25 distributed/Locator declarations address a locator hosted in this process: constructors, start overloads, stop, addresses/ports/files, flags, hosted distributed system and static local registry lookup. They are not pool discovery APIs. ClientPoolFactory/PoolManager configuration and discovery stay required under tasks 20/50. Remove DistributedSystemUtils.getLocator (also inherited by GemfireUtils), its native imports and server Javadoc. The only current utility caller is the private test LocatorProcess: capture the locator returned by its own startup as a shutdown callback instead of reaching through the production utility. Existing direct native server/locator test infrastructure stays private. Add a reflection assertion to ClientOnlyConfigurationUnitTests that getLocator is absent while client runtime helpers remain. InternalLocator exact IDs remain assigned to task 73; do not steal that assignment.

## management-class-name

**10 IDs — excluded-server.** management/configuration/ClassName is a management declaration value (source in gemfire-management/src/main/java/org/apache/geode/). Its constructors, EMPTY, getters, equality/hash and class-name validator support cluster configuration/declarable converters. Blank names normalize to empty properties; JSON properties use the management mapper; Properties aliases remain mutable. Shared cache/configuration/DeclarableType and RegionAttributesType use it for generated cluster configuration models, expiry and eviction declarations. These carriers do not require a public cluster configuration API in the client SPI. Client CacheXmlParser and typed client callback/expiry/eviction configuration remain separate retained paths (tasks 29/35/47). No current Spring production use or SPI mapping exists for these ten missing rows. This exclusion applies to the DTO, not application class names, callback properties or client XML.

## client-runtime-properties

**16 IDs — required-client.** ConfigurationProperties type and the exact keys listed below cover client identity/groups, cache XML, conflation/durability, delta propagation, memory locking, serialization filters and availability selection. ClientCacheFactory.basicCreate forces LOCATORS empty and rejects nonempty values; retain that guard, not a second mechanism for pool locator endpoints. LonerDistributionManager generates member identity from NAME and parseGroups(ROLES,GROUPS); ROLES here is a legacy group input, not peer required-role region policy. PoolImpl uses AVAILABILITY_ZONE; DistributionConfigImpl.get/setRedundancyZone delegates to availability zone, so the deprecated alias remains a client requirement. DISTRIBUTED_SYSTEM_ID is returned by LonerDistributionManager and used by ClientSideHandshakeImpl; it is client metadata, not permission for WAN hosting. LocalRegion.shouldExtractDelta checks DELTA_PROPAGATION before the server-proxy path. InternalDistributedSystem.initialize checks LOCK_MEMORY before selecting loner/peer management; OS support/errors must be explicit. CACHE_XML_FILE remains supported. Task 23 owns typed validation/readback, task 36 utilities, coordinating task 35 configuration/adoption and task 20 durable/pools. See also client-only-spring-surfaces.md#deployment-and-distributed-transaction-properties--2026-10-27 for existing native evidence.

## client-monitoring-properties

**14 IDs — required-client.** ARCHIVE/LOG limits, statistics archive/rate/sampling, ENABLE_TIME_STATISTICS and THREAD_MONITOR keys configure local runtime services. InternalDistributedSystem initializes client statistics/logging. LonerDistributionManager constructs ThreadMonitorImpl when enabled; internal/monitoring/executor/ThreadMonitorTask uses half MEMBER_TIMEOUT for its backoff. MEMBER_TIMEOUT therefore cannot be excluded just because it is also a membership timeout. Keep diagnostic redaction and platform support explicit. Tasks 23/36 coordinate task 73 on local statistics/logging semantics; no management server endpoint is implied.

## client-authentication-properties

**5 IDs — required-client.** SECURITY_CLIENT_AUTH_INIT configures the client authentication initializer; SECURITY_CLIENT_DHALGO participates in EncryptorImpl and client/server handshake. SECURITY_PREFIX carries the security namespace, not blanket acceptance of all server security extensions. SECURITY_LOG_FILE/LEVEL are local diagnostics and need redaction. Pinned sources: ConfigurationProperties, distributed/internal/DistributionConfigImpl, internal/cache/tier/sockets/{ClientSideHandshakeImpl,EncryptorImpl}. Tasks 23/36 coordinate task 24 authentication and task 35 configuration. Server authentication/accessor/manager hooks are separately excluded.

## client-tls-properties

**36 IDs — required-client.** internal/net/SSLConfigurationFactory configures shared modern SSL options, LOCATOR and SERVER channels, aliases and legacy fallback. When SSL_ENABLED_COMPONENTS is empty, legacy cluster SSL seeds the configuration and supplies locator TLS; SERVER uses legacy SERVER_SSL values. Thus CLUSTER_SSL and SERVER_SSL names cannot be discarded as server-only. Retain common ciphers/protocols, client protocols, keystore/truststore/type/password, default context/alias, locator/server alias, endpoint identification, parameter extension and require-authentication. SocketCreator's client path consults require-authentication on SSLPeerUnverifiedException; it is not solely an inbound server switch. Typed configuration must constrain enabled channels to client-supported uses; no gateway/JMX/web/peer hosting follows from retaining the shared key. Task 23 owns configuration semantics with task 24 security/task 20 pools; task 36 coordinates task 35 adoption. Deprecated aliases and missing/invalid credentials need versioned tests, not ignored settings.

## host-tls-properties

**36 IDs — excluded-server.** ConfigurationProperties SSL_CLUSTER_ALIAS, SSL_GATEWAY_ALIAS, SSL_GRPC_ALIAS, SSL_JMX_ALIAS, SSL_WEB_ALIAS, SSL_WEB_SERVICE_REQUIRE_AUTHENTICATION and SSL_SERVER_PROTOCOLS select non-client channels or inbound server behavior. SSLConfigurationFactory selects channel aliases; SocketCreator.configureProtocols uses server protocols for the inbound branch and client protocols outbound. GATEWAY_SSL, JMX_MANAGER_SSL and HTTP_SERVICE_SSL legacy settings likewise configure hosted services. Client outbound SERVER_SSL legacy keys are explicitly retained in the other batch. There are no existing SPI implementations for these excluded rows.

## server-security-properties

**11 IDs — excluded-server.** SECURITY_CLIENT_AUTHENTICATOR/ACCESSOR/ACCESSOR_PP, SECURITY_MANAGER/POST_PROCESSOR, SECURITY_PEER_* and SECURITY_UDP_DHALGO configure hosted security and peer membership. SECURITY_AUTH_TOKEN_ENABLED_COMPONENTS selects accepting server components. ConfigurationProperties documents SECURITY_MULTIUSER_AUTHENTICATION_ENABLED as the server gate for authenticated subscriptions; it is not the client pool multiuser flag. Client authenticated views and CQ/subscription behavior remain required under tasks 20/24, with server configuration private in test fixtures. Task 70 already excludes the corresponding server interfaces; task 77 owns the existing security fixture relocation. Missing mapping rows require no new public SPI.

## peer-transport-properties

**26 IDs — excluded-server.** The exact keys below govern peer acknowledgments, bind/transport sockets, multicast/UDP/membership, partition detection, locator membership wait/rejoin and unique-host placement. ConfigurationProperties and distributed/internal/{DistributionConfigImpl,InternalDistributedSystem}, distributed/internal/direct/DirectChannel and peer messaging implement these controls. A shared config read does not make a peer transport feature a client pool option. PoolFactory owns its separate socket buffer and socket callback APIs. MCAST controls are deprecated with no replacement in the pinned release. DISTRIBUTED_TRANSACTIONS selects DistTXStateProxyImplOnCoordinator, whose client path uses DistClientTXStateStub and rejects distributed operations. Existing source/native transaction evidence and Spring retirement are recorded in client-only-spring-surfaces.md#deployment-and-distributed-transaction-properties--2026-10-27. Ordinary client transactions remain required. No new runtime probe is needed.

## host-administration-properties

**26 IDs — excluded-server.** START_LOCATOR/REMOTE_LOCATORS host or join locator services; SERVER_BIND_ADDRESS binds a hosted server. Cluster configuration flags/directories and DEPLOY_WORKING_DIR support server deployment. InternalDistributedSystem sets the shared classloader directory, but GemFireCacheImpl skips applyJarAndXmlFromClusterConfig and its deployed-JAR loading for clients; client declarative XML is outside that branch and remains supported. JMX_MANAGER, HTTP/REST, USER_COMMAND_PACKAGES and REMOVE_UNRESPONSIVE_CLIENT configure hosted administration/services. GemFireCacheImpl gates management initialization, including DISABLE_JMX, on !isClient and an empty pool manager. Existing Spring annotation/property removals in task 63 remain; no ignored compatibility properties are added. Server fixtures may configure these privately. Pinned sources: ConfigurationProperties, InternalDistributedSystem, GemFireCacheImpl.initialize/applyJarAndXmlFromClusterConfig; see the prior Spring-surface evidence for deployment details.

## Ownership and validation

All retained rows keep active implementation owner 23 and consumer owner 36, with coordination as specified above. All excluded rows are missing and have no SPI/native/mock/test implementation mapping; the existing production locator utility is retired in this task. No frozen requirement, assignment, implementation status or other family scope changes. Task 73 retains internal helper/statistics identities; task 75 retains membership value types. Tasks 59/64 remain open.

The generated matrix now reflects the compiled locator-accessor removal. Validation is recorded below. Gradle was user-run only. Client XML, ordinary transactions, pool discovery, durable identities and outbound TLS are retained.

## Exact ID ledger

### client-cancellation — 9 IDs

| API ID | Native receiver | Member | JVM descriptor | Owner | Consumer |
| --- | --- | --- | --- | --- | --- |
| `API-f747292621d0a362` | `org.apache.geode.CancelCriterion` | `<init>` | `()V` | 23 | 36 |
| `API-671316e36ee249ca` | `org.apache.geode.CancelCriterion` | `<type>` | `(type)` | 23 | 36 |
| `API-5e2e25b2e2addcea` | `org.apache.geode.CancelCriterion` | `NONE` | `Lorg/apache/geode/CancelCriterion;` | 23 | 36 |
| `API-89d0da978baeb6ee` | `org.apache.geode.CancelCriterion` | `cancelInProgress` | `()Ljava/lang/String;` | 23 | 36 |
| `API-63bf5092b7cd89cd` | `org.apache.geode.CancelCriterion` | `checkCancelInProgress` | `()V` | 23 | 36 |
| `API-19491347b6e2bd9e` | `org.apache.geode.CancelCriterion` | `checkCancelInProgress` | `(Ljava/lang/Throwable;)V` | 23 | 36 |
| `API-256dd2e93909cead` | `org.apache.geode.CancelCriterion` | `checkFailure` | `()Ljava/lang/String;` | 23 | 36 |
| `API-7aa574c9c6ad5d98` | `org.apache.geode.CancelCriterion` | `generateCancelledException` | `(Ljava/lang/Throwable;)Ljava/lang/RuntimeException;` | 23 | 36 |
| `API-cf0d0b786c301a93` | `org.apache.geode.CancelCriterion` | `isCancelInProgress` | `()Z` | 23 | 36 |

### client-durable-identity — 11 IDs

| API ID | Native receiver | Member | JVM descriptor | Owner | Consumer |
| --- | --- | --- | --- | --- | --- |
| `API-ad945127d18dde82` | `org.apache.geode.distributed.DurableClientAttributes` | `<init>` | `()V` | 23 | 36 |
| `API-9258e6d7d999f24d` | `org.apache.geode.distributed.DurableClientAttributes` | `<init>` | `(Ljava/lang/String;I)V` | 23 | 36 |
| `API-181b05274635cff3` | `org.apache.geode.distributed.DurableClientAttributes` | `<type>` | `(type)` | 23 | 36 |
| `API-fa54e8e61acd9655` | `org.apache.geode.distributed.DurableClientAttributes` | `equals` | `(Ljava/lang/Object;)Z` | 23 | 36 |
| `API-1f52a3e51dcb37c4` | `org.apache.geode.distributed.DurableClientAttributes` | `getId` | `()Ljava/lang/String;` | 23 | 36 |
| `API-97c454b33d3b90e4` | `org.apache.geode.distributed.DurableClientAttributes` | `getPoolName` | `()Ljava/lang/String;` | 23 | 36 |
| `API-0891eaaa88d62151` | `org.apache.geode.distributed.DurableClientAttributes` | `getTimeout` | `()I` | 23 | 36 |
| `API-b088a86269585233` | `org.apache.geode.distributed.DurableClientAttributes` | `hashCode` | `()I` | 23 | 36 |
| `API-e521d9ca9253413b` | `org.apache.geode.distributed.DurableClientAttributes` | `setPoolName` | `(Ljava/lang/String;)V` | 23 | 36 |
| `API-d72b06ac99f01c54` | `org.apache.geode.distributed.DurableClientAttributes` | `toString` | `()Ljava/lang/String;` | 23 | 36 |
| `API-5851f20511958f8c` | `org.apache.geode.distributed.DurableClientAttributes` | `updateTimeout` | `(I)V` | 23 | 36 |

### client-runtime-context — 28 IDs

| API ID | Native receiver | Member | JVM descriptor | Owner | Consumer |
| --- | --- | --- | --- | --- | --- |
| `API-eb2cbc709fa17153` | `org.apache.geode.distributed.DistributedSystem` | `<init>` | `()V` | 23 | 36 |
| `API-881002935ced17da` | `org.apache.geode.distributed.DistributedSystem` | `<type>` | `(type)` | 23 | 36 |
| `API-db06684bd71a3aa2` | `org.apache.geode.distributed.DistributedSystem` | `PROPERTIES_FILE_DEFAULT` | `Ljava/lang/String;` | 23 | 36 |
| `API-8184c06fc378dab2` | `org.apache.geode.distributed.DistributedSystem` | `PROPERTIES_FILE_PROPERTY` | `Ljava/lang/String;` | 23 | 36 |
| `API-7bcd6945802a1472` | `org.apache.geode.distributed.DistributedSystem` | `PROPERTY_FILE` | `Ljava/lang/String;` | 23 | 36 |
| `API-c25b0a2d80448358` | `org.apache.geode.distributed.DistributedSystem` | `SECURITY_PROPERTIES_FILE_DEFAULT` | `Ljava/lang/String;` | 23 | 36 |
| `API-c68bed3d8f100896` | `org.apache.geode.distributed.DistributedSystem` | `SECURITY_PROPERTIES_FILE_PROPERTY` | `Ljava/lang/String;` | 23 | 36 |
| `API-c823255220ed7997` | `org.apache.geode.distributed.DistributedSystem` | `SECURITY_PROPERTY_FILE` | `Ljava/lang/String;` | 23 | 36 |
| `API-1b62c8bc70642945` | `org.apache.geode.distributed.DistributedSystem` | `addSystem` | `(Lorg/apache/geode/distributed/internal/InternalDistributedSystem;)V` | 23 | 36 |
| `API-377cbbcd90369408` | `org.apache.geode.distributed.DistributedSystem` | `connect` | `(Ljava/util/Properties;)Lorg/apache/geode/distributed/DistributedSystem;` | 23 | 36 |
| `API-43408d153f828965` | `org.apache.geode.distributed.DistributedSystem` | `disconnect` | `()V` | 23 | 36 |
| `API-fe96d1c9a762d606` | `org.apache.geode.distributed.DistributedSystem` | `existingSystems` | `Ljava/util/List;` | 23 | 36 |
| `API-131a94f3bd7aa529` | `org.apache.geode.distributed.DistributedSystem` | `existingSystemsLock` | `Ljava/lang/Object;` | 23 | 36 |
| `API-f2ae8d8c7e5c7907` | `org.apache.geode.distributed.DistributedSystem` | `getCancelCriterion` | `()Lorg/apache/geode/CancelCriterion;` | 23 | 36 |
| `API-bf757b728e385ce2` | `org.apache.geode.distributed.DistributedSystem` | `getConnection` | `(Ljava/util/Properties;)Lorg/apache/geode/distributed/DistributedSystem;` | 23 | 36 |
| `API-d2d2551a57e3e0e8` | `org.apache.geode.distributed.DistributedSystem` | `getDistributedMember` | `()Lorg/apache/geode/distributed/DistributedMember;` | 23 | 36 |
| `API-c954e232a0eb2094` | `org.apache.geode.distributed.DistributedSystem` | `getId` | `()J` | 23 | 36 |
| `API-24677a0a9962290c` | `org.apache.geode.distributed.DistributedSystem` | `getMemberId` | `()Ljava/lang/String;` | 23 | 36 |
| `API-8cfa8d32836a81ae` | `org.apache.geode.distributed.DistributedSystem` | `getName` | `()Ljava/lang/String;` | 23 | 36 |
| `API-a1ce6075690d70fa` | `org.apache.geode.distributed.DistributedSystem` | `getProperties` | `()Ljava/util/Properties;` | 23 | 36 |
| `API-5b0ca892ed58fd71` | `org.apache.geode.distributed.DistributedSystem` | `getPropertiesFile` | `()Ljava/lang/String;` | 23 | 36 |
| `API-9b32be1e142dc739` | `org.apache.geode.distributed.DistributedSystem` | `getPropertiesFileURL` | `()Ljava/net/URL;` | 23 | 36 |
| `API-2385d155b90a1da3` | `org.apache.geode.distributed.DistributedSystem` | `getPropertyFileURL` | `()Ljava/net/URL;` | 23 | 36 |
| `API-936cce7138573930` | `org.apache.geode.distributed.DistributedSystem` | `getSecurityProperties` | `()Ljava/util/Properties;` | 23 | 36 |
| `API-b7e52a61e609d8c2` | `org.apache.geode.distributed.DistributedSystem` | `getSecurityPropertiesFile` | `()Ljava/lang/String;` | 23 | 36 |
| `API-53fc9480874157ee` | `org.apache.geode.distributed.DistributedSystem` | `getSecurityPropertiesFileURL` | `()Ljava/net/URL;` | 23 | 36 |
| `API-2e6a6a9b2a8dc7a0` | `org.apache.geode.distributed.DistributedSystem` | `isConnected` | `()Z` | 23 | 36 |
| `API-b80d143d0dd34351` | `org.apache.geode.distributed.DistributedSystem` | `removeSystem` | `(Lorg/apache/geode/distributed/internal/InternalDistributedSystem;)V` | 23 | 36 |

### client-runtime-statistics — 26 IDs

| API ID | Native receiver | Member | JVM descriptor | Owner | Consumer |
| --- | --- | --- | --- | --- | --- |
| `API-9b3c5eebdcf6056f` | `org.apache.geode.distributed.DistributedSystem` | `createAtomicStatistics` | `(Lorg/apache/geode/StatisticsType;)Lorg/apache/geode/Statistics;` | 23 | 36 |
| `API-62812f3a7931ab5d` | `org.apache.geode.distributed.DistributedSystem` | `createAtomicStatistics` | `(Lorg/apache/geode/StatisticsType;Ljava/lang/String;)Lorg/apache/geode/Statistics;` | 23 | 36 |
| `API-2d90ec013acd075b` | `org.apache.geode.distributed.DistributedSystem` | `createAtomicStatistics` | `(Lorg/apache/geode/StatisticsType;Ljava/lang/String;J)Lorg/apache/geode/Statistics;` | 23 | 36 |
| `API-187f0bb2069e16bc` | `org.apache.geode.distributed.DistributedSystem` | `createDoubleCounter` | `(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Lorg/apache/geode/StatisticDescriptor;` | 23 | 36 |
| `API-6d73eec94631d400` | `org.apache.geode.distributed.DistributedSystem` | `createDoubleCounter` | `(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Z)Lorg/apache/geode/StatisticDescriptor;` | 23 | 36 |
| `API-9578684e1b115ea4` | `org.apache.geode.distributed.DistributedSystem` | `createDoubleGauge` | `(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Lorg/apache/geode/StatisticDescriptor;` | 23 | 36 |
| `API-fa83d2bf0375024d` | `org.apache.geode.distributed.DistributedSystem` | `createDoubleGauge` | `(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Z)Lorg/apache/geode/StatisticDescriptor;` | 23 | 36 |
| `API-82cfb2780f130129` | `org.apache.geode.distributed.DistributedSystem` | `createIntCounter` | `(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Lorg/apache/geode/StatisticDescriptor;` | 23 | 36 |
| `API-ae6fe09a3abeba37` | `org.apache.geode.distributed.DistributedSystem` | `createIntCounter` | `(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Z)Lorg/apache/geode/StatisticDescriptor;` | 23 | 36 |
| `API-2d540929d2d3e42b` | `org.apache.geode.distributed.DistributedSystem` | `createIntGauge` | `(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Lorg/apache/geode/StatisticDescriptor;` | 23 | 36 |
| `API-da706c4354b67068` | `org.apache.geode.distributed.DistributedSystem` | `createIntGauge` | `(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Z)Lorg/apache/geode/StatisticDescriptor;` | 23 | 36 |
| `API-dd10ea426760a0d5` | `org.apache.geode.distributed.DistributedSystem` | `createLongCounter` | `(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Lorg/apache/geode/StatisticDescriptor;` | 23 | 36 |
| `API-3d8adb205fe0c355` | `org.apache.geode.distributed.DistributedSystem` | `createLongCounter` | `(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Z)Lorg/apache/geode/StatisticDescriptor;` | 23 | 36 |
| `API-3293d181d21a134e` | `org.apache.geode.distributed.DistributedSystem` | `createLongGauge` | `(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Lorg/apache/geode/StatisticDescriptor;` | 23 | 36 |
| `API-42149e72896984d9` | `org.apache.geode.distributed.DistributedSystem` | `createLongGauge` | `(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Z)Lorg/apache/geode/StatisticDescriptor;` | 23 | 36 |
| `API-eb082c86fae9d03d` | `org.apache.geode.distributed.DistributedSystem` | `createStatistics` | `(Lorg/apache/geode/StatisticsType;)Lorg/apache/geode/Statistics;` | 23 | 36 |
| `API-4e0fa3a3c81c4216` | `org.apache.geode.distributed.DistributedSystem` | `createStatistics` | `(Lorg/apache/geode/StatisticsType;Ljava/lang/String;)Lorg/apache/geode/Statistics;` | 23 | 36 |
| `API-9fa02f3ca36729ed` | `org.apache.geode.distributed.DistributedSystem` | `createStatistics` | `(Lorg/apache/geode/StatisticsType;Ljava/lang/String;J)Lorg/apache/geode/Statistics;` | 23 | 36 |
| `API-f8d2c89a815e6780` | `org.apache.geode.distributed.DistributedSystem` | `createType` | `(Ljava/lang/String;Ljava/lang/String;[Lorg/apache/geode/StatisticDescriptor;)Lorg/apache/geode/StatisticsType;` | 23 | 36 |
| `API-00c02fd10f385e8a` | `org.apache.geode.distributed.DistributedSystem` | `createTypesFromXml` | `(Ljava/io/Reader;)[Lorg/apache/geode/StatisticsType;` | 23 | 36 |
| `API-94c6353f95a8979e` | `org.apache.geode.distributed.DistributedSystem` | `findStatisticsByNumericId` | `(J)[Lorg/apache/geode/Statistics;` | 23 | 36 |
| `API-a4de7179d17d62a6` | `org.apache.geode.distributed.DistributedSystem` | `findStatisticsByTextId` | `(Ljava/lang/String;)[Lorg/apache/geode/Statistics;` | 23 | 36 |
| `API-3f89e0360ccd8c1e` | `org.apache.geode.distributed.DistributedSystem` | `findStatisticsByType` | `(Lorg/apache/geode/StatisticsType;)[Lorg/apache/geode/Statistics;` | 23 | 36 |
| `API-0f8c07cc74de9759` | `org.apache.geode.distributed.DistributedSystem` | `findType` | `(Ljava/lang/String;)Lorg/apache/geode/StatisticsType;` | 23 | 36 |
| `API-f1daad83300ee481` | `org.apache.geode.distributed.DistributedSystem` | `getLogWriter` | `()Lorg/apache/geode/LogWriter;` | 23 | 36 |
| `API-f2677b664e687099` | `org.apache.geode.distributed.DistributedSystem` | `getSecurityLogWriter` | `()Lorg/apache/geode/LogWriter;` | 23 | 36 |

### peer-runtime-administration — 12 IDs

| API ID | Native receiver | Member | JVM descriptor | Owner | Consumer |
| --- | --- | --- | --- | --- | --- |
| `API-af66cd5dcc4c554e` | `org.apache.geode.distributed.DistributedSystem` | `SHUTDOWN_COMMAND` | `Ljava/lang/String;` | 23 | 36 |
| `API-2024e2fb032312cc` | `org.apache.geode.distributed.DistributedSystem` | `connectForAdmin` | `(Ljava/util/Properties;)Lorg/apache/geode/distributed/DistributedSystem;` | 23 | 36 |
| `API-53f94b630cbedb01` | `org.apache.geode.distributed.DistributedSystem` | `findDistributedMember` | `(Ljava/lang/String;)Lorg/apache/geode/distributed/DistributedMember;` | 23 | 36 |
| `API-fdfac1c12359e527` | `org.apache.geode.distributed.DistributedSystem` | `findDistributedMembers` | `(Ljava/net/InetAddress;)Ljava/util/Set;` | 23 | 36 |
| `API-9bcb1e46fae99b4d` | `org.apache.geode.distributed.DistributedSystem` | `getAllOtherMembers` | `()Ljava/util/Set;` | 23 | 36 |
| `API-6b06ba93e65e6bfa` | `org.apache.geode.distributed.DistributedSystem` | `getGroupMembers` | `(Ljava/lang/String;)Ljava/util/Set;` | 23 | 36 |
| `API-e9cfdd62c2805b53` | `org.apache.geode.distributed.DistributedSystem` | `getReconnectedSystem` | `()Lorg/apache/geode/distributed/DistributedSystem;` | 23 | 36 |
| `API-b02ef126f794cae7` | `org.apache.geode.distributed.DistributedSystem` | `isReconnecting` | `()Z` | 23 | 36 |
| `API-0c5dfd0a5332f762` | `org.apache.geode.distributed.DistributedSystem` | `releaseThreadsSockets` | `()V` | 23 | 36 |
| `API-686dcdfde2030b49` | `org.apache.geode.distributed.DistributedSystem` | `setThreadsSocketPolicy` | `(Z)V` | 23 | 36 |
| `API-5de8fdde3e86d617` | `org.apache.geode.distributed.DistributedSystem` | `stopReconnecting` | `()V` | 23 | 36 |
| `API-b091f05f7a80f285` | `org.apache.geode.distributed.DistributedSystem` | `waitUntilReconnected` | `(JLjava/util/concurrent/TimeUnit;)Z` | 23 | 36 |

### locator-hosting — 25 IDs

| API ID | Native receiver | Member | JVM descriptor | Owner | Consumer |
| --- | --- | --- | --- | --- | --- |
| `API-690bdc365a374ea8` | `org.apache.geode.distributed.Locator` | `<init>` | `()V` | 23 | 36 |
| `API-41428117f64cb26c` | `org.apache.geode.distributed.Locator` | `<type>` | `(type)` | 23 | 36 |
| `API-0842fbb04fe9cb27` | `org.apache.geode.distributed.Locator` | `asString` | `()Ljava/lang/String;` | 23 | 36 |
| `API-bc00e1f78b7e8abd` | `org.apache.geode.distributed.Locator` | `bindAddress` | `Ljava/net/InetAddress;` | 23 | 36 |
| `API-d97cde66d73a36c8` | `org.apache.geode.distributed.Locator` | `getBindAddress` | `()Ljava/net/InetAddress;` | 23 | 36 |
| `API-266855c9ecf0a8f0` | `org.apache.geode.distributed.Locator` | `getDistributedSystem` | `()Lorg/apache/geode/distributed/DistributedSystem;` | 23 | 36 |
| `API-5335b4971e141ac3` | `org.apache.geode.distributed.Locator` | `getHostnameForClients` | `()Ljava/lang/String;` | 23 | 36 |
| `API-a82babf8b37984fa` | `org.apache.geode.distributed.Locator` | `getLocator` | `()Lorg/apache/geode/distributed/Locator;` | 23 | 36 |
| `API-3cd33688a6e4384a` | `org.apache.geode.distributed.Locator` | `getLocators` | `()Ljava/util/List;` | 23 | 36 |
| `API-fd41ff679fc8f16d` | `org.apache.geode.distributed.Locator` | `getLogFile` | `()Ljava/io/File;` | 23 | 36 |
| `API-1c27e5ce2f48bd31` | `org.apache.geode.distributed.Locator` | `getPort` | `()Ljava/lang/Integer;` | 23 | 36 |
| `API-dc0fe4c62fca17d8` | `org.apache.geode.distributed.Locator` | `hasLocator` | `()Z` | 23 | 36 |
| `API-b7c105c09eeda013` | `org.apache.geode.distributed.Locator` | `hasLocators` | `()Z` | 23 | 36 |
| `API-ecc482e5c28a3be3` | `org.apache.geode.distributed.Locator` | `hostAddress` | `Lorg/apache/geode/distributed/internal/tcpserver/HostAddress;` | 23 | 36 |
| `API-daba57dbd9e43177` | `org.apache.geode.distributed.Locator` | `hostnameForClients` | `Ljava/lang/String;` | 23 | 36 |
| `API-11b82836b1dbe876` | `org.apache.geode.distributed.Locator` | `isPeerLocator` | `()Z` | 23 | 36 |
| `API-e9595cf57f7ca870` | `org.apache.geode.distributed.Locator` | `isServerLocator` | `()Z` | 23 | 36 |
| `API-f2d5e3995e0c06ca` | `org.apache.geode.distributed.Locator` | `logFile` | `Ljava/io/File;` | 23 | 36 |
| `API-02f3d3c194916e09` | `org.apache.geode.distributed.Locator` | `startLocator` | `(ILjava/io/File;)Lorg/apache/geode/distributed/Locator;` | 23 | 36 |
| `API-8701fc7bff7ae4bf` | `org.apache.geode.distributed.Locator` | `startLocator` | `(ILjava/io/File;Ljava/net/InetAddress;)Lorg/apache/geode/distributed/Locator;` | 23 | 36 |
| `API-10d29abe538c53b7` | `org.apache.geode.distributed.Locator` | `startLocatorAndDS` | `(ILjava/io/File;Ljava/net/InetAddress;Ljava/util/Properties;)Lorg/apache/geode/distributed/Locator;` | 23 | 36 |
| `API-752e30803a19d3b9` | `org.apache.geode.distributed.Locator` | `startLocatorAndDS` | `(ILjava/io/File;Ljava/net/InetAddress;Ljava/util/Properties;ZZLjava/lang/String;)Lorg/apache/geode/distributed/Locator;` | 23 | 36 |
| `API-dcbf0afa7356d16c` | `org.apache.geode.distributed.Locator` | `startLocatorAndDS` | `(ILjava/io/File;Ljava/util/Properties;)Lorg/apache/geode/distributed/Locator;` | 23 | 36 |
| `API-66a17c585f2e798d` | `org.apache.geode.distributed.Locator` | `stop` | `()V` | 23 | 36 |
| `API-0181e3cac7e4d12e` | `org.apache.geode.distributed.Locator` | `toString` | `()Ljava/lang/String;` | 23 | 36 |

### management-class-name — 10 IDs

| API ID | Native receiver | Member | JVM descriptor | Owner | Consumer |
| --- | --- | --- | --- | --- | --- |
| `API-4a85530747f968be` | `org.apache.geode.management.configuration.ClassName` | `<init>` | `(Ljava/lang/String;)V` | 23 | 36 |
| `API-20c5cb56a44d71b3` | `org.apache.geode.management.configuration.ClassName` | `<init>` | `(Ljava/lang/String;Ljava/lang/String;)V` | 23 | 36 |
| `API-ea04062229a50a14` | `org.apache.geode.management.configuration.ClassName` | `<init>` | `(Ljava/lang/String;Ljava/util/Properties;)V` | 23 | 36 |
| `API-e9c7f5b8f7320b0c` | `org.apache.geode.management.configuration.ClassName` | `<type>` | `(type)` | 23 | 36 |
| `API-d03bd9d8fb8d47a7` | `org.apache.geode.management.configuration.ClassName` | `EMPTY` | `Lorg/apache/geode/management/configuration/ClassName;` | 23 | 36 |
| `API-e4b8e2d0d3c989b9` | `org.apache.geode.management.configuration.ClassName` | `equals` | `(Ljava/lang/Object;)Z` | 23 | 36 |
| `API-8636a518cb01d538` | `org.apache.geode.management.configuration.ClassName` | `getClassName` | `()Ljava/lang/String;` | 23 | 36 |
| `API-55cf0898ed0bef2b` | `org.apache.geode.management.configuration.ClassName` | `getInitProperties` | `()Ljava/util/Properties;` | 23 | 36 |
| `API-38beb76f634111bc` | `org.apache.geode.management.configuration.ClassName` | `hashCode` | `()I` | 23 | 36 |
| `API-1a7dddde29529782` | `org.apache.geode.management.configuration.ClassName` | `isClassNameValid` | `(Ljava/lang/String;)Z` | 23 | 36 |

### client-runtime-properties — 16 IDs

| API ID | Native receiver | Member | JVM descriptor | Owner | Consumer |
| --- | --- | --- | --- | --- | --- |
| `API-c752ad8a7c9181c7` | `org.apache.geode.distributed.ConfigurationProperties` | `<type>` | `(type)` | 23 | 36 |
| `API-3a948ad32cfd0b82` | `org.apache.geode.distributed.ConfigurationProperties` | `AVAILABILITY_ZONE` | `Ljava/lang/String;` | 23 | 36 |
| `API-6e51761b881e6f79` | `org.apache.geode.distributed.ConfigurationProperties` | `CACHE_XML_FILE` | `Ljava/lang/String;` | 23 | 36 |
| `API-4968e8a369bb7016` | `org.apache.geode.distributed.ConfigurationProperties` | `CONFLATE_EVENTS` | `Ljava/lang/String;` | 23 | 36 |
| `API-7ce5812c12aef91e` | `org.apache.geode.distributed.ConfigurationProperties` | `DELTA_PROPAGATION` | `Ljava/lang/String;` | 23 | 36 |
| `API-12263eab3ec8aaa5` | `org.apache.geode.distributed.ConfigurationProperties` | `DISTRIBUTED_SYSTEM_ID` | `Ljava/lang/String;` | 23 | 36 |
| `API-d14b75b139233662` | `org.apache.geode.distributed.ConfigurationProperties` | `DURABLE_CLIENT_ID` | `Ljava/lang/String;` | 23 | 36 |
| `API-91dfcb8a27f13396` | `org.apache.geode.distributed.ConfigurationProperties` | `DURABLE_CLIENT_TIMEOUT` | `Ljava/lang/String;` | 23 | 36 |
| `API-812c69362b7530f8` | `org.apache.geode.distributed.ConfigurationProperties` | `GROUPS` | `Ljava/lang/String;` | 23 | 36 |
| `API-4578fcba9f87d9d4` | `org.apache.geode.distributed.ConfigurationProperties` | `LOCATORS` | `Ljava/lang/String;` | 23 | 36 |
| `API-c43814cd0bbbc2e3` | `org.apache.geode.distributed.ConfigurationProperties` | `LOCK_MEMORY` | `Ljava/lang/String;` | 23 | 36 |
| `API-cdbc265942d7185d` | `org.apache.geode.distributed.ConfigurationProperties` | `NAME` | `Ljava/lang/String;` | 23 | 36 |
| `API-da50b9d48e26f4f7` | `org.apache.geode.distributed.ConfigurationProperties` | `REDUNDANCY_ZONE` | `Ljava/lang/String;` | 23 | 36 |
| `API-3fcd8794f1b8a4d7` | `org.apache.geode.distributed.ConfigurationProperties` | `ROLES` | `Ljava/lang/String;` | 23 | 36 |
| `API-66ca4a5ccfa6611a` | `org.apache.geode.distributed.ConfigurationProperties` | `SERIALIZABLE_OBJECT_FILTER` | `Ljava/lang/String;` | 23 | 36 |
| `API-45a724358cf6ab00` | `org.apache.geode.distributed.ConfigurationProperties` | `VALIDATE_SERIALIZABLE_OBJECTS` | `Ljava/lang/String;` | 23 | 36 |

### client-monitoring-properties — 14 IDs

| API ID | Native receiver | Member | JVM descriptor | Owner | Consumer |
| --- | --- | --- | --- | --- | --- |
| `API-fd28a7609208802c` | `org.apache.geode.distributed.ConfigurationProperties` | `ARCHIVE_DISK_SPACE_LIMIT` | `Ljava/lang/String;` | 23 | 36 |
| `API-57002a724030dd6f` | `org.apache.geode.distributed.ConfigurationProperties` | `ARCHIVE_FILE_SIZE_LIMIT` | `Ljava/lang/String;` | 23 | 36 |
| `API-2ca7074f3c819304` | `org.apache.geode.distributed.ConfigurationProperties` | `ENABLE_TIME_STATISTICS` | `Ljava/lang/String;` | 23 | 36 |
| `API-4c0e829184c778b6` | `org.apache.geode.distributed.ConfigurationProperties` | `LOG_DISK_SPACE_LIMIT` | `Ljava/lang/String;` | 23 | 36 |
| `API-4a2523c9e51a12b9` | `org.apache.geode.distributed.ConfigurationProperties` | `LOG_FILE` | `Ljava/lang/String;` | 23 | 36 |
| `API-fbb79d3181b40ff7` | `org.apache.geode.distributed.ConfigurationProperties` | `LOG_FILE_SIZE_LIMIT` | `Ljava/lang/String;` | 23 | 36 |
| `API-91796efed997dc8a` | `org.apache.geode.distributed.ConfigurationProperties` | `LOG_LEVEL` | `Ljava/lang/String;` | 23 | 36 |
| `API-e62a7b8b4b4cc5fc` | `org.apache.geode.distributed.ConfigurationProperties` | `MEMBER_TIMEOUT` | `Ljava/lang/String;` | 23 | 36 |
| `API-3a95f8f275faff9b` | `org.apache.geode.distributed.ConfigurationProperties` | `STATISTIC_ARCHIVE_FILE` | `Ljava/lang/String;` | 23 | 36 |
| `API-63fcc4dcd88a6ba8` | `org.apache.geode.distributed.ConfigurationProperties` | `STATISTIC_SAMPLE_RATE` | `Ljava/lang/String;` | 23 | 36 |
| `API-aa59ab14430a2ce8` | `org.apache.geode.distributed.ConfigurationProperties` | `STATISTIC_SAMPLING_ENABLED` | `Ljava/lang/String;` | 23 | 36 |
| `API-8d8aedeabf405cfd` | `org.apache.geode.distributed.ConfigurationProperties` | `THREAD_MONITOR_ENABLED` | `Ljava/lang/String;` | 23 | 36 |
| `API-e033223da64c59db` | `org.apache.geode.distributed.ConfigurationProperties` | `THREAD_MONITOR_INTERVAL` | `Ljava/lang/String;` | 23 | 36 |
| `API-ab3251baae1e9ea5` | `org.apache.geode.distributed.ConfigurationProperties` | `THREAD_MONITOR_TIME_LIMIT` | `Ljava/lang/String;` | 23 | 36 |

### client-authentication-properties — 5 IDs

| API ID | Native receiver | Member | JVM descriptor | Owner | Consumer |
| --- | --- | --- | --- | --- | --- |
| `API-b2347ae8d079e779` | `org.apache.geode.distributed.ConfigurationProperties` | `SECURITY_CLIENT_AUTH_INIT` | `Ljava/lang/String;` | 23 | 36 |
| `API-d9118a0dff272bc1` | `org.apache.geode.distributed.ConfigurationProperties` | `SECURITY_CLIENT_DHALGO` | `Ljava/lang/String;` | 23 | 36 |
| `API-36af523ec8b3729f` | `org.apache.geode.distributed.ConfigurationProperties` | `SECURITY_LOG_FILE` | `Ljava/lang/String;` | 23 | 36 |
| `API-8c2926e68d0bd7c9` | `org.apache.geode.distributed.ConfigurationProperties` | `SECURITY_LOG_LEVEL` | `Ljava/lang/String;` | 23 | 36 |
| `API-c4105a8417592e0d` | `org.apache.geode.distributed.ConfigurationProperties` | `SECURITY_PREFIX` | `Ljava/lang/String;` | 23 | 36 |

### client-tls-properties — 36 IDs

| API ID | Native receiver | Member | JVM descriptor | Owner | Consumer |
| --- | --- | --- | --- | --- | --- |
| `API-876bb3687fb7351d` | `org.apache.geode.distributed.ConfigurationProperties` | `CLUSTER_SSL_CIPHERS` | `Ljava/lang/String;` | 23 | 36 |
| `API-86c818aa8eb74cb5` | `org.apache.geode.distributed.ConfigurationProperties` | `CLUSTER_SSL_ENABLED` | `Ljava/lang/String;` | 23 | 36 |
| `API-b4a85af4157c072c` | `org.apache.geode.distributed.ConfigurationProperties` | `CLUSTER_SSL_KEYSTORE` | `Ljava/lang/String;` | 23 | 36 |
| `API-abd4c022f44a154a` | `org.apache.geode.distributed.ConfigurationProperties` | `CLUSTER_SSL_KEYSTORE_PASSWORD` | `Ljava/lang/String;` | 23 | 36 |
| `API-a714691bdcad6fde` | `org.apache.geode.distributed.ConfigurationProperties` | `CLUSTER_SSL_KEYSTORE_TYPE` | `Ljava/lang/String;` | 23 | 36 |
| `API-9f7e47eda19cd078` | `org.apache.geode.distributed.ConfigurationProperties` | `CLUSTER_SSL_PREFIX` | `Ljava/lang/String;` | 23 | 36 |
| `API-3ad098881670b666` | `org.apache.geode.distributed.ConfigurationProperties` | `CLUSTER_SSL_PROTOCOLS` | `Ljava/lang/String;` | 23 | 36 |
| `API-9c2ae020483e9e3a` | `org.apache.geode.distributed.ConfigurationProperties` | `CLUSTER_SSL_REQUIRE_AUTHENTICATION` | `Ljava/lang/String;` | 23 | 36 |
| `API-4f43f2c7f12f703d` | `org.apache.geode.distributed.ConfigurationProperties` | `CLUSTER_SSL_TRUSTSTORE` | `Ljava/lang/String;` | 23 | 36 |
| `API-680671385939a6f2` | `org.apache.geode.distributed.ConfigurationProperties` | `CLUSTER_SSL_TRUSTSTORE_PASSWORD` | `Ljava/lang/String;` | 23 | 36 |
| `API-c9672a92d4e0eaf2` | `org.apache.geode.distributed.ConfigurationProperties` | `SERVER_SSL_CIPHERS` | `Ljava/lang/String;` | 23 | 36 |
| `API-a7ad2d9d904fa4c6` | `org.apache.geode.distributed.ConfigurationProperties` | `SERVER_SSL_ENABLED` | `Ljava/lang/String;` | 23 | 36 |
| `API-04a5517d6c46a41b` | `org.apache.geode.distributed.ConfigurationProperties` | `SERVER_SSL_KEYSTORE` | `Ljava/lang/String;` | 23 | 36 |
| `API-9af6359974d8749a` | `org.apache.geode.distributed.ConfigurationProperties` | `SERVER_SSL_KEYSTORE_PASSWORD` | `Ljava/lang/String;` | 23 | 36 |
| `API-ef74e7bb30da490f` | `org.apache.geode.distributed.ConfigurationProperties` | `SERVER_SSL_KEYSTORE_TYPE` | `Ljava/lang/String;` | 23 | 36 |
| `API-f8ff3ffa2f7ebb33` | `org.apache.geode.distributed.ConfigurationProperties` | `SERVER_SSL_PROTOCOLS` | `Ljava/lang/String;` | 23 | 36 |
| `API-01b4047a58ffe2ef` | `org.apache.geode.distributed.ConfigurationProperties` | `SERVER_SSL_REQUIRE_AUTHENTICATION` | `Ljava/lang/String;` | 23 | 36 |
| `API-f1d5c83b23f8de33` | `org.apache.geode.distributed.ConfigurationProperties` | `SERVER_SSL_TRUSTSTORE` | `Ljava/lang/String;` | 23 | 36 |
| `API-c96e79979581d5b5` | `org.apache.geode.distributed.ConfigurationProperties` | `SERVER_SSL_TRUSTSTORE_PASSWORD` | `Ljava/lang/String;` | 23 | 36 |
| `API-1a81197c27e8130d` | `org.apache.geode.distributed.ConfigurationProperties` | `SSL_CIPHERS` | `Ljava/lang/String;` | 23 | 36 |
| `API-84e695c0eb927918` | `org.apache.geode.distributed.ConfigurationProperties` | `SSL_CLIENT_PROTOCOLS` | `Ljava/lang/String;` | 23 | 36 |
| `API-79deb2a61bdc6100` | `org.apache.geode.distributed.ConfigurationProperties` | `SSL_DEFAULT_ALIAS` | `Ljava/lang/String;` | 23 | 36 |
| `API-4bddf623151625bf` | `org.apache.geode.distributed.ConfigurationProperties` | `SSL_ENABLED_COMPONENTS` | `Ljava/lang/String;` | 23 | 36 |
| `API-c9a3df0121bf0769` | `org.apache.geode.distributed.ConfigurationProperties` | `SSL_ENDPOINT_IDENTIFICATION_ENABLED` | `Ljava/lang/String;` | 23 | 36 |
| `API-1597ed86510ec539` | `org.apache.geode.distributed.ConfigurationProperties` | `SSL_KEYSTORE` | `Ljava/lang/String;` | 23 | 36 |
| `API-bbba07c03949c1b3` | `org.apache.geode.distributed.ConfigurationProperties` | `SSL_KEYSTORE_PASSWORD` | `Ljava/lang/String;` | 23 | 36 |
| `API-19526fbcdbab43e3` | `org.apache.geode.distributed.ConfigurationProperties` | `SSL_KEYSTORE_TYPE` | `Ljava/lang/String;` | 23 | 36 |
| `API-6285d9d72915ed69` | `org.apache.geode.distributed.ConfigurationProperties` | `SSL_LOCATOR_ALIAS` | `Ljava/lang/String;` | 23 | 36 |
| `API-933385999b68d5af` | `org.apache.geode.distributed.ConfigurationProperties` | `SSL_PARAMETER_EXTENSION` | `Ljava/lang/String;` | 23 | 36 |
| `API-9eac2486250f044c` | `org.apache.geode.distributed.ConfigurationProperties` | `SSL_PROTOCOLS` | `Ljava/lang/String;` | 23 | 36 |
| `API-702f8ea90f7afb14` | `org.apache.geode.distributed.ConfigurationProperties` | `SSL_REQUIRE_AUTHENTICATION` | `Ljava/lang/String;` | 23 | 36 |
| `API-e37fb77791f4bc1a` | `org.apache.geode.distributed.ConfigurationProperties` | `SSL_SERVER_ALIAS` | `Ljava/lang/String;` | 23 | 36 |
| `API-0d19f0519b944024` | `org.apache.geode.distributed.ConfigurationProperties` | `SSL_TRUSTSTORE` | `Ljava/lang/String;` | 23 | 36 |
| `API-01a04469f648002f` | `org.apache.geode.distributed.ConfigurationProperties` | `SSL_TRUSTSTORE_PASSWORD` | `Ljava/lang/String;` | 23 | 36 |
| `API-c756a714151aa697` | `org.apache.geode.distributed.ConfigurationProperties` | `SSL_TRUSTSTORE_TYPE` | `Ljava/lang/String;` | 23 | 36 |
| `API-1655cba1237fb6c9` | `org.apache.geode.distributed.ConfigurationProperties` | `SSL_USE_DEFAULT_CONTEXT` | `Ljava/lang/String;` | 23 | 36 |

### host-tls-properties — 36 IDs

| API ID | Native receiver | Member | JVM descriptor | Owner | Consumer |
| --- | --- | --- | --- | --- | --- |
| `API-1640ac8f23c47926` | `org.apache.geode.distributed.ConfigurationProperties` | `GATEWAY_SSL_CIPHERS` | `Ljava/lang/String;` | 23 | 36 |
| `API-fc18c856ee514528` | `org.apache.geode.distributed.ConfigurationProperties` | `GATEWAY_SSL_ENABLED` | `Ljava/lang/String;` | 23 | 36 |
| `API-ad44a47a0a884fc2` | `org.apache.geode.distributed.ConfigurationProperties` | `GATEWAY_SSL_KEYSTORE` | `Ljava/lang/String;` | 23 | 36 |
| `API-ace79f429babf5f4` | `org.apache.geode.distributed.ConfigurationProperties` | `GATEWAY_SSL_KEYSTORE_PASSWORD` | `Ljava/lang/String;` | 23 | 36 |
| `API-eb3faaea0aa9dac1` | `org.apache.geode.distributed.ConfigurationProperties` | `GATEWAY_SSL_KEYSTORE_TYPE` | `Ljava/lang/String;` | 23 | 36 |
| `API-59017f2dc202a653` | `org.apache.geode.distributed.ConfigurationProperties` | `GATEWAY_SSL_PROTOCOLS` | `Ljava/lang/String;` | 23 | 36 |
| `API-7ad8e09e5c78116c` | `org.apache.geode.distributed.ConfigurationProperties` | `GATEWAY_SSL_REQUIRE_AUTHENTICATION` | `Ljava/lang/String;` | 23 | 36 |
| `API-e116fa49229864ec` | `org.apache.geode.distributed.ConfigurationProperties` | `GATEWAY_SSL_TRUSTSTORE` | `Ljava/lang/String;` | 23 | 36 |
| `API-5d79bf562e550bed` | `org.apache.geode.distributed.ConfigurationProperties` | `GATEWAY_SSL_TRUSTSTORE_PASSWORD` | `Ljava/lang/String;` | 23 | 36 |
| `API-07bc8fea43883a40` | `org.apache.geode.distributed.ConfigurationProperties` | `HTTP_SERVICE_SSL_CIPHERS` | `Ljava/lang/String;` | 23 | 36 |
| `API-88dbec80a6a43a6b` | `org.apache.geode.distributed.ConfigurationProperties` | `HTTP_SERVICE_SSL_ENABLED` | `Ljava/lang/String;` | 23 | 36 |
| `API-e4ab9dc32064af38` | `org.apache.geode.distributed.ConfigurationProperties` | `HTTP_SERVICE_SSL_KEYSTORE` | `Ljava/lang/String;` | 23 | 36 |
| `API-d31ff7144255674a` | `org.apache.geode.distributed.ConfigurationProperties` | `HTTP_SERVICE_SSL_KEYSTORE_PASSWORD` | `Ljava/lang/String;` | 23 | 36 |
| `API-7afd8dddc91ce3d5` | `org.apache.geode.distributed.ConfigurationProperties` | `HTTP_SERVICE_SSL_KEYSTORE_TYPE` | `Ljava/lang/String;` | 23 | 36 |
| `API-feeab790e3f56b2e` | `org.apache.geode.distributed.ConfigurationProperties` | `HTTP_SERVICE_SSL_PREFIX` | `Ljava/lang/String;` | 23 | 36 |
| `API-f42b55b11599d437` | `org.apache.geode.distributed.ConfigurationProperties` | `HTTP_SERVICE_SSL_PROTOCOLS` | `Ljava/lang/String;` | 23 | 36 |
| `API-cc85b1e9582d6637` | `org.apache.geode.distributed.ConfigurationProperties` | `HTTP_SERVICE_SSL_REQUIRE_AUTHENTICATION` | `Ljava/lang/String;` | 23 | 36 |
| `API-671e5a40bffa3ec9` | `org.apache.geode.distributed.ConfigurationProperties` | `HTTP_SERVICE_SSL_TRUSTSTORE` | `Ljava/lang/String;` | 23 | 36 |
| `API-16cbec01311d6454` | `org.apache.geode.distributed.ConfigurationProperties` | `HTTP_SERVICE_SSL_TRUSTSTORE_PASSWORD` | `Ljava/lang/String;` | 23 | 36 |
| `API-aba4be8af6308beb` | `org.apache.geode.distributed.ConfigurationProperties` | `JMX_MANAGER_SSL_CIPHERS` | `Ljava/lang/String;` | 23 | 36 |
| `API-988bc3f19d212fa9` | `org.apache.geode.distributed.ConfigurationProperties` | `JMX_MANAGER_SSL_ENABLED` | `Ljava/lang/String;` | 23 | 36 |
| `API-c58b516e78d9a927` | `org.apache.geode.distributed.ConfigurationProperties` | `JMX_MANAGER_SSL_KEYSTORE` | `Ljava/lang/String;` | 23 | 36 |
| `API-b189a9ec1e04fce2` | `org.apache.geode.distributed.ConfigurationProperties` | `JMX_MANAGER_SSL_KEYSTORE_PASSWORD` | `Ljava/lang/String;` | 23 | 36 |
| `API-31a07f31c07f868a` | `org.apache.geode.distributed.ConfigurationProperties` | `JMX_MANAGER_SSL_KEYSTORE_TYPE` | `Ljava/lang/String;` | 23 | 36 |
| `API-a6c17f2cdb7faf8e` | `org.apache.geode.distributed.ConfigurationProperties` | `JMX_MANAGER_SSL_PREFIX` | `Ljava/lang/String;` | 23 | 36 |
| `API-7391ad50ace1664a` | `org.apache.geode.distributed.ConfigurationProperties` | `JMX_MANAGER_SSL_PROTOCOLS` | `Ljava/lang/String;` | 23 | 36 |
| `API-974d396db41c5558` | `org.apache.geode.distributed.ConfigurationProperties` | `JMX_MANAGER_SSL_REQUIRE_AUTHENTICATION` | `Ljava/lang/String;` | 23 | 36 |
| `API-2643caed283a6b85` | `org.apache.geode.distributed.ConfigurationProperties` | `JMX_MANAGER_SSL_TRUSTSTORE` | `Ljava/lang/String;` | 23 | 36 |
| `API-e383a149267e4da7` | `org.apache.geode.distributed.ConfigurationProperties` | `JMX_MANAGER_SSL_TRUSTSTORE_PASSWORD` | `Ljava/lang/String;` | 23 | 36 |
| `API-f65d4ce77497f2dd` | `org.apache.geode.distributed.ConfigurationProperties` | `SSL_CLUSTER_ALIAS` | `Ljava/lang/String;` | 23 | 36 |
| `API-2ac49cbf89d52801` | `org.apache.geode.distributed.ConfigurationProperties` | `SSL_GATEWAY_ALIAS` | `Ljava/lang/String;` | 23 | 36 |
| `API-d16e2241b9c48713` | `org.apache.geode.distributed.ConfigurationProperties` | `SSL_GRPC_ALIAS` | `Ljava/lang/String;` | 23 | 36 |
| `API-f1592093c9dcde84` | `org.apache.geode.distributed.ConfigurationProperties` | `SSL_JMX_ALIAS` | `Ljava/lang/String;` | 23 | 36 |
| `API-96cf657b5f6daf50` | `org.apache.geode.distributed.ConfigurationProperties` | `SSL_SERVER_PROTOCOLS` | `Ljava/lang/String;` | 23 | 36 |
| `API-07f73aafab4b126a` | `org.apache.geode.distributed.ConfigurationProperties` | `SSL_WEB_ALIAS` | `Ljava/lang/String;` | 23 | 36 |
| `API-692ddd86c94d3d6c` | `org.apache.geode.distributed.ConfigurationProperties` | `SSL_WEB_SERVICE_REQUIRE_AUTHENTICATION` | `Ljava/lang/String;` | 23 | 36 |

### server-security-properties — 11 IDs

| API ID | Native receiver | Member | JVM descriptor | Owner | Consumer |
| --- | --- | --- | --- | --- | --- |
| `API-5081f6b71a7322f6` | `org.apache.geode.distributed.ConfigurationProperties` | `SECURITY_AUTH_TOKEN_ENABLED_COMPONENTS` | `Ljava/lang/String;` | 23 | 36 |
| `API-9f422847a505a405` | `org.apache.geode.distributed.ConfigurationProperties` | `SECURITY_CLIENT_ACCESSOR` | `Ljava/lang/String;` | 23 | 36 |
| `API-84941c81d6d6269d` | `org.apache.geode.distributed.ConfigurationProperties` | `SECURITY_CLIENT_ACCESSOR_PP` | `Ljava/lang/String;` | 23 | 36 |
| `API-13409448c6c9ace4` | `org.apache.geode.distributed.ConfigurationProperties` | `SECURITY_CLIENT_AUTHENTICATOR` | `Ljava/lang/String;` | 23 | 36 |
| `API-238dea4a66a5845e` | `org.apache.geode.distributed.ConfigurationProperties` | `SECURITY_MANAGER` | `Ljava/lang/String;` | 23 | 36 |
| `API-dbee74eb8dd81d92` | `org.apache.geode.distributed.ConfigurationProperties` | `SECURITY_MULTIUSER_AUTHENTICATION_ENABLED` | `Ljava/lang/String;` | 23 | 36 |
| `API-b38b79be4ed3d56c` | `org.apache.geode.distributed.ConfigurationProperties` | `SECURITY_PEER_AUTHENTICATOR` | `Ljava/lang/String;` | 23 | 36 |
| `API-d568418aa2c4cf72` | `org.apache.geode.distributed.ConfigurationProperties` | `SECURITY_PEER_AUTH_INIT` | `Ljava/lang/String;` | 23 | 36 |
| `API-5859c69e3f66b522` | `org.apache.geode.distributed.ConfigurationProperties` | `SECURITY_PEER_VERIFY_MEMBER_TIMEOUT` | `Ljava/lang/String;` | 23 | 36 |
| `API-96451159953450fd` | `org.apache.geode.distributed.ConfigurationProperties` | `SECURITY_POST_PROCESSOR` | `Ljava/lang/String;` | 23 | 36 |
| `API-4694aff59fa95a04` | `org.apache.geode.distributed.ConfigurationProperties` | `SECURITY_UDP_DHALGO` | `Ljava/lang/String;` | 23 | 36 |

### peer-transport-properties — 26 IDs

| API ID | Native receiver | Member | JVM descriptor | Owner | Consumer |
| --- | --- | --- | --- | --- | --- |
| `API-ba2886ea5b1e8895` | `org.apache.geode.distributed.ConfigurationProperties` | `ACK_SEVERE_ALERT_THRESHOLD` | `Ljava/lang/String;` | 23 | 36 |
| `API-384e57017ecd5087` | `org.apache.geode.distributed.ConfigurationProperties` | `ACK_WAIT_THRESHOLD` | `Ljava/lang/String;` | 23 | 36 |
| `API-1c40a7219a7817a7` | `org.apache.geode.distributed.ConfigurationProperties` | `BIND_ADDRESS` | `Ljava/lang/String;` | 23 | 36 |
| `API-fc1e04eb9d2b49fe` | `org.apache.geode.distributed.ConfigurationProperties` | `CONSERVE_SOCKETS` | `Ljava/lang/String;` | 23 | 36 |
| `API-a2beb4ce5b386baf` | `org.apache.geode.distributed.ConfigurationProperties` | `DISABLE_AUTO_RECONNECT` | `Ljava/lang/String;` | 23 | 36 |
| `API-fcd1722e6b4076aa` | `org.apache.geode.distributed.ConfigurationProperties` | `DISABLE_TCP` | `Ljava/lang/String;` | 23 | 36 |
| `API-374a418fd2dd5b8f` | `org.apache.geode.distributed.ConfigurationProperties` | `DISTRIBUTED_TRANSACTIONS` | `Ljava/lang/String;` | 23 | 36 |
| `API-ccf335dc41346c13` | `org.apache.geode.distributed.ConfigurationProperties` | `ENABLE_NETWORK_PARTITION_DETECTION` | `Ljava/lang/String;` | 23 | 36 |
| `API-419004646bad8c9a` | `org.apache.geode.distributed.ConfigurationProperties` | `ENFORCE_UNIQUE_HOST` | `Ljava/lang/String;` | 23 | 36 |
| `API-d117e64e7f6ab66f` | `org.apache.geode.distributed.ConfigurationProperties` | `LOCATOR_WAIT_TIME` | `Ljava/lang/String;` | 23 | 36 |
| `API-8506feea98300781` | `org.apache.geode.distributed.ConfigurationProperties` | `MAX_NUM_RECONNECT_TRIES` | `Ljava/lang/String;` | 23 | 36 |
| `API-59470778f6f9b232` | `org.apache.geode.distributed.ConfigurationProperties` | `MAX_WAIT_TIME_RECONNECT` | `Ljava/lang/String;` | 23 | 36 |
| `API-f02f650c74fb1cc5` | `org.apache.geode.distributed.ConfigurationProperties` | `MCAST_ADDRESS` | `Ljava/lang/String;` | 23 | 36 |
| `API-ebbb861f6871cbad` | `org.apache.geode.distributed.ConfigurationProperties` | `MCAST_FLOW_CONTROL` | `Ljava/lang/String;` | 23 | 36 |
| `API-909b9d64767cbca3` | `org.apache.geode.distributed.ConfigurationProperties` | `MCAST_PORT` | `Ljava/lang/String;` | 23 | 36 |
| `API-b1bb08c70b8a0396` | `org.apache.geode.distributed.ConfigurationProperties` | `MCAST_RECV_BUFFER_SIZE` | `Ljava/lang/String;` | 23 | 36 |
| `API-d922f86d1ee3b021` | `org.apache.geode.distributed.ConfigurationProperties` | `MCAST_SEND_BUFFER_SIZE` | `Ljava/lang/String;` | 23 | 36 |
| `API-b4aec1237fad0879` | `org.apache.geode.distributed.ConfigurationProperties` | `MCAST_TTL` | `Ljava/lang/String;` | 23 | 36 |
| `API-bd861c7f88e097a4` | `org.apache.geode.distributed.ConfigurationProperties` | `MEMBERSHIP_PORT_RANGE` | `Ljava/lang/String;` | 23 | 36 |
| `API-21a0890308a799c8` | `org.apache.geode.distributed.ConfigurationProperties` | `SOCKET_BUFFER_SIZE` | `Ljava/lang/String;` | 23 | 36 |
| `API-67a7883d5d68b7fe` | `org.apache.geode.distributed.ConfigurationProperties` | `SOCKET_LEASE_TIME` | `Ljava/lang/String;` | 23 | 36 |
| `API-f1b015ea552ea7c7` | `org.apache.geode.distributed.ConfigurationProperties` | `TCP_PORT` | `Ljava/lang/String;` | 23 | 36 |
| `API-1a08bf71dfd5e906` | `org.apache.geode.distributed.ConfigurationProperties` | `UDP_FRAGMENT_SIZE` | `Ljava/lang/String;` | 23 | 36 |
| `API-c3be66f823ea6a21` | `org.apache.geode.distributed.ConfigurationProperties` | `UDP_RECV_BUFFER_SIZE` | `Ljava/lang/String;` | 23 | 36 |
| `API-4352a2cf4aafa762` | `org.apache.geode.distributed.ConfigurationProperties` | `UDP_SEND_BUFFER_SIZE` | `Ljava/lang/String;` | 23 | 36 |
| `API-74d8ff740696b04f` | `org.apache.geode.distributed.ConfigurationProperties` | `USE_UDP_MEMBERSHIP_MESSENGER` | `Ljava/lang/String;` | 23 | 36 |

### host-administration-properties — 26 IDs

| API ID | Native receiver | Member | JVM descriptor | Owner | Consumer |
| --- | --- | --- | --- | --- | --- |
| `API-cb8a4671f7fab498` | `org.apache.geode.distributed.ConfigurationProperties` | `CLUSTER_CONFIGURATION_DIR` | `Ljava/lang/String;` | 23 | 36 |
| `API-977b569c75261825` | `org.apache.geode.distributed.ConfigurationProperties` | `DEPLOY_WORKING_DIR` | `Ljava/lang/String;` | 23 | 36 |
| `API-6bcdfcada53a7502` | `org.apache.geode.distributed.ConfigurationProperties` | `DISABLE_JMX` | `Ljava/lang/String;` | 23 | 36 |
| `API-1f7fe4c92f3b282a` | `org.apache.geode.distributed.ConfigurationProperties` | `ENABLE_CLUSTER_CONFIGURATION` | `Ljava/lang/String;` | 23 | 36 |
| `API-165b12eae5e0a05f` | `org.apache.geode.distributed.ConfigurationProperties` | `ENABLE_MANAGEMENT_REST_SERVICE` | `Ljava/lang/String;` | 23 | 36 |
| `API-e6d86d43f5874bc9` | `org.apache.geode.distributed.ConfigurationProperties` | `HTTP_SERVICES` | `Ljava/lang/String;` | 23 | 36 |
| `API-8ef8f426b36ff513` | `org.apache.geode.distributed.ConfigurationProperties` | `HTTP_SERVICE_BIND_ADDRESS` | `Ljava/lang/String;` | 23 | 36 |
| `API-689f6020bfab180a` | `org.apache.geode.distributed.ConfigurationProperties` | `HTTP_SERVICE_PORT` | `Ljava/lang/String;` | 23 | 36 |
| `API-6f80837069f1ce43` | `org.apache.geode.distributed.ConfigurationProperties` | `JMX_MANAGER` | `Ljava/lang/String;` | 23 | 36 |
| `API-77335915b0f19800` | `org.apache.geode.distributed.ConfigurationProperties` | `JMX_MANAGER_ACCESS_FILE` | `Ljava/lang/String;` | 23 | 36 |
| `API-7bfc3035830c379c` | `org.apache.geode.distributed.ConfigurationProperties` | `JMX_MANAGER_BIND_ADDRESS` | `Ljava/lang/String;` | 23 | 36 |
| `API-545cfecc0e5b74d3` | `org.apache.geode.distributed.ConfigurationProperties` | `JMX_MANAGER_HOSTNAME_FOR_CLIENTS` | `Ljava/lang/String;` | 23 | 36 |
| `API-3f08c29ec087e77d` | `org.apache.geode.distributed.ConfigurationProperties` | `JMX_MANAGER_HTTP_PORT` | `Ljava/lang/String;` | 23 | 36 |
| `API-072abf10f92e1dd9` | `org.apache.geode.distributed.ConfigurationProperties` | `JMX_MANAGER_PASSWORD_FILE` | `Ljava/lang/String;` | 23 | 36 |
| `API-a5ebc49ceadfffa2` | `org.apache.geode.distributed.ConfigurationProperties` | `JMX_MANAGER_PORT` | `Ljava/lang/String;` | 23 | 36 |
| `API-0bd5b4dd68e14645` | `org.apache.geode.distributed.ConfigurationProperties` | `JMX_MANAGER_START` | `Ljava/lang/String;` | 23 | 36 |
| `API-5ed52c297311c6f9` | `org.apache.geode.distributed.ConfigurationProperties` | `JMX_MANAGER_UPDATE_RATE` | `Ljava/lang/String;` | 23 | 36 |
| `API-0a08c9381bcd6eeb` | `org.apache.geode.distributed.ConfigurationProperties` | `LOAD_CLUSTER_CONFIGURATION_FROM_DIR` | `Ljava/lang/String;` | 23 | 36 |
| `API-1f1b964fe1ce0539` | `org.apache.geode.distributed.ConfigurationProperties` | `REMOTE_LOCATORS` | `Ljava/lang/String;` | 23 | 36 |
| `API-5d438d16222adfdd` | `org.apache.geode.distributed.ConfigurationProperties` | `REMOVE_UNRESPONSIVE_CLIENT` | `Ljava/lang/String;` | 23 | 36 |
| `API-67e891337ed1c18c` | `org.apache.geode.distributed.ConfigurationProperties` | `REST_JSON_STORAGE_FORMAT` | `Ljava/lang/String;` | 23 | 36 |
| `API-4edd1cc1917651f2` | `org.apache.geode.distributed.ConfigurationProperties` | `SERVER_BIND_ADDRESS` | `Ljava/lang/String;` | 23 | 36 |
| `API-71a138c4b6e0d499` | `org.apache.geode.distributed.ConfigurationProperties` | `START_DEV_REST_API` | `Ljava/lang/String;` | 23 | 36 |
| `API-196ac98b1cbfe6c9` | `org.apache.geode.distributed.ConfigurationProperties` | `START_LOCATOR` | `Ljava/lang/String;` | 23 | 36 |
| `API-f4b6e2aeff20cc6a` | `org.apache.geode.distributed.ConfigurationProperties` | `USER_COMMAND_PACKAGES` | `Ljava/lang/String;` | 23 | 36 |
| `API-b8c64cf0cd2fee3d` | `org.apache.geode.distributed.ConfigurationProperties` | `USE_CLUSTER_CONFIGURATION` | `Ljava/lang/String;` | 23 | 36 |


## Validation and closure — 2026-10-28

The user reported that the requested Gradle command passed for GemFire `10.4.0-build.3056.df98c835`: focused `ClientOnlyConfigurationUnitTests` and `GemfireUtilsUnitTests`, Spring integration-test compilation, spring-test test compilation, baseline pruning and transitional boundary verification. Local JUnit XML confirms 15 tests with zero failures, errors or skips (2026-10-28T04:15:28–04:15:29Z). No Gradle command was run by the agent.

After compilation, the native-reference scan and deterministic coverage generation/check passed. All 10 scope-policy tests pass; independent javap matched 372 declarations across 21 types, with ownership/source/ID/scope checks and both negative controls passing. All 2,723 assignments remain unique and receiver/member/descriptor-stable, and unresolved rows retain their assigned owners. Current inventory: 3,851 API IDs, 131 production native imports, 547 source/resource references and 508 Spring signature exposures. Source scopes are 449 required-client / three excluded-server / 95 unresolved. Task 72 closes at **291 assigned / 291 classified / zero remaining**; implementation stays with tasks 23/36. This validates the cleanup and classification, not full native-free compliance.
