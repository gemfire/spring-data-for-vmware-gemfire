Type: grilling
Status: resolved

## Question

What are the exact SPI module/package boundaries and build/artifact layout: how many modules, what are they named, what package(s) do they live under, and how do they depend on each other and on Spring Data GemFire?

## Answer

**Two modules, not one.** The SPI contracts (`GemFireDriver`, `DriverMetadata`, `Capability`/`CapabilityAware`, the SPI-owned exception hierarchy, SPI-owned enums like `DataPolicy`) live in a module separate from the runtime scaffolding that binds and validates Driver Impls (`GemFireDriverManager`'s `ServiceLoader` lookup, bind-time SPI-version-range and support-matrix-floor checks per [SPI versioning scheme](10-spi-versioning-scheme.md) and [Driver Impl support matrix and bind-time version check](11-driver-impl-support-matrix-and-bind-time-check.md)). This keeps what a Driver Impl author must compile against reduced to the pure contracts — they never need the manager/orchestration logic.

- **Modules and Maven coordinates** (group `com.vmware.gemfire`, alongside the existing `spring-data-vmware-gemfire` / `spring-test-vmware-gemfire`):
  - `spring-data-gemfire-driver-spi` — the pure SPI contracts. Zero dependency on the driver-core module.
  - `spring-data-gemfire-driver-core` — the manager/orchestration logic. Depends on `spring-data-gemfire-driver-spi`.
  - `spring-data-gemfire-driver-<gemfire-version>` per supported GemFire version (e.g. `spring-data-gemfire-driver-10.3`, `spring-data-gemfire-driver-10.4`) — each depends **only** on `spring-data-gemfire-driver-spi`, never on `driver-core`, never on each other, never on `spring-data-vmware-gemfire`.
- **Repo topology**: all of the above live in this same repo, as additional Gradle subprojects alongside `spring-data-vmware-gemfire` and `spring-test-vmware-gemfire`, at least for this initial migration. Decoupled release cadence doesn't require decoupled repos; keeping them together makes it easier to enforce "zero native GemFire outside Driver Impl modules" with one shared build-time check, and to keep the SPI and its impls in lockstep during the breaking migration. Splitting into separate repos remains an option later, once the SPI contract has stabilized — not a blocking decision now.
- **Package layout**: a new top-level package family, `com.vmware.gemfire.driver`, independent of `org.springframework.data.gemfire` — this signals the SPI is a distinct, independently-versioned artifact, not an internal package of Spring Data GemFire. Split by module: `com.vmware.gemfire.driver.spi` (contracts, exceptions, `Capability`) in `driver-spi`; `com.vmware.gemfire.driver.core` (the manager/orchestration) in `driver-core`. The module boundary (separate jars) is what actually keeps Driver Impls off `driver-core`'s classpath — the package split is for readability, not enforcement.
- **Dependency graph**: `driver-core` → `driver-spi`; each `driver-<version>` → `driver-spi` only; `spring-data-vmware-gemfire` → both `driver-spi` and `driver-core`.
