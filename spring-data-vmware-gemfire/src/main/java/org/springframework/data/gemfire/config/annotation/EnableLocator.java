/*
 * Copyright (c) VMware, Inc. 2022-2023. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package org.springframework.data.gemfire.config.annotation;

import java.lang.annotation.Annotation;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.apache.geode.distributed.Locator;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * The {@link EnableLocator} annotation configures a Spring {@link Configuration @Configuration} annotated {@link Class}
 * to start an embedded Pivotal GemFire/Apache Geode {@link Locator} service in this cluster member.
 *
 * However, the embedded Pivotal GemFire/Apache Geode Locator service can be enabled/disabled externally
 * in {@literal application.properties} with the {@literal spring.data.gemfire.service.http.enabled} property
 * even when this {@link Annotation} is present, thereby serving as a toggle.
 *
 * @author John Blum
 * @see Annotation
 * @see Import
 * @see LocatorConfiguration
 * @since 1.9.0
 * @deprecated to be removed in 2.0 release
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
@Import(LocatorConfiguration.class)
@UsesGemFireProperties
@Deprecated(forRemoval = true)
@SuppressWarnings("unused")
public @interface EnableLocator {

	/**
	 * Configures the host/IP address on which the embedded {@link Locator} service will bind to
	 * for accepting connections from clients sending {@link Locator} requests.
	 *
	 * Defaults to {@literal localhost}.
	 *
	 * Use the {@literal spring.data.gemfire.locator.host} property
	 * in Spring Boot {@literal application.properties}.
	 */
	String host() default LocatorConfiguration.DEFAULT_HOST;

	/**
	 * Configures the port on which the embedded {@link Locator} service will bind to
	 * listening for client connections sending {@link Locator} requests.
	 *
	 * Defaults to {@literal 10334}.
	 *
	 * Use the {@literal spring.data.gemfire.locator.port} property
	 * in Spring Boot {@literal application.properties}.
	 */
	int port() default LocatorConfiguration.DEFAULT_LOCATOR_PORT;

}
