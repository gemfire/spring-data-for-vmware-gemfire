/*
 * Copyright 2022-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.client;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.apache.geode.cache.Region;
import org.apache.geode.cache.client.ClientCache;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.data.gemfire.GemfireUtils;
import org.springframework.data.gemfire.fork.ServerProcess;
import org.springframework.data.gemfire.tests.integration.ForkingClientServerIntegrationTestsSupport;
import org.springframework.data.gemfire.tests.process.ProcessWrapper;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.FileSystemUtils;

/**
 * Integration Tests with test cases testing the contract and functionality of the &lt;gfe-data:datasource&gt; element
 * in the context of an Apache Geode cluster running both native, non-Spring configured Geode Server(s) in addition to
 * Spring configured and bootstrapped Geode Server(s).
 *
 * @author John Blum
 * @see org.junit.Test
 * @see Region
 * @see ClientCache
 * @see GemfireDataSourcePostProcessor
 * @see ServerProcess
 * @see ForkingClientServerIntegrationTestsSupport
 * @see org.springframework.test.context.ContextConfiguration
 * @see org.springframework.test.context.junit4.SpringRunner
 * @since 1.7.0
 */
@RunWith(SpringRunner.class)
@ContextConfiguration
@SuppressWarnings({ "rawtypes", "unused"})
public class GemFireDataSourceIntegrationTest extends ForkingClientServerIntegrationTestsSupport {

	@BeforeClass
	public static void startGeodeServer() throws IOException {

		List<String> arguments = new ArrayList<>();

		arguments.add(String.format("-Dgemfire.name=%s",
			GemFireDataSourceIntegrationTest.class.getSimpleName().concat("Server")));

		arguments.add(getServerContextXmlFileLocation(GemFireDataSourceIntegrationTest.class));

		startGemFireServer(ServerProcess.class, arguments.toArray(new String[0]));
	}

	@AfterClass
	public static void stopGemFireServer() {

		if (Boolean.parseBoolean(System.getProperty("spring.gemfire.fork.clean", Boolean.TRUE.toString()))) {
			getGemFireServerProcess()
				.map(ProcessWrapper::getWorkingDirectory)
				.ifPresent(workingDirectory -> FileSystemUtils.deleteRecursively(workingDirectory));
		}
	}

	@Autowired
	private ApplicationContext applicationContext;

	@Autowired
	private ClientCache gemfireClientCache;

	@Autowired
	@Qualifier("ClientOnlyRegion")
	private Region clientOnlyRegion;

	@Autowired
	@Qualifier("ClientServerRegion")
	private Region clientServerRegion;

	@Autowired
	@Qualifier("ServerOnlyRegion")
	private Region serverOnlyRegion;

	@SuppressWarnings("unchecked")
	private void assertRegion(Region actualRegion, String expectedRegionName) {

		assertThat(actualRegion).isNotNull();
		assertThat(actualRegion.getName()).isEqualTo(expectedRegionName);
		assertThat(actualRegion.getFullPath()).isEqualTo(GemfireUtils.toRegionPath(expectedRegionName));
		assertThat(gemfireClientCache.getRegion(actualRegion.getFullPath())).isSameAs(actualRegion);
		assertThat(applicationContext.containsBean(expectedRegionName)).isTrue();
		assertThat(applicationContext.getBean(expectedRegionName, Region.class)).isSameAs(actualRegion);
	}

	@Test
	public void clientProxyRegionBeansExist() {

		assertRegion(clientOnlyRegion, "ClientOnlyRegion");
		assertRegion(clientServerRegion, "ClientServerRegion");
		assertRegion(serverOnlyRegion, "ServerOnlyRegion");
	}
}
