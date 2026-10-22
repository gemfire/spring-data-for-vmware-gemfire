# PDX values and instance factories

Implementation evidence for [PDX values and instance factories](../../wayfinder-issues/53-pdx-values-and-instance-factories.md), a blocking child of [PDX, serialization and JSON SPI](../../wayfinder-issues/26-pdx-serialization-and-json-spi.md).

## Boundary and ownership

The original parent owns 378 frozen API rows across distinct serialization systems. It stays open, preserving all original consumer blockers. The decomposition assigns every original row:

| Child | Original rows | Additional inherited cache rows |
| --- | ---: | ---: |
| PDX values and instance factories | 70 | 8 creation operations |
| PDX callbacks and cache configuration | 60 | 28 PDX configuration setters/getters |
| Binary serialization and registration | 158 | 0 |
| JSON documents and PDX conversion | 36 | 0 |
| Reflection-based PDX serialization | 54 | 0 |

[Serialization value boundary adaptation](../../wayfinder-issues/58-serialization-value-boundary-adaptation.md) is the explicit integration blocker for transporting these values across region operations, callbacks, live entries, exception payloads and later query/function boundaries. It also owns the typed replacement for the native `Serializable` marker. This slice does not Java-serialize adapter fields or offer a native unwrap API. Spring mapping/JSON consumer adoption remains with the existing migration owners after the complete parent.

The PDX callback child explicitly includes application `PdxSerializable` reachability, which needs its own frozen callback inventory during that implementation. Creating an instance through the native factory is not implementation of application reader/writer callbacks.

## Contracts and native implementation

- `DriverDocument` exposes field names, presence, access, traversal, equality/hash and checked field access. The immutable enum `MissingField.INSTANCE` replaces native `Document.FIELD_NOT_FOUND`, preserving the difference between an absent field and a present null value without a static object sentinel.
- `DriverPdxInstance` exposes class name, enum/deserializability state, identity fields, object materialization and writable copies. `DriverWritablePdxInstance.setField` mutates an independent copy using native field validation.
- `DriverPdxInstanceFactory` covers every native factory method: all 22 field kinds, object/object-array portability overloads, both generic field overloads, identity marking, never-deserialize and single-use creation.
- Two new defaults on `DriverRegionService` create factories and enum values, failing with typed unsupported for old providers. Both full connections and borrowed native region-service adapters delegate to their actual native service; no cache lookup singleton is introduced.
- Native field/write/object operations delegate to the bound engine with existing sanitized exception mapping. Application exceptions thrown by document traversal consumers retain their identity.
- Per-operation native conversion wraps nested PDX values in standard arrays/lists/maps/sets and unwraps only driver-owned handles inside the driver. Multidimensional PDX arrays, repeated references and container cycles are preserved by native conversion. Containers without PDX conversion needs pass through unchanged. Custom application containers requiring conversion, foreign-provider PDX values and other unimplemented native object results fail explicitly. Custom container/opaque object serialization and all operation-boundary transport retain the integration blocker; this is not a general application graph serializer.

No mutable static state or driver-owned schema registry is introduced. Native schema/enum registration remains in the engine. Mock enum metadata is scoped to its connection.

## Engine observations preserved by tests

Against GemFire `10.4.0-build.3056.df98c835`:

- Identity fields control equality independently of field declaration order and unmarked data. Added fields with default values can compare equal across schemas; field type changes remain significant, including zero-valued primitive fields.
- Null assignment to a writable primitive field resets it to the primitive default. It is not a type error. Missing fields and incompatible non-null field types fail through the SPI exception boundary.
- A second factory `create()` fails. Duplicate fields and marking an unwritten identity field fail.
- PDX enum values expose `name` and `ordinal`, but those fields are not explicitly marked identity fields. The engine rejects re-registering the same class/name with a different ordinal. Enum values cannot create writable copies.
- A client may allocate a PDX factory before a pool exists. Registering a schema or enum requires an initialized client pool; without it, the engine reports a cache-closed failure. The server fixture creates its proxy region/default pool before registering PDX values. The adapter does not invent a registry or create a pool as a side effect.
- Missing application-class deserialization raises a native `PdxSerializationException`, mapped by the existing exception foundation to `RESOURCE_FAILURE`, retaining its cause.

