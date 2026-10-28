# Client disk, resource and snapshot scope — 2026-10-28

**Task 74: 129 assigned / 129 classified / zero unresolved: 94 required-client and 35 excluded-server.** All implementation statuses remain missing; classification does not implement these contracts.

Evidence is pinned to GemFire **10.4.0-build.3056.df98c835**, source commit `df98c8357afe85853d9a4776255d4f0e7cf064cb` in `/Users/uk016635/projects/gemfire`, read with git show. Native paths below are relative to `gemfire-core/src/main/java/org/apache/geode/`, with a `.java` suffix. Exact IDs include every assigned type, overload, constant and inherited member. No unanswered scope question requires a runtime probe.

## client-local-disk-stores

**50 IDs — required-client.** All 50 DiskStore/DiskStoreFactory declarations apply to client-local persistence and overflow. Sources: `cache/{ClientCache,GemFireCache,DiskStore,DiskStoreFactory}` and `internal/cache/{GemFireCacheImpl,DiskStoreFactoryImpl}`. ClientCache extends GemFireCache; shared createDiskStoreFactory/findDiskStore paths do not require a peer cache. Preserve name/default selection, UUID, auto/manual compaction, compaction threshold, oplog size, directories/sizes, queue, interval, write buffer, segment configuration and warning/critical disk percentages. Units matter: directory/oplog limits are MB, time is milliseconds, write buffer is bytes and modern queue size counts operations. Default constants are versioned native configuration evidence, not permission to import native classes or mutable static configuration into the SPI. The two default oplog constants differ (100 versus legacy 1024 MB absent the property override); segments default to zero for the legacy implementation.

DiskStoreFactoryImpl creates missing directories, rejects mismatched directory/size array lengths and negative sizes, copies arrays, and validates compaction threshold in 0..100. Creation serializes by name, reuses an equal existing definition, rejects conflicting attributes, recovers before publishing and closes on failure/closed-cache races. Preserve applicable failure causes and cleanup. DiskStore documents flush completion, roll, conditional manual compaction and destructive destroy semantics; destroy rejects stores still used by regions or PDX. Destroy of a closed persistent region's store can delete its retained data. Task 27 must test real temporary-directory persistence/overflow, recovery, configuration boundaries, reuse/conflicts and cleanup; task 30 preserves Spring DiskStoreFactoryBean, client XML and region disk-store references. Mock metadata must be honest and reject unsupported disk persistence/recovery rather than simulate durable success.

## client-legacy-disk-write-values

**7 IDs — required-client.** Seven `cache/DiskWriteAttributes` declarations remain client configuration obligations. Read with `internal/cache/DiskWriteAttributesImpl.equals`: synchronous mode, rolling and maximum oplog size participate in equality; interval and byte threshold additionally participate for asynchronous mode. The implementation requires another DiskWriteAttributesImpl, so a native-free equivalent needs explicit value/conversion tests rather than copying native class identity. Preserve synchronous/asynchronous, rolling, maximum MB, interval milliseconds and threshold bytes. The deprecated interface documentation alone is insufficient to infer equality. Tasks 27/30 own values, validation and adoption; coordinate legacy configuration values with 49 without enlarging this audit.

## client-region-disk-operations

**2 IDs — required-client.** Two `cache/Region` methods have implementations in `internal/cache/LocalRegion`: writeToDisk invokes asyncForceFlush for a disk-backed region and throws IllegalStateException for empty or non-persistent/non-overflow regions without a disk region. forceRolling delegates when a disk region exists and otherwise does nothing. Tasks 27/31 must preserve these distinct outcomes in local persistent/overflow configurations; names alone do not authorize server disk administration.

## client-heap-resource-settings

**7 IDs — required-client.** Seven `cache/control/ResourceManager` declarations comprise its client-facing type, two default constants and four heap threshold accessors. `internal/cache/GemFireCacheImpl.getResourceManager` delegates to the internal resource manager with cancellation checking; `internal/cache/control/{InternalResourceManager,HeapMemoryMonitor}` implements the thresholds. Current Spring AbstractBasicCacheFactoryBean and ClientCacheApplication consume these settings/constants. Defaults are zero (disabled). Setters reject values outside 0..100, require enabled critical to exceed enabled eviction, and update monitoring/event state. The pinned implementation throws IllegalArgumentException for invalid percentages despite different interface exception prose. Preserve setter order/validation and actual eviction-default interactions in adapter tests. No off-heap methods are present in this assigned ResourceManager surface. Tasks 27/30 own configuration and local pressure behavior; a mock may model settings but must not claim native heap-pressure or eviction behavior without implementing it.

## excluded-partition-resource-operations

