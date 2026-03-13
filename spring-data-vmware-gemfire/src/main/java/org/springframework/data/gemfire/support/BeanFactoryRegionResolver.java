/*
 * Copyright $originalComment.match(" (\d+)", 1, "-", $today.year)2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

/*
 * @AI-Generated
 * Generated in whole or in part by Cursor
 * Description:
 * 2026-03-13: Migrated from org.apache.geode imports to GUD API types
 */

package org.springframework.data.gemfire.support;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.data.gemfire.RegionResolver;
import org.springframework.data.gemfire.gud.api.GudRegion;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/**
 * {@link RegionResolver} implementation used to resolve a {@link GudRegion} by {@link String name}
 * from a Spring {@link BeanFactory}.
 *
 * @author John Blum
 * @see GudRegion
 * @see BeanFactory
 * @see BeanFactoryAware
 * @see RegionResolver
 * @see AbstractCachingRegionResolver
 * @since 2.3.0
 */
public class BeanFactoryRegionResolver extends AbstractCachingRegionResolver implements BeanFactoryAware {

	private BeanFactory beanFactory;

	/**
	 * Constructs a new instance of {@link BeanFactoryRegionResolver} initialized with the given {@link BeanFactory}.
	 *
	 * @param beanFactory {@link BeanFactory} used to resolve cache {@link GudRegion Regions}.
	 * @throws IllegalArgumentException if {@link BeanFactory} is {@literal null}.
	 * @see BeanFactory
	 * @see #setBeanFactory(BeanFactory)
	 */
	public BeanFactoryRegionResolver(BeanFactory beanFactory) {
		setBeanFactory(beanFactory);
	}

	/**
	 * Sets a reference to the {@link BeanFactory} used to resolve cache {@link GudRegion Regions}.
	 *
	 * @param beanFactory {@link BeanFactory} used to resolve cache {@link GudRegion Regions}.
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
	 * Returns the configured reference to the {@link BeanFactory} used to resolve cache {@link GudRegion Regions}.
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
	protected <K, V> GudRegion<K, V> doResolve(@Nullable String regionName) {

		BeanFactory beanFactory = getBeanFactory();

		return StringUtils.hasText(regionName) && beanFactory.containsBean(regionName)
			? beanFactory.getBean(regionName, GudRegion.class)
			: null;
	}
}
