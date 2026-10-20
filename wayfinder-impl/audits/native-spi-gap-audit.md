# Native GemFire / Driver SPI gap audit

Audit date: 2026-10-21. Baseline: the current working tree, including uncommitted migration work, against [technical-spec.md](../../wayfinder/technical-spec.md) §§1, 3–7 and 10. This records gaps, not implementation completion or a new architecture decision.

Follow-up: the [SPI gap closure implementation plan](../spi-gap-closure-plan.md) assigns all eight findings to implementation and acceptance tickets. Ticket creation does not change the measured baseline or resolve these findings.

**The full `spring-data-vmware-gemfire` module does not meet the specification's native-free boundary.** It still compiles against native GemFire, and its ordinary unit-test runtime includes native GemFire. Most runtime subsystem contracts have no SPI replacement yet. Passing the scoped migration checks or compiling the full mixed module does not demonstrate completion.

The spec permits staged migration on an integration branch, but that sequencing does not waive the final requirements. Missing required SPI methods cannot be counted as supported, and deleting native imports without preserving application-facing behavior is not a valid completion criterion.

The table below retains the original audit snapshot. After [named pool creation and ownership](../../wayfinder-issues/13-named-driver-pool-creation-and-ownership.md), the refreshed machine-readable inventory counts 357 main and 254 unit-test files, with the same native-reference counts; scoped validation covers 21 main and 23 test files. None of the module-wide findings is closed by that foundation.

## Measured scope

| Java source set | Total files | Files with native references outside comments | Native-reference lines | Distinct imported native symbols |
| --- | ---: | ---: | ---: | ---: |
| main | 356 | **186** | 581 | 147 |
| test | 252 | **122** | 350 | 83 |
| integrationTest | 150 | **128** | 446 | 71 |

These are lexical source counts, not numbers of missing methods or a migration-completion percentage. Imports count even when their only use is documentation, because they still require type resolution. Fully qualified code references and native class-name strings count too; comments do not. Types reachable through native public signatures, inherited methods and callbacks can require replacement even when they never appear in an import.

Including comments, native package text occurs in 198 main, 127 unit-test and 133 integration-test Java files. Resources contain another 54 matching lines across eight files, including schema tooling contracts, documentation, fixtures and logger names. Resource text is inventoried separately: a logger category is not a native type dependency, while an XML exported/expected native type is an API contract that needs attention.

Reproduce the inventory with:

```sh
python3 wayfinder-impl/audits/scan_native_references.py
```

Artifacts: [scanner](scan_native_references.py), [per-source-line inventory](native-references.tsv), [imported-symbol inventory and callers](native-imports.tsv), [resource inventory](native-resource-references.tsv), [counts](native-reference-summary.json). This scanner is audit tooling, not the missing Gradle architecture gate or a Java type-resolution analyzer.

## Blocking findings

### G1 — Main and unit-test dependencies still cross the native boundary

**Spec:** §§1, 4, 5.1, 10.

[Spring Data build](../../spring-data-vmware-gemfire/build.gradle.kts), lines 109 and 124, retains `compileOnly(libs.bundles.gemfire)` and `testImplementation(libs.bundles.gemfire)`. `compileOnly` still makes this a native compile-time dependency; it does not establish SPI independence. The [catalog](../../gradle/libs.versions.toml), lines 86–93, includes native core, CQ, deployment classloader, gfsh, logging, TCP server and WAN artifacts.

Fresh offline Gradle dependency reports confirm those seven direct native dependencies on `compileClasspath`, and native artifacts alongside the official mock on `testRuntimeClasspath`. They resolve to **10.4.0-build.3056.df98c835**. This is observed dependency resolution, not just an inference from build declarations. [Evidence extract](resolved-native-dependencies.txt).

**Required closure:** migrate remaining consumers, remove the native bundle from main/test configurations, and reject native artifacts/classes on the complete main and ordinary unit-test classpaths. Native artifacts belong in native driver implementation modules and native integration-test runtimes. The spec explicitly exempts the separate Spring Test module from migration; it does not exempt Spring Data's unit-test classpath.

### G2 — Architecture verification covers only an allowlist

**Spec:** §§4, 7 step 6, 10.

