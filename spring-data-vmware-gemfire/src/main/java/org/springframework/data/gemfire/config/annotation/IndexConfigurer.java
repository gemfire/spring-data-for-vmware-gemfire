/*
 * Copyright 2022-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.config.annotation;

import org.apache.geode.cache.query.Index;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.gemfire.IndexFactoryBean;
import org.springframework.data.gemfire.config.annotation.support.Configurer;

/**
 * The {@link IndexConfigurer} interface defines a contract for implementing {@link Object Objects} in order to
 * customize the configuration of Entity-defined {@link Index Indexes} when a user annotates her Spring application
 * {@link Configuration} {@link Class} with {@link EnableIndexing}.
 *
 * @author John Blum
 * @see org.apache.geode.cache.query.Index
 * @see org.springframework.data.gemfire.IndexFactoryBean
 * @see org.springframework.data.gemfire.config.annotation.EnableIndexing
 * @see org.springframework.data.gemfire.config.annotation.IndexConfiguration
 * @see org.springframework.data.gemfire.config.annotation.support.Configurer
 * @since 2.0.0
 */
public interface IndexConfigurer extends Configurer<IndexFactoryBean> {

	/**
	 * Configuration callback method providing a reference to a {@link IndexFactoryBean} used to construct, configure
	 * and initialize an instance of a peer {@link Index}.
	 *
	 * @param beanName name of the {@link Index} bean declared in the Spring application context.
	 * @param bean reference to the {@link IndexFactoryBean}.
	 * @see org.springframework.data.gemfire.IndexFactoryBean
	 */
	default void configure(String beanName, IndexFactoryBean bean) { }
}
