Type: task
Status: resolved
Blocked by: 60, 61, 62, 63

# Audit security and authentication client scope

## Fixed scope

This child of [task 64](64-complete-client-scope-reconciliation.md) owns **104 API IDs across 10 native types**, frozen on 2026-10-28 in [the assignment ledger](../wayfinder-impl/coverage/client-scope-audit-assignments.tsv), selected by `audit_owner = wayfinder-issues/70-audit-security-and-authentication-client-scope.md`. Read the [audit work rules](../wayfinder-impl/investigations/client-scope-audit-plan.md#work-rules). This is scope classification and prerequisite cleanup, not completion of the downstream SPI implementation.

## Review batches

Client credential initialization; authenticated views/configuration; OperationContext/OperationCode; AccessControl/Authenticator/SecurityManager/PostProcessor; JNDI and communication-channel values.

Distinguish client credentials and received failures from server authentication/authorization hosting callbacks. Permission-value and function-hook scope is already settled and is outside this fixed assignment.

## Completion criteria

- [x] Every assigned ID has an evidence-backed required-client or excluded-server decision; no assigned ID remains unresolved. Use exact IDs, preserve native declarations and distinguish scope from implementation status.
- [x] Required rows have an active implementation and consumer owner with concrete retained behavior. If an earlier foundation is resolved, place outstanding implementation in an active downstream ticket; do not leave it assigned only to a closed foundation.
- [x] Excluded rows have no stale public implementation mapping: remove an existing server surface, prove a retained client equivalent, or record private native integration infrastructure. Perform necessary retirement here before task 59 closes.
- [x] Reconcile linked source/resource and existing-SPI dispositions, including inherited members. Coordinate cross-family payloads without taking over another child's fixed IDs.
- [x] Evidence ledger lists each batch, native source path/member at the pinned commit, disposition, ownership and any unanswered behavior. A runtime probe requires a specific unresolved question and a result that could change the decision; stop probing once settled.
- [x] Deterministic coverage generation/check, scope-policy tests, declaration/ownership validation and assignment accounting pass. For Java/build changes, obtain user-run affected Gradle checks and boundary validation; documentation-only classification needs no new runtime suite.

Close with assigned/resolved/remaining counts and downstream handoff. Do not wait for full SPI/native/mock implementation or every-version compatibility tests; those remain existing implementation acceptance. New discoveries go to task 64 for explicit assignment, rather than silently enlarging this fixed scope. Do not block this task on 59 or 64: that would create a cycle.

## Answer — 2026-10-28

**104 assigned / 28 required-client / 76 excluded-server / zero unresolved.** The [nine-batch evidence and exact-ID ledger](../wayfinder-impl/investigations/client-security-and-authentication-scope.md) retains client credential callbacks/keys, JNDI configuration/binding and client TLS channel values. It excludes server authentication/authorization/post-processing hosting, operation contexts/codes and non-client TLS selectors. Deprecated conveniences, inherited lifecycle, credential constants on an excluded interface and the client XML datasource payload are explicitly accounted for.

All 28 retained requirements remain missing with active implementation owner 24 and consumer owner 35; concrete handoffs are recorded there. All 76 exclusions are missing/unmapped with no public production/SPI surface to retire. The remaining TestSecurityManager is a server integration fixture whose relocation already belongs to task 77; its current Spring test placement is not final native-free compliance. Authenticated views, permission values and other families' shared exception IDs remain unchanged.

Deterministic coverage generation/check and all 10 scope-policy tests passed. Independent javap matched 372 declarations across 21 types; inventory, ownership, source mappings and negative controls passed. All 3,851 API IDs and 2,723 fixed assignments remain intact and descriptor-stable. Exactly these 104 rows changed scope and remaining-work descriptions; implementation status, owners, native declarations and SPI/native/mock/test mappings are unchanged. All assigned IDs appear exactly once in the evidence ledger. Source dispositions are 421 required-client / three excluded-server / 125 unresolved; existing SPI is unchanged at 147 required-client / two unresolved. The 77-task dependency graph and diff whitespace checks pass. No Java/build change, new runtime probe or Gradle execution was needed.

Task 71's fixed 401-ID serialization/PDX/JSON audit is next. Tasks 59/64 remain open with 1,634 API IDs unresolved; downstream SPI/native/mock implementation and final native-free acceptance remain open.
