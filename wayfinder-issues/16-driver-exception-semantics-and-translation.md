Type: task
Status: resolved
Assignee: Codex
Blocked by: 14

# Driver exception semantics and translation

## Question

Represent the native failure distinctions required by Spring Data through SPI exceptions without requiring downstream native type inspection.

Audit coverage: **G3, G6** in the [native/SPI gap audit](../wayfinder-impl/audits/native-spi-gap-audit.md). Apply the [shared completion requirements](../wayfinder-impl/spi-gap-closure-plan.md#shared-completion-requirements) and the signed-off [technical spec](../wayfinder/technical-spec.md).

## Required implementation

Prerequisites: [Required native API coverage matrix](14-required-native-api-coverage-matrix.md).

- Inventory GemfireCacheUtils, transaction, query, function and callback error classifications and map them to stable SPI failure semantics and Spring DataAccessException outcomes.
- Extend SPI exception/translation contracts and native boundary mapping as needed, preserving causes and safe diagnostics; keep unsupported NOT_INTRODUCED/REMOVED separate from execution failures.
- Update currently migrated consumers and provide the common translation path for later subsystem tickets. Do not replace established duplicate/conflict/auth/query classifications with a single uncategorized result.
- Assign each remaining native exception caller to its consumer cutover ticket; retire the final native GemfireCacheUtils dispatch with residual cleanup once all callers move.

## Required validation

- Verify representative native error categories and resulting Spring exceptions, original causes, safe top-level messages, unknown errors and typed unsupported failures.
- Test callback veto/failure propagation through driver boundaries; guard against interpreting errors as absent results or successful no-ops.

## Completion

Typed failure/translation foundation and matrix evidence; remaining caller conversions have explicit owners. Record the Answer with changed source/API rows, commands/results, native version and limitations; update the implementation map. Do not mark dependent work complete from this ticket's result.


## Answer

Resolved 2026-10-21. Implemented the common typed failure/translation foundation and assigned remaining exception payloads and native consumers to their open subsystem/cutover owners. See [exception semantics, Spring outcomes and limitations](../wayfinder-impl/investigations/exception-semantics-and-translation.md), [native classifications](../wayfinder-impl/investigations/native-exception-classifications.tsv) and [remaining callers](../wayfinder-impl/investigations/native-exception-callers.tsv).

### Implementation and coverage

- Added `DriverFailureKind` and additive `DriverOperationException.kind()`/typed constructor; retained the original constructor as `UNKNOWN`. The SPI translator distinguishes duplicate, conflict, retrieval, security, query/index, resource, cancellation, argument/state, timeout and callback failures without native inspection. Unsupported/bind failures retain separate contracts.
- Added native `GemFireExceptionMapper`, adopted by cache/region/pool/loader-helper execution boundaries, preserving original causes with operation-only messages. Loader adapters classify callback failures at entry and preserve existing SPI/unsupported failures. Mock equivalents classify local errors and finish loader cleanup even when callbacks share the same typed exception object.
- Added native-free Spring `GemfireExceptionTranslator`, preserving Gemfire-specific query/index/cancellation/system exception subtypes. Migrated all four wrappers to broad Throwable constructors and made `DriverConnectionFactoryBean` supply translation to Spring repository advice. Existing caller sources compile; constructor descriptor changes require recompilation at the planned Spring major cutover. Cache/resolver/factory lifecycle propagation remains intact.
- Inventoried 69 native exception types and 18 remaining production caller files. Updated all 403 required exception rows: 69 type rows are partial; 334 constructor/member/payload rows remain missing with explicit open owners. Per-row consumer-owner policy overrides are now honored by the generator. Added the corresponding obligations to existing downstream tickets; none of those tickets is resolved here.
- The frozen 3,851-row / 292-type requirement set is unchanged: **3,682 missing / 169 partial**, zero implemented/removed. Current source evidence has 588 Spring signature exposures, 144 imported native symbols and 624 source references. Deleting native constructor signatures did not delete their frozen obligations.
- Pruned 22 resolved boundary identities / 30 native-source occurrences. Baseline is now **4,475 identities / 6,050 occurrences**, including grandfathered statics. Current scan covers 822 Java files and 139 resources; Spring production native files decrease from 186 to **182**, unit/integration counts remain 122/128. Focused source coverage is 26 main / 24 test files. This is still not native-free module compliance.

### Validation

Pinned native artifact: `com.vmware.gemfire:gemfire-core:10.4.0-build.3056.df98c835`, with matching `gemfire-version` and JDK 17 toolchain.

Commands used (Gradle runs offline with `-DgemfireVersion=10.4.+ --console=plain`, session-only sandbox approval; the user's `gradle.properties` edit is unchanged):

```text
./gradlew :spring-data-gemfire-driver-spi:test :spring-data-gemfire-driver-mock:test :spring-data-gemfire-driver-10.4:integrationTest :spring-data-vmware-gemfire:spiCacheTest :spring-data-vmware-gemfire:spiCacheIntegrationTest
./gradlew :spring-data-gemfire-driver-mock:test :spring-data-gemfire-driver-10.4:integrationTest :spring-data-vmware-gemfire:spiCacheTest :spring-data-vmware-gemfire:spiCacheIntegrationTest :pruneMigrationBoundaryBaseline
./gradlew :spring-data-gemfire-driver-spi:check :spring-data-gemfire-driver-core:check :spring-data-gemfire-driver-contract-tests:check :spring-data-gemfire-driver-mock:check :spring-data-gemfire-driver-10.4:check :spring-data-vmware-gemfire:compileJava :spring-data-vmware-gemfire:compileTestJava :spring-data-vmware-gemfire:compileIntegrationTestJava :spring-test-vmware-gemfire:compileJava :spring-test-vmware-gemfire:compileTestJava
./gradlew :spring-data-vmware-gemfire:compileJava :verifyMigrationBoundary
./gradlew :spring-data-gemfire-driver-mock:check
python3 wayfinder-impl/audits/scan_native_references.py
python3 wayfinder-impl/coverage/build_api_coverage.py
python3 wayfinder-impl/coverage/build_api_coverage.py --check
python3 wayfinder-impl/coverage/validate_coverage.py --javap
```

Final reports: **409 passing behavior tests**, zero failures/skips — SPI 17, core 24, contract-kit 6, mock 24, native driver 32, focused Spring mock 153 and focused Spring native 153. These suites executed across the commands above; the final broad check reused already passing suites. Full Spring Data main/unit/integration and Spring Test main/unit source compilation passed. Spring Test has no integration source set. Boundary regression verification and deterministic API regeneration/validation passed; independent javap matched 148 declarations across 13 types, including both inventory negative controls.

The initial mock run correctly exposed an outdated assertion expecting an operation wrapper around typed unsupported; it was replaced with the required direct reason/feature assertion. Final review also exposed cleanup aggregation catching only operation failures; a new regression verifies that all loaders are released when both throw the same typed unsupported exception, with no self-suppression. Both corrections pass.

Tests include 26 representative native exception instances, checked failures, subtype precedence, unknown failures, sanitized messages/cause identity, real native writer veto with no entry insertion, shared mock/native loader unsupported/veto behavior, distinct duplicate/argument categories, and actual Spring repository-advice discovery.

### Remaining limits

This closes the foundation only. Remote authentication, distributed transaction outcomes, CQ/function execution, full callback registration and typed exception payloads remain under their existing open tickets. Cache creation keeps the separate bind-failure contract. Native listener suppression remains native behavior. Throwable causes remain trusted diagnostics and may contain sensitive data; only boundary/top-level translation messages are sanitized. Legacy native consumers can still expose native messages until cutover; final GemfireCacheUtils removal belongs to residual cleanup. The strict native-free gate is still expected to fail and was not rerun simply to restate the known migration debt. No commit or publish action was taken.
