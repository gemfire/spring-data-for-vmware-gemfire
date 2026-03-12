/*
 * Copyright $originalComment.match(" (\d+)", 1, "-", $today.year)2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

/*
 * @AI-Generated
 * Generated in whole or in part by Cursor
 * Description:
 * 2026-03-11: Created GudCacheFactory interface for GUD API
 */

package org.springframework.data.gemfire.gud.api;

import java.util.Properties;

/**
 * GUD API abstraction for GemFire CacheFactory.
 */
public interface GudCacheFactory {

    GudCacheFactory set(String name, String value);
    
    GudCacheFactory setPdxSerializer(GudPdxSerializer serializer);
    
    GudCacheFactory setPdxReadSerialized(boolean readSerialized);
    
    GudCacheFactory setPdxIgnoreUnreadFields(boolean ignoreUnreadFields);
    
    GudCacheFactory setPdxPersistent(boolean persistent);
    
    GudCacheFactory setPdxDiskStore(String diskStoreName);
    
    GudCache create();
    
    static GudCache getAnyInstance() {
        throw new UnsupportedOperationException("Use GudCacheProvider instead");
    }
}