[Spring Data build](../../spring-data-vmware-gemfire/build.gradle.kts), lines 253–377, creates separate `spiCache` source sets and checks only their selected files/classpaths. The actual selected set is **20 production Java files and 21 test Java files**. The full module contains 356 production and 402 test/integration-test files. Many unselected files need no native API, so those ratios are not migration percentages.

`verifySpiCacheBoundary` checks `spiCache.allJava`, `spiCacheTest.allJava`, the selected main compile classpath and selected mock runtime. Attaching it to `check` does not expand its coverage. The full `main`, `test` and `integrationTest` sources remain outside this source gate. The full unit-test runtime remains outside its classpath gate.

**Required closure:** enforce the boundary for every module source and the complete main/unit-test classpaths, including fully qualified references and native class-name reflection. Keep native-specific characterization tests in driver modules. Extend source/resource checks to prevent namespace contracts from continuing to advertise native products. A staged baseline may measure remaining violations, but an allowlist pass must never be reported as module-wide compliance.

### G3 — The SPI currently represents only a small foundation

**Spec:** §§3–5, 7 steps 2–5.

Current operational contracts are [DriverConnection](../../spring-data-gemfire-driver-spi/src/main/java/com/vmware/gemfire/driver/spi/DriverConnection.java), [DriverRegion](../../spring-data-gemfire-driver-spi/src/main/java/com/vmware/gemfire/driver/spi/DriverRegion.java), loader/helper callbacks, and the two-method region lifecycle callback. Connection exposes six default operations: root regions, peer/client shortcut creation, lookup, closed status and close. Region exposes twelve: lifecycle registration, loader installation, argument-bearing get, destroyed status, clear, put-if-absent, name, get, put, remove, contains-key and size. `CacheConfiguration` supplies mode and properties; three SPI enums exist.

The following table maps required families to concrete native consumers. “Absent” means no corresponding typed subsystem contract exists in the current SPI; it does not mean the native product lacks the feature.

| Family | Current Spring Data evidence | SPI gap |
| --- | --- | --- |
| Cache configuration and public factory entry points | [AbstractBasicCacheFactoryBean][basic], [ClientCacheFactoryBean][client-cache], `ClientCacheConfiguration`, `ClientCacheParser` | Partial connection owner exists; legacy products remain native ClientCache. Missing typed PDX/resource/transaction/XML/default-pool configuration and readiness/keep-alive lifecycle. |
| Region creation, attributes and mutation | [RegionAttributesFactoryBean][attributes], [ConfigurableRegionFactoryBean][region-config], [ClientRegionFactoryBean][client-region] | Shortcut-only creation cannot express existing attributes, pool association, disk store, constraints, statistics, callbacks, interest, eviction, expiration or compression. |
| Region operations and native callback execution | [GemfireOperations][operations], [GemfireTemplate][template], [GemfireCallback][callback] | Missing contains-key-on-server, contains-value/value-for-key, create and replace variants, plus native operations exposed through callback access to Region. Bulk/native semantics need mapping; replacing everything with loops is not automatically equivalent. |
| Cache listeners and events | [LookupRegionFactoryBean][lookup], [ClientRegionFactoryBean][client-region] | Only region destruction and cleanup notifications exist. Missing eight other CacheListener callbacks, typed event metadata, and general listener configuration/removal. See G4. |
| Cache writers | [LookupRegionFactoryBean][lookup] | No typed CacheWriter, before-event callbacks, event payloads or writer-veto/error translation. |
| Pools | [PoolFactoryBean][pool], native resolver/adapter family | No pool handle/configuration/creation/lookup/lifecycle SPI yet. Named pool foundation is only an open implementation ticket. All tuning, default-pool semantics and socket callbacks also remain. |
| Queries, results and indexes | [GemfireTemplate][template], [DefinedIndexesApplicationListener][indexes], [AbstractIndexSupport][index-support] | No QueryService, Query, SelectResults/result-type or Index/statistics contract. Local versus pooled query routing and query errors remain native. |
| Continuous queries | [ContinuousQueryListenerContainer][cq], `ContinuousQueryDefinition`, `ContinuousQueryListener` | No typed CQ query/attributes/event/listener contract or registration, execution, stop/close lifecycle. |
| Function execution and registration | [FunctionServiceFactoryBean][function-factory], [AbstractFunctionExecution][execution], function templates/proxies | No function, execution target, context, result sender/collector, filter/argument or registration SPI. Native functions handed in by applications need coverage too. |
| Transactions | [GemfireTransactionManager][transactions], `transaction/event` | No manager, ID, begin/commit/rollback/suspend/resume, listener/writer or transaction-event SPI. |
| PDX, serialization and JSON | [MappingPdxSerializer][pdx], `serialization`, [JSONRegionAdvice][json] | No typed PdxReader/Writer/Instance/Serializer or serialization/instantiator/JSON adapter contracts. Application serializer callbacks are part of this gap. |
| Disk stores and persistence configuration | [DiskStoreFactoryBean][disk], disk-store ordering/configuration | No disk-store handle/configuration/lifecycle SPI. Native shortcut enum values alone do not replace this configuration surface. |
| Expiration and eviction | [AnnotationBasedExpiration][expiration], [EvictionAttributesFactoryBean][eviction] | No CustomExpiry/entry context, expiration attributes/actions, eviction attributes/actions or ObjectSizer contract. Existing Spring wrapper enums still hold/return native values. |
| Security and connection customization | [AbstractAuthInitialize][auth], authentication configuration, pool socket factory settings | Properties alone cannot replace typed AuthInitialize, native member arguments, authenticated views or SocketFactory callbacks. |
| Distributed system, members, locator and server metadata | [DistributedSystemUtils][distributed], `support/GemfireFunctions`, [SubscriptionEvictionPolicy][server] | No corresponding typed distributed/member/server contracts; runtime/native internal lookups persist. Need cover actual consumers, not manufacture a global service locator. |
| Repository and mapping integration | [SimpleGemfireRepository][repository], repository query/factory implementations, mapping annotations | Native Region/query/PDX dependencies remain. Foundational SPI handles are not yet consumed by the repository stack. |
| Exception translation | [GemfireCacheUtils][exceptions] | Current driver exceptions/translator exist, but native exception dispatch is still used throughout legacy consumers. Preserve required DataAccessException distinctions; generic wrapping alone is not an equivalent migration. |

