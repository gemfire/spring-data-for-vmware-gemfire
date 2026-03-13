# GUD API Migration Prompt - Complete Guide

## Overview

This prompt guides the migration of Spring Data GemFire from direct Apache Geode/VMware GemFire dependencies to the GemFire Unified Driver (GUD) API. The GUD API provides a JDBC-like abstraction layer that decouples application code from specific GemFire versions.

**Important**: This prompt assumes a clean branch of the original codebase with NO prior migration work.

## Target Architecture

```
spring-data-for-vmware-gemfire/
├── gud-api/                      # GUD API interfaces (TO CREATE)
├── gud-core/                     # Driver management infrastructure (TO CREATE)
├── gud-driver-gemfire-10.3/      # GemFire 10.3.x driver (TO CREATE)
├── spring-data-vmware-gemfire/   # Main Spring Data module (TO MIGRATE)
│   ├── src/main/java             # Migrate to 0 GemFire imports
│   └── src/test/java             # Migrate tests to use GUD API mocks
└── spring-test-vmware-gemfire/   # Test framework (TO MIGRATE)
```

**Note**: Exclude GemFire 10.2 driver (`gud-driver-gemfire-10.2/`) from this migration phase.

---

## Migration Phases

### Phase 1: Infrastructure Setup

Create the GUD module infrastructure before any migration begins.

#### 1.1 Create gud-api Module

Create `gud-api/build.gradle.kts`:
```kotlin
plugins {
    id("java-library")
    id("broadcom.java-conventions")
}

description = "GemFire Unified Driver API"

dependencies {
    // No GemFire dependencies - this is the abstraction layer
    compileOnly("org.springframework:spring-context")
}
```

#### 1.2 Create gud-core Module

Create `gud-core/build.gradle.kts`:
```kotlin
plugins {
    id("java-library")
    id("broadcom.java-conventions")
}

description = "GemFire Unified Driver Core"

dependencies {
    api(project(":gud-api"))
    implementation("org.springframework:spring-context")
    implementation("org.slf4j:slf4j-api")
}
```

#### 1.3 Create gud-driver-gemfire-10.3 Module

Create `gud-driver-gemfire-10.3/build.gradle.kts`:
```kotlin
plugins {
    id("java-library")
    id("broadcom.java-conventions")
}

description = "GemFire Unified Driver for GemFire 10.3.x"

dependencies {
    api(project(":gud-api"))
    implementation(project(":gud-core"))
    
    // GemFire 10.3 dependencies
    implementation("com.vmware.gemfire:gemfire-core:10.3.+")
    implementation("com.vmware.gemfire:gemfire-cq:10.3.+")
}
```

#### 1.4 Update Root settings.gradle.kts

Add to `settings.gradle.kts`:
```kotlin
include("gud-api")
include("gud-core")
include("gud-driver-gemfire-10.3")
```

#### 1.5 Update spring-data-vmware-gemfire Dependencies

Modify `spring-data-vmware-gemfire/build.gradle.kts` to depend on gud-api:
```kotlin
dependencies {
    api(project(":gud-api"))
    // Remove or make optional direct GemFire dependencies
}
```


---

### Phase 2: GUD API Design

Define the GUD API interfaces based on analysis of Spring Data GemFire's usage of native GemFire types.

#### 2.1 Analyze Native Type Usage

For each native GemFire type used in `spring-data-vmware-gemfire/src/main/java`:
1. Identify all methods/properties accessed
2. Document in the migration plan
3. Design corresponding GUD interface

#### 2.2 Core Type Mappings

