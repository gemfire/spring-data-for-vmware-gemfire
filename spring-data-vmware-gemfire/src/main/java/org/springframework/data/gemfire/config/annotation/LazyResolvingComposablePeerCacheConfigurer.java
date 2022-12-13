/*
 * Copyright (c) VMware, Inc. 2022. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.config.annotation;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.data.gemfire.CacheFactoryBean;
import org.springframework.data.gemfire.config.annotation.support.AbstractLazyResolvingComposableConfigurer;
import org.springframework.lang.Nullable;

/**
 * Composition of {@link PeerCacheConfigurer}.
 *
 * @author John Blum
 * @see CacheFactoryBean
 * @see PeerCacheConfigurer
 * @see AbstractLazyResolvingComposableConfigurer
 * @since 2.2.0
 */
public class LazyResolvingComposablePeerCacheConfigurer
		extends AbstractLazyResolvingComposableConfigurer<CacheFactoryBean, PeerCacheConfigurer>
		implements PeerCacheConfigurer {

	public static LazyResolvingComposablePeerCacheConfigurer create() {
		return create(null);
	}

	public static LazyResolvingComposablePeerCacheConfigurer create(@Nullable BeanFactory beanFactory) {
		return new LazyResolvingComposablePeerCacheConfigurer().with(beanFactory);
	}

	@Override
	protected Class<PeerCacheConfigurer> getConfigurerType() {
		return PeerCacheConfigurer.class;
	}
}
