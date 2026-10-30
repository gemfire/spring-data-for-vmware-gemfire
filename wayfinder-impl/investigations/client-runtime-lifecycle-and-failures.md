# Client runtime lifecycle and retained failures

Checkpoint: 2026-10-30. Native version: `10.4.0-build.3056.df98c835`, source
commit `df98c8357afe85853d9a4776255d4f0e7cf064cb`.

This reconciles the final 24 missing task-23 required-client rows with existing
SPI behavior. All become **partial**, leaving 383 partial and zero missing rows.
This is a mapping milestone, not task completion or native-free compliance.
No production API is added by this checkpoint.

## Owned client lifecycle

| Native rows | Existing replacement | Limits |
| --- | --- | --- |
| `DistributedSystem` type, constructor, `connect`, `getConnection` | `GemFireDriver.connect` and its owned `DriverConnection` | Client cache creation owns native system creation/reuse; there is no public process-global connection lookup. |
| `disconnect` | `DriverConnection.close` | Closes the owned client connection, not an arbitrary system obtained from a global registry. |
| `getMemberId` | `DriverClientRuntime.getMember().getId()` | An added native assertion compares the legacy system ID alias with the member ID. |
| `addSystem`, `removeSystem`, `existingSystems`, `existingSystemsLock` | Native driver lifecycle implementation; isolated mock connection state | These protected native registration mechanisms stay private. No static registry or lock is added to the SPI. |

Source evidence is in `gemfire-core/src/main/java/org/apache/geode/distributed/DistributedSystem.java`
(registration, lookup and lifecycle), `distributed/internal/InternalDistributedSystem.java`
(`getMemberId`, `getDistributedMember`, connection creation), and
`cache/client/ClientCacheFactory.java` (client cache creation). Paths are relative
to the pinned GemFire source checkout. Client creation uses the local system;
local connectedness does not prove a server connection. The pre-existing
[configuration/lifecycle scope decision](client-runtime-configuration-and-lifecycle-scope.md)
retains client scaffolding and excludes peer hosting independently.

`NativeConnectionReuseTests.compatibleClientConnectionsShareCacheEffectsAndClosure`
covers compatible native connections sharing cache effects and closure. The mock
intentionally gives each connection independent identity and cancellation; see
`MockClientRuntimeTests.independentConnectionsHaveIndependentIdentityAndCancellation`.
`NativeClientRuntimeTests` covers runtime identity, properties, connectedness and
separate cache/system cancellation. The new member-ID assertion needs user-run
validation. These tests do not establish every native connection configuration
combination or Spring consumer adoption.

## Retained failure values

The 14 missing exception members plus three previously partial type rows now
have explicit mappings to `DriverOperationException` and the native exception
mapper. Native constructors become typed failure values, not copied native
exception subclasses in the SPI.

| Native family | SPI classification | Data access translation |
| --- | --- | --- |
| `RoleException` | `SYSTEM_FAILURE` | Uncategorized data access failure |
| `StatisticsDisabledException` | `SYSTEM_FAILURE` | Uncategorized data access failure |
| `LeaseExpiredException` | `LOCK_FAILURE` | Pessimistic locking failure |

Native source evidence: `cache/RoleException.java`,
`cache/StatisticsDisabledException.java`, and `distributed/LeaseExpiredException.java`
under `gemfire-core/src/main/java/org/apache/geode/`. The first two expose four
constructors; the last exposes its message constructor. Their inheritance is
handled by `GemFireExceptionMapper.classify`. This retains errors clients may
receive without introducing server role or lease hosting APIs.

Two added `GemFireExceptionMapperTests` cover constructor variants, nested causes,
root-cause identity, safe outer messages and string representations, and
outer-operation classification. `GemFireCacheException` accepts `CacheException`,
not the separate `CacheRuntimeException` hierarchy used by role/statistics failures.
The existing wrapper test uses a valid `EntryExistsException`; these runtime
families are tested directly and under arbitrary runtime wrappers. A function
failure with a lease cause stays a function failure; an arbitrary runtime wrapper
stays unknown. Native causes remain available for trusted diagnostics, so callers
must not treat the complete cause chain as redacted output. The common SPI test
covers native-free system-failure translation and cause preservation. The mock
uses this common failure value and does not simulate server role/lease behavior.

These are mapping tests, not evidence of server-to-client wire propagation.
[Shared exception scope](client-shared-exception-scope.md#client-runtime-and-received-peer-failures)
remains applicable. Task 36 owns Spring adoption; task 23 stays open for remaining
behavior verification and explicit handoffs. Partial mappings do not erase those
obligations.

## Validation

Static validation passed: all 16 coverage-policy tests, generated coverage freshness,
and ownership/source/ID/scope checks. Independent `javap` inspection matched 372
public declarations across 21 types. Task 23 has 383 required-client rows, all
partial, with zero missing mappings. `git diff --check` passed.

User reported successful validation on 2026-10-30 after correcting the invalid
native wrapper test fixture. The targeted Java tests, driver compatibility and
transitional migration boundary passed with this command from the repository root:

```sh
./gradlew --offline -DgemfireVersion=10.4.0-build.3056.df98c835 \
  :spring-data-gemfire-driver-spi:test --tests '*GemFireDriverExceptionTranslatorTests' \
  :spring-data-gemfire-driver-mock:test --tests '*MockClientRuntimeTests' \
  :spring-data-gemfire-driver-10.4:integrationTest \
  --tests '*GemFireExceptionMapperTests' \
  --tests '*NativeClientRuntimeTests' \
  --tests '*NativeConnectionReuseTests' \
  verifyDriverCompatibility verifyMigrationBoundary
```

The migration boundary remains transitional. A passing result does not establish
that Spring Data has removed all native dependencies.
