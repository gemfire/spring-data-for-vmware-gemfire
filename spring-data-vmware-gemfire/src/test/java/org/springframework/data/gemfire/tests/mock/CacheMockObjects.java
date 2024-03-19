/*
 * Copyright 2023-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.tests.mock;

import static org.apache.geode.internal.util.CollectionUtils.asSet;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.withSettings;

import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

import org.apache.geode.cache.Cache;
import org.apache.geode.cache.DataPolicy;
import org.apache.geode.cache.GemFireCache;
import org.apache.geode.cache.Region;
import org.apache.geode.cache.RegionAttributes;
import org.apache.geode.cache.client.ClientCache;
import org.apache.geode.cache.control.ResourceManager;
import org.apache.geode.distributed.DistributedMember;
import org.apache.geode.distributed.DistributedSystem;

import org.springframework.data.gemfire.util.RegionUtils;

/**
 * The {@link CacheMockObjects} class is a mock objects class allowing users to mock Apache Geode or VMware GemFire
 * {@link GemFireCache} objects and related objects (e.g. {@link DistributedSystem}, {@link ResourceManager},
 * {@link Region}, etc).
 *
 * @author John Blum
 * @see Cache
 * @see GemFireCache
 * @see Region
 * @see ClientCache
 * @see ResourceManager
 * @see DistributedMember
 * @see DistributedSystem
 * @since 1.0.0
 */
@SuppressWarnings("unused")
public abstract class CacheMockObjects {

	@SuppressWarnings("unchecked")
	public static <T extends GemFireCache> T mockGemFireCache(T mockGemFireCache, String name,
			DistributedSystem distributedSystem, ResourceManager resourceManager, Region<?, ?>... regions) {

		T theMockGemFireCache = mockGemFireCache != null ? mockGemFireCache
			: (T) mock(GemFireCache.class, withSettings().name(name).lenient());

		when(theMockGemFireCache.getDistributedSystem()).thenReturn(distributedSystem);
		when(theMockGemFireCache.getName()).thenReturn(name);
		when(theMockGemFireCache.getResourceManager()).thenReturn(resourceManager);

		Optional.ofNullable(regions)
			.filter(it -> it.length != 0)
			.ifPresent(it ->  when(theMockGemFireCache.rootRegions()).thenReturn(asSet(it)));

		return theMockGemFireCache;
	}

	public static ClientCache mockClientCache(String name, DistributedSystem distributedSystem, ResourceManager resourceManager,
			Region<?, ?>... regions) {

		return mockGemFireCache(mock(ClientCache.class, withSettings().name(name).lenient()),
			name, distributedSystem, resourceManager, regions);
	}

	public static Cache mockPeerCache(String name, DistributedSystem distributedSystem, ResourceManager resourceManager,
			Region<?, ?>... regions) {

		return mockGemFireCache(mock(Cache.class, withSettings().name(name).lenient()),
			name, distributedSystem, resourceManager, regions);
	}

	public static DistributedSystem mockDistributedSystem(DistributedMember distributedMember) {

		DistributedSystem mockDistributedSystem = mock(DistributedSystem.class, withSettings().lenient());

		when(mockDistributedSystem.getDistributedMember()).thenReturn(distributedMember);

		return mockDistributedSystem;
	}

	public static DistributedMember mockDistributedMember(String name, String... groups) {

		DistributedMember mockDistributeMember = mock(DistributedMember.class, withSettings().name(name).lenient());

		when(mockDistributeMember.getName()).thenReturn(name);
		when(mockDistributeMember.getGroups()).thenReturn(Arrays.asList(groups));
		when(mockDistributeMember.getId()).thenReturn(UUID.randomUUID().toString());

		return mockDistributeMember;

	}

	public static ResourceManager mockResourceManager(float criticalHeapPercentage, float criticalOffHeapPercentage,
			float evictionHeapPercentage, float evictionOffHeapPercentage) {

		ResourceManager mockResourceManager = mock(ResourceManager.class, withSettings().lenient());

		when(mockResourceManager.getCriticalHeapPercentage()).thenReturn(criticalHeapPercentage);
		when(mockResourceManager.getCriticalOffHeapPercentage()).thenReturn(criticalOffHeapPercentage);
		when(mockResourceManager.getEvictionHeapPercentage()).thenReturn(evictionHeapPercentage);
		when(mockResourceManager.getEvictionOffHeapPercentage()).thenReturn(evictionOffHeapPercentage);

		return mockResourceManager;
	}

	@SuppressWarnings("unchecked")
	public static <K, V> Region<K, V> mockRegion(String name, DataPolicy dataPolicy) {

		Region<K, V> mockRegion = mock(Region.class, withSettings().name(name).lenient());

		when(mockRegion.getName()).thenReturn(RegionUtils.toRegionName(name));
		when(mockRegion.getFullPath()).thenReturn(RegionUtils.toRegionPath(name));

		RegionAttributes<K, V> mockRegionAttributes = mock(RegionAttributes.class,
			withSettings().name(String.format("%sRegionAttributes", name)).lenient());

		when(mockRegionAttributes.getDataPolicy()).thenReturn(dataPolicy);
		when(mockRegion.getAttributes()).thenReturn(mockRegionAttributes);

		return mockRegion;
	}
}
