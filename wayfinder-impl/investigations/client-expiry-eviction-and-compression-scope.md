# Client expiry, eviction and compression scope — 2026-10-28

[Task 68](../../wayfinder-issues/68-audit-expiry-eviction-and-compression-client-scope.md) settles **154 assigned IDs across 14 types: 154 required-client / zero excluded-server / zero unresolved**. This closes classification only. All 154 implementation statuses remain missing, owned by active task 47 with task 30 consumer adoption. No Java/build change or new runtime probe was needed.

Native evidence is GemFire **10.4.0-build.3056.df98c835**, source commit `df98c8357afe85853d9a4776255d4f0e7cf064cb` in `/Users/uk016635/projects/gemfire`, inspected using git show/git grep. Native paths below are relative to `gemfire-core/src/main/java/org/apache/geode/`; Spring paths are within its main Java source tree. Each decision uses only the fixed exact IDs below, including inherited methods, protected members, constructors, constants and overloads.

## client-expiry-region-settings

**26 IDs — required-client.** `cache/client/internal/ClientRegionFactoryImpl` applies LOCAL scope and delegates expiry, eviction and compressor settings to `internal/cache/InternalAttributesFactory`. That factory copies settings and explicit flags, enables statistics for expiration only when statistics were not explicitly set, and rejects expiration with explicitly disabled statistics. EMPTY/proxy data policy rejects nonzero entry expiration, custom expiry and non-NONE eviction; these restrictions do not exclude the same settings on client regions with local storage. Region expiry remains a separate setting, including deprecated accessors.

`InternalAttributesFactory.setEntryTimeToLive` defaults to LAST_MODIFIED; its two-argument overload stores the policy. `AbstractRegion` implements live getters/mutators, checks readiness and statistics, returns previous expiration attributes/custom callbacks and rejects null expiration attributes (and null reset policy in the live TTL mutator). Custom null clears the callback. Keep factory and mutator validation differences. `LocalRegion.timeToLiveChanged/idleTimeoutChanged` reschedule for custom callbacks, shorter/new expiration or changed TTL policy; other changes can take effect through existing tasks. Region mutation preserves its separate scheduling path.

`InternalAttributesFactory.setCompressor` enables cloning for a nonnull compressor; validation rejects compression with cloning explicitly disabled. `AbstractRegion.getEvictionAttributesMutator` binds the mutator to that region's live attributes. Spring's `client/ClientRegionFactoryBean`, `config/xml/ClientRegionParser` and expiration/eviction/compression annotation configuration still consume these settings. Task 47 supplies native-free contracts; task 30 migrates Java/XML/annotation assembly without restoring server region shortcuts.

## client-expiry-values

**34 IDs — required-client.** `cache/ExpirationAction` supplies four actions, ordinals, predicates and XML names. INVALIDATE/DESTROY being described as distributed actions is not a server-only scope rule: `internal/cache/EntryExpiryTask` creates EXPIRE_DESTROY/EXPIRE_INVALIDATE and local variants for `LocalRegion`. Client region scope/configuration determines effects. Preserve all four values and validation; do not infer peer hosting from their names.

`cache/ExpirationAttributes` constructors clamp negative timeouts to zero and normalize null action to INVALIDATE, despite the older one-argument constructor comment. DEFAULT is zero/INVALIDATE. Equality compares timeout/action; hash combines them; zero timeout renders NO EXPIRATION. isDefault also accepts a null action with zero timeout, which matters after deserialization. Keep constructors, observations and validation distinct from binary hydration.

`cache/TimeToLiveResetPolicy` defines LAST_MODIFIED and NEVER, lowercase hyphenated XML conversion, null XML default to LAST_MODIFIED, invalid-input failure and ordinary enum lookup/values. `internal/cache/EntryExpiryTask` selects creation time for NEVER and last-modified time otherwise; idle timeout uses last-accessed time separately. `LocalRegion` reschedules when the reset policy changes. These are client-local entry expiration behaviors, not server TTL administration.

## client-expiry-binary-values

**3 IDs — required-client.** The three exact `cache/ExpirationAttributes` members `toData`, `fromData` and `createFromData` serialize timeout plus action using native DataSerializer behind the driver boundary. fromData assigns fields directly, without constructor normalization; createFromData constructs then hydrates. These are retained value round-trip observations, not server registration APIs. Task 47 owns the value requirement and task 55 the binary codec collaboration, with task 30 adoption. Keep native framing/translation in versioned drivers; no native serializer signature enters Spring. Compatibility tests belong to implementation acceptance.

## client-custom-expiry

**5 IDs — required-client.** `cache/CustomExpiry.getExpiry` receives a Region.Entry valid for that invocation and may return null to select the region default. `internal/cache/LocalRegion.createExpiryTask` invokes TTL/idle callbacks, validates returned actions, falls back to configured attributes on null and handles callback exceptions (entry/region disappearance is ignored; other Exceptions are logged before fallback). With both resulting timeouts disabled no task is scheduled. Use a typed entry view, not native Region.Entry.

