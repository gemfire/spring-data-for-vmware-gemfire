Type: task
Status: open
Blocked by: 14, 17, 18, 46, 47, 48, 49, 59

# Region configuration, enums, expiration and eviction

## Client-only scope — 2026-10-22

Apply the [approved client-only boundary](../wayfinder-impl/client-only-scope.md). [Cleanup ticket 59](59-remove-server-only-spi-and-reconcile-client-scope.md) must complete first. Full/complete coverage in this ticket means every retained client requirement; historical native reachability alone does not require peer/server APIs. Preserve all retained client tests and native-free boundaries.

## Question

Represent the full client-region configuration surface and application customization callbacks with SPI-owned types.

Audit coverage: **G3, G4, G5** in the [native/SPI gap audit](../wayfinder-impl/audits/native-spi-gap-audit.md). Apply the [shared completion requirements](../wayfinder-impl/spi-gap-closure-plan.md#shared-completion-requirements) and the signed-off [technical spec](../wayfinder/technical-spec.md).

## Required implementation

Prerequisites: [Required native API coverage matrix](14-required-native-api-coverage-matrix.md); [Complete region events, listeners and writers](17-complete-region-events-listeners-and-writers.md); [Complete region operations and metadata SPI](18-complete-region-operations-and-metadata-spi.md).

- Add typed region configuration/attributes/mutation contracts for required data policy, scope, constraints, statistics, subscription/interest, disk/pool references and callback settings; preserve unset/default precedence.
- Complete SPI enums/value types required by annotations and converters; audit native constants in default values rather than merely copying enum names.
- Implement expiration policies/attributes, CustomExpiry context, eviction policies/actions, ObjectSizer and compressor callbacks with native adaptation and explicit lifecycle.
- Replace native-backed expiration/eviction value/converter implementations where independently possible; leave public region factory and annotation wiring to its named cutover ticket with explicit matrix ownership.

- Preserve client-applicable expiry reset policies and typed values. Partition resolver/listener/attribute and peer membership configuration are excluded under ticket 59; subscription/interest policies remain required in ticket 48.

## Required validation

- Verify every configurable attribute maps to the native getter/behavior, including explicit false/zero versus unset, invalid combinations and callbacks.
- Use native tests for expiration/eviction/persistence effects; mock may support metadata/local callbacks but must reject unavailable behavior rather than fake it.

## Completion

Complete assigned configuration/value/callback contracts, with remaining factory/annotation consumers assigned. Record the Answer with changed source/API rows, commands/results, native version and limitations; update the implementation map. Do not mark dependent work complete from this ticket's result.

Region-operation handoff: reuse DriverRegionEntry, DriverCacheStatistics, RegionMetadata and the native adapters. The native configured-subregion/enabled-statistics fixture already verifies traversal and counters; the mock currently creates local roots with disabled statistics. Implement typed configuration/creation for these features and preserve entry-view invalidation behavior. Region.getAttributes/getAttributesMutator/createSubregion rows are assigned here; interest registration itself is owned by the separate interest/subscription ticket. See [region-operation evidence](../wayfinder-impl/investigations/region-operations-and-metadata.md).

## Implementation decomposition

The historical 798 assigned rows require client-scope classification by ticket 59 before implementation; they are not the client requirement count. This parent retains full configuration acceptance and remains open.

- [Typed region creation and attribute access](46-typed-region-creation-and-attribute-access.md).
- [Expiration, eviction and compression SPI](47-expiration-eviction-and-compression-spi.md).
- [Client subscription and interest policy configuration SPI](48-partition-and-membership-configuration-spi.md).
- [Legacy region configuration values and enums](49-legacy-region-configuration-values-and-enums.md).

Foundation progress: [Typed region creation and attribute access](46-typed-region-creation-and-attribute-access.md) is resolved with 463 passing tests and typed native/mock hierarchy creation. [Evidence](../wayfinder-impl/investigations/typed-region-configuration.md) lists supported settings and precise mock limitations. This parent remains open: complete attribute/enum/value contracts, effects and independently separable Spring converter migration still require its other children. Audit attribute-copy/default precedence and mock statistics needs at parent acceptance.
