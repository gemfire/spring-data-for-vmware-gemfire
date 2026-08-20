Type: grilling
Status: resolved

## Question

Branch `udo.kohlmeyer/unified_driver` already contains a substantial prior implementation of this same idea — `gud-api`, `gud-core`, and `gud-driver-gemfire-10.1/10.2/10.3` modules, 200+ `Gud*`-prefixed types, a single-ServiceLoader-entry-per-driver model, claiming compile-complete status as of 2026-04-02 (per `GUD_MIGRATION_STATUS.md`), but with 7 of 9 tracked correctness/test issues still open per its own `In_Progress.md` tracker as of 2026-06-06.

How should this prior art factor into the new spec: formalize/document it as-is, critically re-evaluate and revise it, or treat it as one input among several design options to compare against alternatives?

## Answer

Treat the `unified_driver`/GUD branch as **informative background only** — a clean-slate design, not a document-as-is or direct-reuse effort. Its architecture invariants (zero native GemFire deps in the Spring Data module, no reflection, ServiceLoader-based driver discovery, a mock driver for unit tests) are useful precedent to weigh, but the new spec is not obligated to preserve its structure, module boundaries, or decisions where a better answer emerges during grilling.

The `Gud*` naming convention specifically is **out of scope** for this spec (see map's Out of scope section) — naming is a later, separate decision, not something this effort resolves.
