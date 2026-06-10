# GUD Migration — In-Progress Tracker

Living session-resumption document. Tick off issues as they are fixed.

---

## Session State (2026-06-06)

**Last completed:** Issue C — `GudInterestResultPolicy.DEFAULT` is now a static alias for `KEYS_VALUES`; `InterestResultPolicyType.DEFAULT` is a direct constant reference to `KEYS_VALUES`.

**Currently open issues (in priority order):**
1. **Issue A.1** — Real drivers need `GemFireFunctionService` (function execution for integration tests). Wrapping is clean 1:1 with native `FunctionService` — see detail below.
2. **Issue B** — `fromOrdinal()` null vs exception (3 tests)
3. **Issue C** — `GudInterestResultPolicy.DEFAULT` not an alias (2 tests)
4. **Issue D** — `ClientRegionShortcutWrapper` wrong data policy (2 tests)
5. **Issue E** — `ClientRegionFactoryBean` missing `PERSISTENT_REPLICATE` case (2 tests)
6. **Issue F** — 8 error message mismatches
7. **Issue G** — `RegionDataAccessTracingAspect` pointcuts target wrong type (~30 tests)
8. **Issue H** — `ContinuousQueryListenerContainer` no default CQ attributes supplier (4 tests)
9. **Issue I** — `DefaultableDelegatingPoolAdapter.getSocketFactory()` wrong in preferPool mode

**Cursor rule in effect:** `.cursor/rules/gud-test-driver-separation.mdc` (always applied) — unit tests use `MockGudDriver`, integration tests use `gud-driver-gemfire-10.x`.

---

## Quick Start

**Branch:** `udo.kohlmeyer/unified_driver`

**Key files to read at session start:**

| File | Role |
|------|------|
| `GUD_ARCHITECTURE.md` | Overall GUD module structure and design decisions |
| `API_EVOLUTION.md` | API versioning policy and compatibility rules |
| `API_VERSION_MATRIX.md` | Per-driver capability matrix |
| `GUD_MIGRATION_STATUS.md` | Migration status per class |
| `.cursor/rules/gud-test-driver-separation.mdc` | AI rule: unit tests = mock driver, integration tests = real driver |

**See current test failures:**
```bash
./gradlew :spring-data-vmware-gemfire:test 2>&1 | grep -E "FAILED|ERROR" | head -40
```

**Run a single test class:**
```bash
./gradlew :spring-data-vmware-gemfire:test --tests "org.springframework.data.gemfire.config.annotation.RegionDataAccessTracingAspectUnitTests"
```

**Run integration tests against a specific driver (default 10.3):**
```bash
./gradlew :spring-data-vmware-gemfire:integrationTest -PgudIntegrationDriver=10.1
```

---

## Architecture Context

The project uses a **GUD (GemFire Unified Driver)** abstraction layer so `spring-data-vmware-gemfire` has zero direct dependencies on `org.apache.geode.*` at compile time.

```
spring-data-vmware-gemfire
      │
      ├─ compile: gud-api          (GudRegion, GudFunctionService, GudPool, …)
      ├─ compile: gud-core         (GudDriver SPI, GudDriverManager)
      │
      ├─ test runtime: gud-driver-mock          (MockGudDriver — in-memory, unit tests only)
      └─ integrationTest runtime: gud-driver-gemfire-10.x   (wraps native GemFire)
```

**Driver separation rule** (also enforced by `.cursor/rules/gud-test-driver-separation.mdc`):
- Unit tests (`src/test/java`) → `MockGudDriver` via `testImplementation`; never use real driver here
- Integration tests (`src/integrationTest/java`) → `gud-driver-gemfire-10.x`; `MockGudDriver` is excluded from this classpath by the build

**Key files:**

