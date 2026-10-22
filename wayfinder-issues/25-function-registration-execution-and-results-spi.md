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

Exercise each retained client target, authentication, arguments/filters, result conversion, timeout and error behavior against native servers. Test local collector/orchestration behavior with native-free collaborators and update coverage evidence.

## Completion

Every retained client function contract is implemented and mapped; ticket 32 owns Spring consumer cutover. Record exact native version, validation and any blocking children. Server hosting is not a completion requirement.
