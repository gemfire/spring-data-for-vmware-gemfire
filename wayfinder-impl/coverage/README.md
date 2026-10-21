# Required native API coverage baseline

Recorded 2026-10-21 for [Required native API coverage matrix](../../wayfinder-issues/14-required-native-api-coverage-matrix.md), against GemFire **10.4.0-build.3056.df98c835**. This is the implementation inventory for the [gap closure plan](../spi-gap-closure-plan.md); it does not close the native dependency or behavioral gaps.

The inventory contains **3,851 rows across 292 native types**, **588 current Spring signature exposures**, **419 required direct bytecode member references**, and **624 current source/resource references**. All 144 current production imported native symbols are accounted for; earlier frozen requirements remain. After named pool configuration and socket callbacks, there are **3,066 missing**, **506 partial** and **279 implemented** rows. These are coverage obligations, not 3,851 distinct methods or a completion percentage: rows include types, constructors, constants, overloads and inherited contracts repeated under different receivers.

## Files and interpretation

| Artifact | Purpose |
| --- | --- |
| [API coverage matrix](api-coverage.tsv) | Stable API ID, native receiver/member/JVM descriptor, generic signature, declared exceptions, constants, reachable types, required behavior, SPI/native/mock/test disposition, contract owner and consumer owner. |
| [Reachability edges](api-edges.tsv) | Trace a type back through signatures, inheritance, callback specializations and frozen roots to Spring's exposure. |
| [Spring signatures](spring-exposures.tsv) | Public/protected signatures, generic bounds and native annotation defaults; includes nested implementation classes conservatively. |
| [Spring annotation defaults](spring-annotation-defaults.tsv) | Resolved values of all current Spring annotation defaults, including primitive constants inlined by javac. This supplements the six directly native-typed defaults and prevents a type-only inventory from losing default-value evidence. |
| [Spring evidence](spring-evidence.tsv) | Current source files, line references and bytecode receiver/signature evidence, factored out to avoid repeating long lists in every API row. |
| [Direct members](direct-members.tsv) | Exact native member descriptors referenced by Spring bytecode, including inherited methods and frozen baseline requirements. Constant inlining is covered separately by source references and native constant rows. |
| [Source coverage](source-coverage.tsv) | Every production lexical reference and all audited resource references mapped to a native type or explicit explanation, with an owner. Includes XML exports/expected types, AspectJ expressions, reflection and logger/package strings. |
| [Existing SPI comparison](existing-spi-coverage.tsv) | All 105 default methods/hooks and 30 declared loader/helper/lifecycle/general/socket callback methods mapped to native rows, existing implementation/test sources and remaining obligations. |
| [Frozen requirements](native-api-baseline.json) | Required keys, roots, direct members and historical source evidence. Migrating a source file must not erase the API requirement. |
| [Coverage policy](coverage-policy.json) | Reviewed ownership, current partial mappings, callback subtype traversal, dynamic-result roots and internal boundary dispositions. Edit this instead of generated TSVs. |
| [Manifest](coverage-manifest.json) | Exact artifact hashes, current source/evidence hashes, row counts and output hashes. |

API IDs are `API-` plus the first 16 SHA-256 hex digits of `native-type#member` plus its JVM descriptor. A type row uses `<type>` with an empty descriptor. IDs do not depend on ordering, source lines, owner or implementation status. `REF-` IDs identify source locations and change when those locations move. TSV `file#symbol` values are lookup keys, not Markdown anchors.

`missing` means the requirement has no complete recorded SPI implementation. `partial` means a value, method or narrow adapter exists, but the row names outstanding semantics or adoption. `implemented` requires complete native/mock/test evidence and preserved reachable contracts. `removed` is reserved for verified native removal with the specified typed failure; absence of an SPI method is never evidence of native removal. Implemented leaf/value rows have native and mock/test evidence; partial rows retain reachable-contract and consumer obligations. No row is marked removed.

