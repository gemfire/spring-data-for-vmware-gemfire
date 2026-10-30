# Client runtime and cache cancellation handles — task 23

Implementation checkpoint: 2026-10-29. The user reported the validation command passed. Task 23 stays open; Spring consumers remain on their existing paths until task 36.

`DriverConnection.getRuntime()` exposes local runtime connectedness and reuses `DriverMember` for local identity. Native connectedness delegates to `DistributedSystem.isConnected()`: a client without any server or pool can be connected locally. This flag does not establish server reachability or authentication. There is no system connect/disconnect API, registry or peer administration in the new SPI.

`DriverConnection.getCancellation()` exposes the cache criterion separately. It supports active/null polling, checks with/without a caller cause, and generation of classified cancellation failures. Native reason text is replaced with a fixed safe message. Native exceptions and supplied causes remain in the trusted diagnostic exception graph; they must not be rendered as safe public messages. Native fatal errors propagate. Captured handles survive cache closure; native compatible connections share the underlying cache lifetime.

Pinned source: GemFire `10.4.0-build.3056.df98c835`, commit `df98c8357afe85853d9a4776255d4f0e7cf064cb`, `gemfire-core/src/main/java/org/apache/geode/CancelCriterion.java`, `internal/cache/GemFireCacheImpl.java`, `distributed/internal/InternalDistributedSystem.java` and `distributed/internal/LonerDistributionManager.java`. The loner system criterion returns null even on cache closure; the cache criterion also checks cache closing/disconnect cause. Consequently the new cache handle does not claim to implement `DistributedSystem.getCancelCriterion()`. Native `checkCancelInProgress` retains the native `SystemFailure.checkFailure()` call; the driver catches RuntimeException, never Error.

The mock implements local identity and closure only. Connections have independent identities and cancellation, unlike native compatible cache handles. The configured-identity extension below adds supplied name/groups and durable snapshots. The mock does not model network connectivity, server failures, ambient durable aliases or native JVM-failure machinery.

## Coverage and validation

Nine new default SPI methods have shared behavioral probes; unsupported defaults fail explicitly. Eleven exact native API rows have partial mappings. No frozen API IDs or scope classifications change. Cache runtime access remains partial because the rest of the returned native system's services are not implemented. General RegionService cancellation also remains partial: authenticated views and other service owners require their own adoption.

Shared contract tests cover active and closed lifecycle, identity, cause retention and CANCELLED classification. Native tests cover local connectedness without a server, the cache/system cancellation distinction, native reason redaction, retained trusted causes, fatal-error propagation and compatible-cache shared closure. Mock tests cover independent identities/lifecycles. Local JUnit XML confirms 164 test executions: SPI 23, core 31, shared contract harness 6, mock 56 and native 48, with zero failures/errors/skips (2026-10-28T21:40:11Z–21:40:40Z). The user-run command also passed mock native-free classpath, driver compatibility and transitional migration-boundary verification; this is not full native-free compliance. Coverage generation/check, all 16 policy tests, independent javap verification of 372 declarations, and `git diff --check` pass. Scope remains 2,694 required-client / 1,157 excluded-server / zero unresolved.

## Remaining task 23 work

- Configured identity and pool-qualified durable snapshots are validated below; remote durable lifecycle remains coordinated with tasks 20/24/35. Configuration readback is validated below.
- Endpoint/server connectivity and lifecycle failure evidence with private locator/server fixtures.
- Local statistics descriptors/types/instances/suppliers and logging/message services from task 73.
- Spring adoption in task 36, affected consumer compilation and complete native/mock mappings before task closure.

## Passed user-run validation

```bash
./gradlew --offline -DgemfireVersion=10.4.0-build.3056.df98c835 \
  :spring-data-gemfire-driver-spi:test \
  :spring-data-gemfire-driver-contract-tests:test \
  :spring-data-gemfire-driver-mock:check \
  :spring-data-gemfire-driver-10.4:integrationTest \
  --tests '*GemFire104DriverContractTests' \
  --tests '*NativeClientRuntimeTests' \
  --tests '*NativeConnectionReuseTests' \
  verifyDriverCompatibility verifyMigrationBoundary
```

## System cancellation extension — validation passed

`DriverClientRuntime.getCancellation()` now exposes a separate system-scoped handle backed by `DistributedSystem.getCancelCriterion()`. It reuses the cancellation adapter, with an owner-neutral safe reason. The mock supplies a separate per-runtime criterion that stays clear on ordinary closure, matching the pinned native client behavior. It does not simulate abnormal system termination or JVM failure. Local connectedness, cache cancellation and system cancellation are three independent observations; callers must use the appropriate one.

