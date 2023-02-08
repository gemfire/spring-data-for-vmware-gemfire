/*
 * Copyright (c) VMware, Inc. 2022-2023. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.config.annotation;

import java.lang.annotation.Annotation;
import java.util.Optional;

import org.apache.geode.cache.GemFireCache;
import org.apache.geode.cache.Region;
import org.apache.geode.cache.client.ClientCache;
import org.apache.geode.cache.client.ClientRegionShortcut;

import org.apache.shiro.util.Assert;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportAware;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.annotation.Order;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.data.gemfire.client.GemfireDataSourcePostProcessor;
import org.springframework.data.gemfire.config.annotation.support.AbstractAnnotationConfigSupport;
import org.springframework.data.gemfire.util.CacheUtils;
import org.springframework.util.ObjectUtils;

/**
 * The {@link ClusterDefinedRegionsConfiguration} class configures client Proxy-based {@link Region Regions}
 * for all {@link Region Regions} defined in the cluster to which the cache client is connected.
 *
 * @author John Blum
 * @see Region
 * @see ClientCache
 * @see ClientRegionShortcut
 * @see Bean
 * @see Configuration
 * @see ImportAware
 * @see AnnotationAttributes
 * @see AnnotationMetadata
 * @see GemfireDataSourcePostProcessor
 * @see AbstractAnnotationConfigSupport
 * @since 2.1.0
 */
@Configuration
public class ClusterDefinedRegionsConfiguration extends AbstractAnnotationConfigSupport implements ImportAware {

	protected static final ClientRegionShortcut DEFAULT_CLIENT_REGION_SHORTCUT = ClientRegionShortcut.PROXY;

	private ClientRegionShortcut clientRegionShortcut = DEFAULT_CLIENT_REGION_SHORTCUT;

	@Override
	protected Class<? extends Annotation> getAnnotationType() {
		return EnableClusterDefinedRegions.class;
	}

	@Override
	public void setImportMetadata(AnnotationMetadata importMetadata) {

		AnnotationAttributes enableClusterDefinedRegionsAttributes = getAnnotationAttributes(importMetadata);

		setClientRegionShortcut(enableClusterDefinedRegionsAttributes.getEnum("clientRegionShortcut"));
	}

	protected void setClientRegionShortcut(ClientRegionShortcut clientRegionShortcut) {
		this.clientRegionShortcut = clientRegionShortcut;
	}

	protected Optional<ClientRegionShortcut> getClientRegionShortcut() {
		return Optional.ofNullable(this.clientRegionShortcut);
	}

	protected ClientRegionShortcut resolveClientRegionShortcut() {
		return getClientRegionShortcut().orElse(DEFAULT_CLIENT_REGION_SHORTCUT);
	}

	@Bean
	@Order(Ordered.HIGHEST_PRECEDENCE + 1000000)
	public GemfireDataSourcePostProcessor gemfireDataSourcePostProcessor() {
		return new GemfireDataSourcePostProcessor().using(getBeanFactory()).using(resolveClientRegionShortcut());
	}

	@Bean
	Object nullCacheDependentBean(GemFireCache cache) {

		Assert.isTrue(CacheUtils.isClient(cache), String.format("GemFireCache [%s] must be a %s",
			ObjectUtils.nullSafeClassName(cache), ClientCache.class.getName()));

		return null;
	}
}
