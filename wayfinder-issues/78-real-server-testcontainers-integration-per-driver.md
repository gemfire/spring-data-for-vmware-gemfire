Type: task
Status: open
Blocked by: 38, 59

# Real-server Testcontainers integration tests for every driver implementation

## Client-only scope — 2026-10-31

Apply the [approved client-only boundary](../wayfinder-impl/client-only-scope.md). Only client behavior is tested; the server is a fixture, not an SPI surface.

## Question

Prove that each driver implementation works with its native client libraries against a real GemFire server, not only against LOCAL client regions or the mock.

Today the driver modules have no server-backed tests. The 10.4 `src/integrationTest` suites (for example `NativeQueryTests` and the native contract tests) use `ClientRegionShortcut.LOCAL` with no server. Server-backed paths are therefore unexercised: pool-backed query service, region queries and CRUD routed through a pool, subscriptions, durable clients, authentication, functions, transactions and CQ. The only Testcontainers usage is in the legacy `spring-data-vmware-gemfire/src/integrationTest` tests through `com.vmware.gemfire.testcontainers.GemFireCluster`; no `spring-data-gemfire-driver-*` build file references Testcontainers.

## Required implementation

Prerequisites: [Whole-module native dependency removal](38-whole-module-native-dependency-removal.md); [Server-only cleanup and scope reconciliation](59-remove-server-only-spi-and-reconcile-client-scope.md).

- Add a reusable Testcontainers fixture for driver tests, reusing the `GemFireCluster` pattern (locator plus server containers, image selected by a system property). It must live where every driver module can consume it and must not put native types on the SPI.
- Wire a dedicated Gradle task per driver module (for example `:spring-data-gemfire-driver-10.4:serverIntegrationTest`) so server-backed tests are separate from the fast local-region `integrationTest` task and are skipped, with a clear message, when Docker is unavailable.
- For each driver implementation, run the shared driver contract against a real server through a pool, and add native mapping tests for each server-dependent surface already implemented: pool and routing, region CRUD through a pool, pool-backed query service and region query helpers, and the other server-dependent SPI areas as their tasks land (subscriptions/interest, CQ, functions, transactions, security, PDX/JSON).
- Pin the server image to the same GemFire version as the driver's native artifact and record both in test output.
- Any new driver version added later must add its own server-backed task and results before it is considered supported.

## Required validation

- Each supported driver has a passing server-backed run with exact native client version, server image and SPI ABI recorded.
- Server-backed tests cover at least one scenario for every SPI area that has server-dependent behavior; areas without a scenario are listed as open gaps, not counted as covered.
- Tests do not run in the default `test` or `integrationTest` tasks, and the mock contract still requires no server.

## Completion

Record the Answer with the fixture location, task names, commands and results, native versions and image tags, and any area without real-server coverage. Update the implementation map and [ticket 40](40-complete-driver-contract-and-native-integration-matrix.md), which must consume this ticket's results for its real server-backed scenarios. Do not mark dependent work complete from this ticket's result.

Stop when Docker or the server image cannot be provided in the environment; record the blocker instead of substituting local-region tests.
