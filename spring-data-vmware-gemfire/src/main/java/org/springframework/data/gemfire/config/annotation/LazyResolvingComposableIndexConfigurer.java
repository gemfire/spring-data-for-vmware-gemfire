/*
 * Copyright (c) VMware, Inc. 2022-2023. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.config.annotation;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.data.gemfire.IndexFactoryBean;
import org.springframework.data.gemfire.config.annotation.support.AbstractLazyResolvingComposableConfigurer;
import org.springframework.lang.Nullable;

/**
 * Composition for {@link IndexConfigurer}.
 *
 * @author John Blum
 * @see org.springframework.data.gemfire.IndexFactoryBean
 * @see org.springframework.data.gemfire.config.annotation.IndexConfigurer
 * @see org.springframework.data.gemfire.config.annotation.support.AbstractLazyResolvingComposableConfigurer
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
}
