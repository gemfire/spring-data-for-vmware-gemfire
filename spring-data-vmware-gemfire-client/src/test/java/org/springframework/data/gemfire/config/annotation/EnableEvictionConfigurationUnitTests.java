/*
 * Copyright (c) VMware, Inc. 2022-2024. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.config.annotation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.springframework.data.gemfire.config.annotation.EnableEviction.EvictionPolicy;

import org.apache.geode.cache.client.ClientCache;
import org.apache.geode.cache.client.ClientRegionShortcut;
import org.junit.After;
import org.junit.Test;

import org.apache.geode.cache.EvictionAttributes;
import org.apache.geode.cache.Region;
import org.apache.geode.cache.util.ObjectSizer;

import org.springframework.context.annotation.Bean;
import org.springframework.data.gemfire.client.ClientRegionFactoryBean;
import org.springframework.data.gemfire.eviction.EvictionActionType;
import org.springframework.data.gemfire.eviction.EvictionAttributesFactoryBean;
import org.springframework.data.gemfire.eviction.EvictionPolicyType;
import org.springframework.data.gemfire.tests.integration.SpringApplicationContextIntegrationTestsSupport;
import org.springframework.data.gemfire.tests.mock.annotation.EnableGemFireMockObjects;
import org.springframework.data.gemfire.util.ArrayUtils;

/**
 * Unit Tests for the {@link EnableEviction} annotation and {@link EvictionConfiguration} class.
 *
 * @author John Blum
 * @see org.junit.Test
 * @see org.mockito.Mockito
 * @see org.apache.geode.cache.EvictionAttributes
 * @see org.apache.geode.cache.Region
 * @see org.springframework.data.gemfire.config.annotation.EnableEviction
 * @see org.springframework.data.gemfire.config.annotation.EvictionConfiguration
 * @see org.springframework.data.gemfire.eviction.EvictionAttributesFactoryBean
 * @see org.springframework.data.gemfire.tests.integration.IntegrationTestsSupport
 * @see org.springframework.data.gemfire.tests.mock.annotation.EnableGemFireMockObjects
 * @since 1.9.0
 */
public class EnableEvictionConfigurationUnitTests extends SpringApplicationContextIntegrationTestsSupport {

	@After
	public void cleanupAfterTests() {
		destroyAllGemFireMockObjects();
	}

	private void assertEvictionAttributes(Region<?, ?> region, EvictionAttributes expectedEvictionAttributes) {

		assertThat(region).isNotNull();
		assertThat(region.getAttributes()).isNotNull();
		assertEvictionAttributes(region.getAttributes().getEvictionAttributes(), expectedEvictionAttributes);
	}

	private void assertEvictionAttributes(EvictionAttributes actualEvictionAttributes,
			EvictionAttributes expectedEvictionAttributes) {

		assertThat(actualEvictionAttributes).isNotNull();
		assertThat(actualEvictionAttributes.getAction()).isEqualTo(expectedEvictionAttributes.getAction());
		assertThat(actualEvictionAttributes.getAlgorithm()).isEqualTo(expectedEvictionAttributes.getAlgorithm());
		assertThat(actualEvictionAttributes.getObjectSizer()).isEqualTo(expectedEvictionAttributes.getObjectSizer());

		if (!EvictionPolicyType.HEAP_PERCENTAGE.equals(
				EvictionPolicyType.valueOf(actualEvictionAttributes.getAlgorithm()))) {
			assertThat(actualEvictionAttributes.getMaximum()).isEqualTo(expectedEvictionAttributes.getMaximum());
		}
	}

	@SuppressWarnings("unchecked")
	protected <K, V> Region<K, V> getRegion(String beanName) {
		return getBean(beanName, Region.class);
	}

	private EvictionAttributes newEvictionAttributes(Integer maximum, EvictionPolicyType type, EvictionActionType action,
			ObjectSizer... objectSizer) {

		EvictionAttributesFactoryBean evictionAttributesFactory = new EvictionAttributesFactoryBean();

		evictionAttributesFactory.setAction(action.getEvictionAction());
		evictionAttributesFactory.setObjectSizer(ArrayUtils.getFirst(objectSizer));
		evictionAttributesFactory.setThreshold(maximum);
		evictionAttributesFactory.setType(type);
		evictionAttributesFactory.afterPropertiesSet();

		return evictionAttributesFactory.getObject();
	}

