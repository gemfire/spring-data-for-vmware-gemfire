Type: task
Status: open
Blocked by: 46

# Expiration, eviction and compression SPI

## Question

Implement expiration actions/attributes/reset policies, CustomExpiry and entry contexts, all eviction algorithms/actions/factories/mutators, ObjectSizer and compressor callbacks/defaults. Add region creation/readback/mutation integration. Verify native expiration, eviction, overflow and compression effects plus callback lifecycle; local mock must reject unavailable behavior. Migrate independently separable Spring values/converters and record remaining consumer owners.

Required implementation child of [Region configuration, enums, expiration and eviction](19-region-configuration-enums-expiration-and-eviction.md). Apply the [shared completion requirements](../wayfinder-impl/spi-gap-closure-plan.md#shared-completion-requirements). All frozen coverage rows remain required.

## Completion

Record changed contracts and member mappings, executable native/mock evidence and limitations. Parent configuration acceptance remains open until all children and its full-surface validation pass.
