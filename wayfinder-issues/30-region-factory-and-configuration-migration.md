Type: task
Status: open
Blocked by: 18, 19, 20, 24, 26, 27, 29, 59

# Region factory and configuration migration

## Client-only scope — 2026-10-22

Apply the [approved client-only boundary](../wayfinder-impl/client-only-scope.md). [Cleanup ticket 59](59-remove-server-only-spi-and-reconcile-client-scope.md) must complete first. Full/complete coverage in this ticket means every retained client requirement; historical native reachability alone does not require peer/server APIs. Preserve all retained client tests and native-free boundaries.

## Question

Retarget the complete region factory/configuration consumer group to the full SPI.

Audit coverage: **G3, G4, G5** in the [native/SPI gap audit](../wayfinder-impl/audits/native-spi-gap-audit.md). Apply the [shared completion requirements](../wayfinder-impl/spi-gap-closure-plan.md#shared-completion-requirements) and the signed-off [technical spec](../wayfinder/technical-spec.md).

## Required implementation

Prerequisites: [Complete region operations and metadata SPI](18-complete-region-operations-and-metadata-spi.md); [Region configuration, enums, expiration and eviction](19-region-configuration-enums-expiration-and-eviction.md); [Complete pool, default-cache and durable SPI](20-complete-pool-default-cache-and-durable-spi.md); [Security and authenticated views SPI](24-security-and-authenticated-views-spi.md); [PDX, serialization and JSON SPI](26-pdx-serialization-and-json-spi.md); [Disk-store and resource configuration SPI](27-disk-store-and-resource-configuration-spi.md); [Complete cache configuration and creation assembly](29-complete-cache-configuration-and-creation-assembly.md).

- Migrate LookupRegionFactoryBean, Resolvable/Configurable/ClientRegionFactoryBean and expiry/eviction wrappers together with their actual callers. Task 63 retires RegionAttributesFactoryBean and redirects its callers to existing client factory properties; migrate those XML, entity-annotation and Java configuration paths to the SPI here.
- Replace the remaining native RegionAttributes arguments/readback, including ClientRegionFactoryBean and CacheTypeAwareRegionFactoryBean attributes properties. Task 63's removal of the inherited AttributesFactory API does not satisfy this native-free requirement. Preserve explicit versus omitted settings, chained template inheritance/overrides and client shortcut defaults using typed SPI configuration, including the expiration/eviction/compression contracts from task 47.
- Replace native callbacks, attributes, pool/service arguments and shortcut enums in region annotations/configurers/converters/XML parsers with SPI types.
- Preserve creation versus lookup, named/default pool semantics, callbacks/interest registration and destruction dependencies; use injected driver connections and pool handles.
- Update region namespace schema exports and tests. Remove native PoolResolver dependence from migrated region consumers so pool resolver cutover can happen later without an unwrap.

- Exception foundation follow-up: Migrate DefinedIndexesApplicationListener error handling with typed failed-index payloads; preserve callback and disk failure outcomes during configuration. See [exception semantics and ownership](../wayfinder-impl/investigations/exception-semantics-and-translation.md).

- Callback foundation follow-up: Use DriverCacheListener/DriverCacheWriter and their explicit no-op/specialized adapters as the target for composable callbacks, annotations/XML and factory signatures. RegionCallbacks permits creation-time listener delivery. Preserve distinct declarable initialization, callback instance identity, replacement and resolver lifecycle ownership; see [callback evidence](../wayfinder-impl/investigations/region-callback-contracts.md).

## Required validation

Retire server-shortcut attributes, peer-only region configuration and their schema/annotation exports with ticket 59; migrate retained ClientRegionShortcut and client-local settings without copying server enums.

- Run Java, annotation and namespace region configuration tests against mock/native as applicable, including every required configuration/callback family.
- Verify fail-fast invalid/unsupported settings, qualified wiring and region-before-pool teardown; compile dependent main/unit/integration sources.

## Completion

Region factory public APIs/configuration and their migrated tests are native-free, with no legacy dual-type overload retained. Record the Answer with changed source/API rows, commands/results, native version and limitations; update the implementation map. Do not mark dependent work complete from this ticket's result.

Region-operation handoff: DriverRegion.closeRegion deliberately avoids Spring inferred bean destruction while exposing native Region.close. Preserve explicit factory ownership. Reuse entry/statistics/service views and routing metadata; complete configuration, subregion creation and subscription wiring with their respective owners. See [region-operation evidence](../wayfinder-impl/investigations/region-operations-and-metadata.md).

## Task 66 scope handoff — 2026-10-28

The [137-ID region/callback audit](../wayfinder-impl/investigations/client-region-and-callback-scope.md) retains the attribute-based `Region.createSubregion` overload: native ClientRegionFactoryImpl calls it. This ticket now owns that missing row (`API-30da94e001d7cb96`) and its consumer migration. Complete a typed equivalent for client-compatible attribute-copy creation with tasks 19/29/47, preserving explicit/omitted settings, callback identity, pool/server-region routing and native validation. Existing shortcut creation does not prove equivalence; do not restore peer creation or native RegionAttributes signatures. Coordinate with task 67's separate attribute/factory audit without transferring its fixed IDs.

Four partial CacheCallback/Declarable type and initialize rows also move here from resolved foundation 17. Complete typed owner/configuration assembly and Spring adoption: preserve distinct initialize then init declarative hooks, programmatic registration behavior, properties, failures and cleanup. Existing callback implementations/statuses remain unchanged. Run retained client XML/annotation/factory tests against the migrated contracts; tasks 29/31/45 retain their collaborators and consumers.

## Task 67 scope handoff — 2026-10-28

The [attribute assembly review](../wayfinder-impl/investigations/client-cache-and-region-assembly-scope.md#client-attribute-assembly) assigns this active task 23 partial rows formerly owned by completed foundation 46: client factory settings, callbacks and region attribute/mutator access. Preserve omitted/explicit/default settings, listener order/identity/replacement, live mutators and owner identity, constraints, statistics, pool/server-region routing and local disk persistence. Complete equivalent named/attribute-copy creation, including task 66's subregion requirement. Existing typed creation alone is not full parity.

Coordinate 19/29/47/49 for legacy local policy, directory/write configuration and eviction/declarable values. Retain ordinary client XML. Do not restore peer builders, bucket/partition hosting or server queue conflation aliases. Native/mock and Java/XML consumer acceptance remain this task's implementation work; task 67 closes only classification.

## Task 68 consumer handoff — 2026-10-28

All [154 expiry/eviction/compression IDs](../wayfinder-impl/investigations/client-expiry-eviction-and-compression-scope.md) are client-required and still missing under task 47. Migrate ClientRegionFactoryBean, expiry/eviction wrapper beans, converters, client XML and annotations to the resulting typed contracts. Preserve proxy versus local-storage validation, omitted/explicit defaults, statistics/cloning interactions, callback identity and lifecycle, TTL reset and old-value mutation semantics. Native Snappy use in CompressionConfiguration remains migration work despite native deprecation. Coordinate 49/55 for eviction DTO conversion and expiration binary values. No server shortcuts or peer creation are restored; all retained client tests stay acceptance requirements.

## Task 74 handoff — 2026-10-28

Adopt the **64 retained disk/resource IDs** implemented by 27; see the [member and behavior ledger](../wayfinder-impl/investigations/client-disk-resource-and-snapshot-scope.md). Preserve client XML, DiskStoreFactoryBean configuration, local persistence/overflow and named region/PDX dependencies. Convert legacy disk-write values with their actual units/equality and preserve heap threshold ordering/validation. Do not expose rebalance or partition redundancy factories through ResourceManager. These are classified requirements, not completed SPI implementations.

## Task 75 handoff — 2026-10-28

Adopt all **46 required routing/subscription IDs** from 48 using the [source-backed ledger](../wayfinder-impl/investigations/client-partition-membership-and-subscription-scope.md). Preserve native-free resolver keys/callbacks and typed entry-operation context, supported client configuration/XML lifecycle, and string-prefix routing behavior. Use a private driver bridge for server-advertised resolver class construction; do not expose native classes, PartitionAttributes or hosted partition listeners. Preserve the CACHE_CONTENT value default versus ALL client-factory default and avoid presenting policy assignment as interest registration. Coordinate payload adaptation with 58 and registration/durability with 45/52. These are implementation obligations, not completed SPI support.

## Shared exception handoff — task 76

Own unfinished callback exception construction/emission and typed-failure identity from resolved foundation 17, including native/mock compatibility and Spring adoption. Retain disk failure context from 27. Server exclusions do not remove supported client callbacks. See the [exact exception scope ledger](../wayfinder-impl/investigations/client-shared-exception-scope.md). Task 76 Java validation and generated inventory reconciliation passed on 2026-10-29. Existing implementation statuses are not upgraded.

## Final reconciliation handoff — task 64

Own 56 partial callback/event API rows and 32 current-SPI mappings transferred from foundation 17. Preserve delivered forwarding while completing adoption and reachable member/configuration/cache/transaction contracts with 23/19/29/34. Do not treat resolved foundation coverage as full adoption. See the [final scope ledger](../wayfinder-impl/investigations/client-scope-final-reconciliation.md) for exact counts and source dispositions. These transfers do not upgrade implementation status.
