# SPI gap closure implementation plan

Created 2026-10-21 from the [native/SPI gap audit](audits/native-spi-gap-audit.md), under the existing [implementation map](map.md) and [signed-off spec](../wayfinder/technical-spec.md). This is an executable issue/dependency index, not a claim that the gaps are closed. Ticket files in `wayfinder-issues/` are the source of truth for status, assignment, blockers and resolution.

The [member-level coverage baseline](coverage/README.md) now records required inherited APIs, callbacks and transitive payloads, with stable IDs and implementation owners. Its frozen discovery evidence survives removal of native imports. The [client-only amendment](client-only-scope.md) governs required scope; task 60 adds explicit scope classifications and task 64 owns remaining unresolved decisions under cleanup task 59.

The audit baseline is 186 production, 122 unit-test and 128 integration-test Java files with noncomment native references. The original allowlist validated 20 production and 21 test files; the named-pool foundation expands it to 21 production and 23 test files. Completion requires full retained client API behavior preservation **and** zero native dependencies/references in the required module boundaries.

## Next work

**Next: [63 — Retire Spring server configuration surfaces](../wayfinder-issues/63-retire-spring-server-configuration-surfaces.md)**, the next child of [59 — Remove server-only SPI and reconcile client scope](../wayfinder-issues/59-remove-server-only-spi-and-reconcile-client-scope.md). Tasks 60–62 are resolved; tasks 63/64 keep the parent open. Original downstream tasks continue to wait for 59. Tickets 42 and 43 are superseded, not implemented; ticket 48 covers client subscription/interest configuration.

The user authorized the [client-only scope correction](client-only-scope.md) on 2026-10-22. Current classification preserves all frozen IDs: **362 required-client**, **625 excluded-server**, **2,864 unresolved**. Public peer creation/callbacks and server configuration are removed; Spring cleanup and final reconciliation remain. There are **36 open tasks**, **26 resolved tasks** and **2 superseded tasks** (64 total). The five cleanup children split existing obligations; they do not add product scope.

Ticket metadata is authoritative. Claim an eligible task only when all `Blocked by:` tasks are resolved. After ticket 59, use ascending order among eligible tasks unless the user directs otherwise. Do not automatically invoke skills or parallel agents. Scoped boundary passes do not establish final native-free compliance.

## Shared completion requirements

Task 63 has configuration, function-registration, peer-function and selected property retirement slices (including server placement/endpoint settings and orphan mappings); its [ledger](investigations/client-only-spring-surfaces.md) records breaking changes, retained tests and the remaining property, function fixture placement/audit and inherited attributes work. It remains the next task; counts and parent blockers are unchanged.

Every linked implementation ticket, including the existing named-pool foundation, must satisfy these requirements:

- Follow the signed-off architecture: typed SPI contracts, native adaptation inside the native driver, injected collaborators, no native unwrap/permanent dual API, no new static mutable state/singletons or reference-count registry. Preserve retained client method semantics and ABI rules; new driver operations default to typed unsupported as specified.
- Update stable coverage-matrix rows with native member/type, Spring caller or public exposure, SPI equivalent, provider behavior, tests and owning ticket. Account for client-applicable inherited APIs, callbacks and reachable event/result/configuration types. An absent required-client method is missing implementation. Server-only rows require an explicit scope exclusion and evidence under ticket 59; native reachability alone does not mandate implementation.
- Native support must delegate real behavior. Mock support must state what it implements locally and reject unavailable operations explicitly. Never accept a required configuration silently, discard an event payload, replace native semantics with an unverified approximation or make runtime tests pass with canned remote results.
- For each changed public consumer group, migrate its callers, Java/annotation/XML wiring, schema contracts, exception translation and affected tests together. A foundational SPI ticket may leave consumers native only when their matrix rows name the downstream cutover owner. Preserve full-source compilation during staging; if the group is too large, create bounded blocking children and leave the parent open until all obligations are complete.
- Add meaningful shared behavior/unsupported tests and native-specific integration tests for network, durability, authentication, serialization or persistence semantics. Record exact native artifacts. Run relevant driver/core/contract and consumer checks plus compilation of affected source sets; keep Spring Test compile coupling visible. Existing scoped pass counts are not proof of full migration.
- Run `verifyMigrationBoundary` during migration and prune resolved entries with `pruneMigrationBoundaryBaseline` before completing their owning ticket. The normal `check` tasks also run this whole-module gate. `verifyNativeFreeBoundary` must pass at final dependency removal. No blanket exclusions, deletion of retained client tests, baseline expansion or API removal to manufacture compliance. The approved client-only correction authorizes evidence-backed server API/test retirement under ticket 59; it cannot justify dropping difficult client behavior.
- Preserve user changes in Gradle/settings and other work. If required, use the guarded temporary Spring Test init script and session-only escalation without a persistent prefix. Do not commit, publish or merge merely because a ticket is resolved.
- On resolution, append an Answer with source/coverage rows, behavior, commands/results, limitations and any blocking follow-ups; update the map's completed-decision index. Do not close a contract family while required child work remains.

