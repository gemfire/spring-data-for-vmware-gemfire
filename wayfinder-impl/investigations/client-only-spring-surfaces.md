# Client-only Spring configuration retirement

Task 63 is **in progress**. This ledger records the first retirement slice against GemFire `10.4.0-build.3056.df98c835`, Java 17 and Gradle 9.7. Tasks 59 and 63 remain open; this does not establish native-free Spring compliance.

## Breaking changes and test dispositions

| Removed surface | Migration and retained behavior |
| --- | --- |
| `RegionShortcutWrapper`, `RegionShortcutConverter`, `RegionShortcutToDataPolicyConverter`; `serverRegionShortcut` on caching/entity annotations and configuration | Use `clientRegionShortcut` and client shortcuts. Retain client annotation configuration, pool selection and region creation assertions. Retire the three server wrapper/converter unit suites and server-accessor-only assertions. |
| `InterestPolicyType`, `InterestPolicyConverter`, `SubscriptionAttributesFactoryBean`, `ScopeType`, `ScopeConverter`, server `SubscriptionEvictionPolicy` and its converter | Remove peer subscription/scope and server queue configuration, including custom editor registrations and their seven unit suites. Client `InterestResultPolicy`, pool subscriptions and client persistence remain. The separate Spring Test module keeps its permitted native mock support using native shortcuts and private string normalization instead of these production classes. |
| `CacheTypeAwareRegionFactoryBean` server shortcut, scope and data-policy properties | Remove stored-only server properties. Configure client creation with shortcuts; retain read-only region metadata and the existing client factory's data-policy mapping. |
| Cache factory/configuration transaction writer, `ComposableTransactionWriter`, `TransactionListenerAdapter.beforeCommit` | No client writer replacement: native client writer registration is unsupported, as recorded in task 62. Retire writer composition tests and writer-only assertions. Preserve transaction listener registration, composition and event configuration; outcome delivery remains a task 64 scope decision. |
| `gfe:region-template`, `TemplateRegionParser`, peer/local-server schema types and peer region attributes | Use `gfe:client-region-template`. Convert template fixtures and preserve inheritance, definition-order rejection, callbacks, expiration, eviction, constraints and persistence assertions. Remove the peer-only `ignore-jta` fixture setting. Remove peer subscription, conflation, lock-grantor and partition-default assertions from the template test, and replace client expiration assumptions with assertions so retained checks cannot skip. |
| Peer membership/subscription/scope/WAN parser helpers and client-cache parsing of server lock/search/message settings | Schema rejects retired server XML. Client templates, local persistent shortcuts, loader/writer/listener callbacks, PDX persistence and pool subscriptions retain positive schema checks. |

`RegionDefinitionUsingBeansNamespaceIntegrationTests` keeps its normal and persistent client data-policy assertions. Its nested attributes beans no longer specify ignored data policies; the outer client factory still controls creation.

`ClientOnlyConfigurationUnitTests` checks removed annotation/bean surfaces and schema rejection alongside positive client XML. The retained integration tests use the existing Spring Test native mocks; they are not evidence of a completed official-mock cutover or real-server testing.

## Remaining task 63 obligations

- Review each property in `EnableGemFireProperties`, `GemFirePropertiesConfiguration` and `GemFireProperties`; remove peer/WAN/server configuration with member-level evidence while preserving client properties and their tests. Broad native property reachability alone is insufficient evidence for retention or exclusion.
- Remove public function hosting/registration through `FunctionServiceFactoryBean`, `FunctionServiceParser`, namespace registration and schema exports. Preserve client function invocation and move any required server setup into private native integration fixtures. Coordinate retained invocation requirements with tasks 25/32 and the scope review in 64.
- Replace `RegionAttributesFactoryBean extends AttributesFactory`, which still exposes inherited server setters. Use an SPI-backed client configuration path and migrate its consumers together, coordinating with task 30. Do not introduce a parallel native setter facade, native unwrap or ignored settings. Resolve scheduling explicitly: task 30 currently waits on cleanup parent 59, so task 63 cannot simply wait on task 30 while leaving that dependency cycle implicit.
- Finish the public-surface audit and regenerate evidence after those changes before closing task 63. Remaining client-only migration continues with tasks 29–38; parent 59 stays open through 64.

## Validation and boundary handling

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