An SPI equivalent need not copy the native Java shape. For example, cache factory creation can become injected connection creation, native constants can become SPI values, and string native properties can pass through `CacheConfiguration`. Owners must document those mappings and tests. A row is not permission to add static registries, an unwrap method or native types to SPI. Existing native methods retain their required behavior through the typed replacement.

## Confirmed gaps beyond imports

| Evidence | Current gap | Owner |
| --- | --- | --- |
| `API-bf213065a631b946`: `CacheListener.afterCreate(EntryEvent)` | Full callback forwarding now implemented and behavior-tested; complete reachable region/cache contracts and Spring adoption remain tracked. | [Complete region events, listeners and writers](../../wayfinder-issues/17-complete-region-events-listeners-and-writers.md) |
| `API-2ecad1e66a2d2a3e`: `CacheListener.afterRegionDestroy(RegionEvent)` | Full typed region event now forwarded; the separate resolver lifecycle callback remains intentionally narrow. | [Complete region events, listeners and writers](../../wayfinder-issues/17-complete-region-events-listeners-and-writers.md) |
| `API-2de3cdd34d5c188d`: inherited `RegionEvent.getDistributedMember()` | Typed member metadata now forwarded; remaining distributed-runtime contracts retain their owner. | [Complete region events, listeners and writers](../../wayfinder-issues/17-complete-region-events-listeners-and-writers.md) |
| `API-e06a82af11bb7edd`: `SerializedCacheValue.getSerializedValue()` | Lazy serialized view now tested with native serialized remote events; the mock does not fabricate bytes. | [Complete region events, listeners and writers](../../wayfinder-issues/17-complete-region-events-listeners-and-writers.md) |
| `CacheListener.initialize(Cache, Properties)`, `init(Properties)` | Both separate hooks now forwarded for listeners, writers and loaders; automatic Spring assembly remains downstream. | [Complete region events, listeners and writers](../../wayfinder-issues/17-complete-region-events-listeners-and-writers.md) |
| `RegionMembershipListener.initialMembers`, `CqStatusListener.onCqConnected`, `SocketFactory.createSocket` | Region membership callbacks now have real peer join/departure/crash coverage; socket callbacks now have real server socket-use coverage; the CQ extension point remains open. | [Region callbacks](../../wayfinder-issues/17-complete-region-events-listeners-and-writers.md), [Continuous query SPI](../../wayfinder-issues/22-continuous-query-spi.md), [Complete pool SPI](../../wayfinder-issues/20-complete-pool-default-cache-and-durable-spi.md) |
| `Region.compute`/`merge`, `SelectResults` collection methods | Inherited Java collection contracts remain obligations even though their erased descriptors contain no native types. | [Complete region operations](../../wayfinder-issues/18-complete-region-operations-and-metadata-spi.md), [Query/results SPI](../../wayfinder-issues/21-query-results-and-index-spi.md) |
| Six native enum annotation defaults in Spring signatures | Defining SPI enums has not migrated these defaults or their consumers. | [Region configuration](../../wayfinder-issues/19-region-configuration-enums-expiration-and-eviction.md), [Region factory migration](../../wayfinder-issues/30-region-factory-and-configuration-migration.md) |
| `ClientCacheFactoryBean.JndiDataSource.getProps/setProps` | Generic `List<ConfigProperty>` exposes a native internal payload. All eight public constructors/accessors are inventoried; typed datasource properties must replace it. | [Security and authenticated views SPI](../../wayfinder-issues/24-security-and-authenticated-views-spi.md) |
| `JsonDocument`, `JsonDocumentFactory`, `Struct`, `PdxUnreadFields`, writable PDX | Reachable/dynamic results need typed representations in addition to the directly imported JSON/PDX/query interfaces. | [PDX/serialization/JSON SPI](../../wayfinder-issues/26-pdx-serialization-and-json-spi.md), [Query/results SPI](../../wayfinder-issues/21-query-results-and-index-spi.md) |

