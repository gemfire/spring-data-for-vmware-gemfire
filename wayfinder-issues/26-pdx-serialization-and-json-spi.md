Type: task
Status: open
Blocked by: 14, 16, 53, 54, 55, 56, 57, 58, 59

# PDX, serialization and JSON SPI

## Client-only scope — 2026-10-22

Apply the [approved client-only boundary](../wayfinder-impl/client-only-scope.md). [Cleanup ticket 59](59-remove-server-only-spi-and-reconcile-client-scope.md) must complete first. Full/complete coverage in this ticket means every retained client requirement; historical native reachability alone does not require peer/server APIs. Preserve all retained client tests and native-free boundaries.

## Question

Provide typed serialization and PDX contracts sufficient to migrate mapping, JSON advice and application serializers.

Audit coverage: **G3, G4** in the [native/SPI gap audit](../wayfinder-impl/audits/native-spi-gap-audit.md). Apply the [shared completion requirements](../wayfinder-impl/spi-gap-closure-plan.md#shared-completion-requirements) and the signed-off [technical spec](../wayfinder/technical-spec.md).

## Required implementation

Prerequisites: [Required native API coverage matrix](14-required-native-api-coverage-matrix.md); [Driver exception semantics and translation](16-driver-exception-semantics-and-translation.md).

- Model required PdxReader/Writer/Instance operations, identity fields, unread fields and serializer callbacks, including nested native-returning types.
- Cover DataSerializer/DataSerializable/Instantiator and JSONFormatter behavior actually used or exposed; keep registration state inside native GemFire rather than adding SPI static registries.
- Implement native adapters and documented mock behavior for supported serialization/value operations, rejecting native wire-format promises the mock cannot provide.
- Provide cache-level PDX configuration inputs and disk-store references for Cache configuration assembly; MappingPdxSerializer and JSONRegionAdvice adoption belongs to template/repository/mapping migration.

- Include native `JsonDocument`/`JsonDocumentFactory`, writable PDX, instance factories, unread fields and `ReflectionBasedAutoSerializer` hooks found through public signatures/callback specialization in the [coverage matrix](../wayfinder-impl/coverage/README.md).

- Exception foundation follow-up: Complete serialization/JSON failure emission and assigned exception constructor/member equivalents without leaking native result or throwable type contracts. See [exception semantics and ownership](../wayfinder-impl/investigations/exception-semantics-and-translation.md).

## Required validation

- Verify native round trips, schema/type/identity handling, application serializer callbacks and JSON conversion/error behavior.
- Test mock-supported value semantics separately from native serialization compatibility; no raw native reader/writer/instance escapes.

## Completion

Complete required serialization/PDX/JSON contracts with downstream adoption owners. Record the Answer with changed source/API rows, commands/results, native version and limitations; update the implementation map. Do not mark dependent work complete from this ticket's result.

## Blocking implementation children

The 378 originally assigned rows span distinct serialization systems. The parent remains open and retains every downstream dependency.

- [PDX values and instance factories](53-pdx-values-and-instance-factories.md).
- [PDX callbacks and cache configuration](54-pdx-callbacks-and-cache-configuration.md).
- [Binary serialization and registration](55-binary-serialization-and-registration.md).
- [JSON documents and PDX conversion](56-json-documents-and-pdx-conversion.md).
- [Reflection-based PDX serialization](57-reflection-based-pdx-serialization.md).
- [Serialization value boundary adaptation](58-serialization-value-boundary-adaptation.md).
