Type: task
Status: resolved
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


## Answer

Resolved 2026-10-31. [Implementation and validation evidence](../wayfinder-impl/investigations/client-serialization-binary-codecs-and-registration.md) records exact ownership, native behavior and limits.

- Added an injected typed `DriverDataCodec` (`DriverConnection#getDataCodec`) over the native `DataSerializer` wire format, a `DriverSerializationRegistry` (`getSerializationRegistry`) for serializers and serializer-backed instantiators, and `DriverConnection#registerPdxMetaData`. Native keeps all registry state; the driver has no static registry. The mock has a connection-local registry, validates `registerPdxMetaData`, and refuses to promise a wire format.
- Limits: instantiators share the serializer id space and are not wire-compatible with native-instantiator servers; `distribute` is not honored and registration is never distributed; the Java-serialization ThreadLocal is mapped to a `writeObject` argument and does not restore nested state; region references bind to the issuing connection.
- This task owns 159 implemented rows. The 3 `ExpirationAttributes` binary rows moved here from task 47. The 6 `CopyException` rows map to `SYSTEM_FAILURE` (partial) and moved to [task 31](31-template-repository-and-mapping-migration.md).
- Inventory: 3,851 rows / 292 types; 2,276 missing, 870 partial, 705 implemented. Resolved native artifact: `gemfire-core` 10.4.0-build.3068.c3a2bbfe. The user ran the SPI, mock and 10.4 native tests, `testMigrationBoundary` and the coverage unit tests; all passed. No commit was created.

## Query/CQ audit coordination — 2026-10-28

Task 65 classifies six inherited ObjectType/CollectionType/StructType toData/fromData rows as required-client. Primary implementation ownership stays with task 21, consumer ownership with 31. Supply native-free serialization collaborators for equivalent encode/decode, private versioned wire codecs, nested type/field ordering, class resolution and IO failures. Validate native round trips without promising mock native bytes or a universal cross-version format. See [the source-backed disposition](../wayfinder-impl/investigations/client-query-and-cq-scope.md).

## Task 68 binary-value coordination — 2026-10-28

[ExpirationAttributes binary members](../wayfinder-impl/investigations/client-expiry-eviction-and-compression-scope.md#client-expiry-binary-values) are required-client: toData, fromData and createFromData. Task 47 retains their implementation ownership and task 30 adoption; this task supplies codec collaboration. Preserve timeout/action round trips and direct hydration that does not repeat constructor normalization. Keep native DataSerializer framing inside versioned drivers. No new assignment or status upgrade is implied.

## Serialization audit handoff — 2026-10-28

Task 71 retains 153 binary serialization/registration rows. Provide injected typed codecs, callback and instantiator registration, lookup/remove behavior and client region-reference resolution. Preserve kind/null/order/filter/checked-error semantics and validate collisions, delayed class loading and propagation. Keep native event/membership contexts, registry state, framing constants and Java-serialization ThreadLocal private. Test or document the pinned nested-option reset behavior; do not promise distribute=false is strictly local. Coordinate EnumSerializer and InstantiatorFactoryBean/ASM/Wiring migration with task 31. Query/expiry codecs retain their existing ownership. Scope classification does not certify wire compatibility. See [the exact source-backed ledger](../wayfinder-impl/investigations/client-serialization-pdx-and-json-scope.md).

## Shared exception handoff — task 76

Complete client copy/serialization failure construction, cause preservation and safe diagnostics with consumer 31. Coordinate admin.RegionNotFoundException region decoding with 31 and nested value adaptation 58. See the [exact exception scope ledger](../wayfinder-impl/investigations/client-shared-exception-scope.md). Task 76 Java validation and generated inventory reconciliation passed on 2026-10-29. Existing implementation statuses are not upgraded.
