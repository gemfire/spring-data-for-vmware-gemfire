# Tests Moved from Unit Tests to Integration Tests

The following test files were moved from `src/test` to `src/integrationTest` because they
depend on GemFire server infrastructure or internal APIs that cannot be abstracted through
the GUD API layer. The `testImplementation` configuration no longer includes the GemFire
dependency; only `integrationTestImplementation` retains it.

## Moved Files

### LocatorProcess.java
- **From:** `src/test/java/org/springframework/data/gemfire/fork/LocatorProcess.java`
- **To:** `src/integrationTest/java/org/springframework/data/gemfire/fork/LocatorProcess.java`
- **Reason:** Directly starts a GemFire Locator using `LocatorLauncher`, `InternalLocator`,
  and `Locator` — all server-side infrastructure with no GUD-api equivalent.

### FunctionGemfireAdminTemplateUnitTests.java
- **From:** `src/test/java/org/springframework/data/gemfire/config/admin/remote/FunctionGemfireAdminTemplateUnitTests.java`
- **To:** `src/integrationTest/java/org/springframework/data/gemfire/config/admin/remote/FunctionGemfireAdminTemplateUnitTests.java`
- **Reason:** Uses `org.apache.geode.management.internal.cli.domain.RegionInformation` and
  `org.apache.geode.management.internal.cli.functions.GetRegionsFunction` — internal GemFire
  management types with no GUD-api equivalent.

### GemfireDataSourcePostProcessorTest.java
- **From:** `src/test/java/org/springframework/data/gemfire/client/GemfireDataSourcePostProcessorTest.java`
- **To:** `src/integrationTest/java/org/springframework/data/gemfire/client/GemfireDataSourcePostProcessorTest.java`
- **Reason:** Uses `org.apache.geode.management.internal.cli.domain.RegionInformation` and
  `org.apache.geode.management.internal.cli.functions.GetRegionsFunction` — internal GemFire
  management types with no GUD-api equivalent.

## Remediation Notes

These tests should eventually be refactored to either:
1. Abstract the management/admin operations through GUD-api interfaces, or
2. Remain as integration tests that verify end-to-end behavior with a real GemFire cluster
