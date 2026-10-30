# Client configuration property names — 2026-10-30

Task 23 now exposes the 70 property names retained by completed scope task 72
through native-free `ClientConfigurationProperties`. `CacheConfiguration`
continues to accept copied properties; the native driver forwards them to
`ClientCacheFactory`, while the mock retains a supplied-property snapshot.
This slice provides names, not new configuration behavior.

## Scope and evidence

The frozen inventory contains 71 required-client `ConfigurationProperties` rows:
one type and 70 String constants. Their existing scope decisions remain unchanged:

| Family | Rows | Remaining behavior |
| --- | ---: | --- |
| Client runtime | 16, including type | Validation, defaults, aliases and typed assembly |
| Monitoring | 14 | Value validation and monitoring configuration |
| Authentication | 5 | Authentication and security namespace validation |
| TLS | 36 | Locator/server transport, aliases, channel constraints and credential failures |

The [completed scope evidence](client-runtime-configuration-and-lifecycle-scope.md)
records native source commit `df98c8357afe85853d9a4776255d4f0e7cf064cb`, matching
GemFire `10.4.0-build.3056.df98c835`.
`LOCATORS` must be empty for a client cache; pool locators provide discovery.
`ROLES` contributes legacy group metadata. When `SSL_ENABLED_COMPONENTS` is empty,
legacy `CLUSTER_SSL_*` settings apply to client-to-locator transport and
`SERVER_SSL_*` settings apply to client-to-server transport. Their names do not
expand scope to server hosting. Prefix constants identify namespaces, not
standalone settings or permission to use server-only security options.

## Implementation and limits

`NativeClientConfigurationPropertiesTests` checks the fixed approved field set,
public/static/final String declarations, and exact values against the pinned
native `ConfigurationProperties`. Existing native/mock runtime tests use the
new constants. `CacheConfigurationTests` extends defensive-copy and redacted
display coverage to a TLS password.

All 71 rows remain **partial**. No excluded-server row is mapped. Mock snapshots
do not activate TLS, authentication or monitoring. Tasks 24 and 29 retain
security/TLS behavior and typed assembly; tasks 35/36 retain Spring adoption.
At the names checkpoint, task 23 had 359 partial and 24 missing required-client
rows. The subsequent runtime/failure reconciliation brought it to 383 partial
and zero missing rows. Task 23 remains open; mapping counts do not measure
completed behavior.

## Validation

Static validation passed: all 16 coverage-policy tests, generated coverage freshness,
and inventory validation with `javap` (372 public declarations across 21 types).
The validator checked 3,851 API rows, 123 production imports and 538 source
references, including ownership, scope and negative controls. `git diff --check`
also passed.

The user reported success for the following command, covering SPI configuration
tests, mock/native client runtime tests, native property-name parity, driver
compatibility and the transitional migration boundary. Gradle was run by the user.

```sh
./gradlew --offline -DgemfireVersion=10.4.0-build.3056.df98c835 \
  :spring-data-gemfire-driver-spi:test --tests '*CacheConfigurationTests' \
  :spring-data-gemfire-driver-mock:test --tests '*MockClientRuntimeTests' \
  :spring-data-gemfire-driver-10.4:integrationTest \
  --tests '*NativeClientConfigurationPropertiesTests' \
  --tests '*NativeClientRuntimeTests' \
  verifyDriverCompatibility verifyMigrationBoundary
```

The migration-boundary gate checks the transitional baseline; it does not certify
native-free Spring Data compliance.

## Monitoring configuration checkpoint — 2026-10-30

Added native connection tests for effective readback of all 14 monitoring
properties and rejection of nine invalid values. The pinned native
`DistributionConfigImpl.setStatisticSampleRate` accepts 500 ms but normalizes it
to 1,000 ms; readback must reflect that normalization while the caller's
`CacheConfiguration` retains 500. Integer ranges come from `DistributionConfig`
and are enforced by `AbstractDistributionConfig.checkAttribute`.

The invalid cases cover archive/log size limits, log level, member timeout,
sampling rate and thread-monitor interval/time limit. They assert the existing
`DriverBindException` connection-creation boundary, safe outer message and native
`IllegalArgumentException` cause. The cause is retained, not redacted.

A mock test records the deliberate narrower behavior: sampling, archive and
thread-monitor settings remain detached snapshots without native range validation
or sampling-rate normalization. Mock log thresholds already have their own
validation. These tests do not establish archive creation/rotation or thread
watchdog activation. Typed assembly remains in task 29, security/TLS in task 24,
and Spring adoption in tasks 35/36. All 71 configuration rows remain partial.

**User-reported validation passed:** focused mock/native runtime and configuration
tests and the transitional migration boundary. Gradle was run by the user with
the following command; this does not certify native-free Spring Data compliance:

```sh
./gradlew --offline -DgemfireVersion=10.4.0-build.3056.df98c835 \
  :spring-data-gemfire-driver-mock:test --tests '*MockClientRuntimeTests' \
  :spring-data-gemfire-driver-10.4:integrationTest \
  --tests '*NativeClientConfigurationPropertiesTests' \
  --tests '*NativeClientRuntimeTests' \
  verifyMigrationBoundary
```
