/*
 * Copyright $originalComment.match(" (\d+)", 1, "-", $today.year)2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

/*
 * @AI-Generated
 * Generated in whole or in part by Cursor
 * Description:
 * 2026-03-13: Created GemFire 10.3 PartitionAttributes adapter
 */

package org.springframework.data.gemfire.gud.driver.gemfire103;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.geode.cache.FixedPartitionAttributes;
import org.apache.geode.cache.PartitionAttributes;

import org.springframework.data.gemfire.gud.api.*;

/**
 * GUD API adapter for GemFire 10.3 PartitionAttributes.
 *
 * @param <K> the key type
 * @param <V> the value type
 */
public class GemFire103PartitionAttributes<K, V> implements GudPartitionAttributes<K, V>, NativeWrapper<PartitionAttributes<K, V>> {

    private final PartitionAttributes<K, V> nativeAttributes;

    public GemFire103PartitionAttributes(PartitionAttributes<K, V> nativeAttributes) {
        this.nativeAttributes = nativeAttributes;
    }

    @Override
    public PartitionAttributes<K, V> getNative() {
        return nativeAttributes;
    }

    @Override
    public int getRedundantCopies() {
        return nativeAttributes.getRedundantCopies();
    }

    @Override
    public int getTotalNumBuckets() {
        return nativeAttributes.getTotalNumBuckets();
    }

    @Override
    public int getTotalMaxMemory() {
        return (int) nativeAttributes.getTotalMaxMemory();
    }

    @Override
    public int getLocalMaxMemory() {
        return nativeAttributes.getLocalMaxMemory();
    }

    @Override
    public String getColocatedWith() {
        return nativeAttributes.getColocatedWith();
    }

    @Override
    public long getRecoveryDelay() {
        return nativeAttributes.getRecoveryDelay();
    }

    @Override
    public long getStartupRecoveryDelay() {
        return nativeAttributes.getStartupRecoveryDelay();
    }

    @Override
    @SuppressWarnings("unchecked")
    public GudPartitionResolver<K, V> getPartitionResolver() {
        org.apache.geode.cache.PartitionResolver<K, V> resolver = nativeAttributes.getPartitionResolver();
        return resolver != null ? new GemFire103PartitionResolverWrapper<>(resolver) : null;
    }

    @Override
    public List<GudPartitionListener> getPartitionListeners() {
        org.apache.geode.cache.partition.PartitionListener[] listeners = nativeAttributes.getPartitionListeners();
        if (listeners == null) return java.util.Collections.emptyList();
        return java.util.Arrays.stream(listeners)
            .map(GemFire103PartitionListenerWrapper::new)
            .collect(Collectors.toList());
    }

    @Override
    public GudFixedPartitionAttributes[] getFixedPartitionAttributes() {
        List<FixedPartitionAttributes> list = nativeAttributes.getFixedPartitionAttributes();
        if (list == null || list.isEmpty()) return new GudFixedPartitionAttributes[0];
        return list.stream()
            .map(GemFire103FixedPartitionAttributes::new)
            .toArray(GudFixedPartitionAttributes[]::new);
    }
}
