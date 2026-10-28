Type: task
Status: open
Blocked by: 13, 14, 16, 50, 51, 52, 59

# Complete pool, default-cache and durable SPI

## Client-only scope — 2026-10-22

Apply the [approved client-only boundary](../wayfinder-impl/client-only-scope.md). [Cleanup ticket 59](59-remove-server-only-spi-and-reconcile-client-scope.md) must complete first. Full/complete coverage in this ticket means every retained client requirement; historical native reachability alone does not require peer/server APIs. Preserve all retained client tests and native-free boundaries.

## Question

Extend the named pool foundation to the full pool/default-cache configuration and lifecycle required by legacy entry points.

Audit coverage: **G3, G4, G5** in the [native/SPI gap audit](../wayfinder-impl/audits/native-spi-gap-audit.md). Apply the [shared completion requirements](../wayfinder-impl/spi-gap-closure-plan.md#shared-completion-requirements) and the signed-off [technical spec](../wayfinder/technical-spec.md).

## Required implementation

Prerequisites: [Named driver pool creation and ownership](13-named-driver-pool-creation-and-ownership.md); [Required native API coverage matrix](14-required-native-api-coverage-matrix.md); [Driver exception semantics and translation](16-driver-exception-semantics-and-translation.md).

- Cover every pool setting in the existing pool investigation and coverage matrix: connection limits, timeouts, endpoints, routing, subscriptions, authentication flags, statistics, availability-zone support and socket callbacks.
- Add default-cache pool configuration before connection creation and true default-pool lookup; do not equate it with the literal DEFAULT name or gemfirePool bean name.
- Model durable ID/timeout, readiness and keep-alive lifecycle with native semantics and explicit ownership; reject removed/stored-only options rather than silently accepting them.
- Provide typed configuration merging/inspection required to replace native configuration-view PoolAdapters. Leave query-service operations to Query and index SPI and authenticated-view operations to Security and authenticated views SPI.

- Exception foundation follow-up: Use typed connectivity/resource/argument/state failures for pool operations; preserve distinct native errors without classifying by message. See [exception semantics and ownership](../wayfinder-impl/investigations/exception-semantics-and-translation.md).

## Required validation

- Verify all configuration mappings and precedence using native observations, including mixed endpoints, defaults, duplicate names and unsupported removed settings.
- Test default-pool selection/reuse and durable readiness/queue retention against appropriate native client/server fixtures; mock asserts only supported configuration/lifecycle behavior.

## Completion

Complete pool configuration/lifecycle contracts; native resolvers and namespace consumers remain assigned to their cutover owners. Record the Answer with changed source/API rows, commands/results, native version and limitations; update the implementation map. Do not mark dependent work complete from this ticket's result.

## Blocking implementation children

- [Named pool configuration and socket callbacks](50-named-pool-configuration-and-socket-callbacks.md) owns typed settings, merging/inspection, native mappings, removed thread-local handling and socket adaptation.
- [Default client-cache pool assembly and selection](51-default-client-cache-pool-assembly-and-selection.md) owns configuration before connection creation and actual default selection/reuse.
- [Durable client readiness and pool runtime lifecycle](52-durable-client-readiness-and-pool-runtime-lifecycle.md) owns durable identity/timeout, readiness, keep-alive, queue retention, runtime topology and pool registry lifecycle.

The parent remains open, and all existing consumers continue to depend on it. Child completion does not establish full pool or client-cache migration.

## Task 69 implementation handoff — 2026-10-28

The [pool/durable scope audit](../wayfinder-impl/investigations/client-pool-and-durable-scope.md) retains all 101 assigned IDs. **84 partial settings/default/factory rows move here from resolved foundation 50**; their validated SPI/native/mock/test mappings and implementation statuses remain unchanged. Complete effective readback/default constants, omitted versus explicit values, sentinel/validation behavior, reset/reuse and endpoint/duplicate-name semantics, with task 35 consumer adoption. Preserve task 50 socket identity/transport results; reconcile native setter null acceptance with typed builder rejection as an explicit consumer policy, without reopening transport scope experiments.

Tasks 51/52 retain the other 17 assigned requirements. Parent completion needs their selection, durable/runtime and ownership acceptance as well as the 84 rows here; do not close based solely on prior foundation tests. Exact IDs and concrete behavior are in the ledger. No native defaults/factories or static registry may leak through Spring/SPI.

## Final reconciliation handoff — task 64

Own five partial current-SPI pool mappings transferred from foundation 50. Complete default/runtime semantics with 51/52 and Spring adoption with 35; named-pool foundation validation remains valid. See the [final scope ledger](../wayfinder-impl/investigations/client-scope-final-reconciliation.md) for exact counts and source dispositions. These transfers do not upgrade implementation status.
