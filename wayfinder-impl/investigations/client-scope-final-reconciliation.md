# Final client scope reconciliation — task 64

Reviewed 2026-10-29 against GemFire **10.4.0-build.3056.df98c835**.
Tasks 59–77 are resolved following the user-confirmed aggregate validation on
2026-10-29. Classification, ownership reconciliation and server-only cleanup are
complete. Native-free Spring and complete mock/versioned driver behavior remain
downstream acceptance; the next eligible task is 23, client runtime identity and
connectivity SPI.

## Legacy CQ name

`GemfireCacheUtils` resolves `org.apache.geode.cache.query.CqInvalidException`
optionally, catches `IllegalArgumentException`, and leaves its class sentinel null
when absent. `isCqInvalidException` then returns false; `convertQueryExceptions`
still handles current query exceptions and otherwise returns `GemfireSystemException`.
The selected artifact has no declaration for this old name. Its source comment
identifies removal in 6.5; that historical comment is not a promise to support 6.5.

`REF-f2bb31dde3ceca53` is required-client **migration work**, not a new native API
row. Task 36 owns removal of obsolete reflection after typed exception adoption,
preserving current query-failure translation and unknown-failure fallback. Task 33
owns CQ consumer adoption. Resolved exception foundation 16 is no longer its owner.

## Serializer package filters

`MappingPdxSerializer` composes `EXCLUDE_COM_GEMSTONE_GEMFIRE_TYPES` and
`EXCLUDE_ORG_APACHE_GEODE_TYPES` into its default exclusion predicate. Each checks
the supplied class's package prefix, without loading a class from the string.
This prevents application mapping from serializing provider infrastructure.

`REF-e1a7fd70ff733491` and `REF-ba55233cc7289f10` remain required-client behavior.
Task 31 owns mapping adoption with serialization callback/value tasks 54/58 under
26. Preserve exclusion, null handling and filter composition through typed driver
integration; do not expose native types or simply delete the exclusion to reduce
the native-reference count. These package policies are not server hosting APIs.

## Test logging categories

The `org.apache.geode` entries in `log4j2-test.xml` and `logback.xml` configure
logger names and default ERROR thresholds. They do not resolve Java classes.
`REF-55ec0617cb181f45` and `REF-9a41549405fb8151` are required-client test-resource
reconciliation under task 37. Preserve useful client diagnostics; remove unused
native categories or place native-specific settings with driver integration
resources when test migration establishes the appropriate home. No new SPI logger
API follows from these entries, and no native classpath exemption is created.

## Cache server references

Three source rows inherit the excluded `CacheServer` *type* scope, but their
actual uses are narrower. Explicit source decisions preserve the frozen type
exclusion while preventing client requirements from being discarded:

| Reference | Actual use | Active owner |
| --- | --- | --- |
| `REF-a5f143ec7637ac28` | `AbstractCacheConfiguration`: import used only by `@see CacheServer`; remove stale documentation/import | 36 |
| `REF-166bcb760c1b0a73` | `ClientCacheConfiguration`: `ConnectionEndpointList.parse(CacheServer.DEFAULT_PORT, serverHostsPorts)`; preserve client pool endpoint default using a native-free constant | 35 |
| `REF-f88063470aaa22cf` | `DistributedSystemUtils.DEFAULT_CACHE_SERVER_PORT` exposes the same endpoint value; preserve client callers while removing native linkage | 36 |

These are not negative guards or executable server services. No public server
hosting method is retained. Their cleanup remains downstream Spring migration,
with source rows explicitly marked required-client and assigned active owners.

## Ownership and removed mappings

Transfer 100 **partial** required API rows from completed foundations to the open
task responsible for finishing the work. Keep their status and behavior evidence:

| Foundation | Active owner | API rows | Remaining obligation |
| --- | --- | ---: | --- |
| 17 | 30 | 56 | Callback/event adoption and full reachable contracts; coordinate member metadata with 23, configuration with 19, cache assembly with 29 and transaction payloads with 34 |
| 28 | 34 | 22 | Transaction orchestration/identity/serialization and native-free fixtures with 37; explicit mock rejection is not successful transaction support |
| 46 | 19 | 21 | Full reachable region configuration and factory adoption with 30 |
| 16 | 25 | 1 | FunctionException aggregate/cause semantics and typed function integration with 32/36 |

