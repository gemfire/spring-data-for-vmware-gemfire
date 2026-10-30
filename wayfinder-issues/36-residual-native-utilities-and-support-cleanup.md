Type: task
Status: open
Blocked by: 30, 31, 32, 33, 34, 35, 59

# Residual native utilities and support cleanup

## Client-only scope — 2026-10-22

Apply the [approved client-only boundary](../wayfinder-impl/client-only-scope.md). [Cleanup ticket 59](59-remove-server-only-spi-and-reconcile-client-scope.md) must complete first. Full/complete coverage in this ticket means every retained client requirement; historical native reachability alone does not require peer/server APIs. Preserve all retained client tests and native-free boundaries.

## Question

Eliminate remaining native production references and obsolete migration helpers after consumer cutover.

Audit coverage: **G3, G5, G6** in the [native/SPI gap audit](../wayfinder-impl/audits/native-spi-gap-audit.md). Apply the [shared completion requirements](../wayfinder-impl/spi-gap-closure-plan.md#shared-completion-requirements) and the signed-off [technical spec](../wayfinder/technical-spec.md).

## Required implementation

Prerequisites: [Region factory and configuration migration](30-region-factory-and-configuration-migration.md); [Template, repository and mapping migration](31-template-repository-and-mapping-migration.md); [Spring function consumer migration](32-spring-function-consumer-migration.md); [Spring continuous-query consumer migration](33-spring-continuous-query-consumer-migration.md); [Spring transaction consumer migration](34-spring-transaction-consumer-migration.md); [Cache/pool entry points and resolver cutover](35-cache-pool-entrypoints-and-resolver-cutover.md).

- Use the full reference/coverage inventories to migrate remaining util/root/support/domain/server/schema/converter/configuration callers, not only files in the former allowlist.
- Remove remaining GemfireCacheUtils native dispatch, runtime native reflection/version branches and BeanFactory locator/global-discovery dependencies; route through injected SPI collaborators.
- Finish annotation defaults, native-backed wrapper enums, resource/schema exports and initializer leftovers. Clean historical peer/server documentation (including the empty server package) and replace endpoint/version constants and feature discovery with client SPI/core equivalents; task 63's final surface audit records these retained client uses. Preserve requirements via matrix rows rather than deleting APIs solely to reach zero imports.
- Delete temporary native compatibility code and redundant staging factories after all callers have moved; investigate every residual row and assign blocking children if work exceeds this ticket.
- Under the separate Spring Test migration scope, audit `IntegrationTestsSupport`/`GemFireMockObjectsSupport` function registry cleanup and mocked `Query.execute(RegionFunctionContext)` overloads. Remove obsolete server-oriented helpers after checking callers; do not create public server SPI contracts to retain them. See task 63's [function audit](../wayfinder-impl/investigations/client-only-spring-surfaces.md#client-function-submission-and-fixture-audit--2026-10-27). Task 64 separately owns prerequisite server fixture relocation before parent 59 closes.

- Exception foundation follow-up: Retire final GemfireCacheUtils/CacheUtils native dispatch/reflection after the assigned callers move. Complete remaining general exception constructor/root-cause/serialization-equivalent coverage rows. See [exception semantics and ownership](../wayfinder-impl/investigations/exception-semantics-and-translation.md).

## Required validation

Audit residual server-package utilities by actual client use. Remove server-only public surfaces under ticket 59; preserve native-free helpers needed by client applications.

- Regenerate the whole-module audit and verify zero required native production code/type contracts remain, including reflective class strings.
- Run consumer regression checks and production compilation; inspect public signatures and resources for hidden native escapes.

## Completion

Production native-reference inventory is zero under the final boundary policy; every required API row has an implementation owner and evidence. Record the Answer with changed source/API rows, commands/results, native version and limitations; update the implementation map. Do not mark dependent work complete from this ticket's result.

## Current runtime adoption handoff — 2026-10-30

The [runtime closure ledger](../wayfinder-impl/investigations/client-runtime-closure.md) supersedes historical runtime/configuration counts below. Adopt all 312 required-client rows currently owned by task 23 in `api-coverage.tsv`, each already assigned consumer owner 36: cancellation 9, runtime context 28, member metadata 9, durable identity 11, statistics 133, logging/message values 105, and runtime/received failures 17. These remain partial; this handoff does not prove consumer migration.

- Replace global runtime discovery with injected owned collaborators. Test absence, closed-state behavior, system/cache cancellation distinctions, safe property snapshots and deliberate per-call native property discovery. Do not equate a locally connected runtime with a reachable server.
- Adopt typed member and detached durable identity payloads, preserving null/empty values and pool qualification. Native mutable aliases and native serialization APIs are not part of the client contract; remote durable lifecycle remains with 20/52.
- Migrate statistics callers through typed descriptors, instances, accessors, suppliers and XML loading. Preserve the documented disabled/closed behavior and deprecated int semantics; activation of native archives/thread monitoring belongs to 29.
- Migrate logging/message callers through the unified typed handle, preserving levels, traces, destinations, JUL integration and localization. Respect documented mock fallback and coordinate security/configuration routing with 24/29/35.
- Preserve outer-operation failure classification, safe outer diagnostics and trusted causes. Received ServerOperationException wrappers remain resource failures even with nested role/statistics/lease failures; arbitrary root causes must not be reclassified. The focused foundation regression awaits user-run validation as recorded in the ledger.

For each adopted family, record changed source rows, native-free consumer regression evidence and deliberate behavior differences before upgrading coverage. The separate 71 configuration rows now belong to 24/29 with entrypoint adoption in 35; they are not additional task-23 runtime implementation work. Task 36 retains its existing prerequisites and remains open.

## Task 72 handoff — 2026-10-28

Task 72 retains 145 exact runtime/configuration IDs with implementation owner 23 and this consumer owner. Replace DistributedSystemUtils/GemfireUtils native runtime identity, connectedness, safe property/durable helpers with injected typed client collaborators. Coordinate configuration entrypoints with 35, durable/pools with 20, authentication/TLS with 24 and local statistics/logging with 73. The public locator-hosting accessor is removed in task 72; private LocatorProcess shutdown owns its own started locator. Preserve client XML, pool locator discovery, client TLS including legacy fallbacks, and local cancellation/identity. No public global native registry or opaque Object escape hatch is an acceptable replacement. See [the exact scope ledger](../wayfinder-impl/investigations/client-runtime-configuration-and-lifecycle-scope.md).

## Task 73 client-scope handoff — 2026-10-28

The [task-73 ledger](../wayfinder-impl/investigations/client-statistics-logging-and-utility-scope.md) retains six internal utility IDs under this task and 212 statistics/logging/message consumer obligations with implementation 23. Replace DistributedSystemUtils global lookup with an injected client runtime (23/35), including absence/closed behavior. Remove the documentation-only GemFireCacheImpl reference in CacheUtils; it adds no new operation. Preserve client attribute copying/validation through typed configuration with 29/30 instead of InternalAttributesFactory/InternalRegionAttributes inheritance.

Replace RegionUtils.isLocal's native LocalRegion class test with an explicit interpretation of client local/caching/proxy configuration, preserve null behavior and test those cases with task 30. Do not expose native internal classes. Adopt typed statistics/logging/message behavior documented in the ledger, coordinating authentication diagnostics with 24/35. Nine function identity/discovery consumer IDs move to 32; locator hosting, the peer-cache carrier, WAN property helper and redundancy-management results are excluded. Task 72 already removed the public locator accessor. All six utility IDs remain missing.

## Shared exception handoff — task 76

Complete shared client exception construction, safe diagnostics, designated wrapper handling and translation. Preserve Error propagation and typed callback identity; do not classify arbitrary nested causes. Retain received role/lease failures with 23. CACHE_EXISTS and TYPE_MISMATCH plus explicit excluded native/Spring branches are retired under 76; common-ancestor fallback is not support for excluded APIs. See the [exact exception scope ledger](../wayfinder-impl/investigations/client-shared-exception-scope.md). Task 76 Java validation and generated inventory reconciliation passed on 2026-10-29. Existing implementation statuses are not upgraded.

## Final reconciliation handoff — task 64

Own the optional legacy CqInvalidException reflective source row transferred from resolved foundation 16: remove obsolete reflection after typed adoption, preserving current query translation and unknown-failure fallback. Also remove the stale AbstractCacheConfiguration CacheServer import/Javadoc and replace DistributedSystemUtils native server-port constant linkage while preserving client endpoint callers. See the [final scope ledger](../wayfinder-impl/investigations/client-scope-final-reconciliation.md) for exact counts and source dispositions. These transfers do not upgrade implementation status.
