/*
 * Copyright 2022-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.wan;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import org.apache.geode.cache.Cache;
import org.apache.geode.cache.asyncqueue.AsyncEventListener;
import org.apache.geode.cache.asyncqueue.AsyncEventQueue;
import org.apache.geode.cache.asyncqueue.AsyncEventQueueFactory;
import org.apache.geode.cache.wan.GatewayEventFilter;
import org.apache.geode.cache.wan.GatewayEventSubstitutionFilter;
import org.apache.geode.cache.wan.GatewaySender;

import org.springframework.data.gemfire.TestUtils;

/**
 * Unit Tests for {@link AsyncEventQueueFactoryBean}.
 *
 * @author John Blum
 * @see org.junit.Test
 * @see org.mockito.Mockito
 * @see Cache
 * @see AsyncEventListener
 * @see AsyncEventQueue
 * @see AsyncEventQueueFactory
 * @see GatewayEventFilter
 * @see GatewayEventSubstitutionFilter
 * @see GatewaySender
 * @see TestUtils
 * @see AsyncEventQueueFactoryBean
 * @since 1.3.3
 */
@RunWith(MockitoJUnitRunner.class)
public class AsyncEventQueueFactoryBeanUnitTests {

	@Mock
	private Cache mockCache;

	private Cache mockCache(AsyncEventQueueFactory mockAsyncEventQueueFactory) {

		when(this.mockCache.createAsyncEventQueueFactory()).thenReturn(mockAsyncEventQueueFactory);

		return this.mockCache;
	}

	private AsyncEventListener mockAsyncEventListener() {
		return mock(AsyncEventListener.class);
	}

	private AsyncEventQueue mockAsyncEventQueue(String asyncEventQueueId) {

		AsyncEventQueue mockAsyncEventQueue = mock(AsyncEventQueue.class);

		when(mockAsyncEventQueue.getId()).thenReturn(asyncEventQueueId);

		return mockAsyncEventQueue;
	}

	private AsyncEventQueueFactory mockAsyncEventQueueFactory(String asyncEventQueueId) {

		AsyncEventQueueFactory mockAsyncEventQueueFactory = mock(AsyncEventQueueFactory.class);

		AsyncEventQueue mockAsyncEventQueue = mockAsyncEventQueue(asyncEventQueueId);

		when(mockAsyncEventQueueFactory.create(eq(asyncEventQueueId), isA(AsyncEventListener.class)))
			.thenReturn(mockAsyncEventQueue);

		return mockAsyncEventQueueFactory;
	}

	@Test
	public void setAndGetAsyncEventListener() throws Exception {

		AsyncEventQueueFactoryBean factoryBean = new AsyncEventQueueFactoryBean(this.mockCache);

		AsyncEventListener listenerOne = mockAsyncEventListener();

		factoryBean.setAsyncEventListener(listenerOne);

		assertThat(TestUtils.<AsyncEventListener>readField("asyncEventListener", factoryBean))
			.isSameAs(listenerOne);

		AsyncEventListener listenerTwo = mockAsyncEventListener();

		factoryBean.setAsyncEventListener(listenerTwo);

		assertThat(TestUtils.<AsyncEventListener>readField("asyncEventListener", factoryBean))
			.isSameAs(listenerTwo);
	}

	@Test(expected = IllegalStateException.class)
	public void setAsyncEventListenerAfterAsyncEventQueueCreationThrowsIllegalStateException() throws Exception {

		AsyncEventListener mockAsyncEventListener = mockAsyncEventListener();

		AsyncEventQueue mockAsyncEventQueue = mockAsyncEventQueue("testEventQueue");

		AsyncEventQueueFactoryBean factoryBean = new AsyncEventQueueFactoryBean(this.mockCache, mockAsyncEventListener);

		factoryBean.setAsyncEventQueue(mockAsyncEventQueue);

		try {
			factoryBean.setAsyncEventListener(mockAsyncEventListener());
		}
		catch (IllegalStateException expected) {

			assertThat(expected)
				.hasMessage("Setting an AsyncEventListener is not allowed once the AsyncEventQueue has been created");

			assertThat(expected).hasNoCause();

			throw expected;
		}
		finally {
			assertThat(TestUtils.<AsyncEventListener>readField("asyncEventListener", factoryBean))
				.isSameAs(mockAsyncEventListener);
		}
	}

