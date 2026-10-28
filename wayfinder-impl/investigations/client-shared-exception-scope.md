# Client shared exception scope — 2026-10-28

**Task 76: 390 assigned / 390 classified / zero unresolved: 341 required-client and 49 excluded-server. Validation and generated inventory reconciliation pass; task 76 is resolved.**

Evidence is pinned to GemFire **10.4.0-build.3056.df98c835**, source commit `df98c8357afe85853d9a4776255d4f0e7cf064cb` in `/Users/uk016635/projects/gemfire`, inspected with git show/git grep. Native paths below are relative to `gemfire-core/src/main/java/org/apache/geode/`, with `.java` suffixes (security/JSON support uses its native source module). Exact IDs preserve constructors, constants, overloads and inherited/protected contracts. No new ambiguity probe is required: the source paths settle scope. The client creation regression test checks behavior affected by category retirement.

## client-common-failures

**77 IDs — required-client.** Sources: the named exception declarations; `cache/client/internal/{OpExecutorImpl,AbstractOp,ClientPartitionAdvisor}`, `internal/cache/{LocalRegion,GemFireCacheImpl}`, `internal/InternalDataSerializer`, and `distributed/internal/DistributionConfigImpl`. OpExecutorImpl emits GemFireIOException for client transmission/message-size failures; ClientPartitionAdvisor wraps resolver construction failures in InternalGemFireException. Client startup validates configuration, local region operations can time out or exhaust resources, and cache closure/cancellation reaches the client. Shared base classes remain required through these concrete client paths, not merely because a constructor can be called.

Retain constructor/message/cause and inherited getRootCause/getMessage/toString behavior through native-free diagnostics, preserving null/cause distinctions while sanitizing public messages. CancelException.serialVersionUID concerns native serialization compatibility; it is not a reason to clone a native wire constant into the SPI. GemFireCacheException.getCacheException designates the cache failure and may be unwrapped; arbitrary nested causes must not override the outer failure category. Existing representative mappings and cyclic-cause protection do not prove every constructor, emitter or wrapper. Task 36 owns remaining shared translation, native wrapper recognition, safe diagnostics and consumer retirement, coordinated with each subsystem and 58 for values. Preserve Error propagation rather than catching all Throwable.

## client-cache-creation-failures

**12 IDs — required-client.** Sources: `cache/client/ClientCacheFactory.basicCreate`, `internal/cache/{InternalCacheBuilder,GemFireCacheImpl}`, `internal/cache/xmlcache/{CacheXmlParser,ClientCacheCreation}`, and both exception declarations. Closed-cache checks and client XML parsing are supported client paths. Keep XML parse failures separate from peer cache creation or JNDI factory internals. Complete safe cause/message construction, emission and Spring creation/configuration adoption in 29/35. Client XML remains supported; this audit does not remove its parser.

## client-region-and-callback-failures

**50 IDs — required-client.** Sources: the named declarations; `internal/cache/{LocalRegion,NonTXEntry,TXEntry}`, `DataSerializer.readRegion`, and `cache/client/internal/AbstractOp`. The admin package does not exclude RegionNotFoundException: DataSerializer.readRegion throws it when a referenced region cannot be found. Entry/region operations and loader/writer callbacks are client paths, including local regions. EntryExistsException exposes mutable oldValue; RegionDestroyedException/admin.RegionNotFoundException expose the full path; RegionExistsException exposes the region. Keep payloads outside sanitized messages and convert native values/regions without leaking native carriers (31 with 58). Existing path/old-value mappings are not proof of all construction or nested value adaptation.

Loader/writer wrappers may carry an already typed SPI failure; preserve its identity and unsupported-feature behavior, with the existing callback tests retained. Task 30 owns remaining callback adoption. Region operations/payloads remain with 31. Generic root-cause unwrapping must not turn an unrelated outer error into a duplicate-entry or callback failure.

## client-transaction-failures

**27 IDs — required-client.** Sources: the named declarations; `internal/cache/{LocalRegion,TXState,TXCommitMessage}`, `cache/client/internal/{CommitOp,TXSynchronizationOp}`, and `internal/cache/tier/sockets/command/{CommitCommand,TXSynchronizationCommand}`. LocalRegion JTA enlistment and TXState before-completion produce FailedSynchronizationException/SynchronizationCommitConflictException. Client commit/synchronization operations receive server outcomes and throw decoded failures. Commit conflicts/incomplete outcomes and the shared transaction base remain required. Preserve only the documented transaction-writer veto wrapper inspection, including already typed failures; arbitrary nested causes remain diagnostics. Task 34 owns the remaining construction/emission/native/mock compatibility and Spring transaction adoption after resolved foundation 28. Existing category tests leave non-executed contracts partial/missing.

## client-disk-failures

**13 IDs — required-client.** Sources: `cache/DiskAccessException` and client-local persistence paths in `internal/cache/{LocalRegion,DiskRegion,DiskStoreImpl}`. Constructors taking Region, DiskStore or a string name prefix diagnostic context; the transient remote flag starts false and readObject marks a deserialized failure remote. Both local client disk operations and received failures are relevant. Retain safe typed context, cause and remote-origin behavior without exposing native Region/DiskStore. Task 27/30 owns construction, conversion, emission and compatibility. A generic resource category does not implement getMessage, isRemote or serialization behavior.

## client-pool-failures

**12 IDs — required-client.** Sources: the named declarations; `cache/client/internal/{ConnectionFactoryImpl,OpExecutorImpl,AbstractOp}`, `internal/cache/tier/sockets/{Handshake,ClientSideHandshakeImpl}`. Pool capacity exhaustion and connection establishment/refusal are client failures. AbstractOp.processAck/processObjResponse/processChunkedResponse decodes server Throwable payloads and wraps applicable failures in ServerOperationException (a ServerConnectivityException subtype). Preserve outer connection/operation semantics and designated payloads; do not recursively guess a category from any root cause. A version-refused handshake sends text and is surfaced by Handshake.readMessage as ServerRefusedConnectionException, another connectivity subtype. Tasks 52/35 own pool lifecycle/emission/adoption; 36 owns deliberate wrapper translation. Category mapping alone does not implement all server outcomes.

## client-query-and-cq-failures

**91 IDs — required-client.** Sources: all named `cache/query` declarations; `cache/query/internal/{DefaultQueryService,DefaultQuery,QueryMonitor}`, `cache/query/cq/internal/{ClientCQImpl,CqServiceImpl}`, `internal/cache/LocalRegion`, and `cache/client/internal/QueryOp`. Client-local query/index services, index maintenance on local writes, query validation/execution/timeouts, and client CQ lifecycle give concrete client paths. QueryOp also receives server query failures. Do not exclude index failures because server indexes also exist. Query TypeMismatchException remains required independently of the excluded member-configuration InvalidValueException.

MultiIndexCreationException retains the supplied HashMap alias; getExceptionsMap exposes that map and getFailedIndexNames its live keySet. Message/toString combine failed names, exception and cause detail. Task 21/31 must define typed nested-failure conversion, collection mutation/readback and sanitized diagnostics; a single INDEX_FAILURE kind is not aggregate payload coverage. CQ closure/existence/operation errors stay with 22/33. Constructor, checked/unchecked distinction, inherited diagnostics and causes remain inventoried; task 58 coordinates nested values.

## client-security-failures

**32 IDs — required-client.** Sources: all four declarations; `cache/client/internal/ConnectionFactoryImpl`, `internal/cache/tier/sockets/{ClientSideHandshakeImpl,Handshake}`, and security exception handling in `cache/client/internal/AbstractOp`. Authentication creation/renewal and rejected operations reach clients. GemFireSecurityException keeps a separate cause field and message fallback; getCause guards self-reference. Its protected isSerializable accepts null or Serializable, and getResolvedObj inspects NamingException. Serialization temporarily clears a nonserializable naming resolved object, then restores it in finally. NotAuthorizedException has nullable principal, synchronized getter and analogous temporary stripping/restoration of nonserializable principal/resolved objects.

These inherited protected methods are retained as serialization/construction obligations, not proposed public SPI helper clones. Task 24/35 must specify native-free principal/cause exposure, null and serialization behavior, credential-safe diagnostics and restoration even on serialization failure. Coordinate 58 for value adaptation. Current SECURITY/AUTHENTICATION/NOT_AUTHORIZED categories do not cover principal payload or constructor/serialization behavior.

## client-copy-and-json-failures

**10 IDs — required-client.** Sources: `CopyException`, `CopyHelper.copy/deepCopy`, `json/JsonParseException`, `pdx/JSONFormatter` and its JSON parser calls. Client copy-on-read/copy helpers can fail during cloning or serialization; JSON conversion can fail parsing client-supplied documents. Preserve construction, null/cause and checked/unchecked translation semantics without exposing native payload carriers or echoing private document contents. Tasks 55/31 and 56/31 own emission/native/mock compatibility and adoption, coordinating 58. Generic SYSTEM_FAILURE mapping is only a partial foundation.

## client-runtime-and-received-peer-failures

**17 IDs — required-client.** Sources: the named declarations; `internal/cache/{LocalRegion,NonTXEntry,TXEntry,DistributedRegion}`, `distributed/internal/locks/{DLockService,DLockToken}`, `internal/cache/tier/sockets/command/{Put70,Get70,Destroy70}`, `internal/cache/tier/sockets/BaseCommand` and `cache/client/internal/AbstractOp`. Local client region/entry statistics can throw StatisticsDisabledException. DistributedRegion role access/distribution checks throw RoleException subtypes. Its virtualPut finally unlock path can throw LeaseExpiredException through DLockService.unlock and DLockToken.throwIfCurrentThreadHadExpiredLease. Server region commands catch operation exceptions and write them to the client; AbstractOp decodes the Throwable and wraps it where appropriate.

Thus role/lease failures remain client receive/translation obligations even though peer role-reliability configuration and hosted global-lock APIs are excluded. Task 23/36 owns the typed receive/diagnostic contract and supported wrapper behavior, not new peer-hosting surfaces. The current direct LeaseExpiredException classification does not prove correct translation of a ServerOperationException carrying it; preserve the outer failure unless an explicitly designed wrapper rule applies.

## excluded-peer-startup-and-admin-failures

**28 IDs — excluded-server.** Sources: all named declarations; `distributed/internal/{ClusterDistributionManager,InternalDistributedSystem}`, `internal/ManagerInfo`, `internal/net/{SCAdvancedSocketCreator,AdvancedSocketCreatorImpl}`, and `internal/cache/tier/sockets/ClientSideHandshakeImpl`. IncompatibleSystemException is emitted during peer join. NoSystemException's Uncreated/UnstartedSystemException subtypes concern ManagerInfo locator files; SystemIsRunningException concerns a hosted locator. RuntimeAdminException is an administration base (including OperationCancelledException), with no supported client throw/receive path. SystemConnectException is used for peer join, startInitLocator interruption and the listening-socket createServerSocketUsingPortRange/noFreePortException path; it is not the outbound client connection failure.