The shared contract probes the new default and the active criterion. Native lifecycle tests compare the SPI and real native system criteria after closure, including compatible connections sharing a cache. A synthetic native criterion supplied through a Mockito `DistributedSystem` exercises runtime wiring, `DistributedSystemDisconnectedException` translation, safe reason/message redaction and caller-cause retention; this test does not claim to reproduce abnormal native system termination. Existing adapter tests preserve fatal-error propagation. Mock tests check per-runtime handle isolation and the cache/system distinction after closure.

The first extension validation attempt failed to compile the native integration tests because the new test imported Mockito without declaring its dependency in this source set. The driver now declares the existing `libs.mockito` dependency for integration tests. The user confirmed the rerun passed on 2026-10-29. Current JUnit reports contain 165 tests (SPI 23, core 31, shared contract harness 6, mock 56 and native 49), with zero failures/errors/skips. Native report timestamps are 2026-10-28T21:55:58Z–21:56:17Z; unchanged suites retain earlier reports. The command also passed driver compatibility and transitional migration-boundary verification.

Coverage now has ten new default-method mappings and twelve partial native rows relative to the pre-task-23 baseline. The newly mapped row is `API-f2ae8d8c7e5c7907`; no scope classification or frozen API ID changes. The earlier 164-test pass applies to the initial checkpoint only. The same command above validated this extension; task 23 and consumer task 36 remain open.

## Caller-owned cancellation — validation passed

`DriverClientRuntime.createCancellation(BooleanSupplier)` replaces native criterion subclass construction with an injected caller-owned state source. The supplier must be non-null, thread-safe, non-throwing and monotonic (once true, always true). The driver evaluates it on demand; neither cache closure nor another criterion cancels this service. The public result is always a safe CANCELLED failure with the caller's cause retained. Native exception subclass selection is private; applications can interpret the classified SPI failure without constructing native exception types.

The native implementation creates a private `CancelCriterion` whose polling first consults inherited `checkFailure()`. It reports JVM failure without consulting application state, then the adapter redacts the reason. Inherited check methods call `SystemFailure.checkFailure()` first and propagate the fatal Error unchanged. The mock uses the supplied state only. This replaces the protected helper's behavior without publishing a native JVM-failure registry or control API.

`DriverClientRuntime.noCancellation()` replaces `CancelCriterion.NONE`. Polling and exception generation always return clear/null, even after cache closure or a reported JVM failure; native check methods still invoke fatal-failure handling. No singleton identity is promised. This is intentionally distinct from a caller criterion whose supplier always returns false: the latter's native polling observes JVM failure.

Shared default-method probes cover active/cancelled state, null-supplier rejection, independent criteria, cache-closure independence, both check overloads, safe messages and caller-cause retention in both drivers. Two native tests use scoped Mockito static stubs for `SystemFailure` to verify NONE and protected-helper semantics and fatal-before-supplier ordering. They do not set real JVM-wide failure state or simulate actual JVM corruption. The existing integration-test Mockito dependency is reused.

Four additional native rows (type, constructor, NONE, protected helper) now have partial mappings. Totals relative to the pre-task-23 baseline are twelve SPI defaults and sixteen partial native rows. Frozen API IDs and client/server classifications do not change. The user confirmed the command above passed on 2026-10-29. Current JUnit reports contain 167 tests (SPI 23, core 31, shared contract harness 6, mock 56 and native 51), with zero failures/errors/skips. Native report timestamps are 2026-10-28T22:23:12Z–22:23:30Z; unchanged suites retain earlier reports. Driver compatibility and transitional migration-boundary verification also passed; this is not full native-free compliance. Task 23 stays open for configuration, endpoint connectivity, statistics/logging and task-36 consumer adoption.

The first validation attempt reported a migration-boundary `static-service` violation for the private static cancellation factory. Construction now uses an instance constructor called by the injected runtime; the static factory is removed. No boundary exemption or baseline addition was made. The user-confirmed rerun of the same command above validates this correction.

## Configuration readback — validation passed

`DriverClientRuntime.getProperties()` and `getSecurityProperties()` return a native-free `DriverPropertySnapshot`. It captures only string keys/values, flattens inherited string defaults and gives trusted callers fresh mutable `Properties` copies. Arbitrary native objects are intentionally excluded. Its diagnostic representation is always redacted; explicitly extracted values can contain credentials. This is readback only and adds no peer/server control API.