	@Test
	public void setAndGetAsyncEventQueue() {

		AsyncEventQueueFactoryBean factoryBean = new AsyncEventQueueFactoryBean(this.mockCache);

		assertThat(factoryBean.getAsyncEventQueue()).isNull();

		AsyncEventQueue mockAsyncEventQueue = mockAsyncEventQueue("123");

		factoryBean.setAsyncEventQueue(mockAsyncEventQueue);

		assertThat(factoryBean.getAsyncEventQueue()).isEqualTo(mockAsyncEventQueue);
	}

	@Test
	public void setAndGetPauseEventDispatching() {

		AsyncEventQueueFactoryBean factoryBean = new AsyncEventQueueFactoryBean(this.mockCache);

		assertThat(factoryBean.isPauseEventDispatching()).isFalse();

		factoryBean.setPauseEventDispatching(true);

		assertThat(factoryBean.isPauseEventDispatching()).isTrue();

		factoryBean.setPauseEventDispatching(false);

		assertThat(factoryBean.isPauseEventDispatching()).isFalse();
	}

	@Test
	public void doInitConfiguresAsyncEventQueue() throws Exception {

		AsyncEventListener mockListener = mockAsyncEventListener();

		AsyncEventQueueFactory mockAsyncEventQueueFactory = mockAsyncEventQueueFactory("testQueue");

		AsyncEventQueueFactoryBean factoryBean =
			new AsyncEventQueueFactoryBean(mockCache(mockAsyncEventQueueFactory), mockListener);

		GatewayEventFilter mockGatewayEventFilterOne = mock(GatewayEventFilter.class);
		GatewayEventFilter mockGatewayEventFilterTwo = mock(GatewayEventFilter.class);

		GatewayEventSubstitutionFilter mockGatewayEventSubstitutionFilter = mock(GatewayEventSubstitutionFilter.class);

		factoryBean.setBatchConflationEnabled(true);
		factoryBean.setBatchSize(1024);
		factoryBean.setBatchTimeInterval(600);
		factoryBean.setDiskStoreRef("testDiskStore");
		factoryBean.setDiskSynchronous(false);
		factoryBean.setDispatcherThreads(2);
		factoryBean.setForwardExpirationDestroy(true);
		factoryBean.setGatewayEventFilters(Arrays.asList(mockGatewayEventFilterOne, mockGatewayEventFilterTwo));
		factoryBean.setGatewayEventSubstitutionFilter(mockGatewayEventSubstitutionFilter);
		factoryBean.setMaximumQueueMemory(8192);
		factoryBean.setName("testQueue");
		factoryBean.setOrderPolicy(GatewaySender.OrderPolicy.PARTITION);
		factoryBean.setParallel(false);
		factoryBean.setPersistent(false);
		factoryBean.doInit();

		verify(mockAsyncEventQueueFactory, times(1)).setBatchConflationEnabled(eq(true));
		verify(mockAsyncEventQueueFactory, times(1)).setBatchSize(eq(1024));
		verify(mockAsyncEventQueueFactory, times(1)).setBatchTimeInterval(eq(600));
		verify(mockAsyncEventQueueFactory, times(1)).setDiskStoreName(eq("testDiskStore"));
		verify(mockAsyncEventQueueFactory, times(1)).setDiskSynchronous(eq(false));
		verify(mockAsyncEventQueueFactory, times(1)).setDispatcherThreads(eq(2));
		verify(mockAsyncEventQueueFactory, times(1)).setForwardExpirationDestroy(eq(true));
		verify(mockAsyncEventQueueFactory, times(1)).setGatewayEventSubstitutionListener(eq(mockGatewayEventSubstitutionFilter));
		verify(mockAsyncEventQueueFactory, times(1)).setMaximumQueueMemory(eq(8192));
		verify(mockAsyncEventQueueFactory, times(1)).setOrderPolicy(eq(GatewaySender.OrderPolicy.PARTITION));
		verify(mockAsyncEventQueueFactory, times(1)).setParallel(eq(false));
		verify(mockAsyncEventQueueFactory, times(1)).setPersistent(eq(false));
		verify(mockAsyncEventQueueFactory, times(1)).addGatewayEventFilter(eq(mockGatewayEventFilterOne));
		verify(mockAsyncEventQueueFactory, times(1)).addGatewayEventFilter(eq(mockGatewayEventFilterTwo));
		verify(mockAsyncEventQueueFactory, never()).pauseEventDispatching();

		AsyncEventQueue asyncEventQueue = factoryBean.getObject();

		assertThat(asyncEventQueue).isNotNull();
		assertThat(asyncEventQueue.getId()).isEqualTo("testQueue");
	}