**33 IDs — excluded-server.** Thirty-three IDs: all `cache/control/{RebalanceFactory,RebalanceOperation,RebalanceResults,RestoreRedundancyOperation}` declarations and four ResourceManager methods (createRebalanceFactory, createRestoreRedundancyOperation, getRebalanceOperations, getRestoreRedundancyFutures). Their native interfaces operate on hosted partition regions, bucket creation/transfer, primary movement and redundancy repair, including include/exclude selection, simulation, cancellation, timed results and asynchronous futures. They do not configure client subscription redundancy. Returned management redundancy payloads were already excluded in task 73. No public SPI mapping or linked current production source needs retirement for these exact IDs. Retained client heap settings and pool subscription redundancy remain separate obligations.

## client-local-snapshot-services

**14 IDs — required-client.** Fourteen IDs cover `cache/snapshot/{CacheSnapshotService,RegionSnapshotService}` and Region.getSnapshotService. Sources: `internal/cache/{AbstractRegion,GemFireCacheImpl}` and `internal/cache/snapshot/{CacheSnapshotServiceImpl,RegionSnapshotServiceImpl,LocalExporter}`. AbstractRegion creates a RegionSnapshotServiceImpl; the shared cache implementation exposes CacheSnapshotServiceImpl. ClientCache does not itself declare the cache snapshot accessor: implement a typed client-local facade, keeping implementation access private. Cache#getSnapshotService was separately retained by task 67; coordinate with 29/35 without taking its ID.

CacheSnapshotServiceImpl saves roots and recursive subregions, creates an output directory, derives snapshot filenames from paths, and loads files only into existing regions named by the snapshot. Missing regions, unreadable/empty directories, invalid versions and missing classes have observable failures. RegionSnapshotServiceImpl rejects export during an active transaction, requires the .gfd extension, resolves a parent directory and attempts to create it, and deletes the output on failures caught after writer creation. Writer-creation failures and VM failures follow separate paths; do not promise transactional cleanup. LocalExporter iterates local entries, applies filters and skips concurrently destroyed entries. Import buffers records, preserves invalid entries, waits for asynchronous batches and controls callbacks through options; do not promise atomic import rollback.

Crucially, createExporter selects ClientExporter whenever poolName is non-null, which performs remote export. A client-local facade must restrict its region selection and reject unsupported pool-backed/remote cases explicitly; do not delegate cache-wide enumeration blindly or treat a caching proxy as a local region. Parallel partition import/export is excluded separately. Task 44 owns local save/load round trips, existing-region selection, partial failure/cleanup, transaction and I/O/class-loading errors, with consumer 31 and cache assembly 29/35. A mock must explicitly reject native binary snapshot I/O unless it implements that format.

## client-snapshot-filter-options

**12 IDs — required-client.** Twelve declarations: `cache/snapshot/SnapshotFilter`, five nonparallel SnapshotOptions members, and all SnapshotOptions$SnapshotFormat declarations. Read with `internal/cache/snapshot/{SnapshotOptionsImpl,RegionSnapshotServiceImpl,LocalExporter}`. Filters default to null; null removes a filter. Callback invocation defaults false; getters/fluent setters must preserve the configured state. Export passes entries through the predicate. Import presents a read-only Map.Entry: setValue throws UnsupportedOperationException, and the pinned includeEntry implementation returns a deserialized value only for CachedDeserializable; other stored representations (including byte arrays and invalid tokens) produce null in that filter view. Task 44 must pin this discrepancy with adapter compatibility tests rather than promise identical import/export filter views without evidence.

The format enum includes deprecated GEMFIRE and GEODE, plus valueOf/values. Both labels remain client obligations; the pinned RegionSnapshotServiceImpl does not use its format argument to choose distinct encoding, so do not invent separate formats. Native serialization and PDX registry handling stay private, coordinated with 53/55/58. Preserve errors and callback behavior through typed values/functions; no native SnapshotOptions, entry or PDX object may escape into Spring. These are implementation compatibility checks, not unresolved scope questions.

## client-legacy-stream-snapshots

**2 IDs — required-client.** The two deprecated `cache/Region.saveSnapshot(OutputStream)` and loadSnapshot(InputStream) IDs have concrete local implementations in `internal/cache/LocalRegion`. Save rejects proxy regions, checks access, serializes entries with invalid-value markers and a null-key terminator, skips tombstones and closes the caller-supplied stream. It is a concurrent traversal, not a consistent point-in-time transaction. Load rejects proxies and null input, validates the snapshot version/markers, preserves local invalidation and closes input while loading.

LocalRegion.reinitialize destroys and recreates the region under the destroy lock; this is not a putAll merge. The Region contract specifies replacement of current data, destruction of subregions, invalidation of old handles, reacquisition by path and callback close/reinstallation. Failure may leave the region destroyed. Task 44 with 31 must implement and test the client-local equivalent (or make any unsupported version/path explicit), preserving IOException/ClassNotFoundException and region lifecycle failures. Do not import the peer distribution aspects of the legacy contract. Native save/load availability is clear in source, so no scope probe is required; version-specific round trips and handle/callback assertions remain implementation acceptance.

