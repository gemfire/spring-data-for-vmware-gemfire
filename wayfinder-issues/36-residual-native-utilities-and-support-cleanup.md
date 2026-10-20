Type: task
Status: open
Blocked by: 30, 31, 32, 33, 34, 35

# Residual native utilities and support cleanup

## Question

Eliminate remaining native production references and obsolete migration helpers after consumer cutover.

Audit coverage: **G3, G5, G6** in the [native/SPI gap audit](../wayfinder-impl/audits/native-spi-gap-audit.md). Apply the [shared completion requirements](../wayfinder-impl/spi-gap-closure-plan.md#shared-completion-requirements) and the signed-off [technical spec](../wayfinder/technical-spec.md).

## Required implementation

Prerequisites: [Region factory and configuration migration](30-region-factory-and-configuration-migration.md); [Template, repository and mapping migration](31-template-repository-and-mapping-migration.md); [Spring function consumer migration](32-spring-function-consumer-migration.md); [Spring continuous-query consumer migration](33-spring-continuous-query-consumer-migration.md); [Spring transaction consumer migration](34-spring-transaction-consumer-migration.md); [Cache/pool entry points and resolver cutover](35-cache-pool-entrypoints-and-resolver-cutover.md).

- Use the full reference/coverage inventories to migrate remaining util/root/support/domain/server/schema/converter/configuration callers, not only files in the former allowlist.
- Remove remaining GemfireCacheUtils native dispatch, runtime native reflection/version branches and BeanFactory locator/global-discovery dependencies; route through injected SPI collaborators.
- Finish annotation defaults, native-backed wrapper enums, resource/schema exports and initializer leftovers. Preserve requirements via matrix rows rather than deleting APIs solely to reach zero imports.
- Delete temporary native compatibility code and redundant staging factories after all callers have moved; investigate every residual row and assign blocking children if work exceeds this ticket.

## Required validation

- Regenerate the whole-module audit and verify zero required native production code/type contracts remain, including reflective class strings.
- Run consumer regression checks and production compilation; inspect public signatures and resources for hidden native escapes.

## Completion

Production native-reference inventory is zero under the final boundary policy; every required API row has an implementation owner and evidence. Record the Answer with changed source/API rows, commands/results, native version and limitations; update the implementation map. Do not mark dependent work complete from this ticket's result.
