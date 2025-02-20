/*
 * Copyright 2022-2025 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.config.xml;

import java.util.Arrays;

import org.apache.geode.cache.CacheListener;
import org.apache.geode.cache.CacheLoader;
import org.apache.geode.cache.CacheLoaderException;
import org.apache.geode.cache.DataPolicy;
import org.apache.geode.cache.EvictionAction;
import org.apache.geode.cache.EvictionAlgorithm;
import org.apache.geode.cache.EvictionAttributes;
import org.apache.geode.cache.ExpirationAction;
import org.apache.geode.cache.ExpirationAttributes;
import org.apache.geode.cache.InterestPolicy;
import org.apache.geode.cache.LoaderHelper;
import org.apache.geode.cache.Region;
import org.apache.geode.cache.SubscriptionAttributes;
import org.apache.geode.cache.util.CacheListenerAdapter;
import org.apache.geode.cache.util.CacheWriterAdapter;
import org.apache.geode.cache.util.ObjectSizer;
import org.assertj.core.api.Assertions;
import org.junit.Assume;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanIsAbstractException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.data.gemfire.tests.integration.IntegrationTestsSupport;
import org.springframework.data.gemfire.tests.unit.annotation.GemFireUnitTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.StringUtils;

/**
 * Integration Tests for {@link Region} Templates.
 *
 * @author John Blum
 * @see org.junit.Test
 * @see org.junit.runner.RunWith
 * @see org.apache.geode.cache.Region
 * @see org.springframework.data.gemfire.tests.integration.IntegrationTestsSupport
 * @see org.springframework.data.gemfire.tests.unit.annotation.GemFireUnitTest
 * @see org.springframework.test.context.ContextConfiguration
 * @see org.springframework.test.context.junit4.SpringJUnit4ClassRunner
 * @since 1.5.0
 */
@RunWith(SpringRunner.class)
@GemFireUnitTest
@SuppressWarnings({ "rawtypes", "unused"})
public class TemplateRegionsNamespaceIntegrationTests extends IntegrationTestsSupport {

	@Autowired
	private ApplicationContext applicationContext;

	@Autowired
	@Qualifier("TemplateBasedLocalRegion")
	private Region<Long, String> templateBasedLocalRegion;

	private void assertCacheListeners(Region<?, ?> region, String... expectedNames) {

		Assertions.assertThat(region).isNotNull();
		Assertions.assertThat(region.getAttributes()).isNotNull();
		Assertions.assertThat(region.getAttributes().getCacheListeners()).isNotNull();
		Assertions.assertThat(region.getAttributes().getCacheListeners().length).isEqualTo(expectedNames.length);

		for (CacheListener cacheListener : region.getAttributes().getCacheListeners()) {
			Assertions.assertThat(cacheListener instanceof TestCacheListener).isTrue();
			Assertions.assertThat(Arrays.asList(expectedNames).contains(cacheListener.toString())).isTrue();
		}
	}

	private void assertCacheLoader(Region<?, ?> region, String expectedName) {

		Assertions.assertThat(region).isNotNull();
		Assertions.assertThat(region.getAttributes()).isNotNull();
		Assertions.assertThat(region.getAttributes().getCacheLoader() instanceof TestCacheLoader).isTrue();
		Assertions.assertThat(region.getAttributes().getCacheLoader().toString()).isEqualTo(expectedName);
	}

	private void assertCacheWriter(Region<?, ?> region, String expectedName) {

		Assertions.assertThat(region).isNotNull();
		Assertions.assertThat(region.getAttributes()).isNotNull();
		Assertions.assertThat(region.getAttributes().getCacheWriter() instanceof TestCacheWriter).isTrue();
		Assertions.assertThat(region.getAttributes().getCacheWriter().toString()).isEqualTo(expectedName);
	}

	private void assertDefaultEvictionAttributes(EvictionAttributes evictionAttributes) {
		Assume.assumeNotNull(evictionAttributes);
		assertEvictionAttributes(evictionAttributes, EvictionAction.NONE, EvictionAlgorithm.NONE, 0, null);
	}

