Type: task
Status: resolved
Blocked by: 60, 61, 62, 63

# Retire legacy server test-JAR consumers

## Fixed scope

This child of [task 64](64-complete-client-scope-reconciliation.md) owns the two remaining consumers in the [fixture relocation ledger](../wayfinder-impl/investigations/client-function-fixture-relocation.md): `GemfireTemplateIntegrationTests` (deployed domain data) and `AutoConfiguredAuthenticationConfigurationIntegrationTests` (security fixture). It owns no API inventory IDs. Apply the [bounded audit work rules](../wayfinder-impl/investigations/client-scope-audit-plan.md#work-rules).

## Completion criteria

- [x] Inventory the exact classes/resources each server needs and place native server security implementation in private driver integration infrastructure. Keep shared domain/protocol data native-free and deploy only the needed payload.
- [x] Switch both consumers off the combined Spring test JAR, retaining their client behavior and authentication assertions. Preserve client/server class availability and serialization configuration.
- [x] Remove obsolete server deployment wiring and remove the combined test-JAR task only if no remaining legitimate consumer needs it. Record any legitimate non-server use explicitly; do not delete retained tests or public client XML.
- [x] Extend packaging checks so private server implementation cannot reappear on Spring client classpaths or in client artifacts. Verify both consumers use the intended isolated payload.
- [x] Obtain user-run affected compilation, both suites, packaging verification and migration boundary verification/pruning. Refresh the fixture ledger with exact counts, version, commands and remaining limitations.

Stop when these two consumers and their packaging are reconciled. Full Spring mock/native-free test migration remains task 37; per-version production transport packaging remains tasks 25/40. Do not add general test-platform redesign or depend on tasks 59/64/downstream implementation, which would create a dependency cycle.

## Security scope coordination from task 70 — 2026-10-28

The [security audit](../wayfinder-impl/investigations/client-security-and-authentication-scope.md#ownership-and-reconciliation) excludes SecurityManager hosting but explicitly retains the authentication assertions in AutoConfiguredAuthenticationConfigurationIntegrationTests. Its current TestSecurityManager is loaded on server/locator processes via TEST_JAR_PATH; this is the existing security-fixture consumer in this task's fixed scope. Move the manager and nested principal implementation into private driver infrastructure; separate any client-shared credentials/protocol data into a native-free payload. Do not treat server fixture use as a public SPI requirement or claim current Spring unit-test placement satisfies the final boundary. The separate Spring Test module remains under its existing exception.

## Implementation checkpoint — 2026-10-29

All five criteria are complete following user-run validation.
The template payload contains one JDK-only User class. The security payload contains
the private manager, nested principal and JDK-only credentials holder. Neither
payload is added to a Spring Java classpath. The combined testJar task/property
is removed, and verifyClientServerFixturePackaging checks all three deployment
payloads and rejects private server classes in client classpaths/production JARs.
See the [fixture ledger](../wayfinder-impl/investigations/client-function-fixture-relocation.md#task-77-isolated-domain-and-security-payloads--2026-10-29)
for exact inventories, retained assertions, limitations and the pending command.
The user reported the requested Gradle command passed. Local XML confirms 22 tests
(21 template, one authentication), zero failures/errors/skips. Fresh migration
findings match the pruned baseline with zero new or stale identities. Task 77 is
resolved; tasks 64/59 retain final reconciliation, not additional fixture relocation.