WAN is mentioned in the spec and remains in the native dependency bundle, but this audit found no direct `org.apache.geode.cache.wan` imports in the module's Java source. Do not infer a required method list from the artifact name alone: establish the reachable configuration/public surface before claiming WAN coverage or exclusion.

### G4 — Application extension points expose more API than internal call sites reveal

**Spec:** §§1, 4, 5.3, 7 step 9.

[LookupRegionFactoryBean][lookup], lines 40–47 and 96–117, accepts native CacheListener arrays, CacheLoader, CacheWriter and CustomExpiry callbacks. Applications supply implementations. Spring Data need not call every callback itself for those callbacks to be supported behavior. The driver needs to adapt the entire exposed callback contract and its event types.

The local native 10.4 [callback signature snapshot](native-callback-api.txt) demonstrates this concretely:

| Native CacheListener callback | Current SPI representation |
| --- | --- |
| afterCreate, afterUpdate, afterInvalidate, afterDestroy | Absent |
| afterRegionInvalidate, afterRegionClear, afterRegionCreate, afterRegionLive | Absent |
| afterRegionDestroy | Partial: only a DriverRegion handle, not the native RegionEvent information |
| close (inherited CacheCallback) | Present in DriverRegionLifecycleListener |

Native CacheEvent supplies operation, callback argument and availability, remote-origin flag and distributed member. EntryEvent additionally supplies key, old/new values and availability/serialized forms, transaction ID and client origin. None has a typed SPI event representation. Therefore “add the missing callback names” alone would still lose behavior.

CacheCallback also inherits native Declarable. Its initialization/configuration surface must be assessed with the XML and callback wiring; forwarding `close` alone does not replace that inherited contract.

[GemFireRegionLifecycleListener](../../spring-data-gemfire-driver-10.4/src/main/java/com/vmware/gemfire/driver/gemfire104/GemFireRegionLifecycleListener.java) inherits no-ops from native CacheListenerAdapter. This is sufficient for the resolver's limited destruction use, but neither Java compilation nor the current contract suite detects the missing general listener surface. It must not be treated as the complete CacheListener migration.

