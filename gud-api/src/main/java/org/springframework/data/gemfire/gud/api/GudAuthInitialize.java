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
 * 2026-03-11: Created GudAuthInitialize interface as 1:1 mapping of GemFire AuthInitialize
 */

package org.springframework.data.gemfire.gud.api;

import java.util.Properties;

/**
 * GUD API abstraction for GemFire AuthInitialize interface.
 * Callback for generating credentials for authentication.
 */
public interface GudAuthInitialize {

    String SECURITY_USERNAME = "security-username";
    String SECURITY_PASSWORD = "security-password";

    Properties getCredentials(Properties securityProps, GudDistributedMember server, boolean isPeer);

    default void init(GudLogWriter systemLogger, GudLogWriter securityLogger) {
        // Default no-op
    }

    default void close() {
        // Default no-op
    }
}