GatewayConfigurationException derives from GatewayException; its ClientSideHandshakeImpl emission is guarded by communicationMode.isWAN() and rejects equal distributed-system IDs. A shared source class does not make that WAN branch a client path. Remove explicit Spring/SystemConnect native mapper mappings; clear obsolete public coverage mappings for all excluded rows. Generic common-ancestor fallback is not implementation of excluded contracts. Retain client pool refusal/connectivity failures separately.

## excluded-peer-cache-exists-failure

**7 IDs — excluded-server.** Sources: `cache/CacheExistsException`, `internal/cache/{GemFireCacheImpl,InternalCacheBuilder,CacheFactoryStatics}`, and `cache/client/ClientCacheFactory.basicCreate`. InternalCacheBuilder defaults isExistingOk to true; the native CacheExists throw path requires false. Peer creation explicitly opts out of reuse. ClientCacheFactory uses the default, returns a compatible client cache, validates pool/cache configuration and rejects a peer cache with IllegalStateException; it does not opt into the peer CacheExists path. CacheExistsException.getCache exposes the peer Cache contract, not a required client cache payload.

Remove DriverFailureKind.CACHE_EXISTS and its SPI/native/Spring translation branch. Keep REGION_EXISTS and client INVALID_ARGUMENT/INVALID_STATE. The focused existing mapper suite now checks real compatible client cache reuse and client-only locator-configuration rejection. The first user-run probe confirmed that nonempty distributed-system locators throw IllegalStateException from ClientCacheFactory.basicCreate, consistent with the pinned source; the test expectation was corrected to INVALID_STATE. User-run validation of the corrected test passed on 2026-10-29. Task 29/35 retains client creation/reuse/configuration behavior.

## excluded-member-config-mutation-failures

**7 IDs — excluded-server.** Sources: both declarations; `distributed/internal/{AbstractDistributionConfig,DistributionConfigImpl,RuntimeDistributionConfigImpl}`, `internal/AbstractConfig`, `internal/cache/CacheServerImpl` and `internal/operationstatus/SubComponentStatusImpl`. AbstractDistributionConfig.setAttributeObject throws InvalidValueException for the wrong Java value type; checkAttribute throws UnmodifiableException for disallowed mutation. The other InvalidValueException producers concern hosted cache-server load polling or internal server status.

Client Properties initialization in DistributionConfigImpl.initialize temporarily permits modification, ignores non-String values and passes Strings through AbstractConfig.setAttribute. That method resolves the expected attribute type and converts to exactly that type before setAttributeObject. Bad conversions raise IllegalArgumentException; unsupported type conversion raises InternalGemFireException. Validation/API property initialization also controls the modifiable flag. Supported typed runtime setters use the allowed RuntimeDistributionConfigImpl subset; no client public receiver exposes arbitrary member configuration mutation.

Remove unused DriverFailureKind.TYPE_MISMATCH, its native InvalidValue mapping and legacy Spring branches; do not rename this unused category as an invented client failure. Required query TypeMismatchException and actual client key/value/property validation remain independently assigned to 21,18,23/35/36. Preserve native-free error translation for those real paths.

## excluded-version-and-datasource-internals

**7 IDs — excluded-server.** Sources: `cache/VersionException`, `internal/cache/tier/sockets/{ClientRegistrationMetadata,ServerSideHandshakeFactory,ServerConnection,Handshake}`, `internal/datasource/{DataSourceCreateException,DataSourceFactory}`, `internal/jndi/JNDIInvoker` and `internal/cache/xmlcache/CacheXmlParser`. UnsupportedVersionException is constructed during server handshake decoding. ServerConnection catches it and handleHandshakeException/refuseHandshake sends REPLY_REFUSED plus text, not the exception object. Client Handshake.readMessage reconstructs ServerRefusedConnectionException. The unused IncompatibleVersionException constructor does not establish a client contract for the VersionException API family.

DataSourceCreateException is a private JDBC/JNDI factory failure. CacheXmlParser's JNDI_BINDING path catches it with NamingException and logs the failure instead of exposing the throwable contract to client XML callers. Excluding this internal throwable does not exclude supported client JNDI configuration or CacheXmlException. Remove explicit native mapper VersionException/DataSourceCreateException and Spring VersionException mappings; retire the stale internal-boundary instruction that promised a public JNDI exception contract. Generic common-base fallback remains a diagnostic fallback only.

## Ownership and reconciliation

Every retained row keeps its existing implementation status and mappings; unfinished type rows move from resolved foundation 16 to active subsystem owners. Callback, region and transaction obligations move from resolved foundations 17/18/28 to active tasks 30/31/34; the completed foundation evidence is retained. The exact ledger below records implementation and consumer ownership. Existing partial mappings establish neither all constructor/payload contracts nor all remote wrapper behavior. No required implementation is upgraded by this audit.

All 49 exclusions lose stale public mappings (including generic ancestor mappings); native common-ancestor fallback does not promise an excluded API. CACHE_EXISTS and TYPE_MISMATCH are retired, together with explicit excluded native/Spring converter branches. Frozen native declarations remain for traceability. The existing-SPI audit describes signature references and is not an exhaustive enumeration of DriverFailureKind constants; source cleanup and focused tests supply that evidence. Tasks 59/64 retain final source/signature reconciliation and task 77 fixture cleanup.

All 3,851 frozen API IDs and all 2,723 assignment rows remain. Generated inventory records whole API scope of **2,694 required-client / 1,157 excluded-server / zero unresolved**. The fixed audit ledger is **2,195 required-client / 528 excluded-server**. Task 76 is resolved; classification completion is not SPI/native/mock implementation completion.

## Task 76 completion — 2026-10-29

All **390 assigned exception IDs are classified: 341 required-client / 49 excluded-server / zero unresolved**. All 3,851 frozen API IDs and 2,723 assignment rows are preserved; the fixed assignments total 2,195 required-client / 528 excluded-server. Required implementation statuses and mappings are unchanged. Excluded mappings are retired, including CACHE_EXISTS and TYPE_MISMATCH and explicit native/Spring branches. Required callback/region/transaction work has active owners 30/31/34; other subsystem handoffs are recorded in the exact exception ledger.

The user reported the requested Gradle command passed, including affected compilation, baseline pruning and transitional boundary verification. Local JUnit XML confirms 24 focused tests with zero failures/errors/skips: four SPI translator, six native mapper, two Spring translator and 12 client-only configuration tests. The locator rejection expectation is corrected to IllegalStateException / INVALID_STATE. No Gradle command was run by the agent.

Deterministic coverage generation/check, all 12 Python coverage tests and independent javap validation pass (372 declarations across 21 types). The validator now permits fully retired mappings only for excluded/missing rows; negative tests reject retained, unresolved, partial, implemented or stale SPI claims. Exact-ID/declaration/ownership checks pass. Generated scope is **2,694 required-client / 1,157 excluded-server / zero unresolved**, with **3,125 missing / 471 partial / 255 implemented** across all scopes. The inventory contains 508 Spring exposures, 123 production native imports and 538 source/resource references.

No current source/resource or existing-SPI row links to task 76's 49 exclusions. All 149 existing-SPI references are required-client. Source scopes are 530 required-client / three excluded-server / five unresolved: the remaining CacheServer exclusion references and five unlinked legacy-name/package/logger dispositions stay with parent 64 for final reconciliation. Classification does not establish native-free implementation compliance.

**All 12 API-family classification tasks are resolved. Next: task 77's two legacy test-JAR consumers, then final reconciliation in 64/59.** There are 36 open, 39 resolved and two superseded tasks (77 total).

## Exact ID ledger

### client-common-failures — 77 IDs

