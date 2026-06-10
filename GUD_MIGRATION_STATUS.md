# GUD Migration Status

**Last Updated:** 2026-04-02
**Current Phase:** 9 - Architectural Hardening (COMPLETE)
**Compilation Status:**
- spring-data-vmware-gemfire:compileJava - SUCCESS (0 errors)
- spring-test-vmware-gemfire:compileJava - SUCCESS (0 errors)
**Architecture Rule:** NO reflection in spring-data/spring-test modules - ONLY gud-api references
**Total GUD API Types:** 200+
**Total Files to Migrate:** 188 (main) + ~20 (test support) — all complete

---

## Phase Status

- [x] Phase 1: Infrastructure Setup — COMPLETE
- [x] Phase 2: GUD API Design — COMPLETE (197+ types created)
- [x] Phase 3: Planning and Analysis — COMPLETE
- [x] Phase 4: Deviation Documentation — COMPLETE
- [x] Phase 5: Resolution — COMPLETE (GUD API types created)
- [x] Phase 6: Driver Implementation — COMPLETE (core adapters created for all 3 supported versions)
- [x] Phase 7: Source Code Migration — COMPLETE (spring-data-vmware-gemfire main sources)
- [x] Phase 8: Test Migration — COMPLETE (spring-test-vmware-gemfire compiles successfully)
- [x] Phase 9: Architectural Hardening — COMPLETE (see details below)

---

## Phase 9 Changes (2026-04-02)

This phase addressed architectural issues identified during the SOLID evaluation.

### Critical Priority — Resolved

#### C1: GudCapability and GudApiVersion moved from `gud-core` to `gud-api`
**Problem:** Capability definitions and version semantics are part of the API contract.
Having them in `gud-core` forced `GudUnsupportedOperationException` to use String-based
capability names instead of a type-safe enum.

**Resolution:**
- Created `gud-api/.../GudCapability.java` (formerly `gud-core/.../GudCapability.java`)
- Created `gud-api/.../GudApiVersion.java` (formerly `gud-core/.../GudApiVersion.java`)
- Deleted the old files from `gud-core`
- Updated all imports in `GudDriver.java`, `GudDriverManager.java`, and all 3 `GemFireDriver.java` files

#### C2: GudUnsupportedOperationException now uses GudCapability enum (type-safe)
**Problem:** Constructor took `String requiredCapability` — callers could not inspect the
capability programmatically and were prone to typos.

**Resolution:**
- Rewrote `GudUnsupportedOperationException` with `GudCapability requiredCapability` field
- Updated all 8 `default` method implementations in GUD API interfaces:
  - `GudPoolFactory` — `setMinConnectionsPerServer`, `setMaxConnectionsPerServer`
  - `GudClientCacheFactory` — `setPoolMinConnectionsPerServer`, `setPoolMaxConnectionsPerServer`
  - `GudDiskStoreFactory` — `setSegments`
  - `GudClientRegionFactory` — `setServerRegionName`
  - `GudPool` — `getMinConnectionsPerServer`, `getMaxConnectionsPerServer`

### High Priority — Resolved

#### H1: Single ServiceLoader registration per driver
**Problem:** Each driver registered 4 separate service files, creating fragility (partial
registration, SRP violation, maintenance burden).

**Resolution:**
- Deleted the 4 individual service files from each of the 3 driver modules (12 files total):
  - `META-INF/services/org.springframework.data.gemfire.gud.api.GudClientCacheFactory`
  - `META-INF/services/org.springframework.data.gemfire.gud.api.GudCacheFactory`
  - `META-INF/services/org.springframework.data.gemfire.gud.api.GudJndiBinding`
  - `META-INF/services/org.springframework.data.gemfire.gud.api.GudPoolManager`
- Only `META-INF/services/org.springframework.data.gemfire.gud.core.GudDriver` remains per driver

#### H2: GudDriver extended with createJndiBinding() and createPoolManager()
**Problem:** `GudDriver` only declared `createClientCacheFactory()` and `createCacheFactory()`,
preventing `GudDriverManager` from obtaining all four factories from the driver.

**Resolution:**
- Added `createJndiBinding()` and `createPoolManager()` to `GudDriver` interface
- Implemented in all 3 `GemFireDriver` classes

#### H3: GudDriverManager pushes to GudCacheProvider on registration
**Problem:** `GudCacheProvider` and `GudDriverManager` were independent registries with no
coordination, violating SRP and leading to potential null-factory scenarios.

**Resolution:**
- Added `GudCacheProvider.configure(Supplier, GudCacheFactory, GudJndiBinding, GudPoolManager)` method
- `GudDriverManager.registerDriver()` calls `GudCacheProvider.configure()` after setting default driver

#### H4: Supplier-based factory in GudCacheProvider
**Problem:** `createClientCacheFactory()` returned a shared singleton — wrong for stateful builders.

**Resolution:**
- Changed `clientCacheFactorySupplier` from `GudClientCacheFactory` to `Supplier<GudClientCacheFactory>`
- Each call to `createClientCacheFactory()` now produces a fresh factory instance

#### H5: Fixed version sort in GudDriverManager.getDriverForGemFireVersion()
**Problem:** Lexicographic comparator (`Comparator.comparing(GudDriver::getSupportedVersion)`)
produces wrong ordering for `"10.9"` vs `"10.10"`.

**Resolution:**
- Changed to `Comparator.comparing(d -> GudApiVersion.parse(d.getSupportedVersion()))`

#### H6: Fixed race condition in loadDriversIfNeeded()
**Problem:** Check-then-act on `drivers.isEmpty()` was not thread-safe.

