Type: task
Status: open
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
