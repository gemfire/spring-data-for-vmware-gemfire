Type: task
Status: open
Blocked by: 14, 16

# Binary serialization and registration

## Question

Child of [PDX, serialization and JSON SPI](26-pdx-serialization-and-json-spi.md). Apply the [shared completion requirements](../wayfinder-impl/spi-gap-closure-plan.md#shared-completion-requirements).

Implement DataSerializer/DataSerializable/Instantiator required callbacks, registration, IDs, supported classes, primitive/object/collection helpers and assigned CopyException/internal serializer boundaries. Registration remains delegated to native engine state, never an SPI static registry. Resolve class-based registration and per-instance adapter IDs without global driver registries. Verify real native wire round trips, collisions, callback failures and compatibility; mock rejects unavailable wire-format and global-registration promises explicitly.

## Completion

Record exact API ownership, source/test evidence, resolved native artifact and validation commands/results. Keep the parent open until all children and required behavior are complete; Spring consumer migration remains downstream.
