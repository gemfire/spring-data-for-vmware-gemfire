Type: task
Status: resolved
Blocked by: 60, 61, 62, 63

# Audit query and CQ client scope

## Fixed scope

This child of [task 64](64-complete-client-scope-reconciliation.md) owns **261 API IDs across 26 native types**, frozen on 2026-10-28 in [the assignment ledger](../wayfinder-impl/coverage/client-scope-audit-assignments.tsv), selected by `audit_owner = wayfinder-issues/65-audit-query-and-cq-client-scope.md`. Read the [audit work rules](../wayfinder-impl/investigations/client-scope-audit-plan.md#work-rules). This is scope classification and prerequisite cleanup, not completion of the downstream SPI implementation.

## Review batches

Result collections/structs/type metadata; query routing and execution; client-local index operations; CQ lifecycle/listeners/events. Shared exception types belong to task 76.

Client-local versus remote index/query-service restrictions and all six inherited result-type serialization rows are settled in the final source-backed batch. Tasks 55/71 have the serialization handoff; task 71's fixed inventory is unchanged.

## Completion criteria

- [x] Every assigned ID has an evidence-backed required-client or excluded-server decision; no assigned ID remains unresolved. Use exact IDs, preserve native declarations and distinguish scope from implementation status.
- [x] Required rows have an active implementation and consumer owner with concrete retained behavior. If an earlier foundation is resolved, place outstanding implementation in an active downstream ticket; do not leave it assigned only to a closed foundation.
- [x] Excluded rows have no stale public implementation mapping: remove an existing server surface, prove a retained client equivalent, or record private native integration infrastructure. Perform necessary retirement here before task 59 closes.
- [x] Reconcile linked source/resource and existing-SPI dispositions, including inherited members. Coordinate cross-family payloads without taking over another child's fixed IDs.
- [x] Evidence ledger lists each batch, native source path/member at the pinned commit, disposition, ownership and any unanswered behavior. A runtime probe requires a specific unresolved question and a result that could change the decision; stop probing once settled.
- [x] Deterministic coverage generation/check, scope-policy tests, declaration/ownership validation and assignment accounting pass. For Java/build changes, obtain user-run affected Gradle checks and boundary validation; documentation-only classification needs no new runtime suite.

Close with assigned/resolved/remaining counts and downstream handoff. Do not wait for full SPI/native/mock implementation or every-version compatibility tests; those remain existing implementation acceptance. New discoveries go to task 64 for explicit assignment, rather than silently enlarging this fixed scope. Do not block this task on 59 or 64: that would create a cycle.

## First batch — 2026-10-28

The [source-backed result ledger](../wayfinder-impl/investigations/client-query-result-scope.md) classifies 84 of the 261 assigned IDs as required-client, leaving **177 unresolved**. No new runtime probe or implementation change. Six inherited metadata serialization IDs remain separate source review; then finish query routing/execution/indexes and CQ lifecycle/events. Existing implementation/consumer tasks 21/22/31/33 carry explicit acceptance.

## Answer — 2026-10-28

**261 assigned / 261 classified / zero remaining:** 259 required-client and two excluded-server. The [final evidence ledger](../wayfinder-impl/investigations/client-query-and-cq-scope.md) settles the last 177 IDs from pinned native source: 175 required and the two server function-context query overloads excluded. The first-batch checkpoint above is historical. No existing public SPI mapping or Spring production call needs removal for those two exclusions; Query itself remains required.

Task 21 owns query, result, index and QueryService entry-point implementation; task 22 owns CQ behavior. Tasks 31/33 own Spring consumers, task 35 coordinates routing and task 55 supplies serialization collaborators. Task 71 retains its own fixed inventory. All implementation statuses remain unchanged; this closes scope classification, not native/mock implementation or compatibility acceptance.

Global scope is **758 required-client / 631 excluded-server / 2,462 unresolved**. Task 66 is next, with 137 fixed region/callback IDs. Parents 59/64 remain open.

Validation: deterministic generation/check, all 10 scope-policy tests and inventory/ownership validation passed. Independent javap matched 372 declarations across 21 types. Assignment checks preserve all 2,723 unique frozen assignments and receiver/member/descriptors; all 3,851 API IDs and non-scope fields are unchanged. The final batch changes exactly 177 scopes, with active implementation/consumer owners and no excluded existing-SPI mapping. No Java/build change, new runtime probe or Gradle command was needed.
