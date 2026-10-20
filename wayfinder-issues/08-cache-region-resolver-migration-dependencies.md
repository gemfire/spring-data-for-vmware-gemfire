Type: task
Status: resolved
Assignee: uk016635
Blocked by: 07

## Question

What is the smallest complete migration of the root `CacheResolver` / `RegionResolver` boundary to injected driver handles, including the implementations and consumers whose types must change with it?

The [utility/root inventory](../wayfinder-impl/investigations/utility-root-dependencies.md) shows that remaining runtime helpers are coupled to factory, pool, query and repository consumers. String-path extraction is complete. The next foundational seam named by the signed-off migration plan is cache and region resolution; determine its actual dependency closure before changing public return types.

## Scope

- Inventory the two root resolver interfaces, their implementations in `support`, `ClientCacheFactoryCacheResolver`, and every caller across main, unit-test, integration-test and Spring Test sources.
- Trace return-type and construction dependencies into cache/region factory beans and other consumers. Distinguish directly required changes from later subsystem migrations; include annotation/XML construction where relevant.
- Record existing semantics: missing or blank names, null results, `require` failures, resolver composition order, cached results, closed/destroyed handles and ownership. Check actual implementation/tests rather than assuming all resolvers share behavior.
- Identify native global lookups and static resolver state that must be replaced by constructor-injected ownership. Do not introduce a static driver manager, singleton, untyped native escape or forwarding compatibility facade.
- Compare the required operations with existing `DriverConnection` / `DriverRegion`. Specify only demonstrated SPI gaps, using default typed-unsupported methods for additions to existing interfaces. State native delegation and mock limits explicitly.
- Select a complete implementation slice with its file/caller scope and validation commands. If the resolver boundary cannot migrate independently, identify the concrete factory or other dependency that must move first and ticket that work instead.

## Deliverable and completion

Record a source-linked dependency and behavior inventory under `wayfinder-impl/investigations/`, then create the next actionable implementation ticket and update the map. This ticket is a local code investigation, not an architecture redesign or an implementation of the entire factory hierarchy. The architecture is already signed off; native dependency removal from `spring-test-vmware-gemfire` remains out of scope, though affected call sites must be accounted for.

No additional skills or delegation are implied. Gradle is authorized only for the current user session; preserve that scope if a focused behavior probe is necessary.

## Answer

Completed the [cache/region resolver dependency and behavior inventory](../wayfinder-impl/investigations/cache-region-resolver-dependencies.md) and its [source reference index](../wayfinder-impl/resolver-references.tsv).

The complete direct boundary is 13 main files, 12 unit-test classes and one integration-test class. Factory beans implement their own resolution; annotation/XML factory wiring does not reference these resolver types. No direct Spring Test callers were found.

The next implementation slice migrates both root interfaces and their implementations together. Existing driver handles cover lookup and names; native region destruction callbacks require a typed lifecycle registration operation in the SPI, with native adaptation and local mock coverage. Remove the global singleton cache resolver and replace its integration fixture with explicit driver ownership. The one native pool consumer takes an injected `Supplier<ClientCache>` so pool/factory/query migration need not be pulled into this slice.

The inventory records null/missing-name behavior, memoization, composition ordering, destruction invalidation and ownership. It also identifies eager named-bean fallback, swallowed/retried composition failures, and the current alias/path invalidation limitation. The implementation ticket distinguishes intentional fail-fast/constructor-injection corrections from behavior retained during migration.

Created [Cache and region resolver driver migration](09-cache-region-resolver-driver-migration.md), covering all affected callers, the callback prerequisite, bean wiring and validation commands. Verified all 216 reference locations, local document links, and `git diff --check`. No product code or build configuration changed, and no Gradle tests were run for this investigation.
