# GUD API Version Matrix

> **Updated:** 2026-04-02 — Fixed capability sets for drivers 10.1, 10.2, 10.3;
> updated code examples to use `GudCapability` enum and `GudVersionAwareInvoker`.

This document tracks which GemFire API features are available in each version and
documents the corresponding `GudCapability` enum values used for type-safe detection.

## Supported Driver Versions

All driver modules share the same package (`org.springframework.data.gemfire.gud.driver`)
and class name (`GemFireDriver`). Only one driver module should be on the classpath at runtime.

| Driver Module | GemFire Version |
|---------------|-----------------|
| gud-driver-gemfire-10.1 | 10.1.x |
| gud-driver-gemfire-10.2 | 10.2.x |
| gud-driver-gemfire-10.3 | 10.3.x |

## Capability Matrix

| `GudCapability` | 10.1 | 10.2 | 10.3 | Notes |
|-----------------|------|------|------|-------|
| `BASIC_CACHE_OPERATIONS` | ✓ | ✓ | ✓ | All supported versions |
| `REGIONS` | ✓ | ✓ | ✓ | All supported versions |
| `QUERIES` | ✓ | ✓ | ✓ | All supported versions |
| `CONTINUOUS_QUERY` | ✓ | ✓ | ✓ | All supported versions |
| `TRANSACTIONS` | ✓ | ✓ | ✓ | All supported versions |
| `PDX_SERIALIZATION` | ✓ | ✓ | ✓ | All supported versions |
| `FUNCTIONS` | ✓ | ✓ | ✓ | All supported versions |
| `PER_SERVER_CONNECTION_LIMITS` | ✓ | ✓ | ✓ | Added in 10.1 |
| `DISK_STORE_SEGMENTS` | ✓ | ✓ | ✓ | Added in 10.1 |
| `SECURITY_MANAGER` | ✗ | ✗ | ✓ | Added in 10.3 |
| `SERVER_REGION_NAME` | ✗ | ✗ | ✓ | Added in 10.3 |

> **Note:** `PER_SERVER_CONNECTION_LIMITS` and `DISK_STORE_SEGMENTS` are available in all
> supported driver versions (10.1, 10.2, 10.3) since both features were introduced in GemFire 10.1.

## Version Summary (Feature-Level)

| Feature | 10.1 | 10.2 | 10.3 | Notes |
|---------|------|------|------|-------|
| Basic Cache Operations | ✓ | ✓ | ✓ | |
| PDX Serialization | ✓ | ✓ | ✓ | |
| Regions & Queries | ✓ | ✓ | ✓ | |
| Continuous Query | ✓ | ✓ | ✓ | |
| Transactions | ✓ | ✓ | ✓ | |
| Functions | ✓ | ✓ | ✓ | |
| `DiskStoreFactory.setSegments()` | ✓ | ✓ | ✓ | Added in 10.1 |
| `DiskStore.getSegments()` | ✓ | ✓ | ✓ | Added in 10.1 |
| Per-Server Connection Limits | ✓ | ✓ | ✓ | Added in 10.1 |
| Integrated Security Manager | ✗ | ✗ | ✓ | Added in 10.3 |
| `ClientRegionFactory.setServerRegionName()` | ✗ | ✗ | ✓ | Added in 10.3 |

## Deprecated Features

The following features are deprecated across all GemFire versions and should be avoided:

| Feature | Deprecated Since | Notes |
|---------|-----------------|-------|
| `PoolFactory.setThreadLocalConnections()` | 10.0 (Geode 1.10) | No-op, thread local connections are ignored |
| `ClientCacheFactory.setPdxDiskStore()` | 10.0 | PDX persistence not supported on client side |
| `ClientCacheFactory.setPdxPersistent()` | 10.0 | PDX persistence not supported on client side |
| `QueryService` hash index methods | 10.0 | Use standard indexes instead |
| `IndexType` enum | 10.0 | Use non-IndexType createIndex/defineIndex overloads |

## API Methods by Interface

### GudPoolFactory

| Method | 10.1 | 10.2 | 10.3 | `GudCapability` |
|--------|------|------|------|-----------------|
| `setMinConnectionsPerServer(int)` | ✓ | ✓ | ✓ | `PER_SERVER_CONNECTION_LIMITS` |
| `setMaxConnectionsPerServer(int)` | ✓ | ✓ | ✓ | `PER_SERVER_CONNECTION_LIMITS` |
| `setServerConnectionTimeout(int)` | ✓ | ✓ | ✓ | `BASIC_CACHE_OPERATIONS` |
| `setThreadLocalConnections(boolean)` | ⚠ | ⚠ | ⚠ | **Deprecated** — No-op, ignored |
| Other pool settings | ✓ | ✓ | ✓ | `BASIC_CACHE_OPERATIONS` |

### GudPool

| Method | 10.1 | 10.2 | 10.3 | `GudCapability` |
|--------|------|------|------|-----------------|
| `getMinConnectionsPerServer()` | ✓ | ✓ | ✓ | `PER_SERVER_CONNECTION_LIMITS` |
| `getMaxConnectionsPerServer()` | ✓ | ✓ | ✓ | `PER_SERVER_CONNECTION_LIMITS` |
| Other pool getters | ✓ | ✓ | ✓ | `BASIC_CACHE_OPERATIONS` |

### GudClientCacheFactory

| Method | 10.1 | 10.2 | 10.3 | `GudCapability` |
|--------|------|------|------|-----------------|
| `setPoolMinConnectionsPerServer(int)` | ✓ | ✓ | ✓ | `PER_SERVER_CONNECTION_LIMITS` |
| `setPoolMaxConnectionsPerServer(int)` | ✓ | ✓ | ✓ | `PER_SERVER_CONNECTION_LIMITS` |
| `setPdxDiskStore(String)` | ⚠ | ⚠ | ⚠ | **Deprecated** — Ignored on clients |
| `setPdxPersistent(boolean)` | ⚠ | ⚠ | ⚠ | **Deprecated** — Ignored on clients |
| Other factory settings | ✓ | ✓ | ✓ | `BASIC_CACHE_OPERATIONS` |

