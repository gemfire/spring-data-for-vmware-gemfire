/*
 * Copyright (c) VMware, Inc. 2022-2023. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.config.annotation;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.data.gemfire.client.ClientCacheFactoryBean;
import org.springframework.data.gemfire.config.annotation.support.AbstractLazyResolvingComposableConfigurer;
import org.springframework.lang.Nullable;

/**
 * Composition for {@link ClientCacheConfigurer}.
 *
 * @author John Blum
 * @see org.springframework.data.gemfire.client.ClientCacheFactoryBean
 * @see org.springframework.data.gemfire.config.annotation.ClientCacheConfigurer
 * @see org.springframework.data.gemfire.config.annotation.support.AbstractLazyResolvingComposableConfigurer
 * @since 2.2.0
 */
public class LazyResolvingComposableClientCacheConfigurer
		extends AbstractLazyResolvingComposableConfigurer<ClientCacheFactoryBean, ClientCacheConfigurer>
		implements ClientCacheConfigurer {

	public static LazyResolvingComposableClientCacheConfigurer create() {
		return create(null);
	}

	public static LazyResolvingComposableClientCacheConfigurer create(@Nullable BeanFactory beanFactory) {
		return new LazyResolvingComposableClientCacheConfigurer().with(beanFactory);
	}

	@Override
	protected Class<ClientCacheConfigurer> getConfigurerType() {
		return ClientCacheConfigurer.class;
	}
}
