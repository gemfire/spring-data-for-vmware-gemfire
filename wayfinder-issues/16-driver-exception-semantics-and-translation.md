Type: task
Status: open
Blocked by: 14

# Driver exception semantics and translation

## Question

Represent the native failure distinctions required by Spring Data through SPI exceptions without requiring downstream native type inspection.

Audit coverage: **G3, G6** in the [native/SPI gap audit](../wayfinder-impl/audits/native-spi-gap-audit.md). Apply the [shared completion requirements](../wayfinder-impl/spi-gap-closure-plan.md#shared-completion-requirements) and the signed-off [technical spec](../wayfinder/technical-spec.md).

## Required implementation

Prerequisites: [Required native API coverage matrix](14-required-native-api-coverage-matrix.md).

- Inventory GemfireCacheUtils, transaction, query, function and callback error classifications and map them to stable SPI failure semantics and Spring DataAccessException outcomes.
- Extend SPI exception/translation contracts and native boundary mapping as needed, preserving causes and safe diagnostics; keep unsupported NOT_INTRODUCED/REMOVED separate from execution failures.
- Update currently migrated consumers and provide the common translation path for later subsystem tickets. Do not replace established duplicate/conflict/auth/query classifications with a single uncategorized result.
- Assign each remaining native exception caller to its consumer cutover ticket; retire the final native GemfireCacheUtils dispatch with residual cleanup once all callers move.

## Required validation

- Verify representative native error categories and resulting Spring exceptions, original causes, safe top-level messages, unknown errors and typed unsupported failures.
- Test callback veto/failure propagation through driver boundaries; guard against interpreting errors as absent results or successful no-ops.

## Completion

Typed failure/translation foundation and matrix evidence; remaining caller conversions have explicit owners. Record the Answer with changed source/API rows, commands/results, native version and limitations; update the implementation map. Do not mark dependent work complete from this ticket's result.
