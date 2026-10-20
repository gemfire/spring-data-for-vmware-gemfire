# Spring Data GemFire Driver SPI — Rationale & Executive Summary

**Status:** Revised design signed off in full by Udo Kohlmeyer on 2026-10-20 — see §8. Approval includes the no-static-state rule, SPI ABI compatibility and driver version lock-step, and the finalized driver release and binding details. Implementation-ready.
**Companion document:** `wayfinder/technical-spec.md` (full technical design — read this document first; consult the spec for implementation detail)

## 1. The problem

Spring Data GemFire today compiles directly against native GemFire (`org.apache.geode.*`) types. That means:

- **One Spring Data GemFire release supports exactly one GemFire version family.** Supporting a new GemFire version, or keeping an older one alive, means branching or forking the project.
- Every GemFire API or behavior change is a direct, often breaking, change to Spring Data GemFire itself — the two projects are coupled at the source level, not just at the runtime level.
- There is no clean seam at which "the version of GemFire we're talking to" can vary independently of "the version of Spring Data GemFire we're running."

This is a structural limitation, not a bug — it can't be fixed with a patch. It requires an architectural change to where the boundary between the two projects sits.

## 2. The proposed fix, in one paragraph

Introduce a **Driver SPI** — a stable, version-agnostic contract, modeled on the JDBC driver pattern — that sits between Spring Data GemFire and GemFire itself. Spring Data GemFire is rewritten to depend only on this SPI, with **zero direct references to native GemFire types**. Each supported GemFire version gets its own small, independent **Driver Impl** module that implements the SPI against that version's native API. At runtime, the application picks its GemFire version the same way a JDBC application picks its database: by which driver jar is on the classpath. One Spring Data GemFire release then supports many GemFire versions, and a new GemFire version can be supported by shipping a new Driver Impl — without touching Spring Data GemFire itself.

## 3. Why this model (JDBC pattern)

The JDBC driver pattern was chosen because it is a proven, widely understood solution to exactly this problem — "one client library, many backend versions" — and it keeps the coupling in one place: the SPI contract. Alternatives considered and rejected:

- **Formalizing the existing prior-art branch (`unified_driver`/GUD) as-is.** That branch reached compile-complete status but left the majority of its tracked correctness issues open. It's used here as informative background (its invariants — no native deps, no reflection, `ServiceLoader` discovery — carried forward) but not adopted wholesale; this is a clean-slate design, not a rescue of that branch.
- **An additive/non-breaking shim layer** that keeps today's native-typed APIs working alongside a new abstraction. Rejected because a permanent dual-API surface would mean Spring Data GemFire never actually sheds its native dependency — it would just gain a second one. This is deliberately a **breaking major version migration**.

## 4. What stakeholders are being asked to accept

Five consequences fall directly out of the design and are worth surfacing explicitly before sign-off, rather than discovering them mid-implementation:

