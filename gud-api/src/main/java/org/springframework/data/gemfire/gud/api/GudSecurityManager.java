/*
 * Copyright (c) 2026 Broadcom. All rights reserved.
 */

/*
 * Copyright $originalComment.match(" (\d+)", 1, "-", $today.year)2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

/*
 * @AI-Generated
 * Generated in whole or in part by Cursor
 * Description:
 * 2026-03-13: Created GudSecurityManager interface for security support
 * 2026-03-31: Added USER_NAME/PASSWORD constants and default authorize method
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

    String USER_NAME = "security-username";
    String PASSWORD = "security-password";

    default void init(Properties securityProps) {
    }

    Object authenticate(Properties credentials) throws GudAuthenticationFailedException;

    default boolean authorize(Object principal, GudResourcePermission permission) {
        return true;
    }

    default void close() {
    }
}
