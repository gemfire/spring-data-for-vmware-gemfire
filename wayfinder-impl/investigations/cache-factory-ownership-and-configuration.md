# Cache-factory ownership and configuration dependencies

Source investigation, 2026-10-21. Resolution belongs to [Cache-factory ownership and configuration dependencies](../../wayfinder-issues/10-cache-factory-ownership-and-configuration-dependencies.md). No production code changes or new runtime test results are part of this investigation.

## Boundary

The smallest complete next slice is an explicitly injected Spring `DriverConnectionFactoryBean`: create one connection through an injected, validated `GemFireDriverManager` and `CacheConfiguration`, or expose an explicitly supplied borrowed connection. The factory owns shutdown only in the creation case. Wire the existing driver-based cache manager and resolvers through it in ordinary Java configuration and bean XML.

This is the foundation of the replacement for the native factory hierarchy. It does not yet replace `@ClientCacheApplication` or `<gfe:client-cache>`. Changing those entry points now would change the bean product from `ClientCache` to `DriverConnection` while leaving numerous native consumers and configuration callbacks incompatible. Keep those entry points and their native consumers together until their configuration surfaces are supported; do not insert a native unwrap or a compatibility adapter. The old hierarchy must be removed at final cutover, not retained as a second supported API.

No new SPI operations are needed for this slice. Creation, configuration properties, explicit handle reuse, closure and runtime-version diagnostics already have SPI/core entry points. More configuration methods would pull PDX, pools, transactions and XML into an otherwise independent ownership change.

## Evidence and navigation

In this document, `S/` means `spring-data-vmware-gemfire/src/main/java/org/springframework/data/gemfire/`; `T/` means the corresponding `src/test/java` package root. Line numbers describe the investigation snapshot.

The [reference index](../cache-factory-references.tsv) records 586 lexical references across 288 files, including source, test and resource entry points. Categories are factory types, configurer types, native cache import candidates, `@ClientCacheApplication` uses and XML client-cache declarations. Java block/full-line comments and XML comments are excluded. This is a navigation index, not a claim that every import is a runtime dependency or every source line is a call. In particular:

- The production inheritance chain is `AbstractFactoryBeanSupport<ClientCache>` → `AbstractBasicCacheFactoryBean` → `AbstractConfigurableCacheFactoryBean` → `AbstractResolvableCacheFactoryBean` → `client/ClientCacheFactoryBean`. No additional class declarations extending those four classes were found in the Java source search. Generic bounds such as `<T extends ClientCacheFactoryBean>` are not subclasses.
- `ClientCacheFactoryBean`'s stale self-extension TODO and duplicated/peer-cache Javadocs do not describe the actual hierarchy: this factory produces a native client cache.
- `postProcess(Object)` and `postProcess(T extends ClientCache)` are distinct overloads. Factory post-processing happens only on creation; cache post-processing also happens on reuse.
- `getCache`, `resolveProperties`, `initializeFactory` and `configurePdx` occur elsewhere with unrelated receivers. The inventory below follows the four concrete classes and their inherited calls rather than treating name matches as call edges.

## Current creation and shutdown behavior