| API ID | Native receiver | Member | JVM descriptor | Owner | Consumer |
| --- | --- | --- | --- | --- | --- |
| `API-0af072071f780c6b` | `org.apache.geode.CancelException` | `<init>` | `()V` | 36 | 36 |
| `API-efed0a73a94e9304` | `org.apache.geode.CancelException` | `<init>` | `(Ljava/lang/String;)V` | 36 | 36 |
| `API-3ae485dac817f0e2` | `org.apache.geode.CancelException` | `<init>` | `(Ljava/lang/String;Ljava/lang/Throwable;)V` | 36 | 36 |
| `API-ef079e57388ac104` | `org.apache.geode.CancelException` | `<init>` | `(Ljava/lang/Throwable;)V` | 36 | 36 |
| `API-aa0a6132ee6dac70` | `org.apache.geode.CancelException` | `<type>` | `(type)` | 36 | 36 |
| `API-4e1e4bff6f7f54d5` | `org.apache.geode.CancelException` | `getMessage` | `()Ljava/lang/String;` | 36 | 36 |
| `API-f845085d1e5653f0` | `org.apache.geode.CancelException` | `getRootCause` | `()Ljava/lang/Throwable;` | 36 | 36 |
| `API-36fafcd00e337b1e` | `org.apache.geode.CancelException` | `serialVersionUID` | `J` | 36 | 36 |
| `API-fa96e2b65834e6a7` | `org.apache.geode.CancelException` | `toString` | `()Ljava/lang/String;` | 36 | 36 |
| `API-1c3a361f676f8758` | `org.apache.geode.GemFireCacheException` | `<init>` | `(Ljava/lang/String;Lorg/apache/geode/cache/CacheException;)V` | 36 | 36 |
| `API-6cad248067d428d8` | `org.apache.geode.GemFireCacheException` | `<init>` | `(Lorg/apache/geode/cache/CacheException;)V` | 36 | 36 |
| `API-90abf12669a7d9d4` | `org.apache.geode.GemFireCacheException` | `<type>` | `(type)` | 36 | 36 |
| `API-23a3c2f5fe5f9c11` | `org.apache.geode.GemFireCacheException` | `getCacheException` | `()Lorg/apache/geode/cache/CacheException;` | 36 | 36 |
| `API-56eef5beef056a12` | `org.apache.geode.GemFireCacheException` | `getRootCause` | `()Ljava/lang/Throwable;` | 36 | 36 |
| `API-516782de5f939e52` | `org.apache.geode.GemFireCheckedException` | `<init>` | `()V` | 36 | 36 |
| `API-5a5cd351223f6931` | `org.apache.geode.GemFireCheckedException` | `<init>` | `(Ljava/lang/String;)V` | 36 | 36 |
| `API-78ef98d9c141cde2` | `org.apache.geode.GemFireCheckedException` | `<init>` | `(Ljava/lang/String;Ljava/lang/Throwable;)V` | 36 | 36 |
| `API-494ea5190aa15266` | `org.apache.geode.GemFireCheckedException` | `<init>` | `(Ljava/lang/Throwable;)V` | 36 | 36 |
| `API-4ad429282c0393b9` | `org.apache.geode.GemFireCheckedException` | `<type>` | `(type)` | 36 | 36 |
| `API-9e8ce17a57419b46` | `org.apache.geode.GemFireCheckedException` | `getMessage` | `()Ljava/lang/String;` | 36 | 36 |
| `API-59d9f3a8335c7264` | `org.apache.geode.GemFireCheckedException` | `getRootCause` | `()Ljava/lang/Throwable;` | 36 | 36 |
| `API-4e6ce7cc9f292979` | `org.apache.geode.GemFireConfigException` | `<init>` | `(Ljava/lang/String;)V` | 36 | 36 |
| `API-89717ced00d89ce0` | `org.apache.geode.GemFireConfigException` | `<init>` | `(Ljava/lang/String;Ljava/lang/Throwable;)V` | 36 | 36 |
| `API-dde93e3494b2c1ac` | `org.apache.geode.GemFireConfigException` | `<type>` | `(type)` | 36 | 36 |
| `API-5f7dd71392921bf1` | `org.apache.geode.GemFireConfigException` | `getRootCause` | `()Ljava/lang/Throwable;` | 36 | 36 |
| `API-f4db8dfc7c647f1b` | `org.apache.geode.GemFireException` | `<init>` | `()V` | 36 | 36 |
| `API-b6928950f5e45147` | `org.apache.geode.GemFireException` | `<init>` | `(Ljava/lang/String;)V` | 36 | 36 |
| `API-0970bf1935b8018a` | `org.apache.geode.GemFireException` | `<init>` | `(Ljava/lang/String;Ljava/lang/Throwable;)V` | 36 | 36 |
| `API-ba3a5e86cb6fb015` | `org.apache.geode.GemFireException` | `<init>` | `(Ljava/lang/Throwable;)V` | 36 | 36 |
| `API-3114a629861c2eac` | `org.apache.geode.GemFireException` | `<type>` | `(type)` | 36 | 36 |
| `API-1b51ba1a959e9138` | `org.apache.geode.GemFireException` | `getMessage` | `()Ljava/lang/String;` | 36 | 36 |
| `API-3e6654d5562d3820` | `org.apache.geode.GemFireException` | `getRootCause` | `()Ljava/lang/Throwable;` | 36 | 36 |
| `API-0f08ae7734c54886` | `org.apache.geode.GemFireIOException` | `<init>` | `(Ljava/lang/String;)V` | 36 | 36 |
| `API-69cb2c86734cdefd` | `org.apache.geode.GemFireIOException` | `<init>` | `(Ljava/lang/String;Ljava/lang/Throwable;)V` | 36 | 36 |
| `API-f79cff23561dd267` | `org.apache.geode.GemFireIOException` | `<type>` | `(type)` | 36 | 36 |
| `API-17e9145b4c54b2b8` | `org.apache.geode.GemFireIOException` | `getRootCause` | `()Ljava/lang/Throwable;` | 36 | 36 |
| `API-9d4b9bbb93497b1c` | `org.apache.geode.InternalGemFireException` | `<init>` | `()V` | 36 | 36 |
| `API-910c82103a102f2b` | `org.apache.geode.InternalGemFireException` | `<init>` | `(Ljava/lang/String;)V` | 36 | 36 |
| `API-63a60dbe1d96d257` | `org.apache.geode.InternalGemFireException` | `<init>` | `(Ljava/lang/String;Ljava/lang/Throwable;)V` | 36 | 36 |
| `API-d2b4b6d4e12c4d10` | `org.apache.geode.InternalGemFireException` | `<init>` | `(Ljava/lang/Throwable;)V` | 36 | 36 |
| `API-3b89ee228faa799e` | `org.apache.geode.InternalGemFireException` | `<type>` | `(type)` | 36 | 36 |
| `API-93219289f5c9ea0d` | `org.apache.geode.InternalGemFireException` | `getRootCause` | `()Ljava/lang/Throwable;` | 36 | 36 |
| `API-c83f41fc8fb2202f` | `org.apache.geode.cache.CacheException` | `<init>` | `()V` | 36 | 36 |
| `API-a8721fc0f429cd26` | `org.apache.geode.cache.CacheException` | `<init>` | `(Ljava/lang/String;)V` | 36 | 36 |
| `API-fa2b90f5b2902ed7` | `org.apache.geode.cache.CacheException` | `<init>` | `(Ljava/lang/String;Ljava/lang/Throwable;)V` | 36 | 36 |
| `API-43a47fc12f5a81b9` | `org.apache.geode.cache.CacheException` | `<init>` | `(Ljava/lang/Throwable;)V` | 36 | 36 |
| `API-7f5eb95f79908f01` | `org.apache.geode.cache.CacheException` | `<type>` | `(type)` | 36 | 36 |
| `API-7e98677bdd9dd375` | `org.apache.geode.cache.CacheException` | `getRootCause` | `()Ljava/lang/Throwable;` | 36 | 36 |
| `API-a6931ceddc695b5e` | `org.apache.geode.cache.CacheException` | `toString` | `()Ljava/lang/String;` | 36 | 36 |
| `API-ddf873bb55dcd0c9` | `org.apache.geode.cache.CacheRuntimeException` | `<init>` | `()V` | 36 | 36 |
| `API-722a484a59d271ac` | `org.apache.geode.cache.CacheRuntimeException` | `<init>` | `(Ljava/lang/String;)V` | 36 | 36 |
| `API-a3375efe39dd3cdb` | `org.apache.geode.cache.CacheRuntimeException` | `<init>` | `(Ljava/lang/String;Ljava/lang/Throwable;)V` | 36 | 36 |
| `API-b4757b59b54db471` | `org.apache.geode.cache.CacheRuntimeException` | `<init>` | `(Ljava/lang/Throwable;)V` | 36 | 36 |
| `API-1b5585f8501bfcfc` | `org.apache.geode.cache.CacheRuntimeException` | `<type>` | `(type)` | 36 | 36 |
| `API-1739e540c6d046f0` | `org.apache.geode.cache.CacheRuntimeException` | `getRootCause` | `()Ljava/lang/Throwable;` | 36 | 36 |
| `API-35ef23a75c94c0c8` | `org.apache.geode.cache.CacheRuntimeException` | `toString` | `()Ljava/lang/String;` | 36 | 36 |
| `API-ab357eb5a89eb631` | `org.apache.geode.cache.OperationAbortedException` | `<init>` | `()V` | 36 | 36 |
| `API-ccb6ba8c75f03546` | `org.apache.geode.cache.OperationAbortedException` | `<init>` | `(Ljava/lang/String;)V` | 36 | 36 |
| `API-169b9bb67204870f` | `org.apache.geode.cache.OperationAbortedException` | `<init>` | `(Ljava/lang/String;Ljava/lang/Throwable;)V` | 36 | 36 |
| `API-5fa774147f473168` | `org.apache.geode.cache.OperationAbortedException` | `<init>` | `(Ljava/lang/Throwable;)V` | 36 | 36 |
| `API-4ae8ec03d2c284d3` | `org.apache.geode.cache.OperationAbortedException` | `<type>` | `(type)` | 36 | 36 |
| `API-22bbef11fb437354` | `org.apache.geode.cache.OperationAbortedException` | `getRootCause` | `()Ljava/lang/Throwable;` | 36 | 36 |
| `API-b9e5391606816d08` | `org.apache.geode.cache.OperationAbortedException` | `toString` | `()Ljava/lang/String;` | 36 | 36 |
| `API-20f3b5a60e8ab16f` | `org.apache.geode.cache.ResourceException` | `<init>` | `()V` | 36 | 36 |
| `API-8c7011177defba34` | `org.apache.geode.cache.ResourceException` | `<init>` | `(Ljava/lang/String;)V` | 36 | 36 |
| `API-ddfc7036a1df96b2` | `org.apache.geode.cache.ResourceException` | `<init>` | `(Ljava/lang/String;Ljava/lang/Throwable;)V` | 36 | 36 |
| `API-d61a75794b5361de` | `org.apache.geode.cache.ResourceException` | `<init>` | `(Ljava/lang/Throwable;)V` | 36 | 36 |
| `API-ed2cdd6080e26c7d` | `org.apache.geode.cache.ResourceException` | `<type>` | `(type)` | 36 | 36 |
| `API-acf34afbac7f546b` | `org.apache.geode.cache.ResourceException` | `getRootCause` | `()Ljava/lang/Throwable;` | 36 | 36 |
| `API-1ef1f82d7a7d569d` | `org.apache.geode.cache.ResourceException` | `toString` | `()Ljava/lang/String;` | 36 | 36 |
| `API-44cc1aaa2426b7a3` | `org.apache.geode.cache.TimeoutException` | `<init>` | `()V` | 36 | 36 |
| `API-08c99ec3343e3e05` | `org.apache.geode.cache.TimeoutException` | `<init>` | `(Ljava/lang/String;)V` | 36 | 36 |
| `API-9359e8a3946dfb16` | `org.apache.geode.cache.TimeoutException` | `<init>` | `(Ljava/lang/String;Ljava/lang/Throwable;)V` | 36 | 36 |
| `API-d8cd82002147ad35` | `org.apache.geode.cache.TimeoutException` | `<init>` | `(Ljava/lang/Throwable;)V` | 36 | 36 |
| `API-56e9dfb18028f9ba` | `org.apache.geode.cache.TimeoutException` | `<type>` | `(type)` | 36 | 36 |
| `API-a8f8cb9cdc9f8393` | `org.apache.geode.cache.TimeoutException` | `getRootCause` | `()Ljava/lang/Throwable;` | 36 | 36 |
| `API-a39641978d498c82` | `org.apache.geode.cache.TimeoutException` | `toString` | `()Ljava/lang/String;` | 36 | 36 |

### client-cache-creation-failures — 12 IDs

| API ID | Native receiver | Member | JVM descriptor | Owner | Consumer |
| --- | --- | --- | --- | --- | --- |
| `API-6efa0f72f06e885a` | `org.apache.geode.cache.CacheClosedException` | `<init>` | `()V` | 29 | 35 |
| `API-07db53fafc69e24c` | `org.apache.geode.cache.CacheClosedException` | `<init>` | `(Ljava/lang/String;)V` | 29 | 35 |
| `API-ab437f80d5a2367c` | `org.apache.geode.cache.CacheClosedException` | `<init>` | `(Ljava/lang/String;Ljava/lang/Throwable;)V` | 29 | 35 |
| `API-7dad7408abea7400` | `org.apache.geode.cache.CacheClosedException` | `<init>` | `(Ljava/lang/Throwable;)V` | 29 | 35 |
| `API-1d7ccd9ee7d52182` | `org.apache.geode.cache.CacheClosedException` | `<type>` | `(type)` | 29 | 35 |
| `API-a090ef84b9fd915c` | `org.apache.geode.cache.CacheClosedException` | `getRootCause` | `()Ljava/lang/Throwable;` | 29 | 35 |
| `API-ec712dc06f3fdd9b` | `org.apache.geode.cache.CacheClosedException` | `toString` | `()Ljava/lang/String;` | 29 | 35 |
| `API-4240248a98d320ac` | `org.apache.geode.cache.CacheXmlException` | `<init>` | `(Ljava/lang/String;)V` | 29 | 35 |
| `API-373acd8dc739a24c` | `org.apache.geode.cache.CacheXmlException` | `<init>` | `(Ljava/lang/String;Ljava/lang/Throwable;)V` | 29 | 35 |
| `API-bd4d452f861e0ece` | `org.apache.geode.cache.CacheXmlException` | `<type>` | `(type)` | 29 | 35 |
| `API-69ede2b5a439dfd0` | `org.apache.geode.cache.CacheXmlException` | `getRootCause` | `()Ljava/lang/Throwable;` | 29 | 35 |
| `API-2bb7a95ba9f97b57` | `org.apache.geode.cache.CacheXmlException` | `toString` | `()Ljava/lang/String;` | 29 | 35 |

