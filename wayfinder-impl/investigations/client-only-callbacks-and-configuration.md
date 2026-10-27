# Client-only callbacks and configuration

Task 62 removes public peer/server contracts against GemFire **10.4.0-build.3056.df98c835**, Java 17 and Gradle 9.7.0. This is an intentional pre-first-release SPI change. Native server processes remain private integration infrastructure. Spring server configuration retirement is task 63; complete member-level scope reconciliation is task 64.

## Removed contracts

| Surface | Evidence and disposition |
| --- | --- |
| Region membership/role listeners and adapters, role events and member roles | Peer membership and role delivery require peer participation. Remove SPI types, native specialized adapters, mock rejection branches and peer-only tests. Ordinary client member identity remains. |
| Timestamped entry events | WAN conflict payloads are outside client event delivery. Remove the SPI subtype and native timestamp wrapper; preserve ordinary entry events and lazy serialized values. |
| Gateway sender/async queue IDs and mutators, lock-grantor, multicast, early-ack, publisher and subscription-conflation options | Remove public configuration/attribute/mutator declarations and implementations. Client interest policy and pool subscription settings remain. No public distributed-lock or partition-hosting operation was present to remove; classify corresponding frozen native members as excluded. |
| Region creation data policy/scope | Client shortcuts control these attributes. Remove arbitrary creation setters; retain read-only metadata. Persistent client-local regions can report `PERSISTENT_REPLICATE`. |
| Creation ignore-JTA/index-maintenance options | The pinned `ClientRegionFactory` has neither setter. Remove unsupported creation options; retain native attribute getters. This does not exclude all JTA or client-local indexing work. |
| Loader helper network search | Native `LoaderHelperImpl.netSearch` rejects local scope; client shortcuts use local scope. Remove the method, adapters, synthetic helper and forwarding-only tests. Client loader key/region/argument helpers remain. |
| Transaction writer/getter and writer exception | Pinned `TXManagerImpl.setWriter` checks client mode first and throws `IllegalStateException`, including for null. A direct native control test preserves this evidence. Remove public writer hooks, adapters and peer writer-veto tests. Failure categories received from servers remain separate from writer registration. |
| Distributed transaction mode switch/query | A successful `setDistributed(true)` is insufficient: a real client write followed by commit throws `UnsupportedOperationInTransactionException` because the coordinator receives `DistClientTXStateStub`. Remove both SPI mode methods; retain this native negative control and ordinary client transactions. |

## Retained client contracts

`NativeCallbackCoverageTests` verifies all **12** ordinary native cache-listener methods (nine event callbacks plus close/init/initialize), including inherited hooks, with a client cache. Client loader/writer declarative hooks and lazy serialized entry payloads retain explicit forwarding tests. Synthetic callback invocation proves adaptation only; actual subscribed delivery has separate integration coverage.

`NativeDistributedCallbackTests.subscribedClientReceivesRemoteEntryValuesAndMemberMetadata` subscribes a real client to a separate server and verifies remote create/update values, member identity and callback failures. Serialized values are asserted when the native event makes them available; a synthetic native event verifies lazy access and failure propagation independently. Existing region-live, proxy/routing, socket, PDX and client transaction cases remain.

All 50 operation constants, ordinals and predicates remain descriptive event metadata. Scope and data-policy enums describe attributes without enabling peer creation. Member identity and durable-client metadata remain client requirements; broader metadata implementation gaps retain their owners.

`NativeTransactionTests` now uses a client caching proxy against a separate server. Commit, rollback, concurrent-write conflict, thread association, timed resume, ID serialization, entry-event transaction identity and error classification pass. Listener registration, duplicate/replacement semantics, native identity and cleanup also work on the client.

**Server-backed caching-proxy transactions do not notify transaction listeners in the tested commit, rollback and conflict scenarios.** Tests explicitly assert zero outcome notifications while verifying region results and entry IDs. Synthetic transaction-event forwarding is recorded as partial evidence. Task 64 subsequently established required-client scope for outcome callbacks and `TransactionEvent` using local transactions; registration alone did not establish outcome delivery. Mock transactions remain explicitly unsupported, with implementation/simulation owned by downstream client tasks.

Task 64 correction: this result does not establish silence for client-local or empty transactions. Pinned native source uses a different transaction-state implementation for those paths. The [client-local investigation](client-local-transaction-outcomes.md) records five passing local tests alongside eleven passing existing transaction tests. All 11 listener/event rows are now required-client; retain the outcome/event APIs.

