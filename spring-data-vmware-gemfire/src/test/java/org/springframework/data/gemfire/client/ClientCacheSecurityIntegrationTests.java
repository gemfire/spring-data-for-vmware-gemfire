/*
 * Copyright (c) VMware, Inc. 2022-2023. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.client;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.apache.geode.cache.CacheLoader;
import org.apache.geode.cache.CacheLoaderException;
import org.apache.geode.cache.LoaderHelper;
import org.apache.geode.cache.Region;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.gemfire.fork.ServerProcess;
import org.springframework.data.gemfire.tests.integration.ForkingClientServerIntegrationTestsSupport;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Integration Tests to test SSL configuration between a Pivotal GemFire or Apache Geode client and server
 * using GemFire/Geode System properties.
 *
 * @author John Blum
 * @see Test
 * @see ContextConfiguration
 * @see SpringRunner
 * @since 1.7.0
 */
@RunWith(SpringRunner.class)
@ContextConfiguration
@SuppressWarnings("all")
public class ClientCacheSecurityIntegrationTests extends ForkingClientServerIntegrationTestsSupport {

	@BeforeClass
	public static void startGeodeServer() throws IOException {

		List<String> arguments = new ArrayList<String>();

		org.springframework.core.io.Resource trustedKeystore = new ClassPathResource("trusted.keystore");

		//System.err.printf("trusted.keystore file is located at [%s]%n", trustedKeystore.getFile().getAbsolutePath());

		arguments.add(String.format("-Dgemfire.name=%s",
			asApplicationName(ClientCacheSecurityIntegrationTests.class).concat("Server")));

		arguments.add(String.format("-Djavax.net.ssl.keyStore=%s", trustedKeystore.getFile().getAbsolutePath()));

		arguments.add(getServerContextXmlFileLocation(ClientCacheSecurityIntegrationTests.class));

		startGemFireServer(ServerProcess.class, arguments.toArray(new String[arguments.size()]));

		System.setProperty("javax.net.ssl.keyStore", trustedKeystore.getFile().getAbsolutePath());
	}

	@Autowired
	@Qualifier("Example")
	private Region<String, String> example;

	@Test
	public void exampleRegionGet() {
		assertThat(String.valueOf(example.get("TestKey"))).isEqualTo("TestValue");
	}

	@SuppressWarnings("unused")
	public static class TestCacheLoader implements CacheLoader<String, String> {

		@Override
		public String load(LoaderHelper<String, String> helper) throws CacheLoaderException {
			return "TestValue";
		}

		@Override
		public void close() { }

	}
}
