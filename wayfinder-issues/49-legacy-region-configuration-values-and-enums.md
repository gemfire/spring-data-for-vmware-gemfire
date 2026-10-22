Type: task
Status: open
Blocked by: 46, 47, 48, 59

# Client configuration values and enum migration

## Client-only scope — 2026-10-22

Apply the [approved client-only boundary](../wayfinder-impl/client-only-scope.md). [Cleanup ticket 59](59-remove-server-only-spi-and-reconcile-client-scope.md) must complete first. Full/complete coverage in this ticket means every retained client requirement; historical native reachability alone does not require peer/server APIs. Preserve all retained client tests and native-free boundaries.

## Question

Complete native-free values, enum predicates/converters and annotation defaults actually needed by retained client configuration.

## Required implementation

- Audit client annotations/configurers/XML and map required values without copying full native enums. Preserve unset/default semantics and valid client-local persistence settings.
- Retire RegionShortcut and obsolete server-only aliases/configuration with ticket 59. Review MirrorType, legacy disk settings and configuration DTO nodes individually; retain only demonstrated client contracts, using native-free values rather than mirroring server configuration schemas.
- Map every historical row to a retained client owner or an evidence-backed scope exclusion. Server authorization OperationContext/OperationCode hooks are not client configuration requirements; client authentication and denial handling remain with ticket 24.

## Required validation

Exercise retained client annotation defaults, converters, Java/XML contexts and native value mappings. Verify excluded server configuration is absent rather than silently ignored.

## Completion

All retained client values and defaults are native-free and behavior-validated. Record row dispositions and remaining consumer owners; ticket 19 stays open until its other children complete.
