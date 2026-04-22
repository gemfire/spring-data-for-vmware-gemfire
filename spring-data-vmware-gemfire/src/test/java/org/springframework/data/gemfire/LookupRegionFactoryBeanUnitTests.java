/*
 * Copyright (c) 2026 Broadcom. All rights reserved.
 */

/*
 * Copyright $originalComment.match(" (\d+)", 1, "-", $today.year)2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire;

import org.junit.Test;
import org.springframework.data.gemfire.gud.api.GudAttributesMutator;
import org.springframework.data.gemfire.gud.api.GudCacheListener;
import org.springframework.data.gemfire.gud.api.GudCacheLoader;
import org.springframework.data.gemfire.gud.api.GudCacheWriter;
import org.springframework.data.gemfire.gud.api.GudClientCache;
import org.springframework.data.gemfire.gud.api.GudCustomExpiry;
import org.springframework.data.gemfire.gud.api.GudEvictionAttributesMutator;
import org.springframework.data.gemfire.gud.api.GudExpirationAttributes;
import org.springframework.data.gemfire.gud.api.GudRegion;
import org.springframework.data.gemfire.gud.api.GudRegionAttributes;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit Tests for {@link LookupRegionFactoryBean}.
 *
 * @author John Blum
 * @see org.junit.Test
 * @see org.mockito.Mockito
 * @see org.apache.geode.cache.GudAttributesMutator
 * @see org.apache.geode.cache.GudEvictionAttributesMutator
 * @see org.apache.geode.cache.GudRegion
 * @see org.springframework.data.gemfire.LookupRegionFactoryBean
 * @since 1.7.0
 */
@SuppressWarnings("rawtypes")
public class LookupRegionFactoryBeanUnitTests {

	@Test
	@SuppressWarnings("unchecked")
	public void testAfterPropertiesSet() throws Exception {

		GudClientCache mockCache = mock(GudClientCache.class, "testAfterPropertiesSet.MockCache");

		GudRegion<Object, Object> mockRegion = mock(GudRegion.class, "testAfterPropertiesSet.MockRegion");

		GudRegionAttributes<Object, Object> mockRegionAttributes = mock(GudRegionAttributes.class,
			"testAfterPropertiesSet.MockRegionAttributes");

		GudEvictionAttributesMutator mockEvictionAttributesMutator = mock(GudEvictionAttributesMutator.class,
			"testAfterPropertiesSet.GudEvictionAttributesMutator");

		GudAttributesMutator<Object, Object> mockAttributesMutator = mock(GudAttributesMutator.class,
			"testAfterPropertiesSet.MockAttributesMutator");

		when(mockCache.getRegion(eq("Example"))).thenReturn(mockRegion);
		when(mockRegion.getFullPath()).thenReturn("/Example");
		when(mockRegion.getName()).thenReturn("Example");
		when(mockRegion.getAttributes()).thenReturn(mockRegionAttributes);
		when(mockRegionAttributes.getStatisticsEnabled()).thenReturn(true);
		when(mockRegion.getAttributesMutator()).thenReturn(mockAttributesMutator);
		when(mockAttributesMutator.getEvictionAttributesMutator()).thenReturn(mockEvictionAttributesMutator);

		GudCacheListener mockCacheListenerZero = mock(GudCacheListener.class, "testAfterPropertiesSet.MockCacheListener.0");
		GudCacheListener mockCacheListenerOne = mock(GudCacheListener.class, "testAfterPropertiesSet.MockCacheListener.1");
		GudCacheListener mockCacheListenerTwo = mock(GudCacheListener.class, "testAfterPropertiesSet.MockCacheListener.2");

		GudCacheLoader mockCacheLoader = mock(GudCacheLoader.class, "testAfterPropertiesSet.MockCacheLoader");

		GudCacheWriter mockCacheWriter = mock(GudCacheWriter.class, "testAfterPropertiesSet.MockCacheWriter");

		GudCustomExpiry mockCustomExpiryTti = mock(GudCustomExpiry.class, "testAfterPropertiesSet.MockCustomExpiry.TTI");
		GudCustomExpiry mockCustomExpiryTtl = mock(GudCustomExpiry.class, "testAfterPropertiesSet.MockCustomExpiry.TTL");

		GudExpirationAttributes mockExpirationAttributesEntryTti = mock(GudExpirationAttributes.class,
			"testAfterPropertiesSet.MockExpirationAttributes.Entry.TTI");
		GudExpirationAttributes mockExpirationAttributesEntryTtl = mock(GudExpirationAttributes.class,
			"testAfterPropertiesSet.MockExpirationAttributes.Entry.TTL");
		GudExpirationAttributes mockExpirationAttributesRegionTti = mock(GudExpirationAttributes.class,
			"testAfterPropertiesSet.MockExpirationAttributes.GudRegion.TTI");
		GudExpirationAttributes mockExpirationAttributesRegionTtl = mock(GudExpirationAttributes.class,
			"testAfterPropertiesSet.MockExpirationAttributes.GudRegion.TTL");

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

		factoryBean.setCacheListeners(new GudCacheListener[] {
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

		GudClientCache mockCache = mock(GudClientCache.class);

		GudRegion<Object, Object> mockRegion = mock(GudRegion.class);

		GudRegionAttributes<Object, Object> mockRegionAttributes = mock(GudRegionAttributes.class);

		GudAttributesMutator mockAttributesMutator = mock(GudAttributesMutator.class);

		GudEvictionAttributesMutator mockEvictionAttributesMutator = mock(GudEvictionAttributesMutator.class);

		GudExpirationAttributes mockExpirationAttributesEntryTtl = mock(GudExpirationAttributes.class);

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
			verify(mockAttributesMutator, never()).setEntryTimeToLive(any(GudExpirationAttributes.class));
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
