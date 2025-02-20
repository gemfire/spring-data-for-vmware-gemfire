/*
 * Copyright 2022-2025 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.config.xml;

import org.apache.geode.cache.Region;
import org.apache.geode.cache.client.ClientCache;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.data.gemfire.tests.integration.IntegrationTestsSupport;
import org.springframework.data.gemfire.util.SpringExtensions;

/**
 * Integration Tests for multiple Apache Geode {@link ClientCache caches}.
 *
 * @author David Turanski
 * @author John Blum
 * @see org.junit.Test
 * @see org.apache.geode.cache.client.ClientCache
 * @see org.apache.geode.cache.Region
 * @see org.springframework.context.ConfigurableApplicationContext
 * @see org.springframework.context.support.ClassPathXmlApplicationContext
 * @see org.springframework.data.gemfire.tests.integration.IntegrationTestsSupport
 */

public class MultipleCacheIntegrationTests extends IntegrationTestsSupport {

	@Test
	public void testMultipleCaches() {

		String configLocation = IntegrationTestsSupport.getContextXmlFileLocation(MultipleCacheIntegrationTests.class);

		ConfigurableApplicationContext applicationContextOne = null;
		ConfigurableApplicationContext applicationContextTwo = null;

		try {

			applicationContextOne = new ClassPathXmlApplicationContext(configLocation);
			applicationContextTwo = new ClassPathXmlApplicationContext(configLocation);

			ClientCache cacheOne = applicationContextOne.getBean(ClientCache.class);
			ClientCache cacheTwo = applicationContextTwo.getBean(ClientCache.class);

			Assertions.assertThat(cacheOne).isNotNull();
			Assertions.assertThat(cacheTwo).isSameAs(cacheOne);

			Region<?, ?> regionOne = applicationContextOne.getBean(Region.class);
			Region<?, ?> regionTwo = applicationContextTwo.getBean(Region.class);

			Assertions.assertThat(regionOne).isNotNull();
			Assertions.assertThat(regionTwo).isSameAs(regionOne);
			Assertions.assertThat(cacheOne.isClosed()).isFalse();
			Assertions.assertThat(regionOne.isDestroyed()).isFalse();

			applicationContextOne.close();

			Assertions.assertThat(cacheOne.isClosed()).isFalse();
			Assertions.assertThat(regionOne.isDestroyed()).describedAs("Region was destroyed").isFalse();
		}
		finally {

			final ConfigurableApplicationContext applicationContextOneRef = applicationContextOne;
			final ConfigurableApplicationContext applicationContextTwoRef = applicationContextTwo;

			SpringExtensions.safeDoOperation(() -> IntegrationTestsSupport.closeApplicationContext(applicationContextOneRef));
			SpringExtensions.safeDoOperation(() -> IntegrationTestsSupport.closeApplicationContext(applicationContextTwoRef));
		}
	}
}
