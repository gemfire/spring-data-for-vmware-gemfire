/*
 * Copyright 2022-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.client;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;

import com.vmware.gemfire.testcontainers.GemFireCluster;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.apache.geode.cache.Region;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.testcontainers.utility.MountableFile;

/**
 * Integration Tests to test SSL configuration between a Pivotal GemFire or Apache Geode client and server
 * using GemFire/Geode System properties.
 *
 * @author John Blum
 * @see org.junit.Test
 * @see org.springframework.test.context.ContextConfiguration
 * @see org.springframework.test.context.junit4.SpringRunner
 * @since 1.7.0
 */
@RunWith(SpringRunner.class)
@ContextConfiguration
@SuppressWarnings("all")
public class ClientCacheSecurityIntegrationTests {

	private static GemFireCluster gemFireCluster;

	@BeforeClass
	public static void startGemFire() throws IOException {
		org.springframework.core.io.Resource trustedKeystore = new ClassPathResource("trusted.keystore");
		System.setProperty("javax.net.ssl.keyStore", trustedKeystore.getFile().getAbsolutePath());

		gemFireCluster = new GemFireCluster(System.getProperty("spring.test.gemfire.docker.image"), 1, 1)
				.withCacheXml(GemFireCluster.ALL_GLOB, "/client-cache-security-integration-tests-cache.xml")
				.withConfiguration(GemFireCluster.ALL_GLOB, container ->
						container.withCopyFileToContainer(MountableFile.forClasspathResource("trusted.keystore"), "/trusted.keystore"))
				.withGemFireProperty(GemFireCluster.ALL_GLOB, "javax.net.ssl.keyStore", "/trusted.keystore")
				.withGemFireProperty(GemFireCluster.ALL_GLOB, "ssl-keystore", "/trusted.keystore")
				.withGemFireProperty(GemFireCluster.ALL_GLOB, "ssl-keystore-password", "s3cr3t")
				.withGemFireProperty(GemFireCluster.ALL_GLOB, "ssl-truststore", "/trusted.keystore")
				.withGemFireProperty(GemFireCluster.ALL_GLOB, "ssl-truststore-password", "s3cr3t")
				.withGemFireProperty(GemFireCluster.ALL_GLOB, "ssl-enabled-components", "all");

		gemFireCluster.acceptLicense().start();

		System.setProperty("gemfire.locator.port", String.valueOf(gemFireCluster.getLocatorPort()));
		System.setProperty("spring.data.gemfire.cache.server.port", String.valueOf(gemFireCluster.getServerPorts().get(0)));
	}

	@AfterClass
	public static void shutdown() {
		gemFireCluster.close();
	}

	@Autowired
	@Qualifier("Example")
	private Region<String, String> example;

	@Test
	public void exampleRegionGet() {
		assertThat(String.valueOf(example.get("TestKey"))).isEqualTo("TestValue");
	}
}
