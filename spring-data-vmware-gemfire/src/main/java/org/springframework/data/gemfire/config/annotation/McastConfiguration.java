/*
 * Copyright (c) VMware, Inc. 2022-2023. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.config.annotation;

import java.lang.annotation.Annotation;
import java.util.Map;
import java.util.Properties;

import org.springframework.data.gemfire.config.annotation.support.EmbeddedServiceConfigurationSupport;
import org.springframework.data.gemfire.util.PropertiesBuilder;

/**
 * The {@link McastConfiguration} class is a Spring {@link org.springframework.context.annotation.ImportBeanDefinitionRegistrar}
 * that applies additional GemFire configuration by way of GemFire System properties to configure and use
 * multi-cast networking for GemFire communication and distribution rather than the (preferred)
 * Locator-based location services.
 *
 * @author John Blum
 * @see org.springframework.data.gemfire.config.annotation.EnableMcast
 * @see org.springframework.data.gemfire.config.annotation.support.EmbeddedServiceConfigurationSupport
 * @since 1.9.0
 */
public class McastConfiguration extends EmbeddedServiceConfigurationSupport {

	public static final int DEFAULT_MCAST_PORT = 10334;
	public static final int DEFAULT_MCAST_RECEIVE_BUFFER_SIZE = 1048576;
	public static final int DEFAULT_MCAST_SEND_BUFFER_SIZE = 65535;

	public static final String DEFAULT_MCAST_ADDRESS = "239.192.81.1";
	public static final String DEFAULT_MCAST_FLOW_CONTROL = "1048576,0.25,5000";

	/**
	 * Returns the {@link EnableMcast} {@link java.lang.annotation.Annotation} {@link Class} type.
	 *
	 * @return the {@link EnableMcast} {@link java.lang.annotation.Annotation} {@link Class} type.
	 * @see org.springframework.data.gemfire.config.annotation.EnableMcast
	 */
	@Override
	protected Class<? extends Annotation> getAnnotationType() {
		return EnableMcast.class;
	}

	@Override
	protected Properties toGemFireProperties(Map<String, Object> annotationAttributes) {

		PropertiesBuilder gemfireProperties = PropertiesBuilder.create();

		gemfireProperties.unsetProperty("locators");

		gemfireProperties.setPropertyIfNotDefault("mcast-address",
			resolveProperty(propertyName("mcast.address"),
				(String) annotationAttributes.get("address")), DEFAULT_MCAST_ADDRESS);

		gemfireProperties.setPropertyIfNotDefault("mcast-flow-control",
			resolveProperty(propertyName("mcast.flow-control"),
				(String) annotationAttributes.get("flowControl")), DEFAULT_MCAST_FLOW_CONTROL);

		gemfireProperties.setPropertyIfNotDefault("mcast-port",
			resolveProperty(propertyName("mcast.port"),
				(Integer) annotationAttributes.get("port")), DEFAULT_MCAST_PORT);

		gemfireProperties.setPropertyIfNotDefault("mcast-recv-buffer-size",
			resolveProperty(propertyName("mcast.receive-buffer-size"),
				(Integer) annotationAttributes.get("receiveBufferSize")), DEFAULT_MCAST_RECEIVE_BUFFER_SIZE);

		gemfireProperties.setPropertyIfNotDefault("mcast-send-buffer-size",
			resolveProperty(propertyName("mcast.send-buffer-size"),
				(Integer) annotationAttributes.get("sendBufferSize")), DEFAULT_MCAST_SEND_BUFFER_SIZE);

		return gemfireProperties.build();
	}
}
