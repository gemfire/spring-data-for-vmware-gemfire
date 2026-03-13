# GemFire Unified Driver (GUD) Architecture

## Overview

The GemFire Unified Driver (GUD) is an abstraction layer that decouples Spring Data GemFire from native GemFire APIs. This enables:

1. **Driver Swappability** - Switch between GemFire versions (10.3, 10.4, etc.) without changing application code
2. **API Evolution** - Gracefully handle API changes between GemFire versions
3. **Clean Separation** - Application code never directly imports native GemFire classes

## Architecture Layers

```
┌─────────────────────────────────────────────────────────────────┐
│                      Application Layer                          │
│  (TestCachingApp, User Applications)                           │
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
│  - Dependencies: gud-api (api), gud-core (implementation)       │
└─────────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────────┐
│                          gud-api                                │
│  - Pure Java interfaces (no Spring, no native GemFire)         │
│  - GudClientCache, GudRegion, GudPool, etc.                    │
│  - GudCacheProvider - ServiceLoader-based factory discovery     │
│  - Defines the contract between Spring layer and drivers        │
└─────────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────────┐
│                         gud-core                                │
│  - Shared utilities and base implementations                    │
│  - Common functionality used by drivers                         │
└─────────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────────┐
│                   gud-driver-gemfire-10.3                       │
│  - Concrete implementations of GUD interfaces                   │
│  - Wraps native GemFire 10.3 APIs                              │
│  - Registered via ServiceLoader (META-INF/services/)           │
│  - NO Spring dependencies                                       │
└─────────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────────┐
│                    Native GemFire 10.3                          │
│  (org.apache.geode.* classes)                                  │
└─────────────────────────────────────────────────────────────────┘
```

## Key Design Principles

### 1. No Native GemFire Imports in Application Code
Applications should only import from:
- `org.springframework.data.gemfire.*` (Spring Data GemFire)
- `org.springframework.data.gemfire.gud.api.*` (GUD API interfaces)

### 2. Driver Layer Has No Spring Dependencies
The driver (`gud-driver-gemfire-10.3`) contains zero Spring imports. It only:
- Implements GUD API interfaces
- Wraps native GemFire classes
- Registers implementations via ServiceLoader

### 3. ServiceLoader-Based Discovery
Drivers are discovered automatically via Java's ServiceLoader mechanism:
```
META-INF/services/org.springframework.data.gemfire.gud.api.GudClientCacheFactory
META-INF/services/org.springframework.data.gemfire.gud.api.GudPoolManager
META-INF/services/org.springframework.data.gemfire.gud.api.GudJndiBinding
```

### 4. GudCacheProvider as the Bridge
`GudCacheProvider` is the central access point that:
- Auto-discovers driver implementations via ServiceLoader
- Provides factories to the Spring layer
- Maintains singleton references for caches

## Module Dependency Graph

```
TestCachingApp
├── implementation: spring-data-vmware-gemfire
└── runtimeOnly: gud-driver-gemfire-10.3

spring-data-vmware-gemfire
├── api: gud-api
└── implementation: gud-core

gud-driver-gemfire-10.3
├── implementation: gud-api
├── implementation: gud-core
└── implementation: gemfire-core (native)
```

## What Has Been Implemented

### gud-api Module
| Interface | Status | Description |
|-----------|--------|-------------|
| `GudClientCache` | ✅ Complete | Client cache abstraction |
| `GudClientCacheFactory` | ✅ Complete | Factory for creating client caches |
| `GudRegion` | ✅ Complete | Region abstraction with full Map-like API |
| `GudPool` | ✅ Complete | Connection pool abstraction |
| `GudPoolFactory` | ✅ Complete | Factory for creating pools |
| `GudPoolManager` | ✅ Complete | Pool management (find, getAll, close) |
| `GudDiskStore` | ✅ Complete | Disk store abstraction |
| `GudDiskStoreFactory` | ✅ Complete | Factory for creating disk stores |
| `GudCacheListener` | ✅ Complete | Event listener interface |
| `GudCacheWriter` | ✅ Complete | Cache writer interface |
| `GudCacheLoader` | ✅ Complete | Cache loader interface |
| `GudAttributesMutator` | ✅ Complete | Region attributes modification |
| `GudDistributedSystem` | ✅ Complete | Distributed system abstraction |
| `GudDistributedMember` | ✅ Complete | Cluster member abstraction |
| `GudJndiBinding` | ✅ Complete | JNDI data source management |
| `GudCacheProvider` | ✅ Complete | ServiceLoader-based factory discovery |
| `GudClientRegionShortcut` | ✅ Complete | Region type shortcuts (PROXY, CACHING_PROXY, etc.) |

