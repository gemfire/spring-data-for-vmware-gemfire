Type: task
Status: resolved
Blocked by: 46, 47, 48, 59

# Client configuration values and enum migration

## Client-only scope — 2026-10-22

Apply the [approved client-only boundary](../wayfinder-impl/client-only-scope.md). [Cleanup ticket 59](59-remove-server-only-spi-and-reconcile-client-scope.md) must complete first. Full/complete coverage in this ticket means every retained client requirement; historical native reachability alone does not require peer/server APIs. Preserve all retained client tests and native-free boundaries.

## Question

Complete native-free values, enum predicates/converters and annotation defaults actually needed by retained client configuration.

## Required implementation

- Audit client annotations/configurers/XML and map required values without copying full native enums. Preserve unset/default semantics and valid client-local persistence settings.
- Retire RegionShortcut and obsolete server-only aliases/configuration with ticket 59. Review MirrorType, legacy disk settings and configuration DTO nodes individually; retain only demonstrated client contracts, using native-free values rather than mirroring server configuration schemas.
- Map every historical row to a retained client owner or an evidence-backed scope exclusion. Server authorization OperationContext/OperationCode hooks are not client configuration requirements; client authentication and denial handling remain with ticket 24.

## Required validation

Exercise retained client annotation defaults, converters, Java/XML contexts and native value mappings. Verify excluded server configuration is absent rather than silently ignored.

## Completion

All retained client values and defaults are native-free and behavior-validated. Record row dispositions and remaining consumer owners; ticket 19 stays open until its other children complete.

## Task 67 scope handoff — 2026-10-28

The [legacy local policy decision](../wayfinder-impl/investigations/client-cache-and-region-assembly-scope.md#client-legacy-local-policy) retains MirrorType and persist-backup equivalents only for client-local storage/persistence. Keep ordinal/predicate/string conversion and validation, including the distinction between LOCAL scope and distributed replication; do not add peer shortcuts.

The [eviction configuration decision](../wayfinder-impl/investigations/client-cache-and-region-assembly-scope.md#client-eviction-config-values) retains 112 missing value/constructor/inherited member requirements through the actual public client eviction conversion path. Complete native-free action/default/maximum/sizer/declarable values, nested parameters, null versus empty/live-list behavior, equality/hash/string observations and factory/conversion precedence. This does not require exposing native JAXB or management classes or copying the whole schema. Task 30 owns adoption; 19/47 own eviction integration. Preserve client legacy disk directory/write settings and conflicting-setting failures.

PartitionAttributes hosting DTOs, bucket markers, server conflation aliases and DeclarableType.EMPTY's gfsh removal sentinel are excluded with no existing SPI mapping. Scope is settled for these exact task-67 IDs; the separately assigned task-68 eviction members and other audit families keep their own owners. Full native/mock conversion and Java/XML tests remain required here.

## Task 68 eviction conversion coordination — 2026-10-28

[EvictionAttributes conversion](../wayfinder-impl/investigations/client-expiry-eviction-and-compression-scope.md#client-eviction-values) remains task 47's required-client member, paired with this task's DTOs/inverse conversion retained by task 67. Preserve heap/memory/otherwise-entry branch selection, NONE-action failure and sizer class-string behavior; do not normalize LIFO into a new DTO branch or silently fix lossy native conversion. Task 30 owns adoption. Fixed IDs and ownership remain unchanged.

## Answer — 2026-10-31

Resolved in five slices, each pinned to the native 10.4 build `3068.c3a2bbfe` by `NativeLegacy*Tests` in the 10.4 `integrationTest` source set plus native-free SPI tests:

1. `DataPolicy`, `Scope` and `MirrorType` values with legacy names, ordinals and predicates, and `LegacyPolicyConversions` for the mirror-type and persist-backup conversions.
2. Legacy disk directories, sizes and write attributes: `LegacyDiskConfiguration` (`new LegacyDiskConfiguration()` holds the defaults) and `DiskWriteSettings`.
3. `ClientRegionShortcut`, constant-for-constant in native order.
4. Eviction: `LegacyEvictionSettings` with `LegacyEvictionSettingsFactory.generate(...)` replacing the native generator, including the heap, memory and entry branch choice, the `none` action failure and the invalid-class-name failure.

All 191 required-client rows owned by this task are dispositioned in `coverage-policy.json`. Disk and eviction values are not wired into `RegionConfiguration` or `DriverRegionAttributes`, because native `ClientRegionFactory` has no `setDiskDirs` or `setDiskWriteAttributes`; ticket 30 is the consumer. Conflation setters and getters are excluded as server-only. The assembly rows are dispositioned as replaced by the `RegionConfiguration` builder, `DriverRegionAttributes` and these value carriers.

16 rows that remain unfinished were re-owned to [ticket 19](19-region-configuration-enums-expiration-and-eviction.md), which stays open for its other children.

Validation: the SPI and 10.4 native-pinning tests pass, `verifyMigrationBoundary` passes with no new violations (static-state 162, static-service 96, static-reset 1, all below the baseline), `verifyDriverCompatibility` passes, and coverage regenerates and validates (3,851 rows: 2,694 required-client, 1,157 excluded-server, 0 unresolved).
