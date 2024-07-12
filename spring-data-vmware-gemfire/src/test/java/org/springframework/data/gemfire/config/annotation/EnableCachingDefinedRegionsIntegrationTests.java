/*
 * Copyright 2022-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.config.annotation;

import static org.assertj.core.api.Assertions.assertThat;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.cache.annotation.CacheDefaults;
import javax.cache.annotation.CacheRemoveAll;
import javax.cache.annotation.CacheResult;
import org.apache.geode.cache.client.ClientCache;
import org.apache.geode.cache.client.ClientRegionShortcut;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.data.gemfire.tests.integration.IntegrationTestsSupport;
import org.springframework.stereotype.Service;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Integration Tests for {@link EnableCachingDefinedRegions} and {@link CachingDefinedRegionsConfiguration}.
 *
 * @author John Blum
 * @see org.apache.geode.cache.client.ClientCache
 * @see org.apache.geode.cache.Region
 * @see org.springframework.data.gemfire.config.annotation.CachingDefinedRegionsConfiguration
 * @see org.springframework.data.gemfire.config.annotation.EnableCachingDefinedRegions
 * @see org.springframework.data.gemfire.tests.integration.IntegrationTestsSupport
 * @see org.springframework.test.context.ContextConfiguration
 * @see org.springframework.test.context.junit4.SpringRunner
 * @since 2.0.0
 */
@RunWith(SpringRunner.class)
@ContextConfiguration
@SuppressWarnings("unused")
public class EnableCachingDefinedRegionsIntegrationTests extends IntegrationTestsSupport {

	@Autowired
	private ApplicationContext applicationContext;

	@Autowired
	private JCacheEchoService jcacheEchoService;

	@Autowired
	private SpringCacheableEchoService springEchoService;

	@Autowired
	private ClientCache gemfireCache;

	@Before
	public void setup() {
		assertThat(this.gemfireCache).isNotNull();
	}

	@Test
	public void cacheRegionsExists() {

		assertThat(gemfireCache.getRegion("/Echo")).isNotNull();
		assertThat(gemfireCache.getRegion("/JCacheOne")).isNotNull();
		assertThat(gemfireCache.getRegion("/JCacheTwo")).isNotNull();
		assertThat(gemfireCache.getRegion("/SpringOne")).isNotNull();
	}

	@Test
	public void echoServiceCachingWithJCacheIsSuccessful() {

		assertThat(jcacheEchoService.isCacheMiss()).isFalse();
		assertThat(jcacheEchoService.echo("four")).isEqualTo("four");
		assertThat(jcacheEchoService.isCacheMiss()).isTrue();
		assertThat(jcacheEchoService.echo("five")).isEqualTo("five");
		assertThat(jcacheEchoService.isCacheMiss()).isTrue();
		assertThat(jcacheEchoService.echo("four")).isEqualTo("four");
		assertThat(jcacheEchoService.isCacheMiss()).isFalse();
		assertThat(jcacheEchoService.echo("six")).isEqualTo("six");
		assertThat(jcacheEchoService.isCacheMiss()).isTrue();
		assertThat(jcacheEchoService.echo("five")).isEqualTo("five");
		assertThat(jcacheEchoService.isCacheMiss()).isFalse();
	}

	@Test
	public void echoServiceCachingWithSpringIsSuccessful() {

		assertThat(springEchoService.isCacheMiss()).isFalse();
		assertThat(springEchoService.echo("one")).isEqualTo("one");
		assertThat(springEchoService.isCacheMiss()).isTrue();
		assertThat(springEchoService.echo("two")).isEqualTo("two");
		assertThat(springEchoService.isCacheMiss()).isTrue();
		assertThat(springEchoService.echo("one")).isEqualTo("one");
		assertThat(springEchoService.isCacheMiss()).isFalse();
		assertThat(springEchoService.echo("three")).isEqualTo("three");
		assertThat(springEchoService.isCacheMiss()).isTrue();
		assertThat(springEchoService.echo("two")).isEqualTo("two");
		assertThat(springEchoService.isCacheMiss()).isFalse();
	}

	@ClientCacheApplication(name = "EnableCachingDefinedRegionsIntegrationTests", logLevel = "error")
	@EnableCachingDefinedRegions(clientRegionShortcut = ClientRegionShortcut.LOCAL)
	static class TestConfiguration {

		@Bean
		JCacheEchoService jcacheEchoService() {
			return new JCacheEchoService();
		}

		@Bean
		SpringCacheableEchoService springEchoService() {
			return new SpringCacheableEchoService();
		}

		@Bean
		TestService testServiceOne() {
			return new SpringCachingTestService();
		}

		@Bean
		TestService testServiceTwo() {
			return new Jsr107CachingTestService();
		}
	}

	static abstract class AbstractCacheableService {

		private final AtomicBoolean cacheMiss = new AtomicBoolean(false);

		public boolean isCacheMiss() {
			return this.cacheMiss.compareAndSet(true, false);
		}

		public void setCacheMiss() {
			this.cacheMiss.set(true);
		}
	}

	@Service
	static class JCacheEchoService extends AbstractCacheableService {

		@CacheResult(cacheName = "Echo")
		public Object echo(String key) {
			setCacheMiss();
			return key;
		}
	}

	@Service
	static class SpringCacheableEchoService extends AbstractCacheableService {

		@Cacheable("Echo")
		public Object echo(String key) {
			setCacheMiss();
			return key;
		}
	}

	interface TestService {
		Object testMethod(String key);
	}

	@CacheDefaults(cacheName = "JCacheOne")
	@CacheRemoveAll(cacheName = "SpringOne")
	static class Jsr107CachingTestService implements TestService {

		@CacheResult(cacheName = "JCacheTwo")
		public Object testMethod(String key) {
			return null;
		}
	}

	static class SpringCachingTestService implements TestService {

		@CachePut("SpringOne")
		public Object testMethod(String key) {
			return "test";
		}
	}
}
