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
 * 2026-03-13: Created GemFire 10.1 PartitionResolver wrapper
 */

package org.springframework.data.gemfire.gud.driver;

import org.apache.geode.cache.PartitionResolver;

import org.springframework.data.gemfire.gud.api.GudEntryOperation;
import org.springframework.data.gemfire.gud.api.GudPartitionResolver;

/**
 * Wrapper for native PartitionResolver to expose as GudPartitionResolver.
 *
 * @param <K> the key type
 * @param <V> the value type
 */
public class GemFirePartitionResolverWrapper<K, V> implements GudPartitionResolver<K, V>, NativeWrapper<PartitionResolver<K, V>> {

    private final PartitionResolver<K, V> nativeResolver;

    public GemFirePartitionResolverWrapper(PartitionResolver<K, V> nativeResolver) {
        this.nativeResolver = nativeResolver;
    }

    @Override
    public PartitionResolver<K, V> getNative() {
        return nativeResolver;
    }

    @Override
    public Object getRoutingObject(GudEntryOperation<K, V> opDetails) {
        // This wrapper is for exposing native resolvers, not for wrapping GUD events
        return null;
    }

    @Override
    public String getName() {
        return nativeResolver.getName();
    }

    @Override
    public void close() {
        nativeResolver.close();
    }
}
