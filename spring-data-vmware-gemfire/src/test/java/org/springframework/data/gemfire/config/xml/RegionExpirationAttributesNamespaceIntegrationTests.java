/*
 * Copyright 2022-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.config.xml;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import org.junit.Test;
import org.junit.runner.RunWith;

import org.apache.geode.cache.CacheListener;
import org.apache.geode.cache.CacheLoader;
import org.apache.geode.cache.CacheLoaderException;
import org.apache.geode.cache.CustomExpiry;
import org.apache.geode.cache.DataPolicy;
import org.apache.geode.cache.ExpirationAction;
import org.apache.geode.cache.ExpirationAttributes;
import org.apache.geode.cache.LoaderHelper;
import org.apache.geode.cache.Region;
import org.apache.geode.cache.util.CacheListenerAdapter;
import org.apache.geode.cache.util.CacheWriterAdapter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.gemfire.tests.integration.IntegrationTestsSupport;
import org.springframework.data.gemfire.tests.unit.annotation.GemFireUnitTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.Assert;

/**
 * Integration Tests testing the configuration of {@link ExpirationAttributes} settings on {@link Region} entries.
 *
 * @author John Blum
 * @see org.junit.Test
 * @see org.mockito.Mockito
 * @see org.apache.geode.cache.ExpirationAttributes
 * @see org.apache.geode.cache.Region
 * @see org.springframework.data.gemfire.tests.integration.IntegrationTestsSupport
 * @see org.springframework.data.gemfire.tests.unit.annotation.GemFireUnitTest
 * @see org.springframework.test.context.ContextConfiguration
 * @see org.springframework.test.context.junit4.SpringRunner
 * @since 1.5.0
 */
@RunWith(SpringRunner.class)
@GemFireUnitTest
@SuppressWarnings("unused")
public class RegionExpirationAttributesNamespaceIntegrationTests extends IntegrationTestsSupport {

	@Autowired
	@Qualifier("ReplicateExample")
	private Region<?, ?> replicateExample;

	@Autowired
	@Qualifier("PreloadedExample")
	private Region<?, ?> preloadedExample;

	@Autowired
	@Qualifier("LocalExample")
	private Region<?, ?> localExample;

	private void assertRegionMetaData(Region<?, ?> region, String regionName, DataPolicy dataPolicy) {
		assertRegionMetaData(region, regionName, Region.SEPARATOR + regionName, dataPolicy);
	}

	private void assertRegionMetaData(Region<?, ?> region,
			String regionName, String regionFullPath, DataPolicy dataPolicy) {

		assertThat(region)
			.describedAs("The '%s' Region was not properly configured and initialized", regionName)
			.isNotNull();

		assertThat(region.getName()).isEqualTo(regionName);
		assertThat(region.getFullPath()).isEqualTo(regionFullPath);
		assertThat(region.getAttributes()).isNotNull();
		assertThat(region.getAttributes().getDataPolicy()).isEqualTo(dataPolicy);
	}

	private void assertNoExpiration(ExpirationAttributes expirationAttributes) {

		if (expirationAttributes != null) {
			//assertEquals(ExpirationAction.INVALIDATE, expirationAttributes.getAction());
			assertThat(expirationAttributes.getTimeout()).isEqualTo(0);
		}
	}

	private void assertExpirationAttributes(ExpirationAttributes expirationAttributes,
			int timeout, ExpirationAction action) {

		assertThat(expirationAttributes).isNotNull();
		assertThat(expirationAttributes.getTimeout()).isEqualTo(timeout);
		assertThat(expirationAttributes.getAction()).isEqualTo(action);
	}

	@SuppressWarnings("unchecked")
	private void assertCustomExpiry(CustomExpiry<?, ?> customExpiry, String name, int timeout,
			ExpirationAction action) {

		assertThat(customExpiry).isNotNull();
		assertThat(customExpiry.toString()).isEqualTo(name);
		assertExpirationAttributes(customExpiry.getExpiry(mock(Region.Entry.class)), timeout, action);
	}

	@Test
	public void exampleReplicateRegionExpirationAttributesAreCorrect() {

		assertRegionMetaData(replicateExample, "ReplicateExample", DataPolicy.REPLICATE);
		assertExpirationAttributes(replicateExample.getAttributes().getEntryTimeToLive(),
			600, ExpirationAction.DESTROY);
		assertExpirationAttributes(replicateExample.getAttributes().getEntryIdleTimeout(),
			300, ExpirationAction.INVALIDATE);
		assertThat(replicateExample.getAttributes().getCustomEntryTimeToLive()).isNull();
		assertThat(replicateExample.getAttributes().getCustomEntryIdleTimeout()).isNull();
	}

	@Test
	public void examplePreloadedRegionExpirationAttributesAreCorrect() {

		assertRegionMetaData(preloadedExample, "PreloadedExample", DataPolicy.PRELOADED);
		assertExpirationAttributes(preloadedExample.getAttributes().getEntryTimeToLive(),
			120, ExpirationAction.LOCAL_DESTROY);
		assertNoExpiration(preloadedExample.getAttributes().getEntryIdleTimeout());
		assertThat(preloadedExample.getAttributes().getCustomEntryTimeToLive()).isNull();
		assertThat(preloadedExample.getAttributes().getCustomEntryIdleTimeout()).isNull();
	}

	@Test
	public void exampleLocalRegionExpirationAttributesAreCorrect() {

		assertRegionMetaData(localExample, "LocalExample", DataPolicy.NORMAL);
		assertNoExpiration(localExample.getAttributes().getEntryTimeToLive());
		assertNoExpiration(localExample.getAttributes().getEntryIdleTimeout());
		assertCustomExpiry(localExample.getAttributes().getCustomEntryTimeToLive(), "LocalTtlCustomExpiry",
			180, ExpirationAction.LOCAL_DESTROY);
		assertCustomExpiry(localExample.getAttributes().getCustomEntryIdleTimeout(), "LocalTtiCustomExpiry",
			60, ExpirationAction.LOCAL_INVALIDATE);
	}

	public static class TestCacheListener<K, V> extends CacheListenerAdapter<K, V> { }

	public static class TestCacheLoader<K, V> implements CacheLoader<K, V> {

		@Override
		public V load(LoaderHelper<K, V> loaderHelper) throws CacheLoaderException {
			return null;
		}
	}

	public static class TestCacheWriter<K, V> extends CacheWriterAdapter<K, V> { }

	public static class TestCustomExpiry<K, V> implements CustomExpiry<K, V> {

		private ExpirationAction action;

		private Integer timeout;

		private String name;

		@Override
		public ExpirationAttributes getExpiry(final Region.Entry<K, V> kvEntry) {
			Assert.state(timeout != null, "The expiration 'timeout' must be specified");
			Assert.state(action != null, "The expiration 'action' must be specified");
			return new ExpirationAttributes(timeout, action);
		}

		public void setAction(ExpirationAction action) {
			this.action = action;
		}

		public void setName(String name) {
			this.name = name;
		}

		public void setTimeout(Integer timeout) {
			this.timeout = timeout;
		}

		@Override
		public void close() { }

		@Override
		public String toString() {
			return this.name;
		}
	}
}