	@Test
	public void doInitConfiguresConcurrentParallelAsyncEventQueue() throws Exception {

		AsyncEventQueueFactory mockAsyncEventQueueFactory =
			mockAsyncEventQueueFactory("concurrentParallelQueue");

		AsyncEventQueueFactoryBean factoryBean =
			new AsyncEventQueueFactoryBean(mockCache(mockAsyncEventQueueFactory));

		factoryBean.setAsyncEventListener(mockAsyncEventListener());
		factoryBean.setDispatcherThreads(8);
		factoryBean.setName("concurrentParallelQueue");
		factoryBean.setParallel(true);
		factoryBean.doInit();

		verify(mockAsyncEventQueueFactory, never()).setBatchConflationEnabled(anyBoolean());
		verify(mockAsyncEventQueueFactory, never()).setBatchSize(anyInt());
		verify(mockAsyncEventQueueFactory, never()).setBatchTimeInterval(anyInt());
		verify(mockAsyncEventQueueFactory, never()).setDiskStoreName(anyString());
		verify(mockAsyncEventQueueFactory, never()).setDiskSynchronous(anyBoolean());
		verify(mockAsyncEventQueueFactory, times(1)).setDispatcherThreads(eq(8));
		verify(mockAsyncEventQueueFactory, never()).setForwardExpirationDestroy(anyBoolean());
		verify(mockAsyncEventQueueFactory, never()).setGatewayEventSubstitutionListener(any());
		verify(mockAsyncEventQueueFactory, never()).setMaximumQueueMemory(anyInt());
		verify(mockAsyncEventQueueFactory, never()).setOrderPolicy(any());
		verify(mockAsyncEventQueueFactory, times(1)).setParallel(eq(true));
		verify(mockAsyncEventQueueFactory, never()).setPersistent(anyBoolean());
		verify(mockAsyncEventQueueFactory, never()).addGatewayEventFilter(any());
		verify(mockAsyncEventQueueFactory, never()).pauseEventDispatching();

		AsyncEventQueue asyncEventQueue = factoryBean.getObject();

		assertThat(asyncEventQueue).isNotNull();
		assertThat(asyncEventQueue.getId()).isEqualTo("concurrentParallelQueue");
	}

	@Test
	public void doInitConfiguresParallelAsyncEventQueue() throws Exception {

		AsyncEventQueueFactory mockAsyncEventQueueFactory =
			mockAsyncEventQueueFactory("parallelQueue");

		AsyncEventQueueFactoryBean factoryBean =
			new AsyncEventQueueFactoryBean(mockCache(mockAsyncEventQueueFactory));

		factoryBean.setAsyncEventListener(mockAsyncEventListener());
		factoryBean.setName("parallelQueue");
		factoryBean.setParallel(true);
		factoryBean.doInit();

		verify(mockAsyncEventQueueFactory, never()).setBatchConflationEnabled(anyBoolean());
		verify(mockAsyncEventQueueFactory, never()).setBatchSize(anyInt());
		verify(mockAsyncEventQueueFactory, never()).setBatchTimeInterval(anyInt());
		verify(mockAsyncEventQueueFactory, never()).setDiskStoreName(anyString());
		verify(mockAsyncEventQueueFactory, never()).setDiskSynchronous(anyBoolean());
		verify(mockAsyncEventQueueFactory, never()).setDispatcherThreads(anyInt());
		verify(mockAsyncEventQueueFactory, never()).setForwardExpirationDestroy(anyBoolean());
		verify(mockAsyncEventQueueFactory, never()).setGatewayEventSubstitutionListener(any());
		verify(mockAsyncEventQueueFactory, never()).setMaximumQueueMemory(anyInt());
		verify(mockAsyncEventQueueFactory, never()).setOrderPolicy(any());
		verify(mockAsyncEventQueueFactory, times(1)).setParallel(eq(true));
		verify(mockAsyncEventQueueFactory, never()).setPersistent(anyBoolean());
		verify(mockAsyncEventQueueFactory, never()).addGatewayEventFilter(any());
		verify(mockAsyncEventQueueFactory, never()).pauseEventDispatching();

		AsyncEventQueue asyncEventQueue = factoryBean.getObject();

		assertThat(asyncEventQueue).isNotNull();
		assertThat(asyncEventQueue.getId()).isEqualTo("parallelQueue");
	}

