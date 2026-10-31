Type: task
Status: resolved
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

## Answer — resolved 2026-10-31

Parent 20 is resolved; Spring pool adapter adoption and resolver cutover (task 35) and cache assembly (task 29) stay open.

**SPI and mappings.** Children 50, 51 and 52 are resolved. They deliver `PoolConfiguration` with typed endpoints, the `Pool` getters and `PoolFactory` defaults, named-pool and default-pool creation and selection, `DriverSocketFactory`, `getPools`, `getPoolForRegion`, `closePools`, `readyForEvents`, `close(boolean)`, `destroy(boolean)` and `getPendingEventCount`. They map to `PoolManager`, `PoolFactory`, `Pool` and `ClientCache`.

**Native artifact.** GemFire build `10.4.0-build.3068.c3a2bbfe` (the frozen inventory keeps the `3056.df98c835` label).

**Behavior evidence.** `NativePoolLifecycleTests` and the pool and default-pool native tests cover registry, readiness, keep-alive and destroy semantics. New in this ticket, `PoolConfigurationTests.nullSocketFactoryIsRejectedAndOmissionInheritsTheFallback` records the consumer policy: native `setSocketFactory` accepts null, the typed builder rejects it, and omission means unset.

**Coverage.** The 84 partial pool rows moved to task 35 (Spring pool adapter) with consumption by tasks 35 and 29, as did the 14 required-client rows formerly owned by task 52 and the existing-SPI pool mappings. No status was changed and no row was upgraded without row-level evidence.

**Validation (user-run):** `./gradlew :spring-data-gemfire-driver-spi:test --tests '*PoolConfigurationTests' :spring-data-gemfire-driver-mock:test :spring-data-gemfire-driver-10.4:integrationTest --tests '*NativePoolLifecycleTests' --tests '*GemFire104DriverContractTests' --tests '*NativePoolTests' --tests '*NativeDefaultPoolTests' --tests '*NativePoolConfigurationTests' verifyMigrationBoundary verifyDriverCompatibility`
