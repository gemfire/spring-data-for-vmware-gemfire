/*
 * Copyright (c) VMware, Inc. 2022-2023. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.config.annotation;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.apache.geode.cache.CacheLoader;
import org.apache.geode.cache.CacheLoaderException;
import org.apache.geode.cache.GemFireCache;
import org.apache.geode.cache.LoaderHelper;
import org.apache.geode.cache.Region;
import org.apache.geode.cache.client.ClientRegionShortcut;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.gemfire.PartitionedRegionFactoryBean;
import org.springframework.data.gemfire.client.ClientRegionFactoryBean;
import org.springframework.data.gemfire.tests.integration.ForkingClientServerIntegrationTestsSupport;
import org.springframework.data.gemfire.tests.process.ProcessWrapper;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Integration Tests for {@link EnableSsl} and {@link SslConfiguration}.
 *
 * @author John Blum
 * @see org.junit.Test
 * @see org.apache.geode.cache.GemFireCache
 * @see org.apache.geode.cache.Region
 * @see org.springframework.data.gemfire.config.annotation.ClientCacheApplication
 * @see org.springframework.data.gemfire.config.annotation.EnableSsl
 * @see org.springframework.data.gemfire.config.annotation.SslConfiguration
 * @see org.springframework.data.gemfire.tests.integration.ForkingClientServerIntegrationTestsSupport
 * @see org.springframework.test.context.ContextConfiguration
 * @see org.springframework.test.context.junit4.SpringRunner
 * @since 2.1.0
 */
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = EnableSslConfigurationIntegrationTests.GeodeClientTestConfiguration.class)
@SuppressWarnings("all")
public class EnableSslConfigurationIntegrationTests extends ForkingClientServerIntegrationTestsSupport {

	private static final String LOG_LEVEL = "error";

	private static ProcessWrapper gemfireServer;

	@Autowired
	@Qualifier("Echo")
	private Region<String, String> echo;

	@BeforeClass
	public static void startGeodeServer() throws Exception {

		org.springframework.core.io.Resource trustedKeystore = new ClassPathResource("trusted.keystore");

		startGemFireServer(GeodeServerTestConfiguration.class,
			String.format("-Dgemfire.name=%s", asApplicationName(EnableSslConfigurationIntegrationTests.class).concat("Server")),
			String.format("-Djavax.net.ssl.keyStore=%s", trustedKeystore.getFile().getAbsolutePath()));

		System.setProperty("javax.net.ssl.keyStore", trustedKeystore.getFile().getAbsolutePath());
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

		@Bean("Echo")
		ClientRegionFactoryBean<String, String> echoRegion(GemFireCache gemfireCache) {

			ClientRegionFactoryBean<String, String> echoRegion = new ClientRegionFactoryBean<>();

			echoRegion.setCache(gemfireCache);
			echoRegion.setClose(false);
			echoRegion.setShortcut(ClientRegionShortcut.PROXY);

			return echoRegion;
		}
	}

	@CacheServerApplication(name = "EnableSslConfigurationIntegrationTests", logLevel = LOG_LEVEL)
	@EnableSsl(keystorePassword = "s3cr3t", truststorePassword = "s3cr3t")
	static class GeodeServerTestConfiguration {

		public static void main(String[] args) {
			runSpringApplication(GeodeServerTestConfiguration.class, args);
		}

		@Bean
		PeerCacheConfigurer cacheServerSslConfigurer(
				@Value("${javax.net.ssl.keyStore:trusted.keystore}") String keystoreLocation) {

			return (beanName, bean) -> {
				bean.getProperties().setProperty("ssl-keystore", keystoreLocation);
				bean.getProperties().setProperty("ssl-truststore", keystoreLocation);
			};
		}

		@Bean("Echo")
		PartitionedRegionFactoryBean<String, String> echoRegion(GemFireCache gemfireCache) {

			PartitionedRegionFactoryBean<String, String> echoRegion = new PartitionedRegionFactoryBean<>();

			echoRegion.setCache(gemfireCache);
			echoRegion.setCacheLoader(echoCacheLoader());
			echoRegion.setPersistent(false);

			return echoRegion;
		}

		@Bean
		CacheLoader<String, String> echoCacheLoader() {

			return new CacheLoader<String, String>() {

				@Override
				public String load(LoaderHelper<String, String> helper) throws CacheLoaderException {
					return helper.getKey();
				}

				@Override
				public void close() { }

			};
		}
	}
}
