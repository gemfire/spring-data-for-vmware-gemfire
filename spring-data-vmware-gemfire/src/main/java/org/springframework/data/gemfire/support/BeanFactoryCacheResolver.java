// Copyright (c) VMware, Inc. 2022. All rights reserved.
// SPDX-License-Identifier: Apache-2.0

package org.springframework.data.gemfire.support;

import java.util.Optional;

import org.apache.geode.cache.GemFireCache;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.data.gemfire.CacheResolver;
import org.springframework.lang.NonNull;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/**
 * Cacheable {@link CacheResolver} implementation capable of resolving a {@link GemFireCache} instance
 * from the Spring {@link BeanFactory}.
 *
 * @author John Blum
 * @see GemFireCache
 * @see BeanFactory
 * @see BeanFactoryAware
 * @see CacheResolver
 * @see AbstractCachingCacheResolver
 * @since 2.3.0
 */
public class BeanFactoryCacheResolver extends AbstractCachingCacheResolver<GemFireCache> implements BeanFactoryAware {

	private BeanFactory beanFactory;

	private String cacheBeanName;

	/**
	 * Constructs a new instance of {@link BeanFactoryCacheResolver} initialized with the given, required
	 * Spring {@link BeanFactory}.
	 *
	 * @param beanFactory {@link BeanFactory} used to resolve the {@link GemFireCache}.
	 * @throws IllegalArgumentException if {@link BeanFactory} is {@literal null}.
	 * @see BeanFactory
	 */
	public BeanFactoryCacheResolver(BeanFactory beanFactory) {
		setBeanFactory(beanFactory);
	}

	/**
	 * Sets a reference to the {@link BeanFactory} used to resolve the {@link GemFireCache}.
	 *
	 * @param beanFactory {@link BeanFactory} used to resolve the {@link GemFireCache}.
	 * @throws IllegalArgumentException if {@link BeanFactory} is {@literal null}.
	 * @throws BeansException if configuration of the {@link BeanFactory} fails.
	 * @see BeanFactory
	 */
	@Override
	public final void setBeanFactory(@NonNull BeanFactory beanFactory) throws BeansException {

		Assert.notNull(beanFactory, "BeanFactory must not be null");

		this.beanFactory = beanFactory;
	}

	/**
	 * Returns the configured reference to the Spring {@link BeanFactory} used to resolve the single instance
	 * of the {@link GemFireCache}.
	 *
	 * @return a reference to the configured Spring {@link BeanFactory}.
	 * @see BeanFactory
	 */
	protected @NonNull BeanFactory getBeanFactory() {
		return this.beanFactory;
	}

	/**
	 * Sets (configures) the {@link String bean name} used to further qualify the resolution of
	 * the {@link GemFireCache} object reference in a Spring context.
	 *
	 * @param cacheBeanName {@link String name} of the {@link GemFireCache} bean in the Spring context.
	 */
	public void setCacheBeanName(String cacheBeanName) {
		this.cacheBeanName = cacheBeanName;
	}

	/**
	 * Returns the optionally configured {@link String bean name} used to further qualify the resolution of
	 * the {@link GemFireCache} object reference in a Spring context.
	 *
	 * @return the configured {@link String name} of the {@link GemFireCache} bean in the Spring context.
	 */
	public Optional<String> getCacheBeanName() {

		return Optional.ofNullable(this.cacheBeanName)
			.filter(StringUtils::hasText);
	}

	/**
	 * Uses the configured Spring {@link BeanFactory} to resolve a reference to
	 * the single {@link GemFireCache} instance.
	 *
	 * @return a reference to the {@link GemFireCache} bean.
	 * @see BeanFactory#getBean(Class)
	 * @see GemFireCache
	 * @see #getBeanFactory()
	 */
	@Override
	protected GemFireCache doResolve() {

		return getCacheBeanName()
			.map(cacheBeanName -> getBeanFactory().getBean(cacheBeanName, GemFireCache.class))
			.orElse(getBeanFactory().getBean(GemFireCache.class));
	}
}
