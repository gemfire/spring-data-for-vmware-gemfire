Type: task
Status: resolved
Assignee: Codex (resumed 2026-10-21)
Blocked by: 09

## Question

What is the smallest complete migration boundary for injected cache creation and ownership, starting with `AbstractBasicCacheFactoryBean`, `AbstractConfigurableCacheFactoryBean`, `AbstractResolvableCacheFactoryBean` and `ClientCacheFactoryBean`?

The resolver interfaces now accept explicitly registered driver handles. The existing factory hierarchy and its annotation/XML callers still produce native caches. Before changing those factories, trace the configuration and lifecycle contracts that a driver connection must support and determine which callers must migrate together.

## Investigation scope

- Trace the four factory classes, subclasses, direct consumers, annotation configuration, XML parsers, and Spring Test fixtures. Record source/resource references and distinguish callers from similarly named methods.
- Characterize creation versus reuse, native global cache/distributed-system lookups, property merging, initialization callbacks, destruction ownership and failure behavior. Identify where constructor-injected collaborators can replace global access without introducing an unwrap or compatibility facade.
- Inventory native configuration types and operations (including PDX, cache XML, resource/transaction settings and callbacks). Compare each with the existing SPI and distinguish operations needed for a complete first slice from operations whose consumers can remain native temporarily.
- Revisit the utility/root inventory's cache/distributed-system dependencies, including durable-client properties and runtime version diagnostics. Keep pool, region-factory, query and function dependencies visible without assuming they can all be migrated in one ticket.
- Establish an executable validation approach for explicit connection creation, reuse and closure on the mock and reference driver. Unsupported distributed behavior must remain typed unsupported; do not invent mock guarantees or infer connected state from closure alone.

## Completion

Write a dependency/behavior inventory under `wayfinder-impl/investigations/`, record the chosen boundary and any decisions still requiring a live user exchange, and create the next precise implementation or decision ticket. Update the implementation map. This investigation does not implement factory changes or reopen the signed-off architecture.

The existing user edits to `gradle.properties` and `settings.gradle.kts` remain outside this ticket. The resolver migration used a temporary Gradle init script to include the existing Spring Test module during validation; document any equivalent validation setup rather than silently reverting that settings edit.

## Answer

Completed the [ownership/configuration dependency inventory](../wayfinder-impl/investigations/cache-factory-ownership-and-configuration.md) and [source/resource reference index](../wayfinder-impl/cache-factory-references.tsv), covering 586 lexical references in 288 files. The inventory separates actual inheritance/call edges from generic bounds, imports and same-named methods; records creation/reuse, property precedence, callbacks, cleanup and native configuration gaps; and identifies annotation/XML, native product consumers and Spring Test coupling.

The next complete boundary is a constructor-injected Spring connection owner/borrower using existing SPI/core contracts. Legacy factory entry points stay native while their PDX/pool/transaction/XML consumers migrate. This provides ownership to already migrated cache/resolver consumers without a native unwrap or changing the product type underneath native clients. The old hierarchy remains scheduled for removal at cutover.

Native bytecode from `10.4.0-build.3056.df98c835` confirms that compatible client creation can reuse the underlying cache. The new owner's shutdown responsibility must not be described as exclusive creation. The official mock's independent connections remain intentional; native reuse characterization and shared lifecycle tests are specified separately.

No architectural sign-off or additional live decision is needed for this foundation. No factory code or user Gradle settings changed. Validation was source/reference inspection, native bytecode inspection and document/index checks; no Gradle tests were rerun for this documentation-only investigation.

Created [Injected driver connection ownership](11-injected-driver-connection-ownership.md) as the next implementation ticket, with concrete lifecycle, Java/XML wiring, failure and mock/native validation criteria.
