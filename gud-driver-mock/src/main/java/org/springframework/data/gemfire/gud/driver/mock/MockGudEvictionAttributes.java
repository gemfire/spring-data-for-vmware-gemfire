/*
 * Copyright (c) 2026 Broadcom. All rights reserved.
 */

// Copyright (c) 2026 Broadcom. All Rights Reserved.

/*
 * @AI-Generated
 * Generated in whole or in part by Cursor
 * Description:
 * 2026-04-17: In-memory GudEvictionAttributes for MockGudDriver eviction factories
 */

package org.springframework.data.gemfire.gud.driver.mock;

import org.springframework.data.gemfire.gud.api.GudEvictionAction;
import org.springframework.data.gemfire.gud.api.GudEvictionAlgorithm;
import org.springframework.data.gemfire.gud.api.GudEvictionAttributes;
import org.springframework.data.gemfire.gud.api.GudObjectSizer;

/**
 * Simple eviction-attribute holder for unit tests (no native GemFire dependency).
 */
public final class MockGudEvictionAttributes implements GudEvictionAttributes {

    private final GudEvictionAlgorithm algorithm;
    private final GudEvictionAction action;
    private final int maximum;
    private final GudObjectSizer objectSizer;

    public MockGudEvictionAttributes(GudEvictionAlgorithm algorithm, GudEvictionAction action, int maximum,
            GudObjectSizer objectSizer) {
        this.algorithm = algorithm;
        this.action = action;
        this.maximum = maximum;
        this.objectSizer = objectSizer;
    }

    @Override
    public GudEvictionAlgorithm getAlgorithm() {
        return algorithm;
    }

    @Override
    public GudEvictionAction getAction() {
        return action;
    }

    @Override
    public int getMaximum() {
        return maximum;
    }

    @Override
    public GudObjectSizer getObjectSizer() {
        return objectSizer;
    }
}