| Native GemFire Type | GUD API Type | Package |
|---------------------|--------------|---------|
| Region | GudRegion | org.springframework.data.gemfire.gud.api |
| Cache / ClientCache | GudCache | org.springframework.data.gemfire.gud.api |
| QueryService | GudQueryService | org.springframework.data.gemfire.gud.api |
| Query | GudQuery | org.springframework.data.gemfire.gud.api |
| SelectResults | GudSelectResults | org.springframework.data.gemfire.gud.api |
| Pool | GudPool | org.springframework.data.gemfire.gud.api |
| PoolFactory | GudPoolFactory | org.springframework.data.gemfire.gud.api |
| CqEvent | GudCqEvent | org.springframework.data.gemfire.gud.api |
| CqListener | GudCqListener | org.springframework.data.gemfire.gud.api |
| CqQuery | GudContinuousQuery | org.springframework.data.gemfire.gud.api |
| TransactionEvent | GudTransactionEvent | org.springframework.data.gemfire.gud.api |
| TransactionWriter | GudTransactionWriter | org.springframework.data.gemfire.gud.api |
| TransactionListener | GudTransactionListener | org.springframework.data.gemfire.gud.api |
| PdxSerializer | GudPdxSerializer | org.springframework.data.gemfire.gud.api |
| PdxReader | GudPdxReader | org.springframework.data.gemfire.gud.api |
| PdxWriter | GudPdxWriter | org.springframework.data.gemfire.gud.api |
| DataPolicy | RegionDataPolicy | org.springframework.data.gemfire.gud.api |
| RegionShortcut | RegionShortcutWrapper | org.springframework.data.gemfire.gud.api |
| ClientRegionShortcut | ClientRegionShortcutWrapper | org.springframework.data.gemfire.gud.api |
| RegionAttributes | GudRegionAttributes | org.springframework.data.gemfire.gud.api |
| AttributesMutator | GudAttributesMutator | org.springframework.data.gemfire.gud.api |
| CacheLoader | GudCacheLoader | org.springframework.data.gemfire.gud.api |
| CacheWriter | GudCacheWriter | org.springframework.data.gemfire.gud.api |
| CacheListener | GudCacheListener | org.springframework.data.gemfire.gud.api |
| Function | GudFunction | org.springframework.data.gemfire.gud.api |
| FunctionContext | GudFunctionContext | org.springframework.data.gemfire.gud.api |
| FunctionService | GudFunctionService | org.springframework.data.gemfire.gud.api |
| DiskStore | GudDiskStore | org.springframework.data.gemfire.gud.api |
| DiskStoreFactory | GudDiskStoreFactory | org.springframework.data.gemfire.gud.api |
| Index | GudIndex | org.springframework.data.gemfire.gud.api |
| Scope | ScopeType | org.springframework.data.gemfire.gud.api |
| ExpirationAttributes | ExpirationConfig | org.springframework.data.gemfire.gud.api |
| ExpirationAction | ExpirationAction | org.springframework.data.gemfire.gud.api |
| EvictionAttributes | EvictionConfig | org.springframework.data.gemfire.gud.api |

#### 2.3 Create GUD API Interfaces

For each mapping, create the interface in `gud-api/src/main/java/org/springframework/data/gemfire/gud/api/`.

**Critical Rule**: Create 1:1 mappings of native GemFire interfaces. GUD API interfaces should mirror the complete native GemFire interface to ensure full compatibility and prevent future migration issues.

---

### Phase 3: Planning and Analysis

Before modifying any production code, analyze all files that need migration.

#### 3.1 Source Code Analysis

For each Java file in `spring-data-vmware-gemfire/src/main/java`:

1. **Identify native GemFire imports**
2. **Check if GUD API equivalents exist**
3. **Document any missing types or methods**
4. **Do not use reflection to access native GemFire types**

Output format for each file:
```
### File: [path/to/File.java]

**Native Imports:**
- org.apache.geode.cache.Region -> GudRegion (EXISTS/MISSING)
- org.apache.geode.cache.Cache -> GudCache (EXISTS/MISSING)

**Methods/Constructs Required:**
- Region.put(K, V) -> GudRegion.put(K, V) - EXISTS/MISSING
- Region.getAttributes() -> GudRegion.getAttributes() - EXISTS/MISSING

**Migration Complexity:** LOW/MEDIUM/HIGH

**Blockers:** [List any missing GUD API types or methods]
```

#### 3.2 Test Code Analysis

For each test file in `spring-data-vmware-gemfire/src/test/java`:

Same analysis as above, noting that tests will mock GUD API types instead of native types.

---

### Phase 4: Deviation Documentation

**CRITICAL**: If migration requires constructs that don't exist in the planned GUD API:

1. **DO NOT create new GUD API types or methods without approval**
2. **DO NOT implement workarounds that deviate from the type mappings**
3. **Document the deviation** in `GUD_MIGRATION_DEVIATIONS.md`

#### Deviation Document Format

Create `GUD_MIGRATION_DEVIATIONS.md`:

```markdown
# GUD API Migration Deviations

This document tracks required GUD API additions or changes discovered during migration.
Each deviation must be reviewed and approved before implementation.

## Deviation Template

### DEV-001: [Short Description]

**Source File:** [path/to/File.java]

**Issue:** [Describe what's missing or why the current GUD API is insufficient]

**Native GemFire Usage:**
```java
// Code snippet showing how native GemFire is used
```

**Suggested GUD API Addition:**
```java
// Proposed interface method or new type
```

**Justification:** [Why this is needed, cannot be worked around]

**Status:** PENDING REVIEW | APPROVED | REJECTED | IMPLEMENTED

**Resolution:** [How it was resolved after review]

---
```

---

### Phase 5: Resolution

After Phase 3 and Phase 4 are complete:

