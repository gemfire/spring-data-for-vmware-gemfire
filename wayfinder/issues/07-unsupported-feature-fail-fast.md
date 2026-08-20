Type: grilling
Status: resolved

## Question

Different GemFire versions support different feature sets. When application code (via the SPI) invokes a capability that the currently-bound Driver Impl doesn't support, what should happen?

## Answer

**Fail fast**, at the point of invocation, with a descriptive, typed exception identifying the missing capability — not a silent no-op, not a generic `UnsupportedOperationException`. This policy is the baseline that later decisions build on: it's what makes it safe for the SPI's capability surface to grow ahead of any individual Driver Impl (see [Capability enum evolution and removal](08-capability-enum-evolution-and-removal.md)) and what [Driver Impl support matrix and bind-time version check](11-driver-impl-support-matrix-and-bind-time-check.md) complements with an earlier, bind-time fail-fast for wholly incompatible impls.
