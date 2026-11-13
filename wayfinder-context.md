# Wayfinder working context (condensed)

Read this instead of the full `wayfinder*` trees. Open the long files only for the one task you are working on.

## What the project is
- Branch `wayfinder/unified_driver`. Goal: migrate `spring-data-vmware-gemfire` off native GemFire onto a Driver SPI.
- Modules: `spring-data-gemfire-driver-spi` (typed API, no native types), `-core`, `-10.4` (native adapter, the only place native types may appear), `-mock` (in-memory test provider).
- Plan: 9 steps in `wayfinder/technical-spec.md` §7. Scope is client-only: 3,851 frozen API IDs, 2,694 required-client, 1,157 excluded-server.
- Native build under test: `10.4.0-build.3068.c3a2bbfe` (frozen inventory and old evidence are labelled `3056.df98c835`).
- Status (per `wayfinder-impl/map.md`): 78 tasks, 23 open, 53 resolved, 2 superseded. Tasks 19 and 20 were completed earlier; task 20's unfinished rows moved to 35.

## Where things live
- `wayfinder-issues/NN-*.md`: one ticket per task (status, checklist, `## Answer` when resolved).
- `wayfinder-impl/map.md` and `spi-gap-closure-plan.md`: counts, "Latest slice", "Next". Update both on every resolve.
- `wayfinder-impl/coverage/`: `api-coverage.tsv`, `coverage-manifest.json`, `coverage-policy.json`, `build_api_coverage.py`, `validate_coverage.py` (use `python3`, no `.venv` here; never pipe its output to `head`).
- `wayfinder-impl/investigations/`: evidence traces, e.g. `client-pool-and-durable-scope.md#client-default-pool-selection`.
- Gradle gates: `verifyMigrationBoundary`, `verifyDriverCompatibility` (`verifyNativeFreeBoundary` at final removal).

## Rules to follow
- Never run tests. Write them, then give the user the exact `./gradlew` command and wait.
- Design: collaborators injected, no static state or singletons, no native unwrap. Unsupported operations throw `UnsupportedFeatureException` (`notIntroduced` / `removed`).
- New files: first line `// Copyright 2026 Broadcom. All Rights Reserved.` (no Apache block). Existing Apache headers stay.
- Evidence-based claims: cite primary sources (javap on the 3068 jar, traces). Python via `.venv/bin/python`.
- Commits only when asked. Commit trailer: `Co-Authored-By: Claude Sonnet 5.5 <noreply@anthropic.com>`.

## Task 23: resolved
Recorded in ticket, map, plan and `client-runtime-closure.md`. User confirmed the test passed. Nothing committed.

## Task 51: resolved
Typed `CacheConfiguration.defaultPool()` and `DriverConnection.getDefaultPool()` are implemented in the SPI, native adapter (`GemFireDefaultPoolConfiguration`) and mock. User-run mock (5) and native (11) tests passed. Ticket Answer, map and plan updated. Unfinished task-23 rows were reassigned to task 36. Nothing committed. Parent 20, task 35 and task 52 stay open.

Native behaviour to remember (`GemFireCacheImpl.determineDefaultPool`, lazy): explicit factory with no endpoints adds a local-host-name **server** on 40404; created names are DEFAULT, DEFAULT1, … and are never selection keys; `threadLocalConnections` is rejected as `removed`.

## Task 21 (query results and index SPI): implemented, still OPEN
Implemented in SPI, native adapter (`GemFireQueryService`, `GemFireQuery`, `GemFireIndex`, `GemFireIndexStatistics`, `GemFireSelectResults`, `GemFireStruct`, `GemFireQueryValues`) and tests. User-run `NativeQueryTests` (10.4 integrationTest, LOCAL regions) and `GemFire104DriverContractTests` passed. The ticket stays `Status: open` because unfinished rows need an open owner.

