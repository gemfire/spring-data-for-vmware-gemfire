Type: task
Status: open
Blocked by: 18, 26, 59

# Client-local snapshot services SPI

## Client-only scope — 2026-10-22

Apply the [approved client-only boundary](../wayfinder-impl/client-only-scope.md). [Cleanup ticket 59](59-remove-server-only-spi-and-reconcile-client-scope.md) must complete first. Full/complete coverage in this ticket means every retained client requirement; historical native reachability alone does not require peer/server APIs. Preserve all retained client tests and native-free boundaries.

## Question

Determine and implement the snapshot operations valid for retained client-local regions. Broad Region/Cache reachability alone does not require cluster snapshot administration.

## Required implementation

- Use task 74's pinned source evidence for client-local region/cache snapshot acquisition, file/stream overloads, formats, filters and options; verify version-specific behavior during implementation.
- Implement required client-local save/load and filter callbacks where supported, preserving native serialization, defaults, checked I/O/class-loading failures and entry/value adaptation.
- Exclude cluster-wide/partitioned/parallel server administration through ticket 59. Treat unsupported legacy stream methods according to verified native behavior; do not invent a format or silently accept unsupported options.
- The mock must explicitly reject unavailable native snapshot formats unless verified compatibility is implemented. Task 74 confirms retained client-local operations; this task remains required.

## Required validation

Native client-local round trips for retained operations, filter/options/errors and explicit unsupported cases. Private server fixtures may support client tests but must not turn this task into server snapshot management.

## Completion

Every historical snapshot row has a reviewed disposition; every required client row has implementation/test evidence. Keep cache assembly and template/repository blockers until this disposition and implementation are complete.

## Task 74 handoff — 2026-10-28

The [source-backed ledger](../wayfinder-impl/investigations/client-disk-resource-and-snapshot-scope.md) retains **28 snapshot IDs** and excludes the two parallel-mode options. Source confirms client-local file and legacy stream implementations in the pinned version; no further scope probe is needed. Implement the retained contracts and versioned compatibility tests here, with adoption in 31. Coordinate the cache accessor already classified by task 67 with 29/35; native ClientCache does not itself declare that accessor.

Restrict the facade to client-local regions: native pool-backed export uses ClientExporter, and cache-wide native enumeration can include such regions. Define explicit rejection/selection before delegation. Preserve .gfd/file-directory validation, existing-region lookup, active-transaction export rejection, invalid entries, I/O/class-loading failures, failed-export cleanup, callback default false and nullable filters. GEMFIRE/GEODE labels do not imply distinct encodings in the pinned implementation. Test import filter views for byte arrays/invalid entries: the pinned implementation returns null unless its stored value is CachedDeserializable. Keep native serializers, PDX registry and entry representations private.

Legacy stream load destroys/recreates the region, destroys subregions and invalidates old handles; test reacquisition, callback close/reinstallation and failure outcomes. Save/load close supplied streams. Do not implement legacy load as a merge or conflate its format with service snapshots. Mock native-format operations must reject unsupported behavior explicitly unless compatibility is implemented. These semantics and round trips remain implementation acceptance, not blockers for closing task 74.
