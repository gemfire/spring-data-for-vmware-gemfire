/*
 * Copyright (c) VMware, Inc. 2022-2024. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.client;

import java.io.IOException;

import org.apache.geode.cache.Region;
import org.apache.geode.cache.client.ClientCache;
import org.apache.geode.cache.client.Pool;
import org.assertj.core.api.Assertions;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import com.vmware.gemfire.testcontainers.GemFireCluster;

/**
 * Integration Tests for {@link ClientCache} {@link Pool Pools}.
 *
 * @author John Blum
 * @see org.junit.Test
 * @see org.apache.geode.cache.Region
 * @see org.apache.geode.cache.client.ClientCache
 * @see org.apache.geode.cache.client.Pool
 * @see org.springframework.data.gemfire.tests.integration.ForkingClientServerIntegrationTestsSupport
 * @see org.springframework.test.context.ContextConfiguration
 * @see org.springframework.test.context.junit4.SpringRunner
 * @since 1.0.0
 */
@RunWith(SpringRunner.class)
@ContextConfiguration
@SuppressWarnings("all")
public class ClientCachePoolIntegrationTests {

	private static GemFireCluster gemFireCluster;

	@BeforeClass
	public static void startGeodeServer() throws IOException {

		gemFireCluster = new GemFireCluster(System.getProperty("spring.test.gemfire.docker.image"), 1, 1);

		gemFireCluster.acceptLicense().start();

		gemFireCluster.gfsh(false, "create region --name=Factorials --type=REPLICATE");

		gemFireCluster.gfsh(false, "put --region=/Factorials --key-class=java.lang.Long --value-class=java.lang.Long --key=0 --value=1");
		gemFireCluster.gfsh(false, "put --region=/Factorials --key-class=java.lang.Long --value-class=java.lang.Long --key=1 --value=1");
		gemFireCluster.gfsh(false, "put --region=/Factorials --key-class=java.lang.Long --value-class=java.lang.Long --key=2 --value=2");
		gemFireCluster.gfsh(false, "put --region=/Factorials --key-class=java.lang.Long --value-class=java.lang.Long --key=3 --value=6");

		System.setProperty("gemfire.locator.port", String.valueOf(gemFireCluster.getLocatorPort()));
	}

	@AfterClass
	public static void shutdown() {
		gemFireCluster.close();
	}

	@Autowired
	@Qualifier("Factorials")
	private Region<Long, Long> factorials;

	@Test
	public void computeFactorials() {

		Assertions.assertThat(factorials.get(0l)).isEqualTo(1l);
		Assertions.assertThat(factorials.get(1l)).isEqualTo(1l);
		Assertions.assertThat(factorials.get(2l)).isEqualTo(2l);
		Assertions.assertThat(factorials.get(3l)).isEqualTo(6l);
	}
}
