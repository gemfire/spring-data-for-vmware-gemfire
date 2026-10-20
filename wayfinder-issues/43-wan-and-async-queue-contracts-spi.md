Type: task
Status: open
Blocked by: 14, 16, 17, 19

# WAN and asynchronous queue contracts and callbacks SPI

## Question

Close the concrete public API reachability gap discovered by [Required native API coverage matrix](14-required-native-api-coverage-matrix.md).

## Required implementation

Prerequisites: [Required native API coverage matrix](14-required-native-api-coverage-matrix.md); [Driver exception semantics and translation](16-driver-exception-semantics-and-translation.md); [Complete region events, listeners and writers](17-complete-region-events-listeners-and-writers.md); [Region configuration, enums, expiration and eviction](19-region-configuration-enums-expiration-and-eviction.md).

Implement Cache-accessible gateway sender/receiver and asynchronous event queue creation/lookup/lifecycle. Cover all factory settings, event/transport/substitution filters, AsyncEventListener, GatewayEventFilter, routing/order policies and typed event/payload contracts reachable through those APIs. Region configuration references to queue/sender IDs must resolve coherently. The mock must reject remote delivery rather than report successful replication.

Use the [coverage matrix and update procedure](../wayfinder-impl/coverage/README.md) to identify every owned member, including inherited contracts. Follow the [shared completion requirements](../wayfinder-impl/spi-gap-closure-plan.md#shared-completion-requirements) and the [signed-off specification](../wayfinder/technical-spec.md). Existing public exposure through a native Cache is evidence of required capability, even when Spring itself never calls an operation. Do not silently narrow application APIs.

## Required validation

Verify complete typed contracts, native delegation, callback payload/lifecycle and exception mapping. Exercise real native server/cluster behavior in integration tests and honest mock unsupported behavior in shared contract tests. Update every owned matrix row with SPI/native/mock/test evidence and compile affected consumers.

## Completion

All owned API rows implemented and available for runtime-service/cache assembly and residual consumer cutover. If implementation needs splitting, create blocking children and keep this parent open. Record exact native version and validation results in an Answer; update the implementation map. This ticket does not authorize publication or merge.
