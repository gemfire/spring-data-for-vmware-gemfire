Type: task
Status: open
Blocked by: 14, 16, 18, 42, 43

# Distributed-system, membership and runtime services SPI

## Question

Move the actual distributed-system/member/runtime service dependencies behind injected driver contracts.

Audit coverage: **G3, G6** in the [native/SPI gap audit](../wayfinder-impl/audits/native-spi-gap-audit.md). Apply the [shared completion requirements](../wayfinder-impl/spi-gap-closure-plan.md#shared-completion-requirements) and the signed-off [technical spec](../wayfinder/technical-spec.md).

## Required implementation

Prerequisites: [Required native API coverage matrix](14-required-native-api-coverage-matrix.md); [Driver exception semantics and translation](16-driver-exception-semantics-and-translation.md); [Complete region operations and metadata SPI](18-complete-region-operations-and-metadata-spi.md).

- Inventory DistributedSystemUtils, GemfireFunctions, locator/server metadata and internal CLI/JNDI consumers; separate required operations from incidental diagnostics.
- Implement typed member/system status, properties, identity and required runtime/server/locator operations using native adaptation. Reuse identity types introduced for events.
- Provide explicit native-boundary replacements for internal serialization/JNDI/runtime accesses as required; never expose native internal classes or add global Spring/driver registries.
- The [coverage matrix](../wayfinder-impl/coverage/README.md) confirms public Cache reachability of server/WAN/queue APIs. Integrate the blocking [Cache-server lifecycle and application callbacks SPI](42-cache-server-lifecycle-and-callbacks-spi.md) and [WAN and asynchronous queue contracts and callbacks SPI](43-wan-and-async-queue-contracts-spi.md); these are required contracts, not unresolved scope. Preserve typed runtime discovery and service access without native implementation exposure.

- Exception foundation follow-up: Complete assigned native exception constructor/member equivalents and system/lease failure emission under the exception coverage rows. See [exception semantics and ownership](../wayfinder-impl/investigations/exception-semantics-and-translation.md).

- Callback foundation follow-up: Reuse DriverMember/DriverRole and the event mapper's identity/metadata views. Durable client attributes are currently immutable event metadata; mutable attributes and full distributed runtime contracts remain here. Real membership/role callback fixtures already exist, including the 10.4 RoleEvent Set<String>/Role payload discrepancy; see [callback evidence](../wayfinder-impl/investigations/region-callback-contracts.md).

## Required validation

- Verify actual connectedness and membership operations natively; cache-not-closed must not stand in for distributed connectivity.
- Test injected isolation of collaborators, no secret-bearing diagnostics, required native internal adaptations and honest unsupported mock behavior.

## Completion

Required runtime-service rows implemented or tracked by blocking children; diagnostics remain independent of global service discovery. Record the Answer with changed source/API rows, commands/results, native version and limitations; update the implementation map. Do not mark dependent work complete from this ticket's result.

Region-operation handoff: Region.getDistributedLock/getRegionDistributedLock/becomeLockGrantor rows are assigned here. Region-service/entry access and typed region failure payloads already exist; distributed locks are still unimplemented and require native lifecycle/ownership validation. See [region-operation evidence](../wayfinder-impl/investigations/region-operations-and-metadata.md).