### gud-driver-gemfire-10.3 Module
| Implementation | Status | Description |
|----------------|--------|-------------|
| `GemFire103ClientCache` | ✅ Complete | Wraps native ClientCache |
| `GemFire103ClientCacheFactory` | ✅ Complete | Wraps native ClientCacheFactory |
| `GemFire103Region` | ✅ Complete | Wraps native Region |
| `GemFire103ClientRegionFactory` | ✅ Complete | Wraps native ClientRegionFactory |
| `GemFire103Pool` | ✅ Complete | Wraps native Pool |
| `GemFire103PoolFactory` | ✅ Complete | Wraps native PoolFactory |
| `GemFire103PoolManager` | ✅ Complete | Wraps native PoolManager |
| `GemFire103DiskStore` | ✅ Complete | Wraps native DiskStore |
| `GemFire103DiskStoreFactory` | ✅ Complete | Wraps native DiskStoreFactory |
| `GemFire103AttributesMutator` | ✅ Complete | Wraps native AttributesMutator |
| `GemFire103DistributedSystem` | ✅ Complete | Wraps native DistributedSystem |
| `GemFire103DistributedMember` | ✅ Complete | Wraps native DistributedMember |
| `GemFire103JndiBinding` | ✅ Complete | Wraps native JNDIInvoker |
| `GemFire103CacheListenerAdapter` | ✅ Complete | Adapts GudCacheListener to native |
| `GemFire103CacheWriterAdapter` | ✅ Complete | Adapts GudCacheWriter to native |
| `GemFire103CacheLoaderAdapter` | ✅ Complete | Adapts GudCacheLoader to native |
| `GemFire103EntryEvent` | ✅ Complete | Wraps native EntryEvent |
| `GemFire103RegionEvent` | ✅ Complete | Wraps native RegionEvent |
| `GemFire103LoaderHelper` | ✅ Complete | Wraps native LoaderHelper |

### spring-data-vmware-gemfire Module
| Component | Status | Description |
|-----------|--------|-------------|
| `ClientCacheFactoryBean` | ✅ Migrated | Uses GudCacheProvider for factory |
| `ClientRegionFactoryBean` | ✅ Migrated | Creates GudRegion instances |
| `DiskStoreFactoryBean` | ✅ Migrated | Uses GudClientCache for factory |
| `ClientCacheConfiguration` | ✅ Migrated | @ClientCacheApplication support |
| `@EnableDiskStore` | ✅ Working | Disk store annotation support |
| `@EnableGemfireRepositories` | ✅ Working | Repository support |
| `PoolManagerPoolResolver` | ✅ Migrated | Uses GudCacheProvider.getPoolManager() |
| `DistributedSystemUtils` | ✅ Migrated | Uses GudCacheProvider |

### ServiceLoader Registrations (META-INF/services/)
| Service Interface | Registered Implementation |
|-------------------|---------------------------|
| `GudClientCacheFactory` | `GemFire103ClientCacheFactory` |
| `GudPoolManager` | `GemFire103PoolManager` |
| `GudJndiBinding` | `GemFire103JndiBinding` |

## What Remains To Be Done

### High Priority

#### 1. Server-Side Cache Support
- [ ] `GudCache` interface (peer cache, not client)
- [ ] `GudCacheFactory` implementation
- [ ] `GudCacheServer` interface
- [ ] Server region types (REPLICATE, PARTITION, etc.)

#### 2. Query Support
- [ ] `GudQueryService` interface
- [ ] `GudQuery` interface
- [ ] `GudSelectResults` interface
- [ ] OQL query execution

#### 3. Function Execution
- [ ] `GudFunctionService` interface
- [ ] `GudExecution` interface
- [ ] `GudResultCollector` interface
- [ ] Function registration and execution

#### 4. Continuous Query (CQ)
- [ ] `GudCqService` interface
- [ ] `GudCqQuery` interface
- [ ] `GudCqListener` interface
- [ ] CQ event handling

#### 5. Transaction Support
- [ ] `GudCacheTransactionManager` interface
- [ ] Transaction begin/commit/rollback
- [ ] Transaction event listeners

### Medium Priority

#### 6. PDX Serialization
- [ ] `GudPdxInstance` interface
- [ ] `GudPdxInstanceFactory` interface
- [ ] PDX field access methods
- [ ] PDX type registry

#### 7. Security
- [ ] `GudSecurityManager` interface
- [ ] Authentication callbacks
- [ ] Authorization support

#### 8. Statistics
- [ ] `GudStatistics` interface
- [ ] `GudStatisticsType` interface
- [ ] Statistics sampling

#### 9. WAN Replication
- [ ] `GudGatewaySender` interface
- [ ] `GudGatewayReceiver` interface
- [ ] WAN event handling

### Lower Priority

#### 10. Additional Spring Data Features
- [ ] `@EnableClusterConfiguration` support
- [ ] `@EnableContinuousQueries` support
- [ ] `@EnableGemfireFunctionExecutions` full support
- [ ] Lucene index support