The native adapter reads current effective configuration on each call, including native defaults, and translates runtime failures through the existing exception mapper. Pinned `InternalDistributedSystem.getProperties()` delegates to `config.toProperties()`; `getSecurityProperties()` delegates to `DistributionConfigImpl.getSecurityProps()`, which already returns a copy. The SPI snapshot adds a typed, string-only boundary and redacted diagnostics rather than fixing a native alias. Both native getters retain configuration after ordinary closure. The mock preserves only supplied configuration and selects `security-` keys for security readback; it does not emulate native defaults, files, system properties, authentication or TLS resolution.

Shared tests cover both drivers' copy isolation, security values, redaction and post-closure reads. SPI tests cover inherited defaults and rejection of non-string entries; mock tests cover independent configurations and absence of fabricated defaults. Native tests compare effective values/defaults to the real native getters, then use a synthetic mutable Mockito source to verify detachment and classified read failures. That synthetic source is not evidence of native aliasing.

Two native rows (`API-a1ce6075690d70fa`, `API-936cce7138573930`) now have partial mappings and two more defaults have shared probes: fourteen new defaults and eighteen partial native rows relative to the pre-task-23 baseline. No frozen IDs or scope dispositions change. The user confirmed the command above passed on 2026-10-29. Current JUnit reports contain 174 tests (SPI 25, core 31, shared contract harness 6, mock 58 and native 54), with zero failures/errors/skips. Report timestamps span 2026-10-28T22:51:53Z–22:52:26Z. Driver compatibility and transitional migration-boundary verification also passed; this is not full native-free compliance. Coverage generation/check, inventory validation and all 16 scope-policy tests passed before the Java handoff. Property-file resolution is the next bounded configuration slice; endpoint connectivity, statistics/logging and task-36 adoption remain open.


## Property-file discovery — validation passed

`GemFireDriver.resolvePropertiesFile()` and `resolveSecurityPropertiesFile()` expose discovery before connection creation through the injected provider. `DriverPropertyFile` contains the selected filename and an optional JDK URL, with a fixed redacted `toString()`. Missing locations are normal. Discovery does not read contents, alter configuration or report which file supplied an existing connection's values. Explicit filename/URL access can reveal sensitive paths. Repeated calls consult current native process settings; concurrent process-property changes are not an atomic snapshot.

The 10.4 driver delegates to `DistributedSystem.getPropertiesFile()/getPropertiesFileURL()` and the security equivalents at pinned source `df98c8357afe85853d9a4776255d4f0e7cf064cb`. This preserves native selection, canonicalization and working-directory/home/classloader lookup instead of reproducing them. The pinned private `getFileURL` tries the home path and classloader after a missing initial path, without an explicit absolute-path guard; rely on actual native behavior rather than strengthening its documentation into a new SPI promise. The deprecated URL alias maps to the regular discovery method. Six native filename/default/property-key constants map to this typed result and native selection: legacy class-initialization snapshots deliberately become per-call discovery, not exported global constants. No file is loaded by discovery, and native connection creation retains its existing loading behavior.

The mock returns `gemfire.properties` and `gfsecurity.properties` with absent locations. It does not inspect system properties, the filesystem or classloaders and does not simulate native file loading. Applications needing synthetic discovery results can supply an injected driver; these methods require no open connection.

Tests add shared default-method probes, SPI result/redaction checks, mock unresolved-default checks and three native tests. The native tests cover defaults, independent overrides, invalid file contents that must not be parsed, missing files, captured results versus later discovery, working-directory/home/classpath precedence, deprecated URL alias parity and translated runtime/fatal failures. Process properties are restored with scoped helpers and a JUnit system-properties resource lock; temporary working files are removed in `finally`. Native static failure stubs are scoped Mockito mocks and do not replace the real lookup tests.

Eleven native rows remain partial for task-36 adoption. Task-23 totals relative to its starting point are now **16 new SPI defaults and 29 partial native rows**. No frozen IDs or client/server scope classifications change. The prior 174-test pass applies to the preceding configuration-readback slice, not this extension. Task 23 remains open for configured/durable identity, endpoint connectivity, statistics/logging and task-36 adoption.

The user confirmed successful validation on 2026-10-29. Local JUnit XML records 179 tests (SPI 26, core 31, shared contract harness 6, mock 59 and native 57), with zero failures/errors/skips, including all three new native discovery tests. Report timestamps span 2026-10-28T23:11:57Z–23:12:28Z. The user-run command also passed driver compatibility and transitional migration-boundary verification; this is not full native-free compliance. Coverage generation/check, inventory validation and all 16 scope-policy tests passed for this extension.

User-run validation command (not executed by the agent):