The existing `DefaultMethodContract` verifies methods already declared in SPI. It cannot detect a callback or entire interface that has never been declared. The frozen baseline and validation below close that inventory blind spot; whole-module CI wiring still belongs to [boundary enforcement](../../wayfinder-issues/15-whole-module-migration-boundary-enforcement.md) and the [complete test matrix](../../wayfinder-issues/40-complete-driver-contract-and-native-integration-matrix.md).

## Newly specified implementation work

Native `Cache` is exposed by the existing factory/callback APIs. Its `addCacheServer/getCacheServers`, `createGatewaySenderFactory`, `createGatewayReceiverFactory`, `createAsyncEventQueueFactory`, lookup and conflict-resolver methods make server/WAN/queue contracts reachable. The source-only absence of a Spring call to those methods does not remove the application contract.

- [Cache-server lifecycle and application callbacks SPI](../../wayfinder-issues/42-cache-server-lifecycle-and-callbacks-spi.md): server lifecycle/settings, subscription configuration, load probes/metrics, client sessions and interest listeners/events.
- [WAN and asynchronous queue contracts and callbacks SPI](../../wayfinder-issues/43-wan-and-async-queue-contracts-spi.md): sender/receiver/queue factories and lifecycle, filters, asynchronous listeners, conflict resolution, failure listeners, routing/order policies and event payloads.

Both block [Distributed-system, membership and runtime services SPI](../../wayfinder-issues/23-distributed-system-membership-and-runtime-services-spi.md), and therefore cache assembly and final acceptance. Existing owners retain partition/resolver callbacks, rebalance/redundancy results, security and serialization families; those discoveries do not require duplicate tickets. Named pool creation remains a foundation under the complete pool owner, not a claim of full pool coverage.

## Extraction boundaries

The extractor reads class files without loading or initializing native classes. It combines the source audit with compiled Spring main signatures and constant-pool references, then follows native signatures, generic bounds, exceptions and supertypes. It includes public/protected members of public native roots conservatively, inherited JDK instance contracts, explicit callback/result subtype families, and an explicit OQL `Struct` dynamic-result root. Annotation enum defaults and native constant values are retained. It does not claim arbitrary reflective strings or arbitrary application classes can be inferred from bytecode.

Native internal implementation types are **boundary replacement obligations**, not a request to reproduce the engine's private implementation API. Every such type has an explicit rationale in `internal_boundaries` and an open owning row. Directly used internal members are retained, while public contracts inherited by a public native facade are inventoried under that facade. For example, `RegionFactory.getCache()` needs an SPI cache handle; the native `InternalCache` implementation must remain inside the driver. The actually exposed `ConfigProperty` payload is an explicit exception to the bounded traversal and gets its full public shape. New exposed internal payloads fail extraction until classified. None of these rows is treated as complete or excluded.

The initial root selection is deliberately conservative: importing a public native class brings its public surface into review. Before treating an incidental helper as a new product feature, its owner must inspect the attached signature/source path and map the required behavior to the typed contract. Narrowing a required application capability requires the separate scope process; deleting an inconvenient row is not a valid migration.

This baseline is for the pinned 10.4 artifact. The full release/driver matrix, semantic behavior tests and ABI gate are separate open work. Native exception constructors and repeated inherited/value members make raw row counts unsuitable as effort estimates. Recorded existing test source links are evidence of earlier coverage, not a claim those tests were rerun here.

## Updating and validating

