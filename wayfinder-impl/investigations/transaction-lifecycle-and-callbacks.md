# Transaction lifecycle and callbacks

Implementation evidence for [Transaction lifecycle and callbacks SPI](../../wayfinder-issues/28-transaction-lifecycle-and-callbacks-spi.md), against GemFire **10.4.0-build.3056.df98c835**.

## Contracts and ownership

`DriverConnection.getTransactionManager()` returns a cache-owned service. `DriverTransactionManager` covers all 21 native `CacheTransactionManager` operations: begin/commit/rollback; current/identified existence; ID lookup; suspend/resume/immediate and timed try-resume; suspended status; single/list listener inspection, replacement, addition, removal and initialization; writer inspection/replacement; and distributed-mode inspection/change. Each operation is a typed-unsupported default for older providers. The accessor and two ID codec operations bring the added defaults to 24, and the total to 131.

`DriverTransactionListener` exposes all three outcome callbacks; its adapter supplies no-op implementations. `DriverTransactionWriter` exposes `beforeCommit` with a checked `DriverTransactionWriterException`. Both inherit the separate declarable initialization hooks and close callback. `DriverTransactionEvent` supplies a typed ID, lazy list of typed cache events and borrowed connection. Existing entry events now use the same native transaction-ID wrapper, with native equality/hash/member identity preserved.

Native `TransactionId` externalization maps to manager `writeTransactionId`/`readTransactionId`. Reading returns an opaque immutable identity wrapper instead of mutating a public SPI handle. The exact-version driver delegates to native `writeExternal` and native `TXId.readExternal`; checked IO failures propagate. This is the bound engine's format, not a new cross-version format. Native implementation classes remain inside the driver, and foreign-provider IDs fail with a typed invalid-argument error. No public unwrap, registry, mutable static state or stronger transaction guarantee is added.

All 66 original transaction-owned rows and three inherited cache transaction getters have mappings. These 69 rows remain partial because Spring adoption and reachable cache/member/serialization contracts are tracked separately. Exception type ownership stays with the exception foundation. Constructor overloads for runtime native exceptions map to the SPI kind/message/cause constructor; the checked writer exception has its own two constructors. Remaining inventory: **2,921 missing / 650 partial / 280 implemented**, with 3,851 frozen rows across 292 native types unchanged. The existing-SPI comparison contains 131 defaults and 34 callback declarations.

## Native behavior preserved

- Real local-region transactions commit and roll back values and deliver outcome events. Entry and transaction callbacks expose equal IDs. GemFire clears the current thread's transaction association before invoking the writer; the event still carries the committing transaction ID.
- A staged write followed by a concurrent ordinary write produces a real commit conflict, retains the winning value and delivers failed-commit notification. Checked writer vetoes and ordinary runtime failures reject actual commits. Typed unsupported callback failures preserve their SPI exception identity.
- The engine wraps writer vetoes in `CommitConflictException` with a `TransactionWriterException` cause. The boundary recognizes only that specific wrapper, preserving veto versus ordinary conflict. Incomplete commit, synchronization failure and general transaction failure retain distinct typed categories. Incomplete/synchronization cases use injected native failures to verify mapping; they are not evidence of distributed fault recovery.
- Suspended IDs move between threads. Immediate/zero-timeout resume fails while another thread owns the transaction; a bounded timed resume succeeds after release. Timed resume waits for suspension and does not configure transaction lifetime. Spring transaction timeout handling remains with the consumer migration.
- Listener identity, native equality-based deduplication, duplicate initialization, replacement/removal close behavior and multiple-listener access errors follow the engine. Initialization hooks remain separate. Callback adapters also handle callbacks already installed directly in the engine, preserving native identity when round-tripped through SPI.
- Native writer replacement does not close the old writer. Cache shutdown closes listeners but the transaction manager does not close its writer; the adapter does not invent additional ownership. Application lifecycle code remains responsible for explicitly owned writers. Listener exceptions after commit retain the native engine's behavior and do not roll back committed data.
- Distributed-mode setting and active-transaction restrictions delegate to the engine. Client writer registration is rejected by the engine. A real separate-process cache server validates client commit, suspend/resume and rollback over a proxy region. This does not claim multi-peer distributed atomicity or network-failure recovery coverage.

