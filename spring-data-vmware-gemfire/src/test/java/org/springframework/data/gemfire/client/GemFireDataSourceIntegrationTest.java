/*
 * Copyright 2022-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.client;

import com.vmware.gemfire.testcontainers.GemFireCluster;
import org.apache.geode.cache.DataPolicy;
import org.apache.geode.cache.Region;
import org.apache.geode.cache.client.ClientCache;
import org.apache.geode.cache.client.Pool;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.data.gemfire.GemfireUtils;
import org.springframework.data.gemfire.repository.sample.Person;
import org.springframework.data.gemfire.repository.sample.PersonRepository;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import javax.sql.DataSource;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration Tests with test cases testing the contract and functionality of the {@link GemfireDataSourcePostProcessor}
 * using the &lt;gfe-data:datasource&gt; element in Spring config to setup a {@link ClientCache} connecting to a native,
 * non-Spring configured Apache Geode Server as the {@link DataSource} to assert that client {@link Region} proxies
 * are registered as Spring beans in the Spring {@link ApplicationContext} correctly.
 *
 * @author John Blum
 * @see org.junit.Test
 * @see org.apache.geode.cache.Region
 * @see org.apache.geode.cache.client.ClientCache
 * @see org.springframework.data.gemfire.client.GemfireDataSourcePostProcessor
 * @see org.springframework.data.gemfire.tests.integration.ForkingClientServerIntegrationTestsSupport
 * @see org.springframework.test.context.ContextConfiguration
 * @see org.springframework.test.context.junit4.SpringRunner
 * @since 1.7.0
 */
@RunWith(SpringRunner.class)
@ContextConfiguration
@SuppressWarnings({ "rawtypes", "unused"})
public class GemFireDataSourceIntegrationTest {

	private static GemFireCluster gemFireCluster;

	@BeforeClass
	public static void startGeodeServer() throws IOException {

		gemFireCluster = new GemFireCluster(System.getProperty("spring.test.gemfire.docker.image"), 1, 1)
				.withCacheXml(GemFireCluster.ALL_GLOB, "/gemfire-datasource-integration-tests-cache.xml");

		gemFireCluster.acceptLicense().start();

		System.setProperty("gemfire.locator.port",String.valueOf(gemFireCluster.getLocatorPort()));
	}

	@AfterClass
	public static void shutdown() {
		gemFireCluster.close();
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

	private void assertRegion(Region<?, ?> region, String name, DataPolicy dataPolicy) {
		assertRegion(region, name, GemfireUtils.toRegionPath("simple"), dataPolicy);
	}

	private void assertRegion(Region<?, ?> region, String name, String fullPath, DataPolicy dataPolicy) {

		assertThat(region).isNotNull();
		assertThat(region.getName()).isEqualTo(name);
		assertThat(region.getFullPath()).isEqualTo(fullPath);
		assertThat(region.getAttributes()).isNotNull();
		assertThat(region.getAttributes().getDataPolicy()).isEqualTo(dataPolicy);
	}

	@Test
	public void clientProxyRegionBeansExist() {

		assertRegion(clientOnlyRegion, "ClientOnlyRegion");
		assertRegion(clientServerRegion, "ClientServerRegion");
		assertRegion(serverOnlyRegion, "ServerOnlyRegion");
	}

	@Test
	public void gemfireServerDataSourceCreated() {

		Pool pool = this.applicationContext.getBean("gemfirePool", Pool.class);

		assertThat(pool).isNotNull();

		List<String> regionList = Arrays.asList(this.applicationContext.getBeanNamesForType(Region.class));

		assertThat(regionList).hasSize(5);
		assertThat(regionList).containsExactlyInAnyOrder("ClientOnlyRegion", "ClientServerRegion", "ServerOnlyRegion", "AnotherServerRegion", "simple");

		Region<?, ?> simple = this.applicationContext.getBean("simple", Region.class);

		assertRegion(simple, "simple", DataPolicy.EMPTY);
	}

	@Test
	public void repositoryCreatedAndFunctional() {

		Person daveMathews = new Person(1L, "Dave", "Mathews");

		PersonRepository repository = this.applicationContext.getBean(PersonRepository.class);

		assertThat(repository.save(daveMathews)).isSameAs(daveMathews);

		Optional<Person> result = repository.findById(1L);

		assertThat(result.isPresent()).isTrue();
		assertThat(result.map(Person::getFirstname).orElse(null)).isEqualTo("Dave");
	}
}