**Resolution:**
- Added `AtomicBoolean loaded` flag; `loadDrivers()` is `synchronized`

#### H7: GudVersionAwareInvoker utility (new)
**Problem:** Ad-hoc `try/catch GudUnsupportedOperationException` scattered across FactoryBeans.

**Resolution:**
- Created `spring-data-vmware-gemfire/.../support/GudVersionAwareInvoker.java`
- Refactored `DiskStoreFactoryBean.configureSegments()` to use it
- Refactored `PoolFactoryBean.configurePerServerConnectionLimits()` to use it

### Medium Priority — Resolved

#### M1: Fixed capability sets in 10.1 and 10.2 drivers
**Problem:** Drivers 10.1 and 10.2 were missing `PER_SERVER_CONNECTION_LIMITS` and
`DISK_STORE_SEGMENTS` from their capability sets (both were introduced in 10.1).
Driver 10.3 was missing `DISK_STORE_SEGMENTS`.

**Resolution:** Updated `SUPPORTED_CAPABILITIES` in all three drivers.

#### M2: Removed GudAttributesFactoryImpl from gud-api
**Problem:** `GudAttributesFactoryImpl` was an implementation class inside `gud-api`,
violating the principle that API modules should contain only contracts.

**Resolution:** Deleted the file.

#### M3: Fixed stacked duplicate license headers
**Problem:** The 5 API files modified in this session had 2–5 duplicate `$originalComment`
Velocity-template-style copyright blocks.

**Resolution:** Replaced with a single clean `// Copyright (c) 2026 Broadcom. All Rights Reserved.`
header in the 5 files modified: `GudPoolFactory`, `GudDiskStoreFactory`, `GudClientCacheFactory`,
`GudClientRegionFactory`, `GudPool`.

---

## Module Status (2026-04-02)

| Module | Status | Compilation |
|--------|--------|-------------|
| gud-api | COMPLETE | SUCCESS |
| gud-core | COMPLETE | SUCCESS |
| gud-driver-gemfire-10.1 | COMPLETE | SUCCESS |
| gud-driver-gemfire-10.2 | COMPLETE | SUCCESS |
| gud-driver-gemfire-10.3 | COMPLETE | SUCCESS |
| spring-data-vmware-gemfire/main | COMPLETE | SUCCESS |
| spring-data-vmware-gemfire/test | COMPLETE | SUCCESS |
| spring-test-vmware-gemfire | COMPLETE | SUCCESS |

---

## Architecture Invariants (Updated)

The following invariants must be maintained as the codebase evolves:

1. **No reflection** in spring-data-vmware-gemfire and spring-test-vmware-gemfire
2. **Only gud-api references** in spring-data and spring-test modules (no gud-core, no native GemFire)
3. **GudCapability and GudApiVersion live in gud-api** — they are part of the API contract
4. **Single ServiceLoader entry per driver** (`GudDriver` only)
5. **GudCacheProvider.createClientCacheFactory() returns fresh instances** (Supplier pattern)
6. **All version-specific calls in Spring FactoryBeans use GudVersionAwareInvoker**
7. **No GudAttributesFactoryImpl or other implementation classes in gud-api**

---

## Resume Instructions

### Quick Verification
```bash
cd /Users/udo/projects/spring-data-for-vmware-gemfire

# Verify main sources compile
./gradlew :spring-data-vmware-gemfire:compileJava 2>&1 | grep "error:" | wc -l
# Expected: 0

# Verify all driver modules compile
./gradlew :gud-driver-gemfire-10.1:compileJava \
          :gud-driver-gemfire-10.2:compileJava \
          :gud-driver-gemfire-10.3:compileJava 2>&1 | grep "error:"
# Expected: (no output)
```

---

## GUD API Types Created (200+ total)

**Core Types:** GudRegion, GudCache, GudClientCache, GudRegionService, GudQueryService,
GudQuery, GudSelectResults, GudPool, GudPoolFactory, GudPoolManager

**Version/Capability:** GudCapability (in `gud-api`), GudApiVersion (in `gud-api`)

**Spring Layer Utilities:** GudVersionAwareInvoker (in `spring-data-vmware-gemfire/support`)

**Region Configuration:** GudRegionFactory, GudClientRegionFactory, GudRegionAttributes,
GudAttributesMutator, GudDataPolicy, GudScope, GudRegionShortcut, GudClientRegionShortcut

**Callbacks:** GudCacheLoader, GudCacheWriter, GudCacheListener, GudTransactionListener,
GudTransactionWriter, GudTransactionEvent, GudCacheTransactionManager, GudCacheCallback,
GudDeclarable

**PDX:** GudPdxSerializer, GudPdxReader, GudPdxWriter, GudPdxInstance

**Functions:** GudFunction, GudFunctionContext, GudFunctionService, GudExecution

**Continuous Query:** GudCqQuery, GudCqEvent, GudCqAttributes

**Compression:** GudCompressor, GudSnappyCompressor

**Eviction:** GudEvictionAttributes, GudEvictionAction, GudEvictionAlgorithm, GudObjectSizer

**Exceptions (52+):** GudGemFireException, GudGemFireCheckedException, GudCacheException,
GudRegionException, GudQueryException, GudCacheLoaderException, GudTimeoutException,
GudTransactionException, and 44+ more

---

## Git Branch

Current branch: `udo.kohlmeyer/unified_driver`

```bash
git status
# Shows modified files across gud-api, gud-core, all driver modules,
# spring-data-vmware-gemfire, and documentation
```
