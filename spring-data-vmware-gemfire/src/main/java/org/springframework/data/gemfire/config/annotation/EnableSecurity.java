/*
 * Copyright (c) VMware, Inc. 2022-2023. All rights reserved.
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
 * to configure and enable Pivotal GemFire/Apache Geode's Security features for authentication, authorization
 * and post processing.
 *
 * @author John Blum
 * @see java.lang.annotation.Annotation
 * @see org.apache.geode.security.AuthInitialize
 * @see org.apache.geode.security.SecurityManager
 * @see org.apache.geode.security.PostProcessor
 * @see org.springframework.context.annotation.Import
 * @see org.springframework.data.gemfire.config.annotation.ApacheShiroSecurityConfiguration
 * @see org.springframework.data.gemfire.config.annotation.AuthenticationBeanConfiguration
 * @see org.springframework.data.gemfire.config.annotation.AutoConfiguredAuthenticationConfiguration
 * @see org.springframework.data.gemfire.config.annotation.GeodeIntegratedSecurityConfiguration
 * @since 1.0.0
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
@Import({
	ApacheShiroSecurityConfiguration.class,
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
	 * Used with authentication. Static creation method returning an {@link AuthInitialize} object, which obtains
	 * credentials for peers in a distributed system.
	 *
	 * Defaults to unset.
	 *
	 * Use the {@literal spring.data.gemfire.security.peer.authentication-initializer} property
	 * in {@literal application.properties}.
	 */
	String peerAuthenticationInitializer() default "";

	/**
	 * Specifies the application {@link Class} type implementing the Apache Geode
	 * {@link org.apache.geode.security.SecurityManager} interface to enable security in Apache Geode.
	 *
	 * Defaults to {@link Void}.
	 */
	Class<?> securityManagerClass() default Void.class;

	/**
	 * Specifies the fully-qualified class name of the application {@link Class} implementing the Apache Geode
	 * {@link org.apache.geode.security.SecurityManager} interface to enable security in Apache Geode.
	 *
	 * Use this Annotation attribute if you are uncertain whether the application class is on the classpath or not.
	 *
	 * Default is unset.
	 *
	 * Use the {@literal spring.data.gemfire.security.manager.class-name} property in {@literal application.properties}.
	 */
	String securityManagerClassName() default "";

	/**
	 * Specifies the application {@link Class} type implementing the Apache Geode
	 * {@link org.apache.geode.security.PostProcessor} interface, which used to transform sensitive data
	 * returned from secure data access operations.
	 *
	 * Defaults to {@link Void}.
	 */
	Class<?> securityPostProcessorClass() default Void.class;

	/**
	 * Specifies the fully-qualified class name of the application {@link Class} implementing the Apache Geode
	 * {@link org.apache.geode.security.PostProcessor} interface, which used to transform sensitive data
	 * returned from secure data access operations.
	 *
	 * Use this Annotation attribute if you are uncertain whether the application class is on the classpath or not.
	 *
	 * Default is unset.
	 *
	 * Use the {@literal spring.data.gemfire.security.postprocessor.class-name} property
	 * in {@literal application.properties}.
	 */
	String securityPostProcessorClassName() default "";

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

	/**
	 * Sets the Geode System Property referring to the location of an Apache Shiro INI file used to configure
	 * the Apache Shiro Security Framework to secure Apache Geode.
	 *
	 * Default is unset.
	 *
	 * Use the {@literal spring.data.gemfire.security.shiro.ini-resource-path} property
	 * in {@literal application.properties}.
	 */
	String shiroIniResourcePath() default "";

}
