Type: task
Status: open
Blocked by: 18, 26, 59

# Client-local snapshot services SPI

## Client-only scope — 2026-10-22

Apply the [approved client-only boundary](../wayfinder-impl/client-only-scope.md). [Cleanup ticket 59](59-remove-server-only-spi-and-reconcile-client-scope.md) must complete first. Full/complete coverage in this ticket means every retained client requirement; historical native reachability alone does not require peer/server APIs. Preserve all retained client tests and native-free boundaries.

## Question

Determine and implement the snapshot operations valid for retained client-local regions. Broad Region/Cache reachability alone does not require cluster snapshot administration.

## Required implementation

- Verify exact-version client availability of region/cache snapshot acquisition, file/stream overloads, formats, filters and options. Record evidence for every historical snapshot row before implementation.
- Implement required client-local save/load and filter callbacks where supported, preserving native serialization, defaults, checked I/O/class-loading failures and entry/value adaptation.
- Exclude cluster-wide/partitioned/parallel server administration through ticket 59. Treat unsupported legacy stream methods according to verified native behavior; do not invent a format or silently accept unsupported options.
- The mock must explicitly reject unavailable native snapshot formats unless verified compatibility is implemented. If no snapshot operation belongs to the retained client contract, record that evidence, resolve as superseded and remove dependent blockers explicitly.

## Required validation

Native client-local round trips for retained operations, filter/options/errors and explicit unsupported cases. Private server fixtures may support client tests but must not turn this task into server snapshot management.

## Completion

Every historical snapshot row has a reviewed disposition; every required client row has implementation/test evidence. Keep cache assembly and template/repository blockers until this disposition and implementation are complete.
