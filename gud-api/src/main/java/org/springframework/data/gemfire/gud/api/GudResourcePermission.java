/*
 * Copyright $originalComment.match(" (\d+)", 1, "-", $today.year)2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

/*
 * @AI-Generated
 * Generated in whole or in part by Cursor
 * Description:
 * 2026-03-13: Created GudResourcePermission for security authorization
 */
package org.springframework.data.gemfire.gud.api;

/**
 * GUD API abstraction for resource permissions used in security authorization.
 * This is used to check if a principal has permission to perform an operation.
 *
 * @since 1.0.0
 */
public interface GudResourcePermission {

    /**
     * Resource types that can be secured.
     */
    enum Resource {
        NULL, CLUSTER, DATA
    }

    /**
     * Operations that can be performed on resources.
     */
    enum Operation {
        NULL, READ, WRITE, MANAGE
    }

    /**
     * Returns the resource type for this permission.
     *
     * @return the resource type
     */
    Resource getResource();

    /**
     * Returns the operation for this permission.
     *
     * @return the operation
     */
    Operation getOperation();

    /**
     * Returns the target region for this permission, if applicable.
     *
     * @return the target region name, or null if not applicable
     */
    String getRegion();

    /**
     * Returns the key for this permission, if applicable.
     *
     * @return the key, or null if not applicable
     */
    String getKey();
}
