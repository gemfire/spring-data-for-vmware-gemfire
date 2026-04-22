/*
 * Copyright (c) 2026 Broadcom. All rights reserved.
 */

/*
 * Copyright (c) 2026 Broadcom. All rights reserved.
 */

/*
 * Copyright $originalComment.match(" (\d+)", 1, "-", $today.year)2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

/*
 * @AI-Generated
 * Generated in whole or in part by Cursor
 * Description:
 * 2026-04-17: usesDefaultEvictionPolicyConfiguration expects Mockito GudEvictionAttributes (unit isolation)
 */
package org.springframework.data.gemfire.config.annotation;

import org.junit.After;
import org.junit.Test;
import org.springframework.context.annotation.Bean;
import org.springframework.data.gemfire.client.ClientRegionFactoryBean;
import org.springframework.data.gemfire.eviction.EvictionActionType;
import org.springframework.data.gemfire.eviction.EvictionAttributesFactoryBean;
import org.springframework.data.gemfire.eviction.EvictionPolicyType;
import org.springframework.data.gemfire.gud.api.GudClientCache;
import org.springframework.data.gemfire.gud.api.GudClientRegionShortcut;
import org.springframework.data.gemfire.gud.api.GudDataPolicy;
import org.springframework.data.gemfire.gud.api.GudEvictionAction;
import org.springframework.data.gemfire.gud.api.GudEvictionAlgorithm;
import org.springframework.data.gemfire.gud.api.GudEvictionAttributes;
import org.springframework.data.gemfire.gud.api.GudObjectSizer;
import org.springframework.data.gemfire.gud.api.GudRegion;
import org.springframework.data.gemfire.tests.integration.SpringApplicationContextIntegrationTestsSupport;
import org.springframework.data.gemfire.tests.mock.annotation.EnableGemFireMockObjects;
import org.springframework.data.gemfire.util.ArrayUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.data.gemfire.config.annotation.EnableEviction.EvictionPolicy;

/**
 * Unit Tests for the {@link EnableEviction} annotation and {@link EvictionConfiguration} class.
 *
 * @author John Blum
 * @see org.junit.Test
 * @see org.mockito.Mockito
 * @see org.apache.geode.cache.GudEvictionAttributes
 * @see org.apache.geode.cache.GudRegion
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

	private void assertEvictionAttributes(GudRegion<?, ?> region, GudEvictionAttributes expectedEvictionAttributes) {

		assertThat(region).isNotNull();
		assertThat(region.getAttributes()).isNotNull();
		assertEvictionAttributes(region.getAttributes().getEvictionAttributes(), expectedEvictionAttributes);
	}

	private void assertEvictionAttributes(GudEvictionAttributes actualEvictionAttributes,
			GudEvictionAttributes expectedEvictionAttributes) {

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
	protected <K, V> GudRegion<K, V> getRegion(String beanName) {
		return getBean(beanName, GudRegion.class);
	}

	private GudEvictionAttributes newEvictionAttributes(Integer maximum, EvictionPolicyType type, EvictionActionType action,
			GudObjectSizer... objectSizer) {

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

		GudEvictionAttributes defaultEvictionAttributes = mock(GudEvictionAttributes.class);
		when(defaultEvictionAttributes.getAction()).thenReturn(GudEvictionAction.DEFAULT_EVICTION_ACTION);
		when(defaultEvictionAttributes.getAlgorithm()).thenReturn(GudEvictionAlgorithm.LRU_ENTRY);
		when(defaultEvictionAttributes.getMaximum()).thenReturn(GudEvictionAttributes.DEFAULT_ENTRIES_MAXIMUM);
		when(defaultEvictionAttributes.getObjectSizer()).thenReturn(null);

		assertEvictionAttributes(getBean("LocalRegion", GudRegion.class), defaultEvictionAttributes);
	}

	@Test
	public void usesCustomEvictionPolicyConfiguration() {

		newApplicationContext(CustomEvictionPolicyConfiguration.class);

		GudObjectSizer mockObjectSizer = getBean("mockObjectSizer", GudObjectSizer.class);

		GudEvictionAttributes customEvictionAttributes =
			newEvictionAttributes(65536, EvictionPolicyType.MEMORY_SIZE, EvictionActionType.OVERFLOW_TO_DISK,
				mockObjectSizer);

		assertEvictionAttributes(getBean("LocalRegion", GudRegion.class), customEvictionAttributes);
	}

	@Test
	public void usesLastMatchingEvictionPolicyConfiguration() {

		newApplicationContext(LastMatchingWinsEvictionPolicyConfiguration.class);

		GudEvictionAttributes lastMatchingEvictionAttributes =
			newEvictionAttributes(99, EvictionPolicyType.ENTRY_COUNT, EvictionActionType.OVERFLOW_TO_DISK);

		assertEvictionAttributes(getBean("LocalRegion", GudRegion.class), lastMatchingEvictionAttributes);
	}

	@ClientCacheApplication
	@EnableGemFireMockObjects
	@SuppressWarnings("unused")
	static class CacheRegionConfiguration {

		@Bean("LocalRegion")
		ClientRegionFactoryBean<Object, Object> mockLocalRegion(GudClientCache gemfireCache) {

			ClientRegionFactoryBean<Object, Object> clientRegionFactory =
				new ClientRegionFactoryBean<>();

			clientRegionFactory.setCache(gemfireCache);
			clientRegionFactory.setPersistent(false);
			clientRegionFactory.setDataPolicy(GudDataPolicy.NORMAL);
			clientRegionFactory.setShortcut(GudClientRegionShortcut.LOCAL);

			return clientRegionFactory;
		}

		@Bean
		GudObjectSizer mockObjectSizer() {
			return mock(GudObjectSizer.class);
		}
	}

	@EnableEviction
	static class DefaultEvictionPolicyConfiguration extends CacheRegionConfiguration { }

	@EnableEviction(policies = @EvictionPolicy(maximum = 65536, type = EvictionPolicyType.MEMORY_SIZE,
		action = EvictionActionType.OVERFLOW_TO_DISK, objectSizerName = "mockObjectSizer"))
	static class CustomEvictionPolicyConfiguration extends CacheRegionConfiguration { }

	@EnableEviction(policies = {
		@EvictionPolicy(maximum = 1, type = EvictionPolicyType.ENTRY_COUNT, action = EvictionActionType.LOCAL_DESTROY,
			objectSizerName = "mockObjectSizer", regionNames = "LocalRegion"),
		@EvictionPolicy(maximum = 99, type = EvictionPolicyType.ENTRY_COUNT, action = EvictionActionType.OVERFLOW_TO_DISK)
	})
	static class LastMatchingWinsEvictionPolicyConfiguration extends CacheRegionConfiguration { }

}
