/*
 * Copyright (c) VMware, Inc. 2022. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.listener.adapter;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;

import org.apache.geode.cache.client.ClientCache;
import org.apache.geode.cache.client.Pool;
import org.apache.geode.cache.query.CqQuery;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.data.gemfire.listener.ContinuousQueryListenerContainer;
import org.springframework.data.gemfire.tests.integration.IntegrationTestsSupport;
import org.springframework.data.gemfire.tests.unit.annotation.GemFireUnitTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Integration Tests for CQ Listener Container
 *
 * @author Costin Leau
 * @author John Blum
 * @see org.junit.Test
 * @see ClientCache
 * @see Pool
 * @see CqQuery
 * @see org.springframework.data.gemfire.fork.CqCacheServerProcess
 * @see ContinuousQueryListenerContainer
 * @see IntegrationTestsSupport
 * @see GemFireUnitTest
 * @see org.springframework.test.context.ContextConfiguration
 * @see org.springframework.test.context.junit4.SpringRunner
 */
@RunWith(SpringRunner.class)
@GemFireUnitTest
@SuppressWarnings("unused")
public class ContainerXmlConfigurationIntegrationTests extends IntegrationTestsSupport {

	@Autowired
	private ApplicationContext applicationContext;

	@Test
	public void containerSetup() {

		ContinuousQueryListenerContainer container =
			applicationContext.getBean(ContinuousQueryListenerContainer.class);

		assertThat(container).isNotNull();
		assertThat(container.isRunning()).isTrue();
		assertThat(container).isSameAs(applicationContext.getBean("testContainerId",
			ContinuousQueryListenerContainer.class));

		ClientCache cache = applicationContext.getBean(ClientCache.class);
		Pool pool = applicationContext.getBean(Pool.class);

		assertThat(cache.getName()).isEqualTo("ContainerXmlConfigurationIntegrationTests");
		assertThat(pool.getName()).isEqualTo("client");

		CqQuery[] cacheCqs = cache.getQueryService().getCqs();
		CqQuery[] poolCqs = pool.getQueryService().getCqs();

		assertThat(pool.getQueryService().getCq("test-bean-1")).isNotNull();
		assertThat(cacheCqs.length).isEqualTo(3);
		assertThat(poolCqs.length).isEqualTo(3);
	}
}
