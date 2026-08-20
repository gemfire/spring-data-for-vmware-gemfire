Type: grilling
Status: resolved

## Question

What is the concrete migration/rollout sequencing plan for the existing Spring Data GemFire codebase — order of subsystems to re-target, how in-flight work is handled, and whether there's an intermediate branch strategy?

## Answer

**Subsystem order — bottom-up by dependency, not by size.** Migrate in this order:

1. `cache`, `util`, and the root package (`org.springframework.data.gemfire`) — the `*FactoryBean` hierarchy, `GemfireTemplate`/`GemfireOperations`, `RegionResolver`/`CacheResolver`, and the `*Converter`s. This is the seam nearly everything else calls into to obtain a `Cache`/`Region`; it's both foundational and small enough to prove the SPI pattern cheaply before committing to it everywhere.
2. `eviction`, `expiration`, `transaction`, `serialization`, `listener`, `domain`, `dao`, `server` — small, low-risk packages, each touching only a narrow native surface.
3. `config` — the largest package by file count, but majority Spring bean-wiring rather than direct native usage; migrated once the seam it wires together (step 1) is already SPI-based, so the wiring changes are mechanical.
4. `client`, `function`, `repository`, `mapping` — last. `client` sits directly on the SPI and is heavily geode-typed; `function`/`repository`/`mapping` are the widest-radius consumers of everything below them and benefit most from migrating against an already-proven, stable SPI rather than one still being shaken out.

**In-flight work handling.** The migration happens on a dedicated long-lived integration branch, not incrementally on `develop` — this is a breaking major version (see [Major version breaking migration](04-major-version-breaking-migration.md)) touching the whole compile surface, so landing it piecemeal on `develop` would force every routine fix in flight to rebase across a moving native-vs-SPI boundary. Net-new feature work against the *current* native API is frozen only for whichever packages are actively being migrated at a given time, per the order above. Routine fixes to not-yet-migrated packages continue landing on `develop` as normal and get rebased onto the integration branch periodically (e.g. weekly), rather than the migration trying to chase every `develop` commit in real time.

**Branch strategy.** The migration branch is kept separate from this repo's existing `support/<gemfire>-<spring>-<sdgf>.x` per-version-combination convention, which exists to let already-released version combinations keep receiving patches independently — a different purpose from a temporary, forward-looking integration effort. The migration branch is named distinctly from that pattern (e.g. `driver-spi-migration`) to avoid implying it's a maintenance branch for a shipped combination, and merges once into `develop` when the SPI cutover is complete, consistent with there being no permanent compatibility facade. No new `support/*` branches are cut for GemFire versions during the migration window; that mechanism continues operating as-is until the SPI model supersedes it post-migration.
