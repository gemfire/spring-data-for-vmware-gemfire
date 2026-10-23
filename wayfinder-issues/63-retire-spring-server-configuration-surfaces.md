Type: task
Status: open
Blocked by: 61, 62

# Retire Spring server configuration surfaces

## Question

Remove the Spring server-only APIs, annotation attributes, wrappers, converters, XML/schema exports and tests identified by task 59. Record breaking migration notes and test dispositions. Preserve client-local configuration, client callbacks and all retained assertions; no accepted-but-ignored settings, native unwraps or compatibility stubs. Leave remaining client migration with the existing tasks 29–38.

## Validation

Compile full Spring main/unit/integration and separate Spring Test source sets under its existing dependency exception. Run affected retained Java/XML configuration tests, regenerate source/coverage evidence and prune only justified migration-boundary entries. No full native-free claim until tasks 37/38. Task 59 remains open through task 64. Do not commit without instruction.

## Progress — 2026-10-23

Prerequisites 61/62 are resolved. Removed Spring peer shortcut/scope/subscription wrappers and converters, server annotation/bean properties, transaction writer hooks/composition, peer template/schema exports and parser helpers. Retained client annotation/XML fixtures and Spring Test support are updated together; see the [migration and test ledger](../wayfinder-impl/investigations/client-only-spring-surfaces.md).

Full affected source compilation and 105 focused tests pass without failures or skips. Ten coverage policy tests, deterministic generation, independent native declaration validation and the transitional migration boundary pass. Frozen API IDs and scope counts remain unchanged; this task is not resolved.

Still open:

- [ ] Review and retire server properties in `EnableGemFireProperties`, `GemFirePropertiesConfiguration` and `GemFireProperties` with client retention evidence.
- [ ] Retire function registration/hosting exports and relocate required server fixtures while preserving client invocation.
- [ ] Remove the inherited server API from `RegionAttributesFactoryBean` through SPI-backed client configuration and consumer migration. Resolve overlap with task 30 without a circular dependency through parent 59.
- [ ] Complete the remaining surface audit, test dispositions and final evidence refresh before resolution.
