/*
 * Copyright 2022-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.apache.geode.cache.AttributesMutator;
import org.apache.geode.cache.CacheListener;
import org.apache.geode.cache.CacheLoader;
import org.apache.geode.cache.CacheWriter;
import org.apache.geode.cache.CustomExpiry;
import org.apache.geode.cache.EvictionAttributesMutator;
import org.apache.geode.cache.ExpirationAttributes;
import org.apache.geode.cache.Region;
import org.apache.geode.cache.RegionAttributes;
import org.apache.geode.cache.client.ClientCache;
import org.junit.Test;

/**
 * Unit Tests for {@link LookupRegionFactoryBean}.
 *
 * @author John Blum
 * @see org.junit.Test
 * @see org.mockito.Mockito
 * @see org.apache.geode.cache.AttributesMutator
 * @see org.apache.geode.cache.EvictionAttributesMutator
 * @see org.apache.geode.cache.Region
 * @see org.springframework.data.gemfire.LookupRegionFactoryBean
 * @since 1.7.0
 */
@SuppressWarnings("rawtypes")
public class LookupRegionFactoryBeanUnitTests {

	@Test
	@SuppressWarnings("unchecked")
	public void testAfterPropertiesSet() throws Exception {

		ClientCache mockCache = mock(ClientCache.class, "testAfterPropertiesSet.MockCache");

		Region<Object, Object> mockRegion = mock(Region.class, "testAfterPropertiesSet.MockRegion");

		RegionAttributes<Object, Object> mockRegionAttributes = mock(RegionAttributes.class,
			"testAfterPropertiesSet.MockRegionAttributes");

		EvictionAttributesMutator mockEvictionAttributesMutator = mock(EvictionAttributesMutator.class,
			"testAfterPropertiesSet.EvictionAttributesMutator");

		AttributesMutator<Object, Object> mockAttributesMutator = mock(AttributesMutator.class,
			"testAfterPropertiesSet.MockAttributesMutator");

		when(mockCache.getRegion(eq("Example"))).thenReturn(mockRegion);
		when(mockRegion.getFullPath()).thenReturn("/Example");
		when(mockRegion.getName()).thenReturn("Example");
		when(mockRegion.getAttributes()).thenReturn(mockRegionAttributes);
		when(mockRegionAttributes.getStatisticsEnabled()).thenReturn(true);
		when(mockRegion.getAttributesMutator()).thenReturn(mockAttributesMutator);
		when(mockAttributesMutator.getEvictionAttributesMutator()).thenReturn(mockEvictionAttributesMutator);

		CacheListener mockCacheListenerZero = mock(CacheListener.class, "testAfterPropertiesSet.MockCacheListener.0");
		CacheListener mockCacheListenerOne = mock(CacheListener.class, "testAfterPropertiesSet.MockCacheListener.1");
		CacheListener mockCacheListenerTwo = mock(CacheListener.class, "testAfterPropertiesSet.MockCacheListener.2");

		CacheLoader mockCacheLoader = mock(CacheLoader.class, "testAfterPropertiesSet.MockCacheLoader");

		CacheWriter mockCacheWriter = mock(CacheWriter.class, "testAfterPropertiesSet.MockCacheWriter");

		CustomExpiry mockCustomExpiryTti = mock(CustomExpiry.class, "testAfterPropertiesSet.MockCustomExpiry.TTI");
		CustomExpiry mockCustomExpiryTtl = mock(CustomExpiry.class, "testAfterPropertiesSet.MockCustomExpiry.TTL");

		ExpirationAttributes mockExpirationAttributesEntryTti = mock(ExpirationAttributes.class,
			"testAfterPropertiesSet.MockExpirationAttributes.Entry.TTI");
		ExpirationAttributes mockExpirationAttributesEntryTtl = mock(ExpirationAttributes.class,
			"testAfterPropertiesSet.MockExpirationAttributes.Entry.TTL");
		ExpirationAttributes mockExpirationAttributesRegionTti = mock(ExpirationAttributes.class,
			"testAfterPropertiesSet.MockExpirationAttributes.Region.TTI");
		ExpirationAttributes mockExpirationAttributesRegionTtl = mock(ExpirationAttributes.class,
			"testAfterPropertiesSet.MockExpirationAttributes.Region.TTL");

		LookupRegionFactoryBean factoryBean = new LookupRegionFactoryBean();

		factoryBean.setBeanName("Example");
		factoryBean.setCache(mockCache);
		factoryBean.setCacheLoader(mockCacheLoader);
		factoryBean.setCacheWriter(mockCacheWriter);
		factoryBean.setCloningEnabled(true);
		factoryBean.setCustomEntryIdleTimeout(mockCustomExpiryTti);
		factoryBean.setCustomEntryTimeToLive(mockCustomExpiryTtl);
		factoryBean.setEntryIdleTimeout(mockExpirationAttributesEntryTti);
		factoryBean.setEntryTimeToLive(mockExpirationAttributesEntryTtl);
		factoryBean.setEvictionMaximum(1000);
		factoryBean.setRegionIdleTimeout(mockExpirationAttributesRegionTti);
		factoryBean.setRegionTimeToLive(mockExpirationAttributesRegionTtl);
		factoryBean.setStatisticsEnabled(true);

		factoryBean.setCacheListeners(new CacheListener[] {
			mockCacheListenerZero, mockCacheListenerOne, mockCacheListenerTwo
		});

		factoryBean.afterPropertiesSet();

		verify(mockAttributesMutator, times(1)).addCacheListener(same(mockCacheListenerZero));
		verify(mockAttributesMutator, times(1)).addCacheListener(same(mockCacheListenerOne));
		verify(mockAttributesMutator, times(1)).addCacheListener(same(mockCacheListenerTwo));
		verify(mockAttributesMutator, times(1)).setCacheLoader(same(mockCacheLoader));
		verify(mockAttributesMutator, times(1)).setCacheWriter(same(mockCacheWriter));
		verify(mockAttributesMutator, times(1)).setCloningEnabled(eq(true));
		verify(mockAttributesMutator, times(1)).setCustomEntryIdleTimeout(same(mockCustomExpiryTti));
		verify(mockAttributesMutator, times(1)).setCustomEntryTimeToLive(same(mockCustomExpiryTtl));
		verify(mockAttributesMutator, times(1)).setEntryIdleTimeout(same(mockExpirationAttributesEntryTti));
		verify(mockAttributesMutator, times(1)).setEntryTimeToLive(same(mockExpirationAttributesEntryTtl));
		verify(mockEvictionAttributesMutator, times(1)).setMaximum(eq(1000));
		verify(mockAttributesMutator, times(1)).setRegionIdleTimeout(same(mockExpirationAttributesRegionTti));
		verify(mockAttributesMutator, times(1)).setRegionTimeToLive(same(mockExpirationAttributesRegionTtl));
	}

