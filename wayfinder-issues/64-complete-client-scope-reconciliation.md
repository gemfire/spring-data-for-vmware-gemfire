Type: task
Status: resolved
Blocked by: 60, 61, 62, 63, 65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76, 77

# Complete client scope reconciliation

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


## Current execution boundary — 2026-10-28

The [bounded audit plan](../wayfinder-impl/investigations/client-scope-audit-plan.md) supersedes this ticket's former open-ended review loop. Children **65–76** own the fixed 2,723 remaining API IDs; child **77** owns the two remaining legacy server test-JAR consumers. This ticket is now the final reconciliation gate. Existing function/transaction decisions and validated probes remain complete.

- [x] Children 65–77 resolved with their fixed scope, exclusion/removal consistency and implementation/consumer handoffs complete.
- [x] Zero unresolved API rows; all frozen IDs/declarations preserved. Reconcile source/resource dispositions and existing SPI mappings, with every retained gap owned by an active implementation ticket.
- [x] Reconcile genuinely new or unlinked findings explicitly; do not silently add them to a child's fixed assignment.
- [x] Complete the existing cleanup validation below, update task 59/map and close this gate. Full retained SPI implementation, native-free Spring cutover and per-version compatibility remain downstream acceptance.

All bounded children 65–77 are resolved. Their evidence and exact assignments remain
in the [audit plan](../wayfinder-impl/investigations/client-scope-audit-plan.md).
The final totals and successful aggregate gate are recorded above; the dated
receipts below preserve earlier checkpoints.

## Question

Resolve every remaining `unresolved` API/source disposition from task 60 using member-level client-callability evidence and the approved scope. Shared exceptions, member metadata, functions, transactions, snapshots, security, native internals and resource settings require individual review. Keep all frozen IDs and original native declarations; exclusions are distinct from implementation status/native removal. Reassign all required rows to active client implementation/consumer tickets.

Review the code/test disposition ledger from tasks 61–63, regenerate scope counts and remove stale active server mandates from plans/policy. Every excluded implemented mapping must either be removed, mapped to retained client semantics with evidence, or private integration infrastructure; no public server API can remain. Unresolved rows block this task and task 59.

## Incoming function audit and fixture placement from task 63

