/*
 * Copyright $originalComment.match(" (\d+)", 1, "-", $today.year)2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

/*
 * @AI-Generated
 * Generated in whole or in part by Cursor
 * Description:
 * 2026-03-11: Created GudSocketFactory interface as 1:1 mapping of GemFire SocketFactory
 */

package org.springframework.data.gemfire.gud.api;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * GUD API abstraction for GemFire SocketFactory interface.
 * Factory for creating custom sockets.
 */
public interface GudSocketFactory {

    Socket createSocket() throws IOException;
}
