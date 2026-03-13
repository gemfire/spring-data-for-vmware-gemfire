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

package org.springframework.data.gemfire.config.support;

import static org.springframework.data.gemfire.util.RuntimeExceptionFactory.newIllegalStateException;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.data.gemfire.gud.api.GudClientCache;
import org.springframework.data.gemfire.gud.api.GudRegion;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

/**
 * The {@link AutoRegionLookupBeanPostProcessor} class is a Spring {@link BeanPostProcessor} that post processes
 * a {@link GudClientCache} by registering all cache {@link GudRegion Regions} that have not been explicitly defined
 * in the Spring application context.
 *
 * This is usually the case for {@link GudRegion Regions} that have been defined in GemFire's native {@literal cache.xml}.
 *
 * @author John Blum
 * @see GudClientCache
 * @see GudRegion
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

		if (bean instanceof GudClientCache clientCacheBean) {
			registerCacheRegionsAsBeans(clientCacheBean);
		}

		return bean;
	}

	void registerCacheRegionsAsBeans(GudClientCache cache) {
		cache.rootRegions().forEach(this::registerCacheRegionAsBean);
	}

	void registerCacheRegionAsBean(GudRegion<?, ?> region) {

		if (region != null) {

			String regionBeanName = getBeanName(region);

			if (!getBeanFactory().containsBean(regionBeanName)) {
				getBeanFactory().registerSingleton(regionBeanName, region);
			}

			for (GudRegion<?, ?> subregion : nullSafeSubregions(region)) {
				registerCacheRegionAsBean(subregion);
			}
		}
	}

	String getBeanName(GudRegion<?, ?> region) {

		return Optional.ofNullable(region.getFullPath())
			.filter(StringUtils::hasText)
			.filter(regionFullPath -> regionFullPath.lastIndexOf(GudRegion.SEPARATOR) > 0)
			.orElseGet(region::getName);
	}

	Set<GudRegion<?, ?>> nullSafeSubregions(GudRegion<?, ?> parentRegion) {
		return Optional.ofNullable(parentRegion.subregions(false)).orElse(Collections.emptySet());
	}
}
