# Client function submission representation

## Status and evidence boundary

Task 64 transport investigation, 2026-10-28. The private envelope probe **compiled and passed in the user-run Gradle command**. Local JUnit XML confirms three envelope, three submission, six registry and three permission tests: 15 tests with zero failures, errors or skips (2026-10-28T00:39:55–00:40:11Z). The representation decision below now settles the task 64 semantic boundary; it does not implement a public SPI or complete production acceptance. The [function member ledger](client-function-member-scope.md) and [permission value disposition](client-permission-values.md) remain binding requirements. Scope stays 499 required-client / 629 excluded-server / 2,723 unresolved; no frozen ID or implementation status changes.

Evidence uses GemFire `10.4.0-build.3056.df98c835`, source commit `df98c8357afe85853d9a4776255d4f0e7cf064cb` in `/Users/uk016635/projects/gemfire`. Paths below are relative to `gemfire-core/src/main/java/org/apache/geode/`. The previously validated native submission, registry and permission probes establish native behavior; the new envelope results add the limited transport evidence described below.

## Transport constraints

- `internal/cache/execute/AbstractExecution.java:362–406` distinguishes object execution from ID execution and checks object identity/HA/result metadata. Object execution requests function serialization even when the object is registered locally.
- `cache/client/internal/ExecuteFunctionOp.java:177–194` encodes metadata and either the object or its ID. The region equivalent is `ExecuteRegionFunctionOp`; existing native submission tests cover both routes.
- `internal/cache/tier/sockets/command/ExecuteFunction70.java:145–245` deserializes the function, selects a server registration only for a string ID, and evaluates permissions on that selected instance before invoking the body. A client wrapper cannot evaluate permissions in advance or replace object submission with an ID without changing behavior.
- The existing `NativeServerFixture` launches a separate JVM with the explicit `nativeFixtureClasspath`. Object serialization does not deploy class definitions. Adapter, application body and payload classes must be available to the receiving process, subject to its serialization policy. Missing classes and rejected payloads require failure mapping; registration is a separate concern.

## Selected boundary

An application declaration contains its serializable state, metadata, execution callback and permission callback using native-free types. A private versioned-driver envelope implements native `Function`, delegates metadata to the retained declaration, and submits the envelope as an object. It must serialize the declaration rather than the live connection, pool, injected execution service or local registry. Local registration retains the declaration identity through the envelope and delegates native sharing/lifetime. It does not create a global driver registry.

The server deserializes an independent declaration copy, evaluates its permission callback and then executes that same copy. The driver creates a callback context/sender for that invocation. Invocation arguments and results remain application values; they are not an arbitrary native-handle escape hatch. The local client object must not receive server callback mutations. Later submissions must capture current object state; no cached serialized body or permission list.

Client-local callbacks retain borrowed client cache/region handles under the existing disposition. A remote callback cannot obtain a client handle wrapping a server cache. The experiment exposes arguments, function ID, region path/filter, duplicate metadata and result delivery. The decision below separates remote metadata from client-local handles and assigns full principal/member/sender acceptance to implementation tasks. Native service bodies such as client region discovery through `GetRegionsFunction` belong behind a typed driver operation, with their native context and results converted inside the driver.

Deployable adapter/application class packaging, compatibility checks and allowed payloads are implementation requirements. A successful shared-classpath probe would not prove that an ordinary unmodified server can execute the envelope. ID invocation of an already installed native server function must continue without requiring that server to load an envelope it never receives. No public server installation or hosting API follows from object transport.

## Private probe

`spring-data-gemfire-driver-10.4/src/submissionProtocol/java/com/vmware/gemfire/driver/fixture/submission/SubmissionProbe.java` is in a separate JDK-only source set with no declared dependencies. Only the native integration source set consumes its output. It is neither part of the public driver artifact nor shared with Spring production/tests. Its simple four-component permission record tests delegation only; it does not implement the 57-row permission model or serialize effective wildcard parts.

