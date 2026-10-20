Type: task
Status: open
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
