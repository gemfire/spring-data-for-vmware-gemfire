# Client serialization, PDX and JSON scope — 2026-10-28

[Task 71](../../wayfinder-issues/71-audit-serialization-pdx-and-json-client-scope.md) settles **401 assigned IDs across 24 native types: 396 required-client / five excluded-server / zero unresolved**. Classification closes here; implementation status is unchanged.

Evidence: GemFire **10.4.0-build.3056.df98c835**, pinned source commit `df98c8357afe85853d9a4776255d4f0e7cf064cb` in `/Users/uk016635/projects/gemfire`, read using git show. Unless qualified, native source paths below are relative to `gemfire-core/src/main/java/org/apache/geode/` and end in `.java`; Spring paths are relative to `org.springframework.data.gemfire`. The exact-ID ledger defines each batch. Source resolves scope and representation; no new runtime probe or Java/build change is needed. Downstream compatibility acceptance remains open.

## pdx-values

**78 IDs — required-client.** The assigned Document, PdxInstance, WritablePdxInstance and PdxInstanceFactory members and inherited createPdxEnum/createPdxInstanceFactory operations are client value contracts. Document distinguishes absent fields using FIELD_NOT_FOUND through getAndCheckField; getField alone returns null for both absence and explicit null. Preserve immutable field-name views and foreach callback failures. PDX identity uses identity fields (all fields when none are marked), schema/type distinctions and default values, not generic Document equality. Writable copies retain schema; primitive null assignments become defaults. Factories reject duplicate fields, marking unwritten identity fields and repeated creation. Enum name/ordinal consistency and non-writable enum behavior matter. Creation may allocate a factory without a pool, but registration requires a pool; do not silently create one.

Pinned sources: cache/Document, cache/RegionService, cache/GemFireCache, cache/Cache, cache/client/ClientCache; pdx/PdxInstance, WritablePdxInstance, PdxInstanceFactory; pdx/internal/PdxInstanceImpl and PdxInstanceFactoryImpl. The earlier [foundation investigation](pdx-values-and-instance-factories.md) records source, shared contract tests and native byte/client-server evidence. Existing native-free value interfaces and adapters remain mapped. Mock supports local snapshots, identity, writable values and connection-local enums, with explicit unsupported application materialization and cyclic encoding. Task 58 must finish keys/values, batches, conditional operations, entry/live views, callbacks/error old values, nested query/function results, supported aliases/cycles and Serializable replacement. Task 31 owns Spring mapping adoption. The implemented missing-field sentinel remains owned by resolved task 53; the other 77 partial rows move to active task 58 without upgrading their status.

## pdx-callbacks

**60 IDs — required-client.** Pinned pdx/PdxReader, PdxWriter, PdxSerializer and PdxUnreadFields define scalar/object/array/nested reads and writes, metadata, identity and portability options. pdx/internal/PdxReaderImpl and PdxWriterImpl implement missing-field defaults versus type errors. readUnreadFields explicitly selects manual preservation; writeUnreadFields must be the first writer operation. Ignore-unread-fields yields an empty carrier. Automatic preservation must not be confused with explicitly reading but then discarding the carrier. PdxSerializer toData=false and fromData=null decline handling; retain application callback/class-loading errors through agreed translation. Task 54 owns typed callbacks and native/mock acceptance; task 31 owns MappingPdxSerializer adoption. PdxSerializable discovery already belongs to task 54 and is not silently added to these 401 fixed IDs.

## client-pdx-settings

**18 IDs — required-client.** Five getters and registerPdxMetaData on each of Cache, GemFireCache and ClientCache account for 18 rows. These shared operations have client equivalents despite a Cache receiver. GemFireCacheImpl.registerPdxMetaData serializes through BlobHelper, requires the PDX marker and rejects non-PDX/serialization failures; its JDBC-loader documentation does not restrict it to server hosting. Preserve actual serialize/validation behavior, not a no-op mock success.

cache/client/ClientCacheFactory explicitly deprecates setPdxPersistent and setPdxDiskStore since 9.8 and says they are ignored on clients. Configuration/getters can preserve requested values; pdx/internal/ClientTypeRegistration warns rather than providing client metadata persistence. TypeRegistry.mayNeedDiskStore excludes caches with pools, and client type/enum registration uses server pools. Task 54 must test acceptance/readback and absence of a client persistence promise; task 35 owns client factory/annotation/XML adoption. Retain read-serialized, unread-field and serializer configuration through typed client contracts. ClientCacheFactory setters already assigned elsewhere remain unchanged.

## peer-pdx-settings

**5 IDs — excluded-server.** cache/CacheFactory setPdxDiskStore, setPdxIgnoreUnreadFields, setPdxPersistent, setPdxReadSerialized and setPdxSerializer configure peer creation. Task 67 already excluded its peer constructor/create path. These five rows have missing status and no implementation mappings or linked production/SPI references to retire. Do not exclude the entire mixed CacheFactory type: previously classified static client lookup replacements and ClientCacheFactory configuration remain independent. No public peer builder is required for client PDX support.

## binary-callback-registration

**19 IDs — required-client.** DataSerializable supplies DataInput/DataOutput callbacks and checked IO/class-loading behavior. DataSerializer provides a no-arg construction contract, getId/getSupportedClasses, boolean toData decline and fromData; equality requires both ID and concrete class, hashCode uses ID. register(Class) instantiates a serializer, requires supported classes and checks collisions/predefined classes through internal/InternalDataSerializer. Instantiator requires a non-null DataSerializable class; implementation rejects zero ID but accepts negative IDs despite the broader Javadoc claim. newInstance creates the empty target subsequently populated by fromData.

Instantiator register defaults to distribution. Its deprecated boolean overload does not guarantee strict local-only behavior: internal/InternalInstantiator gates peer/server propagation but still notifies clients/listeners. PoolManagerImpl and cache/client/internal/RegisterDataSerializersOp/RegisterInstantiatorsOp establish client-to-server propagation. Keep versioned native registry ownership private; task 55 provides injected registration/typed callbacks and mock-controlled collisions. Task 31 migrates serialization/EnumSerializer, InstantiatorFactoryBean, AsmInstantiatorGenerator and WiringInstantiator. EnumSerializer really uses lookup/unregister/register when extending enum classes; do not invent a registrar consumer. Preserve the factory's autoRegister/distribute configuration without promising false means strictly local. DataSerializable source is in gemfire-serialization/src/main/java, unlike the remaining core sources.

## binary-registration-metadata

**8 IDs — required-client.** DataSerializer and Instantiator each expose get/setEventId and get/setContext. Their Object signatures conceal EventID and ClientProxyMembershipID casts and registration propagation state. This is required internal behavior, not eight public opaque SPI payload methods. Task 55 must carry the necessary state inside its versioned registration adapter and test it with client registration; the mock models observable registration outcomes. Task 31 removes public/native dependence in Spring consumers.

## binary-codec-state

**6 IDs — required-client.** DataSerializer PRESENT=0x80, INT=1, LONG=2 and DOUBLE=4 describe optional-value framing. TRACE_SERIALIZABLE is a deprecated system-property diagnostic. DISALLOW_JAVA_SERIALIZATION is native mutable thread-local state used by writeObject(...,allowJavaSerialization). The pinned implementation sets true and finally false rather than restoring prior nested state. Task 55 must explicitly test/version that behavior or record a deliberate replacement; an injected typed per-call option must not accidentally promise native nested restoration. No public ThreadLocal or native diagnostic constant is needed to preserve client serialization requirements.

## binary-region-codecs

**2 IDs — required-client.** DataSerializer.writeRegion writes a null marker or full region path; readRegion resolves against the existing cache and raises region-not-found or cache-closed failures. This represents client region references, not server region creation. Task 55 owns typed codec/resolver behavior and failure translation; task 31 owns Spring adoption. Keep the native lookup private and bind the SPI codec to an explicit client context.

## binary-value-codecs

**115 IDs — required-client.** The remaining DataSerializer read/write methods cover scalars, primitive/object/nested arrays, strings, class names, enums, dates/files/addresses, collections/maps including sorted/identity/concurrent forms, properties, Optional forms and object-as-byte-array encoding. Pinned DataSerializer and internal/InternalDataSerializer are the source for exact encodings; do not equate the helpers with Java object serialization. Preserve nullness, collection kind, ordering, comparator/class resolution, framing and IO/class-loading failures. readObject(ObjectInputFilter) only applies that filter to the Java Serializable branch, not PDX or DataSerializable. Checked portability and Java-serialization options need typed equivalents and private versioned wire codecs. Task 55 owns round-trip/native compatibility and mock local-versus-unsupported boundaries, task 31 the consumers. Query metadata codecs from task 65 and expiry codecs from task 68 stay in their original assignments.

## binary-registry-lookup

**3 IDs — required-client.** internal/InternalDataSerializer.getSerializer(int) can wait for registration markers, resolve and instantiate a holder's class and return null for unavailable/expired registration. unregister removes completed serializer/class mappings and relevant holders; an absent ID is a no-op. This does not establish atomic distributed unregister. Spring EnumSerializer uses the lookup/remove/register sequence when enum support changes. Task 55 owns injected client registration and versioned ordering/collision/failure acceptance, with task 31 replacing the static consumer. The internal type row denotes this capability, not public native internals.

## json-documents

**18 IDs — required-client.** json/JsonDocument inherits Document and SerializableAsJson; JsonDocumentFactory creates a single JSON object, rejects malformed/non-object input and is reusable/thread-safe. Nested objects are JsonDocument, arrays are lists, and scalar numeric representation can include int/long/BigInteger/double and PDX byte/short narrowing. PDX storage uses an external type registry; BSON is self-contained. Both StorageFormat values are retained, with native-free public selection. Task 56 owns typed documents, parser/materialization errors, native format compatibility and explicit mock local JSON versus unsupported wire behavior. Task 31 owns serialization/json/JSONRegionAdvice and other Spring adoption.

## json-pdx-conversion

**15 IDs — required-client.** pdx/JSONFormatter constructors support absent or supplied RegionService; its static conveniences, identity overloads, JSON_CLASSNAME (__GEMFIRE_JSON) and SORT_JSON_FIELD_NAMES_PROPERTY (gemfire.pdx.mapper.sort-json-field-names) affect conversion. pdx/internal/json/PdxJsonDocumentFactory creates the PDX document and JSONFormatter wraps parse failures. For ProxyCache, native UserAttributes thread-local state is set then cleared, not restored; keep authentication context private and verify cleanup. pdx/internal/json/DocumentToPdxCreator uses CacheFactory.getAnyInstance and createPdxInstanceFactory(JSON_CLASSNAME,false); the public replacement must use an injected client service.

Preserve numeric narrowing, nested/list/null values and identity marking after field writes. toJsonByteArrayFromPdxInstance produces UTF-8 bytes, but fromPdxInstance uses new String(bytes) with the default charset. Task 56 must test pinned legacy behavior or explicitly document normalization; do not assert all existing string paths use UTF-8. Task 31 migrates JSONRegionAdvice's static conversions and CRUD/bulk/values/query integration. This audit does not change existing Spring conversion behavior.

## reflection-serializer

**22 IDs — required-client.** pdx/ReflectionBasedAutoSerializer and pdx/internal/AutoSerializableManager implement constructors, class patterns, check-portability, init/initialize/reconfigure, getConfig, serializer callbacks and protected customization hooks. Pattern grammar includes identity/exclude expressions; unknown properties fail. Reconfiguration clears cached metadata. getConfig is normalized equivalent configuration, not byte-identical original properties, and no patterns yields empty properties. Default field selection excludes static/transient/explicitly excluded fields; default names/types follow reflection/FieldType, transforms are disabled and transform hooks return input.

Class selection rejects enums/internal/Java exclusions and handles constructors, Java serialization hooks and Externalizable restrictions, with native unsafe construction optimization affecting constructor requirements. initialize(Cache,Properties) supplies context, not permission to create a peer cache. Task 57 owns typed configuration/hooks and versioned selection/construction/transform acceptance, task 54 callback integration, task 31 MappingPdxSerializer/PdxConfiguration adoption. Mock must define deterministic local reflection support or explicit unsupported behavior without emulating native wire encoding.

