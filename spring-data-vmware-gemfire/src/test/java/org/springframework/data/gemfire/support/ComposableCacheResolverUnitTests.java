/*
 * Copyright 2022-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.support;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.util.function.Supplier;
import com.vmware.gemfire.driver.spi.DriverOperationException;
import com.vmware.gemfire.driver.spi.UnsupportedFeatureException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import com.vmware.gemfire.driver.spi.DriverConnection;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.data.gemfire.CacheResolver;

/**
 * Unit Tests for {@link ComposableCacheResolver}.
 *
 * @author John Blum
 * @see org.junit.Test
 * @see org.mockito.Mock
 * @see org.mockito.Mockito
 * @see org.mockito.junit.MockitoJUnitRunner
 * @see com.vmware.gemfire.driver.spi.DriverConnection
 * @see org.springframework.data.gemfire.support.ComposableCacheResolver
 * @since 2.3.0
 */
@RunWith(MockitoJUnitRunner.class)
@SuppressWarnings({ "rawtypes", "unchecked" })
public class ComposableCacheResolverUnitTests {

	@Mock
	private DriverConnection mockCache;

	@Mock(name = "CacheResolverOne")
	private CacheResolver mockCacheResolverOne;

	@Mock(name = "CacheResolverTwo")
	private CacheResolver mockCacheResolverTwo;

	@Mock(name = "CacheResolverThree")
	private CacheResolver mockCacheResolverThree;

	@Test
	public void composeAllNullCacheResolvers() {
		assertThat(ComposableCacheResolver.compose(null, null)).isNull();
	}

	@Test
	public void composeOneNullCacheResolver() {

		assertThat(ComposableCacheResolver.compose(this.mockCacheResolverOne, null))
			.isSameAs(this.mockCacheResolverOne);

		assertThat(ComposableCacheResolver.compose(null, this.mockCacheResolverTwo))
			.isSameAs(this.mockCacheResolverTwo);
	}

	@Test
	public void composeTwoNonNullCacheResolvers() {

		CacheResolver composed = ComposableCacheResolver.compose(this.mockCacheResolverOne, this.mockCacheResolverTwo);

		assertThat(composed).isInstanceOf(ComposableCacheResolver.class);
		assertThat(((ComposableCacheResolver) composed).getCacheResolverOne()).isEqualTo(this.mockCacheResolverOne);
		assertThat(((ComposableCacheResolver) composed).getCacheResolverTwo()).isEqualTo(this.mockCacheResolverTwo);
	}

	@Test
	@SuppressWarnings("all")
	public void composeArrayOfCacheResolversBuildsComposition() {

		CacheResolver[] cacheResolvers = {
			this.mockCacheResolverOne, this.mockCacheResolverTwo, this.mockCacheResolverThree
		};

		CacheResolver composed = ComposableCacheResolver.compose(cacheResolvers);

		assertThat(composed).isInstanceOf(ComposableCacheResolver.class);

		CacheResolver cacheResolverOne = ((ComposableCacheResolver) composed).getCacheResolverOne();
		CacheResolver cacheResolverTwo = ((ComposableCacheResolver) composed).getCacheResolverTwo();

		assertThat(cacheResolverOne).isInstanceOf(ComposableCacheResolver.class);
		assertThat(((ComposableCacheResolver) cacheResolverOne).getCacheResolverOne()).isEqualTo(this.mockCacheResolverOne);
		assertThat(((ComposableCacheResolver) cacheResolverOne).getCacheResolverTwo()).isEqualTo(this.mockCacheResolverTwo);
		assertThat(cacheResolverTwo).isEqualTo(this.mockCacheResolverThree);
	}

	@Test
	@SuppressWarnings("all")
	public void composeArrayContainingCombinationOfNonNullAndNullCacheResolverReturnsNonNullCacheResolver() {

		CacheResolver[] cacheResolvers = { null, this.mockCacheResolverTwo, null, null };

		assertThat(ComposableCacheResolver.compose(cacheResolvers)).isSameAs(this.mockCacheResolverTwo);

		cacheResolvers = new CacheResolver[] { null, this.mockCacheResolverTwo, this.mockCacheResolverThree, null };

		CacheResolver composed = ComposableCacheResolver.compose(cacheResolvers);

		assertThat(composed).isInstanceOf(ComposableCacheResolver.class);
		assertThat(((ComposableCacheResolver) composed).getCacheResolverOne()).isEqualTo(this.mockCacheResolverTwo);
		assertThat(((ComposableCacheResolver) composed).getCacheResolverTwo()).isEqualTo(this.mockCacheResolverThree);

		cacheResolvers = new CacheResolver[] { null, this.mockCacheResolverOne, null, null, this.mockCacheResolverTwo };

		composed = ComposableCacheResolver.compose(cacheResolvers);

		assertThat(composed).isInstanceOf(ComposableCacheResolver.class);
		assertThat(((ComposableCacheResolver) composed).getCacheResolverOne()).isEqualTo(this.mockCacheResolverOne);
		assertThat(((ComposableCacheResolver) composed).getCacheResolverTwo()).isEqualTo(this.mockCacheResolverTwo);
	}

	@Test
	public void composeArrayContainingNonNullCacheResolverAndNullCacheResolverReturnsNonNullCacheResolver() {

		CacheResolver[] cacheResolvers = { null, this.mockCacheResolverTwo };

		assertThat(ComposableCacheResolver.compose(cacheResolvers)).isSameAs(this.mockCacheResolverTwo);
	}

