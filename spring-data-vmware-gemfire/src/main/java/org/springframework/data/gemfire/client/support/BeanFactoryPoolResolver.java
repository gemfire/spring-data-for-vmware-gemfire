/*
 * Copyright (c) VMware, Inc. 2022-2023. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.client.support;

import org.apache.geode.cache.client.Pool;

import org.apache.shiro.util.StringUtils;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.data.gemfire.client.PoolResolver;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

/**
 * {@link PoolResolver} implementation that uses the Spring {@link BeanFactory} to resolve managed {@link Pool} objects.
 * This means the {@link Pool} was configured and initialized by the Spring container given the {@link Pool} would be a
 * proper bean declaration in this case.
 *
 * @author John Blum
 * @see Pool
 * @see BeanFactory
 * @see BeanFactoryAware
 * @see PoolResolver
 * @since 2.3.0
 */
public class BeanFactoryPoolResolver implements BeanFactoryAware, PoolResolver {

	private BeanFactory beanFactory;

	/**
	 * Constructs a new instance of the {@link BeanFactoryPoolResolver} initialized with
	 * the given Spring {@link BeanFactory} used to resolve managed {@link Pool} objects.
	 *
	 * @param beanFactory Spring {@link BeanFactory} used to resolve managed {@link Pool} objects.
	 * @see BeanFactory
	 */
	public BeanFactoryPoolResolver(@NonNull BeanFactory beanFactory) {
		setBeanFactory(beanFactory);
	}

	/**
	 * @inheritDoc
	 */
	@Override
	public final void setBeanFactory(@NonNull BeanFactory beanFactory) throws BeansException {

		Assert.notNull(beanFactory, "BeanFactory must not be null");

		this.beanFactory = beanFactory;
	}

	/**
	 * Returns a reference to the configured Spring {@link BeanFactory} used to resolve managed {@link Pool} objects.
	 *
	 * @return a reference to the configured Spring {@link BeanFactory}.
	 * @see BeanFactory
	 */
	protected BeanFactory getBeanFactory() {
		return this.beanFactory;
	}

	/**
	 * Resolves the managed, {@link String named} Apache Geode {@link Pool} from the Spring {@link BeanFactory}.
	 *
	 * @param poolName {@link String name} of the {@link Pool} to resolve.
	 * @return the resolved, {@link String named}, managed {@link Pool} object or {@literal null} if no {@link Pool}
	 * with the given {@link String name} could be resolved from the configured Spring {@link BeanFactory}.
	 * @see Pool
	 * @see #getBeanFactory()
	 */
	@Nullable @Override
	public Pool resolve(@Nullable String poolName) {

		BeanFactory beanFactory = getBeanFactory();

		return StringUtils.hasText(poolName) && beanFactory.containsBean(poolName)
			? beanFactory.getBean(poolName, Pool.class)
			: null;
	}
}
