Type: task
Status: resolved
Assignee: uk016635
Blocked by: 02

## Question

Begin Step 3's bottom-up migration with the Spring Cache integration: move `GemfireCache`, `GemfireCacheManager` and `GemfireCachingConfiguration` onto the Driver SPI and constructor-injected connections. Preserve cache lookup, loading, put-if-absent, eviction, clearing, explicit-region/name selection and dynamic lookup behavior.

## Scope

- Add region enumeration, lifecycle status, clear and put-if-absent to the SPI and both implementations, backed by shared driver contract tests.
- Migrate the three Spring Cache consumers and their callers/tests; leave `CallableCacheLoaderAdapter` for the loader-callback migration.
- Exercise the migrated integration with the official native-free mock, and enforce a native-free classpath for the focused migration tests while the rest of the module remains native-dependent.
- Record full-module compile results and the exact remaining migration boundary; do not claim completion of the cache/util/root phase.

## Comments

### 2026-10-20 — claimed

Continues the user's instruction to proceed after Step 2. The current `wayfinder/unified_driver` integration branch and all earlier uncommitted work are retained. Native validation continues against the requested `10.4.+` line, resolved and pinned by the reference driver build.

## Answer

Completed the Spring Cache slice on 2026-10-20. This is the first slice of Step 3, not completion of cache/util/root migration.

- `GemfireCache` now wraps `DriverRegion` and returns that SPI handle from `getNativeCache()`. It preserves typed/wrapped gets, null-value policy, callable loading and failure wrapping, synchronized loading through the same handle, put-if-absent, eviction and clear. Construct it directly with the SPI region; the native `wrap(Region)` entry point is removed.
- `GemfireCacheManager` requires a `DriverConnection` in its constructor. It borrows the connection rather than owning shutdown. Explicit regions override explicit names; either disables dynamic lookup; with neither configured it discovers root regions and resolves subsequently created regions. Missing regions and closed/destroyed resources fail descriptively. `setCache(ClientCache)` is removed.
- `GemfireCachingConfiguration` receives the application-owned `DriverConnection` through Spring injection. Applications configure their connection from an injected `GemFireDriverManager`; XML fixtures now demonstrate the equivalent factory methods and constructor injection.
- Added throwing default SPI methods `DriverConnection.rootRegions()` and `DriverRegion.isDestroyed()`, `clear()` and `putIfAbsent()`. Implemented them in the native driver and mock, with behavioral probes in the shared contract. The mock uses a concurrent map for atomic put-if-absent and retains state per connection.
- Replaced native Mockito setups in the cache unit tests with the official mock provider. The same behavior tests run against the real 10.4 provider. Tests cover loading/concurrency, region selection, annotations, XML wiring, error handlers and resource ownership. Error-handler tests inject SPI failures rather than catching native exceptions.
- Added transitional `spiCache`, `spiCacheTest` and `spiCacheIntegrationTest` source sets. They compile the **actual migrated production files**, not copied implementations. The mock suite has its own native-free compile/runtime dependencies and an enforced source/classpath boundary. The normal main source set still compiles these files as part of the application.
- Updated affected legacy integration callers. The repository-backed compound caching test still uses the unmigrated native repository setup; it now supplies an SPI connection alongside that setup. It could not be run through the full integration source set because of the baseline compile failures below.

### Validation

**101 tests passing, none skipped:** 22 migrated Spring Cache tests on the mock, the same 22 on the real driver, and 57 SPI/core/mock/native-driver/contract-kit tests. Both native-free boundary checks pass. Native runs used `10.4.0-build.3056.df98c835` selected via the user's `10.4.+` instruction.

```sh
./gradlew :spring-data-vmware-gemfire:spiCacheTest :spring-data-vmware-gemfire:spiCacheIntegrationTest :spring-data-gemfire-driver-mock:check :spring-data-gemfire-driver-10.4:check --console=plain
./gradlew :spring-data-gemfire-driver-spi:check :spring-data-gemfire-driver-core:check :spring-data-gemfire-driver-contract-tests:check --console=plain
```

The complete Spring Data main-source compile was attempted **before and after** the migration with `-DgemfireVersion=10.4.+`. Both fail with the same three pre-existing errors:

1. `PoolFactoryBean`'s anonymous `PoolAdapter` lacks native `Pool.getAvailabilityZone()`.
2. `DelegatingPoolAdapter`'s anonymous implementation lacks the same method.
3. `GemFireProperties` references the removed native `ConfigurationProperties.SECURITY_SHIRO_INIT` constant.

No additional main-source compile errors were introduced. The full legacy unit/integration suites are consequently unverified. `git diff --check` passes. Follow-up: [Restore the native 10.4 compile baseline](04-native-10.4-compile-baseline.md).

### Remaining boundary

`CallableCacheLoaderAdapter`, cache/region resolvers, `CacheUtils`, the factory beans and `GemfireTemplate`/`GemfireOperations` remain native-dependent. Continue bottom-up through those consumers after restoring the full compile baseline; migrate callback types and the remaining region/query surface as needed. The focused source sets are transitional verification infrastructure, not a permanent compatibility facade or a claim that the whole module is native-free.
