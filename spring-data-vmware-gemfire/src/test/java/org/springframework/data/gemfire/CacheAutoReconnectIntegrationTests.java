/*
 * Copyright 2022-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;

import org.junit.Test;

import org.apache.geode.cache.Cache;

import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.data.gemfire.config.xml.GemfireConstants;
import org.springframework.data.gemfire.tests.integration.SpringApplicationContextIntegrationTestsSupport;

/**
 * Integration Tests testing SDG support of Apache Geode Auto-Reconnect functionality.
 *
 * @author John Blum
 * @see org.junit.Test
 * @see org.apache.geode.cache.Cache
 * @see org.springframework.data.gemfire.CacheFactoryBean
 * @see org.springframework.data.gemfire.tests.integration.SpringApplicationContextIntegrationTestsSupport
 * @since 1.5.0
 */
public class CacheAutoReconnectIntegrationTests extends SpringApplicationContextIntegrationTestsSupport {

	protected Cache getCache(String configLocation) {

		String baseConfigLocation =
			File.separator.concat(getClass().getPackage().getName().replace('.', File.separatorChar));

		String resolvedConfigLocation = baseConfigLocation.concat(File.separator).concat(configLocation);

		setApplicationContext(new ClassPathXmlApplicationContext(resolvedConfigLocation));

		return getBean(GemfireConstants.DEFAULT_GEMFIRE_CACHE_NAME, Cache.class);
	}

	@Test
	public void autoReconnectIsDisabled() {

		Cache cache = getCache("cacheAutoReconnectDisabledIntegrationTests.xml");

		assertThat(cache).isNotNull();
		assertThat(cache.getDistributedSystem()).isNotNull();
		assertThat(cache.getDistributedSystem().getProperties()).isNotNull();
		assertThat(Boolean.valueOf(cache.getDistributedSystem().getProperties().getProperty("disable-auto-reconnect")))
			.isTrue();
	}

	@Test
	public void autoReconnectIsEnabled() {

		Cache cache = getCache("cacheAutoReconnectEnabledIntegrationTests.xml");

		assertThat(cache).isNotNull();
		assertThat(cache.getDistributedSystem()).isNotNull();
		assertThat(cache.getDistributedSystem().getProperties()).isNotNull();
		assertThat(Boolean.valueOf(cache.getDistributedSystem().getProperties().getProperty("disable-auto-reconnect")))
			.isFalse();
	}
}
