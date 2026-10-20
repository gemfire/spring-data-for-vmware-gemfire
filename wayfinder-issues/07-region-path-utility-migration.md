Type: task
Status: resolved
Assignee: uk016635
Blocked by: 06

## Question

Extract the string-only region-path helpers from the native utility inheritance chain into `org.springframework.data.gemfire.util.RegionPaths`, and migrate all their repository callers together.

The [utility/root dependency inventory](../wayfinder-impl/investigations/utility-root-dependencies.md) establishes this boundary. It also records the legacy behavior table and the runtime utility groups that remain native. This ticket implements only the string-path extraction; it does not claim that `RegionUtils`, the utility package or the root package is fully migrated.

## Implementation scope

- Add `spring-data-vmware-gemfire/src/main/java/org/springframework/data/gemfire/util/RegionPaths.java`, an independently compilable native-free utility with a private constructor, no inherited utility superclass, no mutable static state, and `toRegionName(String)` / `toRegionPath(String)`.
- Move the implementations out of `RegionUtils`; remove those two String overloads so there is no forwarding compatibility facade. Keep its native Region overloads with their existing consumers. `GemfireUtils` loses the String methods through inheritance; migrate those calls too.
- Preserve existing behavior for null, empty/blank strings, already-prefixed paths, nested paths, trailing separators and whitespace. These methods format strings; do not add validation, normalization or idempotence. Use the literal `/` within this pure formatter, removing its dependency on the native constant.
- Retarget the 17 existing caller files listed below. `spring-test-vmware-gemfire` receives import/call-site updates only; its native dependency and its independent internal normalizers remain unchanged.
- Add focused `RegionPathsTests` under `src/test/java/org/springframework/data/gemfire/util/`. Cover the inventory's observed behavior table, without mocking any native type.
- Extend the existing `spiCache` source/test selection and native-free boundary verification to include this utility and its tests. Keep the cache-package wildcard; do not weaken its coverage. Update task descriptions to mention the added utility if needed.

### Existing callers to update

- `spring-data-vmware-gemfire/src/integrationTest/java/org/springframework/data/gemfire/RegionDataPolicyShortcutsIntegrationTests.java`
- `spring-data-vmware-gemfire/src/integrationTest/java/org/springframework/data/gemfire/client/ClientRegionIntegrationTests.java`
- `spring-data-vmware-gemfire/src/integrationTest/java/org/springframework/data/gemfire/client/GemFireDataSourceIntegrationTest.java`
- `spring-data-vmware-gemfire/src/integrationTest/java/org/springframework/data/gemfire/client/LocalOnlyClientCacheIntegrationTests.java`
- `spring-data-vmware-gemfire/src/integrationTest/java/org/springframework/data/gemfire/config/annotation/AutoConfiguredAuthenticationConfigurationIntegrationTests.java`
- `spring-data-vmware-gemfire/src/integrationTest/java/org/springframework/data/gemfire/config/annotation/EnableClusterDefinedRegionsIntegrationTests.java`
- `spring-data-vmware-gemfire/src/integrationTest/java/org/springframework/data/gemfire/config/annotation/EnableEntityDefinedRegionsIntegrationTests.java`
- `spring-data-vmware-gemfire/src/integrationTest/java/org/springframework/data/gemfire/config/xml/support/PoolAlreadyExistsIntegrationTests.java`
- `spring-data-vmware-gemfire/src/integrationTest/java/org/springframework/data/gemfire/repository/support/SimpleGemfireRepositoryTransactionalIntegrationTests.java`
- `spring-data-vmware-gemfire/src/test/java/org/springframework/data/gemfire/client/GemfireDataSourcePostProcessorTest.java`
- `spring-data-vmware-gemfire/src/test/java/org/springframework/data/gemfire/config/annotation/EnableCompressionConfigurationUnitTests.java`
- `spring-data-vmware-gemfire/src/test/java/org/springframework/data/gemfire/config/annotation/EnableEntityDefinedRegionsUnitTests.java`
- `spring-data-vmware-gemfire/src/test/java/org/springframework/data/gemfire/repository/support/GemfireRepositoryFactoryUnitTests.java`
- `spring-data-vmware-gemfire/src/test/java/org/springframework/data/gemfire/repository/support/SimpleGemfireRepositoryUnitTests.java`
- `spring-test-vmware-gemfire/src/main/java/org/springframework/data/gemfire/tests/mock/CacheMockObjects.java`
- `spring-test-vmware-gemfire/src/main/java/org/springframework/data/gemfire/tests/mock/GemFireMockObjectsSupport.java`
- `spring-test-vmware-gemfire/src/test/java/org/springframework/data/gemfire/MockClientCacheApplicationIntegrationTests.java`

