/*
 * Copyright $originalComment.match(" (\d+)", 1, "-", $today.year)2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.support;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import org.springframework.data.gemfire.gud.api.GudAttributesMutator;
import org.springframework.data.gemfire.gud.api.GudRegion;

import org.springframework.beans.factory.BeanFactory;

/**
 * Unit Tests for {@link BeanFactoryRegionResolver}.
 *
 * @author John Blum
 * @see org.junit.Test
 * @see org.mockito.Mockito
 * @see org.apache.geode.cache.GudRegion
 * @see org.springframework.beans.factory.BeanFactory
 * @see org.springframework.data.gemfire.RegionResolver
 * @see org.springframework.data.gemfire.support.BeanFactoryRegionResolver
 * @since 2.3.0
 */
@RunWith(MockitoJUnitRunner.class)
public class BeanFactoryRegionResolverUnitTests {

	@Mock
	private BeanFactory mockBeanFactory;

	@SuppressWarnings("unchecked")
	private <K, V> GudRegion<K, V> mockRegion() {

		GudRegion<K, V> mockRegion = mock(GudRegion.class);

		GudAttributesMutator<K, V> mockAttributesMutator = mock(GudAttributesMutator.class);

		when(mockRegion.getAttributesMutator()).thenReturn(mockAttributesMutator);
		when(mockAttributesMutator.getRegion()).thenReturn(mockRegion);

		return mockRegion;
	}

	@Test
	public void constructBeanFactoryRegionResolverWithBeanFactory() {

		BeanFactoryRegionResolver regionResolver = new BeanFactoryRegionResolver(this.mockBeanFactory);

		assertThat(regionResolver).isNotNull();
		assertThat(regionResolver.getBeanFactory()).isEqualTo(this.mockBeanFactory);
	}

	@Test(expected = IllegalArgumentException.class)
	public void constructBeanFactoryRegionResolverWithNull() {

		try {
			new BeanFactoryRegionResolver(null);
		}
		catch (IllegalArgumentException expected) {

			assertThat(expected).hasMessage("BeanFactory must not be null");
			assertThat(expected).hasNoCause();

			throw expected;
		}
	}

	@Test
	@SuppressWarnings("rawtypes")
	public void doResolveReturnsRegionForName() {

		GudRegion mockRegion = mockRegion();

		when(this.mockBeanFactory.containsBean(anyString())).thenReturn(true);
		when(this.mockBeanFactory.getBean(anyString(), eq(GudRegion.class))).thenReturn(mockRegion);

		BeanFactoryRegionResolver regionResolver = spy(new BeanFactoryRegionResolver(this.mockBeanFactory));

		assertThat(regionResolver.resolve("MockRegion")).isEqualTo(mockRegion);
		assertThat(regionResolver.resolve("MockRegion")).isEqualTo(mockRegion);

		verify(this.mockBeanFactory, times(1)).containsBean(eq("MockRegion"));
		verify(this.mockBeanFactory, times(1)).getBean(eq("MockRegion"), eq(GudRegion.class));
		verify(regionResolver, times(1)).doResolve(eq("MockRegion"));
	}

	@Test
	public void doResolveReturnsNullForNonRegionBean() {

		when(this.mockBeanFactory.containsBean(anyString())).thenReturn(false);

		BeanFactoryRegionResolver regionResolver = spy(new BeanFactoryRegionResolver(this.mockBeanFactory));

		assertThat(regionResolver.resolve("MockRegion")).isNull();
		assertThat(regionResolver.resolve("MockRegion")).isNull();

		verify(this.mockBeanFactory, times(2)).containsBean(eq("MockRegion"));
		verify(this.mockBeanFactory, never()).getBean(eq("MockRegion"), eq(GudRegion.class));
		verify(regionResolver, times(2)).doResolve(eq("MockRegion"));
	}

	public void testDoResolveWithInvalidRegionBeanName(String regionBeanName) {

		BeanFactoryRegionResolver regionResolver = spy(new BeanFactoryRegionResolver(this.mockBeanFactory));

		assertThat(regionResolver.doResolve(regionBeanName)).isNull();

		verify(this.mockBeanFactory, never()).containsBean(anyString());
		verify(this.mockBeanFactory, never()).getBean(anyString(), eq(GudRegion.class));
	}

	@Test
	public void doResolveWithBlankRegionBeanNameReturnsNull() {
		testDoResolveWithInvalidRegionBeanName("  ");
	}

	@Test
	public void doResolveWithEmptyRegionBeanNameReturnsNull() {
		testDoResolveWithInvalidRegionBeanName("");
	}

	@Test
	public void doResolveWithNullRegionBeanNameReturnsNull() {
		testDoResolveWithInvalidRegionBeanName(null);
	}
}
