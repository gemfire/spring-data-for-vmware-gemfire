/*
 * Copyright (c) VMware, Inc. 2022-2023. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.config.annotation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.data.gemfire.config.annotation.TestSecurityManager.SECURITY_PASSWORD;
import static org.springframework.data.gemfire.config.annotation.TestSecurityManager.SECURITY_USERNAME;

import com.vmware.gemfire.testcontainers.GemFireCluster;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.apache.geode.cache.GemFireCache;
import org.apache.geode.cache.client.ClientRegionShortcut;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.data.gemfire.GemfireTemplate;
import org.springframework.data.gemfire.GemfireUtils;
import org.springframework.data.gemfire.client.ClientRegionFactoryBean;
import org.springframework.data.gemfire.support.ConnectionEndpoint;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Collections;

/**
 * Integration Tests for {@link AutoConfiguredAuthenticationConfiguration}.
 *
 * @author John Blum
 * @see org.junit.Test
 * @see org.apache.geode.cache.GemFireCache
 * @see org.springframework.data.gemfire.GemfireTemplate
 * @see org.springframework.data.gemfire.config.annotation.AutoConfiguredAuthenticationConfiguration
 * @see org.springframework.test.context.ContextConfiguration
 * @see org.springframework.test.context.junit4.SpringRunner
 * @since 1.9.0
 */
@RunWith(SpringRunner.class)
@ContextConfiguration(
	classes = AutoConfiguredAuthenticationConfigurationIntegrationTests.TestGemFireClientConfiguration.class
)
@SuppressWarnings("unused")
public class AutoConfiguredAuthenticationConfigurationIntegrationTests {

	private static GemFireCluster gemFireCluster;

	@BeforeClass
	public static void setupGemFireServer() {
		gemFireCluster = new GemFireCluster(System.getProperty("spring.test.gemfire.docker.image"), 1, 1)
				.withClasspath(GemFireCluster.ALL_GLOB, System.getProperty("TEST_JAR_PATH"))
				.withGfsh(false, "create region --name=Echo --type=LOCAL",
						"put --region=/Echo --key=Hello --value=Hello",
						"put --region=/Echo --key=TEST --value=TEST",
						"put --region=/Echo --key=Good-Bye --value=Good-Bye");

		gemFireCluster.acceptLicense().start();
	}

	@Autowired
	private GemfireTemplate echoTemplate;

	@Test
	public void clientAuthenticatesWithServer() {

		assertThat(this.echoTemplate.<String, String>get("Hello")).isEqualTo("Hello");
		assertThat(this.echoTemplate.<String, String>get("TEST")).isEqualTo("TEST");
		assertThat(this.echoTemplate.<String, String>get("Good-Bye")).isEqualTo("Good-Bye");
	}

	@ClientCacheApplication
	@EnableSecurity(securityUsername = SECURITY_USERNAME, securityPassword = SECURITY_PASSWORD)
	static class TestGemFireClientConfiguration {

		@Bean("Echo")
		ClientRegionFactoryBean<Object, Object> echoRegion(GemFireCache gemfireCache) {

			ClientRegionFactoryBean<Object, Object> echoRegion = new ClientRegionFactoryBean<>();

			echoRegion.setCache(gemfireCache);
			echoRegion.setShortcut(ClientRegionShortcut.PROXY);

			return echoRegion;
		}

		@Bean
		GemfireTemplate echoTemplate(GemFireCache cache) {
			return new GemfireTemplate(cache.getRegion(GemfireUtils.toRegionPath("Echo")));
		}

		@Bean
		ClientCacheConfigurer clientCacheConfigurer() {
			return (bean, clientCacheFactoryBean) -> clientCacheFactoryBean.setLocators(
					Collections.singletonList(
							new ConnectionEndpoint("localhost", gemFireCluster.getLocatorPort())));
		}
	}
}
