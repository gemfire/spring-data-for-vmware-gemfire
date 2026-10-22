Type: task
Status: resolved
Assignee: Codex
Blocked by: 14, 16, 17, 18

# Transaction lifecycle and callbacks SPI


> Scope amendment (2026-10-22): implementation and test evidence below is historical. The [client-only decision](../wayfinder-impl/client-only-scope.md) supersedes peer/server requirements; [ticket 59](59-remove-server-only-spi-and-reconcile-client-scope.md) owns removal and coverage reconciliation. This ticket does not authorize further server implementation.

## Question

Provide transaction operations, identifiers and event callbacks required by Spring transaction integration.

Audit coverage: **G3, G4** in the [native/SPI gap audit](../wayfinder-impl/audits/native-spi-gap-audit.md). Apply the [shared completion requirements](../wayfinder-impl/spi-gap-closure-plan.md#shared-completion-requirements) and the signed-off [technical spec](../wayfinder/technical-spec.md).

## Required implementation

Prerequisites: [Required native API coverage matrix](14-required-native-api-coverage-matrix.md); [Driver exception semantics and translation](16-driver-exception-semantics-and-translation.md); [Complete region events, listeners and writers](17-complete-region-events-listeners-and-writers.md); [Complete region operations and metadata SPI](18-complete-region-operations-and-metadata-spi.md).

- Implement manager/status/ID contracts and required begin/commit/rollback/suspend/resume/timeout operations using injected connections.
- Adapt full transaction listener/writer callbacks and reachable entry/event payloads, including writer rejection and conflict/error classifications.
- Preserve native thread association and supported cache-mode behavior without manufacturing stronger transaction guarantees.
- Specify actual mock transaction support or typed unsupported behavior; existing stored-only writer settings must not be mistaken for implemented registration.

- Exception foundation follow-up: Preserve transaction veto/conflict/incomplete/general distinctions and checked transaction-writer failures through actual transaction callbacks. See [exception semantics and ownership](../wayfinder-impl/investigations/exception-semantics-and-translation.md).

- Callback foundation follow-up: Reuse DriverTransactionId equality/member identity from entry events. Native transaction identity is tested through real commit callbacks; externalization and runtime transaction operations remain here. See [callback evidence](../wayfinder-impl/investigations/region-callback-contracts.md).

## Required validation

- Verify native commit/rollback, conflict/failure, suspension/resumption and listener/writer callbacks.
- Test supported mock behavior and explicit rejection of unsupported modes/operations; preserve Spring-visible exception distinctions.

## Completion

Complete transaction SPI rows ready for Spring transaction manager/configuration adoption. Record the Answer with changed source/API rows, commands/results, native version and limitations; update the implementation map. Do not mark dependent work complete from this ticket's result.

## Answer

Implemented the complete native transaction manager surface, typed transaction listener/writer/event contracts, checked writer vetoes and transaction-ID externalization through the injected connection. Native entry and transaction events share ID equality/member semantics. Callback registration/identity/lifecycle, cross-thread suspension/resumption and real peer/client commit/rollback behavior delegate to GemFire. All 23 mock manager operations fail explicitly as unsupported; the mock does not claim transaction support.

[Implementation evidence](../wayfinder-impl/investigations/transaction-lifecycle-and-callbacks.md) records contracts, exact behavior, all commands/results, the 10.4.0-build.3056.df98c835 artifact and limitations. There are 24 added default methods with shared probes and four added callback declarations. The 66 original rows plus three inherited cache getters have partial mappings retaining Spring consumer and reachable subsystem obligations. No frozen requirement was removed.

Validation: **513 tests passed**, zero failures/errors/skips; full Spring Data main/unit/integration and Spring Test main/unit compilation; normal migration boundary; 18 boundary fixtures; coverage regeneration/check and independent javap validation. Actual conflict and veto behavior is exercised through commits. Incomplete/synchronization failures use injected native failures; multi-peer distributed recovery is not claimed. Timed resume is not transaction lifetime configuration.

Tickets 34 and 37 explicitly retain Spring orchestration/timeout and native-free test acceptance, including additional mock behavior if preserved unit tests require it. This resolves the transaction SPI foundation, not the Spring consumer cutover or the overall native-free goal. Changes remain uncommitted.
