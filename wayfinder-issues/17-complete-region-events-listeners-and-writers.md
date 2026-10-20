Type: task
Status: open
Blocked by: 14, 16

# Complete region events, listeners and writers

## Question

Provide full typed cache-listener and cache-writer contracts, their reachable event payloads and lifecycle, beyond the current destruction-only resolver callback.

Audit coverage: **G3, G4** in the [native/SPI gap audit](../wayfinder-impl/audits/native-spi-gap-audit.md). Apply the [shared completion requirements](../wayfinder-impl/spi-gap-closure-plan.md#shared-completion-requirements) and the signed-off [technical spec](../wayfinder/technical-spec.md).

## Required implementation

Prerequisites: [Required native API coverage matrix](14-required-native-api-coverage-matrix.md); [Driver exception semantics and translation](16-driver-exception-semantics-and-translation.md).

- Implement every required CacheListener callback: entry create/update/invalidate/destroy and region invalidate/destroy/clear/create/live, plus cleanup. Preserve the existing lifecycle API's semantics while adding the general listener contract.
- Model CacheEvent, EntryEvent and RegionEvent data: operation, callback argument/availability, remote/member identity, key, old/new value availability and serialized forms, transaction identity and client origin as required by the coverage matrix. Introduce shared identity/value types here; runtime member/transaction operations belong to their own tickets.
- Add writer before-event callbacks, rejection semantics, registration/replacement/removal and declarative callback initialization through injection. Complete loader/CacheCallback inherited initialization gaps identified by the matrix.
- Implement native adapters and local mock delivery for supported operations. Document native timing/ownership; no synthetic remote events or discarded callbacks. General factory adoption belongs to Region factory and configuration migration.

- Cover application-only listener specializations (`RegionMembershipListener`, `RegionRoleListener`, adapter classes), full inherited `CacheEvent`/`RegionEvent`/`EntryEvent` payloads and `SerializedCacheValue`. Preserve both `Declarable.init(Properties)` and `initialize(Cache, Properties)` semantics using typed callbacks. See the [member-level baseline](../wayfinder-impl/coverage/README.md).

## Required validation

- Exercise every callback and mapped payload through a real native registration; verify writer veto/error behavior, listener identity, removal/replacement and cleanup.
- Share tests for locally supported behavior; test region-live/remote/serialization-specific events in native fixtures. Prove missing adapter callbacks are detected.

## Completion

Complete required callback/event contract rows with native adapters and honest mock behavior, not merely methods inherited as no-ops. Record the Answer with changed source/API rows, commands/results, native version and limitations; update the implementation map. Do not mark dependent work complete from this ticket's result.
