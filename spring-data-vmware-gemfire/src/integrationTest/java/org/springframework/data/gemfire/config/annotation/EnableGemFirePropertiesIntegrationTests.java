/*
 * Copyright 2022-2025 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.config.annotation;

import org.apache.geode.cache.client.ClientCache;
import org.apache.geode.pdx.PdxSerializer;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertySource;
import org.springframework.data.gemfire.client.ClientCacheFactoryBean;
import org.springframework.data.gemfire.tests.integration.SpringApplicationContextIntegrationTestsSupport;
import org.springframework.data.gemfire.tests.mock.annotation.EnableGemFireMockObjects;
import org.springframework.data.gemfire.util.ArrayUtils;
import org.springframework.mock.env.MockPropertySource;
import org.springframework.util.StringUtils;

import java.util.Optional;
import java.util.Properties;
import java.util.function.Function;

/**
 * Integration tests for {@link EnableGemFireProperties}, {@link EnableLogging},
 * {@link EnableSecurity}, {@link EnableSsl}, {@link EnableStatistics}.
 *
 * @author John Blum
 * @see java.util.Properties
 * @see org.junit.Test
 * @see org.apache.geode.cache.client.ClientCache
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

		Assertions.assertThat(containsBean("gemfireCache")).isTrue();

		ClientCache gemfireCache = getBean("gemfireCache", ClientCache.class);

		Assertions.assertThat(gemfireCache).isNotNull();
		Assertions.assertThat(gemfireCache.getDistributedSystem()).isNotNull();

		Properties gemfireProperties = gemfireCache.getDistributedSystem().getProperties();

		Assertions.assertThat(gemfireProperties).isNotNull();
		Assertions.assertThat(gemfireProperties.getProperty("log-disk-space-limit")).isEqualTo("100");
		Assertions.assertThat(gemfireProperties.getProperty("log-file")).isEqualTo("/path/to/file.log");
		Assertions.assertThat(gemfireProperties.getProperty("log-file-size-limit")).isEqualTo("10");
		Assertions.assertThat(gemfireProperties.getProperty("log-level")).isEqualTo("info");
	}

	@Test
	public void nameAndGroupsAnnotationBasedGemFirePropertiesConfiguration() {

		newApplicationContext(TestNameAndGroupsAnnotationBasedGemFirePropertiesConfiguration.class);

		Assertions.assertThat(containsBean("gemfireCache")).isTrue();
		Assertions.assertThat(containsBean("gemfireProperties")).isTrue();

		//Properties gemfireProperties = this.applicationContext.getBean("gemfireProperties", Properties.class);

		ClientCache gemfireCache = getBean("gemfireCache", ClientCache.class);

		Assertions.assertThat(gemfireCache).isNotNull();
		Assertions.assertThat(gemfireCache.getDistributedSystem()).isNotNull();

		Properties gemfireProperties = gemfireCache.getDistributedSystem().getProperties();

		Assertions.assertThat(gemfireProperties).isNotNull();
		Assertions.assertThat(gemfireProperties.containsKey("name")).isTrue();
		Assertions.assertThat(gemfireProperties.getProperty("name")).isEqualTo("TestName");
		Assertions.assertThat(gemfireProperties.containsKey("groups")).isTrue();
		Assertions.assertThat(gemfireProperties.getProperty("groups")).isEqualTo("TestGroupOne,TestGroupTwo");
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

		Assertions.assertThat(containsBean("gemfireCache")).isTrue();
		Assertions.assertThat(containsBean("mockPdxSerializer")).isTrue();

		ClientCacheFactoryBean gemfireCache = getBean("&gemfireCache", ClientCacheFactoryBean.class);

		Assertions.assertThat(gemfireCache).isNotNull();
		Assertions.assertThat(gemfireCache.getPdxDiskStoreName()).isEqualTo("TestDiskStore");
		Assertions.assertThat(gemfireCache.getPdxIgnoreUnreadFields()).isTrue();
		Assertions.assertThat(gemfireCache.getPdxPersistent()).isTrue();
		Assertions.assertThat(gemfireCache.getPdxReadSerialized()).isTrue();

		PdxSerializer mockPdxSerializer = getBean("mockPdxSerializer", PdxSerializer.class);

		Assertions.assertThat(mockPdxSerializer).isNotNull();
		Assertions.assertThat(gemfireCache.getPdxSerializer()).isEqualTo(mockPdxSerializer);
	}

	@Test
	public void securityGemFirePropertiesConfiguration() {

		PropertySource testPropertySource = new MockPropertySource("TestPropertySource")
			.withProperty("spring.data.gemfire.security.client.authentication-initializer", "example.security.client.AuthenticationInitializer")
			.withProperty("spring.data.gemfire.security.peer.authentication-initializer", "example.security.peer.AuthenticationInitializer")
			.withProperty("spring.data.gemfire.security.manager.class-name", "example.security.SecurityManager")
			.withProperty("spring.data.gemfire.security.postprocessor.class-name", "example.security.PostProcessor");

		newApplicationContext(testPropertySource, TestSecurityGemFirePropertiesConfiguration.class);

		Assertions.assertThat(containsBean("gemfireCache")).isTrue();

		ClientCache gemfireCache = getBean("gemfireCache", ClientCache.class);

		Assertions.assertThat(gemfireCache).isNotNull();
		Assertions.assertThat(gemfireCache.getDistributedSystem()).isNotNull();

		Properties gemfireProperties = gemfireCache.getDistributedSystem().getProperties();

		Assertions.assertThat(gemfireProperties).isNotNull();
		Assertions.assertThat(gemfireProperties.getProperty("security-client-auth-init")).isEqualTo("example.security.client.AuthenticationInitializer");
		Assertions.assertThat(gemfireProperties.getProperty("security-peer-auth-init")).isEqualTo("example.security.peer.AuthenticationInitializer");
		Assertions.assertThat(gemfireProperties.getProperty("security-manager")).isEqualTo("example.security.SecurityManager");
		Assertions.assertThat(gemfireProperties.getProperty("security-post-processor")).isEqualTo("example.security.PostProcessor");
	}

	@Test
	public void serializableObjectFilterAndValidateSerializableObjectsGemFirePropertiesConfiguration() {

		newApplicationContext(TestSerializableObjectFilterAndValidateSerializableObjectsGemFirePropertiesConfiguration.class);

		Assertions.assertThat(containsBean("gemfireProperties")).isTrue();

		Properties gemfireProperties = getBean("gemfireProperties", Properties.class);

		Assertions.assertThat(gemfireProperties).isNotNull();
		Assertions.assertThat(gemfireProperties.containsKey("serializable-object-filter")).isTrue();
		Assertions.assertThat(gemfireProperties.getProperty("serializable-object-filter"))
			.isEqualTo("example.app.model.TypeOne,example.app.model.TypeTwo");
		Assertions.assertThat(gemfireProperties.containsKey("validate-serializable-objects")).isTrue();
		Assertions.assertThat(gemfireProperties.getProperty("validate-serializable-objects")).isEqualTo("true");
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

		Assertions.assertThat(containsBean("gemfireCache")).isTrue();
		Assertions.assertThat(containsBean("gemfireProperties")).isTrue();

		ClientCache gemfireCache = getBean("gemfireCache", ClientCache.class);

		Assertions.assertThat(gemfireCache).isNotNull();
		Assertions.assertThat(gemfireCache.getDistributedSystem()).isNotNull();

		Properties gemfireProperties = gemfireCache.getDistributedSystem().getProperties();

		String sslEnabledComponents = Optional.ofNullable(gemfireProperties.getProperty("ssl-enabled-components"))
			.filter(StringUtils::hasText)
			.map(it -> StringUtils.arrayToCommaDelimitedString(ArrayUtils.sort(
				StringUtils.commaDelimitedListToStringArray(it))))
			.orElse(null);

		Assertions.assertThat(gemfireProperties).isNotNull();
		Assertions.assertThat(gemfireProperties.getProperty("ssl-ciphers")).isEqualTo("DSA, RSA");
		Assertions.assertThat(sslEnabledComponents).isEqualTo("cluster,gateway,locator,server,web");
		Assertions.assertThat(gemfireProperties.getProperty("ssl-default-alias")).isEqualTo("TestCert");
		Assertions.assertThat(gemfireProperties.getProperty("ssl-keystore")).isEqualTo("/path/to/keystore");
		Assertions.assertThat(gemfireProperties.getProperty("ssl-keystore-password")).isEqualTo("p@55w0rd");
		Assertions.assertThat(gemfireProperties.getProperty("ssl-keystore-type")).isEqualTo("JKS");
		Assertions.assertThat(gemfireProperties.getProperty("ssl-protocols")).isEqualTo("IP, TCP/IP, UDP");
		Assertions.assertThat(gemfireProperties.getProperty("ssl-require-authentication")).isEqualTo("false");
		Assertions.assertThat(gemfireProperties.getProperty("ssl-truststore")).isEqualTo("/path/to/truststore");
		Assertions.assertThat(gemfireProperties.getProperty("ssl-truststore-password")).isEqualTo("p@55w0rd");
		Assertions.assertThat(gemfireProperties.getProperty("ssl-truststore-type")).isEqualTo("PKCS11");
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

		Assertions.assertThat(containsBean("gemfireCache")).isTrue();

		ClientCache gemfireCache = getBean("gemfireCache", ClientCache.class);

		Assertions.assertThat(gemfireCache).isNotNull();
		Assertions.assertThat(gemfireCache.getDistributedSystem()).isNotNull();

		Properties gemfireProperties = gemfireCache.getDistributedSystem().getProperties();

		Assertions.assertThat(gemfireProperties).isNotNull();
		Assertions.assertThat(gemfireProperties.getProperty("statistic-sampling-enabled")).isEqualTo("true");
		Assertions.assertThat(gemfireProperties.getProperty("archive-disk-space-limit")).isEqualTo("50");
		Assertions.assertThat(gemfireProperties.getProperty("statistic-archive-file")).isEqualTo("/path/to/archive.stats");
		Assertions.assertThat(gemfireProperties.getProperty("archive-file-size-limit")).isEqualTo("10");
		Assertions.assertThat(gemfireProperties.getProperty("enable-time-statistics")).isEqualTo("true");
		Assertions.assertThat(gemfireProperties.getProperty("statistic-sample-rate")).isEqualTo("100");
	}

	@EnableGemFireMockObjects
	@ClientCacheApplication
	@EnableGemFireProperties
	static class TestAuthGemFirePropertiesConfiguration { }

	@EnableGemFireMockObjects
	@ClientCacheApplication
	@EnableGemFireProperties
	@EnableLogging
	static class TestLoggingGemFirePropertiesConfiguration { }

	@EnableGemFireMockObjects
	@ClientCacheApplication
	@EnableGemFireProperties(name = "TestName", groups = { "TestGroupOne", "TestGroupTwo" })
	static class TestNameAndGroupsAnnotationBasedGemFirePropertiesConfiguration { }

	@EnableGemFireMockObjects
	@ClientCacheApplication
	@EnablePdx
	@SuppressWarnings("unused")
	static class TestPdxGemFirePropertiesConfiguration {

		@Bean
		PdxSerializer mockPdxSerializer() {
			return Mockito.mock(PdxSerializer.class);
		}
	}

	@EnableGemFireMockObjects
	@ClientCacheApplication
	@EnableGemFireProperties
	@EnableSecurity
	static class TestSecurityGemFirePropertiesConfiguration { }

	@EnableGemFireMockObjects
	@ClientCacheApplication
	@EnableGemFireProperties(serializableObjectFilter = { "example.app.model.TypeOne", "example.app.model.TypeTwo" },
		validateSerializableObjects = true)
	static class TestSerializableObjectFilterAndValidateSerializableObjectsGemFirePropertiesConfiguration { }

	@EnableGemFireMockObjects
	@ClientCacheApplication
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
	@ClientCacheApplication
	@EnableGemFireProperties
	@EnableStatistics
	static class TestStatisticsGemFirePropertiesConfiguration { }

}
