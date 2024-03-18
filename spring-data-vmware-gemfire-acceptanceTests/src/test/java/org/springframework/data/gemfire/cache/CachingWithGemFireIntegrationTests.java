/*
 * Copyright (c) VMware, Inc. 2022-2024. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package org.springframework.data.gemfire.cache;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;

import java.util.Map;

import org.apache.geode.cache.Region;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.gemfire.tests.integration.IntegrationTestsSupport;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.Assert;

/**
 * Integration Tests testing the contract and functionality of Spring Framework's Cache Abstraction using Apache Geode
 * as a caching provider applied with Spring Data for Apache Geode.
 *
 * @author John Blum
 * @see org.junit.Test
 * @see org.junit.runner.RunWith
 * @see org.springframework.cache.annotation.Cacheable
 * @see org.springframework.data.gemfire.tests.integration.IntegrationTestsSupport
 * @see org.springframework.test.context.ActiveProfiles
 * @see org.springframework.test.context.ContextConfiguration
 * @see org.springframework.test.context.junit4.SpringRunner
 * @since 1.5.1
 */
@RunWith(SpringRunner.class)
@ContextConfiguration
@ActiveProfiles("replica")
@SuppressWarnings("unused")
public class CachingWithGemFireIntegrationTests extends IntegrationTestsSupport {

	@Autowired
	private NamedNumbersService namedNumbersService;

	@Resource(name = "NamedNumbersRegion")
	private Region<String, Integer> namedNumbersRegion;

	@Test(expected = NullPointerException.class)
	public void testRegionCacheHit() {

		Assertions.assertThat(namedNumbersRegion.get("eleven")).isNull();
		Assertions.assertThat(namedNumbersRegion.containsKey("eleven")).isFalse();

		namedNumbersRegion.put("eleven", 11);

		Assertions.assertThat(namedNumbersRegion.containsKey("eleven")).isTrue();
		Assertions.assertThat(namedNumbersService.get("eleven").intValue()).isEqualTo(11);
		Assertions.assertThat(namedNumbersService.wasCacheMiss()).isFalse();

		try {
			namedNumbersRegion.put("eleven", null); // GemFire does not accept null values on put(key, value)
		}
		finally {
			Assertions.assertThat(namedNumbersRegion.containsKey("eleven")).isTrue();
			Assertions.assertThat(namedNumbersRegion.get("eleven").intValue()).isEqualTo(11);
			Assertions.assertThat(namedNumbersService.get("eleven").intValue()).isEqualTo(11);
			Assertions.assertThat(namedNumbersService.wasCacheMiss()).isFalse();
		}
	}

	@Test
	public void testRegionCaching() {

		Assertions.assertThat(namedNumbersService.wasCacheMiss()).isFalse();
		Assertions.assertThat(namedNumbersService.get("one").intValue()).isEqualTo(1);
		Assertions.assertThat(namedNumbersService.wasCacheMiss()).isTrue();
		Assertions.assertThat(namedNumbersService.get("one").intValue()).isEqualTo(1);
		Assertions.assertThat(namedNumbersService.wasCacheMiss()).isFalse();
		Assertions.assertThat(namedNumbersService.get("two").intValue()).isEqualTo(2);
		Assertions.assertThat(namedNumbersService.wasCacheMiss()).isTrue();
		Assertions.assertThat(namedNumbersService.get("two").intValue()).isEqualTo(2);
		Assertions.assertThat(namedNumbersService.wasCacheMiss()).isFalse();
		Assertions.assertThat(namedNumbersService.get("twelve")).isNull();
		Assertions.assertThat(namedNumbersService.wasCacheMiss()).isTrue();
		Assertions.assertThat(namedNumbersService.get("twelve")).isNull();
		Assertions.assertThat(namedNumbersService.wasCacheMiss()).isTrue();
	}

	public static class NamedNumbersService {

		private NamedNumbersInMemoryRepository namedNumbersRepo;

		public final void setNamedNumbersRepo(final NamedNumbersInMemoryRepository namedNumbersRepo) {
			Assert.notNull(namedNumbersRepo, "The 'NamedNumbers' Repository must not be null");
			this.namedNumbersRepo = namedNumbersRepo;
		}

		protected NamedNumbersInMemoryRepository getNamedNumbersRepo() {
			Assert.state(namedNumbersRepo != null,
				"A reference to the 'NamedNumbers' Repository was not properly configured and initialized");
			return namedNumbersRepo;
		}

		@Cacheable("NamedNumbersRegion")
		public Integer get(final String namedNumber) {
			return getNamedNumbersRepo().get(namedNumber);
		}

		public boolean wasCacheMiss() {
			return getNamedNumbersRepo().wasCacheMiss();
		}
	}

	public static class NamedNumbersInMemoryRepository {

		private volatile boolean cacheMiss;

		private Map<String, Integer> namedNumbers;

		@PostConstruct
		public void init() {
			getNamedNumbers();
		}

		public final void setNamedNumbers(final Map<String, Integer> namedNumbers) {
			Assert.notNull(namedNumbers, "The reference to the 'NamedNumbers' Map must not be null");
			this.namedNumbers = namedNumbers;
		}

		protected Map<String, Integer> getNamedNumbers() {
			Assert.state(namedNumbers != null, "The 'NamedNumbers' Map was not properly configured and initialized");
			return namedNumbers;
		}

		public Integer get(final String namedNumber) {
			this.cacheMiss = true;
			return namedNumbers.get(namedNumber);
		}

		public boolean wasCacheMiss() {
			boolean localCacheMiss = this.cacheMiss;
			this.cacheMiss = false;
			return localCacheMiss;
		}
	}
}
