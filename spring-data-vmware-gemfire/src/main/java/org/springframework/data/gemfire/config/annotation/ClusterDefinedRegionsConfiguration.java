/*
 * Copyright $originalComment.match(" (\d+)", 1, "-", $today.year)2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

/*
 * @AI-Generated
 * Generated in whole or in part by Cursor
 * Description:
 * 2026-03-13: Migrated from org.apache.geode imports to GUD API types
 */

package org.springframework.data.gemfire.config.annotation;

import java.lang.annotation.Annotation;
import java.util.Optional;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportAware;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.annotation.Order;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.data.gemfire.client.GemfireDataSourcePostProcessor;
import org.springframework.data.gemfire.config.annotation.support.AbstractAnnotationConfigSupport;
import org.springframework.data.gemfire.gud.api.GudClientCache;
import org.springframework.data.gemfire.gud.api.GudClientRegionShortcut;
import org.springframework.data.gemfire.gud.api.GudRegion;

/**
 * The {@link ClusterDefinedRegionsConfiguration} class configures client Proxy-based {@link GudRegion Regions}
 * for all {@link GudRegion Regions} defined in the cluster to which the cache client is connected.
 *
 * @author John Blum
 * @see GudRegion
 * @see GudClientCache
 * @see GudClientRegionShortcut
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

	protected static final GudClientRegionShortcut DEFAULT_CLIENT_REGION_SHORTCUT = GudClientRegionShortcut.PROXY;

	private GudClientRegionShortcut clientRegionShortcut = DEFAULT_CLIENT_REGION_SHORTCUT;

	@Override
	protected Class<? extends Annotation> getAnnotationType() {
		return EnableClusterDefinedRegions.class;
	}

	@Override
	public void setImportMetadata(AnnotationMetadata importMetadata) {

		AnnotationAttributes enableClusterDefinedRegionsAttributes = getAnnotationAttributes(importMetadata);

		setClientRegionShortcut(enableClusterDefinedRegionsAttributes.getEnum("clientRegionShortcut"));
	}

	protected void setClientRegionShortcut(GudClientRegionShortcut clientRegionShortcut) {
		this.clientRegionShortcut = clientRegionShortcut;
	}

	protected Optional<GudClientRegionShortcut> getClientRegionShortcut() {
		return Optional.ofNullable(this.clientRegionShortcut);
	}

	protected GudClientRegionShortcut resolveClientRegionShortcut() {
		return getClientRegionShortcut().orElse(DEFAULT_CLIENT_REGION_SHORTCUT);
	}

	@Bean
	@Order(Ordered.HIGHEST_PRECEDENCE + 1_000_000)
	public GemfireDataSourcePostProcessor gemfireDataSourcePostProcessor() {
		return new GemfireDataSourcePostProcessor().using(getBeanFactory()).using(resolveClientRegionShortcut());
	}

	@Bean
	Object nullCacheDependentBean(GudClientCache cache) {
		return null;
	}
}
