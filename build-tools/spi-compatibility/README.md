# SPI compatibility verification

This gate implements technical spec §§6.1–6.3. Run it with:

```sh
./gradlew verifyDriverCompatibility -DgemfireVersion=10.4.0-build.3056.df98c835
```

The example pins a development build for reproducibility; it does not establish a supported release. Without an override, the reference driver uses the `gemfireVersion` Gradle property (currently `10.4.+`) and pins the resulting concrete version. `-DgemfireVersion` takes precedence. Use `--offline` only after dependencies and the selected native release are cached.

## Verification and release entry points

| Task | What it checks |
| --- | --- |
| `testSpiCompatibility` | Freshly compiled ABI fixtures, including application callbacks, and packaged-stamp negative fixtures. |
| `verifySpiCompatibility` | Runs those fixtures, then compares the current SPI jar with the configured immutable released baseline using japicmp 0.23.1, or reports explicit first-release status. |
| `<core-or-provider>:verifyDriverStamp` | Opens the built jar and checks its stamp against the resolved compile SPI artifact/resource and exact native dependency versions. |
| `:spring-data-gemfire-driver-core:test` | Includes an older compiled provider loaded with fresh SPI/core jars, additive typed defaults, independent core/provider version checks, malformed/missing stamps, exact native mismatch and actual linkage errors. |
| `verifyDriverCompatibility` | Combines the ABI gate, core runtime tests and all core/provider packaged-stamp checks. |

Root `check` includes `verifyDriverCompatibility`. Each driver module's `check` includes the ABI gate; core and providers additionally check their own packaged stamps. All tasks whose names start with `publish`, and `artifactoryPublish`, depend on the aggregate compatibility gate, including publication tasks added later. This is the repository's verification/release integration; it does not create an external CI service or publish artifacts. The complete per-driver integration matrix remains ticket 40.

The SPI jar and core jar both use the SPI's `major.minor` artifact version. Core records its own resolved SPI requirement in `META-INF/gemfire-driver-core.properties`; this is checked before provider discovery. Provider stamps remain keyed by provider class. The native provider's stamp and dependencies use the exact resolved `gemfire-core`/`gemfire-version` version, including build metadata. The mock has the explicit `mock` native-version marker and no native dependency. Stamp verification reads the packaged artifact rather than trusting the generator's output directory.

## Before the first release

`baseline.properties` deliberately says `status=unreleased`, with empty coordinates, version and checksum. The gate accepts this state only while the current SPI is `1.0`, and prints:

```text
PRE-FIRST-RELEASE: no released SPI baseline; ABI comparison unavailable. Fixture gate runs separately.
```

This is not a released-ABI compatibility pass. No published SPI baseline currently exists; generated test jars are synthetic fixtures only. Do not replace this state with a copy of the current artifact, a locally rebuilt imitation of a release, or a fabricated coordinate. Self-comparison is rejected.

## Establishing and updating a baseline

1. For the first SPI release, review the public API and behavior, run normal verification and the driver integration suites, and record the explicit absence of a previous release. Publication requires the normal release authorization outside this gate.
2. Once that release is available from its immutable Maven repository, download its released SPI jar and calculate its SHA-256 (`shasum -a 256 <released-jar>`). Read its embedded `com/vmware/gemfire/driver/spi/spi-version.properties`.
3. In a reviewed change, set `status=released`, `coordinates=<group>:spring-data-gemfire-driver-spi:<major.minor>`, `spi.version=<major.minor>`, `sha256=<released-jar-sha256>` and `allowMajorChange=false`. The coordinate must be exact, without a snapshot, dynamic selector or range. Ensure the release repository is configured for dependency resolution. Project substitution is disabled for this configuration so the local SPI cannot masquerade as the baseline.
4. For subsequent work, keep the latest released baseline fixed. Change the SPI version in `spring-data-gemfire-driver-spi/build.gradle.kts` when adding API; core follows it. Run `verifyDriverCompatibility` and the required behavioral/integration suites. Commit the API/version and verification evidence together.
5. After each authorized release, advance the baseline to that actual released artifact and checksum. Never advance it to an unreviewed candidate merely to hide an incompatibility. The old dependency graph is resolved separately from the current compile classpath; missing comparison dependencies fail verification.

Within a major, the gate rejects binary/source incompatibilities, removals, changed signatures/throws clauses, new abstract methods on existing interfaces (including callbacks), and changed inlined constant values. Additive API changes require a minor bump. The stricter source checks intentionally reject changes which would break recompilation even when a JVM might still link old bytecode.

For an intentional major transition, review the support matrix and removal policy first, record the rationale in the release change, bump the SPI major, and explicitly set `allowMajorChange=true`. The gate reports the approved major transition and requires all drivers to be rebuilt; it makes no within-major compatibility claim for that transition. After release, advance the baseline and reset the flag to `false`.

ABI inspection cannot prove unchanged behavior, enum use by every consumer, or complete native API coverage. Existing-method semantics remain a review and behavior-test obligation. The coverage matrix, migration boundary checks and remaining Wayfinder tickets continue to govern those requirements.
