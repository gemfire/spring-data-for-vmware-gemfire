Type: grilling
Status: resolved — shape approved by Udo Kohlmeyer 2026-10-01; full revised-design sign-off recorded in `rationale.md` §8 on 2026-10-20 (per [ticket 17](17-spec-review-sign-off-process.md))

## Question

[Ticket 18](18-spi-impl-generation-range-compatibility.md) (SPI generation ranges, Axis A) and [ticket 15](15-driver-impl-self-declared-gemfire-compatibility-mechanics.md) (Driver Impl native-version ranges and verdict, Axis B) assume a Driver Impl can span several SPI generations and several GemFire versions. Raised during Step 1 implementation: that model has no treatment of **binary compatibility (ABI)**, cannot express attribute-level version differences (e.g. Pool `min/maxConnections` vs `min/maxConnectionsPerServer`, absent in GemFire 10.1, present in 10.3) without growing the `Capability` enum per attribute, and carries more machinery than the JDBC model it is meant to follow.

Should compatibility instead rest on (a) an additive-only, ABI-stable SPI versioned by `major.minor`, (b) Driver Impls locked 1:1 to a GemFire version, and (c) `default` methods on the SPI in place of the `Capability` model?

## Answer

### 1. Dependency graph

```
Spring Data GemFire ──► Spring Data (specific version)
        │
        └────────────► driver-core ──► SPI (major.minor)   [no GemFire dependency]

Driver <G> ───────────► SPI (major.minor)
        │
        └────────────► GemFire <G>   (same version number as the driver)
```

- Spring Data GemFire depends on a specific Spring Data version and on the SPI (through `driver-core`). It has **no dependency on any GemFire version**.
- A Driver Impl depends on the SPI and on exactly one GemFire version. **The driver's version is the GemFire version** (driver `10.3.2` targets GemFire `10.3.2`). To use GemFire 10.2.2, the application uses driver 10.2.2.
- Three versions matter: Spring Data GemFire, the SPI, the Driver Impl.

### 2. SPI versioning: `major.minor`, additive within a major

- **Minor bump:** additive only. New interface methods are `default`. Existing signatures (names, parameter types, return types, `throws`), and the observable behaviour of existing methods, never change. No enum constant, method or type is removed.
- **Major bump:** the only point at which a method, type or constant may be removed or changed. Every Driver Impl must be recompiled.
- This is an invariant, not a convention. **No behavioural change to an existing method within a major** — a behaviour change arrives as a new method.
- Binary-compatibility rules follow JLS ch. 13: no abstract methods added to existing interfaces, no return-type changes, no removal of public members or enum constants, no reliance on `ordinal()`, no exhaustive `switch` over SPI enums without a default across the SPI boundary, no public `static final` primitive/String constants whose value may change (inlined by javac).
- **The SPI reports its own `major.minor` from a resource or manifest entry inside the SPI jar, read at runtime — never from a compile-time constant** (a constant is inlined into `driver-core` and would report the version `driver-core` was built against, not the one on the classpath).

### 3. The driver stamp

- Each Driver Impl build runs a Gradle step that reads the **resolved** SPI version and writes a properties file containing the SPI `major.minor` the driver was built against. The same file records the GemFire version the driver was built against.
- The file is keyed by the provider class name (e.g. `META-INF/gemfire-driver/<provider-fqcn>.properties`) so it stays correctly attributed when several drivers share a classpath, and is readable **before** the Impl class is loaded or instantiated. A Java class is rejected as the carrier, because loading it can hit the very `LinkageError` the check exists to prevent.
- The stamp is generated, never hand-maintained. CI asserts the stamp equals the SPI version on the compile classpath.

### 4. Bind-time check (`driver-core`)

1. **SPI compatibility:** the stamped SPI major equals the runtime SPI major, **and** the runtime SPI minor is at least the stamped minor. A driver built against 1.4 may reference members added in 1.3–1.4, so it cannot run on 1.2. Older drivers on newer SPIs in the same major pass. A different major fails.
2. **Native compatibility:** `driver-core` compares `runtimeGemFireVersion()` (kept on the SPI so `driver-core` stays native-free) with the GemFire version stamped in the driver. Mechanical; there is no Impl-supplied verdict. **Exact match** (decided 2026-10-01): the runtime GemFire version must equal the stamped GemFire version, patch included. Driver 10.3.2 requires GemFire 10.3.2; every GemFire release needs a matching driver release.
3. Both failures are typed `DriverBindException` subclasses thrown only by `driver-core`, each naming the stamped and runtime versions.
4. **Backstop:** `driver-core` catches `LinkageError` and `ServiceConfigurationError` raised while loading or instantiating a provider and rethrows them as a typed `DriverBindException`. The stamp catches the expected mismatches; the backstop handles a missing or wrong stamp.
5. `driver-core` carries the same SPI stamp check for itself and is versioned and shipped together with the SPI.

### 5. `Capability` model removed; `default` methods carry unsupported features

