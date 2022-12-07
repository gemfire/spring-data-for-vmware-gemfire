/*
 * Copyright (c) VMware, Inc. 2022. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.repository.config;

import java.lang.annotation.Annotation;

import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.config.RepositoryBeanDefinitionRegistrarSupport;
import org.springframework.data.repository.config.RepositoryConfigurationExtension;
import org.springframework.lang.NonNull;

/**
 * {@link ImportBeanDefinitionRegistrar} to configure and setup Apache Geode {@link Repository Repositories}
 * via {@link EnableGemfireRepositories}.
 *
 * @author Oliver Gierke
 * @author John Blum
 * @see org.springframework.context.annotation.ImportBeanDefinitionRegistrar
 * @see org.springframework.data.repository.Repository
 * @see org.springframework.data.repository.config.RepositoryBeanDefinitionRegistrarSupport
 * @see org.springframework.data.repository.config.RepositoryConfigurationExtension
 */
public class GemfireRepositoriesRegistrar extends RepositoryBeanDefinitionRegistrarSupport {

	/**
	 * Identifies the {@link Annotation} enabling Apache Geode {@link Repository Repositories}.
	 *
	 * Defaults to {@link EnableGemfireRepositories}.
	 *
	 * @return the {@link Annotation} {@link Class} enabling Apache Geode {@link Repository Repositories}.
	 * @see java.lang.annotation.Annotation
	 * @see java.lang.Class
	 */
	@Override
	protected @NonNull Class<? extends Annotation> getAnnotation() {
		return EnableGemfireRepositories.class;
	}

	/**
	 * Returns the {@link RepositoryConfigurationExtension} implementing class to configure Apache Geode
	 * {@link Repository Repositories}.
	 *
	 * @return the {@link RepositoryConfigurationExtension} implementing class to configure Apache Geode
	 * {@link Repository Repositories}.
	 * @see org.springframework.data.repository.config.RepositoryConfigurationExtension
	 */
	@Override
	protected @NonNull RepositoryConfigurationExtension getExtension() {
		return new GemfireRepositoryConfigurationExtension();
	}
}
