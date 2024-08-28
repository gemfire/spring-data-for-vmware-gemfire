/*
 * Copyright 2022-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.config.annotation;

import static java.util.Arrays.asList;
import static java.util.Arrays.stream;
import static org.springframework.data.gemfire.util.ArrayUtils.asArray;
import static org.springframework.data.gemfire.util.ArrayUtils.nullSafeArray;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.geode.cache.GemFireCache;
import org.apache.geode.cache.Region;
import org.apache.geode.cache.RegionShortcut;
import org.apache.geode.cache.client.ClientRegionShortcut;
import org.apache.geode.cache.client.Pool;

import org.springframework.beans.BeanInstantiationException;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.context.Lifecycle;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportAware;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.data.gemfire.client.ClientRegionFactoryBean;
import org.springframework.data.gemfire.config.annotation.support.AbstractAnnotationConfigSupport;
import org.springframework.data.gemfire.config.annotation.support.CacheTypeAwareRegionFactoryBean;
import org.springframework.data.gemfire.support.CompositeLifecycle;
import org.springframework.data.gemfire.util.CollectionUtils;
import org.springframework.data.gemfire.util.StreamUtils;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;

/**
 * The {@link CachingDefinedRegionsConfiguration} class is a Spring {@link Configuration @Configuration} class
 * that applies configuration to a Spring (Data GemFire/Geode) application to create GemFire/Geode cache
 * {@link Region Regions} based on the use of Spring's Cache Abstraction to enable caching for application
 * service classes and methods.
 *
 * @author John Blum
 * @see Annotation
 * @see AnnotatedElement
 * @see GemFireCache
 * @see Region
 * @see RegionShortcut
 * @see ClientRegionShortcut
 * @see Pool
 * @see org.springframework.beans.factory.annotation.AnnotatedBeanDefinition
 * @see org.springframework.beans.factory.config.BeanDefinition
 * @see BeanPostProcessor
 * @see ConfigurableBeanFactory
 * @see org.springframework.beans.factory.support.AbstractBeanDefinition
 * @see org.springframework.beans.factory.support.BeanDefinitionBuilder
 * @see org.springframework.beans.factory.support.BeanDefinitionRegistry
 * @see org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor
 * @see CacheConfig
 * @see CacheEvict
 * @see CachePut
 * @see Cacheable
 * @see Caching
 * @see Bean
 * @see Configuration
 * @see ImportAware
 * @see org.springframework.core.annotation.AnnotatedElementUtils
 * @see org.springframework.core.annotation.AnnotationUtils
 * @see EnableCachingDefinedRegions
 * @see AbstractAnnotationConfigSupport
 * @see org.springframework.data.gemfire.config.annotation.support.BeanDefinitionRegistryPostProcessorSupport
 * @see CacheTypeAwareRegionFactoryBean
 * @since 2.0.0
 */
@Configuration
public class CachingDefinedRegionsConfiguration extends AbstractAnnotationConfigSupport implements ImportAware {

	private final CacheNameResolver[] configuredCacheNameResolvers = {
		new Jsr107CacheAnnotationsCacheNameResolverFactory().create(),
		new SpringCacheAnnotationsCacheNameResolver()
	};

	private final CacheNameResolver composableCacheNameResolver = type ->
		asList(this.configuredCacheNameResolvers).stream()
			.flatMap(cacheNameResolver -> cacheNameResolver.resolveCacheNames(type).stream())
			.collect(Collectors.toSet());

	private ClientRegionShortcut clientRegionShortcut = ClientRegionShortcut.PROXY;

	private final CompositeLifecycle compositeLifecycle = new CompositeLifecycle();

	@Autowired(required = false)
	private final List<RegionConfigurer> regionConfigurers = Collections.emptyList();

	private RegionShortcut serverRegionShortcut = RegionShortcut.PARTITION;

	private String poolName = ClientRegionFactoryBean.DEFAULT_POOL_NAME;

	/**
	 * Returns the {@link Annotation} {@link Class type} that configures and creates {@link Region Regions}
	 * for application service {@link Method Methods} that are annotated with Spring's Cache Abstraction Annotations.
	 *
	 * @return the {@link Annotation} {@link Class type} that configures and creates {@link Region Regions}
	 * for application service {@link Method Methods} that are annotated with Spring's Cache Abstraction Annotations.
	 * @see EnableCachingDefinedRegions
	 * @see Annotation
	 * @see Class
	 */
	@Override
	protected Class<? extends Annotation> getAnnotationType() {
		return EnableCachingDefinedRegions.class;
	}