1. **This is a breaking major version.** Application code using Spring Data GemFire's current native-typed APIs (e.g., annotation attributes typed as `org.apache.geode.cache.DataPolicy`) will need to migrate to the new SPI-owned equivalents. There is no permanent compatibility shim — see §7 of the technical spec.
2. **No feature is silently dropped.** Every native-typed API being replaced has an SPI-owned equivalent; nothing is removed for lack of a home. (There is one governed exception: a feature can, rarely, leave the SPI — but only at an SPI major version bump, and only once no supported GemFire version still needs it, per the support-matrix mechanism below.)
3. **Failures are fail-fast, not degraded.** Version mismatches are checked along two independent lines at startup — whether the Driver Impl was built against an SPI version the running SPI can support (same major, runtime minor at least the one it was built against), and whether the GemFire version on the classpath is exactly the one the Driver Impl was built for (a Driver Impl's version is the GemFire version it wraps) — and either one failing means the application will not start. A feature the Driver Impl does not support throws a clear, typed exception instead, at startup where the feature is configuration-time. Never silently no-op or degrade. This trades a softer failure mode for predictability and easier diagnosis.
4. **Driver Impls become a supported-matrix commitment.** Committing to this model means committing to maintaining a Driver Impl per supported GemFire version, and to publishing and honoring a support matrix that mirrors GemFire's own end-of-life dates. This is an ongoing release-process obligation, not a one-time cost.
5. **The SPI adds no guarantees the underlying GemFire client doesn't already provide** (e.g., no manufactured thread-safety contract). It's a thin pass-through by design, which keeps it simple and honest, but means the SPI cannot be treated as a place to "fix" underlying GemFire behavior — that expectation should be corrected early.

## 5. What this buys us

- **Decoupled release cadences.** GemFire version support becomes a matter of shipping a new Driver Impl module, not a new Spring Data GemFire release.
- **Reduced blast radius for GemFire changes.** A GemFire API change is absorbed by one Driver Impl, translated to the stable SPI contract — it does not ripple through the whole Spring Data GemFire codebase.
- **A governed, predictable deprecation path.** The support matrix (mirroring GemFire's own EOL policy) gives a documented answer to "when can we stop supporting GemFire version X," instead of an ad hoc decision each time.

## 6. Formerly open questions — now resolved

The five areas the technical spec originally left open have all since been resolved (each is a linked decision ticket under `wayfinder/issues/`; see the technical spec's Appendix A for full traceability):

- **Module/package layout and artifact naming** — two modules (`spring-data-gemfire-driver-spi`, `spring-data-gemfire-driver-core`), plus one `spring-data-gemfire-driver-<version>` module per supported GemFire version, all under a new `com.vmware.gemfire.driver` package family, alongside the existing `spring-data-vmware-gemfire`/`spring-test-vmware-gemfire` modules in this same repo.
- **Migration sequencing** — a bottom-up subsystem order (the shared cache/util seam first, then small low-risk packages, then config wiring, then the widest-radius consumers last), on a dedicated integration branch kept separate from the repo's existing per-version-combination support branches, merged into `develop` once at cutover.
- **Testing strategy** — an official mock Driver Impl becomes the standard unit-test runtime, with the unit-test classpath build-enforced to stay native-GemFire-free, and one integration test configuration per Driver Impl version rather than a single parameterized suite.
- **Initial version list and support matrix scope** — the concrete initial GemFire version list is deferred to a later release-planning decision; the support matrix itself is narrowed to a documentation/governance-only artifact with no runtime role (superseded, for runtime purposes, by the two self-declared compatibility checks described in §4 item 3 above).
- **Review/sign-off process** — a fresh sign-off round is requested once, when the whole spec reaches implementation-ready (this point), not per item; Udo Kohlmeyer is the sole approver; post-implementation-start revisions are handled case-by-case (significant changes get a new sign-off round, smaller ones are handled inline).

One further item was raised during this work and explicitly marked **out of scope** rather than resolved: whether `spring-test-vmware-gemfire` itself should migrate off its current direct dependency on native GemFire. It keeps that dependency; this is not part of this effort.

## 7. What sign-off means

Approving this document means agreeing to the architectural direction in §2–§4 — the SPI/Driver Impl split, the JDBC-style pattern, the breaking-major-version approach, and the fail-fast philosophy — as the basis for a full implementation plan. The initial §8 entry approved §2–§4 only; the subsequent entries approved the complete design and then its revisions. The 2026-10-20 entry covers the current technical spec and this rationale, including the finalized driver release and binding details. The design is approved for implementation under the review process resolved in §6.

## 8. Sign-off log

| Approver | Date | Scope of approval |
|---|---|---|
| Udo Kohlmeyer | 2026-08-19 | Architectural direction (§2–§4) as basis for the full implementation plan, per §7. Open items in §6 remain unresolved and out of scope of this approval. |
| Udo Kohlmeyer | 2026-08-20 | Full design, implementation-ready. All items formerly open in §6 are resolved and reflected in `technical-spec.md`; approval now covers the complete document (§1–§10 and Appendix A) as the basis for implementation, per [ticket 17](issues/17-spec-review-sign-off-process.md)'s once-at-implementation-ready cadence. |
| Udo Kohlmeyer | 2026-10-20 | Full revised technical design and companion rationale approved for implementation, including [No static state or singletons](issues/19-no-static-state-or-singletons.md), [SPI ABI compatibility and driver version lock-step](issues/20-spi-abi-compatibility-and-driver-version-lockstep.md), and [Finalize provisional driver release and binding details](issues/21-finalize-provisional-driver-release-and-binding-details.md). Explicit approval in this session: “ok... docs sign-off”. |
