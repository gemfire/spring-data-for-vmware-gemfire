Type: task
Status: resolved
Blocked by: 60, 61, 62, 63

# Audit partition membership and subscription client scope

## Fixed scope

This child of [task 64](64-complete-client-scope-reconciliation.md) owns **158 API IDs across 16 native types**, frozen on 2026-10-28 in [the assignment ledger](../wayfinder-impl/coverage/client-scope-audit-assignments.tsv), selected by `audit_owner = wayfinder-issues/75-audit-partition-membership-and-subscription-client-scope.md`. Read the [audit work rules](../wayfinder-impl/investigations/client-scope-audit-plan.md#work-rules). This is scope classification and prerequisite cleanup, not completion of the downstream SPI implementation.

## Review batches

Partition attributes/factory/resolver/listener/member information; membership/loss/resumption values; subscription attributes/interest policy; entry operation and string-prefix resolver.

Trace client routing/resolver and local subscription use before excluding shared values. Peer membership or partition hosting alone is excluded; a type name is insufficient evidence.

## Completion criteria

- [x] Every assigned ID has an evidence-backed required-client or excluded-server decision; no assigned ID remains unresolved. Use exact IDs, preserve native declarations and distinguish scope from implementation status.
- [x] Required rows have an active implementation and consumer owner with concrete retained behavior. If an earlier foundation is resolved, place outstanding implementation in an active downstream ticket; do not leave it assigned only to a closed foundation.
- [x] Excluded rows have no stale public implementation mapping: remove an existing server surface, prove a retained client equivalent, or record private native integration infrastructure. Perform necessary retirement here before task 59 closes.
- [x] Reconcile linked source/resource and existing-SPI dispositions, including inherited members. Coordinate cross-family payloads without taking over another child's fixed IDs.
- [x] Evidence ledger lists each batch, native source path/member at the pinned commit, disposition, ownership and any unanswered behavior. A runtime probe requires a specific unresolved question and a result that could change the decision; stop probing once settled.
- [x] Deterministic coverage generation/check, scope-policy tests, declaration/ownership validation and assignment accounting pass. For Java/build changes, obtain user-run affected Gradle checks and boundary validation; documentation-only classification needs no new runtime suite.

Close with assigned/resolved/remaining counts and downstream handoff. Do not wait for full SPI/native/mock implementation or every-version compatibility tests; those remain existing implementation acceptance. New discoveries go to task 64 for explicit assignment, rather than silently enlarging this fixed scope. Do not block this task on 59 or 64: that would create a cycle.

## Task 75 completion — 2026-10-28

All **158 partition/membership/subscription IDs** are classified: **46 required-client / 112 excluded-server / zero unresolved**. Active implementation/adoption owners remain 48/30. Client request routing requires PartitionResolver, EntryOperation and StringPrefixPartitionResolver; the blanket exclusions in tasks 19/48 are corrected. Partition hosting attributes/listeners, rebalance metadata and peer role-reliability values remain excluded, with no current public implementation or linked production/SPI mapping to retire. Client subscription value defaults and client factory defaults are distinguished. See the exact member ledger and pinned source evidence in `wayfinder-impl/investigations/client-partition-membership-and-subscription-scope.md`.

Deterministic generation/check, all 10 policy tests, independent javap validation (372 declarations / 21 types) and exact assignment accounting pass. All 3,851 frozen API IDs, implementation statuses, ownership and mappings are unchanged; the 2,723-entry assignment ledger is byte-for-byte unchanged. Exactly 158 rows change scope/remaining-work fields. Assigned scope is **2,333 classified / 390 unresolved** (1,854 required-client / 479 excluded-server). Whole inventory is **2,353 required-client / 1,108 excluded-server / 390 unresolved**. Source scopes remain 473 required-client / three excluded-server / 71 unresolved; all 149 existing SPI references remain required-client. The 77-task dependency graph remains acyclic: 37 open / 38 resolved / two superseded. No Java/build behavior changed, no new runtime probe was needed and no Gradle command was run.

**One classification task remains: 76 (390 shared-exception IDs).** Task 77 retains fixture cleanup and tasks 59/64 final reconciliation. Required SPI/native/mock implementation remains downstream work.

[Evidence and exact IDs](../wayfinder-impl/investigations/client-partition-membership-and-subscription-scope.md).
