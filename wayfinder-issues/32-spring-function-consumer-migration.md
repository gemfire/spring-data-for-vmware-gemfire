Type: task
Status: open
Blocked by: 25, 59

# Spring client function consumer migration

## Client-only scope — 2026-10-22

Apply the [approved client-only boundary](../wayfinder-impl/client-only-scope.md). [Cleanup ticket 59](59-remove-server-only-spi-and-reconcile-client-scope.md) must complete first. Full/complete coverage in this ticket means every retained client requirement; historical native reachability alone does not require peer/server APIs. Preserve all retained client tests and native-free boundaries.

## Question

Retarget client function templates, proxies and configuration to ticket 25's client execution SPI.

## Required implementation

- Migrate retained region/server/server-group execution templates, function proxies, arguments, filters, collectors and return conversion to typed injected pool/connection collaborators.
- Migrate client annotation/XML/configurer wiring and error translation; remove native PoolResolver and static native service lookups.
- Coordinate retirement of peer execution targets and server function registration/hosting configuration with ticket 59. Preserve any function-object submission proven necessary by ticket 25; do not preserve server APIs merely because legacy types expose them.
- Remove native-backed public overloads and update retained client schema declarations and tests. Keep server function bodies private to native driver integration fixtures.

## Required validation

Task 63's [function audit](../wayfinder-impl/investigations/client-only-spring-surfaces.md#client-function-submission-and-fixture-audit--2026-10-27) found that all three `GemfireOnRegionOperations` function-object/filter defaults discard the object and delegate by ID. Repair this during cutover and verify an unregistered stateful object plus filter through iterable, extracted and no-result forms. Preserve `FunctionGemfireAdminTemplate` client region discovery with driver-owned execution and typed results. Replace native execution callbacks and collector/error adaptation without losing their client behavior. Server fixture relocation is task 64's prerequisite responsibility, not deferred to this blocked ticket.

Run Spring client proxy/template/configuration tests with native-free collaborators and real client function integration with each native driver. Verify filters, collectors, timeout/interruption, missing targets and typed no-result/error behavior.

## Completion

All retained Spring client function consumers and tests are native-free. Record changed APIs, removed server surfaces, exact validation and limitations.

## Member-level acceptance from task 64

Use the [function member ledger](../wayfinder-impl/investigations/client-function-member-scope.md) when replacing native execution/collector/error dependencies. Cover target restrictions, typed member/result delivery, completion/retry clearing and aggregated causes in native-free Spring tests. Replace message-based no-result inference with typed metadata/behavior while preserving timeout/interruption translation. Keep all retained real-server suites, including object/filter submission and driver-owned region discovery. Apply task 64's selected representation below; this migration does not introduce a public server registry.

The five native registry probes and three object-submission tests passed in user-run validation on 2026-10-28. Missing registration produces native `FunctionException` for region execution and `ServerOperationException` for pool/cache server targets. Spring consumers must use task 25's typed failure mapping for both paths and preserve function identity/cause diagnostics; native exception classes must not enter the Spring contract.

