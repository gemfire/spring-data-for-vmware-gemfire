/*
 * Copyright 2022-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.support;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import edu.umd.cs.mtc.MultithreadedTestCase;
import edu.umd.cs.mtc.TestFramework;
import java.util.concurrent.atomic.AtomicReference;
import org.apache.geode.cache.client.ClientCache;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

/**
 * Unit Tests for {@link AbstractCachingCacheResolver}.
 *
 * @author John Blum
 * @see org.junit.Test
 * @see org.mockito.Mockito
 * @see org.mockito.junit.MockitoJUnitRunner
 * @see edu.umd.cs.mtc.MultithreadedTestCase
 * @see edu.umd.cs.mtc.TestFramework
 * @see org.springframework.data.gemfire.CacheResolver
 * @see org.springframework.data.gemfire.support.AbstractCachingCacheResolver
 * @since 2.3.0
 */
@RunWith(MockitoJUnitRunner.class)
public class AbstractCachingCacheResolverUnitTests {

	@Mock
	private ClientCache mockCache;

	@Test
	@SuppressWarnings("rawtypes")
	public void resolveCachesTheGemFireCacheObject() {

		AbstractCachingCacheResolver cacheResolver = mock(AbstractCachingCacheResolver.class);

		when(cacheResolver.doResolve()).thenReturn(this.mockCache);
		when(cacheResolver.resolve()).thenCallRealMethod();

		assertThat(cacheResolver.resolve()).isEqualTo(this.mockCache);
		assertThat(cacheResolver.resolve()).isSameAs(this.mockCache);

		verify(cacheResolver, times(2)).resolve();
		verify(cacheResolver, times(1)).doResolve();
	}

	@Test
	public void abstractCachinCacheResolverIsThreadSafe() throws Throwable {
		TestFramework.runOnce(new AbstractCachingCacheResolverMultithreadedTestCase());
	}

	@SuppressWarnings("unused")
	public final class AbstractCachingCacheResolverMultithreadedTestCase extends MultithreadedTestCase {

		private AbstractCachingCacheResolver<?> mockCacheResolver;

		private AtomicReference<ClientCache> gemfireCacheFromThreadOne = new AtomicReference<>(null);
		private AtomicReference<ClientCache> gemfireCacheFromThreadTwo = new AtomicReference<>(null);

		@Override
		public void initialize() {

			super.initialize();

			this.mockCacheResolver = mock(AbstractCachingCacheResolver.class);

			when(this.mockCacheResolver.resolve()).thenCallRealMethod();

			when(this.mockCacheResolver.doResolve()).thenAnswer(invocation -> {

				waitForTick(2);

				return AbstractCachingCacheResolverUnitTests.this.mockCache;
			});
		}

		public void thread1() {

			Thread.currentThread().setName("ClientCache Access Thread 1");

			assertTick(0);

			this.gemfireCacheFromThreadOne.set(this.mockCacheResolver.resolve());
		}

		public void thread2() {

			Thread.currentThread().setName("ClientCache Access Thread 2");

			assertTick(0);
			waitForTick(1);

			this.gemfireCacheFromThreadTwo.set(this.mockCacheResolver.resolve());
		}

		@Override
		public void finish() {

			ClientCache gemfireCacheFromThreadOne = this.gemfireCacheFromThreadOne.get();

			assertThat(gemfireCacheFromThreadOne).isNotNull();
			assertThat(gemfireCacheFromThreadOne).isEqualTo(this.gemfireCacheFromThreadTwo.get());

			verify(this.mockCacheResolver, times(2)).resolve();
			verify(this.mockCacheResolver, times(1)).doResolve();
		}
	}
}
