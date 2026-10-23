/*
 * Copyright 2022-2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.cache;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.vmware.gemfire.driver.core.GemFireDriverManager;
import com.vmware.gemfire.driver.spi.CacheConfiguration;
import com.vmware.gemfire.driver.spi.DriverConnection;
import com.vmware.gemfire.driver.spi.DriverRegion;
import com.vmware.gemfire.driver.spi.config.ClientRegionShortcut;

class GemfireCacheManagerUnitTests {

	private DriverConnection connection;
	private GemfireCacheManager manager;

	@BeforeEach
	void createConnection() {
		connection = new GemFireDriverManager().driver().connect(
			new CacheConfiguration(Map.of()));
		manager = new GemfireCacheManager(connection);
	}

	@AfterEach
	void closeConnection() { connection.close(); }

	private DriverRegion<?, ?> createRegion(String name) {
		return connection.createClientRegion(name, ClientRegionShortcut.LOCAL);
	}

	@Test
	void connectionIsRequiredAndBorrowed() {
		assertThatThrownBy(() -> new GemfireCacheManager(null)).isInstanceOf(IllegalArgumentException.class);
		assertThat(manager.getCache()).isSameAs(connection);
		manager.afterPropertiesSet();
		assertThat(connection.isClosed()).isFalse();
	}

	@Test
	void discoversRootRegionsAndRegionsCreatedLater() {
		createRegion("one");
		manager.afterPropertiesSet();
		assertThat(manager.getCacheNames()).containsExactly("one");
		DriverRegion<?, ?> later = createRegion("later");
		assertThat(manager.getCache("later").getName()).isEqualTo(later.name());
		assertThat(manager.getCacheNames()).containsExactlyInAnyOrder("one", "later");
		assertThat(manager.isDynamic()).isTrue();
	}

	@Test
	void explicitRegionsTakePrecedenceOverNamesAndDisableDynamicLookup() {
		DriverRegion<?, ?> selected = createRegion("selected");
		createRegion("other");
		manager.setRegions(Set.of(selected));
		manager.setCacheNames(Set.of("missing"));
		manager.afterPropertiesSet();
		assertThat(manager.getCacheNames()).containsExactly("selected");
		assertThat(manager.getCache("selected").getNativeCache()).isSameAs(selected);
		assertThat(manager.getCache("other")).isNull();
		assertThat(manager.isDynamic()).isFalse();
	}

	@Test
	void explicitNamesSelectOnlyThoseRegions() {
		createRegion("one");
		createRegion("two");
		manager.setCacheNames(Set.of("two"));
		manager.afterPropertiesSet();
		assertThat(manager.getCacheNames()).containsExactly("two");
		assertThat(manager.getCache("one")).isNull();
	}

	@Test
	void emptySelectionsRetainDynamicLookup() {
		manager.setRegions(Set.of());
		manager.setCacheNames(Set.of());
		manager.afterPropertiesSet();
		createRegion("later");
		assertThat(manager.getCache("later")).isNotNull();
	}

	@Test
	void missingExplicitAndDynamicRegionsFailDescriptively() {
		manager.setCacheNames(Set.of("missing"));
		assertThatThrownBy(manager::afterPropertiesSet).isInstanceOf(IllegalStateException.class)
			.hasMessage("No Region for cache name [missing] was found");
		manager.setCacheNames(null);
		manager.afterPropertiesSet();
		assertThatThrownBy(() -> manager.getCache("missing")).isInstanceOf(IllegalStateException.class)
			.hasMessage("No Region for cache name [missing] was found");
	}

	@Test
	void closedConnectionsFailDuringInitializationAndDynamicLookup() {
		manager.afterPropertiesSet();
		connection.close();
		assertThatThrownBy(manager::afterPropertiesSet).isInstanceOf(IllegalStateException.class)
			.hasMessageContaining("closed");
		assertThatThrownBy(() -> manager.getCache("missing")).isInstanceOf(IllegalStateException.class);
	}

	@Test
	void rejectsAnExplicitRegionFromAClosedConnection() {
		DriverRegion<Object, Object> destroyed = new DriverRegion<>() {
			@Override public String name() { return "destroyed"; }
			@Override public boolean isDestroyed() { return true; }
		};
		manager.setRegions(Set.of(destroyed));
		assertThatThrownBy(manager::afterPropertiesSet).isInstanceOf(IllegalStateException.class)
			.hasMessageContaining("destroyed");
	}
}
