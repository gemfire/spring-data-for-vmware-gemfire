# GemFire Unified Driver (GUD) Architecture

> **Updated:** 2026-04-17 — Client-only GUD contract documented; peer/server types deprecated (use GemFire Testcontainers).

## Overview

The GemFire Unified Driver (GUD) is an abstraction layer that decouples Spring Data GemFire from native GemFire APIs. This enables:

1. **Driver Swappability** - Switch between GemFire versions (10.1, 10.2, 10.3) without changing application code
2. **API Evolution** - Gracefully handle API additions, deprecations, and signature changes between GemFire versions
3. **Clean Separation** - Application code never directly imports native GemFire classes
4. **Type-Safe Capability Detection** - `GudCapability` enum allows compile-time checked feature detection

### Client-only GUD contract

The **supported GUD application surface is client-side only**: `GudClientCache`, `GudClientCacheFactory`,
`GudClientRegionFactory`, pools, client regions, PDX, queries, and transactions on a client cache.

**Peer (server) cache**, native **locator/server bootstrap**, and **server-side regions** (REPLICATE,
PARTITION, etc.) are **not** part of that contract. Integration tests and fixtures that need a real
cluster should use **[GemFire Testcontainers](https://github.com/gemfire/gemfire-testcontainers)**
(or native `org.apache.geode` server APIs in test code), not `GudCacheFactory` / `GudCache` /
`GudRegionFactory` in application modules.

The types `GudCache`, `GudCacheFactory`, and `GudRegionFactory` remain on the classpath for backward
compatibility and driver SPI (`GudDriver#createCacheFactory`, `wrapCache`) but are **deprecated since 4.0**
with removal planned after call sites migrate.

## Architecture Layers

```
┌─────────────────────────────────────────────────────────────────┐
│                      Application Layer                          │
│  (User Applications)                                            │
│  - Uses Spring annotations (@ClientCacheApplication, @Region)   │
│  - Only imports from spring-data-vmware-gemfire and gud-api     │
└─────────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────────┐
│                 spring-data-vmware-gemfire                      │
│  - Spring FactoryBeans (ClientCacheFactoryBean, etc.)          │
│  - Spring Configurations (@ClientCacheApplication support)      │
│  - Repository infrastructure                                    │
│  - Uses GudCacheProvider to obtain factories                    │
│  - GudVersionAwareInvoker for graceful version degradation      │
│  - Dependencies: gud-api (api), gud-core (implementation)       │
└─────────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────────┐
│                          gud-api                                │
│  - Pure Java interfaces (no Spring, no native GemFire)         │
│  - GudClientCache, GudClientRegionFactory, GudRegion (client), │
│    GudPool, etc. — client-side contract only                   │
│  - GudCapability, GudApiVersion (capability/version contracts)  │
│  - GudCacheProvider (Supplier-based factory; configure() API)  │
│  - GudUnsupportedOperationException (type-safe, enum-based)    │
│  - Defines the client contract between Spring layer and drivers  │
└─────────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────────┐
│                         gud-core                                │
│  - GudDriver (SPI interface)                                    │
│  - GudDriverManager (ServiceLoader discovery + push to provider)│
└─────────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────────┐
│            gud-driver-gemfire-{10.1, 10.2, 10.3}               │
│  - Concrete implementations of GUD interfaces                   │
│  - Wraps native GemFire APIs for that version                  │
│  - Registered via ServiceLoader (single GudDriver service file) │
│  - NO Spring dependencies                                       │
└─────────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────────┐
│                 Native GemFire (org.apache.geode.*)             │
└─────────────────────────────────────────────────────────────────┘
```

## Key Design Principles

### 1. No Native GemFire Imports in Application Code
Applications should only import from:
- `org.springframework.data.gemfire.*` (Spring Data GemFire)
- `org.springframework.data.gemfire.gud.api.*` (GUD API interfaces)

### 2. Driver Layer Has No Spring Dependencies
Each driver module (`gud-driver-gemfire-10.x`) contains zero Spring imports. It only:
- Implements GUD API interfaces
- Wraps native GemFire classes
- Registers a single `GudDriver` implementation via ServiceLoader

### 3. Single ServiceLoader Registration per Driver
Each driver module registers exactly **one** service entry:
```
META-INF/services/org.springframework.data.gemfire.gud.core.GudDriver
```
`GudDriverManager` discovers the driver and pushes all its factories (client cache, cache,
JNDI binding, pool manager) into `GudCacheProvider` via `GudCacheProvider.configure()`.
This ensures both registration paths (driver-manager and direct service-loader) remain
coordinated through a single authoritative source.

### 4. GudCacheProvider as the Bridge
`GudCacheProvider` is the central access point that:
- Receives factory suppliers pushed by `GudDriverManager` (`configure()` method)
- Falls back to direct ServiceLoader discovery if invoked before `GudDriverManager`
- Returns **fresh** factory instances per call (Supplier pattern — correct for stateful builders)
- Maintains references to the current cache instances

### 5. Type-Safe Capability Detection
`GudCapability` (in `gud-api`) enumerates all version-specific features.
`GudUnsupportedOperationException` carries the `GudCapability` enum value, enabling callers
to inspect the required capability without relying on String comparison.

### 6. Graceful Degradation via GudVersionAwareInvoker
`GudVersionAwareInvoker` (in `spring-data-vmware-gemfire`) centralises the
`try/catch GudUnsupportedOperationException` pattern used across FactoryBeans:
```java
GudVersionAwareInvoker.invokeIfSupported(
    () -> poolFactory.setMinConnectionsPerServer(min),
    GudCapability.PER_SERVER_CONNECTION_LIMITS,
    logger,
    "Per-server connection limits (min={}) not supported; skipping.", min
);
```

## Module Dependency Graph

```
spring-data-vmware-gemfire
├── api: gud-api
└── implementation: gud-core

gud-core
└── implementation: gud-api

gud-driver-gemfire-10.x
├── implementation: gud-api
├── implementation: gud-core
└── implementation: gemfire-core (native)
```

## Package Ownership

| Package | Responsibility |
|---------|----------------|
| `o.s.d.g.gud.api` | Interfaces, enums (`GudCapability`, `GudApiVersion`), exceptions, `GudCacheProvider` |
| `o.s.d.g.gud.core` | Driver SPI (`GudDriver`), driver registry (`GudDriverManager`) |
| `o.s.d.g.gud.driver` | Version-specific implementations (one class `GemFireDriver` per module) |
| `o.s.d.g.support` | `GudVersionAwareInvoker` and other Spring-layer utilities |

## What Is Implemented

### gud-api Module
| Interface/Class | Status | Description |
|-----------------|--------|-------------|
| `GudClientCache` | ✅ Complete | Client cache abstraction |
| `GudClientCacheFactory` | ✅ Complete | Factory for creating client caches (versioned defaults) |
| `GudRegion` | ✅ Complete | Region abstraction with full Map-like API |
| `GudPool` | ✅ Complete | Connection pool abstraction (versioned defaults) |
| `GudPoolFactory` | ✅ Complete | Factory for creating pools (versioned defaults) |
| `GudPoolManager` | ✅ Complete | Pool management (find, getAll, close) |
| `GudDiskStore` | ✅ Complete | Disk store abstraction |
| `GudDiskStoreFactory` | ✅ Complete | Factory for disk stores (versioned defaults) |
| `GudClientRegionFactory` | ✅ Complete | Client region factory (versioned defaults) |
| `GudCacheProvider` | ✅ Complete | Supplier-based factory access + `configure()` push entry-point |
| `GudCapability` | ✅ Complete | Type-safe capability enum — **now in gud-api** |
| `GudApiVersion` | ✅ Complete | Semantic version comparison — **now in gud-api** |
| `GudUnsupportedOperationException` | ✅ Complete | Type-safe with `GudCapability` field |

### gud-core Module
| Class | Status | Description |
|-------|--------|-------------|
| `GudDriver` | ✅ Complete | SPI — includes `createJndiBinding()` and `createPoolManager()` |
| `GudDriverManager` | ✅ Complete | ServiceLoader discovery; fixed version comparator; fixed race condition; pushes to `GudCacheProvider` |

### Driver Modules
| Version | Capabilities |
|---------|--------------|
| `gemfire-10.1` | BASIC_CACHE_OPERATIONS, REGIONS, QUERIES, CONTINUOUS_QUERY, TRANSACTIONS, PDX_SERIALIZATION, FUNCTIONS, PER_SERVER_CONNECTION_LIMITS, DISK_STORE_SEGMENTS |
| `gemfire-10.2` | All 10.1 capabilities |
| `gemfire-10.3` | All 10.2 + SECURITY_MANAGER, SERVER_REGION_NAME |

### spring-data-vmware-gemfire Module
| Component | Status | Description |
|-----------|--------|-------------|
| `ClientCacheFactoryBean` | ✅ Migrated | Uses `GudCacheProvider.createClientCacheFactory()` |
| `PoolFactoryBean` | ✅ Migrated | Uses `GudVersionAwareInvoker` for per-server limits |
| `DiskStoreFactoryBean` | ✅ Migrated | Uses `GudVersionAwareInvoker` for segments |
| `GudVersionAwareInvoker` | ✅ New | Central utility for graceful version degradation |

### ServiceLoader Registrations (META-INF/services/)
Each driver module now registers **one service only**:
| Service Interface | Registered Implementation |
|-------------------|---------------------------|
| `GudDriver` | `GemFireDriver` (per version module) |

All other factories (ClientCacheFactory, CacheFactory, JndiBinding, PoolManager) are
obtained from the driver via its `createXxx()` methods and pushed into `GudCacheProvider`.

## API Evolution Strategy

### Adding New Features
1. Add new method to GUD interface with a `default` that throws `GudUnsupportedOperationException`
2. Add a `GudCapability` enum value for the new feature
3. Implement the method in drivers that support the feature
4. Old drivers continue to work via the default (throw on use)
5. Spring FactoryBeans use `GudVersionAwareInvoker.invokeIfSupported()` for graceful skip

### Handling Deprecated Features
1. Mark deprecated methods with `@Deprecated` in GUD interfaces
2. Include `@deprecated` Javadoc with migration guidance
3. Driver implementations continue to support methods for compatibility

### Currently Deprecated Features
| Feature | Deprecated Since | Reason |
|---------|-----------------|--------|
| `PoolFactory.setThreadLocalConnections()` | 10.0 | No-op since Geode 1.10 |
| `ClientCacheFactory.setPdxDiskStore()` | 10.0 | PDX persistence not supported on client side |
| `ClientCacheFactory.setPdxPersistent()` | 10.0 | PDX persistence not supported on client side |
| `QueryService.createHashIndex()` | 10.0 | Hash indexes deprecated |
| `QueryService.defineHashIndex()` | 10.0 | Hash indexes deprecated |

## File Structure (Condensed)

```
spring-data-for-vmware-gemfire/
├── gud-api/
│   └── src/main/java/.../gud/api/
│       ├── GudCapability.java          ← Moved from gud-core
│       ├── GudApiVersion.java          ← Moved from gud-core
│       ├── GudCacheProvider.java       ← configure() + Supplier pattern
│       ├── GudUnsupportedOperationException.java  ← Uses GudCapability enum
│       └── ... (interfaces)
│
├── gud-core/
│   └── src/main/java/.../gud/core/
│       ├── GudDriver.java              ← createJndiBinding() + createPoolManager()
│       └── GudDriverManager.java       ← Fixed sort, race cond., push to provider
│
├── gud-driver-gemfire-10.x/ (x = 1,2,3)
│   ├── src/main/java/.../gud/driver/
│   │   └── GemFireDriver.java          ← Fixed capabilities; new createXxx() methods
│   └── src/main/resources/META-INF/services/
│       └── org.springframework.data.gemfire.gud.core.GudDriver  ← Only this file
│
└── spring-data-vmware-gemfire/
    └── src/main/java/.../
        ├── support/GudVersionAwareInvoker.java   ← New utility
        ├── DiskStoreFactoryBean.java             ← Uses GudVersionAwareInvoker
        └── client/PoolFactoryBean.java           ← Uses GudVersionAwareInvoker
```
