/*
 * Copyright $originalComment.match(" (\d+)", 1, "-", $today.year)2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

/*
 * Copyright $originalComment.match(" (\d+)", 1, "-", $today.year)2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

/*
 * Copyright $originalComment.match(" (\d+)", 1, "-", $today.year)2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

/*
 * @AI-Generated
 * Generated in whole or in part by Cursor
 * Description:
 * 2026-03-11: Created GudClientCache interface as 1:1 mapping of GemFire ClientCache
 */

package org.springframework.data.gemfire.gud.api;

import java.util.Set;

/**
 * GUD API abstraction for GemFire ClientCache interface.
 * Represents a client cache that connects to GemFire servers.
 */
public interface GudClientCache extends GudRegionService {

    String getName();

    // Distributed system
    GudDistributedSystem getDistributedSystem();

    // Pool management
    GudPool getDefaultPool();
    Set<GudPool> getPools();

    // Region factory
    <K, V> GudClientRegionFactory<K, V> createClientRegionFactory(GudClientRegionShortcut shortcut);

    // Query service
    GudQueryService getQueryService();
    GudQueryService getQueryService(String poolName);

    // PDX
    void setPdxSerializer(GudPdxSerializer serializer);
    GudPdxSerializer getPdxSerializer();
    void setPdxReadSerialized(boolean readSerialized);
    boolean getPdxReadSerialized();
    void setPdxDiskStore(String diskStoreName);
    String getPdxDiskStore();
    void setPdxPersistent(boolean persistent);
    boolean getPdxPersistent();
    void setPdxIgnoreUnreadFields(boolean ignoreUnreadFields);
    boolean getPdxIgnoreUnreadFields();

    // Disk store
    GudDiskStoreFactory createDiskStoreFactory();
    GudDiskStore findDiskStore(String name);

    // Copy strategy
    void setCopyOnRead(boolean copyOnRead);
    boolean getCopyOnRead();

    // Lifecycle
    boolean isClosed();
    void close();
    void close(boolean keepAlive);
    boolean isReconnecting();
    GudClientCache getReconnectedCache();

    // Transactions
    GudCacheTransactionManager getCacheTransactionManager();

    // Logging
    GudLogWriter getLogger();
    GudLogWriter getSecurityLogger();

    // Resource management
    GudResourceManager getResourceManager();

    // Cache XML
    void loadCacheXml(java.io.InputStream inputStream);

    // Ready for events (client cache)
    void readyForEvents();

    // Local query service
    GudQueryService getLocalQueryService();

    // Current servers
    java.util.Set<?> getCurrentServers();
}
