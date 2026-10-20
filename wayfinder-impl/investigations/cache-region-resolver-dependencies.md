# Cache and region resolver migration dependencies

Snapshot: 2026-10-20, `wayfinder/unified_driver`, after region-path extraction. This records source inspection, not a completed resolver migration.

## Selected boundary

Migrate both root resolver interfaces and their implementations to `DriverConnection` / `DriverRegion` together. Remove the process-global `ClientCacheFactoryCacheResolver`; its only repository consumer is its own integration test. Replace that test with coverage of explicitly injected connection ownership.

One production consumer crosses into the native pool subsystem: `ClientCacheDefaultPoolResolver`. Change its constructor dependency from `CacheResolver<ClientCache>` to a typed `Supplier<ClientCache>`, retaining its native pool result and current lookup behavior. It receives a native cache from its native caller; it must never unwrap a driver connection. This removes a dependency on the migrated interface without adding a forwarding facade, a second resolver API, or a static locator. Its only repository construction sites are its unit tests.

Region resolution needs one additional driver surface: registration of a typed region lifecycle callback. The existing implementation actively invalidates its cache through native callbacks; replacing that with no notification or speculative polling is not equivalent. Implement the callback adapter, local mock behavior and resolver migration in the same slice. No cache/region factory migration or pool SPI is required to compile and exercise this boundary. [Implementation ticket](../../wayfinder-issues/09-cache-region-resolver-driver-migration.md).

## Reference inventory and scope

The [reference index](../resolver-references.tsv) contains 216 matching source lines in 26 files: 73 lines in 13 main files, 142 in 12 unit-test files, and one in one integration-test file. These include imports, declarations, type uses and assertions; they are not invocation counts. Block comments and comment-only lines were excluded. Searches covered module `src` trees, including Java, Kotlin, XML, AsciiDoc, properties and YAML. No direct references were found in Spring Test sources or resource wiring. This does not establish compatibility for external application code.

The complete main-source set is:

| File | Native boundary and direct dependency |
| --- | --- |
| [CacheResolver][cache] | `T extends ClientCache`, also `Supplier<T>`; `get()` calls `resolve()`. |
| [RegionResolver][region] | Returns native `Region<K,V>` from `resolve` and `require`. |
| [AbstractCachingCacheResolver][caching-cache] | Memoizes a native cache reference. |
| [SingleCacheCacheResolver][single-cache] | Returns a lambda capturing the supplied nullable native cache. |
| [BeanFactoryCacheResolver][bean-cache] | Looks up a native `ClientCache` bean by configured name or type. |
| [ComposableCacheResolver][compose-cache] | Composes cache resolvers; its generic bound and exception documentation are native. |
| [ClientCacheFactoryCacheResolver][global-cache] | Singleton plus `ClientCacheFactory.getAnyInstance()`. |
| [AbstractCachingRegionResolver][caching-region] | Native `CacheListenerAdapter`, region attributes/mutator, listener registration and destruction event. |
| [SingleRegionRegionResolver][single-region] | Captures one native region and compares its name. |
| [BeanFactoryRegionResolver][bean-region] | Looks up native `Region` beans, then uses the caching/listener base. |
| [RegionServiceRegionResolver][service-region] | Adapts `CacheResolver<ClientCache>` or a supplied native `RegionService` into `getRegion(name)`. Its nested `RegionServiceResolver` returns `Optional<T>`. |
| [ComposableRegionResolver][compose-region] | Composes region resolvers and inherits listener registration/caching. |
| [ClientCacheDefaultPoolResolver][default-pool] | Resolves a native cache through `CacheResolver`, then calls `getDefaultPool()`. The only consumer outside the resolver family. |

All 12 unit-test classes are under `spring-data-vmware-gemfire/src/test/java/org/springframework/data/gemfire/`: `CacheResolverUnitTests`, `RegionResolverUnitTests`, the nine `support/*ResolverUnitTests` corresponding to the nine support implementations above, and `client/support/ClientCacheDefaultPoolResolverUnitTests`. The one integration test is [ClientCacheFactoryCacheResolverIntegrationTests][global-test]. Paths and matching lines for every class are in the index.

Refresh searches:

```sh
rg -n '\b(CacheResolver|RegionResolver)\b' --glob '*.java' --glob '*.xml' --glob '!**/build/**'
rg -n '\b(AbstractCachingCacheResolver|SingleCacheCacheResolver|BeanFactoryCacheResolver|ComposableCacheResolver|ClientCacheFactoryCacheResolver|AbstractCachingRegionResolver|SingleRegionRegionResolver|BeanFactoryRegionResolver|RegionServiceRegionResolver|ComposableRegionResolver)\b' --glob '!**/build/**'
rg -n '\b(ClientCacheDefaultPoolResolver|PoolResolver)\b' --glob '*.java' --glob '*.xml' --glob '!**/build/**'
```