| Stage | Actual behavior and evidence | Consequence for replacement |
| --- | --- | --- |
| Bean initialization | `AbstractBasicCacheFactoryBean:526` calls configurers; `AbstractConfigurableCacheFactoryBean:205` then optionally registers `GemfireBeanFactoryLocator`. It does not open a cache. | Preserve the distinction between configuring a factory and creating its product when migrating legacy callbacks. The new minimal owner can initialize eagerly without importing those callbacks. |
| Product access | Basic `getObject():286` returns its stored cache or invokes `doGetObject`; the resolvable implementation calls `init():50`. `getObjectType():310` reports native `ClientCache` or its runtime class. | The new factory must advertise `DriverConnection` before initialization, cache one successful product and never reopen implicitly. |
| Class loading | `init` temporarily installs Spring's bean classloader as the thread context loader and restores it in `finally`; it wraps `Exception` and stores the cache only after `resolveCache` returns. | Inject an already validated manager. If creation needs Spring's loader, scope the loader change and restore it on every failure; never change a global loader. |
| Reuse | `resolveCache():114` fetches, post-processes and returns an existing cache. `doFetchCache():181` calls `ClientCacheFactory.getAnyInstance()`. | Borrowing must use the exact injected handle; no global search and no reconfiguration of borrowed connections. |
| Creation trigger | The `catch (CacheClosedException)` covers both fetching and post-processing of the existing cache. Even a closed-cache failure during post-processing enters creation. Other failures propagate to `init`'s wrapper. | Do not reproduce exception-driven create-on-failure. The construction path or borrowed path is explicit. |
| Creation order | Resolve properties → construct native factory → initializer callback → configure PDX/pool → factory post-process → native `create()` → cache post-process. | Callback ordering matters when eventually replacing the legacy entry points. `Object` callbacks accepting native factories cannot migrate unchanged. |
| Cache post-process | Load XML, set copy-on-read, set heap percentages, register transaction listeners; client override then registers JNDI data sources. Applies to reused caches too. | Not supported by the first ownership slice; these need typed driver configuration/operations before legacy entry points switch. |
| Destruction | Basic `destroy():574` calls `close(fetchCache())` when `close=true` (default), so an uninitialized factory can close a globally found cache. No created/borrowed ownership flag exists. | An unopened new factory must not discover or close anything. A borrowed factory must never close its supplied connection. |
| Client close override | Client `close():487` directly calls `cache.close(isKeepAlive())`. It bypasses the base `isClosed` guard and `setCache(null)`. | Do not infer the base class's clearing/idempotence behavior for the real client factory. Test shutdown through the actual new owner. Keep-alive requires a future explicit SPI operation. |
| Failure cleanup | A cache created before post-processing throws is not stored by `init`; no local cleanup block surrounds that cache. Locator cleanup follows `super.destroy()` without `finally`. | Do not copy these gaps. A returned owned handle must be closed if subsequent initialization fails; a driver failure before returning a handle remains the driver's cleanup responsibility. |
| Ready-for-events | Client `onApplicationEvent():466` responds to refresh events, uses explicit `readyForEvents` or inferred durability, and swallows `IllegalStateException`/`CacheClosedException`. `isReadyForEvents():822` uses `safeGetValue`, which catches `Throwable`. | The migrated path must not silently suppress unsupported or failed operations. No readiness action belongs to the minimal owner. |

`CacheFactoryInitializer<T>` is a `Function<T,T>`; an initializer can replace the native factory. Its null return falls back to the original factory via `Optional.map(...).orElse(factory)` (`Basic:691`). This is an actual native-object substitution seam used by Spring Test, not merely a notification callback.

The current factory also implements `PersistenceExceptionTranslator` through native `GemfireCacheUtils` (`Basic:732`). Reusing this superclass would reintroduce native dependencies. Keep the new owner independent; general exception translation remains the separate planned migration step.

## Native creation is not exclusive ownership

`GemFire104Driver.connect` calls native `CacheFactory.create()` or `ClientCacheFactory.create()` and wraps the result in a fresh `GemFireConnection`. A new wrapper is not proof of a new native cache.

This was checked against the locally resolved `gemfire-core-10.4.0-build.3056.df98c835.jar`, not inferred from a different Geode release. `javap -c -p org.apache.geode.cache.client.ClientCacheFactory` shows `basicCreate` retrieving the internal cache, checking whether it is open and client-mode, validating pool/cache configuration, then returning that existing cache (bytecode offsets 61–114). A pre-existing peer cache causes a failure. The command's transcript is `/private/tmp/sdgf-client-cache-factory-bytecode.txt`; reproduce it against the matching cached jar if the temporary file is gone.

Therefore the new owner assumes responsibility for closing the handle returned by `connect`; it must not promise that the underlying native cache is exclusive. Applications sharing a cache should create one owner and inject/borrow its connection. Two owners can affect the same native cache. Enforcing exclusivity with a static registry, reference counting or a check-then-create global lookup would add guarantees contrary to the thin-driver design.

The mock deliberately creates independent state for every `connect` (`MockGemFireDriver`, `MockConnection`). Do not change it to emulate native global state, or put a two-independent-native-caches assertion into the shared contract. A reference-driver-only characterization test should demonstrate shared-cache effects; mock isolation belongs in its own tests.

## Properties and runtime utility dependencies

