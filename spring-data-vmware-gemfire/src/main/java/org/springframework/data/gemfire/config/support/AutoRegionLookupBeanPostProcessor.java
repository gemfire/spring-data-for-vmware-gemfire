// Copyright (c) VMware, Inc. 2022. All rights reserved.
// SPDX-License-Identifier: Apache-2.0

package org.springframework.data.gemfire.config.support;

import static org.springframework.data.gemfire.util.RuntimeExceptionFactory.newIllegalStateException;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;

import org.apache.geode.cache.GemFireCache;
import org.apache.geode.cache.Region;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

/**
 * The {@link AutoRegionLookupBeanPostProcessor} class is a Spring {@link BeanPostProcessor} that post processes
 * a {@link GemFireCache} by registering all cache {@link Region Regions} that have not been explicitly defined
 * in the Spring application context.
 *
 * This is usually the case for {@link Region Regions} that have been defined in GemFire's native {@literal cache.xml}
 * or defined using GemFire Cluster-based Configuration Service.
 *
 * @author John Blum
 * @see GemFireCache
 * @see Region
 * @see BeanFactory
 * @see BeanFactoryAware
 * @see BeanPostProcessor
 * @see ConfigurableListableBeanFactory
 * @since 1.5.0
 */
public class AutoRegionLookupBeanPostProcessor implements BeanPostProcessor, BeanFactoryAware {

	private ConfigurableListableBeanFactory beanFactory;

	/**
	 * Sets a reference to the configured Spring {@link BeanFactory}.
	 *
	 * @param beanFactory configured Spring {@link BeanFactory}.
	 * @throws IllegalArgumentException if the given {@link BeanFactory} is not an instance of
	 * {@link ConfigurableListableBeanFactory}.
	 * @see BeanFactoryAware
	 * @see BeanFactory
	 */
	@Override
	@SuppressWarnings("all")
	public final void setBeanFactory(BeanFactory beanFactory) throws BeansException {

		Assert.isInstanceOf(ConfigurableListableBeanFactory.class, beanFactory,
			String.format("BeanFactory [%1$s] must be an instance of %2$s",
				ObjectUtils.nullSafeClassName(beanFactory), ConfigurableListableBeanFactory.class.getSimpleName()));

		this.beanFactory = (ConfigurableListableBeanFactory) beanFactory;
	}

	/**
	 * Returns a reference to the containing Spring {@link BeanFactory}.
	 *
	 * @return a reference to the containing Spring {@link BeanFactory}.
	 * @throws IllegalStateException if the {@link BeanFactory} was not configured.
	 * @see BeanFactory
	 */
	protected ConfigurableListableBeanFactory getBeanFactory() {
		return Optional.ofNullable(this.beanFactory)
			.orElseThrow(() -> newIllegalStateException("BeanFactory was not properly configured"));
	}

	@Override
	public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {

		if (bean instanceof GemFireCache) {
			registerCacheRegionsAsBeans((GemFireCache) bean);
		}

		return bean;
	}

	void registerCacheRegionsAsBeans(GemFireCache cache) {
		cache.rootRegions().forEach(this::registerCacheRegionAsBean);
	}

	void registerCacheRegionAsBean(Region<?, ?> region) {

		if (region != null) {

			String regionBeanName = getBeanName(region);

			if (!getBeanFactory().containsBean(regionBeanName)) {
				getBeanFactory().registerSingleton(regionBeanName, region);
			}

			for (Region<?, ?> subregion : nullSafeSubregions(region)) {
				registerCacheRegionAsBean(subregion);
			}
		}
	}

	String getBeanName(Region region) {

		return Optional.ofNullable(region.getFullPath())
			.filter(StringUtils::hasText)
			.filter(regionFullPath -> regionFullPath.lastIndexOf(Region.SEPARATOR) > 0)
			.orElseGet(region::getName);
	}

	Set<Region<?, ?>> nullSafeSubregions(Region<?, ?> parentRegion) {
		return Optional.ofNullable(parentRegion.subregions(false)).orElse(Collections.emptySet());
	}
}
