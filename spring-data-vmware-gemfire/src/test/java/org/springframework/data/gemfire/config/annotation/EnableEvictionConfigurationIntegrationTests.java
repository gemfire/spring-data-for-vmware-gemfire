/*
 * Copyright 2022-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.config.annotation;

import static org.assertj.core.api.Assertions.assertThat;
import org.apache.geode.cache.DataPolicy;
import org.apache.geode.cache.Region;
import org.apache.geode.cache.client.ClientRegionShortcut;
import org.junit.After;
import org.junit.Test;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.data.gemfire.eviction.EvictionActionType;
import org.springframework.data.gemfire.eviction.EvictionPolicyType;
import org.springframework.data.gemfire.test.model.Person;
import org.springframework.data.gemfire.tests.integration.SpringApplicationContextIntegrationTestsSupport;
import org.springframework.data.gemfire.tests.mock.annotation.EnableGemFireMockObjects;
import org.springframework.stereotype.Service;

/**
 * Integration Tests for {@link EnableEviction} and {@link EvictionConfiguration}.
 *
 * @author John Blum
 * @see org.apache.geode.cache.Region
 * @see org.springframework.data.gemfire.config.annotation.EnableEviction
 * @see org.springframework.data.gemfire.config.annotation.EvictionConfiguration
 * @see org.springframework.data.gemfire.tests.integration.SpringApplicationContextIntegrationTestsSupport
 * @see org.springframework.data.gemfire.tests.mock.annotation.EnableGemFireMockObjects
 * @since 1.9.0
 */
@SuppressWarnings("unused")
public class EnableEvictionConfigurationIntegrationTests extends SpringApplicationContextIntegrationTestsSupport {

	private static final String GEMFIRE_LOG_LEVEL = "error";

	@After
	public void cleanupAfterTests() {
		destroyAllGemFireMockObjects();
	}

	@SuppressWarnings("unchecked")
	private <K, V> Region<K, V> getRegion(ConfigurableApplicationContext applicationContext, String beanName) {
		return applicationContext.getBean(beanName, Region.class);
	}

	private void assertRegionEvictionConfiguration(ConfigurableApplicationContext applicationContext,
			String regionBeanName, EvictionActionType expectedEvictionActionType, int expectedEvictionMaximum) {

		Region<?, ?> region = getRegion(applicationContext, regionBeanName);

		assertThat(region).isNotNull();
		assertThat(region.getName()).isEqualTo(regionBeanName);
		assertThat(region.getAttributes()).isNotNull();
		assertThat(region.getAttributes().getDataPolicy()).isEqualTo(DataPolicy.NORMAL);
		assertThat(region.getAttributes().getEvictionAttributes()).isNotNull();

		assertThat(region.getAttributes().getEvictionAttributes().getAction())
			.isEqualTo(expectedEvictionActionType.getEvictionAction());

		assertThat(region.getAttributes().getEvictionAttributes().getAlgorithm())
			.isEqualTo(EvictionPolicyType.ENTRY_COUNT.getEvictionAlgorithm());

		assertThat(region.getAttributes().getEvictionAttributes().getMaximum()).isEqualTo(expectedEvictionMaximum);
	}

	@Test
	public void assertApplicationCachingDefinedRegionsEvictionPolicyIsCorrect() {

		ConfigurableApplicationContext applicationContext = newApplicationContext(ApplicationConfiguration.class);

		assertRegionEvictionConfiguration(applicationContext, "CacheOne",
			EvictionActionType.LOCAL_DESTROY, 100);

		assertRegionEvictionConfiguration(applicationContext, "CacheTwo",
			EvictionActionType.LOCAL_DESTROY, 100);
	}

	@Test
	public void assertClientCacheRegionEvictionPolicyIsCorrect() {
		assertRegionEvictionConfiguration(newApplicationContext(ClientCacheRegionEvictionConfiguration.class),
			"People", EvictionActionType.LOCAL_DESTROY, 100);
	}

	@ClientCacheApplication(name = "EnableEvictionConfigurationIntegrationTests")
	@EnableCachingDefinedRegions(clientRegionShortcut = ClientRegionShortcut.LOCAL)
	@EnableEviction(policies = @EnableEviction.EvictionPolicy(maximum = 100))
	@EnableGemFireMockObjects
	static class ApplicationConfiguration {

		@Bean
		ApplicationService applicationService() {
			return new ApplicationService();
		}
	}

	@Service
	static class ApplicationService {

		@Cacheable("CacheOne")
		public Object someMethod(Object key) {
			return null;
		}

		@Cacheable("CacheTwo")
		public Object someOtherMethod(Object key) {
			return null;
		}
	}

	@ClientCacheApplication(name = "EnableEvictionConfigurationIntegrationTests", logLevel = GEMFIRE_LOG_LEVEL)
	@EnableEntityDefinedRegions(basePackageClasses = Person.class, clientRegionShortcut = ClientRegionShortcut.LOCAL)
	@EnableEviction(policies = @EnableEviction.EvictionPolicy(regionNames = "People", maximum = 100))
	@EnableGemFireMockObjects
	static class ClientCacheRegionEvictionConfiguration { }

}
