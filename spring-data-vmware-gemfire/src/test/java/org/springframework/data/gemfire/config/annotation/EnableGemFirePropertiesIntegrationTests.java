/*
 * Copyright 2022-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.config.annotation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import java.util.Optional;
import java.util.Properties;
import java.util.function.Function;

import org.junit.Test;

import org.apache.geode.cache.GemFireCache;
import org.apache.geode.pdx.PdxSerializer;

import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertySource;
import org.springframework.data.gemfire.CacheFactoryBean;
import org.springframework.data.gemfire.tests.integration.SpringApplicationContextIntegrationTestsSupport;
import org.springframework.data.gemfire.tests.mock.annotation.EnableGemFireMockObjects;
import org.springframework.data.gemfire.util.ArrayUtils;
import org.springframework.mock.env.MockPropertySource;
import org.springframework.util.StringUtils;

/**
 * Integration tests for {@link EnableGemFireProperties}, {@link EnableLogging},
 * {@link EnableSecurity}, {@link EnableSsl}, {@link EnableStatistics}.
 *
 * @author John Blum
 * @see java.util.Properties
 * @see org.junit.Test
 * @see org.apache.geode.cache.GemFireCache
 * @see org.springframework.context.ConfigurableApplicationContext
 * @see org.springframework.context.annotation.AnnotationConfigApplicationContext
 * @see org.springframework.core.env.PropertySource
 * @see org.springframework.data.gemfire.tests.integration.SpringApplicationContextIntegrationTestsSupport
 * @see org.springframework.data.gemfire.tests.mock.annotation.EnableGemFireMockObjects
 * @since 2.0.0
 */
@SuppressWarnings("rawtypes")
public class EnableGemFirePropertiesIntegrationTests extends SpringApplicationContextIntegrationTestsSupport {

	@Override
	protected ConfigurableApplicationContext newApplicationContext(Class<?>... annotatedClasses) {
		return newApplicationContext((PropertySource<?>) null, annotatedClasses);
	}

	private ConfigurableApplicationContext newApplicationContext(PropertySource<?> testPropertySource,
																															 Class<?>... annotatedClasses) {

		Function<ConfigurableApplicationContext, ConfigurableApplicationContext> applicationContextInitializer =
				testPropertySource != null ? applicationContext -> {
					Optional.ofNullable(testPropertySource).ifPresent(it -> {

						MutablePropertySources propertySources = applicationContext.getEnvironment().getPropertySources();

						propertySources.addFirst(testPropertySource);
					});

					return applicationContext;
				}
						: Function.identity();

		return newApplicationContext(applicationContextInitializer, annotatedClasses);
	}

	@Test
	public void loggingGemFirePropertiesConfiguration() {

		PropertySource testPropertySource = new MockPropertySource("TestPropertySource")
				.withProperty("spring.data.gemfire.logging.log-disk-space-limit", "100")
				.withProperty("spring.data.gemfire.logging.log-file", "/path/to/file.log")
				.withProperty("spring.data.gemfire.logging.log-file-size-limit", "10")
				.withProperty("spring.data.gemfire.logging.level", "info");

		newApplicationContext(testPropertySource, TestLoggingGemFirePropertiesConfiguration.class);

		assertThat(containsBean("gemfireCache")).isTrue();

		GemFireCache gemfireCache = getBean("gemfireCache", GemFireCache.class);

		assertThat(gemfireCache).isNotNull();
		assertThat(gemfireCache.getDistributedSystem()).isNotNull();

		Properties gemfireProperties = gemfireCache.getDistributedSystem().getProperties();

		assertThat(gemfireProperties).isNotNull();
		assertThat(gemfireProperties.getProperty("log-disk-space-limit")).isEqualTo("100");
		assertThat(gemfireProperties.getProperty("log-file")).isEqualTo("/path/to/file.log");
		assertThat(gemfireProperties.getProperty("log-file-size-limit")).isEqualTo("10");
		assertThat(gemfireProperties.getProperty("log-level")).isEqualTo("info");
	}

