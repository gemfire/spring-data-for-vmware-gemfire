Type: task
Status: open
Blocked by: 46

# Partition and membership configuration SPI

## Question

Implement partition attributes/builders, resolver/listener contracts and entry-operation contexts, partition member/rebalance result values and StringPrefixPartitionResolver behavior. Include membership attributes/loss/resumption and subscription/interest policy values; integrate typed creation/readback. Test native routing/callback lifecycle and complete value mappings; mock distributed behavior must be explicitly unsupported.

Required implementation child of [Region configuration, enums, expiration and eviction](19-region-configuration-enums-expiration-and-eviction.md). Apply the [shared completion requirements](../wayfinder-impl/spi-gap-closure-plan.md#shared-completion-requirements). All frozen coverage rows remain required.

## Completion

Record changed contracts and member mappings, executable native/mock evidence and limitations. Parent configuration acceptance remains open until all children and its full-surface validation pass.
