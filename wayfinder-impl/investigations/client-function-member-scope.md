# Client function member scope

## Decision and evidence boundary

Task 64 resolves 41 of the 72 frozen `org.apache.geode.cache.execute` rows: 37 are required-client and four peer-target overloads are excluded-server. The remaining 31 stay unresolved. Scope classification does not implement an SPI or close tasks 25/32. All frozen IDs and implementation statuses remain unchanged.

Evidence is GemFire `10.4.0-build.3056.df98c835`, native source commit `df98c8357afe85853d9a4776255d4f0e7cf064cb` in the local GemFire checkout. Paths below are relative to `gemfire-core/src/main/java/org/apache/geode/`; line references describe that commit, not checkout HEAD. Read with `git show <commit>:<path>`. These are source-backed dispositions, not new runtime test results. The previously validated [object-submission audit](client-only-spring-surfaces.md#client-function-submission-and-fixture-audit--2026-10-27) and [fixture relocation](client-function-fixture-relocation.md) supply the recorded client test evidence.

## Client execution

`internal/cache/execute/AbstractExecution.java:362–406` accepts both ID and object submission, including timed forms. Object submission validates identity and the HA/result combination and preserves the object for serialization. `OnServerExecutor.java:35–108` implements client ID lookup, metadata retrieval, collector selection and result/no-result execution. `ServerRegionFunctionExecutor.java:128–149,240–259` preserves region filters, arguments and collectors; `ServerFunctionExecutor.java:187–220` rejects filters for data-independent server execution and accepts arguments and collectors. Required behavior includes this rejection, not universal filter support. `withArgs` and `setArguments` may share a typed equivalent; native names need not be copied into the SPI.

## Client submission metadata

`cache/execute/Function.java:40–121` defines identity, result, HA and write-routing metadata. `cache/client/internal/ExecuteFunctionOp.java:177–194` and `ExecuteRegionFunctionOp.java:163–166` consume these flags and serialize either the function or its ID. Both frozen `getId` return descriptors remain mapped to the same identity requirement. A required type row does not settle every member of that type: execution-body and permission hooks remain under review.

## Client failures

`cache/execute/FunctionException.java:44–119` supplies constructors and exception aggregation; inherited message and root-cause access preserve failure diagnostics (`GemFireException.java:79–80`). `cache/client/internal/ExecuteRegionFunctionOp.java:317–361,417–429` builds aggregate failures and wraps collector errors. `ExecuteFunctionOp.java:304–369` handles returned failures and collector errors. Task 25 must preserve causes and aggregate contents through typed exceptions rather than expose native exception classes.

## Client targets

`internal/cache/execute/InternalFunctionExecutionServiceImpl.java:128–154` distinguishes a client region's server proxy from peer execution. Lines 217–284 create pool, default-client-pool and authenticated-view executors, including native restrictions on direct multiuser-pool execution. The service type and five region/pool/region-service target overloads are required through injected client collaborators. This does not require a public static service or its protected native constructor.

## Client collectors

`cache/execute/ResultCollector.java:65–123` defines result retrieval, timed interruption, member-tagged results, completion and clearing for retries. `cache/client/internal/ExecuteFunctionOp.java:233–235,304–344` clears retries and delivers member/result pairs; `internal/cache/execute/ServerFunctionExecutor.java:122–144` signals completion. All six rows require typed equivalents, including member metadata without native `DistributedMember`. Preserve no-result behavior and timeout/interruption handling without strengthening ordering or retry guarantees.

## Peer targets

The four `onMember`/`onMembers` overloads route to distributed-system member/group execution (`internal/cache/execute/InternalFunctionExecutionServiceImpl.java:94–110,291–358`). They are excluded by the [approved client boundary](../client-only-scope.md). Client pool/server-group routing remains a separate requirement; it must not be implemented by restoring peer targets.

## Remaining review and implementation owners

The remaining 31 rows comprise three `Function` body/permission members, eight `FunctionContext` rows, ten `RegionFunctionContext` rows, four `ResultSender` rows and six `FunctionService` rows (five registry operations and the protected constructor). Keep these unresolved until member-level mapping and submission design are recorded. Server fixture relocation alone does not prove that every callback type can disappear from a typed submission contract.

In particular, `internal/cache/execute/OnServerExecutor.java:35–71` checks the local registry before fetching and caching server metadata for ID submission. `InternalFunctionExecutionServiceImpl.java:158–211` implements local registration and lookup; it is not a remote function-existence query. Task 64 must decide the equivalent client metadata behavior before excluding these rows. Task 25 then owns the typed implementation and per-driver compatibility tests; task 32 owns Spring cutover and native-free orchestration tests. Both remain blocked by parent 59. Retain real-server exception/PDX/result/multi-server coverage and explicit mock rejection of remote execution.

The Spring cutover must also repair the three function-object/filter defaults identified in task 63, replace message-based no-result inference with typed behavior, preserve timeout/interruption translation and keep region-discovery execution behind the driver boundary.

### Registry compatibility probes — validated

`NativeClientFunctionRegistryTests` adds five native integration cases for client region, pool one/all-server and client-cache one/all-server targets. Each case starts a separate server process and exercises the five registry operations without exposing them through the SPI. A client registration must remain local: ID invocation fails before the server has a body. A submitted setup function then registers a different instance on the server. ID execution must read the locally registered result/HA/write metadata and execute the server instance; object execution must preserve the submitted instance's state. Unregistering locally must clear lookup, registration status and enumeration while allowing ID execution through remote metadata retrieval. The test cleans its local registration in `finally`; server registrations end with the private process.

The source basis is `internal/cache/execute/OnServerExecutor.java:35–82` and `InternalFunctionExecutionServiceImpl.java:158–211`, plus the function-versus-ID message branches in `cache/client/internal/ExecuteFunctionOp.java:177–185` and `ExecuteRegionFunctionOp.java:163–166`. These probes distinguish client metadata behavior from server installation. They do not establish a public registry design, multi-server fan-out, authenticated metadata isolation, replacement semantics, or coverage for execution-context/permission members.

The first user-run validation compiled and ran all eight tests: the region registry case and all three existing object-submission cases passed; four server-target registry cases failed at the missing-function exception assertion. The fixture incorrectly expected `FunctionException` for every target. The server-target reports show `ServerOperationException` identifying the unregistered function. Pinned `cache/client/internal/ExecuteFunctionOp.java:372–380` throws that type for an `EXECUTE_FUNCTION_ERROR` response; `internal/cache/execute/ServerRegionFunctionExecutor.java:165–177` preserves or wraps failures as `FunctionException`. The assertion now selects the expected type per target and checks the function ID in the message. Tasks 25/32 must preserve typed failure handling for both native paths rather than assume all function failures arrive as native `FunctionException`.

The user-run corrected command below passed in 25s on 2026-10-28. Local JUnit XML confirms all five registry tests and all three object-submission tests passed, with zero failures, errors or skips. The four server-target cases now exercise their full registration, metadata, ID/object state and unregister assertions. The command also passed transitional migration boundary verification; this does not establish native-free compliance. Keep all 31 remaining rows unresolved and tasks 64/59 open. Next settle typed metadata ownership/lifetime and submission-body representation before changing their scope. Do not recreate a process-wide static registry in the SPI to mirror the native implementation.

```sh
./gradlew --offline -DgemfireVersion=10.4.0-build.3056.df98c835 \
  :spring-data-gemfire-driver-10.4:integrationTest \
  --tests '*NativeClientFunctionRegistryTests' \
  --tests '*NativeClientFunctionSubmissionTests' \
  verifyMigrationBoundary
```

## Exact frozen row dispositions

The tables below enumerate the reviewed rows; overload identity uses the frozen JVM descriptor. Implementation status remains unchanged: 40 rows are `missing`; the `FunctionException` type row is `partial`.

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
