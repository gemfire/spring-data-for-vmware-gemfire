/*
 * Copyright (c) VMware, Inc. 2023-2024. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.tests.mock.context;

import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.data.gemfire.tests.mock.beans.factory.config.GemFireMockObjectsBeanPostProcessor;
import org.springframework.lang.NonNull;

/**
 * A Spring {@link ApplicationContextInitializer} implementation used to initialize the Spring
 * {@link ConfigurableApplicationContext} with GemFire/Geode {@link Object Mock Objects}.
 *
 * {@link Object Mock Objects} will be created for caches, {@literal Regions}, {@literal Indexes}, {@literal DiskStores}
 * OQL query objects, and so on.
 *
 * @author John Blum
 * @see org.springframework.beans.factory.config.BeanPostProcessor
 * @see org.springframework.beans.factory.config.ConfigurableListableBeanFactory
 * @see ApplicationContextInitializer
 * @see ConfigurableApplicationContext
 * @see GemFireMockObjectsBeanPostProcessor
 * @since 0.0.1
 */
public class GemFireMockObjectsApplicationContextInitializer
		implements ApplicationContextInitializer<ConfigurableApplicationContext> {

	@Override
	public void initialize(@NonNull ConfigurableApplicationContext applicationContext) {
		applicationContext.getBeanFactory().addBeanPostProcessor(GemFireMockObjectsBeanPostProcessor.newInstance());
	}
}
