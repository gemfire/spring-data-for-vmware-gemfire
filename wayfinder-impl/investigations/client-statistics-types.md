# Client statistics descriptors and named types

Task 23 bounded implementation checkpoint, 2026-10-29. User-run Java validation passed.
The preceding endpoint-observation result remains a separate checkpoint.

## Implemented scope

`DriverClientRuntime.getStatisticsTypes()` exposes an injected schema factory.
`StatisticDescriptorConfiguration`, `DriverStatisticDescriptor`,
`DriverStatisticsType` and `DriverStatisticsTypes` carry no native types.
Both the 10.4 adapter and mock implement descriptor construction, type creation
and name lookup. Four SPI defaults have shared contract probes. Sixty native API
rows, including inherited factory methods, now have partial mappings.

The native adapter delegates through the connection's `DistributedSystem`.
Native registry lifetime and visibility are retained; the mock registry belongs
to each connection. Neither implementation adds sampling or statistic instances.
Native failures are classified with safe outer messages; fatal errors propagate.

## Semantics and evidence

Source evidence is the pinned GemFire 10.4.0-build.3056.df98c835 source at
df98c8357afe85853d9a4776255d4f0e7cf064cb: `StatisticsTypeFactory`,
`StatisticDescriptorImpl`, `StatisticsTypeImpl` and `StatisticsTypeFactoryImpl`.

- Long and double counters/gauges retain metadata and explicit `largerBetter`.
  Defaults are true for counters and false for gauges. Null descriptor
  descriptions/units become empty strings. Deprecated int factories use long
  storage, represented by `ValueType.LONG`.
- Descriptor IDs start at -1. Type creation assigns long IDs first, then double
  IDs, while retaining declaration order. Reusing descriptors can change IDs
  visible through older types; callers should use fresh descriptors per type.
- Input lists are copied and returned lists are immutable; descriptor handles
  remain live. This avoids exposing a native mutable array through the SPI.
- Equal definitions reuse a registered type; conflicting definitions, duplicate
  names and more than 254 descriptors fail with INVALID_ARGUMENT. Candidate
  construction can mutate descriptor IDs before registration fails.
- Type descriptions retain native null behavior rather than being normalized.
  A repeated definition with a null description can fail native equality and is
  translated to INVALID_ARGUMENT. Descriptor comparison orders names lexically.
- Captured schema handles remain readable after connection closure. The mock
  deliberately does not emulate the native process-wide registry.

## Coverage and validation

Shared contract tests cover metadata/flags, order and IDs, equality, conflicting
definitions, invalid inputs, the descriptor limit, copy isolation, live ID reuse
and post-close reads. Native tests compare deprecated int factory behavior and
native registry results, and exercise translated failures/foreign handles/fatal
errors using Mockito. Mock tests verify registry isolation across connections.

Coverage generation/check, inventory validation and all 16 policy tests passed.
`git diff --check` passed. User-run validation passed. Local JUnit reports contain 179 tests with zero failures/errors/skips: 28 SPI, 68 mock, 52 native and 31 core tests. Driver compatibility and transitional migration-boundary checks passed in the user-run command.
The native reports include 50 shared contract tests and two statistics-type
tests (2026-10-29T00:38:53–00:39:12Z). These counts describe current reports,
including tasks that Gradle may have reused as up-to-date. The boundary pass is
against the transitional baseline, not native-free compliance.

Validated user-run command (agent did not execute Gradle):

```sh
./gradlew --offline -DgemfireVersion=10.4.0-build.3056.df98c835 \
  :spring-data-gemfire-driver-spi:test \
  :spring-data-gemfire-driver-mock:test \
  :spring-data-gemfire-driver-10.4:integrationTest \
  --tests '*GemFire104DriverContractTests' \
  --tests '*NativeStatisticsTypeTests' \
  verifyDriverCompatibility verifyMigrationBoundary
```

Task 23 remains open. Statistics instances, values, atomicity, suppliers,
sampling/closure, XML type loading, logging and remaining runtime mappings are
outside this checkpoint. Task 36 owns Spring consumer adoption. Partial mapping
does not claim full native API parity or native-free Spring compliance.
