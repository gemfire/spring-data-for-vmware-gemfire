Type: task
Status: open
Blocked by: 14, 16, 19, 59

# Disk-store and resource configuration SPI

## Client-only scope — 2026-10-22

Apply the [approved client-only boundary](../wayfinder-impl/client-only-scope.md). [Cleanup ticket 59](59-remove-server-only-spi-and-reconcile-client-scope.md) must complete first. Full/complete coverage in this ticket means every retained client requirement; historical native reachability alone does not require peer/server APIs. Preserve all retained client tests and native-free boundaries.

## Question

Represent disk stores, persistence-related configuration and cache resource management behind the driver.

Audit coverage: **G3, G4** in the [native/SPI gap audit](../wayfinder-impl/audits/native-spi-gap-audit.md). Apply the [shared completion requirements](../wayfinder-impl/spi-gap-closure-plan.md#shared-completion-requirements) and the signed-off [technical spec](../wayfinder/technical-spec.md).

## Required implementation

Prerequisites: [Required native API coverage matrix](14-required-native-api-coverage-matrix.md); [Driver exception semantics and translation](16-driver-exception-semantics-and-translation.md); [Region configuration, enums, expiration and eviction](19-region-configuration-enums-expiration-and-eviction.md).

- Add required disk-store creation/lookup/configuration/lifecycle operations and typed directory/configuration values.
- Cover resource thresholds and settings demonstrated to work on client caches; the pinned assigned surface has no off-heap methods, so route any newly discovered setting through task 64. Do not copy internal types into SPI.
- Preserve dependency ordering for disk stores referenced by regions/PDX and failure cleanup when creation only partially succeeds.
- Document supported mock metadata and explicitly reject persistence, recovery or memory-pressure guarantees that are not implemented.

- Exclude rebalance/restore-redundancy factories, operations and results. Ticket 59 records their server-only dispositions. Retain client-local disk, overflow and PDX persistence requirements.

- Exception foundation follow-up: Preserve disk failure metadata including the remote flag through a native-free exception payload, as well as the resource-failure category. See [exception semantics and ownership](../wayfinder-impl/investigations/exception-semantics-and-translation.md).

## Required validation

- Use native temporary-directory tests for store creation/lookup, persistence/recovery where required, invalid configuration and cleanup.
- Verify resource-setting delegation and threshold validation; do not infer eviction behavior from the mock storing a percentage.

## Completion

Complete assigned disk/resource contracts ready for cache and region factory adoption. Record the Answer with changed source/API rows, commands/results, native version and limitations; update the implementation map. Do not mark dependent work complete from this ticket's result.

Region-operation handoff: Region.writeToDisk/forceRolling rows are assigned here. Snapshot services and legacy stream snapshot methods retain their separate snapshot owner; do not conflate disk-store configuration with snapshot format support. See [region-operation evidence](../wayfinder-impl/investigations/region-operations-and-metadata.md).

## Task 74 handoff — 2026-10-28

The [exact member ledger](../wayfinder-impl/investigations/client-disk-resource-and-snapshot-scope.md) retains **66 disk/resource IDs**: 50 disk-store/factory declarations, seven legacy disk-write values, two Region disk operations and seven heap-resource declarations. Implementation remains here; adoption is 30 for 64 IDs and 31 for the two Region operations. All remain missing. No public rebalance/restore-redundancy API is required.

Preserve disk units, native defaults, defensive directory arrays, directory creation, named-store reuse/conflict behavior, recovery and failure cleanup. Validate flush/roll/compaction/destroy against temporary client-local persistent/overflow stores, including stores still used by regions or PDX. Legacy disk-write equality compares mode/rolling/max-oplog and, when asynchronous, interval/byte threshold; do not substitute the modern operation-count queue size. Region.writeToDisk rejects a region without disk storage while forceRolling is a no-op there.

Heap thresholds default disabled, validate ranges and strict eviction/critical ordering, and affect native monitoring. Pin actual exception behavior and setter ordering in driver tests. The assigned ResourceManager surface has no off-heap methods; any newly discovered requirement goes to 64 for assignment. Mock metadata may be supported but must explicitly reject unimplemented persistence/recovery/pressure guarantees. Classification is complete; this implementation task remains open.

## Shared exception handoff — task 76

Complete DiskAccessException region/disk context and remote-origin serialization behavior. Generic RESOURCE_FAILURE is incomplete; coordinate consumer 30 and value adaptation 58. See the [exact exception scope ledger](../wayfinder-impl/investigations/client-shared-exception-scope.md). Task 76 Java validation and generated inventory reconciliation passed on 2026-10-29. Existing implementation statuses are not upgraded.
