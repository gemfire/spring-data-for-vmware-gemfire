/*
 * Copyright 2022-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.config.annotation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.function.Function;
import org.junit.Test;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertySource;
import org.springframework.data.gemfire.client.ClientCacheFactoryBean;
import org.springframework.data.gemfire.tests.integration.SpringApplicationContextIntegrationTestsSupport;
import org.springframework.data.gemfire.tests.mock.annotation.EnableGemFireMockObjects;
import org.springframework.data.gemfire.util.ArrayUtils;
import org.springframework.mock.env.MockEnvironment;
import org.springframework.mock.env.MockPropertySource;
import org.springframework.util.StringUtils;

/**
 * Unit Tests for {@link EnableSsl} and {@link SslConfiguration}.
 *
 * @author John Blum
 * @author Srikanth Manvi
 * @see org.junit.Test
 * @see org.apache.geode.cache.client.ClientCache
 * @see org.springframework.data.gemfire.config.annotation.EnableSsl
 * @see org.springframework.data.gemfire.config.annotation.SslConfiguration
 * @see org.springframework.data.gemfire.tests.integration.SpringApplicationContextIntegrationTestsSupport
 * @see org.springframework.data.gemfire.tests.mock.annotation.EnableGemFireMockObjects
 * @since 2.1.0
 */
public class EnableSslConfigurationUnitTests extends SpringApplicationContextIntegrationTestsSupport {

	private ConfigurableApplicationContext newApplicationContext(PropertySource<?> testPropertySource,
			Class<?>... annotatedClasses) {

		Function<ConfigurableApplicationContext, ConfigurableApplicationContext> applicationContextInitializer = applicationContext -> {

			MutablePropertySources propertySources = applicationContext.getEnvironment().getPropertySources();

			propertySources.addFirst(testPropertySource);

			return applicationContext;
		};

		return newApplicationContext(applicationContextInitializer, annotatedClasses);
	}

	@Test
	public void sslAnnotationBasedConfigurationIsCorrect() {

		newApplicationContext(new MockPropertySource("TestPropertySource"), SslAnnotationBasedConfiguration.class);

		assertThat(containsBean("gemfireCache")).isTrue();
		assertThat(containsBean("gemfireProperties")).isTrue();

		ClientCacheFactoryBean clientCache = getBean("&gemfireCache", ClientCacheFactoryBean.class);

		assertThat(clientCache).isNotNull();

		Properties gemfireProperties = clientCache.getProperties();

		assertThat(gemfireProperties).isNotNull();
		assertThat(gemfireProperties.getProperty("ssl-ciphers")).isEqualTo("FISH,Scream,SEAL,SNOW");
		assertThat(gemfireProperties.getProperty("ssl-client-protocols")).isEqualTo("TLSv1.2 TLSv1.3");
		assertThat(gemfireProperties.getProperty("ssl-enabled-components")).isEqualTo("locator,server");
		assertThat(gemfireProperties.getProperty("ssl-default-alias")).isEqualTo("TestCert");
		assertThat(gemfireProperties.getProperty("ssl-locator-alias")).isEqualTo("LocatorCert");
		assertThat(gemfireProperties.getProperty("ssl-endpoint-identification-enabled")).isEqualTo("true");
		assertThat(gemfireProperties.getProperty("ssl-keystore")).isEqualTo("/path/to/keystore.jks");
		assertThat(gemfireProperties.getProperty("ssl-keystore-password")).isEqualTo("s3cr3t!");
		assertThat(gemfireProperties.getProperty("ssl-keystore-type")).isEqualTo("JKS");
		assertThat(gemfireProperties.getProperty("ssl-protocols")).isEqualTo("TCP/IP,HTTP");
		assertThat(gemfireProperties.getProperty("ssl-require-authentication")).isEqualTo("true");
		assertThat(gemfireProperties.getProperty("ssl-truststore")).isEqualTo("/path/to/truststore.jks");
		assertThat(gemfireProperties.getProperty("ssl-truststore-password")).isEqualTo("p@55w0rd!");
		assertThat(gemfireProperties.getProperty("ssl-truststore-type")).isEqualTo("PKCS11");
		assertThat(gemfireProperties.getProperty("ssl-use-default-context")).isEqualTo("true");
		assertThat(gemfireProperties).doesNotContainKeys("ssl-web-require-authentication", "ssl-server-protocols",
			"ssl-cluster-alias", "ssl-gateway-alias", "ssl-web-alias");
	}

