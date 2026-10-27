Type: task
Status: open
Blocked by: 14, 15, 17, 18, 28, 39, 46, 60, 61, 62, 63, 64

# Remove server-only SPI and reconcile client scope

## Question

Remove the server-side SPI work already introduced and align implementations, consumers, tests and coverage with the approved [client-only scope](../wayfinder-impl/client-only-scope.md). This is the next implementation task. No production removal is claimed by creating this ticket.

## Required implementation

1. Inventory committed and working-tree server APIs by stable coverage ID and source symbol. Record keep/remove/private-test-fixture dispositions with client-callability evidence. Reconcile policy, coverage generator and validator with explicit scope classification; preserve the frozen discovery evidence and distinguish scope exclusions from native feature removal. Ambiguous mixed surfaces must receive evidence and an owner before this task closes.
2. Remove SPI `RegionShortcut`, peer cache mode/creation, peer-region factory overloads and their native/mock implementations. Preserve `ClientRegionShortcut`, client-local regions and every verified client configuration value. Adapt client tests that currently use peer creation to genuine client-local creation; maintain configuration, ownership, hierarchy and callback assertions.
3. Remove ticket 42's unfinished server additions: `DriverConnection.addCacheServer/getCacheServers`, `DriverCacheServer`, server-side `DriverClientSession`/`DriverClientSubscriptionConfig`, `DriverServerLoad`/probe/adapter/metrics, and `DriverInterestRegistrationEvent/Listener`. Remove corresponding native adapters, shared `CacheServerContract`, mock server probes and contract inheritance. Audit mapper helpers before removing them; retain helpers used by client behavior. **Keep `InterestResultPolicy`** for client interest registration.
4. Audit and remove committed server-only callbacks/configuration: peer membership/role APIs, WAN timestamp/conflict payloads, gateway/queue settings, partition configuration, distributed locks and server-only transaction hooks. Preserve client-visible metadata, event methods, exceptions and client-supported transaction callbacks. Do not assume all callback methods or all data-policy constants are server-only.
5. Remove resulting server-only Spring API/configuration references, including legacy server-shortcut annotation attributes, wrappers, converters and XML surfaces, so affected full source sets still compile. Record breaking migration notes. Keep client migration obligations with their existing owners; do not introduce compatibility stubs, native unwraps or accepted-but-ignored server settings.
6. Remove or relocate server-only test cases with a disposition ledger. Review `NativeServerFixture` and `ServerSubscriptionClientProcess` from ticket 42: discard server-SPI-only scenarios or retain/refactor useful private native fixtures for client behavior. Native servers/locators/function bodies needed to test clients stay inside driver integration infrastructure, without a public hosting SPI. Preserve unrelated user changes in Gradle and all retained client tests.
7. Regenerate coverage artifacts and report required-client, excluded and unresolved counts separately. Reassign retained mixed rows to active client tickets; no required-client row may remain owned only by retired tickets 42/43. Remove stale active server mandates from plans and coverage policies. Record exact removals and remaining consumer owners in the Answer and implementation map.

## Required validation

- Full compilation of affected SPI/core/mock/native/contract and Spring main/unit/integration source sets; preserve the separate Spring Test module's existing dependency exception.
- Shared client contracts and mock/local lifecycle tests; native client/server tests for affected client region operations, callbacks, interest/subscription, PDX and transactions. Record exact resolved native version and honest mock limits.
- Coverage generation/validation with stable IDs, evidence-backed exclusions, no unclassified rows and no public server-only SPI; migration boundary verification and pruning only for justified changes.
- ABI/stamp checks under ticket 39's explicit pre-first-release policy. Do not fabricate a released baseline or disable compatibility checks.
- Verify no executable public peer/server/WAN/queue API remains in the SPI, while real native integration fixtures can still start servers privately. Compile-only success or renaming types does not establish this condition.

## Completion

Resolve only when existing server-only code and test requirements are removed, retained client behavior passes relevant checks, coverage dispositions are complete and no stale dependency requires implementation of tickets 42/43. If this needs bounded children, add them as blockers and keep this parent open. Full Spring migration, native-free acceptance and all-version integration remain owned by tickets 29–41; completing this cleanup alone does not satisfy those gates. Do not commit or publish without instruction.

## Partial progress — working-tree cleanup

The user authorized committing valid work and removing the rest. Discarded ticket 42's 16 untracked server SPI/adapter/contract/fixture files and reverted its additions to connection APIs, contract inheritance, mock probes and native contract setup. Removed the now-unused member unwrapping helper and the IOException translation added for server startup; existing client socket/transaction IOException behavior remains unchanged. The discarded native fixture pair exercised the removed server-session SPI; existing private native client/server callback fixtures remain.

