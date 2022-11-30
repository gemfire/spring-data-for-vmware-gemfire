// Copyright (c) VMware, Inc. 2022. All rights reserved.
// SPDX-License-Identifier: Apache-2.0

package org.springframework.data.gemfire.config.annotation;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.data.gemfire.PeerRegionFactoryBean;
import org.springframework.data.gemfire.client.ClientRegionFactoryBean;
import org.springframework.data.gemfire.config.annotation.support.AbstractLazyResolvingComposableConfigurer;
import org.springframework.lang.Nullable;

/**
 * Composition for {@link RegionConfigurer}.
 *
 * @author John Blum
 * @see BeanFactory
 * @see org.springframework.data.gemfire.ConfigurableRegionFactoryBean
 * @see PeerRegionFactoryBean
 * @see ClientRegionFactoryBean
 * @see RegionConfigurer
 * @see AbstractLazyResolvingComposableConfigurer
 * @since 2.2.0
 */
public class LazyResolvingComposableRegionConfigurer
		extends AbstractLazyResolvingComposableConfigurer<ClientRegionFactoryBean<?, ?>, RegionConfigurer>
		implements RegionConfigurer {

	public static LazyResolvingComposableRegionConfigurer create() {
		return create(null);
	}

	public static LazyResolvingComposableRegionConfigurer create(@Nullable BeanFactory beanFactory) {
		return new LazyResolvingComposableRegionConfigurer().with(beanFactory);
	}

	@Override
	protected Class<RegionConfigurer> getConfigurerType() {
		return RegionConfigurer.class;
	}

	@Override
	public void configure(String beanName, PeerRegionFactoryBean<?, ?> peerRegionFactoryBean) {
		resolveConfigurers().forEach(configurer -> configurer.configure(beanName, peerRegionFactoryBean));
	}
}
