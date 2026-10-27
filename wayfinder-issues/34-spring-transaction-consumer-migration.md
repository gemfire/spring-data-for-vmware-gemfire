Type: task
Status: open
Blocked by: 28, 29, 59

# Spring transaction consumer migration

## Client-only scope — 2026-10-22

Apply the [approved client-only boundary](../wayfinder-impl/client-only-scope.md). [Cleanup ticket 59](59-remove-server-only-spi-and-reconcile-client-scope.md) must complete first. Full/complete coverage in this ticket means every retained client requirement; historical native reachability alone does not require peer/server APIs. Preserve all retained client tests and native-free boundaries.

## Question

Retarget Spring transaction management and application events to the transaction SPI.

Audit coverage: **G3, G4, G5, G6** in the [native/SPI gap audit](../wayfinder-impl/audits/native-spi-gap-audit.md). Apply the [shared completion requirements](../wayfinder-impl/spi-gap-closure-plan.md#shared-completion-requirements) and the signed-off [technical spec](../wayfinder/technical-spec.md).

## Required implementation

Prerequisites: [Transaction lifecycle and callbacks SPI](28-transaction-lifecycle-and-callbacks-spi.md); [Complete cache configuration and creation assembly](29-complete-cache-configuration-and-creation-assembly.md).

- Migrate GemfireTransactionManager, holders/resource keys, client-supported transaction events and callbacks to SPI types. Server-only writer composition is removed under ticket 59.
- Retarget @EnableGemfireCacheTransactions and XML/configurer wiring to injected connection/transaction collaborators.
- Preserve transaction synchronization, thread association, suspend/resume and existing Spring error classifications.
- Remove direct native exception checks and native event types from all affected public APIs and tests.

- Exception foundation follow-up: Preserve CannotCreateTransactionException, GemfireTransactionCommitException and NoTransactionException phase behavior while replacing native transaction catches and retained client callback signatures. See [exception semantics and ownership](../wayfinder-impl/investigations/exception-semantics-and-translation.md).

- Transaction foundation follow-up: use `DriverConnection.getTransactionManager`, the client-supported callback/event contracts retained by ticket 59. Peer-only writer/veto contracts must not be required for client acceptance. Timed `tryResume` waits for suspension; preserve Spring timeout validation separately. Retained native callbacks preserve their verified client lifecycle. See [transaction evidence](../wayfinder-impl/investigations/transaction-lifecycle-and-callbacks.md).
- The official mock currently rejects every transaction operation explicitly. Preserve Spring orchestration unit coverage with native-free SPI collaborators, and real transactional storage behavior in provider integration suites. If a migrated unit test needs actual mock transactions, implement and test that support before acceptance; do not skip/delete the test or treat unsupported as transactional success.
- Task 64 resolved all 11 transaction listener/event scope rows as required-client after 16 passing native tests. Preserve local commit/rollback/conflict outcomes, empty-transaction callbacks, transaction identity/cache/entry payloads and listener failure/cleanup behavior. Server-backed client transactions remain notification-silent. This ticket owns the remaining partial implementation and consumer rows, with task 37 owning native-free test/mock support; see the [validated scope evidence](../wayfinder-impl/investigations/client-local-transaction-outcomes.md).

## Required validation

- Run Spring @Transactional/programmatic transaction scenarios with commit/rollback, propagation/suspension, retained client callback failures and cleanup.
- Use native transaction tests for real semantics and explicit mock unsupported behavior where appropriate.

## Completion

Spring transaction consumer group and configuration operate through the driver contracts. Record the Answer with changed source/API rows, commands/results, native version and limitations; update the implementation map. Do not mark dependent work complete from this ticket's result.
