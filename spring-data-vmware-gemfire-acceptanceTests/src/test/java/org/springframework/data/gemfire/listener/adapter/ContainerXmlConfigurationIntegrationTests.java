/*
 * Copyright (c) VMware, Inc. 2022-2024. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.listener.adapter;

import org.apache.geode.cache.client.ClientCache;
import org.apache.geode.cache.client.Pool;
import org.apache.geode.cache.query.CqQuery;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
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
 * @see org.apache.geode.cache.client.ClientCache
 * @see org.apache.geode.cache.client.Pool
 * @see org.apache.geode.cache.query.CqQuery
 * @see org.springframework.data.gemfire.fork.CqCacheServerProcess
 * @see org.springframework.data.gemfire.listener.ContinuousQueryListenerContainer
 * @see org.springframework.data.gemfire.tests.integration.IntegrationTestsSupport
 * @see org.springframework.data.gemfire.tests.unit.annotation.GemFireUnitTest
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

		Assertions.assertThat(container).isNotNull();
		Assertions.assertThat(container.isRunning()).isTrue();
		Assertions.assertThat(container).isSameAs(applicationContext.getBean("testContainerId",
			ContinuousQueryListenerContainer.class));

		ClientCache cache = applicationContext.getBean(ClientCache.class);
		Pool pool = applicationContext.getBean(Pool.class);

		Assertions.assertThat(cache.getName()).isEqualTo("ContainerXmlConfigurationIntegrationTests");
		Assertions.assertThat(pool.getName()).isEqualTo("client");

		CqQuery[] cacheCqs = cache.getQueryService().getCqs();
		CqQuery[] poolCqs = pool.getQueryService().getCqs();

		Assertions.assertThat(pool.getQueryService().getCq("test-bean-1")).isNotNull();
		Assertions.assertThat(cacheCqs.length).isEqualTo(3);
		Assertions.assertThat(poolCqs.length).isEqualTo(3);
	}
}
