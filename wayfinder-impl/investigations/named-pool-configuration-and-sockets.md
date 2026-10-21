# Named pool configuration and socket callbacks

Implementation evidence for [Named pool configuration and socket callbacks](../../wayfinder-issues/50-named-pool-configuration-and-socket-callbacks.md), 2026-10-22. The complete [pool/default-cache/durable parent](../../wayfinder-issues/20-complete-pool-default-cache-and-durable-spi.md) remains open.

## Implemented boundary

`PoolConfiguration` now has an immutable builder covering 25 native settings: global/per-server minimum and maximum connections; socket-connect/free/server/read/ping timeouts; load conditioning, idle/ping/statistics intervals and retries; socket buffer; subscription enabled/redundancy/tracking/ack/timeout multiplier; single-hop routing; server group/availability zone; multiuser authentication; and an SPI-owned socket callback. Endpoint lists remain immutable and mutually exclusive. The original three-argument named-pool constructor retains its nonempty-endpoint and minimum validation.

Unset settings are optional, so native defaults are not frozen into SPI constants. An empty builder is valid as a configuration overlay; a named native/mock pool still requires endpoints. `withFallback` takes explicit scalar values first, including false, zero and empty strings. A nonempty endpoint list replaces both fallback endpoint lists; empty lists inherit them. Null server-group/availability-zone inputs normalize to the native empty-string representation. Socket callbacks must be nonnull when explicitly supplied.

`DriverPool.configuration()` is a new default-unsupported operation for older drivers. It returns an immutable snapshot of actual native getters, including native defaults, rather than echoing requested settings. The local mock returns its documented effective configuration policy. The snapshot is not a connectivity assertion and contains no runtime topology, event queue count, credentials or authenticated view.

`DriverSocketFactory.createSocket()` carries only JDK `Socket`/`IOException`. Each default implementation is an ordinary `DefaultDriverSocketFactory` instance. There is no driver-owned global factory or registry. The native adapter retains application callback identity across named lookup and inspection, delegates actual socket creation, and preserves checked IOException identity. A foreign native socket callback can also be represented by a typed delegate. Null native callbacks appear absent in the optional configuration view.

Any explicit thread-local-connection setting, including false, fails before publication. Native 10.4 reports `REMOVED` with its exact version; the mock reports `NOT_INTRODUCED`. The stored-only `ClientCacheFactory.setPoolThreadLocalConnections` path remains explicitly assigned to default-cache assembly; it must not be silently accepted there.

## Native behavior and mock limits

All 25 setters are applied to a fresh native `PoolFactory`; the native engine validates and publishes the pool. In this artifact, nondefault global and per-server limits conflict, and per-server limits require single-hop routing. Native duplicate pool creation is an invalid-state failure; the existing mock duplicate is an invalid-argument failure. Shared tests preserve the common failure outcome without erasing that distinction. Native attachment/global registry semantics remain unchanged.

The mock models local immutable settings, validation and named lifecycle. It never resolves endpoints or invokes networking callbacks. Subscription, authentication, routing and statistics flags remain explicitly documented metadata; the mock does not claim to authenticate, route, publish statistics, maintain remote queues or create sockets. Its default values are local policy, not a promise that every native version has the same defaults. Default-cache selection, online topology, readiness, durable identity and keep-alive/queue retention require the blocking follow-ups.

## Evidence

The reference artifact is `com.vmware.gemfire:gemfire-core:10.4.0-build.3056.df98c835`, cached at SHA-1 directory `747633c557af8a845af3cac42c0efac8297661d5`. Inspection used the existing [public pool API snapshot](pool-native-10.4-api.txt) and `javap -c -p` on `org.apache.geode.internal.cache.PoolFactoryImpl`. Native observations remain authoritative over inferred configuration behavior.

