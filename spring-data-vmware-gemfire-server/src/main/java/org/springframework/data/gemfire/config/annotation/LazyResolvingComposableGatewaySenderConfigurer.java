/*
 * Copyright (c) VMware, Inc. 2022-2024. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.config.annotation;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.data.gemfire.config.annotation.support.AbstractLazyResolvingComposableConfigurer;
import org.springframework.data.gemfire.wan.GatewaySenderFactoryBean;
import org.springframework.lang.Nullable;

/**
 * Composition of {@link GatewaySenderConfigurer}.
 *
 * @author Udo Kohlmeyer
 * @see GatewaySenderFactoryBean
 * @see GatewaySenderConfigurer
 * @see AbstractLazyResolvingComposableConfigurer
 * @since 2.2.0
 */
public class LazyResolvingComposableGatewaySenderConfigurer
		extends AbstractLazyResolvingComposableConfigurer<GatewaySenderFactoryBean, GatewaySenderConfigurer>
		implements GatewaySenderConfigurer {

	public static LazyResolvingComposableGatewaySenderConfigurer create() {
		return create(null);
	}

	public static LazyResolvingComposableGatewaySenderConfigurer create(@Nullable BeanFactory beanFactory) {
		return new LazyResolvingComposableGatewaySenderConfigurer().with(beanFactory);
	}

	@Override
	protected Class<GatewaySenderConfigurer> getConfigurerType() {
		return GatewaySenderConfigurer.class;
	}
}
