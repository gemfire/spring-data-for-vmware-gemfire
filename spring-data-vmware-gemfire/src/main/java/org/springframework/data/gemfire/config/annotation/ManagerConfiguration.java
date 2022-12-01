/*
 * Copyright (c) VMware, Inc. 2022. All rights reserved.
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
 * The {@link ManagerConfiguration} class is a Spring {@link ImportBeanDefinitionRegistrar} that applies
 * additional configuration using Pivotal GemFire/Apache Geode {@link Properties} to configure an embedded Manager
 * in this cluster member.
 *
 * @author John Blum
 * @see ImportBeanDefinitionRegistrar
 * @see EnableManager
 * @see EmbeddedServiceConfigurationSupport
 * @since 1.9.0
 */
public class ManagerConfiguration extends EmbeddedServiceConfigurationSupport {

	protected static final int DEFAULT_JMX_MANAGER_PORT = 1099;

	/**
	 * Returns the {@link EnableManager} {@link Annotation} {@link Class} type.
	 *
	 * @return the {@link EnableManager} {@link Annotation} {@link Class} type.
	 * @see EnableManager
	 */
	@Override
	protected Class<? extends Annotation> getAnnotationType() {
		return EnableManager.class;
	}

	@Override
	protected Properties toGemFireProperties(Map<String, Object> annotationAttributes) {

		return Optional.of(resolveProperty(managerProperty("enabled"), Boolean.TRUE))
			.filter(Boolean.TRUE::equals)
			.map(enabled -> {

				PropertiesBuilder gemfireProperties = PropertiesBuilder.create();

				gemfireProperties.setProperty("jmx-manager",
					resolveProperty(managerProperty("enabled"), Boolean.TRUE));

				gemfireProperties.setProperty("jmx-manager-access-file",
					resolveProperty(managerProperty("access-file"),
						(String) annotationAttributes.get("accessFile")));

				gemfireProperties.setProperty("jmx-manager-bind-address",
					resolveProperty(managerProperty("bind-address"),
						(String) annotationAttributes.get("bindAddress")));

				gemfireProperties.setProperty("jmx-manager-hostname-for-clients",
					resolveProperty(managerProperty("hostname-for-clients"),
						(String) annotationAttributes.get("hostnameForClients")));

				gemfireProperties.setProperty("jmx-manager-password-file",
					resolveProperty(managerProperty("password-file"),
						(String) annotationAttributes.get("passwordFile")));

				gemfireProperties.setProperty("jmx-manager-port",
					resolvePort(resolveProperty(managerProperty("port"),
						(Integer) annotationAttributes.get("port")), DEFAULT_JMX_MANAGER_PORT));

				gemfireProperties.setProperty("jmx-manager-start",
					resolveProperty(managerProperty("start"), (Boolean) annotationAttributes.get("start")));

				gemfireProperties.setProperty("jmx-manager-update-rate",
					resolveProperty(managerProperty("update-rate"),
						(Integer) annotationAttributes.get("updateRate")));

				return gemfireProperties.build();

			})
			.orElseGet(Properties::new);
	}
}