The [client-local registry decision](../wayfinder-impl/investigations/client-function-member-scope.md#client-local-registry-disposition) retains five client operations through task 25's injected typed collaborator. Preserve local object lookup/metadata behavior where used by retained clients; do not reintroduce server registration annotations/XML or static service calls. Local registration is neither remote function installation nor an authorization/existence check. The native-free Spring suite must distinguish ID submission from retained-object submission and use task 25's typed failures.

The [validated client-local callback decision](../wayfinder-impl/investigations/client-function-member-scope.md#client-local-callback-disposition) retains 23 body/context/sender rows. Preserve client-local stateful callback submission and typed borrowed cache/dataset access during template/proxy cutover, including argument/filter/metadata and terminal-result behavior. Exercise these paths with native-free injected collaborators and each native driver's supported local regions. Do not expose server cache contexts, invent mock persistence support, or replace local object submission with ID lookup. Apply the selected remote/local context distinction below; both permission hooks are retained through native-free declarations.

The [service assembly mapping](../wayfinder-impl/investigations/client-function-member-scope.md#client-function-service-assembly) replaces native constructor/internal-service reachability with an injected typed client execution collaborator. Verify template/proxy wiring with native-free substitutes; retain no native service type, subclass or static service locator in Spring.

Preserve task 25's [typed permission declarations](../wayfinder-impl/investigations/client-function-member-scope.md#client-function-permission-disposition) during submitted-object cutover, including dynamic region/argument behavior. Spring must neither evaluate remote authorization nor import native/Shiro permission types. Verify typed authorization failure propagation and ID-versus-object submission with native-free collaborators; tasks 24/25 own secured per-version enforcement tests.

The [57 permission-value rows](../wayfinder-impl/investigations/client-permission-values.md#client-permission-value-disposition) now name this ticket as primary consumer owner; task 24 owns implementation and task 25 owns callback/transport. Adopt typed construction, effective-part editing and comparison where exposed by function configuration, preserving callback-time state and typed authorization failures. Do not serialize through component getters or rendering. Verify retained Spring function tests with the mock/native-free collaborators; per-version secured and wire evidence stays with tasks 24/25. Coordinate configuration entry points with task 35; retaining permission labels does not add server security management.

Task 64's [remote envelope experiment](../wayfinder-impl/investigations/client-function-submission-representation.md) passed user-run validation with all 15 envelope/submission/registry/permission tests and transitional boundary verification. Keep Spring consumers independent of envelope classes and native transport packaging. During cutover, distinguish submitted stateful declarations from server-registered IDs, preserve native-free local orchestration tests, and route region discovery through a typed driver operation. A remote context must not masquerade as a borrowed client cache. The subsequent representation decision below settles the semantic boundary; implementation remains outstanding.

The subsequent deployment negative control passed alongside the 15 regression tests in the user-run command; local XML confirms 16 tests with zero failures/errors/skips. Consumer acceptance must preserve typed deployment failures and must not retry a failed object submission as ID execution, even when that ID exists remotely. Keep server class deployment outside Spring's client API and dependencies; task 25 owns native packaging/serialization compatibility tests.


## Selected representation and consumer acceptance

Apply the [representation decision](../wayfinder-impl/investigations/client-function-submission-representation.md#representation-decision--2026-10-28) through task 25's native-free contracts. Keep the private transport artifact out of Spring dependencies. Preserve separate object and ID paths, stateful object/filter submission in all three `GemfireOnRegionOperations` default forms, typed deployment failures without ID fallback, and local-only borrowed cache/dataset contexts.

Migrate both `FunctionGemfireAdminTemplate.getAvailableServerRegions` and `GemfireDataSourcePostProcessor.regionNames` to injected typed server-region-name discovery. Coordinate cache wiring with task 35. Preserve multi-server aggregation, proxy creation and existing-name handling. Both legacy callers swallow discovery failures; reconcile that behavior explicitly with the spec's fail-fast rule. Test successful empty discovery separately from typed failures, and do not embed silent failure in the driver contract.

Keep native server-data fixtures (`OneArgFunction`, `TwoArgFunction`, `GetMapWithNoArgsFunction`) and native PDX field functions separately deployed and invoked by ID, preserving existing real-server assertions. Migration notes must explain that application bodies using native server caches/services remain separately built server functions; neutral submitted callbacks expose metadata and values, with borrowed client handles only for local execution. No server cache programming model is added to the client SPI.

Verify native-free Spring template/proxy/configuration orchestration for object/ID separation, local context access, discovery and failure propagation. Tasks 25/40 own isolated production transport packaging and secured per-version integration. The 16 validated native probes support the decision, not completion of this migration; existing blockers remain.

## Task 73 client-scope handoff — 2026-10-28

The [task-73 ledger](../wayfinder-impl/investigations/client-statistics-logging-and-utility-scope.md) transfers nine missing consumer IDs here, paired with implementation 25: five function identity declarations and four region-discovery declarations. Adopt typed function metadata/ordinary Java helpers and migrate FunctionGemfireAdminTemplate and GemfireDataSourcePostProcessor to typed region names. Preserve aggregation across servers, empty-versus-failure handling and explicit root/subregion/duplicate behavior in consumer tests. Keep native GetRegionsFunction, RegionInformation, Identifiable and Object[] transport out of Spring-facing contracts. These owner transfers do not complete implementation or adoption.
