/*
 * Copyright 2022-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.config.xml;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import org.junit.Test;
import org.junit.runner.RunWith;

import org.apache.geode.cache.DataPolicy;
import org.apache.geode.cache.Region;
import org.apache.geode.cache.RegionAttributes;
import org.apache.geode.cache.asyncqueue.AsyncEventListener;
import org.apache.geode.cache.asyncqueue.AsyncEventQueue;
import org.apache.geode.cache.wan.GatewaySender;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.gemfire.support.AbstractFactoryBeanSupport;
import org.springframework.data.gemfire.tests.integration.IntegrationTestsSupport;
import org.springframework.lang.Nullable;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Integration Tests for SDG lookup {@link Region} resolution configured with an {@link AsyncEventQueue}
 * and {@link GatewaySender} using SDG XML namespace configuration metadata.
 *
 * @author John Blum
 * @see org.apache.geode.cache.Region
 * @see org.apache.geode.cache.asyncqueue.AsyncEventQueue
 * @see org.apache.geode.cache.wan.GatewaySender
 * @see org.springframework.data.gemfire.tests.integration.IntegrationTestsSupport
 * @see org.springframework.test.context.ContextConfiguration
 * @see org.springframework.test.context.junit4.SpringRunner
 * @since 1.0.0
 */
@RunWith(SpringRunner.class)
@ContextConfiguration
@SuppressWarnings("unused")
public class LookupRegionWithAsyncEventQueuesAndGatewaySendersIntegrationTests extends IntegrationTestsSupport {

	@Autowired
	@Qualifier("Example")
	private Region<?, ?> example;

	@Test
	public void regionWithAsyncEventQueuesAndGatewaySenderConfigurationIsCorrect() {

		assertThat(this.example).isNotNull();
		assertThat(this.example.getName()).isEqualTo("Example");

		RegionAttributes<?, ?> exampleAttributes = this.example.getAttributes();

		assertThat(exampleAttributes).isNotNull();
		assertThat(exampleAttributes.getDataPolicy()).isEqualTo(DataPolicy.REPLICATE);

		assertThat(exampleAttributes.getAsyncEventQueueIds()).containsExactlyInAnyOrder(
			"TestAsyncEventQueueZero",
			"TestAsyncEventQueueOne",
			"TestAsyncEventQueueTwo",
			"TestAsyncEventQueueThree",
			"TestAsyncEventQueueFour"
		);

		assertThat(exampleAttributes.getGatewaySenderIds()).containsExactlyInAnyOrder(
			"TestGatewaySenderZero",
			"TestGatewaySenderOne",
			"TestGatewaySenderTwo",
			"TestGatewaySenderThree",
			"TestGatewaySenderFour"
		);
	}

	public static final class AsyncEventListenerFactoryBean extends AbstractFactoryBeanSupport<AsyncEventListener> {

		private final AsyncEventListener mockAsyncEventListener = mock(AsyncEventListener.class);

		@Nullable @Override
		public AsyncEventListener getObject() {
			return this.mockAsyncEventListener;
		}

		@Nullable @Override
		public Class<?> getObjectType() {

			return this.mockAsyncEventListener != null
				? this.mockAsyncEventListener.getClass()
				: AsyncEventListener.class;
		}
	}
}