### client-region-and-callback-failures — 50 IDs

| API ID | Native receiver | Member | JVM descriptor | Owner | Consumer |
| --- | --- | --- | --- | --- | --- |
| `API-28c85e6fb818a17f` | `org.apache.geode.admin.RegionNotFoundException` | `<init>` | `(Ljava/lang/String;)V` | 31 | 31 |
| `API-c7edab223413225b` | `org.apache.geode.admin.RegionNotFoundException` | `<init>` | `(Ljava/lang/String;Ljava/lang/String;)V` | 31 | 31 |
| `API-e0ddafd873b639ae` | `org.apache.geode.admin.RegionNotFoundException` | `<type>` | `(type)` | 31 | 31 |
| `API-f81479e6e0f81d4d` | `org.apache.geode.admin.RegionNotFoundException` | `getRegionFullPath` | `()Ljava/lang/String;` | 31 | 31 |
| `API-864e9e7525ae6375` | `org.apache.geode.admin.RegionNotFoundException` | `getRootCause` | `()Ljava/lang/Throwable;` | 31 | 31 |
| `API-84a8d4d686c68845` | `org.apache.geode.admin.RegionNotFoundException` | `toString` | `()Ljava/lang/String;` | 31 | 31 |
| `API-17c0d01924cd750a` | `org.apache.geode.cache.CacheLoaderException` | `<init>` | `()V` | 30 | 30 |
| `API-c38e56ca431ed334` | `org.apache.geode.cache.CacheLoaderException` | `<init>` | `(Ljava/lang/String;)V` | 30 | 30 |
| `API-f1d4c14bc24f2610` | `org.apache.geode.cache.CacheLoaderException` | `<init>` | `(Ljava/lang/String;Ljava/lang/Throwable;)V` | 30 | 30 |
| `API-749441b84b899fa5` | `org.apache.geode.cache.CacheLoaderException` | `<init>` | `(Ljava/lang/Throwable;)V` | 30 | 30 |
| `API-938137009615cca2` | `org.apache.geode.cache.CacheLoaderException` | `<type>` | `(type)` | 30 | 30 |
| `API-ef0b31e90f498929` | `org.apache.geode.cache.CacheLoaderException` | `getRootCause` | `()Ljava/lang/Throwable;` | 30 | 30 |
| `API-1263b722f8b0971f` | `org.apache.geode.cache.CacheLoaderException` | `toString` | `()Ljava/lang/String;` | 30 | 30 |
| `API-0161ff769e85aab1` | `org.apache.geode.cache.CacheWriterException` | `<init>` | `()V` | 30 | 30 |
| `API-90057faddb83afb0` | `org.apache.geode.cache.CacheWriterException` | `<init>` | `(Ljava/lang/String;)V` | 30 | 30 |
| `API-8f6409a5fc454575` | `org.apache.geode.cache.CacheWriterException` | `<init>` | `(Ljava/lang/String;Ljava/lang/Throwable;)V` | 30 | 30 |
| `API-922feb6d35184680` | `org.apache.geode.cache.CacheWriterException` | `<init>` | `(Ljava/lang/Throwable;)V` | 30 | 30 |
| `API-2fcf999a6ba53500` | `org.apache.geode.cache.CacheWriterException` | `<type>` | `(type)` | 30 | 30 |
| `API-4580e937959e21ac` | `org.apache.geode.cache.CacheWriterException` | `getRootCause` | `()Ljava/lang/Throwable;` | 30 | 30 |
| `API-d14eda9a4ba44741` | `org.apache.geode.cache.CacheWriterException` | `toString` | `()Ljava/lang/String;` | 30 | 30 |
| `API-6ddc7f66e00c9364` | `org.apache.geode.cache.EntryDestroyedException` | `<init>` | `()V` | 31 | 31 |
| `API-89c9c9a9e74929b2` | `org.apache.geode.cache.EntryDestroyedException` | `<init>` | `(Ljava/lang/String;)V` | 31 | 31 |
| `API-9c67b4aafcb69504` | `org.apache.geode.cache.EntryDestroyedException` | `<init>` | `(Ljava/lang/String;Ljava/lang/Throwable;)V` | 31 | 31 |
| `API-7d04916186b7c903` | `org.apache.geode.cache.EntryDestroyedException` | `<init>` | `(Ljava/lang/Throwable;)V` | 31 | 31 |
| `API-274783726b4440a4` | `org.apache.geode.cache.EntryDestroyedException` | `<type>` | `(type)` | 31 | 31 |
| `API-e8d65451839ac3db` | `org.apache.geode.cache.EntryDestroyedException` | `getRootCause` | `()Ljava/lang/Throwable;` | 31 | 31 |
| `API-cfffd62bbd565b01` | `org.apache.geode.cache.EntryDestroyedException` | `toString` | `()Ljava/lang/String;` | 31 | 31 |
| `API-b53a60db0c5c323e` | `org.apache.geode.cache.EntryExistsException` | `<init>` | `(Ljava/lang/String;Ljava/lang/Object;)V` | 31 | 31 |
| `API-58b7b9d6f01117aa` | `org.apache.geode.cache.EntryExistsException` | `<type>` | `(type)` | 31 | 31 |
| `API-765c3de4e9b6bc48` | `org.apache.geode.cache.EntryExistsException` | `getOldValue` | `()Ljava/lang/Object;` | 31 | 31 |
| `API-6ec831c6a39ff094` | `org.apache.geode.cache.EntryExistsException` | `getRootCause` | `()Ljava/lang/Throwable;` | 31 | 31 |
| `API-5543dd61d107e7f6` | `org.apache.geode.cache.EntryExistsException` | `setOldValue` | `(Ljava/lang/Object;)V` | 31 | 31 |
| `API-e9260e1b7e430908` | `org.apache.geode.cache.EntryExistsException` | `toString` | `()Ljava/lang/String;` | 31 | 31 |
| `API-a3837733cce4ee02` | `org.apache.geode.cache.EntryNotFoundException` | `<init>` | `(Ljava/lang/String;)V` | 31 | 31 |
| `API-bbc76e1a0c538750` | `org.apache.geode.cache.EntryNotFoundException` | `<init>` | `(Ljava/lang/String;Ljava/lang/Throwable;)V` | 31 | 31 |
| `API-570eb6c914573532` | `org.apache.geode.cache.EntryNotFoundException` | `<type>` | `(type)` | 31 | 31 |
| `API-184bd78cf79f95c6` | `org.apache.geode.cache.EntryNotFoundException` | `getRootCause` | `()Ljava/lang/Throwable;` | 31 | 31 |
| `API-dfbc423d182e475c` | `org.apache.geode.cache.EntryNotFoundException` | `toString` | `()Ljava/lang/String;` | 31 | 31 |
| `API-c3c8da21abfee502` | `org.apache.geode.cache.RegionDestroyedException` | `<init>` | `(Ljava/lang/String;Ljava/lang/String;)V` | 31 | 31 |
| `API-d38bd7f93301111c` | `org.apache.geode.cache.RegionDestroyedException` | `<init>` | `(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)V` | 31 | 31 |
| `API-04fff424517f3085` | `org.apache.geode.cache.RegionDestroyedException` | `<type>` | `(type)` | 31 | 31 |
| `API-c78c788ddde778ba` | `org.apache.geode.cache.RegionDestroyedException` | `getRegionFullPath` | `()Ljava/lang/String;` | 31 | 31 |
| `API-219bded15a97c45e` | `org.apache.geode.cache.RegionDestroyedException` | `getRootCause` | `()Ljava/lang/Throwable;` | 31 | 31 |
| `API-341c93e785ea7d14` | `org.apache.geode.cache.RegionDestroyedException` | `toString` | `()Ljava/lang/String;` | 31 | 31 |
| `API-38fbf4278674990f` | `org.apache.geode.cache.RegionExistsException` | `<init>` | `(Lorg/apache/geode/cache/Region;)V` | 31 | 31 |
| `API-7340b043fa65042c` | `org.apache.geode.cache.RegionExistsException` | `<init>` | `(Lorg/apache/geode/cache/Region;Ljava/lang/Throwable;)V` | 31 | 31 |
| `API-04408f423be5e46a` | `org.apache.geode.cache.RegionExistsException` | `<type>` | `(type)` | 31 | 31 |
| `API-1910b1a9ec304cc3` | `org.apache.geode.cache.RegionExistsException` | `getRegion` | `()Lorg/apache/geode/cache/Region;` | 31 | 31 |
| `API-14283c4bea19bf70` | `org.apache.geode.cache.RegionExistsException` | `getRootCause` | `()Ljava/lang/Throwable;` | 31 | 31 |
| `API-44bb92ef72ab89b6` | `org.apache.geode.cache.RegionExistsException` | `toString` | `()Ljava/lang/String;` | 31 | 31 |

### client-transaction-failures — 27 IDs

