Type: task
Status: open
Blocked by: 14, 16, 18, 20, 59

# Query, results and index SPI

## Client-only scope — 2026-10-22

Apply the [approved client-only boundary](../wayfinder-impl/client-only-scope.md). [Cleanup ticket 59](59-remove-server-only-spi-and-reconcile-client-scope.md) must complete first. Full/complete coverage in this ticket means every retained client requirement; historical native reachability alone does not require peer/server APIs. Preserve all retained client tests and native-free boundaries.

## Question

Provide typed query services, result models and index operations for templates, repositories and CQ.

Audit coverage: **G3, G4** in the [native/SPI gap audit](../wayfinder-impl/audits/native-spi-gap-audit.md). Apply the [shared completion requirements](../wayfinder-impl/spi-gap-closure-plan.md#shared-completion-requirements) and the signed-off [technical spec](../wayfinder/technical-spec.md).

## Required implementation

Prerequisites: [Required native API coverage matrix](14-required-native-api-coverage-matrix.md); [Driver exception semantics and translation](16-driver-exception-semantics-and-translation.md); [Complete region operations and metadata SPI](18-complete-region-operations-and-metadata-spi.md); [Complete pool, default-cache and durable SPI](20-complete-pool-default-cache-and-durable-spi.md).

- Implement local/client/pool query-service routing, parameterized query execution and required query inspection/lifecycle operations.
- Model SelectResults, collection/object/struct result types and nested values required by public Spring APIs; do not expose native result instances through Object.
- Implement index definition/create/list/remove/statistics and deferred creation only where verified for client-local query services. Remote server index administration is excluded. Preserve typed errors for retained operations.
- Map required query methods exposed through Region and application callbacks as well as template internals; CQ callbacks/lifecycle belong to Continuous query SPI.

- Cover inherited collection methods, `Struct` projection results and `StructType`, as well as query/result/index methods in the [coverage matrix](../wayfinder-impl/coverage/README.md). `Query.execute` returning Object must not hide typed projection payload requirements.

- Exception foundation follow-up: Preserve MultiIndexCreationException failed-index names and exception map through typed SPI payloads; retain query/index failure kinds and Spring outcomes. See [exception semantics and ownership](../wayfinder-impl/investigations/exception-semantics-and-translation.md).

## Required validation

- Run native query/result/index scenarios, including parameters, empty/scalar/collection/struct results and error categories.
- Declare mock-supported query behavior precisely and reject unsupported syntax/features; do not substitute canned successful results for native semantics.

## Completion

Complete assigned query/result/index contracts with native evidence and explicit mock disposition. Record the Answer with changed source/API rows, commands/results, native version and limitations; update the implementation map. Do not mark dependent work complete from this ticket's result.

Region-operation handoff: use DriverRegionService and RegionMetadata (dataPolicy, poolName) for query routing without native cache/region casts in Spring. Region.query/existsValue/selectValue rows are assigned here. The real proxy fixture demonstrates local size zero alongside nonzero server state; do not infer query routing from local contents. See [region-operation evidence](../wayfinder-impl/investigations/region-operations-and-metadata.md).

## Query result scope handoff — 2026-10-28

Implement the retained result collection, struct and logical type metadata contracts, including inherited Collection behavior, native mutability/failures, view/copy behavior, duplicate/order information and nested native-free values. Preserve application class information while mapping native type classes to typed equivalents. Use the [84-row source-backed ledger](../wayfinder-impl/investigations/client-query-result-scope.md); these rows are required-client, not implemented. Adapter/native/mock behavior tests remain implementation acceptance. Task 65 has now settled the six inherited metadata serialization rows and the remaining query/index/CQ scope; use the final handoff below. Do not export DataSerializable.


## Completed query/CQ audit handoff — 2026-10-28

Implement the retained query/pool/region paths, local index definition/creation/inspection/statistics and QueryService CQ entry points (with task 22). Preserve typed UNDEFINED, unsupported compile semantics, local versus pool/authenticated-view restrictions, aliases, actual per-method failure behavior and index failure details. Exclude only the two Query.execute RegionFunctionContext overloads. All six metadata encode/decode rows remain required through native-free collaborators and private versioned codecs; coordinate round-trip/class-resolution/failure acceptance with task 55. No native DataSerializable or Class leakage. Scope classification does not upgrade implementation status. See the [final 177-ID disposition](../wayfinder-impl/investigations/client-query-and-cq-scope.md), which completes all 261 task-65 IDs.

## Shared exception handoff — task 76

Complete client/local and received query/index failures, including MultiIndexCreationException typed nested failures, mutable map/live-key-set behavior, combined diagnostics and inherited constructor contracts. Coordinate consumer 31 and value adaptation 58. See the [exact exception scope ledger](../wayfinder-impl/investigations/client-shared-exception-scope.md). Task 76 Java validation and generated inventory reconciliation passed on 2026-10-29. Existing implementation statuses are not upgraded.
