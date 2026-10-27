# Client-only Spring configuration retirement

Task 63 is **resolved**. This ledger records configuration, function-registration, peer-function, property and feature-catalog retirement against GemFire `10.4.0-build.3056.df98c835`, Java 17 and Gradle 9.7. Task 59 remains open through task 64; this does not establish native-free Spring compliance. Progress and pending-validation notes below are historical; the final validation section records closure.

## Breaking changes and test dispositions

| Removed surface | Migration and retained behavior |
| --- | --- |
| `RegionShortcutWrapper`, `RegionShortcutConverter`, `RegionShortcutToDataPolicyConverter`; `serverRegionShortcut` on caching/entity annotations and configuration | Use `clientRegionShortcut` and client shortcuts. Retain client annotation configuration, pool selection and region creation assertions. Retire the three server wrapper/converter unit suites and server-accessor-only assertions. |
| `InterestPolicyType`, `InterestPolicyConverter`, `SubscriptionAttributesFactoryBean`, `ScopeType`, `ScopeConverter`, server `SubscriptionEvictionPolicy` and its converter | Remove peer subscription/scope and server queue configuration, including custom editor registrations and their seven unit suites. Client `InterestResultPolicy`, pool subscriptions and client persistence remain. The separate Spring Test module keeps its permitted native mock support using native shortcuts and private string normalization instead of these production classes. |
| `CacheTypeAwareRegionFactoryBean` server shortcut, scope and data-policy properties | Remove stored-only server properties. Configure client creation with shortcuts; retain read-only region metadata and the existing client factory's data-policy mapping. |
| Cache factory/configuration transaction writer, `ComposableTransactionWriter`, `TransactionListenerAdapter.beforeCommit` | No client writer replacement: native client writer registration is unsupported, as recorded in task 62. Retire writer composition tests and writer-only assertions. Preserve transaction listener registration, composition and event configuration; outcome delivery remains a task 64 scope decision. |
| `gfe:region-template`, `TemplateRegionParser`, peer/local-server schema types and peer region attributes | Use `gfe:client-region-template`. Convert template fixtures and preserve inheritance, definition-order rejection, callbacks, expiration, eviction, constraints and persistence assertions. Remove the peer-only `ignore-jta` fixture setting. Remove peer subscription, conflation, lock-grantor and partition-default assertions from the template test, and replace client expiration assumptions with assertions so retained checks cannot skip. |
| Peer membership/subscription/scope/WAN parser helpers and client-cache parsing of server lock/search/message settings | Schema rejects retired server XML. Client templates, local persistent shortcuts, loader/writer/listener callbacks, PDX persistence and pool subscriptions retain positive schema checks. |
| `FunctionServiceFactoryBean`, `FunctionServiceParser`, `DEFAULT_GEMFIRE_FUNCTION_SERVICE_NAME` and `gfe:function-service` | Deploy/register functions on the server outside Spring client configuration. Retire `FunctionServiceNamespaceIntegrationTests` and its XML, whose only assertion was local registration. Retain client function proxy, target, collector and XML configuration tests. Remove the unused exception-function server XML: the existing exception-propagation integration test already deploys its test JAR through `GemFireCluster`, and its client assertions remain intact. |
| `@OnMember`, `@OnMembers`, both member templates, six member execution implementations and three member bean-definition builders | Select `@OnRegion`, `@OnServer` or `@OnServers` with client region/pool configuration. Member/group targeting through a distributed system has no retained peer equivalent. Scanner and factory dispatch now accept only the three client annotations. The protected proxy hook is renamed from `isFunctionExecutedOnSingleServerOrSingleMember` to `isFunctionExecutedOnSingleServer`; overrides must follow that name. Retire the peer group-normalization test; preserve discovery/exclusion assertions with an `@OnServers` fixture. |
| Package-private `BatchingResultSender` and its unit suite | Retire the unused server result-sending helper and server-only chunking assertions. Client result collectors remain. |
| Five `EnableGemFireProperties` attributes, their defaults/mappings, and seven `GemFireProperties` constants listed below | Configure locator hosting, cluster configuration, WAN discovery, server queue handling and GFSH commands outside the Spring client application. Remove these annotation arguments and enum references; no replacement client facade is provided. Client cache XML, subscriptions and serialization configuration remain. The existing native `LocatorProcess` test fixture uses its two native property names directly instead of the retired public enum constants. |
| `ENFORCE_UNIQUE_HOST`, `REST_JSON_STORAGE_FORMAT`, `SERVER_BIND_ADDRESS`, `DEFAULT_ENFORCE_UNIQUE_HOST` and orphan annotation mappings for `enforceUniqueHost`/`redundancyZone` | Configure server partition placement, REST storage and listening addresses outside Spring client configuration. `availability-zone` and the native `redundancy-zone` alias remain in the property enum; the later shared-property audit below removes `bind-address`; the removed mappings had no corresponding annotation attributes. |
| Legacy `LazyWiringDeclarableSupportFunctionBasedIntegrationTests`, `HelloFunctionExecution` and native function-registration XML | Replace the local member function with `LazyWiringDeclarableSupportClientLoaderIntegrationTests` and a native `LOCAL` client region/cache loader. Preserve the greeting assertion, verify Spring property injection and initialized state, and test caller-argument precedence over XML parameters. The loader unregisters its lazy-wiring listener on close. |

`RegionDefinitionUsingBeansNamespaceIntegrationTests` keeps its normal and persistent client data-policy assertions. Its nested attributes beans no longer specify ignored data policies; the outer client factory still controls creation.

`ClientOnlyConfigurationUnitTests` checks removed annotation/bean surfaces and schema rejection alongside positive client XML. The retained integration tests use the existing Spring Test native mocks; they are not evidence of a completed official-mock cutover or real-server testing.

## Task 63 audit obligations and ownership

- The property scope review is complete for `EnableGemFireProperties`, `GemFirePropertiesConfiguration` and `GemFireProperties`, including security/SSL configuration. The shared-property audit below completes client-consumer evidence and corrects earlier bind-address/DSID assumptions. Driver-neutral property implementation and complete behavioral contracts remain with the existing migration tasks.
- Public registration, peer-member execution exports and the unused server result helper are removed; the legacy lazy-wiring fixture now uses a client-local loader. The function audit is complete; task 64 owns prerequisite relocation of required server function bodies, and tasks 25/32 own subsequent client SPI/consumer migration. Preserve supported client function-object submission pending exact-version member-level decisions in 64. Existing real-server exception tests remain intact and still need that fixture migration.
- Retirement of `RegionAttributesFactoryBean extends AttributesFactory` and migration of its callers to existing explicit client factory properties are validated below, including the fresh user-run boundary pass. Full SPI configuration and remaining native signatures belong to tasks 30/47 after cleanup parent 59. This split removes the dependency cycle while preserving native-free acceptance; no replacement native setter facade or unwrap is introduced.
- The public-surface audit, test dispositions and final evidence refresh are complete; see the final validation below. Remaining client-only migration continues with tasks 29–38; parent 59 stays open through 64.

## First-slice validation and boundary handling

Full Spring production/unit/integration and separate Spring Test production/unit sources compile. Focused reports contain **105 tests**, zero failures/errors/skips: 92 Spring unit, eight Spring namespace integration and five Spring Test helper tests. The namespace integration suite was rerun after removing the obsolete peer assumption. Ten coverage policy tests, deterministic generation, independent `javap` validation (372 public declarations across 21 types), the transitional boundary and `git diff --check` pass.

The regenerated inventory contains 537 Spring exposures, 135 production native imports and 584 source references. Native-referencing Java files remain **171 production / 112 unit / 128 integration**. These counts demonstrate remaining migration work, not full acceptance.

```sh
./gradlew --offline -DgemfireVersion=10.4.0-build.3056.df98c835 \
  :spring-data-vmware-gemfire:test \
  --tests '*EnableCachingDefinedRegionsUnitTests' \
  --tests '*EnableEntityDefinedRegionsUnitTests' \
  --tests '*AbstractCacheConfigurationUnitTests' \
  --tests '*ClientCacheConfigurationUnitTests' \
  --tests '*CustomEditorBeanFactoryPostProcessorUnitTests' \
  --tests '*TransactionListenerAdapterUnitTests' \
  --tests '*AbstractRegionParserUnitTests' \
  --tests '*ClientRegionFactoryBeanUnitTests' \
  --tests '*ClientOnlyConfigurationUnitTests' \
  :spring-data-vmware-gemfire:integrationTest \
  --tests '*Template*NamespaceIntegrationTests' \
  --tests '*TemplateRegionDefinitionOrderErrorNamespaceIntegrationTests' \
  --tests '*RegionDefinitionUsingBeansNamespaceIntegrationTests' \
  --tests '*TransactionEventHandlersIntegrationTests' \
  :spring-test-vmware-gemfire:test --tests '*GemFireMockObjectsSupportUnitTests' \
  pruneMigrationBoundaryBaseline verifyMigrationBoundary
python3 wayfinder-impl/audits/scan_native_references.py
python3 -m unittest discover -s wayfinder-impl/coverage -p 'test_*.py' -v
python3 wayfinder-impl/coverage/build_api_coverage.py
python3 wayfinder-impl/coverage/build_api_coverage.py --check
python3 wayfinder-impl/coverage/validate_coverage.py --javap
git diff --check
```

The boundary baseline prunes resolved occurrences. One retained declaration identity is normalized: `TransactionListenerAdapter` still implements native `TransactionListener`, but no longer implements `TransactionWriter`; the scanner includes the complete implements clause in its identity. This changes the retained entry's descriptive text without adding an occurrence or expanding the allowed boundary. Transitional boundary success does not mean native-free compliance.

All 3,851 frozen native API IDs remain. Current scope remains 362 required-client, 625 excluded-server and 2,864 unresolved; removing Spring exports does not automatically resolve native scope or implementation requirements.

## Function-registration slice — 2026-10-23

The two added rejection checks failed before removing the exports and passed afterward: the schema rejects `gfe:function-service`, and the namespace handler rejects it even with schema validation disabled. The retained client XML/configuration tests still construct region and server function proxies and preserve the configured collector and pool assertions. No native function invocation implementation or function-object submission contract changed.

Full Spring main/unit/integration and separate Spring Test main/unit sources compile. **18 focused tests pass with zero failures/errors/skips**: 15 unit and three configuration integration tests. These include native client configuration and existing Spring Test mock contexts, not real distributed function execution; the Docker exception-propagation test was compiled but not rerun. Its existing server deployment and client assertions are unchanged.

```sh
./gradlew --offline -DgemfireVersion=10.4.0-build.3056.df98c835 \
  :spring-data-vmware-gemfire:test \
  --tests '*ClientOnlyConfigurationUnitTests' \
  --tests '*GemfireFunctionProxyFactoryBeanUnitTests' \
  --tests '*ServerBasedFunctionExecutionBeanDefinitionBuilderUnitTests' \
  --tests '*FunctionExecutionComponentProviderUnitTests' \
  :spring-data-vmware-gemfire:integrationTest \
  --tests '*XmlConfiguredFunctionExecutionIntegrationTests' \
  --tests '*FunctionsWithClientCacheIntegrationTests' \
  --tests '*FunctionExecutionClientCacheIntegrationTests' \
  :spring-test-vmware-gemfire:compileTestJava \
  pruneMigrationBoundaryBaseline verifyMigrationBoundary
python3 wayfinder-impl/audits/scan_native_references.py
python3 wayfinder-impl/coverage/build_api_coverage.py
python3 wayfinder-impl/coverage/build_api_coverage.py --check
python3 wayfinder-impl/coverage/validate_coverage.py --javap
git diff --check
```

The transitional boundary passes with 17 obsolete occurrences pruned and no added allowances. Deterministic coverage generation and independent validation of 372 declarations across 21 types pass. Current evidence has **534 Spring exposures**, **135 production native imports**, **579 source references**, and native-referencing Java files **170 production / 112 unit / 127 integration**. All frozen IDs and scope/implementation statuses remain unchanged. These reductions do not establish native-free compliance or close tasks 59/63.

## Peer-function retirement slice — 2026-10-23

Exact-version bytecode inspection of `org.apache.geode.internal.cache.execute.InternalFunctionExecutionServiceImpl` confirms that the native member overloads construct `MemberFunctionExecutor` through distributed-system paths, while client pool/region-service server overloads construct `ServerFunctionExecutor`. This supports retiring the peer targets under the approved client-only scope; it does not imply that every native function API is unavailable to clients. The frozen native API dispositions remain unchanged for task 64.

Full Spring main/unit/integration and separate Spring Test main/unit sources compile. **35 focused tests pass with zero failures/errors/skips**: 29 unit and six integration tests. Two new proxy tests exercise single-server result extraction/argument forwarding and multi-server collected results. Existing Java/XML configuration tests preserve region proxies, pool selection and collector wiring. The new lazy-wiring fixture exercises native client-local XML initialization without a server. These checks do not establish remote function behavior, native-free Spring tests or the official mock cutover; the Docker exception test was compiled but not rerun.

