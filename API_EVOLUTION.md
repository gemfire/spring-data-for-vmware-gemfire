# GUD API Evolution Strategy

**Document Version:** 1.2
**Created:** 2026-03-13
**Updated:** 2026-04-02
**Status:** Implemented

## Executive Summary

This document describes the GemFire Unified Driver (GUD) architecture's strategy for handling API evolution across GemFire versions (10.1–10.3) while maintaining backward compatibility. The design ensures `spring-data-vmware-gemfire` runs seamlessly across multiple driver versions without requiring code changes in the Spring layer.

---

## Table of Contents

1. [Architecture Overview](#architecture-overview)
2. [API Evolution Scenarios](#api-evolution-scenarios)
3. [Implemented Design Decisions](#implemented-design-decisions)
4. [Migration Guide](#migration-guide)

---

## Architecture Overview

### Layer Diagram

```
┌────────────────────────────────────────────────────────────────────┐
│  spring-data-vmware-gemfire / spring-test-vmware-gemfire           │
│  (NO native GemFire references - only gud-api interfaces)          │
│  Uses GudVersionAwareInvoker for graceful degradation              │
└──────────────────────────┬─────────────────────────────────────────┘
                           │ depends on
┌──────────────────────────▼─────────────────────────────────────────┐
│                        gud-api (182+ interfaces)                    │
│  GudRegion, GudCache, GudClientCache, GudQueryService, etc.        │
│  GudCapability, GudApiVersion (part of API contract)               │
│  GudUnsupportedOperationException (type-safe, GudCapability enum)  │
│  GudCacheProvider (Supplier-based; configure() entry-point)        │
└──────────────────────────┬─────────────────────────────────────────┘
                           │ implemented by
┌──────────────────────────▼─────────────────────────────────────────┐
│                       gud-core (Driver SPI)                         │
│  GudDriver (SPI), GudDriverManager (ServiceLoader + push-to-provider)│
└──────────────────────────┬─────────────────────────────────────────┘
                           │ implemented by
┌──────────────────────────▼─────────────────────────────────────────┐
│        gud-driver-gemfire-{10.1, 10.2, 10.3}                       │
│  GemFireDriver with full capability sets                           │
│  (Direct org.apache.geode dependencies)                             │
└────────────────────────────────────────────────────────────────────┘
```

---

## API Evolution Scenarios

### Scenario 1: New Method Added to Native API

**Problem:** GemFire 10.1 added new methods to `PoolFactory`:
```java
PoolFactory setMinConnectionsPerServer(int min);  // NEW in 10.1
PoolFactory setMaxConnectionsPerServer(int max);  // NEW in 10.1
```

**Solution:** GUD interfaces use `default` methods with `GudCapability` enum:

```java
// GudPoolFactory.java
default GudPoolFactory setMinConnectionsPerServer(int minConnections) {
    throw new GudUnsupportedOperationException(
        "setMinConnectionsPerServer() is not supported. This feature was added in GemFire 10.1.",
        GudCapability.PER_SERVER_CONNECTION_LIMITS, "10.1");
}
```

The 10.1, 10.2, and 10.3 drivers override with native implementations.

### Scenario 2: Version-Specific Features

**Problem:** GemFire 10.1 added `setSegments()` to `DiskStoreFactory`.

**Solution:** Same `default` method pattern with `GudCapability.DISK_STORE_SEGMENTS`.

### Scenario 3: Exception Handling

**Problem:** Native GemFire exceptions must not leak into the Spring layer.

**Solution:** `GudDriver.translateException()` maps native exceptions to GUD types.

### Scenario 4: Graceful Degradation

**Problem:** Applications should continue working when a feature is not available in the current driver.

**Solution:** Spring FactoryBeans use `GudVersionAwareInvoker.invokeIfSupported()`:

```java
GudVersionAwareInvoker.invokeIfSupported(
    () -> poolFactory.setMinConnectionsPerServer(min),
    GudCapability.PER_SERVER_CONNECTION_LIMITS,
    logger,
    "Per-server connection limits (min={}) not supported by this GemFire version; skipping.", min
);
```

This replaces ad-hoc `try/catch` blocks scattered across FactoryBeans.

### Scenario 5: Handling Deprecated Features

**Solution:** GUD API interfaces mark deprecated methods with `@Deprecated` + `@deprecated` Javadoc. Driver implementations continue to support these methods for compatibility.

---

## Implemented Design Decisions

### 1. GudCapability and GudApiVersion in `gud-api` (not `gud-core`)

**Rationale:** Capability definitions and version semantics are part of the API contract, not implementation
details. Moving them to `gud-api` allows:
- `GudUnsupportedOperationException` to carry the `GudCapability` enum value directly (type-safe)
- Interface `default` methods to reference `GudCapability` without any module boundary issues
- Callers to inspect the required capability and minimum version programmatically

### 2. Single ServiceLoader Registration per Driver

**Before:** Each driver registered 4 separate service files (GudClientCacheFactory, GudCacheFactory, GudJndiBinding, GudPoolManager).

**After:** Each driver registers a single `GudDriver` service file. `GudDriverManager` obtains all
factories via `driver.createXxx()` and pushes them into `GudCacheProvider.configure()`.

**Benefits:**
- Single point of driver registration — no risk of partial registrations
- Single Responsibility Principle: `GudDriverManager` is the sole discovery authority
- Adding a new factory type requires no changes to service files

### 3. Supplier-Based ClientCacheFactory in GudCacheProvider

**Before:** `GudCacheProvider.getClientCacheFactory()` returned a shared singleton.

**Problem:** `ClientCacheFactory` is a **stateful builder** — returning the same instance across multiple calls led to configuration bleeding between `ClientCache` instances.

**After:** `GudCacheProvider.createClientCacheFactory()` invokes a `Supplier<GudClientCacheFactory>`,
returning a fresh, independently-configurable factory on each call.

### 4. GudDriverManager Push to GudCacheProvider

`GudDriverManager.registerDriver()` calls `GudCacheProvider.configure(...)` after registering a driver
as default. This keeps both the driver-manager and ServiceLoader registration paths in sync, preventing
the case where `GudCacheProvider` returns `null` factories when `GudDriverManager` has already loaded
a driver.

### 5. Fixed Version Comparator in GudDriverManager

**Before:** `Comparator.comparing(GudDriver::getSupportedVersion)` — lexicographic string sort.

**Problem:** `"10.9"` sorts after `"10.10"` lexicographically (incorrect).

**After:** `Comparator.comparing(d -> GudApiVersion.parse(d.getSupportedVersion()))` — numeric
per-segment comparison via `GudApiVersion.compareTo()`.

### 6. Fixed Race Condition in loadDriversIfNeeded()

**Before:** Check-then-act on `drivers.isEmpty()` was not thread-safe.

**After:** `loadDriversIfNeeded()` uses `AtomicBoolean loaded` with `synchronized loadDrivers()` to
ensure exactly-once loading even under concurrent initialisation.

### 7. GudDriver Extended with createJndiBinding() and createPoolManager()

`GudDriver` now includes `createJndiBinding()` and `createPoolManager()` methods, completing the
factory surface. This allows `GudDriverManager` to obtain all four factories from the driver and push
them atomically into `GudCacheProvider`.

---

## Capabilities by Version

| Version | Capabilities |
|---------|--------------|
| 10.1 | BASIC_CACHE_OPERATIONS, REGIONS, QUERIES, CONTINUOUS_QUERY, TRANSACTIONS, PDX_SERIALIZATION, FUNCTIONS, PER_SERVER_CONNECTION_LIMITS, DISK_STORE_SEGMENTS |
| 10.2 | All 10.1 capabilities |
| 10.3 | All 10.2 + SECURITY_MANAGER, SERVER_REGION_NAME |

---

## Migration Guide

### For Driver Implementers (Creating a New Version Driver)

1. **Create new module**: `gud-driver-gemfire-X.Y`

2. **Implement `GudDriver`** including:
   - `createClientCacheFactory()` — fresh factory per call
   - `createCacheFactory()` — peer cache factory
   - `createJndiBinding()` — JNDI binding implementation
   - `createPoolManager()` — pool manager implementation
   - `supportsCapability()` + `getCapabilities()` — accurate capability set
   - `translateException()` — native → GUD exception mapping

3. **Define `SUPPORTED_CAPABILITIES`** using `GudCapability.forVersion(SUPPORTED_VERSION)`
   or an explicit `EnumSet.of(...)`.

4. **Override `default` methods** for features supported in the new version:
   ```java
   @Override
   public GudPoolFactory setMinConnectionsPerServer(int min) {
       nativeFactory.setMinConnectionsPerServer(min);
       return this;
   }
   ```

5. **Register via ServiceLoader**: Create exactly one file:
   ```
   META-INF/services/org.springframework.data.gemfire.gud.core.GudDriver
   ```
   containing the fully-qualified class name of your `GemFireDriver` implementation.

### For spring-data-vmware-gemfire Maintainers

Use `GudVersionAwareInvoker.invokeIfSupported()` for version-specific calls:

```java
GudVersionAwareInvoker.invokeIfSupported(
    () -> diskStoreFactory.setSegments(this.segments),
    GudCapability.DISK_STORE_SEGMENTS,
    getLogger(),
    "DiskStore 'segments' ({}) not supported by this GemFire version; skipping.", this.segments
);
```

For capability-gated branches:

```java
if (GudDriverManager.isCapabilityAvailable(GudCapability.SECURITY_MANAGER)) {
    // Configure security-specific features
}
```

### For Users (Switching Drivers)

1. Change the `runtimeOnly` driver dependency in your `build.gradle.kts`:
   ```kotlin
   runtimeOnly(project(":gud-driver-gemfire-10.1"))
   ```

2. Update the GemFire native library dependency to match.

3. No code changes required — the driver is automatically discovered via ServiceLoader.
   Features not available in the older driver are silently skipped with a warning log.

---

## Key Files Reference

| File | Description |
|------|-------------|
| `gud-api/.../GudCapability.java` | Capability enum — now in `gud-api` |
| `gud-api/.../GudApiVersion.java` | Version class — now in `gud-api` |
| `gud-api/.../GudCacheProvider.java` | Supplier-based factory + `configure()` method |
| `gud-api/.../GudUnsupportedOperationException.java` | Type-safe with `GudCapability` field |
| `gud-core/.../GudDriver.java` | SPI interface including `createJndiBinding/PoolManager` |
| `gud-core/.../GudDriverManager.java` | Discovery, fixed comparator, race condition, push-to-provider |
| `spring-data-vmware-gemfire/.../GudVersionAwareInvoker.java` | Central graceful-degradation utility |
| `spring-data-vmware-gemfire/.../DiskStoreFactoryBean.java` | Uses `GudVersionAwareInvoker` |
| `spring-data-vmware-gemfire/.../PoolFactoryBean.java` | Uses `GudVersionAwareInvoker` |

---

## References

- [GUD Architecture](./GUD_ARCHITECTURE.md)
- [API Version Matrix](./API_VERSION_MATRIX.md)
- [GUD Migration Status](./GUD_MIGRATION_STATUS.md)
- [Java ServiceLoader Documentation](https://docs.oracle.com/javase/8/docs/api/java/util/ServiceLoader.html)
- [Semantic Versioning](https://semver.org/)
