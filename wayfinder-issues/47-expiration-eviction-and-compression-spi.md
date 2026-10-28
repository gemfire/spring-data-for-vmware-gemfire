Type: task
Status: open
Blocked by: 46, 59

# Expiration, eviction and compression SPI

## Client-only scope — 2026-10-22

Apply the [approved client-only boundary](../wayfinder-impl/client-only-scope.md). [Cleanup ticket 59](59-remove-server-only-spi-and-reconcile-client-scope.md) must complete first. Full/complete coverage in this ticket means every retained client requirement; historical native reachability alone does not require peer/server APIs. Preserve all retained client tests and native-free boundaries.

## Question

Implement expiration actions/attributes/reset policies, CustomExpiry and entry contexts, client-supported eviction algorithms/actions/factories/mutators, ObjectSizer and compressor callbacks/defaults. Add region creation/readback/mutation integration. Verify native expiration, eviction, overflow and compression effects plus callback lifecycle; local mock must reject unavailable behavior. Migrate independently separable Spring values/converters and record remaining consumer owners.

Required implementation child of [Region configuration, enums, expiration and eviction](19-region-configuration-enums-expiration-and-eviction.md). Apply the [shared completion requirements](../wayfinder-impl/spi-gap-closure-plan.md#shared-completion-requirements). All historical rows require a disposition; only client-required rows remain implementation obligations. Exclude peer-only actions/settings through ticket 59.

## Completion

Task 63 removes the generic RegionAttributesFactoryBean and routes XML settings directly to the existing client factory. Those settings and the expiration/eviction wrapper beans still use native types. This task must provide their typed SPI creation/readback/mutation contracts; task 30 must migrate the client factories and parser/annotation consumers. Removing the generic attributes factory does not implement these contracts or change this task's acceptance criteria. This split permits cleanup parent 59 to finish before its dependent SPI implementation tasks.

Record changed contracts and member mappings, executable native/mock evidence and limitations. Parent configuration acceptance remains open until all children and its full-surface validation pass.

## Task 68 implementation handoff — 2026-10-28

The [fixed scope audit](../wayfinder-impl/investigations/client-expiry-eviction-and-compression-scope.md) retains **154 exact IDs across 14 types**, all still missing and owned here; task 30 owns adoption. Seven evidence batches cover 26 region configuration members, 34 expiry value members, three binary methods, five custom-expiry members, 71 eviction members, five sizing members and ten compression members. No server exclusions or new runtime scope probes were needed.

Implement typed creation/readback/mutation with proxy/local-storage restrictions, explicit statistics/cloning choices, old-value/callback identity, TTL reset/rescheduling and null/default behavior. Preserve the source-backed constructor-versus-binary hydration distinction, inherited value members, LIFO controller acceptance, action-based no-eviction observation, live maximum update ordering, sizer strategies and Snappy construction/failure behavior. Coordinate task 49 conversion and task 55 codecs. The evidence ledger contains the exact native paths and value quirks; do not replace them with generic configuration properties or native types.

For custom expiry, distinguish the inherited close contract from observed lifecycle: replacement returns the old callback without closing it, and inspected shutdown paths omit it. Make ownership explicit and test actual adapter behavior; do not assume automatic closure from the base callback documentation. Shared/native/mock tests must cover retained client effects and explicit unsupported mock operations. Scope closure does not provide implementation or compatibility acceptance.
