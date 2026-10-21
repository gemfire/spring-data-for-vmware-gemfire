/*
 * Copyright 2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package com.vmware.gemfire.driver.gemfire104;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.apache.geode.cache.client.ClientCacheFactory;
import org.apache.geode.cache.client.ClientRegionShortcut;
import org.apache.geode.cache.client.PoolFactory;
import org.apache.geode.cache.client.PoolManager;
import com.vmware.gemfire.driver.spi.CacheConfiguration;
import com.vmware.gemfire.driver.spi.DriverConnection;
import com.vmware.gemfire.driver.spi.DriverOperationException;
import com.vmware.gemfire.driver.spi.PoolConfiguration;
import com.vmware.gemfire.driver.spi.PoolConfiguration.Endpoint;

/** Native registry and attachment behavior is deliberately outside mock isolation claims. */
class NativePoolTests {
	private final CacheConfiguration cache = new CacheConfiguration(CacheConfiguration.Mode.CLIENT,
		Map.of("mcast-port", "0", "locators", "", "log-level", "error"));
	private final PoolConfiguration config = new PoolConfiguration(List.of(new Endpoint("127.0.0.1", 40404)), List.of(), 0);

	@Test
	void compatibleConnectionsSeeTheSameRegistryAndDuplicatesPreserveTheOriginal() {
		var driver = new GemFire104Driver();
		try (DriverConnection first = driver.connect(cache); DriverConnection second = driver.connect(cache)) {
			String name = UUID.randomUUID().toString();
			var pool = first.createPool(name, config);
			try {
				assertThat(second.getPool(name).name()).isEqualTo(name);
				assertThatThrownBy(() -> second.createPool(name, config)).isInstanceOf(DriverOperationException.class)
					.hasCauseInstanceOf(IllegalStateException.class)
					.satisfies(failure -> assertThat(((DriverOperationException) failure).kind()).isEqualTo(com.vmware.gemfire.driver.spi.DriverFailureKind.INVALID_STATE));
				assertThat(pool.isDestroyed()).isFalse();
				assertThat(PoolManager.find(name).isDestroyed()).isFalse();
			}
			finally { pool.destroy(); }
			assertThat(second.getPool(name)).isNull();
		}
	}

	@Test
	void nativeFactoryRejectsMixedEndpointKindsInEitherOrder() {
		assertThatThrownBy(() -> PoolManager.createFactory().addServer("127.0.0.1", 40404).addLocator("127.0.0.1", 10334))
			.isInstanceOf(IllegalStateException.class);
		assertThatThrownBy(() -> PoolManager.createFactory().addLocator("127.0.0.1", 10334).addServer("127.0.0.1", 40404))
			.isInstanceOf(IllegalStateException.class);
	}

	@Test
	void unsetMinimumRetainsNativeDefault() {
		try (DriverConnection connection = new GemFire104Driver().connect(cache)) {
			var pool = connection.createPool(UUID.randomUUID().toString(),
				new PoolConfiguration(config.servers(), List.of(), null));
			try { assertThat(pool.minimumConnections()).isEqualTo(PoolFactory.DEFAULT_MIN_CONNECTIONS); }
			finally { pool.destroy(); }
		}
	}

	@Test
	void attachedProxyRegionRejectsDestructionUntilDetached() {
		try (var nativeCache = new ClientCacheFactory(cache.properties()).create()) {
			DriverConnection connection = new GemFireConnection(nativeCache, CacheConfiguration.Mode.CLIENT);
			var pool = connection.createPool(UUID.randomUUID().toString(), config);
			try {
				var region = nativeCache.createClientRegionFactory(ClientRegionShortcut.PROXY)
					.setPoolName(pool.name()).create("attached");
				try {
					assertThatThrownBy(pool::destroy).isInstanceOf(DriverOperationException.class)
						.hasCauseInstanceOf(IllegalStateException.class)
					.satisfies(failure -> assertThat(((DriverOperationException) failure).kind()).isEqualTo(com.vmware.gemfire.driver.spi.DriverFailureKind.INVALID_STATE));
					assertThat(pool.isDestroyed()).isFalse();
					assertThat(connection.getPool(pool.name())).isNotNull();
				}
				finally { region.localDestroyRegion(); }
			}
			finally { pool.destroy(); }
			assertThat(connection.getPool(pool.name())).isNull();
		}
	}
}
