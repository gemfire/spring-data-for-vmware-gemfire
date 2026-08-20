## Destination

Two documents, planning/spec only — no production code changes happen on this map:

1. A full technical design spec (interfaces, module boundaries, migration plan) for implementation handoff, defining a Driver SPI/API layer that abstracts the GemFire version from Spring Data GemFire, so that Spring Data GemFire compiles with zero direct references to native GemFire (`org.apache.geode.*`) and a single Spring Data GemFire release is compatible with multiple GemFire versions via swappable Driver Impls.
2. A shorter companion rationale/executive-summary document for stakeholder review and sign-off.

See [Deliverables and destination](issues/01-deliverables-and-destination.md).

## Notes

- Domain: GemFire version abstraction via a JDBC-driver-style SPI/Driver Impl model, as a breaking major version migration of Spring Data GemFire.
- Skills to consult when working any ticket: `/grilling`, `/domain-modeling`.
- Standing architectural stance carried across every decision: the **SPI is a thin delegate** around the Driver Impl — it adds no contract, guarantee, or behavior beyond what the underlying Driver Impl naturally provides (see [Thread safety stance](issues/09-thread-safety-stance.md)).
- Standing philosophy: **fail fast** everywhere — unsupported capability invocation, SPI/Driver Impl version incompatibility, out-of-support Driver Impl at bind time — with descriptive, typed exceptions, never silent degradation or soft-deprecation grace periods.
- The `udo.kohlmeyer/unified_driver` branch (`gud-api`, `gud-core`, `gud-driver-gemfire-10.x`, `Gud*` types) is informative background only, not something this spec formalizes as-is or reuses directly. See [Prior art disposition](issues/02-prior-art-disposition.md).

## Decisions so far

