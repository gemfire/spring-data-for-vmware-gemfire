/*
 * Copyright 2022-2025 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.config.xml;

import java.io.File;
import java.util.Properties;

import org.apache.geode.cache.CustomExpiry;
import org.apache.geode.cache.DiskStore;
import org.apache.geode.cache.DiskStoreFactory;
import org.apache.geode.cache.EvictionAction;
import org.apache.geode.cache.EvictionAlgorithm;
import org.apache.geode.cache.EvictionAttributes;
import org.apache.geode.cache.ExpirationAction;
import org.apache.geode.cache.ExpirationAttributes;
import org.apache.geode.cache.Region;
import org.apache.geode.cache.RegionAttributes;
import org.apache.geode.cache.Scope;
import org.apache.geode.cache.client.ClientCache;
import org.assertj.core.api.Assertions;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.gemfire.TestUtils;
import org.springframework.data.gemfire.client.ClientRegionFactoryBean;
import org.springframework.data.gemfire.tests.integration.IntegrationTestsSupport;
import org.springframework.data.gemfire.tests.unit.annotation.GemFireUnitTest;
import org.springframework.data.gemfire.tests.util.FileSystemUtils;
import org.springframework.data.gemfire.util.ArrayUtils;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Integration Tests testing the contract and functionality of using SDG's XML namespace configuration metadata to
 * configure {@link DiskStore DiskStores}.
 *
 * @author John Blum
 * @see org.junit.Test
 * @see org.apache.geode.cache.DiskStore
 * @see org.springframework.data.gemfire.DiskStoreFactoryBean
 * @see org.springframework.data.gemfire.tests.integration.IntegrationTestsSupport
 * @see org.springframework.data.gemfire.tests.unit.annotation.GemFireUnitTest
 * @see org.springframework.test.context.ContextConfiguration
 * @see org.springframework.test.context.junit4.SpringRunner
 * @since 2.0.0
 */
@RunWith(SpringRunner.class)
@GemFireUnitTest
@SuppressWarnings("unused")
public class DiskStoreNamespaceIntegrationTests extends IntegrationTestsSupport {

	private static File diskStoreDirectory;

	@Autowired
	private ClientCache cache;

	@Autowired
	@Qualifier("diskStore1")
	private DiskStore diskStoreOne;

	@Autowired
	@Qualifier("ds2")
	private DiskStore diskStoreTwo;

	@Autowired
	@Qualifier("fullyConfiguredDiskStore")
	private DiskStore fullyConfiguredDiskStore;

	@Autowired
	@Qualifier("diskStoreProperties")
	private Properties diskStoreProperties;

	@BeforeClass
	public static void createDiskStoreDirectory() {
		IntegrationTestsSupport.createDirectory(diskStoreDirectory = new File("./tmp"));
		diskStoreDirectory.deleteOnExit();
	}

	@AfterClass
	public static void deleteDiskStoreDirectory() {

		FileSystemUtils.deleteRecursive(diskStoreDirectory);

		for (String name : ArrayUtils.nullSafeArray(FileSystemUtils.WORKING_DIRECTORY
			.list((dir, name) -> name.startsWith("BACKUP")), String.class)) {
			new File(name).delete();
		}
	}

	@Test
	public void diskStoreOneIsAccessibleFromTheCache() {
		Assertions.assertThat(this.cache.findDiskStore("diskStore1")).isSameAs(this.diskStoreOne);
	}

	@Test
	public void diskStoreTwoConfigurationIsCorrect() {

		Assertions.assertThat(diskStoreTwo).isNotNull();
		Assertions.assertThat(diskStoreTwo.getName()).isEqualTo("ds2");
		Assertions.assertThat(diskStoreTwo.getQueueSize()).isEqualTo(50);
		Assertions.assertThat(diskStoreTwo.getAutoCompact()).isTrue();
		Assertions.assertThat(diskStoreTwo.getCompactionThreshold()).isEqualTo(DiskStoreFactory.DEFAULT_COMPACTION_THRESHOLD);
		Assertions.assertThat(diskStoreTwo.getTimeInterval()).isEqualTo(9999);
		Assertions.assertThat(diskStoreTwo.getMaxOplogSize()).isEqualTo(1);
		Assertions.assertThat(diskStoreTwo.getDiskDirs()[0]).isEqualTo(diskStoreDirectory.getParentFile());
	}