```bash
./gradlew --offline -DgemfireVersion=10.4.0-build.3056.df98c835 \
  :spring-data-gemfire-driver-spi:test \
  :spring-data-gemfire-driver-contract-tests:test \
  :spring-data-gemfire-driver-mock:check \
  :spring-data-gemfire-driver-10.4:integrationTest \
  --tests '*GemFire104DriverContractTests' \
  --tests '*NativeClientRuntimeTests' \
  --tests '*NativeConnectionReuseTests' \
  --tests '*NativePropertyFileTests' \
  verifyDriverCompatibility verifyMigrationBoundary
```


## Configured and durable identity — validation passed

`DriverClientRuntime.getName()` exposes the configured local name. `getLegacyId()` adapts the deprecated native numeric connection ID, distinct from the `distributed-system-id` configuration property. Native loner clients and the mock return zero; use `DriverMember` for member identity. Both values remain readable after ordinary closure. Native runtime failures are translated and fatal errors propagate unchanged.

The mock now captures supplied `name`, `roles`/`groups`, `durable-client-id` and `durable-client-timeout` at connection creation. Groups follow pinned native parsing: roles first, then groups, comma tokenization, trimming and ordered deduplication. This configuration readback does not introduce peer-role administration. Defaults remain empty name/groups/durable ID and timeout 300. Malformed timeouts produce a classified argument failure; negative integer values remain representable, matching the native configuration setter rather than inventing a new bound.

`DriverDurableClientAttributes` captures base ID, timeout and pool name, splitting at the first `_gem_` separator. `withPoolName` and `withTimeout` return independent values. The native adapter reads the native qualified ID and then pool field: capture may observe native thread-local pool context, but the returned value never consults it again. Null/empty base IDs retain pool metadata without inventing a qualified ID. Equality and hashing use all three immutable fields, including null IDs; diagnostics are redacted. This deliberately replaces native mutable aliases, ambient mutation during `getId`/hashing/diagnostics and native non-value equality for distinct null-ID objects. Copies do not reconfigure a connection, pool or server queue.

The former two-component SPI record becomes a final immutable class. The two-argument constructor and `id()`/`timeout()` methods remain, but record reflection/pattern matching and old equality/diagnostic semantics are not preserved. This is a documented pre-first-release shape change; no released SPI ABI baseline exists. The compatibility fixture gate passed; this does not establish released binary compatibility.

Evidence is pinned source `df98c8357afe85853d9a4776255d4f0e7cf064cb`: `DurableClientAttributes` constructors/getId/setPoolName/updateTimeout/equals/hashCode/toString, `ClientProxyMembershipID` pool ThreadLocal, `InternalDistributedSystem.getName/getId` and connection-ID assignment, `LonerDistributionManager.generateMemberId/getDistribution`, `MemberDataBuilder.parseGroups` and the native durable-timeout configuration setter. This distinguishes legacy connection ID from configured system ID and explicit copies from native mutable state.

Tests cover shared configured identity and closure; SPI null/empty/pool values, immutable copies, stable hash membership and diagnostics; mock configuration isolation and malformed timeouts; real native durable attributes under saved/restored thread-local context; and native legacy-ID versus configured-ID behavior plus translated/fatal read failures. Mockito supplies the member wrapper for the real native durable value and injects read failures; it does not simulate native durable value behavior. Existing callback coverage is included in the validation command because its member adapter changed.

Coverage generation/check, inventory validation, all 16 scope-policy tests and `git diff --check` passed for this extension. Java tests and Gradle compatibility/boundary gates have not been run by the agent.

Coverage adds two runtime default mappings and maps all eleven durable-value rows, including eight previously missing rows. All remain partial for consumer adoption and remaining durable lifecycle work; no frozen IDs or client/server classifications change. The earlier 179-test success covers property discovery, not this extension. Endpoint connectivity, statistics/logging, remote durable lifecycle and task-36 consumer adoption remain open.

The user confirmed successful validation on 2026-10-29. Local JUnit XML records **194 tests** (SPI 28, core 31, shared contract harness 6, mock 62 and native 67), with zero failures/errors/skips. Report timestamps span 2026-10-28T23:41:52Z–23:42:26Z. This includes the new durable identity tests, shared configured identity test and existing native callback coverage. Driver compatibility and transitional migration-boundary checks also passed; this is not full native-free compliance.

User-run validation command (not executed by the agent):

