/*
 * Copyright (c) VMware, Inc. 2022-2023. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.wan;

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
import org.springframework.data.gemfire.tests.unit.annotation.GemFireUnitTest;
import org.springframework.lang.Nullable;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Unit Tests for {@link Region} with {@link AsyncEventQueue} and {@link GatewaySender}.
 *
 * @author John Blum
 * @see Test
 * @see Region
 * @see AsyncEventQueue
 * @see GatewaySender
 * @see IntegrationTestsSupport
 * @see GemFireUnitTest
 * @see org.springframework.test.context.ContextConfiguration
 * @see SpringRunner
 * @since 2.0.0
 */
@RunWith(SpringRunner.class)
@GemFireUnitTest
@SuppressWarnings("unused")
public class RegionsWithAsyncEventQueuesAndGatewaySendersUnitTests extends IntegrationTestsSupport {

	@Autowired
	@Qualifier("TemplateBasedLocalRegion")
	private Region<?, ?> templateBasedLocalRegion;

	@Autowired
	@Qualifier("LocalRegion")
	private Region<?, ?> localRegion;

	@Autowired
	@Qualifier("PartitionRegion")
	private Region<?, ?> partitionRegion;

	@Autowired
	@Qualifier("ReplicateRegion")
	private Region<?, ?> replicateRegion;

	private void assertRegion(Region<?, ?> region, String name, DataPolicy dataPolicy) {

		assertThat(region).isNotNull();
		assertThat(region.getName()).isEqualTo(name);

		RegionAttributes<?, ?> regionAttributes = region.getAttributes();

		assertThat(regionAttributes).isNotNull();
		assertThat(regionAttributes.getDataPolicy()).isEqualTo(dataPolicy);
	}

	@Test
	public void templateBasedlocalRegionConfigurationIsCorrect() {

		assertRegion(this.templateBasedLocalRegion, "TemplateBasedLocalRegion", DataPolicy.NORMAL);

		assertThat(this.templateBasedLocalRegion.getAttributes().getAsyncEventQueueIds())
			.containsExactlyInAnyOrder("X", "Y", "Z");

		assertThat(this.templateBasedLocalRegion.getAttributes().getGatewaySenderIds())
			.containsExactlyInAnyOrder("99", "100", "101");
	}

	@Test
	public void localRegionConfigurationIsCorrect() {

		assertRegion(this.localRegion, "LocalRegion", DataPolicy.NORMAL);

		assertThat(this.localRegion.getAttributes().getAsyncEventQueueIds())
			.containsExactlyInAnyOrder("A", "B", "C", "D", "E");

		assertThat(this.localRegion.getAttributes().getGatewaySenderIds())
			.containsExactlyInAnyOrder("1", "2", "3", "4", "5");
	}

	@Test
	public void partitionRegionConfigurationIsCorrect() {

		assertRegion(this.partitionRegion, "PartitionRegion", DataPolicy.PARTITION);

		assertThat(this.partitionRegion.getAttributes().getAsyncEventQueueIds())
			.containsExactlyInAnyOrder("E", "F", "G", "H", "I");

		assertThat(this.partitionRegion.getAttributes().getGatewaySenderIds())
			.containsExactlyInAnyOrder("5", "6", "7", "8", "9");
	}

	@Test
	public void sreplicateRegionConfigurationIsCorrect() {

		assertRegion(this.replicateRegion, "ReplicateRegion", DataPolicy.REPLICATE);

		assertThat(this.replicateRegion.getAttributes().getAsyncEventQueueIds())
			.containsExactlyInAnyOrder("E", "J", "K", "L", "M");

		assertThat(this.replicateRegion.getAttributes().getGatewaySenderIds())
			.containsExactlyInAnyOrder("5", "10", "11", "12", "13");
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
