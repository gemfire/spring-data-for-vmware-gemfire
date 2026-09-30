Type: grilling
Status: resolved

## Question

[Capability enum evolution and removal](08-capability-enum-evolution-and-removal.md) gates capability removal on "no still-supported Driver Impl version exposes it" — but what mechanism actually determines and declares that a given Driver Impl version is no longer supported? And separately: should the SPI refuse to bind to a Driver Impl that has fallen off the support window, rather than only failing at the point a missing capability is invoked?

## Answer

Two complementary pieces:

1. **Support matrix as governance source of truth.** A documented support matrix (GemFire version → Driver Impl version → supported-until) lives in the spec/docs, mirroring GemFire's own official support-lifecycle/EOL dates rather than an independent Spring Data GemFire policy. It's enforced by process, not runtime code: a release checklist item requires checking the matrix before a major SPI release removes any capability. This keeps the SPI a thin delegate (per [Thread safety stance](09-thread-safety-stance.md)) — no extra runtime machinery just to serve a release-governance question.

2. **Bind-time minimum-version fail-fast.** The Driver Manager derives a **minimum supported Driver Impl version** from the support matrix (the floor — the oldest entry still on the matrix), alongside the existing maximum (the current SPI version, per [SPI versioning scheme](10-spi-versioning-scheme.md)'s "no forward compatibility" rule). At `ServiceLoader` bind time, an impl below the minimum fails fast immediately — the same hard, no-grace-period fail-fast used everywhere else in this design — rather than binding successfully and only surfacing a problem later via [Unsupported feature fail-fast](07-unsupported-feature-fail-fast.md). Capability-level fail-fast still covers the case of a bound-but-in-range impl simply lacking one specific newer feature.

## Amendment (see [Initial version list and matrix scope](16-initial-version-list-and-matrix-scope.md))

Part 2 above (the Driver Manager-derived, matrix-sourced minimum-version bind-time check) is **superseded and dropped**. `driver-core` should not own GemFire-version compatibility logic or consult a centrally-maintained matrix at bind time — that logic instead belongs to the Driver Impl itself, which self-declares/self-checks its own compatibility with the GemFire version present at runtime; `driver-core` simply asks the Driver Impl that question at bind time (a thin-delegate check, distinct from the still-standing SPI-version-range check in [SPI versioning scheme](10-spi-versioning-scheme.md)). The concrete mechanics of that self-check are open research — see [Driver Impl self-declared GemFire compatibility mechanics](15-driver-impl-self-declared-gemfire-compatibility-mechanics.md).

Part 1 above (the support matrix as a documented artifact) **stands, but with its role narrowed**: it is now purely a documentation/governance record (release planning, capability-removal sign-off) with no runtime enforcement role at all — the process-enforced release-checklist use described above is unaffected.

## Amendment 2 — see [ticket 20](20-spi-abi-compatibility-and-driver-version-lockstep.md)

Part 1 (the support matrix as a governance artifact) still stands. References above to capability removal now read: a feature may be removed from the SPI only at an SPI major bump, gated by the matrix.