## Behavior observed in source and existing tests

| Concern | Existing behavior | Migration requirement |
| --- | --- | --- |
| Cache supplier | `CacheResolver.get()` delegates once to `resolve`; the interface imposes no nonnull requirement. `SingleCacheCacheResolver.from(null)` returns a nonnull resolver yielding null. | Preserve nullable results and supplier behavior with driver handles. |
| Cache memoization | `AbstractCachingCacheResolver.resolve` synchronizes on the instance. A nonnull result is cached indefinitely; null is retried. Exceptions escape and are not cached. No `isClosed()` call or replacement on close. | Keep borrowed ownership and memoization. Do not silently reconnect or close handles. Add explicit closed-handle and null-retry tests. |
| Cache bean lookup | Blank cache bean names are ignored; nonblank names are not trimmed. `getBean(name, ClientCache.class)` is followed by an **eager** `getBean(ClientCache.class)` argument to `Optional.orElse`, even when named lookup succeeded. A real bean factory can therefore fail on ambiguity despite a valid name. Exceptions are not converted to missing results. | Use driver bean types. Correct the eager fallback to lazy lookup and test with a real bean factory containing multiple connections and a qualified name. Document this correction rather than claiming identical behavior. |
| Bean-factory mutation | Both bean resolvers require a nonnull constructor argument but also implement `BeanFactoryAware` with mutable setters. Changing factories does not clear already cached results. | Constructor-inject a final bean factory in rewritten resolvers; remove setter-based replacement and update those tests. No repository XML/property wiring uses these setters. Cache bean-name configuration can remain instance configuration. |
| Required region | `require` does not call `resolve` for null, empty or whitespace names. A missing result throws `IllegalStateException("Region with name [%s] not found")`. A failure from `resolve` propagates. | Preserve validation, message and failure propagation. |
| Single region | Constructor rejects null. Exact comparison `region.getName().equals(regionName)`; no trimming, slash normalization, or lifecycle check. A valid region name does not equal null/blank inputs. | Delegate to `DriverRegion.name()` and preserve name comparison. Do not infer a path from a name. |
| Region bean lookup | Blank names or `containsBean(name) == false` return null. A present bean is requested as `Region.class`; wrong type and bean creation failures escape. Null misses are retried. | Use `DriverRegion.class`, retaining these distinctions. |
| Service lookup | A nullable supplied service/cache produces an empty optional; missing regions return null. A cache-resolver argument itself must be nonnull. Backend `getRegion` exceptions escape. The nested resolver contract returns `Optional`, not a nullable optional. | Use an injected connection or cache resolver, with no global lookup. Existing `DriverConnection.getRegion` covers this operation. |
| Composition construction | Null collections/arrays/elements are tolerated. All null inputs yield null; one nonnull input is returned unchanged. Multiple inputs form a left-associated composition in input order. | Preserve order, nullable construction and first-nonnull success. |
| Composition failure | Both implementations catch **Throwable** from the first resolver and fall through. The second resolver may also be called inside the try when the first returns null; if it throws it is called again by the catch. The cache test explicitly expects this duplicate call. Nested compositions can hide registration errors as well as lookup errors. | Under the signed-off fail-fast policy, fall through only on null. Propagate exceptions and errors unchanged, particularly typed unsupported/driver failures. Replace legacy exception-swallowing expectations with failure/short-circuit tests; do not translate errors to absence. |
| Cached regions | `AbstractCachingRegionResolver.resolve` synchronizes and uses `ConcurrentHashMap.computeIfAbsent` keyed by the **requested string**. Null/blank requests return null without backend access. Null results are not cached. | Preserve serialized initial resolution, null retry and exact requested keys. |
| Listener registration | A successful lookup calls `region.getAttributesMutator().addCacheListener(this)` and returns the mutator's region. The private duplicate-listener predicate is unused. Each composition layer can register its own listener. | Retain notification through a typed driver callback. Registration failure must propagate and must not cache a falsely usable result. Do not copy the unused capability-like check. |
| Destruction invalidation | `afterRegionDestroy` removes the entry keyed by `event.getRegion().getName()` when that name has text. Null event/region/name is ignored. It does **not** remove aliases or `/path` keys cached for that same region. | Record this existing limitation and preserve name-key semantics in the initial migration; do not claim all aliases are invalidated. Add characterization tests so a later fix is deliberate. |
| Closure/destroyed handles | Cache resolvers never inspect closure. Region resolvers do not inspect `isDestroyed()`. The caching base overrides destruction notification, not listener `close()`. A single-region resolver registers no listener. | Avoid inventing automatic reconnection, destruction probes or closure invalidation. Determine actual native notification timing in driver tests. Resolution itself never owns shutdown. |
| Default pool | Only exact `DEFAULT_POOL_NAME` causes cache resolution. A null cache or null default pool returns null; other names, including null, do not invoke the cache resolver. Failures escape. No caching in this class. | Keep this behavior with `Supplier<ClientCache>` and `Supplier.get()`. It remains explicitly native pending pool migration. |

