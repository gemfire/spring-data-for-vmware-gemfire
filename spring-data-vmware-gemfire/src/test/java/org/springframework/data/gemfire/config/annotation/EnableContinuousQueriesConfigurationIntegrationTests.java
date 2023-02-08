/*
 * Copyright (c) VMware, Inc. 2022-2023. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.config.annotation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.data.gemfire.util.ArrayUtils.asArray;

import java.io.Serializable;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.apache.geode.cache.CacheListener;
import org.apache.geode.cache.CacheLoader;
import org.apache.geode.cache.CacheLoaderException;
import org.apache.geode.cache.EntryEvent;
import org.apache.geode.cache.GemFireCache;
import org.apache.geode.cache.LoaderHelper;
import org.apache.geode.cache.Operation;
import org.apache.geode.cache.Region;
import org.apache.geode.cache.client.ClientRegionShortcut;
import org.apache.geode.cache.query.CqEvent;
import org.apache.geode.cache.util.CacheListenerAdapter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.gemfire.PartitionedRegionFactoryBean;
import org.springframework.data.gemfire.client.ClientRegionFactoryBean;
import org.springframework.data.gemfire.listener.ContinuousQueryListenerContainer;
import org.springframework.data.gemfire.listener.annotation.ContinuousQuery;
import org.springframework.data.gemfire.support.ConnectionEndpoint;
import org.springframework.data.gemfire.tests.integration.ForkingClientServerIntegrationTestsSupport;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * Integration tests for {@link EnableContinuousQueries}, {@link ContinuousQueryConfiguration}, {@link ContinuousQuery}
 * and {@link ContinuousQueryListenerContainer}.
 *
 * @author John Blum
 * @see org.junit.Test
 * @see org.apache.geode.cache.GemFireCache
 * @see org.apache.geode.cache.Region
 * @see org.apache.geode.cache.query.CqEvent
 * @see org.springframework.data.gemfire.config.annotation.ContinuousQueryConfiguration
 * @see org.springframework.data.gemfire.config.annotation.EnableContinuousQueries
 * @see org.springframework.data.gemfire.listener.annotation.ContinuousQuery
 * @see org.springframework.data.gemfire.tests.integration.ForkingClientServerIntegrationTestsSupport
 * @see org.springframework.test.context.ContextConfiguration
 * @see org.springframework.test.context.junit4.SpringRunner
 * @since 2.0.0
 */
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = EnableContinuousQueriesConfigurationIntegrationTests.TestConfiguration.class)
@SuppressWarnings("unused")
public class EnableContinuousQueriesConfigurationIntegrationTests extends ForkingClientServerIntegrationTestsSupport {

	private static final AtomicInteger boilingTemperatureReadingsCounter = new AtomicInteger(0);
	private static final AtomicInteger freezingTemperatureReadingsCounter = new AtomicInteger(0);
	private static final AtomicInteger totalTemperatureReadingsCounter = new AtomicInteger(0);

	@BeforeClass
	public static void startGeodeServer() throws Exception {
		startGemFireServer(GeodeServerTestConfiguration.class);
	}

	@Autowired
	@Qualifier("TemperatureReadings")
	private Region<Long, TemperatureReading> temperatureReadings;

	@Before
	public void setup() {

		TemperatureReading temperatureReading = this.temperatureReadings.get(1L);

		assertThat(temperatureReading).isNotNull();
		assertThat(temperatureReading.getTemperature()).isEqualTo(99);

		assertThat(this.temperatureReadings.sizeOnServer()).isEqualTo(10);

		//waitOn(() -> totalTemperatureReadingsCounter.get() >= 5, 100L);

		//assertThat(totalTemperatureReadingsCounter.get()).isNotZero();
	}

	@Test
	public void boilingTemperatureReadingsEqualsThree() {
		assertThat(boilingTemperatureReadingsCounter.get()).isEqualTo(3);
	}

	@Test
	public void freezingTemperatureReadingsEqualsTwo() {
		assertThat(freezingTemperatureReadingsCounter.get()).isEqualTo(2);
	}

	@Configuration
	@EnableContinuousQueries
	@Import(GeodeClientConfiguration.class)
	static class TestConfiguration {

		@ContinuousQuery(name = "BoilingTemperatures",
			query = "SELECT * FROM /TemperatureReadings r WHERE r.temperature >= 212")
		public void boilingTemperatures(CqEvent event) {
			boilingTemperatureReadingsCounter.incrementAndGet();
		}