`ClientCacheFactoryBean:207–225` obtains properties from the configurable superclass, asks `GemfireUtils::getDistributedSystem` for the global system, and overlays configured properties on the connected system's properties. Then `configureDurableClient` overlays a nonblank durable ID and, only when that ID is supplied, its optional timeout. Existing system properties are defaults, explicit bean properties win, and explicit durable settings win last. `resolveProperties` can mutate the supplied `Properties` when no merge creates a copy.

The superclass rejects a nonblank `security-shiro-init` property, including values inherited through `Properties` defaults. This check is performed before the connected-system merge; do not claim it validates every merged property. `CacheConfiguration` instead takes an immutable `Map<String,String>` and returns fresh `Properties`; values are redacted in its `toString`. Converting a future legacy `Properties` input must deliberately handle inherited defaults and non-string entries rather than simply casting it to a map.

| Utility dependency | Actual role | Migration treatment |
| --- | --- | --- |
| `util/DistributedSystemUtils:42` | Pure durable-property mutation, but lives in native utility hierarchy. | Keep with future durable configuration; the first slice passes explicit `CacheConfiguration` unchanged. |
| `DistributedSystemUtils:59,71` | Native `isConnected`, global `InternalDistributedSystem.getAnyInstance`. | No global merge in the new owner. If merging is later needed, supply configuration explicitly; do not add a driver service locator. |
| `util/CacheUtils:57` | Durability means a connected distributed system with a nonblank durable-ID property. | `!DriverConnection.isClosed()` is not equivalent. Future state observation needs a typed SPI contract with native evidence. |
| `GemfireUtils:26,37` | Static runtime version snapshot and native `CacheFactory.getVersion`, with fallback on any throwable. | Use injected manager/driver `runtimeGemFireVersion`, metadata and validated SPI version; do not catch failures as `unknown`. |
| `AbstractResolvableCacheFactoryBean:73` | Logs native cache name and distributed member ID, groups, roles, host and PID. | First slice can report driver/version without inventing a membership API just for logging. Membership diagnostics remain with the distributed-system slice. |
| `GemfireBeanFactoryLocator` | Mutable static `BEAN_FACTORIES` registry plus global resolution/reset operations. | Forbidden in new/reworked code. Explicit bean/callback injection must replace this for migrated consumers. |

## Configuration surface versus current SPI

The current SPI supplies mode/properties, creation, region lookup/enumeration/creation, lifecycle status and close. It supplies region loading/lifecycle callbacks, not cache factory, PDX or transaction callbacks.

| Native surface | Current callers/behavior | Needed for next owner slice? / later work |
| --- | --- | --- |
| Mode and distributed-system properties | `new ClientCacheFactory(Properties)`; driver also supports peer mode. | Already supported. Pass the configuration once through the validated driver. No native property merge. |
| PDX flags/disk-store name | Basic setters and `ClientCacheFactoryToPdxConfigurerAdapter`; configured before native create. | No. Future immutable SPI configuration must distinguish unset from false. Disk-store ordering accompanies this surface. |
| `PdxSerializer` | Factory accepts native serializer; `PdxConfiguration` resolves it through mapping/conversion infrastructure. | No. Needs SPI-owned serialization contracts and migration of actual serializer consumers; cannot hide a native serializer in `Object`. |
| Copy-on-read | Native setter after creation, including reuse. | No. Future typed configuration/operation, native delegation and documented mock behavior. |
| Heap percentages | Each value checked for 0–100 before resource-manager setter; native enforces relationships. | No. Future resource-manager delegation; a local mock must not claim real memory pressure/eviction semantics. |
| Transaction listeners/writer | Native callbacks. Listeners are registered, skipping null elements. The writer is stored and exposed, but this hierarchy never calls native `setWriter`. | No. Migrate transaction event types and manager together. The stored-only writer is an existing gap, not evidence that writer registration works. |
| XML resource | `loadCacheXml` opens an input stream and passes it to native cache on create/reuse; catches IO failures. No explicit stream-close block here. | No. Future contract must define stream ownership and injected callback resolution; no static BeanFactory locator. |
| Pool configuration | Explicit bean values override a supplied/resolved pool via `preferDefault`; defaults come from native pool adapter. Endpoints have server/locator selection logic. | No. Needs SPI pool configuration/handle types, including native `SocketFactory` replacement. A property bag cannot express object callbacks. |
| Pool lookup | Explicit `Pool`, named resolver, then dereferenced `PoolFactoryBean`; default resolver is a static `PoolManagerPoolResolver` instance. | No. Migrate this group with injection, pool factory/resolvers and their consumers. |
| Thread-local connections | Setter/getter and annotation mapping exist, but `configurePool` never applies the value. | No. Record as a gap; do not silently add semantics during ownership migration. |
| JNDI data sources | Native internal `ConfigProperty`, global `JNDIInvoker.mapDatasource`; validates/normalizes type and wraps thrown failures. | No. Needs an explicit driver boundary or separately chosen replacement; do not carry internal native types into SPI. |
| `readyForEvents`, `close(keepAlive)` | Durable-client queue lifecycle. | No. Existing `close()` has no keep-alive argument. Future methods default to typed unsupported on old drivers; mock cannot emulate remote queues. |
| Lock/search/message settings in parser | `ClientCacheParser:75–83` names `lockLease`, `lockTimeout`, `messageSyncInterval`, `searchTimeout`; no setters found in the four-class hierarchy. | No. These parser mappings are not proof of working configuration; characterize/fix when migrating that entry point. |

