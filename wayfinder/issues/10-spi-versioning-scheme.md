Type: grilling
Status: resolved

## Question

How is the SPI itself versioned, and how does a Driver Impl declare which SPI version(s) it's compatible with?

## Answer

**Independent SemVer, with a declared compatibility range.** The SPI is versioned on its own release cadence (not tied 1:1 to a Spring Data or GemFire version number). Each Driver Impl declares the range of SPI versions it requires/supports. At `ServiceLoader` binding time, the Driver Manager checks the impl's declared range against the current SPI version and fails fast, with a descriptive/typed exception, if incompatible — consistent with the fail-fast policy in [Unsupported feature fail-fast](07-unsupported-feature-fail-fast.md).

This range check is extended by [Driver Impl support matrix and bind-time version check](11-driver-impl-support-matrix-and-bind-time-check.md) to also enforce a *minimum* supported Driver Impl version, not just a maximum.

## Amendment — superseded by [ticket 18](18-spi-impl-generation-range-compatibility.md)

The SemVer basis above is superseded. `driver-spi`'s external release version number is not a meaningful compatibility unit on its own, since it says nothing about which GemFire feature generation it exposes. It is replaced by an **internal generation-number scheme with a mutual range-intersection bind-time check**:

- `driver-spi` owns the generation numbering scheme, defined entirely by which entries exist in the [`Capability` enum](08-capability-enum-evolution-and-removal.md) as of that generation; `driver-spi` declares the range of generations it currently supports, `[spiMin, spiMax]`.
- Each `driver-impl` self-declares its own compatible range, `[implMin, implMax]`.
- At `ServiceLoader` bind time, binding succeeds iff `implMax >= spiMin && implMin <= spiMax`; `driver-core` computes and exposes the effective intersected range, `[max(implMin, spiMin), min(implMax, spiMax)]`, for diagnostics.
- A Driver Impl's declared range is only trustworthy if backed by continuous CI enforcement (a passing [`integrationTest`](14-testing-strategy.md) configuration for every generation in the declared range, re-verified whenever `driver-spi`/`driver-core` change).

See [ticket 18](18-spi-impl-generation-range-compatibility.md) for the full research and sign-off rationale.
