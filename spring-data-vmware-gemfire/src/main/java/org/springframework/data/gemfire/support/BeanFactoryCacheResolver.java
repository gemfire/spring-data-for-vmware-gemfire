/*
 * Copyright 2022-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.support;

import java.util.Optional;
import com.vmware.gemfire.driver.spi.DriverConnection;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.data.gemfire.CacheResolver;
import org.springframework.lang.NonNull;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/**
 * Cacheable {@link CacheResolver} implementation capable of resolving a {@link DriverConnection} instance
 * from the Spring {@link BeanFactory}.
 *
 * @author John Blum
 * @see DriverConnection
 * @see BeanFactory
 * @see CacheResolver
 * @see AbstractCachingCacheResolver
 * @since 2.3.0
 */
public class BeanFactoryCacheResolver extends AbstractCachingCacheResolver<DriverConnection> {

	private final BeanFactory beanFactory;

	private String cacheBeanName;

	/**
	 * Constructs a new instance of {@link BeanFactoryCacheResolver} initialized with the given, required
	 * Spring {@link BeanFactory}.
	 *
	 * @param beanFactory {@link BeanFactory} used to resolve the {@link DriverConnection}.
	 * @throws IllegalArgumentException if {@link BeanFactory} is {@literal null}.
	 * @see BeanFactory
	 */
	public BeanFactoryCacheResolver(BeanFactory beanFactory) {
		Assert.notNull(beanFactory, "BeanFactory must not be null");
		this.beanFactory = beanFactory;
	}

	/**
	 * Returns the configured reference to the Spring {@link BeanFactory} used to resolve the single instance
	 * of the {@link DriverConnection}.
	 *
	 * @return a reference to the configured Spring {@link BeanFactory}.
	 * @see BeanFactory
	 */
	protected @NonNull BeanFactory getBeanFactory() {
		return this.beanFactory;
	}

	/**
	 * Sets (configures) the {@link String bean name} used to further qualify the resolution of
	 * the {@link DriverConnection} object reference in a Spring context.
	 *
	 * @param cacheBeanName {@link String name} of the {@link DriverConnection} bean in the Spring context.
	 */
	public void setCacheBeanName(String cacheBeanName) {
		this.cacheBeanName = cacheBeanName;
	}

	/**
	 * Returns the optionally configured {@link String bean name} used to further qualify the resolution of
	 * the {@link DriverConnection} object reference in a Spring context.
	 *
	 * @return the configured {@link String name} of the {@link DriverConnection} bean in the Spring context.
	 */
	public Optional<String> getCacheBeanName() {

		return Optional.ofNullable(this.cacheBeanName)
			.filter(StringUtils::hasText);
	}

	/**
	 * Uses the configured Spring {@link BeanFactory} to resolve a reference to
	 * the single {@link DriverConnection} instance.
	 *
	 * @return a reference to the {@link DriverConnection} bean.
	 * @see BeanFactory#getBean(Class)
	 * @see DriverConnection
	 * @see #getBeanFactory()
	 */
	@Override
	protected DriverConnection doResolve() {

		return getCacheBeanName()
			.map(cacheBeanName -> getBeanFactory().getBean(cacheBeanName, DriverConnection.class))
			.orElseGet(() -> getBeanFactory().getBean(DriverConnection.class));
	}
}
