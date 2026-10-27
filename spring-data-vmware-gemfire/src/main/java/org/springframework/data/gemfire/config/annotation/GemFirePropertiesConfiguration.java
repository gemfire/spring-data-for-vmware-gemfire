/*
 * Copyright 2022-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.config.annotation;

import java.lang.annotation.Annotation;
import java.util.Map;
import java.util.Properties;

import org.springframework.data.gemfire.config.annotation.support.EmbeddedServiceConfigurationSupport;
import org.springframework.data.gemfire.util.PropertiesBuilder;

/**
 * The {@link GemFirePropertiesConfiguration} class is a Spring {@link org.springframework.context.annotation.ImportBeanDefinitionRegistrar}
 * capable of configuring additional GemFire Properties on a (Spring Boot) application class at runtime
 * during startup.
 *
 * @author John Blum
 * @see EnableGemFireProperties
 * @see EmbeddedServiceConfigurationSupport
 * @see PropertiesBuilder
 * @since 1.9.0
 */
public class GemFirePropertiesConfiguration extends EmbeddedServiceConfigurationSupport {

	public static final boolean DEFAULT_DELTA_PROPAGATION = true;
	public static final boolean DEFAULT_LOCK_MEMORY = false;
	public static final boolean DEFAULT_VALIDATE_SERIALIZABLE_OBJECTS = false;

	public static final int DEFAULT_DISTRIBUTED_SYSTEM_ID = -1;

	public static final long DEFAULT_MEMBER_TIMEOUT = 5000L;

	public static final String DEFAULT_CACHE_XML_FILE = "";
	public static final String DEFAULT_CONFLATE_EVENTS = "server";
	public static final String DEFAULT_NAME = "";

	@SuppressWarnings("unused")
	public static final String[] DEFAULT_GROUPS = {};

	@SuppressWarnings("unused")
	public static final String[] DEFAULT_SERIALIZABLE_OBJECT_FILTER = {};

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Class<? extends Annotation> getAnnotationType() {
		return EnableGemFireProperties.class;
	}

	@Override
	protected Properties toGemFireProperties(Map<String, Object> annotationAttributes) {

		PropertiesBuilder gemfireProperties = new PropertiesBuilder();

		gemfireProperties.setProperty("cache-xml-file", annotationAttributes.get("cacheXmlFile"));

		gemfireProperties.setPropertyIfNotDefault("conflate-events",
			annotationAttributes.get("conflateEvents"), DEFAULT_CONFLATE_EVENTS);

		gemfireProperties.setPropertyIfNotDefault("delta-propagation",
			annotationAttributes.get("deltaPropagation"), DEFAULT_DELTA_PROPAGATION);

		gemfireProperties.setPropertyIfNotDefault("distributed-system-id",
			annotationAttributes.get("distributedSystemId"), DEFAULT_DISTRIBUTED_SYSTEM_ID);

		gemfireProperties.setProperty("groups", (String[]) annotationAttributes.get("groups"));

		gemfireProperties.setPropertyIfNotDefault("lock-memory",
			annotationAttributes.get("lockMemory"), DEFAULT_LOCK_MEMORY);

		gemfireProperties.setPropertyIfNotDefault("member-timeout",
			annotationAttributes.get("memberTimeout"), DEFAULT_MEMBER_TIMEOUT);

		gemfireProperties.setProperty("name", annotationAttributes.get("name"));

		gemfireProperties.setProperty("serializable-object-filter",
			String.join(";", (String[]) annotationAttributes.get("serializableObjectFilter")));

		gemfireProperties.setPropertyIfNotDefault("validate-serializable-objects",
			annotationAttributes.get("validateSerializableObjects"), DEFAULT_VALIDATE_SERIALIZABLE_OBJECTS);

		return gemfireProperties.build();
	}
}