```sh
./gradlew --offline -DgemfireVersion=10.4.0-build.3056.df98c835 \
  :spring-data-vmware-gemfire:test \
  --tests '*ClientOnlyConfigurationUnitTests' \
  --tests '*GemfireFunctionProxyFactoryBeanUnitTests' \
  --tests '*ServerBasedFunctionExecutionBeanDefinitionBuilderUnitTests' \
  --tests '*FunctionExecutionComponentProviderUnitTests' \
  --tests '*LazyWiringDeclarableSupportUnitTests' \
  :spring-data-vmware-gemfire:integrationTest \
  --tests '*XmlConfiguredFunctionExecutionIntegrationTests' \
  --tests '*FunctionsWithClientCacheIntegrationTests' \
  --tests '*FunctionExecutionClientCacheIntegrationTests' \
  --tests 'org.springframework.data.gemfire.function.config.FunctionExecutionIntegrationTests' \
  --tests '*LazyWiringDeclarableSupportClientLoaderIntegrationTests' \
  :spring-test-vmware-gemfire:compileTestJava \
  pruneMigrationBoundaryBaseline verifyMigrationBoundary
python3 wayfinder-impl/audits/scan_native_references.py
python3 wayfinder-impl/coverage/build_api_coverage.py
python3 wayfinder-impl/coverage/build_api_coverage.py --check
python3 wayfinder-impl/coverage/validate_coverage.py --javap
git diff --check
```

The first combined run passed compilation/tests and correctly rejected the renamed replacement fixture at the boundary. The explicit transitional test-scope adjustment replaces seven native occurrences in the retired function fixture with nine occurrences in its client-loader replacement (`ClientCache`, `Region`, `CacheLoader`, `LoaderHelper`), owned by task 40. This preserves native initialization coverage until the support/test migration; it is not a native-free test. No production allowances or scanner rules are added. The boundary passes when rerun separately after that adjustment and pruning obsolete entries: this slice removes 67 old occurrences and adds nine replacement-test occurrences, a net reduction of 58.

Deterministic coverage generation and independent validation of 372 declarations across 21 types pass. Evidence now contains **512 Spring exposures**, **134 production native imports**, **552 source references**, and native-referencing Java files **160 production / 111 unit / 127 integration**. All 3,851 frozen IDs and scope/implementation statuses remain unchanged. Tasks 59/63 stay open.

## Selected property retirement — 2026-10-23

Removed the following confirmed non-client configuration surfaces. This is a partial property audit; it does not classify all remaining native properties as client requirements.

| Native property | Removed Spring export | Evidence and disposition |
| --- | --- | --- |
| `cluster-configuration-dir` | `clusterConfigurationDirectory`, default and mapper | The annotation contract explicitly limits this directory to dedicated locators with cluster configuration enabled. It is separate from client `cache-xml-file`; no corresponding enum constant existed. |
| `enable-cluster-configuration` | `ENABLE_CLUSTER_CONFIGURATION` | Exact-version `InternalLocator.isSharedConfigurationEnabled` and `startConfigurationPersistenceService` read this setting for locator configuration hosting. |
| `load-cluster-configuration-from-dir` | `loadClusterConfigurationFromDirectory`, default/mapper and `LOAD_CLUSTER_CONFIGURATION_FROM_DIR` | Exact-version `InternalLocator.loadFromSharedConfigDir` reads the setting. The annotation describes loading the locator's cluster configuration directory. |
| `use-cluster-configuration` | `USE_CLUSTER_CONFIGURATION` | Exact-version `GemFireCacheImpl.requestSharedConfiguration` returns before consulting shared configuration when `isClient` is true. This does not remove client XML loading. |
| `start-locator` | `START_LOCATOR` | Exact-version `InternalDistributedSystem.startInitLocator` consumes it to start an embedded locator. Client locator discovery through pools remains. |
| `remote-locators` | `remoteLocators`, default/mapper and `REMOTE_LOCATORS` | Exact-version WAN module `WanLocatorDiscovererImpl.exchangeRemoteLocators` uses it for remote distributed-system discovery. It is not a client pool locator list. |
| `remove-unresponsive-client` | `removeUnresponsiveClient`, default/mapper and `REMOVE_UNRESPONSIVE_CLIENT` | Exact-version `HARegionQueue$BlockingHARegionQueue.waitForPermission` reads it for server-side queue pressure; the annotation describes dropping clients from primary/secondary servers. Client subscription configuration remains. |
| `user-command-packages` | `userCommandPackages`, default/mapper and `USER_COMMAND_PACKAGES` | Exact-version GFSH `CommandManager.getUserCommandPackages` consumes it for shell command discovery. |

The bytecode evidence above was inspected with `javap -c -p` against cached `com.vmware.gemfire` core, WAN and GFSH JARs at `10.4.0-build.3056.df98c835`. Class names identify reproducible inspection targets; in this first property slice, local source checkout documentation was used only for discovery. The deprecated directory setting is retired on its explicit Spring annotation contract, not on a claim that the pinned native version implements it.

Retention needs individual evidence. In that same binary, `PoolImpl` falls back from an empty pool availability zone to `DistributionConfig.getAvailabilityZone`; `ClientSideHandshakeImpl.handshakeWithServer` reads the distributed-system ID only in its WAN branch (correction from the later shared-property audit below). Its actual retained client consumer is SSL parameter-extension initialization, not that handshake branch. Those findings rule out blanket removal based on server-sounding names, but do not complete their public configuration contract review. Deployment working directory, membership/socket settings, the redundancy-zone alias, orphan property mappings, security and SSL surfaces still require review. Existing security/SSL integration assertions remain pending that review.

The enum regression checks reject every explicitly unsupported server property, with and without the `gemfire.` prefix. Annotation reflection checks reject the five removed attributes while retaining client XML, conflation and serialization attributes. Existing retained property integration assertions and native client-local XML loader tests remain intact. The locator fixture remains a transitional native test fixture; direct property strings do not make it native-free.

Full Spring main/unit/integration and separate Spring Test main/unit sources compile. **27 focused tests pass with zero failures/errors/skips**: 18 unit and nine integration tests. These tests use existing native mocks and a native client-local XML loader; no real locator/server integration was run and no official-mock or native-free claim is made.

```sh
./gradlew --offline -DgemfireVersion=10.4.0-build.3056.df98c835 \
  :spring-data-vmware-gemfire:test \
  --tests '*GemFirePropertiesUnitTests' \
  --tests '*ClientOnlyConfigurationUnitTests' \
  :spring-data-vmware-gemfire:integrationTest \
  --tests '*EnableGemFirePropertiesIntegrationTests' \
  --tests '*LazyWiringDeclarableSupportClientLoaderIntegrationTests' \
  :spring-test-vmware-gemfire:compileTestJava \
  pruneMigrationBoundaryBaseline verifyMigrationBoundary
python3 wayfinder-impl/audits/scan_native_references.py
python3 wayfinder-impl/coverage/build_api_coverage.py
python3 wayfinder-impl/coverage/build_api_coverage.py --check
python3 wayfinder-impl/coverage/validate_coverage.py --javap
git diff --check
```

The initial combined run passed compilation/tests but rejected the changed static test-property set at the boundary. Making the set an instance field removes shared static state. The two unit suites and both boundary tasks then passed on rerun. The baseline prunes seven retired production native constant references and the obsolete test static-state entry; no new allowances or scanner changes were added. Nineteen boundary tool tests pass.

Coverage generation/check and independent validation of 372 declarations across 21 types pass. The generated annotation-default inventory drops the five removed attributes. Coarser evidence counts remain **512 Spring exposures**, **134 production native imports**, **552 source references**, and native-referencing Java files **160 production / 111 unit / 127 integration**. All 3,851 frozen IDs and scope/implementation statuses remain unchanged. Task 63 remains open for the property audit, remaining function work and inherited attributes migration.


## Server placement and endpoint properties — 2026-10-23

The second property slice removes three more enum constants and the unused `DEFAULT_ENFORCE_UNIQUE_HOST` constant. It also removes the `enforceUniqueHost` and `redundancyZone` mappings: neither name is declared by `EnableGemFireProperties`, so neither can be supplied by that annotation. This does not remove the native `redundancy-zone` alias from `GemFireProperties`.

| Property | Decision | Evidence |
| --- | --- | --- |
| `enforce-unique-host` | Retire Spring enum/default/orphan mapper | `ClusterDistributionManager` reads the flag into unique-zone enforcement; `PartitionedRegionBucketMgmtHelper.bucketIsAllowedOnThisHost`, `PRHARedundancyProvider` and `PartitionedRegionRebalanceOp` consume enforcement for server partition placement. `LonerDistributionManager` exposes a getter too; that alone does not establish a supported client placement operation. |
| `server-bind-address` | Retire Spring enum | Pinned `AcceptorImpl.calcBindHostName` uses it for the server acceptor and `RestAgent.getBindAddressForHttpService` uses it for HTTP service binding. The separate `bind-address` was provisionally retained here; the later shared-property audit removes it after tracing outgoing client sockets. |
| `rest-json-storage-format` | Retire Spring enum | `gemfire-web-api`'s `AbstractBaseController.convert(String)` reads it through `getRestJsonStorageFormat()` for REST-side JSON conversion. Client JSON/PDX serialization APIs remain. |
| `availability-zone` / `redundancy-zone` | Retain enum constants | Pinned `DistributionConfigImpl.setRedundancyZone` forwards to `setAvailabilityZone`, and `getRedundancyZone` forwards to `getAvailabilityZone`. `PoolImpl` uses that availability zone as a fallback when no pool-specific zone is set. Removing the orphan annotation mapper does not retire this client alias. |

Inspection used the cached core binary at `10.4.0-build.3056.df98c835` and the local native repository's committed source at **`df98c8357afe85853d9a4776255d4f0e7cf064cb`**, matching that build's revision suffix. The REST module is not cached as a binary; its decision uses the source at that revision, not the working checkout. Reproduce with `git -C /Users/uk016635/projects/gemfire show df98c835:<path>` and `javap -c -p` against the pinned core JAR. Source call-site searches covered production Java across modules.

Additional retention evidence: pinned `InternalDistributedSystem` initializes the class loader working directory from `getDeployWorkingDir` and consults `getLockMemory` during initialization. `TXManagerImpl.isDistributed` consults `getDistributedTransactions` without a client guard. These properties remain pending full client contract review; their presence in shared initialization is not blanket client acceptance. The remaining audit must also address the unmapped `conserveSockets` annotation attribute and the `tombstone-gc-threshold` mapping, alongside membership/transport, security and SSL settings.

The existing enum rejection regression now covers all three removed names, with and without the `gemfire.` prefix. At this point positive lookup assertions retained `bind-address`, `availability-zone` and `redundancy-zone`; the later shared-property audit replaces the bind-address assertion with rejection. No retained integration assertion or client XML surface was removed.

Full Spring main/unit/integration and separate Spring Test main/unit source compilation passes. The same focused command as the selected-property slice above was rerun: **27 tests pass with zero failures/errors/skips** (10 property unit, eight client configuration unit, seven property integration and two native client XML loader tests). The transitional boundary passes with **three additional native constant references pruned**, no new allowances and all 19 boundary-tool tests passing. No real-server tests were run; these results do not establish native-free Spring tests or official-mock completion.

`scan_native_references.py`, `build_api_coverage.py`, its `--check` mode, `validate_coverage.py --javap` and `git diff --check` pass. Independent validation still matches 372 declarations across 21 types; the inventory retains 512 Spring exposures, 134 production imports and 552 source references. Native-referencing Java file counts remain 160 production / 111 unit / 127 integration. All 3,851 frozen API IDs and scope/implementation statuses remain unchanged. Task 63 stays open.


## Peer socket and tombstone property retirement — 2026-10-27

Evidence uses native source at `df98c8357afe85853d9a4776255d4f0e7cf064cb`, matching the pinned `10.4.0-build.3056.df98c835` build. Removed three annotation attributes and their defaults, two property mappings and two enum constants. Removed the commented-out tombstone enum candidate as well.

| Removed surface | Exact-version evidence | Client disposition and migration |
| --- | --- | --- |
| `conserveSockets`, `DEFAULT_CONSERVE_SOCKETS`, `CONSERVE_SOCKETS` | `ConfigurationProperties.CONSERVE_SOCKETS` describes cluster-member messaging. `DirectChannel` passes `getConserveSockets()` to `p2p.shareSockets`; `InternalDistributedSystem` initializes `shareSockets`, and its resource-ownership accessors feed peer message sending and `ConnectionTable`'s P2P reader pool. The Spring annotation had no mapper, so its argument was already ineffective. | Remove the annotation argument and enum reference. Do not add a mapper for this peer option. Client connection pooling remains configured through the pool's connection limits and idle timeout. |
| `socketLeaseTime`, `DEFAULT_SOCKET_LEASE_TIME`, `SOCKET_LEASE_TIME`, `socket-lease-time` mapper | `DirectChannel` passes `getSocketLeaseTime()` to `p2p.idleConnectionTimeout`. Other getter occurrences are configuration copying/access and management reporting, rather than client pool connection management. | Remove the peer lease setting. Choose the client pool's `idleTimeout` based on client requirements; it is a separate setting, not an equivalent lease contract. |
| `tombstoneGcThreshold`, `DEFAULT_TOMBSTONE_THRESHOLD`, `tombstone-gc-threshold` mapper | `TombstoneService.EXPIRED_TOMBSTONE_LIMIT_DEFAULT` reads the JVM system property `gemfire.tombstone-gc-threshold` using `Integer.getInteger`, not a distributed-system property. `ReplicateTombstoneSweeper` uses this limit for batch expiration. `getSweeper` selects that sweeper for distributed replicated regions without a server proxy; client regions use the non-replicated sweeper. | Remove the annotation argument; do not replace it with a global system-property side effect. Client tombstone handling remains native behavior. The retired mapper emitted an unsupported distributed-system property for a non-default annotation value. |