```bash
./gradlew --offline -DgemfireVersion=10.4.0-build.3056.df98c835 \
  :spring-data-gemfire-driver-spi:test \
  :spring-data-gemfire-driver-contract-tests:test \
  :spring-data-gemfire-driver-mock:check \
  :spring-data-gemfire-driver-10.4:integrationTest \
  --tests '*GemFire104DriverContractTests' \
  --tests '*NativeClientRuntimeTests' \
  --tests '*NativeConnectionReuseTests' \
  --tests '*NativePropertyFileTests' \
  --tests '*NativeDurableIdentityTests' \
  --tests '*NativeCallbackCoverageTests' \
  verifyDriverCompatibility verifyMigrationBoundary
```

## Endpoint observations — validation passed

`DriverConnection.getCurrentServers()` and `DriverPool.onlineLocators()` reuse the native-free `PoolConfiguration.Endpoint` host/port value. Both return detached immutable snapshots without resolving names. The native driver delegates public native methods and translates runtime failures; fatal errors propagate. The mock reports empty observations, including after closure: configured endpoints do not establish network connections. Shared default probes and a mock test enforce this distinction.

Ownership remains task 52 for these two exact API rows, with consumer adoption in task 35. This is a bounded foundation linked from task 23, not completion of pool lifecycle/readiness, default-pool assembly, or task 23. Both coverage rows remain partial for consumer adoption.

Pinned native source `df98c8357afe85853d9a4776255d4f0e7cf064cb` establishes the limits:

- `GemFireCacheImpl.getCurrentServers()` reads `PoolManager.getAll()` and each pool's endpoint map. It has no closed-cache guard and is JVM-global, not a per-handle isolation guarantee. An observed connection can disappear immediately; it is not a health or authorization probe.
- `AutoConnectionSourceImpl.getOnlineLocators()` copies the last successful periodic locator-list response. A failed update leaves that list unchanged, and `stop()` does not clear it. Empty results before discovery and retained results after outage/destruction are valid. `ExplicitConnectionSourceImpl` reports no online locators for direct-server pools.
- Local runtime `isConnected()` describes the client's distributed-system lifecycle; it can remain true after a remote server disappears and does not imply any server connection.

`NativeEndpointObservationTests` adds four cases: unresolved-name preservation and snapshot isolation; safe translated failures and fatal errors; a separate real server process followed by forced server loss; and a separate locator-only process followed by destruction with last-observed locator data. Mockito only supplies mutable snapshots and injected failures. Existing server fixture construction now accepts a private process main class so the locator can share its control/cleanup protocol. These fixtures are confined to native-driver integration tests and introduce no server-hosting SPI.

The preceding 194-test result covers configured/durable identity, not this extension. The original user-run validation command follows; the corrected native rerun and result are recorded below. The agent has not run Gradle:

```bash
./gradlew --offline -DgemfireVersion=10.4.0-build.3056.df98c835 \
  :spring-data-gemfire-driver-spi:test \
  :spring-data-gemfire-driver-contract-tests:test \
  :spring-data-gemfire-driver-mock:check \
  :spring-data-gemfire-driver-10.4:integrationTest \
  --tests '*GemFire104DriverContractTests' \
  --tests '*NativeEndpointObservationTests' \
  --tests '*NativePoolTests' \
  --tests '*NativeDistributedCallbackTests' \
  verifyDriverCompatibility verifyMigrationBoundary
```

The first endpoint validation run failed during pool construction in both real-process tests: native 10.4 requires `pingInterval >= 1000ms`, while the fixtures supplied 100ms. Both now use 1000ms, retaining bounded condition polling. The native test run reported 61 tests with two failures; the corrected rerun passed as recorded below.

The user confirmed the corrected rerun succeeded on 2026-10-29. Local XML confirms **61 native tests, zero failures/errors/skips**: 46 shared driver-contract tests, four endpoint tests, four pool tests and seven distributed-callback tests. Suite timestamps span 2026-10-29T00:12:18Z–00:12:48Z. The reported successful command also includes driver compatibility and transitional migration-boundary checks; this is not full native-free compliance. The rerun command was:

```bash
./gradlew --offline -DgemfireVersion=10.4.0-build.3056.df98c835 \
  :spring-data-gemfire-driver-10.4:integrationTest \
  --tests '*GemFire104DriverContractTests' \
  --tests '*NativeEndpointObservationTests' \
  --tests '*NativePoolTests' \
  --tests '*NativeDistributedCallbackTests' \
  verifyDriverCompatibility verifyMigrationBoundary
```

Task 52 remains open for durable readiness, keepAlive, queue/registry lifecycle and consumer adoption. Task 23 remains open for statistics/logging, remaining runtime mappings and consumer adoption.
