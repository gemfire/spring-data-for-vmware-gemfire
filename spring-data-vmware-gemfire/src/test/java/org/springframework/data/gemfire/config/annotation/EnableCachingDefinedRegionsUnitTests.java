/*
 * Copyright $originalComment.match(" (\d+)", 1, "-", $today.year)2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.config.annotation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.data.gemfire.util.CollectionUtils.asSet;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.cache.annotation.CacheDefaults;
import javax.cache.annotation.CacheRemove;
import javax.cache.annotation.CacheRemoveAll;
import javax.cache.annotation.CacheResult;
import org.springframework.data.gemfire.gud.api.GudRegion;
import org.springframework.data.gemfire.gud.api.GudRegionShortcut;
import org.springframework.data.gemfire.gud.api.GudClientCache;
import org.springframework.data.gemfire.gud.api.GudClientRegionShortcut;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.data.gemfire.client.ClientRegionFactoryBean;
import org.springframework.data.gemfire.test.support.MapBuilder;
import org.springframework.data.gemfire.tests.mock.GemFireMockObjectsSupport;
import org.springframework.stereotype.Service;

/**
 * Unit Tests for {@link EnableCachingDefinedRegions} and {@link CachingDefinedRegionsConfiguration}.
 *
 * @author John Blum
 * @see org.junit.Test
 * @see org.apache.geode.cache.GudRegion
 * @see org.springframework.cache.annotation.Cacheable
 * @see org.springframework.cache.annotation.CacheEvict
 * @see org.springframework.cache.annotation.CachePut
 * @see org.springframework.cache.annotation.Caching
 * @see org.springframework.data.gemfire.config.annotation.CachingDefinedRegionsConfiguration
 * @see org.springframework.data.gemfire.config.annotation.EnableCachingDefinedRegions
 * @see org.springframework.data.gemfire.tests.mock.GemFireMockObjectsSupport
 * @see org.springframework.stereotype.Service
 * @since 2.0.0
 */
public class EnableCachingDefinedRegionsUnitTests {

	// Subject Under Test (SUT)
	private CachingDefinedRegionsConfiguration configuration;

	@Before
	public void setup() {
		this.configuration = new CachingDefinedRegionsConfiguration();
		GemFireMockObjectsSupport.destroy();
	}

	@Test
	public void annotationTypeIsEnableCachingDefinedRegions() {
		assertThat(this.configuration.getAnnotationType()).isEqualTo(EnableCachingDefinedRegions.class);
	}

	@Test
	public void getCacheNameResolverIsConfiguredProperly() {
		assertThat(this.configuration.getCacheNameResolver()).isNotNull();
	}

	@Test
	public void setGetAndResolveClientRegionShortcut() {

		assertThat(this.configuration.getClientRegionShortcut().orElse(null))
			.isEqualTo(GudClientRegionShortcut.PROXY);
		assertThat(this.configuration.resolveClientRegionShortcut()).isEqualTo(GudClientRegionShortcut.PROXY);

		this.configuration.setClientRegionShortcut(GudClientRegionShortcut.LOCAL);

		assertThat(this.configuration.getClientRegionShortcut().orElse(null))
			.isEqualTo(GudClientRegionShortcut.LOCAL);
		assertThat(this.configuration.resolveClientRegionShortcut()).isEqualTo(GudClientRegionShortcut.LOCAL);

		this.configuration.setClientRegionShortcut(null);

		assertThat(this.configuration.getClientRegionShortcut().orElse(null)).isNull();
		assertThat(this.configuration.resolveClientRegionShortcut()).isEqualTo(GudClientRegionShortcut.PROXY);

		this.configuration.setClientRegionShortcut(GudClientRegionShortcut.CACHING_PROXY);

		assertThat(this.configuration.getClientRegionShortcut().orElse(null))
			.isEqualTo(GudClientRegionShortcut.CACHING_PROXY);
		assertThat(this.configuration.resolveClientRegionShortcut()).isEqualTo(GudClientRegionShortcut.CACHING_PROXY);
	}

