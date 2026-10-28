# Client function member scope

## Decision and evidence boundary

Task 64 resolves all 72 frozen `org.apache.geode.cache.execute` rows: 68 are required-client and four peer-target overloads are excluded-server. The associated internal service type is also required through typed client assembly. Both permission hooks require native-free equivalents as specified below; the [representation decision](client-function-submission-representation.md#representation-decision--2026-10-28) now settles remote context and deployment requirements. Scope classification does not implement an SPI or close tasks 25/32. All frozen IDs and implementation statuses remain unchanged.

Evidence is GemFire `10.4.0-build.3056.df98c835`, native source commit `df98c8357afe85853d9a4776255d4f0e7cf064cb` in the local GemFire checkout. Paths below are relative to `gemfire-core/src/main/java/org/apache/geode/`; line references describe that commit, not checkout HEAD. Read with `git show <commit>:<path>`. Dispositions use the pinned source and the explicitly recorded runtime results below; they do not imply that every behavior has runtime coverage. The previously validated [object-submission audit](client-only-spring-surfaces.md#client-function-submission-and-fixture-audit--2026-10-27) and [fixture relocation](client-function-fixture-relocation.md) supply the recorded client test evidence.

## Client execution

`internal/cache/execute/AbstractExecution.java:362–406` accepts both ID and object submission, including timed forms. Object submission validates identity and the HA/result combination and preserves the object for serialization. `OnServerExecutor.java:35–108` implements client ID lookup, metadata retrieval, collector selection and result/no-result execution. `ServerRegionFunctionExecutor.java:128–149,240–259` preserves region filters, arguments and collectors; `ServerFunctionExecutor.java:187–220` rejects filters for data-independent server execution and accepts arguments and collectors. Required behavior includes this rejection, not universal filter support. `withArgs` and `setArguments` may share a typed equivalent; native names need not be copied into the SPI.

## Client submission metadata

`cache/execute/Function.java:40–121` defines identity, result, HA and write-routing metadata. `cache/client/internal/ExecuteFunctionOp.java:177–194` and `ExecuteRegionFunctionOp.java:163–166` consume these flags and serialize either the function or its ID. Both frozen `getId` return descriptors remain mapped to the same identity requirement. A required type row does not settle every member of that type: the execution body is retained by the client-local evidence below; permission hooks are retained by the permission disposition below.

## Client failures

`cache/execute/FunctionException.java:44–119` supplies constructors and exception aggregation; inherited message and root-cause access preserve failure diagnostics (`GemFireException.java:79–80`). `cache/client/internal/ExecuteRegionFunctionOp.java:317–361,417–429` builds aggregate failures and wraps collector errors. `ExecuteFunctionOp.java:304–369` handles returned failures and collector errors. Task 25 must preserve causes and aggregate contents through typed exceptions rather than expose native exception classes.

## Client targets

`internal/cache/execute/InternalFunctionExecutionServiceImpl.java:128–154` distinguishes server-proxy region execution from locally hosted region execution, including the validated persistent client-local path below. Lines 217–284 create pool, default-client-pool and authenticated-view executors, including native restrictions on direct multiuser-pool execution. The service type and five region/pool/region-service target overloads are required through injected client collaborators. This does not require a public static service or its protected native constructor.

## Client collectors

`cache/execute/ResultCollector.java:65–123` defines result retrieval, timed interruption, member-tagged results, completion and clearing for retries. `cache/client/internal/ExecuteFunctionOp.java:233–235,304–344` clears retries and delivers member/result pairs; `internal/cache/execute/ServerFunctionExecutor.java:122–144` signals completion. All six rows require typed equivalents, including member metadata without native `DistributedMember`. Preserve no-result behavior and timeout/interruption handling without strengthening ordering or retry guarantees.

## Peer targets

The four `onMember`/`onMembers` overloads route to distributed-system member/group execution (`internal/cache/execute/InternalFunctionExecutionServiceImpl.java:94–110,291–358`). They are excluded by the [approved client boundary](../client-only-scope.md). Client pool/server-group routing remains a separate requirement; it must not be implemented by restoring peer targets.

## Remaining review and implementation owners

Both `Function#getRequiredPermissions` overloads are required-client under the permission disposition below. This settles their retention and semantic mapping, independently of the still-open remote submission transport design and reachable permission-value member review. The protected constructor and its internal service dependency map to injected typed client assembly below. The client-local disposition below retains 23 body/context/sender rows; typed implementation and remote submission representation remain outstanding. The five local registry operations are now required-client; their typed implementation remains outstanding. Server fixture relocation alone does not prove that every callback type can disappear from a typed submission contract.

In particular, `internal/cache/execute/OnServerExecutor.java:35–71` checks the local registry before fetching and caching server metadata for ID submission. `InternalFunctionExecutionServiceImpl.java:158–211` implements local registration and lookup; it is not a remote function-existence query. The client-local registry disposition below retains these five operations; their native static API shape is not required. Task 25 then owns the typed implementation and per-driver compatibility tests; task 32 owns Spring cutover and native-free orchestration tests. Both remain blocked by parent 59. Retain real-server exception/PDX/result/multi-server coverage and explicit mock rejection of remote execution.

The Spring cutover must also repair the three function-object/filter defaults identified in task 63, replace message-based no-result inference with typed behavior, preserve timeout/interruption translation and keep region-discovery execution behind the driver boundary.

### Registry compatibility probes — validated

`NativeClientFunctionRegistryTests` adds five native integration cases for client region, pool one/all-server and client-cache one/all-server targets. Each case starts a separate server process and exercises the five registry operations without exposing them through the SPI. A client registration must remain local: ID invocation fails before the server has a body. A submitted setup function then registers a different instance on the server. ID execution must read the locally registered result/HA/write metadata and execute the server instance; object execution must preserve the submitted instance's state. Unregistering locally must clear lookup, registration status and enumeration while allowing ID execution through remote metadata retrieval. The test cleans its local registration in `finally`; server registrations end with the private process.

The source basis is `internal/cache/execute/OnServerExecutor.java:35–82` and `InternalFunctionExecutionServiceImpl.java:158–211`, plus the function-versus-ID message branches in `cache/client/internal/ExecuteFunctionOp.java:177–185` and `ExecuteRegionFunctionOp.java:163–166`. These probes distinguish client metadata behavior from server installation. They do not establish a public registry design, multi-server fan-out, authenticated metadata isolation, replacement semantics, or coverage for execution-context/permission members.

The first user-run validation compiled and ran all eight tests: the region registry case and all three existing object-submission cases passed; four server-target registry cases failed at the missing-function exception assertion. The fixture incorrectly expected `FunctionException` for every target. The server-target reports show `ServerOperationException` identifying the unregistered function. Pinned `cache/client/internal/ExecuteFunctionOp.java:372–380` throws that type for an `EXECUTE_FUNCTION_ERROR` response; `internal/cache/execute/ServerRegionFunctionExecutor.java:165–177` preserves or wraps failures as `FunctionException`. The assertion now selects the expected type per target and checks the function ID in the message. Tasks 25/32 must preserve typed failure handling for both native paths rather than assume all function failures arrive as native `FunctionException`.

The user-run corrected command below passed in 25s on 2026-10-28. Local JUnit XML confirms all five registry tests and all three object-submission tests passed, with zero failures, errors or skips. The four server-target cases now exercise their full registration, metadata, ID/object state and unregister assertions. The command also passed transitional migration boundary verification; this does not establish native-free compliance. At that checkpoint, all 31 remaining rows stayed unresolved and tasks 64/59 remained open, pending typed metadata ownership/lifetime and submission-body decisions. The subsequent registry disposition below resolves five of those rows. Do not recreate a process-wide static registry in the SPI to mirror the native implementation.

```sh
./gradlew --offline -DgemfireVersion=10.4.0-build.3056.df98c835 \
  :spring-data-gemfire-driver-10.4:integrationTest \
  --tests '*NativeClientFunctionRegistryTests' \
  --tests '*NativeClientFunctionSubmissionTests' \
  verifyMigrationBoundary
```

### Registry replacement and lifetime — validated

The next source review narrows the lifecycle design without changing scope counts. `InternalFunctionExecutionServiceImpl.java:63–65,158–211` owns a native static concurrent map: registration replaces the object for an existing ID, lookup returns that object, and enumeration returns a new map excluding internal functions. The copy retains the same function objects; changing its entries does not change native registration. `OnServerExecutor.java:35–71` looks up the current registered object on each ID execution, including when an executor is reused. Replacing local registration must not replace the server's body.

The registry is not simply immortal process state. `internal/cache/GemFireCacheImpl.java:2645–2649` normally disconnects the distributed system on cache close; `distributed/internal/InternalDistributedSystem.java:1694–1697` calls `InternalFunctionService.unregisterAllFunctions()`, implemented at `InternalFunctionExecutionServiceImpl.java:387–394`. The cache-close branch is conditional: this evidence covers ordinary close/disconnect, not reconnect, `keepDS` or disabled-disconnect configurations. The private server process has a separate registry and remains running when the client disconnects.

This follows technical-spec §§5.6–5.7: the future injected client function collaborator must not create a driver-owned process-wide registry, but it also must not promise isolation that native GemFire's shared registry does not provide. A borrowed collaborator must not unregister another caller's functions when released. Native disconnect owns native bulk cleanup; no public reset hook or server-installation API follows from that fact. Task 25 must retain replacement, lookup identity and detached enumeration semantics when defining the typed representation. An immutable metadata snapshot alone is insufficient evidence of equivalence to native lookup of a retained function object. Do not turn locally cached metadata into a claim of remote availability or authorization. Authenticated-view metadata isolation and typed submission-body representation remain unresolved.

`NativeClientFunctionRegistryTests` now extends all five target cases to replace the registered instance, check detached enumeration, reuse the executor, observe metadata reads only on the replacement, and distinguish server ID execution from replacement object execution. A sixth test closes the ordinary client, verifies local lookup/status/enumeration are cleared, opens a fresh client and invokes the still-registered server body by ID. Both client caches and the local registration have failure-safe cleanup; each test retains a separate server process. The user-run command above passed in 23s on 2026-10-28. Local JUnit XML confirms six registry tests and three submission tests passed with zero failures, errors or skips; transitional migration boundary verification also passed.

This establishes the tested replacement/snapshot/ordinary-disconnect behaviors on the pinned native release. It does not establish authenticated isolation, multi-server fan-out, automatic reconnect behavior, typed SPI completion or native-free compliance. At that checkpoint, all 31 rows remained unresolved. The subsequent disposition below retains five local registry operations while keeping 26 representation/constructor rows unresolved.

## Client-local registry disposition

The five local registry operations are **required-client**, independently of the unresolved submission-body representation. Their client callability and observable behavior are established by `InternalFunctionExecutionServiceImpl.java:158–211`, `OnServerExecutor.java:35–82` and the six validated registry probes above. A client-only boundary excludes server installation; it does not exclude native operations that retain a submitted object and supply client invocation metadata. This decision adds no public server hosting contract and does not mark an SPI implemented.

Task 25 must provide an injected, typed equivalent with the following acceptance obligations:

| Native operation | Required client behavior |
| --- | --- |
| `registerFunction` | Retain the submitted object and its state; replace an existing registration with the same ID. Reject null functions/IDs and HA-without-results through typed failures. Registration alone does not make ID execution possible on a server. |
| `getFunction` | Return the retained submission representation, preserving underlying object identity and current metadata rather than just a copied ID/flag snapshot; return absence for an unknown ID. Reject null IDs. |
| `isRegistered` | Inspect local registration only; reject null IDs. Do not report remote existence or permission. |
| `getRegisteredFunctions` | Return detached map entries retaining the registered objects; exclude native internal functions. Mutating the returned map must not register or unregister anything. Do not promise an atomic snapshot under concurrent native updates. |
| `unregisterFunction` | Remove the local entry; an absent ID is a no-op and null is rejected. A server registration remains usable through ID execution and remote metadata retrieval. |

Delegate native sharing and disconnect cleanup to the native registry. The injected collaborator does not own a new process-wide map, an isolated per-view registry, a public reset operation or automatic unregister-on-release. Reused executors must observe replacement metadata. Mock local registration can exercise retained object identity, replacement, enumeration and validation through explicitly shared injected state; remote execution must still fail explicitly as unsupported. Native lifecycle/sharing compatibility requires per-version tests, not a claim that the mock simulates a distributed system.

The validated tests cover replacement, object/ID distinctions, metadata reads, detached enumeration, unregister and ordinary client disconnect. Null/invalid metadata, native internal-entry filtering, authenticated execution and concurrency remain acceptance work for task 25; source evidence establishes their obligations without claiming runtime validation. Task 32 must use injected collaborators for any retained client registry use, preserve native-free signatures and avoid restoring retired server registration configuration.

At the registry-only checkpoint, 26 rows remained unresolved. That decision did not choose a generic executable callback or opaque native handle, settle server permission hooks, or prove that a metadata-only submission is equivalent. Task 64 must finish the body/context/sender/permission representation and constructor disposition before parent 59 closes. No native cache/context or arbitrary unwrap may leak into Spring to bypass that decision.

## Client-local function callbacks — validated

The pinned source at `df98c8357afe85853d9a4776255d4f0e7cf064cb` exposes a second execution path that must be checked before excluding callback contexts and result senders. `InternalFunctionExecutionServiceImpl.java:148–154,401–402` selects the server-region executor when the region has a server proxy; other non-partitioned regions use `DistributedRegionFunctionExecutor`. This distinction is not simply whether the owning cache is a `ClientCache`.

`GemFireCacheImpl.java:5106–5142` configures `LOCAL_PERSISTENT` and `LOCAL_PERSISTENT_OVERFLOW` with `PERSISTENT_REPLICATE`, while the other three local client shortcuts use `NORMAL`. `DistributedRegionFunctionExecutor.java:158–201` rejects `NORMAL` and otherwise delegates execution to the region. `LocalRegion.java:10243–10268` constructs a region function context and result sender with the local cache, dataset, arguments and filter; `AbstractExecution.java:274–294,320–358` invokes the body locally and reports callback failures or missing terminal results. The runtime probes below confirm client-side use of these callbacks on both persistent local shortcuts without server hosting.

`NativeClientLocalFunctionTests` adds five private native integration tests without a server fixture or server endpoints:

- Both persistent local shortcuts execute an unregistered object and a registered ID. The probes check retained object state, client cache/dataset identity, filter, arguments, function ID, member name, unauthenticated principal, duplicate flag and local process identity. `sendResult` and `lastResult` deliver both results.
- The three non-persistent local shortcuts reject both submission forms before the callback, identifying the `NORMAL` data-policy restriction.
- `sendException` completes collection with the exception as a result; a callback without a terminal result fails collection. These expectations follow `DistributedRegionFunctionResultSender` and `LocalResultCollectorImpl` in the same pinned source.

All result collection is bounded and registry cleanup runs in `finally`. The test records callback observations for assertions on the test thread. It does not establish authenticated principals, retries, permission-hook behavior, disk recovery, mock support or a typed SPI design. In particular, client-local callbacks must not be used to justify exposing server caches or peer execution through the SPI. The future design must distinguish borrowed client-local handles from remotely executed submission bodies.

The user-run command passed in 25s on 2026-10-28. Local JUnit XML confirms all five local tests, six registry tests and three submission tests passed with zero failures, errors or skips. Transitional migration boundary verification also passed; this does not establish native-free compliance:

```sh
./gradlew --offline -DgemfireVersion=10.4.0-build.3056.df98c835 \
  :spring-data-gemfire-driver-10.4:integrationTest \
  --tests '*NativeClientLocalFunctionTests' \
  --tests '*NativeClientFunctionRegistryTests' \
  --tests '*NativeClientFunctionSubmissionTests' \
  verifyMigrationBoundary
```

This validation supports the 23 client-local dispositions below. At this callback checkpoint, scope was 438 required-client / 629 excluded-server / 2,784 unresolved, with three function rows still unresolved; the subsequent service assembly decision below resolves the constructor and its internal dependency. No Gradle command was run by the agent.

## Client-local callback disposition

The 23 body/context/sender rows listed below are **required-client**. The validated tests establish that a client cache can invoke a function body on its own persistent local region using both object and registered-ID submission. Their retention does not authorize server cache creation, peer execution, or public server hosting. Native implementation class names and the native `Cache` return type do not determine the supported SPI surface.

Task 25 must provide these typed equivalents; names and signatures remain implementation work:

| Native surface | Retained client behavior and boundary |
| --- | --- |
| `Function#execute` | Invoke the retained stateful body with a typed client-local context when the target supports local execution. Preserve native rejection for the three `NORMAL` local shortcuts. Do not substitute an ID for object submission. |
| `FunctionContext` and inherited region-context members | Preserve arguments, function ID, member name, available principal and possible-duplicate metadata. The tested initial unauthenticated calls report null principal and false duplicate; do not hardcode those values or claim authenticated/retry coverage. |
| Both `getCache` members | Return a borrowed typed client-cache view of the executing client cache. Do not expose native `Cache`, grant ownership through the callback, create a new cache, or manufacture a client handle for a remote server context. |
| `RegionFunctionContext#getDataSet`, `getFilter` | Expose the executing local dataset through a typed borrowed region and retain the invocation filter. Preserve underlying cache/region identity and lifetime without promising the same Java wrapper instance. |
| Both `getResultSender` members and `ResultSender` | Supply a typed sender valid during the callback. Preserve intermediate and terminal results, `sendException` as a terminal exception-valued result, and missing-terminal failure. Do not strengthen native ordering, cancellation or retry guarantees. |

The tests observe the same retained function instance and native cache/dataset, with local process identity and state. `FunctionContextImpl` and `RegionFunctionContextImpl` in the pinned source supply the remaining metadata semantics. An application principal is invocation data, not an escape hatch for native service/context access. Native-free callback types must not expose arbitrary native unwraps.

Task 32 must preserve these callbacks in client template/proxy flows and native-free orchestration tests. Task 25 must add shared contract and per-version native tests for the typed adapters, lifecycle/failure translation and supported local execution. Mock callback/orchestration tests must use explicit injected collaborators; the validated native persistent-region path does not imply that the mock supports disk persistence or remote execution. Any unsupported mock target must still fail explicitly. Keep real-driver tests for native persistence and execution routing.

The local callback representation is settled at this semantic boundary, but does not solve transport of remotely submitted bodies or their server-side permission hooks. Task 64 retains those remote representation and permission decisions; tasks 25/32 remain blocked by parent 59. No SPI implementation status changes with this classification.

## Client function service assembly

`cache/execute/FunctionService.java:43–54` constructs a native singleton with an `InternalFunctionExecutionServiceImpl` and stores that dependency through the protected constructor. Its comment identifies subclass assembly for `InternalFunctionService`; `internal/cache/execute/InternalFunctionService.java:35–46` confirms that use. Public static operations obtain the dependency from the native singleton (`FunctionService.java:288–289`), so constructing a subclass does not redirect those static operations to an application-supplied instance.

The constructor and reachable internal service type are **required-client through equivalent typed assembly**. This preserves the existing frozen internal-boundary requirement to replace implementation injection with a typed execution service; it does not import the internal interface or its peer operations into the SPI. Task 25 owns a constructor-injected client execution collaborator supporting the retained targets, registration, submission and result contracts. Native static delegation may remain private to the versioned adapter. Do not add a driver-owned singleton, static registry, service locator, subclassing requirement or native constructor signature. Borrowed connection/pool ownership and the registry lifecycle constraints above still apply.

Task 32 must obtain that collaborator by injection and verify template/proxy wiring with native-free substitutes. Task 25 must validate the retained behavior through shared contracts and each native adapter; the native protected constructor itself need not be reproduced or executed by a compatibility test. These are source-backed equivalence decisions, not implemented SPI coverage.

## Permission hook probes — validated

`cache/execute/Function.java:146–148,174–175` defaults to DATA:WRITE and delegates the argument-aware overload to the region-only overload. `internal/cache/tier/sockets/command/ExecuteFunction70.java:213–220` calls the argument-aware hook with a null region and submitted arguments before execution. `ExecuteRegionFunction66.java` likewise calls it with the region path and arguments. The server authorizes every returned permission. A submitted object's declaration therefore cannot be dismissed merely because authorization runs on the server.

`NativeClientFunctionPermissionTests` adds three probes: defaults for both overloads; remote delegation to an overridden region-only hook; and remote dispatch to an overridden argument-aware hook. Each remote case exercises region, pool one/all-server and client-cache one/all-server targets. Results report hook counts, region/arguments and process identity; the client object's counters must remain untouched, and submission must not create a local registration. Collection is bounded and the cache/private server have resource-safe cleanup.

These probes use an unsecured, single-server fixture. They do not validate permission enforcement, authenticated views, denial, retries or multi-server fan-out. Tasks 24/25 retain secured per-driver acceptance. At this probe checkpoint both permission rows remained unresolved; the disposition below now retains their typed equivalents. Remote submission representation remains open; no server cache/context or native permission type may leak into Spring.

The user-run command below passed in 31s on 2026-10-28. Local JUnit XML confirms all 17 tests passed: three permission, five local, six registry and three submission tests, with zero failures, errors or skips. The permission probes confirm DATA:WRITE defaults, delegation to the region-only override, argument-aware override precedence, `/distributed` for region execution and null for server targets, and server-process hook execution without mutating the client object or registering it. Transitional migration boundary verification also passed. This validates hook dispatch on the pinned release, not secured authorization, typed SPI completion or native-free compliance. Scope counts and implementation statuses are unchanged. Validated command:

```sh
./gradlew --offline -DgemfireVersion=10.4.0-build.3056.df98c835 \
  :spring-data-gemfire-driver-10.4:integrationTest \
  --tests '*NativeClientFunctionPermissionTests' \
  --tests '*NativeClientLocalFunctionTests' \
  --tests '*NativeClientFunctionRegistryTests' \
  --tests '*NativeClientFunctionSubmissionTests' \
  verifyMigrationBoundary
```

## Client function permission disposition

Both permission hooks are **required-client through typed native-free declarations**. The validated submitted-object probes establish that client-authored function state determines which hook executes on the remote process and what it receives. Server execution of that declaration does not turn it into a server security-management API. This decision retains behavior; task 25 still owns implementation and remote submission representation remains a task 64 prerequisite.

| Native behavior | Required typed equivalent |
| --- | --- |
| Region-only declaration | A submission callback taking the optional region path and returning permission values. Its default requires DATA:WRITE without restricting the target or key. Preserve the observed `/distributed` callback input; do not normalize it before application callback dispatch. |
| Argument-aware declaration | A callback taking the same optional path and application arguments. By default delegate to the region-only callback; an explicit argument-aware override takes precedence. Do not replace dynamic declarations with a cached permission list at registration or submission time. |
| Permission values | Native-free, immutable declaration values that can represent resource, operation, target and key restrictions and native wildcard/subpart forms. Conversion to native `ResourcePermission` belongs inside the versioned implementation. No Shiro/native object, generic unwrap or server security-manager handle crosses the SPI. |
| Remote evaluation | Evaluate against the function instance selected on the server. Object submission preserves submitted state; ID execution uses the server-registered body and its declarations. Local registration does not override server permissions or prove authorization. Do not pre-evaluate the callback on the client to fabricate equivalent authorization. |
| Collection semantics | Preserve the native requirement that every declared permission is authorized before the body runs. An empty collection declares no additional function permissions; it does not bypass other server checks. Preserve failures from invalid declarations or callback exceptions rather than silently substituting an empty collection. |

`security/ResourcePermission.java` at the pinned commit supplies resource/operation enums, target/key strings, wildcard constants, comma-separated subparts and a wildcard-string constructor. Its component constructors strip leading region separators from target strings; that conversion is separate from the path supplied to the function callback. A four-field snapshot obtained through getters is not proven sufficient for the wildcard-string constructor, which initializes inherited permission parts directly. Task 24 must review those reachable value/constructor/inherited rows individually and specify lossless typed conversion, equality and validation before implementation. Their scope is not silently changed by these two hook dispositions. A permission naming CLUSTER or MANAGE is a declaration value, not permission to add cluster management operations to the client SPI.

Tasks 24/25 own these acceptance cases:

- Native-free contract tests for defaults, region-only delegation, argument-aware override precedence and argument-dependent declarations. Verify target/key/wildcard/subpart conversion using adapter tests; do not drop declared restrictions or infer grant semantics in Spring.
- Secured per-version tests for successful execution, denied execution with no body effect, and multiple permissions where granting only one still denies execution. Cover region and server targets, region-specific versus wildcard declarations, authenticated views and two users without metadata or authorization leakage.
- ID-versus-object tests with different server and submitted declarations, proving that client registry replacement cannot change authorization of the registered server body. Preserve remote callback state and typed authorization failure translation.
- Native-free Spring orchestration tests with injected collaborators; mock remote execution/authentication remain explicitly unsupported. The validated local-callback path does not justify inventing a local authorization pass absent from native execution.

The 31s build validates defaults and dispatch only. Secured enforcement, full permission-value compatibility and the typed implementation are outstanding. Current scope becomes 442 required-client / 629 excluded-server / 2,780 unresolved; function-package scope is 68 required-client / four excluded-server / zero unresolved. All 3,851 frozen IDs and implementation statuses are preserved. Tasks 64/59 remain open for remote submission design and the rest of the scope review.

## Exact frozen row dispositions

The tables below enumerate the reviewed rows; overload identity uses the frozen JVM descriptor. Including the internal service type, implementation status remains unchanged: 70 reviewed rows are `missing`; the `FunctionException` type row is `partial`.

### Client execution rows — required-client

| Frozen ID | Native member |
| --- | --- |
| `API-76c9615b454e3d35` | `Execution#<type>` |
| `API-5f6fe5a9b2dc6869` | `Execution#execute(Ljava/lang/String;)Lorg/apache/geode/cache/execute/ResultCollector;` |
| `API-95a37985debdd6f3` | `Execution#execute(Ljava/lang/String;JLjava/util/concurrent/TimeUnit;)Lorg/apache/geode/cache/execute/ResultCollector;` |
| `API-0e1489ada6ff8e20` | `Execution#execute(Lorg/apache/geode/cache/execute/Function;)Lorg/apache/geode/cache/execute/ResultCollector;` |
| `API-619bec5aec13c56e` | `Execution#execute(Lorg/apache/geode/cache/execute/Function;JLjava/util/concurrent/TimeUnit;)Lorg/apache/geode/cache/execute/ResultCollector;` |
| `API-4cd4f11eaf338e41` | `Execution#setArguments(Ljava/lang/Object;)Lorg/apache/geode/cache/execute/Execution;` |
| `API-02e2d75cb85d2cd7` | `Execution#withArgs(Ljava/lang/Object;)Lorg/apache/geode/cache/execute/Execution;` |
| `API-271bf3201411e91a` | `Execution#withCollector(Lorg/apache/geode/cache/execute/ResultCollector;)Lorg/apache/geode/cache/execute/Execution;` |
| `API-fa6a2c5671b92a30` | `Execution#withFilter(Ljava/util/Set;)Lorg/apache/geode/cache/execute/Execution;` |

### Client submission metadata rows — required-client

| Frozen ID | Native member |
| --- | --- |
| `API-095022b3f73578a3` | `Function#<type>` |
| `API-a83b1ba8342cbbb0` | `Function#getId()Ljava/lang/Comparable;` |
| `API-6a10b95b6679010a` | `Function#getId()Ljava/lang/String;` |
| `API-452a71383981d97c` | `Function#hasResult()Z` |
| `API-9334fc194da25721` | `Function#isHA()Z` |
| `API-75d9ae1630ba0aba` | `Function#optimizeForWrite()Z` |

### Client failures rows — required-client

| Frozen ID | Native member |
| --- | --- |
| `API-10638e1cb18bb167` | `FunctionException#<init>()V` |
| `API-e8b188a97a074c95` | `FunctionException#<init>(Ljava/lang/String;)V` |
| `API-4e587337e5503fe1` | `FunctionException#<init>(Ljava/lang/String;Ljava/lang/Throwable;)V` |
| `API-c0369bbab705882f` | `FunctionException#<init>(Ljava/lang/Throwable;)V` |
| `API-3404ce5f6163344e` | `FunctionException#<type>` |
| `API-85ae22fe72f538ad` | `FunctionException#addException(Ljava/lang/Throwable;)V` |
| `API-ff3966e8edd010b5` | `FunctionException#addExceptions(Ljava/util/Collection;)V` |
| `API-042090fa213f82dc` | `FunctionException#getExceptions()Ljava/util/List;` |
| `API-a1e598870915a0e0` | `FunctionException#getMessage()Ljava/lang/String;` |
| `API-fe75f6d8b01a42d6` | `FunctionException#getRootCause()Ljava/lang/Throwable;` |

### Client targets rows — required-client

| Frozen ID | Native member |
| --- | --- |
| `API-2383913d686058dc` | `FunctionService#<type>` |
| `API-d6df4e75148238b0` | `FunctionService#onRegion(Lorg/apache/geode/cache/Region;)Lorg/apache/geode/cache/execute/Execution;` |
| `API-014fbb8a4a98b5d1` | `FunctionService#onServer(Lorg/apache/geode/cache/RegionService;)Lorg/apache/geode/cache/execute/Execution;` |
| `API-be5d66886fba7548` | `FunctionService#onServer(Lorg/apache/geode/cache/client/Pool;)Lorg/apache/geode/cache/execute/Execution;` |
| `API-17d28953ee1654d7` | `FunctionService#onServers(Lorg/apache/geode/cache/RegionService;)Lorg/apache/geode/cache/execute/Execution;` |
| `API-1f2689cdf8ce4a41` | `FunctionService#onServers(Lorg/apache/geode/cache/client/Pool;)Lorg/apache/geode/cache/execute/Execution;` |

### Client collectors rows — required-client

| Frozen ID | Native member |
| --- | --- |
| `API-5c2a123d36e8d337` | `ResultCollector#<type>` |
| `API-832377418c76ec45` | `ResultCollector#addResult(Lorg/apache/geode/distributed/DistributedMember;Ljava/lang/Object;)V` |
| `API-34999528c518d0e5` | `ResultCollector#clearResults()V` |
| `API-f55df3e524b65cd5` | `ResultCollector#endResults()V` |
| `API-0f4587d36f06101a` | `ResultCollector#getResult()Ljava/lang/Object;` |
| `API-5f20f0ac436c3e68` | `ResultCollector#getResult(JLjava/util/concurrent/TimeUnit;)Ljava/lang/Object;` |

### Peer targets rows — excluded-server

| Frozen ID | Native member |
| --- | --- |
| `API-26af807f9c4bc441` | `FunctionService#onMember(Lorg/apache/geode/distributed/DistributedMember;)Lorg/apache/geode/cache/execute/Execution;` |
| `API-ebeaa12060fee7ba` | `FunctionService#onMember([Ljava/lang/String;)Lorg/apache/geode/cache/execute/Execution;` |
| `API-5b913eec21e43909` | `FunctionService#onMembers(Ljava/util/Set;)Lorg/apache/geode/cache/execute/Execution;` |
| `API-e2677defba29d5a2` | `FunctionService#onMembers([Ljava/lang/String;)Lorg/apache/geode/cache/execute/Execution;` |

### Client-local registry rows — required-client

| Frozen ID | Native member |
| --- | --- |
| `API-05299a0c8ed5652d` | `FunctionService#registerFunction(Lorg/apache/geode/cache/execute/Function;)V` |
| `API-86bbb5f69618e01e` | `FunctionService#getFunction(Ljava/lang/String;)Lorg/apache/geode/cache/execute/Function;` |
| `API-6e60c1088fdee40b` | `FunctionService#isRegistered(Ljava/lang/String;)Z` |
| `API-530daae11956d6f8` | `FunctionService#getRegisteredFunctions()Ljava/util/Map;` |
| `API-cdf8e9343191da45` | `FunctionService#unregisterFunction(Ljava/lang/String;)V` |

### Client-local body/context/sender rows — required-client

| Frozen ID | Native member |
| --- | --- |
| `API-55c4bca49150194a` | `Function#execute(Lorg/apache/geode/cache/execute/FunctionContext;)V` |
| `API-0d3a1c2ba7734193` | `FunctionContext#<type>` |
| `API-652e3913bcdf25b6` | `FunctionContext#getArguments()Ljava/lang/Object;` |
| `API-99be4ae779e5d78f` | `FunctionContext#getCache()Lorg/apache/geode/cache/Cache;` |
| `API-d0deca99c3236fb7` | `FunctionContext#getFunctionId()Ljava/lang/String;` |
| `API-d2d09391e3ec2905` | `FunctionContext#getMemberName()Ljava/lang/String;` |
| `API-75357c3e6db472bd` | `FunctionContext#getPrincipal()Ljava/lang/Object;` |
| `API-e95702edea420241` | `FunctionContext#getResultSender()Lorg/apache/geode/cache/execute/ResultSender;` |
| `API-087853f7eec532ff` | `FunctionContext#isPossibleDuplicate()Z` |
| `API-e72e985dbf77aa7a` | `RegionFunctionContext#<type>` |
| `API-d43b4c778a257a21` | `RegionFunctionContext#getArguments()Ljava/lang/Object;` |
| `API-ddf636b1dcf92a6f` | `RegionFunctionContext#getCache()Lorg/apache/geode/cache/Cache;` |
| `API-9bc8ce34fc8c67f6` | `RegionFunctionContext#getDataSet()Lorg/apache/geode/cache/Region;` |
| `API-2c2b97ffd6510d4e` | `RegionFunctionContext#getFilter()Ljava/util/Set;` |
| `API-fdd6768ac84e87f5` | `RegionFunctionContext#getFunctionId()Ljava/lang/String;` |
| `API-4a10dc894a01d7d4` | `RegionFunctionContext#getMemberName()Ljava/lang/String;` |
| `API-0ccca15cdb53b07a` | `RegionFunctionContext#getPrincipal()Ljava/lang/Object;` |
| `API-fe3882536f4faedf` | `RegionFunctionContext#getResultSender()Lorg/apache/geode/cache/execute/ResultSender;` |
| `API-0a8b986b16dce358` | `RegionFunctionContext#isPossibleDuplicate()Z` |
| `API-9fe4e37e2a68a921` | `ResultSender#<type>` |
| `API-92030ca5f8811bd0` | `ResultSender#lastResult(Ljava/lang/Object;)V` |
| `API-96507bc98f418ddd` | `ResultSender#sendException(Ljava/lang/Throwable;)V` |
| `API-2a992c3b0fcfd0c7` | `ResultSender#sendResult(Ljava/lang/Object;)V` |

### Client service assembly rows — required-client equivalents

| Frozen ID | Native member |
| --- | --- |
| `API-4537456a28dcace3` | `FunctionService#<init>(Lorg/apache/geode/internal/cache/execute/FunctionExecutionService;)V` |
| `API-8444ef10dc2fbe19` | `internal.cache.execute.FunctionExecutionService#<type>` (internal-boundary row) |

### Client function permission rows — required-client equivalents

| Frozen ID | Native member |
| --- | --- |
| `API-a79aa86d83b77acd` | `Function#getRequiredPermissions(Ljava/lang/String;)Ljava/util/Collection;` |
| `API-fe127e5b9e37e6f3` | `Function#getRequiredPermissions(Ljava/lang/String;Ljava/lang/Object;)Ljava/util/Collection;` |

## Remote representation follow-up

The [submission representation investigation](client-function-submission-representation.md) records the pinned transport path, a private JDK-only callback/native-envelope experiment and a deployment negative control. All 16 deployment/envelope/submission/registry/permission tests passed user-run execution, with zero failures/errors/skips confirmed in local JUnit XML; transitional boundary verification also passed in the reported command.

The [representation decision](client-function-submission-representation.md#representation-decision--2026-10-28) supersedes the historical open-representation notes above. Use serializable application declarations in private versioned envelopes, preserving independent ID invocation and local retained identity. Common remote contexts expose metadata/values and region/filter information; borrowed typed cache/dataset handles belong only to client-local contexts. Native server-data bodies stay separately deployed behind ID execution. Both Spring native region-discovery callers move to a typed driver-owned operation.

Tasks 25/32/40 now carry production packaging, consumer migration and secured per-version acceptance. The semantic decision is settled, but no public SPI implementation or coverage status changes. Scope remains 499 required-client / 629 excluded-server / 2,723 unresolved; task 64 and parent 59 remain open for the remaining families and infrastructure.
