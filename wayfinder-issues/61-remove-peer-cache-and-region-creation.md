Type: task
Status: resolved
Blocked by: 60

# Remove peer cache and region creation

## Question

Implement task 59's creation cleanup: remove public SPI `RegionShortcut`, peer cache mode/creation and peer region/subregion factories, together with native/mock implementations. Preserve `ClientRegionShortcut` and all client-local variants, ownership, hierarchy, constraints, statistics and callback lifecycle assertions. Update exact scope/implementation mappings without deleting frozen rows.

Convert shared contracts and mock tests to client creation. Native client PDX schema/enum tests need a real server and pool; reuse private integration fixtures. Install client loaders/writers through supported mutation when native client factories do not support creation-time registration. Do not merely replace shortcut names or suppress failing assertions. Record any temporarily retained private peer fixture and its removal owner in task 62; it cannot establish client behavior.

## Validation

Full affected source-set compilation, shared mock/native client contracts, native PDX and client region tests, mock native-free classpath, ABI/stamps, coverage regeneration/validation and migration boundary. Record native version and test dispositions. Keep task 59 open for callbacks/configuration, Spring cleanup and final scope review. Do not commit without instruction.

## Answer — 2026-10-23

Removed `CacheConfiguration.Mode`, public `RegionShortcut`, three connection peer-region factories and the peer subregion factory, together with native/mock implementations. `CacheConfiguration` now takes only properties; the native driver connects through `ClientCacheFactory`. Client shortcuts and configured client/subregion creation remain. Java callers and four Spring XML fixtures use the new constructor.

Shared contracts and mock tests now create clients. Native client tests retain ownership, hierarchy, constraints, statistics and lifecycle assertions, cover all five LOCAL shortcut variants against native control attributes, and use real server/pool infrastructure for PDX schemas/enums. Client loader/writer registration uses supported attribute mutation. The mock supports LOCAL and explicitly rejects unsupported shortcuts and transactions.

The [implementation evidence and test disposition ledger](../wayfinder-impl/investigations/client-only-creation.md) records retired peer probes, retained client assertions and every temporarily retained private peer fixture. Task 62 owns their migration/removal; they are not client support evidence. Task 63 owns Spring native server configuration surfaces. Task 59 remains open.

Coverage preserves all 3,851 native IDs and independent scope counts (96 required-client, 549 excluded-server, 3,206 unresolved). Four deleted defaults leave 127 SPI defaults and 34 callback methods. Fifty-four stale partial mappings are cleared with explicit evidence, giving 2,975 missing / 596 partial / 280 implemented. The native attribute-copy subregion overload remains unresolved under task 64; client shortcut/configuration creation does not prove its equivalence.

The migration scanner now recognizes immutable same-enum constant aliases, fixing the false positive for retained `InterestResultPolicy.DEFAULT`. Adversarial fixtures reject mutable instances, object payloads and non-final aliases. No baseline expansion or exemption was added.

### Validation

Pinned native GemFire **10.4.0-build.3056.df98c835**, Java 17, Gradle 9.7.0, offline cached dependencies. Final JUnit reports contain **520 executions**, no failures/errors/skips: SPI 24, core 31, contract-kit 6, mock 57, native integration 96, focused Spring cache mock/native 153 each. Driver checks completed in the broad run; five Spring XML failures were fixed by removing obsolete mode constructor arguments, then both focused Spring runtimes and remaining compilation/gates passed.

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

The Gradle tasks above were validated across the broad driver run and the subsequent successful Spring/compilation/gate rerun. Mock native-free classpath, 32 compiled ABI/stamp fixtures, packaged stamps and 19 boundary fixtures pass. Ten coverage policy tests pass; independent `javap` matches 372 declarations across 21 types. Deterministic generation and ID/scope/ownership/source validation pass.

These are focused Spring cache tests plus full source compilation, not the complete Spring unit suite. The boundary remains transitional; Spring still has native dependencies. The ABI gate correctly reports no released SPI baseline. Server-side API cleanup and final zero-unresolved coverage remain tasks 62–64. No commit made.
