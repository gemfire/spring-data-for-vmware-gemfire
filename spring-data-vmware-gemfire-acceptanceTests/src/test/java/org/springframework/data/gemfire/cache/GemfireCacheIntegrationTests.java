/*
 * Copyright (c) VMware, Inc. 2022-2024. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.cache;

import edu.umd.cs.mtc.MultithreadedTestCase;
import edu.umd.cs.mtc.TestFramework;

import java.util.Properties;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.geode.cache.CacheFactory;
import org.apache.geode.cache.Region;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.springframework.cache.Cache;
import org.springframework.data.gemfire.GemfireUtils;
import org.springframework.data.gemfire.test.support.AbstractNativeCacheTests;

/**
 * Integration Tests for {@link GemfireCache}.
 *
 * @author Costin Leau
 * @author John Blum
 * @author Oliver Gierke
 * @see org.junit.Test
 * @see edu.umd.cs.mtc.MultithreadedTestCase
 * @see edu.umd.cs.mtc.TestFramework
 * @see org.apache.geode.cache.Region
 * @see org.springframework.cache.Cache
 */
public class GemfireCacheIntegrationTests extends AbstractNativeCacheTests<Region<Object, Object>> {

	@Override
	protected Cache newCache(Region<Object, Object> nativeCache) {
		return new GemfireCache(nativeCache);
	}

	@Override
	protected Region<Object, Object> newNativeCache() {

		Properties gemfireProperties = new Properties();

		gemfireProperties.setProperty("name", GemfireCacheIntegrationTests.class.getName());
		gemfireProperties.setProperty("log-level", "error");

		org.apache.geode.cache.Cache cache = GemfireUtils.getCache();

		cache = cache != null ? cache : new CacheFactory(gemfireProperties).create();

		Region<Object, Object> region = cache.getRegion(AbstractNativeCacheTests.CACHE_NAME);

		region = region != null ? region : cache.createRegionFactory().create(AbstractNativeCacheTests.CACHE_NAME);

		return region;
	}

	/**
	 * @see <a href="https://jira.spring.io/browse/SGF-317">Improve GemfireCache implementation to be able to build on Spring 4.1</a>
	 */
	@Test
	public void findsTypedValue() throws Exception {

		Cache cache = newCache();

		cache.put("key", "value");

		Assertions.assertThat(cache.get("key", String.class)).isEqualTo("value");
	}

	/**
	 * @see <a href="https://jira.spring.io/browse/SGF-317">Improve GemfireCache implementation to be able to build on Spring 4.1</a>
	 */
	@Test
	public void skipTypeChecksIfTargetTypeIsNull() throws Exception {

		Cache cache = newCache();

		cache.put("key", 1);

		Assertions.assertThat(cache.get("key", (Class<?>) null)).isEqualTo(1);
	}

	/**
	 * @see <a href="https://jira.spring.io/browse/SGF-317">Improve GemfireCache implementation to be able to build on Spring 4.1</a>
	 */
	@Test(expected = IllegalStateException.class)
	public void throwsIllegalStateExceptionIfTypedAccessDoesNotFindMatchingType() throws Exception {

		Cache cache = newCache();

		try {
			cache.put("key", "value");
		}
		catch (IllegalStateException expected) {

			Assertions.assertThat(expected).hasMessageContaining("Cached value [value] is not an instance of type [%s]",
				Integer.class.getName());
			Assertions.assertThat(expected.getCause()).hasNoCause();

			throw expected;
		}
		finally  {
			cache.get("key", Integer.class);
		}
	}

	@Test
	public void cacheGetWithValueLoaderFindsValue() throws Exception {

		GemfireCache cache = newCache();

		cache.put("key", "value");

		Assertions.assertThat(cache.get("key", TestValueLoader.NULL_VALUE)).isEqualTo("value");
		Assertions.assertThat(TestValueLoader.NULL_VALUE.wasCalled()).isFalse();
	}

