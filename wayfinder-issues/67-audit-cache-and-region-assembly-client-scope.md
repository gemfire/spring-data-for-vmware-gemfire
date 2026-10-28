Type: task
Status: resolved
Blocked by: 60, 61, 62, 63

# Audit cache and region assembly client scope

## Fixed scope

This child of [task 64](64-complete-client-scope-reconciliation.md) owns **332 API IDs across 20 native types**, frozen on 2026-10-28 in [the assignment ledger](../wayfinder-impl/coverage/client-scope-audit-assignments.tsv), selected by `audit_owner = wayfinder-issues/67-audit-cache-and-region-assembly-client-scope.md`. Read the [audit work rules](../wayfinder-impl/investigations/client-scope-audit-plan.md#work-rules). This is scope classification and prerequisite cleanup, not completion of the downstream SPI implementation.

## Review batches

Cache/GemFireCache/RegionService/ClientCache and factory entry points; residual AttributesFactory/AttributesMutator/RegionAttributes; legacy XML configuration values, including nested partition/eviction values and MirrorType.

Trace each shared configuration member to client factory/parser acceptance. Reachability from a legacy schema alone is not client support; retain client XML. Separate peer factory methods from shared client services.

## Completion criteria

- [x] Every assigned ID has an evidence-backed required-client or excluded-server decision; no assigned ID remains unresolved. Use exact IDs, preserve native declarations and distinguish scope from implementation status.
- [x] Required rows have an active implementation and consumer owner with concrete retained behavior. If an earlier foundation is resolved, place outstanding implementation in an active downstream ticket; do not leave it assigned only to a closed foundation.
- [x] Excluded rows have no stale public implementation mapping: remove an existing server surface, prove a retained client equivalent, or record private native integration infrastructure. Perform necessary retirement here before task 59 closes.
- [x] Reconcile linked source/resource and existing-SPI dispositions, including inherited members. Coordinate cross-family payloads without taking over another child's fixed IDs.
- [x] Evidence ledger lists each batch, native source path/member at the pinned commit, disposition, ownership and any unanswered behavior. A runtime probe requires a specific unresolved question and a result that could change the decision; stop probing once settled.
- [x] Deterministic coverage generation/check, scope-policy tests, declaration/ownership validation and assignment accounting pass. For Java/build changes, obtain user-run affected Gradle checks and boundary validation; documentation-only classification needs no new runtime suite.

Close with assigned/resolved/remaining counts and downstream handoff. Do not wait for full SPI/native/mock implementation or every-version compatibility tests; those remain existing implementation acceptance. New discoveries go to task 64 for explicit assignment, rather than silently enlarging this fixed scope. Do not block this task on 59 or 64: that would create a cycle.

## Task 66 scope handoff — 2026-10-28

Task 66 settles the single Region.createSubregion(String,RegionAttributes) ID as required-client and assigns equivalent typed attribute-copy creation to task 30 with configuration collaborators 19/29/47. ClientRegionFactoryImpl uses this overload; shortcut creation alone is insufficient evidence of complete equivalence. Use the [subregion evidence](../wayfinder-impl/investigations/client-region-and-callback-scope.md#client-attribute-subregions) when reviewing this task's attributes/factory members. Your fixed 332 IDs remain unchanged.

## Answer — 2026-10-28

**332 assigned / 332 classified / zero remaining: 275 required-client and 57 excluded-server.** The [exact member ledger](../wayfinder-impl/investigations/client-cache-and-region-assembly-scope.md) records pinned source, each decision and implementation/consumer ownership. Retain client cache services, XML, named attribute copies, local persistence, callbacks and client eviction configuration values. Exclude partition hosting, peer lock/search/rejoin/member discovery, server queue aliases, WAN creation, peer builders and the gfsh removal sentinel.

Task 30 takes 23 unfinished rows from completed foundation 46. Active 19/29/49 owners keep the other required work; consumers stay 30/35. No excluded ID has an existing SPI mapping. Scope completion does not complete those implementations. Next is task 68's fixed 154-ID expiry/eviction/compression audit; tasks 59/64 remain open.

Deterministic coverage generation/check and all 10 scope-policy tests passed. Independent javap matched 372 declarations across 21 native types; inventory, ownership, source mapping and negative controls passed (3,851 API rows, 133 production imports, 549 source/resource references and 509 Spring exposures). All 2,723 fixed assignments remain unique and receiver/member/descriptor-stable: 730 classified and 1,993 unresolved. Exactly task 67's 332 IDs changed scope; outside scope fields only 23 unfinished implementation owners and 330 remaining-work descriptions changed. Native declarations, implementation statuses and SPI/native/mock/test mappings are unchanged. Every required unfinished row and consumer has an active owner. All 57 exclusions are missing/unmapped, and no linked current source reference needs retirement. Source dispositions are 323 required-client / three excluded-server / 223 unresolved; existing-SPI dispositions are 138 required-client / 11 unresolved. The 77-task dependency graph remains acyclic. No new runtime probe, Java/build change or Gradle execution was required.
