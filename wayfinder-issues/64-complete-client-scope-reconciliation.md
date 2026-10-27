Type: task
Status: open
Blocked by: 60, 61, 62, 63

# Complete client scope reconciliation

## Question

Resolve every remaining `unresolved` API/source disposition from task 60 using member-level client-callability evidence and the approved scope. Shared exceptions, member metadata, functions, transactions, snapshots, security, native internals and resource settings require individual review. Keep all frozen IDs and original native declarations; exclusions are distinct from implementation status/native removal. Reassign all required rows to active client implementation/consumer tickets.

Review the code/test disposition ledger from tasks 61–63, regenerate scope counts and remove stale active server mandates from plans/policy. Every excluded implemented mapping must either be removed, mapped to retained client semantics with evidence, or private integration infrastructure; no public server API can remain. Unresolved rows block this task and task 59.

## Incoming function audit and fixture placement from task 63

Use the [function audit and fixture inventory](../wayfinder-impl/investigations/client-only-spring-surfaces.md#client-function-submission-and-fixture-audit--2026-10-27) for member-level function dispositions. Client function-object submission is supported by the pinned native source; its three native integration tests pass in user-run validation. Review execution context/result-sender members individually instead of excluding every function-related declaration as server-only.

- [x] Move the 13 inventoried Spring fixture source files (15 server function bodies) and their server packaging/configuration into private native integration infrastructure. Split the current combined Spring `testJar`; preserve client DTO/PDX needs and neutral function IDs without native server implementation dependencies.
- [x] Preserve every retained exception, PDX/argument, collection, no-result, result-conversion and multi-server assertion while moving fixtures. The new single-server object-submission suite does not replace them.
- [x] Resolve the 37 evidenced client execution/metadata/failure/collector/target rows and four peer-target exclusions; link typed implementation and consumer acceptance to tasks 25/32.
- [ ] Resolve the remaining 31 function body/permission/context/sender/registry/constructor rows. Keep hosting infrastructure private and audit local registry metadata shortcuts separately.
- [x] Validate the five `NativeClientFunctionRegistryTests` probes with the existing three object-submission tests. All eight passed with zero failures/errors/skips in the user-run build (25s), along with transitional migration boundary verification. The [validation record](../wayfinder-impl/investigations/client-function-member-scope.md#registry-compatibility-probes--validated) covers local metadata lookup, separate server registration, ID versus object state and local unregister followed by remote ID execution. Scope decisions remain pending; these probes add no public hosting API.

Complete this prerequisite cleanup here, before closing task 59. Tasks 25/32 are blocked by 59, so assigning them required fixture removal would create a dependency cycle.

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
