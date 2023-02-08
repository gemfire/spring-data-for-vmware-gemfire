/*
 * Copyright (c) VMware, Inc. 2022-2023. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.config.xml;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import org.junit.Test;
import org.junit.runner.RunWith;

import org.apache.geode.cache.CustomExpiry;
import org.apache.geode.cache.DataPolicy;
import org.apache.geode.cache.ExpirationAction;
import org.apache.geode.cache.ExpirationAttributes;
import org.apache.geode.cache.Region;

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
 * @see Test
 * @see org.mockito.Mockito
 * @see ExpirationAttributes
 * @see Region
 * @see IntegrationTestsSupport
 * @see GemFireUnitTest
 * @see org.springframework.test.context.ContextConfiguration
 * @see SpringRunner
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
	@Qualifier("PartitionExample")
	private Region<?, ?> partitionExample;

	@Autowired
	@Qualifier("LocalExample")
	private Region<?, ?> localExample;

	private void assertRegionMetaData(final Region<?, ?> region, final String regionName, final DataPolicy dataPolicy) {
		assertRegionMetaData(region, regionName, Region.SEPARATOR + regionName, dataPolicy);
	}

	private void assertRegionMetaData(Region<?, ?> region, String regionName, String regionFullPath,
			DataPolicy dataPolicy) {

		assertThat(region)
			.as(String.format("The '%1$s' Region was not properly configured and initialized", regionName))
			.isNotNull();
		assertThat(region.getName()).isEqualTo(regionName);
		assertThat(region.getFullPath()).isEqualTo(regionFullPath);
		assertThat(region.getAttributes()).isNotNull();
		assertThat(region.getAttributes().getDataPolicy()).isEqualTo(dataPolicy);
	}

	private void assertNoExpiration(final ExpirationAttributes expirationAttributes) {

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
	public void testReplicateExampleExpirationAttributes() {

		assertRegionMetaData(replicateExample, "ReplicateExample", DataPolicy.REPLICATE);
		assertExpirationAttributes(replicateExample.getAttributes().getEntryTimeToLive(),
			600, ExpirationAction.DESTROY);
		assertExpirationAttributes(replicateExample.getAttributes().getEntryIdleTimeout(),
			300, ExpirationAction.INVALIDATE);
		assertThat(replicateExample.getAttributes().getCustomEntryTimeToLive()).isNull();
		assertThat(replicateExample.getAttributes().getCustomEntryIdleTimeout()).isNull();
	}

	@Test
	public void testPreloadedExampleExpirationAttributes() {

		assertRegionMetaData(preloadedExample, "PreloadedExample", DataPolicy.PRELOADED);
		assertExpirationAttributes(preloadedExample.getAttributes().getEntryTimeToLive(),
			120, ExpirationAction.LOCAL_DESTROY);
		assertNoExpiration(preloadedExample.getAttributes().getEntryIdleTimeout());
		assertThat(preloadedExample.getAttributes().getCustomEntryTimeToLive()).isNull();
		assertThat(preloadedExample.getAttributes().getCustomEntryIdleTimeout()).isNull();
	}

	@Test
	public void testPartitionExampleExpirationAttributes() {

		assertRegionMetaData(partitionExample, "PartitionExample", DataPolicy.PARTITION);
		assertExpirationAttributes(partitionExample.getAttributes().getEntryTimeToLive(),
			300, ExpirationAction.DESTROY);
		assertNoExpiration(partitionExample.getAttributes().getEntryIdleTimeout());
		assertThat(partitionExample.getAttributes().getCustomEntryTimeToLive()).isNull();
		assertCustomExpiry(partitionExample.getAttributes().getCustomEntryIdleTimeout(), "PartitionCustomExpiry",
			120, ExpirationAction.INVALIDATE);
	}

	@Test
	public void testLocalExampleExpirationAttributes() {

		assertRegionMetaData(localExample, "LocalExample", DataPolicy.NORMAL);
		assertNoExpiration(localExample.getAttributes().getEntryTimeToLive());
		assertNoExpiration(localExample.getAttributes().getEntryIdleTimeout());
		assertCustomExpiry(localExample.getAttributes().getCustomEntryTimeToLive(), "LocalTtlCustomExpiry",
			180, ExpirationAction.LOCAL_DESTROY);
		assertCustomExpiry(localExample.getAttributes().getCustomEntryIdleTimeout(), "LocalTtiCustomExpiry",
			60, ExpirationAction.LOCAL_INVALIDATE);
	}

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

		public void setAction(final ExpirationAction action) {
			this.action = action;
		}

		public void setName(final String name) {
			this.name = name;
		}

		public void setTimeout(final Integer timeout) {
			this.timeout = timeout;
		}

		@Override
		public void close() {
		}

		@Override
		public String toString() {
			return this.name;
		}
	}
}
