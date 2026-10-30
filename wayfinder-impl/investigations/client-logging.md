# Plain-text client logging — task 23

Implementation checkpoint: 2026-10-29. **User-run Java validation passed on 2026-10-30.**

## Bounded contract

`DriverClientRuntime.getLog()` and `getSecurityLog()` expose `DriverLog` handles.
Eight typed levels, effective enablement, a nullable message/cause pair and three
method-tracing operations cover ordinary client logging without native types.
A null message represents throwable-only output and a null cause message-only
output. The adapter delegates to the original native writer so native destinations,
filtering and causes remain intact; runtime acquisition is lazy. Runtime failures
use the existing safe exception mapper; fatal errors propagate. Payloads explicitly
submitted for logging are not redacted. There is no promise of logger identity,
post-disconnect delivery, persistence or dynamic reconfiguration.

Pinned native evidence: GemFire `10.4.0-build.3056.df98c835`, commit
`df98c8357afe85853d9a4776255d4f0e7cf064cb`:

- `gemfire-core/src/main/java/org/apache/geode/distributed/internal/InternalDistributedSystem.java`:
  client runtime accessors return the system and security writers.
- `gemfire-core/src/main/java/org/apache/geode/logging/internal/log4j/LogWriterLogger.java`:
  actual client writers map CONFIG/INFO to INFO, FINER/FINEST to TRACE, FINE to DEBUG,
  WARNING to WARN, ERROR to ERROR and SEVERE to FATAL. Throwable-only overloads
  pass a null message. Traces produce ENTRY/RETURN/THROW class:method at FINER,
  retaining the throwable. Base `LogWriterImpl` alone is not sufficient evidence:
  it has different throwable-only defaults and does not describe the actual aliases.
- `gemfire-core/src/main/java/org/apache/geode/distributed/internal/DistributionConfigImpl.java`
  and `DistributionConfig.java`: independent system/security log levels default to CONFIG.
- `gemfire-log4j/src/main/java/org/apache/geode/logging/log4j/internal/impl/Log4jLoggingProvider.java`:
  security configuration supports its own threshold and shared appenders when no
  separate security file is configured.

The mock has connection-local, synchronized recorders. Named threshold aliases
preserve the pinned client filtering behavior. Immutable snapshots retain original
message/cause associations for test assertions, while record diagnostics redact
payloads. It does not write files, rotate logs, resolve custom/numeric levels,
use ambient logger configuration or combine system/security destinations.
Recording consumes memory until the connection/recorder is released; this is a
test recorder, not a production logging backend.

## Coverage and remaining work

64 existing API rows receive partial mappings: plain-text LogWriter/LogWriterI18n
operations, their type rows and the two DistributedSystem accessors. Frozen IDs,
client scope and task ownership do not change. The broader logging contract stays
incomplete: localized StringId messages/parameters, Handler bridging and conversion
semantics remain task 23 work. Spring consumers still require migration in task 36.
No task is closed by this checkpoint.

Two shared tests check the configured threshold and classified null-level failures;
seven default probes cover every new SPI default. Four native tests check all level
routes and nullable payloads, runtime failure translation, fatal propagation, real
trace output, independent thresholds and shared/separate destinations. Three mock
tests cover payload/tracing snapshots, filtering aliases and connection/channel
isolation.

## Validation

The first user-run Java validation reported 73 native tests with two failures:
the real-destination logging tests observed disabled FINEST/CONFIG levels. The
test report also states `Log4j API could not find a logging provider`. The driver
declared `gemfire-core` and `gemfire-version` but lacked `gemfire-log4j`, whose
provider applies native thresholds and configures file destinations. Added
`gemfire-log4j` to the driver's runtime dependencies, strictly pinned to the same
resolved native version; integration tests inherit that runtime dependency.
The forwarding/failure tests passed; the destination assertions remain unchanged.
The user confirmed successful validation of this dependency fix on 2026-10-30.
The validation command omitted `--offline` to allow resolution of the pinned
logging artifact.

Local checks passed on 2026-10-30: generated coverage is current, all 16 coverage
policy tests pass, and the validator accepts all 3,851 API rows with zero unresolved
scope entries. Ownership/source/ID checks and inventory negative controls pass;
`git diff --check` is clean. These checks do not validate Java compilation or runtime
behavior.

The user ran the following command and reported success. The agent did not run
Gradle, per the user's standing instruction:

```bash
./gradlew -DgemfireVersion=10.4.0-build.3056.df98c835 \
  :spring-data-gemfire-driver-spi:test \
  :spring-data-gemfire-driver-mock:test \
  :spring-data-gemfire-driver-10.4:integrationTest \
  --tests '*GemFire104DriverContractTests' \
  --tests '*NativeLogTests' \
  verifyDriverCompatibility verifyMigrationBoundary
```

