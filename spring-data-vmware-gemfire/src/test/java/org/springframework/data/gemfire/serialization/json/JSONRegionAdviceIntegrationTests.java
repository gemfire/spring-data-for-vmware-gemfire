/*
 * Copyright (c) VMware, Inc. 2022-2023. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.serialization.json;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.data.gemfire.util.RuntimeExceptionFactory.newIllegalArgumentException;

import java.util.Arrays;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.apache.geode.cache.Region;
import org.apache.geode.cache.query.SelectResults;

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
 * @see Region
 * @see GemfireOperations
 * @see JSONRegionAdvice
 * @see IntegrationTestsSupport
 * @see org.springframework.test.context.ContextConfiguration
 * @see org.springframework.test.context.junit4.SpringRunner
 * @see ObjectMapper
 */
@RunWith(SpringRunner.class)
@ContextConfiguration
@SuppressWarnings("unused")
public class JSONRegionAdviceIntegrationTests extends IntegrationTestsSupport {

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
