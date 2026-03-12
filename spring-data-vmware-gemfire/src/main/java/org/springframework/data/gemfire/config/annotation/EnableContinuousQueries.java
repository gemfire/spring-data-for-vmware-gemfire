/*
 * Copyright $originalComment.match(" (\d+)", 1, "-", $today.year)2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

/*
 * @AI-Generated
 * Generated in whole or in part by Cursor
 * Description:
 * 2026-03-12: Migrated from org.apache.geode imports to GUD API types
 */

package org.springframework.data.gemfire.config.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.Executor;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.gemfire.gud.api.GudPool;
import org.springframework.data.gemfire.gud.api.GudQueryService;
import org.springframework.data.gemfire.listener.ContinuousQueryListenerContainer;
import org.springframework.util.ErrorHandler;

/**
 * The {@link EnableContinuousQueries} annotation marks a Spring {@link Configuration @Configuration} annotated
 * application configuration class to enable Pivotal GemFire / Apache Geode Continuous Queries (CQ) feature.
 *
 * @author John Blum
 * @see Documented
 * @see Inherited
 * @see Retention
 * @see Target
 * @see Executor
 * @see GudPool
 * @see GudQueryService
 * @see Configuration
 * @see Import
 * @see ContinuousQueryConfiguration
 * @see ContinuousQueryListenerContainer
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
	 * Refers to the name of the {@link GudPool} over which CQs are registered and CQ events are received.
	 *
	 * Defaults to unset.
	 */
	String poolName() default "";

	/**
	 * Refers to the name of the {@link GudQueryService} bean used to define CQs.
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