Pinned `PoolImpl` obtains `idleTimeout`, connection limits and socket options from its pool attributes independently. Existing `PoolFactoryBeanUnitTests` retain assertions that these settings reach the native factory and resulting pool. The client XML regression now explicitly asserts pool idle timeout and minimum/maximum connections, alongside subscriptions and local persistence/callbacks. Reflection and property-name rejection checks include the retired exports. No existing client assertions or fixtures were removed.

Remaining property review still includes membership/transport, security/SSL and the shared initialization settings recorded above. This slice does not resolve task 63 or classify any frozen native API rows.

Validation: **52 focused tests**, zero failures/errors/skips (10 property enum, 25 pool factory, eight client configuration, seven annotation integration, two client XML loader integration). Full Spring main/unit/integration and separate Spring Test sources compile. The 19 boundary-tool tests pass; pruning removes only the two native enum references, with no new allowances. Deterministic generation and independent validation of 372 declarations across 21 types pass. The annotation-default inventory shrinks from 234 to 231; all 3,851 frozen API IDs and scope counts remain unchanged. Transitional boundary success is not native-free acceptance. Real-server tests were not run for this configuration-only slice.

```sh
./gradlew --offline -DgemfireVersion=10.4.0-build.3056.df98c835 \
  :spring-data-vmware-gemfire:test \
  --tests '*GemFirePropertiesUnitTests' \
  --tests '*ClientOnlyConfigurationUnitTests' \
  --tests '*PoolFactoryBeanUnitTests' \
  :spring-data-vmware-gemfire:integrationTest \
  --tests '*EnableGemFirePropertiesIntegrationTests' \
  --tests '*LazyWiringDeclarableSupportClientLoaderIntegrationTests' \
  :spring-test-vmware-gemfire:compileTestJava \
  pruneMigrationBoundaryBaseline verifyMigrationBoundary
python3 wayfinder-impl/audits/scan_native_references.py
python3 wayfinder-impl/coverage/build_api_coverage.py
python3 wayfinder-impl/coverage/build_api_coverage.py --check
python3 wayfinder-impl/coverage/validate_coverage.py --javap
git diff --check
```


## Peer acknowledgment and obsolete transport properties — 2026-10-27

This slice removes seven annotation attributes, their defaults and mappings, and seven enum constants. Evidence again uses native commit `df98c8357afe85853d9a4776255d4f0e7cf064cb`, matching `10.4.0-build.3056.df98c835`.

| Retired property / annotation attribute | Pinned native evidence | Migration |
| --- | --- | --- |
| `ack-wait-threshold` / `ackWaitThreshold`; `ack-severe-alert-threshold` / `ackSevereAlertThreshold` | `ReplyProcessor21` reads the thresholds while waiting for distributed member replies and reporting/suspecting unresponsive members. Other production consumers include `DistributionImpl`, `PeerSenderImpl`, distributed/partitioned regions, bucket advisors and initial-image/state-flush operations. | Remove these peer alert arguments and enum references. Client pool `readTimeout` controls server response waits and `subscriptionAckInterval` controls subscription acknowledgments; neither is an equivalent replacement for peer alert thresholds. |
| `tcp-port` / `tcpPort` | `DirectChannel` passes the configured port into the peer `TCPConduit`; remaining getters are configuration copying, remote transport configuration and management reporting. `ClientCacheFactory` forces empty distributed-system locators, and `InternalDistributedSystem` selects `LonerDistributionManager` when locators are empty, skipping cluster membership. | Remove the peer listening-port argument and enum reference. Client server/locator destinations remain pool endpoints. |
| `disable-tcp` / `disableTcp`; `udp-fragment-size` / `udpFragmentSize`; `udp-recv-buffer-size` / `udpReceiveBufferSize`; `udp-send-buffer-size` / `udpSendBufferSize` | `DistributionConfig` marks these obsolete since UDP messaging deprecation in GemFire 10.0. In the pinned `DistributionConfigImpl`, the getters return fixed values and the setters throw for non-default values. | Remove these obsolete transport arguments and enum references (including the misspelled `UPD_SEND_BUFFER_SIZE`). No compatibility stubs or replacement mappings are added. |

Call-site searches covered production Java across native modules. The client retention check follows `PoolImpl`: it obtains socket-connect timeout, read timeout and subscription acknowledgment interval from pool attributes, independently of these distributed-system settings, and passes connection timeouts into `ConnectionFactoryImpl`. Existing `PoolFactoryBeanUnitTests` assert factory forwarding and resulting pool values. The positive client XML test now asserts all three settings in parsed pool bean definitions. Existing client assertions and fixtures remain intact; property-name rejection (plain and `gemfire.` prefixed) and annotation reflection checks cover all seven removed exports.

Membership, security/SSL and shared initialization properties remain under review. This change does not resolve task 63 or reclassify frozen native API rows.

Validation reran the exact Gradle command from the preceding peer socket/tombstone slice: **52 focused tests pass with zero failures/errors/skips** (10 enum, 25 pool factory, eight client configuration, seven annotation integration and two client XML loader integration). Full affected main/unit/integration and separate Spring Test sources compile. The 19 boundary-tool tests and transitional boundary pass; the baseline removes seven additional obsolete enum references, with no new allowances. No real-server tests were run for this configuration-only slice.

Native-reference scanning, deterministic coverage generation/check and independent `javap` validation pass (372 declarations across 21 types). The first coverage attempt overlapped compilation and correctly failed its missing-class/stale-evidence checks; rerunning after main compilation passed. Annotation defaults shrink from 231 to 224. All 3,851 frozen API IDs, scope counts, 512 Spring exposures, 134 production imports and 552 source references remain unchanged. These checks establish regression coverage against the transitional baseline, not native-free compliance. `git diff --check` passes.


## Peer membership and reconnect properties — 2026-10-27

This slice removes six enum constants and four annotation attributes with their defaults/mappings. Evidence uses production source at native commit `df98c8357afe85853d9a4776255d4f0e7cf064cb`, matching the pinned `10.4.0-build.3056.df98c835` build. Searches cover production Java across native modules; configuration copying, management reporting and historical test helpers do not establish client behavior.

| Retired property / Spring export | Pinned native evidence | Migration |
| --- | --- | --- |
| `enable-network-partition-detection` / `ENABLE_NETWORK_PARTITION_DETECTION`, `enableNetworkPartitionDetection` | `ServiceConfig` supplies membership partition detection; `InternalLocator`, `DistributedRegion` and `PeerReceiverImpl` use the flag for locator/peer behavior. | Remove the annotation argument and enum reference. Client subscription recovery remains pool behavior. |
| `locator-wait-time` / `LOCATOR_WAIT_TIME`, `locatorWaitTimeout` | `GMSJoinLeave` uses this timeout when discovering/joining membership; `MembershipLocatorImpl` supplies it to its locator request handler. | Remove the peer startup wait. Client locator destinations and connection timeouts belong to the client pool; this setting is not a client locator retry contract. |
| `membership-port-range` / `MEMBERSHIP_PORT_RANGE`, `membershipPortRange` | `DirectChannel` configures peer ports; `GMSHealthMonitor` binds the failure-detection socket and `TCPMessenger` selects membership transport ports from this range. | Remove the peer port-range argument and enum reference. Client server/locator endpoints remain supported. |
| `disable-auto-reconnect` / `DISABLE_AUTO_RECONNECT`; `max-num-reconnect-tries` / `MAX_NUM_RECONNECT_TRIES`; `max-wait-time-reconnect` / `MAX_WAIT_TIME_RECONNECT`, `maxWaitTimeReconnect` | `InternalDistributedSystem.tryReconnect`/`reconnect` implement membership rejoin following a forced disconnect or required-role loss. Production callers are `disconnect(String, boolean)` after a `ForcedDisconnectException` and `DistributedRegion` role-loss handling. `LonerDistributionManager.getRootCause()` and `getDistribution()` return null. Remaining consumers are cluster distribution/membership, locator reconnect, configuration and management reporting. | Remove these peer reconnect exports. Client pool retry attempts, pings and subscription redundancy are separate controls, not equivalent replacements. |

`ClientCacheFactory.create` sets distributed-system `locators` to empty. `InternalDistributedSystem.initialize` then creates a `LonerDistributionManager` and marks membership/cluster join skipped. Client `PoolImpl` instead reads retry attempts, ping interval and subscription redundancy from pool attributes, wiring the operation executor, live-server pinger and queue manager. `QueueManagerImpl.recoverPrimary` recovers client subscription connections independently of distributed-system auto-reconnect.

The shared `GemFireDriverContract.configuration()` no longer sets `disable-auto-reconnect`; no contract assertion is removed. The native driver's `NativeServerProcess` keeps its explicit flag because it actually hosts a peer server. Other generic fixture properties and the remaining function fixtures still require task 64's audit. This removal does not restrict arbitrary driver property maps or claim those maps are fully audited.

`member-timeout` remains: pinned `LonerDistributionManager` constructs `ThreadMonitorImpl` when monitoring is enabled, and `ThreadMonitorTask` uses half the configured member timeout as its backoff threshold. That is a concrete client-side consumer despite the property's membership name. Its public documentation/default contract, security/SSL, remaining transport and shared initialization settings still need review.

Existing enum rejection tests now cover the six removed names both plain and prefixed with `gemfire.`; annotation reflection covers the four removed attributes. The client XML regression adds assertions for retry attempts, ping interval and subscription redundancy. Existing pool factory tests already verify forwarding and resulting values for these settings and locator endpoints. Client XML parsers and all prior retained assertions remain.

Validation: **138 focused tests pass with zero failures/errors/skips**: 52 Spring tests (10 property enum, 25 pool factory, eight client configuration, seven annotation integration and two client XML loader integration), 43 mock driver contract tests and 43 native 10.4 driver contract tests. Full affected Spring main/unit/integration and separate Spring Test sources compile. Native contract tests exercise client-local regions; real-server recovery and network failover were not tested. The 19 boundary-tool tests and transitional boundary pass. Pruning removes six additional obsolete enum references, with no new allowances.

Native-reference scanning, deterministic coverage generation/check and independent `javap` validation pass (372 declarations across 21 types). Annotation defaults decrease from 224 to 220. All 3,851 frozen API IDs and scope counts, 512 Spring exposures, 134 production imports and 552 source references remain unchanged. These results do not establish native-free compliance. Task 63 remains open.

```sh
./gradlew --offline -DgemfireVersion=10.4.0-build.3056.df98c835 \
  :spring-data-vmware-gemfire:test \
  --tests '*GemFirePropertiesUnitTests' \
  --tests '*ClientOnlyConfigurationUnitTests' \
  --tests '*PoolFactoryBeanUnitTests' \
  :spring-data-vmware-gemfire:integrationTest \
  --tests '*EnableGemFirePropertiesIntegrationTests' \
  --tests '*LazyWiringDeclarableSupportClientLoaderIntegrationTests' \
  :spring-test-vmware-gemfire:compileTestJava \
  :spring-data-gemfire-driver-mock:test --tests '*MockDriverContractTests' \
  :spring-data-gemfire-driver-10.4:integrationTest --tests '*GemFire104DriverContractTests' \
  pruneMigrationBoundaryBaseline verifyMigrationBoundary
python3 wayfinder-impl/audits/scan_native_references.py
python3 wayfinder-impl/coverage/build_api_coverage.py
python3 wayfinder-impl/coverage/build_api_coverage.py --check
python3 wayfinder-impl/coverage/validate_coverage.py --javap
git diff --check
```


## Peer authentication and obsolete UDP security — 2026-10-27

This slice removes three enum constants, the peer authentication annotation attribute and its property mapping, and automatic peer authentication initialization on client caches. Evidence uses production sources at native commit `df98c8357afe85853d9a4776255d4f0e7cf064cb`, matching `10.4.0-build.3056.df98c835`.

| Retired property / Spring export | Pinned native evidence | Migration |
| --- | --- | --- |
| `security-peer-auth-init` / `SECURITY_PEER_AUTH_INIT`, `EnableSecurity.peerAuthenticationInitializer`, `spring.data.gemfire.security.peer.authentication-initializer` mapping | `GMSAuthenticator.getCredentials` reads this property and calls `Handshake.getCredentials` with `isPeer=true`. Other consumers are membership configuration, `InternalLocator`, `GMSMembership` and `PeerReceiverImpl`; client caches use the loner distribution manager described above. | Remove the peer annotation argument, enum references and Spring environment property. Configure peer credentials in server deployment/driver test fixtures when needed. Client applications retain `clientAuthenticationInitializer`, its `spring.data.gemfire.security.client.authentication-initializer` mapping and username/password configuration. |
| `security-peer-verifymember-timeout` / `SECURITY_PEER_VERIFY_MEMBER_TIMEOUT` | `ServiceConfig` passes the timeout into `GMSMembership.membershipCheckTimeout`, used when awaiting membership confirmation for an incoming connection. Remaining getters copy/report configuration. | Remove the peer verification timeout enum reference. It is not a client authentication or pool connection timeout. |
| `security-udp-dhalgo` / misspelled `SECURITY_UDP_DHALO` | `DistributionConfigImpl.setSecurityUDPDHAlgo` throws for every non-default value; the default is empty. No active transport encryption consumer remains in the pinned production source. | Remove the obsolete property/enum reference. Client SSL configuration remains subject to the separate SSL audit. |

