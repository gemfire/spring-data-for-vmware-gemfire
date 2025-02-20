/*
 * Copyright 2022-2025 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.config.annotation;

import java.util.Properties;
import java.util.function.Function;

import org.apache.geode.cache.client.ClientCache;
import org.apache.geode.cache.client.Pool;
import org.apache.geode.cache.client.PoolFactory;
import org.apache.geode.cache.client.SocketFactory;
import org.apache.geode.cache.control.ResourceManager;
import org.apache.geode.pdx.PdxSerializer;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertySource;
import org.springframework.data.gemfire.client.ClientCacheFactoryBean;
import org.springframework.data.gemfire.tests.integration.SpringApplicationContextIntegrationTestsSupport;
import org.springframework.data.gemfire.tests.mock.annotation.EnableGemFireMockObjects;
import org.springframework.mock.env.MockPropertySource;

/**
 * Integration Tests for {@link ClientCacheApplication}.
 *
 * @author John Blum
 * @see java.util.Properties
 * @see org.junit.Test
 * @see org.apache.geode.cache.client.ClientCache
 * @see org.springframework.core.env.PropertySource
 * @see org.springframework.data.gemfire.client.ClientCacheFactoryBean
 * @see org.springframework.data.gemfire.config.annotation.ClientCacheApplication
 * @see org.springframework.data.gemfire.tests.integration.SpringApplicationContextIntegrationTestsSupport
 * @see org.springframework.data.gemfire.tests.mock.annotation.EnableGemFireMockObjects
 * @see org.springframework.mock.env.MockPropertySource
 * @since 2.0.0
 */
public class ClientCachePropertiesIntegrationTests extends SpringApplicationContextIntegrationTestsSupport {

	private ConfigurableApplicationContext newApplicationContext(PropertySource<?> testPropertySource,
			Class<?>... annotatedClasses) {

		Function<ConfigurableApplicationContext, ConfigurableApplicationContext> applicationContextInitializer = applicationContext -> {

			MutablePropertySources propertySources = applicationContext.getEnvironment().getPropertySources();

			propertySources.addFirst(testPropertySource);

			return applicationContext;
		};

		return newApplicationContext(applicationContextInitializer, annotatedClasses);
	}

