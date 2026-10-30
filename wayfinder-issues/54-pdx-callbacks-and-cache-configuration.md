Type: task
Status: resolved
Blocked by: 53, 59

# PDX callbacks and cache configuration

## Client-only scope — 2026-10-22

Apply the [approved client-only boundary](../wayfinder-impl/client-only-scope.md). [Cleanup ticket 59](59-remove-server-only-spi-and-reconcile-client-scope.md) must complete first. Full/complete coverage in this ticket means every retained client requirement; historical native reachability alone does not require peer/server APIs. Preserve all retained client tests and native-free boundaries.

## Question

Child of [PDX, serialization and JSON SPI](26-pdx-serialization-and-json-spi.md). Apply the [shared completion requirements](../wayfinder-impl/spi-gap-closure-plan.md#shared-completion-requirements).

Implement all PdxReader/PdxWriter/PdxUnreadFields/PdxSerializer and application PdxSerializable equivalents, typed cache PDX configuration (readSerialized, ignoreUnreadFields, persistence, disk store, serializer), native adaptation/identity and meaningful local mock behavior. Preserve unread fields across schema evolution, nested callbacks, serializer decline, checked/unchecked errors and cache compatibility. Verify real native round trips and effective client-cache settings. Discover and freeze any required callback types omitted by the initial inventory.

## Completion

Record exact API ownership, source/test evidence, resolved native artifact and validation commands/results. Keep the parent open until all children and required behavior are complete; Spring consumer migration remains downstream.

## Answer

Resolved 2026-10-31. [Implementation and validation evidence](../wayfinder-impl/investigations/client-pdx-callbacks-and-cache-configuration.md) records exact ownership, native behavior and limits.

- Added typed `DriverPdxReader/Writer/UnreadFields/Serializer/Serializable` contracts, an immutable `PdxConfiguration` (readSerialized, ignoreUnreadFields, persistence, disk store, serializer) carried by `CacheConfiguration`, and the `DriverConnection#getPdxConfiguration` default. The 10.4 adapter bridges native callbacks and applies the settings in `GemFire104Driver#connect`. Client persistence and disk store are read back but ignored by the pinned client; no client PDX disk persistence is promised.
- Mock provides local reader/writer/unread-field behavior over a connection-local codec and returns the configured settings.
- Tests: `PdxConfigurationTests` (3), `MockPdxCallbackTests` (7) and `NativePdxCallbackTests` (5) cover effective client-cache settings, real native byte round trips, unread-field preservation across rewrite, declining serializers and driver exceptions in the cause chain. Native tests need a live pool, so they use the private server fixture.
- Validation: the user ran the three test classes and `testMigrationBoundary`; all passed. Coverage generation `--check` and `validate_coverage.py --javap` pass. Resolved native artifact: `gemfire-core` 10.4.0-build.3068.c3a2bbfe.
- Inventory remains 3,851 rows / 292 types: 2,440 missing, 865 partial, 546 implemented. This child owns 85 rows: 80 implemented (60 callback, 15 getter, 5 `ClientCacheFactory` setter) and 5 peer `CacheFactory#setPdx*` rows excluded. The 3 `registerPdxMetaData` rows are unimplemented and now owned by [task 55](55-binary-serialization-and-registration.md).

The serialization parent remains open for the other children; entry-point and callback adoption stay with tasks 35 and 31. No commit was created.

## Serialization audit handoff — 2026-10-28

Task 71 retains 60 reader/writer/serializer/unread-field rows plus 18 shared cache settings/metadata-registration rows. Test missing defaults versus type failures, manual unread-field ordering/preservation, automatic preservation, serializer decline and nested callback failure translation. Client persistence and disk-store options are accepted/read back but ignored by the pinned client implementation; do not promise client PDX metadata disk persistence. Preserve real metadata serialization/validation. Five peer CacheFactory setters are excluded; use typed client configuration. Task 35 owns entry-point adoption, task 31 callback/mapping adoption. Existing PdxSerializable discovery stays here without enlarging task 71. See [the exact source-backed ledger](../wayfinder-impl/investigations/client-serialization-pdx-and-json-scope.md).