Mock `connect` currently consumes mode and does not emulate supplied native properties. Passing a durable ID to it does not demonstrate durable behavior. Shared tests must stay within supported local operations; unsupported distributed region shortcuts already throw `UnsupportedFeatureException`.

## Callers that must move with legacy entry points

| Group | Concrete references | Coupling |
| --- | --- | --- |
| Annotation construction | `config/annotation/AbstractCacheConfiguration:256,284`; `ClientCacheConfiguration:119,196`; `ClientCacheApplication` imports configuration. | No-arg factory creation and setter configuration; native return/product types. Changing only the factory constructor or product breaks these. |
| Configurer protocol | `ClientCacheConfigurer`, `LazyResolvingComposableClientCacheConfigurer`, `ClientCacheConfiguration:179`. | Generic parameter is the concrete native factory; lookup and ordering are Spring lifecycle behavior. |
| Configurer producers | `PdxConfiguration:178`; `AutoConfiguredAuthenticationConfiguration:206`; `BeanFactoryLocatorConfiguration:70`; `support/EmbeddedServiceConfigurationSupport:240`; `transaction/config/GemfireCacheTransactionsConfiguration:94`. | They mutate the factory, so migration must preserve supported settings through typed configuration, or explicitly reject an unsupported request. |
| Post-processors | `BeanFactoryLocatorConfiguration:52`; `config/support/AbstractDependencyStructuringBeanFactoryPostProcessor:79`; `PdxDiskStoreAwareBeanFactoryPostProcessor`. | Match concrete factory type and establish native initialization dependencies. |
| Namespace | `config/xml/ClientCacheParser:57,62,179`; `GemfireNamespaceHandler:25`; `src/main/resources/.../config/spring-geode-3.0.xsd:213`. | Factory class, setter names, native JNDI values, aliases and default bean names are coupled. Ordinary `<bean>` wiring for the new owner avoids changing this namespace prematurely. |
| Spring Test | `spring-test-vmware-gemfire/.../GemFireMockObjectsBeanPostProcessor:148–208`. | Replaces the native factory initializer with spies; both initializer classes return native `ClientCacheFactory`. Official SPI mock replaces this approach for migrated code. Spring Test's native migration remains out of scope, but compile dependencies cannot be ignored at cutover. |
| Native product consumers | `DiskStoreFactoryBean`, `ResolvableRegionFactoryBean`, `client/ClientRegionFactoryBean`, `PoolFactoryBean`, `GemfireDataSourcePostProcessor`; `transaction/GemfireTransactionManager`. | Require native cache operations/types, regardless of whether their source mentions the factory. |
| Higher-level product consumers | `GemfireTemplate`, repository factories/implementations, client function templates, CQ/cluster-defined-region configuration, schema collection, auto region lookup, Spring context bootstrapping. | A bean named `gemfireCache` with a new product type cannot satisfy their native injection contracts. These remain separate migration groups, not silently redirected to the new owner. |