Use the [function audit and fixture inventory](../wayfinder-impl/investigations/client-only-spring-surfaces.md#client-function-submission-and-fixture-audit--2026-10-27) for member-level function dispositions. Client function-object submission is supported by the pinned native source; its three native integration tests pass in user-run validation. Review execution context/result-sender members individually instead of excluding every function-related declaration as server-only.

- [x] Move the 13 inventoried Spring fixture source files (15 server function bodies) and their server packaging/configuration into private native integration infrastructure. Split the current combined Spring `testJar`; preserve client DTO/PDX needs and neutral function IDs without native server implementation dependencies.
- [x] Preserve every retained exception, PDX/argument, collection, no-result, result-conversion and multi-server assertion while moving fixtures. The new single-server object-submission suite does not replace them.
- [x] Resolve the 37 evidenced client execution/metadata/failure/collector/target rows and four peer-target exclusions; link typed implementation and consumer acceptance to tasks 25/32.
- [x] Resolve the final two function-package scope rows: both permission hooks require typed native-free declarations under the [permission disposition](../wayfinder-impl/investigations/client-function-member-scope.md#client-function-permission-disposition). All 72 function-package rows are classified; the representation decision is recorded below. The 23 client-local body/context/sender rows and five registry rows are required-client.
- [x] Validate the five `NativeClientLocalFunctionTests` probes and reconcile callback/context/sender scope with the [client-local evidence](../wayfinder-impl/investigations/client-function-member-scope.md#client-local-function-callbacks--validated). Both persistent local client shortcuts execute bodies locally; the three non-persistent local shortcuts reject them. The 25s user-run build passed all 14 local/registry/submission tests with zero failures/errors/skips and transitional boundary verification. The 23 body/context/sender rows are required-client through typed equivalents; no server cache or peer hosting API follows.

- [x] Validate the five `NativeClientFunctionRegistryTests` probes with the existing three object-submission tests. All eight passed with zero failures/errors/skips in the user-run build (25s), along with transitional migration boundary verification. The [validation record](../wayfinder-impl/investigations/client-function-member-scope.md#registry-compatibility-probes--validated) covers local metadata lookup, separate server registration, ID versus object state and local unregister followed by remote ID execution. Scope decisions remain pending; these probes add no public hosting API.

Complete this prerequisite cleanup here, before closing task 59. Tasks 25/32 are blocked by 59, so assigning them required fixture removal would create a dependency cycle.

- [x] Validate the extended registry suite: five target cases now check replacement, detached enumeration and executor reuse; a sixth checks ordinary client disconnect cleanup while server registration survives. See [replacement and lifetime evidence](../wayfinder-impl/investigations/client-function-member-scope.md#registry-replacement-and-lifetime--validated). The user-run build passed in 23s on 2026-10-28. Local JUnit XML confirms six registry tests plus three submission tests passed with zero failures/errors/skips; transitional migration boundary verification also passed.
- [x] Settle the typed registration/submission representation at the semantic boundary in the [representation decision](../wayfinder-impl/investigations/client-function-submission-representation.md#representation-decision--2026-10-28); task 25 owns implementation and final Java signatures. Preserve native shared state through injected collaborators without adding a driver-owned global map, promising per-view isolation, or clearing other callers' registrations when a borrowed collaborator is released. Registration is local client metadata/object lookup, not server installation or an authorization/existence query.

### Function fixture relocation — validated

The [relocation record](../wayfinder-impl/investigations/client-function-fixture-relocation.md) describes the implemented private driver source set, deployment-only JAR, neutral shared DTO/IDs and server PDX XML. All seven client suites now deploy that payload; existing assertions remain and multi-server DTO field assertions were added. A packaging gate rejects server fixture classes on client classpaths or in the Spring test JAR. Two unrelated legacy test-JAR consumers remain explicitly recorded. The fixture relocation and retained-assertion checkboxes above are complete. Coverage checks pass with unchanged scope counts.

User-run compilation and packaging verification pass, along with all 15 tests in the seven suites (zero failures/errors/skips). Boundary-tool tests pass (19); boundary verification reported one deployment-path identity change and 68 stale entries. Reconciled only the existing initializer's property/JAR text, preserving count and owner. The user then ran pruning and verification successfully: 67 stale entries were removed and the fresh findings match the baseline with zero new or stale identities. Native-source occurrences are 5,103 and static-state occurrences are 163. Task 64 remains open for the remaining member dispositions and infrastructure reconciliation.

## Validation and completion

Run coverage generation/check, independent declaration comparison, migration boundary, all affected full compilations, shared/native client regression suites, mock native-free verification and ABI/stamps. Record exact versions, counts, remaining client migration owners and honest limitations in task 59 and the implementation map. Only then resolve this child and parent; tasks 29–41 still own complete client migration and all-version acceptance. Do not commit without instruction.

## Incoming mixed transaction decision from task 62

The pinned client supports transaction listener registration/identity/cleanup, but the tested **server-backed caching-proxy** commit, rollback and conflict cases emit no transaction listener outcomes. Ordinary client entry events carry transaction IDs. Resolve final client scope for `TransactionListener` outcome callbacks, `TransactionEvent` and their adapters using member-level evidence; synthetic forwarding is only partial implementation evidence. Retire remaining outcome surfaces only if client use cannot be established. See the [task 62 investigation](../wayfinder-impl/investigations/client-only-callbacks-and-configuration.md#retained-client-contracts). Shared failure categories for errors received from servers must be reviewed independently of removed transaction writer registration.

### Client-local transaction evidence — validated

Pinned native source distinguishes locally hosted transaction state from the server-backed client stub: local application-region and empty transactions can dispatch outcome callbacks. The previous negative controls therefore do not justify removing these APIs. See [source evidence and validation cases](../wayfinder-impl/investigations/client-local-transaction-outcomes.md).

- [x] Add five native client-local tests covering commit, rollback, conflict, empty transactions and listener failure/cleanup; verify real event identity, cache and entry payloads without a server fixture.
- [x] Obtain user-run Gradle results for the new tests, existing server-backed transaction controls and migration boundary.
- [x] Resolve the 11 frozen `TransactionListener`/`TransactionEvent` scope rows using both paths and refresh coverage evidence. Keep downstream mock and Spring transaction migration obligations explicit.

User-run validation passed in 26s: five local tests and eleven existing transaction tests, zero failures/errors/skips, plus transitional migration boundary verification. All 11 listener/event rows are required-client and remain partial with active owner 34; native-free test/mock support remains task 37. Current scope: 373 required-client / 625 excluded-server / 2,853 unresolved, all 3,851 IDs preserved. No transaction API was removed. Task 64 and parent 59 remain open for the remaining scope and fixture work.


## Prerequisite receipt — 2026-10-27

Task 63 is resolved; prerequisites 60–63 are complete. Its final surface audit and user-run validation retain client XML and record remaining client native coupling with tasks 30/47 and 31/35/36. Coverage generation/check and independent declaration validation pass with 3,851 frozen IDs, 362 required-client / 625 excluded-server / 2,864 unresolved. Start from the fixture inventory and transaction evidence above; do not treat the transitional boundary pass or task 63 closure as final client-scope/native-free acceptance. Parent 59 remains open.

Transaction scope inventory checks passed: deterministic coverage generation/check, all 10 scope-policy tests, independent javap comparison of 372 declarations across 21 types, ownership/source/ID validation and both inventory negative controls. No Gradle commands were run by the agent.

## Function member scope receipt — 2026-10-28

The [member ledger](../wayfinder-impl/investigations/client-function-member-scope.md) records exact IDs and pinned native source for 41 dispositions. Client ID/object execution, metadata, failures, collectors and five target overloads remain required; four peer member/group targets are excluded. Registry lookup is a local metadata shortcut before remote retrieval, so it remains under review with submission callbacks and permissions. Current scope: 410 required-client / 629 excluded-server / 2,812 unresolved. All 3,851 IDs and implementation statuses remain unchanged. Tasks 25/32 have explicit behavior and test obligations; this task and parent 59 remain open.

Validation for this slice: all 10 scope-policy tests pass; deterministic generation/check passes; javap independently matches 372 public declarations across 21 types. Ownership/source/ID/scope checks and both inventory negative controls pass for all 3,851 rows. No Gradle commands were run.

## Client-local registry scope receipt — 2026-10-28

- [x] Classify the five local registry operations as required-client using pinned source and the validated six registry probes. Record exact frozen IDs, behavior and implementation/consumer obligations in the [registry disposition](../wayfinder-impl/investigations/client-function-member-scope.md#client-local-registry-disposition) and tasks 25/32.

Current scope is 415 required-client / 629 excluded-server / 2,807 unresolved. Function-package scope is 42 required-client / four excluded-server / 26 unresolved. All 3,851 IDs and implementation statuses remain unchanged. The required registry behavior is settled; the typed submission/body/context/sender/permission representation and protected constructor disposition remain open. This receipt does not complete the representation checkbox or tasks 64/59.

Validation: deterministic coverage generation/check, all 10 scope-policy tests, independent javap comparison of 372 declarations across 21 types, and all ownership/source/ID/scope checks including both negative controls pass. No Gradle commands were run; this slice changes scope records and acceptance obligations only.

## Client-local callback scope receipt — 2026-10-28

The [client-local disposition](../wayfinder-impl/investigations/client-function-member-scope.md#client-local-callback-disposition) resolves 23 body/context/sender rows as required-client following the validated persistent-region probes. Tasks 25/32 now require typed local callbacks, borrowed client cache/dataset handles, invocation metadata and result-sender semantics, with explicit native-free orchestration and per-version native acceptance. Native persistence support is not implied for the mock.

Current scope: 438 required-client / 629 excluded-server / 2,784 unresolved. Function scope: 65 required-client / four excluded-server / three unresolved. All 3,851 frozen IDs and implementation statuses remain unchanged. Two permission hooks, the protected service constructor and remote submission representation remain open; tasks 64/59 are not complete.

Validation: the user-run 25s build passed all 14 native tests (five local, six registry, three submission), with zero failures/errors/skips, and transitional migration boundary verification. Deterministic coverage generation/check, all 10 scope-policy tests, independent javap comparison of 372 declarations across 21 types, ownership/source/ID/scope checks and both inventory negative controls pass. No Gradle commands were run by the agent. This scope receipt does not establish typed SPI completion or native-free compliance.

## Service assembly scope receipt — 2026-10-28

- [x] Map the protected `FunctionService` constructor and reachable internal `FunctionExecutionService` type to injected typed client assembly using the [pinned-source evidence](../wayfinder-impl/investigations/client-function-member-scope.md#client-function-service-assembly). Tasks 25/32 own implementation and native-free consumer wiring; no native inheritance, singleton or peer operations are required.
- [x] Validate the three new `NativeClientFunctionPermissionTests` probes with the existing local/registry/submission suites and migration boundary. The user-run 31s build passed all 17 tests with zero failures/errors/skips. The [validation record](../wayfinder-impl/investigations/client-function-member-scope.md#permission-hook-probes--validated) confirms defaults, overload dispatch and remote region/argument observations. The unsecured fixture does not establish authorization enforcement.

Current scope: 440 required-client / 629 excluded-server / 2,782 unresolved. Function-package scope: 66 required-client / four excluded-server / two unresolved; the internal service type is an additional required-client row. All 3,851 frozen IDs and implementation statuses remain unchanged. Permission contracts and remote submission representation still block this slice; tasks 64/59 remain open. User-run Gradle validation passed in 31s on 2026-10-28, including transitional migration boundary verification. Local XML confirms three permission, five local, six registry and three submission tests passed. This does not complete the permission disposition or remote representation decisions.

Inventory validation passes after regeneration: deterministic generation/check, all 10 scope-policy tests, independent javap comparison of 372 declarations across 21 types, ownership/source/ID/scope checks and both negative controls. No Gradle commands were run.

## Permission hook scope receipt — 2026-10-28

Both permission declarations are required-client using the validated 31s native probes and pinned source. The [semantic mapping](../wayfinder-impl/investigations/client-function-member-scope.md#client-function-permission-disposition) preserves defaults, overload delegation, dynamic region/argument input and evaluation on the server-selected function instance. Tasks 24/25 own native-free permission values and secured allow/deny, conjunction, authenticated-view and ID-versus-object tests; task 32 owns native-free consumer cutover. No server security-management API follows.

Current scope: 442 required-client / 629 excluded-server / 2,780 unresolved. All 72 function-package rows are classified (68 required-client / four excluded-server); the internal service type is an additional required-client row. All 3,851 frozen IDs and implementation statuses remain unchanged. Remote submission representation, reachable permission-value member review and the remaining scope families keep tasks 64/59 open. The previous unsecured test receipt does not establish authorization enforcement or typed implementation completion.

Validation for this disposition: deterministic coverage generation/check, all 10 scope-policy tests, independent javap comparison of 372 declarations across 21 types, ownership/source/ID/scope validation and both inventory negative controls pass. `git diff --check` passes. This slice changes scope records and acceptance criteria only; no Gradle command was needed or run.

## Permission value probes — validated, 2026-10-28

The [permission value investigation](../wayfinder-impl/investigations/client-permission-values.md) inventories all 57 reachable value/enum rows and pins native source and GemFire's bundled Shiro implementation. Seven `NativeClientPermissionValueTests` cases cover construction-mode differences, lossy getter/rendering conversions, subpart/equality/implication behavior, mutable inherited aliases, serialization and invalid declarations. No production or public SPI changes are included.

- [x] Obtain user-run validation of the seven value probes, the three existing permission-hook probes and transitional boundary verification using the command in the investigation. Local JUnit XML confirms 10 tests with zero failures/errors/skips (2026-10-28T00:17:57–00:17:59Z); the user confirmed the full command passed.
- [x] Resolve all 57 exact value-family scope rows and assign final typed behavior/acceptance to tasks 24/25/32/35. Review the four inherited parts access/mutation rows and generic Shiro comparison overload individually; native reachability alone does not justify exporting them.

Scope remains 442 required-client / 629 excluded-server / 2,780 unresolved. The first user-run command compiled the probes but failed two case-sensitivity assertions. Pinned GemFire source and bytecode confirm a case-sensitive default in its bundled Shiro implementation. The tests and evidence are corrected, including explicit false-mode coverage; the user-run rerun passed. The prior 31s receipt predates these probes. No final permission representation, secured enforcement or native-free compliance is claimed. Task 64 and parent 59 remain open.

Inventory validation for this addition passes: regenerated evidence manifest, deterministic generation/check, all 10 scope-policy tests, independent javap comparison of 372 declarations across 21 types, ownership/source/ID/scope checks and both negative controls. `git diff --check` passes. Java compilation and the new behavioral tests passed in the user-run command; no Gradle commands were run by the agent.

Failure follow-up: the inventory now resolves inherited Shiro signatures from the pinned `gemfire-shiro` artifact rather than upstream `shiro-core:2.2.1`. This also prevents the evidence generator from suggesting the wrong behavioral source. After the correction, deterministic generation/check, all 10 scope-policy tests, independent javap validation and inventory negative controls pass; all 3,851 IDs and scope counts remain unchanged. `git diff --check` passes. The corrected Java tests and transitional migration boundary passed in the user-run command. The member-level scope reconciliation is recorded in the following receipt.

## Permission value scope receipt — 2026-10-28

All 57 frozen permission-value rows are now required-client under the [exact member ledger](../wayfinder-impl/investigations/client-permission-values.md#frozen-member-ledger). Ten policy decisions cover construction (11), aliases (4), observation (8), comparison (3), enum vocabulary (26), and five individually reviewed inherited methods. The client function declaration path justifies these values; it does not add server security management, Shiro interfaces or native subclass compatibility.

Task 24 owns typed values/editing and versioned conversion, task 25 owns callbacks/transport and secured acceptance, task 32 is the primary consumer owner, and task 35 retains configuration wiring. Local typed editing must preserve effective parts independently of component fields, including native alias effects; explicit freeze/transport captures both list and sets. No callback policy caching or lossy getter/rendering serialization is permitted. Full typed conversion, constructor/enum matrices and secured wire validation remain implementation acceptance, not completed by the seven probes.

Current scope: **499 required-client / 629 excluded-server / 2,723 unresolved**. All 3,851 frozen IDs and implementation totals remain unchanged (3,111 missing / 484 partial / 256 implemented); all 57 value rows remain missing. Remote submission representation and remaining mixed scope families keep task 64 and parent 59 open. Next: settle the native-free remote function submission representation against the retained callback and permission requirements.

Validation for this disposition: deterministic coverage generation/check, all 10 scope-policy tests, independent javap comparison of 372 declarations across 21 types, ownership/source/ID/scope checks and both inventory negative controls pass. `git diff --check` passes. This slice changes policy, inventory and task records only; no Gradle command was needed or run.

## Remote submission envelope probe — validated, 2026-10-28

The [representation investigation](../wayfinder-impl/investigations/client-function-submission-representation.md) traces native object/ID transport and introduces a private JDK-only application callback source set plus a native integration envelope. Three new tests cover five target variants, retained local registration identity, changed-state resubmission, remote permission/body state sharing, region filters and intermediate/terminal results. No public API or production implementation changes.

- [x] Obtain user-run compilation, all three envelope probes and existing submission/registry/permission regression tests using the command in the investigation. The user confirmed success; local JUnit XML confirms 15 tests (three envelope, three submission, six registry and three permission) with zero failures/errors/skips, timestamped 2026-10-28T00:39:55–00:40:11Z. Transitional migration boundary verification also passed in the reported command.
- [x] Settle representation and deployment requirements in the [decision](../wayfinder-impl/investigations/client-function-submission-representation.md#representation-decision--2026-10-28) without exposing server cache handles or losing existing client-local callback contracts; implementation acceptance stays with tasks 25/32/40. Account for native service bodies, class availability, serialization policy, metadata/principal/result adaptation and permission conversion. A shared-classpath test is not production deployment acceptance.

Scope remains 499 required-client / 629 excluded-server / 2,723 unresolved; all implementation statuses remain unchanged. The earlier registration/submission representation checkbox remains open, as do tasks 64/59. No Gradle command was run by the agent.

## Remote submission deployment control — validated, 2026-10-28

The [deployment negative control](../wayfinder-impl/investigations/client-function-submission-representation.md#deployment-negative-control--validated) removes envelope and application callback classes from a private server classpath. It requires object submission to fail while an independently installed same-ID body remains callable by ID before and after the failure. Remote class-absence checks prevent accidental shared-classpath success. The existing fixture constructor retains its default classpath; only this test opts into isolation.

- [x] Obtain user-run compilation and execution of `NativeClientFunctionDeploymentTests` alongside the 15 validated envelope/submission/registry/permission tests and transitional migration boundary verification. The user reported success; local JUnit XML confirms all 16 tests with zero failures/errors/skips (2026-10-28T00:53:43–00:54:01Z).
- [x] Complete the [representation decision](../wayfinder-impl/investigations/client-function-submission-representation.md#representation-decision--2026-10-28), including remote data-access/native-service body disposition and the source-backed context/deployment acceptance table in the investigation. This negative control alone does not settle production packaging, serialization policy, secured principals or version compatibility.

The deployment control confirms missing-envelope failure without falling back to the independent same-ID native body; ID execution succeeds before and after that failure. Scope and implementation counts remain unchanged; task 64 and parent 59 remain open.

Inventory validation passes: deterministic generation/check, all 10 scope-policy tests, independent javap comparison of 372 declarations across 21 types, ownership/source/ID/scope checks and both inventory negative controls. `git diff --check` passes. Java compilation and behavioral validation of this control passed in the user-run command; no Gradle command was run by the agent.


## Function representation decision — 2026-10-28

The representation decision is complete; implementation is not. Use a native-free serializable declaration in a private versioned envelope, with common invocation metadata and region/filter context. Only client-local callbacks expose borrowed typed client cache/dataset handles. Native server-data bodies remain separately deployed and invoked by ID. Object submission must not fall back to ID execution.

The consumer audit identifies both `FunctionGemfireAdminTemplate.getAvailableServerRegions` and `GemfireDataSourcePostProcessor.regionNames` for typed driver-owned discovery. Tasks 25/32 own that operation and migration, including empty-success versus failure behavior. Task 25 owns the deployable transport artifact and context adaptation; task 40 requires isolated packaged deployment, failure controls and secured compatibility for each driver. These requirements are recorded in the linked investigation and downstream tickets.

Evidence remains the validated 16-test transport/deployment receipt and pinned source/consumer audit. This documentation decision adds no Java implementation or new native test result. Scope remains **499 required-client / 629 excluded-server / 2,723 unresolved**; all 3,851 IDs and implementation totals remain unchanged. Tasks 64/59 stay open for remaining mixed-family dispositions and legacy test infrastructure. Downstream blockers remain in force.

Validation for this documentation slice: deterministic coverage generation/check passes; independent `javap` validation matches 372 public declarations across 21 types, with ownership/source/ID/scope checks and both inventory negative controls passing. All 10 scope-policy tests and `git diff --check` pass. No Gradle command was run and no Java source changed in this slice.

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