1. **Review `GUD_MIGRATION_DEVIATIONS.md`** with stakeholders
2. **Get approval** for each GUD API change
3. **Implement approved changes** to gud-api module
4. **Update type mappings** if new types were added
5. **Re-verify** that all blockers are resolved

**DO NOT proceed to Phase 6 until all deviations are resolved.**

---

### Phase 6: Driver Implementation

Implement the GemFire 10.3 driver that bridges GUD API to native GemFire.

#### 6.1 Driver Structure

```
gud-driver-gemfire-10.3/src/main/java/
└── org/springframework/data/gemfire/gud/driver/gemfire103/
    ├── GemFire103Driver.java          # Main driver entry point
    ├── GemFire103Cache.java           # GudCache implementation
    ├── GemFire103Region.java          # GudRegion implementation
    ├── GemFire103QueryService.java    # GudQueryService implementation
    ├── GemFire103Pool.java            # GudPool implementation
    └── ... (adapter for each GUD API type)
```

#### 6.2 Adapter Pattern

Each driver class wraps the native GemFire type:

```java
public class GemFire103Region<K, V> implements GudRegion<K, V> {
    
    private final Region<K, V> nativeRegion;
    
    public GemFire103Region(Region<K, V> nativeRegion) {
        this.nativeRegion = nativeRegion;
    }
    
    @Override
    public V get(K key) {
        return nativeRegion.get(key);
    }
    
    // ... implement all GudRegion methods
}
```

---

### Phase 7: Source Code Migration

Only after Phases 1-6 are complete, begin migrating production code.

#### 7.1 Migration Process

For each file in `spring-data-vmware-gemfire/src/main/java`:

1. **Replace native imports** with GUD API imports
2. **Update type declarations** from native to GUD types
3. **Verify compilation**: `./gradlew :spring-data-vmware-gemfire:compileJava`
4. **Commit incrementally** (per package or logical grouping)

#### 7.2 Import Replacement Pattern

```java
// Before
import org.apache.geode.cache.Region;
import org.apache.geode.cache.Cache;

// After
import org.springframework.data.gemfire.gud.api.GudRegion;
import org.springframework.data.gemfire.gud.api.GudCache;
```

---

### Phase 8: Test Migration

After production code is migrated, migrate test code.

#### 8.1 Test Migration Strategy

