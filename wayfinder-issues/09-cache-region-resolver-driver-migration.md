Type: task
Status: open
Blocked by: 08

## Question

Implement the complete cache/region resolver boundary using injected driver handles, including region lifecycle notification, all direct callers, and native-free verification.

The [dependency and behavior inventory](../wayfinder-impl/investigations/cache-region-resolver-dependencies.md) identifies 13 main files, 12 unit-test files and one integration-test file. Factory beans perform their own resolution and do not require migration in this slice.

## Implementation scope

1. Retarget `CacheResolver` and its caching, single, bean-factory and composable implementations to `DriverConnection`. Preserve supplier/null semantics, synchronized memoization and borrowed ownership. Constructor-inject bean factories. Named lookup must use a lazy type fallback; cover multiple driver beans with a qualified name.
2. Retarget `RegionResolver` and its caching, single, bean-factory, service and composable implementations to `DriverRegion`. Replace native `RegionService` dependencies with injected `DriverConnection` / `CacheResolver`; update nested helper types/names and documentation accordingly. Preserve exact-name lookup, missing/blank behavior, `require` failures, cache keys and successful resolution ordering.
3. Replace native listener inheritance with a typed driver lifecycle callback. Add only the callback operation required for current destruction invalidation; new methods on `DriverRegion` must default to typed unsupported. Define callback registration/identity/cleanup behavior explicitly, adapt it inside the 10.4 driver, and implement supported local mock behavior. Native fixtures must establish destroy versus close notifications and duplicate-registration behavior before the mock asserts parity. Do not add public operations solely as test triggers, simulate distributed events, or silently skip unsupported registration.
4. Remove `ClientCacheFactoryCacheResolver` and its singleton/global lookup. Replace its integration test with explicit injected-connection coverage using existing single/bean resolvers. Resolution must not create or close a connection. Remove all references to the old class, including Javadocs.
5. Change `ClientCacheDefaultPoolResolver` to accept an injected `Supplier<ClientCache>` and invoke `get`. Update its test callers. Preserve exact-default-name filtering and null/failure behavior. It remains a native pool component; no driver unwrap, compatibility facade or pool SPI belongs here.
6. Compositions may fall through on null only. Propagate driver exceptions, unsupported failures, ordinary runtime exceptions and errors without invoking later resolvers. Update legacy swallowing/duplicate-call tests and document this intentional fail-fast behavior change.
7. Preserve and characterize current name-based destruction invalidation, including its alias/path limitation. Do not silently broaden it to a separate cache invalidation redesign. Retain serialized initial resolution; test null retry, failed registration, ownership and closed handles.
8. Extend isolated native-free source compilation/checks to the migrated interfaces/support implementations and needed native-free utilities. Retain existing coverage of the entire cache package and `RegionPaths`. Keep the native pool consumer outside this boundary.

Use the existing tests and reference index to account for every caller. Correct the cache-bean null-constructor test that currently instantiates `BeanFactoryPoolResolver`. Adapt or replace native mocks with SPI mocks/real driver fixtures as appropriate. No Spring Test source directly references the affected types; compiling that module is still required.

## Integration and validation

Add Java-configuration and ordinary Spring bean-XML tests registering explicit driver connections/regions. Exercise both official mock and native 10.4 runtimes, including qualified bean selection and lifecycle callbacks. Do not claim the legacy `@ClientCacheApplication` or `gfe` namespace now returns driver handles.

Run:

```sh
./gradlew :spring-data-vmware-gemfire:compileJava :spring-data-vmware-gemfire:compileTestJava :spring-data-vmware-gemfire:compileIntegrationTestJava :spring-test-vmware-gemfire:compileJava :spring-test-vmware-gemfire:compileTestJava -DgemfireVersion=10.4.+ --console=plain
./gradlew :spring-data-vmware-gemfire:test --tests '*CacheResolverUnitTests' --tests '*RegionResolverUnitTests' --tests '*ClientCacheDefaultPoolResolverUnitTests' -DgemfireVersion=10.4.+ --console=plain
./gradlew :spring-data-vmware-gemfire:spiCacheTest :spring-data-vmware-gemfire:spiCacheIntegrationTest :spring-data-gemfire-driver-spi:check :spring-data-gemfire-driver-core:check :spring-data-gemfire-driver-contract-tests:check :spring-data-gemfire-driver-mock:check :spring-data-gemfire-driver-10.4:check -DgemfireVersion=10.4.+ --console=plain
```

The test-name patterns cover the 11 cache/region resolver unit classes plus the default-pool class. Include any renamed or newly added suites explicitly. Wire the new native-free resolver tests into the mock/native focused tasks, or add equivalent dedicated tasks and run them. Extend contract coverage for the new default SPI operation and native callback adaptation. Recheck the resolved native version and generated stamp.

Gradle execution is authorized for the current user session only. Do not persist a Gradle permission rule.

## Completion

Record changed contracts, exact runtime version, executed checks and remaining limits. Update the map with the completed boundary and create the next precise migration/investigation ticket from the remaining utility/root dependencies. Factory, pool, query/function and legacy namespace migration remain later work; the signed-off architecture is unchanged.
