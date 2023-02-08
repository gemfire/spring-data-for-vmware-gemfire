/*
 * Copyright (c) VMware, Inc. 2022-2023. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.config.annotation;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.data.gemfire.LocatorFactoryBean;
import org.springframework.data.gemfire.config.annotation.support.AbstractLazyResolvingComposableConfigurer;
import org.springframework.lang.Nullable;

/**
 * Composition for {@link LocatorConfigurer}.
 *
 * @author John Blum
 * @see org.springframework.data.gemfire.LocatorFactoryBean
 * @see org.springframework.data.gemfire.config.annotation.LocatorConfigurer
 * @see org.springframework.data.gemfire.config.annotation.support.AbstractLazyResolvingComposableConfigurer
 * @since 2.2.0
 */
public class LazyResolvingComposableLocatorConfigurer
		extends AbstractLazyResolvingComposableConfigurer<LocatorFactoryBean, LocatorConfigurer>
		implements LocatorConfigurer {

	public static LazyResolvingComposableLocatorConfigurer create() {
		return create(null);
	}

	public static LazyResolvingComposableLocatorConfigurer create(@Nullable BeanFactory beanFactory) {
		return new LazyResolvingComposableLocatorConfigurer().with(beanFactory);
	}

	@Override
	protected Class<LocatorConfigurer> getConfigurerType() {
		return LocatorConfigurer.class;
	}
}
