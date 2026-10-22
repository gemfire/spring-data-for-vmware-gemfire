# Spring Data GemFire — Driver SPI Technical Design Specification

**Status:** Revised design signed off in full, implementation-ready — see `wayfinder/rationale.md` §8 (2026-10-20 entry). Approval covers the no-static-state rule, SPI ABI compatibility and driver version lock-step, and the finalized driver release and binding details. All design decisions are resolved (see `wayfinder/map.md`).
**Companion document:** `wayfinder/rationale.md` (executive summary / stakeholder sign-off)
**Traceability:** every normative statement below is sourced from a resolved decision under `wayfinder/issues/`; see [Appendix A](#appendix-a-decision-traceability).

## 1. Purpose and scope

This spec defines a **Driver SPI/API layer** that abstracts the GemFire version out of Spring Data GemFire. After this migration:

- Spring Data GemFire compiles against the Driver SPI only. It carries **zero** direct references to native GemFire (`org.apache.geode.*`) types, anywhere in main or test sources.
- A single Spring Data GemFire release (per Spring Data version) runs against **multiple GemFire versions** by swapping which Driver Impl is present on the runtime classpath.
- The Driver Impl handles feature and API differences between GemFire versions **transparently** — Spring Data GemFire code is written once, against the SPI's stable contract.

This is a **breaking major version migration**, not an additive one. There is no permanent compatibility facade preserving today's native-typed APIs; they are replaced, not shimmed.

Out of scope for this spec: the `Gud*` naming convention or any other artifact of the `unified_driver` prior-art branch, any production code changes (this document is the handoff artifact that precedes that work), and whether `spring-test-vmware-gemfire` migrates off its current direct native GemFire dependency (§11).

### Client-only scope amendment — 2026-10-22

The user explicitly directed the backlog to be client-only and requested removal of server-side SPI work. The [scope decision](../wayfinder-impl/client-only-scope.md) defines this approved correction; [implementation ticket 59](../wayfinder-issues/59-remove-server-only-spi-and-reconcile-client-scope.md) owns cleanup of existing code.

Required SPI coverage comprises client caches/regions (including local regions), pools, queries/CQ, client function invocation, subscriptions/durability, authentication, PDX/serialization and supported client transactions/local storage. It excludes peer-cache/region hosting, cache servers, WAN/queues, partition/rebalance administration and server-only callbacks/configuration. Native API reachability is discovery evidence, not automatic product scope. Mixed surfaces require member-level client evidence. Client operations against servers remain required, with private native server fixtures for integration tests.

The full retained Spring client unit suite must run with the official mock and native-free collaborators; each supported native driver retains compatibility and integration tests. This correction changes scope, not the SPI/core-only dependency boundary or ABI/stamp enforcement. Server-only public APIs, annotation/XML settings and tests are retired with explicit evidence in this breaking migration; client behavior is preserved.

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
│   (GemFireDriverManager,          │   lookup, SPI-version and
│   bind-time validation)           │   native-version checks (§5.2, §6)
└───────────────┬─────────────────┘
                │ depends on
┌───────────────▼─────────────────┐
│   spring-data-gemfire-driver-spi  │   the stable contract:
│   (GemFireDriver, DriverMetadata, │   interfaces (additive-only,
│   SpiVersion, exceptions,         │   default methods), exception
│   SPI-owned config enums)         │   hierarchy, annotation enums
└───────────────┬─────────────────┘
                │ implemented by (one jar per GemFire version)
     ┌──────────┼──────────┬─────────────────┐
┌────▼────┐ ┌───▼─────┐ ┌──▼──────┐   ┌──────▼──────┐
│ driver- │ │ driver- │ │ driver- │...│ driver-mock  │
│ 10.1    │ │ 10.2    │ │ 10.3    │   │ (test only,  │
│         │ │         │ │         │   │  see §10)    │
└─────────┘ └─────────┘ └─────────┘   └──────────────┘
```

- **`spring-data-gemfire-driver-spi`** — the pure SPI contracts (`GemFireDriver`, `DriverMetadata`, `SpiVersion`, the SPI-owned exception hierarchy, SPI-owned config enums). Zero dependency on `driver-core` or on any GemFire jar. This is what a Driver Impl author compiles against.
- **`spring-data-gemfire-driver-core`** — the manager/orchestration module: `GemFireDriverManager`'s `ServiceLoader` lookup and the two independent bind-time checks — SPI-version compatibility and native GemFire version (§5.2, §6). Depends only on `driver-spi`.
- **`spring-data-gemfire-driver-<gemfire-version>`** (e.g. `spring-data-gemfire-driver-10.3`) — one module per supported GemFire version, packaged as an independent, swappable runtime artifact whose version is the GemFire version it wraps (§6.3). Depends on the native GemFire jars for that version plus `driver-spi` only — **never** on `driver-core`, never on another Driver Impl module, never on `spring-data-vmware-gemfire`.
- **`spring-data-vmware-gemfire`** — the existing Spring Data GemFire module, depending on both `driver-spi` and `driver-core`.
- **Discovery**: Driver Impls register via **`java.util.ServiceLoader`**. Spring Data GemFire never references a concrete Driver Impl class; it resolves one at startup through `GemFireDriverManager` (§5.2).

This mirrors JDBC deliberately: an application picks its GemFire version by putting the matching driver jar on the classpath, the same way a JDBC application picks its database by classpath, not by code change.

All of the above live in this same repo as additional Gradle subprojects, at least for this initial migration — decoupled release cadence doesn't require decoupled repos, and keeping them together makes the "zero native GemFire outside Driver Impl modules" build check and SPI/impl lockstep easier during the breaking migration. Splitting into separate repos remains a future option once the SPI contract has stabilized, not a decision this spec makes now.

## 4. Module boundaries and build dependencies

Maven/Gradle group `com.vmware.gemfire`, alongside the existing `spring-data-vmware-gemfire` / `spring-test-vmware-gemfire`. Package root: a new top-level family, `com.vmware.gemfire.driver`, independent of `org.springframework.data.gemfire` — signaling the SPI is a distinct, independently-versioned artifact rather than an internal package of Spring Data GemFire. Split by module: `com.vmware.gemfire.driver.spi` in `driver-spi`, `com.vmware.gemfire.driver.core` in `driver-core`. Note that it is the **module boundary** (separate jars) that actually keeps Driver Impls off `driver-core`'s classpath — the package split is for readability, not enforcement.

| Module | Depends on | Contains |
|---|---|---|
| `spring-data-gemfire-driver-spi` | (nothing GemFire-specific) | `GemFireDriver`, `DriverMetadata`, `SpiVersion`, SPI exception hierarchy, SPI-owned annotation attribute enums |
| `spring-data-gemfire-driver-core` | `spring-data-gemfire-driver-spi` | `GemFireDriverManager` — `ServiceLoader` discovery, SPI-version and native-version bind-time checks (§5.2, §6) |
| `spring-data-vmware-gemfire` | `spring-data-gemfire-driver-spi`, `spring-data-gemfire-driver-core` | Repositories, client cache/region configuration, annotation processing, client function execution, CQ and PDX — all routed through the SPI |
| `spring-data-gemfire-driver-<gemfire-version>` (e.g. `spring-data-gemfire-driver-10.3`) | `spring-data-gemfire-driver-spi` only, native `com.vmware.gemfire:gemfire-core:<resolved-version>` (and related artifacts) | Concrete `GemFireDriver` implementation, native↔SPI type/exception translation for that GemFire version; the build-generated driver stamp (§6.2) |
| `spring-data-gemfire-driver-mock` (test only — see §10) | `spring-data-gemfire-driver-spi` only | In-memory mock `GemFireDriver` implementation; standard unit-test runtime (§10) |
| `spring-data-gemfire-driver-contract-tests` (test only) | `driver-spi`, `driver-core`, JUnit Jupiter, AssertJ | Shared provider, cache/region and default-method contract tests; no native GemFire dependency |

**Build-time enforcement:** add an architecture fitness function (e.g. an ArchUnit rule or equivalent banned-import check) to the `spring-data-vmware-gemfire` build that fails the build on any `org.apache.geode.*` import in main **or test** sources — the unit-test classpath is build-enforced to contain no native GemFire, only `driver-spi` and the mock Driver Impl (§10). This is the mechanical guarantee behind "zero native GemFire references" — a module compiling cleanly today can regress silently on a later PR without it. The same fitness function also enforces the no-static-state/no-singleton rules of §5.7 on the new modules.

## 5. Core SPI contracts

### 5.1 `GemFireDriver`

Three versions matter: Spring Data GemFire, the SPI (`major.minor`, §6.1), and the Driver Impl, whose version **is** the GemFire version it wraps (§6.3). Spring Data GemFire depends on a specific Spring Data version and on the SPI (through `driver-core`); it has no dependency on any GemFire version. Two independent bind-time checks follow from this: the driver's stamped SPI version against the runtime SPI (§6.2), and the driver's stamped GemFire version against the GemFire actually on the classpath (§6.3).

```java
package com.vmware.gemfire.driver.spi;

public interface GemFireDriver {

    DriverMetadata metadata();

    /**
     * The GemFire product version actually present on the classpath this
     * Driver Impl is co-located with, read locally from the native jars'
     * build metadata (§6.3). Lets driver-core, which has no native GemFire
     * dependency, compare it with the version stamped into the driver.
     */
    String runtimeGemFireVersion();

    /**
     * Establishes the runtime connection this driver mediates.
     * Thread-safety is whatever the underlying GemFire client/cache
     * naturally provides — the SPI adds no additional guarantee. See §5.6.
     */
    DriverConnection connect(DriverConfiguration configuration) throws DriverBindException;
}

public interface DriverMetadata {
    String driverName();   // identity only, for bind-time logging and diagnostics (§8)
}

public final class SpiVersion implements Comparable<SpiVersion> {
    public static SpiVersion of(int major, int minor) { ... }
    public static SpiVersion runtime() { ... }   // reads this SPI jar's own version from a resource inside the jar — never a compile-time constant (§6.1)
    public int major();
    public int minor();
    /** True iff a driver built against {@code builtAgainst} can run on this SPI version (§6.2). */
    public boolean accepts(SpiVersion builtAgainst) { ... }   // same major && this.minor >= builtAgainst.minor
}
```

The SPI carries no capability set and no version range. Which features a driver supports is expressed by the methods it implements (§5.3).

**Step 2 implementation boundary (amended 2026-10-22).** The usable connection surface is `CacheConfiguration` (client configuration and defensively copied native properties), `DriverConnection` (client-region creation, lookup and lifecycle), and `DriverRegion<K,V>` (client operations). Configuration diagnostics redact property values. Connections are `AutoCloseable`; the caller owns closing the underlying cache. New connection/region methods default to typed `NOT_INTRODUCED` failures. Native exceptions are translated at the driver boundary.

The native driver delegates client operations; the mock holds independent in-memory state per connection, implements supported local client behavior and explicitly rejects unavailable distribution, persistence or eviction. Its synthetic identity is `mock`, not a native release. Existing peer mode/creation and `RegionShortcut` are historical implementation to remove under ticket 59. Pools, queries, client functions/transactions, PDX and remaining retained client APIs expand through consumer migration. See [Reference driver and mock](../wayfinder-issues/02-reference-driver-and-mock.md).


**Step 3 Spring Cache slice (2026-10-20).** `GemfireCache` now adapts a `DriverRegion`; `GemfireCacheManager` takes a constructor-injected `DriverConnection`, with the connection owner retaining shutdown responsibility. Region enumeration, lifecycle status, clear and put-if-absent have been added to the SPI and both drivers. `@EnableGemfireCaching` requires an application-supplied `DriverConnection` bean; XML uses the same injected connection. Native cache setters/factory entry points on these migrated adapters are replaced by their SPI constructors. The remaining cache/util/root APIs proceed in subsequent slices. See [Spring Cache driver migration](../wayfinder-issues/03-spring-cache-driver-migration.md).

**Step 3 cache-loader slice (2026-10-20).** `CallableCacheLoaderAdapter` now consumes `DriverCacheLoader`, `DriverLoaderHelper` and `DriverRegion`. The region SPI adds loader installation and argument-bearing reads. Native callback adaptation stays in the reference driver; the mock supports local loading and explicitly rejects network search. Synthetic callable contexts also reject network search. The complete Spring Data cache package now compiles and tests without native dependencies. See [Cache loader driver migration](../wayfinder-issues/05-cache-loader-driver-migration.md).

### 5.2 `GemFireDriverManager` — discovery and bind-time validation

`GemFireDriverManager` is an ordinary **instance** — no static state, no static accessor, no singleton (§5.7). It resolves and validates the Driver Impl once, eagerly, in its constructor, and exposes the result through a `final` field. Consumers receive the manager (or the `GemFireDriver` it resolved) by constructor injection; the Spring container owns its lifecycle, and a non-Spring caller simply constructs one and passes it where needed.

```java
package com.vmware.gemfire.driver.core;

public final class GemFireDriverManager {

    private final SpiVersion spiVersion;   // §6.1 — the SPI jar's own major.minor, read at runtime
    private final GemFireDriver driver;    // resolved and validated once, immutable thereafter

    public GemFireDriverManager() {
        this(DriverCandidate.discover(ServiceLoader.load(GemFireDriver.class)), SpiVersion.runtime());
    }

    public GemFireDriverManager(ClassLoader classLoader) {
        this(DriverCandidate.discover(ServiceLoader.load(GemFireDriver.class, classLoader)), SpiVersion.runtime());
    }

    // package-private: lets tests supply candidates and an SPI version without any static reset hook
    GemFireDriverManager(List<DriverCandidate> candidates, SpiVersion spiVersion) {
        this.spiVersion = spiVersion;
        this.driver = resolveAndValidate(candidates);
    }

    public GemFireDriver driver() {
        return driver;
    }

    private GemFireDriver resolveAndValidate(List<DriverCandidate> candidates) {
        if (candidates.isEmpty()) {
            throw new NoDriverFoundException();
        }
        if (candidates.size() > 1) {
            throw new AmbiguousDriverException(candidates.stream().map(DriverCandidate::providerClass).toList());
        }

        DriverCandidate candidate = candidates.get(0);
        validateSpiCompatibility(candidate);                      // §6.2 — from the stamp, before the provider is instantiated
        GemFireDriver instance = instantiate(candidate);          // §6.2 — LinkageError backstop
        validateRuntimeGemFireVersion(instance, candidate);       // §6.3 — sequenced after, and independent of, the SPI check
        return instance;
    }

    private void validateSpiCompatibility(DriverCandidate candidate) {
        SpiVersion builtAgainst = candidate.stamp().builtAgainstSpiVersion();
        if (!spiVersion.accepts(builtAgainst)) {
            throw new SpiVersionIncompatibleException(candidate.providerClass(), builtAgainst, spiVersion);
        }
    }

    private GemFireDriver instantiate(DriverCandidate candidate) {
        try {
            return candidate.instantiate();
        } catch (LinkageError | ServiceConfigurationError e) {
            throw new DriverBindException("Driver Impl " + candidate.providerClass() + " could not be loaded: " + e, e);   // typed, cause attached
        }
    }

    // Exact match: the driver's version is the GemFire version it wraps (§6.3). driver-core has no
    // native GemFire dependency, so the runtime version comes from the driver (§5.1).
    private void validateRuntimeGemFireVersion(GemFireDriver instance, DriverCandidate candidate) {
        String runtimeVersion = instance.runtimeGemFireVersion();
        String stampedVersion = candidate.stamp().gemFireVersion();
        if (!runtimeVersion.equals(stampedVersion)) {
            throw new NativeVersionIncompatibleException(candidate.providerClass(), stampedVersion, runtimeVersion);
        }
    }
}
```

`DriverCandidate` is a package-private immutable value: the provider class name, its parsed `DriverStamp` (§6.2), and a supplier that instantiates the provider. `DriverCandidate.discover` — a stateless static factory on an immutable value type (§5.7 rule 3) — lists providers via `ServiceLoader.stream()`, locates each provider's stamp from `Provider.type()`, and throws `DriverBindException` if a stamp is missing or malformed. Nothing is instantiated until the stamp has been checked.

Binding happens **once, at construction**, so a missing, ambiguous, or incompatible Driver Impl fails fast at application startup — when the manager bean is created — rather than at first use deep in a request path. Because `driver` is a `final` field assigned in the constructor, the JMM's final-field semantics safely publish the fully-validated driver to every thread that obtains the manager; no `volatile`, no synchronization, and no lazy-initialization race exists to reason about. A failed construction yields no manager at all, so there is no cached failure state to reset.

If exactly one `GemFireDriver` is expected on the classpath and more than one is found, that is also a bind-time failure (`AmbiguousDriverException`) rather than an arbitrary pick — silently choosing "first found" would make classpath ordering a hidden, non-reproducible source of behavior differences between environments.

The two checks are deliberately kept **sequenced and independent**: the SPI-version check runs first, the native-version check after, each can fail fast on its own with its own typed exception, and passing one implies nothing about the other. A live-cluster variant of the native-version check (querying `MemberMXBean`/`ManagementService` on an already-joined distributed system, for client/server deployments where the client jar version and server cluster version can diverge) is explicitly deferred past v1; only the classpath-local check above gates bind time today.

### 5.3 Unsupported-feature model

There is no capability enum. **The method is the capability:** a driver that implements an SPI method supports that feature; a driver that does not, does not. Two cases, one typed exception (`UnsupportedFeatureException`, §5.4):

- **The driver predates the feature** (the SPI gained it in a later minor). Every method added after the SPI's first release is a `default` method whose body throws `UnsupportedFeatureException` with reason `NOT_INTRODUCED`. An older driver does not override it.
- **The driver's GemFire version dropped the feature**, but the SPI still carries the method (the SPI removes nothing within a major, §6.1). The driver overrides the method and throws `UnsupportedFeatureException` with reason `REMOVED`, naming the feature and its own GemFire version.

Illustrative only (not the final SPI types) — the Pool case, where `minConnectionsPerServer` is absent in GemFire 10.1 and present in 10.3:

```java
public interface PoolConfigurer {
    PoolConfigurer minConnections(int value);      // present in every supported GemFire version

    // added in a later SPI minor
    default PoolConfigurer minConnectionsPerServer(int value) {
        throw UnsupportedFeatureException.notIntroduced("Pool.minConnectionsPerServer");
    }
}
// driver-10.1: does not override minConnectionsPerServer  -> NOT_INTRODUCED at configuration time
// driver-10.3: overrides it and delegates to the native pool builder
```

Rules:

1. **New SPI methods are `default`** (§6.1). An abstract method added to an existing interface breaks every already-compiled Driver Impl with `AbstractMethodError`.
2. **Fail fast means fail at startup where possible.** Configuration-time methods (pool and region setup) run during context startup, so an unsupported attribute fails the application at start, naming the attribute. Operations first invoked at runtime (for example continuous-query registration) are registered eagerly during Spring Data GemFire's startup wiring so that an unsupported feature fails at startup, not on first request.
3. **No `supports(...)` probe and no capability set in v1.** If a consumer later needs a side-effect-free check, it is added as a `default` SPI method — an additive, ABI-safe change. The inverse (restoring a removed enum) would not be.
4. **Never a silent no-op, never a bare `UnsupportedOperationException`** (ticket 07).
5. **Contract test kit (§10).** Each driver supplies its list of expected-unsupported methods; the kit asserts exactly those throw, with the expected reason, and that every other `default` SPI method works. This is what keeps "the method is the capability" honest.

### 5.4 Exception hierarchy

```java
package com.vmware.gemfire.driver.spi;

public abstract class GemFireDriverException extends RuntimeException { }

// bind-time failures
public class DriverBindException extends GemFireDriverException { }
public class NoDriverFoundException extends DriverBindException { }
public class AmbiguousDriverException extends DriverBindException { }
public class SpiVersionIncompatibleException extends DriverBindException { }     // §6.2 — stamped SPI version not runnable on the runtime SPI
public class NativeVersionIncompatibleException extends DriverBindException { }  // §6.3 — runtime GemFire version differs from the driver's stamped version

// invocation-time failures
public class UnsupportedFeatureException extends GemFireDriverException {        // §5.3
    public enum Reason { NOT_INTRODUCED, REMOVED }
    // carries: feature name, reason; REMOVED also carries the driver's GemFire version
}
public class DriverOperationException extends GemFireDriverException { }        // wraps a translated native failure
```

Both bind-time exceptions above are thrown **only by `driver-core`**, never by a Driver Impl: each is a mechanical comparison `driver-core` performs itself — the stamped SPI version against the runtime SPI (§6.2) and the stamped GemFire version against `runtimeGemFireVersion()` (§6.3). Centralizing the throw in `driver-core` avoids duplicating exception-construction logic across every Driver Impl. `UnsupportedFeatureException` is the one exception with two legitimate throw sites — the SPI's `default` methods (`NOT_INTRODUCED`) and a driver's own overrides (`REMOVED`) — which is why it carries a structured reason.

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

Retained client annotation attributes typed as native GemFire enums are retyped to **SPI-owned delegation enums** covering verified client values. Preserve client defaults and capabilities, including valid client-local persistence policies. Do not mirror entire native enums: server-side `RegionShortcut` and server-only annotation/configuration attributes are removed under the approved client-only amendment; retain `ClientRegionShortcut`.

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

### 5.7 No static state, no singletons

Applies to every new module this effort creates — `driver-spi`, `driver-core`, every Driver Impl, and `driver-mock` — and to all new code written in `spring-data-vmware-gemfire` while migrating it. Rules:

1. **No mutable static state.** No non-`final` static fields, and no `static final` fields holding mutable objects (caches, registries, holders, `AtomicReference`, mutable collections).
2. **No singletons and no static service locators.** No `getInstance()`, no static `driver()`-style accessors, no static holder idiom, no enum-singleton. A collaborator is obtained by constructor injection; the Spring container (or, outside Spring, the caller) owns its lifecycle and scope.
3. **Permitted statics:** `static final` compile-time constants and immutable values (including enum constants and immutable value objects such as `SpiVersion`); stateless static factory methods on immutable value types (`SpiVersion.of`, `SpiVersion.runtime`, `DriverCandidate.discover` — each reads its inputs and caches nothing); and unavoidable JDK/framework static calls (`ServiceLoader.load`). A `private static` helper is acceptable only if it is pure — it reads no static state and produces no side effects; anything that needs instance state is an instance method.
4. **Loggers are instance-scoped**, obtained from the class of the instance, not a `static final` field.
5. **No static reset or test hooks.** Tests construct their own instances with the collaborators they need (§5.2's package-private constructor); production code never carries `reset()`/`clear()` methods that exist only so tests can undo static state.
6. **Driver Impls hold no process-wide state of their own.** `ServiceLoader` instantiates a fresh Driver Impl per loader, so an Impl must behave correctly when several instances exist. Process-global state that lives inside native GemFire itself (for example its cache singleton) is passed through unchanged, per the thin-delegate stance (§5.6) — the SPI neither hides nor adds to it.
7. **Enforcement.** The architecture fitness function from §4 gains a rule for the new modules: fail the build on any non-`final` static field, on any static field of a non-immutable type, and on any static method returning an instance of its own declaring class other than on an immutable value type.

Existing statics and singletons in pre-migration `spring-data-vmware-gemfire` code are not removed merely because that code is migrated to the SPI; the rules bind new and rewritten code. The one hard requirement is that migrated code obtains the driver by injection, never through a static accessor.

## 6. Versioning and support lifecycle

### 6.1 SPI versioning — `major.minor`, additive within a major

`driver-spi` is versioned `major.minor`, and that version is the **ABI (binary-compatibility) unit**:

- **Minor bump: additive only.** New interface methods are `default`. Existing signatures — names, parameter types, return types, `throws` clauses — never change. Nothing is removed: no method, type, or enum constant. **No behavior change to an existing method within a major**; a behavior change arrives as a new method.
- **Major bump: the only point at which anything is removed or changed.** Every Driver Impl must be recompiled.
- **Binary-compatibility rules** (JLS ch. 13): no abstract method added to an existing interface; no return-type change; no removal of public members or enum constants; no reliance on `ordinal()`; no exhaustive `switch` over an SPI enum across the SPI boundary without a default branch; no public `static final` primitive or `String` whose value may change (javac inlines it into the compiling module).
- **The SPI reports its own version from a resource inside the SPI jar, read at runtime (`SpiVersion.runtime()`, §5.1) — never from a compile-time constant.** A constant would be inlined into `driver-core` at its build time and report the version `driver-core` was built against, not the one on the classpath, so the compatibility check would pass exactly when it should fail.
- **Enforcement: an SPI API-compatibility gate** (japicmp or Revapi; the tool is chosen when the gate is wired into CI) fails the build on any binary-incompatible change between SPI releases within a major. The stamp (§6.2) is a declaration; this gate is what makes "additive within a major" true. It has no baseline to compare against until the first SPI release.
- `driver-core` is versioned and shipped together with the SPI and is itself subject to the same stamp check against the SPI.

The compatibility unit is the ABI only. Which features a given driver supports is not a version question: it is fixed by the driver's GemFire version and by which SPI methods it implements (§5.3).

### 6.2 Driver stamp and SPI-version compatibility

Each Driver Impl build runs a Gradle step that reads the **resolved** SPI dependency version and writes a properties file — the **driver stamp** — into the driver jar:

```properties
# META-INF/gemfire-driver/<provider-fully-qualified-class-name>.properties   (generated, never hand-edited)
spi.version=1.4
gemfire.version=10.3.2
```

- `spi.version` is the SPI `major.minor` the driver was built against (patch is ignored). `gemfire.version` is the GemFire version it was built against (§6.3).
- The stamp is a **resource, not a class**, and is keyed by provider class name. It must be readable *before* the provider class is instantiated — a stamp class could itself fail to link, which is the failure the check exists to prevent — and keying by provider keeps it correctly attributed when several driver jars share a classpath.
- The stamp is generated from the resolved dependency; CI asserts that the stamp equals the SPI version on the driver's compile classpath.

**Bind-time rule (`driver-core`, §5.2):** the driver's stamped SPI `major` must equal the runtime SPI `major`, **and** the runtime SPI `minor` must be at least the stamped `minor`. Otherwise `SpiVersionIncompatibleException` names the provider class, the stamped version, and the runtime version.

- An older driver on a newer SPI in the same major passes — new methods are `default`, so nothing it was compiled against is missing. A driver built against SPI 1.4 cannot run on 1.2, because it may reference members added in 1.3 or 1.4.
- A different major fails: a removed or changed member would surface as a lazy `NoSuchMethodError` at first use, long after startup.
- This is also where "no forward compatibility" is enforced: a driver built against a newer SPI than the runtime SPI is rejected at bind time.
- **Missing or malformed stamp** is a bind failure (`DriverBindException`), not a pass.
- **`LinkageError` backstop.** `driver-core` catches `LinkageError` and `ServiceConfigurationError` raised while instantiating a provider and rethrows them as `DriverBindException` with the cause attached. The stamp catches the expected mismatches (an application server or fat jar bundling an old SPI, a BOM or `force` rule pinning the SPI down, shading); the backstop handles a missing or wrong stamp.

### 6.3 Driver/GemFire lock-step and the native-version check

A Driver Impl wraps exactly one GemFire version, and **its initial artifact version is that GemFire version**: driver `10.3.2` depends on GemFire `10.3.2`, and an application that wants GemFire 10.2.2 uses driver 10.2.2. Driver-only fixes append a revision as described below. A driver does not declare a range and does not span GemFire versions; a new GemFire version means a new driver release.

- **Exact-match bind-time check.** `driver-core` compares `GemFireDriver.runtimeGemFireVersion()` (§5.1) with the `gemfire.version` in the driver's stamp, and throws `NativeVersionIncompatibleException` unless they are equal, patch included. The 10.4 driver obtains the runtime value from `com.vmware.gemfire.internal.version.GemFireVersion.getGemFireVersion()` in `gemfire-version`, pinned to the same resolved version as `gemfire-core`. This is a local read of the build metadata packaged in the native jars actually on the classpath; the older `org.apache.geode.internal.GemFireVersion` class is absent from this release. The check exists because the application, not the driver, controls what is on the classpath: another library or an override could place a different GemFire jar under the driver.
- **10.4 reference build selection.** Per the implementation instruction, `10.4.+` selects the native release at build time. The driver then pins its native dependencies and initial artifact version to that one concrete resolved version; its generated stamp records the same version. This selector does not relax the exact-match runtime check.
- **Driver-only fixes.** A driver bug fix that does not track a GemFire release appends an incrementing numeric revision to the target GemFire version: `10.3.2.1`, `10.3.2.2`, and so on. The generated `gemfire.version` remains `10.3.2` for all these releases. Compatibility compares the runtime GemFire version with the stamped `gemfire.version`; it never parses the driver artifact version. Confirmed in [Finalize provisional driver release and binding details](issues/21-finalize-provisional-driver-release-and-binding-details.md).
- **Per-version differences live in the driver.** A driver never branches on a runtime GemFire version. The Pool case (§5.3) is resolved by `driver-10.1` not overriding a `default` setter and `driver-10.3` overriding it.
- **Sequenced after, and independent from, the SPI-version check** (§5.2).
- **Live-cluster check deferred past v1.** A live check against an already-joined distributed system's actual version (via `org.apache.geode.management.MemberMXBean`/`ManagementService`) is a candidate for a future ticket if a real version-skew scenario surfaces in practice; only the classpath-local check gates v1 bind time.

### 6.4 Support matrix — documentation and governance only

A **documented support matrix** — GemFire version → Driver Impl version → supported-until — is maintained alongside the spec, mirroring GemFire's own official support-lifecycle/EOL dates rather than an independently invented policy. It is the governance source of truth for when a feature may be removed from the SPI (§6.5): removal is permitted only at an SPI major bump, and only once no still-supported driver version needs it. The concrete initial GemFire version list to populate it with at launch is a later release-planning decision, deliberately out of scope for this spec.

The matrix has **no runtime role**. It is enforced by process only: a release-checklist item requires checking it before any SPI major release removes a feature — no runtime machinery is needed for this, and adding code to consult it at bind time would both violate the thin-delegate stance (§5.6) and duplicate what the stamp checks (§6.2, §6.3) already do mechanically.

### 6.5 Feature removal

Within a major, nothing is removed from the SPI (§6.1); a feature stays as a method indefinitely, even if the latest GemFire version no longer supports it — the driver for that version overrides the method and throws `UnsupportedFeatureException` with reason `REMOVED` (§5.3). A feature leaves the SPI only at an SPI **major** bump, once the support matrix (§6.4) shows no still-supported driver needs it. That major bump requires every Driver Impl to be recompiled, so it is deliberately rare.

## 7. Migration plan

This is sequencing guidance for implementation, not a schedule commitment.

**Branch strategy.** The migration happens on a dedicated, long-lived integration branch (e.g. `driver-spi-migration`), not incrementally on `develop` — this is a breaking major version (§1) touching the whole compile surface, so landing it piecemeal on `develop` would force every routine fix in flight to rebase across a moving native-vs-SPI boundary. This branch is distinct in purpose and naming from the repo's existing `support/<gemfire>-<spring>-<sdgf>.x` per-version-combination convention (which lets already-released combinations keep receiving patches independently) — no new `support/*` branches are cut during the migration window, and that convention continues operating as-is until the SPI model supersedes it post-migration. Net-new feature work against the *current* native API is frozen only for whichever subsystems are actively being migrated at a given time (per the order in step 3 below); routine fixes to not-yet-migrated packages continue landing on `develop` and get rebased onto the integration branch periodically (e.g. weekly). The branch merges once into `develop` when cutover is complete — no permanent compatibility facade.

1. **SPI skeleton.** Stand up `spring-data-gemfire-driver-spi` and `spring-data-gemfire-driver-core` with `GemFireDriver`, `GemFireDriverManager`, `SpiVersion`, the driver stamp and its bind-time checks, the exception hierarchy, and the SPI-owned annotation config enums. No behavior yet — types and contracts only.
2. **Reference Driver Impl(s) and mock.** Implement `spring-data-gemfire-driver-<X.Y>` for the GemFire version(s) Spring Data GemFire currently targets, wrapping today's native calls behind the new SPI contract, the Gradle step that generates each driver's stamp (§6.2), the driver contract test kit (§10), and the official `spring-data-gemfire-driver-mock` for unit testing (§10).
3. **Migrate `spring-data-vmware-gemfire` subsystem-by-subsystem, bottom-up by dependency, not by size:**
   1. `cache`, `util`, and the root package (`org.springframework.data.gemfire`) — the `*FactoryBean` hierarchy, `GemfireTemplate`/`GemfireOperations`, `RegionResolver`/`CacheResolver`, the `*Converter`s. This is the seam nearly everything else calls into to obtain a `Cache`/`Region`; foundational and small enough to prove the SPI pattern cheaply before committing to it everywhere.
   2. `eviction`, `expiration`, `transaction`, `serialization`, `listener`, `domain`, `dao` and client-needed utilities — retain client behavior; retire server-only surfaces under ticket 59.
   3. `config` — the largest package by file count, but majority Spring bean-wiring rather than direct native usage; migrated once step 1's seam is already SPI-based, so the wiring changes are mechanical.
   4. `client`, `function`, `repository`, `mapping` — last. `client` sits directly on the SPI and is heavily geode-typed; the others are the widest-radius consumers of everything below them and benefit most from migrating against an already-proven, stable SPI.

   Every native call encountered along the way is routed through the `GemFireDriver` obtained from an injected `GemFireDriverManager` (§5.2, §5.7) — never through a static accessor.
4. **Retarget exception translation** — move `GemfireCacheUtils`-equivalent `DataAccessException` translation to consume `GemFireDriverException` (§5.4).
5. **Retarget annotation attributes** to the SPI-owned delegation enums (§5.5); update annotation processors and configuration classes accordingly.
6. **Close the gap to zero native references** — remove remaining `org.apache.geode.*` imports from main and test sources; turn on the architecture fitness function from §4 to prevent regression.
7. **Stand up the integration test matrix** (§10) — one `integrationTest` Gradle configuration per Driver Impl version, running the full test suite against each, each running against exactly that driver's own GemFire version (§6.3), and wire in the SPI API-compatibility gate (§6.1).
8. **Publish the support matrix** (§6.4, documentation/governance only — no runtime wiring) before the first release that ships this architecture.
9. **Cut the major version release** by merging the integration branch into `develop`. Old native-typed APIs and any interim compatibility code used only to stage the migration are removed at this point — there is no ongoing dual-API support window (§4/§5.5).

## 8. Security considerations

- **Classpath trust boundary.** `ServiceLoader` discovery means whatever `GemFireDriver` implementation is on the classpath is trusted implicitly and gets full delegation authority over cache/region/function operations — the same trust model as a JDBC driver jar. This should be called out explicitly in deployment documentation: only vetted, first- or known-third-party Driver Impl artifacts belong on the runtime classpath.
- **Startup auditability.** `GemFireDriverManager` should log the resolved `DriverMetadata` (driver identity, stamped SPI and GemFire versions, runtime GemFire version) at bind time, so which driver a given deployment actually loaded is always visible after the fact — useful both for support and for detecting an unexpected/unauthorized driver jar on the classpath.
- **No sensitive data in bind-time exceptions.** `DriverBindException` and its subtypes carry version and provider-class metadata only; `DriverConfiguration` (which may carry credentials or connection secrets) must never be included in an exception message or `toString()`.
- **Exception translation must not widen exposure.** When native GemFire exceptions are translated into SPI exceptions (§5.4), Driver Impls must not pass through native exception messages verbatim if they could embed internal topology, hostnames, or credentials — sanitize at the translation boundary, not downstream.

## 9. Concurrency considerations

- Driver resolution happens once, in `GemFireDriverManager`'s constructor (§5.2), and the resolved driver is held in a `final` field — safe publication by final-field semantics, with no lazy initialization, `volatile`, or locking to get wrong. There is no static state (§5.7) whose visibility or ordering across threads needs reasoning about.
- Unsupported-feature dispatch (§5.3) is an ordinary virtual call to a `default` or overridden method; there is no capability-set lookup on any hot path.
- Beyond driver resolution, the SPI itself imposes no concurrency contract (§5.6) — any thread-safety needed by a specific operation is the Driver Impl's responsibility to provide and document, matching whatever guarantee the underlying native GemFire client offers for that operation today.

## 10. Testing strategy

- **Official mock Driver Impl.** `spring-data-gemfire-driver-mock` (§4) — an official mock `GemFireDriver` implementation, distributed as its own module — is the standard unit-test runtime, replacing today's ad-hoc per-test Mockito mocking of native GemFire types. It tests against the actual SPI contract boundary, the same one every real Driver Impl implements, rather than mocks of internals.
- **Zero native GemFire enforced on the unit-test classpath.** The `src/test` classpath is build-enforced (§4) to contain no native GemFire — only `driver-spi` and the mock Driver Impl — mirroring the same invariant enforced for main code.
- **Integration test matrix shape: one `integrationTest` configuration per Driver Impl version.** Rather than a single parameterized suite looping over every supported Driver Impl, the build defines a separate `integrationTest` Gradle source set/configuration per Driver Impl version, each wired to run the shared integration test suite against that specific Driver Impl's real GemFire dependency. This keeps per-version classpaths, dependency resolution, and version-specific test exclusions cleanly isolated.
- **This matrix backs the lock-step guarantee.** Each driver's `integrationTest` configuration runs the shared suite against exactly one GemFire version — the driver's own (§6.3) — re-run whenever `driver-spi`/`driver-core` change. There are no declared ranges to cover, so no range-coverage rule.
- **Driver contract test kit.** Shipped with the reference driver and the mock, it asserts that each driver's expected-unsupported methods throw `UnsupportedFeatureException` with the expected reason (§5.3) and that every other `default` SPI method works.
- **SPI API-compatibility gate.** japicmp or Revapi fails CI on any binary-incompatible SPI change within a major (§6.1).

Out of scope for this testing strategy: whether `spring-test-vmware-gemfire` itself should migrate off its current direct dependency on native GemFire (to depend only on `driver-spi`/the mock Driver Impl instead) — that module keeps its current direct native GemFire dependency; it is not migrated as part of this effort (§11).

## 11. Out of scope

- Public peer/server hosting, WAN/queues, partition/rebalance administration and server-only configuration/callbacks. Private native servers remain permitted as client integration-test infrastructure. See the approved client-only amendment in §1.

- The `Gud*` naming convention or any other artifact/naming carried over from the `unified_driver` prior-art branch.
- Any production code changes as part of producing this spec — this document is the handoff artifact; implementation follows it.
- Whether `spring-test-vmware-gemfire` should migrate off its current direct dependency on native GemFire — it keeps that dependency; not migrated as part of this effort (§10).

## Appendix A: Decision traceability

| Section | Decision ticket |
|---|---|
| §1 | `wayfinder/issues/01-deliverables-and-destination.md`; [client-only amendment](../wayfinder-impl/client-only-scope.md) (2026-10-22) |
| §2 | `wayfinder/issues/02-prior-art-disposition.md` |
| §3 | `wayfinder/issues/03-driver-spi-architecture-model.md` |
| §4, §7 (breaking-change framing) | `wayfinder/issues/04-major-version-breaking-migration.md` |
| §5.5 | `wayfinder/issues/05-annotation-attribute-types.md` |
| §5.4 | `wayfinder/issues/06-exception-hierarchy.md` |
| §5.3 (fail-fast policy) | `wayfinder/issues/07-unsupported-feature-fail-fast.md` (mechanism amended by ticket 20) |
| §5.3, §6.5 | `wayfinder/issues/08-capability-enum-evolution-and-removal.md` (superseded by ticket 20 — capability enum removed) |
| §5.6, §9 | `wayfinder/issues/09-thread-safety-stance.md` |
| §6.1 (superseded by ticket 18, then SemVer partly revived by ticket 20) | `wayfinder/issues/10-spi-versioning-scheme.md` |
| §6.4 (Part 1 stands, narrowed); runtime floor check dropped (see ticket 16) | `wayfinder/issues/11-driver-impl-support-matrix-and-bind-time-check.md` |
| §3, §4 | `wayfinder/issues/12-spi-module-package-layout.md` |
| §7 (steps 1–3, branch strategy) | `wayfinder/issues/13-migration-rollout-sequencing.md` |
| §4, §10 | `wayfinder/issues/14-testing-strategy.md` (amended three times — range CI enforcement via tickets 18/15, then replaced by exact-version integration tests, contract kit and API gate via ticket 20) |
| §6.3 (Axis B mechanism superseded by ticket 20; survey retained) | `wayfinder/issues/15-driver-impl-self-declared-gemfire-compatibility-mechanics.md` |
| §6.4, §11 | `wayfinder/issues/16-initial-version-list-and-matrix-scope.md` |
| (process, not a spec section) | `wayfinder/issues/17-spec-review-sign-off-process.md` |
| §5.2, §5.7, §7 (step 3), §9 | `wayfinder/issues/19-no-static-state-or-singletons.md` (amends §5.2's static/DCL resolution model and §9; supersedes the double-checked-locking rationale) |
| (history) | `wayfinder/issues/18-spi-impl-generation-range-compatibility.md` (Axis A generation ranges — superseded by ticket 20) |
| §3, §4, §5.1–§5.4, §6.1–§6.5, §7 (steps 1, 2, 7), §8–§10 | `wayfinder/issues/20-spi-abi-compatibility-and-driver-version-lockstep.md` (SPI `major.minor` ABI unit, driver stamp, driver/GemFire lock-step, capability model removed — supersedes ticket 18 and ticket 08, amends 07/10/14/15) |
| §4, §5.1, §6.3, §10 (Step 2 implementation details) | [Reference driver and mock](../wayfinder-issues/02-reference-driver-and-mock.md) |
| §5.1, §7, §10 (Step 3 Spring Cache slice) | [Spring Cache driver migration](../wayfinder-issues/03-spring-cache-driver-migration.md) |
