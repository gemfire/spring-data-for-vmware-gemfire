Type: task
Status: open
Blocked by: 14, 17, 20, 21, 45, 59

# Continuous query SPI

## Client-only scope — 2026-10-22

Apply the [approved client-only boundary](../wayfinder-impl/client-only-scope.md). [Cleanup ticket 59](59-remove-server-only-spi-and-reconcile-client-scope.md) must complete first. Full/complete coverage in this ticket means every retained client requirement; historical native reachability alone does not require peer/server APIs. Preserve all retained client tests and native-free boundaries.

## Question

Represent CQ definitions, events, listener callbacks and execution lifecycle through the driver.

Audit coverage: **G3, G4** in the [native/SPI gap audit](../wayfinder-impl/audits/native-spi-gap-audit.md). Apply the [shared completion requirements](../wayfinder-impl/spi-gap-closure-plan.md#shared-completion-requirements) and the signed-off [technical spec](../wayfinder/technical-spec.md).

## Required implementation

Prerequisites: [Required native API coverage matrix](14-required-native-api-coverage-matrix.md); [Complete region events, listeners and writers](17-complete-region-events-listeners-and-writers.md); [Complete pool, default-cache and durable SPI](20-complete-pool-default-cache-and-durable-spi.md); [Query, results and index SPI](21-query-results-and-index-spi.md).

- Add typed CQ query/attributes/listener/event contracts, initial results and required execute/stop/close/durable inspection operations.
- Preserve query/operation/key/new-value/error context and all required callback forms; wire through the appropriate pool/query service.
- Adapt native registration and cleanup, with startup failures propagated rather than fallback to a different service.
- Define mock behavior explicitly; unsupported streaming/distributed behavior must fail on registration or invocation rather than silently install a listener.

- Include `CqStatusListener` connection/disconnection callbacks and inherited declarative initialization even though Spring never invokes these application callbacks directly; see the [coverage matrix](../wayfinder-impl/coverage/README.md).

- Exception foundation follow-up: Preserve CQ closed/existing/query distinctions and listener error payloads with the common failure mapper. See [exception semantics and ownership](../wayfinder-impl/investigations/exception-semantics-and-translation.md).

## Required validation

- Use native client/server fixtures for initial results, event delivery, listener errors and CQ start/stop/close behavior.
- Verify typed unsupported paths and common local metadata/ownership behavior without asserting fake remote event delivery.

## Completion

Complete required CQ contracts ready for Spring listener-container migration. Record the Answer with changed source/API rows, commands/results, native version and limitations; update the implementation map. Do not mark dependent work complete from this ticket's result.

Region-operation follow-up prerequisites: [Region interest registration and subscription SPI](45-region-interest-registration-and-subscription-spi.md). These preserve the split snapshot/subscription requirements before consumer or cache cutover.

## Query result scope handoff — 2026-10-28

Reuse the typed query result model for CQ initial results, including key/value structs, collection metadata and inherited collection behavior. This handoff does not settle the remaining CQ lifecycle/event scope. Use the [84-row source-backed ledger](../wayfinder-impl/investigations/client-query-result-scope.md); these rows are required-client, not implemented. Adapter/native/mock behavior tests remain implementation acceptance. Task 65 has now settled the six inherited metadata serialization rows and the remaining query/index/CQ scope; use the final handoff below. Do not export DataSerializable.


## Completed query/CQ audit handoff — 2026-10-28

Implement retained CQ creation, pool routing, durable and state inspection, initial results, bulk/per-region lifecycle, query/service statistics and event filtering. Preserve listener defaults, identity/order/alias and close semantics, distinct event operations, delta-to-full-value recovery, onError and status callbacks. Inherited init/initialize use typed client ownership and are not automatically called by programmatic registration. Coordinate QueryService entry points with task 21 and authenticated-view restrictions with task 35. Native delivery and explicit mock unsupported behavior remain acceptance. See the [final 177-ID disposition](../wayfinder-impl/investigations/client-query-and-cq-scope.md), which completes all 261 task-65 IDs.

## Shared exception handoff — task 76

Complete CQ closed/existing/operation failure construction, conversion and lifecycle emission, coordinating consumer 33. A generic CQ kind does not establish every inherited contract. See the [exact exception scope ledger](../wayfinder-impl/investigations/client-shared-exception-scope.md). Task 76 Java validation and generated inventory reconciliation passed on 2026-10-29. Existing implementation statuses are not upgraded.
