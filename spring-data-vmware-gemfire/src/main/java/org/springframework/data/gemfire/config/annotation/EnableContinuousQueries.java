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
import java.util.concurrent.Executor;

import org.apache.geode.cache.client.Pool;
import org.apache.geode.cache.query.QueryService;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.gemfire.listener.ContinuousQueryListenerContainer;
import org.springframework.util.ErrorHandler;

/**
 * The {@link EnableContinuousQueries} annotation marks a Spring {@link Configuration @Configuration} annotated
 * application configuration class to enable Pivotal GemFire / Apache Geode Continuous Queries (CQ) feature.
 *
 * @author John Blum
 * @see java.lang.annotation.Documented
 * @see java.lang.annotation.Inherited
 * @see java.lang.annotation.Retention
 * @see java.lang.annotation.Target
 * @see java.util.concurrent.Executor
 * @see org.apache.geode.cache.client.Pool
 * @see org.apache.geode.cache.query.QueryService
 * @see org.springframework.context.annotation.Configuration
 * @see org.springframework.context.annotation.Import
 * @see org.springframework.data.gemfire.config.annotation.ContinuousQueryConfiguration
 * @see org.springframework.data.gemfire.listener.ContinuousQueryListenerContainer
 * @since @.0.0
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
@Import(ContinuousQueryConfiguration.class)
@SuppressWarnings("unused")
public @interface EnableContinuousQueries {

	/**
	 * Refers to the {@link String name} of the declared {@link ErrorHandler} bean that will handle errors
	 * thrown during CQ event processing by CQ listeners.
	 *
	 * Defaults to unset.
	 */
	String errorHandlerBeanName() default "";

	/**
	 * Defines the Spring container lifecycle phase in which the SDG {@link ContinuousQueryListenerContainer}
	 * will be started on auto-start.
	 *
	 * Defaults to {@literal 0}.
	 */
	int phase() default 0;

	/**
	 * Refers to the name of the {@link Pool} over which CQs are registered and CQ events are received.
	 *
	 * Defaults to unset.
	 */
	String poolName() default "";

	/**
	 * Refers to the name of the {@link QueryService} bean used to define CQs.
	 *
	 * Defaults to unset.
	 */
	String queryServiceBeanName() default "";

	/**
	 * Refers to the name of the {@link Executor} bean used to process CQ events asynchronously.
	 *
	 * Defaults to unset.
	 */
	String taskExecutorBeanName() default "";

}
