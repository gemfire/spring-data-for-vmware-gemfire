# Client-only cache and region creation

Task 61 removes public peer creation against GemFire **10.4.0-build.3056.df98c835**. The SPI now accepts `new CacheConfiguration(properties)` and exposes `createClientRegion` / `createClientSubregion` with `ClientRegionShortcut`. `CacheConfiguration.Mode`, `RegionShortcut`, three `DriverConnection.createRegion` overloads and `DriverRegion.createSubregion` are removed together with their native/mock implementations. This is an intentional pre-first-release API change, not a released compatibility claim.

Spring Java callers and four XML cache/resolver/pool test fixtures now use the properties-only constructor; the obsolete `CLIENT` constructor argument is removed.

The native driver connects through `ClientCacheFactory` only. Internal callback wrappers accept the native `GemFireCache` abstraction for callback cache handles; task 62 has now removed the temporary peer fixtures recorded below. No public peer creation path remains. Native cache implementations can implement both `Cache` and `ClientCache`, so internal client checks use `InternalCache.isClient()` instead of trusting Java interface identity.

## Retained client behavior

Shared contracts and mock isolation tests use actual client creation. Ownership, independent mock connections, cache/region closure, callback cleanup, region hierarchy and lookup, live views, sizing/default attributes, constraints and exception identity remain asserted. Native local client tests preserve statistics, lifecycle and attribute parity. All five native LOCAL shortcuts, including persistent and overflow variants, are compared with native control factories; disk-backed variants use a temporary named disk store. The mock still supports LOCAL only and rejects unsupported shortcuts explicitly.

Client loaders and writers are installed through the attributes mutator, since `ClientRegionFactory` does not provide their creation setters. Creation-time listener registration remains covered. Shared and native PDX schema, enum, identity, writable-value, nested-value and deserialization tests now use real client caches with a private server and an explicit named pool. A separate missing-pool failure test remains. Merely configuring a default server without initializing its lazy pool did not support standalone PDX factories; the tests now establish the pool explicitly.

At task 61, the shared transaction writer probe verified client rejection (`INVALID_STATE`), including `setWriter(null)`. Task 62 removes that unsupported SPI hook and preserves direct native rejection evidence. The pinned native `TXManagerImpl.setWriter` checks client mode before setting the writer.

## Test dispositions

| Previous coverage | Disposition |
| --- | --- |
| Four shared default probes for peer region/subregion creation | Retired with the deleted SPI methods. Client factory, configured attributes and hierarchical lookup assertions remain in client probes. |
| Shared PEER/CLIENT loops | Run once through client creation; retain ownership, lookup, callbacks and lifecycle assertions. |
| Wrong-cache-mode creation/pool tests | Retired because the public mode selector and peer factory no longer exist. Closed-cache, invalid arguments and unsupported shortcut checks remain. |
| Explicit peer factory configuration in shared region tests | Remove peer setters from creation; retain client attributes/defaults/constraints. Task 62 owns removal of their still-declared configuration fields and mutators. |
| Shared no-op removal of absent WAN sender/queue IDs | Retired from client behavior assertions; task 62 owns WAN surface removal. |
| Duplicate mock connection isolation tests | Consolidated into `compatibleClientConnectionsRemainIndependentInTheLocalMock`, retaining the stronger independent-state and independent-close assertions. |
| Native PDX, local operations, lifecycle and configuration tests | Converted to actual client caches; PDX uses a real server/pool. No disabled tests or weakened PDX assertions. |
| Native shared contract setup | Creates a client connection and explicit PDX pool against the private server for each connection. |

## Private peer fixture ledger — removal owner task 62

This historical task 61 ledger is now fully resolved by the [task 62 dispositions](client-only-callbacks-and-configuration.md#resolved-private-fixture-ledger). These fixtures were temporary adapter regression evidence, **not proof of client support**. Retained behavior now uses clients; server-only assertions and hooks are retired.

| Fixture | Required disposition |
| --- | --- |
| `NativeTransactionTests`: all eleven methods use the private peer `cache()` helper (commit/rollback events, conflicts, writer vetoes, suspension, externalized identity, registration/lifecycle, native callback identity, listener failure, distribution settings and error mapping) | Split client-supported lifecycle/identity/error behavior into real client/server cases. Retire successful writer registration/veto and server-only distribution settings after exact-version review. The private region helper directly invokes native `Cache.createRegionFactory`; it does not restore SPI peer creation. |
| `NativeCallbackCoverageTests`: `registeredAdapterForwardsEveryNativeCallbackIncludingInheritedAndSpecializedMethods`, `serializedAndTimestampedEntryViewsStayLazyAndPreserveNativeAvailabilityAndFailures`, `realTransactionEventsRetainIdentityAndMemberMetadata`, `registeredWriterAndLoaderForwardBothDeclarableHooksWithoutAutomaticRegistrationInitialization` | Separate retained client callback/serialization/initialization assertions from membership/role/WAN timestamp assertions. Replace peer-backed retained behavior with client evidence; remove server-only adapters and tests. The operation constant comparison is independent of peer creation and needs member-level scope review. |
| `NativeDistributedCallbackTests.realRemoteMembershipRoleChangesAndSerializedEntryEventsReachInjectedCallbacks` | Retire peer membership/role behavior; preserve any required remote serialized-event assertions in a subscribed client scenario. |
| `CallbackPeerProcess` and extracted package-private `NativeServerFixture` | Retain private server infrastructure for client PDX, subscription, proxy operations and transactions. Remove the locator/peer role commands needed only by the preceding membership test. Public server SPI is not reintroduced. |

`NativeDistributedCallbackTests` also retains genuine client cases for region-live notifications, proxy routing, PDX schema/enum registration and commit/rollback; these must survive task 62. Scope/data-policy values, server-only configuration fields, specialized callback interfaces and transaction hooks are intentionally not all removed by task 61. Spring native factory/annotation/XML surfaces remain task 63's responsibility.

## Coverage and validation

Keep all 3,851 frozen native API IDs. Remove the four deleted defaults from the current SPI inventory. Clear 54 stale partial implementation mappings: 24 peer shortcut rows, 28 peer factory rows, one cache factory row and one native attribute-copy subregion overload. The last overload remains unresolved pending task 64; typed client shortcut/configuration creation does not establish full native attribute-copy equivalence. Excluded peer rows remain historical native inventory with no current SPI implementation; none is labeled native `removed`.

The boundary scanner also needed a correction for immutable enum constant aliases such as `InterestResultPolicy.DEFAULT`: recognize a final alias to a constant of the same immutable enum. Adversarial fixtures still reject mutable enum instances, mutable object payloads and non-final aliases. No boundary baseline additions or exemptions were made.

Validation commands and final outcomes are recorded in task 61. The transitional migration gate is not proof that Spring is native-free; parent 59 and the downstream migration/testing tasks remain open.