## reflection-manager

**1 IDs — required-client.** ReflectionBasedAutoSerializer.getManager returns the internal manager and expressly documents it as internal use, not public API. AutoSerializableManager owns cached configuration/field decisions and serialization operations. Task 57 must preserve those behaviors behind typed configuration and hooks, without adding an Object-returning SPI escape hatch. Spring adoption remains task 31; required-client classification does not mandate copying every native internal accessor.

## pdx-field-types

**31 IDs — required-client.** pdx/FieldType defines 22 values plus type/conversions/metadata. get(Class) recognizes primitive types, exact String/Date, primitive arrays, String[] and byte[][]; other arrays map to OBJECT_ARRAY, other objects (including boxed Integer) to OBJECT. Fixed width versus -1 variable width and date's eight-byte/-1 wire default matter. Java default values are the actual source literals: several numeric defaults are boxed Integer zero, not automatically the corresponding wrapper type. Do not infer wrapper classes from enum names.

getDefaultBytes returns the same read-only ByteBuffer: bytes are read-only, position remains mutable. This is native implementation metadata, not a reason to expose a shared mutable buffer through the SPI. Task 57 must specify stable typed kinds/defaults with private versioned codec bytes and record any intentional replacement of native alias behavior; coordinate task 55 codecs and task 54 field callbacks. Task 31 owns Spring adoption. No runtime probe is required to classify these source-explicit declarations.

## Ownership and reconciliation

All unfinished retained rows have active implementation owners 54–58 and existing active consumer owners 31, 35 or 58. The 77 unfinished PDX value entries move from resolved foundation 53 to active boundary-adaptation task 58; their existing consumer owner 58 remains, with Spring adoption explicitly handed to task 31. The implemented FIELD_NOT_FOUND sentinel stays with foundation 53. Binary registration, JSON conversion and reflection work remain with 55, 56 and 57. Five peer CacheFactory setters are unmapped/missing and have no linked production or existing SPI references requiring retirement. Shared client settings/creation and all frozen member declarations stay intact.

Private native metadata, registries, codec constants and manager objects are behavioral requirements with typed replacements or private adapters, not a mandate to publish native signatures. Task 64 owns newly discovered IDs; tasks 59/64 remain open until other families, source reconciliation and server fixture retirement complete. No native-free compliance or implementation completion is claimed.

## Exact ID ledger

Each row inherits its batch rationale/source evidence. Owners below show the resulting implementation and consumer task numbers; exclusions carry no new implementation obligation.

### pdx-values — 78 IDs

| API ID | Native receiver | Member | JVM descriptor | Owner | Consumer |
| --- | --- | --- | --- | --- | --- |
| `API-a5b3134c3d799c30` | `org.apache.geode.cache.Cache` | `createPdxEnum` | `(Ljava/lang/String;Ljava/lang/String;I)Lorg/apache/geode/pdx/PdxInstance;` | 58 | 58 |
| `API-12e7fdb6d3304fe9` | `org.apache.geode.cache.Cache` | `createPdxInstanceFactory` | `(Ljava/lang/String;)Lorg/apache/geode/pdx/PdxInstanceFactory;` | 58 | 58 |
| `API-60de3e62439c7fde` | `org.apache.geode.cache.Document` | `<type>` | `(type)` | 58 | 58 |
| `API-cd642fa24e05c872` | `org.apache.geode.cache.Document` | `FIELD_NOT_FOUND` | `Ljava/lang/Object;` | 53 | 58 |
| `API-5da812068b81ede8` | `org.apache.geode.cache.Document` | `equals` | `(Ljava/lang/Object;)Z` | 58 | 58 |
| `API-eb563ca690b9be9b` | `org.apache.geode.cache.Document` | `foreach` | `(Ljava/util/function/BiConsumer;)V` | 58 | 58 |
| `API-089ac550766cace6` | `org.apache.geode.cache.Document` | `getAndCheckField` | `(Ljava/lang/String;)Ljava/lang/Object;` | 58 | 58 |
| `API-60899c0e37472a0e` | `org.apache.geode.cache.Document` | `getField` | `(Ljava/lang/String;)Ljava/lang/Object;` | 58 | 58 |
| `API-16749f1cf55eac07` | `org.apache.geode.cache.Document` | `getFieldNames` | `()Ljava/util/List;` | 58 | 58 |
| `API-7ae00f3c5a11e962` | `org.apache.geode.cache.Document` | `hasField` | `(Ljava/lang/String;)Z` | 58 | 58 |
| `API-4f4279405515eddd` | `org.apache.geode.cache.Document` | `hashCode` | `()I` | 58 | 58 |
| `API-8a4977b13a828c31` | `org.apache.geode.cache.GemFireCache` | `createPdxEnum` | `(Ljava/lang/String;Ljava/lang/String;I)Lorg/apache/geode/pdx/PdxInstance;` | 58 | 58 |
| `API-c9e3e605c71471ca` | `org.apache.geode.cache.GemFireCache` | `createPdxInstanceFactory` | `(Ljava/lang/String;)Lorg/apache/geode/pdx/PdxInstanceFactory;` | 58 | 58 |
| `API-77fa9898c0dc2245` | `org.apache.geode.cache.RegionService` | `createPdxEnum` | `(Ljava/lang/String;Ljava/lang/String;I)Lorg/apache/geode/pdx/PdxInstance;` | 58 | 58 |
| `API-e565fb7c760dac8d` | `org.apache.geode.cache.RegionService` | `createPdxInstanceFactory` | `(Ljava/lang/String;)Lorg/apache/geode/pdx/PdxInstanceFactory;` | 58 | 58 |
| `API-464280c86745c1ab` | `org.apache.geode.cache.client.ClientCache` | `createPdxEnum` | `(Ljava/lang/String;Ljava/lang/String;I)Lorg/apache/geode/pdx/PdxInstance;` | 58 | 58 |
| `API-bd380e2fa3db46e0` | `org.apache.geode.cache.client.ClientCache` | `createPdxInstanceFactory` | `(Ljava/lang/String;)Lorg/apache/geode/pdx/PdxInstanceFactory;` | 58 | 58 |
| `API-f1f1449388932fdf` | `org.apache.geode.pdx.PdxInstance` | `<type>` | `(type)` | 58 | 58 |
| `API-eb4be468c17d988c` | `org.apache.geode.pdx.PdxInstance` | `createWriter` | `()Lorg/apache/geode/pdx/WritablePdxInstance;` | 58 | 58 |
| `API-f3e4d1d71d3f3f1d` | `org.apache.geode.pdx.PdxInstance` | `equals` | `(Ljava/lang/Object;)Z` | 58 | 58 |
| `API-d112bda2cc117765` | `org.apache.geode.pdx.PdxInstance` | `foreach` | `(Ljava/util/function/BiConsumer;)V` | 58 | 58 |
| `API-7eb6be720c40b711` | `org.apache.geode.pdx.PdxInstance` | `getAndCheckField` | `(Ljava/lang/String;)Ljava/lang/Object;` | 58 | 58 |
| `API-86199ff8b63e96bf` | `org.apache.geode.pdx.PdxInstance` | `getClassName` | `()Ljava/lang/String;` | 58 | 58 |
| `API-df039635df3b140b` | `org.apache.geode.pdx.PdxInstance` | `getField` | `(Ljava/lang/String;)Ljava/lang/Object;` | 58 | 58 |
| `API-4aff1060007e5375` | `org.apache.geode.pdx.PdxInstance` | `getFieldNames` | `()Ljava/util/List;` | 58 | 58 |
| `API-d6008dc6a669ae59` | `org.apache.geode.pdx.PdxInstance` | `getObject` | `()Ljava/lang/Object;` | 58 | 58 |
| `API-a0bbc81a0ee83ef6` | `org.apache.geode.pdx.PdxInstance` | `hasField` | `(Ljava/lang/String;)Z` | 58 | 58 |
| `API-e26e0bddb7f55c48` | `org.apache.geode.pdx.PdxInstance` | `hashCode` | `()I` | 58 | 58 |
| `API-307020d7cb8fd152` | `org.apache.geode.pdx.PdxInstance` | `isDeserializable` | `()Z` | 58 | 58 |
| `API-def995d9aa68a704` | `org.apache.geode.pdx.PdxInstance` | `isEnum` | `()Z` | 58 | 58 |
| `API-9f98295f6af6f902` | `org.apache.geode.pdx.PdxInstance` | `isIdentityField` | `(Ljava/lang/String;)Z` | 58 | 58 |
| `API-40e05afabe86dadd` | `org.apache.geode.pdx.PdxInstance` | `toString` | `()Ljava/lang/String;` | 58 | 58 |
| `API-941e3f8a8d8b369f` | `org.apache.geode.pdx.PdxInstanceFactory` | `<type>` | `(type)` | 58 | 58 |
| `API-5b3f7662eafd2889` | `org.apache.geode.pdx.PdxInstanceFactory` | `create` | `()Lorg/apache/geode/pdx/PdxInstance;` | 58 | 58 |
| `API-6e4fed99293ad5fd` | `org.apache.geode.pdx.PdxInstanceFactory` | `markIdentityField` | `(Ljava/lang/String;)Lorg/apache/geode/pdx/PdxInstanceFactory;` | 58 | 58 |
| `API-fade16134f6f3cfb` | `org.apache.geode.pdx.PdxInstanceFactory` | `neverDeserialize` | `()Lorg/apache/geode/pdx/PdxInstanceFactory;` | 58 | 58 |
| `API-1dfe5979a2014685` | `org.apache.geode.pdx.PdxInstanceFactory` | `writeArrayOfByteArrays` | `(Ljava/lang/String;[[B)Lorg/apache/geode/pdx/PdxInstanceFactory;` | 58 | 58 |
| `API-3fb1b018653bfb8b` | `org.apache.geode.pdx.PdxInstanceFactory` | `writeBoolean` | `(Ljava/lang/String;Z)Lorg/apache/geode/pdx/PdxInstanceFactory;` | 58 | 58 |
| `API-da7c8a8485751386` | `org.apache.geode.pdx.PdxInstanceFactory` | `writeBooleanArray` | `(Ljava/lang/String;[Z)Lorg/apache/geode/pdx/PdxInstanceFactory;` | 58 | 58 |
| `API-a327f56581c77ceb` | `org.apache.geode.pdx.PdxInstanceFactory` | `writeByte` | `(Ljava/lang/String;B)Lorg/apache/geode/pdx/PdxInstanceFactory;` | 58 | 58 |
| `API-96e2c67c6a3d41d1` | `org.apache.geode.pdx.PdxInstanceFactory` | `writeByteArray` | `(Ljava/lang/String;[B)Lorg/apache/geode/pdx/PdxInstanceFactory;` | 58 | 58 |
| `API-24368d4c2b7dbb99` | `org.apache.geode.pdx.PdxInstanceFactory` | `writeChar` | `(Ljava/lang/String;C)Lorg/apache/geode/pdx/PdxInstanceFactory;` | 58 | 58 |
| `API-84cbae326014c5eb` | `org.apache.geode.pdx.PdxInstanceFactory` | `writeCharArray` | `(Ljava/lang/String;[C)Lorg/apache/geode/pdx/PdxInstanceFactory;` | 58 | 58 |
| `API-9f65750821e9902b` | `org.apache.geode.pdx.PdxInstanceFactory` | `writeDate` | `(Ljava/lang/String;Ljava/util/Date;)Lorg/apache/geode/pdx/PdxInstanceFactory;` | 58 | 58 |
| `API-2e5ba0fad603cbde` | `org.apache.geode.pdx.PdxInstanceFactory` | `writeDouble` | `(Ljava/lang/String;D)Lorg/apache/geode/pdx/PdxInstanceFactory;` | 58 | 58 |
| `API-4eed7f40e2983903` | `org.apache.geode.pdx.PdxInstanceFactory` | `writeDoubleArray` | `(Ljava/lang/String;[D)Lorg/apache/geode/pdx/PdxInstanceFactory;` | 58 | 58 |
| `API-1c4e1716854c03a3` | `org.apache.geode.pdx.PdxInstanceFactory` | `writeField` | `(Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Class;)Lorg/apache/geode/pdx/PdxInstanceFactory;` | 58 | 58 |
| `API-c2388933f5e27554` | `org.apache.geode.pdx.PdxInstanceFactory` | `writeField` | `(Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Class;Z)Lorg/apache/geode/pdx/PdxInstanceFactory;` | 58 | 58 |
| `API-59145a40756a4b67` | `org.apache.geode.pdx.PdxInstanceFactory` | `writeFloat` | `(Ljava/lang/String;F)Lorg/apache/geode/pdx/PdxInstanceFactory;` | 58 | 58 |
| `API-f4b2139b61087afe` | `org.apache.geode.pdx.PdxInstanceFactory` | `writeFloatArray` | `(Ljava/lang/String;[F)Lorg/apache/geode/pdx/PdxInstanceFactory;` | 58 | 58 |
| `API-5d2ce3d7539626e5` | `org.apache.geode.pdx.PdxInstanceFactory` | `writeInt` | `(Ljava/lang/String;I)Lorg/apache/geode/pdx/PdxInstanceFactory;` | 58 | 58 |
| `API-defcd04c6ba2e79f` | `org.apache.geode.pdx.PdxInstanceFactory` | `writeIntArray` | `(Ljava/lang/String;[I)Lorg/apache/geode/pdx/PdxInstanceFactory;` | 58 | 58 |
| `API-e3d96cb9f39cacfb` | `org.apache.geode.pdx.PdxInstanceFactory` | `writeLong` | `(Ljava/lang/String;J)Lorg/apache/geode/pdx/PdxInstanceFactory;` | 58 | 58 |
| `API-8fec9f69231771a5` | `org.apache.geode.pdx.PdxInstanceFactory` | `writeLongArray` | `(Ljava/lang/String;[J)Lorg/apache/geode/pdx/PdxInstanceFactory;` | 58 | 58 |
| `API-403759d1435232af` | `org.apache.geode.pdx.PdxInstanceFactory` | `writeObject` | `(Ljava/lang/String;Ljava/lang/Object;)Lorg/apache/geode/pdx/PdxInstanceFactory;` | 58 | 58 |
| `API-e37f1ce8c0b8d87d` | `org.apache.geode.pdx.PdxInstanceFactory` | `writeObject` | `(Ljava/lang/String;Ljava/lang/Object;Z)Lorg/apache/geode/pdx/PdxInstanceFactory;` | 58 | 58 |
| `API-53767eaef8d128b2` | `org.apache.geode.pdx.PdxInstanceFactory` | `writeObjectArray` | `(Ljava/lang/String;[Ljava/lang/Object;)Lorg/apache/geode/pdx/PdxInstanceFactory;` | 58 | 58 |
| `API-d4b536ff54df12e0` | `org.apache.geode.pdx.PdxInstanceFactory` | `writeObjectArray` | `(Ljava/lang/String;[Ljava/lang/Object;Z)Lorg/apache/geode/pdx/PdxInstanceFactory;` | 58 | 58 |
| `API-922bad21cc4d2130` | `org.apache.geode.pdx.PdxInstanceFactory` | `writeShort` | `(Ljava/lang/String;S)Lorg/apache/geode/pdx/PdxInstanceFactory;` | 58 | 58 |
| `API-45195151fcc015bf` | `org.apache.geode.pdx.PdxInstanceFactory` | `writeShortArray` | `(Ljava/lang/String;[S)Lorg/apache/geode/pdx/PdxInstanceFactory;` | 58 | 58 |
| `API-5e02fa1470f50705` | `org.apache.geode.pdx.PdxInstanceFactory` | `writeString` | `(Ljava/lang/String;Ljava/lang/String;)Lorg/apache/geode/pdx/PdxInstanceFactory;` | 58 | 58 |
| `API-0fedc53f44bbb738` | `org.apache.geode.pdx.PdxInstanceFactory` | `writeStringArray` | `(Ljava/lang/String;[Ljava/lang/String;)Lorg/apache/geode/pdx/PdxInstanceFactory;` | 58 | 58 |
| `API-946310a99e2cdbe2` | `org.apache.geode.pdx.WritablePdxInstance` | `<type>` | `(type)` | 58 | 58 |
| `API-93220423223f2aaf` | `org.apache.geode.pdx.WritablePdxInstance` | `createWriter` | `()Lorg/apache/geode/pdx/WritablePdxInstance;` | 58 | 58 |
| `API-608f383e4a2f6da6` | `org.apache.geode.pdx.WritablePdxInstance` | `equals` | `(Ljava/lang/Object;)Z` | 58 | 58 |
| `API-12405961e99b7910` | `org.apache.geode.pdx.WritablePdxInstance` | `foreach` | `(Ljava/util/function/BiConsumer;)V` | 58 | 58 |
| `API-5f04f850e3fcc0ae` | `org.apache.geode.pdx.WritablePdxInstance` | `getAndCheckField` | `(Ljava/lang/String;)Ljava/lang/Object;` | 58 | 58 |
| `API-7a6fe58c4ffcd8b0` | `org.apache.geode.pdx.WritablePdxInstance` | `getClassName` | `()Ljava/lang/String;` | 58 | 58 |
| `API-46bc2cb1bcafa821` | `org.apache.geode.pdx.WritablePdxInstance` | `getField` | `(Ljava/lang/String;)Ljava/lang/Object;` | 58 | 58 |
| `API-f794bf8fc189bb72` | `org.apache.geode.pdx.WritablePdxInstance` | `getFieldNames` | `()Ljava/util/List;` | 58 | 58 |
| `API-ed56085f64c8ddc3` | `org.apache.geode.pdx.WritablePdxInstance` | `getObject` | `()Ljava/lang/Object;` | 58 | 58 |
| `API-e442818a18951308` | `org.apache.geode.pdx.WritablePdxInstance` | `hasField` | `(Ljava/lang/String;)Z` | 58 | 58 |
| `API-1875687d9ad48c97` | `org.apache.geode.pdx.WritablePdxInstance` | `hashCode` | `()I` | 58 | 58 |
| `API-5c024ce2da2d72df` | `org.apache.geode.pdx.WritablePdxInstance` | `isDeserializable` | `()Z` | 58 | 58 |
| `API-50d989474afa27e1` | `org.apache.geode.pdx.WritablePdxInstance` | `isEnum` | `()Z` | 58 | 58 |
| `API-f3343044c5b86055` | `org.apache.geode.pdx.WritablePdxInstance` | `isIdentityField` | `(Ljava/lang/String;)Z` | 58 | 58 |
| `API-8dec500959cdd8d1` | `org.apache.geode.pdx.WritablePdxInstance` | `setField` | `(Ljava/lang/String;Ljava/lang/Object;)V` | 58 | 58 |
| `API-ae3802268a1ca336` | `org.apache.geode.pdx.WritablePdxInstance` | `toString` | `()Ljava/lang/String;` | 58 | 58 |

