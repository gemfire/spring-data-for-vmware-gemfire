Type: task
Status: open
Blocked by: 14, 16, 18, 59

# Client runtime identity and connectivity SPI

## Client-only scope — 2026-10-22

Apply the [approved client-only boundary](../wayfinder-impl/client-only-scope.md). [Cleanup ticket 59](59-remove-server-only-spi-and-reconcile-client-scope.md) must complete first. Full/complete coverage in this ticket means every retained client requirement; historical native reachability alone does not require peer/server APIs. Preserve all retained client tests and native-free boundaries.

## Question

Provide the runtime metadata and services required by client cache/pool operations, without peer membership administration or cluster hosting.

## Required implementation

- Inventory DistributedSystemUtils, GemfireFunctions, endpoint/locator metadata and internal CLI/JNDI references. Retain only verified client needs; remove incidental server administration through ticket 59.
- Adapt client connectedness, identity, safe properties and server/member metadata received by clients. Reuse retained event identity types; do not equate an open cache with a working server connection.
- Keep native internal adaptation within the driver; expose typed injected collaborators without global registries, native implementation types or credential-bearing diagnostics.
- Exclude cache-server/WAN/queue lifecycle, peer roles/membership management and Region distributed-lock/lock-grantor operations. Their historical coverage rows require explicit scope dispositions under ticket 59, not new implementations.
- Preserve client-visible failure classifications and required endpoint/identity payloads from the exception and callback foundations.

## Required validation

Verify native client connectivity and metadata with private server/locator fixtures, lifecycle failures, diagnostic redaction and explicit mock limitations. Update retained client rows and compile affected consumers.

## Completion

All retained runtime-service rows have typed native/mock/test mappings or blocking children. Record exact native version, evidence and consumer owners; server-only historical rows must already be excluded by ticket 59.