## excluded-parallel-snapshot-options

**2 IDs — excluded-server.** Two SnapshotOptions methods, isParallelMode and setParallelMode, select partitioned-server snapshot behavior. RegionSnapshotServiceImpl.shouldRunInParallel additionally requires a partitioning data policy and excludes LocalDataSet; SnapshotOptionsImpl chooses a partition mapper for this mode. They do not parallelize a client-local snapshot. Do not expose this server option in the client SPI; the private adapter uses the nonparallel path. Neither row has an existing public implementation mapping or current linked production reference.

## Ownership and reconciliation

Retained disk/resource implementation stays with 27 and adoption with 30 (64 IDs) or 31 (two region disk operations). Retained snapshot implementation stays with 44 and adoption with 31 (28 IDs). Coordinate the independently assigned cache snapshot accessor with 29/35. All owners remain active. Excluded IDs are missing/unmapped and have no linked current production source or existing SPI reference to retire; retained native Spring references remain migration work. Client XML, local persistence and client subscription redundancy are preserved.

The frozen 2,723-entry assignment ledger, native declarations, implementation statuses, ownership and SPI/native/mock/test mappings are unchanged. This is policy/documentation work; no Java/build change, Gradle command or new runtime probe is needed. Tasks 59/64 remain open; task 75 is next.

## Exact ID ledger

### client-local-disk-stores — 50 IDs