| API ID | Native receiver | Member | JVM descriptor | Owner | Consumer |
| --- | --- | --- | --- | --- | --- |
| `API-a52465fb007e961b` | `org.apache.geode.cache.CommitConflictException` | `<init>` | `(Ljava/lang/String;)V` | 34 | 34 |
| `API-e67c3ec90756090b` | `org.apache.geode.cache.CommitConflictException` | `<init>` | `(Ljava/lang/String;Ljava/lang/Throwable;)V` | 34 | 34 |
| `API-4187fb5cd5ca0c56` | `org.apache.geode.cache.CommitConflictException` | `<init>` | `(Ljava/lang/Throwable;)V` | 34 | 34 |
| `API-ee46b18940a2069f` | `org.apache.geode.cache.CommitConflictException` | `<type>` | `(type)` | 34 | 34 |
| `API-9c571a404eb5d08e` | `org.apache.geode.cache.CommitConflictException` | `getRootCause` | `()Ljava/lang/Throwable;` | 34 | 34 |
| `API-63150523d1c1d5b7` | `org.apache.geode.cache.CommitConflictException` | `toString` | `()Ljava/lang/String;` | 34 | 34 |
| `API-b93eb6bec4af3966` | `org.apache.geode.cache.CommitIncompleteException` | `<init>` | `(Ljava/lang/String;)V` | 34 | 34 |
| `API-5e0075a59bdcb762` | `org.apache.geode.cache.CommitIncompleteException` | `<type>` | `(type)` | 34 | 34 |
| `API-15b400d60f6db75b` | `org.apache.geode.cache.CommitIncompleteException` | `getRootCause` | `()Ljava/lang/Throwable;` | 34 | 34 |
| `API-09c47de76a801e11` | `org.apache.geode.cache.CommitIncompleteException` | `toString` | `()Ljava/lang/String;` | 34 | 34 |
| `API-d1d58c6d2ae7771b` | `org.apache.geode.cache.FailedSynchronizationException` | `<init>` | `(Ljava/lang/String;)V` | 34 | 34 |
| `API-67ffd38b16c67023` | `org.apache.geode.cache.FailedSynchronizationException` | `<init>` | `(Ljava/lang/String;Ljava/lang/Throwable;)V` | 34 | 34 |
| `API-647d0804aaa9305f` | `org.apache.geode.cache.FailedSynchronizationException` | `<type>` | `(type)` | 34 | 34 |
| `API-e865258bb26d79c2` | `org.apache.geode.cache.FailedSynchronizationException` | `getRootCause` | `()Ljava/lang/Throwable;` | 34 | 34 |
| `API-fa8dcd38a9774e9e` | `org.apache.geode.cache.FailedSynchronizationException` | `toString` | `()Ljava/lang/String;` | 34 | 34 |
| `API-3574c57a472a2440` | `org.apache.geode.cache.SynchronizationCommitConflictException` | `<init>` | `(Ljava/lang/String;)V` | 34 | 34 |
| `API-aa86ec7a1cbd8f30` | `org.apache.geode.cache.SynchronizationCommitConflictException` | `<init>` | `(Ljava/lang/String;Ljava/lang/Throwable;)V` | 34 | 34 |
| `API-ac0aaa9433c14e10` | `org.apache.geode.cache.SynchronizationCommitConflictException` | `<type>` | `(type)` | 34 | 34 |
| `API-5c62bb841504fa52` | `org.apache.geode.cache.SynchronizationCommitConflictException` | `getRootCause` | `()Ljava/lang/Throwable;` | 34 | 34 |
| `API-845d3844e0a3d007` | `org.apache.geode.cache.SynchronizationCommitConflictException` | `toString` | `()Ljava/lang/String;` | 34 | 34 |
| `API-d0b4e5440c97e332` | `org.apache.geode.cache.TransactionException` | `<init>` | `()V` | 34 | 34 |
| `API-b83b91c0142001f8` | `org.apache.geode.cache.TransactionException` | `<init>` | `(Ljava/lang/String;)V` | 34 | 34 |
| `API-f961de35d4b6641b` | `org.apache.geode.cache.TransactionException` | `<init>` | `(Ljava/lang/String;Ljava/lang/Throwable;)V` | 34 | 34 |
| `API-6b183bba3616bd2e` | `org.apache.geode.cache.TransactionException` | `<init>` | `(Ljava/lang/Throwable;)V` | 34 | 34 |
| `API-0fcf5f922c0bbd4c` | `org.apache.geode.cache.TransactionException` | `<type>` | `(type)` | 34 | 34 |
| `API-ed554b68f1daf076` | `org.apache.geode.cache.TransactionException` | `getRootCause` | `()Ljava/lang/Throwable;` | 34 | 34 |
| `API-b2155b52fd3a4622` | `org.apache.geode.cache.TransactionException` | `toString` | `()Ljava/lang/String;` | 34 | 34 |

### client-disk-failures — 13 IDs

| API ID | Native receiver | Member | JVM descriptor | Owner | Consumer |
| --- | --- | --- | --- | --- | --- |
| `API-25ddebf4c754e846` | `org.apache.geode.cache.DiskAccessException` | `<init>` | `()V` | 27 | 30 |
| `API-695a75ec8ec0d6bd` | `org.apache.geode.cache.DiskAccessException` | `<init>` | `(Ljava/lang/String;)V` | 27 | 30 |
| `API-a8d8287f99e4315a` | `org.apache.geode.cache.DiskAccessException` | `<init>` | `(Ljava/lang/String;Ljava/lang/String;)V` | 27 | 30 |
| `API-15502a8afd821875` | `org.apache.geode.cache.DiskAccessException` | `<init>` | `(Ljava/lang/String;Ljava/lang/Throwable;)V` | 27 | 30 |
| `API-c85fc32bca57b42e` | `org.apache.geode.cache.DiskAccessException` | `<init>` | `(Ljava/lang/String;Ljava/lang/Throwable;Ljava/lang/String;)V` | 27 | 30 |
| `API-0b135d0919742d0f` | `org.apache.geode.cache.DiskAccessException` | `<init>` | `(Ljava/lang/String;Ljava/lang/Throwable;Lorg/apache/geode/cache/DiskStore;)V` | 27 | 30 |
| `API-3c20d1d3be53709c` | `org.apache.geode.cache.DiskAccessException` | `<init>` | `(Ljava/lang/String;Lorg/apache/geode/cache/DiskStore;)V` | 27 | 30 |
| `API-5a4b2f0c54f980fd` | `org.apache.geode.cache.DiskAccessException` | `<init>` | `(Ljava/lang/String;Lorg/apache/geode/cache/Region;)V` | 27 | 30 |
| `API-cb8b439b7feac3d6` | `org.apache.geode.cache.DiskAccessException` | `<init>` | `(Ljava/lang/Throwable;)V` | 27 | 30 |
| `API-78aecbf6a3a4073b` | `org.apache.geode.cache.DiskAccessException` | `<type>` | `(type)` | 27 | 30 |
| `API-8a90f07c8faab416` | `org.apache.geode.cache.DiskAccessException` | `getRootCause` | `()Ljava/lang/Throwable;` | 27 | 30 |
| `API-6f7bf74147abc8a8` | `org.apache.geode.cache.DiskAccessException` | `isRemote` | `()Z` | 27 | 30 |
| `API-f375bfcb909e0a37` | `org.apache.geode.cache.DiskAccessException` | `toString` | `()Ljava/lang/String;` | 27 | 30 |

### client-pool-failures — 12 IDs

| API ID | Native receiver | Member | JVM descriptor | Owner | Consumer |
| --- | --- | --- | --- | --- | --- |
| `API-a4a31eb2389e5854` | `org.apache.geode.cache.client.AllConnectionsInUseException` | `<init>` | `()V` | 52 | 35 |
| `API-60d714885a580458` | `org.apache.geode.cache.client.AllConnectionsInUseException` | `<init>` | `(Ljava/lang/String;)V` | 52 | 35 |
| `API-8ee1bf375f7e9063` | `org.apache.geode.cache.client.AllConnectionsInUseException` | `<init>` | `(Ljava/lang/String;Ljava/lang/Throwable;)V` | 52 | 35 |
| `API-f0d101b5d65f8453` | `org.apache.geode.cache.client.AllConnectionsInUseException` | `<init>` | `(Ljava/lang/Throwable;)V` | 52 | 35 |
| `API-f53c181c6496a1bd` | `org.apache.geode.cache.client.AllConnectionsInUseException` | `<type>` | `(type)` | 52 | 35 |
| `API-81dc04646f002c0f` | `org.apache.geode.cache.client.AllConnectionsInUseException` | `getRootCause` | `()Ljava/lang/Throwable;` | 52 | 35 |
| `API-c06d09f93edbcd3a` | `org.apache.geode.cache.client.ServerConnectivityException` | `<init>` | `()V` | 52 | 35 |
| `API-64a1899e3e8fcf1c` | `org.apache.geode.cache.client.ServerConnectivityException` | `<init>` | `(Ljava/lang/String;)V` | 52 | 35 |
| `API-7696806135b650a7` | `org.apache.geode.cache.client.ServerConnectivityException` | `<init>` | `(Ljava/lang/String;Ljava/lang/Throwable;)V` | 52 | 35 |
| `API-054a1d7237796475` | `org.apache.geode.cache.client.ServerConnectivityException` | `<init>` | `(Ljava/lang/Throwable;)V` | 52 | 35 |
| `API-8ecc3ce52c6f66c5` | `org.apache.geode.cache.client.ServerConnectivityException` | `<type>` | `(type)` | 52 | 35 |
| `API-5df7c8f3f371b6ee` | `org.apache.geode.cache.client.ServerConnectivityException` | `getRootCause` | `()Ljava/lang/Throwable;` | 52 | 35 |

### client-query-and-cq-failures — 91 IDs