	@Test
	public void fullyConfiguredDiskStoreConfigurationIsCorrect() {

		Assertions.assertThat(fullyConfiguredDiskStore)
			.describedAs("The 'fullyConfiguredDiskStore' was not properly configured and initialized")
			.isNotNull();

		Assertions.assertThat(fullyConfiguredDiskStore.getName()).isEqualTo("fullyConfiguredDiskStore");
		Assertions.assertThat(fullyConfiguredDiskStore.getAllowForceCompaction()).isEqualTo(Boolean.valueOf(diskStoreProperties.getProperty("allowForceCompaction")));
		Assertions.assertThat(fullyConfiguredDiskStore.getAutoCompact()).isEqualTo(Boolean.valueOf(diskStoreProperties.getProperty("autoCompact")));
		Assertions.assertThat(Long.valueOf(fullyConfiguredDiskStore.getCompactionThreshold())).isEqualTo(Long.valueOf(diskStoreProperties.getProperty("compactionThreshold")));
		Assertions.assertThat(Double.valueOf(fullyConfiguredDiskStore.getDiskUsageCriticalPercentage())).isEqualTo(Double.valueOf(diskStoreProperties.getProperty("diskUsageCriticalPercentage")));
		Assertions.assertThat(Double.valueOf(fullyConfiguredDiskStore.getDiskUsageWarningPercentage())).isEqualTo(Double.valueOf(diskStoreProperties.getProperty("diskUsageWarningPercentage")));
		Assertions.assertThat(Long.valueOf(fullyConfiguredDiskStore.getMaxOplogSize())).isEqualTo(Long.valueOf(diskStoreProperties.getProperty("maxOplogSize")));
		Assertions.assertThat(Long.valueOf(fullyConfiguredDiskStore.getQueueSize())).isEqualTo(Long.valueOf(diskStoreProperties.getProperty("queueSize")));
		Assertions.assertThat(Long.valueOf(fullyConfiguredDiskStore.getTimeInterval())).isEqualTo(Long.valueOf(diskStoreProperties.getProperty("timeInterval")));
		Assertions.assertThat(Long.valueOf(fullyConfiguredDiskStore.getWriteBufferSize())).isEqualTo(Long.valueOf(diskStoreProperties.getProperty("writeBufferSize")));
		Assertions.assertThat(Long.valueOf(fullyConfiguredDiskStore.getSegments())).isEqualTo(Long.valueOf(diskStoreProperties.getProperty("segments")));
		Assertions.assertThat(fullyConfiguredDiskStore.getDiskDirs()).isNotNull();
		Assertions.assertThat(fullyConfiguredDiskStore.getDiskDirs().length).isEqualTo(1);
		Assertions.assertThat(fullyConfiguredDiskStore.getDiskDirs()[0]).isEqualTo(new File(diskStoreProperties.getProperty("location")));
		Assertions.assertThat(Long.valueOf(fullyConfiguredDiskStore.getDiskDirSizes()[0])).isEqualTo(Long.valueOf(diskStoreProperties.getProperty("maxSize")));
	}

