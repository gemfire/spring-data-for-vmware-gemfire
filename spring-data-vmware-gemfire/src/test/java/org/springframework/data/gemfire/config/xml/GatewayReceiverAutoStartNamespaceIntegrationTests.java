/*
 * Copyright (c) VMware, Inc. 2022-2023. All rights reserved.
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
 * This test class tests the auto start configuration of the {@link GatewayReceiver} component in SDG.
 *
 * @author John Blum
 * @see Test
 * @see GatewayReceiver
 * @see IntegrationTestsSupport
 * @see GemFireMockObjectsApplicationContextInitializer
 * @see GatewayReceiverFactoryBean
 * @see ActiveProfiles
 * @see ContextConfiguration
 * @see SpringRunner
 * @since 1.5.0
 */
@RunWith(SpringRunner.class)
@ContextConfiguration(locations = "GatewayReceiverNamespaceIntegrationTests-context.xml",
	initializers = GemFireMockObjectsApplicationContextInitializer.class)
@ActiveProfiles("autoStart")
@SuppressWarnings("unused")
public class GatewayReceiverAutoStartNamespaceIntegrationTests extends IntegrationTestsSupport {

	@Autowired
	@Qualifier("&Auto")
	private GatewayReceiverFactoryBean autoGatewayReceiverFactory;

	@Test
	public void testAuto() throws Exception {

		assertThat(this.autoGatewayReceiverFactory)
			.describedAs("The 'Auto' GatewayReceiverFactoryBean was not properly configured and initialized")
			.isNotNull();

		GatewayReceiver autoGatewayReceiver = this.autoGatewayReceiverFactory.getObject();

		try {
			assertThat(autoGatewayReceiver).isNotNull();
			assertThat(StringUtils.hasText(autoGatewayReceiver.getBindAddress())).isFalse();
			assertThat(autoGatewayReceiver.getHost()).isEqualTo("neo");
			assertThat(autoGatewayReceiver.getStartPort()).isEqualTo(15500);
			assertThat(autoGatewayReceiver.getEndPort()).isEqualTo(25500);
			assertThat(autoGatewayReceiver.getMaximumTimeBetweenPings()).isEqualTo(10000);
			assertThat(autoGatewayReceiver.isRunning()).isTrue();
			assertThat(autoGatewayReceiver.getSocketBufferSize()).isEqualTo(16384);
		}
		finally {
			autoGatewayReceiver.stop();
		}
	}
}