	@Test
	public void composeArrayContainingSingleCacheResolverReturnsSingleCacheResolver() {
		assertThat(ComposableCacheResolver.compose(this.mockCacheResolverOne)).isSameAs(this.mockCacheResolverOne);
	}

	@Test
	public void composeWithEmptyArrayReturnsNull() {
		assertThat(ComposableCacheResolver.compose()).isNull();
	}

	@Test
	public void composeWithNullArrayReturnsNull() {
		assertThat(ComposableCacheResolver.compose((CacheResolver[]) null)).isNull();
	}

	@Test
	public void composeWithNulIterableReturnsNull() {
		assertThat(ComposableCacheResolver.compose((Iterable) null)).isNull();
	}

	@Test
	public void constructComposableCacheResolver() {

		ComposableCacheResolver cacheResolver =
			new ComposableCacheResolver(this.mockCacheResolverOne, this.mockCacheResolverTwo);

		assertThat(cacheResolver).isNotNull();
		assertThat(cacheResolver.getCacheResolverOne()).isEqualTo(this.mockCacheResolverOne);
		assertThat(cacheResolver.getCacheResolverTwo()).isEqualTo(this.mockCacheResolverTwo);
	}

	public void testConstructComposableCacheResolverWithNullCacheResolverThrowsIllegalArgumentException(
			Supplier<CacheResolver> cacheResolverSupplier, String expectedMessage) {

		try {
			cacheResolverSupplier.get();
		}
		catch (IllegalArgumentException expected) {

			assertThat(expected).hasMessage(expectedMessage);
			assertThat(expected).hasNoCause();

			throw expected;
		}
	}

	@Test(expected = IllegalArgumentException.class)
	public void constructComposableCacheResolverWithNullCacheResolverAsFirstArgument() {

		testConstructComposableCacheResolverWithNullCacheResolverThrowsIllegalArgumentException(
			() -> new ComposableCacheResolver(null, this.mockCacheResolverTwo),
				"CacheResolver 1 must not be null");
	}

	@Test(expected = IllegalArgumentException.class)
	public void constructComposableCacheResolverWithNullCacheResolverAsSecondArgument() {

		testConstructComposableCacheResolverWithNullCacheResolverThrowsIllegalArgumentException(
			() -> new ComposableCacheResolver(this.mockCacheResolverOne, null),
				"CacheResolver 2 must not be null");
	}

	@Test
	public void resolvesGemFireCacheFromLastCacheResolver() {

		when(this.mockCacheResolverThree.resolve()).thenReturn(this.mockCache);

		CacheResolver cacheResolver = ComposableCacheResolver
			.compose(this.mockCacheResolverOne, this.mockCacheResolverTwo, this.mockCacheResolverThree);

		assertThat(cacheResolver).isNotNull();
		assertThat(cacheResolver.resolve()).isEqualTo(this.mockCache);

		verify(this.mockCacheResolverOne, times(1)).resolve();
		verify(this.mockCacheResolverTwo, times(1)).resolve();
		verify(this.mockCacheResolverThree, times(1)).resolve();
	}

	@Test
	public void failuresStopCompositionWithoutRetryOrFallback() {
		for (Throwable failure : new Throwable[] { new DriverOperationException("failed", null),
			UnsupportedFeatureException.notIntroduced("test"), new IllegalStateException("failed"),
			new AssertionError("failed") }) {
			for (boolean failFirst : new boolean[] { true, false }) {
				CacheResolver<DriverConnection> first = org.mockito.Mockito.mock(CacheResolver.class);
				CacheResolver<DriverConnection> second = org.mockito.Mockito.mock(CacheResolver.class);
				CacheResolver<DriverConnection> third = org.mockito.Mockito.mock(CacheResolver.class);
				when((failFirst ? first : second).resolve()).thenThrow(failure);
				CacheResolver<DriverConnection> composed = ComposableCacheResolver.compose(first, second, third);
				assertThatThrownBy(composed::resolve).isSameAs(failure);
				verify(first).resolve();
				verify(second, times(failFirst ? 0 : 1)).resolve();
				verify(third, never()).resolve();
			}
		}
	}

	@Test
	public void shortCircuitsAndResolvesGemFireCacheFromFirstCacheResolver() {

		when(this.mockCacheResolverOne.resolve()).thenReturn(this.mockCache);

		CacheResolver cacheResolver = ComposableCacheResolver
			.compose(this.mockCacheResolverOne, this.mockCacheResolverTwo, this.mockCacheResolverThree);

		assertThat(cacheResolver).isNotNull();
		assertThat(cacheResolver.resolve()).isEqualTo(this.mockCache);

		verify(this.mockCacheResolverOne, times(1)).resolve();
		verify(this.mockCacheResolverTwo, never()).resolve();
		verify(this.mockCacheResolverThree, never()).resolve();
	}

	@Test
	public void shortCircuitsAndResolvesGemFireCacheFromSecondCacheResolver() {

		when(this.mockCacheResolverOne.resolve()).thenReturn(null);
		when(this.mockCacheResolverTwo.resolve()).thenReturn(this.mockCache);

		CacheResolver cacheResolver = ComposableCacheResolver
			.compose(null, this.mockCacheResolverOne, this.mockCacheResolverTwo, null, null,
				this.mockCacheResolverThree, null);

		assertThat(cacheResolver).isNotNull();
		assertThat(cacheResolver.resolve()).isEqualTo(this.mockCache);

		verify(this.mockCacheResolverOne, times(1)).resolve();
		verify(this.mockCacheResolverTwo, times(1)).resolve();
		verify(this.mockCacheResolverThree, never()).resolve();
	}
}
