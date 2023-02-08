/*
 * Copyright (c) VMware, Inc. 2022-2023. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.wan;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import java.io.IOException;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.apache.geode.cache.Cache;
import org.apache.geode.cache.DataPolicy;
import org.apache.geode.cache.GemFireCache;
import org.apache.geode.cache.Region;
import org.apache.geode.cache.RegionAttributes;
import org.apache.geode.cache.asyncqueue.AsyncEventListener;
import org.apache.geode.cache.asyncqueue.AsyncEventQueue;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.data.gemfire.PartitionedRegionFactoryBean;
import org.springframework.data.gemfire.config.annotation.EnableLocator;
import org.springframework.data.gemfire.config.annotation.PeerCacheApplication;
import org.springframework.data.gemfire.tests.integration.ForkingClientServerIntegrationTestsSupport;
import org.springframework.data.gemfire.tests.process.ProcessWrapper;
import org.springframework.data.gemfire.util.ArrayUtils;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Integration Tests testing the configuration of {@link AsyncEventQueue AsyncEventQueue} (AEQ)
 * on a cache {@link Region} by {@literal identifier} using the SDG XML Namespace.
 *
 * @author John Blum
 * @see org.junit.Test
 * @see Cache
 * @see GemFireCache
 * @see Region
 * @see AsyncEventQueue
 * @see EnableLocator
 * @see PeerCacheApplication
 * @see ForkingClientServerIntegrationTestsSupport
 * @see org.springframework.test.context.ContextConfiguration
 * @see org.springframework.test.context.junit4.SpringRunner
 * @since 2.2.0
 */
@RunWith(SpringRunner.class)
@ContextConfiguration(locations = "AsyncEventQueueByIdXmlConfigurationIntegrationTests-context.xml")
@SuppressWarnings("unused")
public class AsyncEventQueueByIdXmlConfigurationIntegrationTests extends ForkingClientServerIntegrationTestsSupport {

	private static ProcessWrapper geodeServer;

	@BeforeClass
	public static void startGeodeServer() throws IOException {

		int port = findAvailablePort();

		System.setProperty("spring.data.gemfire.locator.port", String.valueOf(port));

		geodeServer = run(GeodeServerConfiguration.class, "-Dspring.data.gemfire.locator.port=" + port);

		waitForServerToStart("localhost", port);
	}

	@AfterClass
	public static void stopGeodeServer() {
		stop(geodeServer);
	}

	@Autowired
	@Qualifier("Example")
	private Region<?, ?> example;

	@Test
	public void regionAsyncEventQueuesByIdConfiguredCorrectly() {

		assertThat(this.example).isNotNull();
		assertThat(this.example.getName()).isEqualTo("Example");

		RegionAttributes<?, ?> exampleAttributes = this.example.getAttributes();

		assertThat(exampleAttributes).isNotNull();
		assertThat(exampleAttributes.getDataPolicy()).isEqualTo(DataPolicy.PARTITION);
		assertThat(exampleAttributes.getAsyncEventQueueIds())
			.containsExactlyInAnyOrder("TestAsyncEventQueueOne", "TestAsyncEventQueueTwo");
	}

	@EnableLocator
	@PeerCacheApplication(name = "AsyncEventQueueByIdXmlConfigurationIntegrationTestsServer")
	static class GeodeServerConfiguration {

		public static void main(String[] args) {

			runSpringApplication(GeodeServerConfiguration.class, args);
			block();
		}

		@Bean("TestAsyncEventQueueOne")
		public AsyncEventQueueFactoryBean asyncEventQueueOne(Cache cache) {
			return new AsyncEventQueueFactoryBean(cache, mock(AsyncEventListener.class));
		}

		@Bean("TestAsyncEventQueueTwo")
		public AsyncEventQueueFactoryBean asyncEventQueueTwo(Cache cache) {
			return new AsyncEventQueueFactoryBean(cache, mock(AsyncEventListener.class));
		}

		@Bean("Example")
		public PartitionedRegionFactoryBean<Object, Object> exampleRegion(GemFireCache gemfireCache,
				@Qualifier("TestAsyncEventQueueOne") AsyncEventQueue asyncEventQueueOne,
				@Qualifier("TestAsyncEventQueueTwo") AsyncEventQueue asyncEventQueueTwo) {

			PartitionedRegionFactoryBean<Object, Object> exampleRegion = new PartitionedRegionFactoryBean<>();

			exampleRegion.setCache(gemfireCache);
			exampleRegion.setAsyncEventQueues(ArrayUtils.asArray(asyncEventQueueOne, asyncEventQueueTwo));

			return exampleRegion;
		}
	}
}
