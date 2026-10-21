# Typed region creation and attribute access

Implemented 2026-10-22 against GemFire **10.4.0-build.3056.df98c835**, under [Typed region creation and attribute access](../../wayfinder-issues/46-typed-region-creation-and-attribute-access.md). This is a foundation within [Region configuration, enums, expiration and eviction](../../wayfinder-issues/19-region-configuration-enums-expiration-and-eviction.md); the parent remains open.

## Implemented contract

- `RegionConfiguration<K,V>` is immutable. Its builder captures 23 scalar/reference settings plus callback configuration. Every option distinguishes unset from explicitly set; nullable constraints and named references also distinguish explicit null. Collection inputs are defensively copied. Drivers apply only explicit options after the selected native shortcut, preserving version-specific defaults and last builder assignment.
- `DriverConnection` adds configured peer/client creation overloads. `DriverRegion` adds attribute/mutator views and configured peer/client subregion creation. All six operations default to typed `NOT_INTRODUCED`, retaining compatibility with older providers. The shared contract has an executable probe for every new default (104 defaults total).
- `DriverRegionAttributes` exposes 25 scalar/callback getters; `DriverRegionAttributesMutator` exposes 12 operations: owning region, cloning, sender/queue IDs and callback mutation. Native callbacks retain application object identity, native cleanup and duplicate registration behavior. Replacement returns the callback returned by the native mutator, rather than a separately observed previous value.
- `Scope` is SPI-owned with all four constants, six predicates and configuration-name conversion. Native mapping is by name, never SPI enum ordinal. Remaining native ordinal/string/serialization compatibility belongs to the enum/legacy child.
- Native configuration delegates directly to native factories. Unsupported client-factory options fail before region creation; they are never discarded. Configured `serverRegionName` is verified against a separate server, with client region `alias` reading/writing server region `distributed` through the configured pool.
- Configured native statistics and subregions are exercised through SPI creation. Every exposed scalar native getter is compared with its underlying native attribute; shared tests verify callback getters and lifecycle. Native tests also verify shortcut precedence, invalid settings, cloning and sender/queue attribute mutation.

## Local mock behavior

The mock supports configured LOCAL peer/client regions, key/value constraints, scalar defaults, typed callback mutation and region hierarchy. Absolute/relative lookup, root-only enumeration, descendant enumeration, recursive entry views, invalidation and destruction are exercised by the same tests on both providers. Child cleanup failures still finish closing the local tree and report the failure.

Statistics-enabled regions, cloning/copy effects, nonlocal scopes/data policies, pool routing, disk-store effects, sender/queue effects and other unavailable distributed settings fail explicitly. No expiry, eviction, persistence, copying or remote behavior is synthesized. Setter/readback metadata does not imply the existence of a query, transaction or disk subsystem. Parent configuration acceptance must revisit mock coverage against real Spring consumers and retain precise unsupported evidence where effects remain native-only.

Region handles retain `closeRegion()`: adding `close()` would change Spring inferred bean-destruction ownership. No Spring production source, fixture or assertion was changed in this foundation.

## Coverage ownership

The original configuration task owned 798 frozen rows. They remain required:

| Owner | Rows | Obligation |
| --- | ---: | --- |
| Typed region creation and attribute access | 121 | 37 implemented leaf rows; 84 partial factory/reachable contracts retain parent/consumer obligations. |
| Expiration, eviction and compression SPI | 171 | Policies, values, callbacks, native effects, configuration and mutation integration. |
| Partition and membership configuration SPI | 181 | Partition routing/listeners/results, membership/subscription values and creation integration. |
| Legacy region configuration values and enums | 276 | Remaining enum/constants/defaults, aliases, disk references, configuration DTO conversion/serialization and attribute-copy construction. |
| Security and authenticated views SPI | 47 | OperationContext/OperationCode authorization contracts previously swept into broad cache ownership. |
| Parent configuration acceptance | 2 | Complete RegionAttributes/AttributesMutator type closure after all children. |

Public Spring factories and annotation wiring retain the existing region-factory cutover owner. A mapped scalar setter does not complete the native factory's other methods or its reachable types. The parent is blocked by all four configuration children; downstream tasks retain their original parent blocker. No API row, callback, public native exposure or test was removed to narrow the scope.

## Validation

Final run: **463 tests, zero failures/errors/skips**: SPI 20, core 24, contract kit 6, mock 44, native 63, Spring mock 153 and Spring native 153. Spring suites here are the established migrated-slice regression suites, not the complete Spring unit suite. Boundary fixture verification also passes (18 fixture tests). Full Spring Data main/unit/integration source compilation and Spring Test main/unit compilation pass.

```sh
./gradlew \
 :spring-data-gemfire-driver-spi:check \
 :spring-data-gemfire-driver-core:check \
 :spring-data-gemfire-driver-contract-tests:check \
 :spring-data-gemfire-driver-mock:check \
 :spring-data-gemfire-driver-10.4:check \
 :spring-data-vmware-gemfire:spiCacheTest \
 :spring-data-vmware-gemfire:spiCacheIntegrationTest \
 :spring-data-vmware-gemfire:compileJava \
 :spring-data-vmware-gemfire:compileTestJava \
 :spring-data-vmware-gemfire:compileIntegrationTestJava \
 :spring-test-vmware-gemfire:compileJava \
 :spring-test-vmware-gemfire:compileTestJava \
 :verifyMigrationBoundary \
 -DgemfireVersion=10.4.+ --offline --console=plain

python3 wayfinder-impl/audits/scan_native_references.py
python3 wayfinder-impl/coverage/build_api_coverage.py
python3 wayfinder-impl/coverage/build_api_coverage.py --check
python3 wayfinder-impl/coverage/validate_coverage.py --javap
```

Final Gradle log: `/private/tmp/sdgf-config-final.log` (BUILD SUCCESSFUL, 1m 3s). Coverage consistency, independent javap comparison (148 declarations / 13 types), and negative controls pass. Whole-module native reference counts remain unchanged: 182 production, 122 unit-test and 128 integration-test source files. The shrinking migration baseline was not expanded. Strict zero-native verification is still a later acceptance requirement and is not claimed here.

The earlier region-operations work and these changes remain uncommitted. User-owned Gradle version changes were preserved.
