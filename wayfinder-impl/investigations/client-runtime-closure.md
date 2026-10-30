# Client runtime closure ledger

Current as of 2026-10-30. This ledger replaces obsolete remaining-work statements in task 23's historical checkpoints; it does not upgrade any coverage status. Historical native runtime evidence and the frozen API inventory target GemFire `10.4.0-build.3056.df98c835` (source `df98c8357afe85853d9a4776255d4f0e7cf064cb`). The active test version and Docker image now use `10.4.0-build.3068.c3a2bbfe`. The user reported successful Spring main recompilation with that version; this does not establish a new native runtime test result or rebase the frozen inventory. No Gradle command was run by the agent.

## Current ownership

Validation complete: generated inventories now reflect the ownership changes in `coverage-policy.json`. Following the user-reported recompilation, regeneration, the freshness check and coverage validation with `--javap` passed. Validation matched 372 public declarations across 21 types and checked all 3,851 API rows, 123 production imports and 538 source references, including both inventory negative controls. All 16 policy tests pass. The 71 configuration ownership changes preserve scope, status and implementation/test mappings. The subsequent reconciliation updates remaining-work text for the retained 312 runtime rows without changing their scope, status, owners or implementation/test mappings; validation of that reconciliation is recorded below.

Task 23 previously owned 383 required-client rows, all partial. Seventy-one configuration rows now have explicit implementation owners 24/29 and entrypoint consumer owner 35. Task 23 retains **312 partial rows, zero missing**. All frozen IDs, required/excluded scope decisions, implementation references and test references are preserved. Global task counts and global completion counts are unchanged.

| Retained task-23 family | Rows | Validated evidence | Remaining responsibility |
| --- | ---: | --- | --- |
| Cancellation | 9 | [Runtime handles](client-runtime-handles.md): separate system/cache criteria, caller-owned criteria and failure ordering | Spring utilities in 36; row wording reconciled |
| Runtime context | 28 | [Runtime handles](client-runtime-handles.md), [lifecycle/failures](client-runtime-lifecycle-and-failures.md): owned connections, reuse/closure, discovery and safe snapshots | Preserve deliberate replacement of native globals and per-call discovery; Spring adoption in 36 |
| Member metadata | 9 | [Runtime handles](client-runtime-handles.md): identity/comparison and detached metadata | Typed payload adoption in 36; native serialization APIs are outside this contract |
| Durable identity | 11 | [Runtime handles](client-runtime-handles.md): configured and pool-qualified immutable snapshots | Spring adoption in 36; remote durable lifecycle remains 20/52 |
| Statistics model/runtime | 133 | [Types](client-statistics-types.md), [instances](client-statistics-instances.md), [suppliers](client-statistics-suppliers.md), [XML](client-statistics-xml.md), [lifecycle](client-statistics-lifecycle.md), [disabled mode](client-statistics-disabled.md) | Spring adoption in 36; monitoring assembly/activation in 29 |
| Logging/message values | 105 | [Logging](client-logging.md): levels/destinations, tracing, JUL bridge/formatter, conversion and localized catalogs | Spring adoption in 36; configuration/security routing in 24/29 |
| Runtime/received failures | 17 | [Lifecycle/failures](client-runtime-lifecycle-and-failures.md): constructor/cause/root-cause and outer-operation translation | Source disposition below; focused wrapper test pending; Spring translation in 36 |

These groups are disjoint and sum to 312. Statistics/log accessors declared on DistributedSystem are included in runtime-context/runtime-statistics scope groups; the logging document's API-family totals therefore differ from the logging scope-group count here.

## Received-failure disposition

Pinned native source `df98c8357afe85853d9a4776255d4f0e7cf064cb`, `gemfire-core/src/main/java/org/apache/geode/cache/client/internal/AbstractOp.java`, establishes the response boundary: `processAck`, `processObjResponse` and `processChunkedResponse` wrap received exception payloads in `ServerOperationException` (apart from explicitly handled protocol exceptions). The retained RoleException, StatisticsDisabledException and LeaseExpiredException payloads therefore remain nested causes. See [the shared exception source ledger](client-shared-exception-scope.md) for originating operations and server response paths.

