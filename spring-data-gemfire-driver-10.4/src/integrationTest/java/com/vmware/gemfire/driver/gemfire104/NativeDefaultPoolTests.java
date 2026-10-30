// Copyright 2026 Broadcom. All Rights Reserved.
package com.vmware.gemfire.driver.gemfire104;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.apache.geode.cache.client.PoolManager;

import com.vmware.gemfire.driver.spi.CacheConfiguration;
import com.vmware.gemfire.driver.spi.DriverBindException;
import com.vmware.gemfire.driver.spi.DriverConnection;
import com.vmware.gemfire.driver.spi.DriverSocketFactory;
import com.vmware.gemfire.driver.spi.PoolConfiguration;
import com.vmware.gemfire.driver.spi.PoolConfiguration.Endpoint;
import com.vmware.gemfire.driver.spi.UnsupportedFeatureException;

/** Native characterization of default-pool assembly before cache creation and lazy selection. */
class NativeDefaultPoolTests {
	private final Map<String, String> PROPERTIES = Map.of("mcast-port", "0", "locators", "", "log-level", "error");
	private final PoolConfiguration registered = new PoolConfiguration(List.of(new Endpoint("127.0.0.1", 40404)), List.of(), 0);

	@Test
	void explicitSettingsAreAppliedBeforeCreation() {
		var explicit = PoolConfiguration.builder().servers(List.of(new Endpoint("127.0.0.1", 40405))).minimumConnections(0).readTimeout(12345).build();
		try (DriverConnection connection = new GemFire104Driver().connect(new CacheConfiguration(PROPERTIES, explicit))) {
			var pool = connection.getDefaultPool();
			assertThat(pool.servers()).containsExactly(new Endpoint("127.0.0.1", 40405));
			assertThat(pool.minimumConnections()).isZero();
			assertThat(pool.configuration().readTimeout()).hasValue(12345);
			assertThat(PoolManager.find(pool.name())).isNotNull();
		}
	}

	@Test
	void explicitPoolWithoutEndpointsInheritsNativeLocalServer() {
		var explicit = PoolConfiguration.builder().minimumConnections(0).build();
		try (DriverConnection connection = new GemFire104Driver().connect(new CacheConfiguration(PROPERTIES, explicit))) {
			var pool = connection.getDefaultPool();
			assertThat(pool.locators()).isEmpty();
			assertThat(pool.servers()).hasSize(1).allSatisfy(server -> assertThat(server.port()).isEqualTo(40404));
		}
	}

	@Test
	void absentConfigurationOnEmptyRegistryCreatesLocalServerPool() {
		try (DriverConnection connection = new GemFire104Driver().connect(new CacheConfiguration(PROPERTIES))) {
			var pool = connection.getDefaultPool();
			assertThat(pool.locators()).isEmpty();
			assertThat(pool.servers()).hasSize(1).allSatisfy(server -> assertThat(server.port()).isEqualTo(40404));
			assertThat(connection.getDefaultPool().name()).isEqualTo(pool.name());
		}
	}

	@Test
	void singleRegisteredPoolIsSelectedWithoutRelyingOnItsName() {
		try (DriverConnection connection = new GemFire104Driver().connect(new CacheConfiguration(PROPERTIES))) {
			String name = UUID.randomUUID().toString();
			var pool = connection.createPool(name, registered);
			try { assertThat(connection.getDefaultPool().name()).isEqualTo(name); }
			finally { pool.destroy(); }
		}
	}

	@Test
	void severalIncompatiblePoolsLeaveImplicitDefaultNull() {
		try (DriverConnection connection = new GemFire104Driver().connect(new CacheConfiguration(PROPERTIES))) {
			var first = connection.createPool(UUID.randomUUID().toString(), registered);
			var second = connection.createPool(UUID.randomUUID().toString(), registered);
			try { assertThat(connection.getDefaultPool()).isNull(); }
			finally { first.destroy(); second.destroy(); }
		}
	}

	@Test
	void threadLocalConnectionsIsRejectedBeforeCacheCreation() {
		var explicit = PoolConfiguration.builder().threadLocalConnections(true).build();
		assertThatThrownBy(() -> new GemFire104Driver().connect(new CacheConfiguration(PROPERTIES, explicit)))
			.isInstanceOf(UnsupportedFeatureException.class);
	}

	@Test
	void explicitLocatorsOnlyDoNotAddALocalServer() {
		var explicit = PoolConfiguration.builder().locators(List.of(new Endpoint("127.0.0.1", 10334))).minimumConnections(0).build();
		try (DriverConnection connection = new GemFire104Driver().connect(new CacheConfiguration(PROPERTIES, explicit))) {
			var pool = connection.getDefaultPool();
			assertThat(pool.locators()).containsExactly(new Endpoint("127.0.0.1", 10334));
			assertThat(pool.servers()).isEmpty();
		}
	}

	@Test
	void generatedDefaultNameIsNotTheSelectionKey() {
		var explicit = PoolConfiguration.builder().servers(List.of(new Endpoint("127.0.0.1", 40405))).minimumConnections(0).build();
		try (DriverConnection connection = new GemFire104Driver().connect(new CacheConfiguration(PROPERTIES, explicit))) {
			var pool = connection.getDefaultPool();
			assertThat(pool.name()).matches("DEFAULT\\d*");
			assertThat(connection.getPool(pool.name()).name()).isEqualTo(pool.name());
		}
	}

	@Test
	void socketFactoryCallbackIdentityIsPreserved() {
		DriverSocketFactory callback = java.net.Socket::new;
		var explicit = PoolConfiguration.builder().servers(List.of(new Endpoint("127.0.0.1", 40405)))
			.minimumConnections(0).socketFactory(callback).build();
		try (DriverConnection connection = new GemFire104Driver().connect(new CacheConfiguration(PROPERTIES, explicit))) {
			assertThat(connection.getDefaultPool().configuration().socketFactory()).containsSame(callback);
		}
	}

	@Test
	void compatibleExplicitConfigurationReusesExistingCacheAndDefaultPool() {
		var explicit = PoolConfiguration.builder().servers(List.of(new Endpoint("127.0.0.1", 40405))).minimumConnections(0).build();
		try (DriverConnection first = new GemFire104Driver().connect(new CacheConfiguration(PROPERTIES, explicit))) {
			String name = first.getDefaultPool().name();
			DriverConnection second = new GemFire104Driver().connect(new CacheConfiguration(PROPERTIES, explicit));
			assertThat(second.getDefaultPool().name()).isEqualTo(name);
		}
	}

	@Test
	void incompatibleExplicitConfigurationConflictsWithExistingCache() {
		var explicit = PoolConfiguration.builder().servers(List.of(new Endpoint("127.0.0.1", 40405))).minimumConnections(0).build();
		var other = PoolConfiguration.builder().servers(List.of(new Endpoint("127.0.0.1", 40406))).minimumConnections(0).build();
		try (DriverConnection first = new GemFire104Driver().connect(new CacheConfiguration(PROPERTIES, explicit))) {
			first.getDefaultPool();
			assertThatThrownBy(() -> new GemFire104Driver().connect(new CacheConfiguration(PROPERTIES, other)))
				.isInstanceOf(DriverBindException.class)
				.hasRootCauseInstanceOf(IllegalStateException.class);
		}
	}
}
