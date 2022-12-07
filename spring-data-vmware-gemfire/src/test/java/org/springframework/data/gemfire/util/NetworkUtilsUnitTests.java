/*
 * Copyright (c) VMware, Inc. 2022. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.util;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

/**
 * Unit Tests for {@link NetworkUtils}.
 *
 * @author John Blum
 * @see org.junit.Test
 * @see org.springframework.data.gemfire.util.NetworkUtils
 * @since 2.2.0
 */
public class NetworkUtilsUnitTests {

	@Test
	public void invalidPortMessageIsCorrect() {
		assertThat(String.format(NetworkUtils.INVALID_PORT_MESSAGE, -1))
			.isEqualTo("Port [-1] must be greater than equal to 0 and less than 65536");
	}

	@Test
	public void invalidNonEphemeralPortMessageIsCorrect() {
		assertThat(String.format(NetworkUtils.INVALID_NO_EPHEMERAL_PORT_MESSAGE, -1))
			.isEqualTo("Port [-1] must be greater than 0 and less than 65536");
	}

	@Test
	public void withValidPortsReturnsTrue() {

		for (int port = 0; port < 65536; port++) {
			assertThat(NetworkUtils.isValidPort(port)).isTrue();
		}
	}

	@Test
	public void withInvalidPortsReturnsFalse() {

		assertThat(NetworkUtils.isValidPort(-21)).isFalse();
		assertThat(NetworkUtils.isValidPort(-1)).isFalse();
		assertThat(NetworkUtils.isValidPort(65536)).isFalse();
		assertThat(NetworkUtils.isValidPort(99199)).isFalse();
	}

	@Test
	public void withValidNonEphemeralPortsReturnsTrue() {

		for (int port = 1; port < 65536; port++) {
			assertThat(NetworkUtils.isValidNonEphemeralPort(port)).isTrue();
		}
	}

	@Test
	public void withInvalidNonEphemeralPortsReturnsFalse() {

		assertThat(NetworkUtils.isValidNonEphemeralPort(-21)).isFalse();
		assertThat(NetworkUtils.isValidNonEphemeralPort(-1)).isFalse();
		assertThat(NetworkUtils.isValidNonEphemeralPort(0)).isFalse();
		assertThat(NetworkUtils.isValidNonEphemeralPort(65536)).isFalse();
		assertThat(NetworkUtils.isValidNonEphemeralPort(99199)).isFalse();
	}
}