## Implementation tickets and dependencies

This index reflects ticket metadata after the scope correction. Prerequisites are local issue IDs; resolved foundations remain traceable. Superseded tasks do not represent delivered functionality.

| Ticket | Status | Prerequisites |
| --- | --- | --- |
| [14 — Required native API coverage matrix](../wayfinder-issues/14-required-native-api-coverage-matrix.md) | resolved | 12 |
| [15 — Whole-module migration boundary enforcement](../wayfinder-issues/15-whole-module-migration-boundary-enforcement.md) | resolved | 14 |
| [16 — Driver exception semantics and translation](../wayfinder-issues/16-driver-exception-semantics-and-translation.md) | resolved | 14 |
| [17 — Complete region events, listeners and writers](../wayfinder-issues/17-complete-region-events-listeners-and-writers.md) | resolved | 14, 16 |
| [18 — Complete region operations and metadata SPI](../wayfinder-issues/18-complete-region-operations-and-metadata-spi.md) | resolved | 14, 16, 17 |
| [19 — Region configuration, enums, expiration and eviction](../wayfinder-issues/19-region-configuration-enums-expiration-and-eviction.md) | open | 14, 17, 18, 46, 47, 48, 49, 59 |
| [20 — Complete pool, default-cache and durable SPI](../wayfinder-issues/20-complete-pool-default-cache-and-durable-spi.md) | open | 13, 14, 16, 50, 51, 52, 59 |
| [21 — Query, results and index SPI](../wayfinder-issues/21-query-results-and-index-spi.md) | open | 14, 16, 18, 20, 59 |
| [22 — Continuous query SPI](../wayfinder-issues/22-continuous-query-spi.md) | open | 14, 17, 20, 21, 45, 59 |
| [23 — Client runtime identity and connectivity SPI](../wayfinder-issues/23-distributed-system-membership-and-runtime-services-spi.md) | open | 14, 16, 18, 59 |
| [24 — Security and authenticated views SPI](../wayfinder-issues/24-security-and-authenticated-views-spi.md) | open | 14, 16, 20, 21, 23, 59 |
| [25 — Client function execution and results SPI](../wayfinder-issues/25-function-registration-execution-and-results-spi.md) | open | 14, 16, 18, 20, 23, 24, 59 |
| [26 — PDX, serialization and JSON SPI](../wayfinder-issues/26-pdx-serialization-and-json-spi.md) | open | 14, 16, 53, 54, 55, 56, 57, 58, 59 |
| [27 — Disk-store and resource configuration SPI](../wayfinder-issues/27-disk-store-and-resource-configuration-spi.md) | open | 14, 16, 19, 59 |
| [28 — Transaction lifecycle and callbacks SPI](../wayfinder-issues/28-transaction-lifecycle-and-callbacks-spi.md) | resolved | 14, 16, 17, 18 |
| [29 — Complete cache configuration and creation assembly](../wayfinder-issues/29-complete-cache-configuration-and-creation-assembly.md) | open | 17, 19, 20, 23, 24, 26, 27, 28, 44, 45, 59 |
| [30 — Region factory and configuration migration](../wayfinder-issues/30-region-factory-and-configuration-migration.md) | open | 18, 19, 20, 24, 26, 27, 29, 59 |
| [31 — Template, repository and mapping migration](../wayfinder-issues/31-template-repository-and-mapping-migration.md) | open | 16, 18, 21, 26, 30, 44, 59 |
| [32 — Spring client function consumer migration](../wayfinder-issues/32-spring-function-consumer-migration.md) | open | 25, 59 |
| [33 — Spring continuous-query consumer migration](../wayfinder-issues/33-spring-continuous-query-consumer-migration.md) | open | 22, 59 |
| [34 — Spring transaction consumer migration](../wayfinder-issues/34-spring-transaction-consumer-migration.md) | open | 28, 29, 59 |
| [35 — Cache/pool entry points and resolver cutover](../wayfinder-issues/35-cache-pool-entrypoints-and-resolver-cutover.md) | open | 20, 29, 30, 31, 32, 33, 34, 59 |
| [36 — Residual native utilities and support cleanup](../wayfinder-issues/36-residual-native-utilities-and-support-cleanup.md) | open | 30, 31, 32, 33, 34, 35, 59 |
| [37 — Native-free Spring tests and helper decoupling](../wayfinder-issues/37-native-free-spring-test-fixtures-and-helper-decoupling.md) | open | 36, 59 |
| [38 — Whole-module native dependency removal](../wayfinder-issues/38-whole-module-native-dependency-removal.md) | open | 15, 36, 37, 59 |
| [39 — SPI ABI compatibility and stamp enforcement](../wayfinder-issues/39-spi-abi-compatibility-and-stamp-enforcement.md) | resolved | 14 |
| [40 — Complete driver contract and native integration matrix](../wayfinder-issues/40-complete-driver-contract-and-native-integration-matrix.md) | open | 38, 39, 59 |
| [41 — Final SPI migration spec-conformance audit](../wayfinder-issues/41-final-spi-migration-spec-conformance-audit.md) | open | 40, 59 |
| [42 — Cache-server lifecycle and application callbacks SPI](../wayfinder-issues/42-cache-server-lifecycle-and-callbacks-spi.md) | superseded | — |
| [43 — WAN and asynchronous queue contracts and callbacks SPI](../wayfinder-issues/43-wan-and-async-queue-contracts-spi.md) | superseded | — |
| [44 — Client-local snapshot services SPI](../wayfinder-issues/44-region-and-cache-snapshot-services-spi.md) | open | 18, 26, 59 |
| [45 — Region interest registration and subscription SPI](../wayfinder-issues/45-region-interest-registration-and-subscription-spi.md) | open | 18, 19, 20, 59 |
| [46 — Typed region creation and attribute access](../wayfinder-issues/46-typed-region-creation-and-attribute-access.md) | resolved | 14, 17, 18 |
| [47 — Expiration, eviction and compression SPI](../wayfinder-issues/47-expiration-eviction-and-compression-spi.md) | open | 46, 59 |
| [48 — Client subscription and interest policy configuration SPI](../wayfinder-issues/48-partition-and-membership-configuration-spi.md) | open | 46, 59 |
| [49 — Client configuration values and enum migration](../wayfinder-issues/49-legacy-region-configuration-values-and-enums.md) | open | 46, 47, 48, 59 |
| [50 — Named pool configuration and socket callbacks](../wayfinder-issues/50-named-pool-configuration-and-socket-callbacks.md) | resolved | 13, 14, 16 |
| [51 — Default client-cache pool assembly and selection](../wayfinder-issues/51-default-client-cache-pool-assembly-and-selection.md) | open | 50, 59 |
| [52 — Durable client readiness and pool runtime lifecycle](../wayfinder-issues/52-durable-client-readiness-and-pool-runtime-lifecycle.md) | open | 50, 51, 59 |
| [53 — PDX values and instance factories](../wayfinder-issues/53-pdx-values-and-instance-factories.md) | resolved | 14, 16 |
| [54 — PDX callbacks and cache configuration](../wayfinder-issues/54-pdx-callbacks-and-cache-configuration.md) | open | 53, 59 |
| [55 — Binary serialization and registration](../wayfinder-issues/55-binary-serialization-and-registration.md) | open | 14, 16, 59 |
| [56 — JSON documents and PDX conversion](../wayfinder-issues/56-json-documents-and-pdx-conversion.md) | open | 53, 59 |
| [57 — Reflection-based PDX serialization](../wayfinder-issues/57-reflection-based-pdx-serialization.md) | open | 54, 59 |
| [58 — Serialization value boundary adaptation](../wayfinder-issues/58-serialization-value-boundary-adaptation.md) | open | 53, 54, 55, 56, 57, 59 |
| [59 — Remove server-only SPI and reconcile client scope](../wayfinder-issues/59-remove-server-only-spi-and-reconcile-client-scope.md) | open | 14, 15, 17, 18, 28, 39, 46, 60, 61, 62, 63, 64 |
| [60 — Add explicit client scope to the coverage inventory](../wayfinder-issues/60-client-scope-coverage-classification.md) | resolved | 14, 15 |
| [61 — Remove peer cache and region creation](../wayfinder-issues/61-remove-peer-cache-and-region-creation.md) | resolved | 60 |
| [62 — Remove peer callbacks and server configuration](../wayfinder-issues/62-remove-peer-callbacks-and-server-configuration.md) | resolved | 61 |
| [63 — Retire Spring server configuration surfaces](../wayfinder-issues/63-retire-spring-server-configuration-surfaces.md) | open | 61, 62 |
| [64 — Complete client scope reconciliation](../wayfinder-issues/64-complete-client-scope-reconciliation.md) | open | 60, 61, 62, 63 |

