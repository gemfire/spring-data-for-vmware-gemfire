/*
 * Copyright 2022-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.config.annotation;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.data.gemfire.config.annotation.support.AbstractLazyResolvingComposableConfigurer;
import org.springframework.data.gemfire.wan.GatewayReceiverFactoryBean;
import org.springframework.lang.Nullable;

/**
 * Composition of {@link GatewayReceiverConfigurer}.
 *
 * @author Udo Kohlmeyer
 * @see GatewayReceiverConfigurer
 * @see AbstractLazyResolvingComposableConfigurer
 * @see GatewayReceiverFactoryBean
 * @since 2.2.0
 */
public class LazyResolvingComposableGatewayReceiverConfigurer
		extends AbstractLazyResolvingComposableConfigurer<GatewayReceiverFactoryBean, GatewayReceiverConfigurer>
		implements GatewayReceiverConfigurer {

	public static LazyResolvingComposableGatewayReceiverConfigurer create() {
		return create(null);
	}

	public static LazyResolvingComposableGatewayReceiverConfigurer create(@Nullable BeanFactory beanFactory) {
		return new LazyResolvingComposableGatewayReceiverConfigurer().with(beanFactory);
	}

	@Override
	protected Class<GatewayReceiverConfigurer> getConfigurerType() {
		return GatewayReceiverConfigurer.class;
	}
}