| Path | Role |
|------|------|
| `gud-api/src/main/java/…/GudFunctionService.java` | Delegate-pattern shim; static methods dispatch to registered driver instance |
| `gud-api/src/main/java/…/GudCacheProvider.java` | Creates/wraps caches via active driver |
| `gud-api/src/main/java/…/GudCapability.java` | Feature-detection enum |
| `gud-core/src/main/java/…/GudDriver.java` | Driver SPI — includes `createFunctionService()` default method |
| `gud-core/src/main/java/…/GudDriverManager.java` | ServiceLoader discovery; calls `GudFunctionService.register()` on driver load |
| `gud-driver-mock/src/main/java/…/MockGudDriver.java` | In-memory driver for unit tests |
| `gud-driver-mock/src/main/java/…/MockGudFunctionService.java` | ConcurrentHashMap-backed GudFunctionService for unit tests |
| `gud-driver-mock/src/main/java/…/MockGudFactories.java` | Mock factory implementations; `newExecution()` returns stubbed GudExecution |
| `spring-test-vmware-gemfire/src/main/java/…/GemFireMockObjectsSupport.java` | Test cleanup & mock wiring |
| `spring-test-vmware-gemfire/src/main/java/…/IntegrationTestsSupport.java` | Base class with `@AfterClass` cleanup hooks |

---

## Issue Tracker

### Issue A — `GudFunctionService` static methods throw `UnsupportedOperationException`
- [x] Fixed (delegate pattern implemented 2026-06-06)

**Root cause:** `GudFunctionService` is an abstract class with `static` methods that unconditionally throw `UnsupportedOperationException`. Java resolves static methods at compile time against the declaring class — no driver subclass can override them. There is also no delegate registered anywhere, so every call path that reaches any of the 14 static methods blows up.

**Blocks:** ~40 tests — all `Enable*IntegrationTests`, `RegionDataAccessTracingAspectUnitTests.executionError` (via `@AfterClass` teardown), `FunctionServiceNamespaceIntegrationTests`, and any test extending `IntegrationTestsSupport` (because `unregisterFunctions()` calls `getRegisteredFunctions()`).

**What was implemented (2026-06-06):**

Six files changed across three modules:

| Module | File | Change |
|--------|------|--------|
| `gud-api` | `GudFunctionService.java` | Full delegate-pattern refactor |
| `gud-core` | `GudDriver.java` | Added `default createFunctionService() { return null; }` |
| `gud-core` | `GudDriverManager.java` | `pushFactoriesToCacheProvider()` calls `GudFunctionService.register()`; `clearDrivers()` calls `GudFunctionService.register(null)` |
| `gud-driver-mock` | `MockGudFunctionService.java` | New — `ConcurrentHashMap` registry + Mockito-backed `doOn*()` methods |
| `gud-driver-mock` | `MockGudFactories.java` | Added `newExecution()` — Mockito `GudExecution` stub with `RETURNS_SELF` |
| `gud-driver-mock` | `MockGudDriver.java` | Added `MockGudFunctionService` field; overrides `createFunctionService()` |

**Registration flow:**
```
ServiceLoader discovers MockGudDriver
  → GudDriverManager.registerDriver()
    → pushFactoriesToCacheProvider()
      → GudCacheProvider.configure(...)            (existing)
      → driver.createFunctionService()             (returns MockGudFunctionService)
      → GudFunctionService.register(mockService)   (NEW)
```

**Behaviour by driver:**

| Classpath | `getRegisteredFunctions()` | `onRegion()` |
|-----------|---------------------------|-------------|
| `gud-driver-mock` | delegates to `MockGudFunctionService` (returns empty map, never throws) | returns `MockGudExecution` via Mockito |
| `gud-driver-gemfire-10.x` (current) | `delegate == null` → `Collections.emptyMap()` safe | throws — real driver does NOT yet implement `createFunctionService()` |

**Follow-on required — see Issue A.1 below.**

---

### Issue A.1 — Real drivers need `GemFireFunctionService` to enable function execution
- [ ] Fixed

**Context:** `GemFireDriver` (10.1/10.2/10.3) inherits `createFunctionService() { return null; }` from the `GudDriver` default. Teardown is safe (null delegate → emptyMap). But any call through `GudFunctionService.onRegion/onServer/…` — which is what `OnRegionFunctionExecution`, `OnServerUsingPoolFunctionExecution`, etc. use — will throw `UnsupportedOperationException` with a real driver loaded.

