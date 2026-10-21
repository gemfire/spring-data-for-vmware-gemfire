Type: task
Status: open
Blocked by: 17, 19, 20, 23, 24, 26, 27, 28

# Complete cache configuration and creation assembly

## Question

Assemble the typed cache configuration and lifecycle needed to replace the legacy cache factory hierarchy.

Audit coverage: **G3, G4, G5, G6** in the [native/SPI gap audit](../wayfinder-impl/audits/native-spi-gap-audit.md). Apply the [shared completion requirements](../wayfinder-impl/spi-gap-closure-plan.md#shared-completion-requirements) and the signed-off [technical spec](../wayfinder/technical-spec.md).

## Required implementation

Prerequisites: [Complete region events, listeners and writers](17-complete-region-events-listeners-and-writers.md); [Region configuration, enums, expiration and eviction](19-region-configuration-enums-expiration-and-eviction.md); [Complete pool, default-cache and durable SPI](20-complete-pool-default-cache-and-durable-spi.md); [Distributed-system, membership and runtime services SPI](23-distributed-system-membership-and-runtime-services-spi.md); [Security and authenticated views SPI](24-security-and-authenticated-views-spi.md); [PDX, serialization and JSON SPI](26-pdx-serialization-and-json-spi.md); [Disk-store and resource configuration SPI](27-disk-store-and-resource-configuration-spi.md); [Transaction lifecycle and callbacks SPI](28-transaction-lifecycle-and-callbacks-spi.md).

- Extend connection creation/configuration to cover all required legacy properties, PDX, pool, transaction, resource, XML and JNDI inputs using typed collaborators.
- Preserve explicit/default/configurer precedence and initialization ordering; support callbacks through injection rather than GemfireBeanFactoryLocator.
- Handle cache XML stream ownership, callback construction and native resource cleanup on post-create failures. Distinguish creation, native reuse and explicit borrowing.
- Complete DriverConnectionFactoryBean integration for this configuration while preserving its existing ownership contract. Document peer/client restrictions and typed unsupported settings.

- Preserve Cache-exposed server, WAN and asynchronous queue entry points using the completed runtime-service children. Assemble the typed contracts from the [coverage matrix](../wayfinder-impl/coverage/README.md); connection creation alone is not a full Cache replacement.

- Exception foundation follow-up: Preserve cache-exists failure payload as an SPI cache/connection handle and retain cancellation/bind semantics. See [exception semantics and ownership](../wayfinder-impl/investigations/exception-semantics-and-translation.md).

- Callback foundation follow-up: DriverDeclarable preserves distinct init(Properties) and initialize(DriverConnection, Properties) hooks; registration is deliberately not automatic initialization. Assemble injected callbacks with explicit initialization in native-equivalent order and preserve borrowed connection ownership. Client creation currently accepts listeners only; native loader/writer setters remain post-creation operations. See [callback evidence](../wayfinder-impl/investigations/region-callback-contracts.md).

## Required validation

- Exercise complete Java/ordinary XML connection configurations with native and mock where supported, including precedence, borrowed handles and refresh failure cleanup.
- Use native fixtures for PDX/disk/default-pool/durable/resource behavior and stream/callback cleanup; no accepted-but-ignored settings.

## Completion

Connection/configuration foundation can express every required legacy cache factory setting without native public types. Record the Answer with changed source/API rows, commands/results, native version and limitations; update the implementation map. Do not mark dependent work complete from this ticket's result.
