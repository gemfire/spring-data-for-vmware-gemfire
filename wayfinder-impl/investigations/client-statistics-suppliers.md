# Client statistics suppliers and sampling

Task 23 implementation checkpoint, 2026-10-29. Java validation passed.
User-run validation passed. Local JUnit reports contain 204 tests with zero failures/errors/skips: 28 SPI, 81 mock, 64 native and 31 core tests. Driver compatibility and transitional migration-boundary checks passed in the user-run command.

## Bounded implementation

`DriverStatistics` adds nine default methods for int/long/double suppliers by ID,
name or descriptor. The native driver delegates without wrapping callbacks, so
replacement returns the original supplier by identity. Defaults fail explicitly
for drivers that have not implemented this extension. The mock maintains separate
int, long and double registrations; int and long share long value storage.

Registration validates the identifier, invokes the supplier immediately, writes
its initial value and then replaces the registration. Supplier Exceptions produce
zero; Errors propagate without replacing the previous registration or value.
Negative long IDs pass native type validation and fail storage access after the
callback; the mock preserves that ordering. Null is stored as a failing callback,
not interpreted as removal. Reads never invoke suppliers.

Sampling visits int, long, then double registrations, taking a separate snapshot
for each kind. It catches callback Throwables, preserves the previous value on
failure and continues. Close removes an instance from future registry samples;
it does not wait for already captured callbacks. Registering on a closed instance
still validates and evaluates but public reads remain zero.

The native runtime keeps its existing scheduled sampler and configuration. The
mock's public `MockStatisticsFactory.sampleSuppliers()` is an explicit test control
on the connection's factory. It samples that registry once and returns the number
of callback failures, regardless of automatic sampling settings. The mock has no
background timer, native sampler metrics, archive writer or native internal stats.
Callbacks run outside mock locks so they can reenter statistics operations.
Native supplier warning/deduplication logging remains part of the logging slice;
the mock currently reports failures through the sampling count only.

## Evidence and tests

Pinned source: GemFire `10.4.0-build.3056.df98c835`, commit
`df98c8357afe85853d9a4776255d4f0e7cf064cb`, sibling checkout `../gemfire`:
`gemfire-core/src/main/java/org/apache/geode/internal/statistics/StatisticsImpl.java`,
`StatisticsTypeImpl.java`, `CallbackSampler.java`, and the public `Statistics` API.
The implementation's immediate evaluation, null behavior and Exception/Throwable
handling govern compatibility where interface prose differs.

Four shared contract tests cover all nine overloads, previous supplier identity,
initial values, reads without reevaluation, int/long coexistence, manual sampling,
nulls, exceptions/errors, validation order and close. Nine default-method probes
check implementation availability. The native contract hook privately invokes the
native instance sampler; the mock hook invokes its explicit factory control.
The native scheduled test waits for a later callback to observe an earlier sample's
published value, using a bounded latch rather than sleeps or callback-entry races.
The mock isolation test checks that sampling one runtime leaves another untouched
and excludes closed instances.

Nine retained native rows and nine new SPI defaults receive partial mappings.
Frozen scope and owners remain unchanged: task 23 implementation, task 36 adoption.
Statistics XML loading, logging, disabled-statistics creation, connection shutdown/
reuse and Spring consumer migration remain open. This does not complete task 23.

## Validation passed

The user successfully ran this command from the repository root:

```bash
./gradlew --offline -DgemfireVersion=10.4.0-build.3056.df98c835 \
  :spring-data-gemfire-driver-spi:test \
  :spring-data-gemfire-driver-mock:test \
  :spring-data-gemfire-driver-10.4:integrationTest \
  --tests '*GemFire104DriverContractTests' \
  --tests '*NativeStatisticsTests' \
  --tests '*NativeStatisticsSupplierTests' \
  verifyDriverCompatibility verifyMigrationBoundary
```

Coverage generation/check, ownership/source/ID/scope validation, all 16 Python
coverage tests and `git diff --check` passed. The user-run Java validation also
passed, including driver compatibility and the transitional migration boundary.
The boundary result is regression protection against the transitional baseline;
it does not establish native-free compliance.