	@Test
	public void setGetAndResolvePoolName() {

		assertThat(this.configuration.getPoolName().orElse(null))
			.isEqualTo(ClientRegionFactoryBean.DEFAULT_POOL_NAME);
		assertThat(this.configuration.resolvePoolName()).isEqualTo(ClientRegionFactoryBean.DEFAULT_POOL_NAME);

		this.configuration.setPoolName(null);

		assertThat(this.configuration.getPoolName().orElse(null)).isNull();
		assertThat(this.configuration.resolvePoolName()).isEqualTo(ClientRegionFactoryBean.DEFAULT_POOL_NAME);

		this.configuration.setPoolName("TestPool");

		assertThat(this.configuration.getPoolName().orElse(null)).isEqualTo("TestPool");
		assertThat(this.configuration.resolvePoolName()).isEqualTo("TestPool");
	}

	@Test
	public void setGetAndResolveServerRegionShortcut() {

		assertThat(this.configuration.getServerRegionShortcut().orElse(null)).isEqualTo(GudRegionShortcut.REPLICATE);
		assertThat(this.configuration.resolveServerRegionShortcut()).isEqualTo(GudRegionShortcut.REPLICATE);

		this.configuration.setServerRegionShortcut(GudRegionShortcut.LOCAL);

		assertThat(this.configuration.getServerRegionShortcut().orElse(null)).isEqualTo(GudRegionShortcut.LOCAL);
		assertThat(this.configuration.resolveServerRegionShortcut()).isEqualTo(GudRegionShortcut.LOCAL);

		this.configuration.setServerRegionShortcut(null);

		assertThat(this.configuration.getServerRegionShortcut().orElse(null)).isNull();
		assertThat(this.configuration.resolveServerRegionShortcut()).isEqualTo(GudRegionShortcut.REPLICATE);

		this.configuration.setServerRegionShortcut(GudRegionShortcut.REPLICATE);

		assertThat(this.configuration.getServerRegionShortcut().orElse(null)).isEqualTo(GudRegionShortcut.REPLICATE);
		assertThat(this.configuration.resolveServerRegionShortcut()).isEqualTo(GudRegionShortcut.REPLICATE);
	}

	@Test
	public void setImportMetadataConfiguresClientRegionShortcutPoolNameAndServerRegionShortcut() {

		AnnotationMetadata mockAnnotationMetadata = mock(AnnotationMetadata.class);

		Map<String, Object> annotationAttributes = MapBuilder.<String, Object>newMapBuilder()
			.put("clientRegionShortcut", GudClientRegionShortcut.LOCAL_PERSISTENT)
			.put("poolName", "SwimmingPool")
			.put("serverRegionShortcut", GudRegionShortcut.REPLICATE_PERSISTENT)
			.build();

		when(mockAnnotationMetadata.hasAnnotation(eq(EnableCachingDefinedRegions.class.getName()))).thenReturn(true);
		when(mockAnnotationMetadata.getAnnotationAttributes(eq(EnableCachingDefinedRegions.class.getName())))
			.thenReturn(annotationAttributes);

		this.configuration.setImportMetadata(mockAnnotationMetadata);

		assertThat(this.configuration.resolveClientRegionShortcut()).isEqualTo(GudClientRegionShortcut.LOCAL_PERSISTENT);
		assertThat(this.configuration.resolvePoolName()).isEqualTo("SwimmingPool");
		assertThat(this.configuration.resolveServerRegionShortcut()).isEqualTo(GudRegionShortcut.REPLICATE_PERSISTENT);

		verify(mockAnnotationMetadata, times(1))
			.hasAnnotation(eq(EnableCachingDefinedRegions.class.getName()));

		verify(mockAnnotationMetadata, times(1))
			.getAnnotationAttributes(eq(EnableCachingDefinedRegions.class.getName()));
	}