| API ID | Native receiver | Member | JVM descriptor | Owner | Consumer |
| --- | --- | --- | --- | --- | --- |
| `API-ad4a15b782742577` | `org.apache.geode.cache.query.CqClosedException` | `<init>` | `(Ljava/lang/String;)V` | 22 | 33 |
| `API-ecc104067f306e47` | `org.apache.geode.cache.query.CqClosedException` | `<init>` | `(Ljava/lang/String;Ljava/lang/Throwable;)V` | 22 | 33 |
| `API-9cd1845b54a2b915` | `org.apache.geode.cache.query.CqClosedException` | `<init>` | `(Ljava/lang/Throwable;)V` | 22 | 33 |
| `API-77f9468ada6b55ab` | `org.apache.geode.cache.query.CqClosedException` | `<type>` | `(type)` | 22 | 33 |
| `API-0def48ccef9c2345` | `org.apache.geode.cache.query.CqClosedException` | `getRootCause` | `()Ljava/lang/Throwable;` | 22 | 33 |
| `API-27189dd9839be77d` | `org.apache.geode.cache.query.CqClosedException` | `toString` | `()Ljava/lang/String;` | 22 | 33 |
| `API-4dd05c01ec6da05e` | `org.apache.geode.cache.query.CqException` | `<init>` | `(Ljava/lang/String;)V` | 22 | 33 |
| `API-59d9afa97196edcc` | `org.apache.geode.cache.query.CqException` | `<init>` | `(Ljava/lang/String;Ljava/lang/Throwable;)V` | 22 | 33 |
| `API-b43fb325aef3c550` | `org.apache.geode.cache.query.CqException` | `<init>` | `(Ljava/lang/Throwable;)V` | 22 | 33 |
| `API-a597fc065c8ca620` | `org.apache.geode.cache.query.CqException` | `<type>` | `(type)` | 22 | 33 |
| `API-a04f9f810e3b914a` | `org.apache.geode.cache.query.CqException` | `getRootCause` | `()Ljava/lang/Throwable;` | 22 | 33 |
| `API-6751f32732143b9b` | `org.apache.geode.cache.query.CqExistsException` | `<init>` | `(Ljava/lang/String;)V` | 22 | 33 |
| `API-2c33e2ae5c542bea` | `org.apache.geode.cache.query.CqExistsException` | `<init>` | `(Ljava/lang/String;Ljava/lang/Throwable;)V` | 22 | 33 |
| `API-d467a940951bccc8` | `org.apache.geode.cache.query.CqExistsException` | `<init>` | `(Ljava/lang/Throwable;)V` | 22 | 33 |
| `API-8b569fb4caf5b88e` | `org.apache.geode.cache.query.CqExistsException` | `<type>` | `(type)` | 22 | 33 |
| `API-a769a1d42c5f78df` | `org.apache.geode.cache.query.CqExistsException` | `getRootCause` | `()Ljava/lang/Throwable;` | 22 | 33 |
| `API-5f8cc46f2086ae16` | `org.apache.geode.cache.query.FunctionDomainException` | `<init>` | `(Ljava/lang/String;)V` | 21 | 31 |
| `API-26c32d66c4a8481b` | `org.apache.geode.cache.query.FunctionDomainException` | `<type>` | `(type)` | 21 | 31 |
| `API-aefc3074513b00b7` | `org.apache.geode.cache.query.FunctionDomainException` | `getRootCause` | `()Ljava/lang/Throwable;` | 21 | 31 |
| `API-bd313e69b53b6eae` | `org.apache.geode.cache.query.IndexCreationException` | `<init>` | `(Ljava/lang/String;)V` | 21 | 31 |
| `API-c03b206f4f28859a` | `org.apache.geode.cache.query.IndexCreationException` | `<init>` | `(Ljava/lang/String;Ljava/lang/Throwable;)V` | 21 | 31 |
| `API-85dd4306d86f81ab` | `org.apache.geode.cache.query.IndexCreationException` | `<type>` | `(type)` | 21 | 31 |
| `API-4571cfdcf9ee54e4` | `org.apache.geode.cache.query.IndexCreationException` | `getMessage` | `()Ljava/lang/String;` | 21 | 31 |
| `API-0858a59ac5e9bb3a` | `org.apache.geode.cache.query.IndexCreationException` | `getRootCause` | `()Ljava/lang/Throwable;` | 21 | 31 |
| `API-4fcc7e9c51694a86` | `org.apache.geode.cache.query.IndexExistsException` | `<init>` | `(Ljava/lang/String;)V` | 21 | 31 |
| `API-63971a42957c60ce` | `org.apache.geode.cache.query.IndexExistsException` | `<init>` | `(Ljava/lang/String;Ljava/lang/Throwable;)V` | 21 | 31 |
| `API-72798dc078e7d752` | `org.apache.geode.cache.query.IndexExistsException` | `<type>` | `(type)` | 21 | 31 |
| `API-4641a436b8b2ae64` | `org.apache.geode.cache.query.IndexExistsException` | `getMessage` | `()Ljava/lang/String;` | 21 | 31 |
| `API-7524758633bc4cb5` | `org.apache.geode.cache.query.IndexExistsException` | `getRootCause` | `()Ljava/lang/Throwable;` | 21 | 31 |
| `API-49645f4d59af6687` | `org.apache.geode.cache.query.IndexInvalidException` | `<init>` | `(Ljava/lang/String;)V` | 21 | 31 |
| `API-26b42122afc176d7` | `org.apache.geode.cache.query.IndexInvalidException` | `<init>` | `(Ljava/lang/String;Ljava/lang/Throwable;)V` | 21 | 31 |
| `API-38c676f314aaf30d` | `org.apache.geode.cache.query.IndexInvalidException` | `<init>` | `(Ljava/lang/Throwable;)V` | 21 | 31 |
| `API-5c9a154fbce6839e` | `org.apache.geode.cache.query.IndexInvalidException` | `<type>` | `(type)` | 21 | 31 |
| `API-f65eb9ee26a401a0` | `org.apache.geode.cache.query.IndexInvalidException` | `getMessage` | `()Ljava/lang/String;` | 21 | 31 |
| `API-ee523ed5b4361385` | `org.apache.geode.cache.query.IndexInvalidException` | `getRootCause` | `()Ljava/lang/Throwable;` | 21 | 31 |
| `API-171661af149ba63a` | `org.apache.geode.cache.query.IndexMaintenanceException` | `<init>` | `()V` | 21 | 31 |
| `API-101b410f96c3c961` | `org.apache.geode.cache.query.IndexMaintenanceException` | `<init>` | `(Ljava/lang/String;)V` | 21 | 31 |
| `API-9548bfe777d8c132` | `org.apache.geode.cache.query.IndexMaintenanceException` | `<init>` | `(Ljava/lang/String;Ljava/lang/Throwable;)V` | 21 | 31 |
| `API-c2cb5e6d2d161829` | `org.apache.geode.cache.query.IndexMaintenanceException` | `<init>` | `(Ljava/lang/Throwable;)V` | 21 | 31 |
| `API-5920ac485a413c1f` | `org.apache.geode.cache.query.IndexMaintenanceException` | `<type>` | `(type)` | 21 | 31 |
| `API-ecb3c9d31513220a` | `org.apache.geode.cache.query.IndexMaintenanceException` | `getMessage` | `()Ljava/lang/String;` | 21 | 31 |
| `API-d3b6e6df3692ce47` | `org.apache.geode.cache.query.IndexMaintenanceException` | `getRootCause` | `()Ljava/lang/Throwable;` | 21 | 31 |
| `API-d717d752c25542db` | `org.apache.geode.cache.query.IndexMaintenanceException` | `toString` | `()Ljava/lang/String;` | 21 | 31 |
| `API-c8829afe8eceb299` | `org.apache.geode.cache.query.IndexNameConflictException` | `<init>` | `(Ljava/lang/String;)V` | 21 | 31 |
| `API-00cf56edfdb7354d` | `org.apache.geode.cache.query.IndexNameConflictException` | `<init>` | `(Ljava/lang/String;Ljava/lang/Throwable;)V` | 21 | 31 |
| `API-b806de65a3788bdf` | `org.apache.geode.cache.query.IndexNameConflictException` | `<type>` | `(type)` | 21 | 31 |
| `API-ba620d6f44d38b57` | `org.apache.geode.cache.query.IndexNameConflictException` | `getMessage` | `()Ljava/lang/String;` | 21 | 31 |
| `API-9cbd5b5a56e3b985` | `org.apache.geode.cache.query.IndexNameConflictException` | `getRootCause` | `()Ljava/lang/Throwable;` | 21 | 31 |
| `API-4e6b031ccec834d0` | `org.apache.geode.cache.query.MultiIndexCreationException` | `<init>` | `(Ljava/util/HashMap;)V` | 21 | 31 |
| `API-c445d3786b3366de` | `org.apache.geode.cache.query.MultiIndexCreationException` | `<type>` | `(type)` | 21 | 31 |
| `API-1e4f186cdfa53397` | `org.apache.geode.cache.query.MultiIndexCreationException` | `getExceptionsMap` | `()Ljava/util/Map;` | 21 | 31 |
| `API-93b8e1eee31a6786` | `org.apache.geode.cache.query.MultiIndexCreationException` | `getFailedIndexNames` | `()Ljava/util/Set;` | 21 | 31 |
| `API-7c85ac980861e201` | `org.apache.geode.cache.query.MultiIndexCreationException` | `getMessage` | `()Ljava/lang/String;` | 21 | 31 |
| `API-bf13de362fb13a7d` | `org.apache.geode.cache.query.MultiIndexCreationException` | `toString` | `()Ljava/lang/String;` | 21 | 31 |
| `API-39ae4d04f8074c3c` | `org.apache.geode.cache.query.NameResolutionException` | `<init>` | `(Ljava/lang/String;)V` | 21 | 31 |
| `API-483ebded6b8558de` | `org.apache.geode.cache.query.NameResolutionException` | `<init>` | `(Ljava/lang/String;Ljava/lang/Throwable;)V` | 21 | 31 |
| `API-9a3a721b95a1fee6` | `org.apache.geode.cache.query.NameResolutionException` | `<type>` | `(type)` | 21 | 31 |
| `API-3a4bfd11bec74806` | `org.apache.geode.cache.query.NameResolutionException` | `getRootCause` | `()Ljava/lang/Throwable;` | 21 | 31 |
| `API-de405c17f9c937b6` | `org.apache.geode.cache.query.QueryException` | `<init>` | `()V` | 21 | 31 |
| `API-a04e15d5b7fd8cb5` | `org.apache.geode.cache.query.QueryException` | `<init>` | `(Ljava/lang/String;)V` | 21 | 31 |
| `API-37e9b92de808890a` | `org.apache.geode.cache.query.QueryException` | `<init>` | `(Ljava/lang/String;Ljava/lang/Throwable;)V` | 21 | 31 |
| `API-e2a1b7bb488e1494` | `org.apache.geode.cache.query.QueryException` | `<init>` | `(Ljava/lang/Throwable;)V` | 21 | 31 |
| `API-48ea98d233253bf6` | `org.apache.geode.cache.query.QueryException` | `<type>` | `(type)` | 21 | 31 |
| `API-aad916b2a6ec60bc` | `org.apache.geode.cache.query.QueryException` | `getMessage` | `()Ljava/lang/String;` | 21 | 31 |
| `API-cded5460f7e3ccfb` | `org.apache.geode.cache.query.QueryException` | `getRootCause` | `()Ljava/lang/Throwable;` | 21 | 31 |
| `API-6108836c64282338` | `org.apache.geode.cache.query.QueryExecutionTimeoutException` | `<init>` | `()V` | 21 | 31 |
| `API-4def386b5d5a5e2b` | `org.apache.geode.cache.query.QueryExecutionTimeoutException` | `<init>` | `(Ljava/lang/String;)V` | 21 | 31 |
| `API-3cd8a2c6002707b4` | `org.apache.geode.cache.query.QueryExecutionTimeoutException` | `<init>` | `(Ljava/lang/String;Ljava/lang/Throwable;)V` | 21 | 31 |
| `API-5c105ccd36ace147` | `org.apache.geode.cache.query.QueryExecutionTimeoutException` | `<init>` | `(Ljava/lang/Throwable;)V` | 21 | 31 |
| `API-1938f7014c192fd2` | `org.apache.geode.cache.query.QueryExecutionTimeoutException` | `<type>` | `(type)` | 21 | 31 |
| `API-dca8fc22c3e32dbc` | `org.apache.geode.cache.query.QueryExecutionTimeoutException` | `getMessage` | `()Ljava/lang/String;` | 21 | 31 |
| `API-97d344868b1eb51c` | `org.apache.geode.cache.query.QueryExecutionTimeoutException` | `getRootCause` | `()Ljava/lang/Throwable;` | 21 | 31 |
| `API-097c6bf09bed5e80` | `org.apache.geode.cache.query.QueryExecutionTimeoutException` | `toString` | `()Ljava/lang/String;` | 21 | 31 |
| `API-204c96ee174685f3` | `org.apache.geode.cache.query.QueryInvalidException` | `<init>` | `(Ljava/lang/String;)V` | 21 | 31 |
| `API-59c54360523fb0d6` | `org.apache.geode.cache.query.QueryInvalidException` | `<init>` | `(Ljava/lang/String;Ljava/lang/Throwable;)V` | 21 | 31 |
| `API-22eb5a17cdc43c5a` | `org.apache.geode.cache.query.QueryInvalidException` | `<type>` | `(type)` | 21 | 31 |
| `API-1ec1d2322e75ffac` | `org.apache.geode.cache.query.QueryInvalidException` | `getMessage` | `()Ljava/lang/String;` | 21 | 31 |
| `API-2e8fb54866226996` | `org.apache.geode.cache.query.QueryInvalidException` | `getRootCause` | `()Ljava/lang/Throwable;` | 21 | 31 |
| `API-7b8b493bb855ca64` | `org.apache.geode.cache.query.QueryInvocationTargetException` | `<init>` | `(Ljava/lang/String;)V` | 21 | 31 |
| `API-36842e8fd79c0637` | `org.apache.geode.cache.query.QueryInvocationTargetException` | `<init>` | `(Ljava/lang/String;Ljava/lang/Throwable;)V` | 21 | 31 |
| `API-37fcb6a50ff1424c` | `org.apache.geode.cache.query.QueryInvocationTargetException` | `<init>` | `(Ljava/lang/Throwable;)V` | 21 | 31 |
| `API-550cfd9066987a3b` | `org.apache.geode.cache.query.QueryInvocationTargetException` | `<type>` | `(type)` | 21 | 31 |
| `API-e5a1574b7be77d07` | `org.apache.geode.cache.query.QueryInvocationTargetException` | `getRootCause` | `()Ljava/lang/Throwable;` | 21 | 31 |
| `API-a5ce88c4e2003ddf` | `org.apache.geode.cache.query.RegionNotFoundException` | `<init>` | `(Ljava/lang/String;)V` | 21 | 31 |
| `API-2ba6b6b6be579f59` | `org.apache.geode.cache.query.RegionNotFoundException` | `<init>` | `(Ljava/lang/String;Ljava/lang/Throwable;)V` | 21 | 31 |
| `API-40fdac0b77c2ecdd` | `org.apache.geode.cache.query.RegionNotFoundException` | `<type>` | `(type)` | 21 | 31 |
| `API-cee57e5e2fa66536` | `org.apache.geode.cache.query.RegionNotFoundException` | `getRootCause` | `()Ljava/lang/Throwable;` | 21 | 31 |
| `API-1ebd79c467d2350d` | `org.apache.geode.cache.query.TypeMismatchException` | `<init>` | `(Ljava/lang/String;)V` | 21 | 31 |
| `API-9834ebc269a084d2` | `org.apache.geode.cache.query.TypeMismatchException` | `<init>` | `(Ljava/lang/String;Ljava/lang/Throwable;)V` | 21 | 31 |
| `API-178894c9e61d826a` | `org.apache.geode.cache.query.TypeMismatchException` | `<type>` | `(type)` | 21 | 31 |
| `API-68fabb8c250974f7` | `org.apache.geode.cache.query.TypeMismatchException` | `getRootCause` | `()Ljava/lang/Throwable;` | 21 | 31 |

