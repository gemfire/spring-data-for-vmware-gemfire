Type: task
Status: open
Blocked by: 14, 16, 20, 21, 23

# Security and authenticated views SPI

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

## Required validation

- Use native authenticated client/server tests for successful and rejected authentication, user separation and view closure.
- Verify callback argument/lifecycle mapping, credential redaction and explicit unsupported mock paths.

## Completion

Typed security/view contracts and native evidence for the required public security surface. Record the Answer with changed source/API rows, commands/results, native version and limitations; update the implementation map. Do not mark dependent work complete from this ticket's result.
