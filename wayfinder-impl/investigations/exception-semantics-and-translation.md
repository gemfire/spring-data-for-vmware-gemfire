# Driver exception semantics and translation

**Client-scope correction (task 76):** this is a historical foundation receipt. The [shared exception decision](client-shared-exception-scope.md) supersedes its broad scope and mapping claims for the 49 excluded IDs. CACHE_EXISTS and TYPE_MISMATCH and explicit excluded native/Spring branches are removed; affected Java validation remains pending. The historical tables and test counts below are not current client-scope acceptance.

Recorded 2026-10-21 for [Driver exception semantics and translation](../../wayfinder-issues/16-driver-exception-semantics-and-translation.md), against `com.vmware.gemfire:gemfire-core:10.4.0-build.3056.df98c835`. This is the common failure foundation; subsystem execution, callback payloads and consumer migration remain open.

## Boundary contract

`DriverOperationException.kind()` exposes a stable `DriverFailureKind`. Its existing `(String, Throwable)` constructor remains available and reports `UNKNOWN`. The new constructor takes a non-null kind, a safe message and the original cause. Categories describe the failure, not transaction atomicity, side effects or retry safety. The SPI never asks Spring to inspect native causes.

The native driver's instance `GemFireExceptionMapper` handles checked and unchecked exceptions, tests specific native subclasses before broader ones and uses only driver-owned operation labels in messages. Original exception objects, their causes and suppressed failures remain diagnostic evidence; they are not sanitized and must not be copied into user-facing messages. Unknown non-native exceptions become `UNKNOWN`; general native system exceptions remain `SYSTEM_FAILURE`. The mapper does not walk arbitrary cause chains looking for a more convenient classification. `GemFireCacheException` explicitly delegates classification to its designated cache exception while retaining the outer wrapper as the direct cause.

Already typed SPI failures pass through unchanged. The loader adapter classifies application runtime failures at callback entry as `LOADER_FAILURE`, preventing an application's `IllegalArgumentException` from being mistaken for a driver argument error. Both native and mock loaders preserve typed failures, including `UnsupportedFeatureException` with either `NOT_INTRODUCED` or `REMOVED`. Known native loader/writer wrappers containing a typed SPI failure preserve that failure. Fatal `Error` values are not caught by the mapper or operation wrappers.

Cache, region, pool and loader-helper execution boundaries now use this mapping. Cache creation still has the separate, existing `DriverBindException` contract: failure to bind translates to `DataAccessResourceFailureException`, with the original native cause retained. Execution categories do not change binding/ABI/version checks. Authentication during later operations has its own typed distinctions; authenticated connection/view assembly remains owned by [Security and authenticated views SPI](../../wayfinder-issues/24-security-and-authenticated-views-spi.md).

Native listener dispatch can log and suppress application runtime failures, unlike writer veto and loader failure. The SPI does not manufacture a stronger propagation guarantee. Existing native lifecycle tests retain this distinction. The new native writer test installs a real native writer, calls the SPI region and verifies both the typed veto and absence of an inserted entry; it does not claim a writer registration SPI already exists.

## Spring outcomes

`GemFireDriverExceptionTranslator` provides the common Spring DAO families from SPI only. `GemfireExceptionTranslator` in Spring Data retains the existing Gemfire-specific subtypes. `DriverConnectionFactoryBean` now implements `PersistenceExceptionTranslator`, allowing Spring's repository advice to discover it. A real application-context test verifies discovery and translation without native types.

| Native meaning / SPI category | Spring Data outcome |
| --- | --- |
| Entry exists / `DUPLICATE_ENTRY` | `DuplicateKeyException` |
| Entry absent / `ENTRY_NOT_FOUND` | `DataRetrievalFailureException` |
| Cache/region exists, commit conflict, incomplete commit | `DataIntegrityViolationException`; SPI kinds remain distinct |
| Synchronization/lease conflict / `LOCK_FAILURE` | `PessimisticLockingFailureException` |
| Authentication failed/required, expired, not authorized, other security failure | `PermissionDeniedDataAccessException`; SPI kinds distinguish the cases |
| Invalid value / `TYPE_MISMATCH` | `TypeMismatchDataAccessException` |
| Closed CQ, destroyed entry, invalid arguments/state, function failure | `InvalidDataAccessApiUsageException` |
| Destroyed/unavailable region | `InvalidDataAccessResourceUsageException` |
| Resource, connectivity, disk or version failure | `DataAccessResourceFailureException` |
| Query invalid/execution/timeout, CQ failure or existing CQ | `GemfireQueryException` |
| Index creation/invalid/maintenance/name-conflict/multiple-index failure | `GemfireIndexException` |
| Cancellation / `CANCELLED` | `GemfireCancellationException` |
| Loader failure, writer/transaction veto, general transaction/system or unknown failure | `GemfireSystemException` |
| Generic operation timeout / `TIMEOUT` | `QueryTimeoutException` |
| Typed unsupported | `InvalidDataAccessApiUsageException`, original typed exception retained |
| Binding failure | `DataAccessResourceFailureException` |
| Unrelated runtime exception or already translated DAO exception | Translator returns `null`; it does not claim the exception |

