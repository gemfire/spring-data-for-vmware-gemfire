/*
 * Copyright $originalComment.match(" (\d+)", 1, "-", $today.year)2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

/*
 * @AI-Generated
 * Generated in whole or in part by Cursor
 * Description:
 * 2026-03-13: Created GudSecurityManager interface for security support
 */
package org.springframework.data.gemfire.gud.api;

import java.util.Properties;

/**
 * GUD API abstraction for security managers that handle authentication and authorization.
 * This interface is used by GemFire servers to authenticate clients and authorize operations.
 *
 * @since 1.0.0
 */
public interface GudSecurityManager {

    /**
     * Initialize the security manager with the given properties.
     *
     * @param securityProps security properties from the GemFire configuration
     */
    default void init(Properties securityProps) {
    }

    /**
     * Authenticate a client using the provided credentials.
     *
     * @param credentials the credentials provided by the client
     * @return a principal object representing the authenticated user
     * @throws GudAuthenticationFailedException if authentication fails
     */
    Object authenticate(Properties credentials) throws GudAuthenticationFailedException;

    /**
     * Authorize an operation for a given principal.
     *
     * @param principal the authenticated principal
     * @param permission the permission being requested
     * @return true if the operation is authorized, false otherwise
     */
    boolean authorize(Object principal, GudResourcePermission permission);

    /**
     * Close and clean up any resources used by the security manager.
     */
    default void close() {
    }
}
