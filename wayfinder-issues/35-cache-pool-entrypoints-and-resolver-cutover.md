Type: task
Status: open
Blocked by: 20, 29, 30, 31, 32, 33, 34, 59

# Cache/pool entry points and resolver cutover

## Client-only scope — 2026-10-22

Apply the [approved client-only boundary](../wayfinder-impl/client-only-scope.md). [Cleanup ticket 59](59-remove-server-only-spi-and-reconcile-client-scope.md) must complete first. Full/complete coverage in this ticket means every retained client requirement; historical native reachability alone does not require peer/server APIs. Preserve all retained client tests and native-free boundaries.

## Question

Replace the remaining native cache/pool factories, resolvers and public configuration entry points once all native product consumers can follow.

Audit coverage: **G1, G3, G4, G5, G6, G8** in the [native/SPI gap audit](../wayfinder-impl/audits/native-spi-gap-audit.md). Apply the [shared completion requirements](../wayfinder-impl/spi-gap-closure-plan.md#shared-completion-requirements) and the signed-off [technical spec](../wayfinder/technical-spec.md).

## Required implementation

Prerequisites: [Complete pool, default-cache and durable SPI](20-complete-pool-default-cache-and-durable-spi.md); [Complete cache configuration and creation assembly](29-complete-cache-configuration-and-creation-assembly.md); [Region factory and configuration migration](30-region-factory-and-configuration-migration.md); [Template, repository and mapping migration](31-template-repository-and-mapping-migration.md); [Spring function consumer migration](32-spring-function-consumer-migration.md); [Spring continuous-query consumer migration](33-spring-continuous-query-consumer-migration.md); [Spring transaction consumer migration](34-spring-transaction-consumer-migration.md).

- Replace the legacy cache factory hierarchy and PoolFactoryBean products with injected driver ownership/configuration; converge transitional factories into the final API without permanent dual facades.
- Retarget PoolResolver and all five implementations or remove obsolete variants; constructor-inject connection/bean collaborators, preserve null-only fallback and never introduce native unwrap.
- Migrate @ClientCacheApplication, @EnablePool(s), cache/pool/PDX/security/disk configurers, annotation registrars, namespace parsers/schema types and dependency-ordering post-processors together.
- Replace global/static registration guards and native factory initializer hooks; preserve all matrix-listed settings, named/default pool distinctions and resource ownership.
- Handle Spring Test's compile coupling to removed initializer/factory types concretely while keeping that separate module's native implementation outside this migration's scope; assign remaining test-helper usage to the test migration ticket.

- Exception foundation follow-up: Migrate remaining cache-factory native exception dispatch and authentication/pool declarations; use the connection factory/common translator for Spring repository advice. See [exception semantics and ownership](../wayfinder-impl/investigations/exception-semantics-and-translation.md).

## Required validation

- Exercise existing public Java/annotation/namespace entry points through driver-backed templates, regions, functions, CQ and transactions.
- Verify configuration precedence, callback construction, startup errors and dependent shutdown; compile both modules and all Spring source sets.

## Completion

Legacy native cache/pool products and resolver seams are removed or replaced; no public entry point requires native application types. Record the Answer with changed source/API rows, commands/results, native version and limitations; update the implementation map. Do not mark dependent work complete from this ticket's result.

## Permission declaration wiring from task 64

The [57-row permission-value disposition](../wayfinder-impl/investigations/client-permission-values.md#client-permission-value-disposition) belongs to implementation task 24 and primary consumer task 32, with callback/transport in task 25. Preserve their typed function declaration configuration through the entry-point cutover without native/Shiro types, getter/rendering conversion or client-side authorization. Security configuration must not expose server managers merely because declaration enums contain CLUSTER/MANAGE or server target labels. Validate native-free construction and wiring alongside the function consumer tests; secured enforcement remains per-version driver acceptance.


## Query/CQ audit coordination — 2026-10-28

Task 65 retains Pool.getQueryService and client/local/pool/authenticated query routing. Wire task 21/22 collaborators without native casts in Spring or fallback to another service; retain authenticated-view restrictions and client CQ ownership. See [the source-backed disposition](../wayfinder-impl/investigations/client-query-and-cq-scope.md).

## Task 69 consumer handoff — 2026-10-28

All [101 pool/durable IDs](../wayfinder-impl/investigations/client-pool-and-durable-scope.md) are required-client with consumer ownership here. Active implementation owners are 20 (84 unfinished configuration/default/factory rows transferred from resolved 50), 51 (one selection row), and 52 (16 runtime/registry rows). Migrate PoolFactoryBean, pool adapters/resolvers, ClientCacheFactoryBean and annotation/XML wiring with effective/default/explicit-setting distinctions, reset/reuse, default selection, readiness and keepAlive. Preserve null-only fallback and owned/borrowed isolation. Native global lookup/close must remain private; fake remote topology or durable queue results cannot satisfy mock acceptance. Task 50's socket evidence remains valid and is not reopened.

## Task 70 consumer handoff — 2026-10-28

The [104-row security/authentication audit](../wayfinder-impl/investigations/client-security-and-authentication-scope.md) retains **28 client requirements**, implemented by task 24 and adopted here. Migrate AutoConfiguredAuthenticationConfiguration, EnableSecurity and AuthInitialize support classes to typed client credential callbacks, preserving explicit initialization and property mutation. Replace ClientCacheFactoryBean.JndiDataSource's ConfigProperty payload and JNDIInvoker use with typed name/value/type data and injected binding; retain ClientCacheParser's jndi-binding/config-property support and blank-type default. Keep EnableSsl/SslConfiguration restricted to locator/server and their certificate aliases, with native ALL interpretation private to the driver.

Do not reintroduce the 76 excluded server security/operation-context/channel rows. Username/password/token keys remain client vocabulary even though three originate on SecurityManager. Preserve typed authentication/expiry/authorization failures without inspecting native causes. Task 77 already owns security fixture packaging; these scope decisions do not establish native-free production/unit tests or complete the retained driver/mock behavior.

## Serialization audit handoff — 2026-10-28

Task 71 retains shared client PDX getters/metadata registration with implementation owner 54, and excludes the five peer CacheFactory PDX setters. Migrate ClientCacheFactoryBean, PdxConfiguration/EnablePdx and retained client XML wiring through typed client settings. Preserve accepted/read-back persistence/disk-store options while documenting that the pinned client ignores them; do not restore a peer builder or claim client metadata disk persistence. See [the exact source-backed ledger](../wayfinder-impl/investigations/client-serialization-pdx-and-json-scope.md).

## Shared exception handoff — task 76

Adopt typed cache/XML, pool and authentication/authorization failures from 29/52/24. Preserve connection/server-operation wrappers and safe principal/cause semantics; coordinate shared translation 36 and value adaptation 58. See the [exact exception scope ledger](../wayfinder-impl/investigations/client-shared-exception-scope.md). Task 76 Java validation and generated inventory reconciliation passed on 2026-10-29. Existing implementation statuses are not upgraded.

## Final reconciliation handoff — task 64

Own the ClientCacheConfiguration CacheServer.DEFAULT_PORT source use: retain client endpoint parsing/default behavior through a native-free constant. The server receiver type remains excluded; no hosting SPI follows. See the [final scope ledger](../wayfinder-impl/investigations/client-scope-final-reconciliation.md) for exact counts and source dispositions. These transfers do not upgrade implementation status.
