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