The user-confirmed pass covers the selected Java tests, driver compatibility and
migration-boundary validation. The boundary gate remains transitional regression
protection; native-free acceptance is still outstanding.

## JUL Handler checkpoint — 2026-10-30

Historical checkpoint: the ALL/OFF limitation below is superseded by the next checkpoint.

**User-run Java validation passed on 2026-10-30**, including driver compatibility
and transitional migration-boundary checks. This validates the JUL Handler slice
in addition to the preceding plain-text checkpoint.

`DriverLog.getHandler()` now returns a fresh `DriverLogHandler` from both drivers.
The shared SPI implementation depends only on Java logging and the SPI. Each handler
starts at ALL with a SimpleFormatter; handler thresholds/filters run before formatting
and destination thresholds still apply. Record levels 300/400/500/700/800/900/950/1000
map to the eight SPI levels, including numeric aliases. The formatter's `formatMessage`
resolves resource bundles and parameters; the bridge prefixes `(tid=N msgId=N) ` and
preserves the attached cause. Null records are ignored; null messages produce only
the prefix. Flush is a no-op. Close releases the handler's destination and ignores
later records without closing the client or other handlers.

Pinned source evidence supplements the evidence above:
`gemfire-core/src/main/java/org/apache/geode/internal/logging/GemFireHandler.java`
uses the same prefix, legacy thread ID and `formatMessage` operation, forwards causes,
and constructs independent handlers. `GemFireFormatter.java` supplies a separate
full-record layout that publication does not use. `LogWriterLogger.log` and
`LogWriterLevel.find` accept native ALL/NONE in addition to the eight ordinary levels.
Their real destination behavior for ALL/OFF records remains to be established.

Compatibility limits are explicit. ALL/OFF are accepted as handler thresholds but
rejected as message levels, along with other unsupported numeric levels, using a
payload-free INVALID_ARGUMENT failure. The default formatter does not reproduce
GemFireFormatter's full-record layout. Native close leaves a null writer and can
fail on later publication; the SPI instead makes closed handlers inert. Formatting
and delivery RuntimeExceptions are reported through ErrorManager with FORMAT_FAILURE
and WRITE_FAILURE respectively; native GemFireHandler catches only GemFireException.
Fatal Errors propagate. Filter callback exceptions also propagate. These differences
prevent claiming complete native Handler equivalence.

Four SPI tests cover numeric aliases, bundles/parameters/IDs/causes, filtering,
custom formatting, closure, invalid levels and error reporting. One shared default
probe checks independent handlers. One native file-output test covers system/security
destinations and both filters; one mock test covers independent handlers and closure.
The two getHandler API rows move to partial, bringing this logging work to 66 partial
rows. Native ALL/OFF record behavior, full formatter compatibility, localized native
messages/conversion and task-36 consumer adoption remain outstanding. Task 23 stays open.

Static validation passed: generated coverage matches its policy, all 3,851 API rows
pass ownership/source/ID/scope checks, all 16 coverage tests pass, and `git diff --check`
is clean. The user confirmed successful Java validation for this checkpoint on
2026-10-30; driver compatibility and transitional migration-boundary checks also passed.

Successful user-run validation command:

```bash
./gradlew --offline -DgemfireVersion=10.4.0-build.3056.df98c835 \
  :spring-data-gemfire-driver-spi:test \
  :spring-data-gemfire-driver-mock:test \
  :spring-data-gemfire-driver-10.4:integrationTest \
  --tests '*GemFire104DriverContractTests' \
  --tests '*NativeLogTests' \
  verifyDriverCompatibility verifyMigrationBoundary
```

## ALL/OFF record checkpoint — 2026-10-30

**Validated by the user-run command below.** `DriverLog.Level` adds ALL and
OFF after the existing eight values. The JUL bridge accepts their exact numeric
values (`Integer.MIN_VALUE` and `Integer.MAX_VALUE`), including numeric aliases.
Other unsupported numeric levels still produce a payload-free INVALID_ARGUMENT.

Pinned source: `LogWriterLogger.log(int, String, Throwable)` resolves
`LogWriterLevel.find` through `LogWriterLevelConverter`, mapping ALL to Log4j ALL
and NONE to Log4j OFF. Native client runtime writers use this logger implementation.
The adapter delegates extreme records to that same numeric path and checks the
corresponding Log4j levels. Ordinary levels retain the previous LogWriter methods.
The mock assigns matching threshold order: ALL is the lowest record and OFF the
highest. Consequently an OFF record can pass a NONE/OFF destination threshold,
whereas setting a JUL handler threshold to OFF suppresses every record. The user reports successful runtime validation of these expectations.

