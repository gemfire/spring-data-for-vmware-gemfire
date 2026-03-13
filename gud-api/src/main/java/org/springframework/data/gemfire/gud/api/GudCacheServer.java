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
 * 2026-03-11: Removed - CacheServer is a server-side construct not needed for client API
 */

package org.springframework.data.gemfire.gud.api;

/**
 * GUD API abstraction for GemFire CacheServer.
 * Primarily a server-side construct, but needed for test support utilities.
 */
public interface GudCacheServer {

    int DEFAULT_PORT = 40404;

    String getBindAddress();

    int getPort();

    boolean isRunning();

    void start() throws java.io.IOException;

    void stop();
}
