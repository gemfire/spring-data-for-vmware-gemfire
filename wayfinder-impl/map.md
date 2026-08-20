## Destination

Execute the Driver SPI implementation defined by the signed-off design in `wayfinder/technical-spec.md` (sign-off recorded in `wayfinder/rationale.md` §8, 2026-08-20) — build the `spring-data-gemfire-driver-spi` and `spring-data-gemfire-driver-core` modules, one or more reference Driver Impl modules plus the official mock, and migrate `spring-data-vmware-gemfire` off native GemFire, following the 9-step migration plan in `wayfinder/technical-spec.md` §7.

This map tracks **implementation progress and implementation-time decisions/questions** — it does not re-decide architecture, which is already signed off in the design effort under `wayfinder/`.

See [Migration plan](../wayfinder/technical-spec.md) §7 for the step sequence this map executes.

## Notes

- Architecture is signed off (`wayfinder/rationale.md` §8, 2026-08-20) and is not reopened here. Per [wayfinder/issues/17-spec-review-sign-off-process.md](../wayfinder/issues/17-spec-review-sign-off-process.md): a significant change — one that breaks or reverses an already-completed design decision, or changes the overall approach — requires a new sign-off round on the design docs before being acted on. Smaller clarifications/local corrections are handled inline as normal implementation work, with git history as the audit trail.
- Standing architectural stances carried over from the design effort (do not re-litigate here): the SPI is a thin delegate around the Driver Impl; fail fast everywhere, never silent degradation; two independent bind-time compatibility axes (Axis A — SPI capability-generation range; Axis B — real GemFire native-version range); the `Capability` enum is closed and, once introduced, a capability's semantics are immutable; module/package layout and dependency graph per `wayfinder/technical-spec.md` §3–§4.
- Skills to consult when working any ticket: `/grilling`, `/domain-modeling` — used here only for implementation-time design gaps that genuinely need resolving, not to re-decide anything already settled in `wayfinder/`.
- Branch strategy (per `wayfinder/technical-spec.md` §7): work happens on a dedicated long-lived integration branch (e.g. `driver-spi-migration`), separate from the repo's `support/<gemfire>-<spring>-<sdgf>.x` convention, merged into `develop` once at cutover.

## Decisions so far

_(none yet)_

## Not yet specified

The 9-step migration plan from `wayfinder/technical-spec.md` §7, tracked here as the initial implementation backlog — each becomes its own ticket (or several) as work starts on it:

- Step 1 — SPI skeleton: stand up `spring-data-gemfire-driver-spi` and `spring-data-gemfire-driver-core` as Gradle subprojects with the core contracts/exception hierarchy/config enums (no behavior yet).
- Step 2 — Reference Driver Impl(s) and the official mock Driver Impl.
- Step 3 — Migrate `spring-data-vmware-gemfire` subsystem-by-subsystem in the bottom-up order from §7 (cache/util/root → small low-risk packages → config → client/function/repository/mapping).
- Step 4 — Retarget exception translation (`GemfireCacheUtils`-equivalent → `GemFireDriverException`).
- Step 5 — Retarget annotation attributes to SPI-owned delegation enums.
- Step 6 — Close the gap to zero native references; turn on the architecture fitness function (banned-import check).
- Step 7 — Stand up the per-Driver-Impl-version `integrationTest` matrix backing both compatibility axes.
- Step 8 — Publish the support matrix (documentation/governance only).
- Step 9 — Cut the major version release (merge integration branch into `develop`).

## Out of scope

- Any change to the already-signed-off architecture in `wayfinder/technical-spec.md`/`wayfinder/rationale.md` without going through a new sign-off round per [ticket 17](../wayfinder/issues/17-spec-review-sign-off-process.md).
- The concrete initial GemFire version list to ship Driver Impls for at launch — still deferred to separate release planning, per [wayfinder/issues/16-initial-version-list-and-matrix-scope.md](../wayfinder/issues/16-initial-version-list-and-matrix-scope.md).
- Migrating `spring-test-vmware-gemfire` off its direct native GemFire dependency — called out of scope for the whole effort, per [wayfinder/issues/14-testing-strategy.md](../wayfinder/issues/14-testing-strategy.md).
