Type: task
Status: open
Blocked by: 14, 16, 18, 20, 23, 24

# Function registration, execution and results SPI

## Question

Represent the complete required function registration and execution surface, including application callbacks and results.

Audit coverage: **G3, G4** in the [native/SPI gap audit](../wayfinder-impl/audits/native-spi-gap-audit.md). Apply the [shared completion requirements](../wayfinder-impl/spi-gap-closure-plan.md#shared-completion-requirements) and the signed-off [technical spec](../wayfinder/technical-spec.md).

## Required implementation

Prerequisites: [Required native API coverage matrix](14-required-native-api-coverage-matrix.md); [Driver exception semantics and translation](16-driver-exception-semantics-and-translation.md); [Complete region operations and metadata SPI](18-complete-region-operations-and-metadata-spi.md); [Complete pool, default-cache and durable SPI](20-complete-pool-default-cache-and-durable-spi.md); [Distributed-system, membership and runtime services SPI](23-distributed-system-membership-and-runtime-services-spi.md); [Security and authenticated views SPI](24-security-and-authenticated-views-spi.md).

- Add typed function definition/context/region-context, registration/unregistration and execution targets for regions, servers, members and groups.
- Implement argument/filter, result sender/collector, timeout, exception/result and required execution-option semantics.
- Support application-supplied functions/collectors through SPI types, including native context/member/region values reachable from their signatures.
- Delegate native operations without strengthening ordering/concurrency guarantees; document local mock execution and reject unavailable distribution/retry semantics.

- Exception foundation follow-up: Preserve native function exception collections through typed SPI payloads and result metadata; expose no-result state without requiring Spring to parse exception messages. See [exception semantics and ownership](../wayfinder-impl/investigations/exception-semantics-and-translation.md).

## Required validation

- Test native registration and each required target kind, result collection, timeout/errors, arguments/filters and callback payloads.
- Share local function/collector behavior where supported; require explicit unsupported outcomes for unavailable mock targets.

## Completion

Complete function contract rows; Spring templates/proxies/registration wiring migrate in their dedicated ticket. Record the Answer with changed source/API rows, commands/results, native version and limitations; update the implementation map. Do not mark dependent work complete from this ticket's result.
