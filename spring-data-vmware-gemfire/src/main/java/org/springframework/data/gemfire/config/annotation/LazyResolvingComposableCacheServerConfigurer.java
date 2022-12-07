/*
 * Copyright (c) VMware, Inc. 2022. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.config.annotation;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.data.gemfire.config.annotation.support.AbstractLazyResolvingComposableConfigurer;
import org.springframework.data.gemfire.server.CacheServerFactoryBean;
import org.springframework.lang.Nullable;

/**
 * Composition for {@link CacheServerConfigurer}.
 *
 * @author John Blum
 * @see org.springframework.data.gemfire.config.annotation.CacheServerConfigurer
 * @see org.springframework.data.gemfire.config.annotation.support.AbstractLazyResolvingComposableConfigurer
 * @see org.springframework.data.gemfire.server.CacheServerFactoryBean
 * @since 2.2.0
 */
public class LazyResolvingComposableCacheServerConfigurer
		extends AbstractLazyResolvingComposableConfigurer<CacheServerFactoryBean, CacheServerConfigurer>
		implements CacheServerConfigurer {

	public static LazyResolvingComposableCacheServerConfigurer create() {
		return create(null);
	}

	public static LazyResolvingComposableCacheServerConfigurer create(@Nullable BeanFactory beanFactory) {
		return new LazyResolvingComposableCacheServerConfigurer().with(beanFactory);
	}

	@Override
	protected Class<CacheServerConfigurer> getConfigurerType() {
		return CacheServerConfigurer.class;
	}
}
