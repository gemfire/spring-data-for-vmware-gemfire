Type: task
Status: resolved
Blocked by: 61

# Remove peer callbacks and server configuration

## Question

Complete task 59's SPI/native/mock cleanup after client creation. Audit and remove peer membership/role callbacks, WAN timestamps/conflict payloads, gateway/queue and partition-hosting settings, distributed locks and server-only transaction hooks. Verify exact-version client callability for mixed transaction, event, attribute and metadata APIs. Preserve client interest/subscription, local persistence, serialization and client-supported callbacks; a native data-policy name containing REPLICATE is not sufficient evidence for exclusion.

Migrate or retire affected contract/native/mock tests with explicit dispositions. Keep private native servers/locators/functions only where needed to test retained client behavior. Peer transactions or callback fixtures must not stand in for client coverage. Update stable coverage decisions and retain active owners for unresolved rows.

## Validation

Compile all affected driver source sets; run shared contracts, real client/server transaction/callback/serialization tests, mock native-free checks, ABI/stamps and coverage/boundary validation. No executable public hosting/WAN/peer API may remain. Task 59 stays open through tasks 63/64. Do not commit without instruction.

## Incoming fixture dispositions from task 61

The [creation cleanup ledger](../wayfinder-impl/investigations/client-only-creation.md#private-peer-fixture-ledger--removal-owner-task-62) enumerates the retained private peer cases in `NativeTransactionTests`, `NativeCallbackCoverageTests` and `NativeDistributedCallbackTests`, plus locator/peer commands in `CallbackPeerProcess` and `NativeServerFixture`. Resolve every disposition before closing this task: migrate retained behavior to real client/server tests and retire server-only assertions with their hooks. Private peer tests cannot satisfy client coverage. Preserve existing genuine client subscription, proxy, PDX and transaction cases and the private servers they need.

## Resolution — 2026-10-23

Removed peer membership/role and WAN event interfaces, server configuration/queue mutators, network search, transaction writers and the unsupported distributed transaction mode. Retained ordinary client callbacks, metadata, local persistence and real client transactions. Every incoming private peer fixture has an explicit migrated/retired disposition in the [evidence ledger](../wayfinder-impl/investigations/client-only-callbacks-and-configuration.md), including exact-version negative controls and validation commands.

All driver checks, both focused Spring cache runtimes, affected full compilations, mock native-free verification, ABI/stamps and migration boundary pass: **510 JUnit executions**, zero failures/errors/skips. Coverage policy tests, deterministic generation and independent checks of 372 declarations pass. The 3,851 frozen IDs now classify as 362 required-client / 625 excluded-server / 2,864 unresolved; 136 stale mappings are cleared. Current SPI declarations: 123 defaults / 26 callbacks.

Client transaction listener registration/cleanup works, but real outcomes in these tests emit no notifications. Outcome/event mappings remain partial and explicitly owned by task 64 for final scope reconciliation. Task 63 is next; parent 59 stays open through Spring retirement and reconciliation. This does not claim the full Spring suite is native-free or establish a released ABI baseline. No commit made.
