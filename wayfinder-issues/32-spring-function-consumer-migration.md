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

Run Spring client proxy/template/configuration tests with native-free collaborators and real client function integration with each native driver. Verify filters, collectors, timeout/interruption, missing targets and typed no-result/error behavior.

## Completion

All retained Spring client function consumers and tests are native-free. Record changed APIs, removed server surfaces, exact validation and limitations.
