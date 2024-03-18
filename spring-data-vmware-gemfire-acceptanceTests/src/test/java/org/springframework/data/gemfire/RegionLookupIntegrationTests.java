/*
 * Copyright (c) VMware, Inc. 2022-2024. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire;

import org.apache.geode.cache.DataPolicy;
import org.apache.geode.cache.Region;
import org.apache.geode.cache.RegionExistsException;
import org.apache.geode.cache.Scope;
import org.assertj.core.api.Assertions;
import org.junit.After;
import org.junit.Test;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.data.gemfire.tests.integration.IntegrationTestsSupport;

/**
 * Integration Tests testing SDG lookup functionality for various peer {@link Region} types.
 *
 * @author John Blum
 * @see org.junit.Test
 * @see org.apache.geode.cache.Region
 * @see org.springframework.context.ConfigurableApplicationContext
 * @see org.springframework.context.support.ClassPathXmlApplicationContext
 * @see org.springframework.data.gemfire.tests.integration.IntegrationTestsSupport
 * @since 1.4.0
 * @link https://jira.spring.io/browse/SGF-204
 */
public class RegionLookupIntegrationTests extends IntegrationTestsSupport {

	private void assertNoRegionLookup(String configLocation) {

		ConfigurableApplicationContext applicationContext = null;

		try {
			applicationContext = newApplicationContext(configLocation);
			Assertions.fail("Spring ApplicationContext should have thrown a BeanCreationException caused by a RegionExistsException");
		}
		catch (BeanCreationException expected) {

			Assertions.assertThat(expected.getCause() instanceof RegionExistsException)
				.describedAs(expected.getMessage())
				.isTrue();

			throw (RegionExistsException) expected.getCause();

		}
		finally {
			IntegrationTestsSupport.closeApplicationContext(applicationContext);
		}
	}

	private ConfigurableApplicationContext newApplicationContext(String configLocation) {
		return new ClassPathXmlApplicationContext(configLocation);
	}

	@After
	public void cleanupAfterTests() {
		IntegrationTestsSupport.destroyAllGemFireMockObjects();
	}

	@Test
	public void allowRegionBeanDefinitionOverrides() {

		ConfigurableApplicationContext applicationContext = null;

		try {

			applicationContext =
				newApplicationContext("/org/springframework/data/gemfire/allowRegionBeanDefinitionOverridesTest.xml");

			Assertions.assertThat(applicationContext).isNotNull();
			Assertions.assertThat(applicationContext.containsBean("regionOne")).isTrue();

			Region<?, ?> appDataRegion = applicationContext.getBean("regionOne", Region.class);

			Assertions.assertThat(appDataRegion).isNotNull();
			Assertions.assertThat(appDataRegion.getName()).isEqualTo("AppDataRegion");
			Assertions.assertThat(appDataRegion.getFullPath()).isEqualTo("/AppDataRegion");
			Assertions.assertThat(appDataRegion.getAttributes()).isNotNull();
			Assertions.assertThat(appDataRegion.getAttributes().getDataPolicy()).isEqualTo(DataPolicy.PERSISTENT_REPLICATE);
			Assertions.assertThat(appDataRegion.getAttributes().getMulticastEnabled()).isFalse();
			Assertions.assertThat(appDataRegion.getAttributes().getScope()).isEqualTo(Scope.DISTRIBUTED_ACK);
			Assertions.assertThat(appDataRegion.getAttributes().getInitialCapacity()).isEqualTo(101);
			Assertions.assertThat(appDataRegion.getAttributes().getLoadFactor()).isEqualTo(0.85f);
			Assertions.assertThat(appDataRegion.getAttributes().getCloningEnabled()).isTrue();
			Assertions.assertThat(appDataRegion.getAttributes().getConcurrencyChecksEnabled()).isTrue();
			Assertions.assertThat(appDataRegion.getAttributes().getKeyConstraint()).isEqualTo(Integer.class);
			Assertions.assertThat(appDataRegion.getAttributes().getValueConstraint()).isEqualTo(String.class);
		}
		finally {
			IntegrationTestsSupport.closeApplicationContext(applicationContext);
		}
	}

	@Test(expected = RegionExistsException.class)
	public void noClientRegionLookups() {
		assertNoRegionLookup("/org/springframework/data/gemfire/noClientRegionLookupTest.xml");
	}

	@Test(expected = RegionExistsException.class)
	public void noClientSubRegionLookups() {
		assertNoRegionLookup("/org/springframework/data/gemfire/noClientSubRegionLookupTest.xml");
	}

	@Test(expected = RegionExistsException.class)
	public void noDuplicateRegionDefinitions() {
		assertNoRegionLookup("/org/springframework/data/gemfire/noDuplicateRegionDefinitionsTest.xml");
	}

	@Test(expected = RegionExistsException.class)
	public void noLocalRegionLookups() {
		assertNoRegionLookup("/org/springframework/data/gemfire/noLocalRegionLookupTest.xml");
	}

