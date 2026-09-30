Type: task
Status: resolved

## Question

Step 1 of the migration plan (`wayfinder/technical-spec.md` §7.1): stand up `spring-data-gemfire-driver-spi` and `spring-data-gemfire-driver-core` as Gradle subprojects with `GemFireDriver`, `GemFireDriverManager`, `Capability`, the exception hierarchy, and the SPI-owned annotation config enums. No behavior yet — types and contracts only (except `GemFireDriverManager`'s resolve/validate logic, which the spec names explicitly as in-scope for this step).

Nothing to decide here beyond mechanical execution of what's already specified in `wayfinder/technical-spec.md` §5 — this is a `task` ticket, not a `grilling`/`prototype`/`research` one.

## Resolution

_Superseded in part by [wayfinder ticket 20](../wayfinder/issues/20-spi-abi-compatibility-and-driver-version-lockstep.md) (spec updated 2026-10-01): `Capability`/`CapabilityAware`, `GenerationRange`, `NativeVersionRange`, `SpiGenerationIncompatibleException` and the range-based manager below are to be replaced by `SpiVersion`, the driver stamp and the exact-match manager; the code was reworked on 2026-10-01: `driver-spi` now has `SpiVersion` (version read from a generated `spi-version.properties` inside the jar, `spiVersion = "1.0"` in the module build script), `UnsupportedFeatureException` (`NOT_INTRODUCED`/`REMOVED`), `SpiVersionIncompatibleException`, and `DriverMetadata` reduced to `driverName()`; `Capability`, `CapabilityAware`, `GenerationRange`, `NativeVersionRange`, `SpiGenerationIncompatibleException`, `UnsupportedCapabilityException` and `GemFireDriver.acceptsRuntimeGemFireVersion` are gone. `driver-core` has `DriverStamp`, `DriverCandidate` and the stamp-based, exact-match `GemFireDriverManager`. Build passes with 10 SPI and 24 core tests. Not yet built: the Gradle stamp-generation step for Driver Impls and the japicmp/Revapi gate (Steps 2 and 7). The paragraphs below describe the superseded first version and are kept as history._

_Revised after ticket 19: the manager was reworked from a static singleton into an injected instance; spec §5.1/§5.2 now reflect deviation 1 below._

Step 1 complete. `spring-data-gemfire-driver-spi` and `spring-data-gemfire-driver-core` build clean (`./gradlew :spring-data-gemfire-driver-spi:build :spring-data-gemfire-driver-core:build`); 8 `GemFireDriverManager` tests pass.

- `driver-spi`: `GemFireDriver`, `DriverMetadata`, `Capability`/`CapabilityAware`, `GenerationRange`, `NativeVersionRange`, the full §5.4 exception hierarchy, `GemFireDriverExceptionTranslator`, and SPI config enums `DataPolicy`, `RegionShortcut`, `ClientRegionShortcut`.
- `driver-core`: `GemFireDriverManager` (instance, eager bind in constructor per [wayfinder ticket 19](../wayfinder/issues/19-no-static-state-or-singletons.md), Axis A then Axis B, `NoDriverFoundException`/`AmbiguousDriverException` on 0/>1 drivers, diagnostic `spiRange()`/`effectiveRange()`), with unit tests.

Implementation-time deviations from `technical-spec.md` (small, handled inline per ticket 17; spec text not yet updated):

1. **`GemFireDriver.runtimeGemFireVersion()` added.** Spec §5.2 has `driver-core` call `GemFireVersion.getGemFireVersion()` (native), which conflicts with `driver-core` having zero native dependencies and with §6.3 (the Impl does the read). The Impl now supplies the string; `driver-core` passes it to `acceptsRuntimeGemFireVersion` and reports it on failure.
2. **`SPI_MIN = SPI_MAX = 1`.** The spec gives no concrete values; generation 1 matches the initial 5-entry `Capability` enum.
3. **Config enum inventory.** Only `RegionShortcut` and `ClientRegionShortcut` are native-typed annotation attributes in `spring-data-vmware-gemfire` (`DataPolicy` appears only in javadoc); constants mirror the GemFire 9.9.7 currently on the build. Step 5 must re-verify against the target versions.
