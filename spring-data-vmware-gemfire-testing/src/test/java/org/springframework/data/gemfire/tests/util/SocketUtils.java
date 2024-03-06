/*
 * Copyright (c) VMware, Inc. 2023-2024. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package org.springframework.data.gemfire.tests.util;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Logger;

/**
 * {@link SocketUtils} is a utility class for managing {@link Socket} and {@link ServerSocket} objects.
 *
 * @author John Blum
 * @see ServerSocket
 * @see Socket
 * @since 0.0.1
 */
@SuppressWarnings("unused")
public abstract class SocketUtils {

	private static final Logger log = Logger.getLogger(SocketUtils.class.getName());

	public static boolean close(Socket socket) {

		try {
			if (socket != null) {
				socket.close();
				return true;
			}
		}
		catch (IOException ignore) {
			log.warning(String.format("Failed to close Socket [%s]", socket));
			log.warning(ThrowableUtils.toString(ignore));
		}

		return false;
	}

	public static boolean close(ServerSocket serverSocket) {

		try {
			if (serverSocket != null) {
				serverSocket.close();
				return true;
			}
		}
		catch (IOException ignore) {
			log.warning(String.format("Failed to close ServerSocket [%s]", serverSocket));
			log.warning(ThrowableUtils.toString(ignore));
		}

		return false;
	}
}
