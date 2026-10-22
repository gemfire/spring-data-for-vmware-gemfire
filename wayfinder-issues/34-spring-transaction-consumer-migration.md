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

- Exception foundation follow-up: Preserve CannotCreateTransactionException, GemfireTransactionCommitException and NoTransactionException phase behavior while replacing native transaction catches and writer veto signatures. See [exception semantics and ownership](../wayfinder-impl/investigations/exception-semantics-and-translation.md).

- Transaction foundation follow-up: use `DriverConnection.getTransactionManager`, typed listener/writer/event contracts and the checked `DriverTransactionWriterException`. Timed `tryResume` waits for suspension; preserve Spring timeout validation separately. Native callbacks retain their engine lifecycle, including no automatic writer close on replacement. See [transaction evidence](../wayfinder-impl/investigations/transaction-lifecycle-and-callbacks.md).
- The official mock currently rejects every transaction operation explicitly. Preserve Spring orchestration unit coverage with native-free SPI collaborators, and real transactional storage behavior in provider integration suites. If a migrated unit test needs actual mock transactions, implement and test that support before acceptance; do not skip/delete the test or treat unsupported as transactional success.

## Required validation

- Run Spring @Transactional/programmatic transaction scenarios with commit/rollback, propagation/suspension, listener/writer failures and cleanup.
- Use native transaction tests for real semantics and explicit mock unsupported behavior where appropriate.

## Completion

Spring transaction consumer group and configuration operate through the driver contracts. Record the Answer with changed source/API rows, commands/results, native version and limitations; update the implementation map. Do not mark dependent work complete from this ticket's result.