	/**
	 * Returns the configured {@link CacheNameResolver} to resolve all the declared cache name on Spring application
	 * beans/components declared and registered in the Spring container (context).
	 *
	 * @return the configured {@link CacheNameResolver} to resolve all teh caches used by the Spring application.
	 * @see CacheNameResolver
	 */
	protected CacheNameResolver getCacheNameResolver() {
		return this.composableCacheNameResolver;
	}

	/**
	 * Configures the {@link ClientRegionShortcut} specifying the data management policy to use
	 * when creating a client {@link Region}.
	 *
	 * @param clientRegionShortcut {@link ClientRegionShortcut} specifying the data management policy
	 * to use when creating a client {@link Region}.
	 * @see ClientRegionShortcut
	 */
	public void setClientRegionShortcut(ClientRegionShortcut clientRegionShortcut) {
		this.clientRegionShortcut = clientRegionShortcut;
	}

	/**
	 * Returns the configured {@link ClientRegionShortcut} specifying the data management policy to use
	 * when creating a client {@link Region}.
	 *
	 * @return an {@link Optional} {@link ClientRegionShortcut} specifying the data management policy to use
	 * when creating a client {@link Region}.
	 * @see ClientRegionShortcut
	 * @see #setClientRegionShortcut(ClientRegionShortcut)
	 * @see Optional
	 */
	protected Optional<ClientRegionShortcut> getClientRegionShortcut() {
		return Optional.ofNullable(this.clientRegionShortcut);
	}

	/**
	 * Resolves the {@link ClientRegionShortcut} specifying the data management policy to use
	 * when creating a client {@link Region}; defaults to {@link ClientRegionShortcut#PROXY}.
	 *
	 * @return the resolved {@link ClientRegionShortcut} specifying the data management policy to use
	 * when creating a client {@link Region}; defaults to {@link ClientRegionShortcut#PROXY}.
	 * @see ClientRegionShortcut
	 * @see #getClientRegionShortcut()
	 */
	protected ClientRegionShortcut resolveClientRegionShortcut() {
		return getClientRegionShortcut().orElse(ClientRegionShortcut.PROXY);
	}

	/**
	 * Configures the name of the dedicated {@link Pool} used by all caching-defined client {@link Region Regions}
	 * to send and receive data between the client and server.
	 *
	 * @param poolName {@link String} containing the name of the dedicated {@link Pool} for all
	 * caching-defined client {@link Region Regions}.
	 */
	public void setPoolName(String poolName) {
		this.poolName = poolName;
	}

	/**
	 * Returns the name of the dedicated {@link Pool} used by all caching-defined client {@link Region Regions}
	 * to send and receive data between the client and server.
	 *
	 * @return an {@link Optional} {@link String name} of the dedicated {@link Pool} used by all caching-defined
	 * client {@link Region Regions}.
	 * @see #setPoolName(String)
	 * @see Optional
	 */
	protected Optional<String> getPoolName() {
		return Optional.ofNullable(this.poolName).filter(StringUtils::hasText);
	}

	/**
	 * Resolves the name of the dedicated {@link Pool} used by all caching-defined client {@link Region Regions}
	 * to send and receive data between the client and server; defaults to {@literal DEFAULT}.
	 *
	 * @return the {@link String name} of the dedicated {@link Pool} used by all caching-defined
	 * client {@link Region Regions}; defaults to {@literal DEFAULT}.
	 * @see #getPoolName()
	 */
	protected String resolvePoolName() {
		return getPoolName().orElse(ClientRegionFactoryBean.DEFAULT_POOL_NAME);
	}

	/**
	 * Configures the {@link RegionShortcut} specifying the data management policy to use
	 * when creating a server (peer) {@link Region}.
	 *
	 * @param serverRegionShortcut {@link RegionShortcut} specifying the data management policy to use
	 * when creating a server (peer) {@link Region}.
	 * @see RegionShortcut
	 */
	public void setServerRegionShortcut(RegionShortcut serverRegionShortcut) {
		this.serverRegionShortcut = serverRegionShortcut;
	}

	/**
	 * Returns the configured {@link RegionShortcut} specifying the data management policy to use
	 * when creating a server (peer) {@link Region}.
	 *
	 * @return an {@link Optional} {@link RegionShortcut} specifying the data management policy to use
	 * when creating a server (peer) {@link Region}.
	 * @see #setServerRegionShortcut(RegionShortcut)
	 * @see RegionShortcut
	 * @see Optional
	 */
	protected Optional<RegionShortcut> getServerRegionShortcut() {
		return Optional.ofNullable(this.serverRegionShortcut);
	}