### client-security-failures — 32 IDs

| API ID | Native receiver | Member | JVM descriptor | Owner | Consumer |
| --- | --- | --- | --- | --- | --- |
| `API-2a7d5bdf863f62ad` | `org.apache.geode.security.AuthenticationExpiredException` | `<init>` | `(Ljava/lang/String;)V` | 24 | 35 |
| `API-6cd48c2e2b4197eb` | `org.apache.geode.security.AuthenticationExpiredException` | `<init>` | `(Ljava/lang/String;Ljava/lang/Throwable;)V` | 24 | 35 |
| `API-cee830cbced754cb` | `org.apache.geode.security.AuthenticationExpiredException` | `<type>` | `(type)` | 24 | 35 |
| `API-093b1d71cf0a10de` | `org.apache.geode.security.AuthenticationExpiredException` | `getCause` | `()Ljava/lang/Throwable;` | 24 | 35 |
| `API-322b31b99cfbffb7` | `org.apache.geode.security.AuthenticationExpiredException` | `getResolvedObj` | `()Ljava/lang/Object;` | 24 | 35 |
| `API-acfbb4a7ac1cc0a3` | `org.apache.geode.security.AuthenticationExpiredException` | `getRootCause` | `()Ljava/lang/Throwable;` | 24 | 35 |
| `API-81bb478200b226ff` | `org.apache.geode.security.AuthenticationExpiredException` | `isSerializable` | `(Ljava/lang/Object;)Z` | 24 | 35 |
| `API-12b63bcf002c3ba5` | `org.apache.geode.security.AuthenticationFailedException` | `<init>` | `(Ljava/lang/String;)V` | 24 | 35 |
| `API-c56b2badc89ef6f9` | `org.apache.geode.security.AuthenticationFailedException` | `<init>` | `(Ljava/lang/String;Ljava/lang/Throwable;)V` | 24 | 35 |
| `API-b1d880a7ff7662eb` | `org.apache.geode.security.AuthenticationFailedException` | `<type>` | `(type)` | 24 | 35 |
| `API-2c6706cbcf81e42a` | `org.apache.geode.security.AuthenticationFailedException` | `getCause` | `()Ljava/lang/Throwable;` | 24 | 35 |
| `API-2eb871c9b13a2f80` | `org.apache.geode.security.AuthenticationFailedException` | `getResolvedObj` | `()Ljava/lang/Object;` | 24 | 35 |
| `API-61dcc87810141fd7` | `org.apache.geode.security.AuthenticationFailedException` | `getRootCause` | `()Ljava/lang/Throwable;` | 24 | 35 |
| `API-d191b712abd7989b` | `org.apache.geode.security.AuthenticationFailedException` | `isSerializable` | `(Ljava/lang/Object;)Z` | 24 | 35 |
| `API-c9c4cf8898b89c8f` | `org.apache.geode.security.GemFireSecurityException` | `<init>` | `(Ljava/lang/String;)V` | 24 | 35 |
| `API-b7758d4cc0076763` | `org.apache.geode.security.GemFireSecurityException` | `<init>` | `(Ljava/lang/String;Ljava/lang/Throwable;)V` | 24 | 35 |
| `API-1b4dd89fbfd8dd9e` | `org.apache.geode.security.GemFireSecurityException` | `<init>` | `(Ljava/lang/Throwable;)V` | 24 | 35 |
| `API-a5a0ab0217b01e24` | `org.apache.geode.security.GemFireSecurityException` | `<type>` | `(type)` | 24 | 35 |
| `API-d1a6b1d424732237` | `org.apache.geode.security.GemFireSecurityException` | `getCause` | `()Ljava/lang/Throwable;` | 24 | 35 |
| `API-fbadf07d0bff6b73` | `org.apache.geode.security.GemFireSecurityException` | `getResolvedObj` | `()Ljava/lang/Object;` | 24 | 35 |
| `API-c553c10d567b8846` | `org.apache.geode.security.GemFireSecurityException` | `getRootCause` | `()Ljava/lang/Throwable;` | 24 | 35 |
| `API-21b9ba62ffdc21e0` | `org.apache.geode.security.GemFireSecurityException` | `isSerializable` | `(Ljava/lang/Object;)Z` | 24 | 35 |
| `API-f42b20579018c009` | `org.apache.geode.security.NotAuthorizedException` | `<init>` | `(Ljava/lang/String;)V` | 24 | 35 |
| `API-69911a950a47d658` | `org.apache.geode.security.NotAuthorizedException` | `<init>` | `(Ljava/lang/String;Ljava/lang/Throwable;)V` | 24 | 35 |
| `API-04b43bc076163400` | `org.apache.geode.security.NotAuthorizedException` | `<init>` | `(Ljava/lang/String;Ljava/lang/Throwable;Ljava/security/Principal;)V` | 24 | 35 |
| `API-888d392b909a104b` | `org.apache.geode.security.NotAuthorizedException` | `<init>` | `(Ljava/lang/String;Ljava/security/Principal;)V` | 24 | 35 |
| `API-949943112f36ebf3` | `org.apache.geode.security.NotAuthorizedException` | `<type>` | `(type)` | 24 | 35 |
| `API-5fefaddf8b830486` | `org.apache.geode.security.NotAuthorizedException` | `getCause` | `()Ljava/lang/Throwable;` | 24 | 35 |
| `API-193065bc675cc9a6` | `org.apache.geode.security.NotAuthorizedException` | `getPrincipal` | `()Ljava/security/Principal;` | 24 | 35 |
| `API-7583634d3b3fb3f0` | `org.apache.geode.security.NotAuthorizedException` | `getResolvedObj` | `()Ljava/lang/Object;` | 24 | 35 |
| `API-87bd28e0386e6dff` | `org.apache.geode.security.NotAuthorizedException` | `getRootCause` | `()Ljava/lang/Throwable;` | 24 | 35 |
| `API-c2d59503c82f7af1` | `org.apache.geode.security.NotAuthorizedException` | `isSerializable` | `(Ljava/lang/Object;)Z` | 24 | 35 |

### client-copy-and-json-failures — 10 IDs

| API ID | Native receiver | Member | JVM descriptor | Owner | Consumer |
| --- | --- | --- | --- | --- | --- |
| `API-92305bc3e0239fbc` | `org.apache.geode.CopyException` | `<init>` | `()V` | 55 | 31 |
| `API-529904ad4a739828` | `org.apache.geode.CopyException` | `<init>` | `(Ljava/lang/String;)V` | 55 | 31 |
| `API-67ae6007d24355ef` | `org.apache.geode.CopyException` | `<init>` | `(Ljava/lang/String;Ljava/lang/Throwable;)V` | 55 | 31 |
| `API-7bf59b2084046017` | `org.apache.geode.CopyException` | `<init>` | `(Ljava/lang/Throwable;)V` | 55 | 31 |
| `API-54678bda599349e4` | `org.apache.geode.CopyException` | `<type>` | `(type)` | 55 | 31 |
| `API-4579efd56ea66103` | `org.apache.geode.CopyException` | `getRootCause` | `()Ljava/lang/Throwable;` | 55 | 31 |
| `API-302896c76b945ce0` | `org.apache.geode.json.JsonParseException` | `<init>` | `(Ljava/lang/String;)V` | 56 | 31 |
| `API-279273fb3081e950` | `org.apache.geode.json.JsonParseException` | `<init>` | `(Ljava/lang/String;Ljava/lang/Throwable;)V` | 56 | 31 |
| `API-8de623ddbef1cb70` | `org.apache.geode.json.JsonParseException` | `<type>` | `(type)` | 56 | 31 |
| `API-0b0e2bdb06f053dc` | `org.apache.geode.json.JsonParseException` | `getRootCause` | `()Ljava/lang/Throwable;` | 56 | 31 |

### client-runtime-and-received-peer-failures — 17 IDs