	@Test
	public void clientCacheConfiguration() {

		MockPropertySource testPropertySource = new MockPropertySource()
			.withProperty("spring.data.gemfire.cache.critical-heap-percentage", 90.0f)
			.withProperty("spring.data.gemfire.cache.eviction-heap-percentage", 85.0f)
			.withProperty("spring.data.gemfire.pdx.ignore-unread-fields", false)
			.withProperty("spring.data.gemfire.pdx.persistent", true)
			.withProperty("spring.data.gemfire.pool.free-connection-timeout", 20000L)
			.withProperty("spring.data.gemfire.pool.max-connections", 250)
			.withProperty("spring.data.gemfire.pool.ping-interval", 5000L)
			.withProperty("spring.data.gemfire.pool.pr-single-hop-enabled", false)
			.withProperty("spring.data.gemfire.pool.read-timeout", 20000L)
			.withProperty("spring.data.gemfire.pool.default.read-timeout", 15000L)
			.withProperty("spring.data.gemfire.pool.retry-attempts", 2)
			.withProperty("spring.data.gemfire.pool.server-group", "TestGroup")
			.withProperty("spring.data.gemfire.pool.default.subscription-redundancy", 2);

		newApplicationContext(testPropertySource, TestClientCacheConfiguration.class);

		Assertions.assertThat(containsBean("gemfireCache")).isTrue();
		Assertions.assertThat(containsBean("mockPdxSerializer")).isTrue();

		ClientCacheFactoryBean testClientCacheFactoryBean =
			getBean("&gemfireCache", ClientCacheFactoryBean.class);

		Assertions.assertThat(testClientCacheFactoryBean).isNotNull();
		Assertions.assertThat(testClientCacheFactoryBean.isUseBeanFactoryLocator()).isFalse();

		ClientCache testClientCache = getBean("gemfireCache", ClientCache.class);

		Assertions.assertThat(testClientCache).isNotNull();

		PdxSerializer mockPdxSerializer = getBean("mockPdxSerializer", PdxSerializer.class);

		Assertions.assertThat(mockPdxSerializer).isNotNull();
		Assertions.assertThat(testClientCache).isNotNull();
		Assertions.assertThat(testClientCache.getCopyOnRead()).isTrue();
		Assertions.assertThat(testClientCache.getPdxDiskStore()).isNull();
		Assertions.assertThat(testClientCache.getPdxIgnoreUnreadFields()).isFalse();
		Assertions.assertThat(testClientCache.getPdxPersistent()).isTrue();
		Assertions.assertThat(testClientCache.getPdxReadSerialized()).isFalse();
		Assertions.assertThat(testClientCache.getPdxSerializer()).isSameAs(mockPdxSerializer);

		Pool defaultPool = testClientCache.getDefaultPool();

		Assertions.assertThat(defaultPool).isNotNull();
		Assertions.assertThat(defaultPool.getFreeConnectionTimeout()).isEqualTo(20000);
		Assertions.assertThat(defaultPool.getIdleTimeout()).isEqualTo(15000L);
		Assertions.assertThat(defaultPool.getLoadConditioningInterval()).isEqualTo(180000);
		Assertions.assertThat(defaultPool.getMaxConnections()).isEqualTo(250);
		Assertions.assertThat(defaultPool.getMinConnections()).isEqualTo(50);
		Assertions.assertThat(defaultPool.getMultiuserAuthentication()).isFalse();
		Assertions.assertThat(defaultPool.getName()).isEqualTo("DEFAULT");
		Assertions.assertThat(defaultPool.getPingInterval()).isEqualTo(5000L);
		Assertions.assertThat(defaultPool.getPRSingleHopEnabled()).isFalse();
		Assertions.assertThat(defaultPool.getReadTimeout()).isEqualTo(15000);
		Assertions.assertThat(defaultPool.getRetryAttempts()).isEqualTo(2);
		Assertions.assertThat(defaultPool.getServerConnectionTimeout()).isEqualTo(PoolFactory.DEFAULT_SERVER_CONNECTION_TIMEOUT);
		Assertions.assertThat(defaultPool.getServerGroup()).isEqualTo("TestGroup");
		Assertions.assertThat(defaultPool.getSocketBufferSize()).isEqualTo(PoolFactory.DEFAULT_SOCKET_BUFFER_SIZE);
		Assertions.assertThat(defaultPool.getSocketConnectTimeout()).isEqualTo(20001);
		Assertions.assertThat(defaultPool.getSocketFactory()).isEqualTo(PoolFactory.DEFAULT_SOCKET_FACTORY);
		Assertions.assertThat(defaultPool.getStatisticInterval()).isEqualTo(500);
		Assertions.assertThat(defaultPool.getSubscriptionAckInterval()).isEqualTo(PoolFactory.DEFAULT_SUBSCRIPTION_ACK_INTERVAL);
		Assertions.assertThat(defaultPool.getSubscriptionEnabled()).isTrue();
		Assertions.assertThat(defaultPool.getSubscriptionMessageTrackingTimeout()).isEqualTo(PoolFactory.DEFAULT_SUBSCRIPTION_MESSAGE_TRACKING_TIMEOUT);
		Assertions.assertThat(defaultPool.getSubscriptionRedundancy()).isEqualTo(2);

		ResourceManager resourceManager = testClientCache.getResourceManager();

		Assertions.assertThat(resourceManager).isNotNull();
		Assertions.assertThat(resourceManager.getCriticalHeapPercentage()).isEqualTo(90.0f);
		Assertions.assertThat(resourceManager.getEvictionHeapPercentage()).isEqualTo(90.0f);
	}

