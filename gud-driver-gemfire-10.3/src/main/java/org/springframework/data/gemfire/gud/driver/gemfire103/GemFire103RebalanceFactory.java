/*
 * Copyright $originalComment.match(" (\d+)", 1, "-", $today.year)2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

/*
 * @AI-Generated
 * Generated in whole or in part by Cursor
 * Description:
 * 2026-03-13: Created GemFire 10.3 RebalanceFactory adapter
 */

package org.springframework.data.gemfire.gud.driver.gemfire103;

import java.util.Set;

import org.apache.geode.cache.control.RebalanceFactory;

import org.springframework.data.gemfire.gud.api.GudRebalanceFactory;
import org.springframework.data.gemfire.gud.api.GudRebalanceOperation;

/**
 * GUD API adapter for GemFire 10.3 RebalanceFactory.
 */
public class GemFire103RebalanceFactory implements GudRebalanceFactory, NativeWrapper<RebalanceFactory> {

    private final RebalanceFactory nativeFactory;

    public GemFire103RebalanceFactory(RebalanceFactory nativeFactory) {
        this.nativeFactory = nativeFactory;
    }

    @Override
    public RebalanceFactory getNative() {
        return nativeFactory;
    }

    @Override
    public GudRebalanceFactory includeRegions(Set<String> regions) {
        nativeFactory.includeRegions(regions);
        return this;
    }

    @Override
    public GudRebalanceFactory excludeRegions(Set<String> regions) {
        nativeFactory.excludeRegions(regions);
        return this;
    }

    @Override
    public GudRebalanceOperation start() {
        return new GemFire103RebalanceOperation(nativeFactory.start());
    }

    @Override
    public GudRebalanceOperation simulate() {
        return new GemFire103RebalanceOperation(nativeFactory.simulate());
    }
}
