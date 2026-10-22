Type: task
Status: open
Blocked by: 54, 59

# Reflection-based PDX serialization

## Client-only scope — 2026-10-22

Apply the [approved client-only boundary](../wayfinder-impl/client-only-scope.md). [Cleanup ticket 59](59-remove-server-only-spi-and-reconcile-client-scope.md) must complete first. Full/complete coverage in this ticket means every retained client requirement; historical native reachability alone does not require peer/server APIs. Preserve all retained client tests and native-free boundaries.

## Question

Child of [PDX, serialization and JSON SPI](26-pdx-serialization-and-json-spi.md). Apply the [shared completion requirements](../wayfinder-impl/spi-gap-closure-plan.md#shared-completion-requirements).

Implement ReflectionBasedAutoSerializer configuration, lifecycle, customization hooks, FieldType constants/defaults/widths and typed region service access. Resolve getManager as an internal boundary without native leakage. Verify inclusion/exclusion, identity, naming/type/transform hooks, reconfiguration and real serialization; explicitly document/reject mock-inapplicable reflection/wire promises.

## Completion

Record exact API ownership, source/test evidence, resolved native artifact and validation commands/results. Keep the parent open until all children and required behavior are complete; Spring consumer migration remains downstream.