	private void assertEvictionAttributes(EvictionAttributes evictionAttributes, EvictionAction expectedAction,
			EvictionAlgorithm expectedAlgorithm, int expectedMaximum, ObjectSizer expectedObjectSizer) {

		Assertions.assertThat(evictionAttributes).as("The 'EvictionAttributes' must not be null").isNotNull();
		Assertions.assertThat(evictionAttributes.getAction()).isEqualTo(expectedAction);
		Assertions.assertThat(evictionAttributes.getAlgorithm()).isEqualTo(expectedAlgorithm);
		Assertions.assertThat(evictionAttributes.getMaximum()).isEqualTo(expectedMaximum);
		Assertions.assertThat(evictionAttributes.getObjectSizer()).isEqualTo(expectedObjectSizer);
	}

	private void assertDefaultExpirationAttributes(ExpirationAttributes expirationAttributes) {

		Assume.assumeNotNull(expirationAttributes);
		Assertions.assertThat(expirationAttributes.getAction()).isEqualTo(ExpirationAction.INVALIDATE);
		Assertions.assertThat(expirationAttributes.getTimeout()).isEqualTo(0);
	}

	private void assertExpirationAttributes(ExpirationAttributes expirationAttributes, ExpirationAction expectedAction,
			int expectedTimeout) {

		Assertions.assertThat(expirationAttributes).as("The 'ExpirationAttributes' must not be null").isNotNull();
		Assertions.assertThat(expirationAttributes.getAction()).isEqualTo(expectedAction);
		Assertions.assertThat(expirationAttributes.getTimeout()).isEqualTo(expectedTimeout);
	}

	@SuppressWarnings("unchecked")
	private void assertDefaultRegionAttributes(Region region) {

		Assertions.assertThat(region).describedAs("The Region must not be null").isNotNull();

		Assertions.assertThat(region.getAttributes())
			.describedAs(String.format("Region (%1$s) must have 'RegionAttributes' defined",region.getFullPath()))
			.isNotNull();

		Assertions.assertThat(region.getAttributes().getCompressor()).isNull();
		Assertions.assertThat(region.getAttributes().getCustomEntryIdleTimeout()).isNull();
		Assertions.assertThat(region.getAttributes().getCustomEntryTimeToLive()).isNull();
		Assertions.assertThat(region.getAttributes().getDiskStoreName()).isNull();
		assertNullEmpty(region.getAttributes().getPoolName());
		assertDefaultExpirationAttributes(region.getAttributes().getRegionTimeToLive());
		assertDefaultExpirationAttributes(region.getAttributes().getRegionIdleTimeout());
	}

	private void assertDefaultSubscriptionAttributes(SubscriptionAttributes subscriptionAttributes) {

		Assume.assumeNotNull(subscriptionAttributes);
		assertSubscriptionAttributes(subscriptionAttributes, InterestPolicy.DEFAULT);
	}

	private void assertSubscriptionAttributes(SubscriptionAttributes subscriptionAttributes,
			InterestPolicy expectedInterestPolicy) {

		Assertions.assertThat(subscriptionAttributes).as("The 'SubscriptionAttributes' must not be null").isNotNull();
		Assertions.assertThat(subscriptionAttributes.getInterestPolicy()).isEqualTo(expectedInterestPolicy);
	}

	private static void assertEmpty(Object[] array) {
		Assertions.assertThat((array == null || array.length == 0)).isTrue();
	}

	private static void assertEmpty(Iterable<?> collection) {
		Assertions.assertThat(collection == null || !collection.iterator().hasNext()).isTrue();
	}

	private static void assertNullEmpty(String value) {
		Assertions.assertThat(StringUtils.hasText(value)).isFalse();
	}

	private static void assertRegionMetaData(Region<?, ?> region, String expectedRegionName) {
		assertRegionMetaData(region, expectedRegionName, Region.SEPARATOR + expectedRegionName);
	}

	private static void assertRegionMetaData(Region<?, ?> region, String expectedRegionName, String expectedRegionPath) {

		Assertions.assertThat(region).as(String.format("The '%1$s' Region was not properly configured and initialized",
			expectedRegionName)).isNotNull();
		Assertions.assertThat(region.getName()).isEqualTo(expectedRegionName);
		Assertions.assertThat(region.getFullPath()).isEqualTo(expectedRegionPath);
		Assertions.assertThat(region.getAttributes()).as(String.format("The '%1$s' Region must have RegionAttributes defined",
			expectedRegionName)).isNotNull();
	}

