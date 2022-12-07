/*
 * Copyright (c) VMware, Inc. 2022. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.config.annotation;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.data.gemfire.DiskStoreFactoryBean;
import org.springframework.data.gemfire.config.annotation.support.AbstractLazyResolvingComposableConfigurer;
import org.springframework.lang.Nullable;

/**
 * Composition of {@link DiskStoreConfigurer}.
 *
 * @author John Blum
 * @see org.springframework.data.gemfire.DiskStoreFactoryBean
 * @see org.springframework.data.gemfire.config.annotation.DiskStoreConfigurer
 * @see org.springframework.data.gemfire.config.annotation.support.AbstractLazyResolvingComposableConfigurer
 * @since 2.2.0
 */
public class LazyResolvingComposableDiskStoreConfigurer
		extends AbstractLazyResolvingComposableConfigurer<DiskStoreFactoryBean, DiskStoreConfigurer>
		implements DiskStoreConfigurer {

	public static LazyResolvingComposableDiskStoreConfigurer create() {
		return create(null);
	}

	public static LazyResolvingComposableDiskStoreConfigurer create(@Nullable BeanFactory beanFactory) {
		return new LazyResolvingComposableDiskStoreConfigurer().with(beanFactory);
	}

	@Override
	protected Class<DiskStoreConfigurer> getConfigurerType() {
		return DiskStoreConfigurer.class;
	}
}
