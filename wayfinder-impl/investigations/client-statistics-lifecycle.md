# Client statistics shutdown, reconnect and reuse

## Scope and source evidence

This task-23 slice defines the lifecycle of caller-created statistics when
statistics are enabled. It adds two shared contract tests and one native test;
the existing native and mock implementations require no behavior change.
User-run validation passed. Local JUnit reports contain 218 tests with zero failures/errors/skips: 28 SPI, 89 mock, 70 native and 31 core tests. Driver compatibility and transitional migration-boundary checks passed in the user-run command. The earlier 215-test result describes the XML slice.

Evidence is the local GemFire 10.4 source at
`df98c8357afe85853d9a4776255d4f0e7cf064cb`:

- `InternalDistributedSystem.disconnect` stops the sampler without closing all
  caller-created statistics. Its statistics creation methods continue delegating
  to the captured registry without a connectedness guard.
- `StatisticsRegistry` owns its instance list and ID sequence. Explicit instance
  closure removes an entry; creation and lookup do not require a connected system.
- `HostStatSampler.stop` stops callback sampling and the sampling thread.
- The normal registry uses the process-wide `StatisticsTypeFactoryImpl`;
  reconnect replaces the instance registry while retaining named types.

## Contract and coverage

Captured factories and caller-created instances remain usable after connection
shutdown. Values remain readable and writable, and captured factories can create
additional instances. Callers must explicitly close instances to deregister them.
Lookup results snapshot membership while retaining live handles.

A newly connected runtime has a separate instance registry. Closing an old
instance must not remove its replacement from the new registry. Compatible
native connections reuse a cache and share its registry; closing either shuts
that cache down. Native type registration remains process-wide. The mock retains
its documented per-connection type and instance isolation and explicit sampling.

`StatisticsContract` now exercises shutdown for both atomic and ordinary
instances, post-shutdown creation, snapshot behavior, explicit cleanup and
reconnect isolation. `NativeStatisticsTests` additionally exercises compatible
connection aliases and named-type reuse across reconnect. Unique type/text names
prevent collisions with runtime-owned statistics; resource scopes clean up every
created instance and connection.

SPI Javadoc now records these enabled-statistics lifecycle rules. No new default
methods, API classifications or completion owners are introduced. Existing rows
remain partial. Logging, statistics-disabled mode, remaining runtime mappings and
task-36 consumer adoption remain open; task 23 is not complete.

## Validation

Coverage generation/check, ownership/source/ID/scope validation, all 16 Python
coverage tests and `git diff --check` passed.

The user successfully ran this command from the repository root:

```bash
./gradlew --offline -DgemfireVersion=10.4.0-build.3056.df98c835 \
  :spring-data-gemfire-driver-spi:test \
  :spring-data-gemfire-driver-mock:test \
  :spring-data-gemfire-driver-10.4:integrationTest \
  --tests '*GemFire104DriverContractTests' \
  --tests '*NativeStatisticsTests' \
  verifyDriverCompatibility verifyMigrationBoundary
```

The migration boundary remains transitional regression protection, not native-free
compliance.
