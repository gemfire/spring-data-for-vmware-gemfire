/*
 * Copyright $originalComment.match(" (\d+)", 1, "-", $today.year)2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

/*
 * @AI-Generated
 * Generated in whole or in part by Cursor
 * Description:
 * 2026-03-13: Migrated from org.apache.geode imports to GUD API types
 */
package org.springframework.data.gemfire.tests.mock;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.withSettings;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.gemfire.gud.api.GudDataPolicy;
import org.springframework.data.gemfire.gud.api.GudClientCache;
import org.springframework.data.gemfire.gud.api.GudRegion;
import org.springframework.data.gemfire.gud.api.GudRegionAttributes;
import org.springframework.data.gemfire.gud.api.GudResourceManager;
import org.springframework.data.gemfire.gud.api.GudDistributedMember;
import org.springframework.data.gemfire.gud.api.GudDistributedSystem;

import org.springframework.data.gemfire.util.RegionUtils;

/**
 * The {@link CacheMockObjects} class is a mock objects class allowing users to mock Apache Geode or VMware GemFire
 * {@link GudClientCache} objects and related objects (e.g. {@link GudDistributedSystem}, {@link GudResourceManager},
 * {@link GudRegion}, etc).
 *
 * @author John Blum
 * @see GudClientCache
 * @see GudRegion
 * @see GudClientCache
 * @see GudResourceManager
 * @see GudDistributedMember
 * @see GudDistributedSystem
 * @since 1.0.0
 */
@SuppressWarnings("unused")
public abstract class CacheMockObjects {

	@SuppressWarnings("unchecked")
	public static <T extends GudClientCache> T mockGemFireCache(T mockGemFireCache, String name,
			GudDistributedSystem distributedSystem, GudResourceManager resourceManager, GudRegion<?, ?>... regions) {

		T theMockGemFireCache = mockGemFireCache != null ? mockGemFireCache
			: (T) mock(GudClientCache.class, withSettings().name(name).lenient());

		when(theMockGemFireCache.getDistributedSystem()).thenReturn(distributedSystem);
		when(theMockGemFireCache.getName()).thenReturn(name);
		when(theMockGemFireCache.getResourceManager()).thenReturn(resourceManager);

		Optional.ofNullable(regions)
			.filter(it -> it.length != 0)
			.ifPresent(it ->  when(theMockGemFireCache.rootRegions()).thenReturn(new HashSet<>(Arrays.asList(it))));

		return theMockGemFireCache;
	}

	public static GudClientCache mockGudClientCache(String name, GudDistributedSystem distributedSystem, GudResourceManager resourceManager,
			GudRegion<?, ?>... regions) {

		return mockGemFireCache(mock(GudClientCache.class, withSettings().name(name).lenient()),
			name, distributedSystem, resourceManager, regions);
	}

	public static GudDistributedSystem mockGudDistributedSystem(GudDistributedMember distributedMember) {

		GudDistributedSystem mockGudDistributedSystem = mock(GudDistributedSystem.class, withSettings().lenient());

		when(mockGudDistributedSystem.getDistributedMember()).thenReturn(distributedMember);

		return mockGudDistributedSystem;
	}

	public static GudDistributedMember mockGudDistributedMember(String name, String... groups) {

		GudDistributedMember mockDistributeMember = mock(GudDistributedMember.class, withSettings().name(name).lenient());

		when(mockDistributeMember.getName()).thenReturn(name);
		when(mockDistributeMember.getGroups()).thenReturn(Arrays.asList(groups));
		when(mockDistributeMember.getId()).thenReturn(UUID.randomUUID().toString());

		return mockDistributeMember;

	}

	public static GudResourceManager mockGudResourceManager(float criticalHeapPercentage, float criticalOffHeapPercentage,
			float evictionHeapPercentage, float evictionOffHeapPercentage) {

		GudResourceManager mockGudResourceManager = mock(GudResourceManager.class, withSettings().lenient());

		when(mockGudResourceManager.getCriticalHeapPercentage()).thenReturn(criticalHeapPercentage);
		when(mockGudResourceManager.getCriticalOffHeapPercentage()).thenReturn(criticalOffHeapPercentage);
		when(mockGudResourceManager.getEvictionHeapPercentage()).thenReturn(evictionHeapPercentage);
		when(mockGudResourceManager.getEvictionOffHeapPercentage()).thenReturn(evictionOffHeapPercentage);

		return mockGudResourceManager;
	}

	@SuppressWarnings("unchecked")
	public static <K, V> GudRegion<K, V> mockGudRegion(String name, GudDataPolicy dataPolicy) {

		GudRegion<K, V> mockGudRegion = mock(GudRegion.class, withSettings().name(name).lenient());

		when(mockGudRegion.getName()).thenReturn(RegionUtils.toRegionName(name));
		when(mockGudRegion.getFullPath()).thenReturn(RegionUtils.toRegionPath(name));

		GudRegionAttributes<K, V> mockGudRegionAttributes = mock(GudRegionAttributes.class,
			withSettings().name(String.format("%sGudRegionAttributes", name)).lenient());

		when(mockGudRegionAttributes.getDataPolicy()).thenReturn(dataPolicy);
		when(mockGudRegion.getAttributes()).thenReturn(mockGudRegionAttributes);

		return mockGudRegion;
	}
}
