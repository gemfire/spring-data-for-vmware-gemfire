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

The combined Spring `testJar` still exists for two other consumers:
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