`AutoConfiguredAuthenticationConfiguration` now sets only `security-client-auth-init` when client authentication is requested. Pinned `ClientSideHandshakeImpl` reads that property during client/server authentication; `AuthenticateUserOp.getCredentialBytes` uses it to obtain credentials with `isPeer=false`. The client initializer and credential implementation are preserved. No peer compatibility aliases, native unwraps or ignored annotation attributes were introduced. This change removes the Spring mapping; it does not add general validation of arbitrary environment/native property maps.

Test dispositions: the peer initializer input and success assertion in `GeodeIntegratedSecurityConfigurationUnitTests.toGemFirePropertiesIsCorrect` and `EnableGemFirePropertiesIntegrationTests.securityGemFirePropertiesConfiguration` are retired and replaced with absence assertions. All other assertions in both tests remain. New focused configuration tests verify that requested authentication selects the client initializer without adding peer authentication or copying credentials into cache properties; absent authentication or incomplete credentials preserve an explicit client initializer and unrelated properties. Annotation reflection preserves client initializer/username/password attributes while rejecting the peer attribute. Existing enum rejection checks cover all three removed properties both plain and `gemfire.` prefixed. Client XML parser coverage remains.

The existing real-server `AutoConfiguredAuthenticationConfigurationIntegrationTests` fixture and its authenticated data-access assertions are preserved, but not run in this slice. Security manager/postprocessor, Shiro remnants, token components, SSL channels, shared initialization settings and the member-timeout documentation/default contract still need individual review. Task 63 remains open; no frozen native API rows are reclassified.

Validation: **34 focused tests pass with zero failures/errors/skips** (10 property enum, nine client configuration, three integrated security mapper, three authentication configuration, seven annotation integration and two client XML loader integration). Full affected Spring main/unit/integration and separate Spring Test sources compile. The 19 boundary-tool tests and transitional boundary pass. Three obsolete enum-reference allowances are pruned with no new allowances; the native-source boundary count decreases from 5,194 to 5,191. This is regression acceptance against the transitional baseline, not native-free compliance.

Native-reference scanning, deterministic coverage generation/check and independent `javap` validation pass (372 declarations across 21 types). Annotation defaults decrease from 220 to 219. All 3,851 frozen API IDs and scope counts, 512 Spring exposures, 134 production imports and 552 source references remain unchanged. The source audit records the additional focused unit-test file; no native dependency was added. `git diff --check` passes.

```sh
./gradlew --offline -DgemfireVersion=10.4.0-build.3056.df98c835 \
  :spring-data-vmware-gemfire:test \
  --tests '*GemFirePropertiesUnitTests' \
  --tests '*ClientOnlyConfigurationUnitTests' \
  --tests '*GeodeIntegratedSecurityConfigurationUnitTests' \
  --tests '*AutoConfiguredAuthenticationConfigurationUnitTests' \
  :spring-data-vmware-gemfire:integrationTest \
  --tests '*EnableGemFirePropertiesIntegrationTests' \
  --tests '*LazyWiringDeclarableSupportClientLoaderIntegrationTests' \
  :spring-test-vmware-gemfire:compileTestJava \
  pruneMigrationBoundaryBaseline verifyMigrationBoundary
python3 wayfinder-impl/audits/scan_native_references.py
python3 wayfinder-impl/coverage/build_api_coverage.py
python3 wayfinder-impl/coverage/build_api_coverage.py --check
python3 wayfinder-impl/coverage/validate_coverage.py --javap
git diff --check
```


## Obsolete Shiro integration — 2026-10-27

This slice removes `ApacheShiroSecurityConfiguration`, `EnableSecurity.shiroIniResourcePath`, `GemFireProperties.SECURITY_SHIRO_INIT`, the unused `shiro.properties` realm fixture and the `shiro-spring` dependency/catalog entry. The two remaining authentication classes use Spring's `Assert` instead of Shiro's utility. Client authentication and client XML remain supported.

Evidence uses native commit `df98c8357afe85853d9a4776255d4f0e7cf064cb`, matching `10.4.0-build.3056.df98c835`. `SecurityServiceFactory.create` constructs integrated security using `CallbackInstantiator.getSecurityManager` and `getPostProcessor`; those instantiate the configured callbacks and initialize them with properties. Neither the pinned internal security package nor `ConfigurationProperties` contains Shiro references. The removed Spring class independently registered a Shiro security manager, lifecycle/dependency postprocessors and global `SecurityUtils` state. The Spring mapper's Shiro detection method already returned false unconditionally, and INI configuration was already rejected for 10.4. Its obsolete detection helpers are removed without changing manager/postprocessor mapping behavior.

Migration: remove imports of the deleted configuration class, the annotation argument and enum references. Configure client credentials/initializers through the retained client authentication APIs. Explicit `spring.data.gemfire.security.shiro.enabled=true` now fails with a migration message; explicit false remains valid disablement and preserves client authentication. Existing rejection of `spring.data.gemfire.security.shiro.ini-resource-path` and raw `security-shiro-init` remains, including properties inherited from `Properties` defaults. No replacement Shiro facade, native unwrap or ignored exported attribute is added. This validation is in the existing security configuration/cache factory paths; it is not a general validator for unused environment keys.

Test dispositions: the obsolete annotation-map rejection test is replaced by explicit Shiro-enablement rejection, and annotation reflection confirms the attribute is absent. A new disablement test verifies the client initializer survives. The retained annotation integration suite now asserts that none of the three Shiro infrastructure beans is registered. All other retained assertions remain. The orphan realm resource has no callers and is deleted. The two existing cache-factory INI rejection tests remain and pass, including failure before cache creation and exclusion of the supplied private path from the error. The real-server authentication fixture is preserved but was not run.

Validation: **81 focused tests pass with zero failures/errors/skips** (10 property enum, 46 client cache factory, nine client configuration, four integrated security mapper, three authentication configuration, seven annotation integration and two client XML loader integration). Full affected Spring main/unit/integration and separate Spring Test sources compile. The 19 boundary-tool tests and transitional boundary pass. One obsolete Shiro-class native import allowance is pruned, with no additions; native-source occurrences decrease from 5,191 to 5,190. Gradle dependency insight confirms no Shiro dependency in the Spring module's runtime classpath.

Native-reference scanning, deterministic coverage generation/check and independent `javap` validation pass (372 declarations across 21 types). Annotation defaults decrease from 219 to 218; source coverage references decrease from 552 to 551. All 3,851 frozen API IDs, scope counts, 512 Spring exposures and 134 production imports remain unchanged. Coverage guards rejected an attempt during compilation and later detected a final import-order edit; compilation and coverage validation were repeated after those changes. The source audit now records 159 main files with native references and 507 noncomment reference lines. These checks do not establish native-free compliance. `git diff --check` passes.

Task 63 remains open. Security manager/postprocessor require separate client-contract review because native shared initialization still loads them; this slice does not classify them as client-supported or server-only. Token components, SSL, shared initialization properties, member-timeout documentation/defaults, the function surface/fixture audit and inherited native region attributes remain outstanding.

```sh
./gradlew --offline -DgemfireVersion=10.4.0-build.3056.df98c835 \
  :spring-data-vmware-gemfire:test \
  --tests '*GemFirePropertiesUnitTests' \
  --tests '*ClientOnlyConfigurationUnitTests' \
  --tests '*GeodeIntegratedSecurityConfigurationUnitTests' \
  --tests '*AutoConfiguredAuthenticationConfigurationUnitTests' \
  --tests '*ClientCacheFactoryBeanUnitTests' \
  :spring-data-vmware-gemfire:integrationTest \
  --tests '*EnableGemFirePropertiesIntegrationTests' \
  --tests '*LazyWiringDeclarableSupportClientLoaderIntegrationTests' \
  :spring-test-vmware-gemfire:compileTestJava \
  pruneMigrationBoundaryBaseline verifyMigrationBoundary \
  :spring-data-vmware-gemfire:dependencyInsight --dependency shiro --configuration runtimeClasspath
python3 wayfinder-impl/audits/scan_native_references.py
python3 wayfinder-impl/coverage/build_api_coverage.py
python3 wayfinder-impl/coverage/build_api_coverage.py --check
python3 wayfinder-impl/coverage/validate_coverage.py --javap
git diff --check
```


## Server security callbacks and management token components — 2026-10-27

This slice removes `EnableSecurity.securityManagerClass`, `securityManagerClassName`, `securityPostProcessorClass` and `securityPostProcessorClassName`, their mappings/constants in `GeodeIntegratedSecurityConfiguration`, and `GemFireProperties.SECURITY_MANAGER`, `SECURITY_POST_PROCESSOR` and `SECURITY_AUTH_TOKEN_ENABLED_COMPONENTS`. `EnableSecurity` now documents client authentication and exposes only the client initializer and username/password attributes.

Evidence uses native commit `df98c8357afe85853d9a4776255d4f0e7cf064cb`, matching `10.4.0-build.3056.df98c835`:

| Retired surface | Native evidence and scope |
| --- | --- |
| Security manager and postprocessor | `GemFireCacheImpl` lines 1067–1089 initializes/reuses integrated security only under `!isClient`; the client branch explicitly creates a no-op security service. `InternalDistributedSystem` lines 706–708 still constructs the shared system security service and can initialize supplied callbacks, but that service is not the client cache's security service. `Get70`/`GetAll70`, `BaseCommandQuery` and subscription delivery code use authorization/postprocessing while handling server requests. `PostProcessor` documents server security properties and transforming values before sending them to the requester. Shared initialization side effects do not make these client authentication APIs. |
| `security-auth-token-enabled-components` | The only operational getter consumer is `InternalLocator` lines 892–896, which configures management REST token authentication. Other references declare, copy or report the configuration. This setting does not supply client credentials; the `security-token` credential and custom client initializer support are unaffected. |
| Retained client authentication | `AuthenticateUserOp.getCredentialBytes` reads `security-client-auth-init` and calls `Handshake.getCredentials` with `isPeer=false`. Client initializer/username/password configuration and the native client credential implementation remain. |

Migration: remove the four server annotation arguments and three enum references. Configure security managers, postprocessors and management token components on the servers/locators or their driver integration fixtures. Nonblank `spring.data.gemfire.security.manager.class-name` and `spring.data.gemfire.security.postprocessor.class-name` now fail in the security mapper with a server-configuration migration message, without echoing the supplied class name. Blank values remain unset. Existing Shiro rejection messages now point to client authentication instead of the retired manager setting. This removes typed Spring exports and mappings; it does not validate all arbitrary native properties or unused environment keys.

Test dispositions: server callback success assertions in the security mapper and annotation-property integration tests become absence assertions while retaining client configuration assertions. The mapper also verifies rejection of both stale environment keys and client environment precedence with blank server settings. Annotation reflection covers all four removed attributes, and enum checks cover the three properties plain and `gemfire.` prefixed. The disabled-bean-overriding fixture now configures a client initializer and preserves its cache-name, bean-definition and SSL assertions. The separate Spring Test object-creation fixture now instantiates an `AuthInitialize` callback via its existing mock property mechanism, verifies the property and callback instance, and clears the fixture reference after the class; it tests object creation, not authentication against a server. No suite is deleted or disabled.

The real-server `AutoConfiguredAuthenticationConfigurationIntegrationTests` and its `TestSecurityManager` fixture remain unchanged: the manager configures the server process, while the client supplies username/password. That real-server suite was not run. The separate Spring Test helper's broader native callback simulation and public server fixture remain under its existing dependency exception for later fixture audit; this slice makes no native-free claim. Task 63 remains open for SSL/shared properties, member-timeout documentation/defaults, function fixture placement and inherited native region attributes. Frozen native scope classifications are unchanged.

Validation: **85 focused Java tests pass with zero failures/errors/skips** (10 property enum, 46 client cache factory, nine client configuration, six integrated security mapper, three authentication configuration, seven annotation-property integration, one disabled-bean-overriding integration, two client XML loader integration and one separate Spring Test callback-construction test). Full affected Spring main/unit/integration and separate Spring Test sources compile. The 19 boundary-tool tests and transitional boundary pass. Four obsolete native-reference allowances are pruned without additions; native-source occurrences decrease from 5,190 to 5,186. This establishes regression acceptance against the transitional baseline, not native-free compliance.

Native-reference scanning, deterministic coverage generation/check and independent `javap` validation pass (372 declarations across 21 types). Annotation defaults decrease from 218 to 214. All 3,851 frozen API IDs and scope counts (362 required, 625 excluded, 2,864 unresolved), 512 Spring exposures, 134 production imports and 551 source references remain unchanged. The source audit retains 159 main files and 507 noncomment reference lines; test files containing native references decrease from 111 to 110 and their reference lines from 329 to 328. `git diff --check` passes.