### pdx-callbacks — 60 IDs

| API ID | Native receiver | Member | JVM descriptor | Owner | Consumer |
| --- | --- | --- | --- | --- | --- |
| `API-cd40fef1800b5a54` | `org.apache.geode.pdx.PdxReader` | `<type>` | `(type)` | 54 | 31 |
| `API-01abbca6a2a67360` | `org.apache.geode.pdx.PdxReader` | `hasField` | `(Ljava/lang/String;)Z` | 54 | 31 |
| `API-ec92d2f2b34c8db7` | `org.apache.geode.pdx.PdxReader` | `isIdentityField` | `(Ljava/lang/String;)Z` | 54 | 31 |
| `API-4166414a302e28a9` | `org.apache.geode.pdx.PdxReader` | `readArrayOfByteArrays` | `(Ljava/lang/String;)[[B` | 54 | 31 |
| `API-5b7a656cc7a18790` | `org.apache.geode.pdx.PdxReader` | `readBoolean` | `(Ljava/lang/String;)Z` | 54 | 31 |
| `API-f8115b769fa6ea99` | `org.apache.geode.pdx.PdxReader` | `readBooleanArray` | `(Ljava/lang/String;)[Z` | 54 | 31 |
| `API-8e570940039a2a6d` | `org.apache.geode.pdx.PdxReader` | `readByte` | `(Ljava/lang/String;)B` | 54 | 31 |
| `API-9a068c83516b73aa` | `org.apache.geode.pdx.PdxReader` | `readByteArray` | `(Ljava/lang/String;)[B` | 54 | 31 |
| `API-829ab227ff747dcd` | `org.apache.geode.pdx.PdxReader` | `readChar` | `(Ljava/lang/String;)C` | 54 | 31 |
| `API-8e0647e42522ceb5` | `org.apache.geode.pdx.PdxReader` | `readCharArray` | `(Ljava/lang/String;)[C` | 54 | 31 |
| `API-5c682af80acd534c` | `org.apache.geode.pdx.PdxReader` | `readDate` | `(Ljava/lang/String;)Ljava/util/Date;` | 54 | 31 |
| `API-df9ab1bff3d56601` | `org.apache.geode.pdx.PdxReader` | `readDouble` | `(Ljava/lang/String;)D` | 54 | 31 |
| `API-483df80679b8d9a0` | `org.apache.geode.pdx.PdxReader` | `readDoubleArray` | `(Ljava/lang/String;)[D` | 54 | 31 |
| `API-10ae4d1d14e09fb1` | `org.apache.geode.pdx.PdxReader` | `readField` | `(Ljava/lang/String;)Ljava/lang/Object;` | 54 | 31 |
| `API-91b37df7d31f41a3` | `org.apache.geode.pdx.PdxReader` | `readFloat` | `(Ljava/lang/String;)F` | 54 | 31 |
| `API-f9fb38a58df0b101` | `org.apache.geode.pdx.PdxReader` | `readFloatArray` | `(Ljava/lang/String;)[F` | 54 | 31 |
| `API-9be4c6b0f99d1f86` | `org.apache.geode.pdx.PdxReader` | `readInt` | `(Ljava/lang/String;)I` | 54 | 31 |
| `API-60f349bbf7537560` | `org.apache.geode.pdx.PdxReader` | `readIntArray` | `(Ljava/lang/String;)[I` | 54 | 31 |
| `API-b3bbf98069409baf` | `org.apache.geode.pdx.PdxReader` | `readLong` | `(Ljava/lang/String;)J` | 54 | 31 |
| `API-7a7b02daf8cc6639` | `org.apache.geode.pdx.PdxReader` | `readLongArray` | `(Ljava/lang/String;)[J` | 54 | 31 |
| `API-d231e98b1aa281c0` | `org.apache.geode.pdx.PdxReader` | `readObject` | `(Ljava/lang/String;)Ljava/lang/Object;` | 54 | 31 |
| `API-3ba0fcf36cd7b42f` | `org.apache.geode.pdx.PdxReader` | `readObjectArray` | `(Ljava/lang/String;)[Ljava/lang/Object;` | 54 | 31 |
| `API-70aad29a4bbd9a99` | `org.apache.geode.pdx.PdxReader` | `readShort` | `(Ljava/lang/String;)S` | 54 | 31 |
| `API-a06347344d2d522b` | `org.apache.geode.pdx.PdxReader` | `readShortArray` | `(Ljava/lang/String;)[S` | 54 | 31 |
| `API-85c1ed374f8fcd5d` | `org.apache.geode.pdx.PdxReader` | `readString` | `(Ljava/lang/String;)Ljava/lang/String;` | 54 | 31 |
| `API-d00c2fb39b250ac8` | `org.apache.geode.pdx.PdxReader` | `readStringArray` | `(Ljava/lang/String;)[Ljava/lang/String;` | 54 | 31 |
| `API-efdf7b2842e91393` | `org.apache.geode.pdx.PdxReader` | `readUnreadFields` | `()Lorg/apache/geode/pdx/PdxUnreadFields;` | 54 | 31 |
| `API-fdc6f9bc564f81f8` | `org.apache.geode.pdx.PdxSerializer` | `<type>` | `(type)` | 54 | 31 |
| `API-7eb23c0867108a6a` | `org.apache.geode.pdx.PdxSerializer` | `fromData` | `(Ljava/lang/Class;Lorg/apache/geode/pdx/PdxReader;)Ljava/lang/Object;` | 54 | 31 |
| `API-2d1936e4b47b8f51` | `org.apache.geode.pdx.PdxSerializer` | `toData` | `(Ljava/lang/Object;Lorg/apache/geode/pdx/PdxWriter;)Z` | 54 | 31 |
| `API-941e42d7e7fbb412` | `org.apache.geode.pdx.PdxUnreadFields` | `<type>` | `(type)` | 54 | 31 |
| `API-e881f7062d5fa9c8` | `org.apache.geode.pdx.PdxWriter` | `<type>` | `(type)` | 54 | 31 |
| `API-3c299fcb5204c284` | `org.apache.geode.pdx.PdxWriter` | `markIdentityField` | `(Ljava/lang/String;)Lorg/apache/geode/pdx/PdxWriter;` | 54 | 31 |
| `API-d055e02d51d074a9` | `org.apache.geode.pdx.PdxWriter` | `writeArrayOfByteArrays` | `(Ljava/lang/String;[[B)Lorg/apache/geode/pdx/PdxWriter;` | 54 | 31 |
| `API-3dc9c2e0930e6a39` | `org.apache.geode.pdx.PdxWriter` | `writeBoolean` | `(Ljava/lang/String;Z)Lorg/apache/geode/pdx/PdxWriter;` | 54 | 31 |
| `API-0dd2fc8a93dfdaee` | `org.apache.geode.pdx.PdxWriter` | `writeBooleanArray` | `(Ljava/lang/String;[Z)Lorg/apache/geode/pdx/PdxWriter;` | 54 | 31 |
| `API-469863ccca824cf5` | `org.apache.geode.pdx.PdxWriter` | `writeByte` | `(Ljava/lang/String;B)Lorg/apache/geode/pdx/PdxWriter;` | 54 | 31 |
| `API-3a3793f1e821462b` | `org.apache.geode.pdx.PdxWriter` | `writeByteArray` | `(Ljava/lang/String;[B)Lorg/apache/geode/pdx/PdxWriter;` | 54 | 31 |
| `API-7459c95e7e42e15a` | `org.apache.geode.pdx.PdxWriter` | `writeChar` | `(Ljava/lang/String;C)Lorg/apache/geode/pdx/PdxWriter;` | 54 | 31 |
| `API-49233ca31b77d409` | `org.apache.geode.pdx.PdxWriter` | `writeCharArray` | `(Ljava/lang/String;[C)Lorg/apache/geode/pdx/PdxWriter;` | 54 | 31 |
| `API-7b33f6519f282010` | `org.apache.geode.pdx.PdxWriter` | `writeDate` | `(Ljava/lang/String;Ljava/util/Date;)Lorg/apache/geode/pdx/PdxWriter;` | 54 | 31 |
| `API-64aa92cbcb3a5010` | `org.apache.geode.pdx.PdxWriter` | `writeDouble` | `(Ljava/lang/String;D)Lorg/apache/geode/pdx/PdxWriter;` | 54 | 31 |
| `API-612cbe20acf8e658` | `org.apache.geode.pdx.PdxWriter` | `writeDoubleArray` | `(Ljava/lang/String;[D)Lorg/apache/geode/pdx/PdxWriter;` | 54 | 31 |
| `API-e9c1034295ce9370` | `org.apache.geode.pdx.PdxWriter` | `writeField` | `(Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Class;)Lorg/apache/geode/pdx/PdxWriter;` | 54 | 31 |
| `API-a5e2c99b636dadd3` | `org.apache.geode.pdx.PdxWriter` | `writeField` | `(Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Class;Z)Lorg/apache/geode/pdx/PdxWriter;` | 54 | 31 |
| `API-8048046a4dafa0e3` | `org.apache.geode.pdx.PdxWriter` | `writeFloat` | `(Ljava/lang/String;F)Lorg/apache/geode/pdx/PdxWriter;` | 54 | 31 |
| `API-3e48a10d3717481c` | `org.apache.geode.pdx.PdxWriter` | `writeFloatArray` | `(Ljava/lang/String;[F)Lorg/apache/geode/pdx/PdxWriter;` | 54 | 31 |
| `API-a42ddb85cbbcd53e` | `org.apache.geode.pdx.PdxWriter` | `writeInt` | `(Ljava/lang/String;I)Lorg/apache/geode/pdx/PdxWriter;` | 54 | 31 |
| `API-faf5c5dec3ac08c0` | `org.apache.geode.pdx.PdxWriter` | `writeIntArray` | `(Ljava/lang/String;[I)Lorg/apache/geode/pdx/PdxWriter;` | 54 | 31 |
| `API-7fa6bce393c85b04` | `org.apache.geode.pdx.PdxWriter` | `writeLong` | `(Ljava/lang/String;J)Lorg/apache/geode/pdx/PdxWriter;` | 54 | 31 |
| `API-c40b3fbf74c1a2f5` | `org.apache.geode.pdx.PdxWriter` | `writeLongArray` | `(Ljava/lang/String;[J)Lorg/apache/geode/pdx/PdxWriter;` | 54 | 31 |
| `API-67a5602723dcb61f` | `org.apache.geode.pdx.PdxWriter` | `writeObject` | `(Ljava/lang/String;Ljava/lang/Object;)Lorg/apache/geode/pdx/PdxWriter;` | 54 | 31 |
| `API-e08df6153fed7551` | `org.apache.geode.pdx.PdxWriter` | `writeObject` | `(Ljava/lang/String;Ljava/lang/Object;Z)Lorg/apache/geode/pdx/PdxWriter;` | 54 | 31 |
| `API-43e42eedfa8f6b76` | `org.apache.geode.pdx.PdxWriter` | `writeObjectArray` | `(Ljava/lang/String;[Ljava/lang/Object;)Lorg/apache/geode/pdx/PdxWriter;` | 54 | 31 |
| `API-8bc5144a84a28b7a` | `org.apache.geode.pdx.PdxWriter` | `writeObjectArray` | `(Ljava/lang/String;[Ljava/lang/Object;Z)Lorg/apache/geode/pdx/PdxWriter;` | 54 | 31 |
| `API-11d5bd41fa7d3dbf` | `org.apache.geode.pdx.PdxWriter` | `writeShort` | `(Ljava/lang/String;S)Lorg/apache/geode/pdx/PdxWriter;` | 54 | 31 |
| `API-442390f07e75c42b` | `org.apache.geode.pdx.PdxWriter` | `writeShortArray` | `(Ljava/lang/String;[S)Lorg/apache/geode/pdx/PdxWriter;` | 54 | 31 |
| `API-a2bcf91cb207ecdc` | `org.apache.geode.pdx.PdxWriter` | `writeString` | `(Ljava/lang/String;Ljava/lang/String;)Lorg/apache/geode/pdx/PdxWriter;` | 54 | 31 |
| `API-0b651e03ae48e20c` | `org.apache.geode.pdx.PdxWriter` | `writeStringArray` | `(Ljava/lang/String;[Ljava/lang/String;)Lorg/apache/geode/pdx/PdxWriter;` | 54 | 31 |
| `API-bc148fd99f82ce8d` | `org.apache.geode.pdx.PdxWriter` | `writeUnreadFields` | `(Lorg/apache/geode/pdx/PdxUnreadFields;)Lorg/apache/geode/pdx/PdxWriter;` | 54 | 31 |

