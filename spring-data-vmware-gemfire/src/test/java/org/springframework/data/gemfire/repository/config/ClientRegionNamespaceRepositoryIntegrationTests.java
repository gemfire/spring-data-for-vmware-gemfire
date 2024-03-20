/*
 * Copyright 2022-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.repository.config;

import static org.assertj.core.api.Assertions.assertThat;

import com.vmware.gemfire.testcontainers.GemFireCluster;
import org.apache.geode.cache.Region;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.gemfire.repository.GemfireRepository;
import org.springframework.data.gemfire.repository.sample.Person;
import org.springframework.data.gemfire.repository.sample.PersonRepository;
import org.springframework.data.gemfire.tests.integration.ForkingClientServerIntegrationTestsSupport;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;

/**
 * Integration Tests testing and asserting basic (CRUD) data access operations using a SDG {@link GemfireRepository}
 * in a client/server topology configured with SDG's XML namespace and bootstrapped with Spring.
 *
 * @author David Turanski
 * @author John Blum
 * @see org.junit.Test
 * @see org.springframework.data.gemfire.fork.ServerProcess
 * @see org.springframework.data.gemfire.repository.GemfireRepository
 * @see org.springframework.data.gemfire.tests.integration.ForkingClientServerIntegrationTestsSupport
 * @see org.springframework.test.context.ContextConfiguration
 * @see org.springframework.test.context.junit4.SpringRunner
 */
@RunWith(SpringRunner.class)
@ContextConfiguration(locations = "ClientRegionNamespaceRepositoryIntegrationTests-context.xml")
@SuppressWarnings("unused")
public class ClientRegionNamespaceRepositoryIntegrationTests extends ForkingClientServerIntegrationTestsSupport {

	@Autowired
	private PersonRepository repository;

	@Autowired
	@Qualifier("simple")
	private Region<Long, Person> peopleRegion;

	private static GemFireCluster gemFireCluster;

	@BeforeClass
	public static void startGeodeServer() throws IOException {

		gemFireCluster = new GemFireCluster(System.getProperty("spring.test.gemfire.docker.image"), 1, 1)
				.withGfsh(false, "create region --name=simple --type=REPLICATE");

		gemFireCluster.acceptLicense().start();

		System.setProperty("gemfire.locator.port", String.valueOf(gemFireCluster.getLocatorPort()));
	}

	@AfterClass
	public static void shutdown() {
		gemFireCluster.close();
	}

	@Before
	public void setup() {
		peopleRegion.put(1L, new Person(1L, "First1", "Last1"));
		peopleRegion.put(2L, new Person(2L, "First2", "Last2"));
	}

	@Test
	public void findAllAndCountMatch() {
		assertThat(this.repository.count()).isEqualTo(2);
		assertThat(this.repository.findAll()).hasSize(2);
	}
}
