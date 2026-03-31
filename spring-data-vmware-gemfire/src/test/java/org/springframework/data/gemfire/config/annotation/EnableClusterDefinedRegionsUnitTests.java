/*
 * Copyright $originalComment.match(" (\d+)", 1, "-", $today.year)2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.config.annotation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import java.util.Collections;
import java.util.Map;
import org.springframework.data.gemfire.gud.api.GudClientRegionShortcut;
import org.junit.Test;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.data.gemfire.client.GemfireDataSourcePostProcessor;

/**
 * Unit tests for {@link EnableClusterDefinedRegions} and {@link ClusterDefinedRegionsConfiguration}.
 *
 * @author John Blum
 * @see org.junit.Test
 * @see org.apache.geode.cache.client.ClientCache
 * @see org.apache.geode.cache.client.GudClientRegionShortcut
 * @see org.springframework.core.type.AnnotationMetadata
 * @see org.springframework.data.gemfire.client.GemfireDataSourcePostProcessor
 * @see org.springframework.data.gemfire.config.annotation.ClusterDefinedRegionsConfiguration
 * @see org.springframework.data.gemfire.config.annotation.EnableClusterDefinedRegions
 * @since 2.1.0
 */
public class EnableClusterDefinedRegionsUnitTests {

	@Test
	public void configuresClientRegionShortcutUsingAnnotationMetadata() {

		Map<String, Object> enableClusterDefinedRegionsAttributes =
			Collections.singletonMap("clientRegionShortcut", GudClientRegionShortcut.LOCAL);

		AnnotationMetadata mockAnnotationMetadata = mock(AnnotationMetadata.class);

		when(mockAnnotationMetadata.getAnnotationAttributes(EnableClusterDefinedRegions.class.getName()))
			.thenReturn(enableClusterDefinedRegionsAttributes);

		ClusterDefinedRegionsConfiguration configuration = new ClusterDefinedRegionsConfiguration();

		configuration.setImportMetadata(mockAnnotationMetadata);

		assertThat(configuration.resolveClientRegionShortcut()).isEqualTo(GudClientRegionShortcut.LOCAL);
	}

	@Test
	public void setGetAndResolveClientRegionShortcut() {

		ClusterDefinedRegionsConfiguration configuration = new ClusterDefinedRegionsConfiguration();

		assertThat(configuration.getClientRegionShortcut().orElse(null)).isEqualTo(GudClientRegionShortcut.PROXY);
		assertThat(configuration.resolveClientRegionShortcut()).isEqualTo(GudClientRegionShortcut.PROXY);

		configuration.setClientRegionShortcut(GudClientRegionShortcut.CACHING_PROXY);

		assertThat(configuration.getClientRegionShortcut().orElse(null)).isEqualTo(GudClientRegionShortcut.CACHING_PROXY);
		assertThat(configuration.resolveClientRegionShortcut()).isEqualTo(GudClientRegionShortcut.CACHING_PROXY);

		configuration.setClientRegionShortcut(GudClientRegionShortcut.LOCAL);

		assertThat(configuration.getClientRegionShortcut().orElse(null)).isEqualTo(GudClientRegionShortcut.LOCAL);
		assertThat(configuration.resolveClientRegionShortcut()).isEqualTo(GudClientRegionShortcut.LOCAL);

		configuration.setClientRegionShortcut(null);

		assertThat(configuration.getClientRegionShortcut().orElse(null)).isNull();
		assertThat(configuration.resolveClientRegionShortcut()).isEqualTo(GudClientRegionShortcut.PROXY);
	}

	@Test
	public void configureGemfireDataSourcePostProcessor() {

		ConfigurableBeanFactory mockBeanFactory = mock(ConfigurableBeanFactory.class);

		ClusterDefinedRegionsConfiguration configuration = new ClusterDefinedRegionsConfiguration();

		configuration.setBeanFactory(mockBeanFactory);
		configuration.setClientRegionShortcut(GudClientRegionShortcut.CACHING_PROXY);

		GemfireDataSourcePostProcessor postProcessor = configuration.gemfireDataSourcePostProcessor();

		assertThat(postProcessor.getBeanFactory().orElse(null)).isEqualTo(mockBeanFactory);
		assertThat(postProcessor.getClientRegionShortcut().orElse(null))
			.isEqualTo(GudClientRegionShortcut.CACHING_PROXY);
	}
}
