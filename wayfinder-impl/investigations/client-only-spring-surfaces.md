# Client-only Spring configuration retirement

Task 63 is **in progress**. This ledger records the configuration, function-registration, peer-function and selected property retirement slices against GemFire `10.4.0-build.3056.df98c835`, Java 17 and Gradle 9.7. Tasks 59 and 63 remain open; this does not establish native-free Spring compliance.

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
| `ENFORCE_UNIQUE_HOST`, `REST_JSON_STORAGE_FORMAT`, `SERVER_BIND_ADDRESS`, `DEFAULT_ENFORCE_UNIQUE_HOST` and orphan annotation mappings for `enforceUniqueHost`/`redundancyZone` | Configure server partition placement, REST storage and listening addresses outside Spring client configuration. Client `bind-address`, `availability-zone` and the native `redundancy-zone` alias remain in the property enum; the removed mappings had no corresponding annotation attributes. |
| Legacy `LazyWiringDeclarableSupportFunctionBasedIntegrationTests`, `HelloFunctionExecution` and native function-registration XML | Replace the local member function with `LazyWiringDeclarableSupportClientLoaderIntegrationTests` and a native `LOCAL` client region/cache loader. Preserve the greeting assertion, verify Spring property injection and initialized state, and test caller-argument precedence over XML parameters. The loader unregisters its lazy-wiring listener on close. |

`RegionDefinitionUsingBeansNamespaceIntegrationTests` keeps its normal and persistent client data-policy assertions. Its nested attributes beans no longer specify ignored data policies; the outer client factory still controls creation.

`ClientOnlyConfigurationUnitTests` checks removed annotation/bean surfaces and schema rejection alongside positive client XML. The retained integration tests use the existing Spring Test native mocks; they are not evidence of a completed official-mock cutover or real-server testing.

## Remaining task 63 obligations

- Continue reviewing each remaining property in `EnableGemFireProperties`, `GemFirePropertiesConfiguration` and `GemFireProperties`; remove peer/WAN/server configuration with member-level evidence while preserving client properties and their tests. Broad native property reachability alone is insufficient evidence for retention or exclusion.
- Public registration, peer-member execution exports and the unused server result helper are removed; the legacy lazy-wiring fixture now uses a client-local loader. Finish the function audit and record placement of required server function bodies with tasks 25/32; those tasks already require private native-driver fixtures. Preserve supported client function-object submission pending exact-version member-level decisions in 64. Existing real-server exception tests remain intact and still need that fixture migration.
- Replace `RegionAttributesFactoryBean extends AttributesFactory`, which still exposes inherited server setters. Use an SPI-backed client configuration path and migrate its consumers together, coordinating with task 30. Do not introduce a parallel native setter facade, native unwrap or ignored settings. Resolve scheduling explicitly: task 30 currently waits on cleanup parent 59, so task 63 cannot simply wait on task 30 while leaving that dependency cycle implicit.
- Finish the public-surface audit and regenerate evidence after those changes before closing task 63. Remaining client-only migration continues with tasks 29–38; parent 59 stays open through 64.

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

Retention needs individual evidence. In that same binary, `PoolImpl` falls back from an empty pool availability zone to `DistributionConfig.getAvailabilityZone`; `ClientSideHandshakeImpl.handshakeWithServer` reads the distributed-system ID. Those findings rule out blanket removal based on server-sounding names, but do not complete their public configuration contract review. Deployment working directory, membership/socket settings, the redundancy-zone alias, orphan property mappings, security and SSL surfaces still require review. Existing security/SSL integration assertions remain pending that review.

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
| `server-bind-address` | Retire Spring enum | Pinned `AcceptorImpl.calcBindHostName` uses it for the server acceptor and `RestAgent.getBindAddressForHttpService` uses it for HTTP service binding. The separate client `bind-address` remains. |
| `rest-json-storage-format` | Retire Spring enum | `gemfire-web-api`'s `AbstractBaseController.convert(String)` reads it through `getRestJsonStorageFormat()` for REST-side JSON conversion. Client JSON/PDX serialization APIs remain. |
| `availability-zone` / `redundancy-zone` | Retain enum constants | Pinned `DistributionConfigImpl.setRedundancyZone` forwards to `setAvailabilityZone`, and `getRedundancyZone` forwards to `getAvailabilityZone`. `PoolImpl` uses that availability zone as a fallback when no pool-specific zone is set. Removing the orphan annotation mapper does not retire this client alias. |

Inspection used the cached core binary at `10.4.0-build.3056.df98c835` and the local native repository's committed source at **`df98c8357afe85853d9a4776255d4f0e7cf064cb`**, matching that build's revision suffix. The REST module is not cached as a binary; its decision uses the source at that revision, not the working checkout. Reproduce with `git -C /Users/uk016635/projects/gemfire show df98c835:<path>` and `javap -c -p` against the pinned core JAR. Source call-site searches covered production Java across modules.

Additional retention evidence: pinned `InternalDistributedSystem` initializes the class loader working directory from `getDeployWorkingDir` and consults `getLockMemory` during initialization. `TXManagerImpl.isDistributed` consults `getDistributedTransactions` without a client guard. These properties remain pending full client contract review; their presence in shared initialization is not blanket client acceptance. The remaining audit must also address the unmapped `conserveSockets` annotation attribute and the `tombstone-gc-threshold` mapping, alongside membership/transport, security and SSL settings.

The existing enum rejection regression now covers all three removed names, with and without the `gemfire.` prefix. Positive lookup assertions explicitly retain `bind-address`, `availability-zone` and `redundancy-zone`. No retained integration assertion or client XML surface was removed.

Full Spring main/unit/integration and separate Spring Test main/unit source compilation passes. The same focused command as the selected-property slice above was rerun: **27 tests pass with zero failures/errors/skips** (10 property unit, eight client configuration unit, seven property integration and two native client XML loader tests). The transitional boundary passes with **three additional native constant references pruned**, no new allowances and all 19 boundary-tool tests passing. No real-server tests were run; these results do not establish native-free Spring tests or official-mock completion.

`scan_native_references.py`, `build_api_coverage.py`, its `--check` mode, `validate_coverage.py --javap` and `git diff --check` pass. Independent validation still matches 372 declarations across 21 types; the inventory retains 512 Spring exposures, 134 production imports and 552 source references. Native-referencing Java file counts remain 160 production / 111 unit / 127 integration. All 3,851 frozen API IDs and scope/implementation statuses remain unchanged. Task 63 stays open.