### client-pdx-settings — 18 IDs

| API ID | Native receiver | Member | JVM descriptor | Owner | Consumer |
| --- | --- | --- | --- | --- | --- |
| `API-f16be7a6d64b75e7` | `org.apache.geode.cache.Cache` | `getPdxDiskStore` | `()Ljava/lang/String;` | 54 | 35 |
| `API-5da1deaad4356767` | `org.apache.geode.cache.Cache` | `getPdxIgnoreUnreadFields` | `()Z` | 54 | 35 |
| `API-38ced6723c2792a9` | `org.apache.geode.cache.Cache` | `getPdxPersistent` | `()Z` | 54 | 35 |
| `API-251ab3b287b2fcab` | `org.apache.geode.cache.Cache` | `getPdxReadSerialized` | `()Z` | 54 | 35 |
| `API-a8b4ac3e961c8a2d` | `org.apache.geode.cache.Cache` | `getPdxSerializer` | `()Lorg/apache/geode/pdx/PdxSerializer;` | 54 | 35 |
| `API-2367f2fa266e941e` | `org.apache.geode.cache.Cache` | `registerPdxMetaData` | `(Ljava/lang/Object;)V` | 54 | 35 |
| `API-baf21b05fe5cdc21` | `org.apache.geode.cache.GemFireCache` | `getPdxDiskStore` | `()Ljava/lang/String;` | 54 | 35 |
| `API-789a2bfb40510389` | `org.apache.geode.cache.GemFireCache` | `getPdxIgnoreUnreadFields` | `()Z` | 54 | 35 |
| `API-a191f4f27ec61237` | `org.apache.geode.cache.GemFireCache` | `getPdxPersistent` | `()Z` | 54 | 35 |
| `API-0d4b57cba5c948a6` | `org.apache.geode.cache.GemFireCache` | `getPdxReadSerialized` | `()Z` | 54 | 35 |
| `API-724b8b635a3641c3` | `org.apache.geode.cache.GemFireCache` | `getPdxSerializer` | `()Lorg/apache/geode/pdx/PdxSerializer;` | 54 | 35 |
| `API-30bf6fba9cc2b256` | `org.apache.geode.cache.GemFireCache` | `registerPdxMetaData` | `(Ljava/lang/Object;)V` | 54 | 35 |
| `API-69438ff6a30076a3` | `org.apache.geode.cache.client.ClientCache` | `getPdxDiskStore` | `()Ljava/lang/String;` | 54 | 35 |
| `API-5c3201726f177802` | `org.apache.geode.cache.client.ClientCache` | `getPdxIgnoreUnreadFields` | `()Z` | 54 | 35 |
| `API-da787240071b1c0e` | `org.apache.geode.cache.client.ClientCache` | `getPdxPersistent` | `()Z` | 54 | 35 |
| `API-9060e5f2d13ab0ac` | `org.apache.geode.cache.client.ClientCache` | `getPdxReadSerialized` | `()Z` | 54 | 35 |
| `API-5ed4b2d3e960b50f` | `org.apache.geode.cache.client.ClientCache` | `getPdxSerializer` | `()Lorg/apache/geode/pdx/PdxSerializer;` | 54 | 35 |
| `API-fa50fbc82cf92da5` | `org.apache.geode.cache.client.ClientCache` | `registerPdxMetaData` | `(Ljava/lang/Object;)V` | 54 | 35 |

### peer-pdx-settings — 5 IDs

| API ID | Native receiver | Member | JVM descriptor | Owner | Consumer |
| --- | --- | --- | --- | --- | --- |
| `API-56f5a93c51ed9e63` | `org.apache.geode.cache.CacheFactory` | `setPdxDiskStore` | `(Ljava/lang/String;)Lorg/apache/geode/cache/CacheFactory;` | 54 | 35 |
| `API-355a6a5b5cada671` | `org.apache.geode.cache.CacheFactory` | `setPdxIgnoreUnreadFields` | `(Z)Lorg/apache/geode/cache/CacheFactory;` | 54 | 35 |
| `API-8b5e143d8aebaa7b` | `org.apache.geode.cache.CacheFactory` | `setPdxPersistent` | `(Z)Lorg/apache/geode/cache/CacheFactory;` | 54 | 35 |
| `API-78d0822120882c5e` | `org.apache.geode.cache.CacheFactory` | `setPdxReadSerialized` | `(Z)Lorg/apache/geode/cache/CacheFactory;` | 54 | 35 |
| `API-c9857a9bc9fb7e57` | `org.apache.geode.cache.CacheFactory` | `setPdxSerializer` | `(Lorg/apache/geode/pdx/PdxSerializer;)Lorg/apache/geode/cache/CacheFactory;` | 54 | 35 |

