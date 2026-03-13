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
 * 2026-03-11: Created GudResourceManager interface as 1:1 mapping of GemFire ResourceManager
 */

package org.springframework.data.gemfire.gud.api;

/**
 * GUD API abstraction for GemFire ResourceManager interface.
 * Manages cache resources like memory.
 */
public interface GudResourceManager {

    float DEFAULT_CRITICAL_PERCENTAGE = 0.0f;
    float DEFAULT_EVICTION_PERCENTAGE = 0.0f;

    float getCriticalHeapPercentage();
    void setCriticalHeapPercentage(float percentage);

    float getEvictionHeapPercentage();
    void setEvictionHeapPercentage(float percentage);

    float getCriticalOffHeapPercentage();
    void setCriticalOffHeapPercentage(float percentage);

    float getEvictionOffHeapPercentage();
    void setEvictionOffHeapPercentage(float percentage);

    GudRebalanceFactory createRebalanceFactory();
}