	@Test
	public void nameAndGroupsAnnotationBasedGemFirePropertiesConfiguration() {

		newApplicationContext(TestNameAndGroupsAnnotationBasedGemFirePropertiesConfiguration.class);

		assertThat(containsBean("gemfireCache")).isTrue();
		assertThat(containsBean("gemfireProperties")).isTrue();

		//Properties gemfireProperties = this.applicationContext.getBean("gemfireProperties", Properties.class);

		GemFireCache gemfireCache = getBean("gemfireCache", GemFireCache.class);

		assertThat(gemfireCache).isNotNull();
		assertThat(gemfireCache.getDistributedSystem()).isNotNull();

		Properties gemfireProperties = gemfireCache.getDistributedSystem().getProperties();

		assertThat(gemfireProperties).isNotNull();
		assertThat(gemfireProperties.containsKey("name")).isTrue();
		assertThat(gemfireProperties.getProperty("name")).isEqualTo("TestName");
		assertThat(gemfireProperties.containsKey("groups")).isTrue();
		assertThat(gemfireProperties.getProperty("groups")).isEqualTo("TestGroupOne,TestGroupTwo");
	}

	@Test
	public void pdxGemFirePropertiesConfiguration() {

		PropertySource testPropertySource = new MockPropertySource("TestPropertySource")
			.withProperty("spring.data.gemfire.pdx.disk-store-name", "TestDiskStore")
			.withProperty("spring.data.gemfire.pdx.ignore-unread-fields", "true")
			.withProperty("spring.data.gemfire.pdx.persistent", "true")
			.withProperty("spring.data.gemfire.pdx.read-serialized", "true")
			.withProperty("spring.data.gemfire.pdx.serializer-bean-name", "mockPdxSerializer");

		newApplicationContext(testPropertySource, TestPdxGemFirePropertiesConfiguration.class);

		assertThat(containsBean("gemfireCache")).isTrue();
		assertThat(containsBean("mockPdxSerializer")).isTrue();

		CacheFactoryBean gemfireCache = getBean("&gemfireCache", CacheFactoryBean.class);

		assertThat(gemfireCache).isNotNull();
		assertThat(gemfireCache.getPdxDiskStoreName()).isEqualTo("TestDiskStore");
		assertThat(gemfireCache.getPdxIgnoreUnreadFields()).isTrue();
		assertThat(gemfireCache.getPdxPersistent()).isTrue();
		assertThat(gemfireCache.getPdxReadSerialized()).isTrue();

		PdxSerializer mockPdxSerializer = getBean("mockPdxSerializer", PdxSerializer.class);

		assertThat(mockPdxSerializer).isNotNull();
		assertThat(gemfireCache.getPdxSerializer()).isEqualTo(mockPdxSerializer);
	}

	@Test
	public void securityGemFirePropertiesConfiguration() {

		PropertySource testPropertySource = new MockPropertySource("TestPropertySource")
			.withProperty("spring.data.gemfire.security.client.authentication-initializer", "example.security.client.AuthenticationInitializer")
			.withProperty("spring.data.gemfire.security.peer.authentication-initializer", "example.security.peer.AuthenticationInitializer")
			.withProperty("spring.data.gemfire.security.manager.class-name", "example.security.SecurityManager")
			.withProperty("spring.data.gemfire.security.postprocessor.class-name", "example.security.PostProcessor")
			.withProperty("spring.data.gemfire.security.shiro.ini-resource-path", "/path/to/shiro.ini");

		newApplicationContext(testPropertySource, TestSecurityGemFirePropertiesConfiguration.class);

		assertThat(containsBean("gemfireCache")).isTrue();

		GemFireCache gemfireCache = getBean("gemfireCache", GemFireCache.class);

		assertThat(gemfireCache).isNotNull();
		assertThat(gemfireCache.getDistributedSystem()).isNotNull();

		Properties gemfireProperties = gemfireCache.getDistributedSystem().getProperties();

		assertThat(gemfireProperties).isNotNull();
		assertThat(gemfireProperties.getProperty("security-client-auth-init")).isEqualTo("example.security.client.AuthenticationInitializer");
		assertThat(gemfireProperties.getProperty("security-peer-auth-init")).isEqualTo("example.security.peer.AuthenticationInitializer");
		assertThat(gemfireProperties.getProperty("security-manager")).isEqualTo("example.security.SecurityManager");
		assertThat(gemfireProperties.getProperty("security-post-processor")).isEqualTo("example.security.PostProcessor");
		assertThat(gemfireProperties.getProperty("security-shiro-init")).isEqualTo("/path/to/shiro.ini");
	}

