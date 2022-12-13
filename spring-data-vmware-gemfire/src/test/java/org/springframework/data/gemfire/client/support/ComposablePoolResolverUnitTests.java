/*
 * Copyright (c) VMware, Inc. 2022. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.client.support;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.function.Supplier;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import org.apache.geode.cache.client.Pool;

import org.springframework.data.gemfire.client.PoolResolver;

/**
 * Unit Tests for {@link ComposablePoolResolver}.
 *
 * @author John Blum
 * @see org.junit.Test
 * @see Mock
 * @see org.mockito.Mockito
 * @see MockitoJUnitRunner
 * @see Pool
 * @see PoolResolver
 * @see ComposablePoolResolver
 * @since 2.3.0
 */
@RunWith(MockitoJUnitRunner.class)
public class ComposablePoolResolverUnitTests {

	@Mock
	private Pool mockPool;

	@Mock(name = "one")
	private PoolResolver mockPoolResolverOne;

	@Mock(name = "two")
	private PoolResolver mockPoolResolverTwo;

	@Mock(name = "three")
	private PoolResolver mockPoolResolverThree;

	@Test
	@SuppressWarnings("all")
	public void composeWithArrayOfPoolResolvers() {

		PoolResolver poolResolver = ComposablePoolResolver
			.compose(this.mockPoolResolverOne, this.mockPoolResolverTwo, this.mockPoolResolverThree);

		assertThat(poolResolver).isInstanceOf(ComposablePoolResolver.class);

		PoolResolver one = ((ComposablePoolResolver) poolResolver).getPoolResolverOne();
		PoolResolver two = ((ComposablePoolResolver) poolResolver).getPoolResolverTwo();

		assertThat(one).isInstanceOf(ComposablePoolResolver.class);
		assertThat(((ComposablePoolResolver) one).getPoolResolverOne()).isEqualTo(this.mockPoolResolverOne);
		assertThat(((ComposablePoolResolver) one).getPoolResolverTwo()).isEqualTo(this.mockPoolResolverTwo);
		assertThat(two).isEqualTo(this.mockPoolResolverThree);
	}

	@Test
	public void composeWithArrayContainingSinglePoolResolver() {

		assertThat(ComposablePoolResolver.compose(this.mockPoolResolverOne))
			.isEqualTo(this.mockPoolResolverOne);
	}

	@Test
	public void composeWithArrayContainingNonNullPoolResolverAndNullPoolResolver() {

		PoolResolver[] poolResolvers = { this.mockPoolResolverOne, null };

		assertThat(ComposablePoolResolver.compose(poolResolvers)).isEqualTo(this.mockPoolResolverOne);
	}

	@Test
	@SuppressWarnings("all")
	public void composeWithArrayContainingNullAndNonNullPoolResolvers() {

		PoolResolver[] poolResolvers = { null, this.mockPoolResolverOne, null, this.mockPoolResolverTwo, null };

		PoolResolver poolResolver = ComposablePoolResolver.compose(poolResolvers);

		assertThat(poolResolver).isInstanceOf(ComposablePoolResolver.class);
		assertThat(((ComposablePoolResolver) poolResolver).getPoolResolverOne()).isEqualTo(this.mockPoolResolverOne);
		assertThat(((ComposablePoolResolver) poolResolver).getPoolResolverTwo()).isEqualTo(this.mockPoolResolverTwo);
	}

	@Test
	@SuppressWarnings("all")
	public void composeWithArrayContainingOrganizedNullAndNonNullPoolResolvers() {

		PoolResolver[] poolResolvers = {
			null, this.mockPoolResolverOne, this.mockPoolResolverTwo, null, this.mockPoolResolverThree, null, null
		};

		PoolResolver poolResolver = ComposablePoolResolver.compose(poolResolvers);

		assertThat(poolResolver).isInstanceOf(ComposablePoolResolver.class);

		PoolResolver one = ((ComposablePoolResolver) poolResolver).getPoolResolverOne();
		PoolResolver two = ((ComposablePoolResolver) poolResolver).getPoolResolverTwo();

		assertThat(one).isInstanceOf(ComposablePoolResolver.class);
		assertThat(((ComposablePoolResolver) one).getPoolResolverOne()).isEqualTo(this.mockPoolResolverOne);
		assertThat(((ComposablePoolResolver) one).getPoolResolverTwo()).isEqualTo(this.mockPoolResolverTwo);
		assertThat(two).isEqualTo(this.mockPoolResolverThree);
	}

	@Test
	public void composeWithEmptyArrayReturnsNull() {
		assertThat(ComposablePoolResolver.compose()).isNull();
	}

	@Test
	public void composeWithNullArrayIsNullSafe() {
		assertThat(ComposablePoolResolver.compose((PoolResolver[]) null)).isNull();
	}

	@Test
	public void composeWithNullIterableIsNullSafe() {
		assertThat(ComposablePoolResolver.compose((Iterable<PoolResolver>) null)).isNull();
	}

	@Test
	public void composeWithAllNullPoolResolvers() {
		assertThat(ComposablePoolResolver.compose(null, null)).isNull();
	}

