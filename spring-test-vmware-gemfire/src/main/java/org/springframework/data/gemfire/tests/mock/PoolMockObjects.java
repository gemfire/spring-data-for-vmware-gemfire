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

import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.withSettings;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import org.springframework.data.gemfire.gud.api.GudPool;
import org.springframework.data.gemfire.gud.api.GudQueryService;

/**
 * The {@link PoolMockObjects} class is a mock objects class allowing users to manually mock Apache Geode
 * or VMware GemFire client {@link GudPool} objects and related objects in the {@literal org.apache.geode.cache.client}
 * package.
 *
 * @author John Blum
 * @see GudPool
 * @see org.mockito.Mockito
 * @see MockObjectsSupport
 * @since 1.0.0
 */
@SuppressWarnings("unused")
public abstract class PoolMockObjects extends MockObjectsSupport {

	public static GudPool mockGudPool(String name, boolean initialDestroyedState, int freeConnectionTimeout, long idleTimeout,
			int loadConditioningInterval, List<InetSocketAddress> locators, int maxConnections, int minConnections,
			int maxConnectionsPerServer, int minConnectionsPerServer, boolean multiUserAuthentication,
			List<InetSocketAddress> onlineLocators, int pendingEventCount, long pingInterval,
			boolean prSingleHopEnabled, GudQueryService queryService, int readTimeout, int retryAttempts,
			String serverGroup, List<InetSocketAddress> servers, int socketBufferSize, int socketConnectTimeout,
			int statisticInterval, int subscriptionAckInterval, boolean subscriptionEnabled,
			int subscriptionMessageTrackingTimeout, int subscriptionRedundancy, int subscriptionTimeoutMultiplier,
			boolean threadLocalConnections) {

		AtomicBoolean destroyed = new AtomicBoolean(initialDestroyedState);

		GudPool mockGudPool = mock(GudPool.class, withSettings().name(name).lenient());

		when(mockGudPool.isDestroyed()).thenAnswer(newGetter(destroyed));
		when(mockGudPool.getFreeConnectionTimeout()).thenReturn(freeConnectionTimeout);
		when(mockGudPool.getIdleTimeout()).thenReturn(idleTimeout);
		when(mockGudPool.getLoadConditioningInterval()).thenReturn(loadConditioningInterval);
		when(mockGudPool.getLocators()).thenReturn(locators);
		when(mockGudPool.getMaxConnections()).thenReturn(maxConnections);
		when(mockGudPool.getMinConnections()).thenReturn(minConnections);
		when(mockGudPool.getMaxConnectionsPerServer()).thenReturn(maxConnectionsPerServer);
		when(mockGudPool.getMinConnectionsPerServer()).thenReturn(minConnectionsPerServer);
		when(mockGudPool.getMultiuserAuthentication()).thenReturn(multiUserAuthentication);
		when(mockGudPool.getName()).thenReturn(name);
		when(mockGudPool.getOnlineLocators()).thenReturn(onlineLocators);
		when(mockGudPool.getPendingEventCount()).thenReturn(pendingEventCount);
		when(mockGudPool.getPingInterval()).thenReturn(pingInterval);
		when(mockGudPool.getPRSingleHopEnabled()).thenReturn(prSingleHopEnabled);
		when(mockGudPool.getQueryService()).thenReturn(queryService);
		when(mockGudPool.getReadTimeout()).thenReturn(readTimeout);
		when(mockGudPool.getRetryAttempts()).thenReturn(retryAttempts);
		when(mockGudPool.getServerGroup()).thenReturn(serverGroup);
		when(mockGudPool.getServers()).thenReturn(servers);
		when(mockGudPool.getSocketBufferSize()).thenReturn(socketBufferSize);
		//when(mockGudPool.getSocketConnectTimeout()).thenReturn(socketConnectTimeout);
		when(mockGudPool.getStatisticInterval()).thenReturn(statisticInterval);
		when(mockGudPool.getSubscriptionAckInterval()).thenReturn(subscriptionAckInterval);
		when(mockGudPool.getSubscriptionEnabled()).thenReturn(subscriptionEnabled);
		when(mockGudPool.getSubscriptionMessageTrackingTimeout()).thenReturn(subscriptionMessageTrackingTimeout);
		when(mockGudPool.getSubscriptionRedundancy()).thenReturn(subscriptionRedundancy);

		doAnswer(newSetter(destroyed, true)).when(mockGudPool).destroy();
		doAnswer(newSetter(destroyed, true)).when(mockGudPool).destroy(anyBoolean());

		return mockGudPool;
	}
}
