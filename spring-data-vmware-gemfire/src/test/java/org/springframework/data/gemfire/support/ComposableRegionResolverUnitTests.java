/*
 * Copyright 2022-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.support;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.function.Supplier;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import com.vmware.gemfire.driver.spi.DriverRegion;
import com.vmware.gemfire.driver.spi.DriverOperationException;
import com.vmware.gemfire.driver.spi.UnsupportedFeatureException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.springframework.data.gemfire.RegionResolver;

/**
 * Unit Tests for {@link ComposableRegionResolver}.
 *
 * @author John Blum
 * @see org.junit.Test
 * @see org.mockito.Mockito
 * @see org.mockito.junit.MockitoJUnitRunner
 * @see com.vmware.gemfire.driver.spi.DriverRegion
 * @see org.springframework.data.gemfire.RegionResolver
 * @see org.springframework.data.gemfire.support.ComposableRegionResolver
 * @since 2.3.0
 */
@RunWith(MockitoJUnitRunner.class)
@SuppressWarnings("rawtypes")
public class ComposableRegionResolverUnitTests {

	@Mock
	private DriverRegion mockRegion;

	@Mock(name = "MockRegionResolverOne")
	private RegionResolver mockRegionResolverOne;

	@Mock(name = "MockRegionResolverTwo")
	private RegionResolver mockRegionResolverTwo;

	@Mock(name = "MockRegionResolverThree")
	private RegionResolver mockRegionResolverThree;


	@Test
	public void constructComposableRegionResolver() {

		ComposableRegionResolver regionResolver =
			new ComposableRegionResolver(this.mockRegionResolverOne, this.mockRegionResolverTwo);

		assertThat(regionResolver).isNotNull();
		assertThat(regionResolver.getRegionResolverOne()).isEqualTo(this.mockRegionResolverOne);
		assertThat(regionResolver.getRegionResolverTwo()).isEqualTo(this.mockRegionResolverTwo);
	}

	public void testConstructRegionResolverWithNullRegionResolver(Supplier<RegionResolver> regionResolverSupplier,
			String expectedMessage) {

		try {
			regionResolverSupplier.get();
		}
		catch (IllegalArgumentException expected) {

			assertThat(expected).hasMessage(expectedMessage);
			assertThat(expected).hasNoCause();

			throw expected;
		}
	}

	@Test(expected = IllegalArgumentException.class)
	public void constructComposableRegionResolverWithNullFirstArgument() {
		testConstructRegionResolverWithNullRegionResolver(
			() -> new ComposableRegionResolver(null, this.mockRegionResolverTwo),
			"RegionResolver 1 must not be null");
	}

	@Test(expected = IllegalArgumentException.class)
	public void constructComposableRegionResolverWithNullSecondArgument() {
		testConstructRegionResolverWithNullRegionResolver(
			() -> new ComposableRegionResolver(this.mockRegionResolverOne, null),
			"RegionResolver 2 must not be null");
	}

	@Test
	public void composeWithAllNullRegionResolvers() {
		assertThat(ComposableRegionResolver.compose(null, null)).isNull();
	}

	@Test
	public void composeWithSingleNullRegionResolver() {

		assertThat(ComposableRegionResolver.compose(this.mockRegionResolverOne, null))
			.isSameAs(this.mockRegionResolverOne);

		assertThat(ComposableRegionResolver.compose(null, this.mockRegionResolverTwo))
			.isSameAs(this.mockRegionResolverTwo);
	}

	@Test
	public void composeWithTwoNonNullRegionResolvers() {

		RegionResolver regionResolver =
			ComposableRegionResolver.compose(this.mockRegionResolverOne, this.mockRegionResolverTwo);

		assertThat(regionResolver).isNotNull();
		assertThat(regionResolver).isInstanceOf(ComposableRegionResolver.class);
		assertThat(((ComposableRegionResolver) regionResolver).getRegionResolverOne()).isEqualTo(this.mockRegionResolverOne);
		assertThat(((ComposableRegionResolver) regionResolver).getRegionResolverTwo()).isEqualTo(this.mockRegionResolverTwo);
	}

	@Test
	public void composeArrayOfRegionResolvers() {

		RegionResolver regionResolver = ComposableRegionResolver
			.compose(this.mockRegionResolverOne, this.mockRegionResolverTwo, this.mockRegionResolverThree);

		assertThat(regionResolver).isNotNull();
		assertThat(regionResolver).isInstanceOf(ComposableRegionResolver.class);

		RegionResolver regionResolverOne = ((ComposableRegionResolver) regionResolver).getRegionResolverOne();
		RegionResolver regionResolverTwo = ((ComposableRegionResolver) regionResolver).getRegionResolverTwo();

		assertThat(regionResolverOne).isNotNull();
		assertThat(regionResolverOne).isInstanceOf(ComposableRegionResolver.class);
		assertThat(((ComposableRegionResolver) regionResolverOne).getRegionResolverOne()).isEqualTo(this.mockRegionResolverOne);
		assertThat(((ComposableRegionResolver) regionResolverOne).getRegionResolverTwo()).isEqualTo(this.mockRegionResolverTwo);
		assertThat(regionResolverTwo).isEqualTo(this.mockRegionResolverThree);
	}

