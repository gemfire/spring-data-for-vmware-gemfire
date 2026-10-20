/*
 * Copyright 2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package com.vmware.gemfire.driver.spi;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import com.vmware.gemfire.driver.spi.PoolConfiguration.Endpoint;

class PoolConfigurationTests {
	@Test
	void copiesEndpointsAndDistinguishesUnsetFromZero() {
		var endpoints = new ArrayList<>(List.of(new Endpoint("host", 40404)));
		var config = new PoolConfiguration(endpoints, List.of(), null);
		endpoints.clear();
		assertThat(config.servers()).containsExactly(new Endpoint("host", 40404));
		assertThatThrownBy(() -> config.servers().clear()).isInstanceOf(UnsupportedOperationException.class);
		assertThat(config.minimumConnections()).isEmpty();
		assertThat(new PoolConfiguration(List.of(), config.servers(), 0).minimumConnections()).hasValue(0);
	}
	@Test
	void rejectsMissingMixedAndInvalidEndpointsAndNegativeMinimum() {
		var endpoints = List.of(new Endpoint("host", 40404));
		assertThatThrownBy(() -> new PoolConfiguration(List.of(), List.of(), null)).isInstanceOf(IllegalArgumentException.class);
		assertThatThrownBy(() -> new PoolConfiguration(endpoints, endpoints, null)).isInstanceOf(IllegalArgumentException.class);
		assertThatThrownBy(() -> new PoolConfiguration(endpoints, List.of(), -1)).isInstanceOf(IllegalArgumentException.class);
		assertThatThrownBy(() -> new PoolConfiguration(null, endpoints, null)).isInstanceOf(NullPointerException.class);
		assertThatThrownBy(() -> new PoolConfiguration(endpoints, null, null)).isInstanceOf(NullPointerException.class);
		assertThatThrownBy(() -> new PoolConfiguration(java.util.Arrays.asList((Endpoint) null), List.of(), null))
			.isInstanceOf(NullPointerException.class);
		for (String host : new String[] { null, "", " " }) {
			assertThatThrownBy(() -> new Endpoint(host, 40404)).isInstanceOf(IllegalArgumentException.class);
		}
		for (int port : new int[] { -1, 0, 65536 }) {
			assertThatThrownBy(() -> new Endpoint("host", port)).isInstanceOf(IllegalArgumentException.class);
		}
	}
	@Test
	void newOperationsDefaultToTypedUnsupportedOnOlderImplementations() {
		DriverConnection connection = new DriverConnection() {};
		DriverPool pool = new DriverPool() {};
		List<org.assertj.core.api.ThrowableAssert.ThrowingCallable> calls = List.of(
			() -> connection.createPool("pool", null), () -> connection.getPool("pool"),
			pool::name, pool::servers, pool::locators, pool::minimumConnections, pool::isDestroyed, pool::destroy);
		List<String> features = List.of("Connection.createPool", "Connection.getPool", "Pool.name", "Pool.servers",
			"Pool.locators", "Pool.minimumConnections", "Pool.isDestroyed", "Pool.destroy");
		for (int i = 0; i < calls.size(); i++) {
			String feature = features.get(i);
			assertThatThrownBy(calls.get(i)).isInstanceOfSatisfying(UnsupportedFeatureException.class, failure -> {
				assertThat(failure.feature()).isEqualTo(feature);
				assertThat(failure.reason()).isEqualTo(UnsupportedFeatureException.Reason.NOT_INTRODUCED);
				assertThat(failure.gemFireVersion()).isEmpty();
			});
		}
	}
}
