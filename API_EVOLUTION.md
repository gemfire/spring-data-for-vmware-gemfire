# GUD API Evolution Strategy

**Document Version:** 1.0  
**Created:** 2026-03-13  
**Status:** Proposal

## Executive Summary

This document analyzes the GemFire Unified Driver (GUD) architecture's ability to handle API evolution across GemFire versions (e.g., 10.3 → 10.4) while maintaining backward compatibility. It identifies gaps in the current SPI design and proposes enhancements to ensure `spring-data-vmware-gemfire` can run seamlessly on multiple driver versions.

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
│  GemFire103Region, GemFire103Cache, GemFire103Driver               │
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

When GemFire 10.4 is released with API changes, the following scenarios must be handled gracefully:

### Scenario 1: New Method Added to Native API

```java
// GemFire 10.4 adds new method to Region
public interface Region<K, V> {
    // Existing methods...
    PartitionStatistics getPartitionStatistics();  // NEW in 10.4
}
```

**Current Behavior:** If `GudRegion` adds this method, the 10.3 driver cannot implement it, causing `AbstractMethodError` at runtime.

### Scenario 2: Method Signature Change

```java
// GemFire 10.3
QueryService.createIndex(String name, String expression, String fromClause);

// GemFire 10.4 (hypothetical)
QueryService.createIndex(String name, IndexType type, String expression, String fromClause);
```

**Current Behavior:** No mechanism to detect which signature the driver supports.

### Scenario 3: New Exception Types

```java
// GemFire 10.4 introduces new exception
public class PartitionOfflineException extends CacheException { }
```

**Current Behavior:** No exception translation layer; `spring-data-vmware-gemfire` would need to catch native exceptions.

### Scenario 4: Deprecated/Removed Methods

```java
// GemFire 10.4 removes deprecated method
// Region.writeToDisk() - removed
```

**Current Behavior:** 10.4 driver would fail to compile if GUD API requires this method.

---

## Gap Analysis

| Aspect | Current State | Gap | Risk Level |
|--------|---------------|-----|------------|
| Basic wrapping | ✅ Working | None | Low |
| Driver discovery | ✅ Working | No version preference | Medium |
| API evolution (new methods) | ❌ Not handled | No capability detection | **High** |
| API evolution (signature changes) | ❌ Not handled | No version negotiation | **High** |
| Exception translation | ❌ Missing | Need wrapper factory | Medium |
| Factory abstraction | ⚠️ Partial | Missing createXxxFactory() | Medium |
| Default implementations | ❌ Missing | Need default methods | **High** |
| Version comparison | ❌ Missing | Need GudApiVersion | Medium |

### Detailed Gap Descriptions

#### Gap 1: No Capability Detection

The SPI provides no way to query what features a driver supports:

```java
// Cannot currently do this:
if (driver.supportsCapability(GudCapability.PARTITION_STATISTICS)) {
    region.getPartitionStatistics();
}
```

#### Gap 2: No Default Method Pattern

GUD API interfaces lack default implementations:

```java
// Current (problematic)
public interface GudRegion<K, V> {
    GudPartitionStatistics getPartitionStatistics();  // 10.3 driver can't implement
}

// Needed
public interface GudRegion<K, V> {
    default GudPartitionStatistics getPartitionStatistics() {
        throw new UnsupportedOperationException(
            "getPartitionStatistics() requires GemFire 10.4+ driver");
    }
}
```

#### Gap 3: No Version-Aware Driver Selection

```java
// Current: picks first available driver
if (defaultDriver == null) {
    defaultDriver = driver;
}

// Needed: version-aware selection
public static GudDriver getDriverForVersion(String minVersion) {
    return drivers.values().stream()
        .filter(d -> isVersionCompatible(d.getSupportedVersion(), minVersion))
        .max(Comparator.comparing(GudDriver::getSupportedVersion))
        .orElseThrow();
}
```

#### Gap 4: No Exception Translation

```java
// Currently, native exceptions leak through
try {
    region.get(key);
} catch (org.apache.geode.cache.CacheClosedException e) {  // Native type!
    // Problem: spring-data shouldn't reference native types
}
```

#### Gap 5: AspectJ Pointcuts Reference Native Types