	@Test(expected = RegionExistsException.class)
	public void noPartitionRegionLookups() {
		assertNoRegionLookup("/org/springframework/data/gemfire/noPartitionRegionLookupTest.xml");
	}

	@Test(expected = RegionExistsException.class)
	public void noReplicateRegionLookups() {
		assertNoRegionLookup("/org/springframework/data/gemfire/noReplicateRegionLookupTest.xml");
	}

	@Test(expected = RegionExistsException.class)
	public void noSubRegionLookups() {
		assertNoRegionLookup("/org/springframework/data/gemfire/noSubRegionLookupTest.xml");
	}

	@Test
	public void withEnableClientRegionLookups() {

		ConfigurableApplicationContext applicationContext = null;

		try {

			applicationContext =
				newApplicationContext("/org/springframework/data/gemfire/enableClientRegionLookupsTest.xml");

			Assertions.assertThat(applicationContext).isNotNull();
			Assertions.assertThat(applicationContext.containsBean("NativeClientRegion")).isTrue();
			Assertions.assertThat(applicationContext.containsBean("NativeClientParentRegion")).isTrue();
			Assertions.assertThat(applicationContext.containsBean("/NativeClientParentRegion/NativeClientChildRegion")).isTrue();

			Region<?, ?> nativeClientRegion = applicationContext.getBean("NativeClientRegion", Region.class);

			Assertions.assertThat(nativeClientRegion).isNotNull();
			Assertions.assertThat(nativeClientRegion.getName()).isEqualTo("NativeClientRegion");
			Assertions.assertThat(nativeClientRegion.getFullPath()).isEqualTo("/NativeClientRegion");
			Assertions.assertThat(nativeClientRegion.getAttributes()).isNotNull();
			Assertions.assertThat(nativeClientRegion.getAttributes().getCloningEnabled()).isFalse();
			Assertions.assertThat(nativeClientRegion.getAttributes().getDataPolicy()).isEqualTo(DataPolicy.NORMAL);

			Region<?, ?> nativeClientChildRegion =
				applicationContext.getBean("/NativeClientParentRegion/NativeClientChildRegion", Region.class);

			Assertions.assertThat(nativeClientChildRegion).isNotNull();
			Assertions.assertThat(nativeClientChildRegion.getName()).isEqualTo("NativeClientChildRegion");
			Assertions.assertThat(nativeClientChildRegion.getFullPath())
				.isEqualTo("/NativeClientParentRegion/NativeClientChildRegion");
			Assertions.assertThat(nativeClientChildRegion.getAttributes()).isNotNull();
			Assertions.assertThat(nativeClientChildRegion.getAttributes().getDataPolicy()).isEqualTo(DataPolicy.NORMAL);
		}
		finally {
			IntegrationTestsSupport.closeApplicationContext(applicationContext);
		}
	}