### binary-callback-registration — 19 IDs

| API ID | Native receiver | Member | JVM descriptor | Owner | Consumer |
| --- | --- | --- | --- | --- | --- |
| `API-745900e13e427e47` | `org.apache.geode.DataSerializable` | `<type>` | `(type)` | 55 | 31 |
| `API-68fd9429b6449996` | `org.apache.geode.DataSerializable` | `fromData` | `(Ljava/io/DataInput;)V` | 55 | 31 |
| `API-3c79ec293b53b7c7` | `org.apache.geode.DataSerializable` | `toData` | `(Ljava/io/DataOutput;)V` | 55 | 31 |
| `API-9f5e8421bfbd24d4` | `org.apache.geode.DataSerializer` | `<init>` | `()V` | 55 | 31 |
| `API-eae159757b38d0d8` | `org.apache.geode.DataSerializer` | `<type>` | `(type)` | 55 | 31 |
| `API-3b442b45d2423217` | `org.apache.geode.DataSerializer` | `equals` | `(Ljava/lang/Object;)Z` | 55 | 31 |
| `API-56d65787869e27c0` | `org.apache.geode.DataSerializer` | `fromData` | `(Ljava/io/DataInput;)Ljava/lang/Object;` | 55 | 31 |
| `API-a0729727d81130e9` | `org.apache.geode.DataSerializer` | `getId` | `()I` | 55 | 31 |
| `API-ab5d75545ca2e164` | `org.apache.geode.DataSerializer` | `getSupportedClasses` | `()[Ljava/lang/Class;` | 55 | 31 |
| `API-8ee29d334b65cccf` | `org.apache.geode.DataSerializer` | `hashCode` | `()I` | 55 | 31 |
| `API-c2f659d543c22b27` | `org.apache.geode.DataSerializer` | `register` | `(Ljava/lang/Class;)Lorg/apache/geode/DataSerializer;` | 55 | 31 |
| `API-4131271d0c0f46da` | `org.apache.geode.DataSerializer` | `toData` | `(Ljava/lang/Object;Ljava/io/DataOutput;)Z` | 55 | 31 |
| `API-7814b841172b9e23` | `org.apache.geode.Instantiator` | `<init>` | `(Ljava/lang/Class;I)V` | 55 | 31 |
| `API-38a3217cc0e3ce48` | `org.apache.geode.Instantiator` | `<type>` | `(type)` | 55 | 31 |
| `API-f9896a939e37d8e2` | `org.apache.geode.Instantiator` | `getId` | `()I` | 55 | 31 |
| `API-267c4dfdd4c88e78` | `org.apache.geode.Instantiator` | `getInstantiatedClass` | `()Ljava/lang/Class;` | 55 | 31 |
| `API-4a948bca09fdee48` | `org.apache.geode.Instantiator` | `newInstance` | `()Lorg/apache/geode/DataSerializable;` | 55 | 31 |
| `API-9d7b3ec4e8e8b7e2` | `org.apache.geode.Instantiator` | `register` | `(Lorg/apache/geode/Instantiator;)V` | 55 | 31 |
| `API-b6d0bf0eef88307a` | `org.apache.geode.Instantiator` | `register` | `(Lorg/apache/geode/Instantiator;Z)V` | 55 | 31 |

### binary-registration-metadata — 8 IDs

| API ID | Native receiver | Member | JVM descriptor | Owner | Consumer |
| --- | --- | --- | --- | --- | --- |
| `API-1e53a27164b14c78` | `org.apache.geode.DataSerializer` | `getContext` | `()Ljava/lang/Object;` | 55 | 31 |
| `API-f9dc89949bc8a07d` | `org.apache.geode.DataSerializer` | `getEventId` | `()Ljava/lang/Object;` | 55 | 31 |
| `API-d69c6b425822d8fa` | `org.apache.geode.DataSerializer` | `setContext` | `(Ljava/lang/Object;)V` | 55 | 31 |
| `API-0e893f36426f952e` | `org.apache.geode.DataSerializer` | `setEventId` | `(Ljava/lang/Object;)V` | 55 | 31 |
| `API-17837947bbc5593f` | `org.apache.geode.Instantiator` | `getContext` | `()Ljava/lang/Object;` | 55 | 31 |
| `API-d85a040257772c2b` | `org.apache.geode.Instantiator` | `getEventId` | `()Ljava/lang/Object;` | 55 | 31 |
| `API-b34221effecbd195` | `org.apache.geode.Instantiator` | `setContext` | `(Ljava/lang/Object;)V` | 55 | 31 |
| `API-d120910b7d93f003` | `org.apache.geode.Instantiator` | `setEventId` | `(Ljava/lang/Object;)V` | 55 | 31 |

### binary-codec-state — 6 IDs

| API ID | Native receiver | Member | JVM descriptor | Owner | Consumer |
| --- | --- | --- | --- | --- | --- |
| `API-2c0f28ae5dad0498` | `org.apache.geode.DataSerializer` | `DISALLOW_JAVA_SERIALIZATION` | `Ljava/lang/ThreadLocal;` | 55 | 31 |
| `API-0b557d544baed197` | `org.apache.geode.DataSerializer` | `DOUBLE` | `B` | 55 | 31 |
| `API-90d23d026e6f232a` | `org.apache.geode.DataSerializer` | `INT` | `B` | 55 | 31 |
| `API-ac079d25ae90b22d` | `org.apache.geode.DataSerializer` | `LONG` | `B` | 55 | 31 |
| `API-fb4e660fff982228` | `org.apache.geode.DataSerializer` | `PRESENT` | `B` | 55 | 31 |
| `API-89afc037b62c6f00` | `org.apache.geode.DataSerializer` | `TRACE_SERIALIZABLE` | `Z` | 55 | 31 |

### binary-region-codecs — 2 IDs

| API ID | Native receiver | Member | JVM descriptor | Owner | Consumer |
| --- | --- | --- | --- | --- | --- |
| `API-1c36545fa51fb4cb` | `org.apache.geode.DataSerializer` | `readRegion` | `(Ljava/io/DataInput;)Lorg/apache/geode/cache/Region;` | 55 | 31 |
| `API-373783f510a24276` | `org.apache.geode.DataSerializer` | `writeRegion` | `(Lorg/apache/geode/cache/Region;Ljava/io/DataOutput;)V` | 55 | 31 |

### binary-value-codecs — 115 IDs