| API ID | Native receiver | Member | JVM descriptor | Owner | Consumer |
| --- | --- | --- | --- | --- | --- |
| `API-f4eb2cb1dbc4d54c` | `org.apache.geode.cache.RoleException` | `<init>` | `()V` | 23 | 36 |
| `API-dce076d409470a16` | `org.apache.geode.cache.RoleException` | `<init>` | `(Ljava/lang/String;)V` | 23 | 36 |
| `API-bd895db317dd5f08` | `org.apache.geode.cache.RoleException` | `<init>` | `(Ljava/lang/String;Ljava/lang/Throwable;)V` | 23 | 36 |
| `API-7f13ffb32a659302` | `org.apache.geode.cache.RoleException` | `<init>` | `(Ljava/lang/Throwable;)V` | 23 | 36 |
| `API-282c350d5aa5103a` | `org.apache.geode.cache.RoleException` | `<type>` | `(type)` | 23 | 36 |
| `API-c7d0abd665ac0cf7` | `org.apache.geode.cache.RoleException` | `getRootCause` | `()Ljava/lang/Throwable;` | 23 | 36 |
| `API-a7d1a8baee97d1ff` | `org.apache.geode.cache.RoleException` | `toString` | `()Ljava/lang/String;` | 23 | 36 |
| `API-015ba725816a44e4` | `org.apache.geode.cache.StatisticsDisabledException` | `<init>` | `()V` | 23 | 36 |
| `API-d693a7fd5748a87f` | `org.apache.geode.cache.StatisticsDisabledException` | `<init>` | `(Ljava/lang/String;)V` | 23 | 36 |
| `API-4afd02a70d47c13c` | `org.apache.geode.cache.StatisticsDisabledException` | `<init>` | `(Ljava/lang/String;Ljava/lang/Throwable;)V` | 23 | 36 |
| `API-8141ea1d4e981b8d` | `org.apache.geode.cache.StatisticsDisabledException` | `<init>` | `(Ljava/lang/Throwable;)V` | 23 | 36 |
| `API-e2fb0326cf061af6` | `org.apache.geode.cache.StatisticsDisabledException` | `<type>` | `(type)` | 23 | 36 |
| `API-59468dc488e63b81` | `org.apache.geode.cache.StatisticsDisabledException` | `getRootCause` | `()Ljava/lang/Throwable;` | 23 | 36 |
| `API-547094a38cde4056` | `org.apache.geode.cache.StatisticsDisabledException` | `toString` | `()Ljava/lang/String;` | 23 | 36 |
| `API-8603dc7ca98492b7` | `org.apache.geode.distributed.LeaseExpiredException` | `<init>` | `(Ljava/lang/String;)V` | 23 | 36 |
| `API-d1d30655a4f477c6` | `org.apache.geode.distributed.LeaseExpiredException` | `<type>` | `(type)` | 23 | 36 |
| `API-1c538a57e9dd4d08` | `org.apache.geode.distributed.LeaseExpiredException` | `getRootCause` | `()Ljava/lang/Throwable;` | 23 | 36 |

### excluded-peer-startup-and-admin-failures — 28 IDs

| API ID | Native receiver | Member | JVM descriptor | Owner | Consumer |
| --- | --- | --- | --- | --- | --- |
| `API-2ca3c1cf7dd53b24` | `org.apache.geode.IncompatibleSystemException` | `<init>` | `(Ljava/lang/String;)V` | 76 | 36 |
| `API-ba62b35e0bd20cb0` | `org.apache.geode.IncompatibleSystemException` | `<type>` | `(type)` | 76 | 36 |
| `API-6865126d194d1fdb` | `org.apache.geode.IncompatibleSystemException` | `getRootCause` | `()Ljava/lang/Throwable;` | 76 | 36 |
| `API-5e3fc9dbb7420a99` | `org.apache.geode.NoSystemException` | `<init>` | `(Ljava/lang/String;)V` | 76 | 36 |
| `API-fe6291b3b09c11ef` | `org.apache.geode.NoSystemException` | `<init>` | `(Ljava/lang/String;Ljava/lang/Throwable;)V` | 76 | 36 |
| `API-c0bb76b095e047cc` | `org.apache.geode.NoSystemException` | `<type>` | `(type)` | 76 | 36 |
| `API-a131a4af220747ac` | `org.apache.geode.NoSystemException` | `getRootCause` | `()Ljava/lang/Throwable;` | 76 | 36 |
| `API-5f8de3ab2d295d2c` | `org.apache.geode.SystemConnectException` | `<init>` | `(Ljava/lang/String;)V` | 76 | 36 |
| `API-7878ebf0507af219` | `org.apache.geode.SystemConnectException` | `<init>` | `(Ljava/lang/String;Ljava/lang/Throwable;)V` | 76 | 36 |
| `API-1f7523159f451ed8` | `org.apache.geode.SystemConnectException` | `<type>` | `(type)` | 76 | 36 |
| `API-2af38c02470f5831` | `org.apache.geode.SystemConnectException` | `getRootCause` | `()Ljava/lang/Throwable;` | 76 | 36 |
| `API-ecadd274099383f7` | `org.apache.geode.SystemIsRunningException` | `<init>` | `()V` | 76 | 36 |
| `API-b8098f3200f04051` | `org.apache.geode.SystemIsRunningException` | `<init>` | `(Ljava/lang/String;)V` | 76 | 36 |
| `API-5825aacd6c4efe21` | `org.apache.geode.SystemIsRunningException` | `<type>` | `(type)` | 76 | 36 |
| `API-fe0d06555f7a5e00` | `org.apache.geode.SystemIsRunningException` | `getRootCause` | `()Ljava/lang/Throwable;` | 76 | 36 |
| `API-7c6cabac3ca1a03c` | `org.apache.geode.admin.RuntimeAdminException` | `<init>` | `()V` | 76 | 36 |
| `API-75037ad67ac35c2a` | `org.apache.geode.admin.RuntimeAdminException` | `<init>` | `(Ljava/lang/String;)V` | 76 | 36 |
| `API-08618b3347b4f498` | `org.apache.geode.admin.RuntimeAdminException` | `<init>` | `(Ljava/lang/String;Ljava/lang/Throwable;)V` | 76 | 36 |
| `API-e61ed00cf37e4a46` | `org.apache.geode.admin.RuntimeAdminException` | `<init>` | `(Ljava/lang/Throwable;)V` | 76 | 36 |
| `API-12fa95c37b610967` | `org.apache.geode.admin.RuntimeAdminException` | `<type>` | `(type)` | 76 | 36 |
| `API-7190b33e36cc0f5a` | `org.apache.geode.admin.RuntimeAdminException` | `getRootCause` | `()Ljava/lang/Throwable;` | 76 | 36 |
| `API-0c2a537dbe6ae9c1` | `org.apache.geode.cache.GatewayException` | `<init>` | `()V` | 76 | 36 |
| `API-1415f0405fdac981` | `org.apache.geode.cache.GatewayException` | `<init>` | `(Ljava/lang/String;)V` | 76 | 36 |
| `API-72eb53adec1782c5` | `org.apache.geode.cache.GatewayException` | `<init>` | `(Ljava/lang/String;Ljava/lang/Throwable;)V` | 76 | 36 |
| `API-e79746fcbd14ba6a` | `org.apache.geode.cache.GatewayException` | `<init>` | `(Ljava/lang/Throwable;)V` | 76 | 36 |
| `API-f9dcf9be74797c98` | `org.apache.geode.cache.GatewayException` | `<type>` | `(type)` | 76 | 36 |
| `API-87b607aa8e6daa29` | `org.apache.geode.cache.GatewayException` | `getRootCause` | `()Ljava/lang/Throwable;` | 76 | 36 |
| `API-480db17f8975dc0a` | `org.apache.geode.cache.GatewayException` | `toString` | `()Ljava/lang/String;` | 76 | 36 |

### excluded-peer-cache-exists-failure — 7 IDs

| API ID | Native receiver | Member | JVM descriptor | Owner | Consumer |
| --- | --- | --- | --- | --- | --- |
| `API-c8e8516a4b30f76f` | `org.apache.geode.cache.CacheExistsException` | `<init>` | `()V` | 76 | 36 |
| `API-b7e01d87a31ad0a9` | `org.apache.geode.cache.CacheExistsException` | `<init>` | `(Lorg/apache/geode/cache/Cache;Ljava/lang/String;)V` | 76 | 36 |
| `API-6cad05522baa031d` | `org.apache.geode.cache.CacheExistsException` | `<init>` | `(Lorg/apache/geode/cache/Cache;Ljava/lang/String;Ljava/lang/Throwable;)V` | 76 | 36 |
| `API-f4701f53e24b528e` | `org.apache.geode.cache.CacheExistsException` | `<type>` | `(type)` | 76 | 36 |
| `API-b27047e79f791b79` | `org.apache.geode.cache.CacheExistsException` | `getCache` | `()Lorg/apache/geode/cache/Cache;` | 76 | 36 |
| `API-23b7b612dbc8f8be` | `org.apache.geode.cache.CacheExistsException` | `getRootCause` | `()Ljava/lang/Throwable;` | 76 | 36 |
| `API-9500c6eacedafb89` | `org.apache.geode.cache.CacheExistsException` | `toString` | `()Ljava/lang/String;` | 76 | 36 |

### excluded-member-config-mutation-failures — 7 IDs

| API ID | Native receiver | Member | JVM descriptor | Owner | Consumer |
| --- | --- | --- | --- | --- | --- |
| `API-f1e55fd2962253e9` | `org.apache.geode.InvalidValueException` | `<init>` | `(Ljava/lang/String;)V` | 76 | 36 |
| `API-3e09f851940221da` | `org.apache.geode.InvalidValueException` | `<init>` | `(Ljava/lang/String;Ljava/lang/Throwable;)V` | 76 | 36 |
| `API-b9cad104e3d559b1` | `org.apache.geode.InvalidValueException` | `<type>` | `(type)` | 76 | 36 |
| `API-37f5fae74f286cae` | `org.apache.geode.InvalidValueException` | `getRootCause` | `()Ljava/lang/Throwable;` | 76 | 36 |
| `API-58e776cf0440a0ab` | `org.apache.geode.UnmodifiableException` | `<init>` | `(Ljava/lang/String;)V` | 76 | 36 |
| `API-5350911ae1c1d9a1` | `org.apache.geode.UnmodifiableException` | `<type>` | `(type)` | 76 | 36 |
| `API-aa2d6cac242bef8c` | `org.apache.geode.UnmodifiableException` | `getRootCause` | `()Ljava/lang/Throwable;` | 76 | 36 |

### excluded-version-and-datasource-internals — 7 IDs

| API ID | Native receiver | Member | JVM descriptor | Owner | Consumer |
| --- | --- | --- | --- | --- | --- |
| `API-83205a968b330d48` | `org.apache.geode.cache.VersionException` | `<init>` | `()V` | 76 | 36 |
| `API-d76243712b8208ac` | `org.apache.geode.cache.VersionException` | `<init>` | `(Ljava/lang/String;)V` | 76 | 36 |
| `API-1d5e1a041e805e39` | `org.apache.geode.cache.VersionException` | `<init>` | `(Ljava/lang/String;Ljava/lang/Throwable;)V` | 76 | 36 |
| `API-47ffe4f2d6a81753` | `org.apache.geode.cache.VersionException` | `<init>` | `(Ljava/lang/Throwable;)V` | 76 | 36 |
| `API-1d1ab161de2d3e51` | `org.apache.geode.cache.VersionException` | `<type>` | `(type)` | 76 | 36 |
| `API-921af23b73419c37` | `org.apache.geode.cache.VersionException` | `getRootCause` | `()Ljava/lang/Throwable;` | 76 | 36 |
| `API-b1797970f6bf572a` | `org.apache.geode.internal.datasource.DataSourceCreateException` | `<type>` | `(type)` | 76 | 36 |
