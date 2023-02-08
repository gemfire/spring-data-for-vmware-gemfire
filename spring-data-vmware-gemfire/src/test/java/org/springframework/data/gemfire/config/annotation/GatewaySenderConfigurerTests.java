/*
 * Copyright (c) VMware, Inc. 2022-2023. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.config.annotation;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;
import java.util.function.Function;

import org.junit.Test;

import org.apache.geode.cache.EntryEvent;
import org.apache.geode.cache.GemFireCache;
import org.apache.geode.cache.Region;
import org.apache.geode.cache.wan.GatewayEventFilter;
import org.apache.geode.cache.wan.GatewayEventSubstitutionFilter;
import org.apache.geode.cache.wan.GatewayQueueEvent;
import org.apache.geode.cache.wan.GatewaySender;
import org.apache.geode.cache.wan.GatewayTransportFilter;

import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertySource;
import org.springframework.data.gemfire.PartitionedRegionFactoryBean;
import org.springframework.data.gemfire.tests.integration.SpringApplicationContextIntegrationTestsSupport;
import org.springframework.data.gemfire.tests.mock.annotation.EnableGemFireMockObjects;
import org.springframework.mock.env.MockPropertySource;

/**
 * Tests for {@link EnableGatewaySenders} and {@link EnableGatewaySender}.
 *
 * @author Udo Kohlmeyer
 * @author John Blum
 * @see Test
 * @see org.mockito.Mockito
 * @see GemFireCache
 * @see Region
 * @see GatewaySender
 * @see Configuration
 * @see EnableGatewaySender
 * @see EnableGatewaySenders
 * @see GatewaySenderConfiguration
 * @see GatewaySendersConfiguration
 * @see GatewaySenderConfigurer
 * @see SpringApplicationContextIntegrationTestsSupport
 * @see EnableGemFireMockObjects
 * @see org.springframework.data.gemfire.wan.GatewaySenderFactoryBean
 * @since 2.2.0
 */
@SuppressWarnings("unused")
public class GatewaySenderConfigurerTests extends SpringApplicationContextIntegrationTestsSupport {

	private ConfigurableApplicationContext newApplicationContext(PropertySource<?> testPropertySource,
			Class<?>... annotatedClasses) {

		Function<ConfigurableApplicationContext, ConfigurableApplicationContext> applicationContextInitializer =
			applicationContext -> {

				MutablePropertySources propertySources = applicationContext.getEnvironment().getPropertySources();

				propertySources.addFirst(testPropertySource);

				return applicationContext;
			};

		return newApplicationContext(applicationContextInitializer, annotatedClasses);
	}

	@Test
	public void annotationConfigurationOfMultipleGatewaySendersWithConfigurersAndProperties() {

		MockPropertySource testPropertySource = new MockPropertySource();

		testPropertySource.setProperty("spring.data.gemfire.gateway.sender.alert-threshold", 1234);
		testPropertySource.setProperty("spring.data.gemfire.gateway.sender.TestGatewaySender.batch-size", 1002);
		testPropertySource.setProperty("spring.data.gemfire.gateway.sender.TestGatewaySender2.batch-size", 1002);
		testPropertySource.setProperty("spring.data.gemfire.gateway.sender.batch-time-interval", 2000);
		testPropertySource.setProperty("spring.data.gemfire.gateway.sender.maximum-queue-memory", 400);
		testPropertySource.setProperty("spring.data.gemfire.gateway.sender.TestGatewaySender.socket-read-timeout", 4000);
		testPropertySource.setProperty("spring.data.gemfire.gateway.sender.TestGatewaySender2.socket-read-timeout", 4000);
		testPropertySource.setProperty("spring.data.gemfire.gateway.sender.socket-buffer-size", 16384);

		newApplicationContext(testPropertySource, BaseGatewaySenderTestConfiguration.class,
			TestTwoGatewaySenderConfigurersBasic.class);

		Map<String, GatewaySender> beansOfType = getBeansOfType(GatewaySender.class);

		String[] senders = new String[] { "TestGatewaySender", "TestGatewaySender2" };

		assertThat(beansOfType).hasSize(2);
		assertThat(beansOfType.keySet()).containsExactlyInAnyOrder(senders);

		for (String sender : senders) {

			GatewaySender gatewaySender = getBean(sender, GatewaySender.class);

			assertThat(gatewaySender.isManualStart()).isEqualTo(false);
			assertThat(gatewaySender.getRemoteDSId()).isEqualTo(2);
			assertThat(gatewaySender.getId()).isEqualTo(sender);
			assertThat(gatewaySender.getDispatcherThreads()).isEqualTo(22);
			assertThat(gatewaySender.isBatchConflationEnabled()).isEqualTo(false);
			assertThat(gatewaySender.isParallel()).isEqualTo(true);
			assertThat(gatewaySender.isPersistenceEnabled()).isEqualTo(false);
			assertThat(gatewaySender.getOrderPolicy()).isEqualTo(GatewaySender.OrderPolicy.PARTITION);
			assertThat(gatewaySender.getGatewayEventSubstitutionFilter()).isNull();
			assertThat(gatewaySender.getAlertThreshold()).isEqualTo(1234);
			assertThat(gatewaySender.getBatchSize()).isEqualTo(1002);
			assertThat(gatewaySender.getBatchTimeInterval()).isEqualTo(2000);
			assertThat(gatewaySender.getMaximumQueueMemory()).isEqualTo(400);
			assertThat(gatewaySender.getSocketReadTimeout()).isEqualTo(4000);
			assertThat(gatewaySender.getSocketBufferSize()).isEqualTo(16384);

			assertThat(gatewaySender.getGatewayTransportFilters().size()).isEqualTo(0);

			Region<?, ?> region1 = getBean("Region1", Region.class);
			Region<?, ?> region2 = getBean("Region2", Region.class);

			assertThat(region1.getAttributes().getGatewaySenderIds())
				.containsExactlyInAnyOrder("TestGatewaySender", "TestGatewaySender2");
			assertThat(region2.getAttributes().getGatewaySenderIds())
				.containsExactlyInAnyOrder("TestGatewaySender2");
		}
	}

