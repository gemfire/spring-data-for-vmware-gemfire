/*
 * Copyright (c) VMware, Inc. 2022. All rights reserved.
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
 * @see BeanFactory
 * @see ContinuousQueryListenerContainerConfigurer
 * @see AbstractLazyResolvingComposableConfigurer
 * @see ContinuousQueryListenerContainer
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
