/*
 * Copyright 2022-2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.cache;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.Test;

import com.vmware.gemfire.driver.spi.DriverOperationException;
import com.vmware.gemfire.driver.spi.DriverRegion;

import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CachingConfigurer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.CacheErrorHandler;
import org.springframework.cache.interceptor.SimpleCacheErrorHandler;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Configuration;

/** Verifies that an application's error handler sees SPI failures from the migrated adapter. */
class CacheErrorHandlerIntegrationTests {

	@Test
	void applicationCanHandleDriverFailuresDuringCacheGetAndPut() {
		DriverRegion<Object, Object> failedRegion = new DriverRegion<>() {
			@Override public String name() { return "TestCache"; }
			@Override public Object get(Object key) { throw new DriverOperationException("Unavailable", null); }
			@Override public Object put(Object key, Object value) { throw new DriverOperationException("Unavailable", null); }
		};
		try (var context = new AnnotationConfigApplicationContext()) {
			context.registerBean(CacheManager.class, () -> {
				SimpleCacheManager manager = new SimpleCacheManager();
				manager.setCaches(List.of(new GemfireCache(failedRegion)));
				return manager;
			});
			context.register(ErrorConfiguration.class, TestService.class);
			context.refresh();
			assertThat(context.getBean(TestService.class).process("key")).isEqualTo("TEST");
			assertThat(context.getBean(ErrorConfiguration.class).handler.failures.get()).isEqualTo(2);
		}
	}

	@Configuration(proxyBeanMethods = false)
	@EnableCaching
	public static class ErrorConfiguration implements CachingConfigurer {
		private final TestErrorHandler handler = new TestErrorHandler();
		@Override public CacheErrorHandler errorHandler() { return handler; }
	}

	public static class TestService {
		@Cacheable("TestCache")
		public String process(String key) { return "TEST"; }
	}

	private static class TestErrorHandler extends SimpleCacheErrorHandler {
		private final AtomicInteger failures = new AtomicInteger();

		@Override
		public void handleCacheGetError(RuntimeException ex, Cache cache, Object key) { handle(ex); }

		@Override
		public void handleCachePutError(RuntimeException ex, Cache cache, Object key, Object value) { handle(ex); }

		private void handle(RuntimeException ex) {
			if (!(ex instanceof DriverOperationException)) throw ex;
			failures.incrementAndGet();
		}
	}
}
