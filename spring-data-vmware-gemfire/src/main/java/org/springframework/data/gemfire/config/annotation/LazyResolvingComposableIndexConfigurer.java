/*
 * Copyright (c) VMware, Inc. 2022. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package org.springframework.data.gemfire.config.annotation;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.data.gemfire.IndexFactoryBean;
import org.springframework.data.gemfire.config.annotation.support.AbstractLazyResolvingComposableConfigurer;
import org.springframework.data.gemfire.search.lucene.LuceneIndexFactoryBean;
import org.springframework.lang.Nullable;

/**
 * Composition for {@link IndexConfigurer}.
 *
 * @author John Blum
 * @see IndexFactoryBean
 * @see IndexConfigurer
 * @see AbstractLazyResolvingComposableConfigurer
 * @see LuceneIndexFactoryBean
 * @since 2.2.0
 */
public class LazyResolvingComposableIndexConfigurer
		extends AbstractLazyResolvingComposableConfigurer<IndexFactoryBean, IndexConfigurer>
		implements IndexConfigurer {

	public static LazyResolvingComposableIndexConfigurer create() {
		return create(null);
	}

	public static LazyResolvingComposableIndexConfigurer create(@Nullable BeanFactory beanFactory) {
		return new LazyResolvingComposableIndexConfigurer().with(beanFactory);
	}

	@Override
	protected Class<IndexConfigurer> getConfigurerType() {
		return IndexConfigurer.class;
	}

	@Override
	public void configure(String beanName, LuceneIndexFactoryBean luceneIndexFactoryBean) {

		resolveConfigurers().forEach(configurer ->
			configurer.configure(beanName, luceneIndexFactoryBean));
	}
}
