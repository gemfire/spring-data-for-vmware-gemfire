Type: task
Status: open
Blocked by: 14, 17, 20, 21, 45

# Continuous query SPI

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
