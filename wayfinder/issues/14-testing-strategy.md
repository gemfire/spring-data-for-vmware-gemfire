Type: grilling
Status: resolved

## Question

What is the testing strategy for Spring Data GemFire once it has no native GemFire on its compile classpath — specifically, whether an official mock Driver Impl becomes the unit-test runtime, whether unit tests are build-enforced to stay native-free, and how integration testing against real Driver Impls is structured?

## Answer

**Official mock Driver Impl.** An official mock Driver Impl (implementing the SPI, distributed as its own module) becomes the standard unit-test runtime, replacing today's ad-hoc per-test Mockito mocking of native GemFire types. It tests against the actual SPI contract boundary — the same one every real Driver Impl implements — rather than mocks of internals, giving one supported way to unit-test SPI-based code instead of each test hand-rolling its own mocks.

**Zero native GemFire enforced on the unit-test classpath.** The `src/test` (unit test) classpath is build-enforced to contain no native GemFire — only `driver-spi` and the mock Driver Impl — mirroring the same invariant already enforced for main code. This catches any accidental native leakage into unit tests immediately, rather than it surfacing only when integration tests run against real Driver Impls.

**Integration test matrix shape: one `integrationTest` configuration per Driver Impl version.** Rather than parameterizing a single `integrationTest` source set/task to loop over every supported Driver Impl, the build defines a separate `integrationTest` Gradle source set/configuration per Driver Impl version in the initial support matrix, each wired to run the (shared) integration test suite against that specific Driver Impl's real GemFire dependency. This keeps per-version classpaths, dependency resolution, and any version-specific test exclusions cleanly isolated from each other, rather than forcing one configuration to juggle multiple GemFire versions' dependencies at once.

## Amendment — extended to Axis B native-engine-range CI enforcement

Added during [ticket 15](15-driver-impl-self-declared-gemfire-compatibility-mechanics.md)'s sign-off, symmetric with how [ticket 18](18-spi-impl-generation-range-compatibility.md) already extends this ticket for Axis A (SPI generation-range CI enforcement).

A Driver Impl's self-declared native-engine range, `[nativeMin, nativeMax]` (real GemFire product version numbers — see ticket 15), is only trustworthy if backed by the same per-version `integrationTest` configurations described above: a passing `integrationTest` configuration must exist for every native GemFire version in that declared range, and this coverage is re-verified on an ongoing basis (not just checked once at Driver Impl release). This is the same continuous-CI-enforcement discipline ticket 18 requires for the SPI generation range — the two axes (SPI-generation compatibility and native-engine compatibility) are checked independently at bind time (ticket 15/18), but both are backed by the same governance mechanism: no self-declared range is trustworthy without a corresponding, continuously-run `integrationTest` configuration proving it.

## Open follow-up — marked out of scope

Whether `spring-test-vmware-gemfire` itself should be migrated to depend only on `driver-spi`/the mock Driver Impl (removing its current direct `compileOnly` dependency on native GemFire) was left open pending more thought/research. The user has since marked it **out of scope for this spec** rather than resolving it — `spring-test-vmware-gemfire` keeps its current direct dependency on native GemFire; this module is not migrated as part of this effort. See the map's "Out of scope" section.