| API ID | Native receiver | Member | JVM descriptor | Owner | Consumer |
| --- | --- | --- | --- | --- | --- |
| `API-022a8c021870d8a0` | `org.apache.geode.cache.DiskStore` | `<type>` | `(type)` | 27 | 30 |
| `API-8555d99148a0dd32` | `org.apache.geode.cache.DiskStore` | `destroy` | `()V` | 27 | 30 |
| `API-7c34b9106e5761c2` | `org.apache.geode.cache.DiskStore` | `flush` | `()V` | 27 | 30 |
| `API-01932a17017b1048` | `org.apache.geode.cache.DiskStore` | `forceCompaction` | `()Z` | 27 | 30 |
| `API-a7cba7fdbcfa060c` | `org.apache.geode.cache.DiskStore` | `forceRoll` | `()V` | 27 | 30 |
| `API-7710ed4e761e0a75` | `org.apache.geode.cache.DiskStore` | `getAllowForceCompaction` | `()Z` | 27 | 30 |
| `API-8002cdaa775151be` | `org.apache.geode.cache.DiskStore` | `getAutoCompact` | `()Z` | 27 | 30 |
| `API-593d107c1fa9460b` | `org.apache.geode.cache.DiskStore` | `getCompactionThreshold` | `()I` | 27 | 30 |
| `API-46c999b94192ba7f` | `org.apache.geode.cache.DiskStore` | `getDiskDirSizes` | `()[I` | 27 | 30 |
| `API-8f8e5fb03af3e55d` | `org.apache.geode.cache.DiskStore` | `getDiskDirs` | `()[Ljava/io/File;` | 27 | 30 |
| `API-15e699b8763cc20b` | `org.apache.geode.cache.DiskStore` | `getDiskStoreUUID` | `()Ljava/util/UUID;` | 27 | 30 |
| `API-b3230820046146a5` | `org.apache.geode.cache.DiskStore` | `getDiskUsageCriticalPercentage` | `()F` | 27 | 30 |
| `API-575ab7ac154a9d0e` | `org.apache.geode.cache.DiskStore` | `getDiskUsageWarningPercentage` | `()F` | 27 | 30 |
| `API-1c56c51549612087` | `org.apache.geode.cache.DiskStore` | `getMaxOplogSize` | `()J` | 27 | 30 |
| `API-415e43823c95d6d7` | `org.apache.geode.cache.DiskStore` | `getName` | `()Ljava/lang/String;` | 27 | 30 |
| `API-00b36a886d0bd367` | `org.apache.geode.cache.DiskStore` | `getQueueSize` | `()I` | 27 | 30 |
| `API-b10c4b00cf82edd6` | `org.apache.geode.cache.DiskStore` | `getSegments` | `()I` | 27 | 30 |
| `API-b3cbde3174217f46` | `org.apache.geode.cache.DiskStore` | `getTimeInterval` | `()J` | 27 | 30 |
| `API-849506a57cc2fae6` | `org.apache.geode.cache.DiskStore` | `getWriteBufferSize` | `()I` | 27 | 30 |
| `API-2a1b43ec25492a6a` | `org.apache.geode.cache.DiskStore` | `setDiskUsageCriticalPercentage` | `(F)V` | 27 | 30 |
| `API-617c52c20b1770cf` | `org.apache.geode.cache.DiskStore` | `setDiskUsageWarningPercentage` | `(F)V` | 27 | 30 |
| `API-fcab8229011d515f` | `org.apache.geode.cache.DiskStoreFactory` | `<type>` | `(type)` | 27 | 30 |
| `API-39f86cbf7cc11d5f` | `org.apache.geode.cache.DiskStoreFactory` | `DEFAULT_ALLOW_FORCE_COMPACTION` | `Z` | 27 | 30 |
| `API-93bed7fdc2284125` | `org.apache.geode.cache.DiskStoreFactory` | `DEFAULT_AUTO_COMPACT` | `Z` | 27 | 30 |
| `API-4bc19060ae2527f7` | `org.apache.geode.cache.DiskStoreFactory` | `DEFAULT_COMPACTION_THRESHOLD` | `I` | 27 | 30 |
| `API-19a5ae13d2a49ce9` | `org.apache.geode.cache.DiskStoreFactory` | `DEFAULT_DISK_DIRS` | `[Ljava/io/File;` | 27 | 30 |
| `API-a0d4df7fc72c1f30` | `org.apache.geode.cache.DiskStoreFactory` | `DEFAULT_DISK_DIR_SIZE` | `I` | 27 | 30 |
| `API-54253e0fde199ad7` | `org.apache.geode.cache.DiskStoreFactory` | `DEFAULT_DISK_DIR_SIZES` | `[I` | 27 | 30 |
| `API-bee3db55856f8363` | `org.apache.geode.cache.DiskStoreFactory` | `DEFAULT_DISK_STORE_NAME` | `Ljava/lang/String;` | 27 | 30 |
| `API-00a881a8db4b5722` | `org.apache.geode.cache.DiskStoreFactory` | `DEFAULT_DISK_USAGE_CRITICAL_PERCENTAGE` | `F` | 27 | 30 |
| `API-e4981a27f884adfd` | `org.apache.geode.cache.DiskStoreFactory` | `DEFAULT_DISK_USAGE_WARNING_PERCENTAGE` | `F` | 27 | 30 |
| `API-7bfdc81e4a6a14de` | `org.apache.geode.cache.DiskStoreFactory` | `DEFAULT_LEGACY_MAX_OPLOG_SIZE` | `J` | 27 | 30 |
| `API-0763f58e17070e09` | `org.apache.geode.cache.DiskStoreFactory` | `DEFAULT_MAX_OPLOG_SIZE` | `J` | 27 | 30 |
| `API-20d9f4af0dbb983e` | `org.apache.geode.cache.DiskStoreFactory` | `DEFAULT_QUEUE_SIZE` | `I` | 27 | 30 |
| `API-8ccbb166eaa1ffd3` | `org.apache.geode.cache.DiskStoreFactory` | `DEFAULT_SEGMENTS` | `I` | 27 | 30 |
| `API-f3a7613acbd5bf23` | `org.apache.geode.cache.DiskStoreFactory` | `DEFAULT_TIME_INTERVAL` | `J` | 27 | 30 |
| `API-5eaebcba623c11d3` | `org.apache.geode.cache.DiskStoreFactory` | `DEFAULT_WRITE_BUFFER_SIZE` | `I` | 27 | 30 |
| `API-0599777e28580333` | `org.apache.geode.cache.DiskStoreFactory` | `create` | `(Ljava/lang/String;)Lorg/apache/geode/cache/DiskStore;` | 27 | 30 |
| `API-0202bfee4fc17d47` | `org.apache.geode.cache.DiskStoreFactory` | `setAllowForceCompaction` | `(Z)Lorg/apache/geode/cache/DiskStoreFactory;` | 27 | 30 |
| `API-3492c18fb8152d0a` | `org.apache.geode.cache.DiskStoreFactory` | `setAutoCompact` | `(Z)Lorg/apache/geode/cache/DiskStoreFactory;` | 27 | 30 |
| `API-f01fef211b13b9f8` | `org.apache.geode.cache.DiskStoreFactory` | `setCompactionThreshold` | `(I)Lorg/apache/geode/cache/DiskStoreFactory;` | 27 | 30 |
| `API-295d363c2e413121` | `org.apache.geode.cache.DiskStoreFactory` | `setDiskDirs` | `([Ljava/io/File;)Lorg/apache/geode/cache/DiskStoreFactory;` | 27 | 30 |
| `API-e031f0eccc6623ab` | `org.apache.geode.cache.DiskStoreFactory` | `setDiskDirsAndSizes` | `([Ljava/io/File;[I)Lorg/apache/geode/cache/DiskStoreFactory;` | 27 | 30 |
| `API-073b704c706ab40c` | `org.apache.geode.cache.DiskStoreFactory` | `setDiskUsageCriticalPercentage` | `(F)Lorg/apache/geode/cache/DiskStoreFactory;` | 27 | 30 |
| `API-02b6505b82081a56` | `org.apache.geode.cache.DiskStoreFactory` | `setDiskUsageWarningPercentage` | `(F)Lorg/apache/geode/cache/DiskStoreFactory;` | 27 | 30 |
| `API-3823e3843bf8bf36` | `org.apache.geode.cache.DiskStoreFactory` | `setMaxOplogSize` | `(J)Lorg/apache/geode/cache/DiskStoreFactory;` | 27 | 30 |
| `API-5d1ef32d50a0b4d7` | `org.apache.geode.cache.DiskStoreFactory` | `setQueueSize` | `(I)Lorg/apache/geode/cache/DiskStoreFactory;` | 27 | 30 |
| `API-7690eee12196d68a` | `org.apache.geode.cache.DiskStoreFactory` | `setSegments` | `(I)Lorg/apache/geode/cache/DiskStoreFactory;` | 27 | 30 |
| `API-22b3231735487e09` | `org.apache.geode.cache.DiskStoreFactory` | `setTimeInterval` | `(J)Lorg/apache/geode/cache/DiskStoreFactory;` | 27 | 30 |
| `API-385e64733f407fa2` | `org.apache.geode.cache.DiskStoreFactory` | `setWriteBufferSize` | `(I)Lorg/apache/geode/cache/DiskStoreFactory;` | 27 | 30 |