## Audit finding ownership

Every audit finding has implementation and final acceptance owners. The member-level matrix created by [Required native API coverage matrix](../wayfinder-issues/14-required-native-api-coverage-matrix.md) refines these assignments; it must create owned follow-ups for newly discovered surfaces rather than putting precise gaps back into an unspecified backlog.

| Audit gap | Work owners | Closure evidence |
| --- | --- | --- |
| G1 — Native build dependencies | [Whole-module migration boundary enforcement](../wayfinder-issues/15-whole-module-migration-boundary-enforcement.md); [Cache/pool entry points and resolver cutover](../wayfinder-issues/35-cache-pool-entrypoints-and-resolver-cutover.md); [Whole-module native dependency removal](../wayfinder-issues/38-whole-module-native-dependency-removal.md) | Whole main/unit-test dependency graph and jar/class scans reject native artifacts; clean full compilation passes. |
| G2 — Allowlist-only enforcement | [Whole-module migration boundary enforcement](../wayfinder-issues/15-whole-module-migration-boundary-enforcement.md); [Native-free Spring tests and helper decoupling](../wayfinder-issues/37-native-free-spring-test-fixtures-and-helper-decoupling.md); [Whole-module native dependency removal](../wayfinder-issues/38-whole-module-native-dependency-removal.md) | All production/unit/integration source boundaries checked; no transitional exemption remains at acceptance. |
| G3 — Missing subsystem contracts | [Named driver pool creation and ownership](../wayfinder-issues/13-named-driver-pool-creation-and-ownership.md); [Driver exception semantics and translation](../wayfinder-issues/16-driver-exception-semantics-and-translation.md); [Complete region events, listeners and writers](../wayfinder-issues/17-complete-region-events-listeners-and-writers.md); [Complete region operations and metadata SPI](../wayfinder-issues/18-complete-region-operations-and-metadata-spi.md); [Region configuration, enums, expiration and eviction](../wayfinder-issues/19-region-configuration-enums-expiration-and-eviction.md); [Complete pool, default-cache and durable SPI](../wayfinder-issues/20-complete-pool-default-cache-and-durable-spi.md); [Query, results and index SPI](../wayfinder-issues/21-query-results-and-index-spi.md); [Continuous query SPI](../wayfinder-issues/22-continuous-query-spi.md); [Distributed-system, membership and runtime services SPI](../wayfinder-issues/23-distributed-system-membership-and-runtime-services-spi.md); [Security and authenticated views SPI](../wayfinder-issues/24-security-and-authenticated-views-spi.md); [Function registration, execution and results SPI](../wayfinder-issues/25-function-registration-execution-and-results-spi.md); [PDX, serialization and JSON SPI](../wayfinder-issues/26-pdx-serialization-and-json-spi.md); [Disk-store and resource configuration SPI](../wayfinder-issues/27-disk-store-and-resource-configuration-spi.md); [Transaction lifecycle and callbacks SPI](../wayfinder-issues/28-transaction-lifecycle-and-callbacks-spi.md); [Complete cache configuration and creation assembly](../wayfinder-issues/29-complete-cache-configuration-and-creation-assembly.md); [Server-only cleanup and scope reconciliation](../wayfinder-issues/59-remove-server-only-spi-and-reconcile-client-scope.md) | Every required contract row is implemented and consumed by [Region factory and configuration migration](../wayfinder-issues/30-region-factory-and-configuration-migration.md); [Template, repository and mapping migration](../wayfinder-issues/31-template-repository-and-mapping-migration.md); [Spring function consumer migration](../wayfinder-issues/32-spring-function-consumer-migration.md); [Spring continuous-query consumer migration](../wayfinder-issues/33-spring-continuous-query-consumer-migration.md); [Spring transaction consumer migration](../wayfinder-issues/34-spring-transaction-consumer-migration.md); [Cache/pool entry points and resolver cutover](../wayfinder-issues/35-cache-pool-entrypoints-and-resolver-cutover.md); [Residual native utilities and support cleanup](../wayfinder-issues/36-residual-native-utilities-and-support-cleanup.md). |
| G4 — Application callbacks/reachable types | [Required native API coverage matrix](../wayfinder-issues/14-required-native-api-coverage-matrix.md); [Complete region events, listeners and writers](../wayfinder-issues/17-complete-region-events-listeners-and-writers.md); [Region configuration, enums, expiration and eviction](../wayfinder-issues/19-region-configuration-enums-expiration-and-eviction.md); [Complete pool, default-cache and durable SPI](../wayfinder-issues/20-complete-pool-default-cache-and-durable-spi.md); [Continuous query SPI](../wayfinder-issues/22-continuous-query-spi.md); [Security and authenticated views SPI](../wayfinder-issues/24-security-and-authenticated-views-spi.md); [Function registration, execution and results SPI](../wayfinder-issues/25-function-registration-execution-and-results-spi.md); [PDX, serialization and JSON SPI](../wayfinder-issues/26-pdx-serialization-and-json-spi.md); [Transaction lifecycle and callbacks SPI](../wayfinder-issues/28-transaction-lifecycle-and-callbacks-spi.md); [Server-only cleanup and scope reconciliation](../wayfinder-issues/59-remove-server-only-spi-and-reconcile-client-scope.md) | Full callback method and event/result payload coverage, including inherited/application-only APIs; validated by [Complete driver contract and native integration matrix](../wayfinder-issues/40-complete-driver-contract-and-native-integration-matrix.md). |
| G5 — Native annotations/XML/public entry points | [Region configuration, enums, expiration and eviction](../wayfinder-issues/19-region-configuration-enums-expiration-and-eviction.md); [Region factory and configuration migration](../wayfinder-issues/30-region-factory-and-configuration-migration.md); [Template, repository and mapping migration](../wayfinder-issues/31-template-repository-and-mapping-migration.md); [Spring function consumer migration](../wayfinder-issues/32-spring-function-consumer-migration.md); [Spring continuous-query consumer migration](../wayfinder-issues/33-spring-continuous-query-consumer-migration.md); [Spring transaction consumer migration](../wayfinder-issues/34-spring-transaction-consumer-migration.md); [Cache/pool entry points and resolver cutover](../wayfinder-issues/35-cache-pool-entrypoints-and-resolver-cutover.md); [Residual native utilities and support cleanup](../wayfinder-issues/36-residual-native-utilities-and-support-cleanup.md) | Native-free annotation defaults, public signatures, factory products and schema exports; real Java/namespace contexts pass. |
| G6 — Native internals/globals/exception translation | [Whole-module migration boundary enforcement](../wayfinder-issues/15-whole-module-migration-boundary-enforcement.md); [Driver exception semantics and translation](../wayfinder-issues/16-driver-exception-semantics-and-translation.md); [Distributed-system, membership and runtime services SPI](../wayfinder-issues/23-distributed-system-membership-and-runtime-services-spi.md); [Security and authenticated views SPI](../wayfinder-issues/24-security-and-authenticated-views-spi.md); [Complete cache configuration and creation assembly](../wayfinder-issues/29-complete-cache-configuration-and-creation-assembly.md); [Cache/pool entry points and resolver cutover](../wayfinder-issues/35-cache-pool-entrypoints-and-resolver-cutover.md); [Residual native utilities and support cleanup](../wayfinder-issues/36-residual-native-utilities-and-support-cleanup.md) | Spring obtains typed injected collaborators; no native runtime reflection or exception dispatch remains. |
| G7 — Completeness/ABI/matrix verification | [Required native API coverage matrix](../wayfinder-issues/14-required-native-api-coverage-matrix.md); [Whole-module migration boundary enforcement](../wayfinder-issues/15-whole-module-migration-boundary-enforcement.md); [SPI ABI compatibility and stamp enforcement](../wayfinder-issues/39-spi-abi-compatibility-and-stamp-enforcement.md); [Complete driver contract and native integration matrix](../wayfinder-issues/40-complete-driver-contract-and-native-integration-matrix.md) | Required-surface coverage, ABI and architecture gates, complete shared/native suite evidence. |
| G8 — Native Spring Test helper coupling | [Cache/pool entry points and resolver cutover](../wayfinder-issues/35-cache-pool-entrypoints-and-resolver-cutover.md); [Native-free Spring tests and helper decoupling](../wayfinder-issues/37-native-free-spring-test-fixtures-and-helper-decoupling.md); [Whole-module native dependency removal](../wayfinder-issues/38-whole-module-native-dependency-removal.md) | Spring Data tests use SPI/mock; the separate Spring Test module remains compilable within its native-dependency exception. |

