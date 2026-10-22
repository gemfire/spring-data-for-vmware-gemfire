Type: task
Status: open
Blocked by: 53, 54, 55, 56, 57, 59

# Serialization value boundary adaptation

## Client-only scope — 2026-10-22

Apply the [approved client-only boundary](../wayfinder-impl/client-only-scope.md). [Cleanup ticket 59](59-remove-server-only-spi-and-reconcile-client-scope.md) must complete first. Full/complete coverage in this ticket means every retained client requirement; historical native reachability alone does not require peer/server APIs. Preserve all retained client tests and native-free boundaries.

## Question

Child of [PDX, serialization and JSON SPI](26-pdx-serialization-and-json-spi.md). Apply the [shared completion requirements](../wayfinder-impl/spi-gap-closure-plan.md#shared-completion-requirements).

Integrate native/SPI value conversion at region keys/values, conditional and bulk operations, entries/live views, event/loader/writer payloads, exception old values and later query/function results. Preserve nested containers, cycles/aliasing where supported, and application object types without unwrap escape hatches or driver-owned registries. Address Serializable on native PDX handles through a typed replacement; do not Java-serialize native adapter fields. Verify real client/server readSerialized, nested PDX/JSON keys/values, writable changes, callbacks and engine serializer interactions. Every remaining boundary must have an acceptance blocker; integrate reusable conversion with query/function owners without dependency cycles.

## Completion

Record exact API ownership, source/test evidence, resolved native artifact and validation commands/results. Keep the parent open until all children and required behavior are complete; Spring consumer migration remains downstream.
