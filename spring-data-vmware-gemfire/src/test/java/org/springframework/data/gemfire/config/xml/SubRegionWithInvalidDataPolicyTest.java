/*
 * Copyright (c) VMware, Inc. 2022. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.config.xml;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

import org.apache.geode.cache.DataPolicy;
import org.apache.geode.cache.Region;

import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.xml.XmlBeanDefinitionStoreException;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.data.gemfire.tests.integration.IntegrationTestsSupport;

import org.xml.sax.SAXParseException;

/**
 * Unit Tests testing the {@link DataPolicy} and {@literal persistent} attributes settings are consistent for
 * {@link Region sub-Region} bean definitions.
 *
 * @author John Blum
 * @see org.junit.Test
 * @see org.apache.geode.cache.DataPolicy
 * @see org.apache.geode.cache.Region
 * @see org.springframework.context.support.ClassPathXmlApplicationContext
 * @see org.springframework.data.gemfire.tests.integration.IntegrationTestsSupport
 * @since 1.4.0
 */
public class SubRegionWithInvalidDataPolicyTest extends IntegrationTestsSupport {

	@Test(expected = XmlBeanDefinitionStoreException.class)
	public void subRegionBeanDefinitionWithInconsistentDataPolicyThrowsException() {

		try {
			new ClassPathXmlApplicationContext(
				"/org/springframework/data/gemfire/config/xml/subregion-with-invalid-datapolicy.xml");
		}
		catch (XmlBeanDefinitionStoreException expected) {

			assertThat(expected.getCause()).isInstanceOf(SAXParseException.class);
			assertThat(expected.getCause().getMessage().contains("PERSISTENT_PARTITION")).isTrue();

			throw expected;
		}
	}

	@Test(expected = BeanCreationException.class)
	public void subRegionBeanDefinitionWithInvalidDataPolicyAndPersistentSettingsThrowsException() {

		try {
			new ClassPathXmlApplicationContext(
				"/org/springframework/data/gemfire/config/xml/subregion-with-inconsistent-datapolicy-persistent-settings.xml");
		}
		catch (BeanCreationException expected) {

			assertThat(expected).hasMessageContaining("Error creating bean with name '/Parent/Child'");
			assertThat(expected).hasCauseInstanceOf(IllegalArgumentException.class);
			assertThat(expected.getCause()).hasMessage("Data Policy [REPLICATE] is not valid when persistent is true");

			throw expected;
		}
	}
}
