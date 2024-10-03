/*
 * Copyright 2017-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.tests.integration;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

/**
 * Integration Tests for {@link ClientServerIntegrationTestsSupport}.
 *
 * @author John Blum
 * @see Test
 * @see ClientServerIntegrationTests
 * @since 0.0.14.RELEASE
 */
public class ClientServerIntegrationTestsSupportTests extends ClientServerIntegrationTestsSupport {

	@Test
	public void allocatedPortsAreDifferent() throws IOException {

		int expectedSize = 3;

		Set<Integer> allocatedPorts = new HashSet<>();

		for (int index = 0; index < expectedSize; index++) {
			allocatedPorts.add(findAndReserveAvailablePort());
		}

		assertThat(allocatedPorts).hasSize(expectedSize);
	}
}
