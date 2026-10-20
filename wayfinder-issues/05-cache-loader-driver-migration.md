Type: task
Status: resolved
Assignee: uk016635
Blocked by: 04

## Question

Complete the remaining cache-package migration by retargeting `CallableCacheLoaderAdapter` from native `CacheLoader`, `LoaderHelper` and `Region` types to driver-owned contracts.

Inventory the adapter's consumers and callback behavior first. Extend the SPI, reference driver, official mock and shared contract kit only as needed to preserve loading, key/region/argument propagation, failure propagation and callback lifecycle. Native callback adaptation belongs inside the reference driver. Unsupported operations must fail explicitly; the mock must not simulate distributed guarantees.

Retarget the existing adapter tests, add behavioral coverage across the mock and native driver where applicable, and expand the native-free cache boundary check to cover the entire migrated cache package. Run full main/test/integration-test compilation against `10.4.+` and the driver/cache regressions. Keep subsequent utility/root-package migration in the implementation map's remaining backlog.

## Answer

Resolved 2026-10-20 against `10.4.+`, resolved to `10.4.0-build.3056.df98c835`.

The consumer inventory found no production callers of `CallableCacheLoaderAdapter` elsewhere in the repository. Its existing behavior is preserved through `DriverCacheLoader` and `DriverLoaderHelper`: constructor validation; key, region and argument propagation; direct load/close forwarding; null return values; and unchanged callback exceptions when invoked directly. Synthetic callable network search now fails with a typed `UnsupportedFeatureException`.

Added two default, fail-fast operations to the existing `DriverRegion` interface: `setCacheLoader` and `get(key, argument)`. The new callback/helper interfaces require their initial methods explicitly; no existing interface acquired an abstract method. No SPI version policy or binding rule changed.

The reference driver adapts native callbacks entirely inside `GemFireCacheLoader`. Helper region handles remain SPI-owned, and network search delegates its flag/result to the native helper with boundary exception translation. Loader replacement, removal, same-instance reinstallation and shutdown follow native lifecycle. Shared contract coverage caught and fixed a same-instance reinstallation path that initially bypassed native closed-region validation.

The official mock implements local loading, caches nonnull results, retries null results on subsequent misses, forwards context and closes installed callbacks on replacement/removal/shutdown. It rejects network search explicitly. Mock shutdown releases all registered loaders even if a callback throws a runtime exception, then reports the failure; this is mock cleanup behavior, not a new guarantee imposed on native callback exception handling. Distributed loading and broader native concurrency behavior are not simulated.

The focused source set now includes `cache/**/*.java`, so compilation without native dependencies and the source/classpath boundary check cover the whole cache package rather than four named files. Native region-factory configuration outside this package remains part of later migration.

Validation:

- 33 cache behavior tests passed on each of the mock and native runtimes, including 11 loader-adapter tests per runtime.
- All driver checks passed: SPI 11, core 24, contract-kit 6, mock 14, native 12 — 67 tests. Shared tests exercise peer and client LOCAL loading, callback lifecycle, failure causes and invalid/closed operations. Two native adapter tests verify network-search delegation and failure translation without requiring a distributed cluster.
- Total: 133 passing tests, no failed or skipped cases. Unchanged driver checks reused Gradle's up-to-date outputs.
- Full `compileJava`, `compileTestJava` and `compileIntegrationTestJava` succeeded with `-DgemfireVersion=10.4.+`; both native-free boundary checks passed. Full legacy tests were compiled, not executed.
- Successful run log: `/private/tmp/sdgf-cache-loader-final-validation.log`.

The remaining utility/root migration has consumers spanning native cache factories, templates and repositories. Its next boundary is tracked by [Utility/root migration dependency inventory](06-utility-root-migration-dependencies.md).
