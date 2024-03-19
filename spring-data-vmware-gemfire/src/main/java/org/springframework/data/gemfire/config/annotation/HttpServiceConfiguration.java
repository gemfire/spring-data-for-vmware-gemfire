/*
 * Copyright 2022-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package org.springframework.data.gemfire.config.annotation;

import java.lang.annotation.Annotation;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;

import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.data.gemfire.config.annotation.support.EmbeddedServiceConfigurationSupport;
import org.springframework.data.gemfire.util.PropertiesBuilder;

/**
 * The {@link HttpServiceConfiguration} class is a Spring {@link ImportBeanDefinitionRegistrar} that applies
 * additional configuration by way of Pivotal GemFire/Apache Geode {@link Properties} to configure
 * Pivotal GemFire/Apache Geode's embedded HTTP service and dependent services (e.g. Pulse).
 *
 * @author John Blum
 * @see ImportBeanDefinitionRegistrar
 * @see EnableHttpService
 * @see EmbeddedServiceConfigurationSupport
 * @see <a href="https://geode.apache.org/docs/guide/113/rest_apps/book_intro.html">Developing REST Applications for Apache Geode</a>
 * @since 1.9.0
 */
public class HttpServiceConfiguration extends EmbeddedServiceConfigurationSupport {

	public static final boolean DEFAULT_HTTP_SERVICE_SSL_REQUIRE_AUTHENTICATION = false;
	public static final boolean DEFAULT_HTTP_SERVICE_START_DEVELOPER_REST_API = false;

	public static final int DEFAULT_HTTP_SERVICE_PORT = 7070;

	/**
	 * Returns the {@link EnableHttpService} {@link Annotation} {@link Class} type.
	 *
	 * @return the {@link EnableHttpService} {@link Annotation} {@link Class} type.
	 * @see EnableHttpService
	 */
	@Override
	protected Class<? extends Annotation> getAnnotationType() {
		return EnableHttpService.class;
	}

	@Override
	protected Properties toGemFireProperties(Map<String, Object> annotationAttributes) {

		return Optional.of(resolveProperty(httpServiceProperty("enabled"), Boolean.TRUE))
			.filter(Boolean.TRUE::equals)
			.map(enabled -> {

				PropertiesBuilder gemfireProperties = PropertiesBuilder.create();

				gemfireProperties.setProperty("http-service-bind-address",
					resolveProperty(httpServiceProperty("bind-address"),
						(String) annotationAttributes.get("bindAddress")));

				gemfireProperties.setPropertyIfNotDefault("http-service-port",
					resolveProperty(httpServiceProperty("port"),
						(Integer) annotationAttributes.get("port")),
							DEFAULT_HTTP_SERVICE_PORT);

				gemfireProperties.setPropertyIfNotDefault("http-service-ssl-require-authentication",
					resolveProperty(httpServiceProperty("ssl-require-authentication"),
						(Boolean) annotationAttributes.get("sslRequireAuthentication")),
							DEFAULT_HTTP_SERVICE_SSL_REQUIRE_AUTHENTICATION);

				gemfireProperties.setPropertyIfNotDefault("start-dev-rest-api",
					resolveProperty(httpServiceProperty("dev-rest-api.start"),
						(Boolean) annotationAttributes.get("startDeveloperRestApi")),
							DEFAULT_HTTP_SERVICE_START_DEVELOPER_REST_API);

				return gemfireProperties.build();

			})
			.orElseGet(Properties::new);
	}
}
