Type: task
Status: open
Blocked by: 40, 59

# Final SPI migration spec-conformance audit

## Client-only scope — 2026-10-22

Apply the [approved client-only boundary](../wayfinder-impl/client-only-scope.md). [Cleanup ticket 59](59-remove-server-only-spi-and-reconcile-client-scope.md) must complete first. Full/complete coverage in this ticket means every retained client requirement; historical native reachability alone does not require peer/server APIs. Preserve all retained client tests and native-free boundaries.

## Question

Independently verify that all recorded gaps are closed and the implementation meets the signed-off migration destination.

Audit coverage: **G1, G2, G3, G4, G5, G6, G7, G8** in the [native/SPI gap audit](../wayfinder-impl/audits/native-spi-gap-audit.md). Apply the [shared completion requirements](../wayfinder-impl/spi-gap-closure-plan.md#shared-completion-requirements) and the signed-off [technical spec](../wayfinder/technical-spec.md).

## Required implementation

Prerequisites: [Complete driver contract and native integration matrix](40-complete-driver-contract-and-native-integration-matrix.md).

- Re-audit spec §§1, 3–7 and 10 against source, public signatures, resources, resolved dependencies and the complete API coverage matrix.
- Verify all required rows and implementation children are closed with evidence; no callback/event loss, native public escape, permanent dual facade or unjustified baseline exclusion remains. Approved server-scope exclusions require ticket 59 evidence.
- Confirm strict boundaries, exception semantics, ABI/stamp/static-state checks, full mock/native test coverage and the Spring Test scope exception are correctly enforced.
- Update the map and audit with exact results and remaining release prerequisites. This ticket prepares technical cutover readiness; it does not authorize publishing, merging or selecting a new support policy.

## Required validation

Audit the ticket 59 disposition ledger and public SPI: no peer/server/WAN hosting APIs, unexplained scope exclusions or required-client rows owned only by retired tickets. Require the full native-free client unit suite plus per-driver compatibility/integration evidence.

- Run clean full compilation, strict architecture checks, full unit suites, selected native integration matrix and coverage consistency validation.
- Report independently observed results and limitations; do not reuse historical pass counts as current acceptance evidence.

## Completion

Evidence-backed technical migration acceptance, or explicit reopened blocking issues; release publication/merge remains separate. Record the Answer with changed source/API rows, commands/results, native version and limitations; update the implementation map. Do not mark dependent work complete from this ticket's result.
