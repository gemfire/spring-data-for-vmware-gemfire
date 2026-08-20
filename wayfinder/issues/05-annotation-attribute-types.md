Type: grilling
Status: resolved

## Question

Annotation attributes (e.g. `dataPolicy`, `scope`, region shortcuts) are currently typed as native GemFire enums. Given [Major version breaking migration](04-major-version-breaking-migration.md) rules out a compatibility facade, how should these attributes be typed once Spring Data GemFire no longer references GemFire directly?

Two options were compared:
- **Option A** — SPI-owned delegation enums: the SPI defines its own enum types (e.g. an SPI `DataPolicy`) that mirror the native ones; Driver Impls translate between SPI and native enum values.
- **Option B** — remove the annotation-attribute capability entirely, pushing that configuration to a different mechanism (e.g. programmatic config only).

## Answer

**Option A: SPI-owned delegation enums.** Consistent with the [Major version breaking migration](04-major-version-breaking-migration.md) decision — annotations keep working, just retyped against SPI-owned enums that each Driver Impl maps to/from its native equivalents. No capability is dropped.
