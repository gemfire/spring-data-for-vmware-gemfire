Type: task
Status: open
Blocked by: 53

# JSON documents and PDX conversion

## Question

Child of [PDX, serialization and JSON SPI](26-pdx-serialization-and-json-spi.md). Apply the [shared completion requirements](../wayfinder-impl/spi-gap-closure-plan.md#shared-completion-requirements).

Implement JSONFormatter, JsonDocument/Factory, SerializableAsJson, StorageFormat and assigned parsing failures, including inherited Document operations and native-returning results. Delegate real JSON conversion, storage selection, encoding and failures. Define/test mock local JSON semantics or explicit unsupported behavior. Preserve missing/null/nested/array/numeric/class-name handling; retain Spring JSON advice adoption downstream.

## Completion

Record exact API ownership, source/test evidence, resolved native artifact and validation commands/results. Keep the parent open until all children and required behavior are complete; Spring consumer migration remains downstream.