	/**
	 * Resolves the {@link RegionShortcut} specifying the data management policy to use
	 * when creating a server (peer) {@link Region}; defaults to {@link RegionShortcut#PARTITION}.
	 *
	 * @return the resolved {@link RegionShortcut} specifying the data management policy to use
	 * when creating a server (peer) {@link Region}; defaults to {@link RegionShortcut#PARTITION}.
	 * @see RegionShortcut
	 * @see #getServerRegionShortcut()
	 */
	protected RegionShortcut resolveServerRegionShortcut() {
		return getServerRegionShortcut().orElse(RegionShortcut.PARTITION);
	}

	@Override
	public void setImportMetadata(AnnotationMetadata importMetadata) {

		if (isAnnotationPresent(importMetadata)) {

			AnnotationAttributes enableCachingDefinedRegionsAttributes = getAnnotationAttributes(importMetadata);

			setClientRegionShortcut(enableCachingDefinedRegionsAttributes.getEnum("clientRegionShortcut"));

			setPoolName(enableCachingDefinedRegionsAttributes.getString("poolName"));

			setServerRegionShortcut(enableCachingDefinedRegionsAttributes.getEnum("serverRegionShortcut"));
		}
	}

	@Bean
	public BeanPostProcessor cachingAnnotationsRegionBeanRegistrar(ConfigurableBeanFactory beanFactory) {

		return new BeanPostProcessor() {

			@Nullable @Override
			public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {

				if (isNotInfrastructureBean(bean)) {
					registerRegionBeans(getCacheNameResolver().resolveCacheNames(bean.getClass()), beanFactory);
				}

				return bean;
			}
		};
	}

	private ConfigurableBeanFactory registerRegionBeans(Set<String> cacheNames, ConfigurableBeanFactory beanFactory) {

		cacheNames.forEach(cacheName -> {

			if (!beanFactory.containsBean(cacheName)) {
				try {

					CacheTypeAwareRegionFactoryBean<?, ?> regionFactoryBean = new CacheTypeAwareRegionFactoryBean<>();

					GemFireCache gemfireCache = beanFactory.getBean(GemFireCache.class);

					regionFactoryBean.setBeanFactory(beanFactory);
					regionFactoryBean.setCache(gemfireCache);
					regionFactoryBean.setClientRegionShortcut(resolveClientRegionShortcut());
					regionFactoryBean.setRegionConfigurers(resolveRegionConfigurers());
					regionFactoryBean.setRegionName(cacheName);
					regionFactoryBean.setServerRegionShortcut(resolveServerRegionShortcut());
					regionFactoryBean.setStatisticsEnabled(true);

					String poolName = resolvePoolName();

					if (!ClientRegionFactoryBean.DEFAULT_POOL_NAME.equalsIgnoreCase(poolName)) {
						regionFactoryBean.setPoolName(poolName);
					}

					regionFactoryBean.afterPropertiesSet();

					this.compositeLifecycle.add(regionFactoryBean);

					Optional.ofNullable(regionFactoryBean.getObject())
						.ifPresent(region -> beanFactory.registerSingleton(cacheName, region));
				}
				catch (Exception cause) {
					throw new BeanInstantiationException(Region.class,
						String.format("Failed to create Region for cache [%s]", cacheName), cause);
				}
			}
		});

		return beanFactory;
	}

	private List<RegionConfigurer> resolveRegionConfigurers() {

		return Optional.ofNullable(this.regionConfigurers)
			.filter(regionConfigurers -> !regionConfigurers.isEmpty())
			.orElseGet(() ->
				Collections.singletonList(LazyResolvingComposableRegionConfigurer.create(getBeanFactory())));
	}

	@Bean
	@SuppressWarnings("unused")
	public Lifecycle cachingDefinedRegionsCompositeLifecycleBean() {
		return this.compositeLifecycle;
	}

	/**
	 * {@link CacheNameResolver} is a {@link FunctionalInterface} declaring a contract for all implementations
	 * used to resolve all cache names declared and used by a Spring application.  A resolver typically inspects
	 * all the application beans/components declared and registered in the Spring container (context) setup by
	 * the application to determine whether the application components require caching behavior.
	 *
	 * @see Jsr107CacheAnnotationsCacheNameResolver
	 * @see SpringCacheAnnotationsCacheNameResolver
	 */
	@FunctionalInterface
	public interface CacheNameResolver {
		Set<String> resolveCacheNames(Class<?> type);
	}