**Not required for unit tests** (mock driver handles those). Required for integration tests that exercise Spring function execution.

**The wrapping is clean and mechanical** — GemFire's `FunctionService` static API is 1:1 with `GudFunctionService`:

```java
// In each gud-driver-gemfire-10.x:
class GemFireFunctionService extends GudFunctionService {

    @Override
    protected GudExecution<?,?,?> doOnRegion(GudRegion<?,?> region) {
        Region<?,?> nativeRegion = ((NativeWrapper<Region<?,?>>) region).getNative();
        return new GemFireExecution<>(FunctionService.onRegion(nativeRegion));
    }

    @Override
    protected GudExecution<?,?,?> doOnServerWithPool(GudPool pool) {
        Pool nativePool = ((NativeWrapper<Pool>) pool).getNative();
        return new GemFireExecution<>(FunctionService.onServer(nativePool));
    }

    // … all other doOn*() follow the same NativeWrapper unwrap → FunctionService call → GemFireExecution wrap

    @Override
    protected Map<String, GudFunction> doGetRegisteredFunctions() {
        // GemFire's FunctionService.getRegisteredFunctions() returns Map<String, Function>
        // Each native Function needs to be wrapped as a GudFunction — see GudFunctionAdapter below
        return FunctionService.getRegisteredFunctions().entrySet().stream()
            .collect(Collectors.toMap(Map.Entry::getKey,
                entry -> new GudFunctionAdapter(entry.getValue())));
    }

    @Override
    protected void doRegisterFunction(GudFunction function) {
        // GudFunction registered from Spring side needs adapting to native Function
        FunctionService.registerFunction(toNativeFunction(function));
    }

    @Override protected void doUnregisterFunction(String functionId) {
        FunctionService.unregisterFunction(functionId);
    }
    @Override protected boolean doIsRegistered(String functionId) {
        return FunctionService.isRegistered(functionId);
    }
    @Override protected GudFunction doGetFunction(String functionId) {
        Function nativeFunction = FunctionService.getFunction(functionId);
        return nativeFunction != null ? new GudFunctionAdapter(nativeFunction) : null;
    }
}
```

**Two adapter classes needed (new files per driver):**

1. `GudFunctionAdapter` — wraps a native `Function` as `GudFunction` (and vice versa via `NativeWrapper`)
2. `GemFireFunctionContext` — wraps `FunctionContext` as `GudFunctionContext` (for `execute()` delegation)

**`doRegisterFunction` complication:** When a `GudFunction` registered from the Spring side is a plain POJO (not a `NativeWrapper`), GemFire needs it adapted as a native `Function`:
```java
private static Function toNativeFunction(GudFunction function) {
    if (function instanceof NativeWrapper) {
        return ((NativeWrapper<Function>) function).getNative();
    }
    return new NativeGudFunctionAdapter(function);  // adapter that implements native Function
}
```

**Files to create per driver (10.1, 10.2, 10.3 — API is identical across versions):**
- `GemFireFunctionService.java` — extends `GudFunctionService`
- `GudFunctionAdapter.java` — wraps native `Function` as `GudFunction`
- `NativeGudFunctionAdapter.java` — wraps `GudFunction` as native `Function`
- `GemFireFunctionContext.java` — wraps `FunctionContext` as `GudFunctionContext`

**`GemFireDriver` change:** Override `createFunctionService()`:
```java
@Override
public GudFunctionService createFunctionService() {
    return new GemFireFunctionService();
}
```

This single override is all that's needed — `GudDriverManager` automatically calls `GudFunctionService.register()` from `pushFactoriesToCacheProvider()`.

---

### Issue B — `fromOrdinal()` returns `null` instead of throwing `ArrayIndexOutOfBoundsException`
- [x] Fixed (2026-06-06)