	@Test
	public void cacheableServiceOneRegistersRegionsOneAndTwo() {

		ConfigurableBeanFactory mockBeanFactory = mock(ConfigurableBeanFactory.class);

		GudClientCache mockGemFireCache = GemFireMockObjectsSupport.mockClientCache();

		when(mockBeanFactory.containsBean(anyString())).thenReturn(false);
		when(mockBeanFactory.getBean(eq(GudClientCache.class))).thenReturn(mockGemFireCache);

		this.configuration.setBeanFactory(mockBeanFactory);

		BeanPostProcessor cachingAnnotationsRegionBeanRegistrar =
			this.configuration.cachingAnnotationsRegionBeanRegistrar(mockBeanFactory);

		cachingAnnotationsRegionBeanRegistrar
			.postProcessBeforeInitialization(new CacheableServiceOne(), "cacheableServiceOne");

		Arrays.asList("RegionOne", "RegionTwo").forEach(beanName -> {
			verify(mockBeanFactory, times(1)).containsBean(eq(beanName));
			verify(mockBeanFactory, times(2)).getBean(eq(GudClientCache.class));
			verify(mockBeanFactory, times(1))
				.registerSingleton(eq(beanName), any(GudRegion.class));
		});
	}

	@Test
	public void cacheableServiceTwoRegistersRegionsTwoThreeFourFiveAndSix() {

		ConfigurableBeanFactory mockBeanFactory = mock(ConfigurableBeanFactory.class);

		GudClientCache mockGemFireCache = GemFireMockObjectsSupport.mockClientCache();

		Set<String> registeredBeanNames = new HashSet<>();

		when(mockBeanFactory.containsBean(anyString())).thenAnswer(invocation ->
			registeredBeanNames.contains(invocation.<String>getArgument(0)));

		when(mockBeanFactory.getBean(eq(GudClientCache.class))).thenReturn(mockGemFireCache);

		doAnswer(invocation -> registeredBeanNames.add(invocation.getArgument(0))).when(mockBeanFactory)
			.registerSingleton(anyString(), any());

		this.configuration.setBeanFactory(mockBeanFactory);

		BeanPostProcessor cachingAnnotationsRegionBeanRegistrar =
			this.configuration.cachingAnnotationsRegionBeanRegistrar(mockBeanFactory);

		cachingAnnotationsRegionBeanRegistrar
			.postProcessBeforeInitialization(new CacheableServiceTwo(), "cacheableServiceTwo");

		Arrays.asList("RegionTwo", "RegionThree", "RegionFour", "RegionFive", "RegionSix").forEach(beanName -> {
			verify(mockBeanFactory, times(1)).containsBean(eq(beanName));
			verify(mockBeanFactory, times(5)).getBean(eq(GudClientCache.class));
			verify(mockBeanFactory, times(1))
				.registerSingleton(eq(beanName), any(GudRegion.class));
		});
	}

