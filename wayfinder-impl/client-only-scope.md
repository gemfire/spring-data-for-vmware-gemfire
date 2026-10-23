# Client-only SPI scope correction

Approved by the user's instruction on 2026-10-22: “readjust the tasks required for client-only... then create a task that will remove the existing server-side spi work done already to remove those”. This amends the earlier full-native-reachability interpretation of the technical specification. It authorizes the backlog correction and the removal task; this planning change does not claim that removal has happened.

## Required product boundary

`spring-data-vmware-gemfire` is a client library. Its production code depends on SPI/core only, its complete retained client unit suite runs with the official mock and native-free collaborators, and each supported native driver has compatibility and real client/server integration tests. Client operations against servers remain required. Hosting or administering servers through the public SPI is outside this product boundary.

| Keep, subject to verified client semantics | Exclude from public SPI and Spring client configuration |
| --- | --- |
| Client cache lifecycle; `ClientRegionShortcut`, including local variants; client region operations, attributes, callbacks and supported local hierarchy | `RegionShortcut`, peer-cache mode/creation and partitioned/replicated peer region creation |
| Pools, endpoints, durable clients, interest registration, subscription delivery and CQ | Cache-server creation/lifecycle, load probes, server metrics, server-side client-session management and interest-registration observers |
| Local and remote client queries, results and client-local indexing where supported | Remote server index administration or peer-only index operations |
| Client invocation of functions on regions/servers, arguments, filters, collectors and results | Peer member execution and server function hosting/registration infrastructure |
| Client authentication, multiuser views and authorization failures returned to clients | Server authorization hooks, server operation contexts and security management |
| PDX, serialization, client-local disk/overflow/persistence, client-supported expiration/eviction and transactions | WAN gateways, asynchronous queues, conflict resolution, partition placement/resolvers/listeners, rebalance/redundancy administration and distributed lock grantors |
| Identity/endpoint metadata received by clients and client-applicable event payloads | Peer membership/role administration and callbacks that require hosting a peer |
| Snapshot operations demonstrated on client-local regions, if part of the retained client contract | Cluster-wide/partitioned snapshot administration |

Classify operations by where they run and what client applications can use, not by names alone. `InterestResultPolicy` and client pool subscription settings must survive removal of server subscription configuration. A data-policy value used by valid persistent client-local storage must not be removed just because its native name contains `REPLICATE`. Shared exceptions and member metadata remain where clients receive them. Review function-object submission, transaction callbacks, snapshots and native resource settings against exact-version client behavior before deciding their individual members.

Native servers, locators and server functions may exist as private fixtures inside native-driver integration suites. They must not require a public server SPI or reintroduce native artifacts into Spring Data's production/unit-test classpaths. The separate Spring Test module retains its existing exception.

## Backlog disposition

- Tickets 42 and 43 are resolved **as superseded**, not implemented. Their former server/WAN requirements are retired. Ticket 42's unfinished server additions were discarded during the authorized working-tree cleanup; its client `InterestResultPolicy` was retained.
- Ticket 59 removes existing server-only work, including committed foundations and unfinished additions, and reconciles the coverage inventory. Its children 60–64 sequence coverage classification, creation removal, callback/configuration removal, Spring cleanup and final reconciliation. Children 60–62 complete classification infrastructure and SPI creation/callback/configuration removal; child 63 is next. The parent blocks remaining downstream implementation work.
- Tickets 19, 21, 23–25, 27, 29–32, 34, 44, 47–49 and 54 have narrowed client contracts. Ticket 48 now covers client subscription/interest configuration, not partitions or peer membership.
- Tickets 20, 22, 26, 33, 35–38, 40–41, 45, 51–52 and 55–58 retain their client goals, with cleanup and client-only acceptance explicit. Resolved foundations remain historical evidence; ticket 59 owns their server-only corrections.

## Coverage and acceptance

The 3,851-row frozen inventory is historical discovery evidence, not the new client requirement count. Keep stable API IDs and source evidence. Task 60 adds explicit scope dispositions, rationale and decision references: initially 96 required-client, 549 excluded-server and 3,206 unresolved rows. Task 62 updates these to 362 required-client, 625 excluded-server and 2,864 unresolved rows. Task 64 must finish member-level classification before task 59 closes; do not label exclusions `implemented` or `removed` (the latter means a feature removed by native GemFire). Required-client rows retain implementation status, owners and native/mock/test evidence. Ambiguous rows remain required/pending until evidence resolves them.

Update policy, generator, validator and generated artifacts together. Report required-client and excluded counts separately; reject unclassified rows, unexplained exclusions, missing owners and excluded server APIs still exposed by SPI. Scope exclusions do not relax native-free boundary checks.

Retire server-only Spring APIs, annotations, XML and tests with explicit mappings and migration notes in this breaking major migration. Preserve all retained client behavior and test assertions; do not delete client tests to satisfy the boundary gate. Shared mock contracts must not require server hosting, and unsupported remote behavior must remain explicit. Native compatibility/integration coverage remains mandatory for every supported driver version. Apply the existing pre-first-release ABI policy; never disable ABI/stamp checks to conceal removal.
