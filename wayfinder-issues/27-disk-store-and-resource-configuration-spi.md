Type: task
Status: open
Blocked by: 14, 16, 19

# Disk-store and resource configuration SPI

## Question

Represent disk stores, persistence-related configuration and cache resource management behind the driver.

Audit coverage: **G3, G4** in the [native/SPI gap audit](../wayfinder-impl/audits/native-spi-gap-audit.md). Apply the [shared completion requirements](../wayfinder-impl/spi-gap-closure-plan.md#shared-completion-requirements) and the signed-off [technical spec](../wayfinder/technical-spec.md).

## Required implementation

Prerequisites: [Required native API coverage matrix](14-required-native-api-coverage-matrix.md); [Driver exception semantics and translation](16-driver-exception-semantics-and-translation.md); [Region configuration, enums, expiration and eviction](19-region-configuration-enums-expiration-and-eviction.md).

- Add required disk-store creation/lookup/configuration/lifecycle operations and typed directory/configuration values.
- Cover resource-manager heap/off-heap thresholds and related existing cache configuration without copying internal types into SPI.
- Preserve dependency ordering for disk stores referenced by regions/PDX and failure cleanup when creation only partially succeeds.
- Document supported mock metadata and explicitly reject persistence, recovery or memory-pressure guarantees that are not implemented.

- Include reachable rebalance/restore-redundancy factories, operations and result payloads, not just heap threshold setters; the [coverage matrix](../wayfinder-impl/coverage/README.md) records the required surface.

- Exception foundation follow-up: Preserve disk failure metadata including the remote flag through a native-free exception payload, as well as the resource-failure category. See [exception semantics and ownership](../wayfinder-impl/investigations/exception-semantics-and-translation.md).

## Required validation

- Use native temporary-directory tests for store creation/lookup, persistence/recovery where required, invalid configuration and cleanup.
- Verify resource-setting delegation and threshold validation; do not infer eviction behavior from the mock storing a percentage.

## Completion

Complete assigned disk/resource contracts ready for cache and region factory adoption. Record the Answer with changed source/API rows, commands/results, native version and limitations; update the implementation map. Do not mark dependent work complete from this ticket's result.

Region-operation handoff: Region.writeToDisk/forceRolling rows are assigned here. Snapshot services and legacy stream snapshot methods retain their separate snapshot owner; do not conflate disk-store configuration with snapshot format support. See [region-operation evidence](../wayfinder-impl/investigations/region-operations-and-metadata.md).
