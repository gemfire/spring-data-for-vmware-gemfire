/*
 * Copyright (c) VMware, Inc. 2022-2024. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.wan;

import java.util.List;

import org.apache.geode.cache.DataPolicy;
import org.apache.geode.cache.Region;
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
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Integration Tests testing the concurrent, parallel configuration and functional behavior of {@link AsyncEventQueue}
 * and {@link GatewaySender}.
 *
 * @author John Blum
 * @see org.junit.Test
 * @see org.apache.geode.cache.Region
 * @see org.apache.geode.cache.asyncqueue.AsyncEventQueue
 * @see org.apache.geode.cache.wan.GatewaySender
 * @see org.springframework.data.gemfire.wan.AsyncEventQueueFactoryBean
 * @see org.springframework.data.gemfire.wan.GatewaySenderFactoryBean
 * @see org.springframework.data.gemfire.tests.integration.IntegrationTestsSupport
 * @see org.springframework.test.context.ContextConfiguration
 * @see org.springframework.test.context.junit4.SpringRunner
 * @since 1.5.0
 */
@RunWith(SpringRunner.class)
@ContextConfiguration
@SuppressWarnings("unused")
public class CachePartitionRegionWithConcurrentParallelAsyncEventQueueAndGatewaySenderIntegrationTests
		extends IntegrationTestsSupport {

	@Autowired
	private AsyncEventQueue exampleQueue;

	@Autowired
	private GatewaySender exampleGateway;

	@Autowired
	@Qualifier("ExampleRegion")
	private Region<?, ?> exampleRegion;

	@Test
	public void testPartitionRegionWithConcurrentParallelAsyncEventQueueAndGatewaySenderConfiguration() {

		Assertions.assertThat(exampleRegion)
			.describedAs("The 'ExampleRegion' PARTITION Region was not properly configured and initialized")
			.isNotNull();

		Assertions.assertThat(exampleRegion.getName()).isEqualTo("ExampleRegion");
		Assertions.assertThat(exampleRegion.getFullPath()).isEqualTo("/ExampleRegion");
		Assertions.assertThat(exampleRegion.getAttributes()).isNotNull();
		Assertions.assertThat(exampleRegion.getAttributes().getDataPolicy()).isEqualTo(DataPolicy.PARTITION);
		Assertions.assertThat(exampleRegion.getAttributes().getAsyncEventQueueIds().contains("ExampleQueue")).isTrue();
		Assertions.assertThat(exampleRegion.getAttributes().getGatewaySenderIds().contains("ExampleGateway")).isTrue();
	}

	@Test
	public void testConcurrentParallelAsyncEventQueue() {

		Assertions.assertThat(exampleQueue)
			.describedAs("The 'ExampleQueue' AsyncEventQueue was not properly configured and initialized")
			.isNotNull();

		Assertions.assertThat(exampleQueue.getId()).isEqualTo("ExampleQueue");
		Assertions.assertThat(exampleQueue.getAsyncEventListener()).isNotNull();
		Assertions.assertThat(exampleQueue.getDispatcherThreads()).isEqualTo(4);
		Assertions.assertThat(exampleQueue.isParallel()).isTrue();
	}

	@Test
	public void testConcurrentParallelGatewaySender() {

		Assertions.assertThat(exampleGateway)
			.describedAs("The 'ExampleGateway' was not properly configured and initialized")
			.isNotNull();

		Assertions.assertThat(exampleGateway.getId()).isEqualTo("ExampleGateway");
		Assertions.assertThat(exampleGateway.getRemoteDSId()).isEqualTo(123);
		Assertions.assertThat(exampleGateway.getDispatcherThreads()).isEqualTo(8);
		Assertions.assertThat(exampleGateway.isParallel()).isTrue();
		Assertions.assertThat(exampleGateway.isRunning()).isFalse();
	}

	@SuppressWarnings("unused")
	public static final class TestAsyncEventListener implements AsyncEventListener {

		@Override
		public boolean processEvents(final List<AsyncEvent> events) {
			return false;
		}

		@Override
		public void close() { }

	}
}
