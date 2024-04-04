/*
 * Copyright 2022-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.client;

import static org.assertj.core.api.Assertions.assertThat;
import com.vmware.gemfire.testcontainers.GemFireCluster;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import org.apache.geode.cache.DataPolicy;
import org.apache.geode.cache.EntryEvent;
import org.apache.geode.cache.Region;
import org.apache.geode.cache.client.ClientCache;
import org.apache.geode.cache.client.ClientCacheFactory;
import org.apache.geode.cache.client.ClientRegionShortcut;
import org.apache.geode.cache.util.CacheListenerAdapter;
import org.awaitility.Awaitility;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.data.gemfire.tests.integration.IntegrationTestsSupport;
import org.springframework.data.gemfire.util.DistributedSystemUtils;
import org.springframework.util.Assert;

/**
 * Integration Tests to test Apache Geode durable clients.
 *
 * @author John Blum
 * @see java.util.Properties
 * @see org.junit.Test
 * @see org.apache.geode.cache.Region
 * @see org.apache.geode.cache.client.ClientCache
 * @see org.apache.geode.cache.client.ClientCacheFactory
 * @see org.apache.geode.cache.client.Pool
 * @see org.springframework.test.context.ContextConfiguration
 * @see org.springframework.test.context.junit4.SpringJUnit4ClassRunner
 * @since 1.6.3
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@SuppressWarnings("all")
public class DurableClientCacheIntegrationTests extends IntegrationTestsSupport {

	private static final boolean DEBUG = true;

	private static List<Integer> regionCacheListenerEventValues = Collections.synchronizedList(new ArrayList<Integer>());

	private static final String CLIENT_CACHE_INTEREST_RESULT_POLICY = DurableClientCacheIntegrationTests.class.getName()
			.concat(".interests-result-policy");

	private static final String DURABLE_CLIENT_TIMEOUT = DurableClientCacheIntegrationTests.class.getName()
			.concat(".durable-client-timeout");

	private static final String SERVER_HOST = "localhost";
	private static GemFireCluster gemFireCluster;
	private ClassPathXmlApplicationContext applicationContext;

	@BeforeClass
	public static void startGeodeServer() throws IOException {
		gemFireCluster = new GemFireCluster(System.getProperty("spring.test.gemfire.docker.image"), 1, 1);

		gemFireCluster.acceptLicense().start();

		gemFireCluster.gfsh(false, "create region --name=Example --type=REPLICATE");
		gemFireCluster.gfsh(false, "put --region=Example --value-class=java.lang.Integer --key=one --value=1");
		gemFireCluster.gfsh(false, "put --region=Example --value-class=java.lang.Integer --key=two --value=2");
		gemFireCluster.gfsh(false, "put --region=Example --value-class=java.lang.Integer --key=three --value=3");

		System.setProperty("gemfire.locator.port", String.valueOf(gemFireCluster.getLocatorPort()));
	}

	@AfterClass
	public static void shutdown() {
		gemFireCluster.close();
	}

	private ClientCache clientCache;

	private Region<String, Integer> example;

	@Before
	public void setup() {
		initializeClientCache();

		Properties distributedSystemProperties = this.clientCache.getDistributedSystem().getProperties();

		assertThat(distributedSystemProperties).isNotNull();

		assertThat(distributedSystemProperties.getProperty(DistributedSystemUtils.DURABLE_CLIENT_ID_PROPERTY_NAME))
				.isEqualTo(DurableClientCacheIntegrationTests.class.getSimpleName());
	}

	private void initializeClientCache() {
		applicationContext = new ClassPathXmlApplicationContext(
				"org/springframework/data/gemfire/client/DurableClientCacheIntegrationTests-context.xml");
		applicationContext.start();
		this.clientCache = applicationContext.getBean(ClientCache.class);
		this.example = applicationContext.getBean("Example", Region.class);
	}

	@After
	public void tearDown() {
		closeClientCache(this.clientCache, false);
		applicationContext.close();
		regionCacheListenerEventValues.clear();
	}

	private void closeClientCache(ClientCache clientCache, boolean keepAlive) {

		Function<ClientCache, ClientCache> cacheClosingFunction = cacheToClose -> {
			((ClientCache) cacheToClose).close(keepAlive);
			return cacheToClose;
		};

		if (Objects.nonNull(clientCache)) {
			closeGemFireCacheWaitOnCacheClosedEvent(() -> clientCache, cacheClosingFunction, TimeUnit.SECONDS.toMillis(5L));
		}
	}

	private void runClientCacheProducer() {

		ClientCache clientCache = null;

		try {
			clientCache = new ClientCacheFactory().addPoolLocator("localhost", Integer.getInteger("gemfire.locator.port"))
					.set("name", "ClientCacheProducer").set("log-level", "error").create();

			Region<String, Integer> exampleRegion = clientCache
					.<String, Integer> createClientRegionFactory(ClientRegionShortcut.PROXY).create("Example");

			exampleRegion.put("four", 4);
			exampleRegion.put("five", 5);
		} finally {
			closeClientCache(clientCache, false);
		}
	}

	private void setSystemProperties() {

		System.setProperty(CLIENT_CACHE_INTEREST_RESULT_POLICY, InterestResultPolicyType.NONE.name());
		System.setProperty(DURABLE_CLIENT_TIMEOUT, "600");
	}

	private void assertRegion(Region<?, ?> region, String expectedName, DataPolicy expectedDataPolicy) {
		assertRegion(region, expectedName, Region.SEPARATOR + expectedName, expectedDataPolicy);
	}

	private void assertRegion(Region<?, ?> region, String expectedName, String expectedPath,
			DataPolicy expectedDataPolicy) {

		assertThat(region).isNotNull();
		assertThat(region.getName()).isEqualTo(expectedName);
		assertThat(region.getFullPath()).isEqualTo(expectedPath);
		assertThat(region.getAttributes()).isNotNull();
		assertThat(region.getAttributes().getDataPolicy()).isEqualTo(expectedDataPolicy);
	}

	private void assertRegionValues(Region<?, ?> region, Object... values) {

		assertThat(region.size()).isEqualTo(values.length);

		for (Object value : values) {
			assertThat(region.containsValue(value)).isTrue();
		}
	}

	private void log(String message, Object... args) {

		if (DEBUG) {
			System.err.printf(message, args);
			System.err.flush();
		}
	}

	private void waitForRegionEntryEvents() {

		AtomicInteger counter = new AtomicInteger(0);

		waitOn(() -> {

			if (counter.incrementAndGet() % 3 == 0) {
				// log("NOTIFIED!%n");
				this.clientCache.readyForEvents();
			}

			// log("WAITING...%n");

			return regionCacheListenerEventValues.size() < 2;

		}, TimeUnit.SECONDS.toMillis(15L), 500L);
	}

	@Test
	public void durableClientGetsInitializedWithDataOnServer() {
		assertRegionValues(this.example, 1, 2, 3);
		assertThat(regionCacheListenerEventValues.isEmpty()).isTrue();
	}

	@Test
	public void durableClientGetsUpdatesFromServerWhileClientWasOffline() {
		assertRegionValues(this.example, 1, 2, 3);
		closeClientCache(this.clientCache, true);
		applicationContext.close();
		runClientCacheProducer();

		initializeClientCache();

		waitForRegionEntryEvents();

		Awaitility.await().timeout(4, TimeUnit.SECONDS)
				.until(() -> regionCacheListenerEventValues.containsAll(List.of(4, 5)));
	}

	public static class RegionDataLoadingBeanPostProcessor<K, V> implements BeanPostProcessor {

		private Map<K, V> regionData;

		private final String regionName;

		public RegionDataLoadingBeanPostProcessor(String regionName) {

			Assert.hasText(regionName, "Region name must be specified");

			this.regionName = regionName;
		}

		public void setRegionData(Map<K, V> regionData) {
			this.regionData = regionData;
		}

		protected Map<K, V> getRegionData() {

			Assert.state(this.regionData != null, "Region data was not provided");

			return this.regionData;
		}

		protected String getRegionName() {
			return this.regionName;
		}

		protected void loadData(Region<K, V> region) {
			region.putAll(getRegionData());
		}

		@Override
		public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {

			if (bean instanceof Region) {

				Region<K, V> region = (Region) bean;

				if (getRegionName().equals(region.getName())) {
					loadData(region);
				}
			}

			return bean;
		}
	}

	public static class RegionEntryEventRecordingCacheListener extends CacheListenerAdapter<String, Integer> {

		@Override
		public void afterCreate(EntryEvent<String, Integer> event) {
			regionCacheListenerEventValues.add(event.getNewValue());
		}
	}
}