```java
// JSONRegionAdvice.java - references native types in pointcuts
@Around("execution(* org.apache.geode.cache.Region.get(..))")
public Object get(ProceedingJoinPoint pjp) { ... }
```

While this works (AspectJ pointcuts are string literals), it's inconsistent with the "no native references" rule.

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
    
    // 10.3+ capabilities
    SECURITY_MANAGER("Integrated security manager", "10.3"),
    
    // 10.4+ capabilities (hypothetical)
    PARTITION_STATISTICS("Partition statistics API", "10.4"),
    ENHANCED_SECURITY("Enhanced security features", "10.4"),
    NEW_INDEX_TYPES("New index type support", "10.4"),
    ASYNC_EVENT_QUEUE_V2("Async event queue v2", "10.4");
    
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
    
    public static final GudApiVersion V1_0 = new GudApiVersion(1, 0, 0);
    public static final GudApiVersion V1_1 = new GudApiVersion(1, 1, 0);  // 10.4 support
    
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

Add default implementations to all GUD API interfaces for methods that may not exist in older versions:

```java
// Example: GudRegion.java
public interface GudRegion<K, V> extends ConcurrentMap<K, V> {
    
    // Existing methods (10.3)
    String getName();
    String getFullPath();
    V get(Object key);
    V put(K key, V value);
    // ... etc
    
    // New method added for 10.4 support
    /**
     * Returns partition statistics for this region.
     * 
     * @return partition statistics
     * @throws UnsupportedOperationException if the driver doesn't support this feature
     * @since GUD API 1.1 (GemFire 10.4+)
     */
    default GudPartitionStatistics getPartitionStatistics() {
        throw new UnsupportedOperationException(
            "getPartitionStatistics() requires a GemFire 10.4+ driver. " +
            "Check driver.supportsCapability(GudCapability.PARTITION_STATISTICS) before calling.");
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

### 7. Updated 10.3 Driver Implementation

```java
package org.springframework.data.gemfire.gud.driver.gemfire103;

public class GemFire103Driver implements GudDriver {
    
    public static final String DRIVER_NAME = "gemfire-10.3";
    public static final String SUPPORTED_VERSION = "10.3";
    
    private static final Set<GudCapability> SUPPORTED_CAPABILITIES = EnumSet.of(
        GudCapability.BASIC_CACHE_OPERATIONS,
        GudCapability.REGIONS,
        GudCapability.QUERIES,
        GudCapability.CONTINUOUS_QUERY,
        GudCapability.TRANSACTIONS,
        GudCapability.PDX_SERIALIZATION,
        GudCapability.FUNCTIONS,
        GudCapability.SECURITY_MANAGER
    );
    
    @Override
    public String getName() {
        return DRIVER_NAME;
    }
    
    @Override
    public String getSupportedVersion() {
        return SUPPORTED_VERSION;
    }
    
    @Override
    public GudApiVersion getMinimumApiVersion() {
        return GudApiVersion.V1_0;
    }
    
    @Override
    public GudApiVersion getMaximumApiVersion() {
        return GudApiVersion.V1_0;
    }
    
    @Override
    public boolean supportsCapability(GudCapability capability) {
        return SUPPORTED_CAPABILITIES.contains(capability);
    }
    
    @Override
    public Set<GudCapability> getCapabilities() {
        return Collections.unmodifiableSet(SUPPORTED_CAPABILITIES);
    }
    
    @Override
    public GudException translateException(Throwable nativeException) {
        if (nativeException instanceof CacheClosedException) {
            return new GudCacheClosedException(nativeException.getMessage(), nativeException);
        }
        if (nativeException instanceof RegionExistsException) {
            return new GudRegionExistsException(nativeException.getMessage(), nativeException);
        }
        // ... more exception mappings
        return new GudException(nativeException.getMessage(), nativeException);
    }
    
    // ... existing wrap methods
}
```

### 8. Future 10.4 Driver Skeleton

```java
package org.springframework.data.gemfire.gud.driver.gemfire104;

public class GemFire104Driver implements GudDriver {
    
    public static final String DRIVER_NAME = "gemfire-10.4";
    public static final String SUPPORTED_VERSION = "10.4";
    
