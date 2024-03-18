/*
 * Copyright (c) VMware, Inc. 2022-2024. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire;

import java.io.File;

import org.apache.geode.cache.Cache;
import org.assertj.core.api.Assertions;
import org.junit.Test;
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

		Assertions.assertThat(cache).isNotNull();
		Assertions.assertThat(cache.getDistributedSystem()).isNotNull();
		Assertions.assertThat(cache.getDistributedSystem().getProperties()).isNotNull();
		Assertions.assertThat(Boolean.valueOf(cache.getDistributedSystem().getProperties().getProperty("disable-auto-reconnect")))
			.isTrue();
	}

	@Test
	public void autoReconnectIsEnabled() {

		Cache cache = getCache("cacheAutoReconnectEnabledIntegrationTests.xml");

		Assertions.assertThat(cache).isNotNull();
		Assertions.assertThat(cache.getDistributedSystem()).isNotNull();
		Assertions.assertThat(cache.getDistributedSystem().getProperties()).isNotNull();
		Assertions.assertThat(Boolean.valueOf(cache.getDistributedSystem().getProperties().getProperty("disable-auto-reconnect")))
			.isFalse();
	}
}