## Resolved private fixture ledger

This resolves every entry in the [task 61 ledger](client-only-creation.md#private-peer-fixture-ledger--removal-owner-task-62).

| Previous fixture | Task 62 disposition |
| --- | --- |
| Eleven peer-backed `NativeTransactionTests` methods | Ten client-backed/control tests replace them. Retire writer veto with the removed hook; preserve actual commit/rollback/conflict/suspension/identity/registration/error checks. Replace peer outcome and listener-failure assertions with explicit client notification silence and cleanup. Native distributed-mode rejection is tested at commit. |
| Specialized callback reflection test | Retain all 12 ordinary listener methods against a client; retire six membership/role methods with their SPI interfaces. |
| Serialized/timestamped entry view test | Retain lazy serialized values, metadata, availability and failures; retire WAN timestamps. |
| Real transaction event identity test | Verify transaction IDs and member metadata from real client entry events; do not claim client transaction-event notifications. |
| Writer/loader initialization test | Run on a local client region using mutator-installed callbacks; preserve both declarative hooks without adding automatic initialization. |
| Remote peer membership/role test | Replace with subscribed client remote entry delivery; retire peer join/departure/role assertions. |
| `CallbackPeerProcess` / `NativeServerFixture` | Delete the peer process and locator/role/join/crash-region command protocol. Use `NativeServerProcess` with an ephemeral server endpoint and PUT/STOP commands only. Forced process termination remains failure cleanup. Private servers continue supporting shared client contracts, PDX, proxy, subscriptions and transactions. |
| `GemFireCacheLoaderTests` | Retire two network-search forwarding tests with the deleted method. Shared/native client loader tests remain. |
| Mock/configuration/shared probes and Spring callable helper | Remove probes and assertions for deleted server contracts; retain client-local callbacks, ownership, supported attributes and unsupported-feature checks. Remove the obsolete Spring helper network-search implementation/test. |

## Coverage and validation

Keep all **3,851** frozen native IDs. Clear **136** stale implementation mappings and remove **12** deleted current SPI entries. Newly classify **266 required-client** and **76 excluded-server** members. Exclusions preserve native discovery history and do not mean native removal or implementation completion. Outcome callback/event mappings remain partial pending task 64.

The full Gradle invocation below passed. Its JUnit reports contain **510 executions**, zero failures/errors/skips: SPI 23, core 31, contract-kit 6, mock 54, native integration 92, focused Spring cache mock/native 152 each. These are focused Spring suites and full affected source compilation, not complete Spring native-free acceptance. The transitional boundary remains in force and no released SPI baseline exists.

```sh
./gradlew --offline -DgemfireVersion=10.4.0-build.3056.df98c835 \
  :spring-data-gemfire-driver-spi:check \
  :spring-data-gemfire-driver-core:check \
  :spring-data-gemfire-driver-contract-tests:check \
  :spring-data-gemfire-driver-mock:check \
  :spring-data-gemfire-driver-10.4:check \
  :spring-data-vmware-gemfire:spiCacheTest \
  :spring-data-vmware-gemfire:spiCacheIntegrationTest \
  :spring-data-vmware-gemfire:compileJava \
  :spring-data-vmware-gemfire:compileTestJava \
  :spring-data-vmware-gemfire:compileIntegrationTestJava \
  :spring-test-vmware-gemfire:compileJava \
  :spring-test-vmware-gemfire:compileTestJava \
  :verifyMigrationBoundary :verifyDriverCompatibility
python3 wayfinder-impl/audits/scan_native_references.py
python3 -m unittest discover -s wayfinder-impl/coverage -p 'test_*.py' -v
python3 wayfinder-impl/coverage/build_api_coverage.py
python3 wayfinder-impl/coverage/build_api_coverage.py --check
python3 wayfinder-impl/coverage/validate_coverage.py --javap
git diff --check
```

Final coverage: **362 required-client / 625 excluded-server / 2,864 unresolved**; implementation statuses across all scopes are **3,111 missing / 484 partial / 256 implemented**. Current SPI inventory is **123 defaults and 26 callback declarations**. Ten policy tests pass after regeneration; independent `javap` matches 372 public declarations across 21 types. Mock native-free verification, 32 ABI/stamp fixtures and 19 boundary fixtures pass. Spring native references remain 182 production / 122 unit / 128 integration files. Task 59 remains open.
