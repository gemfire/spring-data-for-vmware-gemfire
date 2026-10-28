Type: task
Status: open
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
