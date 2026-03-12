/*
 * Copyright $originalComment.match(" (\d+)", 1, "-", $today.year)2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

/*
 * @AI-Generated
 * Generated in whole or in part by Cursor
 * Description:
 * 2026-03-11: Created GudClientSubscriptionConfig interface as 1:1 mapping of GemFire ClientSubscriptionConfig
 */

package org.springframework.data.gemfire.gud.api;

/**
 * GUD API abstraction for GemFire ClientSubscriptionConfig interface.
 * Configuration for client subscriptions on a cache server.
 */
public interface GudClientSubscriptionConfig {

    String DEFAULT_EVICTION_POLICY = "none";

    int getCapacity();
    void setCapacity(int capacity);

    String getDiskStoreName();
    void setDiskStoreName(String diskStoreName);

    String getEvictionPolicy();
    void setEvictionPolicy(String evictionPolicy);
}
