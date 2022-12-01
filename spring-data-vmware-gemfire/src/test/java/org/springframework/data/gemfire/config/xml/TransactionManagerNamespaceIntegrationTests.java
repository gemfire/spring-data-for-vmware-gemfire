/*
 * Copyright (c) VMware, Inc. 2022. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.config.xml;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.data.gemfire.tests.integration.IntegrationTestsSupport;
import org.springframework.data.gemfire.tests.unit.annotation.GemFireUnitTest;
import org.springframework.data.gemfire.transaction.GemfireTransactionManager;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Integration Tests for SDG Transaction Manager configuration in SDG XML namespace configuration metadata.
 *
 * @author Costin Leau
 * @author John Blum
 * @see Test
 * @see org.springframework.data.gemfire.CacheFactoryBean
 * @see GemfireTransactionManager
 * @see IntegrationTestsSupport
 * @see GemFireUnitTest
 * @see org.springframework.test.context.ContextConfiguration
 * @see SpringRunner
 */
@RunWith(SpringRunner.class)
@GemFireUnitTest
@SuppressWarnings("unused")
public class TransactionManagerNamespaceIntegrationTests extends IntegrationTestsSupport {

	@Test
	public void basicCacheWithTransactionsIsConfiguredCorrectly() {

		assertThat(requireApplicationContext().containsBean("gemfireTransactionManager")).isTrue();
		assertThat(requireApplicationContext().containsBean("gemfire-transaction-manager")).isTrue();

		GemfireTransactionManager transactionManager =
			requireApplicationContext().getBean("gemfireTransactionManager", GemfireTransactionManager.class);

		assertThat(transactionManager.isCopyOnRead()).isFalse();
	}
}
