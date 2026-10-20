Type: task
Status: resolved
Assignee: Codex
Blocked by: 12

# Required native API coverage matrix

## Question

Establish the member-level coverage baseline for every native capability Spring Data uses or exposes, including inherited APIs and application extension points, so missing SPI methods cannot disappear from the migration.

Audit coverage: **G3, G4, G5, G7** in the [native/SPI gap audit](../wayfinder-impl/audits/native-spi-gap-audit.md). Apply the [shared completion requirements](../wayfinder-impl/spi-gap-closure-plan.md#shared-completion-requirements) and the signed-off [technical spec](../wayfinder/technical-spec.md).

## Required implementation

Prerequisites: [Pool configuration and resolver migration dependencies](12-pool-configuration-and-resolver-dependencies.md).

- Expand the audit's import/reference inventory into a versioned coverage matrix with stable row IDs: native type/member, Spring consumer or exposed signature, reachable payload types, required behavior, SPI equivalent, native implementation, mock disposition, tests and owning implementation ticket.
- Follow public method signatures, generic bounds, superclass/interface contracts, accepted callbacks, native Region callback access, annotation attributes/defaults, XML expected/exported types and reflective class-name lookups. Traverse reachable native types; do not restrict the matrix to internal calls or the 147 imported symbols.
- Compare the existing 18 driver defaults and callback contracts with the required surface. Mark absent and partial coverage explicitly; retain the full native event payload and declarative initialization obligations.
- Assign every known row to the implementation/cutover tickets in the closure plan; create precise follow-ups for uncovered surfaces, including any evidenced WAN/server surface. A missing feature is not an approved exclusion. Keep changes to the signed-off scope separate from routine implementation choices.

## Required validation

- Validate stable IDs, source references, owner links and status vocabulary; every audited production native symbol/resource contract must map to rows or an explained non-runtime reference.
- Demonstrate that an omitted CacheListener callback, an inherited event type and an application-only extension point appear as missing even though current SPI compilation succeeds.

## Completion

Commit-ready coverage matrix and its update/validation procedure, with no unowned required rows; this does not claim any missing API implemented. Record the Answer with changed source/API rows, commands/results, native version and limitations; update the implementation map. Do not mark dependent work complete from this ticket's result.

## Answer

Completed the [required native API coverage baseline and update procedure](../wayfinder-impl/coverage/README.md), [member-level matrix](../wayfinder-impl/coverage/api-coverage.tsv), source/signature/reachability tables, frozen requirements and executable generation/validation scripts.

Against **10.4.0-build.3056.df98c835**, the baseline contains **3,851 rows across 292 native types**, **607 Spring signature exposures**, **419 direct member references** and **635 source/resource references**. Every one of the 147 production imported native symbols maps to owned coverage. Rows include types, fields, constructors and repeated inherited contracts; they are not a count of missing methods or a completion percentage. Current disposition is 3,763 missing and 88 partial, with no new runtime feature claimed complete.

The [existing SPI comparison](../wayfinder-impl/coverage/existing-spi-coverage.tsv) accounts for all 18 operational defaults and eight loader/helper/lifecycle callback methods. Required missing callbacks, inherited event/member metadata, serialized payloads, declarative initialization, callback specializations, Java collection contracts, annotation defaults, XML contracts and reflective names remain visible independently of what currently compiles. `ConfigProperty` is a publicly exposed internal payload and its complete public shape is included. Other native internal implementation types have explicit owned boundary-replacement dispositions; this is not an instruction to mirror engine internals into SPI.

Created [Cache-server lifecycle and application callbacks SPI](42-cache-server-lifecycle-and-callbacks-spi.md) and [WAN and asynchronous queue contracts and callbacks SPI](43-wan-and-async-queue-contracts-spi.md) from concrete public Cache reachability. Both block [Distributed-system, membership and runtime services SPI](23-distributed-system-membership-and-runtime-services-spi.md), and therefore final acceptance. Added newly evidenced callback, query-result, partition, security/JNDI, serialization and resource requirements to their existing implementation owners. No dependent ticket was resolved.

Validation completed:

- `python3 wayfinder-impl/coverage/build_api_coverage.py` and `--check`: deterministic generation, exact member resolution, immutable API IDs and retention of frozen requirements after native imports migrate away.
- `python3 wayfinder-impl/coverage/validate_coverage.py --javap`: source/hash/owner checks passed; independently matched 148 native public declarations across 13 representative types. Negative controls rejected duplicate IDs and removal of `CacheListener.afterCreate` while SPI stayed untouched.
- Dependency traversal found no cycles; every implementation ticket, including the new children, gates final acceptance. Plan dependency tables and local document links were checked; `git diff --check` passed.

The manifest records native artifact hashes and source/evidence fingerprints. Existing compiled production classes were checked for missing/stale/orphan output; no Gradle compilation or runtime tests were rerun for this inventory-only change. Dynamic/application behavior still requires the assigned native/shared tests, and whole-module enforcement/ABI/release-matrix work remains open. Production code and user build changes were preserved; nothing was committed or published.