	@Test
	public void serializableObjectFilterAndValidateSerializableObjectsGemFirePropertiesConfiguration() {

		newApplicationContext(TestSerializableObjectFilterAndValidateSerializableObjectsGemFirePropertiesConfiguration.class);

		assertThat(containsBean("gemfireProperties")).isTrue();

		Properties gemfireProperties = getBean("gemfireProperties", Properties.class);

		assertThat(gemfireProperties).isNotNull();
		assertThat(gemfireProperties.containsKey("serializable-object-filter")).isTrue();
		assertThat(gemfireProperties.getProperty("serializable-object-filter"))
			.isEqualTo("example.app.model.TypeOne,example.app.model.TypeTwo");
		assertThat(gemfireProperties.containsKey("validate-serializable-objects")).isTrue();
		assertThat(gemfireProperties.getProperty("validate-serializable-objects")).isEqualTo("true");
	}

	@Test
	public void sslGemFirePropertiesConfiguration() {

		PropertySource testPropertySource = new MockPropertySource("TestPropertySource")
			.withProperty("spring.data.gemfire.security.ssl.ciphers", "DSA, RSA")
			.withProperty("spring.data.gemfire.security.ssl.certificate.alias.default", "TestCert")
			.withProperty("spring.data.gemfire.security.ssl.keystore", "/path/to/keystore")
			.withProperty("spring.data.gemfire.security.ssl.keystore.password", "p@55w0rd")
			.withProperty("spring.data.gemfire.security.ssl.keystore.type", "JKS")
			.withProperty("spring.data.gemfire.security.ssl.protocols", "IP, TCP/IP, UDP")
			.withProperty("spring.data.gemfire.security.ssl.require-authentication", "false")
			.withProperty("spring.data.gemfire.security.ssl.truststore", "/path/to/truststore")
			.withProperty("spring.data.gemfire.security.ssl.truststore.password", "p@55w0rd")
			.withProperty("spring.data.gemfire.security.ssl.truststore.type", "PKCS11")
			.withProperty("spring.data.gemfire.security.ssl.web-require-authentication", "true");

		newApplicationContext(testPropertySource, TestSslGemFirePropertiesConfiguration.class);

		assertThat(containsBean("gemfireCache")).isTrue();
		assertThat(containsBean("gemfireProperties")).isTrue();

		GemFireCache gemfireCache = getBean("gemfireCache", GemFireCache.class);

		assertThat(gemfireCache).isNotNull();
		assertThat(gemfireCache.getDistributedSystem()).isNotNull();

		Properties gemfireProperties = gemfireCache.getDistributedSystem().getProperties();

		String sslEnabledComponents = Optional.ofNullable(gemfireProperties.getProperty("ssl-enabled-components"))
			.filter(StringUtils::hasText)
			.map(it -> StringUtils.arrayToCommaDelimitedString(ArrayUtils.sort(
				StringUtils.commaDelimitedListToStringArray(it))))
			.orElse(null);

		assertThat(gemfireProperties).isNotNull();
		assertThat(gemfireProperties.getProperty("ssl-ciphers")).isEqualTo("DSA, RSA");
		assertThat(sslEnabledComponents).isEqualTo("cluster,gateway,locator,server,web");
		assertThat(gemfireProperties.getProperty("ssl-default-alias")).isEqualTo("TestCert");
		assertThat(gemfireProperties.getProperty("ssl-keystore")).isEqualTo("/path/to/keystore");
		assertThat(gemfireProperties.getProperty("ssl-keystore-password")).isEqualTo("p@55w0rd");
		assertThat(gemfireProperties.getProperty("ssl-keystore-type")).isEqualTo("JKS");
		assertThat(gemfireProperties.getProperty("ssl-protocols")).isEqualTo("IP, TCP/IP, UDP");
		assertThat(gemfireProperties.getProperty("ssl-require-authentication")).isEqualTo("false");
		assertThat(gemfireProperties.getProperty("ssl-truststore")).isEqualTo("/path/to/truststore");
		assertThat(gemfireProperties.getProperty("ssl-truststore-password")).isEqualTo("p@55w0rd");
		assertThat(gemfireProperties.getProperty("ssl-truststore-type")).isEqualTo("PKCS11");
	}

