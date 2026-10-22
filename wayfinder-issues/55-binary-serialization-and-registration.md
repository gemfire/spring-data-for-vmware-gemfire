Type: task
Status: open
Blocked by: 14, 16, 59

# Binary serialization and registration

## Client-only scope — 2026-10-22

Apply the [approved client-only boundary](../wayfinder-impl/client-only-scope.md). [Cleanup ticket 59](59-remove-server-only-spi-and-reconcile-client-scope.md) must complete first. Full/complete coverage in this ticket means every retained client requirement; historical native reachability alone does not require peer/server APIs. Preserve all retained client tests and native-free boundaries.

## Question

Child of [PDX, serialization and JSON SPI](26-pdx-serialization-and-json-spi.md). Apply the [shared completion requirements](../wayfinder-impl/spi-gap-closure-plan.md#shared-completion-requirements).

Implement DataSerializer/DataSerializable/Instantiator required callbacks, registration, IDs, supported classes, primitive/object/collection helpers and assigned CopyException/internal serializer boundaries. Registration remains delegated to native engine state, never an SPI static registry. Resolve class-based registration and per-instance adapter IDs without global driver registries. Verify real native wire round trips, collisions, callback failures and compatibility; mock rejects unavailable wire-format and global-registration promises explicitly.

## Completion

Require serialization helpers needed by client values and client submission callbacks; broad reachability of the native SDK alone does not justify adding server-only registration or administration.

Record exact API ownership, source/test evidence, resolved native artifact and validation commands/results. Keep the parent open until all children and required behavior are complete; Spring consumer migration remains downstream.
