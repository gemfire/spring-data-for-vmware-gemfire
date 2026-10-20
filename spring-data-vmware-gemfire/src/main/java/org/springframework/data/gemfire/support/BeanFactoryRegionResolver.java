/*
 * Copyright 2022-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.support;

import com.vmware.gemfire.driver.spi.DriverRegion;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.data.gemfire.RegionResolver;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/**
 * {@link RegionResolver} implementation used to resolve a {@link DriverRegion} by {@link String name}
 * from a Spring {@link BeanFactory}.
 *
 * @author John Blum
 * @see DriverRegion
 * @see BeanFactory
 * @see RegionResolver
 * @see AbstractCachingRegionResolver
 * @since 2.3.0
 */
public class BeanFactoryRegionResolver extends AbstractCachingRegionResolver {

	private final BeanFactory beanFactory;

	/**
	 * Constructs a new instance of {@link BeanFactoryRegionResolver} initialized with the given {@link BeanFactory}.
	 *
	 * @param beanFactory {@link BeanFactory} used to resolve cache {@link DriverRegion Regions}.
	 * @throws IllegalArgumentException if {@link BeanFactory} is {@literal null}.
	 * @see BeanFactory
	 */
	public BeanFactoryRegionResolver(BeanFactory beanFactory) {
		Assert.notNull(beanFactory, "BeanFactory must not be null");
		this.beanFactory = beanFactory;
	}

	/**
	 * Returns the configured reference to the {@link BeanFactory} used to resolve cache {@link DriverRegion Regions}.
	 *
	 * @return a reference to the configured {@link BeanFactory}.
	 * @see BeanFactory
	 */
	protected @NonNull BeanFactory getBeanFactory() {
		return this.beanFactory;
	}

	/**
	 * {@inheritDoc}
	 */
	@Nullable @Override @SuppressWarnings("unchecked")
	protected <K, V> DriverRegion<K, V> doResolve(@Nullable String regionName) {

		BeanFactory beanFactory = getBeanFactory();

		return StringUtils.hasText(regionName) && beanFactory.containsBean(regionName)
			? beanFactory.getBean(regionName, DriverRegion.class)
			: null;
	}
}