### client-legacy-disk-write-values — 7 IDs

| API ID | Native receiver | Member | JVM descriptor | Owner | Consumer |
| --- | --- | --- | --- | --- | --- |
| `API-f6f3fea33d56627f` | `org.apache.geode.cache.DiskWriteAttributes` | `<type>` | `(type)` | 27 | 30 |
| `API-8aab44de548aaf41` | `org.apache.geode.cache.DiskWriteAttributes` | `equals` | `(Ljava/lang/Object;)Z` | 27 | 30 |
| `API-0a36dba104a0edd5` | `org.apache.geode.cache.DiskWriteAttributes` | `getBytesThreshold` | `()J` | 27 | 30 |
| `API-92728a9b03877e28` | `org.apache.geode.cache.DiskWriteAttributes` | `getMaxOplogSize` | `()I` | 27 | 30 |
| `API-a30c3530cd87e113` | `org.apache.geode.cache.DiskWriteAttributes` | `getTimeInterval` | `()J` | 27 | 30 |
| `API-368d500bdd26f638` | `org.apache.geode.cache.DiskWriteAttributes` | `isRollOplogs` | `()Z` | 27 | 30 |
| `API-94dfccccfbc844b9` | `org.apache.geode.cache.DiskWriteAttributes` | `isSynchronous` | `()Z` | 27 | 30 |

### client-region-disk-operations — 2 IDs

| API ID | Native receiver | Member | JVM descriptor | Owner | Consumer |
| --- | --- | --- | --- | --- | --- |
| `API-3cac2083db278146` | `org.apache.geode.cache.Region` | `forceRolling` | `()V` | 27 | 31 |
| `API-2036c94b93be3b9e` | `org.apache.geode.cache.Region` | `writeToDisk` | `()V` | 27 | 31 |

### client-heap-resource-settings — 7 IDs

| API ID | Native receiver | Member | JVM descriptor | Owner | Consumer |
| --- | --- | --- | --- | --- | --- |
| `API-fc6100a7f6b5182f` | `org.apache.geode.cache.control.ResourceManager` | `<type>` | `(type)` | 27 | 30 |
| `API-fff193feddbc4c8f` | `org.apache.geode.cache.control.ResourceManager` | `DEFAULT_CRITICAL_PERCENTAGE` | `F` | 27 | 30 |
| `API-7be42a5984b90e4f` | `org.apache.geode.cache.control.ResourceManager` | `DEFAULT_EVICTION_PERCENTAGE` | `F` | 27 | 30 |
| `API-1a64a5364b9f66a6` | `org.apache.geode.cache.control.ResourceManager` | `getCriticalHeapPercentage` | `()F` | 27 | 30 |
| `API-08bb2338e7461444` | `org.apache.geode.cache.control.ResourceManager` | `getEvictionHeapPercentage` | `()F` | 27 | 30 |
| `API-2ac776e0126eeed6` | `org.apache.geode.cache.control.ResourceManager` | `setCriticalHeapPercentage` | `(F)V` | 27 | 30 |
| `API-8e585cf63a13050f` | `org.apache.geode.cache.control.ResourceManager` | `setEvictionHeapPercentage` | `(F)V` | 27 | 30 |

### excluded-partition-resource-operations — 33 IDs