	@Test
	public void withEnableRegionLookups() {

		ConfigurableApplicationContext applicationContext = null;

		try {

			applicationContext =
				newApplicationContext("/org/springframework/data/gemfire/enableRegionLookupsTest.xml");

			Assertions.assertThat(applicationContext).isNotNull();
			Assertions.assertThat(applicationContext.containsBean("NativeLocalRegion")).isTrue();
			Assertions.assertThat(applicationContext.containsBean("NativePartitionRegion")).isTrue();
			Assertions.assertThat(applicationContext.containsBean("NativeReplicateRegion")).isTrue();
			Assertions.assertThat(applicationContext.containsBean("NativeParentRegion")).isTrue();
			Assertions.assertThat(applicationContext.containsBean("/NativeParentRegion/NativeChildRegion")).isTrue();
			Assertions.assertThat(applicationContext.containsBean("SpringReplicateRegion")).isTrue();

			Region<?, ?> nativeLocalRegion = applicationContext.getBean("NativeLocalRegion", Region.class);

			Assertions.assertThat(nativeLocalRegion).isNotNull();
			Assertions.assertThat(nativeLocalRegion.getName()).isEqualTo("NativeLocalRegion");
			Assertions.assertThat(nativeLocalRegion.getFullPath()).isEqualTo("/NativeLocalRegion");
			Assertions.assertThat(nativeLocalRegion.getAttributes()).isNotNull();
			Assertions.assertThat(nativeLocalRegion.getAttributes().getDataPolicy()).isEqualTo(DataPolicy.NORMAL);
			Assertions.assertThat(nativeLocalRegion.getAttributes().getCloningEnabled()).isFalse();
			Assertions.assertThat(nativeLocalRegion.getAttributes().getConcurrencyChecksEnabled()).isFalse();
			Assertions.assertThat(nativeLocalRegion.getAttributes().getConcurrencyLevel()).isEqualTo(80);
			Assertions.assertThat(nativeLocalRegion.getAttributes().getInitialCapacity()).isEqualTo(101);
			Assertions.assertThat(nativeLocalRegion.getAttributes().getKeyConstraint()).isEqualTo(Integer.class);
			Assertions.assertThat(nativeLocalRegion.getAttributes().getLoadFactor()).isEqualTo(0.95f);
			Assertions.assertThat(nativeLocalRegion.getAttributes().getValueConstraint()).isEqualTo(String.class);

			Region<?, ?> nativePartitionRegion = applicationContext.getBean("NativePartitionRegion", Region.class);

			Assertions.assertThat(nativePartitionRegion).isNotNull();
			Assertions.assertThat(nativePartitionRegion.getName()).isEqualTo("NativePartitionRegion");
			Assertions.assertThat(nativePartitionRegion.getFullPath()).isEqualTo("/NativePartitionRegion");
			Assertions.assertThat(nativePartitionRegion.getAttributes()).isNotNull();
			Assertions.assertThat(nativePartitionRegion.getAttributes().getDataPolicy())
				.isEqualTo(DataPolicy.PERSISTENT_PARTITION);
			Assertions.assertThat(nativePartitionRegion.getAttributes().getCloningEnabled()).isTrue();
			Assertions.assertThat(nativePartitionRegion.getAttributes().getConcurrencyChecksEnabled()).isTrue();
			Assertions.assertThat(nativePartitionRegion.getAttributes().getConcurrencyLevel()).isEqualTo(40);
			Assertions.assertThat(nativePartitionRegion.getAttributes().getInitialCapacity()).isEqualTo(51);
			Assertions.assertThat(nativePartitionRegion.getAttributes().getKeyConstraint()).isEqualTo(Integer.class);
			Assertions.assertThat(nativePartitionRegion.getAttributes().getLoadFactor()).isEqualTo(0.85f);
			Assertions.assertThat(nativePartitionRegion.getAttributes().getMulticastEnabled()).isFalse();
			Assertions.assertThat(nativePartitionRegion.getAttributes().getValueConstraint()).isEqualTo(String.class);

			Region<?, ?> nativeReplicateRegion = applicationContext.getBean("NativeReplicateRegion", Region.class);

			Assertions.assertThat(nativeReplicateRegion).isNotNull();
			Assertions.assertThat(nativeReplicateRegion.getName()).isEqualTo("NativeReplicateRegion");
			Assertions.assertThat(nativeReplicateRegion.getFullPath()).isEqualTo("/NativeReplicateRegion");
			Assertions.assertThat(nativeReplicateRegion.getAttributes()).isNotNull();
			Assertions.assertThat(nativeReplicateRegion.getAttributes().getDataPolicy())
				.isEqualTo(DataPolicy.PERSISTENT_REPLICATE);
			Assertions.assertThat(nativeReplicateRegion.getAttributes().getCloningEnabled()).isFalse();
			Assertions.assertThat(nativeReplicateRegion.getAttributes().getConcurrencyChecksEnabled()).isTrue();
			Assertions.assertThat(nativeReplicateRegion.getAttributes().getInitialCapacity()).isEqualTo(23);
			Assertions.assertThat(nativeReplicateRegion.getAttributes().getLoadFactor()).isEqualTo(0.75f);
			Assertions.assertThat(nativeReplicateRegion.getAttributes().getKeyConstraint()).isEqualTo(Integer.class);
			Assertions.assertThat(nativeReplicateRegion.getAttributes().getMulticastEnabled()).isFalse();
			Assertions.assertThat(nativeReplicateRegion.getAttributes().getScope()).isEqualTo(Scope.DISTRIBUTED_NO_ACK);
			Assertions.assertThat(nativeReplicateRegion.getAttributes().getValueConstraint()).isEqualTo(String.class);

			Region<?, ?> nativeChildRegion =
				applicationContext.getBean("/NativeParentRegion/NativeChildRegion", Region.class);

			Assertions.assertThat(nativeChildRegion).isNotNull();
			Assertions.assertThat(nativeChildRegion.getName()).isEqualTo("NativeChildRegion");
			Assertions.assertThat(nativeChildRegion.getFullPath()).isEqualTo("/NativeParentRegion/NativeChildRegion");
			Assertions.assertThat(nativeChildRegion.getAttributes()).isNotNull();
			Assertions.assertThat(nativeChildRegion.getAttributes().getDataPolicy()).isEqualTo(DataPolicy.REPLICATE);

			Region<?, ?> springReplicateRegion = applicationContext.getBean("SpringReplicateRegion", Region.class);

			Assertions.assertThat(springReplicateRegion).isNotNull();
			Assertions.assertThat(springReplicateRegion.getName()).isEqualTo("SpringReplicateRegion");
			Assertions.assertThat(springReplicateRegion.getFullPath()).isEqualTo("/SpringReplicateRegion");
			Assertions.assertThat(springReplicateRegion.getAttributes()).isNotNull();
			Assertions.assertThat(springReplicateRegion.getAttributes().getDataPolicy()).isEqualTo(DataPolicy.REPLICATE);
		}
		finally {
			IntegrationTestsSupport.closeApplicationContext(applicationContext);
		}
	}
}
