# Client query result scope — 2026-10-28

Follow-up: [task 65 completion](client-query-and-cq-scope.md) now settles the six deferred serialization IDs and all query/index/CQ lifecycle IDs. The historical first-batch account below records its original boundary.

First source-backed batch in [task 65](../../wayfinder-issues/65-audit-query-and-cq-client-scope.md): **84 required-client rows**, with implementation statuses unchanged. Six inherited serialization rows remain unresolved for a separate source review. This does not settle query execution, indexes or CQ lifecycle scope.

## Source-backed disposition

Pinned GemFire **10.4.0-build.3056.df98c835**, source commit `df98c8357afe85853d9a4776255d4f0e7cf064cb` in `/Users/uk016635/projects/gemfire`. Paths below are relative to that repository; inspect that commit with `git show`, not the moving worktree.

| Source under `gemfire-core/src/main/java/org/apache/geode/` | Evidence and retained behavior |
| --- | --- |
| `cache/client/internal/QueryOp.java`, `execute` and `QueryOpImpl.processResponse` | The client decodes collection type metadata, constructs SelectResults and creates StructImpl values for struct projections. This is an actual client response path, not reachability inferred from server query internals. |
| `cache/query/SelectResults.java` | Collection contract plus occurrences, list/set views or copies, collection type and element-type replacement. Preserve duplicate/order information and each result implementation's modifiability and failures; this does not promise every collection supports every mutation. `isModifiable` describes the collection, not its elements. |
| `cache/query/CqResults.java` | Extends SelectResults. The documented ClientCache/pool path calls executeWithInitialResults and reads key/value Structs. All inherited collection rows are reviewed as client result operations; this does not add server CQ hosting. |
| `cache/query/Struct.java` | Immutable, thread-safe tuple contract with field-name lookup, ordered field values and StructType; unknown field names throw IllegalArgumentException. Preserve nested values through native-free adaptation. |
| `cache/query/types/ObjectType.java` | Logical type classification, simple name and resolved class; maps, regions and arrays count as collection types. Compare type values by equality rather than identity. Native implementation classes must remain inside the driver: use corresponding SPI types when a resolved class is native, while preserving application/JDK class information. |
| `cache/query/types/CollectionType.java` | Element type, duplicate retention and ordering metadata. These describe the returned collection; do not invent ordering or discard duplicates when adapting it. |
| `cache/query/types/StructType.java` | Field names, types and index lookup; preserve positional correspondence and unknown-field failure. Inherited ObjectType observations are part of the typed value. |

The inherited Java Collection rows include iteration, streams/spliterators, array overloads, membership, equality/hash and mutations. They remain required behavior through typed/JDK equivalents, including unsupported mutations where that is the native result behavior. No native collection, struct or metadata object may escape via Object, arrays, iterators, streams or nested results. View/copy and mutation behavior must be verified when implementing adapters; interface-level scope classification does not claim that implementation already exists.

`ObjectType`, `CollectionType` and `StructType` also inherit `toData` and `fromData` from native DataSerializable. Those **six exact rows are not classified by this batch**. Task 65 owns their disposition, consulting task 71 for the serialization mapping. Trace actual client use and the distinction between private wire serialization and application-visible callbacks before deciding. No new runtime probe is needed merely because these methods are inherited.

## Spring consumers and implementation handoff

- Task 21: native-free results/struct/type metadata and native adapters; preserve the behaviors above. Task 31: `GemfireTemplate.query/find/findUnique`, `repository/query/AbstractSelectResults` and projection/result conversion must use those equivalents, including inherited collection operations.
- Task 22: CQ initial results reuse that model and preserve key/value struct content. Task 33: initial-results consumer migration and listener-container wiring. CQ lifecycle/event classification remains separate unfinished task 65 work.
- Existing unit/native tests belong in those implementation tasks, including duplicate/ordered results, projection fields, nested values, unsupported mutation, metadata replacement and CQ initial results. The mock must specify supported local behavior and reject unavailable query/streaming operations explicitly.

Source establishes client scope without an additional runtime experiment. No Java or build file changes, implementation claims, native mock capability or new runtime test result follow from this decision.

## Frozen member ledger

All rows below are required-client. Original native receivers/descriptors and implementation/consumer owners remain in api-coverage.tsv. The similarly named `cache.configuration.ObjectType` is outside this batch.