	@Test
	public void cacheableServiceThreeRegistersSeventeenRegionBeans() {

		ConfigurableBeanFactory mockBeanFactory = mock(ConfigurableBeanFactory.class);

		GudClientCache mockGemFireCache = GemFireMockObjectsSupport.mockClientCache();

		Set<String> registeredBeanNames = new HashSet<>();

		when(mockBeanFactory.containsBean(anyString())).thenAnswer(invocation ->
			registeredBeanNames.contains(invocation.<String>getArgument(0)));

		when(mockBeanFactory.getBean(eq(GudClientCache.class))).thenReturn(mockGemFireCache);

		doAnswer(invocation -> registeredBeanNames.add(invocation.getArgument(0))).when(mockBeanFactory)
			.registerSingleton(anyString(), any());

		this.configuration.setBeanFactory(mockBeanFactory);

		BeanPostProcessor cachingAnnotationsRegionBeanRegistrar =
			this.configuration.cachingAnnotationsRegionBeanRegistrar(mockBeanFactory);

		cachingAnnotationsRegionBeanRegistrar
			.postProcessBeforeInitialization(new CacheableServiceThree(), "cacheableServiceThree");

		Set<String> registeredRegionBeanNames =
			asSet("RegionSix", "RegionSeven", "RegionEight", "RegionNine", "RegionTen", "RegionEleven",
				"RegionTwelve", "RegionThirteen", "RegionFourteen", "RegionFifteen", "RegionSixteen", "RegionSeventeen",
				"RegionEighteen", "RegionNineteen", "RegionTwenty", "RegionTwentyOne", "RegionTwentyFive");

		registeredRegionBeanNames.forEach(beanName -> {
			verify(mockBeanFactory, times(1)).containsBean(eq(beanName));
			verify(mockBeanFactory, times(registeredBeanNames.size())).getBean(eq(GudClientCache.class));
			verify(mockBeanFactory, times(1))
				.registerSingleton(eq(beanName), any(GudRegion.class));
		});
	}

	@Test
	public void cacheableServiceFourRegistersNineteenRegionBeans() {

		ConfigurableBeanFactory mockBeanFactory = mock(ConfigurableBeanFactory.class);

		GudClientCache mockGemFireCache = GemFireMockObjectsSupport.mockClientCache();

		Set<String> registeredBeanNames = new HashSet<>();

		when(mockBeanFactory.containsBean(anyString())).thenAnswer(invocation ->
			registeredBeanNames.contains(invocation.<String>getArgument(0)));

		when(mockBeanFactory.getBean(eq(GudClientCache.class))).thenReturn(mockGemFireCache);

		doAnswer(invocation -> registeredBeanNames.add(invocation.getArgument(0))).when(mockBeanFactory)
			.registerSingleton(anyString(), any());

		this.configuration.setBeanFactory(mockBeanFactory);

		BeanPostProcessor cachingAnnotationsRegionBeanRegistrar =
			this.configuration.cachingAnnotationsRegionBeanRegistrar(mockBeanFactory);

		cachingAnnotationsRegionBeanRegistrar
			.postProcessBeforeInitialization(new CacheableServiceFour(), "cacheableServiceFour");

		Set<String> registeredRegionBeanNames =
			asSet("RegionSix", "RegionSeven", "RegionEight", "RegionNine", "RegionTen", "RegionEleven",
				"RegionTwelve", "RegionThirteen", "RegionFourteen", "RegionFifteen", "RegionSixteen", "RegionSeventeen",
				"RegionEighteen", "RegionNineteen", "RegionTwenty", "RegionTwentyOne", "RegionTwentyTwo",
				"RegionTwentyThree", "RegionTwentyFour");

		registeredRegionBeanNames.forEach(beanName -> {
			verify(mockBeanFactory, times(1)).containsBean(eq(beanName));
			verify(mockBeanFactory, times(registeredBeanNames.size())).getBean(eq(GudClientCache.class));
			verify(mockBeanFactory, times(1))
				.registerSingleton(eq(beanName), any(GudRegion.class));
		});
	}

