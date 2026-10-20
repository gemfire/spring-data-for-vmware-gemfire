/*
 * Copyright 2022-2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.cache;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/** Verifies XML constructor injection of a driver connection into the cache manager. */
class ClientCacheManagerIntegrationTests {

	@Test
	void cacheManagerUsesConfiguredDriverRegion() {
		try (var context = new ClassPathXmlApplicationContext(
			"org/springframework/data/gemfire/cache/ClientCacheManagerIntegrationTests-context.xml")) {
			GemfireCacheManager manager = context.getBean(GemfireCacheManager.class);
			assertThat(manager.getCache("Example")).isNotNull();
			assertThat(manager.getCacheNames()).containsExactly("Example");
		}
	}
}
