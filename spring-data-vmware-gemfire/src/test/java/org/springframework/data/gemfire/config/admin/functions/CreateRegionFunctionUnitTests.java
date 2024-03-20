/*
 * Copyright 2022-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package org.springframework.data.gemfire.config.admin.functions;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import org.apache.geode.cache.Cache;
import org.apache.geode.cache.Region;
import org.apache.geode.cache.RegionFactory;
import org.apache.geode.cache.RegionShortcut;

import org.springframework.data.gemfire.config.schema.definitions.RegionDefinition;

/**
 * Unit tests for {@link CreateRegionFunction}.
 *
 * @author John Blum
 * @see org.junit.Test
 * @see org.mockito.Mock
 * @see org.mockito.Mockito
 * @see org.apache.geode.cache.Cache
 * @see org.apache.geode.cache.Region
 * @see org.springframework.data.gemfire.config.schema.definitions.RegionDefinition
 * @since 2.0.0
 */
@RunWith(MockitoJUnitRunner.class)
public class CreateRegionFunctionUnitTests {

	@Mock
	private Cache mockCache;

	@Mock
	private Region<Object, Object> mockRegion;

	private CreateRegionFunction createRegionFunction;

	@Before
	public void setup() {

		this.createRegionFunction = spy(new CreateRegionFunction());

		doReturn(this.mockCache).when(this.createRegionFunction).resolveCache();
		when(this.mockRegion.getName()).thenReturn("MockRegion");
	}

	@Test
	@SuppressWarnings("unchecked")
	public void createsRegionWhenRegionDoesNotExist() {

		RegionDefinition regionDefinition = RegionDefinition.from(this.mockRegion);

		RegionFactory<Object, Object> mockRegionFactory = mock(RegionFactory.class);

		when(this.mockCache.getRegion(anyString())).thenReturn(null);
		when(this.mockCache.createRegionFactory(any(RegionShortcut.class))).thenReturn(mockRegionFactory);

		assertThat(this.createRegionFunction.createRegion(regionDefinition)).isTrue();

		verify(this.mockCache, times(1)).getRegion(eq("MockRegion"));
		verify(this.mockCache, times(1)).createRegionFactory(eq(RegionShortcut.PARTITION));
		verify(mockRegionFactory, times(1)).create(eq("MockRegion"));
	}

	@Test
	public void doesNotCreateRegionWhenRegionAlreadyExists() {

		RegionDefinition regionDefinition = RegionDefinition.from(this.mockRegion);

		when(this.mockCache.getRegion(anyString())).thenReturn(this.mockRegion);

		assertThat(this.createRegionFunction.createRegion(regionDefinition)).isFalse();

		verify(this.mockCache, times(1)).getRegion(eq("MockRegion"));
		verify(this.mockCache, never()).createRegionFactory();
	}
}
