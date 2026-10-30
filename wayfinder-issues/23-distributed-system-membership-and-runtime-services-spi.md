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

## Implementation checkpoint — 2026-10-29

Local client runtime identity/connectedness and separate cache cancellation handles now have SPI, native 10.4 and mock implementations, nine default-method probes and lifecycle/redaction/isolation tests. The user-run validation passed; local XML records 164 tests with zero failures/errors/skips. Eleven native rows remain partial for remaining runtime services and consumer adoption; no task completion is claimed. See [implementation evidence and bounded remaining work](../wayfinder-impl/investigations/client-runtime-handles.md). The native loner system criterion does not signal cache closure, so system-scoped cancellation remains a separate obligation. Configuration, endpoint connectivity, statistics/logging and task-36 adoption remain outstanding.

The validated extension implements `DriverClientRuntime.getCancellation()` separately from cache cancellation, including mock normal-closure behavior, a shared default probe, native lifecycle/reuse checks and injected native system-failure translation tests. It adds one partial native row (twelve total). User-run Java validation passed after declaring Mockito for integration tests; current JUnit reports contain 165 tests with zero failures/errors/skips, including 49 native tests. Compatibility and transitional boundary checks passed. The initial 164-test pass remains the earlier checkpoint.

The validated caller-owned cancellation extension implements `DriverClientRuntime.noCancellation()` and `createCancellation(BooleanSupplier)`, replacing NONE/native subclass construction and preserving protected JVM-failure handling privately. Shared probes cover caller-owned lifecycle, independence and causes; scoped Mockito native tests cover JVM failure ordering without altering native global failure state. Totals are twelve new defaults and sixteen partial native rows. User-run validation passed after replacing the flagged static factory with an instance constructor. Current JUnit reports contain 167 tests with zero failures/errors/skips, including 51 native tests. Compatibility and transitional boundary checks passed without a new exemption or baseline addition. Configuration, endpoint connectivity, statistics/logging and task-36 adoption remain outstanding.

Configuration readback passed user-run validation: `DriverClientRuntime.getProperties()` / `getSecurityProperties()` return detached string snapshots with redacted diagnostics and explicit copied-value access. The native driver reads effective configuration/defaults; the mock reports supplied properties only. Shared, SPI, mock and native tests cover copy isolation, security readback, defaults, closure and classified failures. Two more partial native rows/default probes bring task-23 totals to eighteen partial rows and fourteen defaults. Current JUnit reports contain 174 tests with zero failures/errors/skips, including 54 native tests; driver compatibility and transitional migration-boundary verification also passed. The earlier 167-test result predates this extension. Property-file resolution, configured/durable identity, endpoint connectivity, statistics/logging and task-36 adoption remain open; see the implementation evidence above.

Property-file discovery passed user-run Java validation: local JUnit XML records 179 tests with zero failures/errors/skips, including 57 native tests; driver compatibility and transitional migration-boundary checks also passed. Two injected `GemFireDriver` methods work before connection creation and return a redacted filename/optional-URL result. Native lookup is delegated; the mock reports unresolved defaults without ambient lookup. Shared probes and SPI/mock/native tests cover discovery and failure semantics. Eleven more partial rows bring task-23 totals to 29 partial rows and 16 new defaults. Legacy filename constants deliberately map to per-call discovery. See the implementation evidence for the command and remaining obligations; this is not task completion.

