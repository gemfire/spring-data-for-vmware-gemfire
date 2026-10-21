/*
 * Copyright 2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package com.vmware.gemfire.driver.mock;

import static org.assertj.core.api.Assertions.*;
import java.util.*;
import org.junit.jupiter.api.Test;
import com.vmware.gemfire.driver.spi.*;

class MockPoolConfigurationTests {
	@Test void localSettingsDoNotInvokeNetworkingCallbacksOrInventRemoteState() {
		try (var connection = new MockGemFireDriver().connect(new CacheConfiguration(CacheConfiguration.Mode.CLIENT, Map.of()))) {
			DriverSocketFactory socketFactory = () -> { throw new AssertionError("Mock must not open a socket"); };
			var pool = connection.createPool("metadata", PoolConfiguration.builder()
				.servers(List.of(new PoolConfiguration.Endpoint("unresolved.invalid", 40404)))
				.subscriptionEnabled(true).multiuserAuthentication(true).socketFactory(socketFactory).build());
			var configuration = pool.configuration();
			assertThat(configuration.subscriptionEnabled()).contains(true);
			assertThat(configuration.multiuserAuthentication()).contains(true);
			assertThat(configuration.socketFactory()).containsSame(socketFactory);
			pool.destroy(); assertThat(connection.getPool("metadata")).isNull();
			assertThat(configuration.subscriptionEnabled()).contains(true);
		}
	}
}
