Type: task
Status: open
Blocked by: 38, 39

# Complete driver contract and native integration matrix

## Question

Validate the complete migrated surface and real driver behavior rather than only the current local-region subset.

Audit coverage: **G3, G4, G7** in the [native/SPI gap audit](../wayfinder-impl/audits/native-spi-gap-audit.md). Apply the [shared completion requirements](../wayfinder-impl/spi-gap-closure-plan.md#shared-completion-requirements) and the signed-off [technical spec](../wayfinder/technical-spec.md).

## Required implementation

Prerequisites: [Whole-module native dependency removal](38-whole-module-native-dependency-removal.md); [SPI ABI compatibility and stamp enforcement](39-spi-abi-compatibility-and-stamp-enforcement.md).

- Connect the required API coverage matrix to behavioral probes/tests for every supported method, application callback, payload/configuration mapping and expected unsupported feature.
- Run the complete native-free Spring integration suite with each selected native driver runtime; isolate version classpaths and match each driver's exact artifact/stamp.
- Add native client/server fixtures for pools, subscriptions/durability/authentication, queries/CQ, distributed functions, transactions and persistence as required by supported rows.
- Keep mock local semantics honest and provider-specific unsupported declarations precise. No missing API is equivalent to an expected unsupported implementation, and no broad exclusion may replace parity evidence.

## Required validation

- Run all driver/core/mock/contract and complete Spring suites on the selected matrix, with repeatable service setup/teardown and exact version evidence.
- Demonstrate coverage checks catch an unrepresented required callback or an untested new interface, not merely an unprobed existing default method.

## Completion

Every required matrix row has implementation and executable supported/unsupported evidence; unresolved failures remain open blockers. Record the Answer with changed source/API rows, commands/results, native version and limitations; update the implementation map. Do not mark dependent work complete from this ticket's result.
