/*
 * Copyright 2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package com.vmware.gemfire.driver.gemfire104;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.Map;

import org.junit.jupiter.api.Test;

import com.vmware.gemfire.driver.spi.CacheConfiguration;
import com.vmware.gemfire.driver.spi.DriverConnection;
import com.vmware.gemfire.driver.spi.DriverOperationException;
import com.vmware.gemfire.driver.spi.config.ClientRegionShortcut;

/** Native characterization, intentionally outside the shared contract and mock suites. */
class NativeConnectionReuseTests {

	@Test
	void compatibleClientConnectionsShareCacheEffectsAndClosure() {
		var driver = new GemFire104Driver();
		var configuration = new CacheConfiguration(Map.of("mcast-port", "0", "locators", "", "log-level", "error"));
		try (DriverConnection first = driver.connect(configuration);
			DriverConnection second = driver.connect(configuration)) {
			assertThat(first).isNotSameAs(second);
			first.createClientRegion("shared", ClientRegionShortcut.LOCAL).put("key", "first");
			assertThat(second.getRegion("shared").get("key")).isEqualTo("first");
			second.getRegion("shared").put("key", "second");
			assertThat(first.getRegion("shared").get("key")).isEqualTo("second");
			second.close();
			assertThat(first.isClosed()).isTrue();
			assertThatThrownBy(() -> first.getRegion("shared")).isInstanceOf(DriverOperationException.class);
		}
	}
}