```sh
./gradlew --offline -DgemfireVersion=10.4.0-build.3056.df98c835 \
  :spring-data-vmware-gemfire:test \
  --tests '*GemFirePropertiesUnitTests' \
  --tests '*ClientOnlyConfigurationUnitTests' \
  --tests '*GeodeIntegratedSecurityConfigurationUnitTests' \
  --tests '*AutoConfiguredAuthenticationConfigurationUnitTests' \
  --tests '*ClientCacheFactoryBeanUnitTests' \
  :spring-data-vmware-gemfire:integrationTest \
  --tests '*EnableGemFirePropertiesIntegrationTests' \
  --tests '*UsingAnnotationConfigWithBeanDefinitionOverridingDisabledIntegrationTests' \
  --tests '*LazyWiringDeclarableSupportClientLoaderIntegrationTests' \
  :spring-test-vmware-gemfire:test \
  --tests '*GemFireObjectCreationTriggeredByGemFirePropertyConfigurationIntegrationTests' \
  pruneMigrationBoundaryBaseline verifyMigrationBoundary
python3 wayfinder-impl/audits/scan_native_references.py
python3 wayfinder-impl/coverage/build_api_coverage.py
python3 wayfinder-impl/coverage/build_api_coverage.py --check
python3 wayfinder-impl/coverage/validate_coverage.py --javap
git diff --check
```


## Client-only SSL configuration — 2026-10-27

This slice narrows `EnableSsl.Component` to `LOCATOR` and `SERVER`, removes `serverProtocols` and `webRequireAuthentication` and their mappings, and removes five `GemFireProperties` constants: `SSL_CLUSTER_ALIAS`, `SSL_GATEWAY_ALIAS`, `SSL_WEB_ALIAS`, `SSL_SERVER_PROTOCOLS` and `SSL_WEB_SERVICE_REQUIRE_AUTHENTICATION`. The Spring annotation and property enum default for enabled SSL components is now `locator,server`; an empty annotation component array uses the same client channels. Component output is sorted for deterministic property generation.

Evidence uses native commit `df98c8357afe85853d9a4776255d4f0e7cf064cb`, matching `10.4.0-build.3056.df98c835`. Paths below are within native `gemfire-core/src/main/java/org/apache/geode`:

| Surface | Evidence and disposition |
| --- | --- |
| `LOCATOR` and `SERVER` channels and aliases | `cache/client/internal/AutoConnectionSourceImpl` lines 119–124 creates the locator discovery client with the `LOCATOR` socket creator. `ConnectionFactoryImpl` lines 119–136 selects `SERVER` for ordinary client connections; `GATEWAY` is selected only for gateway use. `internal/net/SSLConfigurationFactory` selects the locator/server aliases for those channels. These names describe remote endpoints and remain necessary for client TLS. |
| Cluster, gateway and web channels/aliases; web authentication | `SSLConfigurationFactory` lines 53–113 handles these independently from the client locator/server branches. Only the web branch overrides authentication with `getSSLWebRequireAuthentication()`. These channels and settings are outside the supported cache-client scope. The aggregate `ALL` selection also includes those nonclient channels and is retired from the Spring enum. |
| Client/generic protocols; server protocols | `internal/net/SocketCreator` lines 467–526 configures outgoing sockets in client mode and calls `configureProtocols(true, ...)`, selecting client protocols. `internal/net/SSLConfig.getClientProtocols` falls back to generic protocols when the client-specific value is empty. Server protocols are selected for server-mode sockets. Subscription updates also use `forClient().connect` in `internal/cache/tier/sockets/CacheClientUpdater`; they do not require a client-hosted TLS listener. |
| Authentication, certificate stores, hostname checks and extension | `SocketCreator.configureClientSSLSocket` applies hostname validation, ciphers and client SSL parameter extensions before handshaking. Its `SSLPeerUnverifiedException` handler still reads `sslConfig.isRequireAuth()` (lines 505–509), so `requireAuthentication` remains. `SSLConfigurationFactory` lines 135–153 supplies the shared key/trust stores, default alias, default context and extension to these client channels. Mutual TLS requirements belong on the remote server/locator. Documentation no longer claims that enabling the default context itself enables hostname validation. |

Migration: replace explicit `ALL`/`all` with `LOCATOR, SERVER`/`locator,server`, or select only the remote endpoint type needed by the application. Remove the two retired annotation arguments and five enum references. Configure server protocol restrictions, web authentication and server-only component aliases on the server/locator deployment. Nonblank `spring.data.gemfire.security.ssl.server.protocols`, `web-require-authentication`, and certificate alias suffixes `cluster`, `gateway`, `web`, `jmx`, `grpc`, `all` fail explicitly in the SSL mapper without echoing their values. Server/unknown component selections fail even when mixed with a supported channel. Blank retired settings remain unset. This validates the Spring SSL configuration path; arbitrary raw native `Properties` and unused environment keys are not globally validated. No compatibility stubs or ignored exported settings are added.

Test dispositions: existing SSL mapper/context fixtures now select locator/server channels and aliases. Server/web success assertions become absence assertions in Spring properties, while native distributed-system property snapshots retain assertions for the native default web-authentication value (`false`). The four default-context integration tests still create real native client caches; their formerly peer-named test methods/configuration fixtures now describe clients. All retained certificate, cipher, protocol, authentication, context and hostname assertions remain. Seven additional unit tests cover default/empty channel selection, environment precedence and duplicate/whitespace handling, locator-only selection, retired/unknown channels, stale property rejection without value disclosure, blank retired settings and annotation/enum shape. Existing property-enum rejection checks cover all five removed keys both plain and `gemfire.` prefixed. Client XML/factory tests remain in the focused run. No suite is deleted or disabled.

The real-server `EnableSslConfigurationIntegrationTests` fixture remains unchanged and compiles, including its server-side `ssl-enabled-components=all` configuration. It was not run: this slice verifies property mapping and native client-cache construction, not a TLS handshake against a running server. Shared property review, member-timeout documentation/defaults, function fixture placement and inherited native region attributes keep task 63 open. Frozen native API scope classifications are unchanged.

Validation: **88 focused Java tests pass with zero failures/errors/skips** (10 property enum, 46 client cache factory, nine SSL configuration, nine client configuration, four native SSL default-context integration, seven annotation-property integration, one disabled-bean-overriding integration and two client XML loader integration). Full affected Spring main/unit/integration and separate Spring Test sources compile. The 19 boundary-tool tests and transitional boundary pass. Nine obsolete SSL native-reference allowances are pruned without additions; native-source occurrences decrease from 5,186 to 5,177. A cosmetic local-variable rename initially tripped the boundary's occurrence identity check and was reverted before pruning. This is regression acceptance against the transitional baseline, not native-free compliance.

Native-reference scanning, deterministic coverage generation/check and independent `javap` validation pass (372 declarations across 21 types). Annotation defaults decrease from 214 to 212. All 3,851 frozen API IDs and scope counts (362 required, 625 excluded, 2,864 unresolved), 512 Spring exposures, 134 production imports and 551 source references remain unchanged. The source audit retains 159 main files with 507 noncomment reference lines and 110 test files with 328 reference lines. `git diff --check` passes.

```sh
./gradlew --offline -DgemfireVersion=10.4.0-build.3056.df98c835 \
  :spring-data-vmware-gemfire:test \
  --tests '*GemFirePropertiesUnitTests' \
  --tests '*EnableSslConfigurationUnitTests' \
  --tests '*ClientOnlyConfigurationUnitTests' \
  --tests '*ClientCacheFactoryBeanUnitTests' \
  :spring-data-vmware-gemfire:integrationTest \
  --tests '*EnableSslConfigurationDefaultContextIntegrationTests' \
  --tests '*EnableGemFirePropertiesIntegrationTests' \
  --tests '*UsingAnnotationConfigWithBeanDefinitionOverridingDisabledIntegrationTests' \
  --tests '*LazyWiringDeclarableSupportClientLoaderIntegrationTests' \
  :spring-test-vmware-gemfire:compileTestJava \
  pruneMigrationBoundaryBaseline verifyMigrationBoundary
python3 wayfinder-impl/audits/scan_native_references.py
python3 wayfinder-impl/coverage/build_api_coverage.py
python3 wayfinder-impl/coverage/build_api_coverage.py --check
python3 wayfinder-impl/coverage/validate_coverage.py --javap
git diff --check
```


## Peer socket buffer and client member timeout — 2026-10-27

This slice removes `EnableGemFireProperties.socketBufferSize`, its default and distributed-system property mapping, and `GemFireProperties.SOCKET_BUFFER_SIZE`. Client pool socket buffer configuration remains supported. `memberTimeout` remains, with documentation corrected to describe its client thread-monitor use, native range and default behavior.

Evidence uses native commit `df98c8357afe85853d9a4776255d4f0e7cf064cb`, matching `10.4.0-build.3056.df98c835`. Paths below are within native `gemfire-core/src/main/java/org/apache/geode` unless stated otherwise:

| Surface | Evidence and disposition |
| --- | --- |
| Distributed-system `socket-buffer-size` | `distributed/internal/direct/DirectChannel` lines 132–135 maps the distribution configuration getter to `p2p.tcpBufferSize`. Other distribution configuration getter references copy or report the setting. This controls peer transport and is retired from the Spring annotation and property enum. |
| Client pool socket buffer | `cache/client/internal/PoolImpl` line 247 reads pool attributes, then lines 305–308 pass that buffer size to `ConnectionFactoryImpl`. Its lines 119–136 pass the value to the connection connector. This is separate from the distribution configuration and remains supported. |
| Client `member-timeout` | `distributed/internal/LonerDistributionManager` lines 119–124 creates the native thread monitor when enabled. `internal/monitoring/ThreadMonitorTask` lines 93–107 passes half the member timeout to its backoff monitor. `ThreadMonitorTaskBackoffMonitor` lines 12–65 permits a JVM execution-threshold override and backs off monitoring cycles when execution reaches the threshold. This is client monitoring behavior, independent of pool connection/read/server detection settings. |
| Member timeout range/default | `distributed/internal/DistributionConfig` lines 2094–2109 declares the default and range 10–600000. `gemfire-membership/src/main/java/org/apache/geode/distributed/internal/membership/api/MembershipConfig` line 40 supplies the default 5000 ms. Spring already uses that default and omits default-valued annotation settings; no runtime default change is needed. |

Migration: remove the retired distributed-system annotation argument and enum reference. Configure client buffers through `@ClientCacheApplication(socketBufferSize = ...)`, `@EnablePool(socketBufferSize = ...)`, the pool factory or `<gfe:pool socket-buffer-size="...">`. Existing pool environment settings (`spring.data.gemfire.pool.socket-buffer-size`, `.default.socket-buffer-size` and `.<name>.socket-buffer-size`) remain. The removed Spring mapping is not replaced with an ignored compatibility setting. Arbitrary native properties are not globally validated by this slice.

Test dispositions: enum checks reject the retired property plain and `gemfire.` prefixed; annotation reflection rejects the removed attribute and retains `memberTimeout`. Two new annotation-property integration tests verify that default member timeout remains omitted and an explicit 8000 ms reaches generated properties; both verify absence of the retired socket property. Existing pool integration tests retain default/named buffer values and environment precedence, and client cache factory/default pool and client XML loader tests remain covered. These tests verify configuration mapping, not monitor timing or socket behavior against a running server. No suite is deleted or disabled.

Validation: **82 focused Java tests pass with zero failures/errors/skips** (10 property enum, nine client configuration, 46 client cache factory, nine annotation-property integration, two pool integration, four client cache property integration and two client XML loader integration). Full affected Spring main/unit/integration and separate Spring Test sources compile. All 19 boundary-tool tests and the transitional boundary pass. One obsolete allowance is pruned without additions; native-source occurrences decrease from 5,177 to 5,176. This is regression acceptance against the transitional baseline, not native-free compliance.

Native-reference scanning, deterministic coverage generation/check and independent `javap` validation pass (372 declarations across 21 types). Annotation defaults decrease from 212 to 211. All 3,851 frozen API IDs and scope counts (362 required, 625 excluded, 2,864 unresolved), 512 Spring exposures, 134 production imports and 551 source references remain unchanged. The source audit retains 159 main files with 507 noncomment reference lines and 110 test files with 328 reference lines. `git diff --check` passes. The member-timeout documentation/default review is complete; shared properties, function fixture placement and inherited native region attributes keep task 63 open.

```sh
./gradlew --offline -DgemfireVersion=10.4.0-build.3056.df98c835 \
  :spring-data-vmware-gemfire:test \
  --tests '*GemFirePropertiesUnitTests' \
  --tests '*ClientOnlyConfigurationUnitTests' \
  --tests '*ClientCacheFactoryBeanUnitTests' \
  :spring-data-vmware-gemfire:integrationTest \
  --tests '*EnableGemFirePropertiesIntegrationTests' \
  --tests '*PoolPropertiesIntegrationTests' \
  --tests '*ClientCachePropertiesIntegrationTests' \
  --tests '*LazyWiringDeclarableSupportClientLoaderIntegrationTests' \
  :spring-test-vmware-gemfire:compileTestJava \
  pruneMigrationBoundaryBaseline verifyMigrationBoundary
python3 wayfinder-impl/audits/scan_native_references.py
python3 wayfinder-impl/coverage/build_api_coverage.py
python3 wayfinder-impl/coverage/build_api_coverage.py --check
python3 wayfinder-impl/coverage/validate_coverage.py --javap
git diff --check
```