Configured/durable identity passed user-run validation: runtime name and legacy numeric ID, configured mock name/groups/durable metadata, immutable pool-qualified snapshots and explicit timeout/pool copies. Two new defaults and eleven durable-value mappings remain partial. The former SPI record becomes a final value class while preserving its two-argument constructor and accessors; this pre-first-release shape and semantic change is documented. Shared/native/mock/SPI tests cover configuration, snapshot isolation, native ambient pool capture, null/empty IDs and failure behavior. User-run validation passed: 194 tests with zero failures/errors/skips, including 67 native tests, plus driver compatibility and transitional migration-boundary checks. The earlier 179-test result describes the property-discovery checkpoint. See [the bounded checkpoint and user-run command](../wayfinder-impl/investigations/client-runtime-handles.md#configured-and-durable-identity--validation-passed). Endpoint connectivity, statistics/logging, remote durable lifecycle and task-36 adoption remain outstanding.

Endpoint observations passed user-run validation: 61 native tests with zero failures/errors/skips, plus driver compatibility and transitional migration-boundary verification. `DriverConnection.getCurrentServers()` and `DriverPool.onlineLocators()` expose immutable detached snapshots; the non-networking mock reports empty results. The two native API rows remain owned by task 52, with task 35 adoption. Native tests cover real server loss, locator discovery/staleness, snapshot isolation and failure translation. See [the checkpoint and validation command](../wayfinder-impl/investigations/client-runtime-handles.md#endpoint-observations--validation-passed). The preceding 194-test pass does not validate this extension. Statistics/logging, remaining runtime mappings and consumer adoption keep task 23 open.

## Statistics descriptors and named types — validation passed

The next bounded slice adds native-free descriptor configuration/handles and named type registration/lookup through `DriverClientRuntime.getStatisticsTypes()`. Native and mock adapters preserve metadata, long-before-double ID assignment, declaration order, live descriptor reuse, registration conflicts and post-close reads. Four defaults have contract probes; 60 native rows now have partial implementation mappings. User-run validation passed. Local JUnit reports contain 179 tests with zero failures/errors/skips: 28 SPI, 68 mock, 52 native and 31 core tests. Driver compatibility and transitional migration-boundary checks passed in the user-run command. See [the checkpoint and user-run command](../wayfinder-impl/investigations/client-statistics-types.md). Task 23 remains open for statistics instances/values/suppliers, XML type loading, logging, remaining runtime mappings and task-36 adoption.


## Statistics instances and values — validation passed

Added typed instance creation and registry lookup, metadata, explicit close,
long/double access by ID/name/descriptor, generic values and raw bits to the SPI,
native adapter and mock. Seven shared contract tests, five default probes, native
factory/deprecated-int comparisons and mock isolation coverage passed.
Fifty-nine native rows move from missing to partial. User-run validation passed. Local JUnit reports contain 196 tests with zero failures/errors/skips: 28 SPI, 76 mock, 61 native and 31 core tests. Driver compatibility and transitional migration-boundary checks passed in the user-run command.
The preceding 179-test pass remains an earlier checkpoint. See [implementation,
limits and the user-run command](../wayfinder-impl/investigations/client-statistics-instances.md).
Suppliers/sampling, XML loading, logging, disabled-mode/shutdown characterization
and task-36 adoption remain open. Task 23 is not complete.


## Statistics suppliers and sampling — validation passed

Added nine supplier defaults (int/long/double by ID/name/descriptor), direct native
adaptation and mock registration/sampling. Int and long callbacks retain separate
identity while sharing long storage. Immediate evaluation, Exception versus Error
handling, null replacement, validation ordering, closed registration and registry
sampling have four shared tests and nine default probes. A native test awaits real
scheduled publication; a mock test verifies explicit sampling isolation. Nine
native rows now have partial mappings; no frozen IDs or ownership changed.
See [implementation, limitations and the user-run command](../wayfinder-impl/investigations/client-statistics-suppliers.md).
User-run validation passed. Local JUnit reports contain 204 tests with zero failures/errors/skips: 28 SPI, 81 mock, 64 native and 31 core tests. Driver compatibility and transitional migration-boundary checks passed in the user-run command.
Task 23 remains open for XML type loading, logging,
statistics-disabled/shutdown characterization and task-36 adoption.


## Statistics XML type loading — validation passed

Added `DriverStatisticsTypes.createTypesFromXml(Reader)`, native delegation and a
native-free mock parser. Ordered, nontransactional registration, metadata/defaults,
LONG int storage, registry reuse and failure semantics have four shared tests and
one default probe. Two native adapter tests cover delegation/translation; two mock
tests cover registry isolation and external-resolution rejection. Three native
rows receive partial mappings; frozen IDs and ownership are unchanged.
See [implementation, limits and user-run validation](../wayfinder-impl/investigations/client-statistics-xml.md).
User-run validation passed. Local JUnit reports contain 215 tests with zero failures/errors/skips: 28 SPI, 87 mock, 69 native and 31 core tests. Driver compatibility and transitional migration-boundary checks passed in the user-run command.
Task 23 remains
open for logging, disabled-statistics/shutdown behavior, remaining runtime mappings
and task-36 adoption.

## Statistics lifecycle checkpoint — 2026-10-29

Two shared tests and one native test now cover enabled-statistics shutdown,
retained factory/instance handles, explicit cleanup, reconnect isolation and
compatible native connection registry sharing. SPI Javadoc records the contract;
no production behavior change is needed. User-run validation passed. Local JUnit reports contain 218 tests with zero failures/errors/skips: 28 SPI, 89 mock, 70 native and 31 core tests. Driver compatibility and transitional migration-boundary checks passed in the user-run command. The prior
215-test result applies to the XML slice only. See [source evidence and the
user-run command](../wayfinder-impl/investigations/client-statistics-lifecycle.md).
Coverage statuses remain partial. Logging, statistics-disabled mode, remaining
runtime mappings and task-36 adoption keep task 23 open.


## Disabled-statistics checkpoint — 2026-10-29

Implemented inert statistics in the mock through explicit per-driver configuration,
with the existing native adapter delegating to the native disabled registry.
Three shared contract tests plus native runtime-mode and mock isolation tests cover
exact labels, zero values, ignored suppliers, empty registries, no-op closure and
retained factories. SPI Javadoc distinguishes disabled statistics from disabled
sampling. User-run validation passed. Local JUnit reports contain 226 tests with zero failures/errors/skips: 28 SPI, 93 mock, 74 native and 31 core tests. Driver compatibility and transitional migration-boundary checks passed in the user-run command. The preceding 218-test result describes the lifecycle slice. See [source evidence, limits and user-run command](../wayfinder-impl/investigations/client-statistics-disabled.md).
Coverage remains partial and task 23 remains open for logging, remaining runtime
mappings and task-36 consumer adoption.


## Plain-text logging checkpoint — 2026-10-29

Implemented typed system/security log access, eight level checks, nullable
message/cause forwarding and method tracing. The native adapter uses configured
writers; the mock uses connection-local recorders with native level aliases.
64 API rows receive partial mappings. Two shared tests, seven default probes,
four native tests and three mock tests cover the new contract.
**User-run Java validation passed on 2026-10-30**, including driver compatibility
and the transitional migration-boundary gate. The driver now includes the pinned
`gemfire-log4j` runtime provider required for native thresholds and destinations.
See [evidence, limits and validation command](../wayfinder-impl/investigations/client-logging.md).
Task 23 remains open for localized messages, Handler bridging/conversion, remaining
runtime mappings and task-36 consumer adoption.

## JUL Handler checkpoint — 2026-10-30

Implemented independent native-free Java logging handlers for native and mock logs.
The shared bridge covers eight numeric levels, bundle/parameter formatting, record
IDs, causes, handler/destination filtering and inert closure. Four SPI tests, one
shared default probe, one native file-output test and one mock isolation test are
added. Two getHandler API rows remain partial. **User-run Java validation passed
on 2026-10-30**, including driver compatibility and transitional migration-boundary
checks. This validates the Handler extension as well as the preceding plain-text slice.
See [contract, source evidence, compatibility limits and command](../wayfinder-impl/investigations/client-logging.md#jul-handler-checkpoint--2026-10-30).
Task 23 remains open for native ALL/OFF record output, full native formatter
compatibility, localized native messages/conversion and remaining runtime mappings.
Spring consumer adoption remains task 36.

## ALL/OFF logging record checkpoint — 2026-10-30

Added ALL/OFF record levels to the SPI, native adapter, mock and shared JUL bridge.
Updated SPI/shared/mock coverage and a native system/security comparison test cover
ALL, INFO and NONE destination thresholds and OFF handler suppression.
**User-run Java tests, driver compatibility and transitional migration-boundary validation passed.** The native driver now explicitly declares the Log4j API compile dependency required by this adapter. The 66 logging API rows remain partial;
full formatter compatibility, localized messages/conversion, remaining runtime
mappings and task-36 consumer adoption remain outstanding.
See [source evidence, contract and validation command](../wayfinder-impl/investigations/client-logging.md#alloff-record-checkpoint--2026-10-30).

## Default JUL formatter checkpoint — 2026-10-30

Implemented a shared native-free default formatter for native and mock handlers.
It preserves raw messages, native timestamps, current and record thread identity,
sequence numbers, multiline indentation and throwable traces. Handler publication
continues to resolve bundles and parameters separately. Three SPI tests, one native
formatter comparison test and mock assertions cover the extension.
**User-run Java tests, driver compatibility and transitional migration-boundary
checks passed on 2026-10-30.** The 66 logging API rows remain
partial. Localized native messages/conversion, remaining runtime mappings and
Spring consumer adoption in task 36 remain outstanding; task 23 stays open.
See [source evidence, limits and validation command](../wayfinder-impl/investigations/client-logging.md#default-jul-formatter-checkpoint--2026-10-30).

## Localized logging checkpoint — 2026-10-30

Added native-free `DriverLogMessage` values and `DriverLog.createMessage` /
`logLocalized` defaults, with native and mock implementations. Message IDs and raw
patterns remain separate from explicit formatting and redacted diagnostics.
Publication checks the destination level before touching the message or arguments,
retains the original cause, classifies runtime failures and propagates fatal errors.
The native adapter delegates translation lookup to `StringId`; the mock formats raw
text without a catalog or message-ID prefix.

Two shared contract tests, two default-method probes, two native comparison tests,
an extension to the real system/security destination test and one mock test cover
the slice. **User-run Java tests, driver compatibility and transitional migration-boundary validation passed on 2026-10-30.** Thirty localized
logging rows and nine message-value rows now have partial mappings (105 logging /
message rows mapped partial in total). Conversion semantics, alternate-locale
catalog evidence, remaining runtime mappings and task-36 adoption remain open.
Task 23 is not complete.

## Logger conversion checkpoint — 2026-10-30

Native `LogWriter.convertToLogWriterI18n` and `LogWriterI18n.convertToLogWriter`
return the same logger object. `DriverLog` already unifies plain and localized
publication, so consumers retain the same handle and call `log` or `logLocalized`.
Its contract now explicitly states that these operations share a destination and
threshold. No extra conversion method or native logger type enters the SPI.

A native test checks both conversion identities, plain/localized output, causes,
filtering and separate system/security destinations. A mock test checks mixed
plain/localized publication against the same threshold and captured causes.
The two conversion rows receive partial mappings, bringing logging/message rows
to 107. **User-reported Java tests, driver compatibility and transitional migration-boundary validation passed on 2026-10-30.** Alternate-locale catalog evidence,
remaining runtime mappings and task-36 consumer adoption remain open; task 23 is
not complete.

See [source evidence and validation command](../wayfinder-impl/investigations/client-logging.md#logger-conversion-checkpoint--2026-10-30).

## Translation catalog checkpoint — 2026-10-30

Three isolated JVM tests now cover shipped Japanese/French raw fallback and a
Japanese test catalog supplied only to its child process,
unknown-ID fallback, UTF-8 text, cause forwarding and locale changes after native
catalog initialization. No new production API is required. **The user reported
successful validation on 2026-10-30** of the corrected `NativeLogTests`, driver
compatibility and the transitional migration-boundary gate. The first run exposed
a test assumption: the pinned JAR omits the Japanese source-tree catalog. The
corrected probes now pass. This boundary result is not native-free compliance.
Task 23 remains open for remaining runtime mappings; Spring adoption remains in
task 36.

See [source evidence and validation command](../wayfinder-impl/investigations/client-logging.md#translation-catalog-checkpoint--2026-10-30).

Remaining mapping inventory after this checkpoint: 288 required-client rows are
partial and 95 are missing: the `ConfigurationProperties` type and 70 constants, 10
`DistributedSystem` members, six `RoleException` members, six
`StatisticsDisabledException` members and two `LeaseExpiredException` members.
These counts describe mapping status, not 95 separate implementation tasks.
The next bounded pass should reconcile configuration constants in a batch, then
resolve the runtime methods and exception mappings against existing SPI behavior.

## Client configuration names checkpoint — 2026-10-30

Added native-free `ClientConfigurationProperties` with the 70 client property
names retained by task 72. A native comparison test checks exact scope, field
modifiers and values against the pinned driver. Existing native/mock runtime
tests use the constants; SPI tests cover TLS-password copy isolation and redaction.
**User-reported validation passed:** targeted SPI configuration, mock/native runtime
and native property-name tests, driver compatibility and the transitional migration
boundary.

The type and constants receive 71 partial mappings, bringing task 23 to 359
partial and 24 missing required-client rows. Names alone do not complete
validation, TLS/authentication behavior, typed assembly or Spring adoption;
tasks 23/24/29/35/36 remain open. Next, reconcile the remaining 10
`DistributedSystem`, six `RoleException`, six `StatisticsDisabledException` and
two `LeaseExpiredException` rows against existing SPI behavior.

See [scope, limits and user-run command](../wayfinder-impl/investigations/client-configuration-properties.md).

## Runtime lifecycle and failures checkpoint — 2026-10-30

Reconciled the final 24 missing required-client rows against existing SPI behavior:
10 runtime/lifecycle rows, six role-failure members, six statistics-failure members
and two lease-failure members. The three exception type mappings also gain explicit
evidence. Task 23 now has **383 partial and zero missing required-client rows**.

Owned connections replace public native system construction/lookup; native global
registration remains private. Native compatible-cache reuse and mock connection
isolation remain deliberate differences. Added mapper constructor/cause/translation
tests, a native member-ID alias assertion and a native-free system-failure test.
**User-reported validation passed on 2026-10-30:** targeted Java tests, driver compatibility and the transitional migration boundary. Mapper tests do not prove remote wire propagation.
No production API is added. This task stays open for behavior verification and
explicit handoffs; task 36 owns Spring adoption.

See [evidence, limits and user-run command](../wayfinder-impl/investigations/client-runtime-lifecycle-and-failures.md).

## Monitoring configuration checkpoint — 2026-10-30

Added native tests for effective readback of the 14 monitoring properties,
500-to-1,000 ms sampling-rate normalization and nine invalid-value cases at the
connection-creation boundary. A mock test records defensive snapshots of supplied
sampling/archive/thread-monitor values without native normalization or ranges.
Mock log thresholds retain their existing validation.

**User-reported validation passed:** focused mock/native runtime and configuration
tests and the transitional migration boundary. No production API or mapping count
changes: 383 partial, zero missing required-client rows; this task remains open.
Archive rotation and watchdog activation are not established by these tests.
Typed assembly remains in task 29; TLS/authentication in task 24; Spring adoption
in tasks 35/36.

See [evidence, limits and user-run command](../wayfinder-impl/investigations/client-configuration-properties.md#monitoring-configuration-checkpoint--2026-10-30).
