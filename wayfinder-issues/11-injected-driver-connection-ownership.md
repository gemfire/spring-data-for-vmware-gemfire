Type: task
Status: resolved
Assignee: Codex (resumed 2026-10-21)
Blocked by: 10

## Question

Implement the smallest complete Spring connection ownership boundary: a native-free `DriverConnectionFactoryBean` with constructor-injected creation dependencies or an explicitly borrowed `DriverConnection`, wired to the already migrated cache manager and resolvers.

Use the [cache-factory dependency inventory](../wayfinder-impl/investigations/cache-factory-ownership-and-configuration.md). This is the foundation for replacing the native factory hierarchy; legacy annotation/XML entry points remain native until their configuration consumers migrate. It is not a permanent dual-API compatibility layer.

## Required behavior

- Place the new factory in `org.springframework.data.gemfire`; implement Spring factory/lifecycle contracts directly rather than inheriting native factory support. All collaborators are constructor-injected; no mutable statics, service locators or native types.
- Creation receives a validated `GemFireDriverManager` and `CacheConfiguration`. Initialize one product through the injected driver and expose `DriverConnection` as its type even before creation. Repeated access/initialization must not reconnect; destruction before creation must not open anything. Constructor validation should not open a connection.
- Borrowing receives the exact `DriverConnection`; it performs no discovery, creation, property merge or reconfiguration and never closes that handle. Use an unambiguous constructor/API distinction between creation and borrowing.
- The creator owns shutdown of the returned handle. Make local lifecycle behavior explicit, including access before initialization/after destruction, failed initialization, repeated destruction and close failure. No automatic recovery/reconnect or silent failure. If initialization fails after obtaining an owned handle, clean it up while retaining the original failure. Do not infer distributed-system connected state from `isClosed`.
- Ownership is responsibility for closure, not a guarantee of exclusive native-cache creation. Native `ClientCacheFactory.create()` can return an existing compatible cache; no reference counting or global registry is allowed. Document one owner with injected borrowers as the sharing pattern.
- Pass existing `CacheConfiguration` unchanged. Do not add PDX, pool, transaction, native XML, JNDI, readiness, durable keep-alive or BeanFactory locator surfaces to this ticket. Do not change existing SPI method semantics or make the mock simulate distributed state. Use injected driver metadata for any runtime diagnostics; never log property values.
- Replace the test-local connection helper in the existing Java/XML resolver wiring fixtures with the new factory and add cache-manager wiring. Leave `@ClientCacheApplication`, `<gfe:client-cache>` and native consumer factories coherent during staging; no unwrap, native compatibility facade or automatic bean replacement.

## Validation and completion

- Shared Spring-context tests on mock/native runtimes verify creation, same-handle access, borrowed reuse, qualified injection, shutdown/callbacks and a usable borrowed connection after context closure. Check Spring's FactoryBean product destruction behavior rather than assuming it.
- Focused failure tests verify that creation failure is surfaced, borrowed handles are not closed, unopened destruction does nothing and owned cleanup is not repeated after lifecycle completion. No tests should assert greater driver concurrency guarantees than the SPI provides.
- Add a native-only characterization of compatible repeated client connections sharing underlying cache effects and closure; keep mock connection-isolation tests separate from the shared contract. Sequence native contexts appropriately.
- Extend the existing isolated source/test lists and native-free enforcement to the new factory. Run focused mock/native tests, driver checks, full source compilation and `git diff --check`; record the exact resolved native artifact.
- Preserve user Gradle/settings edits. Use the documented temporary init-script approach for Spring Test compilation and session-only Gradle escalation.
- Update this ticket and the implementation map with evidence, and graduate the next precise configuration dependency ticket. This ticket does not claim that the native factory hierarchy has migrated.

## Answer

Implemented 2026-10-21 on `wayfinder/unified_driver`.

### Completed boundary

Added [DriverConnectionFactoryBean](../spring-data-vmware-gemfire/src/main/java/org/springframework/data/gemfire/DriverConnectionFactoryBean.java), implementing Spring `FactoryBean<DriverConnection>`, `InitializingBean` and `DisposableBean` directly. Creation takes the validated manager and immutable configuration; borrowing takes the exact external handle. All dependencies are constructor-injected. The factory introduces no native types, discovery, static state, property logging or additional SPI operations.

The creation path passes the original configuration object to the injected driver. It rejects a null or already-closed returned handle. The `isClosed` check establishes only local handle availability, not distributed-system connectivity. Borrowing does not inspect the handle at all. The factory publishes its product type before initialization without opening a connection.