## Mock behavior and limits

The native-free mock implements local typed field snapshots, presence/null distinctions, identity/schema comparisons, writable copies, scalar/primitive-array/date/string/object-array fields, standard collection carriers and nested mock PDX values. Input and returned mutable fields are copied, so mutations do not change the stored snapshot. Enum ordinal consistency is checked within the connection.

Mock PDX hashes satisfy its equality contract but are not native wire hashes. It does not encode bytes, register server schemas, invoke application serializers or materialize application classes. Application object serialization/deserialization, foreign provider values and cyclic object encoding are explicitly unsupported. `neverDeserialize` values return themselves from `getObject`. These limits are explicit provider behavior, not claims that callback, JSON, binary serialization or network compatibility work is complete.

## Validation

**498 tests passed, with zero failures/errors/skips:** SPI 22, core 24, contract kit 6, mock 57, native 83, Spring mock 153, Spring native 153. Full compilation of Spring Data main/unit/integration and Spring Test main/unit sources passed. The full run completed in 1m21s (`/private/tmp/sdgf-pdx-full.log`). A final fix for application-supplied native PDX implementations in nested carriers was followed by all 83 native tests and the migration boundary passing again in 46s (`/private/tmp/sdgf-pdx-native-final.log`). All 18 boundary fixtures passed. Coverage regeneration/check, independent javap comparison of 148 declarations across 13 types, both negative controls and whitespace checks passed. The focused suite includes seven shared PDX tests run against both providers, an older-provider default-method test, two mock-specific tests, four native value tests and a real separate-process server/client PDX test. Native-only tests include direct engine-created control values, actual `DataSerializer` byte round trips, application `PdxSerializable` deserialization and enum materialization. The server fixture transports native values internally to validate factory output; full SPI region transport remains explicitly blocked.

Command:

```sh
./gradlew :spring-data-gemfire-driver-spi:check :spring-data-gemfire-driver-core:check :spring-data-gemfire-driver-contract-tests:check :spring-data-gemfire-driver-mock:check :spring-data-gemfire-driver-10.4:check :spring-data-vmware-gemfire:spiCacheTest :spring-data-vmware-gemfire:spiCacheIntegrationTest :spring-data-vmware-gemfire:compileJava :spring-data-vmware-gemfire:compileTestJava :spring-data-vmware-gemfire:compileIntegrationTestJava :spring-test-vmware-gemfire:compileJava :spring-test-vmware-gemfire:compileTestJava :verifyMigrationBoundary -DgemfireVersion=10.4.0-build.3056.df98c835 --offline --console=plain
./gradlew :spring-data-gemfire-driver-10.4:check :verifyMigrationBoundary -DgemfireVersion=10.4.0-build.3056.df98c835 --offline --console=plain
python3 build-tools/migration-boundary/test_boundary.py --java /Users/uk016635/.sdkman/candidates/java/17.0.20+1.1-librca/bin/java --classes build/migration-boundary-classes
python3 wayfinder-impl/audits/scan_native_references.py
python3 wayfinder-impl/coverage/build_api_coverage.py
python3 wayfinder-impl/coverage/build_api_coverage.py --check
python3 wayfinder-impl/coverage/validate_coverage.py --javap
```

The native core jar is `gemfire-core-10.4.0-build.3056.df98c835.jar`, cached under SHA-1 directory `747633c557af8a845af3cac42c0efac8297661d5`; Gradle 9.8.1 and Java 17.0.20 are used. The command pins the inventory artifact explicitly rather than allowing a newer cached dynamic version to change the validation target. User Gradle configuration edits are preserved.

The frozen inventory remains 3,851 rows / 292 native types: 2,988 missing, 583 partial, 280 implemented. This child has 78 rows: 77 partial retaining integration/adoption obligations and one implemented missing-field sentinel replacement. All 107 SPI default methods have native mappings and executable behavior probes. Current Spring native file counts remain 182 main / 122 unit / 128 integration. Normal boundary success is not strict native-free compliance.
