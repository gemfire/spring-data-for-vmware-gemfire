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
 * Copyright $originalComment.match(" (\d+)", 1, "-", $today.year)2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

/*
 * @AI-Generated
 * Generated in whole or in part by Cursor
 * Description:
 * 2026-03-13: Created GemFire 10.0 CacheFactory implementation
 */

package org.springframework.data.gemfire.gud.driver;

import org.apache.geode.cache.CacheFactory;

import org.springframework.data.gemfire.gud.api.GudCache;
import org.springframework.data.gemfire.gud.api.GudCacheFactory;
import org.springframework.data.gemfire.gud.api.GudPdxSerializer;

/**
 * GemFire 10.0 implementation of GudCacheFactory.
 * Wraps the native CacheFactory and delegates all operations.
 */
public class GemFireCacheFactory implements GudCacheFactory {

    private final CacheFactory nativeFactory;

    public GemFireCacheFactory() {
        this.nativeFactory = new CacheFactory();
    }

    @Override
    public GudCacheFactory set(String name, String value) {
        nativeFactory.set(name, value);
        return this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public GudCacheFactory setPdxSerializer(GudPdxSerializer serializer) {
        if (serializer instanceof NativeWrapper) {
            nativeFactory.setPdxSerializer(((NativeWrapper<org.apache.geode.pdx.PdxSerializer>) serializer).getNative());
        }
        return this;
    }

    @Override
    public GudCacheFactory setPdxReadSerialized(boolean readSerialized) {
        nativeFactory.setPdxReadSerialized(readSerialized);
        return this;
    }

    @Override
    public GudCacheFactory setPdxIgnoreUnreadFields(boolean ignoreUnreadFields) {
        nativeFactory.setPdxIgnoreUnreadFields(ignoreUnreadFields);
        return this;
    }

    @Override
    public GudCacheFactory setPdxPersistent(boolean persistent) {
        nativeFactory.setPdxPersistent(persistent);
        return this;
    }

    @Override
    public GudCacheFactory setPdxDiskStore(String diskStoreName) {
        nativeFactory.setPdxDiskStore(diskStoreName);
        return this;
    }

    @Override
    public GudCache create() {
        return new GemFireCache(nativeFactory.create());
    }
}