`NativeClientFunctionEnvelopeTests` adds three cases covering five targets: client region, cache/pool single server, cache/pool all servers. Each case uses a separate server process. The checks require:

1. A locally registered envelope retains the exact application object and remains absent from the server registry.
2. Both permission and body callbacks execute remotely, share the same deserialized application state, and leave client callback counters unchanged.
3. Two object submissions through the reused executor reflect a changed client field and fresh remote counters; a local registration does not force ID execution.
4. The region callback retains its region path/filter; pool/cache callbacks have no region context. Intermediate and terminal results reach the collector without imposing additional ordering guarantees.

The fixture has one server and no security manager. It does not establish fan-out, retry/HA, secured allow/deny, authenticated views, full permission conversion, serialization-policy failures, no-result behavior, local persistent execution, or production packaging compatibility. Those remain explicit requirements under tasks 24/25/32. The existing native suites retain their separate ID/local/permission coverage. The subsequent deployment control below separately validates missing-envelope failure.

## Validation receipt — 2026-10-28

The user confirmed the following command passed; local JUnit XML independently confirms all 15 tests passed. Transitional migration boundary verification passed as part of the reported command. This does not establish native-free compliance. Validated command:

```sh
./gradlew --offline -DgemfireVersion=10.4.0-build.3056.df98c835 \
  :spring-data-gemfire-driver-10.4:integrationTest \
  --tests '*NativeClientFunctionEnvelopeTests' \
  --tests '*NativeClientFunctionSubmissionTests' \
  --tests '*NativeClientFunctionRegistryTests' \
  --tests '*NativeClientFunctionPermissionTests' \
  verifyMigrationBoundary
```

The integration source-set dependency compiles the neutral protocol using its own JDK-only compile classpath before compiling the envelope. The probe is validated within its single-server, unsecured, shared-classpath limits. Production packaging, secured authorization and complete context implementation remain outstanding. The subsequent decision settles task 64 representation requirements; tasks 25/32 remain blocked by parent 59 for the remaining scope reconciliation.

## Deployment negative control — validated

`NativeClientFunctionDeploymentTests` starts the same private server fixture with an isolated classpath. It removes the JDK-only application output and the envelope test classes, while retaining native fixture functions. A native-only installer verifies that neither the application body nor the envelope can be loaded on the server, then registers an independent native body under the application's function ID. No classpath system property is changed globally.

The test requires ID execution to succeed before and after an object submission fails with a missing-envelope class cause. Using the same ID makes a silent object-to-ID fallback observable: it would incorrectly execute the independent body. Client callback counters and local registry lookup must remain unchanged. The observed single-server failure is `ServerOperationException` with a root `ClassNotFoundException`, based on `ExecuteFunction70` deserialization handling and `ExecuteFunctionOp`'s exception response handling; the user-run test confirms this failure path.

The user reported the requested run passed. Local JUnit XML confirms all 16 tests passed: one deployment, three envelope, three submission, six registry and three permission, with zero failures/errors/skips (2026-10-28T00:53:43–00:54:01Z). It removes both callback classes together; it does not establish behavior when only the application class is missing, serialization allow-list rejection, incompatible class versions, or a production deployment artifact. Those cases remain per-version implementation acceptance under task 25. ID execution still requires any classes used by its arguments/results and the installed native body; independence from an unused envelope does not imply independence from all application classes.

Validated user-run command (including transitional migration boundary verification; not native-free compliance):

```sh
./gradlew --offline -DgemfireVersion=10.4.0-build.3056.df98c835 \
  :spring-data-gemfire-driver-10.4:integrationTest \
  --tests '*NativeClientFunctionDeploymentTests' \
  --tests '*NativeClientFunctionEnvelopeTests' \
  --tests '*NativeClientFunctionSubmissionTests' \
  --tests '*NativeClientFunctionRegistryTests' \
  --tests '*NativeClientFunctionPermissionTests' \
  verifyMigrationBoundary
```

