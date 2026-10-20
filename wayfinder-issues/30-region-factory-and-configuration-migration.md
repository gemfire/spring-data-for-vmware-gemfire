Type: task
Status: open
Blocked by: 18, 19, 20, 24, 26, 27, 29

# Region factory and configuration migration

## Question

Retarget the complete region factory/configuration consumer group to the full SPI.

Audit coverage: **G3, G4, G5** in the [native/SPI gap audit](../wayfinder-impl/audits/native-spi-gap-audit.md). Apply the [shared completion requirements](../wayfinder-impl/spi-gap-closure-plan.md#shared-completion-requirements) and the signed-off [technical spec](../wayfinder/technical-spec.md).

## Required implementation

Prerequisites: [Complete region operations and metadata SPI](18-complete-region-operations-and-metadata-spi.md); [Region configuration, enums, expiration and eviction](19-region-configuration-enums-expiration-and-eviction.md); [Complete pool, default-cache and durable SPI](20-complete-pool-default-cache-and-durable-spi.md); [Security and authenticated views SPI](24-security-and-authenticated-views-spi.md); [PDX, serialization and JSON SPI](26-pdx-serialization-and-json-spi.md); [Disk-store and resource configuration SPI](27-disk-store-and-resource-configuration-spi.md); [Complete cache configuration and creation assembly](29-complete-cache-configuration-and-creation-assembly.md).

- Migrate LookupRegionFactoryBean, Resolvable/Configurable/ClientRegionFactoryBean, RegionAttributesFactoryBean and expiry/eviction wrappers together with their actual callers.
- Replace native callbacks, attributes, pool/service arguments and shortcut enums in region annotations/configurers/converters/XML parsers with SPI types.
- Preserve creation versus lookup, named/default pool semantics, callbacks/interest registration and destruction dependencies; use injected driver connections and pool handles.
- Update region namespace schema exports and tests. Remove native PoolResolver dependence from migrated region consumers so pool resolver cutover can happen later without an unwrap.

## Required validation

- Run Java, annotation and namespace region configuration tests against mock/native as applicable, including every required configuration/callback family.
- Verify fail-fast invalid/unsupported settings, qualified wiring and region-before-pool teardown; compile dependent main/unit/integration sources.

## Completion

Region factory public APIs/configuration and their migrated tests are native-free, with no legacy dual-type overload retained. Record the Answer with changed source/API rows, commands/results, native version and limitations; update the implementation map. Do not mark dependent work complete from this ticket's result.
