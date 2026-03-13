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

import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.withSettings;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

import org.springframework.data.gemfire.gud.api.GudCacheServer;
import org.springframework.data.gemfire.gud.api.GudClientSubscriptionConfig;
import org.springframework.data.gemfire.gud.api.GudServerLoad;
import org.springframework.data.gemfire.gud.api.GudServerLoadProbe;
import org.springframework.data.gemfire.gud.api.GudServerMetrics;

/**
 * The {@link CacheServerMockObjects} class is a mock objects class allowing users to manually mock Apache Geode
 * or VMware GemFire {@link GudCacheServer} objects and related objects.
 *
 * @author John Blum
 * @see GudCacheServer
 * @see GudClientSubscriptionConfig
 * @see GudServerLoad
 * @see GudServerLoadProbe
 * @see GudServerMetrics
 * @see org.mockito.Mockito
 * @see MockObjectsSupport
 * @since 1.0.0
 */
@SuppressWarnings("unused")
public abstract class CacheServerMockObjects extends MockObjectsSupport {

	// TODO mock ClientSessions and InterestRegistrationListeners
	public static GudCacheServer mockCacheServer(String bindAddress, GudClientSubscriptionConfig clientSubscriptionConfig,
			String hostnameForClients, long loadPollInterval, GudServerLoadProbe serverLoadProbe, int maxConnections,
			int maxMessageCount, int maxThreads, int maxTimeBetweenPings, int messageTimeToLive, int port,
			boolean running, int socketBufferSize, boolean tcpNoDelay) throws Exception {

		AtomicBoolean runningState = new AtomicBoolean(running);

		GudCacheServer mockCacheServer = mock(GudCacheServer.class, withSettings().lenient());

		when(mockCacheServer.getBindAddress()).thenReturn(bindAddress);
		when(mockCacheServer.getPort()).thenReturn(port);
		when(mockCacheServer.isRunning()).thenAnswer(newGetter(runningState));

		doAnswer(invocation -> {
			runningState.set(true);
			return null;
		}).when(mockCacheServer).start();

		doAnswer(invocation -> {
			runningState.set(false);
			return null;
		}).when(mockCacheServer).stop();

		return mockCacheServer;
	}

	public static GudClientSubscriptionConfig mockClientSubscriptionConfig(int capacity, String diskStoreName,
			String evictionPolicy) {

		GudClientSubscriptionConfig mockClientSubscriptionConfig =
			mock(GudClientSubscriptionConfig.class, withSettings().lenient());

		when(mockClientSubscriptionConfig.getCapacity()).thenReturn(capacity);
		when(mockClientSubscriptionConfig.getDiskStoreName()).thenReturn(diskStoreName);
		when(mockClientSubscriptionConfig.getEvictionPolicy()).thenReturn(evictionPolicy);

		return mockClientSubscriptionConfig;
	}

	public static GudServerLoad mockServerLoad(float connectionLoad, float loadPerConnection,
			float loadPerSubscriptionConnection, float subscriptionConnectionLoad) {

		GudServerLoad mockServerLoad = mock(GudServerLoad.class, withSettings().lenient());

		when(mockServerLoad.getConnectionLoad()).thenReturn(connectionLoad);
		when(mockServerLoad.getLoadPerConnection()).thenReturn(loadPerConnection);
		when(mockServerLoad.getSubscriptionConnectionLoad()).thenReturn(subscriptionConnectionLoad);
		when(mockServerLoad.getLoadPerSubscriptionConnection()).thenReturn(loadPerSubscriptionConnection);

		return mockServerLoad;
	}

	public static GudServerLoadProbe mockServerLoadProbe() {
		return mock(GudServerLoadProbe.class);
	}

	public static GudServerMetrics mockServerMetrics(int clientCount, int connectionCount, int maxConnections,
			int subscriptionConnectionCount) {

		GudServerMetrics mockServerMetrics = mock(GudServerMetrics.class, withSettings().lenient());

		when(mockServerMetrics.getClientCount()).thenReturn(clientCount);
		when(mockServerMetrics.getConnectionCount()).thenReturn(connectionCount);
		when(mockServerMetrics.getMaxConnections()).thenReturn(maxConnections);
		when(mockServerMetrics.getSubscriptionConnectionCount()).thenReturn(subscriptionConnectionCount);

		return mockServerMetrics;
	}
}
