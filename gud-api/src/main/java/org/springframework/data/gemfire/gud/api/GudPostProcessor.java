/*
 * Copyright (c) 2026 Broadcom. All rights reserved.
 */

// Copyright (c) 2026 Broadcom. All Rights Reserved.

/*
 * @AI-Generated
 * Generated in whole or in part by Cursor
 * Description:
 * 2026-03-31: Created GudPostProcessor interface as 1:1 mapping of GemFire PostProcessor
 */

package org.springframework.data.gemfire.gud.api;

import java.util.Properties;

/**
 * GUD API abstraction for GemFire PostProcessor interface.
 * Allows post-processing of values before sending them to the requester.
 */
public interface GudPostProcessor {

    default void init(Properties securityProps) {}

    Object processRegionValue(Object principal, String regionName, Object key, Object value);

    default void close() {}
}
