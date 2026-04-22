/*
 * Copyright (c) 2026 Broadcom. All rights reserved.
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
 * Copyright $originalComment.match(" (\d+)", 1, "-", $today.year)2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

/*
 * @AI-Generated
 * Generated in whole or in part by Cursor
 * Description:
 * 2026-03-11: Created GudCache interface as 1:1 mapping of GemFire Cache
 * 2026-03-17: Removed hypothetical 10.4 features
 * 2026-04-17: Marked deprecated — GUD is client-only; peer cache is outside supported application API
 */

package org.springframework.data.gemfire.gud.api;

import java.util.Set;

/**
 * GUD API abstraction for GemFire {@code Cache} (peer / server-side cache).
 *
 * @deprecated The GUD contract is <strong>client-only</strong>. Application code should use
 *             {@link GudClientCache} and {@link GudClientRegionFactory}. Peer caches and server
 *             regions belong in integration tests with
 *             <a href="https://github.com/gemfire/gemfire-testcontainers">GemFire Testcontainers</a>,
 *             not in the supported GUD surface. This type remains for legacy transaction/function
 *             signatures and driver adapters until those call sites are migrated.
 */
@Deprecated(since = "4.0")
public interface GudCache extends GudRegionService {

    String getName();

    GudDistributedSystem getDistributedSystem();

    GudCacheTransactionManager getCacheTransactionManager();

    GudResourceManager getResourceManager();

    // Region creation
    <K, V> GudRegionFactory<K, V> createRegionFactory();
    <K, V> GudRegionFactory<K, V> createRegionFactory(GudRegionShortcut shortcut);
    <K, V> GudRegionFactory<K, V> createRegionFactory(GudRegionAttributes<K, V> regionAttributes);

    // Disk store
    GudDiskStoreFactory createDiskStoreFactory();
    GudDiskStore findDiskStore(String name);
    Set<GudDiskStore> listDiskStores();

    // Query and Index
    GudQueryService getQueryService();

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

    // Logging
    GudLogWriter getLogger();
    GudLogWriter getSecurityLogger();

    // Lifecycle
    boolean isClosed();
    void close();
    boolean isReconnecting();
    GudCache getReconnectedCache();

    // Copy strategy
    void setCopyOnRead(boolean copyOnRead);
    boolean getCopyOnRead();

    // Lock lease/timeout
    int getLockLease();
    void setLockLease(int seconds);
    int getLockTimeout();
    void setLockTimeout(int seconds);
    int getMessageSyncInterval();
    void setMessageSyncInterval(int seconds);
    int getSearchTimeout();
    void setSearchTimeout(int seconds);
}