	@Test
	public void composeArrayWithNullAndNonNullRegionResolvers() {

		RegionResolver regionResolver = ComposableRegionResolver
			.compose(null, this.mockRegionResolverOne, null, null, this.mockRegionResolverTwo);

		assertThat(regionResolver).isNotNull();
		assertThat(regionResolver).isInstanceOf(ComposableRegionResolver.class);
		assertThat(((ComposableRegionResolver) regionResolver).getRegionResolverOne()).isEqualTo(this.mockRegionResolverOne);
		assertThat(((ComposableRegionResolver) regionResolver).getRegionResolverTwo()).isEqualTo(this.mockRegionResolverTwo);
	}

	@Test
	public void composeArrayWithSingleRegionResolver() {
		assertThat(ComposableRegionResolver.compose(this.mockRegionResolverOne)).isEqualTo(this.mockRegionResolverOne);
	}

	@Test
	public void composeEmptyArrayReturnsNull() {
		assertThat(ComposableRegionResolver.compose()).isNull();
	}

	@Test
	public void composeNullArrayIsNullSafeAndReturnsNull() {
		assertThat(ComposableRegionResolver.compose((RegionResolver[]) null)).isNull();
	}

	@Test
	public void composeNullIterableIsNullSafeAndReturnsNull() {
		assertThat(ComposableRegionResolver.compose((Iterable<RegionResolver>) null)).isNull();
	}

	@Test
	@SuppressWarnings("unchecked")
	public void resolveReturnsRegionFromFirstRegionResolverInComposition() {

		RegionResolver regionResolver = ComposableRegionResolver
			.compose(this.mockRegionResolverOne, this.mockRegionResolverTwo, this.mockRegionResolverThree);

		when(this.mockRegionResolverOne.resolve(anyString())).thenReturn(this.mockRegion);

		assertThat(regionResolver.resolve("MockRegion")).isEqualTo(this.mockRegion);

		verify(this.mockRegionResolverOne, times(1)).resolve(eq("MockRegion"));
		verify(this.mockRegionResolverTwo, never()).resolve(anyString());
		verify(this.mockRegionResolverThree, never()).resolve(anyString());
	}

	@Test
	@SuppressWarnings("unchecked")
	public void resolveReturnsRegionFromLastRegionResolverInCompositionAndCachesResolvedRegion() {

		RegionResolver regionResolver = ComposableRegionResolver
			.compose(this.mockRegionResolverOne, null, this.mockRegionResolverTwo,
				this.mockRegionResolverThree, null, null);

		when(this.mockRegionResolverThree.resolve(anyString())).thenReturn(this.mockRegion);

		assertThat(regionResolver.resolve("MockRegion")).isEqualTo(this.mockRegion);
		assertThat(regionResolver.resolve("MockRegion")).isEqualTo(this.mockRegion);

		InOrder inOrder = Mockito
			.inOrder(this.mockRegionResolverOne, this.mockRegionResolverTwo, this.mockRegionResolverThree);

		inOrder.verify(this.mockRegionResolverOne, times(1)).resolve(eq("MockRegion"));
		inOrder.verify(this.mockRegionResolverTwo, times(1)).resolve(eq("MockRegion"));
		inOrder.verify(this.mockRegionResolverThree, times(1)).resolve(eq("MockRegion"));
	}

	@Test
	@SuppressWarnings("unchecked")
	public void resolveReturnsRegionFromSecondRegionResolverInCompositionAndCachesResolvedRegion() {

		RegionResolver regionResolver = ComposableRegionResolver
			.compose(this.mockRegionResolverOne, this.mockRegionResolverTwo, this.mockRegionResolverThree);

		when(this.mockRegionResolverOne.resolve(anyString()))
			.thenReturn(null);

		when(this.mockRegionResolverTwo.resolve(anyString())).thenReturn(this.mockRegion);

		assertThat(regionResolver.resolve("MockRegion")).isEqualTo(this.mockRegion);
		assertThat(regionResolver.resolve("MockRegion")).isEqualTo(this.mockRegion);

		InOrder inOrder = Mockito
			.inOrder(this.mockRegionResolverOne, this.mockRegionResolverTwo, this.mockRegionResolverThree);

		inOrder.verify(this.mockRegionResolverOne, times(1)).resolve(eq("MockRegion"));
		inOrder.verify(this.mockRegionResolverTwo, times(1)).resolve(eq("MockRegion"));
		inOrder.verify(this.mockRegionResolverThree, never()).resolve(eq("MockRegion"));
	}

	@Test
	public void failuresStopCompositionWithoutRetryOrFallback() {
		for (Throwable failure : new Throwable[] { new DriverOperationException("failed", null),
			UnsupportedFeatureException.notIntroduced("test"), new IllegalStateException("failed"),
			new AssertionError("failed") }) {
			for (boolean failFirst : new boolean[] { true, false }) {
				RegionResolver first = mock(RegionResolver.class);
				RegionResolver second = mock(RegionResolver.class);
				RegionResolver third = mock(RegionResolver.class);
				when((failFirst ? first : second).resolve("entries")).thenThrow(failure);
				RegionResolver composed = ComposableRegionResolver.compose(first, second, third);
				assertThatThrownBy(() -> composed.resolve("entries")).isSameAs(failure);
				verify(first).resolve("entries");
				verify(second, times(failFirst ? 0 : 1)).resolve("entries");
				verify(third, never()).resolve(anyString());
			}
		}
	}
}
