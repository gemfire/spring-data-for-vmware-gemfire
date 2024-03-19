/*
 * Copyright 2022-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.config.xml;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

import org.apache.geode.cache.Cache;
import org.apache.geode.cache.GemFireCache;
import org.apache.geode.cache.Region;

import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.data.gemfire.tests.integration.IntegrationTestsSupport;
import org.springframework.data.gemfire.util.SpringExtensions;

/**
 * Integration Tests for multiple Apache Geode {@link GemFireCache caches}.
 *
 * @author David Turanski
 * @author John Blum
 * @see org.junit.Test
 * @see Cache
 * @see GemFireCache
 * @see Region
 * @see ConfigurableApplicationContext
 * @see ClassPathXmlApplicationContext
 * @see IntegrationTestsSupport
 */

public class MultipleCacheIntegrationTests extends IntegrationTestsSupport {

	@Test
	public void testMultipleCaches() {

		String configLocation = getContextXmlFileLocation(MultipleCacheIntegrationTests.class);

		ConfigurableApplicationContext applicationContextOne = null;
		ConfigurableApplicationContext applicationContextTwo = null;

		try {

			applicationContextOne = new ClassPathXmlApplicationContext(configLocation);
			applicationContextTwo = new ClassPathXmlApplicationContext(configLocation);

			Cache cacheOne = applicationContextOne.getBean(Cache.class);
			Cache cacheTwo = applicationContextTwo.getBean(Cache.class);

			assertThat(cacheOne).isNotNull();
			assertThat(cacheTwo).isSameAs(cacheOne);

			Region<?, ?> regionOne = applicationContextOne.getBean(Region.class);
			Region<?, ?> regionTwo = applicationContextTwo.getBean(Region.class);

			assertThat(regionOne).isNotNull();
			assertThat(regionTwo).isSameAs(regionOne);
			assertThat(cacheOne.isClosed()).isFalse();
			assertThat(regionOne.isDestroyed()).isFalse();

			applicationContextOne.close();

			assertThat(cacheOne.isClosed()).isFalse();
			assertThat(regionOne.isDestroyed()).describedAs("Region was destroyed").isFalse();
		}
		finally {

			final ConfigurableApplicationContext applicationContextOneRef = applicationContextOne;
			final ConfigurableApplicationContext applicationContextTwoRef = applicationContextTwo;

			SpringExtensions.safeDoOperation(() -> closeApplicationContext(applicationContextOneRef));
			SpringExtensions.safeDoOperation(() -> closeApplicationContext(applicationContextTwoRef));
		}
	}
}