| API ID | Native receiver | Member | JVM descriptor | Owner | Consumer |
| --- | --- | --- | --- | --- | --- |
| `API-97864e53cfa68339` | `org.apache.geode.DataSerializer` | `readArrayList` | `(Ljava/io/DataInput;)Ljava/util/ArrayList;` | 55 | 31 |
| `API-05402f906cc94fb7` | `org.apache.geode.DataSerializer` | `readArrayOfByteArrays` | `(Ljava/io/DataInput;)[[B` | 55 | 31 |
| `API-42071603bab56266` | `org.apache.geode.DataSerializer` | `readBoolean` | `(Ljava/io/DataInput;)Ljava/lang/Boolean;` | 55 | 31 |
| `API-32f40c4a98c0d69b` | `org.apache.geode.DataSerializer` | `readBooleanArray` | `(Ljava/io/DataInput;)[Z` | 55 | 31 |
| `API-9787613893724c75` | `org.apache.geode.DataSerializer` | `readByte` | `(Ljava/io/DataInput;)Ljava/lang/Byte;` | 55 | 31 |
| `API-741c7c6a37c54f86` | `org.apache.geode.DataSerializer` | `readByteArray` | `(Ljava/io/DataInput;)[B` | 55 | 31 |
| `API-5b1f260ecba0db7c` | `org.apache.geode.DataSerializer` | `readCharArray` | `(Ljava/io/DataInput;)[C` | 55 | 31 |
| `API-a297e8dfd5d3661a` | `org.apache.geode.DataSerializer` | `readCharacter` | `(Ljava/io/DataInput;)Ljava/lang/Character;` | 55 | 31 |
| `API-2ed02e472d781a4d` | `org.apache.geode.DataSerializer` | `readClass` | `(Ljava/io/DataInput;)Ljava/lang/Class;` | 55 | 31 |
| `API-0ef71359ab648181` | `org.apache.geode.DataSerializer` | `readConcurrentHashMap` | `(Ljava/io/DataInput;)Ljava/util/concurrent/ConcurrentHashMap;` | 55 | 31 |
| `API-e47d5f8cbdf659c5` | `org.apache.geode.DataSerializer` | `readDate` | `(Ljava/io/DataInput;)Ljava/util/Date;` | 55 | 31 |
| `API-c74a4fbaf58371f4` | `org.apache.geode.DataSerializer` | `readDouble` | `(Ljava/io/DataInput;)Ljava/lang/Double;` | 55 | 31 |
| `API-51a1f0f4ee64c8f7` | `org.apache.geode.DataSerializer` | `readDoubleArray` | `(Ljava/io/DataInput;)[D` | 55 | 31 |
| `API-c9aa9909934c6590` | `org.apache.geode.DataSerializer` | `readEnum` | `(Ljava/lang/Class;Ljava/io/DataInput;)Ljava/lang/Enum;` | 55 | 31 |
| `API-6efd48c535b1db98` | `org.apache.geode.DataSerializer` | `readFile` | `(Ljava/io/DataInput;)Ljava/io/File;` | 55 | 31 |
| `API-a0651514b8369a3a` | `org.apache.geode.DataSerializer` | `readFloat` | `(Ljava/io/DataInput;)Ljava/lang/Float;` | 55 | 31 |
| `API-741240250d02e507` | `org.apache.geode.DataSerializer` | `readFloatArray` | `(Ljava/io/DataInput;)[F` | 55 | 31 |
| `API-06705a5bd9361893` | `org.apache.geode.DataSerializer` | `readHashMap` | `(Ljava/io/DataInput;)Ljava/util/HashMap;` | 55 | 31 |
| `API-a2f7e5815dd6b51b` | `org.apache.geode.DataSerializer` | `readHashSet` | `(Ljava/io/DataInput;)Ljava/util/HashSet;` | 55 | 31 |
| `API-e6e641eaec65ce1c` | `org.apache.geode.DataSerializer` | `readHashtable` | `(Ljava/io/DataInput;)Ljava/util/Hashtable;` | 55 | 31 |
| `API-0e562f9b05e8a0eb` | `org.apache.geode.DataSerializer` | `readIdentityHashMap` | `(Ljava/io/DataInput;)Ljava/util/IdentityHashMap;` | 55 | 31 |
| `API-ad1949d07edda440` | `org.apache.geode.DataSerializer` | `readInetAddress` | `(Ljava/io/DataInput;)Ljava/net/InetAddress;` | 55 | 31 |
| `API-1d0bae1dd1de59e1` | `org.apache.geode.DataSerializer` | `readIntArray` | `(Ljava/io/DataInput;)[I` | 55 | 31 |
| `API-a5a62dcabdfa80c4` | `org.apache.geode.DataSerializer` | `readInteger` | `(Ljava/io/DataInput;)Ljava/lang/Integer;` | 55 | 31 |
| `API-ea0a951b2ece54ab` | `org.apache.geode.DataSerializer` | `readLinkedHashMap` | `(Ljava/io/DataInput;)Ljava/util/LinkedHashMap;` | 55 | 31 |
| `API-474c04e546f963cc` | `org.apache.geode.DataSerializer` | `readLinkedHashSet` | `(Ljava/io/DataInput;)Ljava/util/LinkedHashSet;` | 55 | 31 |
| `API-82e96a9489e1377a` | `org.apache.geode.DataSerializer` | `readLinkedList` | `(Ljava/io/DataInput;)Ljava/util/LinkedList;` | 55 | 31 |
| `API-c62b294bf6b21073` | `org.apache.geode.DataSerializer` | `readLong` | `(Ljava/io/DataInput;)Ljava/lang/Long;` | 55 | 31 |
| `API-3dd4636c807fa738` | `org.apache.geode.DataSerializer` | `readLongArray` | `(Ljava/io/DataInput;)[J` | 55 | 31 |
| `API-78a5972c10f55e3b` | `org.apache.geode.DataSerializer` | `readNonPrimitiveClassName` | `(Ljava/io/DataInput;)Ljava/lang/String;` | 55 | 31 |
| `API-bc839c05d68708ed` | `org.apache.geode.DataSerializer` | `readObject` | `(Ljava/io/DataInput;)Ljava/lang/Object;` | 55 | 31 |
| `API-bf68942f3a591a29` | `org.apache.geode.DataSerializer` | `readObject` | `(Ljava/io/DataInput;Ljava/io/ObjectInputFilter;)Ljava/lang/Object;` | 55 | 31 |
| `API-79744fb2349a8e26` | `org.apache.geode.DataSerializer` | `readObjectArray` | `(Ljava/io/DataInput;)[Ljava/lang/Object;` | 55 | 31 |
| `API-218d8e40ebe9a9c8` | `org.apache.geode.DataSerializer` | `readOptional` | `(Ljava/io/DataInput;)Ljava/util/Optional;` | 55 | 31 |
| `API-133049b01e5206b7` | `org.apache.geode.DataSerializer` | `readOptionalDouble` | `(Ljava/io/DataInput;)Ljava/util/OptionalDouble;` | 55 | 31 |
| `API-a891e32c87f9dea8` | `org.apache.geode.DataSerializer` | `readOptionalInt` | `(Ljava/io/DataInput;)Ljava/util/OptionalInt;` | 55 | 31 |
| `API-55ac9b8fffa5beaf` | `org.apache.geode.DataSerializer` | `readOptionalLong` | `(Ljava/io/DataInput;)Ljava/util/OptionalLong;` | 55 | 31 |
| `API-8b0e95a0bb08477a` | `org.apache.geode.DataSerializer` | `readOptionalPrimitive` | `(Ljava/io/DataInput;)Ljava/lang/Object;` | 55 | 31 |
| `API-d915cff6813f1a1f` | `org.apache.geode.DataSerializer` | `readPrimitiveBoolean` | `(Ljava/io/DataInput;)Z` | 55 | 31 |
| `API-4d42acd7ba1b4446` | `org.apache.geode.DataSerializer` | `readPrimitiveByte` | `(Ljava/io/DataInput;)B` | 55 | 31 |
| `API-9048632372a9cd2a` | `org.apache.geode.DataSerializer` | `readPrimitiveChar` | `(Ljava/io/DataInput;)C` | 55 | 31 |
| `API-0141e3d7278ea4d4` | `org.apache.geode.DataSerializer` | `readPrimitiveDouble` | `(Ljava/io/DataInput;)D` | 55 | 31 |
| `API-43ad25391fc321d7` | `org.apache.geode.DataSerializer` | `readPrimitiveFloat` | `(Ljava/io/DataInput;)F` | 55 | 31 |
| `API-b858f2f71c13bf54` | `org.apache.geode.DataSerializer` | `readPrimitiveInt` | `(Ljava/io/DataInput;)I` | 55 | 31 |
| `API-a4c2acd4f2f55f1f` | `org.apache.geode.DataSerializer` | `readPrimitiveLong` | `(Ljava/io/DataInput;)J` | 55 | 31 |
| `API-6089804658e5a669` | `org.apache.geode.DataSerializer` | `readPrimitiveShort` | `(Ljava/io/DataInput;)S` | 55 | 31 |
| `API-2a63b31db1c901b3` | `org.apache.geode.DataSerializer` | `readProperties` | `(Ljava/io/DataInput;)Ljava/util/Properties;` | 55 | 31 |
| `API-3baf0655e611e16b` | `org.apache.geode.DataSerializer` | `readShort` | `(Ljava/io/DataInput;)Ljava/lang/Short;` | 55 | 31 |
| `API-c87c3ead3992bb36` | `org.apache.geode.DataSerializer` | `readShortArray` | `(Ljava/io/DataInput;)[S` | 55 | 31 |
| `API-944b8d72336046f8` | `org.apache.geode.DataSerializer` | `readStack` | `(Ljava/io/DataInput;)Ljava/util/Stack;` | 55 | 31 |
| `API-f00e1f54392b561d` | `org.apache.geode.DataSerializer` | `readString` | `(Ljava/io/DataInput;)Ljava/lang/String;` | 55 | 31 |
| `API-99898350d6fd79ec` | `org.apache.geode.DataSerializer` | `readStringArray` | `(Ljava/io/DataInput;)[Ljava/lang/String;` | 55 | 31 |
| `API-e78584d5add336e9` | `org.apache.geode.DataSerializer` | `readTreeMap` | `(Ljava/io/DataInput;)Ljava/util/TreeMap;` | 55 | 31 |
| `API-d575341c31c56750` | `org.apache.geode.DataSerializer` | `readTreeSet` | `(Ljava/io/DataInput;)Ljava/util/TreeSet;` | 55 | 31 |
| `API-a21c2c8a1a0282b2` | `org.apache.geode.DataSerializer` | `readUnsignedByte` | `(Ljava/io/DataInput;)I` | 55 | 31 |
| `API-53766708d24d4b51` | `org.apache.geode.DataSerializer` | `readUnsignedShort` | `(Ljava/io/DataInput;)I` | 55 | 31 |
| `API-69e334203b1d16f0` | `org.apache.geode.DataSerializer` | `readVector` | `(Ljava/io/DataInput;)Ljava/util/Vector;` | 55 | 31 |
| `API-e0166e88e04d5bcb` | `org.apache.geode.DataSerializer` | `writeArrayList` | `(Ljava/util/ArrayList;Ljava/io/DataOutput;)V` | 55 | 31 |
| `API-dec958261a015056` | `org.apache.geode.DataSerializer` | `writeArrayOfByteArrays` | `([[BLjava/io/DataOutput;)V` | 55 | 31 |
| `API-e2833b56046b456d` | `org.apache.geode.DataSerializer` | `writeBoolean` | `(Ljava/lang/Boolean;Ljava/io/DataOutput;)V` | 55 | 31 |
| `API-56ad9db167b3f55a` | `org.apache.geode.DataSerializer` | `writeBooleanArray` | `([ZLjava/io/DataOutput;)V` | 55 | 31 |
| `API-725c941bd572f476` | `org.apache.geode.DataSerializer` | `writeByte` | `(Ljava/lang/Byte;Ljava/io/DataOutput;)V` | 55 | 31 |
| `API-54617da167b02879` | `org.apache.geode.DataSerializer` | `writeByteArray` | `([BILjava/io/DataOutput;)V` | 55 | 31 |
| `API-22877e6d08313657` | `org.apache.geode.DataSerializer` | `writeByteArray` | `([BLjava/io/DataOutput;)V` | 55 | 31 |
| `API-d5c55ed215f8527e` | `org.apache.geode.DataSerializer` | `writeCharArray` | `([CLjava/io/DataOutput;)V` | 55 | 31 |
| `API-7d360428a83865ba` | `org.apache.geode.DataSerializer` | `writeCharacter` | `(Ljava/lang/Character;Ljava/io/DataOutput;)V` | 55 | 31 |
| `API-2e8f37a955a73d57` | `org.apache.geode.DataSerializer` | `writeClass` | `(Ljava/lang/Class;Ljava/io/DataOutput;)V` | 55 | 31 |
| `API-d0b3407201838fea` | `org.apache.geode.DataSerializer` | `writeConcurrentHashMap` | `(Ljava/util/concurrent/ConcurrentHashMap;Ljava/io/DataOutput;)V` | 55 | 31 |
| `API-c4de1e52a972e927` | `org.apache.geode.DataSerializer` | `writeDate` | `(Ljava/util/Date;Ljava/io/DataOutput;)V` | 55 | 31 |
| `API-ae94022373cb190c` | `org.apache.geode.DataSerializer` | `writeDouble` | `(Ljava/lang/Double;Ljava/io/DataOutput;)V` | 55 | 31 |
| `API-fb177de7c95169da` | `org.apache.geode.DataSerializer` | `writeDoubleArray` | `([DLjava/io/DataOutput;)V` | 55 | 31 |
| `API-0ee14eef6d811982` | `org.apache.geode.DataSerializer` | `writeEnum` | `(Ljava/lang/Enum;Ljava/io/DataOutput;)V` | 55 | 31 |
| `API-8029798fa5f9d44a` | `org.apache.geode.DataSerializer` | `writeFile` | `(Ljava/io/File;Ljava/io/DataOutput;)V` | 55 | 31 |
| `API-afd30c0f799b3263` | `org.apache.geode.DataSerializer` | `writeFloat` | `(Ljava/lang/Float;Ljava/io/DataOutput;)V` | 55 | 31 |
| `API-b13ebe5d0a3643c7` | `org.apache.geode.DataSerializer` | `writeFloatArray` | `([FLjava/io/DataOutput;)V` | 55 | 31 |
| `API-182ba24f125fdf4c` | `org.apache.geode.DataSerializer` | `writeHashMap` | `(Ljava/util/Map;Ljava/io/DataOutput;)V` | 55 | 31 |
| `API-90ab463b9e0061a2` | `org.apache.geode.DataSerializer` | `writeHashSet` | `(Ljava/util/HashSet;Ljava/io/DataOutput;)V` | 55 | 31 |
| `API-e2d3166b563fd1be` | `org.apache.geode.DataSerializer` | `writeHashtable` | `(Ljava/util/Hashtable;Ljava/io/DataOutput;)V` | 55 | 31 |
| `API-e91410526f3e6228` | `org.apache.geode.DataSerializer` | `writeIdentityHashMap` | `(Ljava/util/IdentityHashMap;Ljava/io/DataOutput;)V` | 55 | 31 |
| `API-718746eb4d943f4d` | `org.apache.geode.DataSerializer` | `writeInetAddress` | `(Ljava/net/InetAddress;Ljava/io/DataOutput;)V` | 55 | 31 |
| `API-d3c6fc9ee2f180be` | `org.apache.geode.DataSerializer` | `writeIntArray` | `([ILjava/io/DataOutput;)V` | 55 | 31 |
| `API-0766be324b5b78e5` | `org.apache.geode.DataSerializer` | `writeInteger` | `(Ljava/lang/Integer;Ljava/io/DataOutput;)V` | 55 | 31 |
| `API-a69d7194846c0847` | `org.apache.geode.DataSerializer` | `writeLinkedHashMap` | `(Ljava/util/Map;Ljava/io/DataOutput;)V` | 55 | 31 |
| `API-c14af85e8ca7c0f7` | `org.apache.geode.DataSerializer` | `writeLinkedHashSet` | `(Ljava/util/LinkedHashSet;Ljava/io/DataOutput;)V` | 55 | 31 |
| `API-4a69e85906fbe38e` | `org.apache.geode.DataSerializer` | `writeLinkedList` | `(Ljava/util/LinkedList;Ljava/io/DataOutput;)V` | 55 | 31 |
| `API-c9120fe73408370e` | `org.apache.geode.DataSerializer` | `writeLong` | `(Ljava/lang/Long;Ljava/io/DataOutput;)V` | 55 | 31 |
| `API-1c3d6a650729de59` | `org.apache.geode.DataSerializer` | `writeLongArray` | `([JLjava/io/DataOutput;)V` | 55 | 31 |
| `API-bf93b5bf20c7e830` | `org.apache.geode.DataSerializer` | `writeNonPrimitiveClassName` | `(Ljava/lang/String;Ljava/io/DataOutput;)V` | 55 | 31 |
| `API-2bd8814fbdc8103d` | `org.apache.geode.DataSerializer` | `writeObject` | `(Ljava/lang/Object;Ljava/io/DataOutput;)V` | 55 | 31 |
| `API-a3632b4477d21d7f` | `org.apache.geode.DataSerializer` | `writeObject` | `(Ljava/lang/Object;Ljava/io/DataOutput;Z)V` | 55 | 31 |
| `API-de7bf601a71aeea9` | `org.apache.geode.DataSerializer` | `writeObjectArray` | `([Ljava/lang/Object;Ljava/io/DataOutput;)V` | 55 | 31 |
| `API-5ffe2c79ff2aa373` | `org.apache.geode.DataSerializer` | `writeObjectAsByteArray` | `(Ljava/lang/Object;Ljava/io/DataOutput;)V` | 55 | 31 |
| `API-4eb34684a5638eb9` | `org.apache.geode.DataSerializer` | `writeOptional` | `(Ljava/util/Optional;Ljava/io/DataOutput;)V` | 55 | 31 |
| `API-f9cf081fe2214598` | `org.apache.geode.DataSerializer` | `writeOptionalDouble` | `(Ljava/util/OptionalDouble;Ljava/io/DataOutput;)V` | 55 | 31 |
| `API-46aee7bd0691a000` | `org.apache.geode.DataSerializer` | `writeOptionalInt` | `(Ljava/util/OptionalInt;Ljava/io/DataOutput;)V` | 55 | 31 |
| `API-98762fcbdc33c8e0` | `org.apache.geode.DataSerializer` | `writeOptionalLong` | `(Ljava/util/OptionalLong;Ljava/io/DataOutput;)V` | 55 | 31 |
| `API-d50115690a02a184` | `org.apache.geode.DataSerializer` | `writePrimitiveBoolean` | `(ZLjava/io/DataOutput;)V` | 55 | 31 |
| `API-613f5297f4cb4f3a` | `org.apache.geode.DataSerializer` | `writePrimitiveByte` | `(BLjava/io/DataOutput;)V` | 55 | 31 |
| `API-301203c33c0fd46e` | `org.apache.geode.DataSerializer` | `writePrimitiveChar` | `(CLjava/io/DataOutput;)V` | 55 | 31 |
| `API-5bca88d4fede0b2a` | `org.apache.geode.DataSerializer` | `writePrimitiveDouble` | `(DLjava/io/DataOutput;)V` | 55 | 31 |
| `API-f2ec7a1418b04e28` | `org.apache.geode.DataSerializer` | `writePrimitiveFloat` | `(FLjava/io/DataOutput;)V` | 55 | 31 |
| `API-13ae696c58875054` | `org.apache.geode.DataSerializer` | `writePrimitiveInt` | `(ILjava/io/DataOutput;)V` | 55 | 31 |
| `API-27ba730c0b8b046e` | `org.apache.geode.DataSerializer` | `writePrimitiveLong` | `(JLjava/io/DataOutput;)V` | 55 | 31 |
| `API-c7431638a3f90801` | `org.apache.geode.DataSerializer` | `writePrimitiveShort` | `(SLjava/io/DataOutput;)V` | 55 | 31 |
| `API-ed67ac3ad84f0a84` | `org.apache.geode.DataSerializer` | `writeProperties` | `(Ljava/util/Properties;Ljava/io/DataOutput;)V` | 55 | 31 |
| `API-0bcb082d5858e182` | `org.apache.geode.DataSerializer` | `writeShort` | `(Ljava/lang/Short;Ljava/io/DataOutput;)V` | 55 | 31 |
| `API-3fe3cf616804c040` | `org.apache.geode.DataSerializer` | `writeShortArray` | `([SLjava/io/DataOutput;)V` | 55 | 31 |
| `API-643eb60801eec113` | `org.apache.geode.DataSerializer` | `writeStack` | `(Ljava/util/Stack;Ljava/io/DataOutput;)V` | 55 | 31 |
| `API-1636edb6178d1d67` | `org.apache.geode.DataSerializer` | `writeString` | `(Ljava/lang/String;Ljava/io/DataOutput;)V` | 55 | 31 |
| `API-0ea992aae36e0e0d` | `org.apache.geode.DataSerializer` | `writeStringArray` | `([Ljava/lang/String;Ljava/io/DataOutput;)V` | 55 | 31 |
| `API-78b693942b0ae9b1` | `org.apache.geode.DataSerializer` | `writeTreeMap` | `(Ljava/util/TreeMap;Ljava/io/DataOutput;)V` | 55 | 31 |
| `API-8e75093fead52afe` | `org.apache.geode.DataSerializer` | `writeTreeSet` | `(Ljava/util/TreeSet;Ljava/io/DataOutput;)V` | 55 | 31 |
| `API-102c9cebc4ad6766` | `org.apache.geode.DataSerializer` | `writeUnsignedByte` | `(ILjava/io/DataOutput;)V` | 55 | 31 |
| `API-b8b50f66008c07cd` | `org.apache.geode.DataSerializer` | `writeUnsignedShort` | `(ILjava/io/DataOutput;)V` | 55 | 31 |
| `API-76758f6ba3fa5047` | `org.apache.geode.DataSerializer` | `writeVector` | `(Ljava/util/Vector;Ljava/io/DataOutput;)V` | 55 | 31 |

