Type: task
Status: open
Blocked by: 16, 18, 21, 26, 30, 44, 59

# Template, repository and mapping migration

## Client-only scope — 2026-10-22

Apply the [approved client-only boundary](../wayfinder-impl/client-only-scope.md). [Cleanup ticket 59](59-remove-server-only-spi-and-reconcile-client-scope.md) must complete first. Full/complete coverage in this ticket means every retained client requirement; historical native reachability alone does not require peer/server APIs. Preserve all retained client tests and native-free boundaries.

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

Region-operation follow-up prerequisites: [Region and cache snapshot services SPI](44-region-and-cache-snapshot-services-spi.md). These preserve the split snapshot/subscription requirements before consumer or cache cutover.

Region-operation handoff: bulk/conditional/Map operations and lazy entry/collection views now have native/mock contracts; use direct typed operations rather than reconstructing bulk behavior in Spring. Duplicate-entry old value, existing-region handle and region path payloads are available as typed exceptions. Entry isDestroyed observation is sticky while accessors resolve the current key; preserve this tested behavior. Full native constructor/string replacement and consumer migration remain here. See [region-operation evidence](../wayfinder-impl/investigations/region-operations-and-metadata.md).

## Query result scope handoff — 2026-10-28

Migrate GemfireTemplate query/find/findUnique, AbstractSelectResults and repository projection/result conversion to the typed result model, preserving inherited Collection operations, duplicate/order semantics, field lookup/failures and nested values. Use the [84-row source-backed ledger](../wayfinder-impl/investigations/client-query-result-scope.md); these rows are required-client, not implemented. Adapter/native/mock behavior tests remain implementation acceptance. Task 65 has now settled the six inherited metadata serialization rows and the remaining query/index/CQ scope; use the final handoff below. Do not export DataSerializable.


## Completed query/CQ audit handoff — 2026-10-28

Migrate query service, region query/existsValue/selectValue and result/metadata consumers to task 21 equivalents. Preserve local versus pool routing, undefined versus null, scalar/cardinality failures, nested results and metadata. Client-local index contracts remain in scope; server region-function query overloads are excluded. Validate serialization collaborator usage where metadata encode/decode is exposed; do not leak native types or infer completeness from this audit. See the [final 177-ID disposition](../wayfinder-impl/investigations/client-query-and-cq-scope.md), which completes all 261 task-65 IDs.

## Task 66 scope handoff — 2026-10-28

The [region/callback audit](../wayfinder-impl/investigations/client-region-and-callback-scope.md) retains 94 region/entry/statistics IDs and 24 interest IDs. Eleven partial Region/Entry rows move here from resolved foundation 18: types, entrySet overloads and reachable entry/region/service/hierarchy handles. This ticket owns their completion/adoption with existing subsystem owners 19/21/29/44/45; task 30 owns factory configuration and attribute-copy subregion creation. Implemented rows retain foundation history but still require this ticket's consumer acceptance.

Preserve live view and destroyed/recreated entry behavior, map/bulk callback semantics, statistics/failure ordering, typed service ownership and the distinction between local content and pool-backed server inspection. Keep explicit closeRegion ownership. Use existing shared/native operation evidence; no additional scope probe is required. Interest implementation remains task 45, including per-key iterable registration and observable partial progress, rather than an invented atomic bulk operation.

## Serialization audit handoff — 2026-10-28

Task 71 settles the serialization family: tasks 54/55/56/57 own PDX callbacks, binary codecs/registration, JSON conversion and reflection respectively; task 58 now owns 77 unfinished PDX value transport rows from foundation 53. Retain and migrate MappingPdxSerializer, EnumSerializer, InstantiatorFactoryBean/ASM/Wiring, JSONRegionAdvice and their client tests using typed collaborators. Verify decline/unread fields, registration changes, nested PDX/JSON and conversion failures without native public types. Source classification alone does not complete this consumer migration. See [the exact source-backed ledger](../wayfinder-impl/investigations/client-serialization-pdx-and-json-scope.md).

## Task 74 handoff — 2026-10-28

Adopt the **two Region disk operations** implemented by 27 and **28 client-local snapshot IDs** implemented by 44; see the [exact ledger](../wayfinder-impl/investigations/client-disk-resource-and-snapshot-scope.md). Preserve writeToDisk failure versus forceRolling no-op when no disk region exists. Snapshot adoption must distinguish local/service formats from legacy streams, reject remote/parallel paths, preserve filter/callback/error behavior and handle legacy region destruction/recreation and stale handles. Coordinate cache acquisition with 29/35. Classification is complete; implementation and native/mock acceptance remain open.

## Shared exception handoff — task 76

Own unfinished region exception payload/construction/emission and adoption from resolved foundation 18: mutable old value, region adaptation and full paths, including admin.RegionNotFoundException from DataSerializer.readRegion. Coordinate aggregate query failures (21), copy/JSON failures (55/56) and value adaptation (58). See the [exact exception scope ledger](../wayfinder-impl/investigations/client-shared-exception-scope.md). Task 76 Java validation and generated inventory reconciliation passed on 2026-10-29. Existing implementation statuses are not upgraded.

## Final reconciliation handoff — task 64

Own nine partial current-SPI region mappings transferred from 18. Complete reachable configuration/query/cache/snapshot/subscription adoption with 19/21/29/44/45. Also own the two MappingPdxSerializer package-filter source rows with 54/58: preserve exclusion of provider infrastructure, null handling and predicate composition through typed serialization integration. See the [final scope ledger](../wayfinder-impl/investigations/client-scope-final-reconciliation.md) for exact counts and source dispositions. These transfers do not upgrade implementation status.
