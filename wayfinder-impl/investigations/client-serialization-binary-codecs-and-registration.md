# Client serialization binary codecs and registration — 2026-10-31

Task 55 adds a typed binary codec and a serialization registry to the Driver SPI. Inventory stays at 3,851 rows / 292 types and moves to 2,276 missing, 870 partial, 705 implemented. This task owns 159 rows, all **implemented**: 136 `DataSerializer`, 11 `Instantiator`, 3 `DataSerializable`, 3 `InternalDataSerializer`, 3 `registerPdxMetaData` (`Cache`, `GemFireCache`, `ClientCache`) and 3 `ExpirationAttributes` binary rows moved from task 47. The 6 `CopyException` rows are **partial** and owned by task 31. The build resolved `com.vmware.gemfire:gemfire-core` **10.4.0-build.3068.c3a2bbfe**; `coverage-policy.json` `native_version` still reads `10.4.0-build.3056.df98c835`, and `validate_coverage.py --javap` passes against the policy artifact.

## API ownership

- **`DriverDataCodec`** (SPI, via `DriverConnection#getDataCodec`) carries every `DataSerializer` `readX`/`writeX` helper under its native name, `writeObject(value, out, allowJavaSerialization)`, `readObject(in, ObjectInputFilter)`, `writeRegion`/`readRegion` and `writeExpirationAttributes`/`readExpirationAttributes`. The 10.4 `GemFireDataCodec` delegates to the native static helpers, so the bytes are the native wire format. The mock throws `UnsupportedFeatureException` and promises no wire format.
- **`DriverSerializationRegistry`** (via `DriverConnection#getSerializationRegistry`) provides `registerSerializer`, `registerInstantiator(instantiator, distribute)`, `findSerializer` and `unregisterSerializer`. `DriverDataSerializer`, `DriverInstantiator` and `DriverDataSerializable` are the application callbacks. Native holds the registry state; the driver keeps no static registry. The mock keeps a connection-local registry.
- **`DriverConnection#registerPdxMetaData(Object)`** maps the 3 `registerPdxMetaData` rows. It is a default that throws `UnsupportedFeatureException`.
- Three `existing_spi` default mappings were added (`getDataCodec`, `getSerializationRegistry`, `registerPdxMetaData`).
- **Rows with no SPI method (marked implemented with a "Not exposed" note):**
  - the 8 event-id and context rows (`getEventId`, `setEventId`, `getContext`, `setContext` on `DataSerializer` and `Instantiator`), which are internal adapter state set by the native distribution layer;
  - the `PRESENT`/`INT`/`LONG`/`DOUBLE` marker constants and `TRACE_SERIALIZABLE`;
  - `equals`, `hashCode`, the `DataSerializer` constructor and the protected `readOptionalPrimitive` helper.
- **`CopyException`** (6 rows, owner reassigned to task 31, status partial) has no `DriverFailureKind` of its own. `GemFireExceptionMapper.translate` classifies it as `SYSTEM_FAILURE` with message `GemFire operation failed: <operation>` and the native cause retained. Spring translation adoption stays with task 31.

## Behavior and constraints found

- **Instantiator is serializer-backed.** It shares the serializer id space and writes its id as an int prefix. Its bytes are **not** wire-compatible with servers that register a native `Instantiator`. `distribute` is accepted but not honored, and registration is never distributed. Native `Instantiator.register(boolean)` does not guarantee strictly local behavior either.
- **Collisions.** Native `InternalDataSerializer._register` rejects a duplicate id only when the class differs, so the driver pre-checks `getSerializer(id) != null` and fails with a `GemFireDriverException`. The mock rejects id and class collisions as `INVALID_ARGUMENT`. Unregistering an absent id is a no-op.
- **`DISALLOW_JAVA_SERIALIZATION`** is a native `ThreadLocal` that does not restore nested state. It maps to the `allowJavaSerialization` argument of `writeObject`; no ThreadLocal is exposed.
- **Region references** are bound to the issuing connection. `writeRegion` writes the full path (or the native null marker). `readRegion` resolves against the existing cache and fails with `DriverRegionUnavailableException` for a missing region.
- **`ExpirationAttributes`.** Native `fromData` assigns raw fields, while the SPI value type normalizes (clamps) in its constructor. The codec follows the SPI type, so decoded values are normalized.
- **`registerPdxMetaData`** must reject non-PDX values rather than succeed silently. The mock validates its argument and connection state; the native call needs a live pool.
- **Native registry is process-wide.** The 10.4 `integrationTest` task forks per class, but tests still use distinct ids (9101–9103).

## Source and test evidence

- SPI: `spring-data-gemfire-driver-spi/.../DriverDataCodec.java`, `DriverSerializationRegistry.java`, `DriverDataSerializer.java`, `DriverInstantiator.java`, `DriverDataSerializable.java`, `DriverConnection.java`.
- Native: `spring-data-gemfire-driver-10.4/.../GemFireDataCodec.java`, `GemFireSerializationRegistry.java`, `GemFireDataSerializerBridge.java`, `GemFireInstantiatorSerializer.java`, `GemFireConnection.java`.
- Mock: `spring-data-gemfire-driver-mock/.../MockSerializationRegistry.java`, `MockConnection.java`.
- Tests (counts from `@Test`): `SerializationRegistryDefaultTests` (3), `MockSerializationRegistryTests` (6), `NativeSerializationRegistryTests` (3), `NativeDataCodecTests` (5), `NativeCodecReferencesTests` (5), and `GemFireExceptionMapperTests#copyFailuresAreSystemFailuresWithTrustedCauses`.

## Validation

Run by the user; all passed. Full per-module totals were not reported, so none are recorded here.

- `./gradlew :spring-data-gemfire-driver-spi:test --tests '*SerializationRegistryDefaultTests'`
- `./gradlew :spring-data-gemfire-driver-mock:test --tests '*MockSerializationRegistryTests'`
- `./gradlew :spring-data-gemfire-driver-10.4:integrationTest --tests '*NativeSerializationRegistryTests' --tests '*NativeDataCodecTests' --tests '*NativeCodecReferencesTests' --tests '*GemFireExceptionMapperTests'`
- `./gradlew testMigrationBoundary`
- `python3 -m unittest discover -s wayfinder-impl/coverage -p 'test_*.py' -v`

Agent-run: `build_api_coverage.py --check` and `validate_coverage.py --javap` pass (3,851 rows; 2,694 required-client, 1,157 excluded-server, 0 unresolved), and `verifyMigrationBoundary` passes.

## Boundaries

Task 31 owns Spring adoption and `CopyException` consumer translation. Task 21 owns the query `ObjectType`/`CollectionType`/`StructType` codec implementations, which build on this codec. Task 58 owns nested value adaptation. Parent task 26 stays open.