	@Test
	public void sslPropertyBasedConfigurationIsCorrect() {

		PropertySource<?> testPropertySource = new MockPropertySource("TestPropertySource")
			.withProperty("spring.data.gemfire.security.ssl.ciphers", "Scream, SEAL, SNOW")
			.withProperty("spring.data.gemfire.security.ssl.client.protocols", "TLSv1.3")
			.withProperty("spring.data.gemfire.security.ssl.components", "locator, server")
			.withProperty("spring.data.gemfire.security.ssl.certificate.alias.default", "MockCert")
			.withProperty("spring.data.gemfire.security.ssl.certificate.alias.locator", "LocatorCert")
			.withProperty("spring.data.gemfire.security.ssl.certificate.alias.server", "ServerCert")
			.withProperty("spring.data.gemfire.security.ssl.enable-endpoint-identification", "true")
			.withProperty("spring.data.gemfire.security.ssl.keystore", "~/test/app/keystore.jks")
			.withProperty("spring.data.gemfire.security.ssl.keystore.password", "0p3nS@y5M3")
			.withProperty("spring.data.gemfire.security.ssl.keystore.type", "R2D2")
			.withProperty("spring.data.gemfire.security.ssl.protocols", "IP, TCP/IP, UDP")
			.withProperty("spring.data.gemfire.security.ssl.require-authentication", "false")
			.withProperty("spring.data.gemfire.security.ssl.truststore", "relative/path/to/trusted.keystore")
			.withProperty("spring.data.gemfire.security.ssl.truststore.password", "kn0ckKn0ck")
			.withProperty("spring.data.gemfire.security.ssl.truststore.type", "C3PO")
			.withProperty("spring.data.gemfire.security.ssl.use-default-context", "true");

		newApplicationContext(testPropertySource, SslPropertyBasedConfiguration.class);

		assertThat(containsBean("gemfireCache")).isTrue();
		assertThat(containsBean("gemfireProperties")).isTrue();

		ClientCacheFactoryBean clientCache = getBean("&gemfireCache", ClientCacheFactoryBean.class);

		assertThat(clientCache).isNotNull();

		Properties gemfireProperties = clientCache.getProperties();

		String sslEnabledComponents = Optional.ofNullable(gemfireProperties.getProperty("ssl-enabled-components"))
			.filter(StringUtils::hasText)
			.map(it -> StringUtils.arrayToCommaDelimitedString(
					ArrayUtils.sort(StringUtils.commaDelimitedListToStringArray(it))))
			.orElse(null);

		assertThat(gemfireProperties).isNotNull();
		assertThat(gemfireProperties.getProperty("ssl-ciphers")).isEqualTo("Scream, SEAL, SNOW");
		assertThat(gemfireProperties.getProperty("ssl-client-protocols")).isEqualTo("TLSv1.3");
		assertThat(sslEnabledComponents).isEqualTo("locator,server");
		assertThat(gemfireProperties.getProperty("ssl-default-alias")).isEqualTo("MockCert");
		assertThat(gemfireProperties.getProperty("ssl-locator-alias")).isEqualTo("LocatorCert");
		assertThat(gemfireProperties.getProperty("ssl-server-alias")).isEqualTo("ServerCert");
		assertThat(gemfireProperties.getProperty("ssl-endpoint-identification-enabled")).isEqualTo("true");
		assertThat(gemfireProperties.getProperty("ssl-keystore")).isEqualTo("~/test/app/keystore.jks");
		assertThat(gemfireProperties.getProperty("ssl-keystore-password")).isEqualTo("0p3nS@y5M3");
		assertThat(gemfireProperties.getProperty("ssl-keystore-type")).isEqualTo("R2D2");
		assertThat(gemfireProperties.getProperty("ssl-protocols")).isEqualTo("IP, TCP/IP, UDP");
		assertThat(gemfireProperties.getProperty("ssl-require-authentication")).isEqualTo("false");
		assertThat(gemfireProperties.getProperty("ssl-truststore")).isEqualTo("relative/path/to/trusted.keystore");
		assertThat(gemfireProperties.getProperty("ssl-truststore-password")).isEqualTo("kn0ckKn0ck");
		assertThat(gemfireProperties.getProperty("ssl-truststore-type")).isEqualTo("C3PO");
		assertThat(gemfireProperties.getProperty("ssl-use-default-context")).isEqualTo("true");
		assertThat(gemfireProperties).doesNotContainKeys("ssl-web-require-authentication", "ssl-server-protocols",
			"ssl-cluster-alias", "ssl-gateway-alias", "ssl-web-alias");
	}

	private Map<String, Object> defaultSslAttributes() {
		return AnnotationUtils.getAnnotationAttributes(
			SslPropertyBasedConfiguration.class.getAnnotation(EnableSsl.class), false, true);
	}

	@Test
	public void defaultAndEmptyComponentsEnableOnlyClientChannels() {
		SslConfiguration configuration = new SslConfiguration();
		Map<String, Object> attributes = defaultSslAttributes();

		assertThat(configuration.toGemFireProperties(attributes).getProperty("ssl-enabled-components"))
			.isEqualTo("locator,server");

		attributes.put("components", new EnableSsl.Component[0]);
		assertThat(configuration.toGemFireProperties(attributes).getProperty("ssl-enabled-components"))
			.isEqualTo("locator,server");
	}