Retained the native-free `InterestResultPolicy` enum for client registration, with `NativeInterestResultPolicyTests` comparing every policy predicate and the default against native GemFire. This retains a configuration value, not an implementation of client interest registration; tickets 45/48 remain open. Reverted incidental Gradle 9.8.1 and global `10.4.+` overrides; validation uses the committed Gradle 9.7.0 wrapper and an explicit native version pin.

This completes only the unfinished working-tree cleanup in item 3 and the fixture disposition in item 6. Previously committed peer/server APIs (including `RegionShortcut`), consumer migrations and coverage classification remain outstanding; this task stays open.

Validation passed with native **10.4.0-build.3056.df98c835**: 158 JUnit tests (24 SPI, 58 mock, 31 core, 45 focused native), zero failures/errors/skips; mock native-free classpath verification; 32 compiled ABI/stamp fixtures and packaged core/provider stamp checks. The ABI gate correctly reports no released baseline. No full Spring compilation or coverage reclassification is claimed by this bounded cleanup.

```sh
./gradlew --offline -DgemfireVersion=10.4.0-build.3056.df98c835 \
  :spring-data-gemfire-driver-spi:test \
  :spring-data-gemfire-driver-mock:test \
  :spring-data-gemfire-driver-mock:verifyNativeFreeClasspath \
  :spring-data-gemfire-driver-10.4:integrationTest \
  --tests '*NativeInterestResultPolicyTests' \
  --tests '*GemFire104DriverContractTests' verifyDriverCompatibility
```

## Partial progress — classification prerequisite

[Task 60](60-client-scope-coverage-classification.md) is resolved: coverage policy/generation/validation now distinguishes 96 required-client, 549 excluded-server and 3,206 unresolved entries without changing the frozen baseline or historical implementation statuses. Fourteen retained/unresolved rows have active owners replacing superseded task 43. Ten scope tests, deterministic regeneration and 372 independent native declaration checks pass. This supplies the classification infrastructure in items 1/7; it does not complete all decisions or remove runtime APIs.

The remaining bounded children are [61 — creation](61-remove-peer-cache-and-region-creation.md), [62 — callbacks/configuration](62-remove-peer-callbacks-and-server-configuration.md), [63 — Spring surfaces](63-retire-spring-server-configuration-surfaces.md) and [64 — final reconciliation](64-complete-client-scope-reconciliation.md). Children 61/62 are now resolved; child 63 is next. This parent remains open until all cleanup, retained-client regressions and zero-unresolved acceptance pass.

## Partial progress — client-only creation

[Task 61](61-remove-peer-cache-and-region-creation.md) completes item 2: public peer cache mode, `RegionShortcut` and peer region/subregion creation are removed from SPI/native/mock. Shared contracts use clients; native client PDX has a real server/pool and all five LOCAL shortcut variants retain native attribute checks. The [test disposition ledger](../wayfinder-impl/investigations/client-only-creation.md) assigns remaining private peer fixtures to task 62. Full affected compilation and 520 JUnit executions pass, with mock isolation, ABI/stamps, boundary and coverage validation.

The 3,851 frozen API IDs remain. Removing 54 stale partial mappings yields 2,975 missing / 596 partial / 280 implemented; independent scope remains 96 required-client / 549 excluded-server / 3,206 unresolved. Callback/configuration removal (62), Spring surfaces (63) and complete reconciliation (64) remain mandatory. This parent stays open.

## Child 62 outcome — 2026-10-23

[Task 62](62-remove-peer-callbacks-and-server-configuration.md) resolves callback/configuration cleanup and every private peer fixture disposition from task 61. The [evidence ledger](../wayfinder-impl/investigations/client-only-callbacks-and-configuration.md) records removed hooks and real client transaction/subscription coverage. 510 JUnit executions, full affected compilation, mock isolation, ABI/stamps, boundary and coverage checks pass. All frozen IDs remain: 362 required-client / 625 excluded-server / 2,864 unresolved; 3,111 missing / 484 partial / 256 implemented. Client transaction outcome/event scope remains explicit task 64 work. Spring surface retirement (63) is next; this parent stays open.


## Child 63 outcome — 2026-10-27

[Task 63](63-retire-spring-server-configuration-surfaces.md) is resolved. Its [ledger](../wayfinder-impl/investigations/client-only-spring-surfaces.md) records server configuration/function/feature retirement, retained client XML, breaking changes and test dispositions. The inherited-attributes slice passes 82 focused tests; the final feature-catalog follow-up passes 15, without failures/errors/skips. Affected compilation, transitional boundary and refreshed coverage/declaration checks pass. Frozen scope remains 362 required-client / 625 excluded-server / 2,864 unresolved.

Task 64 is next and all its blockers are resolved. This parent remains open for member-level reconciliation, mixed transaction outcomes, private server function fixture relocation and final acceptance. Remaining client native coupling stays assigned to downstream migration owners; this is not native-free compliance.
