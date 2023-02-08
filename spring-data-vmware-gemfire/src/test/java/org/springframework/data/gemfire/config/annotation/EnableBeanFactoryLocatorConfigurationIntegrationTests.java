/*
 * Copyright (c) VMware, Inc. 2022-2023. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.config.annotation;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.After;
import org.junit.Test;

import org.springframework.data.gemfire.CacheFactoryBean;
import org.springframework.data.gemfire.tests.integration.SpringApplicationContextIntegrationTestsSupport;
import org.springframework.data.gemfire.tests.mock.annotation.EnableGemFireMockObjects;

/**
 * Integration Tests for {@link EnableBeanFactoryLocator} and {@link BeanFactoryLocatorConfiguration}.
 *
 * @author John Blum
 * @see org.junit.Test
 * @see org.springframework.data.gemfire.CacheFactoryBean
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
		closeAllBeanFactoryLocators();
	}

	private <T extends CacheFactoryBean> void testGemFireCacheBeanFactoryLocator(Class<?> configuration,
			Class<T> cacheFactoryBeanType, boolean beanFactoryLocatorEnabled) {

		newApplicationContext(configuration);

		assertThat(containsBean("gemfireCache")).isTrue();

		CacheFactoryBean gemfireCache = getBean("&gemfireCache", CacheFactoryBean.class);

		assertThat(gemfireCache).isNotNull();
		assertThat(gemfireCache).isInstanceOf(cacheFactoryBeanType);
		assertThat(gemfireCache.isUseBeanFactoryLocator()).isEqualTo(beanFactoryLocatorEnabled);
	}

	@Test
	public void gemfireClientCacheBeanFactoryLocatorIsDisabled() {
		testGemFireCacheBeanFactoryLocator(TestClientCacheBeanFactoryLocatorDisabledConfiguration.class,
			CacheFactoryBean.class, false);
	}

	@Test
	public void gemfireClientCacheBeanFactoryLocatorIsEnabled() {
		testGemFireCacheBeanFactoryLocator(TestClientCacheBeanFactoryLocatorEnabledConfiguration.class,
			CacheFactoryBean.class, true);
	}

	@Test
	public void gemfirePeerCacheBeanFactoryLocatorIsDisabled() {
		testGemFireCacheBeanFactoryLocator(TestPeerCacheBeanFactoryLocatorDisabledConfiguration.class,
			CacheFactoryBean.class, false);
	}

	@Test
	public void gemfirePeerCacheBeanFactoryLocatorIsEnabled() {
		testGemFireCacheBeanFactoryLocator(TestPeerCacheBeanFactoryLocatorEnabledConfiguration.class,
			CacheFactoryBean.class, true);
	}

	@ClientCacheApplication
	@EnableGemFireMockObjects
	static class TestClientCacheBeanFactoryLocatorDisabledConfiguration { }

	@ClientCacheApplication
	@EnableBeanFactoryLocator
	@EnableGemFireMockObjects
	static class TestClientCacheBeanFactoryLocatorEnabledConfiguration { }

	@EnableGemFireMockObjects
	@PeerCacheApplication
	static class TestPeerCacheBeanFactoryLocatorDisabledConfiguration { }

	@EnableGemFireMockObjects
	@EnableBeanFactoryLocator
	@PeerCacheApplication
	static class TestPeerCacheBeanFactoryLocatorEnabledConfiguration { }

}