## Deployment and distributed-transaction properties — 2026-10-27

This slice removes `EnableGemFireProperties.deployWorkingDirectory`, its default/mapping and `GemFireProperties.DEPLOY_WORKING_DIRECTORY`. It also removes `GemFireProperties.DISTRIBUTED_TRANSACTIONS`, completing the Spring property-enum counterpart of task 62's unsupported distributed-mode retirement. Ordinary client transactions remain. Delta propagation and memory locking remain with client-specific documentation.

Evidence uses native commit `df98c8357afe85853d9a4776255d4f0e7cf064cb`, matching `10.4.0-build.3056.df98c835`. Paths below are within native `gemfire-core/src/main/java/org/apache/geode`:

| Surface | Evidence and disposition |
| --- | --- |
| Deployment working directory | `distributed/internal/InternalDistributedSystem` line 609 and `internal/cache/InternalCacheBuilder` lines 188–193 initialize the shared classloader working directory. That alone does not establish client JAR deployment: `internal/cache/GemFireCacheImpl` lines 1593–1599 skips `applyJarAndXmlFromClusterConfig()` for clients. Its lines 1677–1684 contain the startup call to `loadJarsFromWorkingDirectory()`. Client declarative cache initialization remains outside that server-only branch. Retire the Spring deployment setting; preserve client XML. |
| Distributed transactions | `internal/cache/TXManagerImpl` lines 2081–2095 falls back to the original `distributed-transactions` configuration when no thread override exists. Its lines 380–405 select the distributed coordinator at begin. `internal/cache/DistTXStateProxyImplOnCoordinator` lines 306–353 selects `DistClientTXStateStub` for server-proxy regions; `internal/cache/tx/DistClientTXStateStub` lines 41–110 rejects distributed client transaction operations. Retire the enum setting, consistent with task 62; retain ordinary client transactions. |
| Delta propagation | `internal/cache/LocalRegion.shouldExtractDelta` lines 1856–1874 checks the distribution configuration flag before the server-proxy branch. This is a real client setting; retain it. Delta extraction does not by itself guarantee delivery of a delta over every server connection. |
| Memory locking | `distributed/internal/InternalDistributedSystem` lines 774–782 checks `getLockMemory()` before the loner/peer branch at lines 794–812. Lines 886–911 check memory availability and invoke native memory locking, with explicit overcommit override handling. This is client-process behavior with Linux/native operating-system constraints; retain it. |

Migration: remove the retired annotation argument and enum references. Put client application JARs on the application's classpath; manage server deployments on servers. Use ordinary client transactions rather than enabling distributed mode. The removed mappings are not replaced with ignored settings or compatibility stubs. Arbitrary raw native `Properties` are not globally validated by this slice.

Test dispositions: property-enum checks reject both retired keys plain and `gemfire.` prefixed. Annotation reflection rejects the removed deployment attribute and retains delta/memory attributes. The annotation integration suite verifies default omission and explicit `delta-propagation=false`/`lock-memory=true` mapping using its existing mock cache fixture; it does not lock OS memory or exercise delta transport. A new native client/server transaction test enables distributed mode through the raw native property and verifies that commit raises `UnsupportedOperationInTransactionException`. Existing normal client transaction and setter-based rejection tests remain, as do client XML and pool tests. No suite is deleted or disabled.

Validation: **94 focused Java tests pass with zero failures/errors/skips** (10 property enum, nine client configuration, 46 client cache factory, 10 annotation-property integration, two pool integration, four client cache property integration, two client XML loader integration and 11 native transaction tests). Full affected Spring main/unit/integration and separate Spring Test sources compile. All 19 boundary-tool tests and the transitional boundary pass. Two obsolete allowances are pruned without additions; native-source occurrences decrease from 5,176 to 5,174. This is regression acceptance against the transitional baseline, not native-free compliance.

Native-reference scanning, deterministic coverage generation/check and independent `javap` validation pass (372 declarations across 21 types). Annotation defaults decrease from 211 to 210. All 3,851 frozen API IDs and scope counts (362 required, 625 excluded, 2,864 unresolved), 512 Spring exposures, 134 production imports and 551 source references remain unchanged. The source audit retains 159 main files with 507 noncomment reference lines and 110 test files with 328 reference lines. `git diff --check` passes.

Task 63 remains open for the remaining property audit, function fixture placement/audit and inherited native region attributes. The next identified property gap is `GemFireProperties.LOCATORS`: pinned `cache/client/ClientCacheFactory.basicCreate` lines 242–250 rejects nonempty distributed-system locators, whereas its client `addPoolLocator` configures server discovery separately. The subsequent peer locator slice below completes that consumer/migration review and removal.

```sh
./gradlew --offline -DgemfireVersion=10.4.0-build.3056.df98c835 \
  :spring-data-vmware-gemfire:test \
  --tests '*GemFirePropertiesUnitTests' \
  --tests '*ClientOnlyConfigurationUnitTests' \
  --tests '*ClientCacheFactoryBeanUnitTests' \
  :spring-data-vmware-gemfire:integrationTest \
  --tests '*EnableGemFirePropertiesIntegrationTests' \
  --tests '*PoolPropertiesIntegrationTests' \
  --tests '*ClientCachePropertiesIntegrationTests' \
  --tests '*LazyWiringDeclarableSupportClientLoaderIntegrationTests' \
  :spring-test-vmware-gemfire:compileTestJava \
  :spring-data-gemfire-driver-10.4:integrationTest \
  --tests '*NativeTransactionTests' \
  pruneMigrationBoundaryBaseline verifyMigrationBoundary
python3 wayfinder-impl/audits/scan_native_references.py
python3 wayfinder-impl/coverage/build_api_coverage.py
python3 wayfinder-impl/coverage/build_api_coverage.py --check
python3 wayfinder-impl/coverage/validate_coverage.py --javap
git diff --check
```


## Peer distributed-system locators — 2026-10-27

This slice removes `GemFireProperties.LOCATORS` and the unused `AbstractCacheConfiguration` locator field, default, accessors and generated `locators` property. Client pool locators and client XML parsing remain supported.

Evidence uses native commit `df98c8357afe85853d9a4776255d4f0e7cf064cb`, matching `10.4.0-build.3056.df98c835`. In native `gemfire-core/src/main/java/org/apache/geode/cache/client/ClientCacheFactory.java`, `basicCreate` lines 242–250 rejects a nonempty distributed-system `locators` property and sets it to the empty string internally. Its separate `addPoolLocator` method delegates to the pool factory for server discovery. Omitting the generated empty peer property therefore preserves native client creation behavior. Repository consumer searches found no production use of the enum constant, and only the base configuration's own mapping and unit test used its locator accessors.

Migration: remove `GemFireProperties.LOCATORS` references and any subclass overrides of the removed protected `locators()` method or references to `DEFAULT_LOCATORS`. Configure server discovery through client cache/pool annotations, the client pool factory or `<gfe:pool>` locator configuration. The default/named pool environment settings remain supported. No ignored compatibility setting replaces the removed API. Arbitrary custom native properties are still merged; this change does not globally validate raw properties.

Test dispositions: the property-enum suite rejects `locators` and `gemfire.locators`; its valid prefixed lookup example now uses the retained durable-client ID. The base configuration test verifies absence of the peer property while preserving name/log-level assertions. The pool integration suite now explicitly verifies default and named pool locator addresses and absence of the peer property in generated properties. Existing XML pool, client cache configuration, client property and client-local XML loader tests remain. These mock configuration tests do not exercise real-server discovery or failover. The real-server locator suite remains in place and compiles; no suite is deleted or disabled.

Validation: **110 focused Java tests pass with zero failures/errors/skips** (10 property enum, two base configuration, seven client cache configuration, 46 client cache factory, nine client-only XML configuration, 18 XML pool parser, 10 annotation-property integration, two pool integration, four client cache property integration and two client XML loader integration). Full affected Spring main/unit/integration and separate Spring Test sources compile. All 19 boundary-tool tests and the transitional boundary pass. One obsolete allowance is pruned without additions; native-source occurrences decrease from 5,174 to 5,173. This remains regression acceptance against a transitional baseline, not native-free compliance.

Native-reference scanning, deterministic coverage generation/check and independent `javap` validation pass (372 declarations across 21 types). All 3,851 frozen API IDs and scope counts (362 required, 625 excluded, 2,864 unresolved), 512 Spring exposures, 134 production imports, 551 source references and 210 annotation defaults remain unchanged. The source audit retains 159 main files with 507 noncomment reference lines, 110 test files with 328 reference lines and 127 integration files with 440 reference lines. `git diff --check` passes. Task 63 remains open for the remaining property audit, function fixture placement/audit and inherited native region attributes.

```sh
./gradlew --offline -DgemfireVersion=10.4.0-build.3056.df98c835 \
  :spring-data-vmware-gemfire:test \
  --tests '*GemFirePropertiesUnitTests' \
  --tests '*AbstractCacheConfigurationUnitTests' \
  --tests '*ClientCacheConfigurationUnitTests' \
  --tests '*ClientCacheFactoryBeanUnitTests' \
  --tests '*PoolParserUnitTests' \
  --tests '*ClientOnlyConfigurationUnitTests' \
  :spring-data-vmware-gemfire:integrationTest \
  --tests '*PoolPropertiesIntegrationTests' \
  --tests '*ClientCachePropertiesIntegrationTests' \
  --tests '*EnableGemFirePropertiesIntegrationTests' \
  --tests '*LazyWiringDeclarableSupportClientLoaderIntegrationTests' \
  :spring-test-vmware-gemfire:compileTestJava \
  pruneMigrationBoundaryBaseline verifyMigrationBoundary
python3 wayfinder-impl/audits/scan_native_references.py
python3 wayfinder-impl/coverage/build_api_coverage.py
python3 wayfinder-impl/coverage/build_api_coverage.py --check
python3 wayfinder-impl/coverage/validate_coverage.py --javap
git diff --check
```


## Shared client properties and bind-address correction — 2026-10-27

This completes the property **scope** review, not the SPI migration or all property behavior contracts. Remove `EnableGemFireProperties.bindAddress`, `DEFAULT_BIND_ADDRESS`, its mapper and `GemFireProperties.BIND_ADDRESS`. Correct client documentation for cache XML, identity, distributed-system ID and serialization. Fix serialization filter array mapping to join patterns with semicolons instead of commas.

Evidence uses native commit `df98c8357afe85853d9a4776255d4f0e7cf064cb`, matching `10.4.0-build.3056.df98c835`. Paths below are relative to `gemfire-core/src/main/java/org/apache/geode/` unless a module is named.

| Property family | Client-consumer evidence and decision |
| --- | --- |
| `bind-address` | Remove. Production getter consumers bind peer channels, membership or server/REST listeners. `cache/client/internal/ConnectionImpl.connect` (112–122) passes the remote address, timeout, buffer and pool socket factory to the client socket creator. `internal/net/SCAdvancedSocketCreator` (66–85) creates/connects that socket without the distributed-system bind address. The non-TLS path in `gemfire-tcp-server`'s `ClientSocketCreatorImpl` (48–56) and `AdvancedSocketCreatorImpl` (79–114) likewise has no such input. A property being accepted by shared configuration is not evidence that it controls outgoing client binding. |
| `distributed-system-id` | Retain for client SSL extensions. `internal/net/SSLConfig.Builder.setSSLParameterExtension` (416–436) initializes the extension with the active system's configured ID. `SSLConfigurationFactory` (133–153) constructs the configuration for locator/server channels; `SocketCreator` (475–480, 513–516) applies the extension to client socket parameters. The earlier handshake argument was incorrect: `ClientSideHandshakeImpl` (241–253) reads that ID only for WAN communication. `pdx/internal/TypeRegistry` (75–86) selects client registration when a pool exists; local DSID is not a client PDX prerequisite. |
| `name`, `groups`, durable client ID/timeout | Retain. `distributed/internal/LonerDistributionManager.generateMemberId` (1007–1018) constructs the client member using its name, groups and durable attributes. Client identity groups are separate from pool server-group targeting. |
| Thread monitor enabled/interval/time limit; member timeout | Retain. `LonerDistributionManager` (108–127) creates the client's monitor with the configured interval/time limit when enabled. The earlier member-timeout slice documents the monitor's additional timeout consumer and default. |
| Time statistics | Retain. `LonerDistributionManager` (115) and `internal/cache/GemFireCacheImpl` (1117) read the configured flag for client clocks. |
| Main/security logging, file/disk limits | Retain. `InternalDistributedSystem` (716–735) creates/starts logging for clients. `internal/logging/ManagerLogWriterFactory` (74–128) chooses main/security files and levels and configures rolling; `ManagerLogWriter` (206–207, 336) applies disk limits. `SecurityLogConfig` (60–61) delegates that limit. |
| Statistics sampling/rate/archive/file/disk limits | Retain. `InternalDistributedSystem` (848, 914–921) starts `GemFireStatSampler` outside the peer/client branch unless statistics are disabled. `internal/statistics/GemFireStatSampler` (137–182) consumes these settings. `cache/client/internal/PoolImpl` (279, 410–412) also uses sampling enablement for publishing client statistics. |
| Serializable-object filter and validation | Retain and repair the mapper. `InternalDistributedSystem` (750–759) initializes deserialization filtering before the peer/client branch. `internal/InternalDataSerializer` (575–604) passes configured filtering and validation to the native filter. In `gemfire-serialization`, `SanctionedSerializablesFilterPattern` (55, 90–120) joins/splits semicolon-delimited rules; `StreamSerialFilterFactory` (68, 84) uses `ObjectInputFilter.Config.createFilter`. Comma-joining annotation entries prevented distinct class rules from matching. |
| Event conflation | Retain. `cache/client/internal/ClientSideHandshakeImpl.initFlags` (135–148) reads `conflate-events` into client handshake flags. |
| Client cache XML | Retain. `internal/cache/GemFireCacheImpl` (1750) reads the XML path; retained native client-local XML loader tests exercise this configuration. |
| Availability/redundancy zone, delta propagation, memory locking, client authentication and SSL | Retain with the dedicated consumer evidence and migration notes in the preceding slices. These families are not reclassified by the bind-address correction. |

