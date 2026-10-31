Type: task
Status: resolved
Blocked by: 50, 51, 59

# Durable client readiness and pool runtime lifecycle

## Client-only scope — 2026-10-22

Apply the [approved client-only boundary](../wayfinder-impl/client-only-scope.md). [Cleanup ticket 59](59-remove-server-only-spi-and-reconcile-client-scope.md) must complete first. Full/complete coverage in this ticket means every retained client requirement; historical native reachability alone does not require peer/server APIs. Preserve all retained client tests and native-free boundaries.

## Question

Child of [Complete pool, default-cache and durable SPI](20-complete-pool-default-cache-and-durable-spi.md). Apply the [shared completion requirements](../wayfinder-impl/spi-gap-closure-plan.md#shared-completion-requirements).

Model typed durable ID/timeout, readyForEvents, client close(keepAlive), pool destroy(keepAlive), pending-event count and online/current topology. Complete injected registry operations needed to replace PoolManager.getAll/close while passing native globals only inside the native driver; never add a driver global registry. Keep query/authenticated-view operations assigned to their subsystem owners. Verify typed connectivity/resource failures and preserve runtime native distinctions.

Use real server/client processes to verify keep-alive queue retention, reconnect replay, readiness timing, expiration/non-retention, attached-pool destruction, and sentinel/error states from pending-event count. Native interest fixture setup may be used internally before the public interest SPI is ready; do not create a dependency cycle with its existing pool prerequisite. Mock must reject unsupported network/queue operations explicitly. Complete parent acceptance only after these and default-pool assembly pass.

## Completion

Record source/API mappings, native artifact, commands and behavior evidence in an Answer. Resolve only this child, keeping the complete parent and downstream cutover owners open.

## Task 69 runtime handoff — 2026-10-28

The [pool/durable ledger](../wayfinder-impl/investigations/client-pool-and-durable-scope.md) retains **16 assigned runtime/registry IDs** here, all with task 35 adoption. Implement lifecycle/keepAlive with attachment guards and explicit ownership: PoolManager/cache shutdown uses basicDestroy, unlike individual public destroy. Preserve absent lookup, mutable map snapshots with live pool values, region-to-pool association and live endpoint views distinct from configured endpoints; expose injected scoped operations, not native global registries.

Readiness and pending-count prerequisites differ: PoolImpl.readyForEvents returns early without a queue manager, while getPendingEventCount rejects that case and post-ready use. Counts are reconnect-time session observations, including -1 expired and -2 first-connect/no-prior-queue sentinels. Preserve dispatch/failover behavior and validate actual reconnect/replay/timeout effects in native integration tests; mock must state unsupported remote behavior. Source settles client scope without another runtime probe. The existing implementation and compatibility acceptance remains open.

## Shared exception handoff — task 76

Complete pool exhaustion, connectivity/refusal and received server-operation failures with consumer 35. Keep outer operation categories and deliberate nested payload handling; version-refused handshakes expose a connectivity failure, not the VersionException contract. See the [exact exception scope ledger](../wayfinder-impl/investigations/client-shared-exception-scope.md). Task 76 Java validation and generated inventory reconciliation passed on 2026-10-29. Existing implementation statuses are not upgraded.

## Endpoint observation foundation — validation passed

The task-23 continuation implements this ticket's two endpoint rows through `DriverConnection.getCurrentServers()` and `DriverPool.onlineLocators()`. Both use immutable detached host/port snapshots; native lookup preserves JVM-global pool aggregation and last-successful locator-list semantics, while the mock reports empty observations. Native tests cover real server loss, locator discovery and retained observations after destruction, plus snapshot isolation and failure translation. See [evidence and the user-run validation command](../wayfinder-impl/investigations/client-runtime-handles.md#endpoint-observations--validation-passed). User-run validation passed: 61 native tests with zero failures/errors/skips, plus driver compatibility and transitional migration-boundary verification. Task-35 adoption remains pending. This bounded foundation does not complete the prerequisites or the durable readiness, keepAlive, queue replay and registry lifecycle obligations above; task 52 remains open.

## Answer — resolved 2026-10-31

Child 52 is resolved; parent 20 and the downstream owners (task 35 adoption, task 36, task 69 handoff, task 76 exception handoff) stay open.

**SPI and mappings.** `DriverConnection` gains `getPools()`, `getPoolForRegion(String)`, `closePools(boolean)`, `readyForEvents()` and `close(boolean)`; `DriverPool` gains `destroy(boolean)` and `getPendingEventCount()`. They map to `PoolManager.getAll/find(Region)/close()/close(boolean)`, `ClientCache.readyForEvents/close(boolean)` and `Pool.destroy(boolean)/getPendingEventCount`. `Pool.destroy()/isDestroyed()` and `PoolManager.createFactory()/find(String)` are upgraded from partial to implemented. The two endpoint rows (`getCurrentServers`, `onlineLocators`) stay partial for task 35 adoption.

**Native artifact.** GemFire build `10.4.0-build.3068.c3a2bbfe` (the frozen inventory keeps the `3056.df98c835` label).

**Behavior evidence** (`NativePoolLifecycleTests`, five tests):
- `getPools()` is a detached copy; `getPoolForRegion` returns the region's pool, null for a local-only region and `INVALID_ARGUMENT` for an absent region.
- `readyForEvents` with pools but no durable pool, and `getPendingEventCount` on a nondurable pool, map to `INVALID_STATE`; an empty registry is accepted. The pending count must be read before `readyForEvents`.
- `destroy()` rejects a pool with attached regions; `closePools` removes every pool.
- A `close(true)` keeps the durable queue and reconnect replays queued events; `close(false)` discards it.

The `GemFireDriverContract` probes cover every new default. The mock rejects `keepAlive`, `readyForEvents`, `getPoolForRegion` and `getPendingEventCount` with `UnsupportedFeatureException.notIntroduced`.

**Validation (user-run):** `./gradlew :spring-data-gemfire-driver-mock:test :spring-data-gemfire-driver-10.4:integrationTest --tests '*NativePoolLifecycleTests' --tests '*GemFire104DriverContractTests' --tests '*NativePoolTests' --tests '*NativeDefaultPoolTests' --tests '*NativeConnectionReuseTests' verifyMigrationBoundary verifyDriverCompatibility` finished with BUILD SUCCESSFUL in 48s. Coverage regeneration, `--check`, `validate_coverage.py` and the coverage unit tests pass.
