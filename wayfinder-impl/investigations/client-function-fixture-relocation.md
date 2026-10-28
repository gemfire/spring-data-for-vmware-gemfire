# Client function fixture relocation — 2026-10-27

Task 64 fixture implementation passes user-run compilation, packaging and client
regressions, baseline pruning and transitional migration-boundary verification. The 13 files and
15 server function bodies inventoried in the
[Spring surface audit](client-only-spring-surfaces.md#server-fixture-placement)
now live in the 10.4 driver's private `src/serverFixtures/java` source set.
Their packages and deployed function IDs remain unchanged. This source set is
separate from the driver's main output and publication; it compiles against the
same strictly pinned native version as the driver.

`serverFunctionFixtureJar` packages only those fixtures, the shared protocol and
server PDX XML. Spring resolves the consumable `serverFunctionFixtures`
configuration as a deployment file, outside its compile/runtime classpaths, and
passes its path as `SERVER_FUNCTION_FIXTURE_JAR_PATH` to integration tests.

## Client/server protocol and PDX

`test-fixtures/function-protocol/java` holds JDK-only `Metric` and
`FunctionFixtureIds` sources. Spring tests and an isolated driver
`functionProtocol` source set compile the same sources independently. No Spring
project dependency on the native driver is added to a Java classpath. `Metric`
retains its wire class name, fields, constructor and getters; explicit methods
replace Lombok so the protocol needs no annotation processor. Client echo calls
use the neutral ID constant instead of referencing `EchoFunction`.

The server cache XML moved with the fixtures. It explicitly reads PDX as serialized
data and uses the native reflection serializer instead of Spring's test-only
`PersonPdxSerializer`. The client retains its custom serializers and domain
classes. `AddressFieldFunction` checks the existing PDX class name without loading
the client `Address` class or Spring assertions. Real-server validation must prove
that PDX field extraction and unconverted argument assertions still pass.

## Retained suites

All seven suites deploy the new payload:

- `ExceptionThrowingFunctionExecutionIntegrationTests`: exception translation.
- `ClientCacheFunctionExecutionWithPdxIntegrationTest`: PDX and argument types.
- `FunctionExecutionIntegrationTests`: client execution and arguments.
- `FunctionIntegrationTests`: collections, arguments and no-result behavior.
- `GemfireFunctionTemplateIntegrationTests`: client template targets and results.
- `FunctionsReturnResultsFromAllServersIntegrationTests`: two-server fan-out and
  single-server results; additional assertions check the shared DTO fields.
- `FunctionResultTypeIntegrationTests`: object, list and primitive conversion.

Existing assertions remain. The added `verifyFunctionFixturePackaging` task checks
the payload inventory (16 class files containing 15 function bodies), protocol and
XML, and rejects server fixture classes in either Spring client test classpath or
the Spring test JAR. It runs before integration tests and during `check`.

At that checkpoint the combined Spring `testJar` still existed for two other consumers:
`GemfireTemplateIntegrationTests` deploys test domain data, and
`AutoConfiguredAuthenticationConfigurationIntegrationTests` uses its security
fixture. Neither uses the relocated function bodies. Their infrastructure still
requires reconciliation; this slice does not claim that all private server test
infrastructure or all native coupling has left Spring.

## Validation status

Source inventory confirms 13 relocated files with 15 function bodies and JDK-only
protocol imports. The refreshed lexical audit finds 97 native-coupled Spring unit
source files, down from 110; main/integration counts remain 158/127. Coverage
generation/check, all 10 coverage-policy tests and independent `javap` validation
(372 declarations across 21 types) pass. Frozen scope remains 373 required-client /
625 excluded-server / 2,853 unresolved, with all 3,851 IDs preserved.

User-run Gradle compilation (including Spring Test), packaging verification and
all seven real-server suites pass: 15 tests, zero failures/errors/skips. All 19
boundary-tool tests also pass. Initial boundary verification failed on one changed identity
and 68 stale identities: the `GemFireCluster` local-variable initializer now names
the separate deployment property/JAR, while 67 other entries represent relocated
native references and removed static state.

Reconciled only that initializer's baseline detail by replacing `TEST_JAR_PATH`
with `SERVER_FUNCTION_FIXTURE_JAR_PATH` and `/testJar.jar` with
`/server-function-fixtures.jar`. Its rule, path, occurrence count (one) and owner
(task 40) are unchanged. Comparing the user-generated findings against that
baseline left zero new identities and 67 stale entries before pruning. No scanner rules or
allowances were broadened. Native-source occurrences fell from 5,169 to 5,103;
static-state occurrences fell from 164 to 163.

User-run `pruneMigrationBoundary verifyMigrationBoundary` now passes with native
`10.4.0-build.3056.df98c835`. All 67 stale entries were pruned; the fresh report
matches the baseline with zero new or stale identities. No Gradle
command was run by the agent. Keep task 64 and parent 59 open; this remains a
transitional boundary, not native-free acceptance.

## Task 77: isolated domain and security payloads — 2026-10-29

Implementation and user-run Gradle validation pass against
`10.4.0-build.3056.df98c835`. The two remaining combined-test-JAR consumers now
resolve separate deployment-only configurations from the 10.4 driver:

| Consumer | Payload | Exact contents |
| --- | --- | --- |
| GemfireTemplateIntegrationTests | template-domain-fixtures.jar | fixtures.template.User (one class) |
| AutoConfiguredAuthenticationConfigurationIntegrationTests | server-security-fixtures.jar | config.annotation.TestSecurityManager, its nested TestPrincipal, fixtures.security.SecurityFixtureCredentials (three classes) |

All names above are relative to `org.springframework.data.gemfire`. The security
manager retains its original fully qualified name and moves into the driver's
private `src/serverSecurityFixtures/java` source set, outside its main artifact.
Spring utility calls are replaced with JDK equivalents. Credentials and the
manager class-name string are shared without a client dependency on the manager.

The JDK-only domain and credentials sources live in
`test-fixtures/client-server-protocol/java`. Spring tests and the driver's isolated
`clientServerProtocol` source set compile them independently. The template suite
uses the new `fixtures.template.User` class on both sides, with the original fields,
accessors and equality behavior and an explicit serialVersionUID. The original
annotated repository.sample.User remains available to repository tests. Neither
new payload includes Spring libraries, native libraries or unrelated test classes.

The template suite retains all 21 tests and its existing LOCAL client region.
It deploys its DTO to the server but does not establish remote serialization
coverage; no remote round-trip claim is made here. The authentication suite retains
its one test with three authenticated reads and the same server/locator credentials.
The principal remains private to server security infrastructure.

The combined testJar task, TEST_JAR_PATH property and unit-test dependency are
removed; no remaining Java/build/XML consumers were found.
`verifyClientServerFixturePackaging` checks exact security/domain contents, the
existing function inventory (16 private classes, two protocol classes and PDX XML),
shared client class availability, and private-class absence from main/test/integration
client classpaths and both Spring/driver production JARs. It also rejects test
protocol classes in production JARs. The old verifyFunctionFixturePackaging command
is retained as an alias. The new check runs before integration tests and during check.

A source-only boundary comparison found zero new identities and seven stale native
source identities from the relocated security manager. The subsequent user-run command below validates the new
Gradle configuration/classpaths and completes baseline pruning and verification. No scanner allowances were broadened.

```sh
./gradlew --offline -DgemfireVersion=10.4.0-build.3056.df98c835 \
  :spring-data-vmware-gemfire:verifyClientServerFixturePackaging \
  :spring-data-vmware-gemfire:integrationTest \
  --tests 'org.springframework.data.gemfire.GemfireTemplateIntegrationTests' \
  --tests '*AutoConfiguredAuthenticationConfigurationIntegrationTests' \
  :spring-test-vmware-gemfire:compileTestJava \
  pruneMigrationBoundaryBaseline verifyMigrationBoundary
```

The refreshed lexical audit records 96 native-coupled Spring unit-test source files
(previously 97), with main/integration counts unchanged at 158/127. Deterministic
coverage generation/check, ownership/source/ID/scope validation and all 12 coverage
policy tests pass. All 3,851 API IDs remain: 2,694 required-client and 1,157 excluded-server.
No Gradle command was run by the agent.

The user reported this command passed. Local JUnit XML confirms 22 tests (21
template, one authentication), zero failures/errors/skips, timestamped
2026-10-28T20:50:52Z. Fresh boundary findings match the pruned baseline with zero
new or stale identities; native-source occurrences are 5,074. Task 77 is resolved.
Tasks 64/59 retain final client-scope reconciliation. Task 37 retains full mock/native-free
Spring test migration; tasks 25/40 retain production function transport packaging.
