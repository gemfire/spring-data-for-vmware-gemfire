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

package org.springframework.data.gemfire.client.support;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.data.gemfire.client.PoolResolver;
import org.springframework.data.gemfire.gud.api.GudPool;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/**
 * {@link PoolResolver} implementation that uses the Spring {@link BeanFactory} to resolve managed {@link GudPool} objects.
 * This means the {@link GudPool} was configured and initialized by the Spring container given the {@link GudPool} would be a
 * proper bean declaration in this case.
 *
 * @author John Blum
 * @see GudPool
 * @see BeanFactory
 * @see BeanFactoryAware
 * @see PoolResolver
 * @since 2.3.0
 */
public class BeanFactoryPoolResolver implements BeanFactoryAware, PoolResolver {

	private BeanFactory beanFactory;

	/**
	 * Constructs a new instance of the {@link BeanFactoryPoolResolver} initialized with
	 * the given Spring {@link BeanFactory} used to resolve managed {@link GudPool} objects.
	 *
	 * @param beanFactory Spring {@link BeanFactory} used to resolve managed {@link GudPool} objects.
	 * @see BeanFactory
	 */
	public BeanFactoryPoolResolver(@NonNull BeanFactory beanFactory) {
		setBeanFactory(beanFactory);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void setBeanFactory(@NonNull BeanFactory beanFactory) throws BeansException {

		Assert.notNull(beanFactory, "BeanFactory must not be null");

		this.beanFactory = beanFactory;
	}

	/**
	 * Returns a reference to the configured Spring {@link BeanFactory} used to resolve managed {@link GudPool} objects.
	 *
	 * @return a reference to the configured Spring {@link BeanFactory}.
	 * @see BeanFactory
	 */
	protected BeanFactory getBeanFactory() {
		return this.beanFactory;
	}

	/**
	 * Resolves the managed, {@link String named} Apache Geode {@link GudPool} from the Spring {@link BeanFactory}.
	 *
	 * @param poolName {@link String name} of the {@link GudPool} to resolve.
	 * @return the resolved, {@link String named}, managed {@link GudPool} object or {@literal null} if no {@link GudPool}
	 * with the given {@link String name} could be resolved from the configured Spring {@link BeanFactory}.
	 * @see GudPool
	 * @see #getBeanFactory()
	 */
	@Nullable @Override
	public GudPool resolve(@Nullable String poolName) {

		BeanFactory beanFactory = getBeanFactory();

		return StringUtils.hasText(poolName) && beanFactory.containsBean(poolName)
			? beanFactory.getBean(poolName, GudPool.class)
			: null;
	}
}