    private static final Set<GudCapability> SUPPORTED_CAPABILITIES = EnumSet.of(
        // All 10.3 capabilities
        GudCapability.BASIC_CACHE_OPERATIONS,
        GudCapability.REGIONS,
        GudCapability.QUERIES,
        GudCapability.CONTINUOUS_QUERY,
        GudCapability.TRANSACTIONS,
        GudCapability.PDX_SERIALIZATION,
        GudCapability.FUNCTIONS,
        GudCapability.SECURITY_MANAGER,
        // New 10.4 capabilities
        GudCapability.PARTITION_STATISTICS,
        GudCapability.ENHANCED_SECURITY,
        GudCapability.NEW_INDEX_TYPES
    );
    
    @Override
    public GudApiVersion getMinimumApiVersion() {
        return GudApiVersion.V1_0;  // Can still work with older API
    }
    
    @Override
    public GudApiVersion getMaximumApiVersion() {
        return GudApiVersion.V1_1;  // Supports new API features
    }
    
    // ... implementations for new 10.4 features
}
```

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

### Task 6: Update GemFire103Driver
Modify `/gud-driver-gemfire-10.3/src/main/java/.../GemFire103Driver.java`:
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

### For Driver Implementers (Creating a 10.4 Driver)

1. **Create new module**: `gud-driver-gemfire-10.4`

2. **Implement GudDriver** with all capabilities:
   ```java
   @Override
   public Set<GudCapability> getCapabilities() {
       return EnumSet.allOf(GudCapability.class);  // 10.4 supports everything
   }
   ```

3. **Implement new methods** in wrapper classes:
   ```java
   // GemFire104Region.java
   @Override
   public GudPartitionStatistics getPartitionStatistics() {
       return new GemFire104PartitionStatistics(nativeRegion.getPartitionStatistics());
   }
   ```

4. **Register via ServiceLoader**: Create `META-INF/services/org.springframework.data.gemfire.gud.core.GudDriver`

### For spring-data-vmware-gemfire Maintainers

1. **Check capabilities before using new features**:
   ```java
   GudDriver driver = GudDriverManager.getDefaultDriver();
   if (driver.supportsCapability(GudCapability.PARTITION_STATISTICS)) {
       GudPartitionStatistics stats = region.getPartitionStatistics();
       // Use stats
   }
   ```

2. **Handle UnsupportedOperationException gracefully**:
   ```java
   try {
       region.getPartitionStatistics();
   } catch (GudUnsupportedOperationException e) {
       logger.debug("Partition statistics not available: {}", e.getMessage());
       // Fallback behavior
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

1. **Remove old driver dependency**:
   ```groovy
   // Remove
   implementation 'com.vmware.gemfire:gud-driver-gemfire-10.3:x.y.z'
   ```

2. **Add new driver dependency**:
   ```groovy
   // Add
   implementation 'com.vmware.gemfire:gud-driver-gemfire-10.4:x.y.z'
   ```

3. **Update GemFire dependency**:
   ```groovy
   implementation 'com.vmware.gemfire:geode-core:10.4.0'
   ```

No code changes required in application code - driver is automatically discovered via ServiceLoader.

---

## Appendix: Files to Modify

| File | Change Type | Description |
|------|-------------|-------------|
| `gud-core/.../GudCapability.java` | NEW | Capability enum |
| `gud-core/.../GudApiVersion.java` | NEW | Version class |
| `gud-core/.../GudDriver.java` | MODIFY | Add new methods |
| `gud-core/.../GudDriverManager.java` | MODIFY | Add version-aware selection |
| `gud-api/.../GudUnsupportedOperationException.java` | NEW | Exception class |
| `gud-api/.../GudRegion.java` | MODIFY | Add default methods |
| `gud-api/.../GudCache.java` | MODIFY | Add default methods |
| `gud-api/.../GudClientCache.java` | MODIFY | Add default methods |
| `gud-api/.../GudQueryService.java` | MODIFY | Add default methods |
| `gud-driver-gemfire-10.3/.../GemFire103Driver.java` | MODIFY | Implement new interface |

---

## References

- [GUD Migration Status](./GUD_MIGRATION_STATUS.md)
- [Java ServiceLoader Documentation](https://docs.oracle.com/javase/8/docs/api/java/util/ServiceLoader.html)
- [Semantic Versioning](https://semver.org/)
