Type: task
Status: open
Blocked by: 14, 16, 17, 18

# Transaction lifecycle and callbacks SPI

## Question

Provide transaction operations, identifiers and event callbacks required by Spring transaction integration.

Audit coverage: **G3, G4** in the [native/SPI gap audit](../wayfinder-impl/audits/native-spi-gap-audit.md). Apply the [shared completion requirements](../wayfinder-impl/spi-gap-closure-plan.md#shared-completion-requirements) and the signed-off [technical spec](../wayfinder/technical-spec.md).

## Required implementation

Prerequisites: [Required native API coverage matrix](14-required-native-api-coverage-matrix.md); [Driver exception semantics and translation](16-driver-exception-semantics-and-translation.md); [Complete region events, listeners and writers](17-complete-region-events-listeners-and-writers.md); [Complete region operations and metadata SPI](18-complete-region-operations-and-metadata-spi.md).

- Implement manager/status/ID contracts and required begin/commit/rollback/suspend/resume/timeout operations using injected connections.
- Adapt full transaction listener/writer callbacks and reachable entry/event payloads, including writer rejection and conflict/error classifications.
- Preserve native thread association and supported cache-mode behavior without manufacturing stronger transaction guarantees.
- Specify actual mock transaction support or typed unsupported behavior; existing stored-only writer settings must not be mistaken for implemented registration.

## Required validation

- Verify native commit/rollback, conflict/failure, suspension/resumption and listener/writer callbacks.
- Test supported mock behavior and explicit rejection of unsupported modes/operations; preserve Spring-visible exception distinctions.

## Completion

Complete transaction SPI rows ready for Spring transaction manager/configuration adoption. Record the Answer with changed source/API rows, commands/results, native version and limitations; update the implementation map. Do not mark dependent work complete from this ticket's result.
