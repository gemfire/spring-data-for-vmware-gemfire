/*
 * Copyright $originalComment.match(" (\d+)", 1, "-", $today.year)2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

/*
 * @AI-Generated
 * Generated in whole or in part by Cursor
 * Description:
 * 2026-03-13: Created GemFire 10.3 ResourceManager adapter
 */

package org.springframework.data.gemfire.gud.driver.gemfire103;

import java.util.Set;

import org.apache.geode.cache.control.ResourceManager;

import org.springframework.data.gemfire.gud.api.GudRebalanceFactory;
import org.springframework.data.gemfire.gud.api.GudResourceManager;

/**
 * GUD API adapter for GemFire 10.3 ResourceManager.
 */
public class GemFire103ResourceManager implements GudResourceManager, NativeWrapper<ResourceManager> {

    private final ResourceManager nativeManager;

    public GemFire103ResourceManager(ResourceManager nativeManager) {
        this.nativeManager = nativeManager;
    }

    @Override
    public ResourceManager getNative() {
        return nativeManager;
    }

    @Override
    public float getCriticalHeapPercentage() {
        return nativeManager.getCriticalHeapPercentage();
    }

    @Override
    public void setCriticalHeapPercentage(float percentage) {
        nativeManager.setCriticalHeapPercentage(percentage);
    }

    @Override
    public float getEvictionHeapPercentage() {
        return nativeManager.getEvictionHeapPercentage();
    }

    @Override
    public void setEvictionHeapPercentage(float percentage) {
        nativeManager.setEvictionHeapPercentage(percentage);
    }

    @Override
    public float getCriticalOffHeapPercentage() {
        return nativeManager.getCriticalOffHeapPercentage();
    }

    @Override
    public void setCriticalOffHeapPercentage(float percentage) {
        nativeManager.setCriticalOffHeapPercentage(percentage);
    }

    @Override
    public float getEvictionOffHeapPercentage() {
        return nativeManager.getEvictionOffHeapPercentage();
    }

    @Override
    public void setEvictionOffHeapPercentage(float percentage) {
        nativeManager.setEvictionOffHeapPercentage(percentage);
    }

    @Override
    public GudRebalanceFactory createRebalanceFactory() {
        return new GemFire103RebalanceFactory(nativeManager.createRebalanceFactory());
    }

    @Override
    public Set<?> getRebalanceOperations() {
        return nativeManager.getRebalanceOperations();
    }
}