Transfer 72 partial current-SPI rows as well: 32 from 17→30, nine from 18→31,
20 from 28→34, four from 46→19, five from 50→20 and two from 53→58. Preserve
all 149 current SPI references and their required-client mappings. Implemented
foundation rows may retain their historical owner; every required missing/partial
API row and partial current-SPI row must have an open completion owner.

Clear three stale inherited `TransactionWriter` mappings (`close`, `init`,
`initialize`). Their native adapter was removed in task 62. Shared callback
interfaces remain required through retained client receivers; they do not establish
support for the excluded transaction-writer receiver. Retain all three frozen IDs
as excluded-server/missing with empty implementation mappings. No Java API changes
are needed for this correction.

Validated inventory: **3,851 API IDs**, **2,694 required-client / 1,157 excluded-server /
zero unresolved**; **3,128 missing / 468 partial / 255 implemented** across scopes.
All **538 source/resource rows** now have explicit or inherited client dispositions,
with zero unresolved. Required-client source scope includes migration/removal work;
it does not assert that every lexical string needs a new public SPI declaration.
All **2,723 frozen audit assignments** remain unchanged. No excluded row may retain
a public SPI mapping or claim implemented/partial coverage.

## Validation

Deterministic generation/check, all 16 scope-policy tests, ownership/source/ID
validation and independent javap comparison of 372 declarations across 21 types
pass. Comparison against the previous inventory preserves all frozen IDs and the
assignment ledger byte-for-byte; exactly three excluded statuses and 100 retained
API owners change. The 77-task graph remains acyclic: 33 open, 42 completed and two
superseded (the superseded tickets carry resolved metadata).

The user confirmed the aggregate command passed on 2026-10-29. Local JUnit XML
records **547 test executions**, with **zero failures, errors or skips**:

| Suite | Executions |
| --- | ---: |
| SPI | 23 |
| Core | 31 |
| Shared contracts | 6 |
| Mock | 54 |
| Native 10.4 integration | 129 |
| Spring SPI cache unit | 152 |
| Spring SPI cache integration | 152 |

These are executions, not unique tests: the Spring suites exercise the same 152
tests in two modes. Report timestamps span 2026-10-28T21:19:10.189Z through
21:20:55.479Z (2026-10-29 in Sydney). Fresh boundary findings have **zero new and
zero stale identities**, with **5,074 native-source occurrences still remaining**.
The successful command also covers affected compilations, fixture packaging,
mock native-free classpath verification and ABI/stamp checks under the explicit
pre-first-release policy; no released compatibility baseline is claimed.

Tasks 64 and 59 are resolved. The agent did not run Gradle or change Java code
for this closeout. The synthetic new-overload policy test now supplies open
implementation/consumer owners instead of inheriting the resolved cleanup owner;
its unresolved-classification assertion and production ownership checks are unchanged.
Earlier child receipts preserve the removal and retained
client behavior evidence. Remaining required API/SPI gaps have open downstream
owners; full native-free Spring and all-version acceptance remain outstanding.

Successful aggregate command (run by the user):

```sh
./gradlew --offline -DgemfireVersion=10.4.0-build.3056.df98c835 \
  :spring-data-gemfire-driver-spi:check \
  :spring-data-gemfire-driver-core:check \
  :spring-data-gemfire-driver-contract-tests:check \
  :spring-data-gemfire-driver-mock:check \
  :spring-data-gemfire-driver-10.4:check \
  :spring-data-vmware-gemfire:spiCacheTest \
  :spring-data-vmware-gemfire:spiCacheIntegrationTest \
  :spring-data-vmware-gemfire:verifyClientServerFixturePackaging \
  :spring-data-vmware-gemfire:compileJava \
  :spring-data-vmware-gemfire:compileTestJava \
  :spring-data-vmware-gemfire:compileIntegrationTestJava \
  :spring-test-vmware-gemfire:compileJava \
  :spring-test-vmware-gemfire:compileTestJava \
  verifyMigrationBoundary verifyDriverCompatibility
```

The native driver `check` includes its full integration source set; mock `check`
includes native-free classpath verification. Spring SPI cache suites cover the
migrated native-free slice. The prior task-77 receipt covers retained Spring
fixture scenarios. This command checks cleanup/compatibility regression against
the transitional baseline; it cannot establish full native-free compliance.