Existing tests cover ordinary successful lookups, composition ordering and exception fallback, null/blank names, region destruction callbacks, and serialized initial cache/region resolution using MultithreadedTC. They do not prove alias invalidation, real named-bean ambiguity handling, or all closed-cache behavior. `BeanFactoryCacheResolverUnitTests.constructWithNullBeanFactoryThrowsIllegalArgumentException` mistakenly constructs `BeanFactoryPoolResolver`; correct that test as part of migration. The current service-resolver null test relies on Mockito supplying an empty `Optional`, not on production support for returning a null `Optional`.

## Factory and wiring dependencies

The similarly named factory methods are independent code, not implementations of these interfaces:

- [AbstractBasicCacheFactoryBean][basic-factory] exposes native `ClientCache` through `getObject`/`getObjectType`; [AbstractResolvableCacheFactoryBean][resolvable-factory] performs its own `resolveCache` sequence.
- [ResolvableRegionFactoryBean][region-factory] holds a native cache, parent and region. Its private `resolveRegion` calls `GemfireFunctions` directly; `ClientRegionFactoryBean` inherits this hierarchy.
- [ClientCacheConfiguration][cache-config] produces a `ClientCacheFactoryBean`; [ClientCacheParser][cache-parser] and [ClientRegionParser][region-parser] construct those native factories. There are no resolver references in their annotation/XML construction.
- The global resolver integration test uses `@ClientCacheApplication` only to manufacture a native cache for the singleton lookup. Replacing that fixture with explicit driver creation removes this test dependency without rewriting `@ClientCacheApplication`.

After migration, the bean resolvers require explicitly registered `DriverConnection` / `DriverRegion` beans. Existing native factory products are not interchangeable with those beans. Demonstrate this boundary using explicit driver-owned beans in both Java configuration and ordinary Spring `<bean>` XML; do not claim the legacy `gfe:client-cache` or region namespace has migrated. Factory migration remains later work.

Expanding the root cache interface directly into a pool SPI would unnecessarily involve [PoolResolver][pool-resolver], all five pool implementations, `ClientCacheFactoryBean`, `ClientRegionFactoryBean`, `AbstractClientFunctionTemplate` and `ContinuousQueryListenerContainer`. Those consumers need pool/query/function operations beyond a default-pool name. The typed native supplier isolates this dependency without pretending any of those packages are native-free.

## Driver surface and ownership

[DriverConnection][connection-spi] already supplies region lookup and closure state; [DriverRegion][region-spi] supplies names and destroyed state. No new connection creation, mode, metadata, pool or capability API is justified by the selected resolver slice.

The demonstrated gap is region lifecycle callback registration. Add a native-free callback type carrying only the region handle needed for destruction invalidation, with lifecycle cleanup semantics documented. Add registration as a `default` operation on `DriverRegion` throwing `UnsupportedFeatureException.notIntroduced(...)`. Do not expose native attributes, events or mutators. The implementation ticket must pin the final callback signature and test native event delivery before relying on shutdown behavior.

The native adapter delegates registration to the native attributes mutator and adapts native destruction notification. Preserve native threading and delivery behavior; no synthetic callback guarantee on cache close. [GemFireRegion][native-region] currently wraps region operation failures as `DriverOperationException`, and separate wrappers can refer to the same native region. Callback identity and duplicate registration must therefore be considered across handles, not just within a wrapper.

The [mock region][mock-region] currently has no standalone destruction operation: `isDestroyed` reflects connection closure, and [MockConnection.close][mock-connection] releases entries/loaders. Extend only the local callback lifecycle supported by that model, using native behavior established by the adapter tests. Do not invent distributed event delivery, add a public destroy API solely to trigger a test, or claim unsupported remote behavior works. Driver-local native fixtures can trigger native region destruction; resolver unit tests can deliver typed callbacks deterministically.

