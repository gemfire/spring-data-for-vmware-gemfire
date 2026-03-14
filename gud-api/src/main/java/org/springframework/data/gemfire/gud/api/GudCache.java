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
 * 2026-03-14: Added default methods for API evolution support (10.4+ features)
 */

package org.springframework.data.gemfire.gud.api;

import java.util.Set;

/**
 * GUD API abstraction for GemFire Cache interface.
 * Represents a peer cache in a GemFire distributed system.
 */
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

    // ===== API Evolution: 10.4+ Features =====
    // These default methods throw UnsupportedOperationException when called on older drivers.

    /**
     * Returns enhanced security manager for this cache.
     * This feature requires GemFire 10.4 or later.
     *
     * @return the enhanced security manager
     * @throws GudUnsupportedOperationException if the driver doesn't support this feature
     * @since GUD API 1.1 (GemFire 10.4+)
     */
    default GudEnhancedSecurityManager getEnhancedSecurityManager() {
        throw new GudUnsupportedOperationException(
            "getEnhancedSecurityManager() requires a GemFire 10.4+ driver. " +
            "Check driver.supportsCapability(GudCapability.ENHANCED_SECURITY) before calling.",
            "ENHANCED_SECURITY", "10.4");
    }
}
