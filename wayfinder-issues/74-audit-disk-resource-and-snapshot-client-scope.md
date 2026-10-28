Type: task
Status: resolved
Blocked by: 60, 61, 62, 63

# Audit disk resource and snapshot client scope

## Fixed scope

This child of [task 64](64-complete-client-scope-reconciliation.md) owns **129 API IDs across 14 native types**, frozen on 2026-10-28 in [the assignment ledger](../wayfinder-impl/coverage/client-scope-audit-assignments.tsv), selected by `audit_owner = wayfinder-issues/74-audit-disk-resource-and-snapshot-client-scope.md`. Read the [audit work rules](../wayfinder-impl/investigations/client-scope-audit-plan.md#work-rules). This is scope classification and prerequisite cleanup, not completion of the downstream SPI implementation.

## Review batches

Disk-store factories/values/lifecycle; disk-write attributes; resource managers/rebalance/redundancy operations; cache/region snapshot services/options/filters.

Separate persistent client-local storage/snapshots from server bucket movement, redundancy and administration. Probe only a client-local persistence/snapshot path whose scope cannot be settled in source.

## Completion criteria

- [x] Every assigned ID has an evidence-backed required-client or excluded-server decision; no assigned ID remains unresolved. Use exact IDs, preserve native declarations and distinguish scope from implementation status.
- [x] Required rows have an active implementation and consumer owner with concrete retained behavior. If an earlier foundation is resolved, place outstanding implementation in an active downstream ticket; do not leave it assigned only to a closed foundation.
- [x] Excluded rows have no stale public implementation mapping: remove an existing server surface, prove a retained client equivalent, or record private native integration infrastructure. Perform necessary retirement here before task 59 closes.
- [x] Reconcile linked source/resource and existing-SPI dispositions, including inherited members. Coordinate cross-family payloads without taking over another child's fixed IDs.
- [x] Evidence ledger lists each batch, native source path/member at the pinned commit, disposition, ownership and any unanswered behavior. A runtime probe requires a specific unresolved question and a result that could change the decision; stop probing once settled.
- [x] Deterministic coverage generation/check, scope-policy tests, declaration/ownership validation and assignment accounting pass. For Java/build changes, obtain user-run affected Gradle checks and boundary validation; documentation-only classification needs no new runtime suite.

Close with assigned/resolved/remaining counts and downstream handoff. Do not wait for full SPI/native/mock implementation or every-version compatibility tests; those remain existing implementation acceptance. New discoveries go to task 64 for explicit assignment, rather than silently enlarging this fixed scope. Do not block this task on 59 or 64: that would create a cycle.

## Task 74 completion — 2026-10-28

All **129 disk/resource/snapshot IDs** are classified: **94 required-client / 35 excluded-server / zero unresolved**. Implementation/adoption remain with 27/30/31 (66 retained disk/resource IDs) and 44/31 (28 snapshot IDs), coordinating cache assembly with 29/35. The exclusions cover partition rebalance/redundancy and parallel snapshot options; none has a public implementation mapping or linked current production/SPI reference to retire. Source establishes legacy local stream snapshots, including destructive region reinitialization, and the boundary between local snapshot export and native remote export.

Deterministic generation/check, all 10 policy tests, independent declaration validation (372 declarations / 21 types) and exact assignment accounting pass. All 3,851 frozen IDs, implementation statuses and mappings remain unchanged. The 2,723 assignments remain unchanged: **2,175 classified / 548 unresolved**. Whole scope is **2,307 required-client / 996 excluded-server / 548 unresolved**; source scopes are 473 required-client / three excluded-server / 71 unresolved, and all 149 existing SPI references remain required-client. No Java/build behavior changed or Gradle command was run.

Two classification tasks remain: **75 (158 IDs)** and **76 (390 IDs)**. Task 77 retains fixture cleanup, and 59/64 final reconciliation. Next: task 75.

Evidence and exact IDs: [client disk/resource/snapshot ledger](../wayfinder-impl/investigations/client-disk-resource-and-snapshot-scope.md).
