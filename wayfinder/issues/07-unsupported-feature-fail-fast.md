Type: grilling
Status: resolved

## Question

Different GemFire versions support different feature sets. When application code (via the SPI) invokes a capability that the currently-bound Driver Impl doesn't support, what should happen?

## Answer

**Fail fast**, at the point of invocation, with a descriptive, typed exception identifying the missing capability — not a silent no-op, not a generic `UnsupportedOperationException`. This policy is the baseline that later decisions build on: it's what makes it safe for the SPI's capability surface to grow ahead of any individual Driver Impl (see [Capability enum evolution and removal](08-capability-enum-evolution-and-removal.md)) and what [Driver Impl support matrix and bind-time version check](11-driver-impl-support-matrix-and-bind-time-check.md) complements with an earlier, bind-time fail-fast for wholly incompatible impls.

## Amendment — mechanism changed by [ticket 20](20-spi-abi-compatibility-and-driver-version-lockstep.md)

The fail-fast policy stands unchanged. Its **mechanism** changes: there is no `Capability` enum or `requireCapability` check. A feature a driver lacks is signalled by a `default` SPI method that throws, or by the driver overriding a method and throwing. The typed exception is renamed from `UnsupportedCapabilityException` to `UnsupportedFeatureException` (working name) and carries a reason: `NOT_INTRODUCED` (the driver predates the feature — thrown by the SPI default) or `REMOVED` (the driver's GemFire version dropped it — thrown by the driver). Still never a silent no-op, never a bare `UnsupportedOperationException`.
