/*
 * Copyright (c) VMware, Inc. 2022-2023. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.config.annotation;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.Collections;
import java.util.Objects;
import java.util.stream.Collectors;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.apache.geode.cache.Region;
import org.apache.geode.cache.client.ClientCache;
import org.apache.geode.cache.client.ClientRegionShortcut;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Bean;
import org.springframework.data.gemfire.tests.integration.IntegrationTestsSupport;
import org.springframework.data.gemfire.tests.mock.annotation.EnableGemFireMockObjects;
import org.springframework.data.gemfire.util.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Integration Tests for {@link EnableCachingDefinedRegions} and {@link CachingDefinedRegionsConfiguration} asserting
 * that the annotation config supports the Spring Cache Abstractions {@link CacheConfig} annotation.
 *
 * @author John Blum
 * @see org.junit.Test
 * @see Region
 * @see ClientCache
 * @see CacheConfig
 * @see Cacheable
 * @see EnableCachingDefinedRegions
 * @see CachingDefinedRegionsConfiguration
 * @see IntegrationTestsSupport
 * @see EnableGemFireMockObjects
 * @see org.springframework.test.context.ContextConfiguration
 * @see org.springframework.test.context.junit4.SpringRunner
 * @see <a href="https://jira.spring.io/browse/DATAGEODE-232">Add support for @CacheConfig in @EnableCachingDefinedRegions</a>
 * @since 2.2.0
 */
@RunWith(SpringRunner.class)
@ContextConfiguration
@SuppressWarnings("unused")
public class CachingDefinedRegionsUsesCacheConfigCacheNamesIntegrationTests extends IntegrationTestsSupport {

	@Autowired
	private ClientCache clientCache;

	@Autowired
	private TestCacheableService service;

	@Autowired
	@Qualifier("A")
	private Region<Object, Object> a;

	@Autowired
	@Qualifier("B")
	private Region<Object, Object> b;

	@Autowired
	@Qualifier("C")
	private Region<Object, Object> c;

	@Autowired
	@Qualifier("D")
	private Region<Object, Object> d;

	@Before
	public void assertClientCacheAndRegionConfiguration() {

		assertThat(this.clientCache).isNotNull();

		assertThat(this.clientCache.getName())
			.isEqualTo(CachingDefinedRegionsUsesCacheConfigCacheNamesIntegrationTests.class.getSimpleName());

		assertThat(CollectionUtils.nullSafeSet(this.clientCache.rootRegions()).stream()
			.filter(Objects::nonNull)
			.map(Region::getName)
			.collect(Collectors.toSet())).containsExactlyInAnyOrder("A", "B", "C", "D");

		Arrays.asList(this.a, this.b, this.c, this.d).forEach(region -> {
			assertThat(region).isNotNull();
			assertThat(region).isEmpty();
		});
	}

	@After
	public void clearRegions() {
		Arrays.asList(this.a, this.b, this.c, this.d).forEach(Region::clear);
	}

	@Test
	public void testNonCacheableMethodCachesNothing() {

		assertThat(this.service.nonCacheableMethod("0")).isEqualTo("TEST");

		Arrays.asList(this.a, this.b, this.c, this.d).forEach(region -> {
			assertThat(region).doesNotContainKey("0");
			assertThat(region).isEmpty();
		});
	}

	@Test
	public void cacheableMethodOneCachesToAAndB() {

		assertThat(this.service.cacheableMethodOne("2")).isEqualTo("ONE");

		Arrays.asList(this.a, this.b).forEach(region -> assertThat(region).containsKey("2"));
		Arrays.asList(this.c, this.d).forEach(region -> assertThat(region).isEmpty());
	}

	@Test
	public void cacheableMethodTwoCachesToCOnly() {

		assertThat(this.service.cacheableMethodTwo("3")).isEqualTo("TWO");

		Collections.singletonList(this.c).forEach(region -> assertThat(region).containsKey("3"));
		Arrays.asList(this.a, this.b, this.d).forEach(region -> assertThat(region).isEmpty());
	}

	@Test
	public void cacheableMethodThreeCachesToAAndD() {

		assertThat(this.service.cacheableMethodThree("4")).isEqualTo("THREE");

		Arrays.asList(this.a, this.d).forEach(region -> assertThat(region).containsKey("4"));
		Arrays.asList(this.b, this.c).forEach(region -> assertThat(region).isEmpty());
	}

	@ClientCacheApplication(name = "CachingDefinedRegionsUsesCacheConfigCacheNamesIntegrationTests")
	@EnableCachingDefinedRegions(clientRegionShortcut = ClientRegionShortcut.LOCAL)
	@EnableGemFireMockObjects
	static class TestConfiguration {

		@Bean
		TestCacheableService testCachingService() {
			return new TestCacheableService();
		}
	}

	@Service
	@CacheConfig(cacheNames = { "A", "B" })
	static class TestCacheableService {

		public Object nonCacheableMethod(String key) {
			return "TEST";
		}

		@Cacheable
		public Object cacheableMethodOne(String key) {
			return "ONE";
		}

		@Cacheable("C")
		public Object cacheableMethodTwo(String key) {
			return "TWO";
		}

		@Cacheable({ "A", "D" })
		public Object cacheableMethodThree(String key) {
			return "THREE";
		}
	}
}
