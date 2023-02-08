/*
 * Copyright (c) VMware, Inc. 2022-2023. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.support;

import java.net.InetSocketAddress;

import org.springframework.data.gemfire.util.SpringExtensions;
import org.springframework.lang.NonNull;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

/**
 * The ConnectionEndpoint class models a GemFire connection endpoint in the format of hostname[portnumber],
 * where hostname is the network name or IP address of the host.
 *
 * @author John Blum
 * @author Jacob Barret
 * @see java.lang.Cloneable
 * @see java.lang.Comparable
 * @since 1.6.3
 */
@SuppressWarnings("unused")
public class ConnectionEndpoint implements Cloneable, Comparable<ConnectionEndpoint> {

	protected static final int DEFAULT_PORT = 0; // ephemeral port

	protected static final String DEFAULT_HOST = "localhost";
	protected static final String GEMFIRE_HOST_PORT_SEPARATOR = "[";
	protected static final String STANDARD_HOST_PORT_SEPARATOR = ":";

	private final int port;
	private final String host;

	/**
	 * Factory method used to construct a new {@link ConnectionEndpoint} with the given {@link Integer port}
	 * listening on the default host.
	 *
	 * @param port {@link Integer port} of the {@link ConnectionEndpoint}.
	 * @return a new {@link ConnectionEndpoint} with the given {@link Integer port} listening on the default host.
	 * @see #from(String, int)
	 */
	public static @NonNull ConnectionEndpoint from(int port) {
		return from(DEFAULT_HOST, port);
	}

	/**
	 * Factory method used to construct a new {@link ConnectionEndpoint} for the given {@link String host}
	 * and {@link Integer port}.
	 *
	 * @param host {@link String host} of the {@link ConnectionEndpoint}.
	 * @param port {@link Integer port} of the {@link ConnectionEndpoint}.
	 * @return a new {@link ConnectionEndpoint} with the given {@link String host} and {@link Integer port}.
	 */
	public static @NonNull ConnectionEndpoint from(String host, int port) {
		return new ConnectionEndpoint(host, port);
	}

	/**
	 * Factory method used to convert the given {@link InetSocketAddress} into a {@link ConnectionEndpoint}.
	 *
	 * @param socketAddress {@link InetSocketAddress} used to construct, configure and initialize
	 * the {@link ConnectionEndpoint}.
	 * @return a {@link ConnectionEndpoint} representing the {@link InetSocketAddress}.
	 * @see java.net.InetSocketAddress
	 */
	public static @NonNull ConnectionEndpoint from(@NonNull InetSocketAddress socketAddress) {
		return new ConnectionEndpoint(socketAddress.getHostString(), socketAddress.getPort());
	}

	/**
	 * Parses the host and port String value into a valid ConnectionEndpoint.
	 *
	 * @param hostPort a String value containing the host and port formatted as 'host[port]'.
	 * @return a valid ConnectionEndpoint initialized with the host and port, or with DEFAULT_PORT
	 * if port was unspecified.
	 * @see #parse(String, int)
	 * @see #DEFAULT_PORT
	 */
	public static ConnectionEndpoint parse(String hostPort) {
		return parse(hostPort, DEFAULT_PORT);
	}

	/**
	 * Factory method used to parse the {@link String host and port} value into a valid {@link ConnectionEndpoint}.
	 *
	 * @param hostPort {@link String} containing the host and port formatted as {@literal host[port]}
	 * or {@literal host:port}.
	 * @param defaultPort {@link Integer} indicating the default port to use if the port is unspecified
	 * in the {@link String host and port} value.
	 * @return a valid {@link ConnectionEndpoint} initialized with the {@link String host and port},
	 * or with the default port if port was unspecified.
	 * @see #ConnectionEndpoint(String, int)
	 */
	public static ConnectionEndpoint parse(String hostPort, int defaultPort) {

		Assert.hasText(hostPort, String.format("Host & Port [%s] must be specified", hostPort));

		String host = StringUtils.trimAllWhitespace(hostPort);

		int port = defaultPort;
		int portIndex = indexOfPort(host);

		if (portIndex > -1) {
			port = parsePort(parseDigits(host.substring(portIndex)), defaultPort);
			host = host.substring(0, portIndex).trim();
		}

		return new ConnectionEndpoint(host, port);
	}

