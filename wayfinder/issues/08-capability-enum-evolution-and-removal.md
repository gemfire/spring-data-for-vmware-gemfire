Type: grilling
Status: resolved

## Question

The SPI needs a way to represent which capabilities/features a given Driver Impl supports (e.g. a `Capability` enum checked by [Unsupported feature fail-fast](07-unsupported-feature-fail-fast.md)). How does that representation evolve over time as GemFire gains and eventually retires features — closed enum with coordinated releases, or an open/extensible representation? And when, if ever, can a capability be removed?

## Answer

**Closed enum, coordinated releases** — not an open/extensible representation. But its evolution has a specific, asymmetric shape:

- The SPI's capability surface always tracks the **latest** GemFire version: when GemFire ships a new feature, it's added to the next Driver SPI / Spring Data GemFire release immediately. The SPI doesn't wait for every Driver Impl to catch up — it is always a **superset** of what's currently implemented anywhere.
- **Backward compatibility** means an *older* Driver Impl (lacking a newer capability) continues to work against the *latest* SPI/Spring Data GemFire. Invoking a capability that impl doesn't have still fails fast per [Unsupported feature fail-fast](07-unsupported-feature-fail-fast.md).
- There is **no forward compatibility**: a Driver Impl requiring a capability newer than the current SPI cannot exist by construction, since the SPI always ships the capability at or before the Driver Impl that needs it. See [SPI versioning scheme](10-spi-versioning-scheme.md) for how this is enforced at bind time.
- A capability can only be **removed** from the SPI once no still-supported Driver Impl version exposes it — i.e. removal is gated by Driver Impl end-of-support, not decided unilaterally by the SPI. See [Driver Impl support matrix and bind-time version check](11-driver-impl-support-matrix-and-bind-time-check.md) for how "still supported" is determined.

## Amendment — capability immutability invariant

Added during [ticket 18](18-spi-impl-generation-range-compatibility.md)'s research/sign-off: this ticket's original answer describes evolution purely in cardinality terms (addition, removal) and was silent on whether a capability's *semantics* can change once introduced. That was a real gap — the generation/range-intersection model in ticket 18 depends on a capability meaning the same thing at every generation where it's present.

**Explicit invariant: once introduced, a capability's semantics are immutable.** A capability enum constant's contract is fixed at the generation it's introduced and never changes underneath existing generations. If GemFire (or the SPI) ever needs to change a capability's behavior in a way that isn't purely additive, that is **not** a modification of the existing constant — it must retire the old constant (subject to the same end-of-support-gated removal rule above) and introduce a new one to represent the changed behavior.



## Amendment — superseded by [ticket 20](20-spi-abi-compatibility-and-driver-version-lockstep.md)

The closed `Capability` enum is removed. The method is the capability: implemented means supported, and a `default` SPI method that throws means not supported (see [ticket 07](07-unsupported-feature-fail-fast.md)'s amendment). What survives from this ticket:

- **Backward compatibility, no forward compatibility.** An older driver runs against a newer SPI in the same major (new methods are `default`). A driver built against a newer SPI minor than the runtime SPI cannot exist by construction — it is now rejected at bind time by the `runtime SPI minor >= stamped minor` check, instead of being prevented by absent enum vocabulary.
- **Immutability invariant** (added via ticket 18), restated as: within a major, existing SPI methods never change behaviour; a behaviour change arrives as a new method.
- **Removal gating.** A feature can only leave the SPI at an SPI **major** bump, and the support matrix ([ticket 11](11-driver-impl-support-matrix-and-bind-time-check.md) part 1, [ticket 16](16-initial-version-list-and-matrix-scope.md)) gates that as a release-checklist item. Within a major nothing is removed.
