## Destination

Execute the Driver SPI implementation defined by the signed-off design in `wayfinder/technical-spec.md` (sign-off recorded in `wayfinder/rationale.md` §8, revised design approved 2026-10-20) — build the `spring-data-gemfire-driver-spi` and `spring-data-gemfire-driver-core` modules, one or more reference Driver Impl modules plus the official mock, and migrate `spring-data-vmware-gemfire` off native GemFire, following the 9-step migration plan in `wayfinder/technical-spec.md` §7.

This map tracks **implementation progress and implementation-time decisions/questions** — it does not re-decide architecture, which is already signed off in the design effort under `wayfinder/`.

See [Migration plan](../wayfinder/technical-spec.md) §7 for the step sequence this map executes.

## Notes

- [Native GemFire / Driver SPI gap audit](audits/native-spi-gap-audit.md) (2026-10-21) records the remaining module-wide dependency, API and verification gaps. Scoped migration checks do not establish full compliance. Completion must preserve application-supplied callbacks and reachable public types as well as internal native calls; the audit provides reproducible source inventories and resolved dependency evidence.
- [SPI gap closure implementation plan](spi-gap-closure-plan.md) indexes the open implementation tickets, their dependency graph and ownership of every audit finding. Follow its shared completion requirements. The API coverage inventory precedes further narrow implementation; full module acceptance requires both behavior preservation and strict native-free boundaries.
- Architecture is signed off (`wayfinder/rationale.md` §8, revised design approved 2026-10-20) and is not reopened here. Per [wayfinder/issues/17-spec-review-sign-off-process.md](../wayfinder/issues/17-spec-review-sign-off-process.md): a significant change — one that breaks or reverses an already-completed design decision, or changes the overall approach — requires a new sign-off round on the design docs before being acted on. Smaller clarifications/local corrections are handled inline as normal implementation work, with git history as the audit trail.
- Standing architectural stances carried over from the design effort (do not re-litigate here): the SPI is a thin delegate around the Driver Impl; fail fast everywhere, never silent degradation; two independent bind-time checks (SPI `major.minor` ABI compatibility via the driver stamp; exact GemFire version match, since a driver's version is the GemFire version it wraps — [wayfinder ticket 20](../wayfinder/issues/20-spi-abi-compatibility-and-driver-version-lockstep.md), which superseded the earlier two-range axes); the `Capability` enum is removed, unsupported features fail through default/overridden SPI methods, and existing method behavior is immutable within an SPI major; module/package layout and dependency graph per `wayfinder/technical-spec.md` §3–§4.
- **No static state, no singletons** (spec §5.7, [wayfinder ticket 19](../wayfinder/issues/19-no-static-state-or-singletons.md)): applies to every ticket on this map. No mutable static state or static service locators; constructor injection; instance loggers; no static reset hooks in tests; immutable `static final` constants and stateless factories on immutable value types are fine. Rules bind new and rewritten code; migrated code obtains the driver by injection.
- Follow the user's skill invocation rule: use only explicitly requested skills. Ticket continuation and implementation do not implicitly authorize `/grilling`, `/domain-modeling` or other additional skills.
- Branch strategy (per `wayfinder/technical-spec.md` §7): work happens on a dedicated long-lived integration branch (e.g. `driver-spi-migration`), separate from the repo's `support/<gemfire>-<spring>-<sdgf>.x` convention, merged into `develop` once at cutover.

## Decisions so far

- [SPI skeleton (Step 1)](../wayfinder-issues/01-spi-skeleton.md) — `driver-spi` and `driver-core` stood up and building; added `GemFireDriver.runtimeGemFireVersion()` so `driver-core` stays native-free (spec §5.1/§5.2 amendment), SPI generation range starts at `[1,1]` (**reworked 2026-10-01** — [wayfinder ticket 20](../wayfinder/issues/20-spi-abi-compatibility-and-driver-version-lockstep.md) replaced generation ranges and `Capability` with `SpiVersion 1.0`, `UnsupportedFeatureException` and the driver stamp; `driver-core` now checks the stamp and requires an exact GemFire version match), config enums are `DataPolicy`, `RegionShortcut`, `ClientRegionShortcut`.

- [Reference driver and mock (Step 2)](../wayfinder-issues/02-reference-driver-and-mock.md) — cache/region foundation implemented: 10.4 reference driver selected via `10.4.+` and pinned to its resolved version, native-free local-region mock, generated stamps/services and shared contract kit; 57 tests passing. Broader subsystem APIs expand during Step 3.

- [Spring Cache driver migration (Step 3, first slice)](../wayfinder-issues/03-spring-cache-driver-migration.md) — cache adapter, manager and annotation/XML wiring now use injected SPI handles; 22 behavior tests pass on each of mock and native runtimes, plus 57 driver regressions. Full module compile still has three confirmed pre-existing native 10.4 API errors; cache/util/root migration continues.

- [Native 10.4 compile baseline](../wayfinder-issues/04-native-10.4-compile-baseline.md) — full main/unit/integration-source compilation restored; native pool availability-zone behavior and removed Shiro configuration handled explicitly. 126 targeted unit tests and 9 integration tests pass, with driver/cache checks green.

- [Cache loader driver migration](../wayfinder-issues/05-cache-loader-driver-migration.md) — callable adapter and loader/helper callbacks now use SPI types; native and local mock loading/lifecycle covered. The entire cache package passes the native-free check; 133 driver/cache tests and full source compilation pass.

- [Utility/root migration dependency inventory](../wayfinder-issues/06-utility-root-migration-dependencies.md) — mapped inherited helpers, native operations and consumers; selected a native-free string-path extraction with all 17 existing caller files scheduled together. Runtime utility groups remain tied to cache/pool/query/repository migration.

- [Region-path utility migration](../wayfinder-issues/07-region-path-utility-migration.md) — extracted native-free string formatting and migrated all 17 caller files with legacy behavior preserved; full source compilation and scoped checks pass, with 195 tests run and 67 unchanged driver tests up-to-date.

- [Cache/region resolver dependency inventory](../wayfinder-issues/08-cache-region-resolver-migration-dependencies.md) — traced 26 files; selected a complete resolver migration with typed lifecycle callbacks, injected ownership and a typed supplier for the remaining native pool consumer, without requiring factory migration.

- [Cache/region resolver driver migration](../wayfinder-issues/09-cache-region-resolver-driver-migration.md) — resolver interfaces and implementations now borrow driver handles; typed region lifecycle callbacks, lazy qualified bean lookup and fail-fast composition are verified on mock/native runtimes. Native pool lookup uses an injected supplier; cache factories remain native.

- [Cache-factory ownership/configuration inventory](../wayfinder-issues/10-cache-factory-ownership-and-configuration-dependencies.md) — traced native factory behavior and configuration consumers; selected an injected connection owner/borrower foundation, with native cache reuse distinguished from exclusive ownership and legacy entry points kept coherent during staging.

- [Injected driver connection ownership](../wayfinder-issues/11-injected-driver-connection-ownership.md) — native-free Spring factory now owns or explicitly borrows an injected connection; Java/XML cache-manager and resolver wiring, terminal failure cleanup and shutdown are verified on mock/native runtimes. Native compatible cache reuse remains distinct from mock isolation.

- [Pool configuration and resolver migration dependencies](../wayfinder-issues/12-pool-configuration-and-resolver-dependencies.md) — traced pool lifecycle/configuration and native consumers; selected injected named pool creation/ownership before resolver cutover, with native registry/default-pool behavior distinguished from mock configuration records.

- [Required native API coverage matrix](../wayfinder-issues/14-required-native-api-coverage-matrix.md) — versioned 3,851-row baseline covers inherited contracts, callback payloads and application extension points across 292 native types, with frozen requirements and owners; public Cache reachability adds blocking server/WAN/queue implementation tickets.

- [Named driver pool creation and ownership](../wayfinder-issues/13-named-driver-pool-creation-and-ownership.md) — typed named pool creation/lookup and Spring owner/borrower lifecycle work on native and mock drivers; 393 tests and full source compilation pass, with complete pool semantics and legacy cutover still tracked downstream.

- [Whole-module migration boundary enforcement](../wayfinder-issues/15-whole-module-migration-boundary-enforcement.md) — normal verification rejects source/resource/classpath and architecture regressions against an owned, shrinking baseline; 18 fixture tests pass, while strict verification correctly rejects the remaining native boundaries.

- [Driver exception semantics and translation](../wayfinder-issues/16-driver-exception-semantics-and-translation.md) — typed failure categories, sanitized native mapping and native-free Spring translation preserve cause/outcome distinctions; 409 tests and full source compilation pass, with payload and consumer obligations retained under open owners.

- [Complete region events, listeners and writers](../wayfinder-issues/17-complete-region-events-listeners-and-writers.md) — full callback contracts, lazy payloads and native/mock registration/lifecycle are implemented; real remote/role/serialization/live fixtures and 430 behavior tests pass, with broader reachable contracts and Spring adoption retained downstream.

- [Complete region operations and metadata SPI](../wayfinder-issues/18-complete-region-operations-and-metadata-spi.md) — direct region/Map/bulk/local/server operations, live typed views, routing metadata and exception payloads are implemented; 443 tests and full compilation pass, with snapshot/subscription blockers and subsystem ownership preserved.

- [Typed region creation and attribute access](../wayfinder-issues/46-typed-region-creation-and-attribute-access.md) — optional typed configuration, attributes/mutators and peer/client/subregion creation preserve native defaults and callback identity; local mock hierarchy and real server routing pass 463 tests. The complete configuration parent remains open with explicit blocking children.

- [Named pool configuration and socket callbacks](../wayfinder-issues/50-named-pool-configuration-and-socket-callbacks.md) — 25 optional settings, effective inspection and socket adaptation preserve native defaults/validation; real server socket use and 476 tests pass. Default-cache selection and durable/runtime lifecycle remain explicit blockers of the complete pool parent.

- [PDX values and instance factories](../wayfinder-issues/53-pdx-values-and-instance-factories.md) — typed document/PDX/writable values and complete instance factories preserve native schema/identity behavior; local mock snapshots and real byte/server round trips pass 498 tests. Serialization callbacks, JSON, binary/reflection support and operation-boundary integration remain blocking children of the open parent.

- [Transaction lifecycle and callbacks SPI](../wayfinder-issues/28-transaction-lifecycle-and-callbacks-spi.md) — all manager operations, typed outcomes/writer vetoes and native ID externalization are implemented; real conflict, cross-thread resume and client/server commit/rollback pass within 513 tests. The mock explicitly rejects transactions; Spring consumer migration and preserved native-free unit coverage remain downstream obligations.

- [SPI ABI compatibility and stamp enforcement](../wayfinder-issues/39-spi-abi-compatibility-and-stamp-enforcement.md) — japicmp and packaged-stamp gates now run during verification/publication; core independently checks its resolved SPI requirement before provider discovery. Fresh compiled fixtures cover interface/callback evolution and bind failures; 32 ABI/stamp fixtures and 520 JUnit tests pass. No released SPI baseline exists yet, so historical ABI compatibility is explicitly unclaimed. Complete integration coverage and native-free migration remain downstream.

## Not yet specified

Known implementation gaps in Steps 3–7 now have explicit tickets, indexed by the [SPI gap closure implementation plan](spi-gap-closure-plan.md). They are no longer unspecified backlog. Member-level inventory may reveal additional reachable native surfaces or require splitting a large implementation group; create precise owned blocking tickets when that evidence appears, without losing the parent requirements.

- Step 8 — Publish the support matrix using the selected launch releases and their verified official support/EOL policies. Exact release selection remains separate release planning; do not infer a support policy from the currently implemented reference driver.
- Step 9 — Plan the major-version release and integration-branch merge once technical acceptance and release prerequisites are met. Publishing/merge timing and authorization remain to be established.

## Out of scope

- Any change to the already-signed-off architecture in `wayfinder/technical-spec.md`/`wayfinder/rationale.md` without going through a new sign-off round per [ticket 17](../wayfinder/issues/17-spec-review-sign-off-process.md).
- The concrete initial GemFire version list to ship Driver Impls for at launch — still deferred to separate release planning, per [wayfinder/issues/16-initial-version-list-and-matrix-scope.md](../wayfinder/issues/16-initial-version-list-and-matrix-scope.md).
- Migrating `spring-test-vmware-gemfire` off its direct native GemFire dependency — called out of scope for the whole effort, per [wayfinder/issues/14-testing-strategy.md](../wayfinder/issues/14-testing-strategy.md).