	@PeerCacheApplication
	@EnableGemFireMockObjects
	static class BaseGatewaySenderTestConfiguration {

		@Bean("Region1")
		PartitionedRegionFactoryBean<?, ?> createRegion1(GemFireCache gemFireCache) {
			return createRegion("Region1", gemFireCache);
		}

		@Bean("Region2")
		PartitionedRegionFactoryBean<?, ?> createRegion2(GemFireCache gemFireCache) {
			return createRegion("Region2", gemFireCache);
		}

		@Bean("transportBean1")
		GatewayTransportFilter createGatewayTransportBean1() {
			return new TestGatewayTransportFilter("transportBean1");
		}

		@Bean("transportBean2")
		GatewayTransportFilter createGatewayTransportBean2() {
			return new TestGatewayTransportFilter("transportBean2");
		}

		@Bean("SomeEventFilter")
		GatewayEventFilter createGatewayEventFilter() {
			return new TestGatewayEventFilter("SomeEventFilter");
		}

		public PartitionedRegionFactoryBean<?, ?> createRegion(String name, GemFireCache gemFireCache) {

			PartitionedRegionFactoryBean<?, ?> regionFactoryBean = new PartitionedRegionFactoryBean<>();

			regionFactoryBean.setCache(gemFireCache);
			regionFactoryBean.setName(name);

			return regionFactoryBean;
		}
	}

	private static class TestGatewayEventFilter implements GatewayEventFilter {

		private final String name;

		public TestGatewayEventFilter(String name) {
			this.name = name;
		}

		@Override
		public boolean beforeEnqueue(GatewayQueueEvent gatewayQueueEvent) {
			return false;
		}

		@Override
		public boolean beforeTransmit(GatewayQueueEvent gatewayQueueEvent) {
			return false;
		}

		@Override
		public void afterAcknowledgement(GatewayQueueEvent gatewayQueueEvent) { }

	}

	private static class TestGatewayEventSubstitutionFilter implements GatewayEventSubstitutionFilter<Object, Object> {

		private final String name;

		public TestGatewayEventSubstitutionFilter(String name) {
			this.name = name;
		}

		@Override
		public Object getSubstituteValue(EntryEvent entryEvent) {
			return null;
		}

		@Override
		public void close() { }

	}

	private static class TestGatewayTransportFilter implements GatewayTransportFilter {

		private final String name;

		public TestGatewayTransportFilter(String name) {
			this.name = name;
		}

		@Override
		public InputStream getInputStream(InputStream inputStream) {
			return null;
		}

		@Override
		public OutputStream getOutputStream(OutputStream outputStream) {
			return null;
		}

		@Override
		public int hashCode() {
			return this.name.hashCode();
		}

		@Override
		public boolean equals(Object obj) {

			if (this == obj) {
				return true;
			}

			if (!(obj instanceof TestGatewayTransportFilter)) {
				return false;
			}

			return this.name.equals(((TestGatewayTransportFilter) obj).name);
		}
	}

	@Configuration
	@EnableGatewaySenders(gatewaySenders = {
		@EnableGatewaySender(name = "TestGatewaySender", regions = "Region1"),
		@EnableGatewaySender(name = "TestGatewaySender2", regions = { "Region1", "Region2" })
	})
	static class TestTwoGatewaySenderConfigurersBasic {

		@Bean
		GatewaySenderConfigurer gatewaySenderConfigurer() {

			return (beanName, gatewaySenderFactoryBean) -> {
				gatewaySenderFactoryBean.setRemoteDistributedSystemId(2);
				gatewaySenderFactoryBean.setDispatcherThreads(22);
				gatewaySenderFactoryBean.setParallel(true);
				gatewaySenderFactoryBean.setOrderPolicy(GatewaySender.OrderPolicy.PARTITION);
			};
		}
	}
}