1. Compile Spring Data main after source changes using the project's guarded Spring Test init script and session-only Gradle approval when needed. The extractor rejects missing/stale top-level class files and orphan compiled classes. Clean stale build output through Gradle when deleting/renaming classes.
2. Run `python3 wayfinder-impl/audits/scan_native_references.py` to refresh lexical evidence.
3. Update `coverage-policy.json` with exact SPI/native/mock/test mappings and owners. Preserve frozen requirements. Newly discovered requirements must be added to `native-api-baseline.json` with their source/reachability evidence; scope changes are reviewed separately.
4. Run `python3 wayfinder-impl/coverage/build_api_coverage.py` and review the generated diff. It reads the pinned artifacts in Gradle's cache, plus resolved Shiro 2.2.1 for `ResourcePermission` inheritance and the installed JDK's supertypes. `--native-cache` and `--java-home` allow explicit locations.
5. Run `python3 wayfinder-impl/coverage/build_api_coverage.py --check` and `python3 wayfinder-impl/coverage/validate_coverage.py --javap`. The latter also works without `--javap` when only stored source/ownership/fingerprint validation is needed.
6. Keep the originating implementation ticket open until its required rows have complete behavior/test evidence. SPI presence alone does not complete consumer migration. Record commands/results and exact native version in that ticket's Answer.

Validation on 2026-10-21: deterministic regeneration passed; every direct member resolved; all imported symbols/source references, IDs, hashes and owners validated. An independent `javap -public -s` comparison matched **148 declarations across 13 representative native types**. Negative controls rejected a duplicate row and a deleted `CacheListener.afterCreate` requirement with the SPI untouched. Existing compiled main classes were current relative to all production sources; no Gradle compilation or runtime test suite was rerun for this inventory-only ticket.

## Named-pool foundation update

[Named driver pool creation and ownership](../../wayfinder-issues/13-named-driver-pool-creation-and-ownership.md) adds eight typed operational defaults and maps 12 native pool creation/lookup/configuration/lifecycle rows to concrete implementations and tests. Those rows advance from missing to partial; full pool contracts, typed exception categories and legacy Spring adoption retain their open owners. Existing-SPI validation now checks current source declarations rather than a hard-coded default count.

The required baseline remains 3,851 rows across 292 types, with unchanged native signature/source exposure. Source inventory is now 357 main, 254 unit-test and 150 integration-test Java files; native reference counts remain 186/122/128. Scoped verification covers 21 main and 23 test files. This foundation does not establish full-module compliance.

Validation for this update: 393 tests passed across SPI/core/contracts/mock/native and both focused Spring runtimes; Spring Data main/unit/integration sources and Spring Test main/unit sources compiled. Coverage regeneration/check and independent `javap` validation passed. See the owning ticket for commands, precise results and native artifact/version limitations.


## Exception foundation update

[Driver exception semantics and translation](../../wayfinder-issues/16-driver-exception-semantics-and-translation.md) adds typed execution categories and native/Spring mapping. Its [exception evidence](../investigations/exception-semantics-and-translation.md) records 69 native exception types and 18 remaining caller files. All 403 exception rows stay required: 69 type rows are partial and 334 remaining constructor/member/payload rows have open owners. The generator now honors per-row consumer-owner overrides. No exception payload or complete subsystem is declared implemented from a scalar failure category.

The required inventory remains 3,851 rows / 292 types: 3,682 missing and 169 partial. Current Spring exposures decrease to 588 as native exception constructor signatures are replaced by Throwable constructors. Current imported symbols/source references are 144/624; historical requirements are frozen. Production native files decrease to 182; unit/integration native files remain 122/128. The scoped slice is 26 main / 24 test files.

Validation: 409 behavior tests passed across SPI/core/contracts/mock/native and both focused Spring runtimes; full Spring Data main/unit/integration and Spring Test main/unit compilation passed. Boundary baseline pruning, deterministic generation/check and independent javap validation passed. Exact commands, counts and limitations are in the owning ticket.

## Region callback foundation update

[Complete region events, listeners and writers](../../wayfinder-issues/17-complete-region-events-listeners-and-writers.md) delivers full listener/writer/declarable contracts, specialized membership/role listeners, lazy event payloads and operation vocabulary, native registration/lifecycle adaptation and local mock dispatch. [Implementation and native evidence](../investigations/region-callback-contracts.md) records the exact behavior, discovered native wrapper/role-payload details and limits.

