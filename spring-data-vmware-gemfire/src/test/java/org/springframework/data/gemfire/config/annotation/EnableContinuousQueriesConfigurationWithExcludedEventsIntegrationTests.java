/*
 * Copyright (c) VMware, Inc. 2022-2023. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.config.annotation;

import org.apache.geode.cache.GemFireCache;
import org.apache.geode.cache.Region;
import org.apache.geode.cache.client.ClientRegionShortcut;
import org.apache.geode.cache.query.CqEvent;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.data.gemfire.PartitionedRegionFactoryBean;
import org.springframework.data.gemfire.client.ClientRegionFactoryBean;
import org.springframework.data.gemfire.listener.CQEvent;
import org.springframework.data.gemfire.listener.annotation.ContinuousQuery;
import org.springframework.data.gemfire.support.ConnectionEndpoint;
import org.springframework.data.gemfire.tests.integration.ForkingClientServerIntegrationTestsSupport;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.Serializable;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = EnableContinuousQueriesConfigurationWithExcludedEventsIntegrationTests.GemFireClientConfiguration.class)
public class EnableContinuousQueriesConfigurationWithExcludedEventsIntegrationTests extends ForkingClientServerIntegrationTestsSupport {

    private static final AtomicInteger updateOnlyFreezingTemperatureCounter = new AtomicInteger(0);
    private static final AtomicInteger excludeDestroyFreezingTemperatureCounter = new AtomicInteger(0);
    private static final AtomicInteger allFreezingTemperatureCounter = new AtomicInteger(0);
    private static final AtomicInteger excludeAllFreezingTemperatureCounter = new AtomicInteger(0);

    @BeforeClass
    public static void startGeodeServer() throws Exception {
        startGemFireServer(GeodeServerTestConfiguration.class);
    }

    @Autowired
    @Qualifier("TemperatureReadings")
    private Region<Long, TemperatureReading> temperatureReadings;

    @Before
    public void insertTemperatureData() {
        if(temperatureReadings.sizeOnServer() != 0) return;

        long key = 0;
        temperatureReadings.put(++key, new TemperatureReading(13));
        temperatureReadings.put(key, new TemperatureReading(17));
        temperatureReadings.put(++key, new TemperatureReading(40));
        temperatureReadings.put(++key, new TemperatureReading(24));
        temperatureReadings.put(++key, new TemperatureReading(43));
        temperatureReadings.put(++key, new TemperatureReading(0));
        temperatureReadings.put(++key, new TemperatureReading(33));
        temperatureReadings.put(key, new TemperatureReading(-45));
        temperatureReadings.put(++key, new TemperatureReading(6));
        temperatureReadings.destroy(key);
    }

    @Test
    public void updateOnlyFreezingTemperatureCount() {
        assertThat(updateOnlyFreezingTemperatureCounter.get()).isEqualTo(2);
    }

    @Test
    public void excludeDestroyFreezingTemperatureCount() {
        assertThat(excludeDestroyFreezingTemperatureCounter.get()).isEqualTo(6);
    }

    @Test
    public void allFreezingTemperatureCount() {
        assertThat(allFreezingTemperatureCounter.get()).isEqualTo(7);
    }

    @Test
    public void excludeAllFreezingTemperatureCount() {
        assertThat(excludeAllFreezingTemperatureCounter.get()).isEqualTo(0);
    }

    @EnableContinuousQueries
    @ClientCacheApplication(subscriptionEnabled = true)
    static class GemFireClientConfiguration {

        private static final String QUERY = "SELECT * FROM /TemperatureReadings r WHERE r.temperature <= 32";

        @ContinuousQuery(name = "OnlyUpdatesFreezingTemperatures", query = QUERY,
                excludedEvents = {CQEvent.CREATE, CQEvent.INVALIDATE, CQEvent.DESTROY})
        public void onlyUpdateFreezingTemperatures(CqEvent event) {
            updateOnlyFreezingTemperatureCounter.incrementAndGet();
        }

        @ContinuousQuery(name = "ExcludeDestroyFreezingTemperatures", query = QUERY, excludedEvents = CQEvent.DESTROY)
        public void excludeDestroyFreezingTemperatures(CqEvent event) {
            excludeDestroyFreezingTemperatureCounter.incrementAndGet();
        }

        @ContinuousQuery(name = "FreezingTemperatures", query = QUERY)
        public void freezingTemperatures(CqEvent event) {
            allFreezingTemperatureCounter.incrementAndGet();
        }

        @ContinuousQuery(name = "ExcludeAllFreezingTemperatures", query = QUERY,
                excludedEvents = {CQEvent.CREATE, CQEvent.INVALIDATE, CQEvent.DESTROY, CQEvent.UPDATE})
        public void excludeAllFreezingTemperatures(CqEvent event) {
            excludeAllFreezingTemperatureCounter.incrementAndGet();
        }

        @Bean
        ClientCacheConfigurer clientCachePoolPortConfigurer(
                @Value("${" + GEMFIRE_CACHE_SERVER_PORT_PROPERTY + ":40404}") int port) {
            return (bean, clientCacheFactoryBean) -> clientCacheFactoryBean.setServers(
                    Collections.singletonList(new ConnectionEndpoint("localhost", port)));
        }

        @Bean(name = "TemperatureReadings")
        ClientRegionFactoryBean<Long, TemperatureReading> temperatureReadingsRegion(GemFireCache gemfireCache) {
            ClientRegionFactoryBean<Long, TemperatureReading> temperatureReadings = new ClientRegionFactoryBean<>();
            temperatureReadings.setCache(gemfireCache);
            temperatureReadings.setClose(false);
            temperatureReadings.setShortcut(ClientRegionShortcut.PROXY);
            return temperatureReadings;
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
            temperatureReadings.setClose(false);
            temperatureReadings.setPersistent(false);

            return temperatureReadings;
        }
    }

    public static class TemperatureReading implements Serializable {
        private Integer temperature;
        public TemperatureReading(Integer temperature) {
            this.temperature = temperature;
        }

        public Integer getTemperature() {
            return temperature;
        }
    }
}
