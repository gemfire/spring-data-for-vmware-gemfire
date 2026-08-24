Type: task
Status: claimed

## Question

Step 1 of the migration plan (`wayfinder/technical-spec.md` §7.1): stand up `spring-data-gemfire-driver-spi` and `spring-data-gemfire-driver-core` as Gradle subprojects with `GemFireDriver`, `GemFireDriverManager`, `Capability`, the exception hierarchy, and the SPI-owned annotation config enums. No behavior yet — types and contracts only (except `GemFireDriverManager`'s resolve/validate logic, which the spec names explicitly as in-scope for this step).

Nothing to decide here beyond mechanical execution of what's already specified in `wayfinder/technical-spec.md` §5 — this is a `task` ticket, not a `grilling`/`prototype`/`research` one.