| API ID | Native receiver | Member | JVM descriptor | Owner | Consumer |
| --- | --- | --- | --- | --- | --- |
| `API-b2b72937e341ff73` | `org.apache.geode.cache.control.RebalanceFactory` | `<type>` | `(type)` | 27 | 30 |
| `API-22cecca9d17e49e9` | `org.apache.geode.cache.control.RebalanceFactory` | `excludeRegions` | `(Ljava/util/Set;)Lorg/apache/geode/cache/control/RebalanceFactory;` | 27 | 30 |
| `API-440f88367c3c1f19` | `org.apache.geode.cache.control.RebalanceFactory` | `includeRegions` | `(Ljava/util/Set;)Lorg/apache/geode/cache/control/RebalanceFactory;` | 27 | 30 |
| `API-7d3d50ddb4893a55` | `org.apache.geode.cache.control.RebalanceFactory` | `simulate` | `()Lorg/apache/geode/cache/control/RebalanceOperation;` | 27 | 30 |
| `API-38bb22e2861c9d22` | `org.apache.geode.cache.control.RebalanceFactory` | `start` | `()Lorg/apache/geode/cache/control/RebalanceOperation;` | 27 | 30 |
| `API-b35f3cb25a11f43c` | `org.apache.geode.cache.control.RebalanceOperation` | `<type>` | `(type)` | 27 | 30 |
| `API-e2a327edb52ac108` | `org.apache.geode.cache.control.RebalanceOperation` | `cancel` | `()Z` | 27 | 30 |
| `API-c2ca1c6cc4ac75fe` | `org.apache.geode.cache.control.RebalanceOperation` | `getResults` | `()Lorg/apache/geode/cache/control/RebalanceResults;` | 27 | 30 |
| `API-df9211a14e77dce7` | `org.apache.geode.cache.control.RebalanceOperation` | `getResults` | `(JLjava/util/concurrent/TimeUnit;)Lorg/apache/geode/cache/control/RebalanceResults;` | 27 | 30 |
| `API-ef355bf08492d90c` | `org.apache.geode.cache.control.RebalanceOperation` | `isCancelled` | `()Z` | 27 | 30 |
| `API-58bc58a1fdfc4406` | `org.apache.geode.cache.control.RebalanceOperation` | `isDone` | `()Z` | 27 | 30 |
| `API-9a475111fd3d6b1c` | `org.apache.geode.cache.control.RebalanceResults` | `<type>` | `(type)` | 27 | 30 |
| `API-9c8aad42a8f0c6d0` | `org.apache.geode.cache.control.RebalanceResults` | `getPartitionRebalanceDetails` | `()Ljava/util/Set;` | 27 | 30 |
| `API-6582027f69b6ab91` | `org.apache.geode.cache.control.RebalanceResults` | `getTotalBucketCreateBytes` | `()J` | 27 | 30 |
| `API-fde67317ce99d164` | `org.apache.geode.cache.control.RebalanceResults` | `getTotalBucketCreateTime` | `()J` | 27 | 30 |
| `API-378e8dc6104d0294` | `org.apache.geode.cache.control.RebalanceResults` | `getTotalBucketCreatesCompleted` | `()I` | 27 | 30 |
| `API-919a32648a003c41` | `org.apache.geode.cache.control.RebalanceResults` | `getTotalBucketTransferBytes` | `()J` | 27 | 30 |
| `API-423642f870f18fb8` | `org.apache.geode.cache.control.RebalanceResults` | `getTotalBucketTransferTime` | `()J` | 27 | 30 |
| `API-ebb37569a777d2b9` | `org.apache.geode.cache.control.RebalanceResults` | `getTotalBucketTransfersCompleted` | `()I` | 27 | 30 |
| `API-6df72b1581b720ab` | `org.apache.geode.cache.control.RebalanceResults` | `getTotalMembersExecutedOn` | `()I` | 27 | 30 |
| `API-1a3d290c9ae7122c` | `org.apache.geode.cache.control.RebalanceResults` | `getTotalPrimaryTransferTime` | `()J` | 27 | 30 |
| `API-c45683b50d19c82d` | `org.apache.geode.cache.control.RebalanceResults` | `getTotalPrimaryTransfersCompleted` | `()I` | 27 | 30 |
| `API-8dce6c34e8e948ba` | `org.apache.geode.cache.control.RebalanceResults` | `getTotalTime` | `()J` | 27 | 30 |
| `API-7fe25efda3118c63` | `org.apache.geode.cache.control.ResourceManager` | `createRebalanceFactory` | `()Lorg/apache/geode/cache/control/RebalanceFactory;` | 27 | 30 |
| `API-458ab0271396cdfd` | `org.apache.geode.cache.control.ResourceManager` | `createRestoreRedundancyOperation` | `()Lorg/apache/geode/cache/control/RestoreRedundancyOperation;` | 27 | 30 |
| `API-5bc36241215896c2` | `org.apache.geode.cache.control.ResourceManager` | `getRebalanceOperations` | `()Ljava/util/Set;` | 27 | 30 |
| `API-31cc08968b086b6b` | `org.apache.geode.cache.control.ResourceManager` | `getRestoreRedundancyFutures` | `()Ljava/util/Set;` | 27 | 30 |
| `API-0ca753e10db64d6a` | `org.apache.geode.cache.control.RestoreRedundancyOperation` | `<type>` | `(type)` | 27 | 30 |
| `API-f390ded227f1567f` | `org.apache.geode.cache.control.RestoreRedundancyOperation` | `excludeRegions` | `(Ljava/util/Set;)Lorg/apache/geode/cache/control/RestoreRedundancyOperation;` | 27 | 30 |
| `API-dcdcac1c5df1562b` | `org.apache.geode.cache.control.RestoreRedundancyOperation` | `includeRegions` | `(Ljava/util/Set;)Lorg/apache/geode/cache/control/RestoreRedundancyOperation;` | 27 | 30 |
| `API-bd8d604b70017bb9` | `org.apache.geode.cache.control.RestoreRedundancyOperation` | `redundancyStatus` | `()Lorg/apache/geode/management/runtime/RestoreRedundancyResults;` | 27 | 30 |
| `API-45b0564e621f6ab6` | `org.apache.geode.cache.control.RestoreRedundancyOperation` | `shouldReassignPrimaries` | `(Z)Lorg/apache/geode/cache/control/RestoreRedundancyOperation;` | 27 | 30 |
| `API-b608bd49cf038bbd` | `org.apache.geode.cache.control.RestoreRedundancyOperation` | `start` | `()Ljava/util/concurrent/CompletableFuture;` | 27 | 30 |

