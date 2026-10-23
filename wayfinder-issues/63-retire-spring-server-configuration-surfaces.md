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

The subsequent function-registration slice removes the public factory/parser/constant/schema export and obsolete registration-only fixtures. Two new XML rejection tests fail before and pass after removal. All affected sources compile; 18 focused client configuration/proxy tests pass without failures or skips, as do deterministic coverage, independent declaration validation and the transitional boundary. The ledger records the exact commands and the unexecuted real-server test limitation.

The peer-function slice removes member annotations/templates/executors/builders and the unused server result helper. The legacy function-hosting fixture is replaced by a client-local XML loader test. All affected sources compile and 35 focused tests pass. Deterministic coverage, independent declaration validation and the adjusted transitional boundary pass; the ledger records the explicit transitional fixture baseline adjustment and native API evidence. Client XML parsers and region/server invocation remain.

The selected-property slice removes five annotation attributes with their defaults/mappings and seven enum constants for locator hosting, cluster configuration, WAN discovery, server queue handling and GFSH commands. Shared settings are retained pending individual review; the ledger records native client uses of availability zones and distributed-system IDs. Full affected sources compile and 27 focused tests pass without failures or skips. Coverage validation and the transitional boundary pass with eight obsolete occurrences pruned and no new allowances. The overall property audit remains open.

The server placement/endpoint property slice removes three more enum constants (`ENFORCE_UNIQUE_HOST`, `REST_JSON_STORAGE_FORMAT`, `SERVER_BIND_ADDRESS`), the unused unique-host default and two orphan annotation mappings. The client bind address and availability/redundancy-zone properties remain, backed by pinned binary/source evidence. All affected sources compile, 27 focused tests pass, coverage checks pass and three more obsolete boundary entries are pruned without new allowances. See the ledger for retained-property evidence and the remaining unmapped/transport/security audit.

Remaining work and completed slices:

- [ ] Review and retire server properties in `EnableGemFireProperties`, `GemFirePropertiesConfiguration` and `GemFireProperties` with client retention evidence.
- [x] Retire `FunctionServiceFactoryBean`, its parser, bean-name constant and `gfe:function-service` schema export; remove the registration-only suite and unused server XML while preserving client invocation.
- [x] Retire peer-member execution exports and server result helper; replace the legacy native function-registration fixture with client-local lazy-wiring coverage.
- [ ] Finish the function surface audit and record required server fixture placement with tasks 25/32/64; preserve supported client function-object submission.
- [ ] Remove the inherited server API from `RegionAttributesFactoryBean` through SPI-backed client configuration and consumer migration. Resolve overlap with task 30 without a circular dependency through parent 59.
- [ ] Complete the remaining surface audit, test dispositions and final evidence refresh before resolution.
