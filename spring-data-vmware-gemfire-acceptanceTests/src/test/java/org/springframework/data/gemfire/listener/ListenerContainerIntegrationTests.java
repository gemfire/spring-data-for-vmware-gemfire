/*
 * Copyright (c) VMware, Inc. 2022-2024. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.listener;

import java.io.IOException;
import java.util.EventListener;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;

import org.apache.geode.cache.client.ClientCache;
import org.apache.geode.cache.client.ClientCacheFactory;
import org.apache.geode.cache.query.CqEvent;
import org.assertj.core.api.Assertions;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.data.gemfire.listener.adapter.ContinuousQueryListenerAdapter;
import org.springframework.data.gemfire.tests.integration.IntegrationTestsSupport;
import org.springframework.data.gemfire.util.SpringExtensions;

import com.vmware.gemfire.testcontainers.GemFireCluster;

/**
 * @author Costin Leau
 * @author John Blum
 */
public class ListenerContainerIntegrationTests extends IntegrationTestsSupport {

	private ClientCache gemfireCache = null;

	private final ContinuousQueryListenerAdapter adapter = new ContinuousQueryListenerAdapter(new EventListener() {

		@SuppressWarnings("unused")
		public void handleEvent(CqEvent event) {
			cqEvents.add(event);
		}
	});

	private ContinuousQueryListenerContainer container;

	private final List<CqEvent> cqEvents = new CopyOnWriteArrayList<>();

	private static GemFireCluster gemFireCluster;

	@BeforeClass
	public static void startGeodeServer() throws IOException {

		gemFireCluster = new GemFireCluster(System.getProperty("spring.test.gemfire.docker.image"), 1, 1)
				.withGfsh(false, "create region --name=test-cq --type=REPLICATE");

		gemFireCluster.acceptLicense().start();

		System.setProperty("gemfire.locator.port", String.valueOf(gemFireCluster.getLocatorPort()));
	}

	@AfterClass
	public static void shutdown() {
		gemFireCluster.close();
	}

	@Before
	public void setupGemFireClient() {

		gemfireCache = new ClientCacheFactory()
			.set("name", "ListenerContainerIntegrationTests")
			.set("log-level", "error")
			.setPoolSubscriptionEnabled(true)
			.addPoolLocator("localhost", gemFireCluster.getLocatorPort())
			.create();

		String query = "SELECT * from /test-cq";

		container = new ContinuousQueryListenerContainer();
		container.setBeanName("cqListenerContainer");
		container.setCache(gemfireCache);
		container.afterPropertiesSet();
		container.addListener(new ContinuousQueryDefinition("test", query, adapter));
		container.start();
	}

	@After
	public void closeGemFireClient() {

		Optional.ofNullable(this.gemfireCache)
			.ifPresent(cache -> SpringExtensions.safeDoOperation(cache::close));
	}

	@Test
	public void testContainer() {

		gemFireCluster.gfsh(false,
				"put --region=test-cq --key-class=java.lang.Integer --key=1 --value=one",
				"put --region=test-cq --key-class=java.lang.Integer --key=2 --value=two",
				"put --region=test-cq --key-class=java.lang.Integer --key=3 --value=three");

		IntegrationTestsSupport.waitOn(() -> this.cqEvents.size() == 3, TimeUnit.SECONDS.toMillis(5));

		Assertions.assertThat(this.cqEvents.size()).isEqualTo(3);
	}
}
