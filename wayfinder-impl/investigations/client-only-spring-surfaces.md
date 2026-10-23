# Client-only Spring configuration retirement

Task 63 is **in progress**. This ledger records the configuration, function-registration and peer-function retirement slices against GemFire `10.4.0-build.3056.df98c835`, Java 17 and Gradle 9.7. Tasks 59 and 63 remain open; this does not establish native-free Spring compliance.

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
| Legacy `LazyWiringDeclarableSupportFunctionBasedIntegrationTests`, `HelloFunctionExecution` and native function-registration XML | Replace the local member function with `LazyWiringDeclarableSupportClientLoaderIntegrationTests` and a native `LOCAL` client region/cache loader. Preserve the greeting assertion, verify Spring property injection and initialized state, and test caller-argument precedence over XML parameters. The loader unregisters its lazy-wiring listener on close. |

`RegionDefinitionUsingBeansNamespaceIntegrationTests` keeps its normal and persistent client data-policy assertions. Its nested attributes beans no longer specify ignored data policies; the outer client factory still controls creation.

`ClientOnlyConfigurationUnitTests` checks removed annotation/bean surfaces and schema rejection alongside positive client XML. The retained integration tests use the existing Spring Test native mocks; they are not evidence of a completed official-mock cutover or real-server testing.

## Remaining task 63 obligations

- Review each property in `EnableGemFireProperties`, `GemFirePropertiesConfiguration` and `GemFireProperties`; remove peer/WAN/server configuration with member-level evidence while preserving client properties and their tests. Broad native property reachability alone is insufficient evidence for retention or exclusion.
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
