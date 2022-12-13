/*
 * Copyright (c) VMware, Inc. 2022. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.config.xml;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;

import org.apache.geode.cache.CacheListener;
import org.apache.geode.cache.Region;
import org.apache.geode.cache.asyncqueue.AsyncEvent;
import org.apache.geode.cache.asyncqueue.AsyncEventListener;
import org.apache.geode.cache.util.CacheListenerAdapter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.gemfire.tests.integration.IntegrationTestsSupport;
import org.springframework.data.gemfire.tests.unit.annotation.GemFireUnitTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Integration Tests for {@link Region sub-Region}, sub-element SDG XML namespace configuration metadata.
 *
 * @author John Blum
 * @link https://jira.springsource.org/browse/SGF-219
 * @link https://jira.springsource.org/browse/SGF-220
 * @link https://jira.springsource.org/browse/SGF-221
 * @see org.junit.Test
 * @see Region
 * @see IntegrationTestsSupport
 * @see GemFireUnitTest
 * @see org.springframework.test.context.ContextConfiguration
 * @see org.springframework.test.context.junit4.SpringRunner
 * @since 1.3.3
 */
@RunWith(SpringRunner.class)
@GemFireUnitTest
@SuppressWarnings("unused")
public class SubRegionSubElementNamespaceIntegrationTests extends IntegrationTestsSupport {

	@Autowired
	@Qualifier("/Customers/Accounts")
	private Region<?, ?> customersAccountsRegion;

	@Autowired
	@Qualifier("/Parent/Child")
	private Region<?, ?> parentChildRegion;

	@Test
	public void testCustomersAccountsSubRegionCacheListener() {

		assertThat(customersAccountsRegion).isNotNull();
		assertThat(customersAccountsRegion.getAttributes()).isNotNull();
		assertThat(customersAccountsRegion.getAttributes().getCacheListeners()).isNotNull();

		boolean found = false;

		for (CacheListener<?, ?> listener : customersAccountsRegion.getAttributes().getCacheListeners()) {
			found |= (listener instanceof TestNoOpCacheListener);
		}

		assertThat(found)
			.describedAs(String.format("Expected a GemFire CacheListener of type (%1$s) to be registered on Region (%2$s)",
				TestNoOpCacheListener.class.getName(), customersAccountsRegion.getName())).isTrue();
	}

	@Test
	public void testOrderItemsSubRegionGatewaySender() {

		Region<?, ?> orderItemsRegion = requireApplicationContext().getBean("/Orders/Items", Region.class);

		assertThat(orderItemsRegion).isNotNull();
		assertThat(orderItemsRegion.getAttributes()).isNotNull();
		assertThat(orderItemsRegion.getAttributes().getGatewaySenderIds()).isNotNull();
		assertThat(orderItemsRegion.getAttributes().getGatewaySenderIds().contains("testSender")).isTrue();
	}

	@Test
	public void testParentChildSubRegionAsyncEventQueue() {

		assertThat(parentChildRegion).isNotNull();
		assertThat(parentChildRegion.getAttributes()).isNotNull();
		assertThat(parentChildRegion.getAttributes().getAsyncEventQueueIds()).isNotNull();
		assertThat(parentChildRegion.getAttributes().getAsyncEventQueueIds().contains("testQueue")).isTrue();
	}

	public static final class TestNoOpCacheListener extends CacheListenerAdapter<Object, Object> { }

	public static final class TestNoOpAsyncEventListener implements AsyncEventListener {

		@Override
		public boolean processEvents(final List<AsyncEvent> events) {
			throw new UnsupportedOperationException("Not Implemented");
		}

		@Override
		public void close() { }

	}
}