- The `Capability` enum, `CapabilityAware`, `requireCapability` and the capability set on `DriverMetadata` are removed. The method is the capability: implemented means supported.
- A feature the SPI added after a driver was built is handled by the `default` method, which throws. A feature a newer driver has dropped, but which the SPI still carries, is handled by the driver overriding the method and throwing.
- **One exception type, two throw sites.** `UnsupportedCapabilityException` is renamed (working name `UnsupportedFeatureException`; not `NotSupportedException`, which collides with `javax.transaction.NotSupportedException`). It carries a structured reason — `NOT_INTRODUCED` (thrown by the SPI default: the driver predates the feature) or `REMOVED` (thrown by the driver: the feature is gone in this GemFire version) — plus the feature name as a field, not only in the message. A driver-thrown `REMOVED` also carries the driver's GemFire version; an SPI-default `NOT_INTRODUCED` throw cannot know it, so that field is absent.
- **Pool attributes:** `setMinConnectionsPerServer`/`setMaxConnectionsPerServer` are `default` setters that throw. The 10.1 driver does not override them; the 10.3 driver does. Config-time methods run at startup, so a mismatch fails fast at context start. No per-attribute capability entries are needed.
- Operations first invoked at runtime (e.g. continuous-query registration) would otherwise fail late; Spring Data GemFire should register them eagerly during startup wiring. A side-effect-free `supports(...)` probe is **not** added now — it can be added later as a `default` method without an ABI break, whereas a removed enum could not be restored without one.

### 6. Enforcement

- **API-compatibility gate** (japicmp or Revapi — selection is a sub-decision): CI fails on any binary-incompatible change to the SPI within a major version. The stamp is a declaration; this gate is what makes "additive within a major" true.
- **Driver contract test kit**, shipped with the reference driver and mock (Step 2): for every `default` SPI method, each driver either works or throws the typed exception with the expected reason. Each driver supplies its expected-unsupported list; the kit asserts exactly those methods throw.
- **`integrationTest` per driver** ([ticket 14](14-testing-strategy.md)) runs against exactly one GemFire version — the driver's own — replacing the range-coverage CI rule from tickets 15 and 18.

## Consequences

- **Supersedes [ticket 18](18-spi-impl-generation-range-compatibility.md)** (generation numbers, `[spiMin, spiMax]`/`[implMin, implMax]`, range intersection, effective range, closed-enum "no forward compatibility" argument) and **the Axis B parts of [ticket 15](15-driver-impl-self-declared-gemfire-compatibility-mechanics.md)** (`[nativeMin, nativeMax]`, `acceptsRuntimeGemFireVersion`, verdict-not-throw). The JDBC-style research in 15 and the dual-range research in 18 are retained as history.
- **Partly revives [ticket 10](10-spi-versioning-scheme.md)** (SPI SemVer), with a narrower role than originally written: `major.minor` is the **ABI** unit, used only for the additive-or-breaking decision. Ticket 18 rejected SemVer because it "says nothing about which GemFire feature generation" the SPI exposes. Under this proposal that question is answered elsewhere: the feature surface is the set of SPI methods, and which of them a given driver supports is fixed by the driver's version (= GemFire version) and by `default`/override.
- **Amends [ticket 07](07-unsupported-feature-fail-fast.md):** the fail-fast policy stands; its mechanism changes from `requireCapability` to `default`-method throws; the exception is renamed.
- **Supersedes [ticket 08](08-capability-enum-evolution-and-removal.md):** there is no enum. Its "no forward compatibility" rule is preserved, but enforced at bind time by the `SPI minor >= stamped minor` check instead of by absent enum vocabulary. Removal of a feature from the SPI is now only possible at a major bump, still gated by the support matrix (ticket 11 part 1, ticket 16) as a release-checklist item.
- **Amends [ticket 14](14-testing-strategy.md):** the range-enforcement amendments from tickets 15 and 18 are replaced by the per-driver exact-version `integrationTest` and the contract kit.
- **Code (Step 1 follow-up, separate pass):** remove `Capability`, `CapabilityAware`, `GenerationRange`, `NativeVersionRange`, `SpiGenerationIncompatibleException`, `acceptsRuntimeGemFireVersion`, and the capability/range members of `DriverMetadata`; rename the unsupported-feature exception; rewrite `GemFireDriverManager` around the stamp checks; update its tests.
- **Spec:** rewrite §5.1, §5.3, §6.1–§6.5, §10 and the related risks; add the ABI rules and the stamp description.
- **Process:** this reverses completed decisions (tickets 08, 15, 18), so per [ticket 17](17-spec-review-sign-off-process.md) it needs a fresh `rationale.md` §8 sign-off row before it is acted on.

## Decided and open points

The formerly provisional defaults below were confirmed on 2026-10-20 in [Finalize provisional driver release and binding details](21-finalize-provisional-driver-release-and-binding-details.md), which records the resolution. Full revised-design sign-off was subsequently recorded on 2026-10-20 in [the sign-off log](../rationale.md#8-sign-off-log).

1. **Native match strictness — decided: exact match** (2026-10-01). Every GemFire release needs a driver release; release automation from a template is expected.
2. **Driver-only fixes — decided.** Incrementing numeric revision appended to the target GemFire version; see the follow-up resolution.
3. **Maintenance of N drivers — deferred to Step 2.** A shared native-free base module could limit duplication; anything touching native types stays per driver.
4. **API-compat tool — deferred.** japicmp or Revapi, selected when the gate is wired into CI. The spec requires the gate, not the tool.
5. **Exception name — decided:** retain the existing name; see the follow-up resolution.
6. **Stamp location — decided:** retain the generated provider-keyed properties resource; see the follow-up resolution.
7. **`driver-core` in the graph — decided:** retain discovery/binding in `driver-core` and the §4 dependency arrangement; see the follow-up resolution.

Applied to the spec and to tickets 07, 08, 10, 14, 15, 18 (amendments) on 2026-10-01. Code changes for Step 1 (removing the capability and range types, stamp-based manager) are a separate follow-up.