Inherited `CacheCallback.close` and `Declarable.init/initialize` remain exact receiver obligations. The latter have distinct properties/cache-context hooks; `internal/cache/xmlcache/CacheCreation.initializeDeclarablesMap`, already traced in task 67, calls initialize then init. Do not assume programmatic setting invokes declarative initialization. `AbstractRegion.setCustomEntryIdleTimeout/setCustomEntryTimeToLive` replaces and returns the old callback without calling close. The inspected `LocalRegion.closeCallbacksExceptListener/closeAllCallbacks` closes loader/writer/listeners, not custom expiry. Thus the general CacheCallback close documentation alone does not establish automatic custom-expiry closure in these paths. Preserve the callable hook and make ownership explicit in task 47/30 acceptance; do not promise an observed automatic close that the source does not implement. This discrepancy does not change client scope and needs no scope probe.

## client-eviction-values

**71 IDs — required-client.** `cache/EvictionAttributes` supplies entry/heap/memory factories, entry default 900 and memory default 10, action/sizer overloads and no-eviction observation. `internal/cache/EvictionAttributesImpl` stores algorithm/action/maximum/sizer, normalizes null action to NONE and reports heap maximum as zero. Factories and live controller validation are distinct. Equality compares algorithm/action/maximum but ignores sizer; hash uses algorithm/maximum; isNoEviction checks the action, not the algorithm. Preserve abstract construction/extension behavior through a typed equivalent.

Deprecated LIFO_ENTRY/LIFO_MEMORY and their factories are retained: `internal/cache/VMLRURegionMap.createEvictionController` calls `eviction/AbstractEvictionController.create`, which explicitly accepts both alongside the LRU variants and builds count/memory controllers. This is a concrete local-region path, not merely an enum reference. This classification does not certify an eviction ordering algorithm from its name; task 47 must verify actual pinned-driver effects and expose explicit mock limitations. No server ownership or hosting dependency is required.

`cache/EvictionAction` has NONE/LOCAL_DESTROY/OVERFLOW_TO_DISK, default LOCAL_DESTROY, exact case-sensitive string parsing with NONE fallback and invalid numeric lookup returning null. `cache/EvictionAlgorithm` has NONE, three LRU and two LIFO constants; unknown string/numeric parsing returns null. Native protected tables return their backing arrays. Inherited `javax.print.attribute.EnumSyntax` behavior was checked with local `javap -c -p`: clone returns self, hash/value use the integer, default offset is zero, toString indexes the string table and readResolve canonicalizes through the enum table with invalid-object failures. Retain equivalent value/extension observations without requiring a printing API inheritance hierarchy in the SPI. These inherited IDs are not silently omitted.

`internal/cache/EvictionAttributesMutatorImpl.setMaximum` updates the region controller first, then stored attributes. `LocalRegion.setEvictionMaximum` calls the controller only if present. Preserve resulting validation/failure ordering and readback, not just a detached integer setter. Overflow requires client-local disk configuration (task 27/49, separately audited by 74); resource thresholds remain separate.

`EvictionAttributes.convertToConfigEvictionAttributes` maps heap/memory/otherwise-entry nodes, action and maximum, plus the sizer's class string. The action DTO accepts local-destroy/overflow; NONE conversion can fail. LIFO uses the fallback branch rather than inventing a new DTO, and sizer uses getClass().toString rather than getName. Task 67 already retained the DTOs and inverse conversion; coordinate task 49 to preserve lossy conversions and failure behavior instead of normalizing them. No server JAXB schema needs to enter Spring.

## client-object-sizing

**5 IDs — required-client.** `cache/util/ObjectSizer` is consumed by memory/heap eviction factories and `internal/cache/eviction/AbstractEvictionController.create`. DEFAULT aliases SIZE_CLASS_ONCE; REFLECTION_SIZE and SIZE_CLASS_ONCE are distinct implementations. The interface documents reflection traversal of reachable nonstatic fields and class-once sizing with String/byte-array exceptions. Keep strategy selection and per-object sizeof callbacks native-free; exact runtime sizing/effects belong to task 47 compatibility tests, not an assumed universal byte count in the mock. These five IDs require no server memory administration.

## client-compression

**10 IDs — required-client.** `compression/Compressor` exposes byte-array compress/decompress. `internal/cache/entries/AbstractRegionEntry.prepareValueForCache/compress/decompress` invokes the region compressor for stored values, handles serialization and byte-array wrappers, and excludes null/invalid/removed tokens. This is local client storage behavior, not a server-only transport setting. Keep codec failures and cloning requirements explicit.

`compression/SnappyCompressor` is deprecated since 10.3 but remains used by Spring `config/annotation/CompressionConfiguration` and the client region compressor path. Its constructor and getDefaultInstance remain retained; the latter returns a new instance despite the singleton-like documentation. Equality/hash use class-name equality/hash. Compress delegates to iq80 Snappy; decompress translates CorruptionException to native CompressionException. Provide equivalent typed behavior and driver-side exception translation without native classes in Spring. The codec's deprecated status alone does not authorize deleting retained client tests. Task 47 owns native/mock compatibility and task 30 consumer migration.

