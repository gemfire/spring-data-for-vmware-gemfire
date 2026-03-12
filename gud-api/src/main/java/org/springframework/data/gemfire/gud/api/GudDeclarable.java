/*
 * Copyright $originalComment.match(" (\d+)", 1, "-", $today.year)2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

/*
 * @AI-Generated
 * Generated in whole or in part by Cursor
 * Description:
 * 2026-03-11: Created GudDeclarable interface as 1:1 mapping of GemFire Declarable
 */

package org.springframework.data.gemfire.gud.api;

import java.util.Properties;

/**
 * GUD API abstraction for GemFire Declarable interface.
 * Marks objects that can be declared in cache.xml.
 */
public interface GudDeclarable {

    default void init(Properties props) {
        // Default no-op - subclasses override as needed
    }

    default void initialize(GudCache cache, Properties props) {
        init(props);
    }
}
