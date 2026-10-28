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

## Serialization audit handoff — 2026-10-28

Task 71 transfers implementation ownership of 77 partial PDX value/factory/document rows from resolved task 53 to this active task; all mappings/statuses and existing consumer ownership stay unchanged. Complete transport across keys/values, conditional/bulk operations, entries/live views, callbacks/error old values and nested query/function results, including supported alias/cycle behavior and Serializable replacement. Keep typed schema/identity/writable/enum behavior from the foundation; no native unwrap or hidden pool creation. Coordinate task 31 for Spring mapping adoption. The implemented missing-field sentinel remains with task 53. See [the exact source-backed ledger](../wayfinder-impl/investigations/client-serialization-pdx-and-json-scope.md).

## Shared exception handoff — task 76

Coordinate native-free exception values across region old-value/region payloads (31), aggregate query failures (21/31), security principal/naming causes (24/35), disk context (27/30) and copy/JSON failures (55/56). Keep the fixed API ownership in the task-76 ledger; this is shared adaptation, not another classification audit. See the [exact exception scope ledger](../wayfinder-impl/investigations/client-shared-exception-scope.md). Task 76 Java validation and generated inventory reconciliation passed on 2026-10-29. Existing implementation statuses are not upgraded.

## Final reconciliation handoff — task 64

Own two partial current-SPI PDX factory/enum mappings transferred from foundation 53. Complete transport and mapping adoption with 31, including provider-infrastructure exclusion at the serialization boundary. See the [final scope ledger](../wayfinder-impl/investigations/client-scope-final-reconciliation.md) for exact counts and source dispositions. These transfers do not upgrade implementation status.
