Type: task
Status: resolved
Blocked by: 60, 61, 62, 63

# Audit pool and durable lifecycle client scope

## Fixed scope

This child of [task 64](64-complete-client-scope-reconciliation.md) owns **101 API IDs across 4 native types**, frozen on 2026-10-28 in [the assignment ledger](../wayfinder-impl/coverage/client-scope-audit-assignments.tsv), selected by `audit_owner = wayfinder-issues/69-audit-pool-and-durable-client-scope.md`. Read the [audit work rules](../wayfinder-impl/investigations/client-scope-audit-plan.md#work-rules). This is scope classification and prerequisite cleanup, not completion of the downstream SPI implementation.

## Review batches

Pool configuration/accessors; PoolFactory settings; default client pool selection; PoolManager and durable readiness/runtime lifecycle.

Reuse existing pool/socket/durable evidence. Resolve ownership and close/keepalive semantics from native source; do not reopen validated transport callbacks without contradictory evidence.

## Completion criteria

- [x] Every assigned ID has an evidence-backed required-client or excluded-server decision; no assigned ID remains unresolved. Use exact IDs, preserve native declarations and distinguish scope from implementation status.
- [x] Required rows have an active implementation and consumer owner with concrete retained behavior. If an earlier foundation is resolved, place outstanding implementation in an active downstream ticket; do not leave it assigned only to a closed foundation.
- [x] Excluded rows have no stale public implementation mapping: remove an existing server surface, prove a retained client equivalent, or record private native integration infrastructure. Perform necessary retirement here before task 59 closes.
- [x] Reconcile linked source/resource and existing-SPI dispositions, including inherited members. Coordinate cross-family payloads without taking over another child's fixed IDs.
- [x] Evidence ledger lists each batch, native source path/member at the pinned commit, disposition, ownership and any unanswered behavior. A runtime probe requires a specific unresolved question and a result that could change the decision; stop probing once settled.
- [x] Deterministic coverage generation/check, scope-policy tests, declaration/ownership validation and assignment accounting pass. For Java/build changes, obtain user-run affected Gradle checks and boundary validation; documentation-only classification needs no new runtime suite.

Close with assigned/resolved/remaining counts and downstream handoff. Do not wait for full SPI/native/mock implementation or every-version compatibility tests; those remain existing implementation acceptance. New discoveries go to task 64 for explicit assignment, rather than silently enlarging this fixed scope. Do not block this task on 59 or 64: that would create a cycle.

## Answer — 2026-10-28

**101 assigned / 101 required-client / zero excluded-server / zero unresolved.** The [eight-batch evidence and exact-ID ledger](../wayfinder-impl/investigations/client-pool-and-durable-scope.md) settles pool settings/defaults/factory assembly, lifecycle, durable readiness, runtime topology, default selection and registry behavior from pinned native source and existing foundation evidence. No assigned API is server-only, so no surface retirement is required here.

The 84 partial rows previously owned only by resolved foundation 50 now belong to active completion task 20. Task 50's validated socket/configuration work remains resolved. Task 51 retains one default-selection row, task 52 retains 16 runtime/registry rows, and task 35 owns all consumer adoption. Concrete handoffs preserve omitted/explicit defaults, native selection behavior, attachment/keepAlive shutdown differences, durable count prerequisites/sentinels and injected ownership. All implementation statuses and SPI/native/mock/test mappings remain unchanged.

Deterministic generation/check, all 10 scope-policy tests and independent javap validation (372 declarations / 21 types) passed, including ownership/source/inventory checks and negative controls. All 3,851 IDs and 2,723 fixed assignments remain intact and descriptor-stable. Exactly these 101 rows changed scope and remaining-work descriptions, and exactly 84 implementation owners moved from 50 to 20; no other API fields changed. The exact-ID evidence ledger covers every assigned row. Source dispositions are 413 required-client / three excluded-server / 133 unresolved; existing SPI dispositions are 147 required-client / two unresolved. Dependency graph and diff whitespace checks pass. No new Java/build change, runtime probe or Gradle execution was needed.

Task 70's fixed 104-ID security/authentication audit is next. Tasks 59/64 remain open with 1,738 API IDs unresolved across remaining audit families. This closes scope classification, not downstream implementation or native-free acceptance.
