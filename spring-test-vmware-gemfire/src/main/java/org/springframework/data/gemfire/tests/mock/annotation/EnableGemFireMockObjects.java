/*
 * Copyright 2017-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.tests.mock.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.apache.geode.cache.client.ClientCache;

import org.springframework.context.ApplicationEvent;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.event.AfterTestClassEvent;

/**
 * The {@link EnableGemFireMockObjects} annotation enables mocking of GemFire Objects in Unit Tests.
 *
 * @author John Blum
 * @see Documented
 * @see Inherited
 * @see Retention
 * @see Target
 * @see ClientCache
 * @see ApplicationEvent
 * @see Import
 * @since 0.0.1
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
@Import(GemFireMockObjectsConfiguration.class)
@SuppressWarnings("unused")
public @interface EnableGemFireMockObjects {

	/**
	 * Configures the {@link Class type} of {@link ApplicationEvent ApplicationEvents} that will trigger all currently
	 * allocated GemFire/Geode {@link Object Mock Objects} to be destroyed.
	 *
	 * @return an array of {@link ApplicationEvent} {@link Class types} that will trigger all currently allocated
	 * GemFire/Geode {@link Object Mock Objects} to be destroyed.
	 * @see ApplicationEvent
	 * @see Class
	 */
	Class<? extends ApplicationEvent>[] destroyOnEvents() default { AfterTestClassEvent.class };

	/**
	 * Configures whether the mock {@link ClientCache} created for Unit Testing is a Singleton.
	 *
	 * Defaults to {@literal false}.
	 *
	 * @return a boolean value indicating whether the mock {@link ClientCache} created for Unit Testing
	 * is a Singleton.
	 */
	boolean useSingletonCache() default GemFireMockObjectsConfiguration.DEFAULT_USE_SINGLETON_CACHE;

}
