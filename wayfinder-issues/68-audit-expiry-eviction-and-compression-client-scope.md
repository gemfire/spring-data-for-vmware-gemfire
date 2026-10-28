Type: task
Status: resolved
Blocked by: 60, 61, 62, 63

# Audit expiry eviction and compression client scope

## Fixed scope

This child of [task 64](64-complete-client-scope-reconciliation.md) owns **154 API IDs across 14 native types**, frozen on 2026-10-28 in [the assignment ledger](../wayfinder-impl/coverage/client-scope-audit-assignments.tsv), selected by `audit_owner = wayfinder-issues/68-audit-expiry-eviction-and-compression-client-scope.md`. Read the [audit work rules](../wayfinder-impl/investigations/client-scope-audit-plan.md#work-rules). This is scope classification and prerequisite cleanup, not completion of the downstream SPI implementation.

## Review batches

Expiration actions/attributes/custom callbacks/TTL reset; eviction algorithms/actions/attributes/mutators/object sizing; compression implementations and region setters/getters.

Trace which client shortcuts accept each setting and which callbacks run locally. Probe only an unresolved supported-client behavior, not every enum constant or accessor.

## Completion criteria

- [x] Every assigned ID has an evidence-backed required-client or excluded-server decision; no assigned ID remains unresolved. Use exact IDs, preserve native declarations and distinguish scope from implementation status.
- [x] Required rows have an active implementation and consumer owner with concrete retained behavior. If an earlier foundation is resolved, place outstanding implementation in an active downstream ticket; do not leave it assigned only to a closed foundation.
- [x] Excluded rows have no stale public implementation mapping: remove an existing server surface, prove a retained client equivalent, or record private native integration infrastructure. Perform necessary retirement here before task 59 closes.
- [x] Reconcile linked source/resource and existing-SPI dispositions, including inherited members. Coordinate cross-family payloads without taking over another child's fixed IDs.
- [x] Evidence ledger lists each batch, native source path/member at the pinned commit, disposition, ownership and any unanswered behavior. A runtime probe requires a specific unresolved question and a result that could change the decision; stop probing once settled.
- [x] Deterministic coverage generation/check, scope-policy tests, declaration/ownership validation and assignment accounting pass. For Java/build changes, obtain user-run affected Gradle checks and boundary validation; documentation-only classification needs no new runtime suite.

Close with assigned/resolved/remaining counts and downstream handoff. Do not wait for full SPI/native/mock implementation or every-version compatibility tests; those remain existing implementation acceptance. New discoveries go to task 64 for explicit assignment, rather than silently enlarging this fixed scope. Do not block this task on 59 or 64: that would create a cycle.

## Task 67 coordination — 2026-10-28

[Task 67](../wayfinder-impl/investigations/client-cache-and-region-assembly-scope.md#client-eviction-config-values) settles the separately assigned eviction configuration DTOs as client-required through EvictionAttributes.convertToConfigEvictionAttributes and EvictionAttributesImpl.fromConfig. Task 49 owns equivalent native-free values/conversion, with task 30 adoption. Retain this source evidence when classifying your EvictionAttributes members; your fixed 154 IDs remain unchanged. Server partition-hosting and conflation configuration are excluded; client-local eviction/persistence and ordinary XML are retained. No additional probe is needed merely because these values share a server schema.

## Answer — 2026-10-28

**154 assigned / 154 required-client / zero excluded-server / zero unresolved.** The [seven-batch evidence and exact-ID ledger](../wayfinder-impl/investigations/client-expiry-eviction-and-compression-scope.md) resolves client region settings, expiration values/binary hydration/custom callbacks, eviction values/mutation, sizing and compression from pinned native source and current Spring consumers. LIFO controller paths and Snappy client use remain requirements despite deprecation. No assigned API is excluded, so no server surface retirement is needed here.

All 154 implementation statuses remain missing under active task 47; task 30 owns adoption, with task 49 eviction-value and task 55 codec coordination. The handoff records proxy/statistics/cloning constraints, reset policy and callback lifecycle discrepancies rather than inferring automatic custom-expiry close. Implementation, mock behavior and per-version compatibility remain downstream acceptance.

Coverage generation/check, all 10 policy tests, independent javap (372 declarations / 21 types), ownership/source/inventory checks and negative controls passed. All 3,851 IDs and 2,723 fixed assignments remain intact and descriptor-stable. Exactly these 154 rows changed scope and remaining-work descriptions; declarations, ownership, implementation statuses and SPI/native/mock/test mappings did not change. The evidence ledger covers every assigned ID. Source dispositions now total 374 required-client / three excluded-server / 172 unresolved; existing SPI remains 138 required-client / 11 unresolved. Dependency graph and diff whitespace checks pass. No Java/build change, runtime probe or Gradle execution was needed.

Task 69's fixed 101-ID pool/durable audit is next. Tasks 59/64 remain open, with 1,839 API IDs unresolved across remaining audit families.
