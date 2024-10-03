/*
 * Copyright 2017-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.tests.integration;

import java.util.Map;
import java.util.function.Function;

import org.junit.After;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.data.gemfire.util.ArrayUtils;
import org.springframework.lang.NonNull;

/**
 * The {@link SpringApplicationContextIntegrationTestsSupport} class is an extension of {@link IntegrationTestsSupport}
 * for writing Integration Tests involving a Spring {@link ApplicationContext}.
 *
 * This class contains functionality common to all Integration Tests involving a Spring {@link ApplicationContext}
 * and can be extended to create, acquire a reference and close the {@link ApplicationContext}
 * on test class completion, properly.
 *
 * @author John Blum
 * @see ApplicationContext
 * @see ApplicationEventPublisher
 * @see ApplicationEventPublisherAware
 * @see ConfigurableApplicationContext
 * @see AnnotationConfigApplicationContext
 * @see IntegrationTestsSupport
 * @since 1.0.0
 */
@SuppressWarnings("unused")
public abstract class SpringApplicationContextIntegrationTestsSupport extends IntegrationTestsSupport
		implements ApplicationEventPublisherAware {

	@After
	public void closeApplicationContext() {
		closeApplicationContext(getApplicationContext());
	}

	protected ConfigurableApplicationContext newApplicationContext(Class<?>... annotatedClasses) {
		return newApplicationContext(this::processBeforeRefresh, annotatedClasses);
	}

	protected ConfigurableApplicationContext newApplicationContext(
			Function<ConfigurableApplicationContext, ConfigurableApplicationContext> applicationContextInitializer,
			Class<?>... annotatedClasses) {

		AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext();

		applicationContext.register(ArrayUtils.nullSafeArray(annotatedClasses, Class.class));
		applicationContext.registerShutdownHook();
		applicationContextInitializer.apply(applicationContext);
		applicationContext.refresh();

		return setApplicationContext(applicationContext);
	}

	protected @NonNull ConfigurableApplicationContext processBeforeRefresh(
			@NonNull ConfigurableApplicationContext applicationContext) {

		setApplicationEventPublisher(applicationContext);

		return applicationContext;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setApplicationEventPublisher(@NonNull ApplicationEventPublisher applicationEventPublisher) {

		if (applicationEventPublisher != null) {
			TestContextCacheLifecycleListenerAdapter.getInstance()
				.setApplicationEventPublisher(applicationEventPublisher);
		}
	}

	protected boolean containsBean(String beanName) {

		ConfigurableApplicationContext applicationContext = requireApplicationContext();

		return applicationContext.containsBean(beanName);
	}

	protected <T> T getBean(Class<T> requiredType) {

		ConfigurableApplicationContext applicationContext = requireApplicationContext();

		return applicationContext.getBean(requiredType);
	}

	protected <T> T getBean(String beanName, Class<T> requiredType) {

		ConfigurableApplicationContext applicationContext = requireApplicationContext();

		return applicationContext.getBean(beanName, requiredType);
	}

	protected <T> Map<String, T> getBeansOfType(Class<T> requiredType) {

		ConfigurableApplicationContext applicationContext = requireApplicationContext();

		return applicationContext.getBeansOfType(requiredType);
	}
}
