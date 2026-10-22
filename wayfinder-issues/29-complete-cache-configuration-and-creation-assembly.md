Type: task
Status: open
Blocked by: 17, 19, 20, 23, 24, 26, 27, 28, 44, 45, 59

# Complete cache configuration and creation assembly

## Client-only scope — 2026-10-22

Apply the [approved client-only boundary](../wayfinder-impl/client-only-scope.md). [Cleanup ticket 59](59-remove-server-only-spi-and-reconcile-client-scope.md) must complete first. Full/complete coverage in this ticket means every retained client requirement; historical native reachability alone does not require peer/server APIs. Preserve all retained client tests and native-free boundaries.

## Question

Assemble the typed cache configuration and lifecycle needed to replace the legacy cache factory hierarchy.

Audit coverage: **G3, G4, G5, G6** in the [native/SPI gap audit](../wayfinder-impl/audits/native-spi-gap-audit.md). Apply the [shared completion requirements](../wayfinder-impl/spi-gap-closure-plan.md#shared-completion-requirements) and the signed-off [technical spec](../wayfinder/technical-spec.md).

## Required implementation

Prerequisites: [Complete region events, listeners and writers](17-complete-region-events-listeners-and-writers.md); [Region configuration, enums, expiration and eviction](19-region-configuration-enums-expiration-and-eviction.md); [Complete pool, default-cache and durable SPI](20-complete-pool-default-cache-and-durable-spi.md); [Distributed-system, membership and runtime services SPI](23-distributed-system-membership-and-runtime-services-spi.md); [Security and authenticated views SPI](24-security-and-authenticated-views-spi.md); [PDX, serialization and JSON SPI](26-pdx-serialization-and-json-spi.md); [Disk-store and resource configuration SPI](27-disk-store-and-resource-configuration-spi.md); [Transaction lifecycle and callbacks SPI](28-transaction-lifecycle-and-callbacks-spi.md).

- Extend connection creation/configuration to cover all required legacy properties, PDX, pool, transaction, resource, XML and JNDI inputs using typed collaborators.
- Preserve explicit/default/configurer precedence and initialization ordering; support callbacks through injection rather than GemfireBeanFactoryLocator.
- Handle cache XML stream ownership, callback construction and native resource cleanup on post-create failures. Distinguish creation, native reuse and explicit borrowing.
- Complete DriverConnectionFactoryBean integration for this configuration while preserving its existing ownership contract. Support client cache creation and typed unsupported client settings; peer creation is excluded.

- Assemble retained client contracts only. Ticket 59 removes server, WAN and asynchronous queue entry points, including server-only XML/configuration. Do not accept server settings silently.

- Exception foundation follow-up: Preserve cache-exists failure payload as an SPI cache/connection handle and retain cancellation/bind semantics. See [exception semantics and ownership](../wayfinder-impl/investigations/exception-semantics-and-translation.md).

- Callback foundation follow-up: DriverDeclarable preserves distinct init(Properties) and initialize(DriverConnection, Properties) hooks; registration is deliberately not automatic initialization. Assemble injected callbacks with explicit initialization in native-equivalent order and preserve borrowed connection ownership. Client creation currently accepts listeners only; native loader/writer setters remain post-creation operations. See [callback evidence](../wayfinder-impl/investigations/region-callback-contracts.md).

## Required validation

- Exercise complete Java/ordinary XML connection configurations with native and mock where supported, including precedence, borrowed handles and refresh failure cleanup.
- Use native fixtures for PDX/disk/default-pool/durable/resource behavior and stream/callback cleanup; no accepted-but-ignored settings.

## Completion

Connection/configuration foundation can express every retained client cache factory setting without native public types. Record the Answer with changed source/API rows, commands/results, native version and limitations; update the implementation map. Do not mark dependent work complete from this ticket's result.

Region-operation follow-up prerequisites: [Region and cache snapshot services SPI](44-region-and-cache-snapshot-services-spi.md); [Region interest registration and subscription SPI](45-region-interest-registration-and-subscription-spi.md). These preserve the split snapshot/subscription requirements before consumer or cache cutover.

Region-operation handoff: DriverConnection now extends DriverRegionService while preserving existing defaults. Region service access is typed and native services are adapted without assuming a full cache. DriverRegion.closeRegion maps native Region.close; a close-named method would trigger Spring destroy-method inference and break borrowing. Preserve the existing owner/borrower tests and the new snapshot/subscription blockers. See [region-operation evidence](../wayfinder-impl/investigations/region-operations-and-metadata.md).
