/*
 * Copyright (c) VMware, Inc. 2022-2024. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.config.annotation;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.data.gemfire.config.annotation.support.EmbeddedServiceConfigurationSupport;
import org.springframework.data.gemfire.util.CollectionUtils;
import org.springframework.data.gemfire.util.PropertiesBuilder;
import org.springframework.util.StringUtils;

/**
 * The {@link SslConfiguration} class is a Spring {@link ImportBeanDefinitionRegistrar} that applies
 * additional configuration using Pivotal GemFire/Apache Geode {@link Properties} to configure SSL.
 *
 * @author John Blum
 * @author Srikanth Manvi
 * @see EnableSsl
 * @see EmbeddedServiceConfigurationSupport
 * @since 1.9.0
 */
public class SslConfiguration extends EmbeddedServiceConfigurationSupport {

	private static final String SPACE_DELIMITER = " ";

	/**
	 * Returns the {@link EnableSsl} {@link Annotation} {@link Class type}.
	 *
	 * @return the {@link EnableSsl} {@link Annotation} {@link Class type}.
	 * @see EnableSsl
	 */
	@Override
	protected Class<? extends Annotation> getAnnotationType() {
		return EnableSsl.class;
	}

	@Override
	protected Properties toGemFireProperties(Map<String, Object> annotationAttributesMap) {

		AnnotationAttributes annotationAttributes = AnnotationAttributes.fromMap(annotationAttributesMap);

		PropertiesBuilder gemfireProperties = PropertiesBuilder.create();

		Set<EnableSsl.Component> components = resolveComponents(annotationAttributes);

		gemfireProperties.setProperty("ssl-enabled-components",
			StringUtils.collectionToCommaDelimitedString(components.stream()
				.map(EnableSsl.Component::toString)
				.collect(Collectors.toSet())))

			.setProperty("ssl-ciphers",
				resolveProperty(sslProperty("ciphers"),
					StringUtils.arrayToCommaDelimitedString(annotationAttributes.getStringArray("ciphers"))))

			.setProperty("ssl-client-protocols",
				resolveProperty(sslProperty("client.protocols"),
					StringUtils.arrayToDelimitedString(annotationAttributes.getStringArray("clientProtocols"),
						SPACE_DELIMITER)))

			.setPropertyIfNotDefault("ssl-default-alias",
				resolveProperty(sslProperty("certificate.alias.default"),
					annotationAttributes.getString("defaultCertificateAlias")), "")

			.setProperty("ssl-endpoint-identification-enabled",
				resolveProperty(sslProperty("enable-endpoint-identification"),
					annotationAttributes.getBoolean("enableEndpointIdentification")))

			.setProperty("ssl-keystore",
				resolveProperty(sslProperty("keystore"),
					annotationAttributes.getString("keystore")))

			.setProperty("ssl-keystore-password",
				resolveProperty(sslProperty("keystore.password"),
					annotationAttributes.getString("keystorePassword")))

			.setProperty("ssl-keystore-type",
				resolveProperty(sslProperty("keystore.type"),
					annotationAttributes.getString("keystoreType")))

			.setProperty("ssl-protocols",
				resolveProperty(sslProperty("protocols"),
					StringUtils.arrayToCommaDelimitedString(annotationAttributes.getStringArray("protocols"))))

			.setProperty("ssl-require-authentication",
				resolveProperty(sslProperty("require-authentication"),
					annotationAttributes.getBoolean("requireAuthentication")))

			.setProperty("ssl-server-protocols",
				resolveProperty(sslProperty("server.protocols"),
					StringUtils.arrayToDelimitedString(annotationAttributes.getStringArray("serverProtocols"),
						SPACE_DELIMITER)))

			.setProperty("ssl-truststore",
				resolveProperty(sslProperty("truststore"),
					annotationAttributes.getString("truststore")))

			.setProperty("ssl-truststore-password",
				resolveProperty(sslProperty("truststore.password"),
					annotationAttributes.getString("truststorePassword")))

			.setProperty("ssl-truststore-type",
				resolveProperty(sslProperty("truststore.type"),
					annotationAttributes.getString("truststoreType")))

			.setProperty("ssl-use-default-context",
				resolveProperty(sslProperty("use-default-context"),
					annotationAttributes.getBoolean("useDefaultContext")))

			.setProperty("ssl-web-require-authentication",
				resolveProperty(sslProperty("web-require-authentication"),
					annotationAttributes.getBoolean("webRequireAuthentication")));

		configureComponentCertificateAliases(annotationAttributes, gemfireProperties);

		return gemfireProperties.build();
	}

	private Set<EnableSsl.Component> resolveComponents(AnnotationAttributes annotationAttributes) {

		Set<EnableSsl.Component> components =
			Arrays.stream(Optional.of(resolveProperty(sslProperty("components"), ""))
				.filter(StringUtils::hasText)
				.map(StringUtils::commaDelimitedListToStringArray)
				.orElseGet(() -> new String[0]))
				.map(EnableSsl.Component::valueOfName)
				.collect(Collectors.toSet());

		components = components.isEmpty()
			? CollectionUtils.asSet((EnableSsl.Component[]) annotationAttributes.get("components"))
			: components;

		components = components.isEmpty() ? Collections.singleton(EnableSsl.Component.ALL) : components;

		return components;
	}

	private void configureComponentCertificateAliases(AnnotationAttributes annotationAttributes,
			PropertiesBuilder gemfireProperties) {

		AnnotationAttributes[] componentCertificateAliases =
			annotationAttributes.getAnnotationArray("componentCertificateAliases");

		Arrays.stream(componentCertificateAliases).forEach(aliasAttributes -> {

			EnableSsl.Component component = aliasAttributes.getEnum("component");
			String alias = aliasAttributes.getString("alias");

			gemfireProperties.setProperty(String.format("ssl-%s-alias", component), alias);
		});

		Arrays.stream(EnableSsl.Component.values()).forEach(component -> {

			String propertyNameSuffix = String.format("certificate.alias.%s", component);

			Optional.ofNullable(resolveProperty(sslProperty(propertyNameSuffix), ""))
				.filter(StringUtils::hasText)
				.ifPresent(alias ->
					gemfireProperties.setProperty(String.format("ssl-%s-alias", component),
						StringUtils.trimWhitespace(alias)));
		});
	}
}