The same review must follow signatures for cache writers, custom expiry, transaction callbacks, CQ callbacks, serializers, result collectors and authentication/socket callbacks. Native types reachable through those signatures—such as SerializedCacheValue or DistributedMember—can be absent from the 147 direct import symbols and still require SPI representation. `GemfireCallback`'s native Region parameter is another exposed surface, broader than internal template calls.

**Required closure:** maintain a native-to-SPI coverage matrix for public/accepted/inherited API surfaces as well as direct native calls. Each required operation and reachable type needs an implementation plus tests, or an explicit, reviewed scope decision. A missing operation must not disappear merely because a newly narrowed interface compiles.

### G5 — Annotation and XML entry points still advertise native contracts

**Spec:** §§1, 4, 5.5, 7 steps 3 and 5.

[ClientRegion annotation](../../spring-data-vmware-gemfire/src/main/java/org/springframework/data/gemfire/mapping/annotation/ClientRegion.java) still returns native ClientRegionShortcut. `EnableEntityDefinedRegions` and `EnableCachingDefinedRegions` also import/use native shortcut enums. Creating SPI enum copies did not retarget these entry points. Spring's `ScopeType`, `ExpirationActionType` and `EvictionActionType` wrap native objects and expose them from getters; their names do not make them native-free.

[spring-geode-3.0.xsd](../../spring-data-vmware-gemfire/src/main/resources/org/springframework/data/gemfire/config/spring-geode-3.0.xsd) still exports native ClientCache (line 221), Pool (285), Region (345), CacheListener (400), CacheLoader (431), CacheWriter (447), CustomExpiry (478), and further native types. Parsers/factories still produce or expect these types.

**Required closure:** migrate annotation types/defaults, enum conversion, namespace parsers, factory products, application callbacks and schema expected/exported types together. Ordinary bean XML for `DriverConnectionFactoryBean` proves the new owner can be wired; it does not establish that `<gfe:client-cache>` or the existing region namespace has migrated.

### G6 — Global/native internal paths and exception translation remain in Spring Data

**Spec:** §§3–5, 5.7 and 7 step 4.

Examples include `ClientCacheFactoryBean.doFetchCache` calling `ClientCacheFactory.getAnyInstance`, `PoolFactoryBean`/`PoolManagerPoolResolver` using PoolManager, `DistributedSystemUtils` using InternalDistributedSystem, and `GemfireCacheUtils` loading a native CQ exception by class name. Native imports include internal cache, serialization, JNDI and CLI types (see the symbol inventory).

`GemfireCacheUtils` still categorizes native exceptions. The new `GemFireDriverExceptionTranslator` recognizes driver exceptions, but it is not a replacement for all these call sites. Transaction/query/function-specific semantics must survive the conversion.

`GemfireBeanFactoryLocator` retains a static BeanFactory registry. Legacy static state is permitted during staging by §5.7's scope rule; it is not permission to copy those service locators into rewritten code. Move native version/internal adaptation into the native driver and inject collaborators into migrated Spring code. Native GemFire's own global behavior may pass through inside the driver (§5.7(6)); adding a driver-owned global registry is different and forbidden.

### G7 — Verification proves the existing small SPI, not required API completeness

**Spec:** §§4, 5.3, 6.1, 7 step 7, 10.

- [DefaultMethodContract](../../spring-data-gemfire-driver-contract-tests/src/main/java/com/vmware/gemfire/driver/contract/DefaultMethodContract.java) enumerates default methods that already exist in the SPI and requires probes. This is useful regression protection, but it cannot discover an omitted native API or missing abstract application callback.
- [GemFireDriverContract](../../spring-data-gemfire-driver-contract-tests/src/main/java/com/vmware/gemfire/driver/contract/GemFireDriverContract.java) probes the current 18 connection/region defaults and covers local CRUD/loaders/destruction. It does not cover the absent pool/query/function/transaction/PDX families. Method probes also do not exhaust every argument combination; local-shortcut success is not proof of distributed behavior.
- The mock intentionally supports local regions and rejects nonlocal shortcuts/network search. That is honest behavior, not a defect to disguise with fake distributed guarantees. Native integration tests must verify real supported network features as the APIs are added.
- The reference driver has an integration suite for its pinned 10.4 version, and the selected Spring tests run on both mock and native runtimes. The full migrated Spring suite/matrix does not yet exist. Only one native version is implemented; the initial release version list is deferred by the spec, so this audit does not invent additional required versions.
- No japicmp/Revapi compatibility gate or equivalent ABI comparison wiring was found in repository build/configuration source. §6.1 acknowledges there is no release baseline yet; the gate still needs wiring before additive-compatibility claims become release guarantees.
- No §5.7 architecture rule enforcing mutable-static/singleton restrictions across new modules was found. The mock classpath check and selected Spring native check are not such a rule. SPI/core boundary assertions also need systematic enforcement rather than relying solely on current dependency declarations.