| API ID | Native receiver suffix | Member | JVM descriptor |
| --- | --- | --- | --- |
| `API-fd08e5fff21a61cb` | `CqResults` | `<type>` | `(type)` |
| `API-c49adaf21cb638b7` | `CqResults` | `add` | `(Ljava/lang/Object;)Z` |
| `API-feda798f58448f14` | `CqResults` | `addAll` | `(Ljava/util/Collection;)Z` |
| `API-03b291200d7e6a36` | `CqResults` | `asList` | `()Ljava/util/List;` |
| `API-8a66c00f288a6994` | `CqResults` | `asSet` | `()Ljava/util/Set;` |
| `API-a7078ff45ad3325f` | `CqResults` | `clear` | `()V` |
| `API-4859a28a95164a92` | `CqResults` | `contains` | `(Ljava/lang/Object;)Z` |
| `API-baf1fe8a759b0ab8` | `CqResults` | `containsAll` | `(Ljava/util/Collection;)Z` |
| `API-646654bf5a193d4a` | `CqResults` | `equals` | `(Ljava/lang/Object;)Z` |
| `API-870f29992b74b136` | `CqResults` | `forEach` | `(Ljava/util/function/Consumer;)V` |
| `API-a91994a70de07068` | `CqResults` | `getCollectionType` | `()Lorg/apache/geode/cache/query/types/CollectionType;` |
| `API-01ba191b38041cde` | `CqResults` | `hashCode` | `()I` |
| `API-b7eb3cb18f3ee49d` | `CqResults` | `isEmpty` | `()Z` |
| `API-0982ca374287fb14` | `CqResults` | `isModifiable` | `()Z` |
| `API-6fb89ffb2d89368c` | `CqResults` | `iterator` | `()Ljava/util/Iterator;` |
| `API-43611ae88585cbef` | `CqResults` | `occurrences` | `(Ljava/lang/Object;)I` |
| `API-7ffe888b0d2e5f6b` | `CqResults` | `parallelStream` | `()Ljava/util/stream/Stream;` |
| `API-6f15a7cbe11b7e53` | `CqResults` | `remove` | `(Ljava/lang/Object;)Z` |
| `API-e459775ab650e488` | `CqResults` | `removeAll` | `(Ljava/util/Collection;)Z` |
| `API-ce18594b1921acde` | `CqResults` | `removeIf` | `(Ljava/util/function/Predicate;)Z` |
| `API-c185e5c556ebd254` | `CqResults` | `retainAll` | `(Ljava/util/Collection;)Z` |
| `API-dfad939b2b76d0dc` | `CqResults` | `setElementType` | `(Lorg/apache/geode/cache/query/types/ObjectType;)V` |
| `API-a638db18d1369ac7` | `CqResults` | `size` | `()I` |
| `API-54ff3e90ff922f88` | `CqResults` | `spliterator` | `()Ljava/util/Spliterator;` |
| `API-036c6992684260d4` | `CqResults` | `stream` | `()Ljava/util/stream/Stream;` |
| `API-80870df038b65924` | `CqResults` | `toArray` | `()[Ljava/lang/Object;` |
| `API-10e03357e0831663` | `CqResults` | `toArray` | `(Ljava/util/function/IntFunction;)[Ljava/lang/Object;` |
| `API-7003f9d95b986ba1` | `CqResults` | `toArray` | `([Ljava/lang/Object;)[Ljava/lang/Object;` |
| `API-ac3985387612631e` | `SelectResults` | `<type>` | `(type)` |
| `API-d0f21b35b86bb67d` | `SelectResults` | `add` | `(Ljava/lang/Object;)Z` |
| `API-675648c16882283d` | `SelectResults` | `addAll` | `(Ljava/util/Collection;)Z` |
| `API-694a65cd7f611d32` | `SelectResults` | `asList` | `()Ljava/util/List;` |
| `API-d6f82af744fec10b` | `SelectResults` | `asSet` | `()Ljava/util/Set;` |
| `API-e24c1a050a976209` | `SelectResults` | `clear` | `()V` |
| `API-40a054f4e0043659` | `SelectResults` | `contains` | `(Ljava/lang/Object;)Z` |
| `API-38cc08aa93549114` | `SelectResults` | `containsAll` | `(Ljava/util/Collection;)Z` |
| `API-e34d17b3c6266d64` | `SelectResults` | `equals` | `(Ljava/lang/Object;)Z` |
| `API-2e42a62da72fe321` | `SelectResults` | `forEach` | `(Ljava/util/function/Consumer;)V` |
| `API-82e921333404d8f0` | `SelectResults` | `getCollectionType` | `()Lorg/apache/geode/cache/query/types/CollectionType;` |
| `API-4602f56e6e2848d3` | `SelectResults` | `hashCode` | `()I` |
| `API-d246807c07d86030` | `SelectResults` | `isEmpty` | `()Z` |
| `API-133af19941a0b289` | `SelectResults` | `isModifiable` | `()Z` |
| `API-a8a24f3a1e8c710f` | `SelectResults` | `iterator` | `()Ljava/util/Iterator;` |
| `API-b07be49e7ab2373d` | `SelectResults` | `occurrences` | `(Ljava/lang/Object;)I` |
| `API-1bef7b42931476cf` | `SelectResults` | `parallelStream` | `()Ljava/util/stream/Stream;` |
| `API-11b09aa6aa29c711` | `SelectResults` | `remove` | `(Ljava/lang/Object;)Z` |
| `API-15cb1a943e591a3d` | `SelectResults` | `removeAll` | `(Ljava/util/Collection;)Z` |
| `API-d2d9be54aef4f251` | `SelectResults` | `removeIf` | `(Ljava/util/function/Predicate;)Z` |
| `API-a6fc29657d3c1e2b` | `SelectResults` | `retainAll` | `(Ljava/util/Collection;)Z` |
| `API-7401d1289af7a10f` | `SelectResults` | `setElementType` | `(Lorg/apache/geode/cache/query/types/ObjectType;)V` |
| `API-6cfcaa11be384f79` | `SelectResults` | `size` | `()I` |
| `API-069cd0d809fa3ee0` | `SelectResults` | `spliterator` | `()Ljava/util/Spliterator;` |
| `API-81643bd273a8b7bb` | `SelectResults` | `stream` | `()Ljava/util/stream/Stream;` |
| `API-00609f0a66a3b771` | `SelectResults` | `toArray` | `()[Ljava/lang/Object;` |
| `API-ed1b8f13c2ff5329` | `SelectResults` | `toArray` | `(Ljava/util/function/IntFunction;)[Ljava/lang/Object;` |
| `API-dfa4dfe84aedde07` | `SelectResults` | `toArray` | `([Ljava/lang/Object;)[Ljava/lang/Object;` |
| `API-5f16dd544fa60666` | `Struct` | `<type>` | `(type)` |
| `API-ced44921a4197d25` | `Struct` | `get` | `(Ljava/lang/String;)Ljava/lang/Object;` |
| `API-20939fe54140bb4f` | `Struct` | `getFieldValues` | `()[Ljava/lang/Object;` |
| `API-6f0a97f189ad0408` | `Struct` | `getStructType` | `()Lorg/apache/geode/cache/query/types/StructType;` |
| `API-927c1bd827f8c698` | `types.CollectionType` | `<type>` | `(type)` |
| `API-1d41ace39a0fca16` | `types.CollectionType` | `allowsDuplicates` | `()Z` |
| `API-89784cd43dbc5c19` | `types.CollectionType` | `getElementType` | `()Lorg/apache/geode/cache/query/types/ObjectType;` |
| `API-c47bffd44acbad64` | `types.CollectionType` | `getSimpleClassName` | `()Ljava/lang/String;` |
| `API-10ca4c799d8fb216` | `types.CollectionType` | `isCollectionType` | `()Z` |
| `API-7c4cb67be8c50dea` | `types.CollectionType` | `isMapType` | `()Z` |
| `API-28847d7baa9545cf` | `types.CollectionType` | `isOrdered` | `()Z` |
| `API-013d85ce1cb40a1e` | `types.CollectionType` | `isStructType` | `()Z` |
| `API-89fffb6f61fc54d1` | `types.CollectionType` | `resolveClass` | `()Ljava/lang/Class;` |
| `API-51a4b5119511ed13` | `types.ObjectType` | `<type>` | `(type)` |
| `API-ee2fcd804d67ff97` | `types.ObjectType` | `getSimpleClassName` | `()Ljava/lang/String;` |
| `API-9d563a6ebfbb1e29` | `types.ObjectType` | `isCollectionType` | `()Z` |
| `API-43171be3b3846777` | `types.ObjectType` | `isMapType` | `()Z` |
| `API-c6eca31c85ef6a65` | `types.ObjectType` | `isStructType` | `()Z` |
| `API-e11b30480c4853ba` | `types.ObjectType` | `resolveClass` | `()Ljava/lang/Class;` |
| `API-34cfb633340c17e7` | `types.StructType` | `<type>` | `(type)` |
| `API-60dec58f0a908b9e` | `types.StructType` | `getFieldIndex` | `(Ljava/lang/String;)I` |
| `API-a4aff986a5377ba6` | `types.StructType` | `getFieldNames` | `()[Ljava/lang/String;` |
| `API-086819d8073e4fcf` | `types.StructType` | `getFieldTypes` | `()[Lorg/apache/geode/cache/query/types/ObjectType;` |
| `API-1fb8d0a87b809ea0` | `types.StructType` | `getSimpleClassName` | `()Ljava/lang/String;` |
| `API-97c3aad26ee9974a` | `types.StructType` | `isCollectionType` | `()Z` |
| `API-274878f3a41c2760` | `types.StructType` | `isMapType` | `()Z` |
| `API-f36e182a587a07c9` | `types.StructType` | `isStructType` | `()Z` |
| `API-c9c38e6f8561f35b` | `types.StructType` | `resolveClass` | `()Ljava/lang/Class;` |

### Deferred inherited serialization IDs

- `API-40a8cac6889d03aa` — `org.apache.geode.cache.query.types.CollectionType.fromData(Ljava/io/DataInput;)V`.
- `API-9ae7ae5bf743b170` — `org.apache.geode.cache.query.types.CollectionType.toData(Ljava/io/DataOutput;)V`.
- `API-35b1f49107860628` — `org.apache.geode.cache.query.types.ObjectType.fromData(Ljava/io/DataInput;)V`.
- `API-c42b35f29af716a7` — `org.apache.geode.cache.query.types.ObjectType.toData(Ljava/io/DataOutput;)V`.
- `API-4c3846b01052f912` — `org.apache.geode.cache.query.types.StructType.fromData(Ljava/io/DataInput;)V`.
- `API-782f00ebda252174` — `org.apache.geode.cache.query.types.StructType.toData(Ljava/io/DataOutput;)V`.
