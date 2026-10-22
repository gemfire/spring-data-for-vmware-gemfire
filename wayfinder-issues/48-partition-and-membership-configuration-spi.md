Type: task
Status: open
Blocked by: 46, 59

# Client subscription and interest policy configuration SPI

## Client-only scope — 2026-10-22

Apply the [approved client-only boundary](../wayfinder-impl/client-only-scope.md). [Cleanup ticket 59](59-remove-server-only-spi-and-reconcile-client-scope.md) must complete first. Full/complete coverage in this ticket means every retained client requirement; historical native reachability alone does not require peer/server APIs. Preserve all retained client tests and native-free boundaries.

## Question

Implement the configuration values needed for client-region subscriptions and interest registration; this replaces the former partition/membership implementation scope.

## Required implementation

- Model client-applicable SubscriptionAttributes/InterestPolicy and InterestResultPolicy values and defaults, with typed creation/readback where supported.
- Coordinate registration flags, delivery and durable behavior with tickets 45 and 52; avoid a dependency cycle by providing configuration before those operation tickets.
- Preserve required client-visible member metadata through ticket 23. PartitionAttributes, partition resolvers/listeners and entry-operation contexts, peer membership loss/resumption/role policies, partition/rebalance results and StringPrefixPartitionResolver hosting are excluded and owned by cleanup ticket 59.
- Account for every historical row: keep verified client requirements here, assign other client requirements to their actual owner, and record server exclusions with evidence. Do not implement all native constants or factories merely to satisfy the old inventory.

## Required validation

Verify native client configuration defaults/readback and invalid combinations; local mock configuration must not claim subscription delivery. Ticket 45 validates real interest delivery with native server fixtures.

## Completion

Client policy contracts and mappings are complete, server-only rows have reviewed dispositions, and parent ticket 19 retains full client configuration acceptance.