	@Test
	public void configurationWithMinMaxConnectionsPerServerProperties() {

		MockPropertySource testPropertySource = new MockPropertySource()
				.withProperty("spring.data.gemfire.pool.max-connections-per-server", 100)
				.withProperty("spring.data.gemfire.pool.min-connections-per-server", 5);

		newApplicationContext(testPropertySource, TestClientCacheSimple.class);

		Assertions.assertThat(containsBean("gemfireCache")).isTrue();

		ClientCacheFactoryBean testClientCacheFactoryBean =
				getBean("&gemfireCache", ClientCacheFactoryBean.class);

		Assertions.assertThat(testClientCacheFactoryBean).isNotNull();
		Assertions.assertThat(testClientCacheFactoryBean.isUseBeanFactoryLocator()).isFalse();

		ClientCache testClientCache = getBean("gemfireCache", ClientCache.class);

		Assertions.assertThat(testClientCache).isNotNull();

		Pool defaultPool = testClientCache.getDefaultPool();

		Assertions.assertThat(defaultPool).isNotNull();

		Assertions.assertThat(defaultPool.getMaxConnectionsPerServer()).isEqualTo(100);
		Assertions.assertThat(defaultPool.getMinConnectionsPerServer()).isEqualTo(5);
	}

	@Test
	public void configurationWithMinMaxConnectionsPerServerAnnotation() {

		MockPropertySource testPropertySource = new MockPropertySource();

		newApplicationContext(testPropertySource, TestClientCacheSimple.class);

		Assertions.assertThat(containsBean("gemfireCache")).isTrue();

		ClientCacheFactoryBean testClientCacheFactoryBean =
				getBean("&gemfireCache", ClientCacheFactoryBean.class);

		Assertions.assertThat(testClientCacheFactoryBean).isNotNull();
		Assertions.assertThat(testClientCacheFactoryBean.isUseBeanFactoryLocator()).isFalse();

		ClientCache testClientCache = getBean("gemfireCache", ClientCache.class);

		Assertions.assertThat(testClientCache).isNotNull();

		Pool defaultPool = testClientCache.getDefaultPool();

		Assertions.assertThat(defaultPool).isNotNull();

		Assertions.assertThat(defaultPool.getMaxConnectionsPerServer()).isEqualTo(50);
		Assertions.assertThat(defaultPool.getMinConnectionsPerServer()).isEqualTo(10);
	}