SPI tests now cover ten numeric levels and adjacent unsupported values. The shared
contract includes OFF in the enabled set at ERROR. Mock tests cover exact payloads,
threshold counts and handler OFF filtering. A new native comparison test publishes
through both native and SPI handlers to separate system/security files at ALL, INFO
and NONE thresholds, checking records, causes, routing and handler suppression after
cache closure. No timers or polling are introduced.

The 66 logging API rows remain partial. Full native formatter compatibility,
localized native messages/conversion and remaining runtime mappings remain task 23;
Spring consumer adoption remains task 36. This extension does not claim native-free
migration acceptance or completed logging equivalence.

The first validation attempt failed compilation because the adapter now directly
uses Log4j API types, while GemFire core declares `log4j-api` with runtime scope.
The native driver now declares `log4j-api` as an implementation dependency using
the existing catalog version. The GemFire logging provider remains a runtime
dependency. The user subsequently reported success for the full command below, including
Java tests, driver compatibility and transitional migration-boundary validation.

Successful user-run validation:

```bash
./gradlew --offline -DgemfireVersion=10.4.0-build.3056.df98c835 \
  :spring-data-gemfire-driver-spi:test \
  :spring-data-gemfire-driver-mock:test \
  :spring-data-gemfire-driver-10.4:integrationTest \
  --tests '*GemFire104DriverContractTests' \
  --tests '*NativeLogTests' \
  verifyDriverCompatibility verifyMigrationBoundary
```

## Default JUL formatter checkpoint — 2026-10-30

**Validated by the user on 2026-10-30.** Native and mock handlers now share
`DriverLogFormatter`, a native-free default formatter. Its full `format(record)`
layout includes the leading newline, level, timestamp, current thread name and
hexadecimal ID, record thread ID, sequence number, raw message and throwable trace.
The timestamp formatter captures the default locale/time zone at construction;
formatting is synchronized because the date formatter is mutable. Message
continuations retain native two-space indentation, including trailing blank lines.

The implementation is adapted from the pinned native source:
`gemfire-core/src/main/java/org/apache/geode/internal/logging/GemFireFormatter.java`
and `LogWriterImpl.formatText` in the same package. The source checkout is
`/Users/uk016635/projects/gemfire`. Attribution and the Apache license header are
retained. Neither the SPI formatter nor the mock imports native classes.

Full formatting deliberately retains raw messages and bundle keys. Inherited
`formatMessage(record)` resolves bundles and parameters. Handler publication still
uses that message-only operation with its existing record-ID prefix, so this slice
does not change the publication payload or its cause forwarding. The documented
SPI close and error-reporting behavior remains unchanged.

Three SPI tests cover raw messages versus resolved parameters, thread identities,
multiline/null/empty output, resource bundles and throwable/cause/suppressed traces.
A native adapter test compares the entire formatted output against GemFireFormatter
for ten message shapes and four levels, with and without exception traces. The mock
test checks the default formatter alongside its existing resolved publication checks.

The 66 logging API rows remain partial. Localized native messages/conversion,
remaining runtime mappings and task-36 Spring consumer adoption remain open.
This checkpoint does not claim complete logging equivalence or native-free
migration acceptance. Static validation passed: generated coverage matches its
policy, all 3,851 API rows pass ownership/source/ID/scope checks, all 16 coverage
tests pass, and `git diff --check` is clean. The user confirmed that Java tests, driver compatibility and
transitional migration-boundary validation passed with this command:

```bash
./gradlew --offline -DgemfireVersion=10.4.0-build.3056.df98c835 \
  :spring-data-gemfire-driver-spi:test \
  :spring-data-gemfire-driver-mock:test \
  :spring-data-gemfire-driver-10.4:integrationTest \
  --tests '*GemFire104DriverContractTests' \
  --tests '*NativeLogTests' \
  verifyDriverCompatibility verifyMigrationBoundary
```

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

Native evidence is the pinned `10.4.0-build.3056.df98c835` source at
`df98c8357afe85853d9a4776255d4f0e7cf064cb`:

- `gemfire-core/src/main/java/org/apache/geode/i18n/StringId.java` retains the
  supplied ID/raw pattern, uses JDK `MessageFormat` for explicit formatting and
  captures the translation-bundle locale at subsystem initialization. The literal
  value is ID 1 / pattern `{0}`. Native localized output can include a message-ID
  prefix when a translation bundle is active.
- `gemfire-core/src/main/java/org/apache/geode/internal/i18n/AbstractStringIdResourceBundle.java`
  loads language-specific UTF-8 bundles and falls back to raw text for missing IDs
  or bundles. The mock intentionally has no native bundle dependency.
