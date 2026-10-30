Type: task
Status: resolved
Blocked by: 46, 59

# Client routing and subscription policy configuration SPI

## Client-only scope — 2026-10-22

Apply the [approved client-only boundary](../wayfinder-impl/client-only-scope.md). [Cleanup ticket 59](59-remove-server-only-spi-and-reconcile-client-scope.md) must complete first. Full/complete coverage in this ticket means every retained client requirement; historical native reachability alone does not require peer/server APIs. Preserve all retained client tests and native-free boundaries.

## Question

Implement native-free client request routing and the configuration values needed for client-region subscriptions and interest registration. Peer partition hosting and role-reliability membership remain excluded.

## Required implementation

- Model client-applicable SubscriptionAttributes/InterestPolicy and InterestResultPolicy values and defaults, with typed creation/readback where supported.
- Coordinate registration flags, delivery and durable behavior with tickets 45 and 52; avoid a dependency cycle by providing configuration before those operation tickets.
- Preserve required client-visible member metadata through ticket 23. PartitionAttributes/factories, hosting listeners, peer membership loss/resumption/role policies and partition/rebalance results are excluded. PartitionResolver, EntryOperation and StringPrefixPartitionResolver remain required for client request routing, as established by task 75.
- Account for every historical row: keep verified client requirements here, assign other client requirements to their actual owner, and record server exclusions with evidence. Do not implement all native constants or factories merely to satisfy the old inventory.

## Required validation

Verify native client configuration defaults/readback and invalid combinations; local mock configuration must not claim subscription delivery. Ticket 45 validates real interest delivery with native server fixtures.

## Completion

Client policy contracts and mappings are complete, server-only rows have reviewed dispositions, and parent ticket 19 retains full client configuration acceptance.

## Task 75 handoff — 2026-10-28

The [exact member ledger](../wayfinder-impl/investigations/client-partition-membership-and-subscription-scope.md) retains **46 IDs**: 23 subscription-policy/attribute values, 13 resolver/context contracts and 10 string-prefix resolver declarations. All remain missing; implementation stays here and adoption stays with 30. This corrects the earlier blanket resolver/context exclusion without restoring PartitionAttributes or peer hosting APIs.

Define a typed client routing callback/context and private native bridge. Native ClientMetadataService selects an advisor resolver before key and callback-argument resolvers, rejects null routing results and uses native bucket metadata. ClientPartitionAdvisor reflectively constructs the server-advertised resolver class; design the native-free representation for this path and key/callback implementations explicitly, coordinating payload conversion with 58 and function routing with 25. Preserve region/operation/key/value/callback availability, inherited close/init/initialize hooks, and string-prefix first-delimiter behavior and errors. Do not claim reflective metadata construction invokes XML initialization hooks. Native class identity, bucket metadata and native context objects stay private.

SubscriptionAttributes() defaults to CACHE_CONTENT; ClientRegionFactoryImpl explicitly installs ALL. Preserve supported creation/readback paths and this distinction. ALL does not register interest or guarantee event delivery. Keep ordinal serialization/canonical values and nullable invalid-state behavior inside explicit conversion/compatibility handling, coordinating 55/58. Native ClientRegionFactory has no public subscription setter; do not fabricate a supported override path.

Add shared typed-value/routing tests and versioned native compatibility tests for selection precedence, metadata construction, context/payload conversion, hook invocation, string keys (empty/missing prefix, non-String/null), defaults and failures. Mock routing/value support must be explicit and must not imply network subscription delivery. Task 45 owns actual interest delivery and 52 durability. The 112 excluded hosting/peer IDs have no current public mapping to retire; this audit is complete, implementation remains open.

## Answer

Resolved 2026-10-30. Only task 48 is resolved; adoption stays with task 30, interest delivery with 45, durability with 52 and function routing with 25.

- **SPI**: `config.InterestPolicy` (`ALL`, `CACHE_CONTENT`, default `CACHE_CONTENT`), `config.SubscriptionAttributes` (value class, bare constructor is `CACHE_CONTENT`), `DriverEntryOperation`, `DriverPartitionResolver` (default `close()`) and `StringPrefixPartitionResolver` (first-delimiter routing, native errors preserved), plus `DriverRegionAttributes#getSubscriptionAttributes`.
- **Native adapter**: `GemFirePartitionResolver` bridges the SPI resolver to the native callback and keeps `EntryOperation` private. `GemFireRegionAttributes` converts the subscription policy by name, not native ordinal. Client regions report `ALL`, matching `ClientRegionFactoryImpl`.
- **Mock**: `MockRegionAttributes` reports `ALL` and does not simulate interest registration or delivery.
- **Coverage**: 46 retained rows are upgraded (39 `implemented`, 7 `partial`). The partial rows are `InterestPolicy#ordinal`/`#fromOrdinal` and the four `SubscriptionAttributes` serialization methods (native ABI, private to the adapter) and `AttributesFactory#setSubscriptionAttributes` (native client factories have no public setter). `existing_spi` gains `DriverPartitionResolver#close()V`. The 112 hosting/peer exclusions are unchanged.
- **Validation (user-run, passed)**: `./gradlew :spring-data-gemfire-driver-spi:test --tests '*StringPrefixPartitionResolverTests' :spring-data-gemfire-driver-10.4:integrationTest --tests '*NativePartitionResolverTests' verifyDriverCompatibility`, plus the earlier `verifyMigrationBoundary` run. Coverage tooling (run directly, passed): `build_api_coverage.py` (3851 rows, unresolved 0), `validate_coverage.py` and the 16 coverage unit tests.
- Known limits: `getName()` reports the implementing class, so SPI and native resolvers differ by design. No commit was created.
