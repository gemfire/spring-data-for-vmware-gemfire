/*
 * Copyright 2022-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.config.annotation;

import static org.assertj.core.api.Assertions.assertThat;
import com.vmware.gemfire.testcontainers.GemFireCluster;
import java.io.IOException;
import java.util.Collections;
import org.apache.geode.cache.Region;
import org.apache.geode.cache.client.ClientCache;
import org.apache.geode.cache.client.ClientRegionShortcut;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.gemfire.client.ClientRegionFactoryBean;
import org.springframework.data.gemfire.support.ConnectionEndpoint;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.testcontainers.utility.MountableFile;

/**
 * Integration Tests for {@link EnableSsl} and {@link SslConfiguration}.
 *
 * @author John Blum
 * @see org.junit.Test
 * @see org.apache.geode.cache.client.ClientCache
 * @see org.apache.geode.cache.Region
 * @see org.springframework.data.gemfire.config.annotation.ClientCacheApplication
 * @see org.springframework.data.gemfire.config.annotation.EnableSsl
 * @see org.springframework.data.gemfire.config.annotation.SslConfiguration
 * @see org.springframework.test.context.ContextConfiguration
 * @see org.springframework.test.context.junit4.SpringRunner
 * @since 2.1.0
 */
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = EnableSslConfigurationIntegrationTests.GeodeClientTestConfiguration.class)
@SuppressWarnings("all")
public class EnableSslConfigurationIntegrationTests {

	private static final String LOG_LEVEL = "error";

	@Autowired
	@Qualifier("Echo")
	private Region<String, String> echo;

	private static GemFireCluster gemFireCluster;

	@BeforeClass
	public static void startGeodeServer() throws IOException {
		org.springframework.core.io.Resource trustedKeystore = new ClassPathResource("trusted.keystore");

		gemFireCluster = new GemFireCluster(System.getProperty("spring.test.gemfire.docker.image"), 1, 1)
				.withCacheXml(GemFireCluster.ALL_GLOB, "/enable-ssl-configuration-integration-tests-cache.xml")
				.withConfiguration(GemFireCluster.ALL_GLOB, container ->
						container.withCopyFileToContainer(MountableFile.forClasspathResource("trusted.keystore"), "/trusted.keystore"))
				.withGemFireProperty(GemFireCluster.ALL_GLOB, "javax.net.ssl.keyStore", "/trusted.keystore")
				.withGemFireProperty(GemFireCluster.ALL_GLOB, "ssl-keystore", "/trusted.keystore")
				.withGemFireProperty(GemFireCluster.ALL_GLOB, "ssl-keystore-password", "s3cr3t")
				.withGemFireProperty(GemFireCluster.ALL_GLOB, "ssl-truststore", "/trusted.keystore")
				.withGemFireProperty(GemFireCluster.ALL_GLOB, "ssl-truststore-password", "s3cr3t")
				.withGemFireProperty(GemFireCluster.ALL_GLOB, "ssl-enabled-components", "all");

		gemFireCluster.acceptLicense().start();

		System.setProperty("gemfire.locator.port", String.valueOf(gemFireCluster.getLocatorPort()));

		System.setProperty("javax.net.ssl.keyStore", trustedKeystore.getFile().getAbsolutePath());
	}

	@AfterClass
	public static void shutdown() {
		gemFireCluster.close();
	}

	@Test
	public void clientServerWithSslEnabledWorks() {
		assertThat(this.echo.get("testing123")).isEqualTo("testing123");
	}

	@ClientCacheApplication(logLevel = LOG_LEVEL)
	@EnableSsl(keystorePassword = "s3cr3t", truststorePassword = "s3cr3t")
	static class GeodeClientTestConfiguration {

		@Bean
		ClientCacheConfigurer clientCacheSslConfigurer(
				@Value("${javax.net.ssl.keyStore:trusted.keystore}") String keystoreLocation) {

			return (beanName, bean) -> {
				bean.getProperties().setProperty("ssl-keystore", keystoreLocation);
				bean.getProperties().setProperty("ssl-truststore", keystoreLocation);
			};
		}

		@Bean
		ClientCacheConfigurer clientCachePortConfigurer() {
			return (bean, clientCacheFactoryBean) -> clientCacheFactoryBean
					.setServers(Collections.singletonList(
							new ConnectionEndpoint("localhost", gemFireCluster.getServerPorts().get(0))));
		}

		@Bean("Echo")
		ClientRegionFactoryBean<String, String> echoRegion(ClientCache gemfireCache) {

			ClientRegionFactoryBean<String, String> echoRegion = new ClientRegionFactoryBean<>();

			echoRegion.setCache(gemfireCache);
			echoRegion.setClose(false);
			echoRegion.setShortcut(ClientRegionShortcut.PROXY);

			return echoRegion;
		}
	}
}