	@Test
	public void statisticsGemFirePropertiesConfiguration() {

		MockPropertySource testPropertySource = new MockPropertySource()
			.withProperty("spring.data.gemfire.stats.archive-disk-space-limit", "50")
			.withProperty("spring.data.gemfire.stats.archive-file", "/path/to/archive.stats")
			.withProperty("spring.data.gemfire.stats.archive-file-size-limit", "10")
			.withProperty("spring.data.gemfire.stats.enable-time-statistics", "true")
			.withProperty("spring.data.gemfire.stats.sample-rate", "100");

		newApplicationContext(testPropertySource, TestStatisticsGemFirePropertiesConfiguration.class);

		assertThat(containsBean("gemfireCache")).isTrue();

		GemFireCache gemfireCache = getBean("gemfireCache", GemFireCache.class);

		assertThat(gemfireCache).isNotNull();
		assertThat(gemfireCache.getDistributedSystem()).isNotNull();

		Properties gemfireProperties = gemfireCache.getDistributedSystem().getProperties();

		assertThat(gemfireProperties).isNotNull();
		assertThat(gemfireProperties.getProperty("statistic-sampling-enabled")).isEqualTo("true");
		assertThat(gemfireProperties.getProperty("archive-disk-space-limit")).isEqualTo("50");
		assertThat(gemfireProperties.getProperty("statistic-archive-file")).isEqualTo("/path/to/archive.stats");
		assertThat(gemfireProperties.getProperty("archive-file-size-limit")).isEqualTo("10");
		assertThat(gemfireProperties.getProperty("enable-time-statistics")).isEqualTo("true");
		assertThat(gemfireProperties.getProperty("statistic-sample-rate")).isEqualTo("100");
	}

	@EnableGemFireMockObjects
	@PeerCacheApplication
	@EnableGemFireProperties
	static class TestAuthGemFirePropertiesConfiguration { }

	@EnableGemFireMockObjects
	@PeerCacheApplication
	@EnableGemFireProperties
	@EnableLogging
	static class TestLoggingGemFirePropertiesConfiguration { }

	@EnableGemFireMockObjects
	@PeerCacheApplication
	@EnableGemFireProperties(name = "TestName", groups = { "TestGroupOne", "TestGroupTwo" })
	static class TestNameAndGroupsAnnotationBasedGemFirePropertiesConfiguration { }

	@EnableGemFireMockObjects
	@PeerCacheApplication
	@EnablePdx
	@SuppressWarnings("unused")
	static class TestPdxGemFirePropertiesConfiguration {

		@Bean
		PdxSerializer mockPdxSerializer() {
			return mock(PdxSerializer.class);
		}
	}

	@EnableGemFireMockObjects
	@PeerCacheApplication
	@EnableGemFireProperties
	@EnableSecurity
	static class TestSecurityGemFirePropertiesConfiguration { }

	@EnableGemFireMockObjects
	@PeerCacheApplication
	@EnableGemFireProperties(serializableObjectFilter = { "example.app.model.TypeOne", "example.app.model.TypeTwo" },
		validateSerializableObjects = true)
	static class TestSerializableObjectFilterAndValidateSerializableObjectsGemFirePropertiesConfiguration { }

	@EnableGemFireMockObjects
	@PeerCacheApplication
	@EnableGemFireProperties
	@EnableSsl(ciphers = "FISH", components = {
		EnableSsl.Component.CLUSTER, EnableSsl.Component.GATEWAY,
		EnableSsl.Component.LOCATOR, EnableSsl.Component.SERVER, EnableSsl.Component.WEB
	}, componentCertificateAliases = {
		@EnableSsl.ComponentAlias(component = EnableSsl.Component.GATEWAY, alias = "WanCert"),
		@EnableSsl.ComponentAlias(component = EnableSsl.Component.WEB, alias = "HttpCert")
	}, defaultCertificateAlias = "MockCert", protocols = "HTTP")
	static class TestSslGemFirePropertiesConfiguration { }

	@EnableGemFireMockObjects
	@PeerCacheApplication
	@EnableGemFireProperties
	@EnableStatistics
	static class TestStatisticsGemFirePropertiesConfiguration { }

}