All 291 callback-owned rows now have implementation evidence: 145 implemented leaf/value rows and 146 partial rows retaining reachable-contract/adoption obligations. Another 33 previously missing registration, identity and metadata rows now have partial evidence under their existing owners. Overall: 3,369 missing / 337 partial / 145 implemented, with 3,851 frozen rows unchanged. Spring exposures/imports/source references remain 588/144/624, and native files remain 182/122/128. No Spring consumer cutover is claimed from this foundation.

The SPI comparison now contains 42 default methods/hooks and 29 callback declarations. Real native peer/locator/client/server fixtures cover remote entry serialization, membership/role changes, graceful departure, process crash and the durable region-live marker. Timestamp payload shape is tested separately; actual WAN conflict delivery remains owned by the WAN/queue ticket.

## Region-operation foundation update

[Complete region operations and metadata SPI](../../wayfinder-issues/18-complete-region-operations-and-metadata-spi.md) adds 56 defaults (98 total), live typed entry/statistics/service views, routing metadata and exception payloads. The [region-operation evidence](../investigations/region-operations-and-metadata.md) records exact native behavior, shared/native tests and downstream ownership. Native bulk/conditional/Map operations delegate directly; the mock implements local behavior and explicitly rejects server operations. `closeRegion()` maps native close without triggering Spring inferred bean ownership.

The frozen inventory remains 3,851 rows / 292 types. The region-operation owner has 127 rows: 94 implemented and 33 partial with reachable-contract or consumer obligations. Current source exposure counts remain unchanged. Snapshot (30 rows) and subscription (24 rows) obligations have explicit new owners and acceptance blockers; exact row counts are available from the generated matrix. Configuration, query, lock and disk methods retain their subsystem owners. Neither partial rows nor reassigned rows are claims of completed consumer migration.

The shared contract probes, real server/proxy fixture, native configured hierarchy/statistics tests and Spring ownership regression tests validate this foundation. See the owning ticket for final full-validation commands and counts.

## Typed region-configuration foundation

[Typed region creation and attribute access](../../wayfinder-issues/46-typed-region-creation-and-attribute-access.md) adds six default operations, taking the SPI default count to 104. Immutable configuration records explicit/unset/null state; 25 typed attribute getters and 12 mutator operations expose native scalar/callback settings. Peer/client/subregion creation, constraint validation, callback identity, duplicate registrations, native statistics, mutation and real server-region routing have executable evidence. The local mock implements hierarchy/constraints and explicitly rejects unavailable configured effects.

The foundation owns 121 rows: 37 implemented and 84 partial. Partial factory/reachable-type rows retain full configuration and Spring consumer obligations. The parent configuration task remains open and blocked by its children: expiration/eviction/compression (171 rows), partition/membership/subscription values (181), and remaining enum/legacy/configuration DTO contracts (276). Two complete attribute-type rows stay with parent acceptance. Security operation contexts (47 rows) move to the existing security owner. All 798 original obligations are retained; none is removed or excluded.

The frozen inventory remains 3,851 rows / 292 types, and Spring native-reference counts remain unchanged. See [configuration evidence](../investigations/typed-region-configuration.md) for validation and limitations. Normal migration-boundary success does not establish strict native-free compliance.

## Complete named-pool configuration update

[Named pool configuration and socket callbacks](../../wayfinder-issues/50-named-pool-configuration-and-socket-callbacks.md) adds 25 typed optional settings, immutable merging, effective provider inspection and socket callbacks. Its [evidence](../investigations/named-pool-configuration-and-sockets.md) records 476 passing tests, full compilation, normal boundary verification and a real server/custom-socket fixture. Thread-local requests are explicitly unsupported; provider defaults remain dynamic.

This child owns 87 rows (84 partial pending Spring adoption/default constants, three implemented callback mappings). Default-cache assembly and durable/runtime lifecycle retain 29 and 26 rows respectively, and block the original parent and all of its consumers. Pool query operations retain their query owner. The required baseline and Spring native exposures are unchanged; default methods increase to 105, each with a behavioral probe and native mapping. No native-free or durable replay claim follows from a successful named-pool configuration test.