**Root cause:** Guard clause `if (ordinal < 0 || ordinal >= VALUES.length) return null` prevents the natural array bounds exception. Tests in `ScopeTypeUnitTests`, `InterestResultPolicyTypeUnitTests`, and `ExpirationActionTypeUnitTests` use the out-of-bounds exception as a loop terminator (`for (int i = 0; ; i++)` — stops when exception is thrown).

**Blocks:** 3 tests.

**Files to change:**
- `gud-api/src/main/java/…/GudScope.java`
- `gud-api/src/main/java/…/GudInterestResultPolicy.java`
- `gud-api/src/main/java/…/GudExpirationAction.java`

**Fix:** Replace `if (ordinal < 0 || ordinal >= VALUES.length) return null;` with `return VALUES[ordinal];` — let the `ArrayIndexOutOfBoundsException` propagate naturally.

---

### Issue C — `GudInterestResultPolicy.DEFAULT` is a separate enum constant, not an alias for `KEYS_VALUES`
- [x] Fixed (2026-06-06)

**Root cause:** `DEFAULT` is declared as its own enum constant (ordinal 0), so `VALUES = values()` has it at index 0. `InterestResultPolicyType` maps from GemFire's `InterestResultPolicy` to `GudInterestResultPolicy` by ordinal — GemFire has no `DEFAULT` constant, so the mapping for ordinal 0 (`DEFAULT`) is `null`, causing `InterestResultPolicyType.DEFAULT` to be `null`.

**Blocks:** `InterestResultPolicyTypeUnitTests.testDefault`, `InterestUnitTests.constructInterestWithKey`.

**Files to change:**
- `gud-api/src/main/java/…/GudInterestResultPolicy.java`
- `spring-data-vmware-gemfire/src/main/java/…/InterestResultPolicyType.java`

**Fix:**
- Remove `DEFAULT` from the enum value list in `GudInterestResultPolicy`
- Add `public static final GudInterestResultPolicy DEFAULT = KEYS_VALUES;` as a static field alias
- Change `InterestResultPolicyType.DEFAULT` constant initialiser to `= InterestResultPolicyType.KEYS_VALUES`

---

### Issue D — `ClientRegionShortcutWrapper` maps `LOCAL_PERSISTENT*` to `NORMAL` instead of `PERSISTENT_REPLICATE`
- [ ] Fixed

**Blocks:** 2 `ClientRegionShortcutToDataPolicyConverterUnitTests`.

**File to change:** `spring-data-vmware-gemfire/src/main/java/…/client/ClientRegionShortcutWrapper.java`

**Fix:** Change the `LOCAL_PERSISTENT` and `LOCAL_PERSISTENT_OVERFLOW` enum entries to map to `GudDataPolicy.PERSISTENT_REPLICATE` instead of `GudDataPolicy.NORMAL`.

---

### Issue E — `ClientRegionFactoryBean.resolveClientRegionShortcut()` missing `PERSISTENT_REPLICATE` case
- [ ] Fixed

**Blocks:** 2 `ClientRegionFactoryBeanUnitTests`.

**File to change:** `spring-data-vmware-gemfire/src/main/java/…/client/ClientRegionFactoryBean.java`

**Fix:** Add the following branch before the terminal `else` throw in `resolveClientRegionShortcut()`:
```java
else if (GudDataPolicy.PERSISTENT_REPLICATE.equals(dataPolicy)) {
    resolvedShortcut = GudClientRegionShortcut.LOCAL_PERSISTENT;
}
```

---

### Issue F — Error message mismatches (tests expect "GudXxx", code still uses old name)
- [ ] Fixed

**Blocks:** 8 individual assertion failures across multiple test classes.

**Fix — 8 string replacements:**

| File | Old string | New string |
|------|-----------|-----------|
| `GemfireDaoSupport` | `"Region"` | `"GudRegion"` |
| `BatchingResultSender` | `"ResultSender"` | `"GudResultSender"` |
| `AbstractFunctionExecution` | `"Execution of Function"` | `"GudExecution of GudFunction"` |
| `AbstractSelectResults` | `"SelectResults"` | `"GudSelectResults"` |
| `Regions` | `"Region name/path"` | `"GudRegion name/path"` |
| `ContinuousQueryListenerContainer` | `"Pool with name"` | `"GudPool with name"` |
| `ContinuousQueryListenerContainer` | `"QueryService is required"` | `"GudQueryService is required"` |
| `SingleRegionRegionResolver` | `"Region must not be null"` | `"GudRegion must not be null"` |