[Final SPI migration spec-conformance audit](../wayfinder-issues/41-final-spi-migration-spec-conformance-audit.md) is the final independent acceptance owner for **all eight** findings.

## Remaining boundaries

- This plan implements the retained Spring client surface, including its transitive public and callback types. Peer/server hosting and administration are excluded by the approved scope amendment. It does not silently expand to mirror every unrelated GemFire SDK API. The coverage inventory must resolve ambiguous reachability with evidence.
- Native GemFire globals may pass through inside a driver; adding a driver-owned global registry remains forbidden. Mock independence is not a shared native isolation guarantee.
- The separate Spring Test module's native migration remains out of scope. Its compilation and Spring Data's removal of native helper usage are explicitly owned above.
- Initial supported release selection, official support/EOL documentation and publication/merge scheduling remain release planning. Technical acceptance does not authorize those actions or establish an unverified support policy.

## Region-operation decomposition

- [Region and cache snapshot services SPI](../wayfinder-issues/44-region-and-cache-snapshot-services-spi.md), blocked by region operations and PDX/serialization, blocks cache assembly and template/repository migration.
- [Region interest registration and subscription SPI](../wayfinder-issues/45-region-interest-registration-and-subscription-spi.md), blocked by region operations, configuration and pool completion, blocks cache assembly and continuous-query SPI.

