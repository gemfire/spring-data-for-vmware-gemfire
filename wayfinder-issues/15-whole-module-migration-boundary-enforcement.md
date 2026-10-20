Type: task
Status: resolved
Assignee: Codex
Blocked by: 14

# Whole-module migration boundary enforcement

## Question

Replace allowlist-only progress reporting with whole-module boundary visibility and enforce regressions while native consumers are being migrated.

Audit coverage: **G1, G2, G6, G7** in the [native/SPI gap audit](../wayfinder-impl/audits/native-spi-gap-audit.md). Apply the [shared completion requirements](../wayfinder-impl/spi-gap-closure-plan.md#shared-completion-requirements) and the signed-off [technical spec](../wayfinder/technical-spec.md).

## Required implementation

Prerequisites: [Required native API coverage matrix](14-required-native-api-coverage-matrix.md).

- Add whole-source and resolved-classpath checks for main, unit tests and integration-test source references, including fully qualified names and native reflection strings. Distinguish native integration runtime allowances from source/API violations.
- Create an explicit, reviewable transitional violation baseline that rejects new or reintroduced dependencies; shrink it as owning tickets close. Also provide a strict zero-violation task that currently fails with actionable locations. Never label a baseline pass as native-free compliance.
- Enforce SPI/core/mock and driver dependency directions and the spec's mutable-static/singleton rules on new modules; apply constructor-injection rules to rewritten Spring code without treating untouched legacy code as newly introduced.
- Check schema exported/expected types and class-bearing resources; classify documentation/logger text separately. Wire regression checks to normal verification and retain scoped tests only as transitional validation.

## Required validation

- Use controlled fixtures to show added native imports, class literals/strings, forbidden artifacts and mutable static state fail; permitted native-driver internals and immutable constants pass.
- Record actual whole-module violation counts and show the strict task fails for known remaining violations instead of reporting a green migration.

## Completion

Build-enforced migration regression controls and a strict completion gate; final removal of the baseline belongs to Whole-module native dependency removal. Record the Answer with changed source/API rows, commands/results, native version and limitations; update the implementation map. Do not mark dependent work complete from this ticket's result.

## Answer

Resolved 2026-10-21. Whole-module regression enforcement and a deliberately failing strict completion gate are implemented. No production API behavior or coverage-matrix disposition changed in this ticket.

### Implementation

- [Build wiring](../build-tools/migration-boundary/migration-boundary.gradle.kts), applied by the root build, adds `verifyMigrationBoundary`, `verifyNativeFreeBoundary`, `pruneMigrationBoundaryBaseline`, `testMigrationBoundary` and resolved-classpath export. Root, Spring Data and every driver module's `check` depend on the regression gate. Existing scoped tests remain transitional validation.
- [Java source parser](../build-tools/migration-boundary/SourceFacts.java) checks entire physical source trees and configured source roots without relying on the `spiCache` allowlist. It detects native imports/qualified names/imported types/class literals/literal reflection strings, module direction violations, static state/singletons and driver construction/injection violations. Native engine globals remain permitted inside native drivers; driver-owned static state is rejected.
- [Checker](../build-tools/migration-boundary/check_boundary.py) checks every source set's resolved compile/runtime components and actual JAR/directory contents, plus schema/class-bearing resources. Integration runtime allowances are reported separately; they do not permit native Spring integration source or compile references. Documentation/logger findings are informational.
- [Owned transitional baseline](../build-tools/migration-boundary/violations-baseline.json) records 4,497 identities / 6,080 occurrences. New occurrences, increased counts and stale entries fail. Pruning only removes resolved entries and refuses outstanding regressions. New driver modules and migrated Spring injection violations cannot be baselined. Existing Spring statics remain grandfathered under spec §5.7, but new or changed ones fail.
- [Usage, counts and limitations](../build-tools/migration-boundary/README.md) document running and shrinking the baseline. Reports are generated under `build/reports/migration-boundary/`; resolved versions and diagnostic locations are refreshed on every Gradle invocation.

### Whole-module result

The scan covers 816 Java files across six modules and 139 resource files. Spring Data has 357 production, 254 unit-test and 150 integration-test files; native references remain in **186 / 122 / 128** files respectively. These agree with the prior lexical audit; the finer parser records 5,615 native source occurrences and 42 native strings.

Other remaining violations: 36 native resource occurrences, 48 forbidden native component placements, 51 native classpath-content placements, four Spring Test dependency edges and ten legacy injection points. There are 274 grandfathered legacy Spring static/singleton occurrences. Component/content checks intentionally overlap, so none of these counts is an API-completion percentage.

The strict gate rejects **5,806 occurrences / 4,223 identities**, excluding grandfathered Spring statics. A regression pass explicitly prints **NOT native-free compliance**. No downstream migration, ABI or behavior ticket is closed by this result.

### Validation

Native version: `com.vmware.gemfire:gemfire-core:10.4.0-build.3056.df98c835`, selected with `-DgemfireVersion=10.4.+` from the offline cache. Gradle invocations used the existing guarded `/private/tmp/sdgf-resolver-validation.init.gradle`; no settings/version-property changes were made.

- `verifyMigrationBoundary`: passed with the explicit baseline; zero new/stale entries.
- `verifyNativeFreeBoundary`: **failed as expected**, with current source locations and remaining native violations. This establishes that the build does not report a completed migration.
- [Controlled fixture suite](../build-tools/migration-boundary/test_boundary.py): **18 tests passed**, covering native imports/qualified literals/escaped and concatenated strings, mutable static forms, holders/singletons, immutable constants, native-driver internal calls, injection, resources, dependency directions and baseline regression/strict behavior.
- End-to-end negative Gradle fixture: temporarily added an unrelated JAR containing `org/apache/geode/Hidden.class` to core's `testRuntimeOnly` via an init script. `verifyMigrationBoundary` **failed as expected**, identifying `spring-data-gemfire-driver-core:testRuntimeClasspath [native-classpath-content] unrelated-helper.jar`. No repository source/build dependency was mutated by the fixture.
- Final normal invocation of `:spring-data-gemfire-driver-spi:check`, `:spring-data-gemfire-driver-core:check`, `:spring-data-gemfire-driver-mock:check`, `:spring-data-gemfire-driver-10.4:check` and `:spring-data-gemfire-driver-contract-tests:check`: **passed** in 9 seconds; 50 tasks, five executed and 45 up-to-date. This reran the boundary fixtures/export/regression checks and confirmed normal verification wiring. Existing Java behavior suites were up-to-date, not counted as fresh test executions. The temporary dependency was absent and reports returned to the real baseline.
- `python3 wayfinder-impl/coverage/build_api_coverage.py --check`: passed; 292 native types, 3,851 API rows and 607 Spring exposures remain unchanged. No source/API rows were marked implemented by this infrastructure ticket.
- `git diff --check`: passed.

Logs are in `/private/tmp/sdgf-boundary-{regression,strict,negative-classpath,final}.log` for this workspace session.

### Limits and handoff

The source check is syntactic, not whole-program type/data-flow analysis. It does not prove indirect reflection/service lookup, inherited deep immutability or arbitrary helper purity. Immutable factory checks inspect owned fields; fresh existing exception factories remain accepted. Runtime behavior and ABI still require their separately owned checks. See the checker README for exact scope and conservative restrictions.

[Whole-module native dependency removal](38-whole-module-native-dependency-removal.md) owns final dependency removal and baseline retirement. Consumer migrations must prune resolved entries as they proceed. The next eligible ticket is [Driver exception semantics and translation](16-driver-exception-semantics-and-translation.md).
