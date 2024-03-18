/*
 * Copyright (c) VMware, Inc. 2022-2024. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.wan;

import java.util.List;

import org.apache.geode.cache.asyncqueue.AsyncEvent;
import org.apache.geode.cache.asyncqueue.AsyncEventListener;
import org.apache.geode.cache.asyncqueue.AsyncEventQueue;
import org.apache.geode.cache.wan.GatewaySender;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.gemfire.tests.integration.IntegrationTestsSupport;
import org.springframework.data.gemfire.tests.unit.annotation.GemFireUnitTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/**
 * Integration Tests with test cases testing the circular references between an {@link AsyncEventQueue}
 * and a registered {@link AsyncEventListener} that refers back to the {@link AsyncEventQueue} on which
 * the listener is registered.
 *
 * @author John Blum
 * @see org.junit.Test
 * @see org.apache.geode.cache.asyncqueue.AsyncEventListener
 * @see org.apache.geode.cache.asyncqueue.AsyncEventQueue
 * @see org.springframework.data.gemfire.tests.integration.IntegrationTestsSupport
 * @see org.springframework.data.gemfire.tests.unit.annotation.GemFireUnitTest
 * @see org.springframework.data.gemfire.wan.AsyncEventQueueFactoryBean
 * @see org.springframework.test.context.ContextConfiguration
 * @see org.springframework.test.context.junit4.SpringRunner
 * @since 1.0.0
 */
@RunWith(SpringRunner.class)
@GemFireUnitTest
@SuppressWarnings("unused")
public class AsyncEventQueueWithListenerIntegrationTests extends IntegrationTestsSupport {

	@Autowired
	@Qualifier("Q1")
	private AsyncEventQueue queueOne;

	@Autowired
	@Qualifier("Q2")
	private AsyncEventQueue queueTwo;

	@Autowired
	@Qualifier("Q3")
	private AsyncEventQueue queueThree;

	@Test
	public void testAsyncEventQueueOneAndListenerConfiguration() {

		Assertions.assertThat(queueOne).isNotNull();
		Assertions.assertThat(queueOne.getId()).isEqualTo("QueueOne");
		Assertions.assertThat(queueOne.isPersistent()).isFalse();
		Assertions.assertThat(queueOne.isParallel()).isFalse();
		Assertions.assertThat(queueOne.getMaximumQueueMemory()).isEqualTo(50);
		Assertions.assertThat(queueOne.getDispatcherThreads()).isEqualTo(4);
		Assertions.assertThat(queueOne.getAsyncEventListener() instanceof TestAsyncEventListener).isTrue();
		Assertions.assertThat(((TestAsyncEventListener) queueOne.getAsyncEventListener()).getQueue()).isSameAs(queueOne);
	}
	@Test
	public void testAsyncEventQueueTwoAndListenerConfiguration() {

		Assertions.assertThat(queueTwo).isNotNull();
		Assertions.assertThat(queueTwo.getId()).isEqualTo("QueueTwo");
		Assertions.assertThat(queueTwo.isPersistent()).isFalse();
		Assertions.assertThat(queueTwo.isParallel()).isTrue();
		Assertions.assertThat(queueTwo.getMaximumQueueMemory()).isEqualTo(150);
		Assertions.assertThat(queueTwo.getDispatcherThreads()).isEqualTo(GatewaySender.DEFAULT_DISPATCHER_THREADS);
		Assertions.assertThat(queueTwo.getAsyncEventListener() instanceof TestAsyncEventListener).isTrue();
		Assertions.assertThat(((TestAsyncEventListener) queueTwo.getAsyncEventListener()).getName()).isEqualTo("ListenerTwo");
	}

	@Test
	public void testAsyncEventQueueThreeAndListenerConfiguration() {

		Assertions.assertThat(queueThree).isNotNull();
		Assertions.assertThat(queueThree.getId()).isEqualTo("QueueThree");
		Assertions.assertThat(queueThree.isPersistent()).isFalse();
		Assertions.assertThat(queueThree.isParallel()).isFalse();
		Assertions.assertThat(queueThree.getMaximumQueueMemory()).isEqualTo(25);
		Assertions.assertThat(queueThree.getDispatcherThreads()).isEqualTo(2);
		Assertions.assertThat(queueThree.getAsyncEventListener() instanceof TestAsyncEventListener).isTrue();
		Assertions.assertThat(((TestAsyncEventListener) queueThree.getAsyncEventListener()).getQueue()).isSameAs(queueThree);
	}

	/**
	 * The QueueAsyncEventListener class is an implementation of the AsyncEventListener interface that contains
	 * a reference to the AsyncEventQueue upon which it is registered.
	 *
	 * @see org.apache.geode.cache.asyncqueue.AsyncEvent
	 * @see org.apache.geode.cache.asyncqueue.AsyncEventListener
	 * @see org.apache.geode.cache.asyncqueue.AsyncEventQueue
	 */
	@SuppressWarnings("unused")
	public static class TestAsyncEventListener implements AsyncEventListener {

		private AsyncEventQueue queue;

		private String name;

		public TestAsyncEventListener() {
			this.queue = null;
		}

		public TestAsyncEventListener(AsyncEventQueue queue) {
			this.queue = queue;
		}

		public void init() {
			getQueue();
		}

		public AsyncEventQueue getQueue() {

			Assert.state(queue != null, String.format("A reference to the AsyncEventQueue on which this listener"
				+ " [%s] has been registered was not properly configured", this));

			return queue;
		}

		public String getName() {
			return name;
		}

		public void setName(final String name) {
			this.name = name;
		}

		public void setQueue(final AsyncEventQueue queue) {
			this.queue = queue;
		}

		@Override
		public boolean processEvents(final List<AsyncEvent> events) {
			return false;
		}

		@Override
		public void close() { }

		@Override
		public String toString() {
			return (StringUtils.hasText(getName()) ? getName() : getClass().getName());
		}
	}
}
