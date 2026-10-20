Type: grilling
Status: resolved
Assignee: uk016635

## Question

Finalize the provisional defaults left by [SPI ABI compatibility and driver version lock-step](20-spi-abi-compatibility-and-driver-version-lockstep.md), before the revised design receives its final sign-off:

1. What artifact-version convention should driver-only fixes use while keeping the stamped GemFire version unchanged? The current spec §6.3 suggests `10.3.2.1`; the original decision also mentioned `10.3.2+2` without choosing a convention.
2. Should the remaining defaults become settled decisions: `UnsupportedFeatureException` as the exception name; a generated properties resource keyed by provider class at `META-INF/gemfire-driver/<provider-fqcn>.properties`; and `driver-core` as the discovery/binding dependency through which Spring Data GemFire obtains the SPI (with a direct SPI dependency also permitted, as specified in §4)?

Driver maintenance strategy and API-compatibility tool selection remain deferred to implementation, as already recorded in the originating decision. This ticket does not select the initial supported GemFire releases.

## Comments

### 2026-10-20 — resumed from the local map

All existing design tickets were marked resolved, but the originating decision explicitly left these defaults provisional. Claimed this follow-up to settle them with the user; no answer or approval is inferred from the existing implementation.

Proposed release convention: initial driver artifact version equals its target GemFire version; driver-only fixes append an incrementing numeric revision (`10.3.2.1`, `10.3.2.2`, ...). The generated `gemfire.version` remains `10.3.2`; binding uses that stamp rather than parsing the artifact version. The recommendation retains the example already in spec §6.3.

Proposed binding defaults: retain the current exception name, provider-keyed resource and module dependency arrangement. The architecture revisions' sign-off remains separate and pending under [Spec review/sign-off process](17-spec-review-sign-off-process.md).

## Answer

Confirmed by Udo Kohlmeyer on 2026-10-20: “1. yes” and “2. yes”.

1. **Driver-only fixes:** the initial artifact version equals the target GemFire version. Subsequent driver-only fixes append an incrementing numeric revision: `10.3.2.1`, `10.3.2.2`, and so on. The stamped `gemfire.version` remains `10.3.2`; binding compares the runtime GemFire version with that stamp, without parsing the driver artifact version.
2. **Binding defaults:** retain `UnsupportedFeatureException`; generate the stamp at `META-INF/gemfire-driver/<provider-fqcn>.properties`; and retain `driver-core` for discovery and binding, with Spring Data GemFire also permitted to depend directly on the SPI as specified in §4.

These choices settle the provisional details in the originating decision. They do not constitute the separate, full revised-design sign-off for [No static state or singletons](19-no-static-state-or-singletons.md) and [SPI ABI compatibility and driver version lock-step](20-spi-abi-compatibility-and-driver-version-lockstep.md). Driver maintenance strategy and API-compatibility tool selection remain deferred to implementation.

### Subsequent design sign-off — 2026-10-20

Udo Kohlmeyer subsequently approved the complete revised design: “ok... docs sign-off”. The separate approval described above is now recorded in [the sign-off log](../rationale.md#8-sign-off-log).