## Ownership and reconciliation

Task 47 implements these 154 missing obligations; task 30 migrates their factories, values, converters, XML and annotations. Task 55 coordinates binary values, task 49 eviction configuration conversion and task 27 client disk/resource effects. These are collaborations, not additional IDs or owner transfers. The assignment ledger and native declarations stay fixed; no implementation, SPI/native/mock/test mapping or status is upgraded. No exclusions or retirement are needed in this family. Generated source/resource and existing-SPI scope dispositions follow linked requirements. Tasks 59/64 stay open; task 69 is next with 101 fixed pool/durable IDs.

## Exact ID ledger

All rows inherit the behavior/evidence in the named batch. Implementation / consumer owner is **47 / 30** throughout.

### client-expiry-region-settings — 26 IDs

| API ID | Native receiver | Member | JVM descriptor |
| --- | --- | --- | --- |
| `API-c1b473c33798ee5a` | `org.apache.geode.cache.AttributesFactory` | `setCompressor` | `(Lorg/apache/geode/compression/Compressor;)V` |
| `API-07fd7811b64a2045` | `org.apache.geode.cache.AttributesFactory` | `setCustomEntryIdleTimeout` | `(Lorg/apache/geode/cache/CustomExpiry;)V` |
| `API-8a64b814a27a5aec` | `org.apache.geode.cache.AttributesFactory` | `setCustomEntryTimeToLive` | `(Lorg/apache/geode/cache/CustomExpiry;)V` |
| `API-14e7c6eaac69967b` | `org.apache.geode.cache.AttributesFactory` | `setEntryIdleTimeout` | `(Lorg/apache/geode/cache/ExpirationAttributes;)V` |
| `API-8eae8a0f9f6e9069` | `org.apache.geode.cache.AttributesFactory` | `setEntryTimeToLive` | `(Lorg/apache/geode/cache/ExpirationAttributes;)V` |
| `API-8d09788145faecbd` | `org.apache.geode.cache.AttributesFactory` | `setEntryTimeToLive` | `(Lorg/apache/geode/cache/ExpirationAttributes;Lorg/apache/geode/cache/TimeToLiveResetPolicy;)V` |
| `API-ff2f71a81009c1a1` | `org.apache.geode.cache.AttributesFactory` | `setEvictionAttributes` | `(Lorg/apache/geode/cache/EvictionAttributes;)V` |
| `API-7e906e497e2efe9a` | `org.apache.geode.cache.AttributesFactory` | `setRegionIdleTimeout` | `(Lorg/apache/geode/cache/ExpirationAttributes;)V` |
| `API-216e14313af16f59` | `org.apache.geode.cache.AttributesFactory` | `setRegionTimeToLive` | `(Lorg/apache/geode/cache/ExpirationAttributes;)V` |
| `API-4a500736190ccaae` | `org.apache.geode.cache.AttributesMutator` | `getEvictionAttributesMutator` | `()Lorg/apache/geode/cache/EvictionAttributesMutator;` |
| `API-3e7771efbaaed965` | `org.apache.geode.cache.AttributesMutator` | `setCustomEntryIdleTimeout` | `(Lorg/apache/geode/cache/CustomExpiry;)Lorg/apache/geode/cache/CustomExpiry;` |
| `API-dcda9bec0df0e7de` | `org.apache.geode.cache.AttributesMutator` | `setCustomEntryTimeToLive` | `(Lorg/apache/geode/cache/CustomExpiry;)Lorg/apache/geode/cache/CustomExpiry;` |
| `API-8fd375e6d17ac3db` | `org.apache.geode.cache.AttributesMutator` | `setEntryIdleTimeout` | `(Lorg/apache/geode/cache/ExpirationAttributes;)Lorg/apache/geode/cache/ExpirationAttributes;` |
| `API-995c4f7a79c063b4` | `org.apache.geode.cache.AttributesMutator` | `setEntryTimeToLive` | `(Lorg/apache/geode/cache/ExpirationAttributes;)Lorg/apache/geode/cache/ExpirationAttributes;` |
| `API-33f61470eae8df2f` | `org.apache.geode.cache.AttributesMutator` | `setEntryTimeToLive` | `(Lorg/apache/geode/cache/ExpirationAttributes;Lorg/apache/geode/cache/TimeToLiveResetPolicy;)Lorg/apache/geode/cache/ExpirationAttributes;` |
| `API-b2ab9e45c2f73947` | `org.apache.geode.cache.AttributesMutator` | `setRegionIdleTimeout` | `(Lorg/apache/geode/cache/ExpirationAttributes;)Lorg/apache/geode/cache/ExpirationAttributes;` |
| `API-e9c5512dd7ddd492` | `org.apache.geode.cache.AttributesMutator` | `setRegionTimeToLive` | `(Lorg/apache/geode/cache/ExpirationAttributes;)Lorg/apache/geode/cache/ExpirationAttributes;` |
| `API-3e8998a7a97c611a` | `org.apache.geode.cache.RegionAttributes` | `getCompressor` | `()Lorg/apache/geode/compression/Compressor;` |
| `API-b4210b05b5c62127` | `org.apache.geode.cache.RegionAttributes` | `getCustomEntryIdleTimeout` | `()Lorg/apache/geode/cache/CustomExpiry;` |
| `API-d7a34d216a8ce558` | `org.apache.geode.cache.RegionAttributes` | `getCustomEntryTimeToLive` | `()Lorg/apache/geode/cache/CustomExpiry;` |
| `API-6113ec74a0566185` | `org.apache.geode.cache.RegionAttributes` | `getEntryIdleTimeout` | `()Lorg/apache/geode/cache/ExpirationAttributes;` |
| `API-72480c408a9b1bf8` | `org.apache.geode.cache.RegionAttributes` | `getEntryTimeToLive` | `()Lorg/apache/geode/cache/ExpirationAttributes;` |
| `API-65ff50dd08da694b` | `org.apache.geode.cache.RegionAttributes` | `getEntryTimeToLiveResetPolicy` | `()Lorg/apache/geode/cache/TimeToLiveResetPolicy;` |
| `API-7a92bbbb684c7157` | `org.apache.geode.cache.RegionAttributes` | `getEvictionAttributes` | `()Lorg/apache/geode/cache/EvictionAttributes;` |
| `API-4691e58397fe7d09` | `org.apache.geode.cache.RegionAttributes` | `getRegionIdleTimeout` | `()Lorg/apache/geode/cache/ExpirationAttributes;` |
| `API-4b44e1ad63e70086` | `org.apache.geode.cache.RegionAttributes` | `getRegionTimeToLive` | `()Lorg/apache/geode/cache/ExpirationAttributes;` |