	@Test
	public void cacheableServiceOneTwoAndThreeRegistersTwentyTwoRegionBeans() {

		ConfigurableBeanFactory mockBeanFactory = mock(ConfigurableBeanFactory.class);

		GudClientCache mockGemFireCache = GemFireMockObjectsSupport.mockClientCache();

		Set<String> registeredBeanNames = new HashSet<>();

		when(mockBeanFactory.containsBean(anyString())).thenAnswer(invocation ->
			registeredBeanNames.contains(invocation.<String>getArgument(0)));

		when(mockBeanFactory.getBean(eq(GudClientCache.class))).thenReturn(mockGemFireCache);

		doAnswer(invocation -> registeredBeanNames.add(invocation.getArgument(0))).when(mockBeanFactory)
			.registerSingleton(anyString(), any());

		this.configuration.setBeanFactory(mockBeanFactory);

		BeanPostProcessor cachingAnnotationsRegionBeanRegistrar =
			this.configuration.cachingAnnotationsRegionBeanRegistrar(mockBeanFactory);

		cachingAnnotationsRegionBeanRegistrar
			.postProcessBeforeInitialization(new CacheableServiceOne(), "cacheableServiceOne");

		cachingAnnotationsRegionBeanRegistrar
			.postProcessBeforeInitialization(new CacheableServiceTwo(), "cacheableServiceTwo");

		cachingAnnotationsRegionBeanRegistrar
			.postProcessBeforeInitialization(new CacheableServiceThree(), "cacheableServiceThree");

		Set<String> registeredRegionBeanNames =
			asSet("RegionOne", "RegionTwo", "RegionThree", "RegionFour", "RegionFive", "RegionSix",
				"RegionSeven", "RegionEight", "RegionNine", "RegionTen", "RegionEleven", "RegionTwelve",
				"RegionThirteen", "RegionFourteen", "RegionFifteen", "RegionSixteen", "RegionSeventeen",
				"RegionEighteen", "RegionNineteen", "RegionTwenty", "RegionTwentyOne", "RegionTwentyFive");

		registeredRegionBeanNames.forEach(beanName -> {

			int wantedNumberOfInvocations = asSet("RegionTwo", "RegionSix").contains(beanName) ? 2 : 1;

			verify(mockBeanFactory, times(wantedNumberOfInvocations)).containsBean(eq(beanName));
			verify(mockBeanFactory, times(registeredBeanNames.size())).getBean(eq(GudClientCache.class));
			verify(mockBeanFactory, times(1))
				.registerSingleton(eq(beanName), any(GudRegion.class));
		});
	}

	@Test
	public void cacheableServiceFiveRegistersRegionOneTwoThreeFourFiveSixAndSeven() {

		ConfigurableBeanFactory mockBeanFactory = mock(ConfigurableBeanFactory.class);

		GudClientCache mockGemFireCache = GemFireMockObjectsSupport.mockClientCache();

		Set<String> registeredBeanNames = new HashSet<>();

		when(mockBeanFactory.containsBean(anyString())).thenAnswer(invocation ->
			registeredBeanNames.contains(invocation.<String>getArgument(0)));

		when(mockBeanFactory.getBean(eq(GudClientCache.class))).thenReturn(mockGemFireCache);

		doAnswer(invocation -> registeredBeanNames.add(invocation.getArgument(0))).when(mockBeanFactory)
			.registerSingleton(anyString(), any());

		this.configuration.setBeanFactory(mockBeanFactory);

		BeanPostProcessor cachingAnnotationsRegionBeanRegistrar =
			this.configuration.cachingAnnotationsRegionBeanRegistrar(mockBeanFactory);

		cachingAnnotationsRegionBeanRegistrar
			.postProcessBeforeInitialization(new CacheableServiceFive(), "cacheableServiceFive");

		Set<String> registeredRegionBeanNames =
			asSet("RegionOne", "RegionTwo", "RegionThree", "RegionFour", "RegionFive",
				"RegionSix", "RegionSeven");

		registeredRegionBeanNames.forEach(beanName -> {
			verify(mockBeanFactory, times(1)).containsBean(eq(beanName));
			verify(mockBeanFactory, times(registeredBeanNames.size())).getBean(eq(GudClientCache.class));
			verify(mockBeanFactory, times(1))
				.registerSingleton(eq(beanName), any(GudRegion.class));
		});
	}

