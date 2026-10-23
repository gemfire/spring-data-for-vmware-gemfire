Type: task
Status: resolved
Blocked by: 14, 15

# Add explicit client scope to the coverage inventory

## Question

Implement the classification prerequisite of task 59. Preserve every frozen API ID and historical implementation status. Add independent `required-client`, `excluded-server` and `unresolved` scope dispositions, with evidence, rationale and an active review/removal owner. Unresolved rows remain obligations until reviewed; exclusion must never mean native `removed` or completed implementation.

Record initial decisions for peer/client creation and unambiguous hosting, WAN and peer callback receivers. Decisions must enumerate reviewed IDs so newly discovered overloads cannot inherit exclusions automatically. Keep mixed contracts, shared exceptions and client metadata unresolved until member-level evidence exists. Retained rows cannot be assigned solely to superseded tickets 42/43.

Generate scope counts separately from implementation counts, propagate scope to source/SPI mappings, and validate policy drift, missing evidence, stale IDs, conflicting decisions and improper exclusions. Task 64 owns remaining decisions; this child does not remove production APIs or close task 59.

## Validation

Deterministic regeneration/check, independent native declaration comparison, negative controls for coverage/scope failures and unchanged frozen baseline/IDs. No native-free or runtime behavior claim follows from inventory validation.

## Answer — 2026-10-23

Implemented independent scope classification in the coverage generator and validator, with reviewed exact IDs in `client-scope-policy.json`, shared policy validation in `scope_policy.py` and evidence in `wayfinder-impl/investigations/client-scope-classification.md`. Counts are **96 required-client**, **549 excluded-server**, **3,206 unresolved**. Unknown/new overloads remain unresolved; the final zero-unresolved gate belongs to task 64.

All 3,851 frozen IDs, native declarations and historical implementation statuses are preserved; `native-api-baseline.json` is byte-identical to the preceding commit. Fourteen EventSequenceID/GatewayException rows move from superseded task 43 to active scope review task 64; the six exception remainder descriptions now reflect that reassignment. Source references and existing SPI mappings carry scope, while manifest totals separate scope from implementation status. Policy, evidence and approved scope are fingerprinted. Exclusion neither deletes a frozen row nor marks a native feature removed.

Validation passed against **10.4.0-build.3056.df98c835**: ten policy tests; deterministic generation/check; 372 independent public declarations across 21 types; all ownership/source/ID checks for 3,851 API rows, 144 production imports and 624 source references; duplicate/deleted-inventory negative controls; unchanged frozen baseline comparison. Commands:

```sh
python3 -m unittest discover -s wayfinder-impl/coverage -p 'test_*.py' -v
python3 wayfinder-impl/coverage/build_api_coverage.py
python3 wayfinder-impl/coverage/build_api_coverage.py --check
python3 wayfinder-impl/coverage/validate_coverage.py --javap
```

No Java/runtime API or runtime test was changed, and no full compilation/native-free acceptance is claimed. Task 59 remains open through creation removal (61), callbacks/configuration (62), Spring surfaces (63) and complete reconciliation (64). Changes are uncommitted pending instruction.
