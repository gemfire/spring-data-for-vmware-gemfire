/*
 * Copyright 2022-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.config.xml;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

import org.apache.geode.cache.Region;

import org.springframework.beans.factory.xml.XmlBeanDefinitionStoreException;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.GenericXmlApplicationContext;
import org.springframework.data.gemfire.tests.integration.SpringApplicationContextIntegrationTestsSupport;

import org.xml.sax.SAXParseException;

/**
 * Integration Tests testing the proper syntax for declaring "custom" expiration attributes on a {@link Region}.
 *
 * @author John Blum
 * @see org.junit.Test
 * @see org.apache.geode.cache.ExpirationAttributes
 * @see Region
 * @see ConfigurableApplicationContext
 * @see GenericXmlApplicationContext
 * @see SpringApplicationContextIntegrationTestsSupport
 * @see org.springframework.data.gemfire.tests.mock.beans.factory.config.GemFireMockObjectsBeanPostProcessor
 * @since 1.5.0
 */
public class InvalidRegionExpirationAttributesNamespaceIntegrationTests
		extends SpringApplicationContextIntegrationTestsSupport {

	private ConfigurableApplicationContext createApplicationContext() {

		GenericXmlApplicationContext applicationContext = new GenericXmlApplicationContext();

		applicationContext.load(getContextXmlFileLocation(InvalidRegionExpirationAttributesNamespaceIntegrationTests.class));
		applicationContext.registerShutdownHook();
		applicationContext.refresh();

		setApplicationContext(applicationContext);

		return applicationContext;
	}

	@Test(expected = XmlBeanDefinitionStoreException.class)
	public void invalidXmlSyntaxThrowsException() {

		try {
			createApplicationContext();
		}
		catch (XmlBeanDefinitionStoreException expected) {
			assertThat(expected).hasCauseInstanceOf(SAXParseException.class);
			throw expected;
		}
	}
}
