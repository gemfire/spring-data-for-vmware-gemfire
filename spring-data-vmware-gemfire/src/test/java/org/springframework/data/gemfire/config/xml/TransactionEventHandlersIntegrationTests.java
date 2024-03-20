/*
 * Copyright 2022-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.config.xml;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;

import org.apache.geode.cache.Cache;
import org.apache.geode.cache.TransactionEvent;
import org.apache.geode.cache.TransactionListener;
import org.apache.geode.cache.TransactionWriter;

import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.gemfire.tests.integration.IntegrationTestsSupport;
import org.springframework.data.gemfire.tests.unit.annotation.GemFireUnitTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Integration Tests for Apache Geode cache transaction event handlers (listeners) declared in SDG XML namespace
 * configuration metadata.
 *
 * @author David Turanski
 * @author John Blum
 * @see org.junit.Test
 * @see org.apache.geode.cache.Cache
 * @see org.apache.geode.cache.TransactionEvent
 * @see org.apache.geode.cache.TransactionListener
 * @see org.apache.geode.cache.TransactionWriter
 * @see org.springframework.data.gemfire.tests.integration.IntegrationTestsSupport
 * @see org.springframework.data.gemfire.tests.unit.annotation.GemFireUnitTest
 * @see org.springframework.test.context.ContextConfiguration
 * @see org.springframework.test.context.junit4.SpringRunner
 */
@RunWith(SpringRunner.class)
@GemFireUnitTest
@SuppressWarnings("unused")
public class TransactionEventHandlersIntegrationTests extends IntegrationTestsSupport {

	@Autowired
	TestTransactionListener txListener1;

	@Autowired
	private TestTransactionListener txListener2;

	@Autowired
	private TestTransactionWriter txWriter;

	@Autowired
	private Cache cache;

	@Test
	public void transactionEventHandlersConfiguredCorrectly() {

		TransactionListener[] listeners = cache.getCacheTransactionManager().getListeners();

		assertThat(listeners.length).isEqualTo(2);
		assertThat(listeners[0]).isSameAs(txListener1);
		assertThat(listeners[1]).isSameAs(txListener2);
		assertThat(cache.getCacheTransactionManager().getWriter()).isSameAs(txWriter);
	}

	public static class TestTransactionListener implements TransactionListener, BeanNameAware {

		private String name;

		public String value;

		public boolean closed;

		public boolean afterCommit;

		@Override
		public void setBeanName(String name) {
			this.name = name;
		}

		@Override
		public void afterCommit(TransactionEvent event) {
			afterCommit = true;
			value = name;
		}

		@Override
		public void afterFailedCommit(TransactionEvent event) { }

		@Override
		public void afterRollback(TransactionEvent event) { }

		@Override
		public void close() {
			closed = true;
		}
	}

	public static class TestTransactionWriter implements TransactionWriter, BeanNameAware {

		private String name;

		public String value;

		@Override
		public void setBeanName(String name) {
			this.name = name;
		}

		@Override
		public void beforeCommit(TransactionEvent event) {
			this.value = name;
		}

		@Override
		public void close() { }

	}
}
