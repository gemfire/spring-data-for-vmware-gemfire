Type: task
Status: open
Blocked by: 14, 16, 17

# Complete region operations and metadata SPI

## Question

Complete the region/service operation surface required by templates, factories and application Region callbacks.

Audit coverage: **G3, G4** in the [native/SPI gap audit](../wayfinder-impl/audits/native-spi-gap-audit.md). Apply the [shared completion requirements](../wayfinder-impl/spi-gap-closure-plan.md#shared-completion-requirements) and the signed-off [technical spec](../wayfinder/technical-spec.md).

## Required implementation

Prerequisites: [Required native API coverage matrix](14-required-native-api-coverage-matrix.md); [Driver exception semantics and translation](16-driver-exception-semantics-and-translation.md); [Complete region events, listeners and writers](17-complete-region-events-listeners-and-writers.md).

- Map the required Region/RegionService public and inherited surface from the coverage matrix, including contains-value/value-for-key, server-key checks, create/replace variants, bulk operations, entry views, paths/subregions, invalidation/destruction and metadata.
- Implement typed operations, native delegation and local mock behavior without substituting loops where they change native atomicity, return values or callback semantics.
- Expose lifecycle operations needed by real users and listener tests, plus typed metadata needed for client pool association and query routing. Query execution itself is owned by Query and index SPI.
- Ensure methods involving callbacks use the full event/listener/writer contracts and that unavailable network operations fail explicitly.

## Required validation

- Cover required CRUD/bulk/conditional operations, callback arguments, entry/region lifecycle, destroyed handles and return semantics on mock/native where supported.
- Validate server/proxy-specific operations natively and typed unsupported behavior in the mock; preserve existing region API behavior.

## Completion

Complete assigned region-operation rows and typed metadata; remaining query/configuration rows link to their owning tickets. Record the Answer with changed source/API rows, commands/results, native version and limitations; update the implementation map. Do not mark dependent work complete from this ticket's result.
