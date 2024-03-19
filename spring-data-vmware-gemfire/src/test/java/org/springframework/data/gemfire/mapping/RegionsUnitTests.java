/*
 * Copyright 2022-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.mapping;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import org.apache.geode.cache.Region;

import org.springframework.data.gemfire.repository.sample.User;
import org.springframework.data.mapping.context.MappingContext;

/**
 * Unit Tests for {@link Regions}.
 *
 * @author John J. Blum
 * @see org.junit.Test
 * @see org.mockito.Mockito
 * @see MockitoJUnitRunner
 * @see Regions
 * @since 1.3.4
 */
@SuppressWarnings("unchecked")
@RunWith(MockitoJUnitRunner.class)
public class RegionsUnitTests {

	@Mock
	@SuppressWarnings("rawtypes")
	private MappingContext mockMappingContext;

	private Region<?, ?> mockUsers;
	private Region<?, ?> mockAdminUsers;
	private Region<?, ?> mockGuestUsers;

	private Regions regions;

	private Region<?, ?> mockRegion(String fullPath) {
		return mockRegion(fullPath.substring(fullPath.lastIndexOf(Region.SEPARATOR) + 1), fullPath);
	}

	private Region<?, ?> mockRegion(String name, String fullPath) {

		Region<?, ?> mockRegion = mock(Region.class, name);

		when(mockRegion.getName()).thenReturn(name);
		when(mockRegion.getFullPath()).thenReturn(fullPath);

		return mockRegion;
	}

	@Before
	public void setup() {

		mockUsers = mockRegion("/Users");
		mockAdminUsers = mockRegion("/Users/Admin");
		mockGuestUsers = mockRegion("/Users/Guest");

		regions = new Regions(Arrays.asList(mockUsers, mockAdminUsers, mockGuestUsers), mockMappingContext);

		assertThat(regions).isNotNull();
	}

	@After
	public void tearDown() {

		mockUsers = mockAdminUsers = mockGuestUsers = null;
		regions = null;
	}

	@Test
	public void getRegionByEntityTypeReturnsRegionForEntityRegionName() {

		GemfirePersistentEntity<User> mockPersistentEntity = mock(GemfirePersistentEntity.class);

		when(mockPersistentEntity.getRegionName()).thenReturn("Users");
		when(mockMappingContext.getPersistentEntity(eq(User.class))).thenReturn(mockPersistentEntity);

		assertThat(regions.getRegion(User.class)).isEqualTo(mockUsers);
	}

	@Test
	public void getRegionByEntityTypeReturnsRegionForEntityTypeSimpleName() {

		when(mockMappingContext.getPersistentEntity(any(Class.class))).thenReturn(null);

		assertThat(regions.getRegion(Users.class)).isEqualTo(mockUsers);
	}

	@Test
	public void getRegionByEntityTypeReturnsNull() {

		when(mockMappingContext.getPersistentEntity(any(Class.class))).thenReturn(null);

		assertThat(regions.getRegion(Object.class)).isNull();
	}

	@Test(expected = IllegalArgumentException.class)
	public void getRegionWithNullEntityTypeThrowsIllegalArgumentException() {

		try {
			regions.getRegion((Class<?>) null);
		}
		catch (IllegalArgumentException expected) {

			assertThat(expected).hasMessage("Entity type must not be null");
			assertThat(expected).hasNoCause();

			throw expected;
		}
	}

	@Test
	public void getRegionWithNameReturnsRegion() {

		assertThat(regions.getRegion("Users")).isSameAs(mockUsers);
		assertThat(regions.getRegion("Admin")).isSameAs(mockAdminUsers);
		assertThat(regions.getRegion("Guest")).isSameAs(mockGuestUsers);
	}

	@Test
	public void getRegionWithPathReturnsRegion() {

		assertThat(regions.getRegion("/Users")).isSameAs(mockUsers);
		assertThat(regions.getRegion("/Users/Admin")).isSameAs(mockAdminUsers);
		assertThat(regions.getRegion("/Users/Guest")).isSameAs(mockGuestUsers);
	}

	@Test
	public void getRegionWithNonExistingNameReturnsNull() {
		assertThat(regions.getRegion("NonExistingRegionName")).isNull();
	}

	@Test
	public void getRegionWithNonExistingPathReturnsNull() {
		assertThat(regions.getRegion("/Non/Existing/Region/Path")).isNull();
	}

	@Test(expected = IllegalArgumentException.class)
	public void getRegionWithNullNameNullPathThrowsIllegalArgumentException() {

		try {
			regions.getRegion((String) null);
		}
		catch (IllegalArgumentException expected) {

			assertThat(expected).hasMessage("Region name/path is required");
			assertThat(expected).hasNoCause();

			throw expected;
		}
	}

	@Test
	public void iterateRegions() {

		List<Region<?, ?>> actualRegions = new ArrayList<>(3);

		for (Region<?, ?> region : regions) {
			actualRegions.add(region);
		}

		List<Region<?, ?>> expectedRegions = Arrays.asList(mockUsers, mockAdminUsers, mockGuestUsers);

		assertThat(actualRegions).hasSize(expectedRegions.size() * 2);
		assertThat(actualRegions).containsAll(expectedRegions);
	}

	interface Users { }

}