The generic SPI translator uses DAO parent classes for query/index/cancellation/system outcomes; Spring Data supplies its historical subtypes. Generic native timeout now has an explicit timeout outcome rather than the old uncategorized fallback. Index-family checked exceptions consistently use the index outcome; the old generic checked-exception utility could classify some as query errors. CQ-exists stays in the query family. No message substring decides any of these mappings.

The four Spring wrappers now accept `(Throwable)` and `(String, Throwable)` instead of native-specific overloads. Existing source callers continue compiling, but their JVM constructor descriptors change: this belongs to the planned Spring major cutover and requires recompilation. Their native-free use through the new translator always receives the sanitized SPI message. Remaining legacy native callers can still copy native messages until their migration; this ticket does not claim those callers are sanitized.

Spring Cache, resolvers and owner/borrower lifecycle factories retain their existing SPI exception propagation contracts. The connection factory supplies repository translation without changing ownership/cleanup failure identity. Spring Cache's `ValueRetrievalException` behavior remains unchanged.

## Remaining owned work

[Native exception classifications](native-exception-classifications.tsv) records all **69** exception types in the frozen required API inventory, their current category and remaining contract/consumer owners. [Native exception callers](native-exception-callers.tsv) records **18** production files still naming these types, including fully qualified transaction catches and documentation-only annotation references. The caller inventory uses the frozen exception type names against current Java source, retaining imports, signatures and text mentions conservatively. It supplements the complete source/bytecode matrix; it is not a claim to infer arbitrary application reflection.

All **403** exception API rows remain required. The 69 type rows advance to partial foundation coverage. The other 334 constructor/member/payload rows retain missing status and now name open subsystem or residual-cleanup owners. No row is marked implemented or removed. The coverage generator honors per-member consumer-owner overrides so exception contracts can belong to the subsystem that must emit them, instead of assigning all consumers to residual cleanup.

| Remaining consumer group | Owner and obligation |
| --- | --- |
| `GemfireAccessor`, `GemfireTemplate`, `GemfireCallback` | [Template, repository and mapping migration](../../wayfinder-issues/31-template-repository-and-mapping-migration.md): use SPI catches and the common translator; preserve query/index/data outcomes and callback failures. |
| `AbstractBasicCacheFactoryBean`, `AbstractResolvableCacheFactoryBean`, `ClientCacheFactoryBean`, pool annotations, authentication initializer | [Cache/pool entry points and resolver cutover](../../wayfinder-issues/35-cache-pool-entrypoints-and-resolver-cutover.md): replace native catches/translation discovery and retain safe lifecycle behavior; security implementation is a prerequisite. |
| `DefinedIndexesApplicationListener` | [Region factory and configuration migration](../../wayfinder-issues/30-region-factory-and-configuration-migration.md): preserve per-index failures supplied by query/index SPI. |
| Function executor and its two native `FunctionException` subclasses | [Spring function consumer migration](../../wayfinder-issues/32-spring-function-consumer-migration.md): replace native types and the existing message-substring “no result” workaround with typed execution/result metadata. Preserve timeout/interruption and exceptions returned in result collections; never turn a real failure into an empty result. |
| CQ listener container and listener adapter error handling | [Spring continuous-query consumer migration](../../wayfinder-issues/33-spring-continuous-query-consumer-migration.md): preserve listener error delivery, query outcomes and native suppression/propagation semantics. |
| Transaction manager and composable transaction writer | [Spring transaction consumer migration](../../wayfinder-issues/34-spring-transaction-consumer-migration.md): keep `CannotCreateTransactionException`, `GemfireTransactionCommitException` and `NoTransactionException` phase semantics using typed failures; preserve veto and commit-conflict distinctions. Generic DAO translation does not replace Spring transaction-manager behavior. |
| `GemfireCacheUtils`, `CacheUtils` | [Residual native utilities and support cleanup](../../wayfinder-issues/36-residual-native-utilities-and-support-cleanup.md): remove final native dispatch/reflection after callers move; ensure unavailable cache is not mistaken for a successful lookup. |

Exception payloads are additional required contracts, not diagnostic cause casts: entry old value and region handle/path (region SPI), existing cache handle (cache assembly), disk remote flag (disk SPI), function exception collections (function SPI), failed-index names/map (query/index SPI), and principal/security payloads (security SPI). Their member rows retain open owners. Exception constructors, root-cause helpers and serialization-related members also remain explicit inventory obligations for those owners to map to native-free equivalents. A scalar failure kind alone does not close these contracts.

## Evidence and limits

Tests cover 26 representative native exceptions including checked exceptions, subtype precedence, original-cause retention, safe top-level messages, unknown errors and typed unsupported failures; shared mock/native tests cover duplicate-region versus invalid-argument failures, loader failures, typed veto and both unsupported reasons. A real native writer veto leaves the entry absent. Spring tests cover DAO outcomes and repository-advice discovery. No remote authentication, distributed commit, CQ service or function execution is simulated as a successful implementation by these tests.

Full validation commands/results, the shrinking boundary counts and exact final inventory are recorded in the owning ticket's Answer. Final native dependency removal and the full behavior matrix remain blocked downstream.
