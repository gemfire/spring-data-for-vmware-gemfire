Type: task
Status: resolved
Blocked by: 60, 61, 62, 63

# Audit statistics logging and internal utility client scope

## Fixed scope

This child of [task 64](64-complete-client-scope-reconciliation.md) owns **265 API IDs across 26 native types**, frozen on 2026-10-28 in [the assignment ledger](../wayfinder-impl/coverage/client-scope-audit-assignments.tsv), selected by `audit_owner = wayfinder-issues/73-audit-statistics-logging-and-utility-client-scope.md`. Read the [audit work rules](../wayfinder-impl/investigations/client-scope-audit-plan.md#work-rules). This is scope classification and prerequisite cleanup, not completion of the downstream SPI implementation.

## Review batches

Statistics descriptors/types/factories/instances; logging/localization; internal native utility entry points; management result/value helpers and region discovery internals.

Retain only evidenced client contracts or private driver equivalents. Internal reachability alone does not authorize a public SPI mirror. Reuse the completed typed region-discovery handoff to tasks 25/32.

## Completion criteria

- [x] Every assigned ID has an evidence-backed required-client or excluded-server decision; no assigned ID remains unresolved. Use exact IDs, preserve native declarations and distinguish scope from implementation status.
- [x] Required rows have an active implementation and consumer owner with concrete retained behavior. If an earlier foundation is resolved, place outstanding implementation in an active downstream ticket; do not leave it assigned only to a closed foundation.
- [x] Excluded rows have no stale public implementation mapping: remove an existing server surface, prove a retained client equivalent, or record private native integration infrastructure. Perform necessary retirement here before task 59 closes.
- [x] Reconcile linked source/resource and existing-SPI dispositions, including inherited members. Coordinate cross-family payloads without taking over another child's fixed IDs.
- [x] Evidence ledger lists each batch, native source path/member at the pinned commit, disposition, ownership and any unanswered behavior. A runtime probe requires a specific unresolved question and a result that could change the decision; stop probing once settled.
- [x] Deterministic coverage generation/check, scope-policy tests, declaration/ownership validation and assignment accounting pass. For Java/build changes, obtain user-run affected Gradle checks and boundary validation; documentation-only classification needs no new runtime suite.

Close with assigned/resolved/remaining counts and downstream handoff. Do not wait for full SPI/native/mock implementation or every-version compatibility tests; those remain existing implementation acceptance. New discoveries go to task 64 for explicit assignment, rather than silently enlarging this fixed scope. Do not block this task on 59 or 64: that would create a cycle.

## Completion — 2026-10-28

**265 assigned / 265 classified / zero remaining: 227 required-client and 38 excluded-server.** The [exact-ID evidence ledger](../wayfinder-impl/investigations/client-statistics-logging-and-utility-scope.md) records the pinned native sources and retained behavior. Statistics/logging/message values (212 IDs) remain with implementation 23 and consumer 36; six internal utility replacements remain with 36; nine function identity/discovery IDs move to active implementation 25 and consumer 32. All 265 implementation statuses remain missing.

The 38 exclusions cover locator hosting, the peer-cache carrier, a WAN property helper and redundancy-management results. None has an existing public SPI implementation or current production reference requiring retirement. Task 72 already removed and validated the locator-hosting accessor. Linked source dispositions and all 149 existing SPI references reconcile. No Java/build change or new runtime probe is required.

Deterministic generation/check, all 10 coverage policy tests and independent declaration validation pass (372 declarations across 21 types). Assignment accounting preserves all 3,851 API IDs and 2,723 frozen assignments, with only this task's IDs classified. Next: task 74 (129 disk/resource/snapshot IDs); tasks 59/64 remain open for final reconciliation.
