/*
 * Copyright 2022-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.client;

import static org.assertj.core.api.Assertions.assertThat;

import com.vmware.gemfire.testcontainers.GemFireCluster;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.apache.geode.cache.Region;
import org.apache.geode.cache.client.ClientCache;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.gemfire.GemfireTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;

/**
 * Integration Tests for client {@link Region sub-Region} configuration.
 *
 * @author John Blum
 * @see org.junit.Test
 * @see org.apache.geode.cache.Region
 * @see org.apache.geode.cache.client.ClientCache
 * @see org.springframework.test.context.ContextConfiguration
 * @see org.springframework.test.context.junit4.SpringRunner
 * @since 1.4.0
 */
@RunWith(SpringRunner.class)
@ContextConfiguration
@SuppressWarnings("unused")
public class ClientSubRegionIntegrationTests {

	private static GemFireCluster gemFireCluster;
	@BeforeClass
	public static void startGeodeServer() throws IOException {

		gemFireCluster = new GemFireCluster(System.getProperty("spring.test.gemfire.docker.image"), 1, 1);

		gemFireCluster.acceptLicense().start();

		gemFireCluster.gfsh(false, "create region --name=Parent --type=REPLICATE");
		gemFireCluster.gfsh(false, "create region --name=Parent/Child --type=REPLICATE");

		System.setProperty("gemfire.locator.port",String.valueOf(gemFireCluster.getLocatorPort()));
		System.setProperty("spring.data.gemfire.locator.port", String.valueOf(gemFireCluster.getLocatorPort()));
	}

	@AfterClass
	public static void shutdown() {
		gemFireCluster.close();
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