### client-expiry-values — 34 IDs

| API ID | Native receiver | Member | JVM descriptor |
| --- | --- | --- | --- |
| `API-08ee234256145bf0` | `org.apache.geode.cache.ExpirationAction` | `<type>` | `(type)` |
| `API-72b231075ee50312` | `org.apache.geode.cache.ExpirationAction` | `DESTROY` | `Lorg/apache/geode/cache/ExpirationAction;` |
| `API-84705b29e4c5a485` | `org.apache.geode.cache.ExpirationAction` | `INVALIDATE` | `Lorg/apache/geode/cache/ExpirationAction;` |
| `API-4bdf42d18f4b49c7` | `org.apache.geode.cache.ExpirationAction` | `LOCAL_DESTROY` | `Lorg/apache/geode/cache/ExpirationAction;` |
| `API-25ffec3b45b251df` | `org.apache.geode.cache.ExpirationAction` | `LOCAL_INVALIDATE` | `Lorg/apache/geode/cache/ExpirationAction;` |
| `API-96dd53ac54fdb03c` | `org.apache.geode.cache.ExpirationAction` | `fromOrdinal` | `(I)Lorg/apache/geode/cache/ExpirationAction;` |
| `API-44121a7d04e19c9b` | `org.apache.geode.cache.ExpirationAction` | `fromXmlString` | `(Ljava/lang/String;)Lorg/apache/geode/cache/ExpirationAction;` |
| `API-f3721eb7c8a9a5a3` | `org.apache.geode.cache.ExpirationAction` | `isDestroy` | `()Z` |
| `API-8bfaa5d2bfd07742` | `org.apache.geode.cache.ExpirationAction` | `isDistributed` | `()Z` |
| `API-a28dfc195f6b7971` | `org.apache.geode.cache.ExpirationAction` | `isInvalidate` | `()Z` |
| `API-1c3986b4c046eb3d` | `org.apache.geode.cache.ExpirationAction` | `isLocal` | `()Z` |
| `API-cfe9a5013b5df2d6` | `org.apache.geode.cache.ExpirationAction` | `isLocalDestroy` | `()Z` |
| `API-7a80be051358db1a` | `org.apache.geode.cache.ExpirationAction` | `isLocalInvalidate` | `()Z` |
| `API-2814de0f43f53bb4` | `org.apache.geode.cache.ExpirationAction` | `ordinal` | `I` |
| `API-0960a87ee777b9cc` | `org.apache.geode.cache.ExpirationAction` | `toString` | `()Ljava/lang/String;` |
| `API-be706c320b202c0d` | `org.apache.geode.cache.ExpirationAction` | `toXmlString` | `()Ljava/lang/String;` |
| `API-3fdbb8e517c897ea` | `org.apache.geode.cache.ExpirationAttributes` | `<init>` | `()V` |
| `API-65ff7518ddb0d18f` | `org.apache.geode.cache.ExpirationAttributes` | `<init>` | `(I)V` |
| `API-04c2e916ab927aa1` | `org.apache.geode.cache.ExpirationAttributes` | `<init>` | `(ILorg/apache/geode/cache/ExpirationAction;)V` |
| `API-33183c2f79454146` | `org.apache.geode.cache.ExpirationAttributes` | `<type>` | `(type)` |
| `API-ecd4c474cd8a7363` | `org.apache.geode.cache.ExpirationAttributes` | `DEFAULT` | `Lorg/apache/geode/cache/ExpirationAttributes;` |
| `API-c5b4ee4a87c9ac16` | `org.apache.geode.cache.ExpirationAttributes` | `equals` | `(Ljava/lang/Object;)Z` |
| `API-995d7777ada3d598` | `org.apache.geode.cache.ExpirationAttributes` | `getAction` | `()Lorg/apache/geode/cache/ExpirationAction;` |
| `API-d0a27faf86539451` | `org.apache.geode.cache.ExpirationAttributes` | `getTimeout` | `()I` |
| `API-39d2b3dec510d3b9` | `org.apache.geode.cache.ExpirationAttributes` | `hashCode` | `()I` |
| `API-0ea735774737533f` | `org.apache.geode.cache.ExpirationAttributes` | `isDefault` | `()Z` |
| `API-bf6f689705f26d07` | `org.apache.geode.cache.ExpirationAttributes` | `toString` | `()Ljava/lang/String;` |
| `API-51cad650799e4044` | `org.apache.geode.cache.TimeToLiveResetPolicy` | `<type>` | `(type)` |
| `API-2d509f18e85e5b07` | `org.apache.geode.cache.TimeToLiveResetPolicy` | `LAST_MODIFIED` | `Lorg/apache/geode/cache/TimeToLiveResetPolicy;` |
| `API-69da49f4f226bcd4` | `org.apache.geode.cache.TimeToLiveResetPolicy` | `NEVER` | `Lorg/apache/geode/cache/TimeToLiveResetPolicy;` |
| `API-55f6a48309e84e93` | `org.apache.geode.cache.TimeToLiveResetPolicy` | `fromXmlString` | `(Ljava/lang/String;)Lorg/apache/geode/cache/TimeToLiveResetPolicy;` |
| `API-c8a3ed3749a30ea6` | `org.apache.geode.cache.TimeToLiveResetPolicy` | `toXmlString` | `()Ljava/lang/String;` |
| `API-f12d7a85ea0ce070` | `org.apache.geode.cache.TimeToLiveResetPolicy` | `valueOf` | `(Ljava/lang/String;)Lorg/apache/geode/cache/TimeToLiveResetPolicy;` |
| `API-1a5319b476aae4e2` | `org.apache.geode.cache.TimeToLiveResetPolicy` | `values` | `()[Lorg/apache/geode/cache/TimeToLiveResetPolicy;` |

