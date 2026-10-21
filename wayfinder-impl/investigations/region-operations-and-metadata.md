# Region operations and metadata

Owner: [Complete region operations and metadata SPI](../../wayfinder-issues/18-complete-region-operations-and-metadata-spi.md).

Native evidence: `com.vmware.gemfire:gemfire-core:10.4.0-build.3056.df98c835`. The implementation delegates this resolved artifact, without runtime version branches. API declarations and the `LocalRegion`, `AbstractRegion`, `NonTXEntry` and `EntriesSet` bytecode were inspected locally; shared tests verify the observed behavior.

## Contracts and implementation

- `DriverRegion` adds 56 typed defaults for create/destroy/invalidate overloads, conditional updates, inherited Map functions, bulk operations, local operations, entry/collection views, paths/subregions, service access, statistics, routing metadata and server inspection. Every new default has an explicit behavioral probe. The existing 42 defaults plus these additions make 98 default probes across the SPI.
- Native operations call the corresponding native overload directly. Native bulk/conditional operations and Map functions are not rewritten as loops. Collection adapters remain live, adapt native entry/region elements, preserve collection mutation outcomes and translate native failures. Application keys and values are not unwrapped as driver handles.
- `DriverRegionEntry` preserves entry value mutation, metadata, region access, equality and lifetime. `DriverCacheStatistics` exposes the six native statistics operations. `RegionMetadata` carries data policy and pool name for later query routing; it is not a replacement for the complete mutable attributes contract.
- `DriverRegionService` preserves region lookup, root regions and service lifecycle, including a native service that is not a full cache. `DriverConnection` extends that interface while retaining its existing defaults. Query, authenticated-view and serialization services remain with their subsystem owners.
- `DriverEntryExistsException`, `DriverRegionExistsException` and `DriverRegionUnavailableException` retain previous values, existing typed handles and paths separately from sanitized diagnostics. The documented native cache-exception wrapper also retains its designated duplicate-entry payload and original cause. Arbitrary nested causes still do not override an outer failure.
- The local mock implements supported operations and callback semantics. Server-only operations explicitly throw `NOT_INTRODUCED`; it never returns invented remote counts or keys. The mock currently creates only local roots with statistics disabled. Typed subregion/statistics configuration remains with region configuration; native configured subregions and enabled statistics are already exercised by integration fixtures.

## Native details that affect callers

**Bean ownership.** The SPI method is `closeRegion()`, delegating native `Region.close()`. Exposing a no-argument `close()` on the borrowed region handle caused Spring to infer a bean destroy method, breaking the existing borrowed-connection integration test. The explicit name preserves existing Java/XML bean ownership without requiring a user to disable destroy-method inference. Cache/service ownership remains explicit. Region factory migration must preserve this distinction.

**Entry lifetime.** A native nontransactional entry view resolves its key against current region contents. Once `isDestroyed()` observes absence it remains true, but accessors can see a subsequently recreated key. Without observing absence, recreation can leave `isDestroyed()` false. Entry value reads do not invoke loaders. The mock follows these observed semantics; it does not claim an immutable identity snapshot.

**Collections.** Values omit invalid entries and retain duplicates. Collection objects reflect subsequent region changes. Unsupported mutations throw only when the native collection would attempt a mutation: removing an absent key or clearing an already empty view can be a no-op. Entry/region view elements remain typed. No additional concurrent iteration guarantee is introduced.

**Events and bulk operations.** Native putAll/removeAll operation tags, callback arguments, conditional return values and writer decisions are preserved. Local invalidation/destruction/clear bypass the corresponding writers as observed natively. Existing resolver lifecycle registrations remain separate from general listeners.

**Network operations.** A real separate server and PROXY client verify containsKeyOnServer, keySetOnServer, sizeOnServer/sizeLongOnServer, isEmptyOnServer, bulk reads/writes/removal, conditional mutations and pool metadata. A proxy's local size can remain zero while server size is nonzero. Unsupported server inspection on a native local region maps its native failure; mock server inspection is explicitly unavailable.

## Coverage and remaining ownership

The frozen baseline remains 3,851 rows across 292 native types. After this change it contains 3,256 missing, 356 partial and 239 implemented rows. The region-operation owner has 127 rows: 94 implemented and 33 partial for complete reachable contracts or downstream adoption. No row was deleted or reclassified as native removal. Spring exposure/import/source-reference counts remain 588/144/624; production native files remain 182.

The broader Region-reachable surface is deliberately still required:

| Family | Owner |
| --- | --- |
| Attributes, mutators, typed subregion creation, statistics/region configuration | [Region configuration](../../wayfinder-issues/19-region-configuration-enums-expiration-and-eviction.md) |
| Query, existsValue, selectValue and results | [Query/results SPI](../../wayfinder-issues/21-query-results-and-index-spi.md) |
| Distributed locks and lock grantor | [Distributed runtime SPI](../../wayfinder-issues/23-distributed-system-membership-and-runtime-services-spi.md) |
| writeToDisk and forceRolling | [Disk/resource SPI](../../wayfinder-issues/27-disk-store-and-resource-configuration-spi.md) |
| Complete region/cache snapshot services, options, filters, formats and legacy stream operations | [Snapshot SPI](../../wayfinder-issues/44-region-and-cache-snapshot-services-spi.md) |
| Every interest overload, policy, durable/receive-values flag and list inspection | [Interest/subscription SPI](../../wayfinder-issues/45-region-interest-registration-and-subscription-spi.md) |
| Region/cache assembly and Spring Region/template consumers | [Cache assembly](../../wayfinder-issues/29-complete-cache-configuration-and-creation-assembly.md), [region factories](../../wayfinder-issues/30-region-factory-and-configuration-migration.md), [template/repositories](../../wayfinder-issues/31-template-repository-and-mapping-migration.md) |

Snapshot and subscription obligations were split into concrete implementation tickets and wired as downstream blockers. The new dependency graph has no cycles. These are required work, not exclusions or claims of implementation.

## Validation

Shared contract runs cover all 98 defaults and local behavior on both providers. Native fixtures cover hierarchy, enabled region/entry statistics, live views and real proxy/server state. The existing Spring owner/borrower tests guard against accidental region ownership.

Full validation commands and final results are recorded in the owning ticket. Normal migration-boundary verification is a regression gate against the existing baseline; it is not strict native-free compliance. The two existing user changes to Gradle configuration are preserved separately.
