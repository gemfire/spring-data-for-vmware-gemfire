// Copyright 2026 Broadcom. All Rights Reserved.
package com.vmware.gemfire.driver.mock;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import com.vmware.gemfire.driver.spi.CacheConfiguration;
import com.vmware.gemfire.driver.spi.PoolConfiguration;
import com.vmware.gemfire.driver.spi.PoolConfiguration.Endpoint;
import com.vmware.gemfire.driver.spi.UnsupportedFeatureException;

class MockDefaultPoolTests {
	private final PoolConfiguration registered = new PoolConfiguration(List.of(new Endpoint("no-dns-needed.invalid", 40404)), List.of(), null);

	@Test
	void emptyRegistryCreatesLocalhostServerPoolAndSelectionIsStable() {
		try (var connection = new MockGemFireDriver().connect(new CacheConfiguration(Map.of()))) {
			var pool = connection.getDefaultPool();
			assertThat(pool.servers()).containsExactly(new Endpoint("localhost", 40404));
			assertThat(connection.getDefaultPool()).isSameAs(pool);
			assertThat(connection.getPool(pool.name())).isSameAs(pool);
		}
	}

	@Test
	void singleRegisteredPoolIsSelectedAndSeveralYieldNull() {
		try (var connection = new MockConnection()) {
			var only = connection.createPool("only", registered);
			assertThat(connection.getDefaultPool()).isSameAs(only);
		}
		try (var connection = new MockConnection()) {
			connection.createPool("a", registered);
			connection.createPool("b", registered);
			assertThat(connection.getDefaultPool()).isNull();
		}
	}

	@Test
	void explicitDefaultPoolWinsOverRegisteredPoolsAndGeneratedNamesAvoidCollisions() {
		var explicit = PoolConfiguration.builder().servers(List.of(new Endpoint("no-dns-needed.invalid", 50505))).minimumConnections(0).build();
		try (var connection = new MockGemFireDriver().connect(new CacheConfiguration(Map.of(), explicit))) {
			connection.createPool("DEFAULT", registered);
			var pool = connection.getDefaultPool();
			assertThat(pool.name()).isNotEqualTo("DEFAULT");
			assertThat(pool.servers()).containsExactly(new Endpoint("no-dns-needed.invalid", 50505));
			assertThat(pool.minimumConnections()).isZero();
		}
	}

	@Test
	void explicitPoolWithoutEndpointsInheritsLocalhostServer() {
		var explicit = PoolConfiguration.builder().minimumConnections(2).build();
		try (var connection = new MockGemFireDriver().connect(new CacheConfiguration(Map.of(), explicit))) {
			assertThat(connection.getDefaultPool().servers()).containsExactly(new Endpoint("localhost", 40404));
			assertThat(connection.getDefaultPool().minimumConnections()).isEqualTo(2);
		}
	}

	@Test
	void unavailableNativeEffectsAreRejectedAtConnect() {
		var explicit = PoolConfiguration.builder().threadLocalConnections(true).build();
		assertThatThrownBy(() -> new MockGemFireDriver().connect(new CacheConfiguration(Map.of(), explicit)))
			.isInstanceOf(UnsupportedFeatureException.class);
	}
}
