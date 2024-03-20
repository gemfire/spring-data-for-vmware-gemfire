/*
 * Copyright 2023-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.tests.extensions.spring.test.context;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.data.gemfire.tests.extensions.spring.context.annotation.DependencyOfBeanFactoryPostProcessor;
import org.springframework.data.gemfire.tests.integration.IntegrationTestsSupport.TestContextCacheLifecycleListenerAdapter;
import org.springframework.lang.NonNull;
import org.springframework.test.context.ContextCustomizer;
import org.springframework.test.context.MergedContextConfiguration;

/**
 * Spring {@link ContextCustomizer} implementation used to register the {@link ConfigurableApplicationContext}
 * with the {@link TestContextCacheLifecycleListenerAdapter} as an {@link ApplicationEventPublisher}.
 *
 * @author John Blum
 * @see ConfigurableApplicationContext
 * @see org.springframework.data.gemfire.tests.integration.IntegrationTestsSupport
 * @see ContextCustomizer
 * @since 0.0.23
 */
public class DependencyOfAnnotationContextCustomizer implements ContextCustomizer {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void customizeContext(@NonNull ConfigurableApplicationContext applicationContext,
			@NonNull MergedContextConfiguration mergedConfig) {

		applicationContext.addBeanFactoryPostProcessor(new DependencyOfBeanFactoryPostProcessor());

		TestContextCacheLifecycleListenerAdapter.getInstance().setApplicationEventPublisher(applicationContext);
	}
}
