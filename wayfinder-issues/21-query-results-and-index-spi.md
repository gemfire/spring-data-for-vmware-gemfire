Type: task
Status: open
Blocked by: 14, 16, 18, 20

# Query, results and index SPI

## Question

Provide typed query services, result models and index operations for templates, repositories and CQ.

Audit coverage: **G3, G4** in the [native/SPI gap audit](../wayfinder-impl/audits/native-spi-gap-audit.md). Apply the [shared completion requirements](../wayfinder-impl/spi-gap-closure-plan.md#shared-completion-requirements) and the signed-off [technical spec](../wayfinder/technical-spec.md).

## Required implementation

Prerequisites: [Required native API coverage matrix](14-required-native-api-coverage-matrix.md); [Driver exception semantics and translation](16-driver-exception-semantics-and-translation.md); [Complete region operations and metadata SPI](18-complete-region-operations-and-metadata-spi.md); [Complete pool, default-cache and durable SPI](20-complete-pool-default-cache-and-durable-spi.md).

- Implement local/client/pool query-service routing, parameterized query execution and required query inspection/lifecycle operations.
- Model SelectResults, collection/object/struct result types and nested values required by public Spring APIs; do not expose native result instances through Object.
- Implement index definition/create/list/remove/statistics and deferred index creation, with typed errors preserving Spring translation.
- Map required query methods exposed through Region and application callbacks as well as template internals; CQ callbacks/lifecycle belong to Continuous query SPI.

- Cover inherited collection methods, `Struct` projection results and `StructType`, as well as query/result/index methods in the [coverage matrix](../wayfinder-impl/coverage/README.md). `Query.execute` returning Object must not hide typed projection payload requirements.

- Exception foundation follow-up: Preserve MultiIndexCreationException failed-index names and exception map through typed SPI payloads; retain query/index failure kinds and Spring outcomes. See [exception semantics and ownership](../wayfinder-impl/investigations/exception-semantics-and-translation.md).

## Required validation

- Run native query/result/index scenarios, including parameters, empty/scalar/collection/struct results and error categories.
- Declare mock-supported query behavior precisely and reject unsupported syntax/features; do not substitute canned successful results for native semantics.

## Completion

Complete assigned query/result/index contracts with native evidence and explicit mock disposition. Record the Answer with changed source/API rows, commands/results, native version and limitations; update the implementation map. Do not mark dependent work complete from this ticket's result.