### client-local-snapshot-services — 14 IDs

| API ID | Native receiver | Member | JVM descriptor | Owner | Consumer |
| --- | --- | --- | --- | --- | --- |
| `API-4e7565d31e1bf74f` | `org.apache.geode.cache.Region` | `getSnapshotService` | `()Lorg/apache/geode/cache/snapshot/RegionSnapshotService;` | 44 | 31 |
| `API-ab6c7a2cf20481a0` | `org.apache.geode.cache.snapshot.CacheSnapshotService` | `<type>` | `(type)` | 44 | 31 |
| `API-3d23cee7d12942d8` | `org.apache.geode.cache.snapshot.CacheSnapshotService` | `createOptions` | `()Lorg/apache/geode/cache/snapshot/SnapshotOptions;` | 44 | 31 |
| `API-ec50afed74530277` | `org.apache.geode.cache.snapshot.CacheSnapshotService` | `load` | `(Ljava/io/File;Lorg/apache/geode/cache/snapshot/SnapshotOptions$SnapshotFormat;)V` | 44 | 31 |
| `API-a8aa6933578a8705` | `org.apache.geode.cache.snapshot.CacheSnapshotService` | `load` | `([Ljava/io/File;Lorg/apache/geode/cache/snapshot/SnapshotOptions$SnapshotFormat;Lorg/apache/geode/cache/snapshot/SnapshotOptions;)V` | 44 | 31 |
| `API-9e69772bb73be923` | `org.apache.geode.cache.snapshot.CacheSnapshotService` | `save` | `(Ljava/io/File;Lorg/apache/geode/cache/snapshot/SnapshotOptions$SnapshotFormat;)V` | 44 | 31 |
| `API-08b2bddd8ad4520a` | `org.apache.geode.cache.snapshot.CacheSnapshotService` | `save` | `(Ljava/io/File;Lorg/apache/geode/cache/snapshot/SnapshotOptions$SnapshotFormat;Lorg/apache/geode/cache/snapshot/SnapshotOptions;)V` | 44 | 31 |
| `API-083adc8df7cdf87b` | `org.apache.geode.cache.snapshot.RegionSnapshotService` | `<type>` | `(type)` | 44 | 31 |
| `API-a2bb963704f55ea8` | `org.apache.geode.cache.snapshot.RegionSnapshotService` | `SNAPSHOT_FILE_EXTENSION` | `Ljava/lang/String;` | 44 | 31 |
| `API-8cba54bf61b7f1d9` | `org.apache.geode.cache.snapshot.RegionSnapshotService` | `createOptions` | `()Lorg/apache/geode/cache/snapshot/SnapshotOptions;` | 44 | 31 |
| `API-10fbb6cf1c558905` | `org.apache.geode.cache.snapshot.RegionSnapshotService` | `load` | `(Ljava/io/File;Lorg/apache/geode/cache/snapshot/SnapshotOptions$SnapshotFormat;)V` | 44 | 31 |
| `API-24f246e27ff07ed3` | `org.apache.geode.cache.snapshot.RegionSnapshotService` | `load` | `(Ljava/io/File;Lorg/apache/geode/cache/snapshot/SnapshotOptions$SnapshotFormat;Lorg/apache/geode/cache/snapshot/SnapshotOptions;)V` | 44 | 31 |
| `API-8dc28c7ba2e6a0d2` | `org.apache.geode.cache.snapshot.RegionSnapshotService` | `save` | `(Ljava/io/File;Lorg/apache/geode/cache/snapshot/SnapshotOptions$SnapshotFormat;)V` | 44 | 31 |
| `API-78c8b2079bf50b1c` | `org.apache.geode.cache.snapshot.RegionSnapshotService` | `save` | `(Ljava/io/File;Lorg/apache/geode/cache/snapshot/SnapshotOptions$SnapshotFormat;Lorg/apache/geode/cache/snapshot/SnapshotOptions;)V` | 44 | 31 |

