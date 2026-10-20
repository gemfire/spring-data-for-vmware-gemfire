Type: task
Status: resolved
Assignee: Codex
Blocked by: 11

## Question

Determine the smallest complete pool configuration/resolution migration boundary that can build on the injected connection owner without carrying native pool types, global registries or native callbacks into the SPI.

Use the [cache-factory inventory](../wayfinder-impl/investigations/cache-factory-ownership-and-configuration.md) and [injected connection ownership result](11-injected-driver-connection-ownership.md). This is a local source/dependency investigation, with native artifact inspection or focused characterization where needed; it does not implement the pool migration or reopen the signed-off architecture.

## Required investigation

- Trace `client/PoolResolver`, its five implementations, `PoolFactoryBean`, `ClientCacheFactoryBean` pool configuration, adapters/helpers and all production callers. Inventory annotation/XML/configurer consumers (`PoolConfigurer`, `AddPoolConfiguration`, `LazyResolvingComposablePoolConfigurer`, `PoolParser`, default-pool handling) and tests, including Spring Test's native initializer coupling.
- Record pool ownership, lookup timing, lazy resolution/caching, close/destroy/keep-alive behavior, cache/pool creation ordering and reuse. Distinguish default client-cache pools from separately created named pools. Identify existing static registries and the injected collaborators needed to replace them without adding a global registry or reference counting.
- Trace configuration precedence and native defaults: server versus locator endpoints, supplied pool versus explicit settings, subscription/durable settings, authentication, socket callbacks and removed/stored-only options. Do not assume an exposed setter means its value is applied. Separate ordinary values from object callbacks that cannot become a string property bag.
- Map downstream pool operations in regions, queries, functions, CQ and other native consumers. Determine whether resolver migration can precede full pool creation/configuration, and what must migrate together to keep legacy entry points coherent. A native unwrap or permanent dual-API facade is not an acceptable seam.
- Specify the minimum typed SPI operations/configuration and driver adaptation required by the selected next slice, including lifecycle and failure behavior. Existing method semantics remain immutable; new unsupported operations default to typed unsupported. Establish honest mock behavior for local configuration/lookup versus network features it cannot simulate.
- Propose concrete shared mock/native tests and any native-only characterization. Use injected ownership throughout; do not mistake native global reuse for a shared contract or assert distributed pool behavior from a mock accepting configuration.

## Completion

Save a source-linked investigation and reference inventory under `wayfinder-impl/`. Select and create the next precise implementation ticket, or record a concrete blocker that needs a separate decision. Update this ticket and the map with evidence and remaining limitations; graduate only the newly specified part of the configuration migration.

Preserve user Gradle/settings edits. If characterization needs Gradle, use the guarded temporary Spring Test init script and session-only escalation documented in the preceding ticket, and record the exact resolved native artifact. Do not claim full pool or legacy factory migration complete from this investigation.

## Answer

Completed the [source-linked pool investigation](../wayfinder-impl/investigations/pool-configuration-and-resolver-dependencies.md), [reference inventory](../wayfinder-impl/pool-references.tsv) (1,283 references in 181 files), and [native API/constants snapshot](../wayfinder-impl/investigations/pool-native-10.4-api.txt).

The five resolver consumers still include native region, function and CQ operations, so changing the resolver product alone is not a complete migration. Selected and created [Named driver pool creation and ownership](13-named-driver-pool-creation-and-ownership.md): explicit endpoint/minimum configuration, named creation/lookup, a driver pool handle and an injected Spring owner/borrower. Legacy resolvers and their native consumers stay together during staging; the new slice introduces neither unwrap nor a parallel Spring resolver facade.

The inventory records early versus late pool adoption (including the late-lookup ownership gap), configuration-view adapters, scalar/endpoint precedence, configurer timing, disabled cache-ordering post-processor registration, static registration guards, Spring Test initializer coupling and removed/stored-only settings. It distinguishes `gemfirePool` from `DEFAULT` and cache-default selection from literal named lookup.

Local bytecode/API inspection used native `10.4.0-build.3056.df98c835`. Native pool creation/lookup use GemFire's registry and duplicate creation fails; cache default-pool lookup can select/reuse/create a pool whose name need not be DEFAULT. The signed-off native-global pass-through rule permits this inside the driver, without adding application/driver static registries. The mock will model local configuration/lifecycle only, not network behavior or native global reuse.

Validation was local source/reference checks, document-link/whitespace checks and `javap` inspection; no Gradle or runtime tests were rerun for this investigation. Production code and user Gradle/settings edits were untouched. No new architectural sign-off is required. Full pool configuration, resolver migration and legacy factory cutover remain pending.