	@Test
	public void doInitConfiguresConcurrentSerialAsyncEventQueue() throws Exception {

		AsyncEventQueueFactory mockAsyncEventQueueFactory =
			mockAsyncEventQueueFactory("concurrentSerialQueue");

		AsyncEventQueueFactoryBean factoryBean =
			new AsyncEventQueueFactoryBean(mockCache(mockAsyncEventQueueFactory));

		factoryBean.setAsyncEventListener(mockAsyncEventListener());
		factoryBean.setDispatcherThreads(16);
		factoryBean.setName("concurrentSerialQueue");
		factoryBean.setParallel(false);
		factoryBean.doInit();

		verify(mockAsyncEventQueueFactory, never()).setBatchConflationEnabled(anyBoolean());
		verify(mockAsyncEventQueueFactory, never()).setBatchSize(anyInt());
		verify(mockAsyncEventQueueFactory, never()).setBatchTimeInterval(anyInt());
		verify(mockAsyncEventQueueFactory, never()).setDiskStoreName(anyString());
		verify(mockAsyncEventQueueFactory, never()).setDiskSynchronous(anyBoolean());
		verify(mockAsyncEventQueueFactory, times(1)).setDispatcherThreads(eq(16));
		verify(mockAsyncEventQueueFactory, never()).setForwardExpirationDestroy(anyBoolean());
		verify(mockAsyncEventQueueFactory, never()).setGatewayEventSubstitutionListener(any());
		verify(mockAsyncEventQueueFactory, never()).setMaximumQueueMemory(anyInt());
		verify(mockAsyncEventQueueFactory, never()).setOrderPolicy(any());
		verify(mockAsyncEventQueueFactory, times(1)).setParallel(eq(false));
		verify(mockAsyncEventQueueFactory, never()).setPersistent(anyBoolean());
		verify(mockAsyncEventQueueFactory, never()).addGatewayEventFilter(any());
		verify(mockAsyncEventQueueFactory, never()).pauseEventDispatching();

		AsyncEventQueue asyncEventQueue = factoryBean.getObject();

		assertThat(asyncEventQueue).isNotNull();
		assertThat(asyncEventQueue.getId()).isEqualTo("concurrentSerialQueue");
	}

	@Test
	public void doInitConfiguresSerialAsyncEventQueue() throws Exception {

		AsyncEventQueueFactory mockAsyncEventQueueFactory = mockAsyncEventQueueFactory("serialQueue");

		AsyncEventQueueFactoryBean factoryBean =
			new AsyncEventQueueFactoryBean(mockCache(mockAsyncEventQueueFactory));

		factoryBean.setAsyncEventListener(mockAsyncEventListener());
		factoryBean.setName("serialQueue");
		factoryBean.doInit();

		verify(mockAsyncEventQueueFactory, never()).setBatchConflationEnabled(anyBoolean());
		verify(mockAsyncEventQueueFactory, never()).setBatchSize(anyInt());
		verify(mockAsyncEventQueueFactory, never()).setBatchTimeInterval(anyInt());
		verify(mockAsyncEventQueueFactory, never()).setDiskStoreName(anyString());
		verify(mockAsyncEventQueueFactory, never()).setDiskSynchronous(anyBoolean());
		verify(mockAsyncEventQueueFactory, never()).setDispatcherThreads(anyInt());
		verify(mockAsyncEventQueueFactory, never()).setForwardExpirationDestroy(anyBoolean());
		verify(mockAsyncEventQueueFactory, never()).setGatewayEventSubstitutionListener(any());
		verify(mockAsyncEventQueueFactory, never()).setMaximumQueueMemory(anyInt());
		verify(mockAsyncEventQueueFactory, never()).setOrderPolicy(any());
		verify(mockAsyncEventQueueFactory, times(1)).setParallel(eq(false));
		verify(mockAsyncEventQueueFactory, never()).setPersistent(anyBoolean());
		verify(mockAsyncEventQueueFactory, never()).addGatewayEventFilter(any());
		verify(mockAsyncEventQueueFactory, never()).pauseEventDispatching();

		AsyncEventQueue asyncEventQueue = factoryBean.getObject();

		assertThat(asyncEventQueue).isNotNull();
		assertThat(asyncEventQueue.getId()).isEqualTo("serialQueue");
	}

