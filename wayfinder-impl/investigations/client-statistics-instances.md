# Client statistics instances and values

Task 23 implementation checkpoint, 2026-10-29. User-run Java validation passed; this
slice does not complete task 23 or establish native-free Spring compliance.

## Bounded implementation

`DriverClientRuntime.getStatisticsFactory()` supplies a typed, injected factory.
`DriverStatisticsFactory` creates ordinary/atomic local instances and finds them
by type identity, text ID or numeric ID. `DriverStatistics` provides metadata,
explicit close, long/double access by ID/name/descriptor, generic numeric reads
and raw bits. The native driver delegates to the active distributed system's
statistics registry. The mock owns a per-runtime registry and in-memory values.
No native type escapes the SPI and no process-global mock registry is introduced.

Creation overloads become one method with explicit text/numeric IDs and atomic
selection. Null/empty text uses the runtime name, numeric zero uses the process
ID, and duplicate labels are permitted. Registry results are immutable membership
snapshots containing live handles. Unique IDs are runtime-local; cross-runtime
handle equality is not promised. Explicit close deregisters an instance while
preserving metadata and already obtained handles.

The mock preserves long-before-double ID spaces, live descriptor IDs, typed
validation ordering, long overflow and ordinary double raw bits. Atomic updates
retain concurrent increments; atomic double storage uses Java DoubleAdder, as
does the pinned native implementation. The mock serializes operations more
strongly than native ordinary statistics; it does not simulate data races.
Closed ID reads return zero and writes are ignored; name lookup and descriptor
type validation can still fail. Generic closed reads return Integer zero.
Absent non-atomic storage and empty atomic storage retain different failure
classifications. Deprecated int operations deliberately map to long storage:
widen writes/increments and explicitly narrow reads with an `(int)` cast.

## Evidence and tests

Source inspected: GemFire `10.4.0-build.3056.df98c835`, commit
`df98c8357afe85853d9a4776255d4f0e7cf064cb`, local sibling checkout `../gemfire`.
Relevant classes in `gemfire-core/src/main/java/org/apache/geode`:
`Statistics`, `StatisticsFactory`, `distributed/internal/InternalDistributedSystem`
and `internal/statistics/{StatisticsRegistry,StatisticsImpl,LocalStatisticsImpl,StripedStatisticsImpl,StatisticsTypeImpl}`.
The active runtime selects StatisticsRegistry (or DummyStatisticsRegistry when
disabled). The older AbstractStatisticsFactory has different numeric-ID behavior;
it is not the basis for this runtime's creation contract.

Seven shared contract tests cover creation/lookup, live snapshots, explicit close,
all value access forms, validation, raw bits, descriptor reuse, absent storage and
concurrent increments. Five default-method probes are registered. Two native
tests compare the active factory and deprecated int behavior directly, and check
failure translation/fatal error propagation with Mockito. One mock test covers
registry isolation and runtime-local IDs. User-run validation passed. Local JUnit reports contain 196 tests with zero failures/errors/skips: 28 SPI, 76 mock, 61 native and 31 core tests. Driver compatibility and transitional migration-boundary checks passed in the user-run command.

Fifty-nine previously missing native instance/value/factory rows now have partial
SPI/native/mock/test mappings. Five new SPI defaults have partial records. Frozen
scope and ownership remain unchanged: task 23 implementation, task 36 adoption.
The existing StatisticsFactory schema type mapping remains partial.

## Validation passed

The user successfully ran from the repository root:

```bash
./gradlew --offline -DgemfireVersion=10.4.0-build.3056.df98c835 \
  :spring-data-gemfire-driver-spi:test \
  :spring-data-gemfire-driver-mock:test \
  :spring-data-gemfire-driver-10.4:integrationTest \
  --tests '*GemFire104DriverContractTests' \
  --tests '*NativeStatisticsTypeTests' \
  --tests '*NativeStatisticsTests' \
  verifyDriverCompatibility verifyMigrationBoundary
```

The preceding descriptor/type slice's 179-test pass remains an earlier checkpoint.
Coverage generation/check and ownership/source/ID/scope validation passed. All
16 Python coverage tests passed, and `git diff --check` passed. These checks do
not compile or execute Java.

## Remaining boundaries

Supplier registration/replacement/initial evaluation, sampling, XML type loading,
logging and task-36 consumer adoption remain open. Statistics-disabled creation
and connection shutdown/reuse behavior need separate characterization before
claiming full instance lifecycle coverage; callers explicitly close instances in
this slice. The mock does not run a sampler, write archives or expose native
runtime-created statistics. Native registries can contain internal instances;
the mock lists only caller-created instances. These limits remain visible through
partial coverage rather than being silently counted as complete.
