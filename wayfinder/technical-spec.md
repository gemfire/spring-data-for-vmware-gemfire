# Spring Data GemFire — Driver SPI Technical Design Specification

**Status:** Signed off in full, implementation-ready — see `wayfinder/rationale.md` §8 (2026-08-20 entry). All previously-open items resolved (see `wayfinder/map.md`).
**Companion document:** `wayfinder/rationale.md` (executive summary / stakeholder sign-off)
**Traceability:** every normative statement below is sourced from a resolved decision under `wayfinder/issues/`; see [Appendix A](#appendix-a-decision-traceability).

## 1. Purpose and scope

This spec defines a **Driver SPI/API layer** that abstracts the GemFire version out of Spring Data GemFire. After this migration:

- Spring Data GemFire compiles against the Driver SPI only. It carries **zero** direct references to native GemFire (`org.apache.geode.*`) types, anywhere in main or test sources.
- A single Spring Data GemFire release (per Spring Data version) runs against **multiple GemFire versions** by swapping which Driver Impl is present on the runtime classpath.
- The Driver Impl handles feature and API differences between GemFire versions **transparently** — Spring Data GemFire code is written once, against the SPI's stable contract.

This is a **breaking major version migration**, not an additive one. There is no permanent compatibility facade preserving today's native-typed APIs; they are replaced, not shimmed.

Out of scope for this spec: the `Gud*` naming convention or any other artifact of the `unified_driver` prior-art branch, any production code changes (this document is the handoff artifact that precedes that work), and whether `spring-test-vmware-gemfire` migrates off its current direct native GemFire dependency (§11).

## 2. Background and prior art

A prior implementation exists on branch `udo.kohlmeyer/unified_driver` (`gud-api`, `gud-core`, `gud-driver-gemfire-10.1/10.2/10.3`), which reached a compile-complete state but left 7 of 9 tracked correctness/test issues open. That branch is treated here as **informative background only**: its architecture invariants — zero native GemFire deps in the Spring Data module, no reflection, `ServiceLoader`-based discovery, a mock driver for unit tests — are useful precedent, but this spec is a clean-slate design and does not inherit its module boundaries, type names, or unresolved defects.

## 3. Architecture overview

Model the abstraction on the **JDBC driver pattern**:

```
┌───────────────────────────────┐
│   spring-data-vmware-gemfire   │   compiles against SPI + core only
│   (repositories, cache config, │   zero org.apache.geode.* refs
│   annotations, ...)            │
└───────────────┬─────────────────┘
                │ depends on
┌───────────────▼─────────────────┐
│   spring-data-gemfire-driver-core │   orchestration: ServiceLoader
│   (GemFireDriverManager,          │   lookup, Axis A + Axis B
│   bind-time validation)           │   bind-time checks (§5.2, §6)
└───────────────┬─────────────────┘
                │ depends on
┌───────────────▼─────────────────┐
│   spring-data-gemfire-driver-spi  │   the stable contract:
│   (GemFireDriver, DriverMetadata, │   interfaces, capability enum,
│   Capability, exceptions,         │   exception hierarchy, SPI-owned
│   SPI-owned config enums)         │   annotation attribute enums
└───────────────┬─────────────────┘
                │ implemented by (one jar per GemFire version)
     ┌──────────┼──────────┬─────────────────┐
┌────▼────┐ ┌───▼─────┐ ┌──▼──────┐   ┌──────▼──────┐
│ driver- │ │ driver- │ │ driver- │...│ driver-mock  │
│ 10.1    │ │ 10.2    │ │ 10.3    │   │ (test only,  │
│         │ │         │ │         │   │  see §10)    │
└─────────┘ └─────────┘ └─────────┘   └──────────────┘
```

- **`spring-data-gemfire-driver-spi`** — the pure SPI contracts (`GemFireDriver`, `DriverMetadata`, `Capability`/`CapabilityAware`, the SPI-owned exception hierarchy, SPI-owned config enums). Zero dependency on `driver-core` or on any GemFire jar. This is what a Driver Impl author compiles against.
- **`spring-data-gemfire-driver-core`** — the manager/orchestration module: `GemFireDriverManager`'s `ServiceLoader` lookup and the two independent bind-time compatibility checks (§5.2, §6). Depends only on `driver-spi`.
- **`spring-data-gemfire-driver-<gemfire-version>`** (e.g. `spring-data-gemfire-driver-10.3`) — one module per supported GemFire version, packaged as an independent, swappable runtime artifact. Depends on the native GemFire jars for that version plus `driver-spi` only — **never** on `driver-core`, never on another Driver Impl module, never on `spring-data-vmware-gemfire`.
- **`spring-data-vmware-gemfire`** — the existing Spring Data GemFire module, depending on both `driver-spi` and `driver-core`.
- **Discovery**: Driver Impls register via **`java.util.ServiceLoader`**. Spring Data GemFire never references a concrete Driver Impl class; it resolves one at startup through `GemFireDriverManager` (§5.2).

This mirrors JDBC deliberately: an application picks its GemFire version by putting the matching driver jar on the classpath, the same way a JDBC application picks its database by classpath, not by code change.

All of the above live in this same repo as additional Gradle subprojects, at least for this initial migration — decoupled release cadence doesn't require decoupled repos, and keeping them together makes the "zero native GemFire outside Driver Impl modules" build check and SPI/impl lockstep easier during the breaking migration. Splitting into separate repos remains a future option once the SPI contract has stabilized, not a decision this spec makes now.

## 4. Module boundaries and build dependencies

Maven/Gradle group `com.vmware.gemfire`, alongside the existing `spring-data-vmware-gemfire` / `spring-test-vmware-gemfire`. Package root: a new top-level family, `com.vmware.gemfire.driver`, independent of `org.springframework.data.gemfire` — signaling the SPI is a distinct, independently-versioned artifact rather than an internal package of Spring Data GemFire. Split by module: `com.vmware.gemfire.driver.spi` in `driver-spi`, `com.vmware.gemfire.driver.core` in `driver-core`. Note that it is the **module boundary** (separate jars) that actually keeps Driver Impls off `driver-core`'s classpath — the package split is for readability, not enforcement.

| Module | Depends on | Contains |
|---|---|---|
| `spring-data-gemfire-driver-spi` | (nothing GemFire-specific) | `GemFireDriver`, `DriverMetadata`, `Capability`/`CapabilityAware`, SPI exception hierarchy, SPI-owned annotation attribute enums |
| `spring-data-gemfire-driver-core` | `spring-data-gemfire-driver-spi` | `GemFireDriverManager` — `ServiceLoader` discovery, Axis A + Axis B bind-time checks (§5.2, §6) |
| `spring-data-vmware-gemfire` | `spring-data-gemfire-driver-spi`, `spring-data-gemfire-driver-core` | Repositories, cache/region configuration, annotation processing, function execution, CQ, PDX, WAN support — all routed through the SPI |
| `spring-data-gemfire-driver-<gemfire-version>` (e.g. `spring-data-gemfire-driver-10.3`) | `spring-data-gemfire-driver-spi` only, native `org.apache.geode:geode-core:<X.Y>` (and related artifacts) | Concrete `GemFireDriver` implementation, native↔SPI type/exception translation for that GemFire version |
| `spring-data-gemfire-driver-mock` (test only — see §10) | `spring-data-gemfire-driver-spi` only | In-memory mock `GemFireDriver` implementation; standard unit-test runtime (§10) |

**Build-time enforcement:** add an architecture fitness function (e.g. an ArchUnit rule or equivalent banned-import check) to the `spring-data-vmware-gemfire` build that fails the build on any `org.apache.geode.*` import in main **or test** sources — the unit-test classpath is build-enforced to contain no native GemFire, only `driver-spi` and the mock Driver Impl (§10). This is the mechanical guarantee behind "zero native GemFire references" — a module compiling cleanly today can regress silently on a later PR without it.

## 5. Core SPI contracts

### 5.1 `GemFireDriver`

There are **two independent compatibility axes**, using two distinct value spaces (§6.1–§6.3): Axis A is the SPI-capability-generation range (`[implMin, implMax]`, defined entirely by which `Capability` enum entries exist at that generation); Axis B is the real GemFire product-version range (`[nativeMin, nativeMax]`, e.g. `10.1.0`–`10.3.x`) the Driver Impl has actually been built/tested against. A Driver Impl declares both.

```java
package com.vmware.gemfire.driver.spi;

public interface GemFireDriver {

    DriverMetadata metadata();

    /**
     * Axis B self-check (§6.3): does this Driver Impl accept the GemFire
     * version actually present on the classpath it's co-located with?
     * A verdict, never a throw — driver-core is the sole thrower (§5.2).
     */
    boolean acceptsRuntimeGemFireVersion(String gemFireVersion);

    /**
     * Establishes the runtime connection this driver mediates.
     * Thread-safety is whatever the underlying GemFire client/cache
     * naturally provides — the SPI adds no additional guarantee. See §5.6.
     */
    DriverConnection connect(DriverConfiguration configuration) throws DriverBindException;
}

public interface DriverMetadata {
    GenerationRange supportedGenerationRange();  // Axis A — [implMin, implMax], §6.1/§6.2
    NativeVersionRange supportedNativeRange();   // Axis B — [nativeMin, nativeMax], §6.3
    Set<Capability> capabilities();              // closed enum, §5.3
}
```

### 5.2 `GemFireDriverManager` — discovery and bind-time validation

```java
package com.vmware.gemfire.driver.core;

public final class GemFireDriverManager {

    private static final GenerationRange CURRENT_SPI_RANGE = GenerationRange.of(SPI_MIN, SPI_MAX); // §6.1
    private static volatile GemFireDriver bound; // resolved once, cached

    public static GemFireDriver driver() {
        GemFireDriver result = bound;
        if (result == null) {
            synchronized (GemFireDriverManager.class) {
                result = bound;
                if (result == null) {
                    result = bound = resolveAndValidate();
                }
            }
        }
        return result;
    }

    private static GemFireDriver resolveAndValidate() {
        GemFireDriver driver = ServiceLoader.load(GemFireDriver.class)
            .findFirst()
            .orElseThrow(NoDriverFoundException::new);

        DriverMetadata metadata = driver.metadata();
        validateGenerationCompatibility(metadata);      // §6.2 — Axis A, checked first
        validateRuntimeGemFireVersion(driver, metadata); // §6.3 — Axis B, sequenced after and independent
        return driver;
    }

    // Axis A — range-intersection bind check (§6.2). Not itself load-bearing for
    // "no forward compatibility": the closed Capability enum (§5.3) already makes
    // capabilities beyond CURRENT_SPI_RANGE.max() uninvocable regardless of what
    // any impl declares. This check is a coarser, earlier, more diagnosable rejection.
    private static void validateGenerationCompatibility(DriverMetadata metadata) {
        GenerationRange implRange = metadata.supportedGenerationRange();
        if (!implRange.intersects(CURRENT_SPI_RANGE)) {
            throw new SpiGenerationIncompatibleException(metadata, CURRENT_SPI_RANGE);
        }
        // Effective intersected range, exposed for diagnostics only (§6.2):
        // GenerationRange effective = implRange.intersectWith(CURRENT_SPI_RANGE);
    }

    // Axis B — JDBC-acceptsURL-style verdict check (§6.3). The Impl returns a
    // boolean; driver-core alone throws, centralizing the fail-fast contract
    // instead of duplicating exception construction across every Driver Impl.
    private static void validateRuntimeGemFireVersion(GemFireDriver driver, DriverMetadata metadata) {
        String runtimeVersion = GemFireVersion.getGemFireVersion(); // classpath-local, zero-dependency
        if (!driver.acceptsRuntimeGemFireVersion(runtimeVersion)) {
            throw new NativeVersionIncompatibleException(metadata, runtimeVersion);
        }
    }
}
```

Binding happens once, lazily, on first use, with double-checked locking over a `volatile` field — the standard safe-publication idiom for a value computed once and read frequently from multiple threads at application startup. Concurrent callers during startup either race harmlessly into the synchronized block or read the already-published `bound` reference; none observe a partially-constructed driver.

If exactly one `GemFireDriver` is expected on the classpath and more than one is found, that is also a bind-time failure (`AmbiguousDriverException`) rather than an arbitrary pick — silently choosing "first found" would make classpath ordering a hidden, non-reproducible source of behavior differences between environments.

The two axis checks are deliberately kept **sequenced and independent**: Axis A runs first, Axis B runs after, each can fail fast on its own with its own typed exception, and passing one implies nothing about the other. A live-cluster variant of the Axis B check (querying `MemberMXBean`/`ManagementService` on an already-joined distributed system, for client/server deployments where the client jar version and server cluster version can diverge) is explicitly deferred past v1; only the classpath-local check above gates bind time today.

### 5.3 Capability model

```java
package com.vmware.gemfire.driver.spi;

public enum Capability {
    CONTINUOUS_QUERY,
    PDX_SERIALIZATION,
    WAN_GATEWAY_REPLICATION,
    LUCENE_INDEXING,
    OFF_HEAP_REGIONS,
    // grows immediately when GemFire ships a new feature; never shrinks
    // except per the removal rule in §6.5
    ;
}

public interface CapabilityAware {
    Set<Capability> supportedCapabilities();

    default void requireCapability(Capability capability) {
        if (!supportedCapabilities().contains(capability)) {
            throw new UnsupportedCapabilityException(capability, this);
        }
    }
}
```

`Capability` is a **closed enum**, evolved only in coordinated SPI releases — never an open/extensible registration mechanism. It is always a **superset** of what any single Driver Impl currently implements: when GemFire ships a new feature, the SPI adds the capability immediately, without waiting for every Driver Impl to catch up. This is what makes backward compatibility possible (an older Driver Impl simply reports a smaller `supportedCapabilities()` set) and is why there is no forward compatibility case to handle (a Driver Impl cannot require a capability the SPI doesn't yet have — see §6).

Every SPI entry point that maps to an optional GemFire feature calls `requireCapability(...)` before delegating, so an unsupported invocation fails fast at the call site with a typed `UnsupportedCapabilityException` naming the missing capability — never a silent no-op, never a bare `UnsupportedOperationException`.

**Capability immutability invariant.** Once introduced, a capability enum constant's semantics are fixed at the generation it's introduced and never change underneath existing generations. A non-additive behavior change to an existing capability is not a modification of that constant — it must retire the old constant (subject to the same end-of-support-gated removal rule above) and introduce a new one. This invariant is what makes Axis A's range-intersection bind check (§6.2) correct: it depends on a wide-range Driver Impl behaving identically, at every generation within its declared range, to an Impl whose range tops out there.

### 5.4 Exception hierarchy

```java
package com.vmware.gemfire.driver.spi;

public abstract class GemFireDriverException extends RuntimeException { }

// bind-time failures
public class DriverBindException extends GemFireDriverException { }
public class NoDriverFoundException extends DriverBindException { }
public class AmbiguousDriverException extends DriverBindException { }
public class SpiGenerationIncompatibleException extends DriverBindException { }  // §6.2 — Axis A: generation ranges don't intersect
public class NativeVersionIncompatibleException extends DriverBindException { }  // §6.3 — Axis B: impl rejects the runtime GemFire version

// invocation-time failures
public class UnsupportedCapabilityException extends GemFireDriverException { }
public class DriverOperationException extends GemFireDriverException { }        // wraps a translated native failure
```

Both bind-time exceptions above are thrown **only by `driver-core`**, never by a Driver Impl: Axis A's check is a pure range-intersection test `driver-core` performs itself against the two declared ranges (§6.2), and Axis B's check has the Driver Impl return a boolean/enum verdict from `acceptsRuntimeGemFireVersion(...)` (§5.1) which `driver-core` alone turns into a thrown exception (§6.3). Centralizing the throw in `driver-core` avoids duplicating exception-construction logic across every Driver Impl.

Driver Impls are responsible for catching native GemFire exceptions at the boundary and translating them into this hierarchy (typically wrapped as `DriverOperationException` or a more specific subtype, at the Driver Impl author's discretion, provided it stays within this hierarchy). Spring Data GemFire's existing `GemfireCacheUtils`-style translator — which today converts native GemFire exceptions to Spring's `DataAccessException` — is retargeted to translate **from `GemFireDriverException`**:

```java
public class GemFireDriverExceptionTranslator implements PersistenceExceptionTranslator {
    @Override
    public DataAccessException translateExceptionIfPossible(RuntimeException ex) {
        if (ex instanceof GemFireDriverException driverEx) {
            return translate(driverEx);
        }
        return null;
    }
}
```

Spring Data GemFire code never catches, references, or type-tests a native GemFire exception class.

### 5.5 Annotation attribute types

Annotation attributes currently typed as native GemFire enums (e.g. `@ClientCacheApplication(dataPolicy = DataPolicy.PERSISTENT_REPLICATE)`) are retyped to **SPI-owned delegation enums** that mirror the native ones. No annotation-driven capability is removed.

```java
package com.vmware.gemfire.driver.spi.config;

public enum DataPolicy {
    EMPTY, NORMAL, PRELOADED, PARTITION,
    PERSISTENT_PARTITION, REPLICATE, PERSISTENT_REPLICATE;
}
```

Each Driver Impl maps every SPI-owned config enum value to/from its native GemFire equivalent. Because this is a breaking major version change (§4/§6 of the map's decisions), there is no dual-typed overload retained for source compatibility — call sites and annotation attributes take the SPI type only.

### 5.6 Thread-safety stance

The SPI documents **no** thread-safety guarantee beyond what the bound Driver Impl itself provides. This is a deliberate, general design stance, not a one-off answer for any single interface: **the SPI is a thin delegate**. It never manufactures a stronger contract (thread-safety, ordering, atomicity, or otherwise) than the underlying Driver Impl naturally offers, and Driver Impl authors must document their own concurrency behavior since the SPI will not paper over the difference. Apply this as the default lens for any SPI design question not explicitly settled elsewhere in this spec: when in doubt, pass the underlying behavior through unchanged rather than adding SPI-level behavior.

## 6. Versioning and support lifecycle

### 6.1 SPI versioning — generation numbers (Axis A)

`driver-spi`'s external release version number (SemVer) is **not** the compatibility unit — it says nothing about which GemFire feature generation the SPI exposes. Instead, `driver-spi` owns an internal **generation numbering scheme**, defined entirely by which entries exist in the `Capability` enum (§5.3) as of that generation: a generation bump happens only when GemFire's feature surface actually changes (e.g. GemFire 10.1/10.2 map to generation 2; GemFire 10.3 adds capabilities and becomes generation 3). `driver-spi` declares the range of generations it currently supports, `[spiMin, spiMax]`; this range narrows as old generations' backing GemFire versions go EOL (per the support matrix, §6.3) and widens as new generations ship. `driver-spi`'s own SemVer release number, if kept at all, is purely a packaging/distribution identifier — never consulted for compatibility decisions.

### 6.2 Driver Impl generation-range compatibility (Axis A)

Each Driver Impl self-declares its own compatible generation range, `[implMin, implMax]` — a range, not a single generation, so one Impl can span multiple GemFire feature generations. At `ServiceLoader` bind time, `GemFireDriverManager` checks this range against `driver-spi`'s own `[spiMin, spiMax]` via **range intersection**, not containment: binding succeeds iff `implMax >= spiMin && implMin <= spiMax`, and fails fast with `SpiGenerationIncompatibleException` otherwise. This mirrors the strongest real-world precedents for mutual range declaration + intersection — Kafka's `ApiVersionsRequest`/`ApiVersionsResponse` (KIP-35) and, most closely, MongoDB's wire-protocol `minWireVersion`/`maxWireVersion` negotiation — both of which declare ranges on both sides, compute the overlap, and hard-fail (no soft/degraded connection) when ranges are disjoint, with neither side's declared range externally validated.

`driver-core` additionally computes and exposes the **effective intersected range**, `[max(implMin, spiMin), min(implMax, spiMax)]`, for diagnostics/introspection. This is not load-bearing for correctness — see below — but without it a bind-time failure could only say "incompatible," not "here's the actual usable generation window," which falls short of this design's fail-fast-with-descriptive-errors philosophy.

**Why this preserves "no forward compatibility" (§5.3, §6.4) without the intersection check itself being load-bearing:** the real ceiling is the **closed `Capability` enum** — a given `driver-spi` jar's enum physically contains only constants up to `spiMax`, so neither Spring Data GemFire nor a Driver Impl compiled against that jar can *reference* a capability beyond it; there is no constant to name. The range-intersection check above is a valuable earlier, coarser rejection (catching wholly-disjoint Impls before binding), and per-capability invocation-time fail-fast (§5.3, ticket 07) is a second, independent backstop — but neither is what ultimately makes forward compatibility structurally impossible; the absent vocabulary is.

**A self-declared `implMin`/`implMax` is only trustworthy if backed by continuous CI enforcement**, not a bare claim: a Driver Impl's declared range must have a passing `integrationTest` configuration (§10) for every generation in that range, and this suite is re-run whenever `driver-spi`/`driver-core` change — not just once at the Driver Impl's own release. A point-in-time-only check reproduces the same rot failure mode observed in IntelliJ plugin `since-build`/`until-build` declarations, npm `engines`, and VS Code's `engines.vscode` — all statically-declared ranges that can silently drift out of truth without continuous enforcement.

### 6.3 Driver Impl native-GemFire-version compatibility (Axis B)

Axis A (above) answers "which SPI capability generations does this Driver Impl expose" — a distinct question from "does this Driver Impl actually work with the real GemFire product version present at runtime." That second, orthogonal axis is Axis B:

- **Self-declared native range.** Each Driver Impl self-declares `[nativeMin, nativeMax]` in **real GemFire product version numbers** (e.g. `10.1.0`–`10.3.x`) — a different value space from Axis A's generation numbers.
- **Verdict-not-throw bind-time check, modeled on JDBC's `Driver.acceptsURL`/`connect`.** At bind time, `GemFireDriverManager` calls `driver.acceptsRuntimeGemFireVersion(...)` (§5.1), which the Driver Impl implements by reading `org.apache.geode.internal.GemFireVersion.getGemFireVersion()` — a static, local, zero-dependency read of the build-embedded version metadata packaged inside the GemFire jars actually on the classpath — and comparing it against its own declared range. The Impl returns a boolean/enum verdict; it never throws. `GemFireDriverManager` alone throws `NativeVersionIncompatibleException` on a negative verdict, centralizing the fail-fast contract instead of duplicating exception-construction logic across every Driver Impl.
- **Sequenced after, and independent from, Axis A** (§5.2). Either check can fail fast on its own; passing one implies nothing about the other.
- **Live-cluster check deferred past v1.** A live check against an already-joined distributed system's actual version (via `org.apache.geode.management.MemberMXBean`/`ManagementService`, relevant when a client jar's version and the server cluster's version could diverge) is a candidate for a future ticket if a real version-skew scenario surfaces in practice — it is not built speculatively now. Only the classpath-local `GemFireVersion` check gates v1 bind time.
- **Same continuous-CI-enforcement discipline as Axis A** (§6.2, §10): a declared `[nativeMin, nativeMax]` range is only trustworthy if backed by a passing `integrationTest` configuration against every native GemFire version in that range, re-verified on an ongoing basis.
- **No change to capability invocation-time fail-fast** (§5.3, ticket 07/08) — this axis covers only the GemFire-version compatibility gate at bind time, orthogonal to per-capability dispatch at invocation time.

### 6.4 Support matrix — documentation and governance only

A **documented support matrix** — GemFire version → Driver Impl version → supported-until — is maintained alongside the spec, mirroring GemFire's own official support-lifecycle/EOL dates rather than an independently invented policy. It is the governance source of truth for when a capability may be removed from the SPI (§6.5): removal is permitted only once no still-supported Driver Impl version exposes it. The concrete initial GemFire version list to populate it with at launch is a later release-planning decision, deliberately out of scope for this spec.

The matrix has **no runtime role**. It is enforced by process only: a release-checklist item requires checking it before any SPI release removes a capability — no runtime machinery is needed for this, and adding code to consult it at bind time would both violate the thin-delegate stance (§5.6) and duplicate what Axis A (§6.2) and Axis B (§6.3) already check via self-declared ranges. (An earlier version of this design had `GemFireDriverManager` derive a matrix-sourced minimum-version bind-time floor; that runtime check is dropped and fully superseded by Axis A's and Axis B's self-declared-range checks.)

### 6.5 Capability removal

A capability may be removed from the closed enum (§5.3) only once the support matrix (§6.4) shows no still-supported Driver Impl exposes it. Until then it remains in the enum indefinitely, even if unused by the latest GemFire version. Per §5.3's immutability invariant, removal is the *only* way an existing capability constant's presence changes — its semantics, once introduced, never change while it remains in the enum.

## 7. Migration plan

This is sequencing guidance for implementation, not a schedule commitment.

**Branch strategy.** The migration happens on a dedicated, long-lived integration branch (e.g. `driver-spi-migration`), not incrementally on `develop` — this is a breaking major version (§1) touching the whole compile surface, so landing it piecemeal on `develop` would force every routine fix in flight to rebase across a moving native-vs-SPI boundary. This branch is distinct in purpose and naming from the repo's existing `support/<gemfire>-<spring>-<sdgf>.x` per-version-combination convention (which lets already-released combinations keep receiving patches independently) — no new `support/*` branches are cut during the migration window, and that convention continues operating as-is until the SPI model supersedes it post-migration. Net-new feature work against the *current* native API is frozen only for whichever subsystems are actively being migrated at a given time (per the order in step 3 below); routine fixes to not-yet-migrated packages continue landing on `develop` and get rebased onto the integration branch periodically (e.g. weekly). The branch merges once into `develop` when cutover is complete — no permanent compatibility facade.

1. **SPI skeleton.** Stand up `spring-data-gemfire-driver-spi` and `spring-data-gemfire-driver-core` with `GemFireDriver`, `GemFireDriverManager`, `Capability`, the exception hierarchy, and the SPI-owned annotation config enums. No behavior yet — types and contracts only.
2. **Reference Driver Impl(s) and mock.** Implement `spring-data-gemfire-driver-<X.Y>` for the GemFire version(s) Spring Data GemFire currently targets, wrapping today's native calls behind the new SPI contract, and the official `spring-data-gemfire-driver-mock` for unit testing (§10).
3. **Migrate `spring-data-vmware-gemfire` subsystem-by-subsystem, bottom-up by dependency, not by size:**
   1. `cache`, `util`, and the root package (`org.springframework.data.gemfire`) — the `*FactoryBean` hierarchy, `GemfireTemplate`/`GemfireOperations`, `RegionResolver`/`CacheResolver`, the `*Converter`s. This is the seam nearly everything else calls into to obtain a `Cache`/`Region`; foundational and small enough to prove the SPI pattern cheaply before committing to it everywhere.
   2. `eviction`, `expiration`, `transaction`, `serialization`, `listener`, `domain`, `dao`, `server` — small, low-risk packages, each touching only a narrow native surface.
   3. `config` — the largest package by file count, but majority Spring bean-wiring rather than direct native usage; migrated once step 1's seam is already SPI-based, so the wiring changes are mechanical.
   4. `client`, `function`, `repository`, `mapping` — last. `client` sits directly on the SPI and is heavily geode-typed; the others are the widest-radius consumers of everything below them and benefit most from migrating against an already-proven, stable SPI.

   Every native call encountered along the way is routed through `GemFireDriverManager.driver()`.
4. **Retarget exception translation** — move `GemfireCacheUtils`-equivalent `DataAccessException` translation to consume `GemFireDriverException` (§5.4).
5. **Retarget annotation attributes** to the SPI-owned delegation enums (§5.5); update annotation processors and configuration classes accordingly.
6. **Close the gap to zero native references** — remove remaining `org.apache.geode.*` imports from main and test sources; turn on the architecture fitness function from §4 to prevent regression.
7. **Stand up the integration test matrix** (§10) — one `integrationTest` Gradle configuration per Driver Impl version, running the full test suite against each, and keep it continuously enforced as the source of truth backing Axis A's and Axis B's self-declared ranges (§6.2, §6.3).
8. **Publish the support matrix** (§6.4, documentation/governance only — no runtime wiring) before the first release that ships this architecture.
9. **Cut the major version release** by merging the integration branch into `develop`. Old native-typed APIs and any interim compatibility code used only to stage the migration are removed at this point — there is no ongoing dual-API support window (§4/§5.5).

## 8. Security considerations

- **Classpath trust boundary.** `ServiceLoader` discovery means whatever `GemFireDriver` implementation is on the classpath is trusted implicitly and gets full delegation authority over cache/region/function operations — the same trust model as a JDBC driver jar. This should be called out explicitly in deployment documentation: only vetted, first- or known-third-party Driver Impl artifacts belong on the runtime classpath.
- **Startup auditability.** `GemFireDriverManager` should log the resolved `DriverMetadata` (impl identity, version, capabilities) at bind time, so which driver a given deployment actually loaded is always visible after the fact — useful both for support and for detecting an unexpected/unauthorized driver jar on the classpath.
- **No sensitive data in bind-time exceptions.** `DriverBindException` and its subtypes carry version/capability metadata only; `DriverConfiguration` (which may carry credentials or connection secrets) must never be included in an exception message or `toString()`.
- **Exception translation must not widen exposure.** When native GemFire exceptions are translated into SPI exceptions (§5.4), Driver Impls must not pass through native exception messages verbatim if they could embed internal topology, hostnames, or credentials — sanitize at the translation boundary, not downstream.

## 9. Concurrency considerations

- `GemFireDriverManager.driver()` resolution is a classic once-computed, many-read value; §5.2's double-checked-locking-over-`volatile` pattern is the safe-publication idiom for that, and should not be replaced with a simpler unsynchronized cache purely for a perceived performance win — the cost is paid once, at startup.
- `Capability` checks (`requireCapability`) sit on potential hot paths (e.g. before every CQ registration). `Set<Capability>` should be backed by `EnumSet` so membership checks are a constant-time bitset test, not a hash lookup.
- Beyond driver resolution and capability checks, the SPI itself imposes no concurrency contract (§5.6) — any thread-safety needed by a specific operation is the Driver Impl's responsibility to provide and document, matching whatever guarantee the underlying native GemFire client offers for that operation today.

## 10. Testing strategy

- **Official mock Driver Impl.** `spring-data-gemfire-driver-mock` (§4) — an official mock `GemFireDriver` implementation, distributed as its own module — is the standard unit-test runtime, replacing today's ad-hoc per-test Mockito mocking of native GemFire types. It tests against the actual SPI contract boundary, the same one every real Driver Impl implements, rather than mocks of internals.
- **Zero native GemFire enforced on the unit-test classpath.** The `src/test` classpath is build-enforced (§4) to contain no native GemFire — only `driver-spi` and the mock Driver Impl — mirroring the same invariant enforced for main code.
- **Integration test matrix shape: one `integrationTest` configuration per Driver Impl version.** Rather than a single parameterized suite looping over every supported Driver Impl, the build defines a separate `integrationTest` Gradle source set/configuration per Driver Impl version, each wired to run the shared integration test suite against that specific Driver Impl's real GemFire dependency. This keeps per-version classpaths, dependency resolution, and version-specific test exclusions cleanly isolated.
- **This matrix backs both compatibility axes.** A Driver Impl's self-declared Axis A generation range (§6.2) and Axis B native-engine range (§6.3) are each only trustworthy if backed by a passing `integrationTest` configuration covering every value in the declared range — and that coverage is **continuously re-verified** (re-run whenever `driver-spi`/`driver-core` change), not checked once at Driver Impl release. Without continuous enforcement, a declared range can silently rot into a false claim, as observed in this project's research into IntelliJ `since-build`/`until-build`, npm `engines`, and VS Code's `engines.vscode`.

Out of scope for this testing strategy: whether `spring-test-vmware-gemfire` itself should migrate off its current direct dependency on native GemFire (to depend only on `driver-spi`/the mock Driver Impl instead) — that module keeps its current direct native GemFire dependency; it is not migrated as part of this effort (§11).

## 11. Out of scope

- The `Gud*` naming convention or any other artifact/naming carried over from the `unified_driver` prior-art branch.
- Any production code changes as part of producing this spec — this document is the handoff artifact; implementation follows it.
- Whether `spring-test-vmware-gemfire` should migrate off its current direct dependency on native GemFire — it keeps that dependency; not migrated as part of this effort (§10).

## Appendix A: Decision traceability

| Section | Decision ticket |
|---|---|
| §1 | `wayfinder/issues/01-deliverables-and-destination.md` |
| §2 | `wayfinder/issues/02-prior-art-disposition.md` |
| §3 | `wayfinder/issues/03-driver-spi-architecture-model.md` |
| §4, §7 (breaking-change framing) | `wayfinder/issues/04-major-version-breaking-migration.md` |
| §5.5 | `wayfinder/issues/05-annotation-attribute-types.md` |
| §5.4 | `wayfinder/issues/06-exception-hierarchy.md` |
| §5.3 (fail-fast policy) | `wayfinder/issues/07-unsupported-feature-fail-fast.md` |
| §5.3, §6.5 | `wayfinder/issues/08-capability-enum-evolution-and-removal.md` (amended — capability immutability invariant, via ticket 18) |
| §5.6, §9 | `wayfinder/issues/09-thread-safety-stance.md` |
| §6.1, §6.2 (superseded — see ticket 18) | `wayfinder/issues/10-spi-versioning-scheme.md` |
| §6.4 (Part 1 stands, narrowed); runtime floor check dropped (see ticket 16) | `wayfinder/issues/11-driver-impl-support-matrix-and-bind-time-check.md` |
| §3, §4 | `wayfinder/issues/12-spi-module-package-layout.md` |
| §7 (steps 1–3, branch strategy) | `wayfinder/issues/13-migration-rollout-sequencing.md` |
| §4, §10 | `wayfinder/issues/14-testing-strategy.md` (amended twice — Axis A CI enforcement via ticket 18, Axis B CI enforcement via ticket 15) |
| §5.1, §5.2, §5.4, §6.3, §10 | `wayfinder/issues/15-driver-impl-self-declared-gemfire-compatibility-mechanics.md` (Axis B) |
| §6.4, §11 | `wayfinder/issues/16-initial-version-list-and-matrix-scope.md` |
| (process, not a spec section) | `wayfinder/issues/17-spec-review-sign-off-process.md` |
| §5.1–§5.4, §6.1, §6.2, §10 | `wayfinder/issues/18-spi-impl-generation-range-compatibility.md` (Axis A — supersedes ticket 10, amends ticket 08) |
