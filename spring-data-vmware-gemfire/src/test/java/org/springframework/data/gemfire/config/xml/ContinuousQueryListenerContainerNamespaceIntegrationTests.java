/*
 * Copyright (c) VMware, Inc. 2022-2023. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.config.xml;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executor;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.apache.geode.cache.client.ClientCache;
import org.apache.geode.cache.query.CqListener;
import org.apache.geode.cache.query.CqQuery;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.gemfire.TestUtils;
import org.springframework.data.gemfire.fork.CqCacheServerProcess;
import org.springframework.data.gemfire.listener.ContinuousQueryListener;
import org.springframework.data.gemfire.listener.ContinuousQueryListenerContainer;
import org.springframework.data.gemfire.listener.GemfireMDP;
import org.springframework.data.gemfire.listener.adapter.ContinuousQueryListenerAdapter;
import org.springframework.data.gemfire.tests.integration.ForkingClientServerIntegrationTestsSupport;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.ErrorHandler;

/**
 * Integration Tests for the configuration and initialization of the SDG {@link ContinuousQueryListenerContainer}
 * using the SDG XML namespace.
 *
 * @author John Blum
 * @see Test
 * @see ClientCache
 * @see CqListener
 * @see CqQuery
 * @see ContinuousQueryListenerContainer
 * @see ForkingClientServerIntegrationTestsSupport
 * @see ContextConfiguration
 * @see SpringRunner
 * @since 1.4.0
 */
@RunWith(SpringRunner.class)
@ContextConfiguration
@SuppressWarnings("unused")
public class ContinuousQueryListenerContainerNamespaceIntegrationTests
		extends ForkingClientServerIntegrationTestsSupport {

	@BeforeClass
	public static void startGemFireServer() throws Exception {
		startGemFireServer(CqCacheServerProcess.class);
	}

	@Autowired
	private ClientCache gemfireCache;

	@Autowired
	private ContinuousQueryListenerContainer container;

	@Autowired
	@Qualifier("testErrorHandler")
	private ErrorHandler testErrorHandler;

	@Autowired
	@Qualifier("testTaskExecutor")
	private Executor testTaskExecutor;

	@Test
	public void testContainerConfiguration() throws Exception {

		assertThat(container).as("The ContinuousQueryListenerContainer was not properly configured").isNotNull();
		assertThat(container.isActive()).as("The CQ Listener Container should be active (initialized)").isTrue();
		assertThat(container.isAutoStartup()).as("The CQ Listener container should not be configured to auto-start")
			.isFalse();
		assertThat(container.isRunning()).as("The CQ Listener Container should not be running").isFalse();
		assertThat(container.getPhase()).isEqualTo(4);
		assertThat(testErrorHandler).isNotNull();
		assertThat(TestUtils.<Object>readField("errorHandler", container)).isSameAs(testErrorHandler);
		assertThat(testTaskExecutor).isNotNull();
		assertThat(TestUtils.<Object>readField("taskExecutor", container)).isSameAs(testTaskExecutor);

		CqQuery[] queries = gemfireCache.getQueryService().getCqs();

		assertThat(queries).isNotNull();
		assertThat(queries.length).isEqualTo(3);

		List<String> actualNames = new ArrayList<>(3);

		for (CqQuery query : queries) {

			actualNames.add(query.getName());

			assertThat(query.getQueryString()).isEqualTo("SELECT * FROM /test-cq");
			assertThat(query.isDurable()).isEqualTo("Q3".equalsIgnoreCase(query.getName()));

			CqListener cqListener = query.getCqAttributes().getCqListener();

			// The CqListener should be an instance of o.s.d.g.listener.ContinuousQueryListenerContainer.EventDispatcherAdapter
			// So, get the SDG "ContinuousQueryListener"
			ContinuousQueryListener listener = TestUtils.readField("listener", cqListener);

			assertThat(listener instanceof ContinuousQueryListenerAdapter).isTrue();
			assertThat(((ContinuousQueryListenerAdapter) listener).getDelegate() instanceof GemfireMDP).isTrue();

			if ("Q2".equalsIgnoreCase(query.getName())) {
				assertThat(TestUtils.<String>readField("defaultListenerMethod", listener)).isEqualTo("handleQuery");
			}
		}

		actualNames.containsAll(Arrays.asList("Q1", "Q2", "Q3"));
	}
}
