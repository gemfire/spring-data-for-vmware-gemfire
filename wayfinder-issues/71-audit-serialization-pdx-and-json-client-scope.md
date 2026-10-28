Type: task
Status: resolved
Blocked by: 60, 61, 62, 63

# Audit serialization PDX and JSON client scope

## Fixed scope

This child of [task 64](64-complete-client-scope-reconciliation.md) owns **401 API IDs across 24 native types**, frozen on 2026-10-28 in [the assignment ledger](../wayfinder-impl/coverage/client-scope-audit-assignments.tsv), selected by `audit_owner = wayfinder-issues/71-audit-serialization-pdx-and-json-client-scope.md`. Read the [audit work rules](../wayfinder-impl/investigations/client-scope-audit-plan.md#work-rules). This is scope classification and prerequisite cleanup, not completion of the downstream SPI implementation.

## Review batches

PDX instances/factories/documents; PDX reader/writer/serializer callbacks/cache settings; binary serialization/registration; JSON conversion/storage format; reflection serializer/FieldType. Work these five groups separately.

Use pinned client read/write/registration paths first. Probe only unresolved alias, class-loading or serialization behavior that changes scope or a required mapping; full format compatibility is downstream implementation acceptance.

## Completion criteria

- [x] Every assigned ID has an evidence-backed required-client or excluded-server decision; no assigned ID remains unresolved. Use exact IDs, preserve native declarations and distinguish scope from implementation status.
- [x] Required rows have an active implementation and consumer owner with concrete retained behavior. If an earlier foundation is resolved, place outstanding implementation in an active downstream ticket; do not leave it assigned only to a closed foundation.
- [x] Excluded rows have no stale public implementation mapping: remove an existing server surface, prove a retained client equivalent, or record private native integration infrastructure. Perform necessary retirement here before task 59 closes.
- [x] Reconcile linked source/resource and existing-SPI dispositions, including inherited members. Coordinate cross-family payloads without taking over another child's fixed IDs.
- [x] Evidence ledger lists each batch, native source path/member at the pinned commit, disposition, ownership and any unanswered behavior. A runtime probe requires a specific unresolved question and a result that could change the decision; stop probing once settled.
- [x] Deterministic coverage generation/check, scope-policy tests, declaration/ownership validation and assignment accounting pass. For Java/build changes, obtain user-run affected Gradle checks and boundary validation; documentation-only classification needs no new runtime suite.

Close with assigned/resolved/remaining counts and downstream handoff. Do not wait for full SPI/native/mock implementation or every-version compatibility tests; those remain existing implementation acceptance. New discoveries go to task 64 for explicit assignment, rather than silently enlarging this fixed scope. Do not block this task on 59 or 64: that would create a cycle.


## Query/CQ audit coordination — 2026-10-28

Task 65 completed the six query metadata serialization IDs in its own fixed inventory: required-client via typed equivalents with private versioned codecs. This task keeps its original 401 IDs; do not reassign or duplicate those six. Tasks 21/55 own implementation and compatibility acceptance; this coordination does not add a blocker or presume the disposition of other serialization APIs. See [the source-backed disposition](../wayfinder-impl/investigations/client-query-and-cq-scope.md).

## Resolution — 2026-10-28

**401 assigned / 401 classified / zero remaining: 396 required-client and five excluded-server.** The [exact source-backed ledger](../wayfinder-impl/investigations/client-serialization-pdx-and-json-scope.md) covers all five review families. Peer CacheFactory PDX setters have no existing implementation/reference to retire; equivalent client settings remain retained, with persistence/disk-store acceptance distinguished from effective client behavior. Native registry metadata, codecs and manager state stay private behind typed client contracts.

All implementation statuses remain unchanged. Transfer the 77 partial PDX value rows from resolved foundation 53 to active task 58; the implemented sentinel stays with 53. Tasks 54–58 own remaining implementation, with active consumer owners 31/35/58 and explicit Spring adoption handoffs. Twenty-two linked source references become required-client; the two unresolved existing-SPI references become required-client. No new IDs, Java/build changes or runtime probes were needed. Tasks 59/64 stay open; task 72 is next.

## Validation

Deterministic generation/check and all 10 scope-policy tests passed. Independent javap matched 372 declarations across 21 types; ownership/source/ID/scope checks and both inventory negative controls passed (3,851 API rows, 133 production imports, 549 source/resource references and 509 Spring exposures). Independent before/after accounting confirms exactly 401 scope/remaining-description changes and 77 owner transfers, with all other fields and implementation mappings/statuses unchanged. All 2,723 frozen assignments remain unique, byte-for-byte unchanged and declaration-stable: 1,490 classified / 1,233 unresolved. The evidence ledger lists each of the 401 assigned IDs exactly once. Five excluded rows have no linked production/SPI reference or implementation mapping; all unfinished required rows have active implementation/consumer owners. Source dispositions are 443 required-client / three excluded-server / 103 unresolved; all 149 existing-SPI references are required-client. The 77-task dependency graph remains acyclic (41 open / 34 resolved / two superseded). No Gradle command was run; this classification changes no Java/build behavior.

Commands: `python3 wayfinder-impl/coverage/build_api_coverage.py`, its `--check` mode, `python3 -m unittest discover -s wayfinder-impl/coverage -p 'test_*.py' -v`, `python3 wayfinder-impl/coverage/validate_coverage.py --javap`, independent `/tmp/verify_scope71.py` assignment/accounting checks, and `git diff --check`.