	@Test
	public void dynamicClientCacheConfiguration() {

		MockPropertySource testPropertySource = new MockPropertySource()
			.withProperty("spring.data.gemfire.use-bean-factory-locator", true)
			.withProperty("spring.data.gemfire.cache.copy-on-read", true)
			.withProperty("spring.data.gemfire.cache.critical-heap-percentage", 90.0f)
			.withProperty("spring.data.gemfire.cache.eviction-heap-percentage", 75.0f)
			.withProperty("spring.data.gemfire.cache.log-level", "info")
			.withProperty("spring.data.gemfire.cache.name", "ABC123")
			.withProperty("spring.data.gemfire.cache.client.durable-client-id", "123")
			.withProperty("spring.data.gemfire.cache.client.durable-client-timeout", 600)
			.withProperty("spring.data.gemfire.cache.client.keep-alive", true)
			.withProperty("spring.data.gemfire.pool.default.free-connection-timeout", 5000)
			.withProperty("spring.data.gemfire.pool.default.idle-timeout", 15000)
			.withProperty("spring.data.gemfire.pool.default.load-conditioning-interval", 120000)
			.withProperty("spring.data.gemfire.pool.default.max-connections", 100)
			.withProperty("spring.data.gemfire.pool.default.min-connections", 10)
			.withProperty("spring.data.gemfire.pool.default.multi-user-authentication", true)
			.withProperty("spring.data.gemfire.pool.default.ping-interval", 15000L)
			.withProperty("spring.data.gemfire.pool.default.pr-single-hop-enabled", false)
			.withProperty("spring.data.gemfire.pool.default.read-timeout", 5000)
			.withProperty("spring.data.gemfire.pool.default.ready-for-events", true)
			.withProperty("spring.data.gemfire.pool.default.retry-attempts", 2)
			.withProperty("spring.data.gemfire.pool.default.server-connection-timeout", 60000)
			.withProperty("spring.data.gemfire.pool.default.server-group", "testGroup")
			.withProperty("spring.data.gemfire.pool.default.socket-buffer-size", 65535)
			.withProperty("spring.data.gemfire.pool.default.socket-connect-timeout", 30001)
			.withProperty("spring.data.gemfire.pool.default.socket-factory-bean-name", "mockSocketFactory")
			.withProperty("spring.data.gemfire.pool.default.statistic-interval", 100)
			.withProperty("spring.data.gemfire.pool.default.subscription-ack-interval", 250)
			.withProperty("spring.data.gemfire.pool.default.subscription-enabled", true)
			.withProperty("spring.data.gemfire.pool.default.subscription-message-tracking-timeout", 300000)
			.withProperty("spring.data.gemfire.pool.default.subscription-redundancy", 2)
			.withProperty("spring.data.gemfire.pdx.disk-store-name", "TestPdxDiskStore")
			.withProperty("spring.data.gemfire.pdx.ignore-unread-fields", false)
			.withProperty("spring.data.gemfire.pdx.persistent", true)
			.withProperty("spring.data.gemfire.pdx.read-serialized", true);

		newApplicationContext(testPropertySource, TestDynamicClientCacheConfiguration.class);

		Assertions.assertThat(containsBean("gemfireCache")).isTrue();
		Assertions.assertThat(containsBean("mockPdxSerializer")).isTrue();

		ClientCacheFactoryBean clientCacheFactoryBean = getBean("&gemfireCache", ClientCacheFactoryBean.class);

		Assertions.assertThat(clientCacheFactoryBean).isNotNull();

		ClientCache clientCache = getBean("gemfireCache", ClientCache.class);

		Assertions.assertThat(clientCache).isNotNull();

		PdxSerializer mockPdxSerializer = getBean("mockPdxSerializer", PdxSerializer.class);

		SocketFactory mockSocketFactory = getBean("mockSocketFactory", SocketFactory.class);

		Assertions.assertThat(mockPdxSerializer).isNotNull();
		Assertions.assertThat(mockSocketFactory).isNotNull();
		Assertions.assertThat(clientCacheFactoryBean.getDurableClientId()).isEqualTo("123");
		Assertions.assertThat(clientCacheFactoryBean.getDurableClientTimeout()).isEqualTo(600);
		Assertions.assertThat(clientCacheFactoryBean.isKeepAlive()).isTrue();
		Assertions.assertThat(clientCacheFactoryBean.isReadyForEvents()).isTrue();
		Assertions.assertThat(clientCacheFactoryBean.isUseBeanFactoryLocator()).isTrue();
		Assertions.assertThat(clientCache).isNotNull();
		Assertions.assertThat(clientCache.getCopyOnRead()).isTrue();
		Assertions.assertThat(clientCache.getDistributedSystem()).isNotNull();
		Assertions.assertThat(clientCache.getPdxDiskStore()).isEqualTo("TestPdxDiskStore");
		Assertions.assertThat(clientCache.getPdxIgnoreUnreadFields()).isFalse();
		Assertions.assertThat(clientCache.getPdxPersistent()).isTrue();
		Assertions.assertThat(clientCache.getPdxReadSerialized()).isTrue();
		Assertions.assertThat(clientCache.getPdxSerializer()).isSameAs(mockPdxSerializer);

		Properties gemfireProperties = clientCache.getDistributedSystem().getProperties();

		Assertions.assertThat(gemfireProperties).isNotNull();
		Assertions.assertThat(gemfireProperties.getProperty("log-level")).isEqualTo("info");
		Assertions.assertThat(gemfireProperties.getProperty("name")).isEqualTo("ABC123");

		Pool defaultPool = clientCache.getDefaultPool();

		Assertions.assertThat(defaultPool).isNotNull();
		Assertions.assertThat(defaultPool.getFreeConnectionTimeout()).isEqualTo(5000);
		Assertions.assertThat(defaultPool.getIdleTimeout()).isEqualTo(15000L);
		Assertions.assertThat(defaultPool.getLoadConditioningInterval()).isEqualTo(120000);
		Assertions.assertThat(defaultPool.getMaxConnections()).isEqualTo(100);
		Assertions.assertThat(defaultPool.getMinConnections()).isEqualTo(10);
		Assertions.assertThat(defaultPool.getMultiuserAuthentication()).isTrue();
		Assertions.assertThat(defaultPool.getName()).isEqualTo("DEFAULT");
		Assertions.assertThat(defaultPool.getPingInterval()).isEqualTo(15000L);
		Assertions.assertThat(defaultPool.getPRSingleHopEnabled()).isFalse();
		Assertions.assertThat(defaultPool.getReadTimeout()).isEqualTo(5000);
		Assertions.assertThat(defaultPool.getRetryAttempts()).isEqualTo(2);
		Assertions.assertThat(defaultPool.getServerConnectionTimeout()).isEqualTo(60000);
		Assertions.assertThat(defaultPool.getServerGroup()).isEqualTo("testGroup");
		Assertions.assertThat(defaultPool.getSocketBufferSize()).isEqualTo(65535);
		Assertions.assertThat(defaultPool.getSocketConnectTimeout()).isEqualTo(30001);
		Assertions.assertThat(defaultPool.getSocketFactory()).isEqualTo(mockSocketFactory);
		Assertions.assertThat(defaultPool.getStatisticInterval()).isEqualTo(100);
		Assertions.assertThat(defaultPool.getSubscriptionAckInterval()).isEqualTo(250);
		Assertions.assertThat(defaultPool.getSubscriptionEnabled()).isTrue();
		Assertions.assertThat(defaultPool.getSubscriptionMessageTrackingTimeout()).isEqualTo(300000);
		Assertions.assertThat(defaultPool.getSubscriptionRedundancy()).isEqualTo(2);

		ResourceManager resourceManager = clientCache.getResourceManager();

		Assertions.assertThat(resourceManager).isNotNull();
		Assertions.assertThat(resourceManager.getCriticalHeapPercentage()).isEqualTo(90.0f);
		Assertions.assertThat(resourceManager.getEvictionHeapPercentage()).isEqualTo(75.0f);
	}

