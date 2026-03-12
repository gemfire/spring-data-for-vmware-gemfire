/*
 * Copyright $originalComment.match(" (\d+)", 1, "-", $today.year)2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

/*
 * @AI-Generated
 * Generated in whole or in part by Cursor
 * Description:
 * 2026-03-11: Created GudJndiBinding interface for JNDI data source management
 */

package org.springframework.data.gemfire.gud.api;

import java.util.List;
import java.util.Map;

/**
 * GUD API interface for managing JNDI data source bindings.
 * Provides abstraction over GemFire's internal JNDIInvoker functionality.
 */
public interface GudJndiBinding {

    /**
     * Maps a data source with the given attributes and configuration properties.
     *
     * @param attributes map of JNDI binding attributes (e.g., jndi-name, type, connection-url)
     * @param props list of additional configuration properties
     */
    void mapDatasource(Map<String, String> attributes, List<GudConfigProperty> props);

    /**
     * Unmaps a previously bound data source.
     *
     * @param jndiName the JNDI name of the data source to unmap
     */
    void unmapDatasource(String jndiName);
}
