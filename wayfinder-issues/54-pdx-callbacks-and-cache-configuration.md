Type: task
Status: open
Blocked by: 53, 59

# PDX callbacks and cache configuration

## Client-only scope — 2026-10-22

Apply the [approved client-only boundary](../wayfinder-impl/client-only-scope.md). [Cleanup ticket 59](59-remove-server-only-spi-and-reconcile-client-scope.md) must complete first. Full/complete coverage in this ticket means every retained client requirement; historical native reachability alone does not require peer/server APIs. Preserve all retained client tests and native-free boundaries.

## Question

Child of [PDX, serialization and JSON SPI](26-pdx-serialization-and-json-spi.md). Apply the [shared completion requirements](../wayfinder-impl/spi-gap-closure-plan.md#shared-completion-requirements).

Implement all PdxReader/PdxWriter/PdxUnreadFields/PdxSerializer and application PdxSerializable equivalents, typed cache PDX configuration (readSerialized, ignoreUnreadFields, persistence, disk store, serializer), native adaptation/identity and meaningful local mock behavior. Preserve unread fields across schema evolution, nested callbacks, serializer decline, checked/unchecked errors and cache compatibility. Verify real native round trips and effective client-cache settings. Discover and freeze any required callback types omitted by the initial inventory.

## Completion

Record exact API ownership, source/test evidence, resolved native artifact and validation commands/results. Keep the parent open until all children and required behavior are complete; Spring consumer migration remains downstream.

## Serialization audit handoff — 2026-10-28

Task 71 retains 60 reader/writer/serializer/unread-field rows plus 18 shared cache settings/metadata-registration rows. Test missing defaults versus type failures, manual unread-field ordering/preservation, automatic preservation, serializer decline and nested callback failure translation. Client persistence and disk-store options are accepted/read back but ignored by the pinned client implementation; do not promise client PDX metadata disk persistence. Preserve real metadata serialization/validation. Five peer CacheFactory setters are excluded; use typed client configuration. Task 35 owns entry-point adoption, task 31 callback/mapping adoption. Existing PdxSerializable discovery stays here without enlarging task 71. See [the exact source-backed ledger](../wayfinder-impl/investigations/client-serialization-pdx-and-json-scope.md).