	// TODO add more tests

	@EnableGemFireMockObjects
	@ClientCacheApplication(name = "TestClientCache", copyOnRead = true,
		criticalHeapPercentage = 95.0f, evictionHeapPercentage = 80.0f, idleTimeout = 15000L,
		maxConnections = 100, minConnections = 10, pingInterval = 15000L, readTimeout = 15000, retryAttempts = 1,
		socketConnectTimeout = 20001, subscriptionEnabled = true, subscriptionRedundancy = 1)
	@EnablePdx(ignoreUnreadFields = true, readSerialized = true, serializerBeanName = "mockPdxSerializer")
	@SuppressWarnings("unused")
	static class TestClientCacheConfiguration {

		@Bean
		ClientCacheConfigurer testClientCacheConfigurer() {

			return (beanName, factoryBean) -> {
				factoryBean.setEvictionHeapPercentage(90.0f);
				factoryBean.setPdxReadSerialized(false);
				factoryBean.setLoadConditioningInterval(180000);
				factoryBean.setMinConnections(50);
				factoryBean.setStatisticsInterval(500);
			};
		}

		@Bean
		PdxSerializer mockPdxSerializer() {
			return Mockito.mock(PdxSerializer.class);
		}
	}

	@EnableGemFireMockObjects
	@ClientCacheApplication(name = "TestClientCacheSimple",minConnectionsPerServer = 10,maxConnectionsPerServer = 50)
	@SuppressWarnings("unused")
	static class TestClientCacheSimple {
	}

	@EnableGemFireMockObjects
	@ClientCacheApplication(name = "TestClientCache")
	@EnablePdx(serializerBeanName = "mockPdxSerializer")
	@SuppressWarnings("unused")
	static class TestDynamicClientCacheConfiguration {

		@Bean
		PdxSerializer mockPdxSerializer() {
			return Mockito.mock(PdxSerializer.class);
		}

		@Bean
		SocketFactory mockSocketFactory() {
			return Mockito.mock(SocketFactory.class);
		}
	}
}
