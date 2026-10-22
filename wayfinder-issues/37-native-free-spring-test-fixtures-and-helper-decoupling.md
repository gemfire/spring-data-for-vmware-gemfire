Type: task
Status: open
Blocked by: 36

# Native-free Spring tests and helper decoupling

## Question

Complete migration of Spring Data unit/integration test sources and remove dependence on native Spring Test helpers.

Audit coverage: **G2, G7, G8** in the [native/SPI gap audit](../wayfinder-impl/audits/native-spi-gap-audit.md). Apply the [shared completion requirements](../wayfinder-impl/spi-gap-closure-plan.md#shared-completion-requirements) and the signed-off [technical spec](../wayfinder/technical-spec.md).

## Required implementation

Prerequisites: [Residual native utilities and support cleanup](36-residual-native-utilities-and-support-cleanup.md).

- Migrate remaining native Mockito fixtures, native cache/region factories, callback/event fixtures and global reset hooks to SPI plus the official mock.
- Move implementation-specific native characterization to driver modules; keep Spring integration source APIs native-free and switch only runtime providers.
- Remove or isolate imports/usages of native Spring Test helper APIs in Spring Data tests. Preserve tests of previously exposed behavior instead of deleting failing cases.
- Verify Spring Test still compiles under its allowed native dependency and resolve any initializer/source compatibility work identified at factory cutover without making native migration of that module a new requirement.

- Transaction fixture follow-up: ticket 28 exposes the full manager/callback SPI but the official mock explicitly rejects transaction operations. Retain all Spring transaction orchestration assertions using SPI fixtures; retain commit/rollback/conflict storage assertions in native provider integration. Add any local mock transaction behavior required to run the preserved unit suite, with its own behavioral tests. Unsupported responses cannot substitute for successful transaction assertions or justify exclusions. See [transaction evidence](../wayfinder-impl/investigations/transaction-lifecycle-and-callbacks.md).

## Required validation

- Run the complete Spring unit suite with the official mock and no native jars, then shared integration source compilation.
- Audit test resources and public fixture types; no native test helper leakage, reset hooks or blanket test exclusions may hide remaining requirements.

## Completion

All Spring Data test source/fixture rows migrated; native-specific cases live in driver suites with preserved coverage. Record the Answer with changed source/API rows, commands/results, native version and limitations; update the implementation map. Do not mark dependent work complete from this ticket's result.
