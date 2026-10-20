/*
 * Copyright 2022-2025 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package org.springframework.data.gemfire.cache;


import java.util.Map;

import com.vmware.gemfire.driver.spi.DriverRegion;
import com.vmware.gemfire.driver.spi.DriverOperationException;
import java.util.concurrent.atomic.AtomicBoolean;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.util.Assert;

/**
 * Integration Tests testing the contract and functionality of Spring Framework's Cache Abstraction using Apache Geode
 * as a caching provider applied with Spring Data for Apache Geode.
 *
 * @author John Blum
 * @see org.springframework.cache.annotation.Cacheable
 * @since 1.5.1
 */
@SuppressWarnings("unused")
public class CachingWithGemFireIntegrationTests {

	private NamedNumbersService namedNumbersService;

	private DriverRegion<String, Integer> namedNumbersRegion;

	private ClassPathXmlApplicationContext context;

	@BeforeEach
	@SuppressWarnings("unchecked")
	void openContext() {
		context = new ClassPathXmlApplicationContext(
			"org/springframework/data/gemfire/cache/CachingWithGemFireIntegrationTests-context.xml");
		namedNumbersService = context.getBean(NamedNumbersService.class);
		namedNumbersRegion = (DriverRegion<String, Integer>) context.getBean("NamedNumbersRegion");
	}

	@AfterEach
	void closeContext() { if (context != null) context.close(); }

	@Test
	public void testRegionCacheHit() {

		Assertions.assertThat(namedNumbersRegion.get("eleven")).isNull();
		Assertions.assertThat(namedNumbersRegion.containsKey("eleven")).isFalse();

		namedNumbersRegion.put("eleven", 11);

		Assertions.assertThat(namedNumbersRegion.containsKey("eleven")).isTrue();
		Assertions.assertThat(namedNumbersService.get("eleven").intValue()).isEqualTo(11);
		Assertions.assertThat(namedNumbersService.wasCacheMiss()).isFalse();

		Assertions.assertThatThrownBy(() -> namedNumbersRegion.put("eleven", null))
			.isInstanceOf(DriverOperationException.class);
		Assertions.assertThat(namedNumbersRegion.containsKey("eleven")).isTrue();
		Assertions.assertThat(namedNumbersRegion.get("eleven")).isEqualTo(11);
		Assertions.assertThat(namedNumbersService.get("eleven")).isEqualTo(11);
		Assertions.assertThat(namedNumbersService.wasCacheMiss()).isFalse();
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

		private final AtomicBoolean cacheMiss = new AtomicBoolean();

		private Map<String, Integer> namedNumbers;

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
			this.cacheMiss.set(true);
			return namedNumbers.get(namedNumber);
		}

		public boolean wasCacheMiss() {
			return this.cacheMiss.getAndSet(false);
		}
	}
}
