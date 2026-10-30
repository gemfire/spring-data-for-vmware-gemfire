Type: task
Status: open
Blocked by: 14, 16, 20, 21, 23, 59

# Security and authenticated views SPI

## Configuration acceptance transferred from task 23 — 2026-10-30

Own the 41 partial ConfigurationProperties rows listed in the [exact handoff ledger](../wayfinder-impl/investigations/client-runtime-closure.md#configuration-row-handoff): 36 client TLS properties and five authentication properties. Existing SPI names, native forwarding, snapshot/redaction tests and version evidence remain intact; this is not completed security behavior.

Validate locator/server TLS selection, legacy CLUSTER_SSL/SERVER_SSL versus SSL_ENABLED_COMPONENTS precedence, supported aliases and channel constraints. Cover accepted and rejected configuration, credential callback lifecycle/security properties, security log routing and safe failure diagnostics. Coordinate typed assembly with task 29 and Spring entrypoints with task 35. The mock must expose its local orchestration limits explicitly rather than claim remote authentication/TLS parity. Excluded server/admin channels remain excluded.


## Client-only scope — 2026-10-22

Apply the [approved client-only boundary](../wayfinder-impl/client-only-scope.md). [Cleanup ticket 59](59-remove-server-only-spi-and-reconcile-client-scope.md) must complete first. Full/complete coverage in this ticket means every retained client requirement; historical native reachability alone does not require peer/server APIs. Preserve all retained client tests and native-free boundaries.

## Question

Support security callbacks and authenticated client views without native security/member types in Spring Data.

Audit coverage: **G3, G4** in the [native/SPI gap audit](../wayfinder-impl/audits/native-spi-gap-audit.md). Apply the [shared completion requirements](../wayfinder-impl/spi-gap-closure-plan.md#shared-completion-requirements) and the signed-off [technical spec](../wayfinder/technical-spec.md).

## Required implementation

Prerequisites: [Required native API coverage matrix](14-required-native-api-coverage-matrix.md); [Driver exception semantics and translation](16-driver-exception-semantics-and-translation.md); [Complete pool, default-cache and durable SPI](20-complete-pool-default-cache-and-durable-spi.md); [Query, results and index SPI](21-query-results-and-index-spi.md); [Distributed-system, membership and runtime services SPI](23-distributed-system-membership-and-runtime-services-spi.md).

- Model AuthInitialize and required initialization/credential/member context using typed injected callbacks; keep sensitive values out of diagnostics.
- Implement authenticated-view creation, region/query access and ownership/close semantics, including multiuser pool constraints.
- Adapt native security failures to the agreed SPI classifications and preserve callback lifecycle; socket factory adaptation is owned by the pool ticket.
- Specify honest mock support for local credential/configuration plumbing and reject authentication/network guarantees it cannot enforce. Public authentication configuration adoption belongs to cache/configuration cutover.

- Replace the public `ClientCacheFactoryBean.JndiDataSource` generic `List<ConfigProperty>` payload with typed native-free name/type/value configuration. Both constructors and all six accessors are in the [coverage matrix](../wayfinder-impl/coverage/README.md); treating this internal class as opaque would lose a public configuration contract.

- Exception foundation follow-up: Preserve authentication failure/expiry versus authorization distinctions and typed principal/security payloads. Cache binding retains its separate bind-failure contract; do not inspect native causes in Spring. See [exception semantics and ownership](../wayfinder-impl/investigations/exception-semantics-and-translation.md).

## Required validation

- Use native authenticated client/server tests for successful and rejected authentication, user separation and view closure.
- Verify callback argument/lifecycle mapping, credential redaction and explicit unsupported mock paths.

## Completion

Typed security/view contracts and native evidence for the required public security surface. Record the Answer with changed source/API rows, commands/results, native version and limitations; update the implementation map. Do not mark dependent work complete from this ticket's result.

Configuration scope correction: task 70 classifies all 47 historical OperationContext/OperationCode rows as excluded server authorization hooks; see its exact-ID ledger below. Retain client credential initialization, authenticated views and authorization failures returned to clients. ClientCacheFactoryBean and ClientCacheParser establish retained JNDI configuration; do not add server security management.

## Function permission value and authorization acceptance from task 64

The two [client submission permission hooks](../wayfinder-impl/investigations/client-function-member-scope.md#client-function-permission-disposition) are required-client; task 25 owns their callback implementation. Define native-free permission declaration values and versioned conversion preserving resource/operation/target/key restrictions, wildcard/subpart forms and validation. Implement the 57 required-client `ResourcePermission`/enum rows under the [member disposition](../wayfinder-impl/investigations/client-permission-values.md#client-permission-value-disposition): its wildcard-string constructor initializes inherited parts directly, so copying its four getters is not a proven lossless conversion. No Shiro/native permission or server security-manager API belongs in Spring or the SPI.

Coordinate secured per-version tests with task 25: allowed and denied execution, denial before body effects, conjunctive multiple permissions, region-specific/wildcard restrictions, authenticated views with separate users, and server-registered ID versus submitted-object declarations. Preserve typed authorization failures independently of authentication failures. Local registry metadata must not imply permission or remote existence. Existing unsecured permission probes establish dispatch only; no authorization acceptance is complete. Mock support remains limited to local configuration/orchestration with explicit rejection of unsupported remote authentication.

### Permission value compatibility review — probes validated

The [57-row member ledger and native probes](../wayfinder-impl/investigations/client-permission-values.md) now record component/wildcard representation divergence, subpart equality versus implication, inherited mutable aliases and native Java serialization. All seven private value tests and three permission-hook tests pass with zero failures/errors/skips; the user also confirmed transitional boundary verification passed on 2026-10-28. Do not reconstruct wildcard declarations from component getters or `toString()`, equate implication with equality, or assume inherited parts are immutable. Task 64 now classifies all 57 value-family rows as required-client through typed equivalents; their implementation remains missing. These probes do not complete typed conversion or secured authorization acceptance.

### Typed permission value acceptance from task 64

Implement the [57-row disposition and five inherited-member mappings](../wayfinder-impl/investigations/client-permission-values.md#inherited-member-dispositions). Preserve component observations separately from effective wildcard parts; typed declaration editing retains case modes and local list/nested-set alias effects. Deep-copy parts when explicitly freezing or transporting a value. Capture each dynamic callback result afresh; never cache policy or normalize it through getters/rendering. Retain wildcard comparisons in the typed domain without importing Shiro's interface or supporting arbitrary native subclasses.

Add native-free shared tests and per-version conversion tests for all ten construction forms, all enum/constants/aliases, malformed input, locale/case modes, independent parts replacement, alias mutation and snapshot isolation, equality/hash versus implication, null/unrelated comparison and serialization. Keep CLUSTER/MANAGE/target labels as declaration vocabulary only. Task 25 owns callback and actual submitted-object wire tests; task 32 owns function adoption, with task 35 handling entry-point wiring. The existing seven native probes do not complete this acceptance matrix.

## Task 70 implementation handoff — 2026-10-28

The [security/authentication ledger](../wayfinder-impl/investigations/client-security-and-authentication-scope.md) assigns **28 missing client requirements** here, with consumer owner 35: ten AuthInitialize rows, three credential-key fields declared by SecurityManager, eleven JNDI/configuration rows and four client TLS channel rows. Preserve credential property mutation, callback initialization/close order and failure translation using native-free contexts. Deprecated no-arg init is not product-invoked, and the deprecated one-argument credential method delegates with a peer flag; document a client-only replacement while the real adapter uses the client callback path. Do not add peer-mode dispatch or native logger/member/cache payloads.

Provide native-free username/password/token keys without exposing SecurityManager. Replace ConfigProperty with typed name/value/type data preserving constructor order and client XML defaults, and wrap datasource binding/validation/failure/ownership behind an injected capability. Keep TLS selection restricted to locator/server; native ALL interpretation does not restore server/admin channel choices. The audit records exact source semantics and IDs; native/mock/shared compatibility acceptance remains open.

The other **76 rows are excluded**: AccessControl/OperationContext/OperationCode, Authenticator, SecurityManager hosting, PostProcessor and non-client TLS selectors. These are not missing implementation obligations. Private server authentication fixtures are permitted; existing TestSecurityManager relocation remains task 77. Mock supports local credential/configuration orchestration with explicit unsupported remote guarantees. Existing authenticated-view and permission-value acceptance above remains required and unchanged.

## Shared exception handoff — task 76

Complete authentication/authorization cause and nullable principal payloads, naming resolved-object serialization and restoration on failure. Keep diagnostics credential-safe; coordinate consumer 35 and value adaptation 58. See the [exact exception scope ledger](../wayfinder-impl/investigations/client-shared-exception-scope.md). Task 76 Java validation and generated inventory reconciliation passed on 2026-10-29. Existing implementation statuses are not upgraded.
