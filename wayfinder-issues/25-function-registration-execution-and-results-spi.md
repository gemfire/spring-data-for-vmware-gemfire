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
- Preserve native function exception collections and explicit unsupported mock remote execution. Native-free Spring orchestration tests must still exercise collectors and error paths without canned claims of real distributed execution.

## Required validation

Task 63's [function audit](../wayfinder-impl/investigations/client-only-spring-surfaces.md#client-function-submission-and-fixture-audit--2026-10-27) identifies pinned native support for unregistered function-object submission on client region and pool/region-service server targets. All three native object-submission tests pass in user-run validation. Define a typed submission contract preserving object state without exposing server registration or arbitrary native unwraps. Preserve the client region-discovery operation currently implemented using `GetRegionsFunction`; native execution bodies and transport belong behind the driver boundary. Task 64 owns prerequisite server fixture relocation before task 59 closes.

Exercise each retained client target, authentication, arguments/filters, result conversion, timeout and error behavior against native servers. Test local collector/orchestration behavior with native-free collaborators and update coverage evidence.

## Completion

Every retained client function contract is implemented and mapped; ticket 32 owns Spring consumer cutover. Record exact native version, validation and any blocking children. Server hosting is not a completion requirement.

## Member-level acceptance from task 64

Implement the 37 required-client rows in the [function member ledger](../wayfinder-impl/investigations/client-function-member-scope.md) through typed equivalents. Preserve ID/object and timed submission, identity/result/HA/write metadata, pool/default-pool/authenticated-view target restrictions, region filters and rejection of filters on data-independent server targets. Collector contracts must retain member-tagged results, completion, retry clearing, timed interruption and aggregate failure causes without native member/exception types. Native naming and static factory shapes are not required.

Verify these paths with each native driver and native-free local collector/orchestration tests; retain explicit mock remote-execution rejection. Do not treat local registry lookup as a remote existence check. Task 64 must settle the remaining 31 callback/permission/registry/constructor dispositions before parent 59 closes; this ticket does not bypass that prerequisite.

Missing server registration has target-specific native failure paths: the pinned registry probes observe `FunctionException` for a client region and `ServerOperationException` for pool/cache server targets. Cover both in typed failure mapping and retain function identity/cause diagnostics. The corrected five registry probes and three existing object-submission tests all passed in user-run validation on 2026-10-28, with zero failures/errors/skips. These native compatibility probes establish behavior; the typed SPI implementation and its tests remain outstanding.
