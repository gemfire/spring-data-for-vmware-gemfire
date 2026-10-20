Type: task
Status: open
Blocked by: 22

# Spring continuous-query consumer migration

## Question

Retarget Spring CQ containers, definitions, adapters and configuration to the CQ SPI.

Audit coverage: **G3, G4, G5, G6** in the [native/SPI gap audit](../wayfinder-impl/audits/native-spi-gap-audit.md). Apply the [shared completion requirements](../wayfinder-impl/spi-gap-closure-plan.md#shared-completion-requirements) and the signed-off [technical spec](../wayfinder/technical-spec.md).

## Required implementation

Prerequisites: [Continuous query SPI](22-continuous-query-spi.md).

- Migrate ContinuousQueryListenerContainer, definitions, listener/adapter interfaces and annotation/XML configuration to typed CQ events/services.
- Use injected pool/query collaborators and remove native PoolResolver/adapter dependencies and bean-error fallback behavior.
- Preserve executor/startup/shutdown ownership and listener lifecycle, with startup rejection for unsupported configured CQ behavior.
- Update schema expected/exported types and event-driven tests, keeping actual network delivery tests in native integration coverage.

## Required validation

- Verify Spring registration, initial results, event conversion, configured listener methods and lifecycle against native client/server fixtures.
- Test local wiring/ownership failures using SPI collaborators and typed mock rejection without synthetic remote CQ success.

## Completion

CQ Spring consumers and their configuration/tests are native-free. Record the Answer with changed source/API rows, commands/results, native version and limitations; update the implementation map. Do not mark dependent work complete from this ticket's result.
