Type: task
Status: open
Blocked by: 36, 59

# Native-free Spring tests and helper decoupling

## Client-only scope — 2026-10-22

Apply the [approved client-only boundary](../wayfinder-impl/client-only-scope.md). [Cleanup ticket 59](59-remove-server-only-spi-and-reconcile-client-scope.md) must complete first. Full/complete coverage in this ticket means every retained client requirement; historical native reachability alone does not require peer/server APIs. Preserve all retained client tests and native-free boundaries.

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

Preserve the entire retained client unit suite against the official mock and native-free collaborators. Server-only test retirement requires the ticket 59 disposition ledger; native server/locator fixtures belong in native driver integration suites. Mock unsupported paths must not replace assertions of supported client behavior.

- Run the complete Spring unit suite with the official mock and no native jars, then shared integration source compilation.
- Audit test resources and public fixture types; no native test helper leakage, reset hooks or blanket test exclusions may hide remaining requirements.

## Completion

All Spring Data test source/fixture rows migrated; native-specific cases live in driver suites with preserved coverage. Record the Answer with changed source/API rows, commands/results, native version and limitations; update the implementation map. Do not mark dependent work complete from this ticket's result.

## Task 63 inherited-attributes test handoff

Task 63 removes the inherited production attributes factory and temporarily exposes six native metadata occurrences in the two CQ mock bean definitions, plus three occurrences in the new explicit-false entity disk-synchrony regression. Replace these with SPI/official-mock fixtures at consumer cutover; preserve both domain constraints and the end-to-end disk-synchrony assertion. These reviewed transitional baseline additions do not satisfy native-free unit coverage. See the [reviewed boundary adjustment](../wayfinder-impl/investigations/client-only-spring-surfaces.md#reviewed-transitional-test-boundary-adjustment).
