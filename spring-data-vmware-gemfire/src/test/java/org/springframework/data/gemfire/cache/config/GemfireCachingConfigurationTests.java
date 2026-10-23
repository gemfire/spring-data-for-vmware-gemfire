/*
 * Copyright 2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.cache.config;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.Test;

import com.vmware.gemfire.driver.core.GemFireDriverManager;
import com.vmware.gemfire.driver.spi.CacheConfiguration;
import com.vmware.gemfire.driver.spi.DriverConnection;
import com.vmware.gemfire.driver.spi.config.ClientRegionShortcut;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.gemfire.cache.GemfireCacheManager;

class GemfireCachingConfigurationTests {

	@Test
	void annotationsUseTheInjectedDriverConnectionAndTheContextOwnsItsLifecycle() {
		DriverConnection connection = new GemFireDriverManager().driver().connect(
			new CacheConfiguration(Map.of()));
		try (connection; var context = new AnnotationConfigApplicationContext()) {
			connection.createClientRegion("numbers", ClientRegionShortcut.LOCAL);
			context.registerBean(DriverConnection.class, () -> connection, bd -> bd.setDestroyMethodName("close"));
			context.registerBean(NumberService.class);
			context.register(CachingConfiguration.class);
			context.refresh();
			NumberService service = context.getBean(NumberService.class);
			assertThat(context.getBean(GemfireCacheManager.class).getCacheNames()).containsExactly("numbers");
			assertThat(service.load("key")).isEqualTo(1);
			assertThat(service.load("key")).isEqualTo(1);
			assertThat(service.loads()).isEqualTo(1);
			service.replace("key", 42);
			assertThat(service.load("key")).isEqualTo(42);
			service.evict("key");
			assertThat(service.load("key")).isEqualTo(2);
			service.clear();
			assertThat(connection.getRegion("numbers").size()).isZero();
			context.close();
			assertThat(connection.isClosed()).isTrue();
		}
	}

	@Configuration(proxyBeanMethods = false)
	@EnableGemfireCaching
	public static class CachingConfiguration { }

	public static class NumberService {
		private final AtomicInteger loads = new AtomicInteger();

		@Cacheable(cacheNames = "numbers", key = "#p0", sync = true)
		public int load(String key) { return loads.incrementAndGet(); }

		@CachePut(cacheNames = "numbers", key = "#p0")
		public int replace(String key, int value) { return value; }

		@CacheEvict(cacheNames = "numbers", key = "#p0")
		public void evict(String key) { }

		@CacheEvict(cacheNames = "numbers", allEntries = true)
		public void clear() { }

		public int loads() { return loads.get(); }
	}
}
