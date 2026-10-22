Type: task
Status: open
Blocked by: 53

# PDX callbacks and cache configuration

## Question

Child of [PDX, serialization and JSON SPI](26-pdx-serialization-and-json-spi.md). Apply the [shared completion requirements](../wayfinder-impl/spi-gap-closure-plan.md#shared-completion-requirements).

Implement all PdxReader/PdxWriter/PdxUnreadFields/PdxSerializer and application PdxSerializable equivalents, typed cache PDX configuration (readSerialized, ignoreUnreadFields, persistence, disk store, serializer), native adaptation/identity and meaningful local mock behavior. Preserve unread fields across schema evolution, nested callbacks, serializer decline, checked/unchecked errors and cache compatibility. Verify real native round trips and effective peer/client settings. Discover and freeze any required callback types omitted by the initial inventory.

## Completion

Record exact API ownership, source/test evidence, resolved native artifact and validation commands/results. Keep the parent open until all children and required behavior are complete; Spring consumer migration remains downstream.