	@Test
	@SuppressWarnings("unchecked")
	public void cacheGetWithValueLoaderUsesValueLoaderReturnsValue() throws Exception {

		GemfireCache cache = newCache();

		TestValueLoader<String> valueLoader = new TestValueLoader<String>("test");

		Assertions.assertThat(cache.get("key", valueLoader)).isEqualTo("test");
		Assertions.assertThat(valueLoader.wasCalled()).isTrue();
		Assertions.assertThat(((Region<Object, String>) cache.getNativeCache()).get("key")).isEqualTo("test");
	}

	@Test
	public void cacheGetWithValueLoaderUsesValueLoaderReturnsNull() throws Exception {

		GemfireCache cache = newCache();

		Assertions.assertThat(cache.get("key", TestValueLoader.NULL_VALUE)).isNull();
		Assertions.assertThat(TestValueLoader.NULL_VALUE.wasCalled()).isTrue();
		Assertions.assertThat(cache.getNativeCache().containsKey("key")).isFalse();
	}

	@Test(expected = Cache.ValueRetrievalException.class)
	public void cacheGetWithValueLoaderUsesValueLoaderAndThrowsException() throws Exception {

		GemfireCache cache = newCache();

		try {

			TestValueLoader<Exception> exceptionThrowingValueLoader =
				new TestValueLoader<>(new IllegalStateException("test"));

			cache.get("key", exceptionThrowingValueLoader);
		}
		catch (Cache.ValueRetrievalException expected) {

			Assertions.assertThat(expected).hasCauseInstanceOf(IllegalStateException.class);

			throw expected;
		}
		finally {
			Assertions.assertThat(cache.getNativeCache().containsKey("key")).isFalse();
		}
	}

	@Test
	public void cacheGetWithValueLoaderIsThreadSafe() throws Throwable {
		TestFramework.runOnce(new CacheGetWithValueLoaderIsThreadSafe());
	}

	@SuppressWarnings("unused")
	protected class CacheGetWithValueLoaderIsThreadSafe extends MultithreadedTestCase {

		private GemfireCache cache;

		private TestValueLoader<String> cacheLoader;

		@Override
		public void initialize(){

			super.initialize();

			this.cache = newCacheHandlesCheckedException();

			this.cacheLoader = new TestValueLoader<String>("test") {

				@Override
				public String call() throws Exception {
					waitForTick(2);
					return super.call();
				}
			};
		}

		<T extends Cache> T newCacheHandlesCheckedException() {

			try {
				return newCache();
			}
			catch (Exception cause) {
				throw new RuntimeException("Failed to create Cache", cause);
			}
		}

		public void thread1() {

			assertTick(0);

			Thread.currentThread().setName("Cache Loader Thread");

			String value = cache.get("key", cacheLoader);

			assertTick(2);
			Assertions.assertThat(value).isEqualTo("test");
			Assertions.assertThat(cacheLoader.wasCalled()).isTrue();
		}

		public void thread2() {

			waitForTick(1);

			Thread.currentThread().setName("Cache Reader Thread");

			TestValueLoader<String> illegalCacheLoader = new TestValueLoader<String>("illegal");

			String value = cache.get("key", illegalCacheLoader);

			assertTick(2);
			Assertions.assertThat(value).isEqualTo("test");
			Assertions.assertThat(illegalCacheLoader.wasCalled()).isFalse();
		}
	}

	protected static class TestValueLoader<T> implements Callable<T> {

		protected static final TestValueLoader<Object> NULL_VALUE = new TestValueLoader<Object>();

		private final AtomicBoolean called = new AtomicBoolean(false);

		private final T value;

		public TestValueLoader() {
			this(null);
		}

		public TestValueLoader(T value) {
			this.value = value;
		}

		protected boolean wasCalled() {
			return called.compareAndSet(true, false);
		}

		@Override
		public T call() throws Exception {
			called.set(true);

			if (value instanceof Exception) {
				throw (Exception) value;
			}

			return value;
		}
	}
}
