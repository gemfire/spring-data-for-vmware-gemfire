Type: task
Status: open
Blocked by: 50, 51

# Durable client readiness and pool runtime lifecycle

## Question

Child of [Complete pool, default-cache and durable SPI](20-complete-pool-default-cache-and-durable-spi.md). Apply the [shared completion requirements](../wayfinder-impl/spi-gap-closure-plan.md#shared-completion-requirements).

Model typed durable ID/timeout, readyForEvents, client close(keepAlive), pool destroy(keepAlive), pending-event count and online/current topology. Complete injected registry operations needed to replace PoolManager.getAll/close while passing native globals only inside the native driver; never add a driver global registry. Keep query/authenticated-view operations assigned to their subsystem owners. Verify typed connectivity/resource failures and preserve runtime native distinctions.

Use real server/client processes to verify keep-alive queue retention, reconnect replay, readiness timing, expiration/non-retention, attached-pool destruction, and sentinel/error states from pending-event count. Native interest fixture setup may be used internally before the public interest SPI is ready; do not create a dependency cycle with its existing pool prerequisite. Mock must reject unsupported network/queue operations explicitly. Complete parent acceptance only after these and default-pool assembly pass.

## Completion

Record source/API mappings, native artifact, commands and behavior evidence in an Answer. Resolve only this child, keeping the complete parent and downstream cutover owners open.