### client-expiry-binary-values — 3 IDs

| API ID | Native receiver | Member | JVM descriptor |
| --- | --- | --- | --- |
| `API-f5beffd1dab40449` | `org.apache.geode.cache.ExpirationAttributes` | `createFromData` | `(Ljava/io/DataInput;)Lorg/apache/geode/cache/ExpirationAttributes;` |
| `API-b6d99271b81a183b` | `org.apache.geode.cache.ExpirationAttributes` | `fromData` | `(Ljava/io/DataInput;)V` |
| `API-78f3b5b63d5d2219` | `org.apache.geode.cache.ExpirationAttributes` | `toData` | `(Ljava/io/DataOutput;)V` |

### client-custom-expiry — 5 IDs

| API ID | Native receiver | Member | JVM descriptor |
| --- | --- | --- | --- |
| `API-c0bc067165fcb0a6` | `org.apache.geode.cache.CustomExpiry` | `<type>` | `(type)` |
| `API-8d453344e853fab4` | `org.apache.geode.cache.CustomExpiry` | `close` | `()V` |
| `API-a9bbf9e67ae40dfe` | `org.apache.geode.cache.CustomExpiry` | `getExpiry` | `(Lorg/apache/geode/cache/Region$Entry;)Lorg/apache/geode/cache/ExpirationAttributes;` |
| `API-b5b3dda5796e3bb6` | `org.apache.geode.cache.CustomExpiry` | `init` | `(Ljava/util/Properties;)V` |
| `API-e2f11b34435b40ce` | `org.apache.geode.cache.CustomExpiry` | `initialize` | `(Lorg/apache/geode/cache/Cache;Ljava/util/Properties;)V` |

### client-eviction-values — 71 IDs

