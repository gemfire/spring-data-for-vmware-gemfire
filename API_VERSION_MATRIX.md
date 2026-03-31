# GUD API Version Matrix

This document tracks which GemFire API features are available in each version.

## Supported Driver Versions

All driver modules share the same package (`org.springframework.data.gemfire.gud.driver.gemfire`)
and class names (e.g. `GemFireDriver`, `GemFireCache`). Only one driver module should be on the
classpath at runtime.

| Driver Module | GemFire Version |
|---------------|-----------------|
| gud-driver-gemfire-10.0 | 10.0.x |
| gud-driver-gemfire-10.1 | 10.1.x |
| gud-driver-gemfire-10.2 | 10.2.x |
| gud-driver-gemfire-10.3 | 10.3.x (develop) |

## Version Summary

| Feature | 10.0 | 10.1 | 10.2 | 10.3 | Notes |
|---------|------|------|------|------|-------|
| Basic Cache Operations | ✓ | ✓ | ✓ | ✓ | |
| PDX Serialization | ✓ | ✓ | ✓ | ✓ | |
| Regions & Queries | ✓ | ✓ | ✓ | ✓ | |
| Continuous Query | ✓ | ✓ | ✓ | ✓ | |
| Transactions | ✓ | ✓ | ✓ | ✓ | |
| Functions | ✓ | ✓ | ✓ | ✓ | |
| Disk Stores | ✓ | ✓ | ✓ | ✓ | |
| WAN Replication | ✓ | ✓ | ✓ | ✓ | |
| DiskStoreFactory.setSegments() | ✗ | ✓ | ✓ | ✓ | Added in 10.1 |
| DiskStore.getSegments() | ✗ | ✓ | ✓ | ✓ | Added in 10.1 |
| Per-Server Connection Limits | ✗ | ✓ | ✓ | ✓ | Added in 10.1 |
| ClientRegionFactory.setServerRegionName() | ✗ | ✗ | ✗ | ✓ | Added in 10.3 |

## Deprecated Features

The following features are deprecated across all GemFire versions and should be avoided:

| Feature | Deprecated Since | Notes |
|---------|-----------------|-------|
| PoolFactory.setThreadLocalConnections() | 10.0 (Geode 1.10) | No-op, thread local connections are ignored |
| ClientCacheFactory.setPdxDiskStore() | 10.0 | PDX persistence not supported on client side |
| ClientCacheFactory.setPdxPersistent() | 10.0 | PDX persistence not supported on client side |
| QueryService hash index methods | 10.0 | Use standard indexes instead |
| IndexType enum | 10.0 | Use non-IndexType createIndex/defineIndex overloads |

## API Methods by Interface

### GudPoolFactory

| Method | 10.0 | 10.1 | 10.2 | 10.3 | Notes |
|--------|------|------|------|------|-------|
| setMinConnectionsPerServer(int) | ✗ | ✓ | ✓ | ✓ | Added in 10.1 |
| setMaxConnectionsPerServer(int) | ✗ | ✓ | ✓ | ✓ | Added in 10.1 |
| setServerConnectionTimeout(int) | ✓ | ✓ | ✓ | ✓ | All versions |
| setThreadLocalConnections(boolean) | ⚠ | ⚠ | ⚠ | ⚠ | **Deprecated** - No-op, ignored |
| Other pool settings | ✓ | ✓ | ✓ | ✓ | All versions |

### GudPool

| Method | 10.0 | 10.1 | 10.2 | 10.3 | Notes |
|--------|------|------|------|------|-------|
| getMinConnectionsPerServer() | ✗ | ✓ | ✓ | ✓ | Added in 10.1 |
| getMaxConnectionsPerServer() | ✗ | ✓ | ✓ | ✓ | Added in 10.1 |
| Other pool getters | ✓ | ✓ | ✓ | ✓ | All versions |

### GudClientCacheFactory

