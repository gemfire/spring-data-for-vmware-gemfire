/*
 * Copyright (c) VMware, Inc. 2022-2024. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.support;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Properties;

import org.junit.Test;
import org.junit.runner.RunWith;

import org.apache.geode.cache.Cache;
import org.apache.geode.cache.execute.Function;
import org.apache.geode.cache.execute.FunctionContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.gemfire.function.sample.HelloFunctionExecution;
import org.springframework.data.gemfire.tests.integration.IntegrationTestsSupport;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/**
 * Integration test to test the functionality of a GemFire Function implementing the Spring Data GemFire
 * {@link LazyWiringDeclarableSupport} class, defined using native GemFire configuration meta-data
 * (i.e {@literal cache.xml}).
 *
 * @author John Blum
 * @see org.junit.Test
 * @see org.apache.geode.cache.GemFireCache
 * @see org.apache.geode.cache.execute.Function
 * @see org.springframework.data.gemfire.tests.integration.IntegrationTestsSupport
 * @see org.springframework.test.context.ContextConfiguration
 * @see org.springframework.test.context.junit4.SpringRunner
 * @since 1.7.0
 */
@RunWith(SpringRunner.class)
@ContextConfiguration
@SuppressWarnings("unused")
public class LazyWiringDeclarableSupportFunctionBasedIntegrationTests extends IntegrationTestsSupport {

	@Autowired
	private Cache gemfireCache;

	@Autowired
	private HelloFunctionExecution helloFunctionExecution;

/*
	@BeforeClass
	public static void setupBeforeClass() {

		Cache gemfireCache = new CacheFactory()
			.set("name", LazyWiringDeclarableSupportFunctionBasedIntegrationTests.class.getSimpleName())
			.set("log-level", "error")
			.set("cache-xml-file", null)
			.create();

		assertThat(gemfireCache, is(notNullValue()));
		assertThat(SpringContextBootstrappingInitializer.getApplicationContext(), is(notNullValue()));
	}

	@AfterClass
	public static void tearDownAfterClass() {
		CacheFactory.getAnyInstance().close();
	}
*/

	@Test
	public void helloGreeting() {
		assertThat(helloFunctionExecution.hello(null)).isEqualTo("Hello Everyone");
	}

	protected static abstract class FunctionAdaptor<T> extends LazyWiringDeclarableSupport implements Function<T> {

		private final String id;

		FunctionAdaptor(String id) {
			Assert.hasText(id, "Function ID must be specified");
			this.id = id;
		}

		@Override
		public String getId() {
			return id;
		}

		@Override
		public boolean hasResult() {
			return true;
		}

		@Override
		public boolean isHA() {
			return false;
		}

		@Override
		public boolean optimizeForWrite() {
			return false;
		}
	}

	@SuppressWarnings("all")
	public static class HelloGemFireFunction extends FunctionAdaptor<Object> {

		protected static final String ADDRESS_TO_PARAMETER = "hello.address.to";
		protected static final String DEFAULT_ADDRESS_TO = "World";
		protected static final String HELLO_GREETING = "Hello %1$s";
		protected static final String ID = "hello";

		@Value("${hello.default.address.to}")
		private String defaultAddressTo;

		private String addressTo;

		public HelloGemFireFunction() {
			super(ID);
		}

		protected String getAddressTo() {
			return addressTo;
		}

		protected String getDefaultAddressTo() {
			return (StringUtils.hasText(defaultAddressTo) ? defaultAddressTo : DEFAULT_ADDRESS_TO);
		}

		@Override
		protected void doPostInit(Properties parameters) {
			addressTo = parameters.getProperty(ADDRESS_TO_PARAMETER, getDefaultAddressTo());
		}

		@Override
		public void execute(FunctionContext context) {
			context.getResultSender().lastResult(formatHelloGreeting(addressTo(context)));
		}

		// precedence is... 1. Caller 2. GemFire 3. Spring
		protected String addressTo(FunctionContext context) {

			Object arguments = context.getArguments();
			String addressTo = null;

			if (arguments instanceof Object[]) {
				Object[] args = (Object[]) arguments;
				addressTo = (args.length > 0 && args[0] != null ? String.valueOf(args[0]) : null);
			}
			else if (arguments != null) {
				addressTo = String.valueOf(arguments);
			}

			return (StringUtils.hasText(addressTo) ? addressTo : getAddressTo());
		}

		protected String formatHelloGreeting(String addressTo) {
			return String.format(HELLO_GREETING, addressTo);
		}
	}
}