Migration: remove the retired bind-address annotation/default/enum references. If an application needs explicit local socket binding, configure its client pool socket factory; the removed distributed-system property did not configure that path. Raw custom properties remain merged and are not globally validated. Serialization annotation entries now follow the native semicolon pattern syntax; no new rejection rule is implicitly added. Empty arrays still omit the property. Existing comma-delimited groups are unchanged.

Test dispositions: enum lookup rejects plain/prefixed bind-address; annotation reflection rejects its accessor and retains client identity accessors; default properties omit the removed key. The serialization test feeds generated properties into the JDK filter and checks two allowed classes and a rejected third class, rather than asserting only a formatted string. The new native driver suite creates real client caches and verifies name/groups/durable identity and SSL extension initialization with ID 17 for both outgoing channels. It uses the SSL configuration factory directly; an initial socket-factory version failed compilation because that internal type's TCP superclass is not on the test compile classpath. No dependency was added to Spring or the driver for that test. These tests do not exercise a remote TLS handshake or native wire deserialization. Pool socket factory/buffer tests and client XML tests remain in the focused validation.

Task 63 remains open for the function audit/fixture placement, inherited region attributes and final surface audit. Tasks 37/38 still own native-free acceptance. Future Gradle commands must be provided to the user and awaited, per the user's instruction in this session.

Validation: **107 focused Java tests pass with zero failures/errors/skips** (83 Spring unit, 18 Spring integration and six native driver integration tests). Full affected Spring main/unit/integration, separate Spring Test test sources and native driver integration sources compile. All 19 boundary-tool tests pass; the transitional boundary prunes one obsolete occurrence without additions, from 5,173 to 5,172 native-source occurrences. This remains transitional regression acceptance, not native-free compliance.

Source scanning, deterministic coverage generation/check and independent `javap` validation pass (372 declarations across 21 types). Annotation defaults decrease from 210 to 209. All 3,851 frozen API IDs and scope counts (362 required, 625 excluded, 2,864 unresolved), 512 Spring exposures, 134 production imports and 551 source references remain unchanged. `git diff --check` passes.

The following Gradle command was already running when the user changed the execution policy; it completed successfully. Do not rerun it automatically. All subsequent Gradle commands require the user to run them and confirm completion.

```sh
./gradlew --offline -DgemfireVersion=10.4.0-build.3056.df98c835 \
  :spring-data-vmware-gemfire:test \
  --tests '*GemFirePropertiesUnitTests' \
  --tests '*ClientOnlyConfigurationUnitTests' \
  --tests '*ClientCacheFactoryBeanUnitTests' \
  --tests '*PoolParserUnitTests' \
  :spring-data-vmware-gemfire:integrationTest \
  --tests '*EnableGemFirePropertiesIntegrationTests' \
  --tests '*PoolPropertiesIntegrationTests' \
  --tests '*ClientCachePropertiesIntegrationTests' \
  --tests '*LazyWiringDeclarableSupportClientLoaderIntegrationTests' \
  :spring-test-vmware-gemfire:compileTestJava \
  :spring-data-gemfire-driver-10.4:integrationTest \
  --tests '*NativeClientPropertiesTests' \
  --tests '*NativePoolConfigurationTests' \
  pruneMigrationBoundaryBaseline verifyMigrationBoundary
python3 wayfinder-impl/audits/scan_native_references.py
python3 wayfinder-impl/coverage/build_api_coverage.py
python3 wayfinder-impl/coverage/build_api_coverage.py --check
python3 wayfinder-impl/coverage/validate_coverage.py --javap
git diff --check
```

## Client function submission and fixture audit — 2026-10-27

Production function execution now exposes client region, one-server and all-server targets; peer-member execution and Spring function hosting were retired in the preceding slices. Function objects, collectors, execution callbacks, arguments, filters and timeout/error adaptation remain client migration requirements. Do not classify whole native function types as server-only merely because they also expose server execution methods.

Evidence uses native commit `df98c8357afe85853d9a4776255d4f0e7cf064cb`, matching `10.4.0-build.3056.df98c835`. Paths below are relative to `gemfire-core/src/main/java/org/apache/geode/`:

- `internal/cache/execute/InternalFunctionExecutionServiceImpl` selects `ServerRegionFunctionExecutor` for client regions (128–149), and client pool/region-service executors for one/all servers (219–284). Authenticated views carry their user context; direct multiuser pool invocation is separately rejected. Peer member execution is a different path.
- `internal/cache/execute/AbstractExecution` (362–406) accepts a function object, validates it and calls `executeFunction` with serialization enabled. `ServerRegionFunctionExecutor` forwards that object to the server-region proxy; `ServerFunctionExecutor` constructs client function operations with the object and serialization flag. Supported client object submission must survive the SPI migration.
- `internal/cache/execute/OnServerExecutor` (40–124) distinguishes ID lookup from object submission. A locally registered function can supply metadata for an ID invocation; that shortcut does not make Spring server hosting necessary. Object execution preserves the serialization flag, collector/no-result behavior and authenticated user context.

Added `NativeClientFunctionSubmissionTests` in the 10.4 driver's private integration source set. Three tests cover client region, pool and region-service one/all-server overloads using an unregistered function object. Serialized instance state and arguments determine the result; the function rejects execution in the client process or server registration. Its class is available on the separate server process classpath. This is evidence for object transmission, not arbitrary class deployment, multi-server fan-out, authenticated execution, retry or timeout behavior. **All three tests pass in the user-run Gradle validation.**

### Retained consumer gaps and owners

| Consumer | Required disposition |
| --- | --- |
| Templates, proxies and five region/server executor implementations | Tasks 25/32: typed client targets, object/ID submission, arguments, filters, collectors, errors and timeout semantics; no native public overloads in the final Spring API. |
| `GemfireOnRegionOperations` object overloads with keys | Task 32: all three defaults currently discard the object and delegate by ID; `GemfireOnRegionFunctionTemplate` only implements the ID/filter variants. Repair this during migration and prove an unregistered stateful object works with a filter for iterable, extracted and no-result forms. Do not preserve this loss of submission semantics in the SPI. |
| `FunctionGemfireAdminTemplate.getAvailableServerRegions()` | Tasks 25/32: preserve remote client region discovery, replacing native `GetRegionsFunction` and `RegionInformation` with driver-owned transport and typed results. This is a client operation, not a reason to retain public hosting APIs. |
| `GemfireFunctionCallback` and `AbstractFunctionExecution` | Tasks 25/32: replace native execution/collector types while preserving orchestration, result adaptation and error behavior. Mock remote execution remains explicitly unsupported; native-free orchestration tests still exercise these paths. |
| Spring Test registry cleanup and mocked `Query.execute(RegionFunctionContext)` | Task 36 under the separate Spring Test exception: audit callers and remove obsolete server-oriented support. These helpers do not justify public server SPI contracts. |

### Server fixture placement

The following 13 Spring unit-source files contain 15 server function bodies used by retained integration tests. Task 64 owns moving these bodies and server packaging into private native integration fixtures **before task 59 closes**. Tasks 25/32 own later SPI consumer cutover; they are blocked by 59 and cannot own cleanup required to close it.

| Files under `src/test/java/org/springframework/data/gemfire/function/` | Retained client assertions |
| --- | --- |
| `ExceptionThrowingFunction` | `ExceptionThrowingFunctionExecutionIntegrationTests`: remote exception translation. |
| `AddressFieldFunction`, `DataFieldFunction`, `CaptureUnconvertedArgumentTypesFunction` | `ClientCacheFunctionExecutionWithPdxIntegrationTest`: PDX fields/data and unconverted arguments. |
| `execution/ArraysFunction`, `CollectionsFunction`, `EchoFunction`, `GetMapWithNoArgsFunction`, `NoResultFunction`, `OneArgFunction`, `TwoArgFunction` | `FunctionIntegrationTests`, `FunctionExecutionIntegrationTests`, `GemfireFunctionTemplateIntegrationTests`: results, arguments, no-result and retained client targets. |
| `execution/onservers/MetricsFunctionServerConfiguration` | `FunctionsReturnResultsFromAllServersIntegrationTests`: multiple-server results. |
| `result/MixedResultTypeFunctions` (three nested functions) | `FunctionResultTypeIntegrationTests`: object/list/primitive result conversion. |

Spring's current `testJar` includes main and test outputs. Split server fixture packaging without dropping client assertions, and move server cache XML with its infrastructure. Replace client references to `EchoFunction.FUNCTION_ID` with a neutral fixture protocol/ID; shared domain/PDX data must not force native server bindings onto the client test classpath. Preserve the PDX and multi-server scenarios rather than substituting a single-server smoke test.

Removed the redundant client `MixedResultTypeFunctions` bean and its `@Component` marker. The integration fixture already deploys the function JAR on the server; the three result assertions and server bodies remain. This is not the fixture relocation itself. Its real-server integration test has not been rerun.

The function surface audit and fixture ownership record are validated. Task 63 remains open for inherited region attributes and the final surface audit; task 64 owns actual fixture relocation. No coverage dispositions or native-free acceptance claims change in this slice.

The user ran the following command; compilation failed because the timed native `ResultCollector.getResult` declares `InterruptedException`. The submission helper and its three JUnit test methods now declare that exception, allowing interruption to fail the test without swallowing it. The user rerun succeeds: 29 focused Java tests (three native integration and 26 Spring unit) pass with zero failures/errors/skips, affected compilation succeeds, and all 19 boundary-tool tests pass. The transitional boundary remains at 5,172 native-source occurrences. The command compiles the retained result-conversion integration suite but does not execute its container-backed server assertions. Source scanning, deterministic coverage generation/check and independent `javap` validation pass (372 declarations across 21 types). All 3,851 frozen API IDs and scope counts (362 required, 625 excluded, 2,864 unresolved), 512 exposures, 134 production imports and 551 source references remain unchanged. `git diff --check` passes.

```sh
./gradlew --offline -DgemfireVersion=10.4.0-build.3056.df98c835 \
  :spring-data-gemfire-driver-10.4:integrationTest \
  --tests '*NativeClientFunctionSubmissionTests' \
  :spring-data-vmware-gemfire:test \
  --tests '*AbstractFunctionTemplateUnitTests' \
  --tests '*GemfireFunctionProxyFactoryBeanUnitTests' \
  --tests '*FunctionGemfireAdminTemplateUnitTests' \
  --tests '*ClientOnlyConfigurationUnitTests' \
  :spring-data-vmware-gemfire:compileIntegrationTestJava \
  :spring-test-vmware-gemfire:compileTestJava \
  verifyMigrationBoundary
```

## Inherited region attributes retirement — 2026-10-27

Implementation and focused user-run Gradle validation pass; migration-boundary reconciliation remains pending. Removed `RegionAttributesFactoryBean`, whose inheritance from native `AttributesFactory` exported peer/server setters. Client XML now writes optional settings, expiration, eviction, compression and statistics directly to `ClientRegionFactoryBean`. Spring bean-definition inheritance carries those settings through chained client templates; the parser still rejects templates declared after their consumers. No generic attributes object is synthesized for omitted settings. Callbacks, interests, pools and disk-store dependencies retain their existing paths.

Entity-defined regions now set disk synchrony on `CacheTypeAwareRegionFactoryBean`, which forwards it to the client factory. Remove the dead `ignoreJta` annotation branch (the client annotation no longer exports it). The repository sample's Java configuration uses client key/value constraint setters. The continuous-query unit fixture uses existing Mockito native attributes solely for its mock region metadata; it no longer instantiates the deleted production factory. Beans-namespace fixtures set statistics directly on the client factory and omit empty generic attributes beans.

### Scheduling and retained obligations

Task 30 depends on 19 and cleanup parent 59; task 47, which supplies missing expiration/eviction/compression configuration, also depends on 59. Requiring the full SPI replacement inside child 63 would form a cycle. The explicit split is: task 63 removes the inherited server API and migrates its callers to existing client setters; tasks 47/19 supply complete typed configuration contracts; task 30 migrates these callers, factory signatures and wrappers to those contracts after 59. The task descriptions now record that handoff.

