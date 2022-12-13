/*
 * Copyright (c) VMware, Inc. 2022. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.config.annotation;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.data.gemfire.client.PoolFactoryBean;
import org.springframework.data.gemfire.config.annotation.support.AbstractLazyResolvingComposableConfigurer;
import org.springframework.lang.Nullable;

/**
 * Composition of {@link PoolConfigurer}.
 *
 * @author John Blum
 * @see PoolFactoryBean
 * @see PoolConfigurer
 * @see AbstractLazyResolvingComposableConfigurer
 * @since 2.2.0
 */
public class LazyResolvingComposablePoolConfigurer
		extends AbstractLazyResolvingComposableConfigurer<PoolFactoryBean, PoolConfigurer>
		implements PoolConfigurer {

	public static LazyResolvingComposablePoolConfigurer create() {
		return create(null);
	}

	public static LazyResolvingComposablePoolConfigurer create(@Nullable BeanFactory beanFactory) {
		return new LazyResolvingComposablePoolConfigurer().with(beanFactory);
	}

	@Override
	protected Class<PoolConfigurer> getConfigurerType() {
		return PoolConfigurer.class;
	}
}
