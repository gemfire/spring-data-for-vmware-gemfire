/*
 * Copyright 2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

/*
 * Copyright 2022-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import org.apache.geode.cache.DataPolicy;
import org.apache.geode.cache.Region;
import org.apache.geode.cache.RegionExistsException;
import org.apache.geode.cache.Scope;
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
			fail("Spring ApplicationContext should have thrown a BeanCreationException caused by a RegionExistsException");
		}
		catch (BeanCreationException expected) {

			assertThat(expected.getCause() instanceof RegionExistsException)
				.describedAs(expected.getMessage())
				.isTrue();

			throw (RegionExistsException) expected.getCause();

		}
		finally {
			closeApplicationContext(applicationContext);
		}
	}

	private ConfigurableApplicationContext newApplicationContext(String configLocation) {
		return new ClassPathXmlApplicationContext(configLocation);
	}

	@After
	public void cleanupAfterTests() {
		destroyAllGemFireMockObjects();
	}

	@Test
	public void allowRegionBeanDefinitionOverrides() {

		ConfigurableApplicationContext applicationContext = null;

		try {

			applicationContext =
				newApplicationContext("/org/springframework/data/gemfire/allowRegionBeanDefinitionOverridesTest.xml");

			assertThat(applicationContext).isNotNull();
			assertThat(applicationContext.containsBean("regionOne")).isTrue();

			Region<?, ?> appDataRegion = applicationContext.getBean("regionOne", Region.class);

			assertThat(appDataRegion).isNotNull();
			assertThat(appDataRegion.getName()).isEqualTo("AppDataRegion");
			assertThat(appDataRegion.getFullPath()).isEqualTo("/AppDataRegion");
			assertThat(appDataRegion.getAttributes()).isNotNull();
			assertThat(appDataRegion.getAttributes().getDataPolicy()).isEqualTo(DataPolicy.PERSISTENT_REPLICATE);
			assertThat(appDataRegion.getAttributes().getInitialCapacity()).isEqualTo(101);
			assertThat(appDataRegion.getAttributes().getLoadFactor()).isEqualTo(0.85f);
			assertThat(appDataRegion.getAttributes().getCloningEnabled()).isTrue();
			assertThat(appDataRegion.getAttributes().getConcurrencyChecksEnabled()).isTrue();
			assertThat(appDataRegion.getAttributes().getKeyConstraint()).isEqualTo(Integer.class);
			assertThat(appDataRegion.getAttributes().getValueConstraint()).isEqualTo(String.class);
		}
		finally {
			closeApplicationContext(applicationContext);
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
	public void noSubRegionLookups() {
		assertNoRegionLookup("/org/springframework/data/gemfire/noSubRegionLookupTest.xml");
	}

	@Test
	public void withEnableClientRegionLookups() {

		ConfigurableApplicationContext applicationContext = null;

		try {

			applicationContext =
				newApplicationContext("/org/springframework/data/gemfire/enableClientRegionLookupsTest.xml");

			assertThat(applicationContext).isNotNull();
			assertThat(applicationContext.containsBean("NativeClientRegion")).isTrue();
			assertThat(applicationContext.containsBean("NativeClientParentRegion")).isTrue();
			assertThat(applicationContext.containsBean("/NativeClientParentRegion/NativeClientChildRegion")).isTrue();

			Region<?, ?> nativeClientRegion = applicationContext.getBean("NativeClientRegion", Region.class);

			assertThat(nativeClientRegion).isNotNull();
			assertThat(nativeClientRegion.getName()).isEqualTo("NativeClientRegion");
			assertThat(nativeClientRegion.getFullPath()).isEqualTo("/NativeClientRegion");
			assertThat(nativeClientRegion.getAttributes()).isNotNull();
			assertThat(nativeClientRegion.getAttributes().getCloningEnabled()).isFalse();
			assertThat(nativeClientRegion.getAttributes().getDataPolicy()).isEqualTo(DataPolicy.NORMAL);

			Region<?, ?> nativeClientChildRegion =
				applicationContext.getBean("/NativeClientParentRegion/NativeClientChildRegion", Region.class);

			assertThat(nativeClientChildRegion).isNotNull();
			assertThat(nativeClientChildRegion.getName()).isEqualTo("NativeClientChildRegion");
			assertThat(nativeClientChildRegion.getFullPath())
				.isEqualTo("/NativeClientParentRegion/NativeClientChildRegion");
			assertThat(nativeClientChildRegion.getAttributes()).isNotNull();
			assertThat(nativeClientChildRegion.getAttributes().getDataPolicy()).isEqualTo(DataPolicy.NORMAL);
		}
		finally {
			closeApplicationContext(applicationContext);
		}
	}

	@Test
	public void withEnableRegionLookups() {

		ConfigurableApplicationContext applicationContext = null;

		try {

			applicationContext =
				newApplicationContext("/org/springframework/data/gemfire/enableRegionLookupsTest.xml");

			assertThat(applicationContext).isNotNull();
			assertThat(applicationContext.containsBean("NativeLocalRegion")).isTrue();
			assertThat(applicationContext.containsBean("NativeReplicateRegion")).isTrue();
			assertThat(applicationContext.containsBean("NativeParentRegion")).isTrue();
			assertThat(applicationContext.containsBean("/NativeParentRegion/NativeChildRegion")).isTrue();
			assertThat(applicationContext.containsBean("SpringLocalRegion")).isTrue();

			Region<?, ?> nativeLocalRegion = applicationContext.getBean("NativeLocalRegion", Region.class);

			assertThat(nativeLocalRegion).isNotNull();
			assertThat(nativeLocalRegion.getName()).isEqualTo("NativeLocalRegion");
			assertThat(nativeLocalRegion.getFullPath()).isEqualTo("/NativeLocalRegion");
			assertThat(nativeLocalRegion.getAttributes()).isNotNull();
			assertThat(nativeLocalRegion.getAttributes().getDataPolicy()).isEqualTo(DataPolicy.NORMAL);
			assertThat(nativeLocalRegion.getAttributes().getCloningEnabled()).isFalse();
			assertThat(nativeLocalRegion.getAttributes().getConcurrencyChecksEnabled()).isFalse();
			assertThat(nativeLocalRegion.getAttributes().getConcurrencyLevel()).isEqualTo(80);
			assertThat(nativeLocalRegion.getAttributes().getInitialCapacity()).isEqualTo(101);
			assertThat(nativeLocalRegion.getAttributes().getKeyConstraint()).isEqualTo(Integer.class);
			assertThat(nativeLocalRegion.getAttributes().getLoadFactor()).isEqualTo(0.95f);
			assertThat(nativeLocalRegion.getAttributes().getValueConstraint()).isEqualTo(String.class);

			Region<?, ?> nativeReplicateRegion = applicationContext.getBean("NativeReplicateRegion", Region.class);

			assertThat(nativeReplicateRegion).isNotNull();
			assertThat(nativeReplicateRegion.getName()).isEqualTo("NativeReplicateRegion");
			assertThat(nativeReplicateRegion.getFullPath()).isEqualTo("/NativeReplicateRegion");
			assertThat(nativeReplicateRegion.getAttributes()).isNotNull();
			assertThat(nativeReplicateRegion.getAttributes().getDataPolicy())
				.isEqualTo(DataPolicy.PERSISTENT_REPLICATE);
			assertThat(nativeReplicateRegion.getAttributes().getCloningEnabled()).isFalse();
			assertThat(nativeReplicateRegion.getAttributes().getConcurrencyChecksEnabled()).isTrue();
			assertThat(nativeReplicateRegion.getAttributes().getInitialCapacity()).isEqualTo(23);
			assertThat(nativeReplicateRegion.getAttributes().getLoadFactor()).isEqualTo(0.75f);
			assertThat(nativeReplicateRegion.getAttributes().getKeyConstraint()).isEqualTo(Integer.class);
			assertThat(nativeReplicateRegion.getAttributes().getScope()).isEqualTo(Scope.DISTRIBUTED_NO_ACK);
			assertThat(nativeReplicateRegion.getAttributes().getValueConstraint()).isEqualTo(String.class);

			Region<?, ?> nativeChildRegion =
				applicationContext.getBean("/NativeParentRegion/NativeChildRegion", Region.class);

			assertThat(nativeChildRegion).isNotNull();
			assertThat(nativeChildRegion.getName()).isEqualTo("NativeChildRegion");
			assertThat(nativeChildRegion.getFullPath()).isEqualTo("/NativeParentRegion/NativeChildRegion");
			assertThat(nativeChildRegion.getAttributes()).isNotNull();
			assertThat(nativeChildRegion.getAttributes().getDataPolicy()).isEqualTo(DataPolicy.NORMAL);

			Region<?, ?> springLocalRegion = applicationContext.getBean("SpringLocalRegion", Region.class);

			assertThat(springLocalRegion).isNotNull();
			assertThat(springLocalRegion.getName()).isEqualTo("SpringLocalRegion");
			assertThat(springLocalRegion.getFullPath()).isEqualTo("/SpringLocalRegion");
			assertThat(springLocalRegion.getAttributes()).isNotNull();
			assertThat(springLocalRegion.getAttributes().getDataPolicy()).isEqualTo(DataPolicy.NORMAL);
		}
		finally {
			closeApplicationContext(applicationContext);
		}
	}
}
