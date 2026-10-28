Type: task
Status: resolved
Blocked by: 60, 61, 62, 63

# Audit region operations and callback client scope

## Fixed scope

This child of [task 64](64-complete-client-scope-reconciliation.md) owns **137 API IDs across 9 native types**, frozen on 2026-10-28 in [the assignment ledger](../wayfinder-impl/coverage/client-scope-audit-assignments.tsv), selected by `audit_owner = wayfinder-issues/66-audit-region-and-callback-client-scope.md`. Read the [audit work rules](../wayfinder-impl/investigations/client-scope-audit-plan.md#work-rules). This is scope classification and prerequisite cleanup, not completion of the downstream SPI implementation.

## Review batches

Region collection/entry/statistics methods; interest registration; cache/declarable callback lifecycle; residual transaction entry points; EventSequenceID. Reuse the completed transaction and function callback evidence.

Only probe an operation if local-client versus server-backed behavior cannot be established from pinned source or existing tests. Do not repeat the completed transaction outcome probes.

## Completion criteria

- [x] Every assigned ID has an evidence-backed required-client or excluded-server decision; no assigned ID remains unresolved. Use exact IDs, preserve native declarations and distinguish scope from implementation status.
- [x] Required rows have an active implementation and consumer owner with concrete retained behavior. If an earlier foundation is resolved, place outstanding implementation in an active downstream ticket; do not leave it assigned only to a closed foundation.
- [x] Excluded rows have no stale public implementation mapping: remove an existing server surface, prove a retained client equivalent, or record private native integration infrastructure. Perform necessary retirement here before task 59 closes.
- [x] Reconcile linked source/resource and existing-SPI dispositions, including inherited members. Coordinate cross-family payloads without taking over another child's fixed IDs.
- [x] Evidence ledger lists each batch, native source path/member at the pinned commit, disposition, ownership and any unanswered behavior. A runtime probe requires a specific unresolved question and a result that could change the decision; stop probing once settled.
- [x] Deterministic coverage generation/check, scope-policy tests, declaration/ownership validation and assignment accounting pass. For Java/build changes, obtain user-run affected Gradle checks and boundary validation; documentation-only classification needs no new runtime suite.

Close with assigned/resolved/remaining counts and downstream handoff. Do not wait for full SPI/native/mock implementation or every-version compatibility tests; those remain existing implementation acceptance. New discoveries go to task 64 for explicit assignment, rather than silently enlarging this fixed scope. Do not block this task on 59 or 64: that would create a cycle.

## Answer — 2026-10-28

**137 assigned / 137 classified / zero remaining:** 129 required-client and eight excluded-server. The [source-backed evidence and exact member ledger](../wayfinder-impl/investigations/client-region-and-callback-scope.md) settle six batches without new runtime probes. Client-compatible attribute-copy subregion creation remains required because the native client factory calls that overload. EventSequenceID is excluded after tracing its server async/WAN payload path; no public implementation, Spring consumer or retained assertion needs retirement.

Eighteen partial rows move from resolved foundations to active tasks 30/31/34. Task 30 also takes the missing attribute-copy subregion equivalent; task 45 keeps all 24 interest implementations. Tasks 30/31/34 retain consumer acceptance with existing subsystem collaborators. Scope classification upgrades no implementation status and does not complete native-free migration or provider compatibility.

Global scope is **887 required-client / 639 excluded-server / 2,325 unresolved**. Task 67 is next with 332 fixed cache/region assembly IDs. Parents 59/64 remain open.

Deterministic coverage generation/check and all 10 scope-policy tests passed. Independent javap matched 372 public declarations across 21 native types; inventory, ownership, source mapping and negative controls passed (3,851 API rows, 133 production imports, 549 source/resource references, 509 Spring exposures). Assignment checks preserve all 2,723 unique frozen IDs and receiver/member/descriptors: 398 classified and 2,325 unresolved, each unresolved row still routed to its assigned child. Exactly 137 rows changed from unresolved; outside scope fields, only 19 implementation owners and nine remaining-work descriptions changed, as explicitly recorded above. Implementation statuses, native declarations and SPI/native/mock/test mappings are unchanged. Required unfinished rows and consumers have active owners; none of the eight exclusions has an existing SPI mapping. Source dispositions are 273 required-client / three excluded-server / 273 unresolved; existing SPI scope dispositions are 125 required-client / 24 unresolved. The 77-task dependency graph is acyclic. No new runtime probe, Java/build change or Gradle execution was needed.