	@Test
	public void environmentOverridesAnnotationChannelsAliasesAndClientProtocols() {
		SslConfiguration configuration = new SslConfiguration();
		configuration.setEnvironment(new MockEnvironment()
			.withProperty("spring.data.gemfire.security.ssl.components", " SERVER, server ")
			.withProperty("spring.data.gemfire.security.ssl.certificate.alias.locator", " OverrideCert ")
			.withProperty("spring.data.gemfire.security.ssl.client.protocols", "TLSv1.3"));

		Properties properties = configuration.toGemFireProperties(AnnotationUtils.getAnnotationAttributes(
			SslAnnotationBasedConfiguration.class.getAnnotation(EnableSsl.class), false, true));

		assertThat(properties.getProperty("ssl-enabled-components")).isEqualTo("server");
		assertThat(properties.getProperty("ssl-locator-alias")).isEqualTo("OverrideCert");
		assertThat(properties.getProperty("ssl-client-protocols")).isEqualTo("TLSv1.3");
	}

	@Test
	public void singleLocatorChannelIsRetained() {
		Map<String, Object> attributes = defaultSslAttributes();
		attributes.put("components", new EnableSsl.Component[] { EnableSsl.Component.LOCATOR });
		assertThat(new SslConfiguration().toGemFireProperties(attributes).getProperty("ssl-enabled-components"))
			.isEqualTo("locator");
	}

	@Test
	public void rejectsServerAndUnknownChannelsInsteadOfEnablingOrIgnoringThem() {
		for (String component : new String[] { "all", "cluster", "gateway", "web", "jmx", "grpc", "unknown" }) {
			for (String components : new String[] { component, "server," + component }) {
				SslConfiguration configuration = new SslConfiguration();
				configuration.setEnvironment(new MockEnvironment()
					.withProperty("spring.data.gemfire.security.ssl.components", components));

				assertThatThrownBy(() -> configuration.toGemFireProperties(defaultSslAttributes()))
					.isInstanceOf(IllegalArgumentException.class)
					.hasMessageContaining("use locator or server");
			}
		}
	}

	@Test
	public void rejectsRetiredSslPropertiesWithoutExposingTheirValues() {
		for (String suffix : new String[] { "server.protocols", "web-require-authentication",
			"certificate.alias.cluster", "certificate.alias.gateway", "certificate.alias.web",
			"certificate.alias.jmx", "certificate.alias.grpc", "certificate.alias.all" }) {
			String property = "spring.data.gemfire.security.ssl." + suffix;
			SslConfiguration configuration = new SslConfiguration();
			configuration.setEnvironment(new MockEnvironment().withProperty(property, "private-setting"));

			assertThatThrownBy(() -> configuration.toGemFireProperties(defaultSslAttributes()))
				.isInstanceOf(UnsupportedOperationException.class)
				.hasMessageContaining(property)
				.hasMessageContaining("configure server SSL on the server")
				.hasMessageNotContaining("private-setting");
		}
	}

	@Test
	public void blankRetiredSettingsRemainUnset() {
		SslConfiguration configuration = new SslConfiguration();
		configuration.setEnvironment(new MockEnvironment()
			.withProperty("spring.data.gemfire.security.ssl.server.protocols", " ")
			.withProperty("spring.data.gemfire.security.ssl.web-require-authentication", "")
			.withProperty("spring.data.gemfire.security.ssl.certificate.alias.gateway", " "));

		assertThat(configuration.toGemFireProperties(defaultSslAttributes()))
			.doesNotContainKeys("ssl-server-protocols", "ssl-web-require-authentication", "ssl-gateway-alias");
	}

	@Test
	public void sslAnnotationExposesOnlyClientChannelsAndSettings() {
		assertThat(EnableSsl.Component.values()).containsExactly(EnableSsl.Component.LOCATOR, EnableSsl.Component.SERVER);
		assertThat(EnableSsl.class.getDeclaredMethods()).extracting(java.lang.reflect.Method::getName)
			.contains("clientProtocols", "requireAuthentication", "defaultCertificateAlias")
			.doesNotContain("serverProtocols", "webRequireAuthentication");
	}

	@EnableGemFireMockObjects
	@ClientCacheApplication(logLevel = "error")
	@EnableSsl(
		ciphers = { "FISH", "Scream", "SEAL", "SNOW" },
		clientProtocols = { "TLSv1.2", "TLSv1.3" },
		components = { EnableSsl.Component.SERVER, EnableSsl.Component.LOCATOR },
		componentCertificateAliases = {
			@EnableSsl.ComponentAlias(component = EnableSsl.Component.LOCATOR, alias = "LocatorCert")
		},
		defaultCertificateAlias = "TestCert",
		enableEndpointIdentification = true,
		keystore = "/path/to/keystore.jks",
		keystorePassword = "s3cr3t!",
		protocols = { "TCP/IP", "HTTP" },
		truststore = "/path/to/truststore.jks",
		truststorePassword = "p@55w0rd!",
		truststoreType = "PKCS11",
		useDefaultContext = true
	)
	static class SslAnnotationBasedConfiguration { }

	@EnableGemFireMockObjects
	@ClientCacheApplication(logLevel = "error")
	@EnableSsl
	static class SslPropertyBasedConfiguration { }

}