- [Deliverables and destination](issues/01-deliverables-and-destination.md) — two documents: full technical spec + rationale/executive-summary companion; this map plans/specifies only, no code.
- [Prior art disposition](issues/02-prior-art-disposition.md) — `unified_driver`/GUD branch is informative background only (clean-slate design); `Gud*` naming is out of scope.
- [Driver SPI architecture model](issues/03-driver-spi-architecture-model.md) — JDBC-driver pattern: Driver SPI/API bundled with Spring Data GemFire (zero native GemFire refs), Driver Impl per GemFire version as a swappable runtime dependency, `ServiceLoader`-based discovery.
- [Major version breaking migration](issues/04-major-version-breaking-migration.md) — this is a breaking major version migration; all native type references, including annotation attribute types, move to SPI-owned types, no permanent compatibility facade.
- [Annotation attribute types](issues/05-annotation-attribute-types.md) — annotation attributes that reference native GemFire types are re-typed to SPI-owned delegation enums (Option A), not removed.
- [Exception hierarchy](issues/06-exception-hierarchy.md) — Driver Impls translate native GemFire exceptions into an SPI-owned exception hierarchy; Spring's `DataAccessException` translation retargets to translate from SPI exceptions.
- [Unsupported feature fail-fast](issues/07-unsupported-feature-fail-fast.md) — invoking a capability the bound Driver Impl doesn't support fails fast at the call site with a descriptive, typed exception.
- [Capability enum evolution and removal](issues/08-capability-enum-evolution-and-removal.md) — closed capability enum, always a superset tracking the latest GemFire version; backward compatible with older impls (fail-fast on gaps), no forward compatibility; removal gated by Driver Impl end-of-support.
- [Thread safety stance](issues/09-thread-safety-stance.md) — thread-safety is impl-defined and undocumented by the SPI; generalizes into the cross-cutting "SPI as thin delegate" principle.
- [SPI versioning scheme](issues/10-spi-versioning-scheme.md) — **superseded by [ticket 18](issues/18-spi-impl-generation-range-compatibility.md)**: `driver-spi`/`driver-impl` each declare a generation range (`[spiMin,spiMax]`/`[implMin,implMax]`), checked via range-intersection and fail-fast at `ServiceLoader` bind time; `driver-core` exposes the effective intersected range for diagnostics; declared Impl ranges are backed by continuous CI enforcement of per-generation integration tests.
- [Driver Impl support matrix and bind-time version check](issues/11-driver-impl-support-matrix-and-bind-time-check.md) — **amended, see [ticket 16](issues/16-initial-version-list-and-matrix-scope.md)**: a documented support matrix mirroring GemFire's official EOL dates remains as a documentation/governance-only artifact (process-enforced, no runtime role); the matrix-derived bind-time minimum-version floor check is dropped, superseded by a self-declared Driver Impl compatibility check (mechanics open, see [ticket 15](issues/15-driver-impl-self-declared-gemfire-compatibility-mechanics.md)).
- [SPI module/package layout](issues/12-spi-module-package-layout.md) — two modules (`spring-data-gemfire-driver-spi`, `spring-data-gemfire-driver-core`), same-repo Driver Impl modules named `spring-data-gemfire-driver-<version>`, package root `com.vmware.gemfire.driver` split into `.spi`/`.core`, Driver Impls depend only on `driver-spi`.
- [Migration/rollout sequencing](issues/13-migration-rollout-sequencing.md) — bottom-up subsystem order (cache/util/root → small packages → config → client/function/repository/mapping); migration on a dedicated `driver-spi-migration` integration branch, separate from `support/*` branches; in-flight `develop` fixes rebased in periodically.
- [Testing strategy](issues/14-testing-strategy.md) — official mock Driver Impl becomes the unit-test runtime; unit-test classpath build-enforced native-free; one `integrationTest` Gradle configuration per Driver Impl version rather than a single parameterized suite; extended by tickets 15/18 to back both axes' self-declared ranges with continuous CI enforcement.
- [Initial version list and matrix scope](issues/16-initial-version-list-and-matrix-scope.md) — the concrete initial GemFire version list is explicitly deferred to a later release-planning decision, out of scope for this spec; the support matrix is narrowed to a documentation/governance-only artifact with no runtime role (see ticket 11's amendment).
- [Spec review/sign-off process](issues/17-spec-review-sign-off-process.md) — one fresh sign-off entry requested at implementation-ready (not per-item); sole approver Udo Kohlmeyer; post-implementation-start spec revisions are case-by-case — significant changes that break completed decisions or change approach require a new sign-off round, smaller changes are handled inline.
- [SPI-vs-Driver-Impl generation/range compatibility model](issues/18-spi-impl-generation-range-compatibility.md) (Axis A) — `driver-spi` and each `driver-impl` self-declare generation ranges (`[spiMin,spiMax]` / `[implMin,implMax]`); bind-time compatibility is range-intersection, not SemVer (supersedes [ticket 10](issues/10-spi-versioning-scheme.md)); `driver-core` computes/exposes the effective intersected range for diagnostics; declared Impl ranges must be backed by continuous CI enforcement of per-generation integration tests (extends [ticket 14](issues/14-testing-strategy.md)); capability semantics are immutable once introduced, formalized as an amendment to [ticket 08](issues/08-capability-enum-evolution-and-removal.md).
- [Driver Impl self-declared GemFire-version compatibility mechanics](issues/15-driver-impl-self-declared-gemfire-compatibility-mechanics.md) (Axis B) — each Driver Impl self-declares a native-engine range, `[nativeMin,nativeMax]`, in real GemFire product version numbers (distinct from Axis A's generation range); bind-time check is JDBC-`acceptsURL`-style — the Impl returns a verdict from a `GemFireVersion.getGemFireVersion()` classpath check, and `driver-core` throws centrally on a negative verdict; runs sequenced after, and independently from, Axis A's check; live-cluster `MemberMXBean` check explicitly deferred past v1; declared ranges backed by continuous CI enforcement extending [ticket 14](issues/14-testing-strategy.md), symmetric with ticket 18; no change to capability invocation-time fail-fast (tickets 07/08 stand).

## Not yet specified

_(none remaining)_

## Out of scope

- Any production code changes as part of this map — the destination is the spec and rationale documents only. See [Deliverables and destination](issues/01-deliverables-and-destination.md).
- The `Gud*` naming convention and any other naming carried over from the `unified_driver`/GUD prior art. See [Prior art disposition](issues/02-prior-art-disposition.md).
- Whether `spring-test-vmware-gemfire` itself should be migrated to depend only on `driver-spi`/the mock Driver Impl, removing its current direct dependency on native GemFire — called out of scope for this spec (see [Testing strategy](issues/14-testing-strategy.md) open follow-up).