1. **Replace native imports** with GUD API imports
2. **Update mock declarations** to use GUD types
3. **Use Mockito** to mock GUD interfaces (they're interfaces, easy to mock)
4. **Remove from exclusion list** in build.gradle.kts if tests were excluded
5. **Run tests**: `./gradlew :spring-data-vmware-gemfire:test`

#### 8.2 Mock Pattern

```java
// Before
@Mock
private Region<Object, Object> mockRegion;

// After
@Mock
private GudRegion<Object, Object> mockRegion;
```

---

## Critical Rules

### ABSOLUTE RULES (NON-NEGOTIABLE):
- **spring-data-vmware-gemfire and spring-test-vmware-gemfire projects are NOT allowed to reference native GemFire libraries at all** - not even as compileOnly dependencies. All GemFire types must come through the GUD API.
- **No deleting of any existing files** - only modify, refactor, or add new files.
- **Create 1:1 mappings of native GemFire interfaces** - GUD API interfaces should mirror the native GemFire interfaces completely.

### DO NOT:
- Reference `org.apache.geode.*` packages from spring-data-vmware-gemfire or spring-test-vmware-gemfire
- Delete any existing source files
- Skip the planning phase and go directly to implementation
- Modify multiple files at once without compilation verification
- Assume a GUD type exists without checking the gud-api module
- Include GemFire 10.2 in this migration phase
- Implement workarounds that bypass the GUD abstraction

### ALWAYS:
- Follow the phased approach in order
- Document deviations before attempting workarounds
- Compile after each file migration to catch errors early
- Preserve existing behavior - migration should be transparent
- Reference native GemFire types only in the driver module (gud-driver-gemfire-10.3)
- Keep gud-api module free of any GemFire dependencies
- Update `GUD_MIGRATION_STATUS.md` as progress is made
- Use reflection when needing to check for internal GemFire types (e.g., LocalRegion instanceof checks)

---

## Code Comment Standards

When creating or modifying files, add the AI-Generated comment block after the license header:

```java
/*
 * Copyright 2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

/*
 * @AI-Generated
 * Generated in whole or in part by Cursor
 * Description:
 * YYYY-MM-DD: [Short description of changes]
 */

package org.springframework.data.gemfire...
```

---

## Progress Tracking

Create and maintain `GUD_MIGRATION_STATUS.md`:

```markdown
# GUD Migration Status

## Phase Status
- [ ] Phase 1: Infrastructure Setup
- [ ] Phase 2: GUD API Design
- [ ] Phase 3: Planning and Analysis
- [ ] Phase 4: Deviation Documentation
- [ ] Phase 5: Resolution
- [ ] Phase 6: Driver Implementation
- [ ] Phase 7: Source Code Migration
- [ ] Phase 8: Test Migration

## Module Status
| Module | Status | GemFire Imports | Notes |
|--------|--------|-----------------|-------|
| gud-api | NOT STARTED | 0 (target) | |
| gud-core | NOT STARTED | 0 (target) | |
| gud-driver-gemfire-10.3 | NOT STARTED | Many (expected) | |
| spring-data-vmware-gemfire/main | NOT STARTED | [count] -> 0 | |
| spring-data-vmware-gemfire/test | NOT STARTED | [count] -> 0 | |
| spring-test-vmware-gemfire | NOT STARTED | [count] -> 0 | |

## Files Migrated
[Track individual file progress here]
```

---

## Current Migration Progress

### Completed Work

#### Phase 1: Infrastructure Setup - COMPLETE
- Created `gud-api` module with build.gradle.kts
- Created `gud-core` module with build.gradle.kts  
- Created `gud-driver-gemfire-10.3` module with build.gradle.kts
- Updated `settings.gradle.kts` to include new modules
- Updated `spring-data-vmware-gemfire/build.gradle.kts` to depend on gud-api

#### Phase 2: GUD API Design - COMPLETE
- Created 84+ GUD API interfaces, enums, and exception classes in `gud-api/src/main/java/org/springframework/data/gemfire/gud/api/`
- Core types: `GudRegion`, `GudCache`, `GudQueryService`, `GudPool`, `GudPdxSerializer`, `GudFunction`, etc.
- Callback interfaces: `GudCacheLoader`, `GudCqListener`, `GudAuthInitialize`, etc.
- Configuration enums: `GudDataPolicy`, `GudScope`, `GudClientRegionShortcut`, etc.
- Exception hierarchy: `GudException` and subclasses

#### Phase 3 & 4: Analysis and Deviation Documentation - IN PROGRESS

**Resolved Deviations:**

1. **DEV-002: Management/CLI APIs** - RESOLVED
   - `FunctionGemfireAdminTemplate.java` - Refactored to use reflection for `RegionInformation.getName()`
   - `GemfireDataSourcePostProcessor.java` - Refactored to use reflection and string-based function ID
   - No more imports of internal management APIs

2. **DEV-003: Security APIs** - RESOLVED
   - Created `GudAuthInitialize`, `GudAuthenticationFailedException`, `GudSecurityException`
   - Created `GudSecurableCommunicationChannel`, `GudLogWriter`

3. **DEV-005: Configuration Properties** - RESOLVED
   - `GemFireProperties` enum already provides abstraction
   - No additional work needed

4. **DEV-001: Internal Cache APIs** - PARTIAL
   - Removed unused `GemFireCacheImpl` import from `CacheUtils.java`
   - Added `isLocal()` to `GudRegion` interface
   - Refactored `RegionUtils.isLocal()` to use reflection instead of `LocalRegion` instanceof
   - **Remaining:** `EnumSerializer.java` (InternalDataSerializer), JNDI support (JNDIInvoker, ConfigProperty)

**Remaining Deviations:**
- DEV-001: Internal Cache APIs (partial - JNDI and serialization)
- DEV-004: DataSerializable/Instantiator (pending review)
- DEV-006: Locator API (pending review)

### Remaining Native GemFire Imports

Files still importing `org.apache.geode.internal.*`:
- `serialization/EnumSerializer.java` - `InternalDataSerializer`
- `config/xml/ClientCacheParser.java` - `ConfigProperty`
- `client/ClientCacheFactoryBean.java` - `JNDIInvoker`, `ConfigProperty`

Files still importing `org.apache.geode.*` (public API - need GUD equivalents):
- ~200 files with various imports (Region, Cache, Pool, etc.)

---

## Starting the Migration

**Current State**: Phases 1-2 complete, Phases 3-4 in progress.

**Next Steps**:
1. Complete deviation resolution for remaining internal API usages
2. Continue creating GUD API 1:1 mappings for all native types
3. Begin systematic migration of spring-data-vmware-gemfire source files to use GUD API

To verify current state:
```bash
# Check GUD modules exist
ls -la gud-*/build.gradle.kts

# Count remaining GemFire imports in main source
grep -r "import org.apache.geode" spring-data-vmware-gemfire/src/main/java | wc -l

# Check for internal API imports (should be minimal)
grep -r "import org.apache.geode.internal" spring-data-vmware-gemfire/src/main/java
```
