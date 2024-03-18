/*
 * Copyright (c) VMware, Inc. 2022-2024. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.config.xml;

import static org.assertj.core.api.Assertions.assertThat;

import org.apache.geode.cache.DataPolicy;
import org.apache.geode.cache.Region;
import org.junit.Test;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.data.gemfire.PeerRegionFactoryBean;
import org.springframework.data.gemfire.RegionAttributesFactoryBean;
import org.springframework.data.gemfire.client.ClientRegionFactoryBean;
import org.springframework.data.gemfire.tests.integration.IntegrationTestsSupport;

/**
 * Integration Tests testing the definition of invalid {@link Region} beans in a Spring context.
 *
 * Specifically, this test class tests the specification of the {@link Region} {@link DataPolicy}
 * on a nested {@link RegionAttributesFactoryBean} conflicting with the {@literal persistent} attribute
 * configuration on the containing {@link ClientRegionFactoryBean} and {@link PeerRegionFactoryBean} definitions.
 *
 * @author John Blum
 * @see org.junit.Test
 * @see org.apache.geode.cache.Region
 * @see org.springframework.data.gemfire.PeerRegionFactoryBean
 * @see org.springframework.data.gemfire.client.ClientRegionFactoryBean
 * @see org.springframework.data.gemfire.tests.integration.IntegrationTestsSupport
 * @since 1.6.0
 */
public class InvalidRegionDefinitionUsingBeansNamespaceIntegrationTests extends IntegrationTestsSupport {

	private static final String CONFIG_LOCATION =
		"org/springframework/data/gemfire/config/xml/InvalidDataPolicyPersistentAttributeSettingsBeansNamespaceIntegrationTests.xml";

	@Test(expected = IllegalArgumentException.class)
	public void testInvalidDataPolicyPersistentAttributeSettings() {

		ConfigurableApplicationContext applicationContext = null;

		try {
			applicationContext = new ClassPathXmlApplicationContext(CONFIG_LOCATION);
		}
		catch (BeanCreationException expected) {

			assertThat(expected.getCause() instanceof IllegalArgumentException).isTrue();
			assertThat(expected.getCause().getMessage())
				.isEqualTo("Data Policy [REPLICATE] is not valid when persistent is true");

			throw (IllegalArgumentException) expected.getCause();
		}
		finally {
			closeApplicationContext(applicationContext);
		}
	}

	@SuppressWarnings("unused")
	public static final class TestRegionFactoryBean<K, V> extends PeerRegionFactoryBean<K, V> { }

}
