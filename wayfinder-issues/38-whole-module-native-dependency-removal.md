Type: task
Status: open
Blocked by: 15, 36, 37

# Whole-module native dependency removal

## Question

Remove native dependencies and make the complete module's strict SPI boundary an unconditional build invariant.

Audit coverage: **G1, G2, G5, G8** in the [native/SPI gap audit](../wayfinder-impl/audits/native-spi-gap-audit.md). Apply the [shared completion requirements](../wayfinder-impl/spi-gap-closure-plan.md#shared-completion-requirements) and the signed-off [technical spec](../wayfinder/technical-spec.md).

## Required implementation

Prerequisites: [Whole-module migration boundary enforcement](15-whole-module-migration-boundary-enforcement.md); [Residual native utilities and support cleanup](36-residual-native-utilities-and-support-cleanup.md); [Native-free Spring tests and helper decoupling](37-native-free-spring-test-fixtures-and-helper-decoupling.md).

- Remove the native bundle from main/test dependencies and decouple native helper dependencies; allow native artifacts only on intended native integration runtime configurations.
- Replace transitional allowlist/baseline compliance with strict whole-module source/API/resource and resolved-classpath checks on normal check/CI tasks.
- Include main/unit-test compile/runtime classpaths and integration source compilation; inspect jar contents as well as coordinates to catch shaded or indirect native leakage.
- Consolidate transitional source sets where no longer needed and ensure no exclusions/stubs/source deletion manufacture a green build.

## Required validation

- Cleanly compile every production/unit/integration source with native-free compile classpaths and run all unit tests on the mock.
- Produce dependency reports and public API/native-reference scans; demonstrate a deliberately introduced native dependency/reference fails normal verification.

## Completion

Strict zero-native boundary passes for the whole module without a remaining migration baseline or allowlist exemption. Record the Answer with changed source/API rows, commands/results, native version and limitations; update the implementation map. Do not mark dependent work complete from this ticket's result.