### binary-registry-lookup — 3 IDs

| API ID | Native receiver | Member | JVM descriptor | Owner | Consumer |
| --- | --- | --- | --- | --- | --- |
| `API-764f2c7613bc86f7` | `org.apache.geode.internal.InternalDataSerializer` | `<type>` | `(type)` | 55 | 31 |
| `API-959435c46189a93d` | `org.apache.geode.internal.InternalDataSerializer` | `getSerializer` | `(I)Lorg/apache/geode/DataSerializer;` | 55 | 31 |
| `API-99f035ca41ce413d` | `org.apache.geode.internal.InternalDataSerializer` | `unregister` | `(I)V` | 55 | 31 |

### json-documents — 18 IDs

| API ID | Native receiver | Member | JVM descriptor | Owner | Consumer |
| --- | --- | --- | --- | --- | --- |
| `API-d3bc6850faba3c16` | `org.apache.geode.json.JsonDocument` | `<type>` | `(type)` | 56 | 31 |
| `API-379bbdaca6ecfaab` | `org.apache.geode.json.JsonDocument` | `equals` | `(Ljava/lang/Object;)Z` | 56 | 31 |
| `API-363ae0b83055ff3b` | `org.apache.geode.json.JsonDocument` | `foreach` | `(Ljava/util/function/BiConsumer;)V` | 56 | 31 |
| `API-42b9b130f9ff9aa8` | `org.apache.geode.json.JsonDocument` | `getAndCheckField` | `(Ljava/lang/String;)Ljava/lang/Object;` | 56 | 31 |
| `API-5e69ad10110a07d4` | `org.apache.geode.json.JsonDocument` | `getField` | `(Ljava/lang/String;)Ljava/lang/Object;` | 56 | 31 |
| `API-0958705a52361327` | `org.apache.geode.json.JsonDocument` | `getFieldNames` | `()Ljava/util/List;` | 56 | 31 |
| `API-eb8e8c40dec07e46` | `org.apache.geode.json.JsonDocument` | `hasField` | `(Ljava/lang/String;)Z` | 56 | 31 |
| `API-de30f5388d25aa9b` | `org.apache.geode.json.JsonDocument` | `hashCode` | `()I` | 56 | 31 |
| `API-9d8a51c90748c40e` | `org.apache.geode.json.JsonDocument` | `toJson` | `()Ljava/lang/String;` | 56 | 31 |
| `API-0d146a1b0555a45c` | `org.apache.geode.json.JsonDocumentFactory` | `<type>` | `(type)` | 56 | 31 |
| `API-5911c5c83a636de6` | `org.apache.geode.json.JsonDocumentFactory` | `create` | `(Ljava/lang/String;)Lorg/apache/geode/json/JsonDocument;` | 56 | 31 |
| `API-c1796f2f33d90a10` | `org.apache.geode.json.SerializableAsJson` | `<type>` | `(type)` | 56 | 31 |
| `API-fa7a975795cd8853` | `org.apache.geode.json.SerializableAsJson` | `toJson` | `()Ljava/lang/String;` | 56 | 31 |
| `API-7640287febc9e4b3` | `org.apache.geode.json.StorageFormat` | `<type>` | `(type)` | 56 | 31 |
| `API-f505a3d045f243c2` | `org.apache.geode.json.StorageFormat` | `BSON` | `Lorg/apache/geode/json/StorageFormat;` | 56 | 31 |
| `API-1455f0520dd21e96` | `org.apache.geode.json.StorageFormat` | `PDX` | `Lorg/apache/geode/json/StorageFormat;` | 56 | 31 |
| `API-68a19ae717308aff` | `org.apache.geode.json.StorageFormat` | `valueOf` | `(Ljava/lang/String;)Lorg/apache/geode/json/StorageFormat;` | 56 | 31 |
| `API-eb9c64d3738a89f6` | `org.apache.geode.json.StorageFormat` | `values` | `()[Lorg/apache/geode/json/StorageFormat;` | 56 | 31 |

### json-pdx-conversion — 15 IDs

| API ID | Native receiver | Member | JVM descriptor | Owner | Consumer |
| --- | --- | --- | --- | --- | --- |
| `API-b4dd335ea1980ef1` | `org.apache.geode.pdx.JSONFormatter` | `<init>` | `()V` | 56 | 31 |
| `API-1e311a4bbb449b59` | `org.apache.geode.pdx.JSONFormatter` | `<init>` | `(Lorg/apache/geode/cache/RegionService;)V` | 56 | 31 |
| `API-5e03571fd14fe706` | `org.apache.geode.pdx.JSONFormatter` | `<type>` | `(type)` | 56 | 31 |
| `API-9f4203db77edf6ce` | `org.apache.geode.pdx.JSONFormatter` | `JSON_CLASSNAME` | `Ljava/lang/String;` | 56 | 31 |
| `API-56d43d5833de9b5c` | `org.apache.geode.pdx.JSONFormatter` | `SORT_JSON_FIELD_NAMES_PROPERTY` | `Ljava/lang/String;` | 56 | 31 |
| `API-3f9d1374316a0859` | `org.apache.geode.pdx.JSONFormatter` | `fromJSON` | `(Ljava/lang/String;)Lorg/apache/geode/pdx/PdxInstance;` | 56 | 31 |
| `API-a68a4771e8edcac9` | `org.apache.geode.pdx.JSONFormatter` | `fromJSON` | `(Ljava/lang/String;[Ljava/lang/String;)Lorg/apache/geode/pdx/PdxInstance;` | 56 | 31 |
| `API-d95439950bf502c1` | `org.apache.geode.pdx.JSONFormatter` | `fromJSON` | `([B)Lorg/apache/geode/pdx/PdxInstance;` | 56 | 31 |
| `API-3faf2a71c3c6db6c` | `org.apache.geode.pdx.JSONFormatter` | `fromJSON` | `([B[Ljava/lang/String;)Lorg/apache/geode/pdx/PdxInstance;` | 56 | 31 |
| `API-a5a8e69d1c165081` | `org.apache.geode.pdx.JSONFormatter` | `fromPdxInstance` | `(Lorg/apache/geode/pdx/PdxInstance;)Ljava/lang/String;` | 56 | 31 |
| `API-2e7923e6dc29fd16` | `org.apache.geode.pdx.JSONFormatter` | `getRegionService` | `()Lorg/apache/geode/cache/RegionService;` | 56 | 31 |
| `API-033fbb81f8188f2e` | `org.apache.geode.pdx.JSONFormatter` | `toJSON` | `(Lorg/apache/geode/pdx/PdxInstance;)Ljava/lang/String;` | 56 | 31 |
| `API-fb9b50fb6466bb5f` | `org.apache.geode.pdx.JSONFormatter` | `toJSONByteArray` | `(Lorg/apache/geode/pdx/PdxInstance;)[B` | 56 | 31 |
| `API-e16b35b9b00940a3` | `org.apache.geode.pdx.JSONFormatter` | `toJsonByteArrayFromPdxInstance` | `(Lorg/apache/geode/pdx/PdxInstance;)[B` | 56 | 31 |
| `API-ebb81bdc6a232a02` | `org.apache.geode.pdx.JSONFormatter` | `toPdxInstance` | `(Ljava/lang/Object;[Ljava/lang/String;)Lorg/apache/geode/pdx/PdxInstance;` | 56 | 31 |

### reflection-serializer — 22 IDs

