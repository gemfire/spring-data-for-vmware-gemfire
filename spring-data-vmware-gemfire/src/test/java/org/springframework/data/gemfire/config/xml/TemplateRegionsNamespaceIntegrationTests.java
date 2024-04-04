/*
 * Copyright 2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

/*
 * Copyright 2022-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.config.xml;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static org.junit.Assume.assumeNotNull;
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

		assertThat(region).isNotNull();
		assertThat(region.getAttributes()).isNotNull();
		assertThat(region.getAttributes().getCacheListeners()).isNotNull();
		assertThat(region.getAttributes().getCacheListeners().length).isEqualTo(expectedNames.length);

		for (CacheListener cacheListener : region.getAttributes().getCacheListeners()) {
			assertThat(cacheListener instanceof TestCacheListener).isTrue();
			assertThat(Arrays.asList(expectedNames).contains(cacheListener.toString())).isTrue();
		}
	}

	private void assertCacheLoader(Region<?, ?> region, String expectedName) {

		assertThat(region).isNotNull();
		assertThat(region.getAttributes()).isNotNull();
		assertThat(region.getAttributes().getCacheLoader() instanceof TestCacheLoader).isTrue();
		assertThat(region.getAttributes().getCacheLoader().toString()).isEqualTo(expectedName);
	}

	private void assertCacheWriter(Region<?, ?> region, String expectedName) {

		assertThat(region).isNotNull();
		assertThat(region.getAttributes()).isNotNull();
		assertThat(region.getAttributes().getCacheWriter() instanceof TestCacheWriter).isTrue();
		assertThat(region.getAttributes().getCacheWriter().toString()).isEqualTo(expectedName);
	}

	private void assertDefaultEvictionAttributes(EvictionAttributes evictionAttributes) {
		assumeNotNull(evictionAttributes);
		assertEvictionAttributes(evictionAttributes, EvictionAction.NONE, EvictionAlgorithm.NONE, 0, null);
	}

	private void assertEvictionAttributes(EvictionAttributes evictionAttributes, EvictionAction expectedAction,
			EvictionAlgorithm expectedAlgorithm, int expectedMaximum, ObjectSizer expectedObjectSizer) {

		assertThat(evictionAttributes).as("The 'EvictionAttributes' must not be null").isNotNull();
		assertThat(evictionAttributes.getAction()).isEqualTo(expectedAction);
		assertThat(evictionAttributes.getAlgorithm()).isEqualTo(expectedAlgorithm);
		assertThat(evictionAttributes.getMaximum()).isEqualTo(expectedMaximum);
		assertThat(evictionAttributes.getObjectSizer()).isEqualTo(expectedObjectSizer);
	}

	private void assertDefaultExpirationAttributes(ExpirationAttributes expirationAttributes) {

		assumeNotNull(expirationAttributes);
		assertThat(expirationAttributes.getAction()).isEqualTo(ExpirationAction.INVALIDATE);
		assertThat(expirationAttributes.getTimeout()).isEqualTo(0);
	}

	private void assertExpirationAttributes(ExpirationAttributes expirationAttributes, ExpirationAction expectedAction,
			int expectedTimeout) {

		assertThat(expirationAttributes).as("The 'ExpirationAttributes' must not be null").isNotNull();
		assertThat(expirationAttributes.getAction()).isEqualTo(expectedAction);
		assertThat(expirationAttributes.getTimeout()).isEqualTo(expectedTimeout);
	}

	@SuppressWarnings("unchecked")
	private void assertDefaultRegionAttributes(Region region) {

		assertThat(region).describedAs("The Region must not be null").isNotNull();

		assertThat(region.getAttributes())
			.describedAs(String.format("Region (%1$s) must have 'RegionAttributes' defined",region.getFullPath()))
			.isNotNull();

		assertThat(region.getAttributes().getCompressor()).isNull();
		assertThat(region.getAttributes().getCustomEntryIdleTimeout()).isNull();
		assertThat(region.getAttributes().getCustomEntryTimeToLive()).isNull();
		assertThat(region.getAttributes().getDiskStoreName()).isNull();
		assertNullEmpty(region.getAttributes().getPoolName());
		assertDefaultExpirationAttributes(region.getAttributes().getRegionTimeToLive());
		assertDefaultExpirationAttributes(region.getAttributes().getRegionIdleTimeout());
	}

	private void assertDefaultSubscriptionAttributes(SubscriptionAttributes subscriptionAttributes) {

		assumeNotNull(subscriptionAttributes);
		assertSubscriptionAttributes(subscriptionAttributes, InterestPolicy.DEFAULT);
	}

	private void assertSubscriptionAttributes(SubscriptionAttributes subscriptionAttributes,
			InterestPolicy expectedInterestPolicy) {

		assertThat(subscriptionAttributes).as("The 'SubscriptionAttributes' must not be null").isNotNull();
		assertThat(subscriptionAttributes.getInterestPolicy()).isEqualTo(expectedInterestPolicy);
	}

	private static void assertEmpty(Object[] array) {
		assertThat((array == null || array.length == 0)).isTrue();
	}

	private static void assertEmpty(Iterable<?> collection) {
		assertThat(collection == null || !collection.iterator().hasNext()).isTrue();
	}

	private static void assertNullEmpty(String value) {
		assertThat(StringUtils.hasText(value)).isFalse();
	}

	private static void assertRegionMetaData(Region<?, ?> region, String expectedRegionName) {
		assertRegionMetaData(region, expectedRegionName, Region.SEPARATOR + expectedRegionName);
	}

	private static void assertRegionMetaData(Region<?, ?> region, String expectedRegionName, String expectedRegionPath) {

		assertThat(region).as(String.format("The '%1$s' Region was not properly configured and initialized",
			expectedRegionName)).isNotNull();
		assertThat(region.getName()).isEqualTo(expectedRegionName);
		assertThat(region.getFullPath()).isEqualTo(expectedRegionPath);
		assertThat(region.getAttributes()).as(String.format("The '%1$s' Region must have RegionAttributes defined",
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
			assertThat(applicationContext.containsBean(beanName)).isTrue();
			assertThat(applicationContext.containsBeanDefinition(beanName)).isTrue();

			try {
				applicationContext.getBean(beanName);
				fail(String
					.format("The abstract bean definition '%1$s' should not exist as a bean in the Spring context",
						beanName));
			}
			catch (BeansException expected) {
				assertThat(expected instanceof BeanIsAbstractException).isTrue();
				assertThat(expected.getMessage().contains(beanName)).isTrue();
			}
		}
	}

	@Test
	public void testTemplateBasedLocalRegion() {

		assertRegionMetaData(templateBasedLocalRegion, "TemplateBasedLocalRegion");
		assertDefaultRegionAttributes(templateBasedLocalRegion);
		assertCacheListeners(templateBasedLocalRegion, "X", "Y", "Z");
		assertThat(templateBasedLocalRegion.getAttributes().getCacheLoader()).isNull();
		assertThat(templateBasedLocalRegion.getAttributes().getCacheWriter()).isNull();
		assertThat(templateBasedLocalRegion.getAttributes().getCloningEnabled()).isTrue();
		assertThat(templateBasedLocalRegion.getAttributes().getConcurrencyChecksEnabled()).isFalse();
		assertThat(templateBasedLocalRegion.getAttributes().getConcurrencyLevel()).isEqualTo(8);
		assertThat(templateBasedLocalRegion.getAttributes().getDataPolicy()).isEqualTo(DataPolicy.NORMAL);
		assertThat(templateBasedLocalRegion.getAttributes().isDiskSynchronous()).isFalse();
		assertThat(templateBasedLocalRegion.getAttributes().getEnableSubscriptionConflation()).isFalse();
		assertEvictionAttributes(templateBasedLocalRegion.getAttributes().getEvictionAttributes(),
			EvictionAction.LOCAL_DESTROY, EvictionAlgorithm.LRU_ENTRY, 4096, null);
		assertExpirationAttributes(templateBasedLocalRegion.getAttributes().getEntryIdleTimeout(),
			ExpirationAction.DESTROY, 600);
		assertExpirationAttributes(templateBasedLocalRegion.getAttributes().getEntryTimeToLive(),
			ExpirationAction.INVALIDATE, 300);
		assertThat(templateBasedLocalRegion.getAttributes().getInitialCapacity()).isEqualTo(51);
		assertThat(templateBasedLocalRegion.getAttributes().getKeyConstraint()).isEqualTo(Long.class);
		assertThat(String.valueOf(templateBasedLocalRegion.getAttributes().getLoadFactor())).isEqualTo("0.85");
		assertThat(templateBasedLocalRegion.getAttributes().isLockGrantor()).isFalse();
		assertThat(templateBasedLocalRegion.getAttributes().getPartitionAttributes()).isNull();
		assertThat(templateBasedLocalRegion.getAttributes().getStatisticsEnabled()).isTrue();
		assertDefaultSubscriptionAttributes(templateBasedLocalRegion.getAttributes().getSubscriptionAttributes());
		assertThat(templateBasedLocalRegion.getAttributes().getValueConstraint()).isEqualTo(String.class);
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