	@Test
	public void testNoAbstractRegionTemplateBeans() {

		String[] beanNames = {
			"BaseRegionTemplate",
			"ExtendedRegionTemplate",
			"LocalRegionTemplate"
		};

		for (String beanName : beanNames) {
			Assertions.assertThat(applicationContext.containsBean(beanName)).isTrue();
			Assertions.assertThat(applicationContext.containsBeanDefinition(beanName)).isTrue();

			try {
				applicationContext.getBean(beanName);
				Assertions.fail(String
					.format("The abstract bean definition '%1$s' should not exist as a bean in the Spring context",
						beanName));
			}
			catch (BeansException expected) {
				Assertions.assertThat(expected instanceof BeanIsAbstractException).isTrue();
				Assertions.assertThat(expected.getMessage().contains(beanName)).isTrue();
			}
		}
	}

	@Test
	public void testTemplateBasedLocalRegion() {

		assertRegionMetaData(templateBasedLocalRegion, "TemplateBasedLocalRegion");
		assertDefaultRegionAttributes(templateBasedLocalRegion);
		assertCacheListeners(templateBasedLocalRegion, "X", "Y", "Z");
		Assertions.assertThat(templateBasedLocalRegion.getAttributes().getCacheLoader()).isNull();
		Assertions.assertThat(templateBasedLocalRegion.getAttributes().getCacheWriter()).isNull();
		Assertions.assertThat(templateBasedLocalRegion.getAttributes().getCloningEnabled()).isTrue();
		Assertions.assertThat(templateBasedLocalRegion.getAttributes().getConcurrencyChecksEnabled()).isFalse();
		Assertions.assertThat(templateBasedLocalRegion.getAttributes().getConcurrencyLevel()).isEqualTo(8);
		Assertions.assertThat(templateBasedLocalRegion.getAttributes().getDataPolicy()).isEqualTo(DataPolicy.NORMAL);
		Assertions.assertThat(templateBasedLocalRegion.getAttributes().isDiskSynchronous()).isFalse();
		Assertions.assertThat(templateBasedLocalRegion.getAttributes().getEnableSubscriptionConflation()).isFalse();
		assertEvictionAttributes(templateBasedLocalRegion.getAttributes().getEvictionAttributes(),
			EvictionAction.LOCAL_DESTROY, EvictionAlgorithm.LRU_ENTRY, 4096, null);
		assertExpirationAttributes(templateBasedLocalRegion.getAttributes().getEntryIdleTimeout(),
			ExpirationAction.DESTROY, 600);
		assertExpirationAttributes(templateBasedLocalRegion.getAttributes().getEntryTimeToLive(),
			ExpirationAction.INVALIDATE, 300);
		Assertions.assertThat(templateBasedLocalRegion.getAttributes().getInitialCapacity()).isEqualTo(51);
		Assertions.assertThat(templateBasedLocalRegion.getAttributes().getKeyConstraint()).isEqualTo(Long.class);
		Assertions.assertThat(String.valueOf(templateBasedLocalRegion.getAttributes().getLoadFactor())).isEqualTo("0.85");
		Assertions.assertThat(templateBasedLocalRegion.getAttributes().isLockGrantor()).isFalse();
		Assertions.assertThat(templateBasedLocalRegion.getAttributes().getPartitionAttributes()).isNull();
		Assertions.assertThat(templateBasedLocalRegion.getAttributes().getStatisticsEnabled()).isTrue();
		assertDefaultSubscriptionAttributes(templateBasedLocalRegion.getAttributes().getSubscriptionAttributes());
		Assertions.assertThat(templateBasedLocalRegion.getAttributes().getValueConstraint()).isEqualTo(String.class);
	}

	public static final class TestCacheListener extends CacheListenerAdapter {

		private String name;

		public void setName(String name) {
			this.name = name;
		}

		@Override
		public String toString() {
			return name;
		}
	}

	public static final class TestCacheLoader implements CacheLoader {

		private String name;

		public void setName(String name) {
			this.name = name;
		}

		@Override
		public Object load(LoaderHelper loaderHelper) throws CacheLoaderException {
			return null;
		}

		@Override
		public void close() { }

		@Override
		public String toString() {
			return name;
		}
	}

	public static final class TestCacheWriter extends CacheWriterAdapter {

		private String name;

		public void setName(String name) {
			this.name = name;
		}

		@Override
		public String toString() {
			return name;
		}
	}
}
