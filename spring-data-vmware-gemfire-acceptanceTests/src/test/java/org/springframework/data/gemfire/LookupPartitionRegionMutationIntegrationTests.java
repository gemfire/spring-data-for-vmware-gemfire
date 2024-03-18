/*
 * Copyright (c) VMware, Inc. 2022-2024. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.apache.geode.cache.CacheListener;
import org.apache.geode.cache.CacheLoader;
import org.apache.geode.cache.CacheLoaderException;
import org.apache.geode.cache.CacheWriter;
import org.apache.geode.cache.CacheWriterException;
import org.apache.geode.cache.CustomExpiry;
import org.apache.geode.cache.DataPolicy;
import org.apache.geode.cache.EntryEvent;
import org.apache.geode.cache.EvictionAction;
import org.apache.geode.cache.EvictionAlgorithm;
import org.apache.geode.cache.EvictionAttributes;
import org.apache.geode.cache.ExpirationAction;
import org.apache.geode.cache.ExpirationAttributes;
import org.apache.geode.cache.LoaderHelper;
import org.apache.geode.cache.Region;
import org.apache.geode.cache.RegionEvent;
import org.apache.geode.cache.asyncqueue.AsyncEvent;
import org.apache.geode.cache.asyncqueue.AsyncEventListener;
import org.apache.geode.cache.util.CacheListenerAdapter;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.gemfire.tests.integration.IntegrationTestsSupport;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.StringUtils;

/**
 * Integration Tests for {@link DataPolicy#PARTITION} {@link Region} {@link Region#getAttributesMutator() mutation}
 * using SDG's {@literal lookup} {@link Region} functionality.
 *
 * @author Udo Kohlmeyer
 * @author John Blum
 * @see org.junit.Test
 * @see org.springframework.data.gemfire.LookupRegionFactoryBean
 * @see org.springframework.data.gemfire.tests.integration.IntegrationTestsSupport
 * @see org.springframework.test.context.ContextConfiguration
 * @see org.springframework.test.context.junit4.SpringJUnit4ClassRunner
 * @since 1.7.0
 */
@RunWith(SpringRunner.class)
@ContextConfiguration
@SuppressWarnings("unused")
public class LookupPartitionRegionMutationIntegrationTests extends IntegrationTestsSupport {

	@Autowired
	@Qualifier("Example")
	private Region<?, ?> example;

	private void assertCacheListeners(CacheListener<?, ?>[] cacheListeners,
			Collection<String> expectedCacheListenerNames) {

		if (!expectedCacheListenerNames.isEmpty()) {
			Assert.assertNotNull("CacheListeners must not be null", cacheListeners);
			Assert.assertEquals(expectedCacheListenerNames.size(), cacheListeners.length);
			Assert.assertTrue(toStrings(cacheListeners).containsAll(expectedCacheListenerNames));
		}
	}

	private void assertEvictionAttributes(EvictionAttributes evictionAttributes, EvictionAction expectedAction,
			EvictionAlgorithm expectedAlgorithm, int expectedMaximum) {

		Assert.assertNotNull("EvictionAttributes must not be null", evictionAttributes);
		Assert.assertEquals(expectedAction, evictionAttributes.getAction());
		Assert.assertEquals(expectedAlgorithm, evictionAttributes.getAlgorithm());
		Assert.assertEquals(expectedMaximum, evictionAttributes.getMaximum());
	}

	private void assertExpirationAttributes(ExpirationAttributes expirationAttributes,
			String description, int expectedTimeout, ExpirationAction expectedAction) {

		Assert.assertNotNull(String.format("ExpirationAttributes for '%1$s' must not be null", description),
			expirationAttributes);
		Assert.assertEquals(expectedAction, expirationAttributes.getAction());
		Assert.assertEquals(expectedTimeout, expirationAttributes.getTimeout());
	}

	private void assertGatewaySenders(Region<?, ?> region, List<String> expectedGatewaySenderIds) {

		Assert.assertNotNull(region.getAttributes());
		Assert.assertNotNull(region.getAttributes().getGatewaySenderIds());
		Assert.assertEquals(expectedGatewaySenderIds.size(), region.getAttributes().getGatewaySenderIds().size());
		Assert.assertTrue(expectedGatewaySenderIds.containsAll(region.getAttributes().getGatewaySenderIds()));
	}

	private void assertGemFireComponent(Object gemfireComponent, String expectedName) {

		Assert.assertNotNull("The GemFire component must not be null", gemfireComponent);
		Assert.assertEquals(expectedName, gemfireComponent.toString());
	}

	private void assertRegionAttributes(Region<?, ?> region, String expectedName, DataPolicy expectedDataPolicy) {

		assertRegionAttributes(region, expectedName, String.format("%1$s%2$s", Region.SEPARATOR, expectedName),
			expectedDataPolicy);
	}

