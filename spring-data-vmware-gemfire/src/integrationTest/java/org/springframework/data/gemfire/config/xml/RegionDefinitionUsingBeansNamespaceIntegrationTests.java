/*
 * Copyright 2022-2025 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.config.xml;

import org.apache.geode.cache.DataPolicy;
import org.apache.geode.cache.Region;
import org.apache.geode.cache.RegionAttributes;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.data.gemfire.TestUtils;
import org.springframework.data.gemfire.client.ClientRegionFactoryBean;
import org.springframework.data.gemfire.tests.integration.IntegrationTestsSupport;
import org.springframework.data.gemfire.tests.unit.annotation.GemFireUnitTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Integration Tests testing the contract and functionality of the SDG {@link ClientRegionFactoryBean} class,
 * and specifically the specification of the Apache Geode {@link Region} {@link DataPolicy} when used as
 * raw bean definition in Spring XML configuration metadata.
 *
 * @author John Blum
 * @see org.junit.Test
 * @see org.apache.geode.cache.DataPolicy
 * @see org.apache.geode.cache.Region
 * @see org.springframework.data.gemfire.client.ClientRegionFactoryBean
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

		Assertions.assertThat(example).as("The '/Example' Region was not properly configured and initialized").isNotNull();
		Assertions.assertThat(example.getName()).isEqualTo("Example");
		Assertions.assertThat(example.getFullPath()).isEqualTo("/Example");
		Assertions.assertThat(example.getAttributes()).isNotNull();
		Assertions.assertThat(example.getAttributes().getDataPolicy()).isEqualTo(DataPolicy.NORMAL);
		Assertions.assertThat(example.getAttributes().getStatisticsEnabled()).isTrue();
		Assertions.assertThat(example.getAttributes().getPartitionAttributes()).isNull();
	}

	@Test
	public void testAnotherExampleRegionFactoryBeanConfiguration() throws Exception {

		ClientRegionFactoryBean<?, ?> anotherExampleRegionFactoryBean =
			applicationContext.getBean("&AnotherExample", ClientRegionFactoryBean.class);

		Assertions.assertThat(anotherExampleRegionFactoryBean).isNotNull();
		Assertions.assertThat(Boolean.TRUE.equals(TestUtils.readField("persistent", anotherExampleRegionFactoryBean))).isTrue();

		RegionAttributes<?, ?> anotherExampleRegionAttributes =
			TestUtils.readField("attributes", anotherExampleRegionFactoryBean);

		Assertions.assertThat(anotherExampleRegionAttributes).isNotNull();
		Assertions.assertThat(anotherExampleRegionAttributes.getDataPolicy()).isEqualTo(DataPolicy.NORMAL);
	}

	@Test
	public void testAnotherExampleRegionDefinitionConfiguration() {

		Assertions.assertThat(anotherExample).as("The '/AnotherExample' Region was not properly configured and initialized")
			.isNotNull();
		Assertions.assertThat(anotherExample.getName()).isEqualTo("AnotherExample");
		Assertions.assertThat(anotherExample.getFullPath()).isEqualTo("/AnotherExample");
		Assertions.assertThat(anotherExample.getAttributes()).isNotNull();
		Assertions.assertThat(anotherExample.getAttributes().getDataPolicy()).isEqualTo(DataPolicy.PERSISTENT_REPLICATE);
		Assertions.assertThat(anotherExample.getAttributes().getPartitionAttributes()).isNull();
	}

	public static final class TestRegionFactoryBean<K, V> extends ClientRegionFactoryBean<K, V> { }

}
