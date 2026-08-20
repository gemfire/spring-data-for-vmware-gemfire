Type: grilling
Status: resolved

## Question

Two remaining pieces of item 4 from the map's "Not yet specified" list: (a) what is the concrete initial list of GemFire versions to ship Driver Impls for at launch, and (b) given the reconsideration of [Driver Impl support matrix and bind-time version check](11-driver-impl-support-matrix-and-bind-time-check.md) (see its amendment), what should the support matrix actually contain and what role should it play?

## Answer

1. **Initial GemFire version list — explicitly deferred, not part of this spec.** Which concrete GemFire versions get a Driver Impl at launch is a release-planning decision made later, separate from this design effort. This spec defines the mechanism (SPI, Driver Impl module shape, `ServiceLoader` discovery, versioning/compatibility checks); it does not pick launch versions.

2. **Support matrix — narrowed to a pure documentation/governance artifact, no runtime role.** The matrix (GemFire version → Driver Impl version → supported-until) remains useful as a human-facing record for release planning and capability-removal governance (per [Capability enum evolution and removal](08-capability-enum-evolution-and-removal.md)), but it is no longer consulted by any runtime code. The bind-time minimum-version-floor check originally specified in [ticket 11](11-driver-impl-support-matrix-and-bind-time-check.md) is dropped; runtime enforcement of GemFire-version compatibility is instead the job of the self-declared Driver Impl compatibility check being designed in [Driver Impl self-declared GemFire compatibility mechanics](15-driver-impl-self-declared-gemfire-compatibility-mechanics.md) (open research). See ticket 11's amendment for the full before/after.
