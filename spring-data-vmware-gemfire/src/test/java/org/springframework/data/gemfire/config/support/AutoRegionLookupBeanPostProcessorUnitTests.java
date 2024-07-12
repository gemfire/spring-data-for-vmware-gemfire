/*
 * Copyright 2022-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.config.support;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import org.apache.geode.cache.Region;
import org.apache.geode.cache.client.ClientCache;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.data.gemfire.util.CollectionUtils;

/**
 * Unit Tests for {@link AutoRegionLookupBeanPostProcessor}.
 *
 * @author John Blum
 * @see org.junit.Test
 * @see org.mockito.Mock
 * @see org.mockito.Mockito
 * @see org.mockito.junit.MockitoJUnitRunner
 * @see org.springframework.data.gemfire.config.support.AutoRegionLookupBeanPostProcessor
 * @since 1.9.0
 */
@RunWith(MockitoJUnitRunner.class)
public class AutoRegionLookupBeanPostProcessorUnitTests {

	private AutoRegionLookupBeanPostProcessor autoRegionLookupBeanPostProcessor;

	@Mock
	private ConfigurableListableBeanFactory mockBeanFactory;

	@Before
	public void setup() {
		autoRegionLookupBeanPostProcessor = new AutoRegionLookupBeanPostProcessor();
	}

	protected Region<?, ?> mockRegion(String regionFullPath) {

		Region<?, ?> mockRegion = mock(Region.class);

		when(mockRegion.getFullPath()).thenReturn(regionFullPath);
		when(mockRegion.getName()).thenReturn(toRegionName(regionFullPath));

		return mockRegion;
	}

	protected String toRegionName(String regionFullPath) {
		int index = regionFullPath.lastIndexOf(Region.SEPARATOR);
		return (index > -1 ? regionFullPath.substring(index + 1) : regionFullPath);
	}

	@Test
	public void setAndGetBeanFactory() {

		autoRegionLookupBeanPostProcessor.setBeanFactory(mockBeanFactory);

		assertThat(autoRegionLookupBeanPostProcessor.getBeanFactory()).isSameAs(mockBeanFactory);
	}

	@Test(expected = IllegalArgumentException.class)
	public void setBeanFactoryToIncompatibleBeanFactoryType() {

		BeanFactory mockBeanFactory = mock(BeanFactory.class);

		try {
			autoRegionLookupBeanPostProcessor.setBeanFactory(mockBeanFactory);
		}
		catch (IllegalArgumentException expected) {

			assertThat(expected).hasMessageStartingWith("BeanFactory [%1$s] must be an instance of %2$s",
				mockBeanFactory.getClass().getName(), ConfigurableListableBeanFactory.class.getSimpleName());

			assertThat(expected).hasNoCause();

			throw expected;
		}
	}

	@Test(expected = IllegalArgumentException.class)
	public void setBeanFactoryToNull() {

		try {
			autoRegionLookupBeanPostProcessor.setBeanFactory(null);
		}
		catch (IllegalArgumentException expected) {

			assertThat(expected).hasMessageStartingWith("BeanFactory [null] must be an instance of %s",
				ConfigurableListableBeanFactory.class.getSimpleName());

			assertThat(expected).hasNoCause();

			throw expected;
		}
	}

	@Test(expected = IllegalStateException.class)
	public void getBeanFactoryUninitialized() {

		try {
			autoRegionLookupBeanPostProcessor.getBeanFactory();
		}
		catch (IllegalStateException expected) {

			assertThat(expected).hasMessage("BeanFactory was not properly configured");
			assertThat(expected).hasNoCause();

			throw expected;
		}
	}

	@Test
	public void postProcessBeforeInitializationReturnsBean() {

		Object bean = new Object();

		assertThat(autoRegionLookupBeanPostProcessor.postProcessBeforeInitialization(bean, "test")).isSameAs(bean);
	}

	@Test
	public void postProcessAfterInitializationWithNonGemFireCacheBean() {

		Object bean = new Object();

		AutoRegionLookupBeanPostProcessor autoRegionLookupBeanPostProcessorSpy =
			spy(this.autoRegionLookupBeanPostProcessor);

		assertThat(autoRegionLookupBeanPostProcessorSpy.postProcessAfterInitialization(bean, "test")).isSameAs(bean);

		verify(autoRegionLookupBeanPostProcessorSpy, never()).registerCacheRegionsAsBeans(any(ClientCache.class));
	}

	@Test
	public void registerCacheRegionsAsBeansIsSuccessful() {

		Set<Region<?, ?>> expected = CollectionUtils.asSet(mockRegion("one"),
			mockRegion("two"), mockRegion("three"));

		Set<Region<?, ?>> actual = new HashSet<>(expected.size());

		AutoRegionLookupBeanPostProcessor autoRegionLookupBeanPostProcessor = new AutoRegionLookupBeanPostProcessor() {
			@Override void registerCacheRegionAsBean(Region<?, ?> region) {
				actual.add(region);
			}
		};

		ClientCache mockClientCache = mock(ClientCache.class);

		when(mockClientCache.rootRegions()).thenReturn(expected);

		autoRegionLookupBeanPostProcessor.registerCacheRegionsAsBeans(mockClientCache);

		assertThat(actual).isEqualTo(expected);

		verify(mockClientCache, times(1)).rootRegions();

		for (Region<?, ?> region : expected) {
			verifyNoInteractions(region);
		}
	}

