/*
 * Copyright (c) VMware, Inc. 2022-2024. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire;

import java.util.Map;

import org.apache.geode.cache.DiskStore;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.gemfire.tests.integration.IntegrationTestsSupport;
import org.springframework.data.gemfire.tests.unit.annotation.GemFireUnitTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Integration Tests testing the use of Spring PropertyPlaceholders to configure and initialize a {@link DiskStore} bean
 * properties using property placeholders in the SDG XML namespace &lt;disk-store&gt; bean definition attributes.
 *
 * @author John Blum
 * @see org.junit.Test
 * @see org.apache.geode.cache.DiskStore
 * @see org.springframework.data.gemfire.DiskStoreFactoryBean
 * @see org.springframework.data.gemfire.tests.integration.IntegrationTestsSupport
 * @see org.springframework.data.gemfire.tests.mock.context.GemFireMockObjectsApplicationContextInitializer
 * @see org.springframework.test.context.ContextConfiguration
 * @see org.springframework.test.context.junit4.SpringRunner
 * @link https://jira.springsource.org/browse/SGF-249
 * @since 1.3.4
 */
@RunWith(SpringRunner.class)
@GemFireUnitTest
@SuppressWarnings("unused")
public class DiskStoreBeanUsingPropertyPlaceholdersIntegrationTests extends IntegrationTestsSupport {

	@Autowired
	private DiskStore testDataStore;

	@Autowired
	@Qualifier("diskStoreConfiguration")
	private Map<Object, Object> diskStoreConfiguration;

	@Before
	public void assertDiskStoreConfiguration() {

		//System.err.printf("Map of Type [%s]%n", ObjectUtils.nullSafeClassName(this.diskStoreConfiguration));
		//System.err.printf("Map with Contents [%s]%n", this.diskStoreConfiguration);
		Assertions.assertThat(this.diskStoreConfiguration).isNotNull();
		Assertions.assertThat(String.valueOf(this.diskStoreConfiguration.get("allowForceCompaction"))).isEqualTo("false");
		Assertions.assertThat(String.valueOf(this.diskStoreConfiguration.get("writeBufferSize"))).isEqualTo("65536");
	}

	private Object getExpectedValue(String propertyPlaceholderName) {
		return this.diskStoreConfiguration.get(propertyPlaceholderName);
	}

	@Test
	public void testDiskStoreBeanWithPropertyPlaceholderConfiguration() {

		Assertions.assertThat(testDataStore).describedAs("The Disk Store was not configured and initialized").isNotNull();
		Assertions.assertThat(testDataStore.getAllowForceCompaction()).isEqualTo(getExpectedValue("allowForceCompaction"));
		Assertions.assertThat(testDataStore.getAutoCompact()).isEqualTo(getExpectedValue("autoCompact"));
		Assertions.assertThat(testDataStore.getCompactionThreshold()).isEqualTo(getExpectedValue("compactionThreshold"));
		Assertions.assertThat(testDataStore.getMaxOplogSize()).isEqualTo(getExpectedValue("maxOplogSize"));
		Assertions.assertThat(testDataStore.getName()).isEqualTo("TestDataStore");
		Assertions.assertThat(testDataStore.getQueueSize()).isEqualTo(getExpectedValue("queueSize"));
		Assertions.assertThat(testDataStore.getTimeInterval()).isEqualTo(getExpectedValue("timeInterval"));
		Assertions.assertThat(testDataStore.getWriteBufferSize()).isEqualTo(getExpectedValue("writeBufferSize"));
	}
}