`ClientRegionFactoryBean` and `CacheTypeAwareRegionFactoryBean` still accept native `RegionAttributes`; callbacks, expiration, eviction and compression still have native types. Task 30 must replace them without dual-type overloads or native unwraps. This slice neither implements those SPI contracts nor reduces the frozen required-client inventory. Task 64 retains ownership of server fixture relocation.

### Test dispositions and migration

- Replace application uses of `RegionAttributesFactoryBean` with explicit client factory properties, client XML attributes/subelements or client entity annotations. Server/peer settings have no client replacement. The client XML parser remains supported.
- Preserve the client XML, disk-store, template and beans-namespace behavior assertions. Read expiration, eviction, constraints and other attributes from the created region instead of the removed intermediate bean.
- Remove the disk-store assertion of `Scope.DISTRIBUTED_NO_ACK`: it observed the generic attributes factory's peer default, not the created client region. Native `ClientRegionFactoryImpl` sets local scope internally. No scope setter is added.
- Replace the beans-namespace assertion of the empty generic attributes object's `DataPolicy.NORMAL` with absence of that object. Existing created-region normal/persistent data-policy assertions remain. The existing Spring Test native mocks are transitional and do not establish native driver behavior or official-mock coverage.
- Add parser checks for three-level template inheritance, explicit overrides, inherited expiration/statistics, absent optional settings and definition-order rejection. Add an entity-annotation check that `diskSynchronous=false` reaches the created mock client region.

Pinned source `gemfire-core/src/main/java/org/apache/geode/cache/client/internal/ClientRegionFactoryImpl.java` at `df98c8357afe85853d9a4776255d4f0e7cf064cb` initializes attributes from the selected client shortcut and sets local scope. Avoiding a generic default-filled attributes object allows omitted settings to retain the selected shortcut's values. Focused parser tests check omission; native behavior and full SPI contracts remain obligations of tasks 30/47.

### Validation status

No Gradle commands or project Java tests were run by the agent. The lexical audit was refreshed: 328 production Java files, 158 with native references, 505 noncomment native-reference lines and 133 imported native symbols. Integration sources have 437 reference lines across 127 native-referencing files; unit sources retain 328 lines across 110 files. No coverage IDs or scope dispositions were changed.

Compilation, focused tests, migration-boundary reconciliation and compiled coverage regeneration remain pending. The prior successful function-audit validation does not cover this slice. After the user runs the command below, inspect results, reconcile any changed transitional fixture identities explicitly, prune resolved boundary allowances, run the boundary gate, then regenerate/check the compiled API coverage inventory. Do not claim the old compiled coverage artifacts are current for this slice.

```sh
./gradlew --offline -DgemfireVersion=10.4.0-build.3056.df98c835 \
  :spring-data-vmware-gemfire:test \
  --tests '*ClientOnlyConfigurationUnitTests' \
  --tests '*ClientRegionFactoryBeanUnitTests' \
  --tests '*EnableEntityDefinedRegionsUnitTests' \
  --tests '*EnableContinuousQueriesConfigurationUnitTests' \
  :spring-data-vmware-gemfire:integrationTest \
  --tests '*ClientRegionNamespaceIntegrationTests' \
  --tests '*TemplateClientRegionNamespaceIntegrationTests' \
  --tests '*DiskStoreNamespaceIntegrationTests' \
  --tests '*RegionDefinitionUsingBeansNamespaceIntegrationTests' \
  --tests '*EnableEntityDefinedRegionsIntegrationTests' \
  :spring-test-vmware-gemfire:compileTestJava
```

The selected integration suites use the existing native mocks; real-server suites are compiled but not selected. Task 63 remains open pending this validation and its final surface audit.

### First validation attempt and mock-default correction

The user ran the command above: production and unit-test compilation passed, then 54 unit tests ran with three failures in `EnableEntityDefinedRegionsUnitTests`. Each failure expected disk synchrony `true` but received `false`. The failed test task stopped the invocation before the selected integration tests and the final Spring Test compilation task.

The shortcut resolver in `GemFireMockObjectsSupport` created a Mockito `RegionAttributes` with no disk-synchrony stub. Its primitive `false` displaced the mock factory's fallback `true`; the deleted generic attributes factory had masked this by explicitly applying the native default. Pinned `InternalAttributesFactory.DEFAULT_DISK_SYNCHRONOUS` is `true`, and `GemFireCacheImpl.initializeClientRegionShortcuts` leaves that default unchanged for every client shortcut. Correct the shortcut-derived mock attributes to return `true`. Production settings remain optional, and existing assertions are retained.

Added two Spring Test regressions covering default `true` and explicit `false` across every client shortcut. `git diff --check` passes; these changes have not yet been compiled or tested. Rerun the command above with `:spring-test-vmware-gemfire:test --tests '*GemFireMockObjectsSupportUnitTests'` in place of the final compile-only task. Task 63 and boundary/coverage validation remain open.

### Successful rerun and refreshed coverage

The user reports the requested rerun passed. Local XML reports confirm 82 tests with zero failures, errors or skips: 54 Spring Data unit tests, 21 Spring Data integration tests and seven Spring Test mock-support tests. This includes both shortcut disk-synchrony regressions. The command above was rerun with `:spring-test-vmware-gemfire:test --tests '*GemFireMockObjectsSupportUnitTests'` replacing the final compile-only task. The selected integration tests use the existing mocks; this is not real-server validation or the full unit suite.

Refreshed the lexical audit and compiled API inventory after the successful build. Deterministic generation/check and independent `javap` validation pass (372 declarations across 21 types). All 3,851 API IDs across 292 native types remain recorded, with unchanged scope counts: 362 required-client, 625 excluded-server and 2,864 unresolved. Current Spring signature exposures decrease from 512 to 509, production imported symbols from 134 to 133, and source/resource references from 551 to 549. The earlier validation-status paragraphs record the initial handoff; compiled coverage is now current for this slice.

Migration-boundary reconciliation remains pending. Ask the user to run pruning followed by regression verification; pruning refuses new findings and can only remove or reduce resolved allowances. Review any changed fixture identities explicitly before adjusting the baseline. Task 63 stays open until this gate and the final surface audit are complete; tasks 30/47 retain full SPI configuration migration and task 64 retains server fixture relocation.

### Reviewed transitional test-boundary adjustment

The user ran `pruneMigrationBoundaryBaseline verifyMigrationBoundary`. All 19 boundary-tool tests passed, but pruning refused new findings; regression verification was not reached. The refreshed scan reports 5,169 native-source occurrences, down from 5,172. Comparing saved findings with the prior baseline identified 18 added occurrences across 15 identities and 21 obsolete occurrences across 17 identities. All additions are in existing test files; no new production, dependency, resource or static-state violations were found.

The following explicit test-scope baseline adjustment supports removal of the inherited production attributes factory. It is not automatic acceptance of all findings and does not establish native-free tests:

| Test file | Added/changed occurrences | Reason and removal owner |
| --- | ---: | --- |
| `ClientRegionNamespaceIntegrationTests` | 4 | Two attributes declaration identities now inspect the created region, plus two `Region.class` lookups, replacing reflection into the deleted intermediate attributes object. Preserve constraint/eviction/default assertions; task 40 owns native-free integration coverage with task 30's factory cutover. |
| `DiskStoreNamespaceIntegrationTests` | 5 | Three attributes declaration identities now read actual region settings, plus two `Region.class` lookups. Preserve expiry/custom-expiry/eviction assertions; task 40 owns integration migration with tasks 30/47. |
| `EnableContinuousQueriesConfigurationUnitTests` | 6 | Two bean return types, two mock declarations and two class literals expose the existing native attributes dependency directly after removing its production factory wrapper. Task 37 must replace this transitional Mockito metadata with native-free fixtures while preserving both CQ domain constraints. |
| `EnableEntityDefinedRegionsUnitTests` | 3 | Region type/class lookup and client-local shortcut for the new explicit-false disk-synchrony regression. Task 37 must retain the end-to-end configuration assertion against SPI/official-mock fixtures. |

Removed all 21 obsolete allowances: four old client-region assertion occurrences, five old disk-store occurrences (including the retired peer-scope import/assertion), three beans-namespace intermediate-attributes occurrences, two repository fixture occurrences and seven occurrences from deleted production `RegionAttributesFactoryBean`. The net reduction is three native-source occurrences. Existing owners are retained: task 37 for unit fixtures and task 40 for integration fixtures. No checker rule or production allowance was added.

A read-only comparison against the user's saved findings now reports zero new/stale entries; `git diff --check` passes. No Java source changed after the passing 82-test run. Fresh user-run Gradle boundary verification is still required. Task 63 remains open for that result and its final surface audit.


### Boundary confirmation

The user reports the requested fresh `verifyMigrationBoundary` invocation succeeded in five seconds. The saved summary records 5,169 native-source occurrences and 158 production, 110 unit-test and 127 integration-test Java files with native references. This completes validation of the inherited-attributes retirement and its reviewed transitional baseline adjustment. The earlier pending statements describe previous handoffs. The following newly discovered follow-up has not yet been compiled or tested.

## Final surface audit — feature catalog follow-up

Reviewed namespace registrations, both shipped schemas, production server-type references and public/native inheritance declarations. Findings:

| Surface | Disposition |
| --- | --- |
| `GemfireNamespaceHandler` | Retains client cache, pool, client region/template, lookup, CQ, disk-store and transaction parsers. No server cache/function-hosting parser registration remains. Client XML remains supported. |
| `EmbeddedServiceConfigurationSupport` | Despite its historical name, its property registration dispatches to `ClientGemFirePropertiesConfigurer` and `ClientCacheFactoryBean`; retain client property configuration. |
| `CacheServer.DEFAULT_PORT`, `CacheFactory.getVersion()` and `Scope.LOCAL` checks | Client endpoint defaults, product metadata and client-local operation detection, respectively; they do not host a server. Remaining native utility/consumer coupling belongs to tasks 31/35/36. |
| `PoolAdapter`, index/query-result/serialization/callback interfaces and native `RegionAttributes` signatures | Retained client-facing or mixed contracts need full SPI migration under their existing owners. Tasks 30/47 explicitly own native attributes; task 64 resolves transaction listener outcomes. Their presence is not native-free acceptance. |
| `server/package-info.java` and historical peer descriptions | Documentation remnants, not executable hosting/configuration APIs. Task 36 owns residual documentation/native-link cleanup with the utility cutover. |
| `GemfireFeature` | Remove public `BACKUP`, `NETWORK_PARTITIONING`, `PEER_TO_PEER` and `REPLICATION`. These name excluded server/peer capabilities and were reported available by the non-CQ fallback in `GemfireUtils.isGemfireFeatureAvailable`. Repository searches found no callers of these four constants. Client persistence, interest/subscription, functions and other retained categories remain. |

Breaking migration note: remove use of the four retired feature constants from Spring client capability checks; cluster backup, peer membership and replication are configured outside this client library. This does not remove client persistence or data received from servers. The general feature-availability helper's native discovery remains task 36 work; this change does not implement driver capability negotiation.

Added one catalog regression to the existing client-only configuration suite, checking both retained capabilities and absence of the four retired constants. Existing tests/assertions remain intact. Corrected the schema description of client-local eviction (previously described as a partitioned region). No schema structure, native boundary allowance or scope disposition changed.

### Required validation

No Gradle command was run by the agent. Run the following, then refresh/check the compiled coverage inventory. The preceding 82-test and boundary passes cover the inherited-attributes slice; they do not validate this follow-up.

```sh
./gradlew --offline -DgemfireVersion=10.4.0-build.3056.df98c835 \
  :spring-data-vmware-gemfire:test \
  --tests '*ClientOnlyConfigurationUnitTests' \
  --tests '*GemfireUtilsUnitTests' \
  :spring-data-vmware-gemfire:compileIntegrationTestJava \
  :spring-test-vmware-gemfire:compileTestJava \
  verifyMigrationBoundary
```

Task 63 remains open pending this validation and final audit closure. Task 64 still owns member-level scope reconciliation and server fixture relocation before parent 59 closes.


### Final validation and task 63 closure — 2026-10-27

The user reported success for the exact command above. Saved JUnit reports confirm **15 tests**, zero failures/errors/skips: 12 `ClientOnlyConfigurationUnitTests` and three `GemfireUtilsUnitTests`. The command includes affected integration/Spring Test compilation and transitional boundary verification. The prior 82-test validation remains evidence for inherited client configuration. These focused passes do not establish full-suite, real-server authentication/TLS or native-free acceptance.

The agent ran only the permitted Python evidence tools: `scan_native_references.py`, `build_api_coverage.py`, its `--check`, and `validate_coverage.py --javap`. All pass. Independent validation matched 372 declarations across 21 types; coverage retains 3,851 frozen IDs, 509 Spring exposures, 133 production imports and 549 source/resource references. Scope remains 362 required-client / 625 excluded-server / 2,864 unresolved. The saved boundary summary remains at 5,169 native-source occurrences, with 158 production, 110 unit-test and 127 integration-test Java files containing native references. No feature-catalog boundary allowance was added.

Task 63 is resolved. Task 64 now owns final member-level scope reconciliation, mixed transaction outcome decisions and relocation of the inventoried 13 server function fixture files (15 bodies) with their packaging. Tasks 30/47 retain native attribute migration and task 36 retains utility/discovery/documentation cleanup. Parent 59 stays open until task 64 and its acceptance obligations complete. No commit was made.
