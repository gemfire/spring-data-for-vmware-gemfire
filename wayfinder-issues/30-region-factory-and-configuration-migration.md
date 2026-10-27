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
