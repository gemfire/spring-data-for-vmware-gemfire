Type: task
Status: resolved
Assignee: Codex (resumed 2026-10-21)
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


## Answer

Implemented 2026-10-21 on `wayfinder/unified_driver`.

### Completed boundary

- `CacheResolver` and its four implementations use borrowed `DriverConnection` handles. Region resolvers use `DriverRegion`; `RegionServiceRegionResolver` accepts injected connections/cache resolvers and its nested strategy is now `ConnectionResolver`. Both bean-factory dependencies are final constructor arguments; setter replacement is removed. Named cache lookup uses a lazy type fallback, verified with two connection beans and an explicit qualifier.
- Removed the global cache resolver and replaced its singleton-based integration fixture with `InjectedResolverIntegrationTests`. Java configuration and ordinary Spring bean XML explicitly register driver connections/regions. The tests verify application ownership of shutdown and run on both driver runtimes. Legacy cache factories, annotations and the `gfe` namespace still produce native objects and are not interchangeable with the migrated resolver beans.
- Native `ClientCacheDefaultPoolResolver` now takes `Supplier<ClientCache>` and calls `get()` only for the exact default-pool name. It remains outside the native-free boundary. No pool SPI, native unwrap or forwarding facade was introduced.
- Cache and region compositions fall through only on null. Driver exceptions, typed unsupported failures, other runtime exceptions and errors propagate unchanged; later resolvers are not invoked and a failing resolver is not retried within the same resolution. This intentionally replaces legacy exception swallowing and duplicate invocation.
- Extended isolated source compilation and native-free classpath/import checks to the resolver interfaces, all retained implementations, and their existing `ArrayUtils`/`CollectionUtils` dependencies. The whole cache package and `RegionPaths` remain covered. All eleven resolver unit classes and the new wiring fixture run in both focused suites; the native pool suite runs separately.

### Lifecycle behavior and limits

Added `DriverRegion.addLifecycleListener(DriverRegionLifecycleListener)`, defaulting to `UnsupportedFeatureException` with feature `Region.addLifecycleListener` and reason `NOT_INTRODUCED`. The callback exposes only `afterRegionDestroy(DriverRegion<?, ?>)` and `close()`; it does not expose a native event/mutator or add a destruction operation solely for tests.

Native fixtures established that local peer region destruction, region close, and peer/client cache close deliver destruction followed by listener cleanup. Re-registering the same callback instance on the same underlying region is a no-op, including through separate SPI handles; distinct callbacks remain separate even if their `equals` methods consider them equal. The native adapter preserves identity through its equality implementation and delegates to native listener registration. Null registration and registration after closure fail through the driver exception boundary.

The mock implements this observed local lifecycle on connection close and releases its listener registrations. Native dispatch logs runtime callback failures and continues other notifications/cleanup; the mock follows that behavior with an instance logger, verified in the shared contract tests. This callback-dispatch behavior does not relax resolver failure propagation. No distributed delivery, additional ordering guarantee, or synthetic SPI-wide close notification is promised.

Resolvers retain serialized initial resolution, retry null misses and failed registrations, and borrow rather than close handles. Destruction invalidation remains keyed by the destroyed region's **name**: cached aliases and `/path` entries retain their existing limitation, covered explicitly by tests. Listener cleanup alone does not invalidate entries or reconnect. Cached connection handles remain cached after closure; single-region resolution does not probe destruction.

### Validation

All checks below passed against native GemFire **10.4.0-build.3056.df98c835**. The generated and packaged driver stamps both report that exact version and SPI `1.0`; the contract suite verifies runtime/stamp matching.

| Validation | Result |
| --- | --- |
| Main/test sources in both modules and Spring Data integration-test sources | Compilation passed |
| Focused cache/utility/resolver suite on native-free mock runtime | 118 tests passed |
| Same focused suite on native 10.4 runtime | 118 tests passed |
| Targeted resolver/pool tests plus explicit wiring fixture in normal test source set | 84 tests passed |
| SPI / core / contract-tool / mock / native-driver checks | 11 / 24 / 6 / 17 / 19 tests passed |

The selected reports total **397 passing test executions**, no failures/errors/skips. This is targeted validation, not a claim that the entire legacy integration suite ran. Reviewed the changed boundary/callers, checked for remaining native imports and deleted-global-resolver references in source, and passed `git diff --check`.

The existing user edit disables `spring-test-vmware-gemfire` in `settings.gradle.kts`, although Spring Data's build still depends on that project. An unmodified Gradle invocation therefore fails project configuration. Validation preserved both existing user-edited files and used `/private/tmp/sdgf-resolver-validation.init.gradle` with:

```groovy
settingsEvaluated { settings ->
    if (new File(settings.settingsDir, 'spring-test-vmware-gemfire').isDirectory()) {
        settings.include(':spring-test-vmware-gemfire')
    }
}
```

The guard avoids changing included build-tools projects. Run the commands in this ticket with `-I /private/tmp/sdgf-resolver-validation.init.gradle` and `-DgemfireVersion=10.4.+`; the targeted test invocation additionally included `--tests '*InjectedResolverIntegrationTests'`. No persistent Gradle permission rule was requested.

Final logs: `/private/tmp/sdgf-resolver-final-compile-tests.log` and `/private/tmp/sdgf-resolver-final-driver-tests.log`; initial native characterization: `/private/tmp/sdgf-resolver-native-lifecycle.log`.

Next: [Cache factory ownership and configuration dependencies](10-cache-factory-ownership-and-configuration-dependencies.md). This is the next investigation, not a completed factory migration. The signed-off architecture remains unchanged.
