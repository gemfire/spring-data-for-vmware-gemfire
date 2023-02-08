/*
 * Copyright (c) VMware, Inc. 2022-2023. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.config.annotation;

import java.util.Arrays;

import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.data.gemfire.util.ArrayUtils;

/**
 * The {@link AddCacheServersConfiguration} class registers {@link org.springframework.data.gemfire.server.CacheServerFactoryBean}
 * bean definitions for all {@link EnableCacheServer} annotation configuration meta-data defined in
 * the {@link EnableCacheServers} annotation on a GemFire peer cache application class.
 *
 * @author John Blum
 * @see AddCacheServerConfiguration
 * @see EnableCacheServer
 * @see EnableCacheServers
 * @since 1.9.0
 */
public class AddCacheServersConfiguration extends AddCacheServerConfiguration {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {

		if (importingClassMetadata.hasAnnotation(EnableCacheServers.class.getName())) {

			AnnotationAttributes enableCacheServersAttributes =
				getAnnotationAttributes(importingClassMetadata, EnableCacheServers.class.getName());

			AnnotationAttributes[] serversAttributes =
				enableCacheServersAttributes.getAnnotationArray("servers");

			Arrays.stream(ArrayUtils.nullSafeArray(serversAttributes, AnnotationAttributes.class))
				.forEach(enableCacheServerAttributes ->
					registerCacheServerFactoryBeanDefinition(enableCacheServerAttributes, registry));
		}
	}
}