	@Test
	public void doInitConfiguresSerialAsyncEventQueueWithOrderPolicy() throws Exception {

		AsyncEventQueueFactory mockAsyncEventQueueFactory =
			mockAsyncEventQueueFactory("orderedSerialQueue");

		AsyncEventQueueFactoryBean factoryBean =
			new AsyncEventQueueFactoryBean(mockCache(mockAsyncEventQueueFactory));

		factoryBean.setAsyncEventListener(mockAsyncEventListener());
		factoryBean.setName("orderedSerialQueue");
		factoryBean.setOrderPolicy(GatewaySender.OrderPolicy.THREAD);
		factoryBean.doInit();

		verify(mockAsyncEventQueueFactory, never()).setBatchConflationEnabled(anyBoolean());
		verify(mockAsyncEventQueueFactory, never()).setBatchSize(anyInt());
		verify(mockAsyncEventQueueFactory, never()).setBatchTimeInterval(anyInt());
		verify(mockAsyncEventQueueFactory, never()).setDiskStoreName(anyString());
		verify(mockAsyncEventQueueFactory, never()).setDiskSynchronous(anyBoolean());
		verify(mockAsyncEventQueueFactory, never()).setDispatcherThreads(anyInt());
		verify(mockAsyncEventQueueFactory, never()).setForwardExpirationDestroy(anyBoolean());
		verify(mockAsyncEventQueueFactory, never()).setGatewayEventSubstitutionListener(any());
		verify(mockAsyncEventQueueFactory, never()).setMaximumQueueMemory(anyInt());
		verify(mockAsyncEventQueueFactory, times(1)).setOrderPolicy(eq(GatewaySender.OrderPolicy.THREAD));
		verify(mockAsyncEventQueueFactory, times(1)).setParallel(eq(false));
		verify(mockAsyncEventQueueFactory, never()).setPersistent(anyBoolean());
		verify(mockAsyncEventQueueFactory, never()).addGatewayEventFilter(any());
		verify(mockAsyncEventQueueFactory, never()).pauseEventDispatching();

		AsyncEventQueue asyncEventQueue = factoryBean.getObject();

		assertThat(asyncEventQueue).isNotNull();
		assertThat(asyncEventQueue.getId()).isEqualTo("orderedSerialQueue");
	}

	@Test(expected = IllegalStateException.class)
	public void doInitWithNullAsyncEventListenerThrowsIllegalStateException() {

		try {
			new AsyncEventQueueFactoryBean(this.mockCache, null).doInit();
		}
		catch (IllegalStateException expected) {

			assertThat(expected).hasMessage("AsyncEventListener must not be null");
			assertThat(expected).hasNoCause();

			throw expected;
		}
	}

