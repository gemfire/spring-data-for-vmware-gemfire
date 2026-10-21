Type: task
Status: resolved
Assignee: Codex
Blocked by: 14, 16, 17

# Complete region operations and metadata SPI

## Question

Complete the region/service operation surface required by templates, factories and application Region callbacks.

Audit coverage: **G3, G4** in the [native/SPI gap audit](../wayfinder-impl/audits/native-spi-gap-audit.md). Apply the [shared completion requirements](../wayfinder-impl/spi-gap-closure-plan.md#shared-completion-requirements) and the signed-off [technical spec](../wayfinder/technical-spec.md).

## Required implementation

Prerequisites: [Required native API coverage matrix](14-required-native-api-coverage-matrix.md); [Driver exception semantics and translation](16-driver-exception-semantics-and-translation.md); [Complete region events, listeners and writers](17-complete-region-events-listeners-and-writers.md).

- Map the required Region/RegionService public and inherited surface from the coverage matrix, including contains-value/value-for-key, server-key checks, create/replace variants, bulk operations, entry views, paths/subregions, invalidation/destruction and metadata.
- Implement typed operations, native delegation and local mock behavior without substituting loops where they change native atomicity, return values or callback semantics.
- Expose lifecycle operations needed by real users and listener tests, plus typed metadata needed for client pool association and query routing. Query execution itself is owned by Query and index SPI.
- Ensure methods involving callbacks use the full event/listener/writer contracts and that unavailable network operations fail explicitly.

- Exception foundation follow-up: Preserve typed exception payloads for EntryExistsException old value, existing region handles and region paths; failure kinds alone do not complete these member rows. See [exception semantics and ownership](../wayfinder-impl/investigations/exception-semantics-and-translation.md).

- Callback foundation follow-up: The callback foundation adds callback-argument put, invalidate/destroy, region invalidate/destroy, plus callback lookup/registration. Reuse these typed events and preserve destroy's previous-value result. Complete remaining overloads, inherited Map behavior and event-reachable region contracts without losing callback timing; see [callback evidence](../wayfinder-impl/investigations/region-callback-contracts.md).

## Required validation

- Cover required CRUD/bulk/conditional operations, callback arguments, entry/region lifecycle, destroyed handles and return semantics on mock/native where supported.
- Validate server/proxy-specific operations natively and typed unsupported behavior in the mock; preserve existing region API behavior.

## Completion

Complete assigned region-operation rows and typed metadata; remaining query/configuration rows link to their owning tickets. Record the Answer with changed source/API rows, commands/results, native version and limitations; update the implementation map. Do not mark dependent work complete from this ticket's result.

## Scope decomposition

The member-level audit includes separately testable snapshot and subscription families. Their requirements remain in the frozen matrix, now under [Region and cache snapshot services SPI](44-region-and-cache-snapshot-services-spi.md) and [Region interest registration and subscription SPI](45-region-interest-registration-and-subscription-spi.md). Both block downstream acceptance; neither is declared implemented by this foundation.

Existing subsystem owners retain Region.getAttributes/getAttributesMutator/createSubregion (region configuration), query/existsValue/selectValue (query/results), distributed locks/grantor (distributed runtime), and writeToDisk/forceRolling (disk/resource). This ticket supplies operational entry views, live statistics adapters, path traversal, CRUD/Map/bulk/local/server operations, service access, routing metadata and exception payloads. Native configured subregions/statistics are tested through native setup until their typed creation/configuration owner lands; the local mock currently creates roots with statistics disabled.

## Answer

Resolved 2026-10-22 as the region-operation foundation. [Implementation and behavior evidence](../wayfinder-impl/investigations/region-operations-and-metadata.md) records the native-specific semantics and required follow-ups.

- Added 56 `DriverRegion` defaults: CRUD/conditional/bulk/Map methods, local lifecycle operations, live entry/collection views, path traversal, region-service/cache access, statistics, pool/data-policy metadata and server inspection. All 98 SPI defaults have behavioral probes. Native methods delegate corresponding operations directly; mock remote operations fail explicitly.
- Added typed entry/statistics/service contracts and duplicate-entry/existing-region/region-path failure payloads. Shared tests preserve writer/event arguments, entry recreation, loader-free entry reads and view mutation behavior. Native tests verify configured hierarchy/statistics and real separate-server PROXY behavior.
- Mapped native `Region.close()` to `closeRegion()`: the initially close-named method caused Spring to infer destruction of borrowed region beans. The corrected API passes the existing Java/XML owner/borrower tests on mock and native runtimes without changing their assertions or wiring.
- Kept all 3,851 coverage rows / 292 types. Totals are 3,256 missing, 356 partial and 239 implemented; the region-operation owner has 94 implemented and 33 partial rows. Partial rows retain complete reachable contracts/consumer obligations. No requirement was deleted and no absent implementation was treated as native removal.
- Split snapshot and subscription requirements into the named follow-up tickets above, wired into cache/template/CQ acceptance. Region attributes/subregion creation, query, locks and disk methods retain exact existing subsystem owners. This resolution does not complete configuration or Spring Region consumer migration.

Validation against **GemFire 10.4.0-build.3056.df98c835**:

```text
./gradlew :spring-data-gemfire-driver-spi:check \
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

All checks passed. **443 behavior tests**: SPI 18, core 24, contract fixtures 6, mock 36, native 53, Spring mock 153, Spring native 153; zero failures/errors/skips. Full Spring Data main/unit/integration source compilation and Spring Test main/unit compilation passed. The migration boundary regression gate passed with no baseline expansion; this is not strict native-free compliance. Coverage validation retained 144 production imports / 624 source references, independently matched 148 native declarations across 13 types, and passed both negative controls. The final Gradle log is `/private/tmp/sdgf-region-full-validation.log`.

The mock currently supports local roots with disabled statistics. Typed configuration/creation of subregions and enabled statistics remains with [Region configuration, enums, expiration and eviction](19-region-configuration-enums-expiration-and-eviction.md), the next eligible ticket. Snapshot/subscription and broader service contracts remain open. Existing user edits to `gradle.properties` and the Gradle wrapper are preserved. No commit or push was made.
