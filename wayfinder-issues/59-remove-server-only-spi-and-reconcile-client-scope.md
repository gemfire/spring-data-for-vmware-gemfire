Type: task
Status: open
Blocked by: 14, 15, 17, 18, 28, 39, 46

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