Resolvers borrow their handles. The application/bean that creates a connection closes it. The native singleton is removed, not relocated into driver-core or replaced by a static supplier. Ordinary instance state, final dependencies, and stateless composition methods suffice.

## Validation and next work

This investigation changed tracker documents and the reference index only. No Gradle commands were needed or run; existing tests were read, not claimed as freshly passing.

The implementation ticket requires full main/unit/integration-source compilation, all 12 affected resolver/pool test classes, injected-ownership integration coverage on native and mock drivers, typed callback adapter/contract tests, and existing driver/cache boundary checks. It also requires extending the native-free source selection to the migrated resolver files and their native-free dependencies. Keep `ClientCacheDefaultPoolResolver` outside that selection until pool migration.

[cache]: ../../spring-data-vmware-gemfire/src/main/java/org/springframework/data/gemfire/CacheResolver.java
[region]: ../../spring-data-vmware-gemfire/src/main/java/org/springframework/data/gemfire/RegionResolver.java
[caching-cache]: ../../spring-data-vmware-gemfire/src/main/java/org/springframework/data/gemfire/support/AbstractCachingCacheResolver.java
[single-cache]: ../../spring-data-vmware-gemfire/src/main/java/org/springframework/data/gemfire/support/SingleCacheCacheResolver.java
[bean-cache]: ../../spring-data-vmware-gemfire/src/main/java/org/springframework/data/gemfire/support/BeanFactoryCacheResolver.java
[compose-cache]: ../../spring-data-vmware-gemfire/src/main/java/org/springframework/data/gemfire/support/ComposableCacheResolver.java
[global-cache]: ../../spring-data-vmware-gemfire/src/main/java/org/springframework/data/gemfire/client/support/ClientCacheFactoryCacheResolver.java
[caching-region]: ../../spring-data-vmware-gemfire/src/main/java/org/springframework/data/gemfire/support/AbstractCachingRegionResolver.java
[single-region]: ../../spring-data-vmware-gemfire/src/main/java/org/springframework/data/gemfire/support/SingleRegionRegionResolver.java
[bean-region]: ../../spring-data-vmware-gemfire/src/main/java/org/springframework/data/gemfire/support/BeanFactoryRegionResolver.java
[service-region]: ../../spring-data-vmware-gemfire/src/main/java/org/springframework/data/gemfire/support/RegionServiceRegionResolver.java
[compose-region]: ../../spring-data-vmware-gemfire/src/main/java/org/springframework/data/gemfire/support/ComposableRegionResolver.java
[default-pool]: ../../spring-data-vmware-gemfire/src/main/java/org/springframework/data/gemfire/client/support/ClientCacheDefaultPoolResolver.java
[global-test]: ../../spring-data-vmware-gemfire/src/integrationTest/java/org/springframework/data/gemfire/client/support/ClientCacheFactoryCacheResolverIntegrationTests.java
[basic-factory]: ../../spring-data-vmware-gemfire/src/main/java/org/springframework/data/gemfire/AbstractBasicCacheFactoryBean.java
[resolvable-factory]: ../../spring-data-vmware-gemfire/src/main/java/org/springframework/data/gemfire/AbstractResolvableCacheFactoryBean.java
[region-factory]: ../../spring-data-vmware-gemfire/src/main/java/org/springframework/data/gemfire/ResolvableRegionFactoryBean.java
[cache-config]: ../../spring-data-vmware-gemfire/src/main/java/org/springframework/data/gemfire/config/annotation/ClientCacheConfiguration.java
[cache-parser]: ../../spring-data-vmware-gemfire/src/main/java/org/springframework/data/gemfire/config/xml/ClientCacheParser.java
[region-parser]: ../../spring-data-vmware-gemfire/src/main/java/org/springframework/data/gemfire/config/xml/ClientRegionParser.java
[pool-resolver]: ../../spring-data-vmware-gemfire/src/main/java/org/springframework/data/gemfire/client/PoolResolver.java
[connection-spi]: ../../spring-data-gemfire-driver-spi/src/main/java/com/vmware/gemfire/driver/spi/DriverConnection.java
[region-spi]: ../../spring-data-gemfire-driver-spi/src/main/java/com/vmware/gemfire/driver/spi/DriverRegion.java
[native-region]: ../../spring-data-gemfire-driver-10.4/src/main/java/com/vmware/gemfire/driver/gemfire104/GemFireRegion.java
[mock-region]: ../../spring-data-gemfire-driver-mock/src/main/java/com/vmware/gemfire/driver/mock/MockRegion.java
[mock-connection]: ../../spring-data-gemfire-driver-mock/src/main/java/com/vmware/gemfire/driver/mock/MockConnection.java
