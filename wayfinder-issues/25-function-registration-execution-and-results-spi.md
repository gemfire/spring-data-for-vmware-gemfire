Type: task
Status: open
Blocked by: 14, 16, 18, 20, 23, 24, 59

# Client function execution and results SPI

## Client-only scope — 2026-10-22

Apply the [approved client-only boundary](../wayfinder-impl/client-only-scope.md). [Cleanup ticket 59](59-remove-server-only-spi-and-reconcile-client-scope.md) must complete first. Full/complete coverage in this ticket means every retained client requirement; historical native reachability alone does not require peer/server APIs. Preserve all retained client tests and native-free boundaries.

## Question

Support client function invocation and typed results for Spring templates and proxies.

## Required implementation

- Implement client execution on a region, one server or multiple servers using client connections/pools and authenticated views. Include server-group selection where supported by the client API.
- Preserve arguments, filters, execution options, result collectors, timeouts, errors, no-result metadata and result value adaptation. Do not strengthen ordering or retry guarantees.
- Audit function-object submission and associated callbacks against the exact-version client API. Preserve supported client submission forms with typed contracts where required; do not introduce server hosting/registration services or peer onMember/onMembers execution to satisfy broad native reachability.
- Keep server function implementations and their execution contexts/result senders private to native integration fixtures unless specific client submission evidence establishes a required public contract. Record member-level dispositions with ticket 59.
- Implement the [validated client-local callback contracts](../wayfinder-impl/investigations/client-function-member-scope.md#client-local-callback-disposition): typed function bodies and contexts, borrowed client cache/dataset access, invocation metadata and result senders. Both persistent local client shortcuts support object and registered-ID execution; the three non-persistent local shortcuts reject it. Keep local borrowed client handles distinct from server execution contexts. Apply the selected remote representation below; permission hooks are retained through the typed declaration contract below.
- Preserve native function exception collections and explicit unsupported mock remote execution. Native-free Spring orchestration tests must still exercise collectors and error paths without canned claims of real distributed execution.

## Required validation

Task 63's [function audit](../wayfinder-impl/investigations/client-only-spring-surfaces.md#client-function-submission-and-fixture-audit--2026-10-27) identifies pinned native support for unregistered function-object submission on client region and pool/region-service server targets. All three native object-submission tests pass in user-run validation. Define a typed submission contract preserving object state without exposing server registration or arbitrary native unwraps. Preserve the client region-discovery operation currently implemented using `GetRegionsFunction`; native execution bodies and transport belong behind the driver boundary. Task 64 owns prerequisite server fixture relocation before task 59 closes.

Exercise each retained client target, authentication, arguments/filters, result conversion, timeout and error behavior against native servers. Test local collector/orchestration behavior with native-free collaborators and update coverage evidence.

## Completion

The [registry replacement/lifetime review](../wayfinder-impl/investigations/client-function-member-scope.md#registry-replacement-and-lifetime--validated) adds source-backed constraints to the selected representation: inject the collaborator, preserve native registry sharing rather than promise per-connection isolation, retain replacement/lookup identity and detached enumeration, and let native disconnect perform its own cleanup. Releasing a borrowed collaborator must not clear other registrations. Do not introduce a driver-owned static registry or expose remote hosting. The extended native probes pass in user-run validation (six registry tests plus three submission tests, zero failures/errors/skips; 23s build). These constraints do not complete task 64's remaining scope decisions.

Every retained client function contract is implemented and mapped; ticket 32 owns Spring consumer cutover. Record exact native version, validation and any blocking children. Server hosting is not a completion requirement.

## Member-level acceptance from task 64

Implement the 68 required-client function-package rows and the associated internal service type in the [function member ledger](../wayfinder-impl/investigations/client-function-member-scope.md) through typed equivalents. Preserve ID/object and timed submission, identity/result/HA/write metadata, pool/default-pool/authenticated-view target restrictions, region filters and rejection of filters on data-independent server targets. Collector contracts must retain member-tagged results, completion, retry clearing, timed interruption and aggregate failure causes without native member/exception types. Native naming and static factory shapes are not required.

Verify these paths with each native driver and native-free local collector/orchestration tests; retain explicit mock remote-execution rejection. Do not treat local registry lookup as a remote existence check. The representation decision below is settled; parent 59 still blocks this ticket pending the remaining scope reconciliation.

Missing server registration has target-specific native failure paths: the pinned registry probes observe `FunctionException` for a client region and `ServerOperationException` for pool/cache server targets. Cover both in typed failure mapping and retain function identity/cause diagnostics. The corrected five registry probes and three existing object-submission tests all passed in user-run validation on 2026-10-28, with zero failures/errors/skips. These native compatibility probes establish behavior; the typed SPI implementation and its tests remain outstanding.

## Client-local registry acceptance from task 64

The [five registry dispositions](../wayfinder-impl/investigations/client-function-member-scope.md#client-local-registry-disposition) retain registration, object lookup, status, detached enumeration and unregister as client behavior. Implement their typed equivalents without server installation or static SPI service access. Preserve submitted object state/identity, current metadata on reused executors, replacement, native sharing and ordinary disconnect cleanup. Borrowed collaborator release must not remove other callers' registrations. Do not substitute immutable metadata snapshots for retained function objects.

Cover null IDs/functions, invalid HA/result flags, missing entries, native internal-entry filtering and detached map mutation. The six existing native probes validate the recorded happy-path/replacement/disconnect behaviors; they do not yet validate all these acceptance cases or a typed implementation. Mock tests may cover local registration semantics with injected state while rejecting remote execution. Coordinate authenticated target semantics with task 24; do not infer per-view registry isolation or authorization from local registration.

## Client-local callback acceptance from task 64

Preserve the 23 [body/context/sender requirements](../wayfinder-impl/investigations/client-function-member-scope.md#client-local-callback-disposition) through native-free typed equivalents. Keep application arguments/principal separate from native handles; callbacks borrow the executing client cache and region without acquiring ownership. Preserve underlying identity/lifetime, filters, function/member identity, possible-duplicate metadata, intermediate/terminal delivery, exception-valued terminal results and missing-terminal failures. Do not hardcode null principals or false duplicate flags from the initial unauthenticated probes.

The five local native tests, six registry tests and three remote submission tests passed in the user-run 25s build. They establish native behavior, not typed adapter completion. Add shared contract and per-version adapter tests; native-free Spring orchestration can use injected callback collaborators. Mock persistence and remote execution remain explicitly unsupported unless separately implemented and verified. Preserve native rejection on unsupported local shortcuts and retain native-driver persistence/routing tests.

The [service assembly mapping](../wayfinder-impl/investigations/client-function-member-scope.md#client-function-service-assembly) retains the protected constructor and internal service dependency through a constructor-injected typed execution collaborator. Do not copy native inheritance, static singleton assembly or peer operations. Verify retained client behavior through shared contracts and per-version adapters; native static delegation stays inside the adapter. The three permission-hook probes pass in the user-run 31s build alongside all 14 local/registry/submission tests and transitional boundary verification. They establish defaults and remote overload dispatch, region/argument delivery and process separation; they do not establish secured authorization behavior. Permission scope is resolved below; typed remote implementation follows the decision below.

## Function permission declaration acceptance from task 64

Implement both [permission-hook equivalents](../wayfinder-impl/investigations/client-function-member-scope.md#client-function-permission-disposition): DATA:WRITE defaults, region-only delegation, argument-aware override precedence and dynamic declarations on the server-selected function instance. Collaborate with task 24 on native-free permission values and lossless native conversion; do not cache permission lists, pre-authorize on the client or leak native/Shiro types. Keep ID execution tied to server registration and object execution tied to submitted state.

The three validated unsecured probes establish defaults and dispatch only. Acceptance also requires secured per-version allow/deny, all-permissions-required, no-body-on-denial, region/wildcard restrictions, authenticated-user separation and ID-versus-object declaration tests. Native-free Spring tests cover orchestration and typed authorization failures; mock remote/authentication guarantees remain unsupported. Remote body transport implementation belongs to this ticket; retaining permission hooks does not expose server hosting or security management.

### Permission value conversion acceptance

The [57-row value disposition](../wayfinder-impl/investigations/client-permission-values.md#client-permission-value-disposition) is required-client and remains unimplemented under task 24. Wire both hooks to its typed declarations, keeping effective parts separate from component fields. Capture the returned values per evaluation/transport, including a deep copy of parts, and prove a later edit cannot change an already captured value while a later callback can produce a changed declaration. Preserve submitted state and server-selected instance behavior. Exercise parsed wildcard and edited-part declarations through actual native function submission and secured enforcement; native Java serialization alone is insufficient. Mock tests cover local declaration/orchestration semantics without claiming remote authorization.

## Selected remote submission contract and acceptance

Implement task 64's [representation decision](../wayfinder-impl/investigations/client-function-submission-representation.md#representation-decision--2026-10-28). The private feasibility/deployment probes passed 16 tests with zero failures/errors/skips in user-run validation. They establish neither a production artifact nor secured/version compatibility. Preserve the local registry/lifetime and permission requirements above.

- Transport current native-free serializable application state in a private versioned envelope. Preserve local object identity and fresh remote state per execution; permission evaluation and body execution use the same server-selected instance. Keep ID invocation separate, without fallback from failed object submission.
- Provide common arguments/function/member/principal/duplicate/sender context and a region variant carrying path and the native local filter subset. Only a client-local extension borrows typed cache/dataset handles. Reject incompatible local-handle callbacks explicitly. Do not serialize live contexts, senders, connections or service collaborators.
- Provide typed server-region-name discovery for both Spring callers identified in task 32. Keep native `GetRegionsFunction(false)` and `RegionInformation` conversion inside the driver, aggregate names across servers, and distinguish empty success from typed discovery failure.
- Produce a deployable versioned transport artifact with private adapters and declared native-free callback runtime dependencies, separate from Spring and test-fixture packaging. Application bodies/payloads are separately deployed; the server supplies its native runtime. Document the exact native version/SPI ABI tuple and serialization requirements. No public server install or hosting API follows.
- Prove packaged execution in a separate server process without Spring or integration-test output. Cover every retained target, fresh state, registry sharing, callback lifetime, sender terminal/error behavior, native-free values, secured principals/permissions, authenticated views, HA/retry and multiple servers.
- Cover missing envelope, application-only class absence, serialization-policy rejection, nonserializable captured state and incompatible deployed classes. Preserve typed diagnostics and causes; use independent same-ID invocation before/after applicable rejections to prove no fallback. ID execution does not require an unused envelope.

General native server-data bodies stay independently deployed behind ID execution; this SPI does not expose server cache/partition/management/PDX APIs. Task 32 documents that breaking callback boundary and migrates Spring consumers. Task 40 carries packaged deployment and secured tests into each supported driver's matrix. Mock remote execution remains explicitly unsupported. Task 59 remains a blocker; final public signatures and implementation evidence belong here.

## Task 73 client-scope handoff — 2026-10-28

The [task-73 ledger](../wayfinder-impl/investigations/client-statistics-logging-and-utility-scope.md) moves nine missing IDs to this task with consumer owner 32: five Identifiable function-identity declarations and four GetRegionsFunction/RegionInformation discovery declarations. Preserve typed function IDs and ordinary Java helper equality, first-match/null lookup and removal semantics without native Identifiable inheritance.

Complete the existing typed region-name discovery handoff: aggregate across servers, distinguish empty results from failures, and test root/subregion and duplicate-name semantics. The native function, verbose flag, Object[] protocol and CLI result DTO stay private to the versioned driver. Native root enumeration and full-path-derived names must be accounted for; recursive DTO structure alone does not require flattening. Classification adds no implementation credit or server-management SPI.

## Task 75 handoff — 2026-10-28

The [task 75 routing evidence](../wayfinder-impl/investigations/client-partition-membership-and-subscription-scope.md#client-request-routing-contracts) confirms ClientMetadataService uses resolver/context contracts for function routing too. Coordinate typed key/payload adaptation with 48/58 and preserve the FUNCTION_EXECUTION context with null value/callback for that path. Routing metadata and native resolver bridge remain private. The 46 routing/subscription IDs retain owners 48/30; this coordination note adds no dependency cycle or separate scope audit.

## Final reconciliation handoff — task 64

Own the partial FunctionException type row transferred from foundation 16, including aggregate/cause semantics and native-free function integration with 32/36. See the [final scope ledger](../wayfinder-impl/investigations/client-scope-final-reconciliation.md) for exact counts and source dispositions. These transfers do not upgrade implementation status.