	@Test
	public void cacheableServiceSixRegistersRegionOneTwoThreeFourAndFive() {

		ConfigurableBeanFactory mockBeanFactory = mock(ConfigurableBeanFactory.class);

		GudClientCache mockGemFireCache = GemFireMockObjectsSupport.mockClientCache();

		Set<String> registeredBeanNames = new HashSet<>();

		when(mockBeanFactory.containsBean(anyString())).thenAnswer(invocation ->
			registeredBeanNames.contains(invocation.<String>getArgument(0)));

		when(mockBeanFactory.getBean(eq(GudClientCache.class))).thenReturn(mockGemFireCache);

		doAnswer(invocation -> registeredBeanNames.add(invocation.getArgument(0))).when(mockBeanFactory)
			.registerSingleton(anyString(), any());

		this.configuration.setBeanFactory(mockBeanFactory);

		BeanPostProcessor cachingAnnotationsRegionBeanRegistrar =
			this.configuration.cachingAnnotationsRegionBeanRegistrar(mockBeanFactory);

		cachingAnnotationsRegionBeanRegistrar
			.postProcessBeforeInitialization(new CacheableServiceSix(), "cacheableServiceSix");

		Set<String> registeredRegionBeanNames =
			asSet("RegionOne", "RegionTwo", "RegionThree", "RegionFour", "RegionFive");

		registeredRegionBeanNames.forEach(beanName -> {
			verify(mockBeanFactory, times(1)).containsBean(eq(beanName));
			verify(mockBeanFactory, times(registeredBeanNames.size())).getBean(eq(GudClientCache.class));
			verify(mockBeanFactory, times(1))
				.registerSingleton(eq(beanName), any(GudRegion.class));
		});
	}

	@Test
	public void cacheableServiceSevenRegistersRegionOneThroughEleven() {

		ConfigurableBeanFactory mockBeanFactory = mock(ConfigurableBeanFactory.class);

		GudClientCache mockGemFireCache = GemFireMockObjectsSupport.mockClientCache();

		Set<String> registeredBeanNames = new HashSet<>();

		when(mockBeanFactory.containsBean(anyString())).thenAnswer(invocation ->
			registeredBeanNames.contains(invocation.<String>getArgument(0)));

		when(mockBeanFactory.getBean(eq(GudClientCache.class))).thenReturn(mockGemFireCache);

		doAnswer(invocation -> registeredBeanNames.add(invocation.getArgument(0))).when(mockBeanFactory)
			.registerSingleton(anyString(), any());

		this.configuration.setBeanFactory(mockBeanFactory);

		BeanPostProcessor cachingAnnotationsRegionBeanRegistrar =
			this.configuration.cachingAnnotationsRegionBeanRegistrar(mockBeanFactory);

		cachingAnnotationsRegionBeanRegistrar
			.postProcessBeforeInitialization(new CacheableServiceSeven(), "cacheableServiceSeven");

		Set<String> registeredRegionBeanNames =
			asSet("RegionOne", "RegionTwo", "RegionThree", "RegionFour", "RegionFive", "RegionSix",
				"RegionSeven", "RegionEight", "RegionNine", "RegionTen", "RegionEleven");

		registeredRegionBeanNames.forEach(beanName -> {
			verify(mockBeanFactory, times(1)).containsBean(eq(beanName));
			verify(mockBeanFactory, times(registeredBeanNames.size())).getBean(eq(GudClientCache.class));
			verify(mockBeanFactory, times(1))
				.registerSingleton(eq(beanName), any(GudRegion.class));
		});
	}

	@Service
	@Cacheable(cacheNames = { "RegionOne", "RegionTwo" })
	@SuppressWarnings("unused")
	static class CacheableServiceOne {

		public void cacheableMethodOne() {}

	}

	@Service
	@Cacheable(cacheNames = { "RegionTwo" })
	@CachePut("RegionSix")
	@SuppressWarnings("unused")
	static class CacheableServiceTwo {

		@Cacheable("RegionThree")
		public void cacheableMethodOne() {}

		@CacheEvict(cacheNames = { "RegionThree", "RegionFour" })
		public void cacheableMethodTwo() {}