## Context and deployment acceptance

Pinned `cache/execute/FunctionContext.java` and `internal/cache/execute/FunctionContextImpl.java` support the following requirements for the selected typed contract:

| Concern | Required boundary and remaining acceptance |
| --- | --- |
| Member identity | Preserve the executing member's name, falling back to its ID when the name is blank. Convert native member values within the driver; do not substitute the submitting client's identity. |
| Principal | Preserve the executing invocation's principal as application data, including legitimate null values. The native context captures it from the security service; unsecured probes cannot justify hardcoding null. Secured and authenticated-view tests remain required. |
| Cache and dataset | Client-local callbacks borrow typed client handles. A remote server cache must not be presented as a client handle. Remote submitted callbacks receive region identity/filter metadata, not a server dataset handle. Native server-data bodies remain independently deployed functions invoked by ID; region discovery belongs behind its typed driver operation. See the consumer disposition below. |
| Result sender | Preserve callback lifetime, intermediate/terminal results, exception-valued terminal results and thrown failures. Native documentation limits sender use to the executing callback. The envelope probe covers ordinary delivery only. |
| Deployment | Object submission requires compatible envelope, application and payload classes plus permitted serialization on the receiving process. Keep native transport packaging out of Spring dependencies. ID submission must not require an envelope that it never sends. |

These are implementation obligations, not new public server APIs or claims of a completed implementation. Scope and implementation statuses remain unchanged.


## Representation decision — 2026-10-28

The selected representation is a native-free, serializable application declaration wrapped by a private versioned native envelope. The declaration retains identity and mutable application state locally; object execution sends its current state on each invocation. ID execution sends the ID and selects the independently installed server body. Neither path is a fallback for the other. Metadata, both permission hooks and body execution follow the existing member/value dispositions. The server evaluates permissions and executes the same deserialized declaration instance; local client callbacks execute against the retained declaration. Contexts, senders, client connections, pools and registry collaborators are invocation/runtime objects and must not be captured as transport state.

Use a common callback context for arguments, function ID, executing member name/ID, invocation principal, duplicate flag and typed result sender. A region-context variant adds the executing region path and native-supplied filter subset; it must not replace that subset with the original client filter. Only a client-local context extension exposes borrowed typed client cache/dataset handles. Remote contexts do not implement that extension. A callback requiring local handles must fail explicitly when submitted to an incompatible target; it must not receive null, a fabricated client connection or a server cache disguised as a client. Concrete public Java names and signatures belong to task 25's implementation and ABI review.

This distinction applies the approved client-only amendment: the retained `getCache`/`getDataSet` rows map to evidenced client-local behavior. Their required-client classification does not require a public server cache/region programming model. General application functions that need native server cache, partition helpers, management services or native PDX types remain separately built/deployed server functions invoked through the typed ID path. Their server code is outside Spring Data and the client SPI. Migrating such a body to the neutral submitted-object form is not promised; this is an explicit breaking API boundary, not native callback source compatibility. Application-value callbacks remain supported as submitted objects without requiring server registration.

### Existing consumer disposition

The source audit checked Spring production function construction and the 13 relocated fixture files:

