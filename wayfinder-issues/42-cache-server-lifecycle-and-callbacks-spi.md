Type: task
Status: open
Blocked by: 14, 16, 18

# Cache-server lifecycle and application callbacks SPI

## Question

Close the concrete public API reachability gap discovered by [Required native API coverage matrix](14-required-native-api-coverage-matrix.md).

## Required implementation

Prerequisites: [Required native API coverage matrix](14-required-native-api-coverage-matrix.md); [Driver exception semantics and translation](16-driver-exception-semantics-and-translation.md); [Complete region operations and metadata SPI](18-complete-region-operations-and-metadata-spi.md).

Implement the Cache.getCacheServers/addCacheServer surface reached through Spring CacheFactoryBean/GemfireCallback and the directly exposed ClientSubscriptionConfig. Cover server configuration/start/stop, load probes and ServerLoad payloads, client subscription settings, interest-registration listeners/events and all transitive public callback types. Preserve actual native server behavior; reject server networking explicitly in the mock.

Use the [coverage matrix and update procedure](../wayfinder-impl/coverage/README.md) to identify every owned member, including inherited contracts. Follow the [shared completion requirements](../wayfinder-impl/spi-gap-closure-plan.md#shared-completion-requirements) and the [signed-off specification](../wayfinder/technical-spec.md). Existing public exposure through a native Cache is evidence of required capability, even when Spring itself never calls an operation. Do not silently narrow application APIs.

## Required validation

Verify complete typed contracts, native delegation, callback payload/lifecycle and exception mapping. Exercise real native server/cluster behavior in integration tests and honest mock unsupported behavior in shared contract tests. Update every owned matrix row with SPI/native/mock/test evidence and compile affected consumers.

## Completion

All owned API rows implemented and available for runtime-service/cache assembly and residual consumer cutover. If implementation needs splitting, create blocking children and keep this parent open. Record exact native version and validation results in an Answer; update the implementation map. This ticket does not authorize publication or merge.
