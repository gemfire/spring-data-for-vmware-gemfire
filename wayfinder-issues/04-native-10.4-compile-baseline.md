Type: task
Status: resolved
Assignee: uk016635
Blocked by: 03

## Question

Restore the existing Spring Data module's main-source compile against the requested GemFire `10.4.+` line so subsequent Step 3 slices can also run the full module's tests.

The before/after baseline captured by [Spring Cache driver migration](03-spring-cache-driver-migration.md) has three errors: missing `Pool.getAvailabilityZone()` implementations in the `PoolAdapter`/`DelegatingPoolAdapter` paths, and the removed `ConfigurationProperties.SECURITY_SHIRO_INIT` constant referenced by `GemFireProperties`.

Adapt the still-native compatibility code to the resolved native API, preserving delegation and explicit failure for unavailable configuration. Do not silently drop configuration or change the signed-off SPI version model. Run full main/test compilation and the focused driver/cache regression checks, and record any further baseline failures exposed by compilation.

## Answer

Resolved 2026-10-20 against `10.4.+`, resolved to `10.4.0-build.3056.df98c835`.

- Added `getAvailabilityZone()` throughout the native pool adapter hierarchy: the base adapter explicitly rejects unsupported access, factory defaults use the native constant, delegating adapters preserve the native value (including null), and the uninitialized `PoolFactoryBean` view reports the native default.
- Retained the deprecated `SECURITY_SHIRO_INIT` enum name as a literal without depending on the removed native constant. Nonblank Shiro INI configuration now fails explicitly through both `@EnableSecurity` annotation/environment settings and cache-factory properties, including inherited `Properties` defaults. Diagnostics recommend `security-manager` and omit the configured path.
- Property-inventory tests exposed two further 10.4 differences: added `AVAILABILITY_ZONE` to `GemFireProperties`, and recognized native `redundancy-zone` deprecation in the test inventory. The existing redundancy-zone mapping remains intact.
- Running the previously unverified compound cache/repository integration test exposed mismatched distributed-system properties in its temporary native/SPI setup. Its driver connection now receives the existing native cache's properties, allowing native cache reuse. This remains test-only migration wiring; repository migration will remove it.

Validation:

- Full `compileJava`, `compileTestJava` and `compileIntegrationTestJava` succeeded with `-DgemfireVersion=10.4.+`. No further compilation errors surfaced.
- 126 targeted unit tests passed: pool factory/client cache factory, pool adapters, integrated security configuration and property inventory.
- All 7 `EnableGemFirePropertiesIntegrationTests` and both `CompoundCachePutCacheEvictIntegrationTests` passed. The former ran in the initial integration pass; the latter passed after correcting the temporary connection wiring.
- Both cache behavior suites (22 tests each), all five driver-module `check` tasks (57 tests), and both native-free boundary checks succeeded. Gradle reused unchanged test outputs where up to date; none of the test cases were skipped.
- Logs: `/private/tmp/sdgf-native-baseline-compile.log`, `/private/tmp/sdgf-native-baseline-final-tests.log` (unit/property success plus the diagnosed compound-cache failure), and `/private/tmp/sdgf-native-baseline-regressions.log` (successful corrected integration and regression checks).

The full legacy test suite has been compiled, not executed. Native API and Gradle deprecation warnings remain. The next bounded Step 3 slice is [Cache loader driver migration](05-cache-loader-driver-migration.md).
