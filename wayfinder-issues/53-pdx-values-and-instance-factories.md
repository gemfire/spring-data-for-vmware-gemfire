Type: task
Status: resolved
Assignee: Codex
Blocked by: 14, 16

# PDX values and instance factories

## Question

Child of [PDX, serialization and JSON SPI](26-pdx-serialization-and-json-spi.md). Apply the [shared completion requirements](../wayfinder-impl/spi-gap-closure-plan.md#shared-completion-requirements).

Implement native-free document/PDX instance, writable instance and factory contracts; cache/region-service creation and enum values. Delegate native field/schema/identity behavior, preserve missing versus null fields, and adapt nested PDX values. Implement documented local mock values with explicit rejection of native deserialization/wire-format behavior. Verify every factory field kind, identity/schema evolution, writable isolation, failures, enums and native wire round trips. Operation-boundary transport and Java serialization of SPI handles remain blocked by the explicit adaptation child.

## Completion

Record exact API ownership, source/test evidence, resolved native artifact and validation commands/results. Keep the parent open until all children and required behavior are complete; Spring consumer migration remains downstream.

## Answer

Resolved 2026-10-22. [Implementation and validation evidence](../wayfinder-impl/investigations/pdx-values-and-instance-factories.md) records exact behavior, mappings and provider limits.

- Added typed document/PDX/writable/factory contracts, all native instance-factory operations and two backward-compatible region-service defaults. Native adapters cover nested typed values, identity/schema comparison, writable copies, enum registration, materialization and failure mapping.
- Mock supports local field snapshots, schema/identity comparison, writable values and connection-scoped enum consistency. Application serialization/deserialization and cyclic object encoding fail explicitly; no wire compatibility or server registry is simulated.
- Seven shared PDX tests run on both providers. Native evidence includes direct native control values, actual byte round trips, application deserialization and a separate-process server/client schema/enum round trip. Application-supplied native PDX implementations inside containers are also adapted without leaking native handles.
- Validation: **498 tests, zero failures/errors/skips**: SPI 22, core 24, contract kit 6, mock 57, native 83, Spring mock 153, Spring native 153. Full Spring Data main/unit/integration and Spring Test main/unit compilation passed. The final native-only conversion correction was followed by a successful rerun of all 83 native tests and the migration boundary. All 18 boundary fixtures, deterministic coverage generation/check, independent javap comparison and whitespace checks passed.
- Inventory remains 3,851 rows / 292 types: 2,988 missing, 583 partial, 280 implemented. This child owns 78 rows (77 partial pending operation-boundary integration/adoption; one implemented sentinel replacement). Every one of the 107 current SPI defaults has a mapping and behavioral probe.

The serialization parent remains open, blocked by [PDX callbacks and cache configuration](54-pdx-callbacks-and-cache-configuration.md), [Binary serialization and registration](55-binary-serialization-and-registration.md), [JSON documents and PDX conversion](56-json-documents-and-pdx-conversion.md), [Reflection-based PDX serialization](57-reflection-based-pdx-serialization.md) and [Serialization value boundary adaptation](58-serialization-value-boundary-adaptation.md). All 378 original parent rows and every downstream blocker are retained. Full SPI region transport, native-free Spring mapping and native serialization compatibility are not declared complete. No commit was created.

## Serialization audit handoff — 2026-10-28

Task 71 retains all 78 assigned value/factory/document rows. The implemented missing-field sentinel stays here. Ownership of 77 partial rows moves to active task 58 for transport and Serializable replacement; implementation mappings/statuses remain unchanged. This resolved foundation is not reopened, and task 31 retains Spring mapping adoption. See [the exact source-backed ledger](../wayfinder-impl/investigations/client-serialization-pdx-and-json-scope.md).
