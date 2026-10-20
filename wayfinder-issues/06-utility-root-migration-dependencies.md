Type: task
Status: resolved
Assignee: uk016635
Blocked by: 05

## Question

Identify the next viable Step 3 implementation slice across the utility and root packages now that the cache package is native-free.

Inventory native operations and direct/transitive consumers of `CacheUtils`, `DistributedSystemUtils`, `RegionUtils` and `GemfireUtils`. Include inherited static utility calls and native global lookup dependencies. `RegionUtils` already has known consumers in `GemfireTemplate`, `ClientRegionFactoryBean`, `SimpleGemfireRepository` and JSON region advice; changing only its signatures would strand those consumers.

Select the smallest migration slice that can compile and run its tests with its consumers migrated or explicitly scheduled in the same slice, without introducing a permanent native facade or new static service locator. Record the concrete files, required SPI operations, native/mock semantics and validation commands in the resulting implementation ticket. Keep the signed-off architecture intact and leave unrelated subsystem work in the map's remaining backlog.

## Answer

Resolved 2026-10-20. The [dependency inventory](../wayfinder-impl/investigations/utility-root-dependencies.md) records the native operations, utility inheritance, direct and transitive consumer groups, global lookups and migration constraints. The accompanying [reference index](../wayfinder-impl/utility-root-references.tsv) contains 118 source-reference records across 49 Java files; records include imports and are not a count of call sites.

The four utilities form `GemfireUtils → RegionUtils → CacheUtils → DistributedSystemUtils → SpringExtensions`. Whole-class retargeting would pull in native cache factories, pool configuration, query/template APIs, repositories, function execution and JSON/PDX advice. Several names conceal important behavior: `isLocal` checks a native implementation class, while `isClient` checks for a pool name rather than the cache's mode. These cannot safely be replaced with shortcut or mode guesses.

The selected next slice is [Region path utility migration](07-region-path-utility-migration.md): extract the two String overloads of the region name/path helpers into a native-free utility and update their 17 existing caller files together. Native Region overloads remain with their current consumers. No new SPI operation or driver/mock behavior is required; this is a pure utility dependency removal. Test-support changes are limited to imports and calls, preserving that module's explicitly out-of-scope native dependency.

The implementation ticket lists exact files, existing behavior to preserve, boundary checks and Gradle commands. A local Java probe confirmed null, blank, already-prefixed and whitespace behavior, including quirks that should not change during extraction. Inventory artifacts and links were checked; no production source changed and no Gradle run was needed for this ticket. Larger utility/root migrations remain in the map's backlog.
