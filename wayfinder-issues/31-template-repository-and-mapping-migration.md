Type: task
Status: open
Blocked by: 16, 18, 21, 26, 30

# Template, repository and mapping migration

## Question

Move templates, repositories, mapping serializers and JSON advice onto the completed driver contracts.

Audit coverage: **G3, G4, G5, G6** in the [native/SPI gap audit](../wayfinder-impl/audits/native-spi-gap-audit.md). Apply the [shared completion requirements](../wayfinder-impl/spi-gap-closure-plan.md#shared-completion-requirements) and the signed-off [technical spec](../wayfinder/technical-spec.md).

## Required implementation

Prerequisites: [Driver exception semantics and translation](16-driver-exception-semantics-and-translation.md); [Complete region operations and metadata SPI](18-complete-region-operations-and-metadata-spi.md); [Query, results and index SPI](21-query-results-and-index-spi.md); [PDX, serialization and JSON SPI](26-pdx-serialization-and-json-spi.md); [Region factory and configuration migration](30-region-factory-and-configuration-migration.md).

- Retarget GemfireOperations/GemfireTemplate/GemfireCallback and repository factories/query implementations to typed region/query/result contracts.
- Replace exposeNativeRegion/native callback access with the corresponding SPI surface; preserve required operations instead of narrowing callbacks until compilation passes.
- Migrate MappingPdxSerializer, serializer helpers and JSONRegionAdvice, plus native-backed mapping annotations and result conversions.
- Use the common exception translator and injected services; remove native Region/query/PDX references from this complete consumer group and its tests.

- Exception foundation follow-up: Migrate GemfireAccessor, GemfireTemplate and GemfireCallback from native exception dispatch to the common SPI/Spring translator and retain callback/query outcomes. See [exception semantics and ownership](../wayfinder-impl/investigations/exception-semantics-and-translation.md).

## Required validation

- Verify repository CRUD, paging/query/result conversion, template bulk/conditional operations, user callbacks and mapping/JSON round trips.
- Exercise public API behavior on native runtimes, supported mock paths and meaningful unsupported failures; compile all callers.

## Completion

Template/repository/mapping/serialization consumer rows migrated with behavioral parity and no native product escape. Record the Answer with changed source/API rows, commands/results, native version and limitations; update the implementation map. Do not mark dependent work complete from this ticket's result.
