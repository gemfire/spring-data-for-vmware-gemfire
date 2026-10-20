/*
 * Copyright 2022-2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.cache.config;

import com.vmware.gemfire.driver.spi.DriverConnection;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.gemfire.cache.GemfireCacheManager;

/**
 * Enables Spring caching backed by a Driver SPI connection supplied by the application. Obtain that
 * connection from an injected GemFireDriverManager; its owner is responsible for closing it.
 */
@Configuration(proxyBeanMethods = false)
@EnableCaching
public class GemfireCachingConfiguration {

	@Bean
	public GemfireCacheManager cacheManager(DriverConnection gemfireCache) {
		return new GemfireCacheManager(gemfireCache);
	}
}
