Type: task
Status: open
Blocked by: 25, 59

# Spring client function consumer migration

## Client-only scope — 2026-10-22

Apply the [approved client-only boundary](../wayfinder-impl/client-only-scope.md). [Cleanup ticket 59](59-remove-server-only-spi-and-reconcile-client-scope.md) must complete first. Full/complete coverage in this ticket means every retained client requirement; historical native reachability alone does not require peer/server APIs. Preserve all retained client tests and native-free boundaries.

## Question

Retarget client function templates, proxies and configuration to ticket 25's client execution SPI.

## Required implementation

- Migrate retained region/server/server-group execution templates, function proxies, arguments, filters, collectors and return conversion to typed injected pool/connection collaborators.
- Migrate client annotation/XML/configurer wiring and error translation; remove native PoolResolver and static native service lookups.
- Coordinate retirement of peer execution targets and server function registration/hosting configuration with ticket 59. Preserve any function-object submission proven necessary by ticket 25; do not preserve server APIs merely because legacy types expose them.
- Remove native-backed public overloads and update retained client schema declarations and tests. Keep server function bodies private to native driver integration fixtures.

## Required validation

Task 63's [function audit](../wayfinder-impl/investigations/client-only-spring-surfaces.md#client-function-submission-and-fixture-audit--2026-10-27) found that all three `GemfireOnRegionOperations` function-object/filter defaults discard the object and delegate by ID. Repair this during cutover and verify an unregistered stateful object plus filter through iterable, extracted and no-result forms. Preserve `FunctionGemfireAdminTemplate` client region discovery with driver-owned execution and typed results. Replace native execution callbacks and collector/error adaptation without losing their client behavior. Server fixture relocation is task 64's prerequisite responsibility, not deferred to this blocked ticket.

Run Spring client proxy/template/configuration tests with native-free collaborators and real client function integration with each native driver. Verify filters, collectors, timeout/interruption, missing targets and typed no-result/error behavior.

## Completion

All retained Spring client function consumers and tests are native-free. Record changed APIs, removed server surfaces, exact validation and limitations.

## Member-level acceptance from task 64

Use the [function member ledger](../wayfinder-impl/investigations/client-function-member-scope.md) when replacing native execution/collector/error dependencies. Cover target restrictions, typed member/result delivery, completion/retry clearing and aggregated causes in native-free Spring tests. Replace message-based no-result inference with typed metadata/behavior while preserving timeout/interruption translation. Keep all retained real-server suites, including object/filter submission and driver-owned region discovery. Task 64 retains unresolved registry and submission callback decisions; this migration does not introduce a public server registry.

The five native registry probes and three object-submission tests passed in user-run validation on 2026-10-28. Missing registration produces native `FunctionException` for region execution and `ServerOperationException` for pool/cache server targets. Spring consumers must use task 25's typed failure mapping for both paths and preserve function identity/cause diagnostics; native exception classes must not enter the Spring contract.
