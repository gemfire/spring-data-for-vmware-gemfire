Type: grilling
Status: resolved

## Question

`technical-spec.md` §5.2 modelled `GemFireDriverManager` as a static singleton (`private static volatile GemFireDriver bound`, static `driver()`, double-checked locking), with §9 defending that pattern. Should the design avoid static state and singletons entirely, and if so what replaces the static access point?

## Answer

Yes. Raised by Udo Kohlmeyer during implementation of Step 1, while reading the spec.

- `GemFireDriverManager` becomes an **instance**: it resolves and validates in its constructor (eager, fail-fast at startup), holds the driver in a `final` field, and is obtained by injection. Final-field semantics replace double-checked locking, so the `volatile`/`synchronized` machinery and the lazy-init race disappear, along with any cached-failure state.
- New general rules, spec §5.7: no mutable static state; no singletons or static service locators; permitted statics limited to immutable constants, stateless factories on immutable value types, and unavoidable JDK/framework calls; instance-scoped loggers; no static reset/test hooks; Driver Impls hold no process-wide state (native GemFire's own globals pass through per the thin-delegate stance); enforced by an extension of the §4 architecture fitness function.
- Scope: new modules and new/rewritten code. Pre-migration statics in `spring-data-vmware-gemfire` are not removed just because their package migrates; migrated code must obtain the driver by injection.

Amends spec §4, §5.2, §7 (step 3) and §9; supersedes the double-checked-locking rationale in §9. Per [ticket 17](17-spec-review-sign-off-process.md) this reverses a completed design decision, so it needs a fresh sign-off row in `rationale.md` §8 (not yet recorded).