	private void assertRegionAttributes(Region<?, ?> region, String expectedName, String expectedFullPath,
			DataPolicy expectedDataPolicy) {

		Assert.assertNotNull(String.format("'%1$s' Region was not properly initialized", region));
		Assert.assertEquals(expectedName, region.getName());
		Assert.assertEquals(expectedFullPath, region.getFullPath());
		Assert.assertNotNull(region.getAttributes());
		Assert.assertEquals(expectedDataPolicy, region.getAttributes().getDataPolicy());
	}

	private Collection<String> toStrings(Object[] objects) {

		List<String> cacheListenerNames = new ArrayList<>(objects.length);

		for (Object object : objects) {
			cacheListenerNames.add(object.toString());
		}

		return cacheListenerNames;
	}

	/**
	 * @see <a href="https://issues.apache.org/jira/browse/GEODE-5039">EvictionAttributesMutator.setMaximum does not work</a>
	 */
	@Test
	public void regionConfigurationIsCorrect() {

		assertRegionAttributes(example, "Example", DataPolicy.PARTITION);
		Assert.assertEquals(13, example.getAttributes().getInitialCapacity());
		Assert.assertEquals(0.85f, example.getAttributes().getLoadFactor(), 0.0f);
		assertCacheListeners(example.getAttributes().getCacheListeners(), Arrays.asList("A", "B"));
		assertGemFireComponent(example.getAttributes().getCacheLoader(), "C");
		assertGemFireComponent(example.getAttributes().getCacheWriter(), "D");
		assertEvictionAttributes(example.getAttributes().getEvictionAttributes(), EvictionAction.OVERFLOW_TO_DISK,
			EvictionAlgorithm.LRU_ENTRY, 1000);
		assertGemFireComponent(example.getAttributes().getCustomEntryIdleTimeout(), "E");
		Assert.assertNotNull(example.getAttributes().getAsyncEventQueueIds());
		Assert.assertEquals(1, example.getAttributes().getAsyncEventQueueIds().size());
		Assert.assertEquals("AEQ", example.getAttributes().getAsyncEventQueueIds().iterator().next());
		assertGatewaySenders(example, Collections.singletonList("GWS"));
	}

	interface Nameable extends BeanNameAware {

		String getName();

		void setName(String name);

	}

	static abstract class AbstractNameable implements Nameable {

		private String name;

		public String getName() {
			return name;
		}

		public void setName(final String name) {
			this.name = name;
		}

		@Override
		public void setBeanName(final String name) {
			if (!StringUtils.hasText(this.name)) {
				setName(name);
			}
		}

		@Override
		public String toString() {
			return getName();
		}
	}

	public static final class TestAsyncEventListener extends AbstractNameable implements AsyncEventListener {

		@Override
		public boolean processEvents(List<AsyncEvent> events) {
			throw new UnsupportedOperationException("Not Implemented");
		}

		@Override
		public void close() { }

	}

	public static final class TestCacheListener<K, V> extends CacheListenerAdapter<K, V> implements Nameable {

		private String name;

		public String getName() {
			return name;
		}

		public void setName(final String name) {
			this.name = name;
		}

		@Override
		public void setBeanName(final String name) {
			if (!StringUtils.hasText(this.name)) {
				setName(name);
			}
		}

		@Override
		public String toString() {
			return getName();
		}
	}

	public static final class TestCacheLoader<K, V> extends AbstractNameable implements CacheLoader<K, V> {

		@Override
		public V load(LoaderHelper<K, V> helper) throws CacheLoaderException {
			throw new UnsupportedOperationException("Not Implemented");
		}

		@Override
		public void close() { }

	}

	public static final class TestCacheWriter<K, V> extends AbstractNameable implements CacheWriter<K, V> {

		@Override
		public void beforeUpdate(EntryEvent<K, V> event) throws CacheWriterException { }

		@Override
		public void beforeCreate(EntryEvent<K, V> event) throws CacheWriterException { }

		@Override
		public void beforeDestroy(EntryEvent<K, V> event) throws CacheWriterException { }

		@Override
		public void beforeRegionDestroy(RegionEvent<K, V> event) throws CacheWriterException { }

		@Override
		public void beforeRegionClear(RegionEvent<K, V> event) throws CacheWriterException { }

		@Override
		public void close() { }

	}

	public static final class TestCustomExpiry<K, V> extends AbstractNameable implements CustomExpiry<K, V> {

		@Override
		public ExpirationAttributes getExpiry(Region.Entry<K, V> entry) {
			throw new UnsupportedOperationException("Not Implemented");
		}

		@Override
		public void close() { }

	}
}
