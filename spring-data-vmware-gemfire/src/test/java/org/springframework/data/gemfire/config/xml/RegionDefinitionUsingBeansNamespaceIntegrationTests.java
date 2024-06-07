/*
 * Copyright 2022-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.config.xml;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;

import org.apache.geode.cache.DataPolicy;
import org.apache.geode.cache.PartitionAttributes;
import org.apache.geode.cache.Region;
import org.apache.geode.cache.RegionAttributes;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.data.gemfire.PeerRegionFactoryBean;
import org.springframework.data.gemfire.TestUtils;
import org.springframework.data.gemfire.tests.integration.IntegrationTestsSupport;
import org.springframework.data.gemfire.tests.unit.annotation.GemFireUnitTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Integration Tests testing the contract and functionality of the SDG {@link PeerRegionFactoryBean} class,
 * and specifically the specification of the Apache Geode {@link Region} {@link DataPolicy} when used as
 * raw bean definition in Spring XML configuration metadata.
 *
 * @author John Blum
 * @see org.junit.Test
 * @see org.apache.geode.cache.DataPolicy
 * @see org.apache.geode.cache.Region
 * @see org.springframework.data.gemfire.PeerRegionFactoryBean
 * @see org.springframework.data.gemfire.RegionAttributesFactoryBean
 * @see org.springframework.data.gemfire.tests.integration.IntegrationTestsSupport
 * @see org.springframework.data.gemfire.tests.unit.annotation.GemFireUnitTest
 * @see org.springframework.test.context.ContextConfiguration
 * @see org.springframework.test.context.junit4.SpringJUnit4ClassRunner
 * @since 1.6.0
 */
@RunWith(SpringRunner.class)
@GemFireUnitTest
@SuppressWarnings("unused")
public class RegionDefinitionUsingBeansNamespaceIntegrationTests extends IntegrationTestsSupport {

	@Autowired
	private ApplicationContext applicationContext;

	@Autowired
	@Qualifier("Example")
	private Region<?, ?> example;

	@Autowired
	@Qualifier("AnotherExample")
	private Region<?, ?> anotherExample;

	@Test
	public void testExampleRegionBeanDefinitionConfiguration() {

		assertThat(example).as("The '/Example' Region was not properly configured and initialized").isNotNull();
		assertThat(example.getName()).isEqualTo("Example");
		assertThat(example.getFullPath()).isEqualTo("/Example");
		assertThat(example.getAttributes()).isNotNull();
		assertThat(example.getAttributes().getDataPolicy()).isEqualTo(DataPolicy.PERSISTENT_REPLICATE);
		assertThat(example.getAttributes().getStatisticsEnabled()).isTrue();
		assertThat(example.getAttributes().getPartitionAttributes()).isNull();
	}

	@Test
	public void testAnotherExampleRegionFactoryBeanConfiguration() throws Exception {

		PeerRegionFactoryBean<?, ?> anotherExampleRegionFactoryBean =
			applicationContext.getBean("&AnotherExample", PeerRegionFactoryBean.class);

		assertThat(anotherExampleRegionFactoryBean).isNotNull();
		assertThat(anotherExampleRegionFactoryBean.getDataPolicy()).isEqualTo(DataPolicy.PERSISTENT_REPLICATE);
		assertThat(Boolean.TRUE.equals(TestUtils.readField("persistent", anotherExampleRegionFactoryBean))).isTrue();

		RegionAttributes<?, ?> anotherExampleRegionAttributes =
			TestUtils.readField("attributes", anotherExampleRegionFactoryBean);

		assertThat(anotherExampleRegionAttributes).isNotNull();
		assertThat(anotherExampleRegionAttributes.getDataPolicy()).isEqualTo(DataPolicy.REPLICATE);
	}

	@Test
	public void testAnotherExampleRegionDefinitionConfiguration() {

		assertThat(anotherExample).as("The '/AnotherExample' Region was not properly configured and initialized")
			.isNotNull();
		assertThat(anotherExample.getName()).isEqualTo("AnotherExample");
		assertThat(anotherExample.getFullPath()).isEqualTo("/AnotherExample");
		assertThat(anotherExample.getAttributes()).isNotNull();
		assertThat(anotherExample.getAttributes().getDataPolicy()).isEqualTo(DataPolicy.PERSISTENT_REPLICATE);
		assertThat(anotherExample.getAttributes().getPartitionAttributes()).isNull();
	}

	public static final class TestRegionFactoryBean<K, V> extends PeerRegionFactoryBean<K, V> { }

}