**Required closure:** link the required API inventory to implementations and behavioral tests, keep default-method probes, add argument/feature-specific native validation, enforce full boundaries and wire ABI/static-state checks. Do not mark the final migration complete from scoped green suites.

### G8 — Spring Test remains a staging dependency that must be decoupled from native-free unit tests

**Spec:** §§4, 10–11.

The full Spring Data test configuration still depends on `spring-test-vmware-gemfire` (build line 159). That module's native mock initializer hooks and test helpers depend on native types and the legacy factories. Its build declares the native bundle as `compileOnly`, so this audit does **not** claim it transitively supplies the native runtime; Spring Data already supplies native jars directly. Its helper APIs nevertheless cannot support native-free migrated tests unchanged.

**Required closure:** move Spring Data tests to the official driver mock/SPI, and remove or isolate uses of native Spring Test helpers. Address cross-module compilation when deleting legacy factory initializer types. Migrating the separate Spring Test module itself is out of scope; retaining native helper usage in Spring Data unit tests is not an exemption.

## Where remaining production dependencies are concentrated

Counts are files containing noncomment native references, grouped by top-level package; they sum to 186.

| Package | Files | Package | Files |
| --- | ---: | --- | ---: |
| config | 52 | function | 31 |
| root | 28 | client | 22 |
| repository | 13 | serialization | 6 |
| mapping | 5 | support | 5 |
| expiration | 5 | eviction | 5 |
| transaction | 4 | listener | 4 |
| util | 3 | dao | 1 |
| server | 1 | domain | 1 |

The per-file/per-symbol indexes identify each location. An import count alone cannot prioritize all migration work: config has many wiring references, while one callback type can expose an extensive native API.

## Closure order and acceptance criteria

1. Establish the required API coverage matrix, including application callbacks and types reachable from public/native parameters. Keep this audit as the baseline. Every narrowed migration ticket must point to the remaining obligations rather than imply whole-family completion.
2. Complete foundational region/event/attribute and cache/pool contracts, native adaptation, mock local behavior and shared/native tests. Use the existing lifecycle/loader work, but complete the exposed callback families and payloads.
3. Migrate query/index/CQ, functions, transactions, PDX/serialization, disk/expiration/eviction and other actual native consumers in dependency order. Route all application callbacks through typed driver contracts.
4. Switch public factories, annotations, namespace products, template/repositories and exception translation; remove obsolete native APIs and initialization seams. A second injected factory alongside a native factory is staging progress, not final cutover.
5. Remove native main/unit-test dependencies and native test helpers, enforce the boundary across the whole module, then validate the complete Spring suite against the mock and each selected native driver. Keep native-specific tests in driver modules. Require both behavior preservation and architectural checks.

No architectural sign-off is needed merely to implement these already specified requirements. Removing previously exposed functionality or changing the agreed architectural approach would need separate treatment; silently omitting callbacks is not a substitute for that decision.

## Validation performed and limitations

- Scanned all 758 Java files under the module's source tree, excluding comments for the primary counts; inventoried resources separately. Cross-checked each indexed row against its original file/line and checked report links.
- Inspected all current operational SPI interfaces, driver contract coverage, build dependencies/gates, representative consumers in every listed family, and native callback/event signatures using the cached exact native artifact.
- Ran successful offline Gradle `dependencies` reports for `compileClasspath` and `testRuntimeClasspath`, using the guarded temporary init script and session-only cache access. Logs: `/private/tmp/sdgf-gap-compile-classpath.log` and `/private/tmp/sdgf-gap-test-classpath.log`; reproducible commands and relevant results are in the evidence extract.
- Did not change production code, dependencies or user Gradle/settings edits; did not rerun compilation or runtime tests. Existing passing tests are not reported as fresh audit validation.
- This is a complete lexical native-reference inventory for the scanned source tree and a subsystem/extension-point gap assessment. It is **not** a fully resolved member-by-member transitive API graph or proof of behavior parity. That matrix is itself a missing completion control identified in G4/G7.

