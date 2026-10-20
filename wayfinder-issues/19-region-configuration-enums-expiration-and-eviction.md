Type: task
Status: open
Blocked by: 14, 17, 18

# Region configuration, enums, expiration and eviction

## Question

Represent the full region configuration surface and application customization callbacks with SPI-owned types.

Audit coverage: **G3, G4, G5** in the [native/SPI gap audit](../wayfinder-impl/audits/native-spi-gap-audit.md). Apply the [shared completion requirements](../wayfinder-impl/spi-gap-closure-plan.md#shared-completion-requirements) and the signed-off [technical spec](../wayfinder/technical-spec.md).

## Required implementation

Prerequisites: [Required native API coverage matrix](14-required-native-api-coverage-matrix.md); [Complete region events, listeners and writers](17-complete-region-events-listeners-and-writers.md); [Complete region operations and metadata SPI](18-complete-region-operations-and-metadata-spi.md).

- Add typed region configuration/attributes/mutation contracts for required data policy, scope, constraints, statistics, subscription/interest, disk/pool references and callback settings; preserve unset/default precedence.
- Complete SPI enums/value types required by annotations and converters; audit native constants in default values rather than merely copying enum names.
- Implement expiration policies/attributes, CustomExpiry context, eviction policies/actions, ObjectSizer and compressor callbacks with native adaptation and explicit lifecycle.
- Replace native-backed expiration/eviction value/converter implementations where independently possible; leave public region factory and annotation wiring to its named cutover ticket with explicit matrix ownership.

- Include reachable partition resolver/listener and entry-operation contexts, partition attributes, time-to-live reset policy and native configuration/result value types identified in the [coverage matrix](../wayfinder-impl/coverage/README.md); do not restrict coverage to directly imported enums.

## Required validation

- Verify every configurable attribute maps to the native getter/behavior, including explicit false/zero versus unset, invalid combinations and callbacks.
- Use native tests for expiration/eviction/persistence effects; mock may support metadata/local callbacks but must reject unavailable behavior rather than fake it.

## Completion

Complete assigned configuration/value/callback contracts, with remaining factory/annotation consumers assigned. Record the Answer with changed source/API rows, commands/results, native version and limitations; update the implementation map. Do not mark dependent work complete from this ticket's result.
