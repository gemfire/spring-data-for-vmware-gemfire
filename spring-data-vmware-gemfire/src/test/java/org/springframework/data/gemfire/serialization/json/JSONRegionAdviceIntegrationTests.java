/*
 * Copyright 2022-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.serialization.json;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.data.gemfire.util.RuntimeExceptionFactory.newIllegalArgumentException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vmware.gemfire.testcontainers.GemFireCluster;
import java.util.Arrays;
import java.util.Map;
import org.apache.geode.cache.Region;
import org.apache.geode.cache.query.SelectResults;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.gemfire.GemfireOperations;
import org.springframework.data.gemfire.repository.sample.Person;
import org.springframework.data.gemfire.test.support.MapBuilder;
import org.springframework.data.gemfire.tests.integration.IntegrationTestsSupport;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Integration Tests for storing and reading JSON data to and from a {@link Region} by (un)marshalling JSON data
 * using Jackson.
 *
 * @author David Turanski
 * @author John Blum
 * @see org.apache.geode.cache.Region
 * @see org.springframework.data.gemfire.GemfireOperations
 * @see org.springframework.data.gemfire.serialization.json.JSONRegionAdvice
 * @see org.springframework.data.gemfire.tests.integration.IntegrationTestsSupport
 * @see org.springframework.test.context.ContextConfiguration
 * @see org.springframework.test.context.junit4.SpringRunner
 * @see com.fasterxml.jackson.databind.ObjectMapper
 */
@RunWith(SpringRunner.class)
@ContextConfiguration
@SuppressWarnings("unused")
public class JSONRegionAdviceIntegrationTests extends IntegrationTestsSupport {

	private static GemFireCluster gemFireCluster;

	@BeforeClass
	public static void startCluster() {
		gemFireCluster = new GemFireCluster(System.getProperty("spring.test.gemfire.docker.image"), 1, 1)
				.withGfsh(false, "create region --name=JsonRegion --type=REPLICATE");

		gemFireCluster.acceptLicense().start();

		System.setProperty("spring.data.gemfire.cache.server.port", String.valueOf(gemFireCluster.getServerPorts().get(0)));
	}

	@AfterClass
	public static void shutdown() {
		gemFireCluster.close();
	}

	private static String toJson(Object bean) {

		try {
			return new ObjectMapper().writeValueAsString(bean);
		}
		catch (JsonProcessingException cause) {
			throw newIllegalArgumentException(cause, "Failed to convert object [%s] into JSON", bean);
		}
	}

	@Autowired
	private GemfireOperations template;

	@Autowired
	@Qualifier("JsonRegion")
	private Region<Object, Object> jsonRegion;

	@Before
	public void setup() {
		this.jsonRegion.clear();
	}

	@Test
	public void putAndCreate() {

		String json = "{\"hello\":\"world\"}";

		this.jsonRegion.put("keyOne", json);

		assertThat(this.jsonRegion.put("keyOne", json)).isEqualTo(json);

		this.jsonRegion.create("keyTwo", json);

		assertThat(this.jsonRegion.get("keyTwo")).isEqualTo(json);
	}

	@Test
	public void putAll() {

		Map<String, String> map = MapBuilder.<String, String>newMapBuilder()
			.put("key1", "{\"hello1\":\"world1\"}")
			.put("key2", "{\"hello2\":\"world2\"}")
			.build();

		this.jsonRegion.putAll(map);

		Map<Object, Object> results = this.jsonRegion.getAll(Arrays.asList("key1", "key2"));

		assertThat(results.get("key1")).isEqualTo("{\"hello1\":\"world1\"}");
		assertThat(results.get("key2")).isEqualTo("{\"hello2\":\"world2\"}");
	}

	@Test
	public void objectToJSon() {

		Person davidTuranski = new Person(1L, "David", "Turanski");

		this.jsonRegion.put("dave", davidTuranski);

		String json = String.valueOf(this.jsonRegion.get("dave"));

		assertThat(toJson(davidTuranski)).isEqualTo(json);

		Object result = jsonRegion.put("dave", davidTuranski);

		assertThat(result).isEqualTo(toJson(davidTuranski));
	}

	@Test
	public void templateFind() {

		Person davidTuranski = new Person(1L, "David", "Turanski");

		this.jsonRegion.put("dave", davidTuranski);

		SelectResults<String> results = this.template.find(String.format("SELECT * FROM %s WHERE firstname=$1",
			this.jsonRegion.getFullPath()), davidTuranski.getFirstname());

		assertThat(results.iterator().next()).isEqualTo(toJson(davidTuranski));
	}

	@Test
	public void templateFindUnique() {

		Person davidTuranski = new Person(1L, "David", "Turanski");

		this.jsonRegion.put("dave", davidTuranski);

		String json = this.template.findUnique(String.format("SELECT * FROM %s WHERE firstname=$1",
			this.jsonRegion.getFullPath()), davidTuranski.getFirstname());

		assertThat(json).isEqualTo(toJson(davidTuranski));
	}

	@Test
	public void templateQuery() {

		Person davidTuranski = new Person(1L, "David", "Turanski");

		this.jsonRegion.put("dave", davidTuranski);

		SelectResults<String> results =
			this.template.query(String.format("firstname='%s'", davidTuranski.getFirstname()));

		assertThat(results.iterator().next()).isEqualTo(toJson(davidTuranski));
	}
}