Note: `AbstractSelectResults` fix also resolves the `PagedSelectResults` failure since it delegates to the same message.

---

### Issue G — `RegionDataAccessTracingAspect` pointcuts still target `org.apache.geode.cache.Region`
- [ ] Fixed

**Blocks:** ~30 `RegionDataAccessTracingAspectUnitTests` (`logsRegionCreate`, `logsRegionGet`, etc.) + `executionError` via Issue A.

**File to change:** `spring-data-vmware-gemfire/src/main/java/…/support/RegionDataAccessTracingAspect.java`

**Fix:**
- Replace `target(org.apache.geode.cache.Region)` with `target(org.springframework.data.gemfire.gud.api.GudRegion)` in `@Pointcut("target(...)")`
- Replace every `execution(* org.apache.geode.cache.Region.xxx(..))` with `execution(* org.springframework.data.gemfire.gud.api.GudRegion.xxx(..))` in `regionDataAccessPointcut()`
- Change the advice log message from `"Region data access call"` to `"GudRegion data access call"`

---

### Issue H — `ContinuousQueryListenerContainer` throws when no `GudCqAttributesFactory` supplier set
- [ ] Fixed

**Root cause:** `newCqAttributesFactory()` asserts that a supplier is non-null; unit tests that call `addContinuousQuery*` / `addManaged*` don't configure one.

**Blocks:** 4 `ContinuousQueryListenerContainerUnitTests`.

**File to change:** `spring-data-vmware-gemfire/src/main/java/…/listener/ContinuousQueryListenerContainer.java`

**Fix options (pick one):**
1. Provide a no-arg default supplier fallback inside `newCqAttributesFactory()` when no supplier has been configured
2. Update the unit tests to configure a mock `GudCqAttributesFactory` supplier before calling `addContinuousQuery`

Option 1 is preferred — it makes the container more robust in general.

---

### Issue I — `DefaultableDelegatingPoolAdapter.getSocketFactory()` returns wrong value in `preferPool` mode
- [ ] Fixed

**Root cause:** `defaultIfNull(arg, poolSupplier)` always prefers the non-null `arg`; in `preferPool` mode the ordering must be reversed so the pool's value takes precedence.

**File to change:** `spring-data-vmware-gemfire/src/main/java/…/client/support/DefaultableDelegatingPoolAdapter.java`

**Fix:** In `getSocketFactory(SocketFactory socketFactory)`, when in `preferPool` mode, call `defaultIfNull(poolSupplier.get(), () -> socketFactory)` instead of `defaultIfNull(socketFactory, poolSupplier)`.

---

## Driver Compatibility Matrix

| Driver | Classpath key | Capabilities beyond base | How to run |
|--------|--------------|--------------------------|-----------|
| 10.1 | `gudIntegrationDriver=10.1` | `PER_SERVER_CONNECTION_LIMITS`, `DISK_STORE_SEGMENTS` | `./gradlew :spring-data-vmware-gemfire:integrationTest -PgudIntegrationDriver=10.1` |
| 10.2 | `gudIntegrationDriver=10.2` | `PER_SERVER_CONNECTION_LIMITS`, `DISK_STORE_SEGMENTS` | `./gradlew :spring-data-vmware-gemfire:integrationTest -PgudIntegrationDriver=10.2` |
| 10.3 | `gudIntegrationDriver=10.3` (default) | + `SECURITY_MANAGER`, `SERVER_REGION_NAME` | `./gradlew :spring-data-vmware-gemfire:integrationTest` |

**Unit tests always use `MockGudDriver` (version `10.99`)** — advertises all `GudCapability` values so feature-gated code paths always take the supported branch.
