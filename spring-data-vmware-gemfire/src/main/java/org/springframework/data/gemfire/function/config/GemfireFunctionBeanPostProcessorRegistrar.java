/*
 * Copyright 2022-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.function.config;

import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionReaderUtils;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;

/**
 * Spring {@link ImportBeanDefinitionRegistrar} to register the {@link GemfireFunctionBeanPostProcessor}
 *
 * @author David Turanski
 */
public class GemfireFunctionBeanPostProcessorRegistrar implements ImportBeanDefinitionRegistrar {

	/**
	 * @inheritDoc
	 */
	@Override
	public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {

		BeanDefinitionBuilder builder =
			BeanDefinitionBuilder.genericBeanDefinition(GemfireFunctionBeanPostProcessor.class);

		BeanDefinitionReaderUtils.registerWithGeneratedName(builder.getBeanDefinition(), registry);
	}
}
