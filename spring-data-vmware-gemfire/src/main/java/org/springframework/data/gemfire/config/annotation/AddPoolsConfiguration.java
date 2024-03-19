/*
 * Copyright 2022-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.config.annotation;

import java.util.Arrays;

import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.data.gemfire.util.ArrayUtils;

/**
 * The {@link AddPoolsConfiguration} class registers {@link org.springframework.data.gemfire.client.PoolFactoryBean}
 * bean definitions for all {@link EnablePool} annotation configuration meta-data defined in
 * the {@link EnablePools} annotation on a GemFire client cache application class.

 * @author John Blum
 * @see org.apache.geode.cache.client.Pool
 * @see AddPoolConfiguration
 * @see EnablePool
 * @see EnablePools
 * @since 1.9.0
 */
public class AddPoolsConfiguration extends AddPoolConfiguration {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {

		if (importingClassMetadata.hasAnnotation(EnablePools.class.getName())) {

			AnnotationAttributes enablePoolsAttributes =
				getAnnotationAttributes(importingClassMetadata, EnablePools.class.getName());

			AnnotationAttributes[] serversAttributes =
				enablePoolsAttributes.getAnnotationArray("pools");

			Arrays.stream(ArrayUtils.nullSafeArray(serversAttributes, AnnotationAttributes.class))
				.forEach(enablePoolAttributes -> registerPoolFactoryBeanDefinition(enablePoolAttributes, registry));
		}
	}
}