### client-snapshot-filter-options — 12 IDs

| API ID | Native receiver | Member | JVM descriptor | Owner | Consumer |
| --- | --- | --- | --- | --- | --- |
| `API-e9d4c9d9c113246f` | `org.apache.geode.cache.snapshot.SnapshotFilter` | `<type>` | `(type)` | 44 | 31 |
| `API-239b594de71f1712` | `org.apache.geode.cache.snapshot.SnapshotFilter` | `accept` | `(Ljava/util/Map$Entry;)Z` | 44 | 31 |
| `API-e0f15e55f206333c` | `org.apache.geode.cache.snapshot.SnapshotOptions` | `<type>` | `(type)` | 44 | 31 |
| `API-7c59f92530651452` | `org.apache.geode.cache.snapshot.SnapshotOptions` | `getFilter` | `()Lorg/apache/geode/cache/snapshot/SnapshotFilter;` | 44 | 31 |
| `API-f939185aa319e950` | `org.apache.geode.cache.snapshot.SnapshotOptions` | `invokeCallbacks` | `(Z)Lorg/apache/geode/cache/snapshot/SnapshotOptions;` | 44 | 31 |
| `API-331752d88ea17d7b` | `org.apache.geode.cache.snapshot.SnapshotOptions` | `setFilter` | `(Lorg/apache/geode/cache/snapshot/SnapshotFilter;)Lorg/apache/geode/cache/snapshot/SnapshotOptions;` | 44 | 31 |
| `API-5f14739b5d7c3622` | `org.apache.geode.cache.snapshot.SnapshotOptions` | `shouldInvokeCallbacks` | `()Z` | 44 | 31 |
| `API-bd6719e6e5591666` | `org.apache.geode.cache.snapshot.SnapshotOptions$SnapshotFormat` | `<type>` | `(type)` | 44 | 31 |
| `API-1b930820a8f25e91` | `org.apache.geode.cache.snapshot.SnapshotOptions$SnapshotFormat` | `GEMFIRE` | `Lorg/apache/geode/cache/snapshot/SnapshotOptions$SnapshotFormat;` | 44 | 31 |
| `API-5ba57b7f1aa09474` | `org.apache.geode.cache.snapshot.SnapshotOptions$SnapshotFormat` | `GEODE` | `Lorg/apache/geode/cache/snapshot/SnapshotOptions$SnapshotFormat;` | 44 | 31 |
| `API-6dad4888046bf5ab` | `org.apache.geode.cache.snapshot.SnapshotOptions$SnapshotFormat` | `valueOf` | `(Ljava/lang/String;)Lorg/apache/geode/cache/snapshot/SnapshotOptions$SnapshotFormat;` | 44 | 31 |
| `API-81ce85dec56a0ee1` | `org.apache.geode.cache.snapshot.SnapshotOptions$SnapshotFormat` | `values` | `()[Lorg/apache/geode/cache/snapshot/SnapshotOptions$SnapshotFormat;` | 44 | 31 |

### client-legacy-stream-snapshots — 2 IDs

| API ID | Native receiver | Member | JVM descriptor | Owner | Consumer |
| --- | --- | --- | --- | --- | --- |
| `API-51dc0d167f15770f` | `org.apache.geode.cache.Region` | `loadSnapshot` | `(Ljava/io/InputStream;)V` | 44 | 31 |
| `API-ff1e161bb09b0949` | `org.apache.geode.cache.Region` | `saveSnapshot` | `(Ljava/io/OutputStream;)V` | 44 | 31 |

### excluded-parallel-snapshot-options — 2 IDs

| API ID | Native receiver | Member | JVM descriptor | Owner | Consumer |
| --- | --- | --- | --- | --- | --- |
| `API-c2021e8efa13e5ec` | `org.apache.geode.cache.snapshot.SnapshotOptions` | `isParallelMode` | `()Z` | 44 | 31 |
| `API-e57dfb34c51f7e09` | `org.apache.geode.cache.snapshot.SnapshotOptions` | `setParallelMode` | `(Z)Lorg/apache/geode/cache/snapshot/SnapshotOptions;` | 44 | 31 |
