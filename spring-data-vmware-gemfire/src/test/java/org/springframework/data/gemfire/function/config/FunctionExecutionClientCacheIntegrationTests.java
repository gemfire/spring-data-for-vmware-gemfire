/*
 * Copyright 2022-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.function.config;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.concurrent.TimeUnit;

import org.junit.Test;
import org.junit.runner.RunWith;

import org.apache.geode.cache.Region;
import org.apache.geode.cache.client.ClientCache;
import org.apache.geode.cache.client.Pool;
import org.apache.geode.cache.execute.Execution;
import org.apache.geode.cache.execute.Function;
import org.apache.geode.cache.execute.FunctionException;
import org.apache.geode.cache.execute.ResultCollector;
import org.apache.geode.distributed.DistributedMember;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.data.gemfire.function.execution.GemfireOnServerFunctionTemplate;
import org.springframework.data.gemfire.tests.integration.IntegrationTestsSupport;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Integration Tests for {@link Function} {@link Execution} on {@link ClientCache}.
 *
 * @author David Turanski
 * @author John Blum
 * @see org.junit.Test
 * @see org.apache.geode.cache.client.ClientCache
 * @see org.apache.geode.cache.execute.Execution
 * @see org.apache.geode.cache.execute.Function
 * @see org.springframework.data.gemfire.tests.integration.SpringApplicationContextIntegrationTestsSupport
 * @see org.springframework.test.context.ContextConfiguration
 * @see org.springframework.test.context.junit4.SpringRunner
 */
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = FunctionExecutionClientCacheIntegrationTests.ClientCacheTestConfiguration.class)
public class FunctionExecutionClientCacheIntegrationTests extends IntegrationTestsSupport {

	@Test
	public void contextCreated() {

		ClientCache cache = requireApplicationContext().getBean("gemfireCache", ClientCache.class);

		Pool pool = requireApplicationContext().getBean("gemfirePool", Pool.class);

		assertThat(pool.getName()).isEqualTo("gemfirePool");
		assertThat(cache.getDefaultPool().getLocators().isEmpty()).isTrue();
		assertThat(cache.getDefaultPool().getServers().size()).isEqualTo(1);
		assertThat(pool.getLocators().isEmpty()).isTrue();
		assertThat(pool.getServers().size()).isEqualTo(1);
		assertThat(cache.getDefaultPool().getServers().get(0)).isEqualTo(pool.getServers().get(0));

		Region<?, ?> region = requireApplicationContext().getBean("r1", Region.class);

		assertThat(region.getName()).isEqualTo("r1");
		assertThat(region.getAttributes()).isNotNull();
		assertThat(region.getAttributes().getPoolName()).isNull();

		GemfireOnServerFunctionTemplate template = requireApplicationContext().getBean(GemfireOnServerFunctionTemplate.class);

		assertThat(template.getResultCollector()).isInstanceOf(MyResultCollector.class);
	}

	@Configuration
	@ImportResource("/org/springframework/data/gemfire/function/config/FunctionExecutionClientCacheIntegrationTests-context.xml")
	@EnableGemfireFunctionExecutions(basePackages = "org.springframework.data.gemfire.function.config.three")
	@SuppressWarnings("unused")
	static class ClientCacheTestConfiguration {

		@Bean
		MyResultCollector myResultCollector() {
			return new MyResultCollector();
		}
	}
}



@SuppressWarnings("rawtypes")
class MyResultCollector implements ResultCollector {

	@Override
	public void addResult(DistributedMember arg0, Object arg1) { }

	@Override
	public void clearResults() { }

	@Override
	public void endResults() { }

	@Override
	public Object getResult() throws FunctionException {
		return null;
	}

	@Override
	public Object getResult(long arg0, TimeUnit arg1) throws FunctionException {
		return null;
	}
}
