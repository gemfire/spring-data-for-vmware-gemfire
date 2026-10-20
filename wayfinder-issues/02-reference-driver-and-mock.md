Type: task
Status: resolved
Assignee: uk016635
Blocked by: 01

## Question

Implement Step 2 of the signed-off migration plan: a reference Driver Impl for the currently targeted GemFire version, the official native-free mock, generated driver stamps and the shared driver contract test kit.

## Scope

- The current version catalog targets GemFire 10.4.0, which is unavailable in the configured repositories. User directed `10.4.+` for this driver; resolve that selector once per build, then pin the native dependency, driver artifact version, stamp and integration tests to the concrete selected version.
- Extend the marker connection/configuration SPI with a usable cache lifecycle and region CRUD boundary. Preserve existing implementations with throwing default methods. Implement peer and client cache creation in the reference driver; exercise local peer regions without external servers.
- Add `spring-data-gemfire-driver-mock` with per-connection in-memory state and no native GemFire dependency.
- Generate provider-keyed stamps from the resolved SPI resource and native dependency; generate service registration from the same provider declaration. The mock uses an explicit synthetic `mock` native identity, never a claim of native compatibility.
- Add `spring-data-gemfire-driver-contract-tests`, shared by the reference driver and mock, covering binding, lifecycle, CRUD and unsupported-method declarations. Keep native tests in the reference driver's `integrationTest` source set.
- Verify the SPI/core regressions, mock contract, generated metadata and the real native contract. Document remaining subsystem APIs to expand during Step 3 as their native calls are migrated.

## Comments

### 2026-10-20 — implementation started

User authorized Step 2 after signing off the revised design. Work continues on the existing dedicated `wayfinder/unified_driver` branch. Step 1's connection and configuration types are markers, so a minimal usable cache/region boundary is necessary to implement and verify this step. No broader architecture decision is reopened.

## Answer

Implemented and verified on 2026-10-20. The Step 2 foundation is usable through the SPI: peer/client cache lifecycle, local-region CRUD, service discovery and exact-version binding. Broader subsystem operations remain to be added as their consumers are migrated in Step 3; this resolution does not claim that all Spring Data native calls have already been wrapped.

- **Reference driver:** `spring-data-gemfire-driver-10.4`, provider `com.vmware.gemfire.driver.gemfire104.GemFire104Driver`. Per the user's instruction, `10.4.+` selects a version at build time; the build then pins `gemfire-core` and `gemfire-version`, the initial driver artifact version, its generated stamp and the native test expectation to the concrete result. This run selected `10.4.0-build.3056.df98c835`.
- **Native API correction:** GemFire 10.4 exposes product version through `com.vmware.gemfire.internal.version.GemFireVersion` in `gemfire-version`; the older class named in the spec is absent. Updated the spec inline. Cache implementations implement both peer/client interfaces, so the driver retains the configured mode rather than relying on `instanceof` alone.
- **SPI surface:** immutable `CacheConfiguration` with redacted diagnostics and defensive property copies; `DriverConnection` now provides lifecycle, lookup and peer/client region creation; `DriverRegion` provides name/get/put/remove/containsKey/size. Added methods default to `UnsupportedFeatureException` with reason `NOT_INTRODUCED`.
- **Mock:** `spring-data-gemfire-driver-mock`, independently owned state per connection, `LOCAL` peer/client regions, explicit typed rejection of unsupported shortcuts. It does not simulate distribution, persistence, eviction, servers or pools. Generated native identity `mock` is a synthetic test identity, not a native compatibility claim. Compile and test runtime classpaths are checked for native dependencies/classes.
- **Generated metadata:** `gradle/driver-stamp.gradle.kts` reads the resolved SPI jar's version resource, uses resolution consistent with the driver's compile classpath, records the selected native dependency, and emits both the provider-keyed properties stamp and `ServiceLoader` registration. Neither is hand-maintained. Packaged jars were inspected to verify the provider and stamp.
- **Contract kit:** `spring-data-gemfire-driver-contract-tests`, a native-free test library shared by mock unit tests and reference-driver integration tests. It discovers every default method in the resolved SPI, requires a behavioral probe for each, and checks each driver's explicit unsupported-method declaration and reason. Negative tests verify missing coverage, wrong feature/reason/version and stale unsupported declarations fail.

### Validation

Successful command:

```sh
./gradlew :spring-data-gemfire-driver-10.4:build :spring-data-gemfire-driver-mock:build :spring-data-gemfire-driver-contract-tests:build :spring-data-gemfire-driver-spi:build :spring-data-gemfire-driver-core:build --console=plain
```

After final argument-validation changes, reran the three changed/dependent module builds successfully. Results: **57 tests passing**, none skipped — 11 SPI, 24 core, 6 contract-kit, 9 mock and 7 native integration tests. Native integration tests use local peer and client regions and close each cache; no external cluster is required. Mock classpath enforcement and `git diff --check` pass.

### Handoff

Use exactly one provider on the runtime classpath: the native driver or the mock. Construct/inject `GemFireDriverManager`, call its driver's `connect(new CacheConfiguration(mode, properties))`, and close the returned connection when its owner shuts down. The driver implementations' main dependencies remain independent of `driver-core`; only the shared test kit depends on core for binding tests.

Next is Step 3's cache/util/root migration, expanding the SPI and both implementations as required by those consumers. The remaining pool, query, function, transaction, PDX and WAN surfaces are not implemented by this first cache/region slice. API-compatibility tooling and the full migrated Spring Data per-driver integration matrix remain Step 7 work.
