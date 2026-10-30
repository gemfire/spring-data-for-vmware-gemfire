# Client statistics XML type loading

Task 23 implementation checkpoint, 2026-10-29. Java validation passed.
User-run validation passed. Local JUnit reports contain 215 tests with zero failures/errors/skips: 28 SPI, 87 mock, 69 native and 31 core tests. Driver compatibility and transitional migration-boundary checks passed in the user-run command.

## Bounded implementation

`DriverStatisticsTypes.createTypesFromXml(Reader)` loads local statistics schemas
into the existing registry and returns an immutable list in document order. The
default fails explicitly for drivers that have not introduced this method. The
native adapter delegates to the injected native factory and wraps its results;
the mock parses with JDK XML APIs and registers through its connection-owned
statistics factory. No native classes cross the SPI.

Omitted storage and `int` storage use LONG on 10.4. Counters default to true;
largerBetter defaults to the counter flag. Flags and storage are case insensitive.
Description/unit whitespace and first text-node behavior follow the native parser.
Descriptor declaration order is retained while LONG IDs precede DOUBLE IDs.
Equal definitions reuse registered types. Registration is sequential: a later
conflict leaves earlier types registered. XML syntax is parsed before registration,
so malformed XML creates no types. Empty documents with a statistics root and
types without descriptors are accepted by the native nonvalidating parser.

Native syntax/read errors are wrapped configuration failures (SYSTEM_FAILURE),
while definition conflicts are INVALID_ARGUMENT. A checked IOException emitted
directly by another native factory implementation follows the existing UNKNOWN
mapping. Native schema assertion Errors propagate; the mock uses AssertionError.
The parser may close the Reader; callers must ensure closure on every path.

The mock resolves the known statistics DTD in memory and rejects other external
entities without file/network access. It enables secure processing and disables
external DTD/schema access. This deliberately does not reproduce arbitrary native
parser-provider behavior, resource failures, entity expansion limits or the native
resolver's null-public-ID failure for SYSTEM-only declarations. Null-reader
behavior is not standardized by this slice. Use trusted configuration XML.

## Evidence and tests

Pinned source: GemFire `10.4.0-build.3056.df98c835`, commit
`df98c8357afe85853d9a4776255d4f0e7cf064cb`, sibling checkout `../gemfire`:
`gemfire-core/src/main/java/org/apache/geode/internal/statistics/StatisticsTypeXml.java`,
`StatisticsTypeFactoryImpl.java`, `StatisticsTypeImpl.java`, the public
`StatisticsTypeFactory` API and `gemfire-core/src/main/resources/org/apache/geode/statisticsType.dtd`.

Four shared contract tests cover plain/DOCTYPE XML, defaults, entity/CDATA text,
metadata, IDs, registry reuse, usable instances, partial registration, duplicate
descriptors, syntax/Reader failures and schema assertions. One default-method
probe verifies availability. Two native adapter tests verify Reader delegation,
type wrapping, checked/native failure translation and Error propagation. Two mock
tests cover isolation, captured handles after close and rejection of external
definitions/entities before registration.

Three retained native rows and one SPI default receive partial mappings. Frozen
scope and owners remain unchanged: task 23 implementation, task 36 adoption.
Logging, disabled-statistics creation, connection shutdown/reuse, remaining runtime
mappings and Spring consumer migration stay open. This does not complete task 23.

## User-run validation

Coverage generation/check, ownership/source/ID/scope validation, all 16 Python
coverage tests and `git diff --check` passed. Java validation also passed.

The user successfully ran this command from the repository root:

```bash
./gradlew --offline -DgemfireVersion=10.4.0-build.3056.df98c835 \
  :spring-data-gemfire-driver-spi:test \
  :spring-data-gemfire-driver-mock:test \
  :spring-data-gemfire-driver-10.4:integrationTest \
  --tests '*GemFire104DriverContractTests' \
  --tests '*NativeStatisticsTypeTests' \
  verifyDriverCompatibility verifyMigrationBoundary
```

The migration boundary is transitional regression protection, not native-free compliance.