| API ID | Native receiver | Member | JVM descriptor |
| --- | --- | --- | --- |
| `API-a163ec965ff9f1c2` | `org.apache.geode.cache.EvictionAction` | `<type>` | `(type)` |
| `API-09743be0d651afcb` | `org.apache.geode.cache.EvictionAction` | `DEFAULT_EVICTION_ACTION` | `Lorg/apache/geode/cache/EvictionAction;` |
| `API-37aa81edd5e26166` | `org.apache.geode.cache.EvictionAction` | `LOCAL_DESTROY` | `Lorg/apache/geode/cache/EvictionAction;` |
| `API-81475b23ea044b7b` | `org.apache.geode.cache.EvictionAction` | `NONE` | `Lorg/apache/geode/cache/EvictionAction;` |
| `API-688f4b6c4135138d` | `org.apache.geode.cache.EvictionAction` | `OVERFLOW_TO_DISK` | `Lorg/apache/geode/cache/EvictionAction;` |
| `API-407fb8e11e5db141` | `org.apache.geode.cache.EvictionAction` | `clone` | `()Ljava/lang/Object;` |
| `API-583e1dcb2105ace5` | `org.apache.geode.cache.EvictionAction` | `getEnumValueTable` | `()[Ljavax/print/attribute/EnumSyntax;` |
| `API-f7f96d33a28996a7` | `org.apache.geode.cache.EvictionAction` | `getOffset` | `()I` |
| `API-57d2ea7d74a96dde` | `org.apache.geode.cache.EvictionAction` | `getStringTable` | `()[Ljava/lang/String;` |
| `API-13834e4a9950b266` | `org.apache.geode.cache.EvictionAction` | `getValue` | `()I` |
| `API-3078b9b83c97d3b7` | `org.apache.geode.cache.EvictionAction` | `hashCode` | `()I` |
| `API-54740f5473da64bd` | `org.apache.geode.cache.EvictionAction` | `isLocalDestroy` | `()Z` |
| `API-9f7df50a7d8c3f48` | `org.apache.geode.cache.EvictionAction` | `isNone` | `()Z` |
| `API-19b424577b952c80` | `org.apache.geode.cache.EvictionAction` | `isOverflowToDisk` | `()Z` |
| `API-54d0a5b43f8da1b0` | `org.apache.geode.cache.EvictionAction` | `parseAction` | `(Ljava/lang/String;)Lorg/apache/geode/cache/EvictionAction;` |
| `API-1dc29c0f2674f823` | `org.apache.geode.cache.EvictionAction` | `parseValue` | `(I)Lorg/apache/geode/cache/EvictionAction;` |
| `API-8488884e55ad2732` | `org.apache.geode.cache.EvictionAction` | `readResolve` | `()Ljava/lang/Object;` |
| `API-183fad3c3b67c255` | `org.apache.geode.cache.EvictionAction` | `toString` | `()Ljava/lang/String;` |
| `API-b5629a68ef240974` | `org.apache.geode.cache.EvictionAlgorithm` | `<type>` | `(type)` |
| `API-8dbf98697be8583c` | `org.apache.geode.cache.EvictionAlgorithm` | `LIFO_ENTRY` | `Lorg/apache/geode/cache/EvictionAlgorithm;` |
| `API-ab04fcafff8478f3` | `org.apache.geode.cache.EvictionAlgorithm` | `LIFO_MEMORY` | `Lorg/apache/geode/cache/EvictionAlgorithm;` |
| `API-18b608108a1b6ab2` | `org.apache.geode.cache.EvictionAlgorithm` | `LRU_ENTRY` | `Lorg/apache/geode/cache/EvictionAlgorithm;` |
| `API-f3dd875a8c13cd0c` | `org.apache.geode.cache.EvictionAlgorithm` | `LRU_HEAP` | `Lorg/apache/geode/cache/EvictionAlgorithm;` |
| `API-d7f9084e31ba120d` | `org.apache.geode.cache.EvictionAlgorithm` | `LRU_MEMORY` | `Lorg/apache/geode/cache/EvictionAlgorithm;` |
| `API-cc74feeadddadd9e` | `org.apache.geode.cache.EvictionAlgorithm` | `NONE` | `Lorg/apache/geode/cache/EvictionAlgorithm;` |
| `API-b3d3e4ec91394518` | `org.apache.geode.cache.EvictionAlgorithm` | `clone` | `()Ljava/lang/Object;` |
| `API-3618bcaaade913f9` | `org.apache.geode.cache.EvictionAlgorithm` | `getEnumValueTable` | `()[Ljavax/print/attribute/EnumSyntax;` |
| `API-f01f8665aa19d8bb` | `org.apache.geode.cache.EvictionAlgorithm` | `getOffset` | `()I` |
| `API-0ef565cadc228b1a` | `org.apache.geode.cache.EvictionAlgorithm` | `getStringTable` | `()[Ljava/lang/String;` |
| `API-8607faf8ca61eafa` | `org.apache.geode.cache.EvictionAlgorithm` | `getValue` | `()I` |
| `API-3c266212c985b9d9` | `org.apache.geode.cache.EvictionAlgorithm` | `hashCode` | `()I` |
| `API-58613d10213407bc` | `org.apache.geode.cache.EvictionAlgorithm` | `isLIFO` | `()Z` |
| `API-a53d5546d3535e1b` | `org.apache.geode.cache.EvictionAlgorithm` | `isLRU` | `()Z` |
| `API-cdcda0bf8fa00e2e` | `org.apache.geode.cache.EvictionAlgorithm` | `isLRUEntry` | `()Z` |
| `API-06c9455aede771eb` | `org.apache.geode.cache.EvictionAlgorithm` | `isLRUHeap` | `()Z` |
| `API-24ef34e82701cec5` | `org.apache.geode.cache.EvictionAlgorithm` | `isLRUMemory` | `()Z` |
| `API-d9e7caea376eb2b1` | `org.apache.geode.cache.EvictionAlgorithm` | `isNone` | `()Z` |
| `API-553af1692ac186cd` | `org.apache.geode.cache.EvictionAlgorithm` | `parseAction` | `(Ljava/lang/String;)Lorg/apache/geode/cache/EvictionAlgorithm;` |
| `API-0fadd267f06cd079` | `org.apache.geode.cache.EvictionAlgorithm` | `parseValue` | `(I)Lorg/apache/geode/cache/EvictionAlgorithm;` |
| `API-03431121b49e15c3` | `org.apache.geode.cache.EvictionAlgorithm` | `readResolve` | `()Ljava/lang/Object;` |
| `API-32d8de5247464e27` | `org.apache.geode.cache.EvictionAlgorithm` | `toString` | `()Ljava/lang/String;` |
| `API-c9c0d91cda4b89c1` | `org.apache.geode.cache.EvictionAttributes` | `<init>` | `()V` |
| `API-fdcee436e4a9e12e` | `org.apache.geode.cache.EvictionAttributes` | `<type>` | `(type)` |
| `API-e5ce9d10a4245790` | `org.apache.geode.cache.EvictionAttributes` | `DEFAULT_ENTRIES_MAXIMUM` | `I` |
| `API-3b7438c3cc1b5ae5` | `org.apache.geode.cache.EvictionAttributes` | `DEFAULT_MEMORY_MAXIMUM` | `I` |
| `API-90d8a9749e5d2a4e` | `org.apache.geode.cache.EvictionAttributes` | `convertToConfigEvictionAttributes` | `()Lorg/apache/geode/cache/configuration/RegionAttributesType$EvictionAttributes;` |
| `API-49aa64425ea32b0b` | `org.apache.geode.cache.EvictionAttributes` | `createLIFOEntryAttributes` | `(ILorg/apache/geode/cache/EvictionAction;)Lorg/apache/geode/cache/EvictionAttributes;` |
| `API-284f63fa7f12ad9e` | `org.apache.geode.cache.EvictionAttributes` | `createLIFOMemoryAttributes` | `(ILorg/apache/geode/cache/EvictionAction;)Lorg/apache/geode/cache/EvictionAttributes;` |
| `API-6734899d93e82683` | `org.apache.geode.cache.EvictionAttributes` | `createLRUEntryAttributes` | `()Lorg/apache/geode/cache/EvictionAttributes;` |
| `API-07f5916b5b751735` | `org.apache.geode.cache.EvictionAttributes` | `createLRUEntryAttributes` | `(I)Lorg/apache/geode/cache/EvictionAttributes;` |
| `API-00cfae8d8c8bc9c6` | `org.apache.geode.cache.EvictionAttributes` | `createLRUEntryAttributes` | `(ILorg/apache/geode/cache/EvictionAction;)Lorg/apache/geode/cache/EvictionAttributes;` |
| `API-7fee1608e3c9eaed` | `org.apache.geode.cache.EvictionAttributes` | `createLRUEntryAttributes` | `(Lorg/apache/geode/cache/EvictionAction;)Lorg/apache/geode/cache/EvictionAttributes;` |
| `API-00b4e9a0b378384a` | `org.apache.geode.cache.EvictionAttributes` | `createLRUHeapAttributes` | `()Lorg/apache/geode/cache/EvictionAttributes;` |
| `API-eba27fc8eb9245bf` | `org.apache.geode.cache.EvictionAttributes` | `createLRUHeapAttributes` | `(Lorg/apache/geode/cache/util/ObjectSizer;)Lorg/apache/geode/cache/EvictionAttributes;` |
| `API-027bd5f15820f82c` | `org.apache.geode.cache.EvictionAttributes` | `createLRUHeapAttributes` | `(Lorg/apache/geode/cache/util/ObjectSizer;Lorg/apache/geode/cache/EvictionAction;)Lorg/apache/geode/cache/EvictionAttributes;` |
| `API-1f6c2a85313b6d3b` | `org.apache.geode.cache.EvictionAttributes` | `createLRUMemoryAttributes` | `()Lorg/apache/geode/cache/EvictionAttributes;` |
| `API-b8b1ead3946ea1f7` | `org.apache.geode.cache.EvictionAttributes` | `createLRUMemoryAttributes` | `(I)Lorg/apache/geode/cache/EvictionAttributes;` |
| `API-c1a453fb278b4160` | `org.apache.geode.cache.EvictionAttributes` | `createLRUMemoryAttributes` | `(ILorg/apache/geode/cache/util/ObjectSizer;)Lorg/apache/geode/cache/EvictionAttributes;` |
| `API-f99dc5eeef747e70` | `org.apache.geode.cache.EvictionAttributes` | `createLRUMemoryAttributes` | `(ILorg/apache/geode/cache/util/ObjectSizer;Lorg/apache/geode/cache/EvictionAction;)Lorg/apache/geode/cache/EvictionAttributes;` |
| `API-531fe92b7262dfbe` | `org.apache.geode.cache.EvictionAttributes` | `createLRUMemoryAttributes` | `(Lorg/apache/geode/cache/util/ObjectSizer;)Lorg/apache/geode/cache/EvictionAttributes;` |
| `API-851fad2735f9cef6` | `org.apache.geode.cache.EvictionAttributes` | `createLRUMemoryAttributes` | `(Lorg/apache/geode/cache/util/ObjectSizer;Lorg/apache/geode/cache/EvictionAction;)Lorg/apache/geode/cache/EvictionAttributes;` |
| `API-55eb1c447571c924` | `org.apache.geode.cache.EvictionAttributes` | `equals` | `(Ljava/lang/Object;)Z` |
| `API-bdac789bd927f967` | `org.apache.geode.cache.EvictionAttributes` | `getAction` | `()Lorg/apache/geode/cache/EvictionAction;` |
| `API-4964cff9ae9bf7a2` | `org.apache.geode.cache.EvictionAttributes` | `getAlgorithm` | `()Lorg/apache/geode/cache/EvictionAlgorithm;` |
| `API-82e9007cec20cec0` | `org.apache.geode.cache.EvictionAttributes` | `getMaximum` | `()I` |
| `API-8fc0aed32ab2fdf5` | `org.apache.geode.cache.EvictionAttributes` | `getObjectSizer` | `()Lorg/apache/geode/cache/util/ObjectSizer;` |
| `API-ba6d79495c7ba5e3` | `org.apache.geode.cache.EvictionAttributes` | `hashCode` | `()I` |
| `API-cdf95a5de0e3c555` | `org.apache.geode.cache.EvictionAttributes` | `isNoEviction` | `()Z` |
| `API-2e958217ef63d30e` | `org.apache.geode.cache.EvictionAttributes` | `toString` | `()Ljava/lang/String;` |
| `API-3c162309099c0840` | `org.apache.geode.cache.EvictionAttributesMutator` | `<type>` | `(type)` |
| `API-6e95ec8eb1d9a5a8` | `org.apache.geode.cache.EvictionAttributesMutator` | `setMaximum` | `(I)V` |