- `PoolConfigurationTests`: unchanged constructor validation, immutable copies, scalar/endpoint precedence, explicit false/zero/empty string and older-provider unsupported operation.
- `PoolConfigurationContract`: round-trip settings and callback identity on both providers, invalid configurations before publication, duplicate preservation, and removed thread-local rejection.
- `NativePoolConfigurationTests`: all typed settings compared with actual native pool getters, unset defaults compared with an independently created native control pool, per-server versus global settings, locator configuration, checked socket failures and exact removed-feature version.
- `NativeDistributedCallbackTests.configuredSocketFactoryCreatesSocketsUsedForRealServerOperations`: separate server process, custom typed socket callback used by a real region put/get, subscription flag accepted by the native pool, callback identity retained. This is not durable replay evidence.
- `MockPoolConfigurationTests`: an unresolved endpoint and a socket callback that would throw are stored without DNS or socket invocation; local destruction removes the pool.

## Coverage and remaining ownership

The frozen inventory remains 3,851 rows / 292 types. This child owns 87 rows: 84 partial pending Spring adoption/default constants, and three implemented SocketFactory type/member/default mappings. There are no missing rows owned by this child. The complete pool parent remains open; its children retain all original obligations. Default-cache assembly owns 29 rows (including ClientCacheFactory setters and true default lookup), and durable/runtime lifecycle owns 26 rows (including registry lifecycle and native resource/connectivity errors). Pool query-service ownership moves to the existing query task.

After this slice the matrix has 3,066 missing, 506 partial and 279 implemented rows. Every current default SPI method has a behavioral probe and native mapping (105 defaults). Spring's 588 native signature exposures, 144 imported native symbols and 624 source/resource references are unchanged. Spring main/unit/integration native file counts remain 182/122/128. No Spring consumer or boundary-baseline exemption was added.

[Default client-cache pool assembly and selection](../../wayfinder-issues/51-default-client-cache-pool-assembly-and-selection.md) and [Durable client readiness and pool runtime lifecycle](../../wayfinder-issues/52-durable-client-readiness-and-pool-runtime-lifecycle.md) block the parent; all original consumers still wait on that parent. These tasks include real default selection/reuse and durable queue tests. Query/authenticated views retain their existing subsystem owners. Spring pool/cache resolver and configuration-view adapter migration remains with the existing entry-point cutover task.

## Validation commands and result

Final Gradle run: **BUILD SUCCESSFUL in 1m 12s**, log `/private/tmp/sdgf-pool-final.log`, using Gradle 9.8.1 and JDK 17.0.20+1.1-librca. Final XML reports contain **476 tests, zero failures/errors/skips**: SPI 21, core 24, contract kit 6, mock 48, native 71, Spring mock 153, Spring native 153. The 18 boundary fixture tests also pass. Main/unit/integration source compilation and Spring Test compilation pass; the normal boundary passes with its unchanged existing baseline. Strict native-free acceptance remains downstream.

```text
./gradlew :spring-data-gemfire-driver-spi:check :spring-data-gemfire-driver-core:check :spring-data-gemfire-driver-contract-tests:check :spring-data-gemfire-driver-mock:check :spring-data-gemfire-driver-10.4:check :spring-data-vmware-gemfire:spiCacheTest :spring-data-vmware-gemfire:spiCacheIntegrationTest :spring-data-vmware-gemfire:compileJava :spring-data-vmware-gemfire:compileTestJava :spring-data-vmware-gemfire:compileIntegrationTestJava :spring-test-vmware-gemfire:compileJava :spring-test-vmware-gemfire:compileTestJava :verifyMigrationBoundary -DgemfireVersion=10.4.+ --offline --console=plain
python3 wayfinder-impl/audits/scan_native_references.py
python3 wayfinder-impl/coverage/build_api_coverage.py
python3 wayfinder-impl/coverage/build_api_coverage.py --check
python3 wayfinder-impl/coverage/validate_coverage.py --javap
git -c core.whitespace=-blank-at-eol diff --check
```

Coverage validation independently matches 148 public declarations across 13 types and passes both negative controls. Earlier iterations caught a test that assumed identical native/mock duplicate failure categories and the boundary's rejection of static callback services; the final implementation preserves provider categories and uses instance-owned default factories. No exemptions or baselines were expanded.