The driver must preserve the outer operation wrapper: `ServerOperationException` maps to `RESOURCE_FAILURE`, and Spring translation maps to `DataAccessResourceFailureException`. Direct local role/statistics failures retain `SYSTEM` classification and direct lease failures retain `LOCK`. Arbitrary nested causes must not change the outer classification. The original wrapper/root cause remain available with a safe outer message; this does not promise global redaction of trusted causes.

`GemFireExceptionMapperTests.receivedRuntimeFailuresPreserveServerOperationClassification` now checks all three wrapped payloads, driver classification, Spring translation, cause identity and safe outer messages. **User-run validation passed on build `10.4.0-build.3068.c3a2bbfe` (2026-10-30).** This is a wrapper-boundary regression test, not a live wire fault-injection test. Source evidence supplies the bounded transport disposition; no peer-role or distributed-lock SPI is needed. Operation-specific wire tests remain part of the corresponding driver integration obligations if later changes affect those response paths.

## Consumer handoff and remaining closure

The exact adoption set is the 312 rows in `api-coverage.tsv` with implementation owner `wayfinder-issues/23-distributed-system-membership-and-runtime-services-spi.md` and scope `required-client`; every row already has consumer owner `wayfinder-issues/36-residual-native-utilities-and-support-cleanup.md`. The seven disjoint family counts above define the handoff. Task 36 now records explicit adoption and regression requirements, including owned runtime absence/closure, typed identity payloads, statistics/logging and outer-failure translation.

Remaining-work statements for all 312 rows have been reconciled against the existing evidence. None becomes complete solely because its foundation is implemented. Task 23 is resolved by its final Answer; the focused wrapper test passed. Required partial rows must retain an open implementation/adoption owner when task 23 closes; task 36 cannot be declared complete by this handoff.

Reconciliation validation passed: regeneration/freshness, coverage validation with `--javap`, all 16 policy tests and `git diff --check`. The generated inventory comparison confirms remaining-work-only changes for these 312 rows and four shared cache runtime/cancellation accessor rows owned by task 29; all other fields are preserved. Validation still covers 3,851 API rows, 123 production imports, 538 source references and 372 public declarations across 21 types. The focused Java regression subsequently passed in the user-run Gradle command.

Tasks 24/29 remain downstream of 23. No reverse dependency is introduced. Configuration activation/security tests belong to their receiving tasks. The final task-23 Answer must record the source disposition, user-run test result, exact version, typed replacements, limits and downstream owners.

## Known limits retained

The mock is isolated and does not simulate server connectivity, remote durable queues, TLS/authentication, native archives or thread monitoring. Property discovery has no ambient lookup in the mock; native filenames are resolved per call. Durable values are detached snapshots rather than native mutable/thread-local aliases. Statistics XML support does not promise arbitrary external resolver parity. Localized mock logs use raw-text fallback. Failure diagnostics are safe at the outer boundary; trusted nested causes are not globally redacted. Endpoint observation rows remain in task 52 with task-35 adoption.

## Configuration row handoff

All rows below remain **partial**. Implementation and test mappings are preserved in `coverage-policy.json`; generated inventories have been regenerated and validated. Task 24 accepts 41 TLS/authentication rows; task 29 accepts 30 runtime/monitoring rows. The consumer owner is task 35; task 36 still owns residual Spring utilities. This handoff changes ownership, not required scope or evidence of completion.

Existing evidence: [client configuration properties](client-configuration-properties.md). Native monitoring tests prove 14 readbacks, sampling normalization and nine invalid values; archive creation/rotation and thread-monitor activation are not yet proved. Task 29 now states those acceptance requirements explicitly. Task 24 states TLS/authentication acceptance explicitly.