	@Test
	public void registerCacheRegionAsBeanIsSuccessful() {

		Region<?, ?> mockRegion = mockRegion("Example");

		when(mockRegion.subregions(anyBoolean())).thenReturn(Collections.emptySet());
		when(mockBeanFactory.containsBean(anyString())).thenReturn(false);

		autoRegionLookupBeanPostProcessor.setBeanFactory(mockBeanFactory);
		autoRegionLookupBeanPostProcessor.registerCacheRegionAsBean(mockRegion);

		verify(mockBeanFactory, times(1)).containsBean(eq("Example"));
		verify(mockBeanFactory, times(1)).registerSingleton(eq("Example"), eq(mockRegion));
		verify(mockRegion, times(1)).getFullPath();
		verify(mockRegion, times(1)).getName();
		verify(mockRegion, times(1)).subregions(eq(false));
	}

	@Test
	public void registerCacheRegionAsBeanRegistersSubRegionIgnoresRootRegion() {

		Region<?, ?> mockRootRegion = mockRegion("Root");
		Region<?, ?> mockSubRegion = mockRegion("/Root/Sub");

		when(mockRootRegion.subregions(anyBoolean())).thenReturn(CollectionUtils.asSet(mockSubRegion));
		when(mockSubRegion.subregions(anyBoolean())).thenReturn(Collections.emptySet());
		when(mockBeanFactory.containsBean(eq("Root"))).thenReturn(true);
		when(mockBeanFactory.containsBean(eq("/Root/Sub"))).thenReturn(false);

		autoRegionLookupBeanPostProcessor.setBeanFactory(mockBeanFactory);
		autoRegionLookupBeanPostProcessor.registerCacheRegionAsBean(mockRootRegion);

		verify(mockBeanFactory, times(1)).containsBean(eq("Root"));
		verify(mockBeanFactory, times(1)).containsBean(eq("/Root/Sub"));
		verify(mockBeanFactory, never()).registerSingleton(eq("Root"), eq(mockRootRegion));
		verify(mockBeanFactory, times(1)).registerSingleton(eq("/Root/Sub"), eq(mockSubRegion));
		verify(mockRootRegion, times(1)).getFullPath();
		verify(mockRootRegion, times(1)).getName();
		verify(mockRootRegion, times(1)).subregions(eq(false));
		verify(mockSubRegion, times(1)).getFullPath();
		verify(mockSubRegion, never()).getName();
		verify(mockSubRegion, times(1)).subregions(eq(false));
	}

	@Test
	public void registerNullCacheRegionAsBeanDoesNothing() {

		autoRegionLookupBeanPostProcessor.setBeanFactory(mockBeanFactory);
		autoRegionLookupBeanPostProcessor.registerCacheRegionAsBean(null);

		verifyNoInteractions(mockBeanFactory);
	}

	@Test
	public void getBeanNameReturnsRegionFullPath() {

		Region<?, ?> mockRegion = mockRegion("/Parent/Child");

		assertThat(autoRegionLookupBeanPostProcessor.getBeanName(mockRegion)).isEqualTo("/Parent/Child");

		verify(mockRegion, times(1)).getFullPath();
		verify(mockRegion, never()).getName();
	}

	@Test
	public void getBeanNameReturnsRegionName() {

		Region<?, ?> mockRegion = mockRegion("/Example");

		assertThat(autoRegionLookupBeanPostProcessor.getBeanName(mockRegion)).isEqualTo("Example");

		verify(mockRegion, times(1)).getFullPath();
		verify(mockRegion, times(1)).getName();
	}

	@Test
	public void nullSafeSubRegionsWhenSubRegionsIsNotNull() {

		Set<Region<?, ?>> mockSubRegions =
			CollectionUtils.asSet(mockRegion("one"), mockRegion("two"));

		Region<?, ?> mockRegion = mockRegion("parent");

		when(mockRegion.subregions(anyBoolean())).thenReturn(mockSubRegions);

		assertThat(autoRegionLookupBeanPostProcessor.nullSafeSubregions(mockRegion)).isEqualTo(mockSubRegions);

		verify(mockRegion, times(1)).subregions(eq(false));
	}

	@Test
	public void nullSafeSubRegionsWhenSubRegionsIsNull() {

		Region<?, ?> mockRegion = mockRegion("parent");

		when(mockRegion.subregions(anyBoolean())).thenReturn(null);

		assertThat(autoRegionLookupBeanPostProcessor.nullSafeSubregions(mockRegion)).isEqualTo(Collections.emptySet());

		verify(mockRegion, times(1)).subregions(eq(false));
	}
}