		@CachePut(cacheNames = "RegionFive")
		public void cacheableMethodThree() {}

	}

	@Service
	@Caching(
		cacheable = { @Cacheable(cacheNames = { "RegionSix", "RegionSeven" }), @Cacheable("RegionEight") },
		evict = { @CacheEvict(cacheNames = { "RegionNine", "RegionTen"}), @CacheEvict("RegionEleven") },
		put = { @CachePut(cacheNames = { "RegionTwelve", "RegionThirteen" }), @CachePut("RegionFourteen") }
	)
	@Cacheable("RegionFifteen")
	@CachePut(cacheNames = { "RegionSixteen", "RegionSeventeen" })
	@SuppressWarnings("unused")
	static class CacheableServiceThree {

		@Caching(
			cacheable = { @Cacheable(cacheNames = { "RegionSix", "RegionTwelve" }), @Cacheable("RegionEighteen") },
			put = { @CachePut({ "RegionTen", "RegionNineteen" }), @CachePut("RegionTwenty") }
		)
		public void cacheableMethodOne() {}

		@CachePut("RegionTwentyOne")
		public void cacheableMethodTwo() {}

		@Cacheable("RegionTwentyFive")
		public void cacheableMethodFour() {}

	}

	@SuppressWarnings("unused")
	static class CacheableServiceFour extends CacheableServiceThree {

		@Cacheable({ "RegionEleven", "RegionTwentyTwo", "RegionTwentyThree", "RegionTwentyFour" })
		public void cacheableMethodThree() {}

		@Override
		@CachePut({ "RegionTwentyOne", "RegionTwentyTwo" })
		public void cacheableMethodFour() {}

		public void cacheableMethodFive() {}

	}

	@CacheDefaults(cacheName = "RegionOne")
	@CacheRemoveAll(cacheName = "RegionSix")
	@CacheResult(cacheName = "RegionSeven")
	@SuppressWarnings("unused")
	static class CacheableServiceFive {

		@javax.cache.annotation.CachePut(cacheName = "RegionTwo")
		public void cacheableMethodOne() {}

		@CacheRemove(cacheName = "RegionThree")
		public void cacheableMethodTwo() {}

		@CacheRemoveAll(cacheName = "RegionFour")
		public void cacheableMethodThree() {}

		@CacheResult(cacheName = "RegionFive")
		public void cacheableMethodFour() {}

	}

	@CacheDefaults(cacheName = "RegionOne")
	@javax.cache.annotation.CachePut(cacheName = "RegionOne")
	@CacheRemoveAll(cacheName = "RegionFive")
	@SuppressWarnings("unused")
	static class CacheableServiceSix {

		@javax.cache.annotation.CachePut(cacheName = "RegionTwo")
		@javax.cache.annotation.CacheResult(cacheName = "RegionOne")
		public void cacheableMethodOne() {}

		@CacheRemove(cacheName = "RegionThree")
		public void cacheableMethodTwo() {}

		@CacheRemoveAll(cacheName = "RegionThree")
		public void cacheableMethodThree() {}

		@CacheResult(cacheName = "RegionFour")
		@CacheRemove(cacheName = "RegionFive")
		public void cacheableMethodFour() {}

	}

	@Caching(
		cacheable = { @Cacheable({ "RegionSix", "RegionSeven", "RegionEight" }), @Cacheable("RegionNine")},
		put = @CachePut("RegionTwo")
	)
	@CacheDefaults(cacheName = "RegionOne")
	@SuppressWarnings("unused")
	static class CacheableServiceSeven extends CacheableServiceSix {

		@CachePut("RegionTen")
		@CacheRemove(cacheName = "RegionFive")
		@CacheResult(cacheName = "RegionEleven")
		public void cacheableMethodFive() {}

		@Cacheable("RegionThree")
		public void cacheableMethodSix() {}

	}
}
