// Copyright (c) VMware, Inc. 2022. All rights reserved.
// SPDX-License-Identifier: Apache-2.0

package org.springframework.data.gemfire.config.annotation;

import org.apache.geode.cache.lucene.LuceneIndex;
import org.apache.geode.cache.query.Index;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.gemfire.IndexFactoryBean;
import org.springframework.data.gemfire.config.annotation.support.Configurer;
import org.springframework.data.gemfire.search.lucene.LuceneIndexFactoryBean;

/**
 * The {@link IndexConfigurer} interface defines a contract for implementing {@link Object Objects} in order to
 * customize the configuration of Entity-defined {@link Index Indexes} when a user annotates her Spring application
 * {@link Configuration} {@link Class} with {@link EnableIndexing}.
 *
 * @author John Blum
 * @see LuceneIndex
 * @see Index
 * @see IndexFactoryBean
 * @see EnableIndexing
 * @see IndexConfiguration
 * @see Configurer
 * @see LuceneIndexFactoryBean
 * @since 2.0.0
 */
public interface IndexConfigurer extends Configurer<IndexFactoryBean> {

	/**
	 * Configuration callback method providing a reference to a {@link IndexFactoryBean} used to construct, configure
	 * and initialize an instance of a peer {@link Index}.
	 *
	 * @param beanName name of the {@link Index} bean declared in the Spring application context.
	 * @param bean reference to the {@link IndexFactoryBean}.
	 * @see IndexFactoryBean
	 */
	default void configure(String beanName, IndexFactoryBean bean) { }

	/**
	 * Configuration callback method providing a reference to a {@link LuceneIndexFactoryBean} used to construct,
	 * configure and initialize an instance of a peer {@link LuceneIndex}.
	 *
	 * @param beanName name of the {@link LuceneIndex} bean declared in the Spring application context.
	 * @param bean reference to the {@link LuceneIndexFactoryBean}.
	 * @see LuceneIndexFactoryBean
	 */
	default void configure(String beanName, LuceneIndexFactoryBean bean) { }

}