These preserve retained client Region requirements. Server-only snapshots and distributed locks receive explicit exclusions in ticket 59. Client configuration, query and disk operations remain with their subsystem owners; the coverage matrix records exact per-member ownership.

## Region-configuration decomposition

The parent configuration task remains open and blocks its original consumers. Its implementation children are:

- [Typed region creation and attribute access](../wayfinder-issues/46-typed-region-creation-and-attribute-access.md).
- [Expiration, eviction and compression SPI](../wayfinder-issues/47-expiration-eviction-and-compression-spi.md).
- [Client subscription and interest policy configuration SPI](../wayfinder-issues/48-partition-and-membership-configuration-spi.md).
- [Legacy region configuration values and enums](../wayfinder-issues/49-legacy-region-configuration-values-and-enums.md).

## Pool-configuration decomposition

The complete pool parent retains all original downstream blockers. Its implementation children are:

- [Named pool configuration and socket callbacks](../wayfinder-issues/50-named-pool-configuration-and-socket-callbacks.md), dependent on named pool creation, coverage inventory and exception semantics.
- [Default client-cache pool assembly and selection](../wayfinder-issues/51-default-client-cache-pool-assembly-and-selection.md), dependent on named pool configuration.
- [Durable client readiness and pool runtime lifecycle](../wayfinder-issues/52-durable-client-readiness-and-pool-runtime-lifecycle.md), dependent on named configuration and default-cache assembly.

