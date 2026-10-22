Type: task
Status: resolved
Assignee: Codex
Blocked by: 14, 16

# Complete region events, listeners and writers


> Scope amendment (2026-10-22): implementation and test evidence below is historical. The [client-only decision](../wayfinder-impl/client-only-scope.md) supersedes peer/server requirements; [ticket 59](59-remove-server-only-spi-and-reconcile-client-scope.md) owns removal and coverage reconciliation. This ticket does not authorize further server implementation.

## Question

Provide full typed cache-listener and cache-writer contracts, their reachable event payloads and lifecycle, beyond the current destruction-only resolver callback.

Audit coverage: **G3, G4** in the [native/SPI gap audit](../wayfinder-impl/audits/native-spi-gap-audit.md). Apply the [shared completion requirements](../wayfinder-impl/spi-gap-closure-plan.md#shared-completion-requirements) and the signed-off [technical spec](../wayfinder/technical-spec.md).

## Required implementation

Prerequisites: [Required native API coverage matrix](14-required-native-api-coverage-matrix.md); [Driver exception semantics and translation](16-driver-exception-semantics-and-translation.md).

- Implement every required CacheListener callback: entry create/update/invalidate/destroy and region invalidate/destroy/clear/create/live, plus cleanup. Preserve the existing lifecycle API's semantics while adding the general listener contract.
- Model CacheEvent, EntryEvent and RegionEvent data: operation, callback argument/availability, remote/member identity, key, old/new value availability and serialized forms, transaction identity and client origin as required by the coverage matrix. Introduce shared identity/value types here; runtime member/transaction operations belong to their own tickets.
- Add writer before-event callbacks, rejection semantics, registration/replacement/removal and declarative callback initialization through injection. Complete loader/CacheCallback inherited initialization gaps identified by the matrix.
- Implement native adapters and local mock delivery for supported operations. Document native timing/ownership; no synthetic remote events or discarded callbacks. General factory adoption belongs to Region factory and configuration migration.

- Cover application-only listener specializations (`RegionMembershipListener`, `RegionRoleListener`, adapter classes), full inherited `CacheEvent`/`RegionEvent`/`EntryEvent` payloads and `SerializedCacheValue`. Preserve both `Declarable.init(Properties)` and `initialize(Cache, Properties)` semantics using typed callbacks. See the [member-level baseline](../wayfinder-impl/coverage/README.md).

- Exception foundation follow-up: Use the common failure mapper for callback entry/exit; preserve loader failures, writer veto, both typed unsupported reasons and native listener suppression. Test actual SPI writer registration once available. See [exception semantics and ownership](../wayfinder-impl/investigations/exception-semantics-and-translation.md).

## Required validation

- Exercise every callback and mapped payload through a real native registration; verify writer veto/error behavior, listener identity, removal/replacement and cleanup.
- Share tests for locally supported behavior; test region-live/remote/serialization-specific events in native fixtures. Prove missing adapter callbacks are detected.

## Completion

Complete required callback/event contract rows with native adapters and honest mock behavior, not merely methods inherited as no-ops. Record the Answer with changed source/API rows, commands/results, native version and limitations; update the implementation map. Do not mark dependent work complete from this ticket's result.

## Answer

Resolved 2026-10-21 by Codex. [Implementation and native evidence](../wayfinder-impl/investigations/region-callback-contracts.md) is the detailed handoff.

Implemented all nine listener and five writer callbacks, specialized membership/role contracts and adapters, separate declarable hooks, lazy typed event/serialized/timestamp payloads, member/transaction identities and the complete 50-constant operation vocabulary. Native adapters explicitly forward callbacks and map failures; the local mock delivers supported events and rejects distributed listener registration without partial creation. Region callback registration/lookup/replacement, creation-time injection and event-producing operations are implemented, including previous-value return from entry destroy. Resolver lifecycle cleanup remains independent of general listener reinitialization.

Native tests verify every callback through registered adapters and real entry/region operations. Separate native processes verify remote entry serialization, membership join/departure/crash, role gain/loss and a durable client's region-live marker. Tests caught and resolved the native membership wrapper's effect on callback identity and the actual Role objects behind RoleEvent's declared Set<String>. Scalar/serialized/timestamp views remain lazy; timestamp payload fixtures do not claim WAN conflict-resolver delivery. Typed writer vetoes and both unsupported reasons retain identity and prevent mutation.

Coverage: all 291 callback-owned rows now have evidence: 145 implemented leaf/value rows and 146 partial reachable-contract/adoption rows. Another 33 previously missing registration/identity rows have partial evidence under existing owners. The frozen 3,851 rows / 292 types remain: 3,369 missing, 337 partial, 145 implemented. The SPI comparison covers 42 default hooks/methods and 29 callback declarations. Full event-reachable region/cache/member/transaction operations, WAN delivery and Spring annotation/XML/composable/factory adoption retain their explicitly named downstream owners. Spring counts remain 588 signature exposures, 144 imported symbols, 624 source references and 182/122/128 native production/unit/integration files.

Validation against `com.vmware.gemfire:gemfire-core:10.4.0-build.3056.df98c835`:

```text
./gradlew :spring-data-gemfire-driver-spi:check :spring-data-gemfire-driver-core:check
  :spring-data-gemfire-driver-contract-tests:check :spring-data-gemfire-driver-mock:check
  :spring-data-gemfire-driver-10.4:check :spring-data-vmware-gemfire:spiCacheTest
  :spring-data-vmware-gemfire:spiCacheIntegrationTest :spring-data-vmware-gemfire:compileJava
  :spring-data-vmware-gemfire:compileTestJava :spring-data-vmware-gemfire:compileIntegrationTestJava
  :spring-test-vmware-gemfire:compileJava :spring-test-vmware-gemfire:compileTestJava
  :pruneMigrationBoundaryBaseline -DgemfireVersion=10.4.+ --offline --console=plain

./gradlew :spring-data-gemfire-driver-mock:test :spring-data-gemfire-driver-10.4:integrationTest
  --tests '*ContractTests' :verifyMigrationBoundary
  -DgemfireVersion=10.4.+ --offline --console=plain

python3 wayfinder-impl/audits/scan_native_references.py
python3 wayfinder-impl/coverage/build_api_coverage.py --java-home <installed-JDK-17>
python3 wayfinder-impl/coverage/build_api_coverage.py --check --java-home <installed-JDK-17>
python3 wayfinder-impl/coverage/validate_coverage.py --javap
git -c core.whitespace=-blank-at-eol diff --check
```

The full run passed **430 behavior tests** (SPI 18, core 24, contract-kit 6, mock 31, native 45, Spring mock/native 153 each), with zero failures/skips. Boundary fixture checks also passed (18 tests). Full Spring Data main/unit/integration and Spring Test main/unit compilation passed. A final focused run passed after matching native null-list removal semantics and asserting resolver cleanup remains deferred. Inventory generation/check, 148 independent javap declarations across 13 types, ownership/source/fingerprint validation and negative controls passed. Boundary pruning removed nothing further: the existing 4,475-identity / 6,050-occurrence debt baseline remains. Strict native-free compliance is still outstanding.

Logs: `/private/tmp/sdgf-callbacks-final-validation.log`, `/private/tmp/sdgf-callbacks-null-list.log`, and `/private/tmp/sdgf-distributed-callbacks.log`. The final full run used the workspace's updated Gradle 9.8.1 wrapper; that independent wrapper edit and the user's gemfireVersion property were preserved. No commit or push was made.

Next frontier in ascending order: [Complete region operations and metadata SPI](18-complete-region-operations-and-metadata-spi.md), [Complete pool, default-cache and durable SPI](20-complete-pool-default-cache-and-durable-spi.md), [PDX serialization and JSON SPI](26-pdx-serialization-and-json-spi.md), [SPI ABI compatibility and stamp enforcement](39-spi-abi-compatibility-and-stamp-enforcement.md).