| Lifecycle action | Behavior |
| --- | --- |
| Construct | Validate dependencies, without connecting. |
| Access before initialization | Throw `IllegalStateException`; no implicit creation. |
| Initialize | Create and check an owned handle, or expose the exact borrowed handle. |
| Initialize/access again after success | Retain the same product; no reconnect. |
| Initialization fails | Propagate the original exception/error; close an acquired owned handle once; suppress cleanup failure on the original. |
| Access/reinitialize after failure | Fail with the original failure as cause; no retry. |
| Destroy before initialization | Mark destroyed without opening anything. |
| Destroy after success | Close an owned handle once; leave a borrowed handle untouched. |
| Close fails | Propagate the failure; lifecycle is still terminal and repeated destruction does not retry closure. |
| Access/reinitialize after destruction | Fail; repeated destruction is a no-op. |

Lifecycle synchronization protects factory state only, without adding concurrency guarantees to the product. Sharing means one owner and injected borrowers. Native compatible connection creation can reuse a cache: separate returned handles do not imply independently closeable caches. This is documented on the factory and characterized in a native-only test. A separate mock-only client test confirms local connection isolation without imposing it on the shared contract.

### Wiring and validation

Replaced the resolver fixtures' test-local connection helper with the actual factory. Java configuration and ordinary bean XML now cover both owning and borrowing contexts, qualified injection among multiple connection candidates, same-handle access, `GemfireCacheManager` reads/writes, resolver behavior and lifecycle callbacks. Borrowed connections remain usable after context shutdown. XML consumers are shared between the owning and borrowing fixtures.

Thirteen focused factory tests cover failures, cleanup and Spring destruction behavior. Recording collaborators establish that Spring destroys the factory, does not independently close its AutoCloseable product, and cleans up a registered owner when downstream product post-processing aborts context refresh. As usual, Spring controls how destruction exceptions are reported during context shutdown; direct factory destruction propagates them.

Extended the isolated source/test lists, XML resources and native-free enforcement to the new boundary. Checks passed against **GemFire 10.4.0-build.3056.df98c835**, with generated/resource driver stamps reporting that exact version and SPI `1.0`:

| Validation | Result |
| --- | --- |
| Focused cache/utility/resolver/connection suite, mock runtime | 133 tests passed |
| Same focused suite, native runtime | 133 tests passed |
| Mock driver checks, including client isolation | 18 tests passed |
| Native driver checks, including compatible client cache reuse | 20 tests passed |
| Unchanged SPI / core / contract-tool checks | Up-to-date; 11 / 24 / 6 passing tests in reports |
| Spring Data main/unit/integration sources and Spring Test main/unit sources | Compilation passed |
| Native-free source/classpath checks and `git diff --check` | Passed |

There were **304 test executions in this continuation**, plus 41 unchanged tests retained as up-to-date; all 345 reported tests have zero failures/errors/skips. This is focused validation, not execution of the entire legacy test suite. An initial wildcard-type mistake in the new fixture was corrected before these successful runs.

Commands used:

```bash
./gradlew -I /private/tmp/sdgf-resolver-validation.init.gradle :spring-data-vmware-gemfire:spiCacheTest :spring-data-vmware-gemfire:spiCacheIntegrationTest -DgemfireVersion=10.4.+ --console=plain
./gradlew -I /private/tmp/sdgf-resolver-validation.init.gradle :spring-data-vmware-gemfire:compileJava :spring-data-vmware-gemfire:compileTestJava :spring-data-vmware-gemfire:compileIntegrationTestJava :spring-test-vmware-gemfire:compileJava :spring-test-vmware-gemfire:compileTestJava :spring-data-gemfire-driver-spi:check :spring-data-gemfire-driver-core:check :spring-data-gemfire-driver-contract-tests:check :spring-data-gemfire-driver-mock:check :spring-data-gemfire-driver-10.4:check -DgemfireVersion=10.4.+ --console=plain
```

Validation used the existing guarded temporary init script and session-only Gradle escalation; no persistent permission rule or edits to the user's Gradle/settings configuration were made. Logs: `/private/tmp/sdgf-connection-focused.log` and `/private/tmp/sdgf-connection-compile-driver-checks.log`.

### Remaining migration

The legacy native factory hierarchy, annotation/XML namespace entry points and their native consumers remain staged. No PDX, pool, transaction, XML loading, JNDI, readiness or durable shutdown behavior was added to this foundation. No native facade or automatic bean replacement was introduced.

Next: [Pool configuration and resolver migration dependencies](12-pool-configuration-and-resolver-dependencies.md), an investigation that selects the next complete pool configuration boundary before expanding the SPI or changing legacy entry points.
