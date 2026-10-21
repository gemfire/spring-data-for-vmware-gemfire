Type: task
Status: open
Blocked by: 25

# Spring function consumer migration

## Question

Retarget Spring function templates, proxies, registration and configuration to the function SPI.

Audit coverage: **G3, G4, G5, G6** in the [native/SPI gap audit](../wayfinder-impl/audits/native-spi-gap-audit.md). Apply the [shared completion requirements](../wayfinder-impl/spi-gap-closure-plan.md#shared-completion-requirements) and the signed-off [technical spec](../wayfinder/technical-spec.md).

## Required implementation

Prerequisites: [Function registration, execution and results SPI](25-function-registration-execution-and-results-spi.md).

- Migrate FunctionServiceFactoryBean, all execution target classes/templates, function proxies and callback interfaces to typed driver contracts.
- Retarget annotation/XML/configurer wiring, application functions/result collectors and member/server/region target resolution.
- Remove native PoolResolver and static FunctionService/cache/member lookup dependencies; use injected pool/connection/member services.
- Update error translation, schema declarations and all affected test fixtures without native-backed public overloads.

- Exception foundation follow-up: Migrate native function exception subclasses, result-collection errors and timeout/interruption handling; replace the existing no-result message-substring workaround with typed metadata. See [exception semantics and ownership](../wayfinder-impl/investigations/exception-semantics-and-translation.md).

## Required validation

- Run actual Spring proxy/template/registration scenarios on mock/native supported paths and native distributed target fixtures.
- Verify configured filters/arguments/collectors, return conversion, timeout/error propagation and missing target failures.

## Completion

Spring function consumer group and its tests/configuration no longer require native APIs. Record the Answer with changed source/API rows, commands/results, native version and limitations; update the implementation map. Do not mark dependent work complete from this ticket's result.
