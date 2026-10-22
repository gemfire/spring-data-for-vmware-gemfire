Type: task
Status: resolved
Assignee: Codex
Blocked by: 14

# SPI ABI compatibility and stamp enforcement

## Question

Wire the specified SPI binary compatibility and stamp checks so independently built drivers remain safe across additive changes.

Audit coverage: **G7** in the [native/SPI gap audit](../wayfinder-impl/audits/native-spi-gap-audit.md). Apply the [shared completion requirements](../wayfinder-impl/spi-gap-closure-plan.md#shared-completion-requirements) and the signed-off [technical spec](../wayfinder/technical-spec.md).

## Required implementation

Prerequisites: [Required native API coverage matrix](14-required-native-api-coverage-matrix.md).

- Configure japicmp/Revapi or equivalent for released SPI baselines, with explicit first-release behavior when no baseline exists; do not compare an artifact only against itself.
- Check forbidden within-major removals/signature changes and new abstract methods on existing driver interfaces; cover callback/interface evolution as well as connection methods.
- Validate generated provider stamps against resolved compile dependencies and exact runtime native versions; verify the core/SPI compatibility requirement in spec §6.1 rather than assuming the provider stamp checks core.
- Wire the checks into normal verification/release CI and document baseline/version updates without silently inventing a prior release.

## Required validation

- Use compiled older-driver/baseline fixtures: compatible additive default works or throws typed unsupported; incompatible major/minor/signature/new-abstract changes fail predictably.
- Verify missing/malformed stamps, mismatched resolved versions and linkage-error wrapping; fresh test fixtures must not require modifying released artifacts.

## Completion

Executable ABI/stamp compatibility gates with honest pre-first-release status and documented baseline procedure. Record the Answer with changed source/API rows, commands/results, native version and limitations; update the implementation map. Do not mark dependent work complete from this ticket's result.

## Answer

Implemented the japicmp ABI gate, strict interface/callback evolution checks, immutable released-baseline policy and independent packaged-stamp verification. Core now carries and checks its own resolved SPI requirement before discovering providers; core/SPI artifact versions match. Provider construction and runtime-version linkage errors produce typed bind failures. The native selector now honors an explicit GemFire pin before resolving its exact dependencies, artifact version and stamp.

[Implementation evidence](../wayfinder-impl/investigations/spi-abi-compatibility-and-stamps.md) records changed sources, commands, native build **10.4.0-build.3056.df98c835**, initial selector findings and limitations. [Gate documentation](../build-tools/spi-compatibility/README.md) describes normal verification/publication wiring and the reviewed baseline/version-update procedure. No SPI Java declarations or coverage rows changed.

Validation: **32 compiled ABI/stamp fixtures and 520 JUnit tests passed**, zero failures/errors/skips; full Spring Data main/unit/integration and Spring Test main/unit compilation; normal migration boundary and all 18 boundary fixtures; coverage/javap/ownership checks. Dry runs confirm compatibility verification precedes publication, without publishing.

There is no released SPI baseline: the gate explicitly reports pre-first-release status and does not claim historical ABI compatibility. Behavioral compatibility, complete per-driver integration coverage, remaining SPI implementations and Spring's native-free cutover retain their existing owners. In particular, tickets 37–38, 40 and 41 remain open.
