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
