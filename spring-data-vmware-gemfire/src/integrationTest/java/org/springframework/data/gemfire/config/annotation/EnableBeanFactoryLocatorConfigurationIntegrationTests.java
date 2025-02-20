/*
 * Copyright 2022-2025 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.config.annotation;

import org.assertj.core.api.Assertions;
import org.junit.After;
import org.junit.Test;
import org.springframework.data.gemfire.client.ClientCacheFactoryBean;
import org.springframework.data.gemfire.tests.integration.IntegrationTestsSupport;
import org.springframework.data.gemfire.tests.integration.SpringApplicationContextIntegrationTestsSupport;
import org.springframework.data.gemfire.tests.mock.annotation.EnableGemFireMockObjects;

/**
 * Integration Tests for {@link EnableBeanFactoryLocator} and {@link BeanFactoryLocatorConfiguration}.
 *
 * @author John Blum
 * @see org.junit.Test
 * @see org.springframework.data.gemfire.client.ClientCacheFactoryBean
 * @see org.springframework.data.gemfire.config.annotation.BeanFactoryLocatorConfiguration
 * @see org.springframework.data.gemfire.config.annotation.EnableBeanFactoryLocator
 * @see org.springframework.data.gemfire.tests.integration.SpringApplicationContextIntegrationTestsSupport
 * @see org.springframework.data.gemfire.tests.mock.annotation.EnableGemFireMockObjects
 * @since 2.0.0
 */
public class EnableBeanFactoryLocatorConfigurationIntegrationTests
		extends SpringApplicationContextIntegrationTestsSupport {

	@After
	public void tearDown() {
		IntegrationTestsSupport.closeAllBeanFactoryLocators();
	}

	private <T extends ClientCacheFactoryBean> void testGemFireCacheBeanFactoryLocator(Class<?> configuration,
			Class<T> cacheFactoryBeanType, boolean beanFactoryLocatorEnabled) {

		newApplicationContext(configuration);

		Assertions.assertThat(containsBean("gemfireCache")).isTrue();

		ClientCacheFactoryBean gemfireCache = getBean("&gemfireCache", ClientCacheFactoryBean.class);

		Assertions.assertThat(gemfireCache).isNotNull();
		Assertions.assertThat(gemfireCache).isInstanceOf(cacheFactoryBeanType);
		Assertions.assertThat(gemfireCache.isUseBeanFactoryLocator()).isEqualTo(beanFactoryLocatorEnabled);
	}

	@Test
	public void gemfireClientCacheBeanFactoryLocatorIsDisabled() {
		testGemFireCacheBeanFactoryLocator(TestClientCacheBeanFactoryLocatorDisabledConfiguration.class,
			ClientCacheFactoryBean.class, false);
	}

	@Test
	public void gemfireClientCacheBeanFactoryLocatorIsEnabled() {
		testGemFireCacheBeanFactoryLocator(TestClientCacheBeanFactoryLocatorEnabledConfiguration.class,
			ClientCacheFactoryBean.class, true);
	}

	@Test
	public void gemfirePeerCacheBeanFactoryLocatorIsDisabled() {
		testGemFireCacheBeanFactoryLocator(TestPeerCacheBeanFactoryLocatorDisabledConfiguration.class,
			ClientCacheFactoryBean.class, false);
	}

	@Test
	public void gemfirePeerCacheBeanFactoryLocatorIsEnabled() {
		testGemFireCacheBeanFactoryLocator(TestPeerCacheBeanFactoryLocatorEnabledConfiguration.class,
			ClientCacheFactoryBean.class, true);
	}

	@ClientCacheApplication
	@EnableGemFireMockObjects
	static class TestClientCacheBeanFactoryLocatorDisabledConfiguration { }

	@ClientCacheApplication
	@EnableBeanFactoryLocator
	@EnableGemFireMockObjects
	static class TestClientCacheBeanFactoryLocatorEnabledConfiguration { }

	@EnableGemFireMockObjects
	@ClientCacheApplication
	static class TestPeerCacheBeanFactoryLocatorDisabledConfiguration { }

	@EnableGemFireMockObjects
	@EnableBeanFactoryLocator
	@ClientCacheApplication
	static class TestPeerCacheBeanFactoryLocatorEnabledConfiguration { }

}
