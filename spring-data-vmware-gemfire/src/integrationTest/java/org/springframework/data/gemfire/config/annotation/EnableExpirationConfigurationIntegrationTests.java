/*
 * Copyright 2022-2025 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.config.annotation;

import org.apache.geode.cache.DataPolicy;
import org.apache.geode.cache.Region;
import org.apache.geode.cache.client.ClientRegionShortcut;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.data.gemfire.expiration.AnnotationBasedExpiration;
import org.springframework.data.gemfire.test.model.Person;
import org.springframework.data.gemfire.tests.integration.SpringApplicationContextIntegrationTestsSupport;
import org.springframework.data.gemfire.tests.mock.annotation.EnableGemFireMockObjects;
import org.springframework.stereotype.Service;

/**
 * Integration Tests for {@link EnableExpiration} and {@link ExpirationConfiguration}.
 *
 * @author John Blum
 * @see org.junit.Test
 * @see org.apache.geode.cache.Region
 * @see org.springframework.context.ConfigurableApplicationContext
 * @see org.springframework.context.annotation.AnnotationConfigApplicationContext
 * @see org.springframework.data.gemfire.config.annotation.EnableExpiration
 * @see org.springframework.data.gemfire.config.annotation.ExpirationConfiguration
 * @see org.springframework.data.gemfire.expiration.AnnotationBasedExpiration
 * @see org.springframework.data.gemfire.tests.integration.SpringApplicationContextIntegrationTestsSupport
 * @see org.springframework.data.gemfire.tests.mock.annotation.EnableGemFireMockObjects
 * @since 1.9.0
 */
@SuppressWarnings("unused")
public class EnableExpirationConfigurationIntegrationTests extends SpringApplicationContextIntegrationTestsSupport {

	private void assertRegionExpirationConfiguration(ConfigurableApplicationContext applicationContext,
			String regionBeanName) {

		Region<?, ?> region = getRegion(applicationContext, regionBeanName);

		Assertions.assertThat(region).isNotNull();

		Assertions.assertThat(region.getName()).isEqualTo(regionBeanName);
		Assertions.assertThat(region.getAttributes()).isNotNull();
		Assertions.assertThat(region.getAttributes().getDataPolicy()).isEqualTo(DataPolicy.NORMAL);
		Assertions.assertThat(region.getAttributes().getStatisticsEnabled()).isTrue();
		Assertions.assertThat(region.getAttributes().getCustomEntryIdleTimeout())
			.isInstanceOf(AnnotationBasedExpiration.class);
		Assertions.assertThat(region.getAttributes().getCustomEntryTimeToLive())
			.isInstanceOf(AnnotationBasedExpiration.class);
	}

	@SuppressWarnings("unchecked")
	private <K, V> Region<K, V> getRegion(ConfigurableApplicationContext applicationContext, String beanName) {
		return applicationContext.getBean(beanName, Region.class);
	}

	@Test
	public void assertApplicationCachingDefinedRegionsExpirationPoliciesAreCorrect() {

		ConfigurableApplicationContext applicationContext = newApplicationContext(ApplicationConfiguration.class);

		Assertions.assertThat(applicationContext).isNotNull();
		assertRegionExpirationConfiguration(applicationContext, "CacheOne");
		assertRegionExpirationConfiguration(applicationContext, "CacheTwo");
	}

	@Test
	public void assertClientCacheRegionExpirationPoliciesAreCorrect() {
		assertRegionExpirationConfiguration(newApplicationContext(ClientCacheRegionExpirationConfiguration.class),
			"People");
	}

	@ClientCacheApplication(name = "EnableExpirationConfigurationIntegrationTests")
	@EnableCachingDefinedRegions(clientRegionShortcut = ClientRegionShortcut.LOCAL)
	@EnableExpiration
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

	@ClientCacheApplication(name = "EnableExpirationConfigurationIntegrationTests")
	@EnableEntityDefinedRegions(basePackageClasses = Person.class, clientRegionShortcut = ClientRegionShortcut.LOCAL)
	@EnableExpiration
	@EnableGemFireMockObjects
	static class ClientCacheRegionExpirationConfiguration { }

}