	/**
	 * {@link AbstractCacheNameResolver} is an abstract base class encapsulating reusable functionality common
	 * to all {@link CacheNameResolver} implementations.
	 *
	 * Current implementations support inlude JSR-107, JCache API annotation and Spring's Cache Abstraction annotations.
	 *
	 * @see CacheNameResolver
	 */
	public abstract static class AbstractCacheNameResolver extends AbstractAnnotationConfigSupport
			implements CacheNameResolver {

		private final String JSR_107_CACHE_NAME_ATTRIBUTE_NAME = "cacheName";
		private final String SPRING_CACHE_NAMES_ATTRIBUTE_NAME = "cacheNames";

		private final String[] EMPTY_ARRAY = new String[0];

		@Override
		protected final Class<? extends Annotation> getAnnotationType() {
			return null;
		}

		protected abstract Class<? extends Annotation>[] getClassCacheAnnotationTypes();

		protected abstract Class<? extends Annotation>[] getMethodCacheAnnotationTypes();

		@SuppressWarnings("rawtypes")
		protected Class[] append(Class[] annotationTypes, Class... additionalAnnotationTypes) {

			List<Class> annotationTypeList = new ArrayList<>(Arrays.asList(annotationTypes));

			Collections.addAll(annotationTypeList, additionalAnnotationTypes);

			return annotationTypeList.toArray(new Class[0]);
		}

		protected Set<String> resolveCacheNames(Annotation annotation) {

			return Optional.ofNullable(annotation)
				.map(this::getAnnotationAttributes)
				.map(annotationAttributes -> {

					String attributeName = annotationAttributes.containsKey(SPRING_CACHE_NAMES_ATTRIBUTE_NAME)
						? SPRING_CACHE_NAMES_ATTRIBUTE_NAME
						: JSR_107_CACHE_NAME_ATTRIBUTE_NAME;

					return annotationAttributes.containsKey(attributeName)
						? annotationAttributes.getStringArray(attributeName)
						: EMPTY_ARRAY;

				})
				.map(CollectionUtils::asSet)
				.orElseGet(Collections::emptySet);
		}

		@Override
		public Set<String> resolveCacheNames(Class<?> type) {

			Set<String> cacheNames = new HashSet<>(resolveCacheNames(type, getClassCacheAnnotationTypes()));

			stream(type.getMethods())
				.filter(this::isUserLevelMethod)
				.forEach(method -> cacheNames.addAll(resolveCacheNames(method, getMethodCacheAnnotationTypes())));

			return cacheNames;
		}

		@SuppressWarnings("all")
		protected Set<String> resolveCacheNames(AnnotatedElement annotatedElement,
				Class<? extends Annotation>... annotationTypes) {

			Stream<String> cacheNames = stream(nullSafeArray(annotationTypes, Class.class))
				.map(annotationType -> resolveAnnotation(annotatedElement, annotationType))
				.flatMap(annotation -> resolveCacheNames((Annotation) annotation).stream());

			return cacheNames.collect(Collectors.toSet());
		}
	}

	protected static class SpringCacheAnnotationsCacheNameResolver extends AbstractCacheNameResolver {

		@Override
		@SuppressWarnings("unchecked")
		protected Class<? extends Annotation>[] getClassCacheAnnotationTypes() {
			return append(getMethodCacheAnnotationTypes(), CacheConfig.class, Caching.class);
		}

		@Override
		protected Class<? extends Annotation>[] getMethodCacheAnnotationTypes() {
			return asArray(Cacheable.class, CacheEvict.class, CachePut.class);
		}

		@Override
		public Set<String> resolveCacheNames(Class<?> type) {

			Set<String> cacheNames = super.resolveCacheNames(type);

			cacheNames.addAll(resolveCachingCacheNames(type));

			stream(type.getMethods())
				.filter(this::isUserLevelMethod)
				.forEach(method -> cacheNames.addAll(resolveCachingCacheNames(method)));

			return cacheNames;
		}

		@SuppressWarnings("unchecked")
		private Set<String> resolveCachingCacheNames(AnnotatedElement annotatedElement) {

			Set<String> cacheNames = new HashSet<>();

			Optional.ofNullable(resolveAnnotation(annotatedElement, Caching.class)).ifPresent(caching ->
				StreamUtils.concat(stream(caching.cacheable()), stream(caching.evict()), stream(caching.put()))
					.flatMap(cacheAnnotation -> resolveCacheNames(cacheAnnotation).stream())
					.collect(Collectors.toCollection(() -> cacheNames)));

			return cacheNames;
		}
	}
}
