# Client PDX callbacks and cache configuration — 2026-10-31

Task 54 adds the application PDX callback contracts and typed client-cache PDX configuration to the Driver SPI. Inventory stays at 3,851 rows / 292 types: 2,440 missing, 865 partial, 546 implemented. This task owns 85 rows: **80 implemented** and 5 excluded peer setters. The 3 `registerPdxMetaData` rows were handed to task 55. The build resolved `com.vmware.gemfire:gemfire-core` **10.4.0-build.3068.c3a2bbfe** (local `gemfireVersion` pin in `gradle.properties`; `spring-data-gemfire-driver-10.4/build.gradle.kts` defaults to `10.4.+` and pins that one version with `strictly(...)`). `coverage-policy.json` `native_version` still reads `10.4.0-build.3056.df98c835`, and the early `javap` signature analysis used the 3069 jar; the member signatures compared are the same across these builds, and `validate_coverage.py --javap` passes against the policy artifact.

## API ownership

- **60 callback rows implemented** — `PdxReader`, `PdxWriter`, `PdxSerializer`, `PdxUnreadFields` members map to `DriverPdxReader`, `DriverPdxWriter`, `DriverPdxSerializer`, `DriverPdxUnreadFields` (SPI), `GemFirePdxReader/Writer/SerializerBridge/UnreadFields` (native) and `MockPdxReader/Writer/Codec/UnreadFields` (mock). Application `PdxSerializable` maps to `DriverPdxSerializable`.
- **15 getter rows implemented** — `Cache`, `GemFireCache` and `ClientCache` `getPdxReadSerialized/getPdxIgnoreUnreadFields/getPdxPersistent/getPdxDiskStore/getPdxSerializer` map to `DriverConnection#getPdxConfiguration` (a default that throws `UnsupportedFeatureException`), `GemFireConnection` and `MockConnection`. The new default has an `existing_spi` mapping.
- **5 `ClientCacheFactory#setPdx*` rows implemented** — typed immutable `PdxConfiguration` carried by the three-argument `CacheConfiguration`, applied in `GemFire104Driver#connect` and `MockGemFireDriver#connect`.
- **3 `registerPdxMetaData` rows remain missing and moved to task 55** (`Cache`, `GemFireCache`, `ClientCache`). They have no SPI method and are not claimed here. Resolving 54 requires every unfinished row to have an open owner, and 55 owns registration.
- **5 `CacheFactory#setPdx*` rows stay excluded** (peer-only).

## Behavior and constraints found

- **Live pool required.** A native client defines PDX types through a pool. `ClientCacheFactory` creates its default pool lazily, so without `getDefaultPool()` the first encode fails with `CacheClosedException` ("Client pools have been closed so the PDX type registry is not available"). Native round-trip tests use the private server fixture and force the pool.
- **Unread fields.** Native `readUnreadFields()` returns an empty placeholder that is filled only after `fromData`, and only when the stored type has fields the local type lacks and `ignoreUnreadFields` is false. Passing the empty placeholder to `writeUnreadFields` throws an NPE inside the native `PdxUnreadData`. The native test therefore registers an evolved type first (extra `age`) and verifies the rewrite restores the unread value.
- **Client persistence and disk store.** They are accepted and read back as configured but are ignored by the pinned client. No client PDX metadata disk persistence is promised.
- **Exceptions.** A `DriverOperationException` thrown from a serializer callback stays in the native exception's cause chain. A declining serializer falls through to native Java serialization.

## Source and test evidence

- SPI: `spring-data-gemfire-driver-spi/.../DriverPdx*.java`, `config/PdxConfiguration.java`, `CacheConfiguration.java`, `DriverConnection.java`.
- Native: `spring-data-gemfire-driver-10.4/.../GemFirePdx*.java`, `GemFire104Driver.java`, `GemFireConnection.java`.
- Mock: `spring-data-gemfire-driver-mock/.../MockPdx*.java`, `MockConnection.java`, `MockGemFireDriver.java`.
- Tests (counts from `@Test`): `PdxConfigurationTests` (3), `MockPdxCallbackTests` (7), `NativePdxCallbackTests` (5: effective settings read back, serializable round trip, unread-field preservation, decline/nested failures, driver exceptions in the cause chain).

## Validation

Run by the user; all passed. Full per-module totals were not reported, so none are recorded here.

- `./gradlew :spring-data-gemfire-driver-spi:test --tests '*PdxConfigurationTests'`
- `./gradlew :spring-data-gemfire-driver-mock:test --tests '*MockPdxCallbackTests'`
- `./gradlew :spring-data-gemfire-driver-10.4:integrationTest --tests '*NativePdxCallbackTests'`
- `./gradlew testMigrationBoundary`
- Agent-run: `build_api_coverage.py --check` and `validate_coverage.py --javap` pass (3,851 rows; 2,694 required-client, 1,157 excluded-server, 0 unresolved).

## Boundaries

Task 35 owns entry-point adoption and task 31 callback/mapping adoption. Task 55 owns binary codecs, 56 JSON, 57 reflection configuration and 58 the value boundary. Parent task 26 stays open.
