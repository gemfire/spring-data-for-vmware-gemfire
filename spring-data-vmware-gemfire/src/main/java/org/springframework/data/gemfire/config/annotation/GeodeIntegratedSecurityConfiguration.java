/*
 * Copyright 2022-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.config.annotation;

import java.lang.annotation.Annotation;
import java.util.Map;
import java.util.Properties;

import org.springframework.data.gemfire.GemFireProperties;
import org.springframework.data.gemfire.config.annotation.support.EmbeddedServiceConfigurationSupport;
import org.springframework.data.gemfire.util.PropertiesBuilder;
import org.springframework.util.StringUtils;

/**
 * The {@link GeodeIntegratedSecurityConfiguration} class is a {@link EmbeddedServiceConfigurationSupport} implementation
 * that configures client authentication for VMware GemFire.
 *
 * @author John Blum
 * @see EmbeddedServiceConfigurationSupport
 * @since 1.0.0
 */
@SuppressWarnings("unused")
public class GeodeIntegratedSecurityConfiguration extends EmbeddedServiceConfigurationSupport {

	protected static final String SECURITY_CLIENT_AUTH_INIT = GemFireProperties.SECURITY_CLIENT_AUTH_INIT.getName();

	/**
	 * Returns the {@link EnableSecurity} {@link Annotation} {@link Class} type.
	 *
	 * @return the {@link EnableSecurity} {@link Annotation} {@link Class} type.
	 * @see EnableSecurity
	 */
	@Override
	protected Class<? extends Annotation> getAnnotationType() {
		return EnableSecurity.class;
	}

	@Override
	protected Properties toGemFireProperties(Map<String, Object> annotationAttributes) {

		PropertiesBuilder gemfireProperties = PropertiesBuilder.create();

		String shiroIniResourcePath = resolveProperty(securityProperty("shiro.ini-resource-path"), (String) null);

		if (StringUtils.hasText(shiroIniResourcePath)) {
			throw new UnsupportedOperationException("security-shiro-init is unavailable in GemFire 10.4; "
				+ "configure client authentication instead");
		}

		if (resolveProperty(securityProperty("shiro.enabled"), Boolean.class, false)) {
			throw new UnsupportedOperationException("spring.data.gemfire.security.shiro.enabled is no longer supported; "
				+ "configure client authentication instead");
		}

		for (String suffix : new String[] { "manager.class-name", "postprocessor.class-name" }) {
			String property = securityProperty(suffix);
			if (StringUtils.hasText(resolveProperty(property, (String) null))) {
				throw new UnsupportedOperationException(property + " configures a server security callback; "
					+ "configure it on the server and use client authentication here");
			}
		}

		gemfireProperties.setProperty(SECURITY_CLIENT_AUTH_INIT,
			resolveProperty(securityProperty("client.authentication-initializer"),
				(String) annotationAttributes.get("clientAuthenticationInitializer")));

		return gemfireProperties.build();
	}
}
