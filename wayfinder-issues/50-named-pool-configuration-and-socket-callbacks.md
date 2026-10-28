Type: task
Status: resolved
Assignee: Codex
Blocked by: 13, 14, 16

# Named pool configuration and socket callbacks

## Question

Child of [Complete pool, default-cache and durable SPI](20-complete-pool-default-cache-and-durable-spi.md). Apply the [shared completion requirements](../wayfinder-impl/spi-gap-closure-plan.md#shared-completion-requirements).

Implement immutable optional settings for every PoolFactory setting; typed merge and effective inspection; native socket callback identity/IOException adaptation; explicit rejection of removed thread-local settings. Preserve the existing named-pool constructor/operations and provider defaults. Mock supports local configuration records only and must not simulate remote effects.

Validate all setters/getters against the pinned native artifact, including conflicting limits, endpoints, unset/default versus explicit values, duplicate names, and a custom socket used by a real server operation. Add shared behavior and older-provider default-method tests. Run driver, focused Spring and whole-module compilation/boundary checks. Update exact coverage rows with downstream consumer ownership.

## Completion

Record source/API mappings, native artifact, commands and behavior evidence in an Answer. Resolve only this child, keeping the complete parent and downstream cutover owners open.

## Answer

Resolved 2026-10-22. [Implementation and validation evidence](../wayfinder-impl/investigations/named-pool-configuration-and-sockets.md) records the mappings, native observations and limits.

- Added 25 optional named-pool settings, immutable fallback/endpoint precedence, effective `DriverPool.configuration()` inspection and native-free socket callbacks with instance-owned defaults. Existing constructors and named lifecycle remain available.
- Native factories apply explicit values and retain engine validation/defaults. Socket identity and checked failures survive adaptation; a separate server fixture proves the custom socket is used for actual put/get operations. Explicit thread-local requests fail as removed on the exact native version.
- Mock supports configuration metadata and local validation/lifecycle only, without DNS, socket invocation or remote queue/authentication/statistics claims.
- Coverage remains 3,851 rows / 292 types: 3,066 missing, 506 partial, 279 implemented. This child owns 87 rows (84 partial pending consumer adoption/default constants, three implemented socket-contract rows). All 105 current SPI defaults have behavior probes/mappings.
- Validation: **476 tests, zero failures/errors/skips** across SPI (21), core (24), contract kit (6), mock (48), native (71), Spring mock (153), Spring native (153); 18 boundary fixture tests also passed. Full Spring Data main/unit/integration and Spring Test main/unit compilation passed. Normal migration boundary, coverage regeneration/check, independent javap comparison and diff whitespace checks passed. Full command and artifact are recorded in the linked evidence.

The full pool parent remains open, blocked by [Default client-cache pool assembly and selection](51-default-client-cache-pool-assembly-and-selection.md) and [Durable client readiness and pool runtime lifecycle](52-durable-client-readiness-and-pool-runtime-lifecycle.md). Every original downstream dependency remains. No Spring consumer was migrated or native-free compliance claimed; no commit was created.

## Task 69 downstream ownership reconciliation — 2026-10-28

The [pool/durable audit](../wayfinder-impl/investigations/client-pool-and-durable-scope.md) transfers 84 unfinished partial configuration/default/factory rows to open parent task 20, with task 35 adoption. This foundation remains resolved: its validated socket/configuration mappings and statuses are preserved, including separately classified SocketFactory rows outside task 69. Remaining API/consumer completion now has an active owner rather than relying on this closed task.
