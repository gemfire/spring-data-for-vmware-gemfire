// Copyright (c) VMware, Inc. 2022. All rights reserved.
// SPDX-License-Identifier: Apache-2.0

package org.springframework.data.gemfire.config.xml;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

import org.apache.geode.cache.Region;

import org.springframework.beans.factory.parsing.BeanDefinitionParsingException;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.data.gemfire.tests.integration.IntegrationTestsSupport;

/**
 * Integration Tests testing the incorrect order of Template {@link Region} bean definitions
 * and regular {@link Region} bean definitions referring to the templates.
 *
 * @author John Blum
 * @see org.junit.Test
 * @see org.junit.runner.RunWith
 * @see org.apache.geode.cache.Region
 * @see org.springframework.data.gemfire.tests.integration.IntegrationTestsSupport
 * @see org.springframework.test.context.ContextConfiguration
 * @see org.springframework.test.context.junit4.SpringJUnit4ClassRunner
 * @since 1.5.0
 */
public class TemplateRegionDefinitionOrderErrorNamespaceIntegrationTests extends IntegrationTestsSupport {

	@Test(expected = BeanDefinitionParsingException.class)
	public void incorrectTemplateRegionBeanDefinitionOrderThrowsParseException() {

		try {
			new ClassPathXmlApplicationContext(getContextXmlFileLocation(
				TemplateRegionDefinitionOrderErrorNamespaceIntegrationTests.class));
		}
		catch (BeanDefinitionParsingException expected) {

			assertThat(expected).hasMessageContaining("The Region template [RegionTemplate] must be defined before"
				+ " the Region [TemplateBasedPartitionRegion] referring to the template");

			assertThat(expected).hasNoCause();

			throw expected;
		}
	}
}
