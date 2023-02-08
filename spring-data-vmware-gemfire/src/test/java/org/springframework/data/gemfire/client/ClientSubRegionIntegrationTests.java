/*
 * Copyright (c) VMware, Inc. 2022-2023. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.client;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.apache.geode.cache.Region;
import org.apache.geode.cache.client.ClientCache;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.gemfire.GemfireTemplate;
import org.springframework.data.gemfire.fork.ServerProcess;
import org.springframework.data.gemfire.tests.integration.ForkingClientServerIntegrationTestsSupport;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Integration Tests for client {@link Region sub-Region} configuration.
 *
 * @author John Blum
 * @see Test
 * @see Region
 * @see ClientCache
 * @see ForkingClientServerIntegrationTestsSupport
 * @see ContextConfiguration
 * @see SpringRunner
 * @since 1.4.0
 */
@RunWith(SpringRunner.class)
@ContextConfiguration
@SuppressWarnings("unused")
public class ClientSubRegionIntegrationTests extends ForkingClientServerIntegrationTestsSupport {

	@BeforeClass
	public static void startGeodeServer() throws Exception {
		startGemFireServer(ServerProcess.class,
			getServerContextXmlFileLocation(ClientSubRegionIntegrationTests.class));
	}

	@Autowired
	private ClientCache clientCache;

	@Autowired
	@Qualifier("parentTemplate")
	private GemfireTemplate parentTemplate;

	@Autowired
	@Qualifier("childTemplate")
	private GemfireTemplate childTemplate;

	@Autowired
	@Qualifier("Parent")
	private Region<?, ?> parent;

	@Autowired
	@Qualifier("/Parent/Child")
	private Region<?, ?> child;

	private void assertRegion(Region<?, ?> region, String name) {
		assertRegion(region, name, String.format("%1$s%2$s", Region.SEPARATOR, name));
	}

	private void assertRegion(Region<?, ?> region, String name, String fullPath) {

		assertThat(region).isNotNull();
		assertThat(region.getName()).isEqualTo(name);
		assertThat(region.getFullPath()).isEqualTo(fullPath);
	}

	@Test
	public void gemFireSubRegionCreationConfigurationIsCorrect() {

		assertThat(this.clientCache)
			.describedAs("The Client Cache was not properly initialized")
			.isNotNull();

		Region<?, ?> parent = this.clientCache.getRegion("Parent");

		assertRegion(parent, "Parent");

		Region<?, ?> child = parent.getSubregion("Child");

		assertRegion(child, "Child", "/Parent/Child");

		Region<?, ?> clientCacheChild = this.clientCache.getRegion("/Parent/Child");

		assertThat(child).isSameAs(clientCacheChild);
	}

	@Test
	public void springSubRegionCreationConfigurationIsCorrect() {

		assertRegion(this.parent, "Parent");
		assertRegion(this.child, "Child", "/Parent/Child");
	}

	@Test
	public void templateCreationConfigurationIsCorrect() {

		assertThat(this.parentTemplate).isNotNull();
		assertThat(this.parentTemplate.getRegion()).isSameAs(this.parent);
		assertThat(this.childTemplate).isNotNull();
		assertThat(this.childTemplate.getRegion()).isSameAs(this.child);
	}
}