Fixtures to revisit when those entry points migrate include `ClientCacheFactoryBeanUnitTests`, `AbstractCacheConfigurationUnitTests`, `ClientCacheConfigurationUnitTests`, `ClientCacheParserUnitTests`, `ClientCacheConfigurerIntegrationTests`, `ClientCachePropertiesIntegrationTests`, `CacheNamespaceIntegrationTests`, PDX/transaction namespace tests and `EnableBeanFactoryLocatorConfigurationIntegrationTests`. XML declarations and application annotations, including Spring Test fixtures, are indexed even when they never name the Java factory class. `basic-tx-config.xml` directly names it.

The first ownership slice only needs to move existing driver-wiring fixtures, notably `support/InjectedResolverIntegrationTests` and `support/resolver-context.xml`, off their test-local `ConnectionFactory` helper. It must also exercise `GemfireCacheManager` using the same injected product. None of the native consumer groups needs an adapter to participate in that slice.

## Executable validation plan

The follow-up implementation must test behavior through real Spring contexts and the existing focused mock/native classpaths:

1. Java and ordinary XML declare an injected manager/configuration and the owner. Resolve its product by name and by `DriverConnection` type before/after creation; repeated access gets the same handle. Use it from the migrated cache manager and resolvers; close the context and observe closure/callback cleanup.
2. Create one external connection, inject it into a borrowed factory, close that context, then read/write through the external handle and close it at its actual owner. Ensure Spring does not infer a second destruction callback for the factory product. Include a second unusable candidate to verify qualified wiring.
3. Use focused recording collaborators for null input, initialization failure, unopened destruction, repeated initialization/destruction and close failure. Verify no hidden lookup/reconnect, no borrowed close, original failure propagation and no resource leak after a handle has been acquired. These are ownership tests, not mocks of native internals.
4. On both runtimes exercise CLIENT/LOCAL, and PEER/LOCAL in separate contexts/JVM-safe sequencing. Do not require simultaneous independent native caches or map closure to distributed connection status. Use the existing native fixture isolation.
5. In the reference-driver integration suite characterize two compatible client `connect` calls sharing cache effects and shutdown. In the mock suite retain independent connections. These intentionally differ and must not become a new shared SPI guarantee.
6. Keep source/classpath native-free enforcement on the new factory and its tests. Run the existing driver/core/contract checks and full main/unit/integration-source compilation to catch staging regressions.

Representative commands after implementation (the init script is described below):

```sh
./gradlew -I /private/tmp/sdgf-resolver-validation.init.gradle :spring-data-vmware-gemfire:spiCacheTest :spring-data-vmware-gemfire:spiCacheIntegrationTest :spring-data-vmware-gemfire:verifySpiCacheBoundary -DgemfireVersion=10.4.+ --console=plain
./gradlew -I /private/tmp/sdgf-resolver-validation.init.gradle :spring-data-vmware-gemfire:compileJava :spring-data-vmware-gemfire:compileTestJava :spring-data-vmware-gemfire:compileIntegrationTestJava :spring-test-vmware-gemfire:compileJava :spring-test-vmware-gemfire:compileTestJava -DgemfireVersion=10.4.+ --console=plain
./gradlew -I /private/tmp/sdgf-resolver-validation.init.gradle :spring-data-gemfire-driver-spi:check :spring-data-gemfire-driver-core:check :spring-data-gemfire-driver-contract-tests:check :spring-data-gemfire-driver-mock:check :spring-data-gemfire-driver-10.4:check -DgemfireVersion=10.4.+ --console=plain
```

Preserve the user's `gradle.properties` and disabled Spring Test include in `settings.gradle.kts`. The previous validation init script adds `:spring-test-vmware-gemfire` only when that directory exists under `settingsDir`, avoiding accidental inclusion in the build-tools included build. If needed, recreate that temporary script rather than editing user settings. Gradle requires session-specific sandbox escalation for its cache/locks and native runtime sockets; do not request persistent permission. Record the exact resolved native version again because `10.4.+` can advance.

This investigation validated source references and inspected native bytecode. It did not rerun Gradle or count earlier resolver tests as new validation. No live architectural decision is required for the selected ownership foundation. Detailed legacy configuration contracts and the Spring Test source-compatibility consequence remain visible work before final factory cutover.
