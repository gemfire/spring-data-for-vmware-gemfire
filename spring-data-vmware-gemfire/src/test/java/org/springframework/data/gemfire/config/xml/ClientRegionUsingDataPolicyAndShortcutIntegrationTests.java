/*
 * Copyright 2022-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.config.xml;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

import org.apache.geode.cache.DataPolicy;
import org.apache.geode.cache.Region;
import org.apache.geode.cache.client.ClientRegionShortcut;

import org.springframework.beans.factory.parsing.BeanDefinitionParsingException;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.data.gemfire.tests.integration.IntegrationTestsSupport;

/**
 * Integration Tests for client {@link Region} bean definition with both {@literal data-policy}(i.e. {@link DataPolicy})
 * and {@literal shortcut} {@link ClientRegionShortcut} attributes specified.
 *
 * @author John Blum
 * @see org.junit.Test
 * @see org.apache.geode.cache.DataPolicy
 * @see org.apache.geode.cache.Region
 * @see org.apache.geode.cache.client.ClientRegionShortcut
 * @see org.springframework.context.support.ClassPathXmlApplicationContext
 * @see org.springframework.data.gemfire.client.ClientCacheFactoryBean
 * @see org.springframework.data.gemfire.tests.integration.IntegrationTestsSupport
 * @since 1.3.3
 */
public class ClientRegionUsingDataPolicyAndShortcutIntegrationTests extends IntegrationTestsSupport {

	@Test(expected = BeanDefinitionParsingException.class)
	public void testClientRegionBeanDefinitionWithDataPolicyAndShortcut() {

		try {
			new ClassPathXmlApplicationContext(getContextXmlFileLocation(ClientRegionUsingDataPolicyAndShortcutIntegrationTests.class));
		}
		catch (BeanDefinitionParsingException expected) {

			assertThat(expected).hasMessageContaining("Only one of [data-policy, shortcut] may be specified with element");

			throw expected;
		}
	}
}
