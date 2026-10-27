/*
 * Copyright 2022-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.config.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.apache.geode.security.AuthInitialize;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * The {@link EnableSecurity} annotation marks a Spring {@link Configuration @Configuration} annotated {@link Class}
 * to configure client authentication with VMware GemFire.
 *
 * @author John Blum
 * @see java.lang.annotation.Annotation
 * @see AuthInitialize
 * @see Import
 * @see AuthenticationBeanConfiguration
 * @see AutoConfiguredAuthenticationConfiguration
 * @see GeodeIntegratedSecurityConfiguration
 * @since 1.0.0
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
@Import({
	AuthenticationBeanConfiguration.class,
	AutoConfiguredAuthenticationConfiguration.class,
	GeodeIntegratedSecurityConfiguration.class
})
@UsesGemFireProperties
@SuppressWarnings({ "unused" })
public @interface EnableSecurity {

	/**
	 * Used for authentication. Static creation method returning an {@link AuthInitialize} object,
	 * which obtains credentials for clients.
	 *
	 * Defaults to unset.
	 *
	 * Use the {@literal spring.data.gemfire.security.client.authentication-initializer} property
	 * in {@literal application.properties}.
	 */
	String clientAuthenticationInitializer() default "";

	/**
	 * The {@literal security-username} used by a GemFire cache client application required to authenticate.
	 *
	 * Defaults to unset.
	 *
	 * Use the {@literal spring.data.gemfire.security.username} in {@literal application.properties}.
	 */
	String securityUsername() default "";

	/**
	 * The {@literal security-password} used by a GemFire cache client application required to authenticate.
	 *
	 * Defaults to unset.
	 *
	 * Use the {@literal spring.data.gemfire.security.password} in {@literal application.properties}.
	 */
	String securityPassword() default "";

}
