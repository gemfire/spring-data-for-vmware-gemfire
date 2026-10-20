Type: task
Status: open
Blocked by: 40

# Final SPI migration spec-conformance audit

## Question

Independently verify that all recorded gaps are closed and the implementation meets the signed-off migration destination.

Audit coverage: **G1, G2, G3, G4, G5, G6, G7, G8** in the [native/SPI gap audit](../wayfinder-impl/audits/native-spi-gap-audit.md). Apply the [shared completion requirements](../wayfinder-impl/spi-gap-closure-plan.md#shared-completion-requirements) and the signed-off [technical spec](../wayfinder/technical-spec.md).

## Required implementation

Prerequisites: [Complete driver contract and native integration matrix](40-complete-driver-contract-and-native-integration-matrix.md).

- Re-audit spec §§1, 3–7 and 10 against source, public signatures, resources, resolved dependencies and the complete API coverage matrix.
- Verify all required rows and implementation children are closed with evidence; no callback/event loss, native public escape, permanent dual facade or baseline exclusion remains.
- Confirm strict boundaries, exception semantics, ABI/stamp/static-state checks, full mock/native test coverage and the Spring Test scope exception are correctly enforced.
- Update the map and audit with exact results and remaining release prerequisites. This ticket prepares technical cutover readiness; it does not authorize publishing, merging or selecting a new support policy.

## Required validation

- Run clean full compilation, strict architecture checks, full unit suites, selected native integration matrix and coverage consistency validation.
- Report independently observed results and limitations; do not reuse historical pass counts as current acceptance evidence.

## Completion

Evidence-backed technical migration acceptance, or explicit reopened blocking issues; release publication/merge remains separate. Record the Answer with changed source/API rows, commands/results, native version and limitations; update the implementation map. Do not mark dependent work complete from this ticket's result.