## Mock and downstream acceptance

The mock returns an instance-owned transaction manager, but **all 23 manager operations are explicitly unsupported**, including listener/writer registration, status queries and ID codecs. It does not store callbacks or pretend that ordinary region writes can be rolled back. Rejected registration never acquires callback ownership. Contract probes exercise each unsupported method independently; a mock-specific test verifies that ordinary writes remain ordinary writes.

[Spring transaction consumer migration](../../wayfinder-issues/34-spring-transaction-consumer-migration.md) still owns Spring manager/holder/event/configuration adoption, timeout/propagation semantics and phase-specific exceptions. [Native-free Spring fixtures](../../wayfinder-issues/37-native-free-spring-test-fixtures-and-helper-decoupling.md) must retain unit coverage through SPI fixtures and the official mock, implementing additional local mock behavior if required by preserved tests. No transaction test may be deleted, skipped or converted from a success assertion to an unsupported assertion merely to finish migration. Native transaction storage semantics remain in provider integration suites.

## Validation

**513 tests passed with zero failures, errors or skips:** SPI 24, core 24, contract kit 6, mock 58, native 95, Spring mock 153, Spring native 153. The focused transaction run completed successfully in 40s (`/private/tmp/sdgf-transaction-tests-3.log`). The full check completed in 44s (`/private/tmp/sdgf-transaction-full.log`), including full Spring Data main/unit/integration and Spring Test main/unit compilation and normal migration-boundary verification. Up-to-date suites retain their successful XML results. All 18 boundary fixtures passed. Coverage generation/check, independent javap matching of 148 declarations across 13 types, ownership/ID/source checks and both negative controls passed.

The native suite adds 11 transaction cases plus one real client/server case. Shared probes exercise all 24 added defaults against both providers. Two SPI tests cover older providers and the checked failure contract; a mock test covers explicit rejection. Existing callback, region, pool, PDX and Spring ownership tests remain green.

```sh
./gradlew :spring-data-gemfire-driver-spi:check :spring-data-gemfire-driver-core:check :spring-data-gemfire-driver-contract-tests:check :spring-data-gemfire-driver-mock:check :spring-data-gemfire-driver-10.4:check :spring-data-vmware-gemfire:spiCacheTest :spring-data-vmware-gemfire:spiCacheIntegrationTest :spring-data-vmware-gemfire:compileJava :spring-data-vmware-gemfire:compileTestJava :spring-data-vmware-gemfire:compileIntegrationTestJava :spring-test-vmware-gemfire:compileJava :spring-test-vmware-gemfire:compileTestJava :verifyMigrationBoundary -DgemfireVersion=10.4.0-build.3056.df98c835 --offline --console=plain
python3 build-tools/migration-boundary/test_boundary.py --java /Users/uk016635/.sdkman/candidates/java/17.0.20+1.1-librca/bin/java --classes build/migration-boundary-classes
python3 wayfinder-impl/audits/scan_native_references.py
python3 wayfinder-impl/coverage/build_api_coverage.py
python3 wayfinder-impl/coverage/build_api_coverage.py --check
python3 wayfinder-impl/coverage/validate_coverage.py --javap
```

Validation uses Gradle 9.8.1, Java 17.0.20 and the pinned native core artifact under SHA-1 cache directory `747633c557af8a845af3cac42c0efac8297661d5`. User Gradle configuration edits are preserved. Current Spring native counts remain **182 main / 122 unit / 128 integration files**. Normal boundary success does not establish strict native-free compliance, complete Spring transaction migration or complete mock transaction support.