| Consumer/body | Required migration and owner |
| --- | --- |
| `FunctionGemfireAdminTemplate.getAvailableServerRegions` | Task 25 provides typed server-region-name discovery; task 32 migrates this caller. Keep native `GetRegionsFunction`, its `false` verbose argument and `RegionInformation` conversion inside the versioned driver. Preserve multi-server name aggregation. |
| `GemfireDataSourcePostProcessor.regionNames` | Task 32 migrates the second caller to the same discovery collaborator and coordinates cache injection with task 35. Preserve proxy-region creation and its existing-name handling. Do not leave a second native function-construction path. |
| `OneArgFunction`, `TwoArgFunction`, `GetMapWithNoArgsFunction` | These private server fixtures read `TestRegion` through native `FunctionContext.getCache()`. `FunctionIntegrationTests` already calls IDs `oneArg`, `twoArg` and `getMapWithNoArgs`; retain those real-server assertions and separate deployment. No remote dataset handle is needed in the client SPI. Tasks 32/40 retain this acceptance. |
| `AddressFieldFunction`, `DataFieldFunction` | Native PDX field extraction remains private server infrastructure invoked by client ID. Preserve client PDX argument/results tests; native types do not enter a neutral application callback contract. |
| Other relocated bodies | Keep their private deployment and existing ID/DTO protocol. Fixture relocation is complete; they are not production envelope implementations. |
| Application object overloads in client templates | Task 32 replaces native `Function` with the typed declaration, retaining state, filters and result/no-result variants. Do not reduce an object to its ID. Document the local-handle extension and the separate server-function ID migration above. |

Pinned `gemfire-gfsh/src/main/java/org/apache/geode/management/internal/cli/functions/GetRegionsFunction.java` reads server root regions, builds native `RegionInformation` and sends an exception result on failure. This is a driver-owned implementation of client discovery, not justification for public server management. The driver returns native-free names or typed failures. Both Spring discovery callers currently catch failures and return empty results; task 32 must explicitly reconcile that legacy behavior with the spec's fail-fast rule, with tests for empty success versus discovery failure. It must not bake silent failure into the new SPI.

### Deployment contract and ownership

Task 25 owns a deployable transport payload from the versioned driver, containing its private envelope/adapters and the required native-free callback API runtime dependencies. Keep this separate from Spring's compile/test dependencies and from the private test-fixture JAR. Application callback and argument/result classes are deployed separately by the application owner. Native runtime classes are supplied by the receiving GemFire installation, not bundled as a second runtime. The neutral callback must not depend on Spring, driver-core binding, ServiceLoader/provider discovery or a client connection to run on the server.

Use the driver's exact native version and SPI ABI rules for the transport payload; task 25 documents the supported deployment tuple and serialization requirements, and task 40 verifies it for every supported driver. Do not promise mixed-version envelope compatibility based on Java serialization or the current probe. No public install/start/register-server API is introduced. ID invocation remains available without installing an envelope that it never sends; its own body/payload dependencies still apply.

Acceptance before task 25 closes:

- Build the real deployable transport artifact and execute a native-free application callback in a separate server process using only that artifact, its declared runtime dependencies, native runtime and explicit application payload. Exclude integration-test output and Spring from that process. Verify classpath/publication contents as well as execution.
- Cover successful region and one/all-server object execution, fresh state on repeated submission, local identity and dynamic permission/body instance sharing. Preserve local supported/rejected shortcuts and native registry sharing/lifetime without a driver-owned static map.
- Cover missing envelope, application-only class absence, serialization-policy rejection, nonserializable captured state and incompatible deployed classes. Map failures with useful typed diagnostics, preserve causes, and never retry as ID execution. Verify independent same-ID invocation before/after each applicable rejection.
- Implement the context/sender table above, lossless permission conversion, secured principals and authorization, authenticated views, HA/retry and multi-server behavior with bounded per-version tests. Do not claim these from the 16 unsecured probe tests.
- Task 32 covers native-free Spring object/ID/local-context orchestration, both discovery consumers and breaking migration notes. Task 40 carries packaged deployment and secured compatibility into the all-version matrix. The mock explicitly rejects remote execution and does not simulate deployment or authorization.

This closes the task 64 representation/deployment **decision**, not implementation. The validated 16-test receipt supplies transport feasibility and a deployment negative control; the additional requirements above are uncompleted acceptance owned by tasks 24/25/32/40. No downstream task is unblocked while parent 59 remains open. Scope stays 499 required-client / 629 excluded-server / 2,723 unresolved, with all 3,851 IDs and implementation statuses unchanged. Task 64 continues with the remaining mixed scope families and infrastructure reconciliation.