- `gemfire-core/src/main/java/org/apache/geode/logging/internal/log4j/LogWriterLogger.java`
  checks the level before localized formatting. No-argument, array-argument and
  single-object native overloads are represented by an explicit argument array;
  a single array-valued argument must be wrapped in a one-element `Object[]`.

Tests compare native and adapter formatting for quoting, missing/null arguments,
literal values and numeric patterns, verify cause forwarding, suppressed invalid
messages, fatal argument errors and real system/security output. The user reports that these tests passed on 2026-10-30. Separate locale processes and
translation-catalog presence/absence are not covered by this command. Diagnostic
`toString` is deliberately redacted; explicit `format` replaces native formatting
through `toString`. No native message object escapes the adapter.

Coverage regeneration and validation, all 16 scope-policy tests and `git diff
--check` passed. The user also confirmed the Java validation command below passed.

Run from the repository root:

```bash
./gradlew --offline -DgemfireVersion=10.4.0-build.3056.df98c835 \
  :spring-data-gemfire-driver-spi:test \
  :spring-data-gemfire-driver-mock:test \
  :spring-data-gemfire-driver-10.4:integrationTest \
  --tests '*GemFire104DriverContractTests' \
  --tests '*NativeLogTests' \
  verifyDriverCompatibility verifyMigrationBoundary
```

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

Source evidence: pinned native commit `df98c8357afe85853d9a4776255d4f0e7cf064cb`,
`gemfire-core/src/main/java/org/apache/geode/logging/internal/log4j/LogWriterLogger.java`,
`convertToLogWriterI18n` and `convertToLogWriter` both return `this`. The SPI does
not promise identity across separate runtime getter calls, dynamic reconfiguration,
or delivery after disconnect; eliminating conversion introduces no such promise.

Coverage generation/check, ownership/source/ID/scope validation, all 16 scope-policy
tests and `git diff --check` passed. The user confirmed the Java validation command below passed.

Run from the repository root:

```bash
./gradlew --offline -DgemfireVersion=10.4.0-build.3056.df98c835 \
  :spring-data-gemfire-driver-spi:test \
  :spring-data-gemfire-driver-mock:test \
  :spring-data-gemfire-driver-10.4:integrationTest \
  --tests '*GemFire104DriverContractTests' \
  --tests '*NativeLogTests' \
  verifyDriverCompatibility verifyMigrationBoundary
```

## Translation catalog checkpoint — 2026-10-30

Added three isolated JVM tests in `NativeLogTests`: shipped-runtime fallback
in Japanese and French, plus a Japanese catalog supplied only to its child JVM.
They compare native and adapter results for catalog entry 1028,
unknown-ID raw fallback, UTF-8 text and number formatting, then change the default
locale. The initialized catalog and message-ID prefix remain fixed, while numeric
formatting follows the current locale. Localized publication retains the original
cause. Process output is captured for failures and child processes are bounded
and cleaned up; the main test process never changes its locale.

Source evidence at pinned commit `df98c8357afe85853d9a4776255d4f0e7cf064cb`:
`StringId` initializes its catalog once using the default locale;
`AbstractStringIdResourceBundle` loads language-specific UTF-8 catalogs and falls
back to raw text for missing catalogs or IDs. The source-tree Japanese catalog
contains entry 1028 (`{0}を処理しています`); there is no French source catalog.
However, inspection of the resolved `gemfire-core-10.4.0-build.3056.df98c835.jar`
shows that it packages neither catalog. The first user run failed in the direct
native assertion: Japanese returned `Processing café`, not a translation.
The corrected tests explicitly assert that both shipped-runtime catalogs are
absent. A minimal UTF-8 Japanese catalog is created under the test temporary
directory and added only to the translation child process classpath; this tests
catalog support, not a translation shipped by GemFire. Pinned JAR bytecode confirms
once-only catalog initialization and per-call `MessageFormat` formatting.
A present catalog adds the
message-ID prefix even for an unknown ID. `MessageFormat` uses the current locale
for each formatting call. No SPI or production changes are needed, and the mock's
explicit raw-text-only contract remains unchanged.

Coverage regeneration/check, ownership/source/ID/scope validation, all 16
scope-policy tests and `git diff --check` passed.

**The user reported successful validation on 2026-10-30** of `NativeLogTests`,
driver compatibility and the transitional migration-boundary gate using the
command below. This validates the corrected catalog probes; the boundary result
is not native-free compliance. Task 23 remains open for remaining runtime
mappings; consumer adoption remains in task 36.

Validated command (run by the user from the repository root):

```bash
./gradlew --offline -DgemfireVersion=10.4.0-build.3056.df98c835 \
  :spring-data-gemfire-driver-10.4:integrationTest \
  --tests '*NativeLogTests' \
  verifyDriverCompatibility verifyMigrationBoundary
```
