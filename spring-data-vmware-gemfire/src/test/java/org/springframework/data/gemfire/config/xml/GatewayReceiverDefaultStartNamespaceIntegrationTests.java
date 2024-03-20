/*
 * Copyright 2022-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.config.xml;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;

import org.apache.geode.cache.wan.GatewayReceiver;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.gemfire.tests.integration.IntegrationTestsSupport;
import org.springframework.data.gemfire.tests.mock.context.GemFireMockObjectsApplicationContextInitializer;
import org.springframework.data.gemfire.wan.GatewayReceiverFactoryBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.StringUtils;

/**
 * Integration Tests with test cases testing the contract and functionality of {@link GatewayReceiver} configuration
 * in SDG using the XML namespace (XSD) configuration metadata.
 *
 * This test class tests the default start configuration of the {@link GatewayReceiver} component in SDG.
 *
 * @author John Blum
 * @see org.junit.Test
 * @see org.apache.geode.cache.wan.GatewayReceiver
 * @see org.springframework.data.gemfire.tests.integration.IntegrationTestsSupport
 * @see org.springframework.data.gemfire.tests.mock.context.GemFireMockObjectsApplicationContextInitializer
 * @see org.springframework.data.gemfire.wan.GatewayReceiverFactoryBean
 * @see org.springframework.test.context.ActiveProfiles
 * @see org.springframework.test.context.ContextConfiguration
 * @see org.springframework.test.context.junit4.SpringRunner
 * @since 1.5.0
 */
@ActiveProfiles("defaultStart")
@RunWith(SpringRunner.class)
@ContextConfiguration(locations = "GatewayReceiverNamespaceIntegrationTests-context.xml",
	initializers = GemFireMockObjectsApplicationContextInitializer.class)
@SuppressWarnings("unused")
public class GatewayReceiverDefaultStartNamespaceIntegrationTests extends IntegrationTestsSupport {

	@Autowired
	@Qualifier("&Default")
	private GatewayReceiverFactoryBean defaultGatewayReceiverFactory;

	@Test
	public void testDefault() throws Exception {

		assertThat(this.defaultGatewayReceiverFactory)
			.describedAs("The 'Default' GatewayReceiverFactoryBean was not properly configured and initialized")
			.isNotNull();

		GatewayReceiver defaultGatewayReceiver = this.defaultGatewayReceiverFactory.getObject();

		try {
			assertThat(defaultGatewayReceiver).isNotNull();
			assertThat(StringUtils.hasText(defaultGatewayReceiver.getBindAddress())).isFalse();
			assertThat(defaultGatewayReceiver.getHost()).isEqualTo("skullbox");
			assertThat(defaultGatewayReceiver.getStartPort()).isEqualTo(12345);
			assertThat(defaultGatewayReceiver.getEndPort()).isEqualTo(54321);
			assertThat(defaultGatewayReceiver.getMaximumTimeBetweenPings()).isEqualTo(5000);
			assertThat(defaultGatewayReceiver.isRunning()).isTrue();
			assertThat(defaultGatewayReceiver.getSocketBufferSize()).isEqualTo(32768);
		}
		finally {
			defaultGatewayReceiver.stop();
		}
	}
}