	@Test
	public void usesDefaultEvictionPolicyConfiguration() {

		newApplicationContext(DefaultEvictionPolicyConfiguration.class);

		EvictionAttributes defaultEvictionAttributes = EvictionAttributes.createLRUEntryAttributes();

		assertEvictionAttributes(getBean("LocalRegion", Region.class), defaultEvictionAttributes);
	}

	@Test
	public void usesCustomEvictionPolicyConfiguration() {

		newApplicationContext(CustomEvictionPolicyConfiguration.class);

		ObjectSizer mockObjectSizer = getBean("mockObjectSizer", ObjectSizer.class);

		EvictionAttributes customEvictionAttributes =
			newEvictionAttributes(65536, EvictionPolicyType.MEMORY_SIZE, EvictionActionType.OVERFLOW_TO_DISK,
				mockObjectSizer);

		assertEvictionAttributes(getBean("LocalRegion", Region.class), customEvictionAttributes);
	}

	@Test
	public void usesRegionSpecificEvictionPolicyConfiguration() {

		newApplicationContext(RegionSpecificEvictionPolicyConfiguration.class);

		ObjectSizer mockObjectSizer = getBean("mockObjectSizer", ObjectSizer.class);

		EvictionAttributes localRegionEvictionAttributes =
			newEvictionAttributes(null, EvictionPolicyType.HEAP_PERCENTAGE, EvictionActionType.OVERFLOW_TO_DISK,
				mockObjectSizer);

		assertEvictionAttributes(getBean("LocalRegion", Region.class), localRegionEvictionAttributes);
	}

	@Test
	public void usesLastMatchingEvictionPolicyConfiguration() {

		newApplicationContext(LastMatchingWinsEvictionPolicyConfiguration.class);

		EvictionAttributes lastMatchingEvictionAttributes =
			newEvictionAttributes(99, EvictionPolicyType.ENTRY_COUNT, EvictionActionType.OVERFLOW_TO_DISK);

		assertEvictionAttributes(getBean("LocalRegion", Region.class), lastMatchingEvictionAttributes);
	}

	@ClientCacheApplication
	@EnableGemFireMockObjects
	@SuppressWarnings("unused")
	static class CacheRegionConfiguration {

		@Bean("LocalRegion")
		ClientRegionFactoryBean<Object, Object> mockLocalRegion(ClientCache gemfireCache) {

			ClientRegionFactoryBean<Object, Object> localRegion =
				new ClientRegionFactoryBean<>();

			localRegion.setCache(gemfireCache);
			localRegion.setShortcut(ClientRegionShortcut.LOCAL);
			localRegion.setPersistent(false);

			return localRegion;
		}

		@Bean
		ObjectSizer mockObjectSizer() {
			return mock(ObjectSizer.class);
		}
	}

	@EnableEviction
	static class DefaultEvictionPolicyConfiguration extends CacheRegionConfiguration { }

	@EnableEviction(policies = @EvictionPolicy(maximum = 65536, type = EvictionPolicyType.MEMORY_SIZE,
		action = EvictionActionType.OVERFLOW_TO_DISK, objectSizerName = "mockObjectSizer"))
	static class CustomEvictionPolicyConfiguration extends CacheRegionConfiguration { }

	@EnableEviction(policies = {
		@EvictionPolicy(maximum = 85, type = EvictionPolicyType.HEAP_PERCENTAGE, action = EvictionActionType.OVERFLOW_TO_DISK,
			objectSizerName = "mockObjectSizer", regionNames = "LocalRegion")
	})
	static class RegionSpecificEvictionPolicyConfiguration extends CacheRegionConfiguration { }

	@EnableEviction(policies = {
		@EvictionPolicy(maximum = 99, type = EvictionPolicyType.ENTRY_COUNT, action = EvictionActionType.OVERFLOW_TO_DISK)
	})
	static class LastMatchingWinsEvictionPolicyConfiguration extends CacheRegionConfiguration { }

}
