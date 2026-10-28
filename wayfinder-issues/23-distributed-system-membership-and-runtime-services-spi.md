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

## Task 72 handoff — 2026-10-28

Task 72 retains 145 exact runtime/configuration IDs under this implementation owner, with consumer owner 36. Implement injected client context/cancellation, safe property-file resolution/readback, local statistics/logging (coordinate 73), durable pool-qualified identity (20), and verified client configuration/TLS (20/24/35). Retain legacy cluster TLS for locator connections and server TLS for outbound server connections, redundancy-zone as availability-zone alias, member-timeout for client monitoring, local system ID and groups. Do not expose native registries or thread-local durable aliases; document/test deliberate typed replacements. Locator hosting, peer discovery/rejoin/transport, management configuration DTOs and server security remain excluded. Implementation statuses are unchanged; this handoff does not provide complete SPI/native/mock coverage. See [the exact scope ledger](../wayfinder-impl/investigations/client-runtime-configuration-and-lifecycle-scope.md).

## Task 73 client-scope handoff — 2026-10-28

The [task-73 ledger](../wayfinder-impl/investigations/client-statistics-logging-and-utility-scope.md) retains 212 statistics/logging/message IDs here, with consumer adoption in task 36. Preserve descriptor flags/IDs/ordering, equal/conflicting type creation, XML reader loading, instance identity/lookups, atomicity, typed name/descriptor/ID access, raw double bits and closed-state validation/read/write behavior. Versioned compatibility tests must cover deprecated int-to-long storage, supplier initial evaluation/previous value/replacement, null replacement, exceptions, sampling and close. The pinned supplier implementation and interface wording differ; the ledger records the distinction.

Preserve effective log levels, Throwable/message/argument association, tracing, standard Java Handler bridging, common log destinations and localized message identity/raw/formatted fallback. Keep native loggers/bundles private, use injected ownership and coordinate authentication diagnostics with 24/35. Cover mock behavior explicitly. Nine function identity/discovery IDs move to 25/32; the 38 server exclusions create no implementation obligation here. Classification leaves all retained IDs missing.

## Shared exception handoff — task 76

Retain StatisticsDisabledException plus server-originated RoleException and LeaseExpiredException received by clients. Preserve the outer operation wrapper and deliberate translation with 36; this does not restore peer hosting/role APIs. See the [exact exception scope ledger](../wayfinder-impl/investigations/client-shared-exception-scope.md). Task 76 Java validation and generated inventory reconciliation passed on 2026-10-29. Existing implementation statuses are not upgraded.