	@Test(expected = IllegalStateException.class)
	public void doInitWithParallelAsyncEventQueueHavingAnOrderPolicyThrowsIllegalStateException() throws Exception {

		AsyncEventQueueFactory mockAsyncEventQueueFactory =
			mockAsyncEventQueueFactory("parallelQueue");

		AsyncEventQueueFactoryBean factoryBean =
			new AsyncEventQueueFactoryBean(mockCache(mockAsyncEventQueueFactory));

		factoryBean.setAsyncEventListener(mockAsyncEventListener());
		factoryBean.setName("parallelQueue");
		factoryBean.setOrderPolicy(GatewaySender.OrderPolicy.KEY);
		factoryBean.setParallel(true);

		try {
			factoryBean.doInit();
		}
		catch (IllegalStateException expected) {

			assertThat(expected).hasMessage("OrderPolicy cannot be used with a Parallel AsyncEventQueue");
			assertThat(expected).hasNoCause();

			throw expected;
		}
		finally {

			assertThat(factoryBean.getObject()).isNull();

			verify(mockAsyncEventQueueFactory, never()).setBatchConflationEnabled(anyBoolean());
			verify(mockAsyncEventQueueFactory, never()).setBatchSize(anyInt());
			verify(mockAsyncEventQueueFactory, never()).setBatchTimeInterval(anyInt());
			verify(mockAsyncEventQueueFactory, never()).setDiskStoreName(anyString());
			verify(mockAsyncEventQueueFactory, never()).setDiskSynchronous(anyBoolean());
			verify(mockAsyncEventQueueFactory, never()).setDispatcherThreads(eq(8));
			verify(mockAsyncEventQueueFactory, never()).setForwardExpirationDestroy(anyBoolean());
			verify(mockAsyncEventQueueFactory, never()).setGatewayEventSubstitutionListener(any());
			verify(mockAsyncEventQueueFactory, never()).setMaximumQueueMemory(anyInt());
			verify(mockAsyncEventQueueFactory, never()).setOrderPolicy(any());
			verify(mockAsyncEventQueueFactory, times(1)).setParallel(eq(true));
			verify(mockAsyncEventQueueFactory, never()).setPersistent(anyBoolean());
			verify(mockAsyncEventQueueFactory, never()).addGatewayEventFilter(any());
			verify(mockAsyncEventQueueFactory, never()).pauseEventDispatching();
		}
	}

	@Test
	public void doInitConfiguresAsyncEventQueueWithSynchronousOverflowNonPersistentDiskStorePausingEventDispatching()
			throws Exception {

		AsyncEventQueueFactory mockAsyncEventQueueFactory =
			mockAsyncEventQueueFactory("SynchronousOverflowNonPersistentQueue");

		AsyncEventQueueFactoryBean factoryBean =
			new AsyncEventQueueFactoryBean(mockCache(mockAsyncEventQueueFactory));

		factoryBean.setAsyncEventListener(mockAsyncEventListener());
		factoryBean.setDiskStoreRef("queueOverflowDiskStore");
		factoryBean.setDiskSynchronous(true);
		factoryBean.setName("SynchronousOverflowNonPersistentQueue");
		factoryBean.setOrderPolicy(GatewaySender.OrderPolicy.KEY);
		factoryBean.setPersistent(false);
		factoryBean.setPauseEventDispatching(true);
		factoryBean.doInit();

		verify(mockAsyncEventQueueFactory, never()).setBatchConflationEnabled(anyBoolean());
		verify(mockAsyncEventQueueFactory, never()).setBatchSize(anyInt());
		verify(mockAsyncEventQueueFactory, never()).setBatchTimeInterval(anyInt());
		verify(mockAsyncEventQueueFactory, times(1)).setDiskStoreName("queueOverflowDiskStore");
		verify(mockAsyncEventQueueFactory, times(1)).setDiskSynchronous(eq(true));
		verify(mockAsyncEventQueueFactory, never()).setDispatcherThreads(anyInt());
		verify(mockAsyncEventQueueFactory, never()).setForwardExpirationDestroy(anyBoolean());
		verify(mockAsyncEventQueueFactory, never()).setGatewayEventSubstitutionListener(any());
		verify(mockAsyncEventQueueFactory, never()).setMaximumQueueMemory(anyInt());
		verify(mockAsyncEventQueueFactory, times(1)).setOrderPolicy(eq(GatewaySender.OrderPolicy.KEY));
		verify(mockAsyncEventQueueFactory, times(1)).setParallel(eq(false));
		verify(mockAsyncEventQueueFactory, times(1)).setPersistent(eq(false));
		verify(mockAsyncEventQueueFactory, never()).addGatewayEventFilter(any());
		verify(mockAsyncEventQueueFactory, times(1)).pauseEventDispatching();

		AsyncEventQueue asyncEventQueue = factoryBean.getObject();

		assertThat(asyncEventQueue).isNotNull();
		assertThat(asyncEventQueue.getId()).isEqualTo("SynchronousOverflowNonPersistentQueue");
	}
}
