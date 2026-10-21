Type: task
Status: open
Blocked by: 18, 26

# Region and cache snapshot services SPI

## Question

Implement the complete region/cache snapshot contract discovered through Region reachability. This is the snapshot portion split from [Complete region operations and metadata SPI](18-complete-region-operations-and-metadata-spi.md), not a scope exclusion.

## Required implementation

- Add typed RegionSnapshotService, CacheSnapshotService, SnapshotOptions, SnapshotFilter and both native format names, preserving their defaults, mutable option behavior and serializable filter contract.
- Adapt region/service acquisition, file and directory save/load overloads, explicit file-array loads, filter entry views, callback invocation and parallel mode. Preserve checked I/O/class-loading failure semantics and native limitations.
- Account for legacy Region.saveSnapshot/loadSnapshot stream overloads with verified native behavior; do not mark unsupported or removed from absence of an implementation.
- Delegate native serialization without introducing another snapshot format. The mock must explicitly reject unavailable native-format behavior unless a verified compatible implementation is supplied.

## Required validation

Real native save/load round trips, filters, callbacks on/off, invalid paths/formats, parallel-mode constraints, cache-wide operation and stream-overload disposition. Retain metadata and serialization ownership from the PDX/serialization ticket.

## Completion

Complete every snapshot-owned coverage row. This ticket blocks template/repository migration and cache assembly. Record exact version, commands, files, behavior and remaining consumer obligations before resolving.