[basic]: ../../spring-data-vmware-gemfire/src/main/java/org/springframework/data/gemfire/AbstractBasicCacheFactoryBean.java
[client-cache]: ../../spring-data-vmware-gemfire/src/main/java/org/springframework/data/gemfire/client/ClientCacheFactoryBean.java
[attributes]: ../../spring-data-vmware-gemfire/src/main/java/org/springframework/data/gemfire/RegionAttributesFactoryBean.java
[region-config]: ../../spring-data-vmware-gemfire/src/main/java/org/springframework/data/gemfire/ConfigurableRegionFactoryBean.java
[client-region]: ../../spring-data-vmware-gemfire/src/main/java/org/springframework/data/gemfire/client/ClientRegionFactoryBean.java
[operations]: ../../spring-data-vmware-gemfire/src/main/java/org/springframework/data/gemfire/GemfireOperations.java
[template]: ../../spring-data-vmware-gemfire/src/main/java/org/springframework/data/gemfire/GemfireTemplate.java
[callback]: ../../spring-data-vmware-gemfire/src/main/java/org/springframework/data/gemfire/GemfireCallback.java
[lookup]: ../../spring-data-vmware-gemfire/src/main/java/org/springframework/data/gemfire/LookupRegionFactoryBean.java
[pool]: ../../spring-data-vmware-gemfire/src/main/java/org/springframework/data/gemfire/client/PoolFactoryBean.java
[indexes]: ../../spring-data-vmware-gemfire/src/main/java/org/springframework/data/gemfire/config/support/DefinedIndexesApplicationListener.java
[index-support]: ../../spring-data-vmware-gemfire/src/main/java/org/springframework/data/gemfire/domain/support/AbstractIndexSupport.java
[cq]: ../../spring-data-vmware-gemfire/src/main/java/org/springframework/data/gemfire/listener/ContinuousQueryListenerContainer.java
[function-factory]: ../../spring-data-vmware-gemfire/src/main/java/org/springframework/data/gemfire/function/FunctionServiceFactoryBean.java
[execution]: ../../spring-data-vmware-gemfire/src/main/java/org/springframework/data/gemfire/function/execution/AbstractFunctionExecution.java
[transactions]: ../../spring-data-vmware-gemfire/src/main/java/org/springframework/data/gemfire/transaction/GemfireTransactionManager.java
[pdx]: ../../spring-data-vmware-gemfire/src/main/java/org/springframework/data/gemfire/mapping/MappingPdxSerializer.java
[json]: ../../spring-data-vmware-gemfire/src/main/java/org/springframework/data/gemfire/serialization/json/JSONRegionAdvice.java
[disk]: ../../spring-data-vmware-gemfire/src/main/java/org/springframework/data/gemfire/DiskStoreFactoryBean.java
[expiration]: ../../spring-data-vmware-gemfire/src/main/java/org/springframework/data/gemfire/expiration/AnnotationBasedExpiration.java
[eviction]: ../../spring-data-vmware-gemfire/src/main/java/org/springframework/data/gemfire/eviction/EvictionAttributesFactoryBean.java
[auth]: ../../spring-data-vmware-gemfire/src/main/java/org/springframework/data/gemfire/config/annotation/support/AbstractAuthInitialize.java
[distributed]: ../../spring-data-vmware-gemfire/src/main/java/org/springframework/data/gemfire/util/DistributedSystemUtils.java
[server]: ../../spring-data-vmware-gemfire/src/main/java/org/springframework/data/gemfire/server/SubscriptionEvictionPolicy.java
[repository]: ../../spring-data-vmware-gemfire/src/main/java/org/springframework/data/gemfire/repository/support/SimpleGemfireRepository.java
[exceptions]: ../../spring-data-vmware-gemfire/src/main/java/org/springframework/data/gemfire/GemfireCacheUtils.java