The coverage matrix reassigns exact ClientCacheFactory setters/default lookup and ClientCache runtime/durable methods to these children, while full cache assembly remains open. Pool query-service operations retain the existing query owner. Historical discovery evidence is retained; ticket 59 classifies required-client versus excluded server rows.

## Serialization decomposition

The complete serialization parent retains every original downstream blocker. Its children are:

- [PDX values and instance factories](../wayfinder-issues/53-pdx-values-and-instance-factories.md), dependent on coverage and exception foundations.
- [PDX callbacks and cache configuration](../wayfinder-issues/54-pdx-callbacks-and-cache-configuration.md), dependent on PDX values.
- [Binary serialization and registration](../wayfinder-issues/55-binary-serialization-and-registration.md), dependent on coverage and exception foundations.
- [JSON documents and PDX conversion](../wayfinder-issues/56-json-documents-and-pdx-conversion.md), dependent on PDX values.
- [Reflection-based PDX serialization](../wayfinder-issues/57-reflection-based-pdx-serialization.md), dependent on PDX callbacks/configuration.
- [Serialization value boundary adaptation](../wayfinder-issues/58-serialization-value-boundary-adaptation.md), dependent on all five serialization foundations.

The 378 original serialization rows remain discovery evidence assigned to bounded children, subject to ticket 59 client-scope classification. Eight inherited cache creation rows and 28 cache PDX configuration rows move from cache assembly to their serialization foundations; cache assembly continues to wait for the complete parent. The boundary-adaptation child owns integration acceptance for PDX value rows and coordinates later query/function value paths with their existing owners. Retained client wire-format requirements and consumer blockers remain mandatory; server-only incidental requirements receive explicit scope dispositions.