		@ContinuousQuery(name = "FreezingTemperatures",
			query = "SELECT * FROM /TemperatureReadings r WHERE r.temperature <= 32")
		public void freezingTemperatures(CqEvent event) {
			freezingTemperatureReadingsCounter.incrementAndGet();
		}
	}

	@ClientCacheApplication(logLevel = "error", subscriptionEnabled = true)
	static class GeodeClientConfiguration {

		@Bean
		ClientCacheConfigurer clientCachePoolPortConfigurer(
				@Value("${" + GEMFIRE_CACHE_SERVER_PORT_PROPERTY + ":40404}") int port) {

			return (bean, clientCacheFactoryBean) -> clientCacheFactoryBean.setServers(
				Collections.singletonList(new ConnectionEndpoint("localhost", port)));
		}

		@Bean(name = "TemperatureReadings")
		ClientRegionFactoryBean<Long, TemperatureReading> temperatureReadingsRegion(GemFireCache gemfireCache) {

			ClientRegionFactoryBean<Long, TemperatureReading> temperatureReadings =
				new ClientRegionFactoryBean<>();

			temperatureReadings.setCache(gemfireCache);
			temperatureReadings.setCacheListeners(asArray(temperatureReadingCounterListener()));
			temperatureReadings.setClose(false);
			temperatureReadings.setShortcut(ClientRegionShortcut.PROXY);

			return temperatureReadings;
		}

		private CacheListener<Long, TemperatureReading> temperatureReadingCounterListener() {

			return new CacheListenerAdapter<Long, TemperatureReading>() {

				@Override
				public void afterCreate(EntryEvent<Long, TemperatureReading> event) {
					if (Operation.LOCAL_LOAD_CREATE.equals(event.getOperation())) {
						totalTemperatureReadingsCounter.incrementAndGet();
					}
				}
			};
		}
	}

	@CacheServerApplication
	static class GeodeServerTestConfiguration {

		public static void main(String[] args) {
			runSpringApplication(GeodeServerTestConfiguration.class, args);
		}

		@Bean(name = "TemperatureReadings")
		PartitionedRegionFactoryBean<Long, TemperatureReading> temperatureReadingsRegion(GemFireCache gemfireCache) {

			PartitionedRegionFactoryBean<Long, TemperatureReading> temperatureReadings =
				new PartitionedRegionFactoryBean<>();

			temperatureReadings.setCache(gemfireCache);
			temperatureReadings.setCacheLoader(temperatureReadingsLoader());
			temperatureReadings.setClose(false);
			temperatureReadings.setPersistent(false);

			return temperatureReadings;
		}

		private CacheLoader<Long, TemperatureReading> temperatureReadingsLoader() {

			return new CacheLoader<Long, TemperatureReading>() {

				@Override
				public TemperatureReading load(LoaderHelper<Long, TemperatureReading> helper) throws CacheLoaderException {

					long key = helper.getKey();

					Region<Long, TemperatureReading> temperatureReadings = helper.getRegion();

					recordTemperature(temperatureReadings, ++key, 213);
					recordTemperature(temperatureReadings, ++key, 72);
					recordTemperature(temperatureReadings, ++key, 400);
					recordTemperature(temperatureReadings, ++key, 1024);
					recordTemperature(temperatureReadings, ++key, 43);
					recordTemperature(temperatureReadings, ++key, 0);
					recordTemperature(temperatureReadings, ++key, 33);
					recordTemperature(temperatureReadings, ++key, -45);
					recordTemperature(temperatureReadings, ++key, 67);

					return TemperatureReading.newTemperatureReading(99);
				}

				private void recordTemperature(Region<Long, TemperatureReading> temperatureReadings,
						long key, int temperature) {

					sleep(50);
					temperatureReadings.put(key, TemperatureReading.newTemperatureReading(temperature));
				}

				private void sleep(long milliseconds) {
					try {
						Thread.sleep(milliseconds);
					}
					catch (InterruptedException ignore) {
					}
				}

				@Override
				public void close() {
				}
			};
		}
	}

	@Data
	@RequiredArgsConstructor(staticName = "newTemperatureReading")
	public static class TemperatureReading implements Serializable {

		@NonNull
		private Integer temperature;

		@Override
		public String toString() {
			return String.format("%d °F", getTemperature());
		}
	}
}
