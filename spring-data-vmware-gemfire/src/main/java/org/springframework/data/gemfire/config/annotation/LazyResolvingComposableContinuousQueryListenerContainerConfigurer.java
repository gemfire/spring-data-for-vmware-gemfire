/*
 * Copyright 2022-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.config.annotation;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.data.gemfire.config.annotation.support.AbstractLazyResolvingComposableConfigurer;
import org.springframework.data.gemfire.listener.ContinuousQueryListenerContainer;
import org.springframework.lang.Nullable;

/**
 * Composition of {@link ContinuousQueryListenerContainerConfigurer}.
 *
 * @author John Blum
 * @see org.springframework.beans.factory.BeanFactory
 * @see org.springframework.data.gemfire.config.annotation.ContinuousQueryListenerContainerConfigurer
 * @see org.springframework.data.gemfire.config.annotation.support.AbstractLazyResolvingComposableConfigurer
 * @see org.springframework.data.gemfire.listener.ContinuousQueryListenerContainer
 * @since 2.2.0
 */
public class LazyResolvingComposableContinuousQueryListenerContainerConfigurer
		extends AbstractLazyResolvingComposableConfigurer<ContinuousQueryListenerContainer, ContinuousQueryListenerContainerConfigurer>
		implements ContinuousQueryListenerContainerConfigurer {

	public static LazyResolvingComposableContinuousQueryListenerContainerConfigurer create() {
		return create(null);
	}

	public static LazyResolvingComposableContinuousQueryListenerContainerConfigurer create(@Nullable BeanFactory beanFactory) {
		return new LazyResolvingComposableContinuousQueryListenerContainerConfigurer().with(beanFactory);
	}

	@Override
	protected Class<ContinuousQueryListenerContainerConfigurer> getConfigurerType() {
		return ContinuousQueryListenerContainerConfigurer.class;
	}
}