| API ID | Native receiver | Member | JVM descriptor | Owner | Consumer |
| --- | --- | --- | --- | --- | --- |
| `API-5dd758090498ec77` | `org.apache.geode.pdx.ReflectionBasedAutoSerializer` | `<init>` | `()V` | 57 | 31 |
| `API-da304d53ae941c2c` | `org.apache.geode.pdx.ReflectionBasedAutoSerializer` | `<init>` | `(Ljava/util/List;)V` | 57 | 31 |
| `API-2b250223c6d54ed8` | `org.apache.geode.pdx.ReflectionBasedAutoSerializer` | `<init>` | `(Z[Ljava/lang/String;)V` | 57 | 31 |
| `API-33bb5a186d3e7b24` | `org.apache.geode.pdx.ReflectionBasedAutoSerializer` | `<init>` | `([Ljava/lang/String;)V` | 57 | 31 |
| `API-74e8d48631918e76` | `org.apache.geode.pdx.ReflectionBasedAutoSerializer` | `<type>` | `(type)` | 57 | 31 |
| `API-d19a74c948dee3a4` | `org.apache.geode.pdx.ReflectionBasedAutoSerializer` | `fromData` | `(Ljava/lang/Class;Lorg/apache/geode/pdx/PdxReader;)Ljava/lang/Object;` | 57 | 31 |
| `API-f44e76b0cf485a35` | `org.apache.geode.pdx.ReflectionBasedAutoSerializer` | `getConfig` | `()Ljava/util/Properties;` | 57 | 31 |
| `API-2a354bf5edce5b1d` | `org.apache.geode.pdx.ReflectionBasedAutoSerializer` | `getFieldName` | `(Ljava/lang/reflect/Field;Ljava/lang/Class;)Ljava/lang/String;` | 57 | 31 |
| `API-b8aeb394ad92eabc` | `org.apache.geode.pdx.ReflectionBasedAutoSerializer` | `getFieldType` | `(Ljava/lang/reflect/Field;Ljava/lang/Class;)Lorg/apache/geode/pdx/FieldType;` | 57 | 31 |
| `API-750bbbb5c61bfe4c` | `org.apache.geode.pdx.ReflectionBasedAutoSerializer` | `getRegionService` | `()Lorg/apache/geode/cache/RegionService;` | 57 | 31 |
| `API-81b6d3958b60ddb1` | `org.apache.geode.pdx.ReflectionBasedAutoSerializer` | `init` | `(Ljava/util/Properties;)V` | 57 | 31 |
| `API-4d8348c77412e83c` | `org.apache.geode.pdx.ReflectionBasedAutoSerializer` | `initialize` | `(Lorg/apache/geode/cache/Cache;Ljava/util/Properties;)V` | 57 | 31 |
| `API-4f757d4f1d10670d` | `org.apache.geode.pdx.ReflectionBasedAutoSerializer` | `isClassAutoSerialized` | `(Ljava/lang/Class;)Z` | 57 | 31 |
| `API-ab3607e2024c4ac0` | `org.apache.geode.pdx.ReflectionBasedAutoSerializer` | `isFieldIncluded` | `(Ljava/lang/reflect/Field;Ljava/lang/Class;)Z` | 57 | 31 |
| `API-00ea84473c75d93a` | `org.apache.geode.pdx.ReflectionBasedAutoSerializer` | `isIdentityField` | `(Ljava/lang/reflect/Field;Ljava/lang/Class;)Z` | 57 | 31 |
| `API-928dfd24d5a78193` | `org.apache.geode.pdx.ReflectionBasedAutoSerializer` | `readTransform` | `(Ljava/lang/reflect/Field;Ljava/lang/Class;Ljava/lang/Object;)Ljava/lang/Object;` | 57 | 31 |
| `API-1862325aa7cb0247` | `org.apache.geode.pdx.ReflectionBasedAutoSerializer` | `reconfigure` | `(Z[Ljava/lang/String;)V` | 57 | 31 |
| `API-de03ee8c39c85dfb` | `org.apache.geode.pdx.ReflectionBasedAutoSerializer` | `reconfigure` | `([Ljava/lang/String;)V` | 57 | 31 |
| `API-503e79152e788f29` | `org.apache.geode.pdx.ReflectionBasedAutoSerializer` | `setSerializableClasses` | `(Ljava/util/List;)V` | 57 | 31 |
| `API-ba74ff31b4b27c7e` | `org.apache.geode.pdx.ReflectionBasedAutoSerializer` | `toData` | `(Ljava/lang/Object;Lorg/apache/geode/pdx/PdxWriter;)Z` | 57 | 31 |
| `API-da4ebc8ebf40a743` | `org.apache.geode.pdx.ReflectionBasedAutoSerializer` | `transformFieldValue` | `(Ljava/lang/reflect/Field;Ljava/lang/Class;)Z` | 57 | 31 |
| `API-eeb93d4e304c0f5c` | `org.apache.geode.pdx.ReflectionBasedAutoSerializer` | `writeTransform` | `(Ljava/lang/reflect/Field;Ljava/lang/Class;Ljava/lang/Object;)Ljava/lang/Object;` | 57 | 31 |

### reflection-manager — 1 IDs

| API ID | Native receiver | Member | JVM descriptor | Owner | Consumer |
| --- | --- | --- | --- | --- | --- |
| `API-61b8577786d4518b` | `org.apache.geode.pdx.ReflectionBasedAutoSerializer` | `getManager` | `()Ljava/lang/Object;` | 57 | 31 |

### pdx-field-types — 31 IDs

| API ID | Native receiver | Member | JVM descriptor | Owner | Consumer |
| --- | --- | --- | --- | --- | --- |
| `API-55d48b59ce3f33c4` | `org.apache.geode.pdx.FieldType` | `<type>` | `(type)` | 57 | 31 |
| `API-20b6f5035765df15` | `org.apache.geode.pdx.FieldType` | `ARRAY_OF_BYTE_ARRAYS` | `Lorg/apache/geode/pdx/FieldType;` | 57 | 31 |
| `API-3dc7f0992318f472` | `org.apache.geode.pdx.FieldType` | `BOOLEAN` | `Lorg/apache/geode/pdx/FieldType;` | 57 | 31 |
| `API-59c475bfa9b8f8b1` | `org.apache.geode.pdx.FieldType` | `BOOLEAN_ARRAY` | `Lorg/apache/geode/pdx/FieldType;` | 57 | 31 |
| `API-0b83c8e886e2198a` | `org.apache.geode.pdx.FieldType` | `BYTE` | `Lorg/apache/geode/pdx/FieldType;` | 57 | 31 |
| `API-643cb8276957644e` | `org.apache.geode.pdx.FieldType` | `BYTE_ARRAY` | `Lorg/apache/geode/pdx/FieldType;` | 57 | 31 |
| `API-8890f0a8d3a32cb0` | `org.apache.geode.pdx.FieldType` | `CHAR` | `Lorg/apache/geode/pdx/FieldType;` | 57 | 31 |
| `API-c00c9ac0429bd173` | `org.apache.geode.pdx.FieldType` | `CHAR_ARRAY` | `Lorg/apache/geode/pdx/FieldType;` | 57 | 31 |
| `API-25dbc84f36014067` | `org.apache.geode.pdx.FieldType` | `DATE` | `Lorg/apache/geode/pdx/FieldType;` | 57 | 31 |
| `API-a68c0fe86979a557` | `org.apache.geode.pdx.FieldType` | `DOUBLE` | `Lorg/apache/geode/pdx/FieldType;` | 57 | 31 |
| `API-87565348dfd5f3d6` | `org.apache.geode.pdx.FieldType` | `DOUBLE_ARRAY` | `Lorg/apache/geode/pdx/FieldType;` | 57 | 31 |
| `API-30778d7ac5123786` | `org.apache.geode.pdx.FieldType` | `FLOAT` | `Lorg/apache/geode/pdx/FieldType;` | 57 | 31 |
| `API-2b6e0cb21c9f59de` | `org.apache.geode.pdx.FieldType` | `FLOAT_ARRAY` | `Lorg/apache/geode/pdx/FieldType;` | 57 | 31 |
| `API-9c990282024f0acb` | `org.apache.geode.pdx.FieldType` | `INT` | `Lorg/apache/geode/pdx/FieldType;` | 57 | 31 |
| `API-fdaf2208b3849043` | `org.apache.geode.pdx.FieldType` | `INT_ARRAY` | `Lorg/apache/geode/pdx/FieldType;` | 57 | 31 |
| `API-11b38b88712ed759` | `org.apache.geode.pdx.FieldType` | `LONG` | `Lorg/apache/geode/pdx/FieldType;` | 57 | 31 |
| `API-63a72033a8c640e9` | `org.apache.geode.pdx.FieldType` | `LONG_ARRAY` | `Lorg/apache/geode/pdx/FieldType;` | 57 | 31 |
| `API-67dee3d666174310` | `org.apache.geode.pdx.FieldType` | `OBJECT` | `Lorg/apache/geode/pdx/FieldType;` | 57 | 31 |
| `API-bbfba1981c1289fe` | `org.apache.geode.pdx.FieldType` | `OBJECT_ARRAY` | `Lorg/apache/geode/pdx/FieldType;` | 57 | 31 |
| `API-3c12659d37860420` | `org.apache.geode.pdx.FieldType` | `SHORT` | `Lorg/apache/geode/pdx/FieldType;` | 57 | 31 |
| `API-59cd66b8d6616d26` | `org.apache.geode.pdx.FieldType` | `SHORT_ARRAY` | `Lorg/apache/geode/pdx/FieldType;` | 57 | 31 |
| `API-8eff33b8c8f69a86` | `org.apache.geode.pdx.FieldType` | `STRING` | `Lorg/apache/geode/pdx/FieldType;` | 57 | 31 |
| `API-84f2c31126100b30` | `org.apache.geode.pdx.FieldType` | `STRING_ARRAY` | `Lorg/apache/geode/pdx/FieldType;` | 57 | 31 |
| `API-4980cbc4e11d4833` | `org.apache.geode.pdx.FieldType` | `get` | `(Ljava/lang/Class;)Lorg/apache/geode/pdx/FieldType;` | 57 | 31 |
| `API-866db610460b9b58` | `org.apache.geode.pdx.FieldType` | `getDefaultBytes` | `()Ljava/nio/ByteBuffer;` | 57 | 31 |
| `API-f6541375ec91d4dd` | `org.apache.geode.pdx.FieldType` | `getDefaultValue` | `()Ljava/lang/Object;` | 57 | 31 |
| `API-5ea63814e8a02ffe` | `org.apache.geode.pdx.FieldType` | `getWidth` | `()I` | 57 | 31 |
| `API-489869db9e5df8b9` | `org.apache.geode.pdx.FieldType` | `isFixedWidth` | `()Z` | 57 | 31 |
| `API-0854d6068d6a620e` | `org.apache.geode.pdx.FieldType` | `toString` | `()Ljava/lang/String;` | 57 | 31 |
| `API-b73214b2a96fb1f3` | `org.apache.geode.pdx.FieldType` | `valueOf` | `(Ljava/lang/String;)Lorg/apache/geode/pdx/FieldType;` | 57 | 31 |
| `API-d9827de9a8b95a7c` | `org.apache.geode.pdx.FieldType` | `values` | `()[Lorg/apache/geode/pdx/FieldType;` | 57 | 31 |

## Validation

Deterministic generation/check and all 10 scope-policy tests passed. Independent javap matched 372 declarations across 21 types; ownership/source/ID/scope checks and both inventory negative controls passed (3,851 API rows, 133 production imports, 549 source/resource references and 509 Spring exposures). Independent before/after accounting confirms exactly 401 scope/remaining-description changes and 77 owner transfers, with all other fields and implementation mappings/statuses unchanged. All 2,723 frozen assignments remain unique, byte-for-byte unchanged and declaration-stable: 1,490 classified / 1,233 unresolved. The evidence ledger lists each of the 401 assigned IDs exactly once. Five excluded rows have no linked production/SPI reference or implementation mapping; all unfinished required rows have active implementation/consumer owners. Source dispositions are 443 required-client / three excluded-server / 103 unresolved; all 149 existing-SPI references are required-client. The 77-task dependency graph remains acyclic (41 open / 34 resolved / two superseded). No Gradle command was run; this classification changes no Java/build behavior.
