Type: task
Status: resolved
Assignee: Codex
Blocked by: 14, 17, 18

# Typed region creation and attribute access

## Question

Implement immutable optional region settings, typed attribute/mutator views, peer/client/subregion creation, scalar getters and callback identity. Preserve shortcut defaults and explicit false/zero, native validation and ownership. Validate local mock hierarchy/constraints and explicitly reject unavailable configured behavior. Native tests compare all mapped settings with native attributes; shared tests exercise creation, views, mutation and failure cases.

Required implementation child of [Region configuration, enums, expiration and eviction](19-region-configuration-enums-expiration-and-eviction.md). Apply the [shared completion requirements](../wayfinder-impl/spi-gap-closure-plan.md#shared-completion-requirements). All frozen coverage rows remain required.

## Completion

Record changed contracts and member mappings, executable native/mock evidence and limitations. Parent configuration acceptance remains open until all children and its full-surface validation pass.

## Answer

Implemented the typed region-configuration foundation with six ABI-safe operational defaults, immutable explicit/unset options, 25 attribute getters and 12 mutator operations. Native factories preserve shortcut defaults and enforce their own validation; client-only server-region routing has real server evidence. Local mock regions support constraints, callback identity/duplicates and hierarchy/lifecycle; unavailable configured effects fail explicitly.

[Implementation and validation evidence](../wayfinder-impl/investigations/typed-region-configuration.md) records exact behavior, native version, commands and limitations. Final verification passed 463 tests, full Spring Data/Spring Test source compilation, normal migration-boundary checks and coverage consistency/javap negative controls. No migration baseline expansion or Spring source/test deletion was used.

Coverage retains all 3,851 rows: this foundation owns 121 (37 implemented, 84 partial with full factory/reachable-type/consumer obligations retained). The [parent configuration task](19-region-configuration-enums-expiration-and-eviction.md) remains open and blocked by the remaining configuration children. No full-family or full-module completion is claimed. Changes remain uncommitted.