	static int indexOfPort(String host) {

		int indexOfPort = host.indexOf(GEMFIRE_HOST_PORT_SEPARATOR);

		return indexOfPort > -1 ? indexOfPort : host.indexOf(STANDARD_HOST_PORT_SEPARATOR);
	}

	static String parseDigits(String value) {

		StringBuilder digits = new StringBuilder();

		if (StringUtils.hasText(value)) {
			for (char character : value.toCharArray()) {
				if (Character.isDigit(character)) {
					digits.append(character);
				}
			}
		}

		return digits.toString();
	}

	static int parsePort(String port, int defaultPort) {

		try {
			return Integer.parseInt(port);
		}
		catch (NumberFormatException ignore) {
			return defaultPort;
		}
	}

	/**
	 * Constructs a ConnectionEndpoint initialized with the specific host and port.
	 *
	 * @param host the hostname or IP address of the ConnectionEndpoint.  If the host is unspecified,
	 * then ConnectionEndpoint.DEFAULT_HOST will be used.
	 * @param port the (service) port number in this ConnectionEndpoint.
	 * @throws IllegalArgumentException if the port number is less than 0.
	 * @see ConnectionEndpoint#DEFAULT_HOST
	 */
	public ConnectionEndpoint(String host, int port) {

		Assert.isTrue(isValidPort(port), String.format("port number [%d] must be between 0 and 65535", port));

		this.host = SpringExtensions.defaultIfEmpty(host, DEFAULT_HOST);
		this.port = port;
	}

	private boolean isValidPort(int port) {
		return port >= 0 && port <= 65535;
	}

	/**
	 * Gets the host in this ConnectionEndpoint.
	 *
	 * @return a String value indicating the hostname or IP address in this ConnectionEndpoint.
	 */
	public String getHost() {
		return this.host;
	}

	/**
	 * Gets the port number in this ConnectionEndpoint.
	 *
	 * @return an Integer value indicating the (service) port number in this ConnectionEndpoint.
	 */
	public int getPort() {
		return this.port;
	}

	/**
	 * Converts this {@link ConnectionEndpoint} into an {@link InetSocketAddress} representation.
	 *
	 * @return an {@link InetSocketAddress} representation of this {@link ConnectionEndpoint}.
	 * @see java.net.InetSocketAddress
	 * @see #getHost()
	 * @see #getPort()
	 */
	public InetSocketAddress toInetSocketAddress() {
		return new InetSocketAddress(getHost(), getPort());
	}

	@Override
	@SuppressWarnings("all")
	protected Object clone() throws CloneNotSupportedException {
		return new ConnectionEndpoint(this.getHost(), this.getPort());
	}

	@Override
	@SuppressWarnings("all")
	public int compareTo(ConnectionEndpoint connectionEndpoint) {

		int compareValue = getHost().compareTo(connectionEndpoint.getHost());

		return compareValue != 0 ? compareValue : getPort() - connectionEndpoint.getPort();
	}

	@Override
	public boolean equals(final Object obj) {

		if (this == obj) {
			return true;
		}

		if (!(obj instanceof ConnectionEndpoint)) {
			return false;
		}

		ConnectionEndpoint that = (ConnectionEndpoint) obj;

		return ObjectUtils.nullSafeEquals(this.getHost(), that.getHost())
			&& ObjectUtils.nullSafeEquals(this.getPort(), that.getPort());
	}

	@Override
	public int hashCode() {

		int hashValue = 17;

		hashValue = 37 * hashValue + ObjectUtils.nullSafeHashCode(getHost());
		hashValue = 37 * hashValue + ObjectUtils.nullSafeHashCode(getPort());

		return hashValue;
	}

	@Override
	public String toString() {
		return String.format("%1$s[%2$d]", getHost(), getPort());
	}
}
