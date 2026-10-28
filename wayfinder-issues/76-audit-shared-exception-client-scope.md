Type: task
Status: resolved
Blocked by: 60, 61, 62, 63

# Audit shared exception client scope

## Fixed scope

This child of [task 64](64-complete-client-scope-reconciliation.md) owns **390 API IDs across 67 native types**, frozen on 2026-10-28 in [the assignment ledger](../wayfinder-impl/coverage/client-scope-audit-assignments.tsv), selected by `audit_owner = wayfinder-issues/76-audit-shared-exception-client-scope.md`. Read the [audit work rules](../wayfinder-impl/investigations/client-scope-audit-plan.md#work-rules). This is scope classification and prerequisite cleanup, not completion of the downstream SPI implementation.

## Review batches

All 67 remaining exception types as complete member families, including constructors/payloads inherited by query/CQ, region, transaction, security, serialization, disk and runtime failures.

Classify by a traced client throw/receive/translation path, including failures originating on a server and received by a client. Constructor reachability alone is insufficient. Reuse existing exception mapping tests; probe only ambiguous client-observable payloads.

## Completion criteria

- [x] Every assigned ID has an evidence-backed required-client or excluded-server decision; no assigned ID remains unresolved. Use exact IDs, preserve native declarations and distinguish scope from implementation status.
- [x] Required rows have an active implementation and consumer owner with concrete retained behavior. If an earlier foundation is resolved, place outstanding implementation in an active downstream ticket; do not leave it assigned only to a closed foundation.
- [x] Excluded rows have no stale public implementation mapping: remove an existing server surface, prove a retained client equivalent, or record private native integration infrastructure. Perform necessary retirement here before task 59 closes.
- [x] Reconcile linked source/resource and existing-SPI dispositions, including inherited members. Coordinate cross-family payloads without taking over another child's fixed IDs.
- [x] Evidence ledger lists each batch, native source path/member at the pinned commit, disposition, ownership and any unanswered behavior. A runtime probe requires a specific unresolved question and a result that could change the decision; stop probing once settled.
- [x] Deterministic coverage generation/check, scope-policy tests, declaration/ownership validation and assignment accounting pass. For Java/build changes, obtain user-run affected Gradle checks and boundary validation; documentation-only classification needs no new runtime suite.

Close with assigned/resolved/remaining counts and downstream handoff. Do not wait for full SPI/native/mock implementation or every-version compatibility tests; those remain existing implementation acceptance. New discoveries go to task 64 for explicit assignment, rather than silently enlarging this fixed scope. Do not block this task on 59 or 64: that would create a cycle.

## Task 76 completion — 2026-10-29

All **390 assigned exception IDs are classified: 341 required-client / 49 excluded-server / zero unresolved**. All 3,851 frozen API IDs and 2,723 assignment rows are preserved; the fixed assignments total 2,195 required-client / 528 excluded-server. Required implementation statuses and mappings are unchanged. Excluded mappings are retired, including CACHE_EXISTS and TYPE_MISMATCH and explicit native/Spring branches. Required callback/region/transaction work has active owners 30/31/34; other subsystem handoffs are recorded in the exact exception ledger.

The user reported the requested Gradle command passed, including affected compilation, baseline pruning and transitional boundary verification. Local JUnit XML confirms 24 focused tests with zero failures/errors/skips: four SPI translator, six native mapper, two Spring translator and 12 client-only configuration tests. The locator rejection expectation is corrected to IllegalStateException / INVALID_STATE. No Gradle command was run by the agent.

Deterministic coverage generation/check, all 12 Python coverage tests and independent javap validation pass (372 declarations across 21 types). The validator now permits fully retired mappings only for excluded/missing rows; negative tests reject retained, unresolved, partial, implemented or stale SPI claims. Exact-ID/declaration/ownership checks pass. Generated scope is **2,694 required-client / 1,157 excluded-server / zero unresolved**, with **3,125 missing / 471 partial / 255 implemented** across all scopes. The inventory contains 508 Spring exposures, 123 production native imports and 538 source/resource references.

No current source/resource or existing-SPI row links to task 76's 49 exclusions. All 149 existing-SPI references are required-client. Source scopes are 530 required-client / three excluded-server / five unresolved: the remaining CacheServer exclusion references and five unlinked legacy-name/package/logger dispositions stay with parent 64 for final reconciliation. Classification does not establish native-free implementation compliance.

**All 12 API-family classification tasks are resolved. Next: task 77's two legacy test-JAR consumers, then final reconciliation in 64/59.** There are 36 open, 39 resolved and two superseded tasks (77 total).

## Prior checkpoint — 2026-10-28, validation pending

The first user-run mapper suite passed five of six tests. The cache-creation probe incorrectly expected IllegalArgumentException for nonempty distributed-system locators; the runtime and pinned ClientCacheFactory.basicCreate source both use IllegalStateException. Corrected the assertion to INVALID_STATE and the task 29 handoff. The existing mapper already handles this correctly. Rerun the affected Gradle checks below before refreshing coverage; classification and API accounting are unchanged.

All **390 assigned IDs are classified: 341 required-client / 49 excluded-server / zero unresolved**, with exact evidence and active implementation/consumer handoffs in the [ledger](../wayfinder-impl/investigations/client-shared-exception-scope.md). Remaining callback/region/transaction work moves from resolved foundations 17/18/28 to 30/31/34; other retained subsystem owners remain active.

Removed CACHE_EXISTS and TYPE_MISMATCH plus explicit excluded native/Spring converter branches. Server role/lease failures received by clients remain required. All 49 excluded rows lose stale public mappings; 13 partial and one implemented historical row become missing, with no required implementation upgrade. Policy projects 2,694 required-client / 1,157 excluded-server / zero unresolved across all 3,851 frozen IDs; all 2,723 assignment rows are preserved.

Source scan completed. Generation correctly stopped at the compiled-source freshness guard for GemfireCacheUtils.java. Generated coverage still reflects task 75. Remaining work is the user-run Gradle checks below, then coverage generation/check, policy tests, javap declaration validation and final source/SPI/assignment reconciliation. This task remains open; no further scope investigation is needed.

Policy-only accounting passes: all 3,851 API IDs and 2,723 assignment rows are preserved, all 390 ledger entries match declarations/owners, all retained owners are active, and all 49 exclusions have cleared mappings. Eight independent scope-policy guard tests pass; the two saved/generated-inventory checks and javap validation remain pending regeneration. The owner regression test now checks both implementation and consumer ownership on a retained row, so it remains valid when no unresolved rows remain. Source scanning and git diff --check pass.

```sh
./gradlew --offline -DgemfireVersion=10.4.0-build.3056.df98c835 \
  :spring-data-gemfire-driver-spi:test \
  --tests '*GemFireDriverExceptionTranslatorTests' \
  :spring-data-gemfire-driver-10.4:integrationTest \
  --tests '*GemFireExceptionMapperTests' \
  :spring-data-vmware-gemfire:test \
  --tests '*GemfireExceptionTranslatorTests' \
  --tests '*ClientOnlyConfigurationUnitTests' \
  :spring-data-vmware-gemfire:compileIntegrationTestJava \
  :spring-test-vmware-gemfire:compileTestJava \
  pruneMigrationBoundaryBaseline verifyMigrationBoundary
```
