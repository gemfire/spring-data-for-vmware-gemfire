Type: task
Status: resolved
Blocked by: 53, 59

# JSON documents and PDX conversion

## Client-only scope — 2026-10-22

Apply the [approved client-only boundary](../wayfinder-impl/client-only-scope.md). [Cleanup ticket 59](59-remove-server-only-spi-and-reconcile-client-scope.md) must complete first. Full/complete coverage in this ticket means every retained client requirement; historical native reachability alone does not require peer/server APIs. Preserve all retained client tests and native-free boundaries.

## Question

Child of [PDX, serialization and JSON SPI](26-pdx-serialization-and-json-spi.md). Apply the [shared completion requirements](../wayfinder-impl/spi-gap-closure-plan.md#shared-completion-requirements).

Implement JSONFormatter, JsonDocument/Factory, SerializableAsJson, StorageFormat and assigned parsing failures, including inherited Document operations and native-returning results. Delegate real JSON conversion, storage selection, encoding and failures. Define/test mock local JSON semantics or explicit unsupported behavior. Preserve missing/null/nested/array/numeric/class-name handling; retain Spring JSON advice adoption downstream.

## Completion

Record exact API ownership, source/test evidence, resolved native artifact and validation commands/results. Keep the parent open until all children and required behavior are complete; Spring consumer migration remains downstream.

## Serialization audit handoff — 2026-10-28

Task 71 retains all 33 JSON/document/storage/conversion rows. Implement native-free document/factory APIs, PDX/BSON selection, nested numeric/null values, parser errors, identity options and injected client conversion context. Test authenticated-context cleanup, external PDX type-registration requirements and the pinned default-charset string conversion versus UTF-8 byte output; record any deliberate normalization. Define local mock JSON support separately from unsupported wire behavior. Task 31 migrates JSONRegionAdvice and retains CRUD/bulk/values/query assertions. See [the exact source-backed ledger](../wayfinder-impl/investigations/client-serialization-pdx-and-json-scope.md).

## Shared exception handoff — task 76

Complete client JSON parse failure construction, cause preservation and safe diagnostics with consumer 31; avoid leaking private document contents and coordinate value adaptation 58. See the [exact exception scope ledger](../wayfinder-impl/investigations/client-shared-exception-scope.md). Task 76 Java validation and generated inventory reconciliation passed on 2026-10-29. Existing implementation statuses are not upgraded.

## Answer

Resolved 2026-10-31. [Implementation and validation evidence](../wayfinder-impl/investigations/client-serialization-pdx-and-json-scope.md#json-resolution) records exact ownership, native behavior and limits.

- Added native-free `DriverJsonFormatter`, `DriverJsonDocument`, `DriverJsonDocumentFactory`, `DriverSerializableAsJson`, `DriverStorageFormat` and `DriverJsonParseException`, exposed as defaults on `DriverRegionService` (`getJsonFormatter`, `getJsonDocumentFactory`, `getJsonDocumentFactory(DriverStorageFormat)`). The 10.4 driver delegates conversion, storage selection, encoding and failures to the native provider; the formatter is bound to the injected service.
- Limits: the mock keeps local JSON with UTF-8 normalization and rejects BSON (`JsonDocument.bsonStorage`, not introduced). The 10.4 driver keeps the native default-charset decode in `fromPdxInstance`, pinned by a test. The ProxyCache set/clear is native-owned and only delegated. Spring translation of parse failures and `JSONRegionAdvice` remain with [task 31](31-template-repository-and-mapping-migration.md).
- This task owns 37 implemented rows. The four `JsonParseException` rows and three `DriverRegionService` JSON accessor mappings stay partial under task 31. Resolved native artifact: `gemfire-core` 10.4.0-build.3068.c3a2bbfe.
- Tests: `JsonDefaultTests` (SPI), `MockJsonTests` (mock), `NativeJsonTests` (10.4 integration). Compile, `verifyMigrationBoundary`, `verifyDriverCompatibility` and the coverage generate/validate passed; the test run is by the user. No commit was created.
