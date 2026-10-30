Type: task
Status: resolved
Blocked by: 50, 59

# Default client-cache pool assembly and selection

## Client-only scope — 2026-10-22

Apply the [approved client-only boundary](../wayfinder-impl/client-only-scope.md). [Cleanup ticket 59](59-remove-server-only-spi-and-reconcile-client-scope.md) must complete first. Full/complete coverage in this ticket means every retained client requirement; historical native reachability alone does not require peer/server APIs. Preserve all retained client tests and native-free boundaries.

## Question

Child of [Complete pool, default-cache and durable SPI](20-complete-pool-default-cache-and-durable-spi.md). Apply the [shared completion requirements](../wayfinder-impl/spi-gap-closure-plan.md#shared-completion-requirements).

Add typed default-pool configuration to cache creation and true DriverConnection default-pool lookup. Apply settings before native ClientCacheFactory.create; preserve optional settings and native cache reuse/conflict behavior. Resolve how absent, empty, inherited and explicit endpoints interact with Spring precedence; do not treat DEFAULT or gemfirePool as the actual selected pool. Inspect all ClientCacheFactory pool setters and preserve removed/stored-only rejection.

Native tests must cover defaults, preexisting compatible/incompatible pools, generated default names, multiple-pool ambiguity, explicit overrides, locator versus server inheritance, and socket callback identity. Mock must document its local selection policy and reject unavailable native effects. Keep Spring entry-point cutover assigned to its existing owner.

## Completion

Record source/API mappings, native artifact, commands and behavior evidence in an Answer. Resolve only this child, keeping the complete parent and downstream cutover owners open.

## Task 69 default-selection handoff — 2026-10-28

The [default-pool source trace](../wayfinder-impl/investigations/client-pool-and-durable-scope.md#client-default-pool-selection) retains the assigned ClientCache.getDefaultPool ID here, with task 35 adoption. Preserve lazy native selection: empty registry/default localhost endpoint, singleton reuse, multiple-pool compatibility search and null when none matches, explicit factory endpoint fallback/compatibility, and unique creation names. Do not assume literal DEFAULT/gemfirePool lookup or deterministic lexical choice among compatible pools. Task 67's separate assembly IDs remain assigned there; no cross-family reassignment or implementation status upgrade occurs.

## Answer

Resolved 2026-10-30. Only task 51 is resolved; parent 20, task 35 (Spring cutover) and task 52 (`PoolManager` registry) stay open.

- **SPI**: `CacheConfiguration.defaultPool()` (optional typed `PoolConfiguration`, nullable 2-arg constructor) and `DriverConnection.getDefaultPool()` (default throws `notIntroduced`; returns null when nothing is selected).
- **Native adapter** (build `10.4.0-build.3068.c3a2bbfe`): `GemFireDefaultPoolConfiguration` applies the settings to `ClientCacheFactory.setPool*`, `addPoolServer` and `addPoolLocator` before `create()`; `GemFire104Driver.connect` lets `UnsupportedFeatureException` through unwrapped; `GemFireConnection.getDefaultPool()` wraps `ClientCache.getDefaultPool()`. `threadLocalConnections` is rejected as `removed`. Native cache reuse/conflict (`Existing cache's default pool was not compatible`) is left to the native cache.
- **Mock**: `MockGemFireDriver`/`MockConnection` document a local selection policy (empty registry creates a pool, one pool is selected, several give null) with a `localhost:40404` server fallback; no native compatibility/reuse is simulated.
- **Coverage**: 29 rows (`ClientCache#getDefaultPool`, `ClientCacheFactory` `addPoolLocator`, `addPoolServer`, 26 `setPool*`) are `implemented`; `existing_spi` gains `DriverConnection#getDefaultPool`. Because task 23 closed, its 312 unfinished retained rows and 47 SPI mappings were reassigned to open task 36 (no content change).
- **Behaviour evidence** (javap of `GemFireCacheImpl` on the 3068 jar): a missing endpoint becomes `LocalHostUtil.getLocalHostName()` as a **server** on port **40404** (not a locator), added only when both lists are empty; `determineDefaultPool` is lazy; generated names `DEFAULT`, `DEFAULT1`, … are never used as the selection key.
- **Validation (user-run, passed)**: `./gradlew :spring-data-gemfire-driver-mock:test --tests '*MockDefaultPoolTests' :spring-data-gemfire-driver-10.4:integrationTest --tests '*NativeDefaultPoolTests'` (5 mock + 11 native tests). Coverage tooling (run directly, passed): `build_api_coverage.py`, `build_api_coverage.py --check`, `validate_coverage.py`, `validate_coverage.py --javap`, and the coverage unittest discovery (16 tests).
- Known limits: Spring consumers still use native pool creation until task 35; this is not native-free compliance, and no commit was created.