### client-object-sizing — 5 IDs

| API ID | Native receiver | Member | JVM descriptor |
| --- | --- | --- | --- |
| `API-6824abeade8f2fd3` | `org.apache.geode.cache.util.ObjectSizer` | `<type>` | `(type)` |
| `API-e8e75b932466f9e7` | `org.apache.geode.cache.util.ObjectSizer` | `DEFAULT` | `Lorg/apache/geode/cache/util/ObjectSizer;` |
| `API-b53297c865dc412a` | `org.apache.geode.cache.util.ObjectSizer` | `REFLECTION_SIZE` | `Lorg/apache/geode/cache/util/ObjectSizer;` |
| `API-4b43bb9b5c0d9e44` | `org.apache.geode.cache.util.ObjectSizer` | `SIZE_CLASS_ONCE` | `Lorg/apache/geode/cache/util/ObjectSizer;` |
| `API-94246c584e9a42da` | `org.apache.geode.cache.util.ObjectSizer` | `sizeof` | `(Ljava/lang/Object;)I` |

### client-compression — 10 IDs

| API ID | Native receiver | Member | JVM descriptor |
| --- | --- | --- | --- |
| `API-b2db8373ab4ee48b` | `org.apache.geode.compression.Compressor` | `<type>` | `(type)` |
| `API-1b1209b487b993f6` | `org.apache.geode.compression.Compressor` | `compress` | `([B)[B` |
| `API-f1ac0a8871e752d1` | `org.apache.geode.compression.Compressor` | `decompress` | `([B)[B` |
| `API-101cbe63fa166ba9` | `org.apache.geode.compression.SnappyCompressor` | `<init>` | `()V` |
| `API-14587e18661c72ce` | `org.apache.geode.compression.SnappyCompressor` | `<type>` | `(type)` |
| `API-0040f5655dd64ed1` | `org.apache.geode.compression.SnappyCompressor` | `compress` | `([B)[B` |
| `API-f00d0f12a33d6a06` | `org.apache.geode.compression.SnappyCompressor` | `decompress` | `([B)[B` |
| `API-0f787e7b4a909f8b` | `org.apache.geode.compression.SnappyCompressor` | `equals` | `(Ljava/lang/Object;)Z` |
| `API-315735b37101bc1f` | `org.apache.geode.compression.SnappyCompressor` | `getDefaultInstance` | `()Lorg/apache/geode/compression/SnappyCompressor;` |
| `API-ad45b7d45d29466a` | `org.apache.geode.compression.SnappyCompressor` | `hashCode` | `()I` |