| Frozen ID | ConfigurationProperties member | Scope decision | Implementation owner |
| --- | --- | --- | --- |
| `API-b2347ae8d079e779` | `SECURITY_CLIENT_AUTH_INIT` | client-authentication-properties | 24 |
| `API-d9118a0dff272bc1` | `SECURITY_CLIENT_DHALGO` | client-authentication-properties | 24 |
| `API-36af523ec8b3729f` | `SECURITY_LOG_FILE` | client-authentication-properties | 24 |
| `API-8c2926e68d0bd7c9` | `SECURITY_LOG_LEVEL` | client-authentication-properties | 24 |
| `API-c4105a8417592e0d` | `SECURITY_PREFIX` | client-authentication-properties | 24 |
| `API-fd28a7609208802c` | `ARCHIVE_DISK_SPACE_LIMIT` | client-monitoring-properties | 29 |
| `API-57002a724030dd6f` | `ARCHIVE_FILE_SIZE_LIMIT` | client-monitoring-properties | 29 |
| `API-2ca7074f3c819304` | `ENABLE_TIME_STATISTICS` | client-monitoring-properties | 29 |
| `API-4c0e829184c778b6` | `LOG_DISK_SPACE_LIMIT` | client-monitoring-properties | 29 |
| `API-4a2523c9e51a12b9` | `LOG_FILE` | client-monitoring-properties | 29 |
| `API-fbb79d3181b40ff7` | `LOG_FILE_SIZE_LIMIT` | client-monitoring-properties | 29 |
| `API-91796efed997dc8a` | `LOG_LEVEL` | client-monitoring-properties | 29 |
| `API-e62a7b8b4b4cc5fc` | `MEMBER_TIMEOUT` | client-monitoring-properties | 29 |
| `API-3a95f8f275faff9b` | `STATISTIC_ARCHIVE_FILE` | client-monitoring-properties | 29 |
| `API-63fcc4dcd88a6ba8` | `STATISTIC_SAMPLE_RATE` | client-monitoring-properties | 29 |
| `API-aa59ab14430a2ce8` | `STATISTIC_SAMPLING_ENABLED` | client-monitoring-properties | 29 |
| `API-8d8aedeabf405cfd` | `THREAD_MONITOR_ENABLED` | client-monitoring-properties | 29 |
| `API-e033223da64c59db` | `THREAD_MONITOR_INTERVAL` | client-monitoring-properties | 29 |
| `API-ab3251baae1e9ea5` | `THREAD_MONITOR_TIME_LIMIT` | client-monitoring-properties | 29 |
| `API-c752ad8a7c9181c7` | `<type>` | client-runtime-properties | 29 |
| `API-3a948ad32cfd0b82` | `AVAILABILITY_ZONE` | client-runtime-properties | 29 |
| `API-6e51761b881e6f79` | `CACHE_XML_FILE` | client-runtime-properties | 29 |
| `API-4968e8a369bb7016` | `CONFLATE_EVENTS` | client-runtime-properties | 29 |
| `API-7ce5812c12aef91e` | `DELTA_PROPAGATION` | client-runtime-properties | 29 |
| `API-12263eab3ec8aaa5` | `DISTRIBUTED_SYSTEM_ID` | client-runtime-properties | 29 |
| `API-d14b75b139233662` | `DURABLE_CLIENT_ID` | client-runtime-properties | 29 |
| `API-91dfcb8a27f13396` | `DURABLE_CLIENT_TIMEOUT` | client-runtime-properties | 29 |
| `API-812c69362b7530f8` | `GROUPS` | client-runtime-properties | 29 |
| `API-4578fcba9f87d9d4` | `LOCATORS` | client-runtime-properties | 29 |
| `API-c43814cd0bbbc2e3` | `LOCK_MEMORY` | client-runtime-properties | 29 |
| `API-cdbc265942d7185d` | `NAME` | client-runtime-properties | 29 |
| `API-da50b9d48e26f4f7` | `REDUNDANCY_ZONE` | client-runtime-properties | 29 |
| `API-3fcd8794f1b8a4d7` | `ROLES` | client-runtime-properties | 29 |
| `API-66ca4a5ccfa6611a` | `SERIALIZABLE_OBJECT_FILTER` | client-runtime-properties | 29 |
| `API-45a724358cf6ab00` | `VALIDATE_SERIALIZABLE_OBJECTS` | client-runtime-properties | 29 |
| `API-876bb3687fb7351d` | `CLUSTER_SSL_CIPHERS` | client-tls-properties | 24 |
| `API-86c818aa8eb74cb5` | `CLUSTER_SSL_ENABLED` | client-tls-properties | 24 |
| `API-b4a85af4157c072c` | `CLUSTER_SSL_KEYSTORE` | client-tls-properties | 24 |
| `API-abd4c022f44a154a` | `CLUSTER_SSL_KEYSTORE_PASSWORD` | client-tls-properties | 24 |
| `API-a714691bdcad6fde` | `CLUSTER_SSL_KEYSTORE_TYPE` | client-tls-properties | 24 |
| `API-9f7e47eda19cd078` | `CLUSTER_SSL_PREFIX` | client-tls-properties | 24 |
| `API-3ad098881670b666` | `CLUSTER_SSL_PROTOCOLS` | client-tls-properties | 24 |
| `API-9c2ae020483e9e3a` | `CLUSTER_SSL_REQUIRE_AUTHENTICATION` | client-tls-properties | 24 |
| `API-4f43f2c7f12f703d` | `CLUSTER_SSL_TRUSTSTORE` | client-tls-properties | 24 |
| `API-680671385939a6f2` | `CLUSTER_SSL_TRUSTSTORE_PASSWORD` | client-tls-properties | 24 |
| `API-c9672a92d4e0eaf2` | `SERVER_SSL_CIPHERS` | client-tls-properties | 24 |
| `API-a7ad2d9d904fa4c6` | `SERVER_SSL_ENABLED` | client-tls-properties | 24 |
| `API-04a5517d6c46a41b` | `SERVER_SSL_KEYSTORE` | client-tls-properties | 24 |
| `API-9af6359974d8749a` | `SERVER_SSL_KEYSTORE_PASSWORD` | client-tls-properties | 24 |
| `API-ef74e7bb30da490f` | `SERVER_SSL_KEYSTORE_TYPE` | client-tls-properties | 24 |
| `API-f8ff3ffa2f7ebb33` | `SERVER_SSL_PROTOCOLS` | client-tls-properties | 24 |
| `API-01b4047a58ffe2ef` | `SERVER_SSL_REQUIRE_AUTHENTICATION` | client-tls-properties | 24 |
| `API-f1d5c83b23f8de33` | `SERVER_SSL_TRUSTSTORE` | client-tls-properties | 24 |
| `API-c96e79979581d5b5` | `SERVER_SSL_TRUSTSTORE_PASSWORD` | client-tls-properties | 24 |
| `API-1a81197c27e8130d` | `SSL_CIPHERS` | client-tls-properties | 24 |
| `API-84e695c0eb927918` | `SSL_CLIENT_PROTOCOLS` | client-tls-properties | 24 |
| `API-79deb2a61bdc6100` | `SSL_DEFAULT_ALIAS` | client-tls-properties | 24 |
| `API-4bddf623151625bf` | `SSL_ENABLED_COMPONENTS` | client-tls-properties | 24 |
| `API-c9a3df0121bf0769` | `SSL_ENDPOINT_IDENTIFICATION_ENABLED` | client-tls-properties | 24 |
| `API-1597ed86510ec539` | `SSL_KEYSTORE` | client-tls-properties | 24 |
| `API-bbba07c03949c1b3` | `SSL_KEYSTORE_PASSWORD` | client-tls-properties | 24 |
| `API-19526fbcdbab43e3` | `SSL_KEYSTORE_TYPE` | client-tls-properties | 24 |
| `API-6285d9d72915ed69` | `SSL_LOCATOR_ALIAS` | client-tls-properties | 24 |
| `API-933385999b68d5af` | `SSL_PARAMETER_EXTENSION` | client-tls-properties | 24 |
| `API-9eac2486250f044c` | `SSL_PROTOCOLS` | client-tls-properties | 24 |
| `API-702f8ea90f7afb14` | `SSL_REQUIRE_AUTHENTICATION` | client-tls-properties | 24 |
| `API-e37fb77791f4bc1a` | `SSL_SERVER_ALIAS` | client-tls-properties | 24 |
| `API-0d19f0519b944024` | `SSL_TRUSTSTORE` | client-tls-properties | 24 |
| `API-01a04469f648002f` | `SSL_TRUSTSTORE_PASSWORD` | client-tls-properties | 24 |
| `API-c756a714151aa697` | `SSL_TRUSTSTORE_TYPE` | client-tls-properties | 24 |
| `API-1655cba1237fb6c9` | `SSL_USE_DEFAULT_CONTEXT` | client-tls-properties | 24 |
