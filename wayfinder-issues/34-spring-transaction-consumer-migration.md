Type: task
Status: open
Blocked by: 28, 29

# Spring transaction consumer migration

## Question

Retarget Spring transaction management and application events to the transaction SPI.

Audit coverage: **G3, G4, G5, G6** in the [native/SPI gap audit](../wayfinder-impl/audits/native-spi-gap-audit.md). Apply the [shared completion requirements](../wayfinder-impl/spi-gap-closure-plan.md#shared-completion-requirements) and the signed-off [technical spec](../wayfinder/technical-spec.md).

## Required implementation

Prerequisites: [Transaction lifecycle and callbacks SPI](28-transaction-lifecycle-and-callbacks-spi.md); [Complete cache configuration and creation assembly](29-complete-cache-configuration-and-creation-assembly.md).

- Migrate GemfireTransactionManager, holders/resource keys, transaction events, listeners and writer composition to SPI types.
- Retarget @EnableGemfireCacheTransactions and XML/configurer wiring to injected connection/transaction collaborators.
- Preserve transaction synchronization, thread association, suspend/resume and existing Spring error classifications.
- Remove direct native exception checks and native event types from all affected public APIs and tests.

## Required validation

- Run Spring @Transactional/programmatic transaction scenarios with commit/rollback, propagation/suspension, listener/writer failures and cleanup.
- Use native transaction tests for real semantics and explicit mock unsupported behavior where appropriate.

## Completion

Spring transaction consumer group and configuration operate through the driver contracts. Record the Answer with changed source/API rows, commands/results, native version and limitations; update the implementation map. Do not mark dependent work complete from this ticket's result.