Coverage state of the 207 required-client rows owned by 21 (after regeneration; validate, `--check` and the 16 Python tests pass):
- implemented 29, partial 57, missing 121.
- 14 `QueryService` CQ rows (`newCq`, `getCqs`, `executeCqs`, `stopCqs`, `closeCqs`, `getCq`, `getCqStatistics`, `getAllDurableCqsFromServer`) were reassigned to ticket 22 (consumer 33) via per-member `owner` in `coverage-policy.json`.
- Policy edits were made with a scratchpad script: `members` keys are `<native_type>#<member><descriptor>`, type rows end in `#<type>`.

**Judgment call to confirm:** the 29 rows are `implemented` although the mock has no query service and rejects all six query entry points as `NOT_INTRODUCED` (noted in each row's `mock` field). README line 53 asks for complete mock evidence and its transaction precedent (line 158) used `partial` for whole-feature typed rejection. Strict reading flips those 29 to `partial`.

### What is left
1. **Partial rows (mapped, no native assertion)** each carry a `remaining` note in the policy:
   - HASH and key index: `createHashIndex` x2, `createKeyIndex`, `defineHashIndex` x2, `defineKeyIndex`, `defineIndex` imports overload, `createIndex` String-typed and imports overloads.
   - `Index` getters: `getFromClause`, `getProjectionAttributes`, `getCanonicalizedFromClause`, `getCanonicalizedIndexedExpression`, `getCanonicalizedProjectionAttributes`.
   - `IndexStatistics` getters other than `getNumberOfKeys`.
   - `Query.getQueryString`, `Query.getStatistics`, the 3 `QueryStatistics` rows, `Struct.getStructType` content (only non-null is asserted).
   - `IndexType` `HASH`, `PRIMARY_KEY`, `getName`; `<type>` rows for QueryService, Query, Index, IndexStatistics, IndexType, QueryStatistics, Struct.
   - `IndexType.valueOfSynonym`: real semantic gap. Native upper-cases input (`gemfire-core/.../cache/query/IndexType.java`, pinned `df98c835`); SPI `DriverIndexType.valueOfSynonym` is case-sensitive. Fix is `toUpperCase(Locale.ROOT)` plus a line in `QueryServiceContractTests`.
   - Follow-up: extend `NativeQueryTests` for the above (roughly 25 rows would move to implemented), then give the user ONE combined `./gradlew` command.
2. **Missing rows with no SPI equivalent** (need a scope or removal decision, not more tests):
   - `SelectResults` rows (28, inherited Collection/Stream members).
   - `DataSerializable` `fromData`/`toData` and `resolveClass` on `CollectionType`, `ObjectType`, `StructType`.
   - Exception-class rows: constructors, `getMessage`, `getRootCause`, `toString` for the query exceptions.
   - `Query.compile` and `Query.isCompiled`.
3. **Server-backed query coverage** (pool-backed `getQueryService`, region query through a pool) is deferred to ticket 78 (real-server Testcontainers per driver, `Blocked by: 38, 59`). Not counted as covered.
4. **Mock** has no query implementation; all query entry points are registered as `unsupported ... NOT_INTRODUCED` in `MockDriverContractTests`.
5. Mark 21 resolved and update `map.md` and `spi-gap-closure-plan.md` only when every row is implemented or has an open reassigned owner.

Native behaviour to remember: `getQueryService()` is pool-backed, needs a server and rejects index operations; `getLocalQueryService()` needs a `ClientCache` and supports indexes. Native `IndexType`: `FUNCTIONAL("RANGE")`, `HASH("HASH")`, `PRIMARY_KEY("KEY")`, deprecated; `getName()` equals SPI `synonym()`.

## Task 22 (continuous query SPI): open
Blocked by 45 and 21. Now also owns the 14 reassigned `QueryService` CQ rows.

## Working-tree state
Uncommitted. Never stage `gradle.properties`, `CLAUDE.md` or `wayfinder-context.md`.

## Next
Decide: (a) strict mock reading for task 21, (b) extend `NativeQueryTests` and fix `valueOfSynonym`, then (c) task 22. Other candidates: 47, 48, 54, 55, 56.
