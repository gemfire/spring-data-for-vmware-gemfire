/*
 * Copyright 2022-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import org.apache.geode.cache.client.ClientCache;
import org.junit.Test;

/**
 * Unit Tests for {@link CacheResolver}.
 *
 * @author John Blum
 * @see org.junit.Test
 * @see org.mockito.Mockito
 * @see org.apache.geode.cache.client.ClientCache
 * @see org.springframework.data.gemfire.CacheResolver
 * @since 2.3.0
 */
public class CacheResolverUnitTests {

	@Test
	@SuppressWarnings("rawtypes")
	public void getCallsResolve() {

		ClientCache mockCache = mock(ClientCache.class);

		CacheResolver mockCacheResolver = mock(CacheResolver.class);

		when(mockCacheResolver.get()).thenCallRealMethod();
		when(mockCacheResolver.resolve()).thenReturn(mockCache);

		assertThat(mockCacheResolver.get()).isEqualTo(mockCache);

		verify(mockCacheResolver, times(1)).resolve();
		verifyNoInteractions(mockCache);
	}
}
