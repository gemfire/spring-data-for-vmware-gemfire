# Migration boundary checks

These checks implement [Whole-module migration boundary enforcement](../../wayfinder-issues/15-whole-module-migration-boundary-enforcement.md). They cover the whole Spring Data module and every `spring-data-gemfire-driver-*` module. Passing the transitional baseline **does not mean Spring Data is native-free**.

Run from the repository root with the repository's configured dependencies and JDK 17 toolchain. Python 3 is also required:

```sh
./gradlew verifyMigrationBoundary -DgemfireVersion=10.4.+ --offline
./gradlew verifyNativeFreeBoundary -DgemfireVersion=10.4.+ --offline
./gradlew testMigrationBoundary --offline
```

`verifyMigrationBoundary` is a dependency of the root, Spring Data and all driver modules' normal `check` tasks. It rejects new findings, increased occurrence counts and stale baseline entries. `verifyNativeFreeBoundary` additionally rejects the existing native violations; it is deliberately red during migration. The existing `spiCache*` compilation/tests remain useful transitional behavior checks.

After completing a migration, run:

```sh
./gradlew pruneMigrationBoundaryBaseline -DgemfireVersion=10.4.+ --offline
./gradlew verifyMigrationBoundary -DgemfireVersion=10.4.+ --offline
```

Pruning can only remove resolved entries or reduce their counts. It refuses to run if new violations exist. Review its diff with the owning ticket. There is no task to bless new violations. Stale entries fail verification so a removed reference cannot silently return later using an unused baseline allowance. Moving lines does not change an entry's identity; its rule, path, normalized evidence and occurrence count do.

## What is checked

- Java sources across the entire physical `src` trees, plus every Gradle source set's configured Java directories, regardless of compilation include/exclude patterns. The JDK parser handles multiline declarations, Unicode escapes, imports, fully qualified references, imported type uses, class literals, literal reflection strings and literal string concatenation. Native implementation modules may use native APIs; Spring integration **sources** may not.
- Resolved components and actual JAR/directory contents of every source set's compile and runtime configurations, including transitive dependencies. Native package entries in an otherwise unrelated artifact also fail. Native implementations may resolve native artifacts; Spring integration runtimes may resolve their native driver. Spring integration compile configurations and main/unit-test configurations remain prohibited boundaries.
- Module directions: SPI cannot depend on core/providers/Spring; core cannot depend on providers/Spring; providers depend on SPI and their native engine, with the shared contract kit/core allowed in provider test configurations. Spring may use providers only on the appropriate test runtime. Both project and published component identities are checked. The separate Spring Test module is outside this migration, but Spring Data's dependency on it is recorded as a violation.
- Static fields, including interface fields, nested holders, arrays, atomic holders, collections and static loggers; singleton accessors, static methods returning their own service type, enum services with one constant, and production static reset hooks. Primitive/String/JDK immutable constants, immutable record values and configuration enums are accepted. Factory checks inspect the declaring type's owned fields; existing fresh exception factories remain accepted. This is an architecture fitness check, not a proof of deep immutability or arbitrary method purity.
- Constructor injection in Spring production sources: field/method injection, mutable or initialized manager/driver fields, manager/driver setters, direct provider/manager construction, ServiceLoader discovery and static driver-handle accessors. Already migrated sources importing the SPI/core cannot baseline injection violations. Untouched legacy injection points are explicit baseline entries. Existing Spring statics are grandfathered by spec §5.7, but adding or changing one fails the regression gate. New driver modules cannot baseline any violations.
- Schema `exports`/`expected-type`, XML bean classes and other class-bearing resource values, service descriptor filenames and archive entries. XML documentation and logger names, Java comments and direct logger string arguments are classified separately as informational findings. XML entities are decoded; external resources are never fetched.

The checks deliberately fail closed on Java parse errors, missing resolved classpath files, unrecognized dependency components, missing modules/configurations and invalid baseline ownership. No production code is executed by the source scanner.

## Reports and baseline

Build outputs are under `build/reports/migration-boundary/`:

- `classpaths.json`: source roots, resolved component coordinates/versions and files, refreshed by Gradle on every verification invocation.
- `findings.json`: every finding with current path, line, rule and evidence, including permitted runtime and documentation classifications.
- `summary.json`: complete source counts, violation counts and remaining native Java files by scope.

[violations-baseline.json](violations-baseline.json) is the reviewable transitional baseline. Every entry names its owning consumer/cleanup ticket. These owners supplement the member-level [API coverage matrix](../../wayfinder-impl/coverage/README.md); they do not replace API obligations or count APIs as implemented. The baseline permits only existing Spring violations. Its metadata records the native version used for initial validation; resolved versions are always available in the fresh classpath report.

Initial evidence, 2026-10-21, native `10.4.0-build.3056.df98c835`:

| Measure | Count |
| --- | ---: |
| Java files scanned across six modules | 816 |
| Resource files scanned | 139 |
| Spring production files / files with native references | 357 / 186 |
| Spring unit-test files / files with native references | 254 / 122 |
| Spring integration-test files / files with native references | 150 / 128 |
| Baseline identities / occurrences | 4,497 / 6,080 |
| Native Java source occurrences / strings | 5,615 / 42 |
| Native resource occurrences | 36 |
| Forbidden native component / classpath-content occurrences | 48 / 51 |
| Spring Test dependency edges | 4 |
| Legacy injection occurrences | 10 |
| Grandfathered Spring static/singleton occurrences | 274 |

Counts represent syntax occurrences or classpath placements, not distinct APIs or completion percentages. Component and content checks intentionally overlap. The strict gate rejects **5,806 occurrences / 4,223 identities**, excluding the 274 grandfathered legacy static/singleton entries. Permitted native runtime placements (163), documentation findings (276) and logger findings (1) are informational.

The 18 controlled fixture tests cover positive and negative source/resource/classpath rules, baseline counts, line movement, stale entries and strict-versus-regression behavior. An additional Gradle invocation injected a temporary `unrelated-helper.jar` containing `org/apache/geode/Hidden.class` into core's `testRuntimeOnly`; the gate failed with `NEW ... core:testRuntimeClasspath [native-classpath-content]`. The fixture never changed repository dependencies. A normal invocation restores the reports.

## Limits and remaining work

The Java scanner parses syntax without resolving every expression's runtime type. It does not prove arbitrary reflection/data-flow behavior, indirect service lookup, inherited deep immutability or private-helper purity. Unknown static field types are rejected conservatively; supporting another immutable value shape requires a reviewed checker change and fixtures. Direct string concatenation is evaluated, but names assembled from variables or external input require review. Wildcard native imports are conservatively tracked along with capitalized uses.

This gate does not replace compilation, the frozen required-API inventory, ABI compatibility checks or behavior tests. Those remain required by the closure plan. Final native dependency/baseline removal belongs to [Whole-module native dependency removal](../../wayfinder-issues/38-whole-module-native-dependency-removal.md); full API/contract verification belongs to the existing ABI, matrix and conformance tickets. Build scripts and baseline changes themselves remain reviewable policy changes.
