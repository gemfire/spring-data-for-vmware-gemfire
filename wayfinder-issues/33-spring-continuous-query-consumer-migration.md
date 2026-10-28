Type: task
Status: open
Blocked by: 22, 59

# Spring continuous-query consumer migration

## Client-only scope — 2026-10-22

Apply the [approved client-only boundary](../wayfinder-impl/client-only-scope.md). [Cleanup ticket 59](59-remove-server-only-spi-and-reconcile-client-scope.md) must complete first. Full/complete coverage in this ticket means every retained client requirement; historical native reachability alone does not require peer/server APIs. Preserve all retained client tests and native-free boundaries.

## Question

Retarget Spring CQ containers, definitions, adapters and configuration to the CQ SPI.

Audit coverage: **G3, G4, G5, G6** in the [native/SPI gap audit](../wayfinder-impl/audits/native-spi-gap-audit.md). Apply the [shared completion requirements](../wayfinder-impl/spi-gap-closure-plan.md#shared-completion-requirements) and the signed-off [technical spec](../wayfinder/technical-spec.md).

## Required implementation

Prerequisites: [Continuous query SPI](22-continuous-query-spi.md).

- Migrate ContinuousQueryListenerContainer, definitions, listener/adapter interfaces and annotation/XML configuration to typed CQ events/services.
- Use injected pool/query collaborators and remove native PoolResolver/adapter dependencies and bean-error fallback behavior.
- Preserve executor/startup/shutdown ownership and listener lifecycle, with startup rejection for unsupported configured CQ behavior.
- Update schema expected/exported types and event-driven tests, keeping actual network delivery tests in native integration coverage.

- Exception foundation follow-up: Migrate CQ listener/container exception handling using typed query/CQ categories; preserve callback error delivery and suppression rules. See [exception semantics and ownership](../wayfinder-impl/investigations/exception-semantics-and-translation.md).

## Required validation

- Verify Spring registration, initial results, event conversion, configured listener methods and lifecycle against native client/server fixtures.
- Test local wiring/ownership failures using SPI collaborators and typed mock rejection without synthetic remote CQ success.

## Completion

CQ Spring consumers and their configuration/tests are native-free. Record the Answer with changed source/API rows, commands/results, native version and limitations; update the implementation map. Do not mark dependent work complete from this ticket's result.

## Query result scope handoff — 2026-10-28

Migrate CQ initial-result consumers and listener-container wiring to the typed result model with key/value structs and no native result/metadata values escaping through Object or collections. Use the [84-row source-backed ledger](../wayfinder-impl/investigations/client-query-result-scope.md); these rows are required-client, not implemented. Adapter/native/mock behavior tests remain implementation acceptance. Task 65 has now settled the six inherited metadata serialization rows and the remaining query/index/CQ scope; use the final handoff below. Do not export DataSerializable.


## Completed query/CQ audit handoff — 2026-10-28

Migrate ContinuousQueryListenerContainer query-service selection, attributes, named/unnamed durable registration, initial-results and listener dispatch/cleanup to tasks 21/22 equivalents. Preserve mutation/close identity, excluded events, callback status/errors and delta full-value recovery. Preserve programmatic versus declarative initialization distinctions and authenticated-view restrictions. No server CQ hosting or invented mock remote delivery. See the [final 177-ID disposition](../wayfinder-impl/investigations/client-query-and-cq-scope.md), which completes all 261 task-65 IDs.

## Shared exception handoff — task 76

Adopt typed client CQ exceptions from 22, retaining lifecycle distinctions, causes and safe messages; remove native translation only after behavior is preserved. See the [exact exception scope ledger](../wayfinder-impl/investigations/client-shared-exception-scope.md). Task 76 Java validation and generated inventory reconciliation passed on 2026-10-29. Existing implementation statuses are not upgraded.