#### 11. Testing Infrastructure
- [ ] `spring-test-vmware-gemfire` migration
- [ ] Mock GUD implementations for testing
- [ ] Integration test utilities

#### 12. Additional Drivers
- [ ] `gud-driver-gemfire-10.4` (when 10.4 is released)
- [ ] Driver version negotiation
- [ ] Graceful feature degradation for older drivers

## API Evolution Strategy

When a new GemFire version (e.g., 10.4) introduces API changes:

### Adding New Features
1. Add new methods to GUD interfaces with `default` implementations that throw `UnsupportedOperationException`
2. Implement in the new driver
3. Old drivers continue to work (throw exception if new feature is used)

### Handling Removed Features
1. Keep methods in GUD interfaces
2. New driver throws `UnsupportedOperationException` with clear message
3. Applications can check driver version and adapt

### Handling Changed Signatures
1. Add new method with new signature
2. Deprecate old method
3. Old driver implements old method
4. New driver implements new method
5. Both can coexist

## Example: TestCachingApp Configuration

```java
@Configuration
@ClientCacheApplication(
    name = "TestCachingApp",
    locators = @ClientCacheApplication.Locator(host = "localhost", port = 23232),
    subscriptionEnabled = true
)
@EnableDiskStore(
    name = "CachingProxyDiskStore",
    autoCompact = true,
    diskDirectories = @EnableDiskStore.DiskDirectory(location = "./data/diskstore")
)
@EnableGemfireRepositories(basePackages = "com.example.testcaching.repository")
public class GemFireClientConfiguration {

    @Bean("RegionProxy")
    public ClientRegionFactoryBean<String, Object> regionProxy(
            GudClientCache clientCache,
            GudCacheListener<String, Object> listener) {
        
        ClientRegionFactoryBean<String, Object> factory = new ClientRegionFactoryBean<>();
        factory.setCache(clientCache);
        factory.setShortcut(GudClientRegionShortcut.PROXY);
        factory.setCacheListeners(new GudCacheListener[] { listener });
        return factory;
    }
}
```

Note: The application imports `GudClientCache`, `GudCacheListener`, and `GudClientRegionShortcut` from the GUD API - never native GemFire classes.

## File Structure

```
spring-data-for-vmware-gemfire/
├── gud-api/
│   └── src/main/java/org/springframework/data/gemfire/gud/api/
│       ├── GudClientCache.java
│       ├── GudClientCacheFactory.java
│       ├── GudCacheProvider.java
│       ├── GudRegion.java
│       ├── GudPool.java
│       ├── GudPoolManager.java
│       ├── GudDiskStore.java
│       ├── GudCacheListener.java
│       ├── GudCacheWriter.java
│       ├── GudCacheLoader.java
│       └── ... (other interfaces)
│
├── gud-core/
│   └── src/main/java/org/springframework/data/gemfire/gud/core/
│       └── (shared utilities)
│
├── gud-driver-gemfire-10.3/
│   ├── src/main/java/org/springframework/data/gemfire/gud/driver/gemfire103/
│   │   ├── GemFire103ClientCache.java
│   │   ├── GemFire103ClientCacheFactory.java
│   │   ├── GemFire103Region.java
│   │   ├── GemFire103Pool.java
│   │   ├── GemFire103PoolManager.java
│   │   ├── GemFire103CacheListenerAdapter.java
│   │   ├── GemFire103CacheWriterAdapter.java
│   │   └── ... (other implementations)
│   └── src/main/resources/META-INF/services/
│       ├── org.springframework.data.gemfire.gud.api.GudClientCacheFactory
│       ├── org.springframework.data.gemfire.gud.api.GudPoolManager
│       └── org.springframework.data.gemfire.gud.api.GudJndiBinding
│
├── spring-data-vmware-gemfire/
│   └── src/main/java/org/springframework/data/gemfire/
│       ├── client/
│       │   ├── ClientCacheFactoryBean.java
│       │   └── ClientRegionFactoryBean.java
│       ├── config/annotation/
│       │   └── ClientCacheConfiguration.java
│       └── ... (other Spring components)
│
└── TestCachingApp/
    └── src/main/java/com/example/testcaching/
        ├── GemFireClientConfiguration.java
        ├── TestCachingApplication.java
        ├── KeyPrintingCacheListener.java
        └── SKeyFilteringCacheWriter.java
```

## Running the Test Application

```bash
# Build all modules
./gradlew build

# Run the test application
./gradlew :TestCachingApp:run

# Expected output:
# TestCachingApplication initialized successfully!
# Beans created:
#   - ClientCache: TestCachingApp
#   - RegionProxy: RegionProxy
#   - RegionCachingProxy: RegionCachingProxy
```

Note: The application will show connection warnings if no GemFire locator is running on port 23232. This is expected behavior.
