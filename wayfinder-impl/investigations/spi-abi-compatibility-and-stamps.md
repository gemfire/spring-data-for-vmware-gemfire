# SPI ABI compatibility and stamps

Evidence for [ticket 39](../../wayfinder-issues/39-spi-abi-compatibility-and-stamp-enforcement.md), implementing technical spec §§6.1–6.3.

## Implementation

`build-tools/spi-compatibility/` contains the japicmp 0.23.1 gate, independent packaged-stamp verifier, compiled fixtures, Gradle integration and [baseline procedure](../../build-tools/spi-compatibility/README.md). The baseline policy explicitly records that no SPI has been released. Verification reports pre-first-release status only for SPI 1.0; it does not compare the current artifact with itself or represent fixtures as a historical release.

Once a release exists, its exact Maven coordinate, SHA-256 and embedded version identify the baseline. Dependency substitution is disabled and old/current classpaths are separate. Within a major the gate rejects binary/source incompatibilities, changed signatures/throws clauses, removals, new abstract methods on existing extension interfaces, and changed constant values. Additions require a minor bump. Major transitions require explicit reviewed policy approval and make no within-major compatibility claim.

`gradle/driver-stamp.gradle.kts` now validates the resolved SPI artifact version against its embedded version, validates exact agreement between resolved native core/version artifacts, and verifies the packaged jar independently. Core receives its own generated stamp; core and SPI artifact versions are both 1.0. `CoreStamp` is an immutable value loaded before provider discovery. `GemFireDriverManager` checks core's requirement independently of the provider and also wraps linkage errors encountered while reading the runtime native version. Malformed properties escapes in provider stamps now produce `DriverBindException`.

The reference driver's version selector now honors `-DgemfireVersion` before the Gradle property/default. Investigation found that its previous hard-coded `10.4.+` ignored the requested pin: an initial online verification selected 10.4.0-build.3060.4b2a69de. After correcting the selector, the complete regression run and packaged-stamp checks used **10.4.0-build.3056.df98c835** for both `gemfire-core` and `gemfire-version`. The driver artifact version and generated stamp match this exact build. Existing user changes to `gradle.properties` and the Gradle 9.8.1 wrapper are preserved.

Root `check` and all publication/artifactory tasks depend on `verifyDriverCompatibility`. Driver module checks run ABI verification; core/provider checks also inspect their packaged stamps. The aggregate includes core's executable old-provider fixtures. Task-graph dry runs confirm the gate precedes verification/publication; no publication was performed.

## Validation

- **32 ABI/stamp fixtures passed.** Fresh javac outputs cover additive defaults and an older compiled callback executing a new default, abstract connection/callback additions, signature/removal/throws changes, enum removal, protected-member removal, finality/access changes, inherited callback changes, constant changes, major approval, version regressions, missing minor bumps, self-comparison, checksum/version mismatch, first-release policy, valid stamps and mismatched/missing stamp data.
- **520 JUnit tests passed, zero failures/errors/skips:** SPI 24, core 31, contract kit 6, mock 58, native 95, Spring mock 153, Spring native 153. Up-to-date suites retain their successful XML results. Core adds seven tests with multiple compiled scenarios: older provider/new typed default; independent core and provider major/newer-minor rejection before construction; malformed/missing core/provider stamps; exact native patch mismatch; actual `NoSuchMethodError` during construction and runtime-version lookup. Fixtures use isolated class loaders and freshly assembled jars; released/cached artifacts are never edited.
- Full Spring Data main/unit/integration and Spring Test main/unit sources compile. The full command succeeded in 1m47s (`/private/tmp/sdgf-abi-full.log`). Final aggregate/core/migration verification after making `CoreStamp` an immutable record succeeded in 18s (`/private/tmp/sdgf-abi-final.log`).
- All **18 migration-boundary fixtures** pass and normal migration verification passes. Native counts remain 182 main / 122 unit / 128 integration files; strict native-free compliance is not claimed.
- Coverage generation check, independent javap comparison (148 declarations across 13 types), ownership/source/ID validation and both negative controls pass. No SPI Java contract or frozen native requirement changed: 3,851 rows across 292 types remain **2,921 missing / 650 partial / 280 implemented**, with 131 SPI defaults and 34 callback declarations. This ticket changes enforcement and artifact/core binding, not member coverage mappings.

```sh
./gradlew :spring-data-gemfire-driver-spi:check :spring-data-gemfire-driver-core:check :spring-data-gemfire-driver-contract-tests:check :spring-data-gemfire-driver-mock:check :spring-data-gemfire-driver-10.4:check :spring-data-vmware-gemfire:spiCacheTest :spring-data-vmware-gemfire:spiCacheIntegrationTest :spring-data-vmware-gemfire:compileJava :spring-data-vmware-gemfire:compileTestJava :spring-data-vmware-gemfire:compileIntegrationTestJava :spring-test-vmware-gemfire:compileJava :spring-test-vmware-gemfire:compileTestJava :verifyDriverCompatibility :verifyMigrationBoundary -DgemfireVersion=10.4.0-build.3056.df98c835 --offline --console=plain
./gradlew verifyDriverCompatibility verifyMigrationBoundary -DgemfireVersion=10.4.0-build.3056.df98c835 --offline --console=plain
./gradlew check :spring-data-vmware-gemfire:publishToMavenLocal --dry-run -DgemfireVersion=10.4.0-build.3056.df98c835 --offline --console=plain
./gradlew :spring-data-vmware-gemfire:publishToMavenLocal --dry-run -DgemfireVersion=10.4.0-build.3056.df98c835 --offline --console=plain
python3 wayfinder-impl/coverage/build_api_coverage.py --check
python3 wayfinder-impl/coverage/validate_coverage.py --javap
git -c core.whitespace=-blank-at-eol diff --check
```

## Limits and downstream ownership

There is **no released baseline yet**. Released ABI comparison cannot be claimed until an actual immutable release is configured; the fixture suite validates the comparator's positive/negative behavior now. ABI analysis cannot prove unchanged semantics or every consumer's enum usage. Those remain behavior-test/review obligations. Major removals still require the support-matrix review and all drivers to be rebuilt.

Ticket 40 retains the complete contract/native integration matrix; tickets 37–38 retain preserved native-free Spring test coverage and dependency removal; ticket 41 retains final conformance. No new native feature, complete mock implementation, supported-version matrix or overall native-free result follows from this gate. No artifacts were published.