	@Test
	@SuppressWarnings("rawtypes")
	public void localDataRegionAttributesIsConfiguredCorrectly() throws Exception {

		Assertions.assertThat(requireApplicationContext().containsBean("local-data")).isTrue();

		ClientRegionFactoryBean localDataRegionFactoryBean =
			requireApplicationContext().getBean("&local-data", ClientRegionFactoryBean.class);

		Assertions.assertThat(localDataRegionFactoryBean instanceof ClientRegionFactoryBean).isTrue();

		Assertions.assertThat(TestUtils.<String>readField("diskStoreName", localDataRegionFactoryBean)).isEqualTo("diskStore1");

		Region localDataRegion = requireApplicationContext().getBean("local-data", Region.class);

		RegionAttributes localDataRegionAttributes = TestUtils.readField("attributes", localDataRegionFactoryBean);

		Assertions.assertThat(localDataRegionAttributes).isNotNull();
		Assertions.assertThat(localDataRegionAttributes.getScope()).isEqualTo(Scope.DISTRIBUTED_NO_ACK);

		EvictionAttributes localDataEvictionAttributes = localDataRegionAttributes.getEvictionAttributes();

		Assertions.assertThat(localDataEvictionAttributes).isNotNull();
		Assertions.assertThat(localDataEvictionAttributes.getAction()).isEqualTo(EvictionAction.OVERFLOW_TO_DISK);
		Assertions.assertThat(localDataEvictionAttributes.getAlgorithm()).isEqualTo(EvictionAlgorithm.LRU_ENTRY);
		Assertions.assertThat(localDataEvictionAttributes.getMaximum()).isEqualTo(50);
		Assertions.assertThat(localDataEvictionAttributes.getObjectSizer()).isNull();
	}

	@Test
	@SuppressWarnings("rawtypes")
	public void entryTtlConfigurationIsCorrect() throws Exception {

		Assertions.assertThat(requireApplicationContext().containsBean("local-data")).isTrue();

		ClientRegionFactoryBean regionFactoryBean =
			requireApplicationContext().getBean("&local-data", ClientRegionFactoryBean.class);

		RegionAttributes regionAttributes = TestUtils.readField("attributes", regionFactoryBean);

		ExpirationAttributes entryTTL = regionAttributes.getEntryTimeToLive();

		Assertions.assertThat(entryTTL.getTimeout()).isEqualTo(100);
		Assertions.assertThat(entryTTL.getAction()).isEqualTo(ExpirationAction.DESTROY);

		ExpirationAttributes entryTTI = regionAttributes.getEntryIdleTimeout();

		Assertions.assertThat(entryTTI.getTimeout()).isEqualTo(200);
		Assertions.assertThat(entryTTI.getAction()).isEqualTo(ExpirationAction.INVALIDATE);

		ExpirationAttributes regionTTL = regionAttributes.getRegionTimeToLive();

		Assertions.assertThat(regionTTL.getTimeout()).isEqualTo(300);
		Assertions.assertThat(regionTTL.getAction()).isEqualTo(ExpirationAction.DESTROY);

		ExpirationAttributes regionTTI = regionAttributes.getRegionIdleTimeout();

		Assertions.assertThat(regionTTI.getTimeout()).isEqualTo(400);
		Assertions.assertThat(regionTTI.getAction()).isEqualTo(ExpirationAction.INVALIDATE);
	}

	@Test
	@SuppressWarnings("rawtypes")
	public void testCustomExpiry() throws Exception {

		Assertions.assertThat(requireApplicationContext().containsBean("local-data-with-custom-expiry")).isTrue();

		ClientRegionFactoryBean regionFactoryBean =
			requireApplicationContext().getBean("&local-data-with-custom-expiry", ClientRegionFactoryBean.class);

		RegionAttributes regionAttributes = TestUtils.readField("attributes", regionFactoryBean);

		Assertions.assertThat(regionAttributes.getCustomEntryIdleTimeout()).isInstanceOf(TestCustomExpiry.class);
		Assertions.assertThat(regionAttributes.getCustomEntryTimeToLive()).isInstanceOf(TestCustomExpiry.class);
	}

	public static class TestCustomExpiry<K,V> implements CustomExpiry<K,V> {

		@Override
		public ExpirationAttributes getExpiry(Region.Entry<K, V> entry) {
			return null;
		}

		@Override
		public void close() { }

	}
}
