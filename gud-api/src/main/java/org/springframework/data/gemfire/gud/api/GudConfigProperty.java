/*
 * Copyright $originalComment.match(" (\d+)", 1, "-", $today.year)2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

/*
 * @AI-Generated
 * Generated in whole or in part by Cursor
 * Description:
 * 2026-03-11: Created GudConfigProperty interface as abstraction for JNDI config properties
 */

package org.springframework.data.gemfire.gud.api;

/**
 * GUD API abstraction for JNDI configuration properties.
 * Used for configuring JNDI data sources in GemFire cache.
 */
public interface GudConfigProperty {

    /**
     * Gets the name of this configuration property.
     *
     * @return the property name
     */
    String getName();

    /**
     * Gets the value of this configuration property.
     *
     * @return the property value
     */
    String getValue();

    /**
     * Gets the type of this configuration property.
     *
     * @return the property type (fully qualified class name)
     */
    String getType();
}
