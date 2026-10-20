/*
 * Copyright 2022-2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.support;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import java.util.concurrent.atomic.AtomicReference;

import edu.umd.cs.mtc.MultithreadedTestCase;
import edu.umd.cs.mtc.TestFramework;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import com.vmware.gemfire.driver.spi.DriverRegion;
import com.vmware.gemfire.driver.spi.DriverRegionLifecycleListener;
import com.vmware.gemfire.driver.spi.UnsupportedFeatureException;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class AbstractCachingRegionResolverUnitTests {

	@Test
	public void cachesOnlyAfterSuccessfulRegistrationAndRetriesMisses() {
		DriverRegion region = mock(DriverRegion.class);
		AbstractCachingRegionResolver resolver = spy(AbstractCachingRegionResolver.class);
		when(resolver.doResolve("entries")).thenReturn(null, region);
		assertThat(resolver.resolve("entries")).isNull();
		assertThat(resolver.resolve("entries")).isSameAs(region);
		assertThat(resolver.resolve("entries")).isSameAs(region);
		verify(resolver, times(2)).doResolve("entries");
		verify(region).addLifecycleListener(resolver);
		verifyNoMoreInteractions(region);
	}

	@Test
	public void failedRegistrationPropagatesAndDoesNotCache() {
		DriverRegion region = mock(DriverRegion.class);
		AbstractCachingRegionResolver resolver = spy(AbstractCachingRegionResolver.class);
		when(resolver.doResolve("entries")).thenReturn(region);
		var failure = UnsupportedFeatureException.notIntroduced("Region.addLifecycleListener");
		doThrow(failure).doNothing().when(region).addLifecycleListener(resolver);
		assertThatThrownBy(() -> resolver.resolve("entries")).isSameAs(failure);
		assertThat(resolver.resolve("entries")).isSameAs(region);
		assertThat(resolver.resolve("entries")).isSameAs(region);
		verify(resolver, times(2)).doResolve("entries");
		verify(region, times(2)).addLifecycleListener(resolver);
	}

	@Test
	public void invalidNamesNeverResolve() {
		AbstractCachingRegionResolver resolver = spy(AbstractCachingRegionResolver.class);
		for (String name : new String[] { null, "", "  " }) {
			assertThat(resolver.resolve(name)).isNull();
		}
		verify(resolver, never()).doResolve(any());
	}

	@Test
	public void destructionInvalidatesOnlyNameAndPreservesAliasAndPathLimitation() {
		DriverRegion region = mock(DriverRegion.class);
		when(region.name()).thenReturn("entries");
		DriverRegion replacement = mock(DriverRegion.class);
		AbstractCachingRegionResolver resolver = spy(AbstractCachingRegionResolver.class);
		when(resolver.doResolve(anyString())).thenReturn(region);
		for (String name : new String[] { "entries", "/entries", "alias" }) {
			assertThat(resolver.resolve(name)).isSameAs(region);
		}
		ArgumentCaptor<DriverRegionLifecycleListener> callbacks = ArgumentCaptor.forClass(DriverRegionLifecycleListener.class);
		verify(region, times(3)).addLifecycleListener(callbacks.capture());
		callbacks.getValue().afterRegionDestroy(region);
		when(resolver.doResolve("entries")).thenReturn(replacement);
		assertThat(resolver.resolve("entries")).isSameAs(replacement);
		assertThat(resolver.resolve("/entries")).isSameAs(region);
		assertThat(resolver.resolve("alias")).isSameAs(region);
	}

	@Test
	public void unrelatedOrNamelessNotificationsAndCleanupKeepCachedHandles() {
		DriverRegion cached = mock(DriverRegion.class);
		when(cached.name()).thenReturn("entries");
		AbstractCachingRegionResolver resolver = spy(AbstractCachingRegionResolver.class);
		resolver.cache(cached);
		resolver.afterRegionDestroy(null);
		for (String name : new String[] { null, "", "  ", "unrelated" }) {
			DriverRegion other = mock(DriverRegion.class);
			when(other.name()).thenReturn(name);
			resolver.afterRegionDestroy(other);
		}
		resolver.close();
		assertThat(resolver.resolve("entries")).isSameAs(cached);
		verify(resolver, never()).doResolve(any());
		verify(cached, never()).isDestroyed();
	}

	@Test
	public void initialResolutionAndRegistrationAreSerialized() throws Throwable {
		TestFramework.runOnce(new ConcurrentResolution());
	}

	public static final class ConcurrentResolution extends MultithreadedTestCase {
		private AbstractCachingRegionResolver resolver;
		private final DriverRegion region = mock(DriverRegion.class);
		private final AtomicReference<DriverRegion> first = new AtomicReference<>();
		private final AtomicReference<DriverRegion> second = new AtomicReference<>();
		@Override public void initialize() {
			resolver = spy(AbstractCachingRegionResolver.class);
			when(resolver.doResolve("entries")).thenAnswer(invocation -> { waitForTick(2); return region; });
		}
		public void thread1() { first.set(resolver.resolve("entries")); }
		public void thread2() { waitForTick(1); second.set(resolver.resolve("entries")); }
		@Override public void finish() {
			assertThat(first.get()).isSameAs(region);
			assertThat(second.get()).isSameAs(region);
			verify(resolver).doResolve("entries");
			verify(region).addLifecycleListener(resolver);
		}
	}
}
