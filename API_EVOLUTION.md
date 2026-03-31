# GUD API Evolution Strategy

**Document Version:** 1.1  
**Created:** 2026-03-13  
**Updated:** 2026-03-17  
**Status:** Implemented

## Executive Summary

This document analyzes the GemFire Unified Driver (GUD) architecture's ability to handle API evolution across GemFire versions (10.0, 10.1, 10.2, 10.3) while maintaining backward compatibility. It documents the implemented SPI design that ensures `spring-data-vmware-gemfire` can run seamlessly on multiple driver versions.

---

## Table of Contents

1. [Current Architecture](#current-architecture)
2. [Problem Statement](#problem-statement)
3. [Gap Analysis](#gap-analysis)
4. [Proposed Changes](#proposed-changes)
5. [Implementation Prompt](#implementation-prompt)
6. [Migration Guide](#migration-guide)

---

## Current Architecture

### Layer Diagram

```
┌────────────────────────────────────────────────────────────────────┐
│  spring-data-vmware-gemfire / spring-test-vmware-gemfire           │
│  (NO native GemFire references - only gud-api interfaces)          │
└──────────────────────────┬─────────────────────────────────────────┘
                           │ depends on
┌──────────────────────────▼─────────────────────────────────────────┐
│                        gud-api (182+ interfaces)                    │
│  GudRegion, GudCache, GudClientCache, GudQueryService, etc.        │
│  (Pure Java interfaces - NO native GemFire dependencies)            │
└──────────────────────────┬─────────────────────────────────────────┘
                           │ implemented by
┌──────────────────────────▼─────────────────────────────────────────┐
│                       gud-core (Driver SPI)                         │
│  GudDriver, GudDriverManager (ServiceLoader-based discovery)       │
└──────────────────────────┬─────────────────────────────────────────┘
                           │ implemented by
┌──────────────────────────▼─────────────────────────────────────────┐
│              gud-driver-gemfire-10.3 (Version-specific)             │
│  GemFireRegion, GemFireCache, GemFireDriver                        │
│  (Direct org.apache.geode dependencies)                             │
└────────────────────────────────────────────────────────────────────┘
```

### Current SPI Interface

```java
// gud-core/src/main/java/.../GudDriver.java
public interface GudDriver {
    String getName();
    String getSupportedVersion();
    GudCache wrapCache(Object nativeCache);
    GudClientCache wrapClientCache(Object nativeClientCache);
    <K, V> GudRegion<K, V> wrapRegion(Object nativeRegion);
    GudPool wrapPool(Object nativePool);
    <T> T unwrap(Object gudObject);
}
```

### Current Driver Manager

```java
// gud-core/src/main/java/.../GudDriverManager.java
public final class GudDriverManager {
    private static final ConcurrentMap<String, GudDriver> drivers = new ConcurrentHashMap<>();
    private static volatile GudDriver defaultDriver;
    
    public static void loadDrivers() {
        ServiceLoader<GudDriver> serviceLoader = ServiceLoader.load(GudDriver.class);
        for (GudDriver driver : serviceLoader) {
            registerDriver(driver);
        }
    }
    
    public static GudDriver getDefaultDriver() {
        if (defaultDriver == null) {
            loadDrivers();
        }
        if (defaultDriver == null) {
            throw new IllegalStateException("No GUD driver registered");
        }
        return defaultDriver;
    }
}
```

---

## Problem Statement

When supporting multiple GemFire versions with API differences, the following scenarios must be handled gracefully:

### Scenario 1: New Method Added to Native API

```java
// GemFire 10.1 adds new method to PoolFactory
public interface PoolFactory {
    // Existing methods...
    PoolFactory setMinConnectionsPerServer(int min);  // NEW in 10.1
    PoolFactory setMaxConnectionsPerServer(int max);  // NEW in 10.1
}
```

**Solution:** GUD interfaces use `default` methods that throw `GudUnsupportedOperationException`. Drivers for 10.1+ override with actual implementations.

### Scenario 2: Version-Specific Features

```java
// GemFire 10.1 adds setSegments() to DiskStoreFactory
DiskStoreFactory.setSegments(int segments);  // NEW in 10.1
```

**Solution:** The 10.0 driver uses the default method that throws. The 10.1, 10.2, and 10.3 drivers override.

### Scenario 3: Exception Handling

Native GemFire exceptions need to be translated to GUD exceptions.

**Solution:** `GudDriver.translateException()` maps native exceptions to GUD types, keeping the Spring layer independent of native types.

### Scenario 4: Graceful Degradation

Applications should continue working even when using features not available in older versions.

**Solution:** Spring Data beans catch `GudUnsupportedOperationException` and log warnings, allowing applications to work across versions.

### Scenario 5: Handling Deprecated Features

GemFire deprecates certain APIs over time. The GUD layer tracks these deprecations to help applications migrate.

**Current Deprecated Features:**

| Feature | Deprecated Since | Reason |
|---------|-----------------|--------|
| `PoolFactory.setThreadLocalConnections()` | 10.0 (Geode 1.10) | Thread local connections are ignored, no-op |
| `ClientCacheFactory.setPdxDiskStore()` | 10.0 | PDX persistence not supported on client side |
| `ClientCacheFactory.setPdxPersistent()` | 10.0 | PDX persistence not supported on client side |
| `QueryService.createHashIndex()` | 10.0 | Hash indexes deprecated, use standard indexes |
| `QueryService.defineHashIndex()` | 10.0 | Hash indexes deprecated, use standard indexes |
| `QueryService.getIndexes(Region, IndexType)` | 10.0 | IndexType parameter deprecated |
| `IndexType` enum | 10.0 | Use non-IndexType method overloads |

**Solution:** GUD API interfaces mark deprecated methods with `@Deprecated` annotation and include `@deprecated` Javadoc with migration guidance. Driver implementations continue to support these methods for compatibility.

---

## Implementation Status

| Aspect | Status | Implementation |
|--------|--------|----------------|
| Basic wrapping | ✅ Complete | Drivers wrap native objects |
| Driver discovery | ✅ Complete | ServiceLoader + version priority |
| API evolution (new methods) | ✅ Complete | Default methods + GudCapability |
| API evolution (deprecated methods) | ✅ Complete | @Deprecated annotations + Javadoc |
| API evolution (signature changes) | ✅ Complete | Version-aware factories |
| Exception translation | ✅ Complete | GudDriver.translateException() |
| Factory abstraction | ✅ Complete | createXxxFactory() methods |
| Default implementations | ✅ Complete | All interfaces have defaults |
| Version comparison | ✅ Complete | GudApiVersion class |

### Implemented Features

#### Capability Detection

Drivers report supported capabilities:

```java
if (driver.supportsCapability(GudCapability.PER_SERVER_CONNECTION_LIMITS)) {
    poolFactory.setMinConnectionsPerServer(5);
}
```

#### Default Method Pattern

GUD API interfaces use default implementations:

```java
public interface GudPoolFactory {
    /**
     * @since GemFire 10.1
     */
    default GudPoolFactory setMinConnectionsPerServer(int minConnections) {
        throw new GudUnsupportedOperationException(
            "setMinConnectionsPerServer() is not supported. This feature was added in GemFire 10.1.",
            "PER_SERVER_CONNECTION_LIMITS", "10.1");
    }
}
```

#### Graceful Degradation in Spring Data

Spring Data beans catch unsupported operations:

```java
// In PoolFactoryBean
if (this.minConnectionsPerServer != null) {
    try {
        poolFactory.setMinConnectionsPerServer(this.minConnectionsPerServer);
    } catch (GudUnsupportedOperationException ex) {
        getLogger().warn("Pool 'minConnectionsPerServer' setting ({}) is not supported: {}",
            this.minConnectionsPerServer, ex.getMessage());
    }
}
```

#### Exception Translation

```java
// Drivers translate native exceptions
try {
    region.get(key);
} catch (GudCacheClosedException e) {  // GUD type, not native
    // Handle appropriately
}
```

---

## Proposed Changes

### 1. Enhanced GudDriver Interface

```java
package org.springframework.data.gemfire.gud.core;

import java.util.Set;

public interface GudDriver {
    
    // ===== Identity (existing) =====
    String getName();
    String getSupportedVersion();
    
    // ===== Version Information (NEW) =====
    
    /**
     * Returns the minimum GUD API version this driver supports.
     */
    GudApiVersion getMinimumApiVersion();
    
    /**
     * Returns the maximum GUD API version this driver supports.
     */
    GudApiVersion getMaximumApiVersion();
    
    // ===== Capability Detection (NEW) =====
    
    /**
     * Checks if this driver supports a specific capability.
     * 
     * @param capability the capability to check
     * @return true if supported
     */
    boolean supportsCapability(GudCapability capability);
    
    /**
     * Returns all capabilities supported by this driver.
     */
    Set<GudCapability> getCapabilities();
    
    // ===== Wrapping (existing) =====
    GudCache wrapCache(Object nativeCache);
    GudClientCache wrapClientCache(Object nativeClientCache);
    <K, V> GudRegion<K, V> wrapRegion(Object nativeRegion);
    GudPool wrapPool(Object nativePool);
    <T> T unwrap(Object gudObject);
    
    // ===== Factory Creation (NEW) =====
    
    /**
     * Creates a cache factory for peer cache creation.
     */
    GudCacheFactory createCacheFactory();
    
    /**
     * Creates a client cache factory.
     */
    GudClientCacheFactory createClientCacheFactory();
    
    // ===== Exception Handling (NEW) =====
    
    /**
     * Translates a native GemFire exception to a GUD exception.
     * 
     * @param nativeException the native exception
     * @return a GUD API exception
     */
    GudException translateException(Throwable nativeException);
    
    // ===== Feature Detection Convenience Methods (NEW) =====
    
    default boolean supportsPartitionStatistics() {
        return supportsCapability(GudCapability.PARTITION_STATISTICS);
    }
    
    default boolean supportsEnhancedSecurity() {
        return supportsCapability(GudCapability.ENHANCED_SECURITY);
    }
}
```

### 2. GudCapability Enum

```java
package org.springframework.data.gemfire.gud.core;

/**
 * Enumeration of capabilities that may vary between GemFire versions.
 */
public enum GudCapability {
    
    // Core capabilities (all versions)
    BASIC_CACHE_OPERATIONS("Basic cache get/put/remove", "10.0"),
    REGIONS("Region creation and management", "10.0"),
    QUERIES("OQL query execution", "10.0"),
    CONTINUOUS_QUERY("Continuous query support", "10.0"),
    TRANSACTIONS("Transaction support", "10.0"),
    PDX_SERIALIZATION("PDX serialization", "10.0"),
    FUNCTIONS("Function execution", "10.0"),

    // 10.1+ capabilities
    PER_SERVER_CONNECTION_LIMITS("Per-server min/max connection limits", "10.1"),
    DISK_STORE_SEGMENTS("Disk store segments API", "10.1"),

    // 10.3+ capabilities
    SECURITY_MANAGER("Integrated security manager", "10.3");
    
    private final String description;
    private final String minimumVersion;
    
    GudCapability(String description, String minimumVersion) {
        this.description = description;
        this.minimumVersion = minimumVersion;
    }
    
    public String getDescription() {
        return description;
    }
    
    public String getMinimumVersion() {
        return minimumVersion;
    }
    
    /**
     * Returns capabilities available at or below the given version.
     */
    public static Set<GudCapability> forVersion(String version) {
        return Arrays.stream(values())
            .filter(c -> compareVersions(c.minimumVersion, version) <= 0)
            .collect(Collectors.toSet());
    }
    
    private static int compareVersions(String v1, String v2) {
        // Simple version comparison implementation
        String[] parts1 = v1.split("\\.");
        String[] parts2 = v2.split("\\.");
        for (int i = 0; i < Math.max(parts1.length, parts2.length); i++) {
            int p1 = i < parts1.length ? Integer.parseInt(parts1[i]) : 0;
            int p2 = i < parts2.length ? Integer.parseInt(parts2[i]) : 0;
            if (p1 != p2) return Integer.compare(p1, p2);
        }
        return 0;
    }
}
```

### 3. GudApiVersion Class

```java
package org.springframework.data.gemfire.gud.core;

/**
 * Represents a GUD API version for compatibility checking.
 */
public final class GudApiVersion implements Comparable<GudApiVersion> {
    
    /** GUD API version 1.0 - initial release supporting GemFire 10.0 through 10.3 */
    public static final GudApiVersion V1_0 = new GudApiVersion(1, 0, 0);
    
    private final int major;
    private final int minor;
    private final int patch;
    
    public GudApiVersion(int major, int minor, int patch) {
        this.major = major;
        this.minor = minor;
        this.patch = patch;
    }
    
    public static GudApiVersion parse(String version) {
        String[] parts = version.split("\\.");
        return new GudApiVersion(
            Integer.parseInt(parts[0]),
            parts.length > 1 ? Integer.parseInt(parts[1]) : 0,
            parts.length > 2 ? Integer.parseInt(parts[2]) : 0
        );
    }
    
    public boolean isCompatibleWith(GudApiVersion other) {
        return this.major == other.major && this.minor >= other.minor;
    }
    
    @Override
    public int compareTo(GudApiVersion other) {
        int result = Integer.compare(this.major, other.major);
        if (result == 0) result = Integer.compare(this.minor, other.minor);
        if (result == 0) result = Integer.compare(this.patch, other.patch);
        return result;
    }
    
    @Override
    public String toString() {
        return major + "." + minor + "." + patch;
    }
}
```

### 4. Enhanced GudDriverManager

```java
package org.springframework.data.gemfire.gud.core;

import java.util.*;

public final class GudDriverManager {
    
    private static final ConcurrentMap<String, GudDriver> drivers = new ConcurrentHashMap<>();
    private static volatile GudDriver defaultDriver;
    
    // ===== Existing methods (unchanged) =====
    
    public static void loadDrivers() { /* ... */ }
    public static void registerDriver(GudDriver driver) { /* ... */ }
    public static GudDriver getDriver(String name) { /* ... */ }
    public static GudDriver getDefaultDriver() { /* ... */ }
    
    // ===== New version-aware methods =====
    
    /**
     * Gets a driver compatible with the specified GemFire version.
     * Returns the highest version driver that supports the requested version.
     */
    public static GudDriver getDriverForGemFireVersion(String gemfireVersion) {
        loadDriversIfNeeded();
        return drivers.values().stream()
            .filter(d -> isVersionCompatible(d.getSupportedVersion(), gemfireVersion))
            .max(Comparator.comparing(GudDriver::getSupportedVersion))
            .orElseThrow(() -> new IllegalStateException(
                "No driver found for GemFire version: " + gemfireVersion));
    }
    
    /**
     * Gets all drivers that support a specific capability.
     */
    public static List<GudDriver> getDriversWithCapability(GudCapability capability) {
        loadDriversIfNeeded();
        return drivers.values().stream()
            .filter(d -> d.supportsCapability(capability))
            .collect(Collectors.toList());
    }
    
    /**
     * Checks if any registered driver supports the given capability.
     */
    public static boolean isCapabilityAvailable(GudCapability capability) {
        return getDefaultDriver().supportsCapability(capability);
    }
    
    private static boolean isVersionCompatible(String driverVersion, String requestedVersion) {
        // Driver version must be >= requested version
        return GudApiVersion.parse(driverVersion)
            .compareTo(GudApiVersion.parse(requestedVersion)) >= 0;
    }
    
    private static void loadDriversIfNeeded() {
        if (drivers.isEmpty()) {
            loadDrivers();
        }
    }
}
```

### 5. Default Methods in GUD API Interfaces

GUD API interfaces use default implementations for version-specific features:

```java
// Example: GudPoolFactory.java
public interface GudPoolFactory {
    
    // Existing methods (all versions)
    GudPoolFactory setMinConnections(int minConnections);
    GudPoolFactory setMaxConnections(int maxConnections);
    // ... etc
    
    // Method added in GemFire 10.1
    /**
     * Sets the minimum connections per server.
     * 
     * @param minConnections the minimum connections per server
     * @return this factory
     * @throws GudUnsupportedOperationException if the driver doesn't support this feature
     * @since GemFire 10.1
     */
    default GudPoolFactory setMinConnectionsPerServer(int minConnections) {
        throw new GudUnsupportedOperationException(
            "setMinConnectionsPerServer() is not supported. This feature was added in GemFire 10.1.",
            "PER_SERVER_CONNECTION_LIMITS", "10.1");
    }
}
```

### 6. GudUnsupportedOperationException

```java
package org.springframework.data.gemfire.gud.api;

/**
 * Exception thrown when an operation is not supported by the current driver.
 */
public class GudUnsupportedOperationException extends GudException {
    
    private final GudCapability requiredCapability;
    private final String minimumVersion;
    
    public GudUnsupportedOperationException(String message, GudCapability capability, String minVersion) {
        super(message);
        this.requiredCapability = capability;
        this.minimumVersion = minVersion;
    }
    
    public GudCapability getRequiredCapability() {
        return requiredCapability;
    }
    
    public String getMinimumVersion() {
        return minimumVersion;
    }
}
```

### 7. Driver Implementations

Each GemFire version has a corresponding driver that supports appropriate capabilities:

**10.0 Driver** - Uses default methods for 10.1+ features:
```java
// GemFirePoolFactory (10.0) does NOT override setMinConnectionsPerServer()
// The default method throws GudUnsupportedOperationException
```

**10.1, 10.2, 10.3 Drivers** - Override with native implementations:
```java
package org.springframework.data.gemfire.gud.driver.gemfire;

public class GemFirePoolFactory implements GudPoolFactory {
    
    private final PoolFactory nativeFactory;
    
    @Override
    public GudPoolFactory setMinConnectionsPerServer(int minConnections) {
        nativeFactory.setMinConnectionsPerServer(minConnections);
        return this;
    }
    
    @Override
    public GudPoolFactory setMaxConnectionsPerServer(int maxConnections) {
        nativeFactory.setMaxConnectionsPerServer(maxConnections);
        return this;
    }
}
```

### 8. Supported Capabilities by Version

| Version | Capabilities |
|---------|--------------|
| 10.0 | BASIC_CACHE_OPERATIONS, REGIONS, QUERIES, CONTINUOUS_QUERY, TRANSACTIONS, PDX_SERIALIZATION, FUNCTIONS |
| 10.1 | All 10.0 + PER_SERVER_CONNECTION_LIMITS, DISK_STORE_SEGMENTS |
| 10.2 | All 10.1 capabilities |
| 10.3 | All 10.2 + SECURITY_MANAGER |

---

## Implementation Prompt

Use the following prompt to implement the proposed changes:

---

### Prompt for AI Assistant

```
I need to enhance the GUD (GemFire Unified Driver) SPI to support API evolution across GemFire versions. The goal is to allow spring-data-vmware-gemfire to run on both 10.3 and future 10.4 drivers without code changes.

## Current State
- gud-api module contains 182+ interfaces (GudRegion, GudCache, etc.)
- gud-core module contains GudDriver interface and GudDriverManager
- gud-driver-gemfire-10.3 implements GudDriver for GemFire 10.3

## Tasks

### Task 1: Create GudCapability enum in gud-core
Create `/gud-core/src/main/java/org/springframework/data/gemfire/gud/core/GudCapability.java`:
- Define capabilities for 10.3: BASIC_CACHE_OPERATIONS, REGIONS, QUERIES, CONTINUOUS_QUERY, TRANSACTIONS, PDX_SERIALIZATION, FUNCTIONS, SECURITY_MANAGER
- Define placeholder capabilities for 10.4: PARTITION_STATISTICS, ENHANCED_SECURITY, NEW_INDEX_TYPES
- Include description and minimumVersion fields
- Add static forVersion(String) method

### Task 2: Create GudApiVersion class in gud-core
Create `/gud-core/src/main/java/org/springframework/data/gemfire/gud/core/GudApiVersion.java`:
- Implement Comparable<GudApiVersion>
- Define V1_0 (current) and V1_1 (for 10.4)
- Add parse(String), isCompatibleWith(GudApiVersion), and compareTo methods

### Task 3: Enhance GudDriver interface
Modify `/gud-core/src/main/java/org/springframework/data/gemfire/gud/core/GudDriver.java`:
- Add: getMinimumApiVersion(), getMaximumApiVersion()
- Add: supportsCapability(GudCapability), getCapabilities()
- Add: translateException(Throwable)
- Add convenience default methods: supportsPartitionStatistics(), supportsEnhancedSecurity()

### Task 4: Enhance GudDriverManager
Modify `/gud-core/src/main/java/org/springframework/data/gemfire/gud/core/GudDriverManager.java`:
- Add: getDriverForGemFireVersion(String)
- Add: getDriversWithCapability(GudCapability)
- Add: isCapabilityAvailable(GudCapability)

### Task 5: Create GudUnsupportedOperationException
Create `/gud-api/src/main/java/org/springframework/data/gemfire/gud/api/GudUnsupportedOperationException.java`:
- Extend GudException
- Include requiredCapability and minimumVersion fields

### Task 6: Update GemFireDriver (10.3)
Modify `/gud-driver-gemfire-10.3/src/main/java/.../GemFireDriver.java`:
- Implement new GudDriver methods
- Define SUPPORTED_CAPABILITIES set
- Implement translateException() with mappings for common GemFire exceptions

### Task 7: Add default methods to key GUD API interfaces
For each interface that may have new methods in 10.4, add default implementations that throw GudUnsupportedOperationException. Priority interfaces:
- GudRegion
- GudCache  
- GudClientCache
- GudQueryService

### Task 8: Update build.gradle.kts
Ensure gud-core depends on gud-api (for GudUnsupportedOperationException)

## Guidelines
- Follow existing code style and AI-Generated comment headers
- All new files need the Broadcom license header
- Use Java 8 compatible code (no var, no records)
- Run ./gradlew compileJava after changes to verify compilation

## Verification
After implementation:
1. ./gradlew :gud-api:compileJava should succeed
2. ./gradlew :gud-core:compileJava should succeed  
3. ./gradlew :gud-driver-gemfire-10.3:compileJava should succeed
4. ./gradlew :spring-data-vmware-gemfire:compileJava should succeed
```

---

## Migration Guide

### For Driver Implementers (Creating a New Version Driver)

1. **Create new module**: `gud-driver-gemfire-X.Y`

2. **Copy from nearest version** and update class names/packages

3. **Override default methods** for features supported in the new version:
   ```java
   // GemFireXYPoolFactory.java
   @Override
   public GudPoolFactory setMinConnectionsPerServer(int minConnections) {
       nativeFactory.setMinConnectionsPerServer(minConnections);
       return this;
   }
   ```

4. **Register via ServiceLoader**: Create `META-INF/services/org.springframework.data.gemfire.gud.core.GudDriver`

### For spring-data-vmware-gemfire Maintainers

1. **Wrap version-specific calls in try-catch**:
   ```java
   if (this.minConnectionsPerServer != null) {
       try {
           poolFactory.setMinConnectionsPerServer(this.minConnectionsPerServer);
       } catch (GudUnsupportedOperationException ex) {
           getLogger().warn("Pool 'minConnectionsPerServer' setting ({}) is not supported: {}",
               this.minConnectionsPerServer, ex.getMessage());
       }
   }
   ```

2. **Check capabilities for optional features**:
   ```java
   GudDriver driver = GudDriverManager.getDefaultDriver();
   if (driver.supportsCapability(GudCapability.PER_SERVER_CONNECTION_LIMITS)) {
       // Feature is available
   }
   ```

3. **Use translated exceptions**:
   ```java
   try {
       region.put(key, value);
   } catch (GudException e) {
       // Handle GUD exception - no native types exposed
   }
   ```

### For Users (Switching Drivers)

1. **Change driver dependency**:
   ```groovy
   // Use 10.1 driver instead of 10.0
   runtimeOnly project(':gud-driver-gemfire-10.1')
   ```

2. **Update GemFire dependency** to match:
   ```groovy
   implementation 'com.vmware.gemfire:geode-core:10.1.x'
   ```

No code changes required in application code - driver is automatically discovered via ServiceLoader. Features not available in older drivers will log warnings but applications continue to work.

---

## Appendix: Key Files

| File | Description |
|------|-------------|
| `gud-core/.../GudCapability.java` | Capability enum (10.0, 10.1, 10.3 features) |
| `gud-core/.../GudApiVersion.java` | Version class |
| `gud-core/.../GudDriver.java` | Driver SPI interface |
| `gud-core/.../GudDriverManager.java` | Driver discovery and selection |
| `gud-api/.../GudUnsupportedOperationException.java` | Exception for unsupported features |
| `gud-api/.../GudPoolFactory.java` | Default methods for 10.1+ features |
| `gud-api/.../GudDiskStoreFactory.java` | Default methods for 10.1+ features |
| `gud-api/.../GudPool.java` | Default methods for 10.1+ features |
| `gud-driver-gemfire-10.0/...` | 10.0 driver (uses default methods) |
| `gud-driver-gemfire-10.1/...` | 10.1 driver (overrides for 10.1 features) |
| `gud-driver-gemfire-10.2/...` | 10.2 driver |
| `gud-driver-gemfire-10.3/...` | 10.3 driver |

---

## References

- [GUD Migration Status](./GUD_MIGRATION_STATUS.md)
- [Java ServiceLoader Documentation](https://docs.oracle.com/javase/8/docs/api/java/util/ServiceLoader.html)
- [Semantic Versioning](https://semver.org/)
