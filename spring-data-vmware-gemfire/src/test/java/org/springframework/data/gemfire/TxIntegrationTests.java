/*
 * Copyright (c) VMware, Inc. 2022-2023. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.gemfire.tests.integration.IntegrationTestsSupport;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.transaction.AfterTransaction;
import org.springframework.test.context.transaction.BeforeTransaction;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration Test for Apache Geode cache transactions
 *
 * @author Costin Leau
 * @author John Blum
 * @see org.junit.Test
 * @see org.apache.geode.cache.GemFireCache
 * @see org.springframework.data.gemfire.tests.integration.IntegrationTestsSupport
 * @see org.springframework.test.context.ContextConfiguration
 * @see org.springframework.test.context.junit4.SpringRunner
 */
@RunWith(SpringRunner.class)
@ContextConfiguration(locations = { "basic-tx-config.xml" })
@Transactional
@SuppressWarnings("unused")
public class TxIntegrationTests extends IntegrationTestsSupport {

	@Autowired
	@Qualifier("rollback-region")
	private Map<String, String> rollbackRegion;

	@Autowired
	@Qualifier("commit-region")
	private Map<String, String> commitRegion;

	private boolean txCommit = false;

	@BeforeTransaction
	public void addItemsToTheCache() {

		rollbackRegion.put("Vlaicu", "Aurel");
		rollbackRegion.put("Coanda", "Henri");
		commitRegion.put("Coanda", "Henri");
		commitRegion.put("Vlaicu", "Aurel");
		txCommit = false;
	}

	@Test
	public void testTransactionRollback() {

		rollbackRegion.remove("Coanda");
		rollbackRegion.put("Ciurcu", "Alexandru");
	}

	@Test
	@Rollback(value = false)
	public void testTransactionCommit()  {

		commitRegion.put("Poenaru", "Petrache");
		commitRegion.remove("Coanda");
		commitRegion.put("Vlaicu", "A");

		txCommit = true;
	}

	@AfterTransaction
	public void testTxOutcome() {

		if (txCommit) {
			assertThat(commitRegion.containsKey("Coanda")).isFalse();
			assertThat(commitRegion.containsKey("Poenaru")).isTrue();
			assertThat(commitRegion.containsValue("A")).isTrue();
		}

		assertThat(rollbackRegion.containsKey("Coanda")).isTrue();
		assertThat(rollbackRegion.containsKey("Vlaicu")).isTrue();
		assertThat(rollbackRegion.containsKey("Ciurcu")).isFalse();
	}
}