| Method | 10.0 | 10.1 | 10.2 | 10.3 | Notes |
|--------|------|------|------|------|-------|
| setPoolMinConnectionsPerServer(int) | ✗ | ✓ | ✓ | ✓ | Added in 10.1 |
| setPoolMaxConnectionsPerServer(int) | ✗ | ✓ | ✓ | ✓ | Added in 10.1 |
| setPdxDiskStore(String) | ⚠ | ⚠ | ⚠ | ⚠ | **Deprecated** - Ignored on clients |
| setPdxPersistent(boolean) | ⚠ | ⚠ | ⚠ | ⚠ | **Deprecated** - Ignored on clients |
| Other pool settings | ✓ | ✓ | ✓ | ✓ | All versions |

### GudClientRegionFactory

| Method | 10.0 | 10.1 | 10.2 | 10.3 | Notes |
|--------|------|------|------|------|-------|
| setServerRegionName(String) | ✗ | ✗ | ✗ | ✓ | Added in 10.3 |
| Other client region settings | ✓ | ✓ | ✓ | ✓ | All versions |

### GudQueryService

| Method | 10.0 | 10.1 | 10.2 | 10.3 | Notes |
|--------|------|------|------|------|-------|
| createIndex(...) | ✓ | ✓ | ✓ | ✓ | All versions |
| createKeyIndex(...) | ✓ | ✓ | ✓ | ✓ | All versions |
| createHashIndex(...) | ⚠ | ⚠ | ⚠ | ⚠ | **Deprecated** - Use createIndex instead |
| defineHashIndex(...) | ⚠ | ⚠ | ⚠ | ⚠ | **Deprecated** - Use defineIndex instead |
| getIndexes(Region, IndexType) | ⚠ | ⚠ | ⚠ | ⚠ | **Deprecated** - Use getIndexes(Region) |

### GudDiskStoreFactory

| Method | 10.0 | 10.1 | 10.2 | 10.3 | Notes |
|--------|------|------|------|------|-------|
| setSegments(int) | ✗ | ✓ | ✓ | ✓ | Added in 10.1 |
| Other disk store settings | ✓ | ✓ | ✓ | ✓ | All versions |

### GudDiskStore

| Method | 10.0 | 10.1 | 10.2 | 10.3 | Notes |
|--------|------|------|------|------|-------|
| getSegments() | ✗ | ✓ | ✓ | ✓ | Added in 10.1 |
| Other disk store getters | ✓ | ✓ | ✓ | ✓ | All versions |

## Default Method Strategy

For features not available in older versions, the GUD API uses default methods that throw
`GudUnsupportedOperationException`. Newer drivers override these methods with actual implementations.

### Example: Adding a Version-Specific Feature

1. **Define as default in API interface** (throws exception):

```java
// In GudDiskStoreFactory interface
default GudDiskStoreFactory setSegments(int segments) {
    throw new GudUnsupportedOperationException(
        "setSegments() is not supported. This feature was added in GemFire 10.1.",
        "DISK_STORE_SEGMENTS", "10.1");
}
```

2. **Override in drivers that support it** (10.1+):

```java
// In GemFireDiskStoreFactory (gud-driver-gemfire-10.1)
@Override
public GudDiskStoreFactory setSegments(int segments) {
    nativeFactory.setSegments(segments);
    return this;
}
```

3. **Do NOT override in older drivers** (10.0) - they use the inherited default.

## Graceful Degradation in Spring Data GemFire

Spring Data GemFire wraps calls to version-specific methods in try-catch blocks:

```java
private void configureSegments(GudDiskStoreFactory diskStoreFactory) {
    if (this.segments != null) {
        try {
            diskStoreFactory.setSegments(this.segments);
        } catch (GudUnsupportedOperationException ex) {
            getLogger().warn("DiskStore 'segments' setting ({}) is not supported: {}",
                this.segments, ex.getMessage());
        }
    }
}
```

This allows applications to use the same configuration across different GemFire versions,
with warnings logged for unsupported features.

## Adding Support for a New GemFire Version

1. Create a new driver module: `gud-driver-gemfire-X.Y`
2. Copy adapter classes from nearest version
3. All drivers share the same package: `org.springframework.data.gemfire.gud.driver.gemfire`
4. All drivers share the same class names: `GemFireDriver`, `GemFireCache`, etc.
5. Override default methods for newly supported features
6. Register via ServiceLoader
7. Add to `settings.gradle.kts`
