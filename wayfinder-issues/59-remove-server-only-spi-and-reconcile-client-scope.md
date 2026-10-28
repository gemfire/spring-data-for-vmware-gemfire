Type: task
Status: resolved
Blocked by: 14, 15, 17, 18, 28, 39, 46, 60, 61, 62, 63, 64

# Remove server-only SPI and reconcile client scope

## Answer — 2026-10-29

All bounded children 65–77 are resolved. Task 77's user-run command passed:
22 retained client tests, zero failures/errors/skips, isolated fixture packaging,
affected compilation and baseline pruning/verification. Fresh findings match
the baseline with zero new or stale identities. No combined server test-JAR
consumer remains. See the [fixture ledger](../wayfinder-impl/investigations/client-function-fixture-relocation.md#task-77-isolated-domain-and-security-payloads--2026-10-29).

Final classification and ownership reconciliation passes: all 3,851 API IDs are
classified (2,694 required-client / 1,157 excluded-server / zero unresolved), and
all 538 source/resource references have client migration dispositions. The five
unlinked references and three client uses of the excluded CacheServer type have
explicit evidence. Transferred 100 partial API and 72 partial current-SPI mappings
to open completion owners; cleared three stale excluded TransactionWriter mappings.
All frozen IDs and 2,723 child assignments are preserved. See the
[final reconciliation ledger](../wayfinder-impl/investigations/client-scope-final-reconciliation.md).

Generation/check, all 16 policy tests, ownership/source checks and independent
javap comparison (372 declarations / 21 types) pass. The user confirmed the final
aggregate command passed on 2026-10-29. Local XML records 547 test executions with
zero failures/errors/skips; boundary findings have zero new or stale identities.
Affected full compilations, fixture packaging, mock native-free classpath and
ABI/stamp checks pass under the pre-first-release policy. The linked final ledger
records the exact command, native version and suite counts.

Tasks 61/62 retired public server-only creation/configuration/callback SPI;
63 retired Spring server surfaces while retaining client XML; 77 isolated private
server fixtures and preserved client assertions. No excluded API row retains a
public SPI implementation mapping. Tasks 64 and 59 are now resolved, with all
prerequisites complete. There are 33 open, 42 completed and two superseded tasks.
Next eligible task: 23, client runtime identity and connectivity SPI.

Downstream client SPI, mock migration and versioned compatibility work remain
separate acceptance. The transitional boundary still records 5,074 native-source
occurrences; this closure does not establish native-free Spring compliance.


## Question

Remove the server-side SPI work already introduced and align implementations, consumers, tests and coverage with the approved [client-only scope](../wayfinder-impl/client-only-scope.md). This is the next implementation task. No production removal is claimed by creating this ticket.

## Required implementation

1. Inventory committed and working-tree server APIs by stable coverage ID and source symbol. Record keep/remove/private-test-fixture dispositions with client-callability evidence. Reconcile policy, coverage generator and validator with explicit scope classification; preserve the frozen discovery evidence and distinguish scope exclusions from native feature removal. Ambiguous mixed surfaces must receive evidence and an owner before this task closes.
2. Remove SPI `RegionShortcut`, peer cache mode/creation, peer-region factory overloads and their native/mock implementations. Preserve `ClientRegionShortcut`, client-local regions and every verified client configuration value. Adapt client tests that currently use peer creation to genuine client-local creation; maintain configuration, ownership, hierarchy and callback assertions.
3. Remove ticket 42's unfinished server additions: `DriverConnection.addCacheServer/getCacheServers`, `DriverCacheServer`, server-side `DriverClientSession`/`DriverClientSubscriptionConfig`, `DriverServerLoad`/probe/adapter/metrics, and `DriverInterestRegistrationEvent/Listener`. Remove corresponding native adapters, shared `CacheServerContract`, mock server probes and contract inheritance. Audit mapper helpers before removing them; retain helpers used by client behavior. **Keep `InterestResultPolicy`** for client interest registration.
4. Audit and remove committed server-only callbacks/configuration: peer membership/role APIs, WAN timestamp/conflict payloads, gateway/queue settings, partition configuration, distributed locks and server-only transaction hooks. Preserve client-visible metadata, event methods, exceptions and client-supported transaction callbacks. Do not assume all callback methods or all data-policy constants are server-only.
5. Remove resulting server-only Spring API/configuration references, including legacy server-shortcut annotation attributes, wrappers, converters and XML surfaces, so affected full source sets still compile. Record breaking migration notes. Keep client migration obligations with their existing owners; do not introduce compatibility stubs, native unwraps or accepted-but-ignored server settings.
6. Remove or relocate server-only test cases with a disposition ledger. Review `NativeServerFixture` and `ServerSubscriptionClientProcess` from ticket 42: discard server-SPI-only scenarios or retain/refactor useful private native fixtures for client behavior. Native servers/locators/function bodies needed to test clients stay inside driver integration infrastructure, without a public hosting SPI. Preserve unrelated user changes in Gradle and all retained client tests.
7. Regenerate coverage artifacts and report required-client, excluded and unresolved counts separately. Reassign retained mixed rows to active client tickets; no required-client row may remain owned only by retired tickets 42/43. Remove stale active server mandates from plans and coverage policies. Record exact removals and remaining consumer owners in the Answer and implementation map.

## Required validation

- Full compilation of affected SPI/core/mock/native/contract and Spring main/unit/integration source sets; preserve the separate Spring Test module's existing dependency exception.
- Shared client contracts and mock/local lifecycle tests; native client/server tests for affected client region operations, callbacks, interest/subscription, PDX and transactions. Record exact resolved native version and honest mock limits.
- Coverage generation/validation with stable IDs, evidence-backed exclusions, no unclassified rows and no public server-only SPI; migration boundary verification and pruning only for justified changes.
- ABI/stamp checks under ticket 39's explicit pre-first-release policy. Do not fabricate a released baseline or disable compatibility checks.
- Verify no executable public peer/server/WAN/queue API remains in the SPI, while real native integration fixtures can still start servers privately. Compile-only success or renaming types does not establish this condition.

## Completion

Resolve only when existing server-only code and test requirements are removed, retained client behavior passes relevant checks, coverage dispositions are complete and no stale dependency requires implementation of tickets 42/43. If this needs bounded children, add them as blockers and keep this parent open. Full Spring migration, native-free acceptance and all-version integration remain owned by tickets 29–41; completing this cleanup alone does not satisfy those gates. Do not commit or publish without instruction.

## Partial progress — working-tree cleanup

The user authorized committing valid work and removing the rest. Discarded ticket 42's 16 untracked server SPI/adapter/contract/fixture files and reverted its additions to connection APIs, contract inheritance, mock probes and native contract setup. Removed the now-unused member unwrapping helper and the IOException translation added for server startup; existing client socket/transaction IOException behavior remains unchanged. The discarded native fixture pair exercised the removed server-session SPI; existing private native client/server callback fixtures remain.

Retained the native-free `InterestResultPolicy` enum for client registration, with `NativeInterestResultPolicyTests` comparing every policy predicate and the default against native GemFire. This retains a configuration value, not an implementation of client interest registration; tickets 45/48 remain open. Reverted incidental Gradle 9.8.1 and global `10.4.+` overrides; validation uses the committed Gradle 9.7.0 wrapper and an explicit native version pin.

This completes only the unfinished working-tree cleanup in item 3 and the fixture disposition in item 6. Previously committed peer/server APIs (including `RegionShortcut`), consumer migrations and coverage classification remain outstanding; this task stays open.

Validation passed with native **10.4.0-build.3056.df98c835**: 158 JUnit tests (24 SPI, 58 mock, 31 core, 45 focused native), zero failures/errors/skips; mock native-free classpath verification; 32 compiled ABI/stamp fixtures and packaged core/provider stamp checks. The ABI gate correctly reports no released baseline. No full Spring compilation or coverage reclassification is claimed by this bounded cleanup.

```sh
./gradlew --offline -DgemfireVersion=10.4.0-build.3056.df98c835 \
  :spring-data-gemfire-driver-spi:test \
  :spring-data-gemfire-driver-mock:test \
  :spring-data-gemfire-driver-mock:verifyNativeFreeClasspath \
  :spring-data-gemfire-driver-10.4:integrationTest \
  --tests '*NativeInterestResultPolicyTests' \
  --tests '*GemFire104DriverContractTests' verifyDriverCompatibility
```

## Partial progress — classification prerequisite

[Task 60](60-client-scope-coverage-classification.md) is resolved: coverage policy/generation/validation now distinguishes 96 required-client, 549 excluded-server and 3,206 unresolved entries without changing the frozen baseline or historical implementation statuses. Fourteen retained/unresolved rows have active owners replacing superseded task 43. Ten scope tests, deterministic regeneration and 372 independent native declaration checks pass. This supplies the classification infrastructure in items 1/7; it does not complete all decisions or remove runtime APIs.

The remaining bounded children are [61 — creation](61-remove-peer-cache-and-region-creation.md), [62 — callbacks/configuration](62-remove-peer-callbacks-and-server-configuration.md), [63 — Spring surfaces](63-retire-spring-server-configuration-surfaces.md) and [64 — final reconciliation](64-complete-client-scope-reconciliation.md). Children 61/62 are now resolved; child 63 is next. This parent remains open until all cleanup, retained-client regressions and zero-unresolved acceptance pass.

## Partial progress — client-only creation

[Task 61](61-remove-peer-cache-and-region-creation.md) completes item 2: public peer cache mode, `RegionShortcut` and peer region/subregion creation are removed from SPI/native/mock. Shared contracts use clients; native client PDX has a real server/pool and all five LOCAL shortcut variants retain native attribute checks. The [test disposition ledger](../wayfinder-impl/investigations/client-only-creation.md) assigns remaining private peer fixtures to task 62. Full affected compilation and 520 JUnit executions pass, with mock isolation, ABI/stamps, boundary and coverage validation.

The 3,851 frozen API IDs remain. Removing 54 stale partial mappings yields 2,975 missing / 596 partial / 280 implemented; independent scope remains 96 required-client / 549 excluded-server / 3,206 unresolved. Callback/configuration removal (62), Spring surfaces (63) and complete reconciliation (64) remain mandatory. This parent stays open.

## Child 62 outcome — 2026-10-23

[Task 62](62-remove-peer-callbacks-and-server-configuration.md) resolves callback/configuration cleanup and every private peer fixture disposition from task 61. The [evidence ledger](../wayfinder-impl/investigations/client-only-callbacks-and-configuration.md) records removed hooks and real client transaction/subscription coverage. 510 JUnit executions, full affected compilation, mock isolation, ABI/stamps, boundary and coverage checks pass. All frozen IDs remain: 362 required-client / 625 excluded-server / 2,864 unresolved; 3,111 missing / 484 partial / 256 implemented. Client transaction outcome/event scope remains explicit task 64 work. Spring surface retirement (63) is next; this parent stays open.


## Child 63 outcome — 2026-10-27

[Task 63](63-retire-spring-server-configuration-surfaces.md) is resolved. Its [ledger](../wayfinder-impl/investigations/client-only-spring-surfaces.md) records server configuration/function/feature retirement, retained client XML, breaking changes and test dispositions. The inherited-attributes slice passes 82 focused tests; the final feature-catalog follow-up passes 15, without failures/errors/skips. Affected compilation, transitional boundary and refreshed coverage/declaration checks pass. Frozen scope remains 362 required-client / 625 excluded-server / 2,864 unresolved.

Task 64 is next and all its blockers are resolved. This parent remains open for member-level reconciliation, mixed transaction outcomes, private server function fixture relocation and final acceptance. Remaining client native coupling stays assigned to downstream migration owners; this is not native-free compliance.

## Task 64 transaction scope receipt — 2026-10-27

[Client-local outcome evidence](../wayfinder-impl/investigations/client-local-transaction-outcomes.md) resolves 11 listener/event rows as required-client. Five local and eleven existing transaction tests pass with zero failures/errors/skips, plus transitional boundary verification. Required callbacks remain in SPI; implementation/consumer and native-free mock obligations remain tasks 34/37. All 3,851 IDs remain: 373 required-client / 625 excluded-server / 2,853 unresolved. Task 64 and this parent remain open for the remaining member dispositions and private fixture relocation.

## Task 64 function fixtures — validated

The 13 inventoried function files (15 server bodies) and server PDX XML have moved to a private native driver source set. Seven Spring client suites deploy a separate fixture JAR, with shared JDK-only DTO/IDs and a packaging isolation gate. See the [relocation record](../wayfinder-impl/investigations/client-function-fixture-relocation.md) for retained assertions and the two remaining non-function legacy test-JAR consumers. Source/coverage checks pass; validation results are recorded below. Neither task 64 nor this parent is complete.

User-run compilation, packaging verification and 15 real-server tests now pass (zero failures/errors/skips). Reconciled the existing boundary identity for the deployment property/JAR rename without changing its count or owner. User-run boundary pruning/verification now passes: 67 stale entries removed, with zero new or stale identities remaining. The fixture relocation slice is validated; both tasks remain open for the remaining scope and infrastructure reconciliation.

## Task 64 function member scope — 2026-10-28

The [member ledger](../wayfinder-impl/investigations/client-function-member-scope.md) resolves 37 required-client rows and four excluded peer targets using pinned native source. There are 31 function rows still awaiting callback/permission/registry decisions. Current scope is 410 required-client / 629 excluded-server / 2,812 unresolved, with all frozen IDs and implementation statuses preserved. Tasks 25/32 retain typed client implementation and Spring cutover; no public server service is added. Task 64 and this parent remain open.

## Bounded remaining audit — 2026-10-28

Task 64 now waits for [children 65–77](../wayfinder-impl/investigations/client-scope-audit-plan.md): 12 fixed API-family inventories and the two remaining test-JAR consumers. Task 64 retains the final zero-unresolved/source/removal/validation gate; existing downstream blockers are unchanged. Task 65 is now resolved with all 261 query/index/CQ IDs classified (259 required-client / two excluded-server). Its [final batch](../wayfinder-impl/investigations/client-query-and-cq-scope.md) settled query/index/CQ scope. Task 66 is also resolved with 137 region/callback IDs (129 required-client / eight excluded-server); the [evidence and handoff](../wayfinder-impl/investigations/client-region-and-callback-scope.md) record that completed audit. Task 67 now also resolves all 332 cache/region assembly IDs (275 required-client / 57 excluded-server); see the [member ledger](../wayfinder-impl/investigations/client-cache-and-region-assembly-scope.md). Current scope is **2,353 required-client / 1,108 excluded-server / 390 unresolved**. Task 68 is also resolved: **154 assigned / 154 required-client / zero remaining**; see the [expiry/eviction/compression ledger](../wayfinder-impl/investigations/client-expiry-eviction-and-compression-scope.md). Tasks 47/30 retain implementation/adoption. Task 69 is also resolved: **101 assigned / 101 required-client / zero remaining**; see the [pool/durable ledger](../wayfinder-impl/investigations/client-pool-and-durable-scope.md). Active tasks 20/51/52 own implementation and task 35 adoption; 84 unfinished rows move off resolved foundation 50. Task 70 is also resolved: **104 assigned / 28 required-client / 76 excluded-server / zero remaining**; see the [security/authentication ledger](../wayfinder-impl/investigations/client-security-and-authentication-scope.md). Tasks 24/35 retain client implementation/adoption; task 77 retains the existing server security fixture relocation. Task 71 is also resolved: **401 assigned / 396 required-client / five excluded-server / zero remaining**; see the [serialization/PDX/JSON ledger](../wayfinder-impl/investigations/client-serialization-pdx-and-json-scope.md). Tasks 54–58 retain implementation, with 77 partial value rows moved from resolved foundation 53 to active task 58. Tasks 72–75 are resolved; task 76 is next. All frozen IDs and implementation statuses remain unchanged. This split adds no product scope and does not establish native-free compliance.

## Task 72 completion — 2026-10-28

All 291 runtime configuration/lifecycle IDs are classified: **145 required-client / 146 excluded-server / zero unresolved**. Whole inventory: **1,986 required-client / 923 excluded-server / 942 unresolved**, with all 3,851 frozen IDs and implementation statuses unchanged. Required work stays with implementation task 23 and consumer task 36. Task 72 is resolved: the user-run Java/boundary checks passed, and the post-removal source/bytecode inventory and declaration checks pass. The public locator-hosting accessor is removed. Current inventory: 131 production native imports, 547 source/resource references and 508 Spring signature exposures. Earlier inventory counts describe their recorded checkpoints. Tasks 73–76 own the remaining 942 API IDs; task 77 retains legacy test-packaging work.

## Task 73 completion — 2026-10-28

All 265 statistics/logging/utility IDs are classified: **227 required-client / 38 excluded-server / zero unresolved**. Whole inventory: **2,213 required-client / 961 excluded-server / 677 unresolved**. All 3,851 frozen IDs and implementation statuses are preserved. Tasks 23/36 retain 212 statistics/logging/message IDs and six internal utility replacements; nine function identity/discovery IDs move to 25/32. The 38 exclusions have no public SPI implementation or linked current production reference requiring removal. Task 72's locator cleanup remains validated.

Deterministic generation/check, all 10 policy tests, independent declaration validation (372 declarations / 21 types) and exact assignment accounting pass. All 2,723 frozen assignments are preserved: 2,046 classified / 677 unresolved. Current source scopes are 457 required-client / three excluded-server / 87 unresolved; all 149 existing SPI references are required-client. This batch changes no Java/build behavior and needs no Gradle run. Three classification tasks remain: 74 (129 IDs), 75 (158) and 76 (390); task 77 retains fixture cleanup and 59/64 final reconciliation. Next: task 74.

## Task 74 completion — 2026-10-28

All **129 disk/resource/snapshot IDs** are classified: **94 required-client / 35 excluded-server / zero unresolved**. Implementation/adoption remain with 27/30/31 (66 retained disk/resource IDs) and 44/31 (28 snapshot IDs), coordinating cache assembly with 29/35. The exclusions cover partition rebalance/redundancy and parallel snapshot options; none has a public implementation mapping or linked current production/SPI reference to retire. Source establishes legacy local stream snapshots, including destructive region reinitialization, and the boundary between local snapshot export and native remote export.

Deterministic generation/check, all 10 policy tests, independent declaration validation (372 declarations / 21 types) and exact assignment accounting pass. All 3,851 frozen IDs, implementation statuses and mappings remain unchanged. The 2,723 assignments remain unchanged: **2,175 classified / 548 unresolved**. Whole scope is **2,307 required-client / 996 excluded-server / 548 unresolved**; source scopes are 473 required-client / three excluded-server / 71 unresolved, and all 149 existing SPI references remain required-client. No Java/build behavior changed or Gradle command was run.

Two classification tasks remain: **75 (158 IDs)** and **76 (390 IDs)**. Task 77 retains fixture cleanup, and 59/64 final reconciliation. Next: task 75.

## Task 75 completion — 2026-10-28

All **158 partition/membership/subscription IDs** are classified: **46 required-client / 112 excluded-server / zero unresolved**. Active implementation/adoption owners remain 48/30. Client request routing requires PartitionResolver, EntryOperation and StringPrefixPartitionResolver; the blanket exclusions in tasks 19/48 are corrected. Partition hosting attributes/listeners, rebalance metadata and peer role-reliability values remain excluded, with no current public implementation or linked production/SPI mapping to retire. Client subscription value defaults and client factory defaults are distinguished. See the exact member ledger and pinned source evidence in `wayfinder-impl/investigations/client-partition-membership-and-subscription-scope.md`.

Deterministic generation/check, all 10 policy tests, independent javap validation (372 declarations / 21 types) and exact assignment accounting pass. All 3,851 frozen API IDs, implementation statuses, ownership and mappings are unchanged; the 2,723-entry assignment ledger is byte-for-byte unchanged. Exactly 158 rows change scope/remaining-work fields. Assigned scope is **2,333 classified / 390 unresolved** (1,854 required-client / 479 excluded-server). Whole inventory is **2,353 required-client / 1,108 excluded-server / 390 unresolved**. Source scopes remain 473 required-client / three excluded-server / 71 unresolved; all 149 existing SPI references remain required-client. The 77-task dependency graph remains acyclic: 37 open / 38 resolved / two superseded. No Java/build behavior changed, no new runtime probe was needed and no Gradle command was run.

**One classification task remains: 76 (390 shared-exception IDs).** Task 77 retains fixture cleanup and tasks 59/64 final reconciliation. Required SPI/native/mock implementation remains downstream work.

## Task 76 completion — 2026-10-29

All **390 assigned exception IDs are classified: 341 required-client / 49 excluded-server / zero unresolved**. All 3,851 frozen API IDs and 2,723 assignment rows are preserved; the fixed assignments total 2,195 required-client / 528 excluded-server. Required implementation statuses and mappings are unchanged. Excluded mappings are retired, including CACHE_EXISTS and TYPE_MISMATCH and explicit native/Spring branches. Required callback/region/transaction work has active owners 30/31/34; other subsystem handoffs are recorded in the exact exception ledger.

The user reported the requested Gradle command passed, including affected compilation, baseline pruning and transitional boundary verification. Local JUnit XML confirms 24 focused tests with zero failures/errors/skips: four SPI translator, six native mapper, two Spring translator and 12 client-only configuration tests. The locator rejection expectation is corrected to IllegalStateException / INVALID_STATE. No Gradle command was run by the agent.

Deterministic coverage generation/check, all 12 Python coverage tests and independent javap validation pass (372 declarations across 21 types). The validator now permits fully retired mappings only for excluded/missing rows; negative tests reject retained, unresolved, partial, implemented or stale SPI claims. Exact-ID/declaration/ownership checks pass. Generated scope is **2,694 required-client / 1,157 excluded-server / zero unresolved**, with **3,125 missing / 471 partial / 255 implemented** across all scopes. The inventory contains 508 Spring exposures, 123 production native imports and 538 source/resource references.

No current source/resource or existing-SPI row links to task 76's 49 exclusions. All 149 existing-SPI references are required-client. Source scopes are 530 required-client / three excluded-server / five unresolved: the remaining CacheServer exclusion references and five unlinked legacy-name/package/logger dispositions stay with parent 64 for final reconciliation. Classification does not establish native-free implementation compliance.

**All 12 API-family classification tasks are resolved. Next: task 77's two legacy test-JAR consumers, then final reconciliation in 64/59.** There are 36 open, 39 resolved and two superseded tasks (77 total).
