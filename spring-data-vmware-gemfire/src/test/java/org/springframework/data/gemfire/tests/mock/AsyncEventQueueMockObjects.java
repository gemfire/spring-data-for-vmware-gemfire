/*
 * Copyright 2023-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package org.springframework.data.gemfire.tests.mock;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.withSettings;

import org.apache.geode.cache.asyncqueue.AsyncEventQueue;
import org.apache.geode.cache.wan.GatewaySender;

/**
 * The {@link AsyncEventQueueMockObjects} class is a mock objects class allowing users to manually mock Apache Geode
 * or VMware GemFire {@link AsyncEventQueue} objects and related objects in the
 * {@literal org.apache.geode.cache.asyncqueue} package.
 *
 * @author John Blum
 * @see AsyncEventQueue
 * @see org.mockito.Mockito
 * @see MockObjectsSupport
 * @since 1.0.0
 */
@SuppressWarnings("unused")
public abstract class AsyncEventQueueMockObjects {

	public static AsyncEventQueue mockAsyncEventQueue(String id, boolean batchConflationEnabled, int batchSize,
			int batchTimeInterval, String diskStoreName, boolean diskSynchronous, int dispatcherThreads,
			boolean forwardExpirationDestroy, int maximumQueueMemory, GatewaySender.OrderPolicy orderPolicy,
			boolean parallel, boolean persistent, boolean primary, int size) {

		AsyncEventQueue mockAsyncEventQueue = mock(AsyncEventQueue.class, withSettings().name(id).lenient());

		when(mockAsyncEventQueue.getId()).thenReturn(id);
		when(mockAsyncEventQueue.isBatchConflationEnabled()).thenReturn(batchConflationEnabled);
		when(mockAsyncEventQueue.getBatchSize()).thenReturn(batchSize);
		when(mockAsyncEventQueue.getBatchTimeInterval()).thenReturn(batchTimeInterval);
		when(mockAsyncEventQueue.getDiskStoreName()).thenReturn(diskStoreName);
		when(mockAsyncEventQueue.isDiskSynchronous()).thenReturn(diskSynchronous);
		when(mockAsyncEventQueue.getDispatcherThreads()).thenReturn(dispatcherThreads);
		when(mockAsyncEventQueue.isForwardExpirationDestroy()).thenReturn(forwardExpirationDestroy);
		when(mockAsyncEventQueue.getMaximumQueueMemory()).thenReturn(maximumQueueMemory);
		when(mockAsyncEventQueue.getOrderPolicy()).thenReturn(orderPolicy);
		when(mockAsyncEventQueue.isParallel()).thenReturn(parallel);
		when(mockAsyncEventQueue.isPersistent()).thenReturn(persistent);
		when(mockAsyncEventQueue.isPrimary()).thenReturn(primary);
		when(mockAsyncEventQueue.size()).thenReturn(size);

		return mockAsyncEventQueue;
	}
}