### GudClientRegionFactory

| Method | 10.1 | 10.2 | 10.3 | `GudCapability` |
|--------|------|------|------|-----------------|
| `setServerRegionName(String)` | ✗ | ✗ | ✓ | `SERVER_REGION_NAME` |
| Other client region settings | ✓ | ✓ | ✓ | `REGIONS` |

### GudQueryService

| Method | 10.1 | 10.2 | 10.3 | Notes |
|--------|------|------|------|-------|
| `createIndex(...)` | ✓ | ✓ | ✓ | All supported versions |
| `createKeyIndex(...)` | ✓ | ✓ | ✓ | All supported versions |
| `createHashIndex(...)` | ⚠ | ⚠ | ⚠ | **Deprecated** — Use createIndex instead |
| `defineHashIndex(...)` | ⚠ | ⚠ | ⚠ | **Deprecated** — Use defineIndex instead |
| `getIndexes(Region, IndexType)` | ⚠ | ⚠ | ⚠ | **Deprecated** — Use getIndexes(Region) |

### GudDiskStoreFactory

| Method | 10.1 | 10.2 | 10.3 | `GudCapability` |
|--------|------|------|------|-----------------|
| `setSegments(int)` | ✓ | ✓ | ✓ | `DISK_STORE_SEGMENTS` |
| Other disk store settings | ✓ | ✓ | ✓ | `BASIC_CACHE_OPERATIONS` |

### GudDiskStore

| Method | 10.1 | 10.2 | 10.3 | `GudCapability` |
|--------|------|------|------|-----------------|
| `getSegments()` | ✓ | ✓ | ✓ | `DISK_STORE_SEGMENTS` |
| Other disk store getters | ✓ | ✓ | ✓ | `BASIC_CACHE_OPERATIONS` |

## Default Method Strategy

For features not available in older versions, GUD API interfaces use `default` methods that throw
`GudUnsupportedOperationException` carrying the required `GudCapability` enum value. Newer drivers
override these methods with actual native implementations.

### Example: Version-Specific Default Method

```java
// In GudDiskStoreFactory interface
/**
 * Sets the number of disk store segments.
 *
 * @param segments the segment count
 * @return this factory for chaining
 * @throws GudUnsupportedOperationException if not supported by the driver
 * @since GemFire 10.1
 */
default GudDiskStoreFactory setSegments(int segments) {
    throw new GudUnsupportedOperationException(
        "setSegments() is not supported. This feature was added in GemFire 10.1.",
        GudCapability.DISK_STORE_SEGMENTS, "10.1");
}
```

Drivers that support the feature override the default:

```java
// In GemFireDiskStoreFactory (gud-driver-gemfire-10.1 through 10.3)
@Override
public GudDiskStoreFactory setSegments(int segments) {
    nativeFactory.setSegments(segments);
    return this;
}
```

Drivers that do not support the feature inherit the default, which throws `GudUnsupportedOperationException` on invocation.

## Graceful Degradation via GudVersionAwareInvoker

Spring FactoryBeans use `GudVersionAwareInvoker` to standardise the graceful degradation pattern:

```java
// DiskStoreFactoryBean.java
GudVersionAwareInvoker.invokeIfSupported(
    () -> diskStoreFactory.setSegments(this.segments),
    GudCapability.DISK_STORE_SEGMENTS,
    getLogger(),
    "DiskStore 'segments' setting ({}) is not supported by this GemFire version; skipping.",
    this.segments
);
```

```java
// PoolFactoryBean.java
GudVersionAwareInvoker.invokeIfSupported(
    () -> {
        poolFactory.setMaxConnectionsPerServer(this.maxConnectionsPerServer);
        poolFactory.setMinConnectionsPerServer(this.minConnectionsPerServer);
    },
    GudCapability.PER_SERVER_CONNECTION_LIMITS,
    getLogger(),
    "Per-server connection limits (min={}, max={}) are not supported by this GemFire version; skipping.",
    this.minConnectionsPerServer, this.maxConnectionsPerServer
);
```

This replaces scattered `try { ... } catch (GudUnsupportedOperationException ex) { ... }` blocks
with a single, consistent, well-documented pattern.

## Capability-Based Feature Gating

For optional features that should be proactively checked rather than reactively caught:

```java
if (GudDriverManager.isCapabilityAvailable(GudCapability.SECURITY_MANAGER)) {
    // Configure security-specific settings
}
```

```java
if (GudDriverManager.isCapabilityAvailable(GudCapability.PER_SERVER_CONNECTION_LIMITS)) {
    // Per-server limits are fully supported — configure without the invoker overhead
    poolFactory.setMinConnectionsPerServer(this.minConnectionsPerServer);
}
```

## Adding Support for a New GemFire Version

1. Create a new driver module: `gud-driver-gemfire-X.Y`
2. Implement `GemFireDriver` extending `GudDriver`
3. Define `SUPPORTED_CAPABILITIES` accurately (use `GudCapability.forVersion("X.Y")` as a baseline)
4. Implement `createClientCacheFactory()`, `createCacheFactory()`, `createJndiBinding()`, `createPoolManager()`
5. Implement `translateException()` for native exception mapping
6. Override `default` methods for newly-supported API surface
7. Register via a single ServiceLoader entry:
   `META-INF/services/org.springframework.data.gemfire.gud.core.GudDriver`
8. Add the new module to `settings.gradle.kts`
