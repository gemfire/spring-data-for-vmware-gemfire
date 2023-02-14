/*
 * Copyright (c) VMware, Inc. 2022-2023. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.config.xml;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.stream.Collectors;

import org.junit.Test;
import org.junit.runner.RunWith;

import org.apache.geode.cache.EntryEvent;
import org.apache.geode.cache.asyncqueue.AsyncEvent;
import org.apache.geode.cache.asyncqueue.AsyncEventListener;
import org.apache.geode.cache.asyncqueue.AsyncEventQueue;
import org.apache.geode.cache.wan.GatewayEventFilter;
import org.apache.geode.cache.wan.GatewayEventSubstitutionFilter;
import org.apache.geode.cache.wan.GatewayQueueEvent;
import org.apache.geode.cache.wan.GatewaySender;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.gemfire.tests.integration.IntegrationTestsSupport;
import org.springframework.data.gemfire.tests.unit.annotation.GemFireUnitTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Integration Tests testing the contract and functionality of an {@link AsyncEventQueue}
 * using SDG XML namespace configuration metadata.
 *
 * @author John Blum
 * @see org.junit.Test
 * @see org.apache.geode.cache.asyncqueue.AsyncEventQueue
 * @see org.springframework.data.gemfire.config.AsyncEventQueueParser
 * @see org.springframework.data.gemfire.tests.integration.IntegrationTestsSupport
 * @see org.springframework.data.gemfire.tests.unit.annotation.GemFireUnitTest
 * @see org.springframework.data.gemfire.wan.AsyncEventQueueFactoryBean
 * @see org.springframework.test.context.ContextConfiguration
 * @see org.springframework.test.context.junit4.SpringJUnit4ClassRunner
 * @since 1.0.0
 */
@RunWith(SpringRunner.class)
@GemFireUnitTest
@SuppressWarnings("all")
public class AsyncEventQueueNamespaceIntegrationTests extends IntegrationTestsSupport {

	@Autowired
	@Qualifier("TestAsyncEventQueue")
	private AsyncEventQueue asyncEventQueue;

	@Autowired
	@Qualifier("TestAsyncEventQueueWithFilters")
	private AsyncEventQueue asyncEventQueueWithFilters;

	@Autowired
	@Qualifier("TestPausedAsyncEventQueue")
	private AsyncEventQueue pausedAsyncEventQueue;

	@Test
	public void asyncEventQueueIsConfiguredProperly() {

		assertThat(asyncEventQueue).isNotNull();
		assertThat(asyncEventQueue.getId()).isEqualTo("TestAsyncEventQueue");
		assertThat(asyncEventQueue.isBatchConflationEnabled()).isTrue();
		assertThat(asyncEventQueue.getBatchSize()).isEqualTo(100);
		assertThat(asyncEventQueue.getBatchTimeInterval()).isEqualTo(30);
		assertThat(asyncEventQueue.getDiskStoreName()).isEqualTo("TestDiskStore");
		assertThat(asyncEventQueue.isDiskSynchronous()).isTrue();
		assertThat(asyncEventQueue.getDispatcherThreads()).isEqualTo(4);
		assertThat(asyncEventQueue.isDispatchingPaused()).isFalse();
		assertThat(asyncEventQueue.isForwardExpirationDestroy()).isTrue();
		assertThat(asyncEventQueue.getMaximumQueueMemory()).isEqualTo(50);
		assertThat(asyncEventQueue.getOrderPolicy()).isEqualTo(GatewaySender.OrderPolicy.KEY);
		assertThat(asyncEventQueue.isParallel()).isFalse();
		assertThat(asyncEventQueue.isPersistent()).isTrue();
	}

	@Test
	public void asyncEventQueueListenerEqualsExpected() {

		AsyncEventListener asyncEventListener = asyncEventQueue.getAsyncEventListener();

		assertThat(asyncEventListener).isNotNull();
		assertThat(asyncEventListener.toString()).isEqualTo("TestAeqListener");
	}

	@Test
	public void asyncEventQueueWithFiltersIsConfiguredProperly() {

		assertThat(asyncEventQueueWithFilters).isNotNull();
		assertThat(asyncEventQueueWithFilters.getId()).isEqualTo("TestAsyncEventQueueWithFilters");
		assertThat(asyncEventQueueWithFilters.isDispatchingPaused()).isFalse();

		AsyncEventListener listener = asyncEventQueueWithFilters.getAsyncEventListener();

		assertThat(listener).isNotNull();
		assertThat(listener.toString()).isEqualTo("TestListenerOne");

		List<GatewayEventFilter> gatewayEventFilters = asyncEventQueueWithFilters.getGatewayEventFilters();

		assertThat(gatewayEventFilters).isNotNull();
		assertThat(gatewayEventFilters).hasSize(2);
		assertThat(gatewayEventFilters.stream().map(Object::toString).collect(Collectors.toList()))
			.containsExactly("GatewayEventFilterOne", "GatewayEventFilterTwo");

		GatewayEventSubstitutionFilter<?, ?> gatewayEventSubstitutionFilter =
			asyncEventQueueWithFilters.getGatewayEventSubstitutionFilter();

		assertThat(gatewayEventSubstitutionFilter).isNotNull();
		assertThat(gatewayEventSubstitutionFilter.toString()).isEqualTo("GatewayEventSubstitutionFilterOne");
	}

	@Test
	public void pausedAsyncEventQueueIsConfiguredProperly() {

		assertThat(pausedAsyncEventQueue).isNotNull();
		assertThat(pausedAsyncEventQueue.getId()).isEqualTo("TestPausedAsyncEventQueue");
		assertThat(pausedAsyncEventQueue.isDispatchingPaused()).isTrue();
	}

	public static class TestAsyncEventListener implements AsyncEventListener {

		private final String name;

		public TestAsyncEventListener(String name) {
			this.name = name;
		}

		@Override
		public boolean processEvents(List<AsyncEvent> events) {
			return false;
		}

		@Override
		public void close() {
		}

		@Override
		public String toString() {
			return this.name;
		}
	}

	public static class TestGatewayEventFilter implements GatewayEventFilter {

		private final String name;

		public TestGatewayEventFilter(String name) {
			this.name = name;
		}

		@Override
		public boolean beforeEnqueue(GatewayQueueEvent event) {
			return false;
		}

		@Override
		public boolean beforeTransmit(GatewayQueueEvent event) {
			return false;
		}

		@Override
		public void afterAcknowledgement(GatewayQueueEvent event) { }

		@Override
		public void close() { }

		@Override
		public String toString() {
			return this.name;
		}
	}

	public static class TestGatewayEventSubstitutionFilter implements GatewayEventSubstitutionFilter<Object, Object> {

		private final String name;

		public TestGatewayEventSubstitutionFilter(String name) {
			this.name = name;
		}

		@Override
		public Object getSubstituteValue(EntryEvent<Object, Object> event) {
			return null;
		}

		@Override
		public void close() { }

		@Override
		public String toString() {
			return this.name;
		}
	}
}
