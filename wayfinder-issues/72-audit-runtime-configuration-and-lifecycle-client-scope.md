Type: task
Status: resolved
Blocked by: 60, 61, 62, 63

# Audit runtime configuration and lifecycle client scope

## Fixed scope

This child of [task 64](64-complete-client-scope-reconciliation.md) owns **291 API IDs across 6 native types**, frozen on 2026-10-28 in [the assignment ledger](../wayfinder-impl/coverage/client-scope-audit-assignments.tsv), selected by `audit_owner = wayfinder-issues/72-audit-runtime-configuration-and-lifecycle-client-scope.md`. Read the [audit work rules](../wayfinder-impl/investigations/client-scope-audit-plan.md#work-rules). This is scope classification and prerequisite cleanup, not completion of the downstream SPI implementation.

## Review batches

ConfigurationProperties (separate client, shared and peer-only property groups); DistributedSystem; CancelCriterion; DurableClientAttributes; Locator; management ClassName.

A shared configuration constant or class is not proof that a client consumes it. Trace property use and client connection/lifecycle paths; exclude locator hosting without excluding client locator discovery.

## Completion criteria

- [x] Every assigned ID has an evidence-backed required-client or excluded-server decision; no assigned ID remains unresolved. Use exact IDs, preserve native declarations and distinguish scope from implementation status.
- [x] Required rows have an active implementation and consumer owner with concrete retained behavior. If an earlier foundation is resolved, place outstanding implementation in an active downstream ticket; do not leave it assigned only to a closed foundation.
- [x] Excluded rows have no stale public implementation mapping: remove an existing server surface, prove a retained client equivalent, or record private native integration infrastructure. Perform necessary retirement here before task 59 closes.
- [x] Reconcile linked source/resource and existing-SPI dispositions, including inherited members. Coordinate cross-family payloads without taking over another child's fixed IDs.
- [x] Evidence ledger lists each batch, native source path/member at the pinned commit, disposition, ownership and any unanswered behavior. A runtime probe requires a specific unresolved question and a result that could change the decision; stop probing once settled.
- [x] Deterministic coverage generation/check, scope-policy tests, declaration/ownership validation and assignment accounting pass. For Java/build changes, obtain user-run affected Gradle checks and boundary validation; documentation-only classification needs no new runtime suite.

Close with assigned/resolved/remaining counts and downstream handoff. Do not wait for full SPI/native/mock implementation or every-version compatibility tests; those remain existing implementation acceptance. New discoveries go to task 64 for explicit assignment, rather than silently enlarging this fixed scope. Do not block this task on 59 or 64: that would create a cycle.

## Progress — 2026-10-28

[The exact-ID evidence ledger](../wayfinder-impl/investigations/client-runtime-configuration-and-lifecycle-scope.md) classifies all **291 IDs: 145 required-client / 146 excluded-server / zero unresolved**. All retained rows keep active implementation owner 23 and consumer owner 36; implementation status is unchanged. Client TLS includes legacy cluster/server settings used for outbound locator/server connections. Client monitoring retains member-timeout; availability selection retains redundancy-zone as its legacy alias. Peer deployment/distributed transactions remain excluded using the earlier source/native evidence.

Removed the public DistributedSystemUtils.getLocator accessor, also inherited by GemfireUtils. LocatorProcess owns its started native locator through a private shutdown callback. ClientOnlyConfigurationUnitTests guards the removed surface while retaining client utilities. Client XML is unchanged. Task 73 still owns InternalLocator IDs; this task removes the production caller without reassigning them.

## Validation and closure — 2026-10-28

The user reported that the requested Gradle command passed for GemFire `10.4.0-build.3056.df98c835`: focused `ClientOnlyConfigurationUnitTests` and `GemfireUtilsUnitTests`, Spring integration-test compilation, spring-test test compilation, baseline pruning and transitional boundary verification. Local JUnit XML confirms 15 tests with zero failures, errors or skips (2026-10-28T04:15:28–04:15:29Z). No Gradle command was run by the agent.

After compilation, the native-reference scan and deterministic coverage generation/check passed. All 10 scope-policy tests pass; independent javap matched 372 declarations across 21 types, with ownership/source/ID/scope checks and both negative controls passing. All 2,723 assignments remain unique and receiver/member/descriptor-stable, and unresolved rows retain their assigned owners. Current inventory: 3,851 API IDs, 131 production native imports, 547 source/resource references and 508 Spring signature exposures. Source scopes are 449 required-client / three excluded-server / 95 unresolved. Task 72 closes at **291 assigned / 291 classified / zero remaining**; implementation stays with tasks 23/36. This validates the cleanup and classification, not full native-free compliance.
