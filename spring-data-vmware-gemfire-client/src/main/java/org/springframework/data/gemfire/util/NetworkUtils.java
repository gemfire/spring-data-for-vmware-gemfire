/*
 * Copyright (c) VMware, Inc. 2022-2024. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.util;

/**
 * Abstract utility class providing functions for networking.
 *
 * @author John Blum
 * @since 2.2.0
 */
public abstract class NetworkUtils {

	public static final String INVALID_PORT_MESSAGE =
		"Port [%d] must be greater than equal to 0 and less than 65536";

	public static final String INVALID_NO_EPHEMERAL_PORT_MESSAGE =
		"Port [%d] must be greater than 0 and less than 65536";

	/**
	 * Determines whether the given {@link Integer#TYPE port} is valid.
	 *
	 * Technically, port 0 is valid too but no client would use port 0 (the ephemeral port) to connect to a service.
	 *
	 * @param port port to evaluate.
	 * @return a boolean value indicating whether the {@link Integer#TYPE port} is valid or not.
	 */
	public static boolean isValidPort(int port) {
		return port > -1 && port < 65536;
	}

	public static boolean isValidNonEphemeralPort(int port) {
		return isValidPort(port) && port > 0;
	}
}