`JSONRegionAdvice` appears in the path-reference index but consumes native Region overloads; its source is not part of this extraction. `AutoRegionLookupBeanPostProcessorUnitTests` and the private normalizers inside `GemFireMockObjectsSupport` declare their own helpers; do not accidentally rewrite them based on matching names alone.

## SPI, native driver and mock behavior

No new SPI operations are required. Neither driver implementation needs changing. The utility can run without a driver or native classes. Existing callback, region, mock isolation, unsupported-feature and exact-version binding behavior stays covered by the established checks. Constants/defaults, durable-client configuration and native lifecycle/query/pool operations remain with their current consumers pending later tickets.

## Validation

Inspect the complete diff and search every use of `toRegionName`/`toRegionPath` after the change. Confirm remaining `RegionUtils` or inherited `GemfireUtils` calls resolve only to native Region overloads; there must be no unresolved String callers. Full source compilation is required to check the overload change across modules.

Run, with the session's Gradle authorization and `-DgemfireVersion=10.4.+`:

```sh
./gradlew :spring-data-vmware-gemfire:compileJava :spring-data-vmware-gemfire:compileTestJava :spring-data-vmware-gemfire:compileIntegrationTestJava :spring-test-vmware-gemfire:compileJava :spring-test-vmware-gemfire:compileTestJava -DgemfireVersion=10.4.+ --console=plain
./gradlew :spring-data-vmware-gemfire:spiCacheTest :spring-data-vmware-gemfire:spiCacheIntegrationTest :spring-data-gemfire-driver-spi:check :spring-data-gemfire-driver-core:check :spring-data-gemfire-driver-contract-tests:check :spring-data-gemfire-driver-mock:check :spring-data-gemfire-driver-10.4:check -DgemfireVersion=10.4.+ --console=plain
./gradlew :spring-data-vmware-gemfire:test --tests '*RegionUtilsUnitTests' --tests '*GemfireDataSourcePostProcessorTest' --tests '*EnableCompressionConfigurationUnitTests' --tests '*EnableEntityDefinedRegionsUnitTests' --tests '*GemfireRepositoryFactoryUnitTests' --tests '*SimpleGemfireRepositoryUnitTests' -DgemfireVersion=10.4.+ --console=plain
./gradlew :spring-test-vmware-gemfire:test --tests '*GemFireMockObjectsSupportUnitTests' --tests '*MockClientCacheApplicationIntegrationTests' -DgemfireVersion=10.4.+ --console=plain
```

The new pure behavior tests run in both focused cache/utility suites. Existing native integration-test callers must compile; broader native integration execution is only needed if a failure or a change beyond the planned import/call-site update warrants it. Record newly surfaced baseline failures separately rather than claiming the entire legacy suite passes. Do not request persistent Gradle authorization on the basis of the user's session-only permission.

## Answer

Implemented 2026-10-20.

- Added the native-free [RegionPaths](../spring-data-vmware-gemfire/src/main/java/org/springframework/data/gemfire/util/RegionPaths.java) utility and removed the String overloads from `RegionUtils`. Its native Region overloads remain for `JSONRegionAdvice`.
- Updated all 17 listed caller files, including the inherited `GemfireUtils` calls. Spring Test's private normalizers retain their existing behavior.
- Added eight [behavior tests](../spring-data-vmware-gemfire/src/test/java/org/springframework/data/gemfire/util/RegionPathsTests.java), covering both methods for every row in the inventory's table. Null, duplicate slash and whitespace behavior is preserved.
- Extended the focused source sets and native-free verification to include the utility and tests while retaining the entire cache-package wildcard. No SPI or driver behavior changes were needed.

All four validation commands above passed with `-DgemfireVersion=10.4.+`, resolving to `10.4.0-build.3056.df98c835`:

| Validation | Result |
| --- | --- |
| Main and test sources in both modules, plus Spring Data integration-test sources | Compilation passed |
| Cache/utility behavior with native-free mock runtime | 41 tests passed |
| Same behavior with native 10.4 runtime | 41 tests passed |
| Existing SPI/core/contract/mock/native driver checks | 67 passing tests retained; unchanged tasks up-to-date |
| Targeted Spring Data caller and RegionUtils tests | 106 tests passed |
| Targeted Spring Test mock support and client-cache tests | 7 tests passed |

The selected reports total 262 passing tests, with no failures, errors or skips; 195 ran in this validation and the 67 unchanged driver tests were up-to-date. Full legacy integration-test execution was not required for these import/call-site changes and is not claimed. Existing native API and Gradle deprecation warnings remain.

Reviewed the complete extraction/caller diff, searched all repository path-helper references, and passed `git diff --check`. The remaining old utility references to these methods consume native Region arguments only. Validation logs are local under `/private/tmp/sdgf-region-path-{compile,driver-tests,caller-tests,support-tests}.log`.

The next bounded investigation is [cache and region resolver migration dependencies](08-cache-region-resolver-migration-dependencies.md), covering the foundational runtime handle boundary and its consumers.
