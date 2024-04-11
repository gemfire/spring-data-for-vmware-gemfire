/*
 * Copyright 2022-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.config.annotation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.data.gemfire.util.ArrayUtils.asArray;

import java.io.IOException;
import java.io.Serializable;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicInteger;

import com.vmware.gemfire.testcontainers.GemFireCluster;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.apache.geode.cache.CacheListener;
import org.apache.geode.cache.EntryEvent;
import org.apache.geode.cache.GemFireCache;
import org.apache.geode.cache.Operation;
import org.apache.geode.cache.Region;
import org.apache.geode.cache.client.ClientRegionShortcut;
import org.apache.geode.cache.query.CqEvent;
import org.apache.geode.cache.util.CacheListenerAdapter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.data.gemfire.client.ClientRegionFactoryBean;
import org.springframework.data.gemfire.listener.ContinuousQueryListenerContainer;
import org.springframework.data.gemfire.listener.annotation.ContinuousQuery;
import org.springframework.data.gemfire.support.ConnectionEndpoint;
import org.springframework.data.gemfire.tests.integration.IntegrationTestsSupport;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

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
 * @see org.springframework.test.context.ContextConfiguration
 * @see org.springframework.test.context.junit4.SpringRunner
 * @since 2.0.0
 */
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = EnableContinuousQueriesConfigurationIntegrationTests.TestConfiguration.class)
@SuppressWarnings("unused")
public class EnableContinuousQueriesConfigurationIntegrationTests extends IntegrationTestsSupport {

	private static final AtomicInteger boilingTemperatureReadingsCounter = new AtomicInteger(0);
	private static final AtomicInteger freezingTemperatureReadingsCounter = new AtomicInteger(0);
	private static final AtomicInteger totalTemperatureReadingsCounter = new AtomicInteger(0);

	private static GemFireCluster gemFireCluster;

	@BeforeClass
	public static void startGeodeServer() throws IOException {

		gemFireCluster = new GemFireCluster(System.getProperty("spring.test.gemfire.docker.image"), 1, 1)
				.withGfsh(false, "create region --name=TemperatureReadings --type=PARTITION");

		gemFireCluster.acceptLicense().start();

		System.setProperty("gemfire.locator.port", String.valueOf(gemFireCluster.getLocatorPort()));
	}

	@AfterClass
	public static void shutdown() {
		gemFireCluster.close();
	}

	@Autowired
	@Qualifier("TemperatureReadings")
	private Region<Long, TemperatureReading> temperatureReadings;

	@Before
	public void setup() {
		if(temperatureReadings.sizeOnServer() != 0) return;

		long key = 0;
		temperatureReadings.put(++key, new TemperatureReading(99));
		temperatureReadings.put(++key, new TemperatureReading(213));
		temperatureReadings.put(++key, new TemperatureReading(72));
		temperatureReadings.put(++key, new TemperatureReading(400));
		temperatureReadings.put(++key, new TemperatureReading(1024));
		temperatureReadings.put(++key, new TemperatureReading(43));
		temperatureReadings.put(++key, new TemperatureReading(0));
		temperatureReadings.put(++key, new TemperatureReading(33));
		temperatureReadings.put(++key, new TemperatureReading(-45));
		temperatureReadings.put(++key, new TemperatureReading(67));

		TemperatureReading temperatureReading = this.temperatureReadings.get(1L);

		assertThat(temperatureReading).isNotNull();
		assertThat(temperatureReading.temperature()).isEqualTo(99);

		assertThat(this.temperatureReadings.sizeOnServer()).isEqualTo(10);

		waitOn(() -> totalTemperatureReadingsCounter.get() >= 5, 100L);

		assertThat(totalTemperatureReadingsCounter.get()).isNotZero();
	}

	@Test
	public void boilingTemperatureReadingsEqualsThree() {
		assertThat(boilingTemperatureReadingsCounter.get()).isEqualTo(3);
	}

	@Test
	public void freezingTemperatureReadingsEqualsTwo() {
		assertThat(freezingTemperatureReadingsCounter.get()).isEqualTo(2);
	}

	@EnableContinuousQueries
	@EnablePdx(includeDomainTypes = TemperatureReading.class)
	@ClientCacheApplication(logLevel = "error", subscriptionEnabled = true)
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

		@Bean
		ClientCacheConfigurer clientCachePoolPortConfigurer() {
			return (bean, clientCacheFactoryBean) -> clientCacheFactoryBean.setLocators(
					Collections.singletonList(new ConnectionEndpoint("localhost", gemFireCluster.getLocatorPort())));
		}

		@Bean(name = "TemperatureReadings")
		ClientRegionFactoryBean<Long, TemperatureReading> temperatureReadingsRegion(GemFireCache gemfireCache) {

			ClientRegionFactoryBean<Long, TemperatureReading> temperatureReadings = new ClientRegionFactoryBean<>();

			temperatureReadings.setCache(gemfireCache);
			temperatureReadings.setCacheListeners(asArray(temperatureReadingCounterListener()));
			temperatureReadings.setClose(false);
			temperatureReadings.setShortcut(ClientRegionShortcut.PROXY);

			return temperatureReadings;
		}

		private CacheListener<Long, TemperatureReading> temperatureReadingCounterListener() {

			return new CacheListenerAdapter<>() {

				@Override
				public void afterCreate(EntryEvent<Long, TemperatureReading> event) {
					if (Operation.LOCAL_LOAD_CREATE.equals(event.getOperation())) {
						totalTemperatureReadingsCounter.incrementAndGet();
					}
				}
			};
		}
	}

	public record TemperatureReading(Integer temperature) implements Serializable { }
}
