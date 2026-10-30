# Client statistics disabled mode

## Scope and evidence

This task-23 slice implements disabled statistics in the mock and exercises the
existing native delegation through a shared contract. User-run validation passed. Local JUnit reports contain 226 tests with zero failures/errors/skips: 28 SPI, 93 mock, 74 native and 31 core tests. Driver compatibility and transitional migration-boundary checks passed in the user-run command.
The preceding 218-test result describes the lifecycle slice.

Evidence is the local GemFire 10.4 source at
`df98c8357afe85853d9a4776255d4f0e7cf064cb`:

- `InternalDistributedSystem` captures `gemfire.statsDisabled` for each runtime,
  selects `DummyStatisticsRegistry`, and omits the sampler when disabled.
- `DummyStatisticsRegistry` returns `DummyStatisticsImpl` for ordinary and atomic
  creation and never registers instances. Its instance list is always empty.
- `DummyStatisticsImpl` preserves supplied type/text/numeric metadata, reports
  unique ID zero and atomic true, and always reports closed false. Close is a no-op.
  Values and raw bits are zero, generic reads return Integer zero, and all supplier
  registrations return null without evaluating or retaining the callback.
- Value operations ignore names/IDs and descriptor value kinds. Explicit
  `nameToId`/`nameToDescriptor` still resolve against the type. The existing driver
  adapter additionally checks descriptor provenance before invoking native code.

## Implementation and tests

`MockGemFireDriver(false)` explicitly selects disabled statistics for its
connections. The default constructor retains enabled statistics. The mock reads
no ambient JVM flag and each connection retains independent state. The setting
is passed into its runtime/factory; `MockDisabledStatistics` preserves inert
behavior without registration or supplier storage. Native production code needs
no change because the adapter already delegates to the native factory/handle.

`DisabledStatisticsContract` adds three shared tests covering metadata for both
creation modes, null/empty labels, invalid selectors, descriptor validation,
zero values, all nine supplier overloads, explicit closure and retained factories.
`NativeDisabledStatisticsTests` also verifies that compatible runtime reuse keeps
its captured mode while reconnect can change modes. It restores the JVM property
and runs under the integration task's existing per-class fork policy.
`MockDisabledStatisticsTests` verifies explicit mode isolation and that disabled
instances never participate in manual sampling, including after shutdown.

Disabling sampling alone still allows values and registration. Disabled-statistics
mode is a runtime creation choice, not a mutable SPI property or a new public
server-side API. Existing coverage statuses remain partial; no frozen IDs or
ownership change. Logging, remaining runtime mappings and task-36 consumer
adoption keep task 23 open.

## Validation

Coverage generation/check, ownership/source/ID/scope validation, all 16 Python
coverage tests and `git diff --check` passed. No Gradle command was run by the agent.

The user successfully ran the Java tests, driver compatibility and transitional
migration-boundary verification from the repository root:

```bash
./gradlew --offline -DgemfireVersion=10.4.0-build.3056.df98c835 \
  :spring-data-gemfire-driver-spi:test \
  :spring-data-gemfire-driver-mock:test \
  :spring-data-gemfire-driver-10.4:integrationTest \
  --tests '*GemFire104DriverContractTests' \
  --tests '*NativeStatisticsTests' \
  --tests '*NativeDisabledStatisticsTests' \
  verifyDriverCompatibility verifyMigrationBoundary
```

The migration boundary remains transitional regression protection, not native-free
compliance.