	@Test
	public void composeWithSingleNullPoolResolver() {

		assertThat(ComposablePoolResolver.compose(this.mockPoolResolverOne, null))
			.isEqualTo(this.mockPoolResolverOne);

		assertThat(ComposablePoolResolver.compose(null, this.mockPoolResolverTwo))
			.isEqualTo(this.mockPoolResolverTwo);
	}

	@Test
	public void composeWithTwoNonNullPoolResolvers() {

		PoolResolver poolResolver = ComposablePoolResolver.compose(this.mockPoolResolverOne, this.mockPoolResolverTwo);

		assertThat(poolResolver).isInstanceOf(ComposablePoolResolver.class);
		assertThat(((ComposablePoolResolver) poolResolver).getPoolResolverOne()).isEqualTo(this.mockPoolResolverOne);
		assertThat(((ComposablePoolResolver) poolResolver).getPoolResolverTwo()).isEqualTo(this.mockPoolResolverTwo);
	}

	@Test
	public void constructComposablePoolResolver() {

		ComposablePoolResolver poolResolver =
			new ComposablePoolResolver(this.mockPoolResolverOne, this.mockPoolResolverTwo);

		assertThat(poolResolver).isNotNull();
		assertThat(poolResolver.getPoolResolverOne()).isEqualTo(this.mockPoolResolverOne);
		assertThat(poolResolver.getPoolResolverTwo()).isEqualTo(this.mockPoolResolverTwo);
	}

	public void testConstructComposablePoolResolverWithNullPoolResolver(Supplier<ComposablePoolResolver> constructor,
			String expectedMessage) {

		try {
			constructor.get();
		}
		catch (IllegalArgumentException expected) {

			assertThat(expected).hasMessage(expectedMessage);
			assertThat(expected).hasNoCause();

			throw expected;
		}
	}

	@Test(expected = IllegalArgumentException.class)
	public void constructComposablePoolResolverWithNullPoolResolverAsFirstArgument() {
		testConstructComposablePoolResolverWithNullPoolResolver(
			() -> new ComposablePoolResolver(null, this.mockPoolResolverTwo),
			"PoolResolver 1 must not be null");
	}

	@Test(expected = IllegalArgumentException.class)
	public void constructComposablePoolResolverWithNullPoolResolverAsSecondArgument() {
		testConstructComposablePoolResolverWithNullPoolResolver(
			() -> new ComposablePoolResolver(this.mockPoolResolverOne, null),
			"PoolResolver 2 must not be null");
	}

	@Test
	public void resolveReturnsFromFirstPoolResolver() {

		when(this.mockPoolResolverOne.resolve(anyString())).thenReturn(this.mockPool);

		PoolResolver poolResolver = ComposablePoolResolver.compose(this.mockPoolResolverOne, this.mockPoolResolverTwo);

		assertThat(poolResolver.resolve("TestPool")).isEqualTo(this.mockPool);

		verify(this.mockPoolResolverOne, times(1)).resolve(eq("TestPool"));
		verify(this.mockPoolResolverTwo, never()).resolve(anyString());
	}

	@Test
	public void resolveReturnsFromSecondPoolResolver() {

		when(this.mockPoolResolverTwo.resolve(anyString())).thenReturn(this.mockPool);

		PoolResolver poolResolver = ComposablePoolResolver.compose(this.mockPoolResolverOne, this.mockPoolResolverTwo);

		assertThat(poolResolver.resolve("TestPool")).isEqualTo(this.mockPool);

		verify(this.mockPoolResolverOne, times(1)).resolve(eq("TestPool"));
		verify(this.mockPoolResolverTwo, times(1)).resolve(eq("TestPool"));
	}

	@Test
	@SuppressWarnings("all")
	public void resolveReturnsPoolFromLastPoolResolverInOrder() {

		when(this.mockPoolResolverTwo.resolve(anyString())).thenReturn(this.mockPool);

		PoolResolver poolResolver = ComposablePoolResolver.compose(null, this.mockPoolResolverThree,
			null , null, this.mockPoolResolverOne, null, this.mockPoolResolverTwo, null, null);

		assertThat(poolResolver.resolve("TestPool")).isEqualTo(this.mockPool);

		InOrder ordered = inOrder(this.mockPoolResolverThree, this.mockPoolResolverOne, this.mockPoolResolverTwo);

		ordered.verify(this.mockPoolResolverThree, times(1)).resolve(eq("TestPool"));
		ordered.verify(this.mockPoolResolverOne, times(1)).resolve(eq("TestPool"));
		ordered.verify(this.mockPoolResolverTwo, times(1)).resolve(eq("TestPool"));
	}

	@Test
	public void resolveReturnsNull() {

		PoolResolver poolResolver = ComposablePoolResolver.compose(this.mockPoolResolverOne, this.mockPoolResolverTwo);

		assertThat(poolResolver.resolve("TestPool")).isNull();

		verify(this.mockPoolResolverOne, times(1)).resolve(eq("TestPool"));
		verify(this.mockPoolResolverTwo, times(1)).resolve(eq("TestPool"));
	}
}