	@Test(expected = IllegalStateException.class)
	@SuppressWarnings("unchecked")
	public void testAfterPropertiesSetWhenRegionStatisticsDisabledAndExpirationSpecified() throws Exception {

		ClientCache mockCache = mock(ClientCache.class);

		Region<Object, Object> mockRegion = mock(Region.class);

		RegionAttributes<Object, Object> mockRegionAttributes = mock(RegionAttributes.class);

		AttributesMutator mockAttributesMutator = mock(AttributesMutator.class);

		EvictionAttributesMutator mockEvictionAttributesMutator = mock(EvictionAttributesMutator.class);

		ExpirationAttributes mockExpirationAttributesEntryTtl = mock(ExpirationAttributes.class);

		when(mockCache.getRegion(eq("Example"))).thenReturn(mockRegion);
		when(mockRegion.getFullPath()).thenReturn("/Example");
		when(mockRegion.getName()).thenReturn("Example");
		when(mockRegion.getAttributes()).thenReturn(mockRegionAttributes);
		when(mockRegion.getAttributesMutator()).thenReturn(mockAttributesMutator);
		when(mockAttributesMutator.getEvictionAttributesMutator()).thenReturn(mockEvictionAttributesMutator);
		when(mockRegionAttributes.getStatisticsEnabled()).thenReturn(false);

		LookupRegionFactoryBean factoryBean = new LookupRegionFactoryBean();

		factoryBean.setBeanName("Example");
		factoryBean.setCache(mockCache);
		factoryBean.setEntryTimeToLive(mockExpirationAttributesEntryTtl);
		//factoryBean.setStatisticsEnabled(true);

		assertThat(factoryBean.isStatisticsEnabled()).isTrue();

		try {
			factoryBean.afterPropertiesSet();
		}
		catch (IllegalStateException expected) {
			assertThat(expected.getMessage()).isEqualTo(
				"Statistics for Region [/Example] must be enabled to change Entry & Region TTL/TTI Expiration settings");
			throw expected;
		}
		finally {
			verify(mockAttributesMutator, never()).setEntryTimeToLive(any(ExpirationAttributes.class));
		}
	}

	@Test
	public void testIsLookupEnabledAlways() {

		LookupRegionFactoryBean factoryBean = new LookupRegionFactoryBean();

		assertThat(factoryBean.isLookupEnabled()).isTrue();

		factoryBean.setLookupEnabled(false);

		assertThat(factoryBean.isLookupEnabled()).isTrue();
	}
}
