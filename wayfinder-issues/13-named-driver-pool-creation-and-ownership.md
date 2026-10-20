Type: task
Status: resolved
Assignee: Codex
Blocked by: 12, 14

## Question

Implement the named driver pool lifecycle foundation selected by [Pool configuration and resolver migration dependencies](12-pool-configuration-and-resolver-dependencies.md), using injected connection ownership without moving native pool resolvers ahead of their native consumers.

The [investigation](../wayfinder-impl/investigations/pool-configuration-and-resolver-dependencies.md) specifies the source dependencies, current lifecycle gaps, minimal API, native artifact evidence and validation plan. Architecture remains signed off; do not reopen it or load additional skills implicitly.

## Required implementation

First complete [Required native API coverage matrix](14-required-native-api-coverage-matrix.md) and apply the [shared completion requirements](../wayfinder-impl/spi-gap-closure-plan.md#shared-completion-requirements). This ticket owns the named-pool foundation rows only. [Complete pool, default-cache and durable SPI](20-complete-pool-default-cache-and-durable-spi.md) owns the remaining pool contract, and [Cache/pool entry points and resolver cutover](35-cache-pool-entrypoints-and-resolver-cutover.md) owns legacy public factory/resolver adoption. None of that work is implicitly complete when this foundation passes.

- Add immutable native-free `PoolConfiguration`: explicit server or locator host/port endpoints, exactly one nonempty endpoint kind, defensively copied collections, optional minimum connections preserving unset versus zero. Leave unspecified native settings at provider defaults. No arbitrary property bag, native types or callback objects in this slice.
- Add typed default-unsupported `DriverConnection.createPool(name, configuration)` and `getPool(name)`, requiring an open CLIENT connection and a nonblank name. Named lookup returns null only for absence. Creation fails on duplicate names; never find-and-adopt an existing pool. Preserve existing SPI method semantics and the established ABI/version-stamp policy.
- Add `DriverPool` with name, configured server/locator endpoints, effective minimum connections, destroyed state and `destroy()`. Use typed unsupported defaults. Endpoint metadata is not live topology. Do not promise wrapper identity, cache isolation or unconditional destruction. No native unwrap, query service, keep-alive or network-status API.
- Implement native 10.4 adaptation inside the driver, translating native runtime failures with causes. Delegate named creation/lookup to the native PoolManager after checking the injected connection; the native registry is permitted by spec §5.7(6). Add no process-wide state, wrapper registry, reference counting, global close or internal replacement registry. A failed duplicate must never destroy the existing pool.
- Implement official mock configuration/lookup/lifecycle records per connection. No sockets, servers or fake connectivity; configured minimum values are metadata. Document mock-specific defaults/isolation. Destroy removes lookup visibility and connection close destroys local records. Existing unsupported nonlocal-region/network-search behavior remains intact.
- Add native-free `DriverPoolFactoryBean` with constructor-injected ownership `(DriverConnection, String, PoolConfiguration)` or explicit borrowing `(DriverPool)`. Initialize once and publish one valid product; clean up a returned owned handle on subsequent initialization failure and preserve cleanup failure as suppressed. Shutdown is terminal and attempts owned destruction at most once, propagating failure. Never destroy unopened/borrowed pools or close the injected connection. No implicit borrowed-handle validation/reconfiguration, native superclass or bean/global lookup. Define pre-init/failed/destroyed access consistently with the connection owner.
- Wire real Java and ordinary XML Spring contexts to the existing `DriverConnectionFactoryBean`; ensure dependent pool shutdown precedes connection shutdown and the FactoryBean product has no second inferred destruction callback. Keep the legacy `PoolFactoryBean`, PoolResolver family, annotation/XML namespace entry points and native query/function/CQ consumers coherent. Do not add a second Spring pool resolver interface or a compatibility adapter.

## Required validation

- Shared mock/native contracts: create/find/missing lookup, endpoint/minimum metadata, duplicate rejection preserving the original, destroy/removal/recreation, closed connection and wrong mode, unsupported defaults. Avoid wrapper-identity, DNS spelling, server reachability and multi-cache isolation assertions. Use unique names and sequential native fixtures; minimum connections zero does not promise absence of native background networking.
- Spring contexts on both focused classpaths: owner and borrower in Java/XML, qualified connection with a second unusable candidate, product type/identity, reverse dependency shutdown, borrowed handle still usable after context close, downstream refresh failure cleanup.
- Focused recording-collaborator tests: argument validation, no creation at construction, unopened/repeated destruction, null/destroyed creation result, terminal initialization failure, cleanup suppression and destroy failure. Test actual lifecycle outcomes rather than mirroring implementation details.
- Native-only characterization: global pool visibility/collision across compatible connection wrappers, raw native mixed-endpoint rejection, and destruction rejection while a native client region is attached. Use a proxy region without server operations where the artifact permits it; clean up region before pool. Keep default-pool selection and distributed subscription/authentication/durable queue behavior outside shared claims.
- Run driver/core/contract checks, focused Spring mock/native checks and native-free enforcement for new code/tests, plus full main/unit/integration-source compilation and Spring Test compilation. Preserve user Gradle/settings edits; use the guarded temporary init script and session-only escalation without a persistent prefix if needed. Record the exact resolved native artifact.

## Completion

Record behavior, test evidence, limitations and source pointers in an Answer; update the implementation map and assigned coverage rows. This is named pool creation/ownership, not full pool configuration, resolver migration or legacy factory cutover. Continue through the existing [gap closure plan](../wayfinder-impl/spi-gap-closure-plan.md); full pool contracts and consumer cutover already have named owners. Create additional tickets only for newly discovered obligations or necessary bounded splits.

## Answer

Resolved 2026-10-21. The named-pool creation/ownership foundation is implemented and verified. Full pool settings, default-cache pool selection, query/CQ/function consumers and legacy factory/resolver adoption remain open under their existing owners.

### Contracts and implementations

- [PoolConfiguration](../spring-data-gemfire-driver-spi/src/main/java/com/vmware/gemfire/driver/spi/PoolConfiguration.java) is immutable and native-free: defensively copied server/locator lists, exactly one nonempty kind, nonblank hosts, ports 1–65535, and an optional nonnegative minimum that distinguishes unset from zero. Endpoint values perform no DNS lookup.
- [DriverConnection](../spring-data-gemfire-driver-spi/src/main/java/com/vmware/gemfire/driver/spi/DriverConnection.java) adds `createPool` and `getPool`; [DriverPool](../spring-data-gemfire-driver-spi/src/main/java/com/vmware/gemfire/driver/spi/DriverPool.java) adds name, configured endpoints, effective minimum, destruction state and destruction. All eight new operations have typed `NOT_INTRODUCED` defaults, including tests against implementations that supply no overrides. Existing SPI signatures/behavior and the generated stamp mechanism remain intact. The unreleased SPI baseline stays 1.0; release ABI gating remains assigned to [SPI ABI compatibility and stamp enforcement](39-spi-abi-compatibility-and-stamp-enforcement.md).
- [GemFireConnection](../spring-data-gemfire-driver-10.4/src/main/java/com/vmware/gemfire/driver/gemfire104/GemFireConnection.java) checks the injected connection's open CLIENT mode and arguments, then delegates directly to native `PoolManager.createFactory/find`. It applies only specified settings and never preflights/adopts or destroys a duplicate. [GemFirePool](../spring-data-gemfire-driver-10.4/src/main/java/com/vmware/gemfire/driver/gemfire104/GemFirePool.java) delegates metadata and lifecycle, wrapping native runtime failures in `DriverOperationException` with the original cause. No driver registry, native unwrap, global close or reference counting was added.
- [MockConnection](../spring-data-gemfire-driver-mock/src/main/java/com/vmware/gemfire/driver/mock/MockConnection.java) and [MockPool](../spring-data-gemfire-driver-mock/src/main/java/com/vmware/gemfire/driver/mock/MockPool.java) keep per-connection records. Destruction removes only that record; destroying an old handle cannot remove its replacement. Connection closure destroys local pools. The documented mock default minimum is 1. These are metadata records: no DNS, sockets, discovery, subscriptions or native attachment simulation.
- [DriverPoolFactoryBean](../spring-data-vmware-gemfire/src/main/java/org/springframework/data/gemfire/DriverPoolFactoryBean.java) creates once from an injected connection/name/configuration or explicitly borrows a supplied pool. Initialization rejects null/destroyed owned products and cleans up returned handles, suppressing cleanup failure without replacing the original. Borrowing does not inspect or reconfigure. Shutdown is terminal and attempts owned destruction once; the factory never closes its connection. Java/XML tests verify Spring destroys the dependent pool before its connection and does not infer a second destruction callback for the FactoryBean product.

### Coverage accounting

The [matrix](../wayfinder-impl/coverage/api-coverage.tsv) advances these 12 previously missing native rows to **partial**, with SPI/native/mock/test evidence:

| Native obligation | Stable API ID |
| --- | --- |
| `PoolFactory.create(String)` | `API-facbf87d7bfe1d69` |
| `PoolManager.createFactory()` | `API-dacd4348020e7fd8` |
| `PoolManager.find(String)` | `API-f0345d377ef88ca6` |
| `PoolFactory.addServer` | `API-29e313efe5131942` |
| `PoolFactory.addLocator` | `API-66bbbbad5a28e1da` |
| `PoolFactory.setMinConnections` | `API-f4055bbf5d829d09` |
| `Pool.getName()` | `API-bf3fb094e518dfe1` |
| `Pool.getServers()` | `API-06a3499cdcce590a` |
| `Pool.getLocators()` | `API-808cd6a9a8d488d8` |
| `Pool.getMinConnections()` | `API-584c4a16055fa788` |
| `Pool.isDestroyed()` | `API-ee5f65fd3384db8a` |
| `Pool.destroy()` | `API-6a8a4bd25506af1a` |

The bounded foundation is complete; the rows conservatively remain partial while [complete pool contracts](20-complete-pool-default-cache-and-durable-spi.md), [exception categories](16-driver-exception-semantics-and-translation.md) and [legacy Spring cutover](35-cache-pool-entrypoints-and-resolver-cutover.md) remain open. Those owners remain attached; a resolved foundation must not conceal their obligations. The existing-SPI comparison now maps 26 operational defaults plus eight callbacks/helpers. Its validator compares default mappings with current source declarations rather than a frozen count of 18.

All 3,851 frozen obligations across 292 native types remain: 3,751 missing, 100 partial. None is newly marked fully implemented or removed. Scoped native-free enforcement expands to 21 main and 23 test files. Whole-module native dependencies/reference counts are unchanged.

### Validation

All seven suites below passed, **393 tests, zero failures/errors/skips**:

| Suite | Tests |
| --- | ---: |
| SPI | 14 |
| Core | 24 |
| Contract verifier | 6 |
| Official mock | 21 |
| Native driver integration | 26 |
| Spring scoped mock | 151 |
| Spring scoped native | 151 |

The [shared contract](../spring-data-gemfire-driver-contract-tests/src/main/java/com/vmware/gemfire/driver/contract/GemFireDriverContract.java) exercises all eight new default operations, create/find/missing lookup, server and locator metadata, duplicate preservation, destruction/removal/recreation, stale-handle destruction, wrong mode, closed connections and invalid arguments. [Native characterization](../spring-data-gemfire-driver-10.4/src/integrationTest/java/com/vmware/gemfire/driver/gemfire104/NativePoolTests.java) verifies visibility/collision across compatible connection wrappers, original native causes, both mixed-endpoint orders, unset provider defaults and rejection of destruction while a proxy client region is attached. The region is detached before final pool cleanup; no server operations or reachability assertions are used.

[Factory tests](../spring-data-vmware-gemfire/src/test/java/org/springframework/data/gemfire/DriverPoolFactoryBeanTests.java) cover constructor validation, unopened/repeated shutdown, exact configuration, identity, null/destroyed products, terminal failures, suppressed cleanup, errors, borrowed handles and product post-processing failure. [Context tests](../spring-data-vmware-gemfire/src/test/java/org/springframework/data/gemfire/support/InjectedPoolIntegrationTests.java) run owner/borrower Java and ordinary XML contexts on both runtimes, with qualified owner injection alongside an unusable second connection, reverse dependency shutdown and downstream refresh-failure cleanup. XML assets: [owners](../spring-data-vmware-gemfire/src/test/resources/org/springframework/data/gemfire/support/pool-context.xml), [borrowers](../spring-data-vmware-gemfire/src/test/resources/org/springframework/data/gemfire/support/borrowed-pool-context.xml).

Executed successfully with session-only Gradle approval and the guarded temporary init script:

```sh
./gradlew -I /private/tmp/sdgf-resolver-validation.init.gradle \
  :spring-data-gemfire-driver-spi:check :spring-data-gemfire-driver-core:check \
  :spring-data-gemfire-driver-contract-tests:check :spring-data-gemfire-driver-mock:check \
  :spring-data-gemfire-driver-10.4:check \
  :spring-data-vmware-gemfire:spiCacheTest :spring-data-vmware-gemfire:spiCacheIntegrationTest \
  :spring-data-vmware-gemfire:compileJava :spring-data-vmware-gemfire:compileTestJava \
  :spring-data-vmware-gemfire:compileIntegrationTestJava \
  :spring-test-vmware-gemfire:compileJava :spring-test-vmware-gemfire:compileTestJava \
  -DgemfireVersion=10.4.+ --offline --console=plain
```

Log: `/private/tmp/sdgf-pool-validation.log`. The first invocation selected a nonexistent Spring Test integration-test task and ran no tests; the corrected invocation above passed. Spring Test has main/test source sets. A final mock-only check also passed after retaining the preexisting region-error wording while separating pool validation (`/private/tmp/sdgf-pool-mock-final.log`); its 21 rerun tests are not counted again above.

Exact native artifact: **`com.vmware.gemfire:gemfire-core:10.4.0-build.3056.df98c835`**. Native minimum-connection methods are deprecated for removal in this artifact but still present and delegated; no removed-feature behavior is invented. Zero minimum does not guarantee absence of native background networking. Configured endpoints are not live topology, wrappers need not be identical, and attached-region destruction may fail.

Also passed: lexical inventory refresh; deterministic coverage regeneration/check; coverage validation including independent `javap` matching 148 declarations across 13 native types, all source/owner/ID checks and both negative controls; `git diff --check`. Full ordinary runtime test suites were not run; all required source sets compiled and the focused runtime suites above ran. User Gradle/settings changes and prior uncommitted work were preserved. No commit, publication or merge was performed.
