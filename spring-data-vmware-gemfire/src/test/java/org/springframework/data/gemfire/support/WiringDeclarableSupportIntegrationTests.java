/*
 * Copyright (c) VMware, Inc. 2022-2023. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.support;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;

import org.apache.geode.cache.CacheLoader;
import org.apache.geode.cache.CacheLoaderException;
import org.apache.geode.cache.DataPolicy;
import org.apache.geode.cache.LoaderHelper;
import org.apache.geode.cache.Region;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.gemfire.tests.integration.IntegrationTestsSupport;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Integration Tests for {@link WiringDeclarableSupport} and {@link GemfireBeanFactoryLocator}.
 *
 * @author Costin Leau
 * @author John Blum
 * @see org.junit.Test
 * @see org.apache.geode.cache.CacheLoader
 * @see org.apache.geode.cache.Region
 * @see org.springframework.beans.factory.BeanFactory
 * @see org.springframework.data.gemfire.support.GemfireBeanFactoryLocator
 * @see org.springframework.data.gemfire.support.WiringDeclarableSupport
 * @see org.springframework.data.gemfire.tests.integration.IntegrationTestsSupport
 * @see org.springframework.test.context.ContextConfiguration
 * @see org.springframework.test.context.junit4.SpringRunner
 */
@RunWith(SpringRunner.class)
@ContextConfiguration
public class WiringDeclarableSupportIntegrationTests extends IntegrationTestsSupport {

	@Autowired
	@SuppressWarnings("unused")
	private BeanFactory beanFactory;

	@Autowired
	@Qualifier("Example")
	@SuppressWarnings("unused")
	private Region<String, String> example;

	private void assertRegion(Region<?, ?> region, String name, DataPolicy dataPolicy) {

		assertThat(region).isNotNull();
		assertThat(region.getName()).isEqualTo(name);
		assertThat(region.getFullPath()).isEqualTo(String.format("%1$s%2$s", Region.SEPARATOR, name));
		assertThat(region.getAttributes()).isNotNull();
		assertThat(region.getAttributes().getDataPolicy()).isEqualTo(dataPolicy);
	}

	@Test
	public void declarableObjectAutoWiredSuccessfully() {

		assertThat(beanFactory.containsBean("testBean")).isTrue();
		assertRegion(example, "Example", DataPolicy.NORMAL);

		CacheLoader<?, ?> testCacheLoader = example.getAttributes().getCacheLoader();

		assertThat(testCacheLoader).isInstanceOf(TestCacheLoader.class);
		assertThat(((TestCacheLoader) testCacheLoader).getBeanFactory()).isSameAs(beanFactory);
		assertThat(((TestCacheLoader) testCacheLoader).getPropertyOne()).isEqualTo(beanFactory.getBean("testBean"));
		assertThat(((TestCacheLoader) testCacheLoader).getPropertyTwo()).isEqualTo("GoodBye");
	}

	@Getter
	@Setter
	@NoArgsConstructor
	public static class TestBean {
		private String name;
	}

	@Getter
	@Setter
	@NoArgsConstructor
	@SuppressWarnings("unused")
	public static class TestCacheLoader extends WiringDeclarableSupport implements CacheLoader<String, String> {

		private Object propertyOne;
		private String propertyTwo;

		/**
		 * @inheritDoc
		 */
		@Override
		public String load(LoaderHelper<String, String> helper) throws CacheLoaderException {
			return helper.getKey();
		}
	}
}
