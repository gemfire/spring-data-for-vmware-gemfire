/*
 * Copyright (c) VMware, Inc. 2022-2024. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.cache.config;

import org.apache.geode.cache.GemFireCache;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.gemfire.cache.GemfireCacheManager;

/**
 * The {@link GemfireCachingConfiguration} class is a Spring {@link Configuration @Configuration} class
 * used to configure Pivotal GemFire or Apache Geode as the caching provider in Spring's Cache Abstraction.
 *
 * This {@link Configuration @Configuration} class is specifically responsible for declaring and registering
 * Spring Data GemFire/Geode's {@link GemfireCacheManager} implementation to properly enable either Pivotal GemFire
 * or Apache Geode as the caching provider used with Springs Cache Abstraction.
 *
 * Additionally, this Spring {@link Configuration @Configuration} class also enables the Spring Cache Abstraction
 * by declaring Spring's {@link EnableCaching} annotation for the user extending or importing this class using
 * the SDG provided {@link EnableGemfireCaching} annotation.
 *
 * @author John Blum
 * @see GemFireCache
 * @see EnableCaching
 * @see Bean
 * @see Configuration
 * @see GemfireCacheManager
 * @see EnableGemfireCaching
 * @see <a href="https://docs.spring.io/spring-framework/docs/current/reference/html/integration.html#cache">Cache Abstraction</a>
 * @see <a href="https://docs.spring.io/spring-framework/docs/current/reference/html/integration.html#cache-store-configuration-gemfire">GemFire-based Cache</a>
 * @see <a href="https://docs.spring.io/spring-data-gemfire/docs/current/reference/html/#apis:spring-cache-abstraction">Support for Spring Cache Abstraction</a>
 * @since 2.0.0
 */
@Configuration
@EnableCaching
@SuppressWarnings("unused")
public class GemfireCachingConfiguration {

	/**
	 * SDG's {@link GemfireCacheManager} used to position Pivotal GemFire or Apache Geode as the caching provider
	 * in Spring's Cache Abstraction.
	 *
	 * @return an instance of {@link GemfireCacheManager}.
	 * @see GemfireCacheManager
	 * @see GemFireCache
	 */
	@Bean
	public GemfireCacheManager cacheManager(GemFireCache gemfireCache) {

		GemfireCacheManager gemfireCacheManager = new GemfireCacheManager();

		gemfireCacheManager.setCache(gemfireCache);

		return gemfireCacheManager;
	}
}
